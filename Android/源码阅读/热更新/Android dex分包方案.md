
### dex分包方案

https://www.cnblogs.com/linghu-java/p/8615702.html

### QQ空间开发团队方案

https://mp.weixin.qq.com/s?__biz=MzI1MTA1MzM2Nw==&mid=400118620&idx=1&sn=b4fdd5055731290eef12ad0d17f39d4a&scene=1&srcid=1106Imu9ZgwybID13e7y2nEi#wechat_redirect

### 热修复小结
https://blog.csdn.net/lmj623565791/article/details/49883661

### 一、分包的原因：

 当一个app的功能越来越复杂，代码量越来越多，也许有一天便会突然遇到下列现象：

1. 生成的apk在2.3以前的机器无法安装，提示`INSTALL_FAILED_DEXOPT`

2. 方法数量过多，编译时出错，提示：

Conversion to Dalvik format failed:Unable to execute dex: method ID not in [0, 0xffff]: 65536  

 

出现这种问题的原因是：

1. Android2.3及以前版本用来执行dexopt(用于优化dex文件)的内存只分配了5M

2. 一个dex文件最多只支持65536个方法。

 

针对上述问题，也出现了诸多解决方案，使用的最多的是插件化，即将一些独立的功能做成一个单独的apk，当打开的时候使用DexClassLoader动态加载，然后使用反射机制来调用插件中的类和方法。这固然是一种解决问题的方案：但这种方案存在着以下两个问题：

1. 插件化只适合一些比较独立的模块；

2. 必须通过反射机制去调用插件的类和方法，因此，必须搭配一套插件框架来配合使用；

 

由于上述问题的存在，通过不断研究，便有了dex分包的解决方案。简单来说，其原理是将编译好的class文件拆分打包成两个dex，绕过dex方法数量的限制以及安装时的检查，在运行时再动态加载第二个dex文件中。faceBook曾经遇到相似的问题，具体可参考：

 

https://www.facebook.com/notes/facebook-engineering/under-the-hood-dalvik-patch-for-facebook-for-android/10151345597798920

文中有这么一段话：

However, there was no way we could break our app up this way--too many of our classes are accessed directly by the Android framework. Instead, we needed to inject our secondary dex files directly into the system class loader。

文中说得比较简单，我们来完善一下该方案：除了第一个dex文件（即正常apk包唯一包含的Dex文件），其它dex文件都以资源的方式放在安装包中，并在Application的onCreate回调中被注入到系统的ClassLoader。因此，对于那些在注入之前已经引用到的类（以及它们所在的jar）,必须放入第一个Dex文件中。

### ClassLoader

在Android中，有两个ClassLoader，分别是DexClassLoader和PathClassLoader，它们的父类都是BaseDexClassLoader，DexClassLoader和PathClassLoader的实现都是在BaseDexClassLoader之中，而BaseDexClassLoader的实现又基本是通过调用DexPathList的方法完成的。DexPathList里面封装了加载dex文件为DexFile对象（调用了native方法，有兴趣的童鞋可以继续跟踪下去）的方法。 
上述代码中的逻辑如下：

1. 通过反射获取pathList对象
2. 通过pathList把输入的dex文件输出为elements数组，elements数组中的元素封装了DexFile对象
3. 把新输出的elements数组合并到原pathList的dexElements数组中
4. 异常处理

当把dex文件加载到pathList的dexElements数组之后，整个multidex.install基本上就完成了。

### MultiDex实现原理

1. Dex拆分
dex拆分步骤为：

自动扫描整个工程代码得到main-dex-list；
根据main-dex-list对整个工程编译后的所有class进行拆分，将主、从dex的class文件分开；
用dx工具对主、从dex的class文件分别打包成 .dex文件，并放在apk的合适目录。
怎么自动生成 main-dex-list？ Android SDK 从 build tools 21 开始提供了 mainDexClasses 脚本来生成主 dex 的文件列表。查看这个脚本的源码，可以看到它主要做了下面两件事情：

1）调用 proguard 的 shrink 操作来生成一个临时 jar 包；

2）将生成的临时 jar 包和输入的文件集合作为参数，然后调用com.android.multidex.MainDexListBuilder 来生成主 dex 文件列表。

2. Dex加载
因为Android系统在启动应用时只加载了主dex（Classes.dex），其他的 dex 需要我们在应用启动后进行动态加载安装。android-support-multidex.jar就是做这个用的，该 jar 包从 build tools 21.1 开始支持。
android系统使用BaseDexClassLoader来加载Dex文件，它有两个子类DexClassLoader和PathClassLoader，它们使用场景如下：

PathClassLoader是Android应用中的默认加载器，PathClassLoader只能加载/data/app中的apk，也就是已经安装到手机中的apk。这个也是PathClassLoader作为默认的类加载器的原因，因为一般程序都是安装了，在打开，这时候PathClassLoader就去加载指定的apk(解压成dex，然后在优化成odex)就可以了。
DexClassLoader可以加载任何路径的apk/dex/jar，PathClassLoader只能加载已安装到系统中（即/data/app目录下）的apk文件。

基本实现原理：

1. 除了第一个dex文件（即正常apk包唯一包含的Dex文件），其它dex文件都以资源的方式放在安装包中。所以我们需要将其他dex文件并在Application的onCreate回调中注入到系统的ClassLoader。并且对于那些在注入之前已经引用到的类（以及它们所在的jar）,必须放入第一个Dex文件中。

2. PathClassLoader作为默认的类加载器，在打开应用程序的时候PathClassLoader就去加载指定的apk(解压成dex，然后在优化成odex)，也就是第一个dex文件是PathClassLoader自动加载的。所以，我们需要做的就是将其他的dex文件注入到这个PathClassLoader中去。

3. 因为PathClassLoader和DexClassLoader的原理基本一致，从前面的分析来看，我们知道PathClassLoader里面的dex文件是放在一个Element数组里面，可以包含多个dex文件，每个dex文件是一个Element，所以我们只需要将其他的dex文件放到这个数组中去就可以了。

实现：

1. 通过反射获取PathClassLoader中的DexPathList中的Element数组（已加载了第一个dex包，由系统加载）
2. 通过反射获取DexClassLoader中的DexPathList中的Element数组（将第二个dex包加载进去）
3. 将两个Element数组合并之后，再将其赋值给PathClassLoader的Element数组