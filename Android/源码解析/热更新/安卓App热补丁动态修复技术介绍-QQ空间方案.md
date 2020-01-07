### 1.背景

以补丁的方式动态修复紧急Bug，不再需要重新发布App，不再需要用户重新下载，覆盖安装

### 2.解决方案

该方案基于的是android dex分包方案的，关于dex分包方案，网上有几篇解释了，[可以看看这里](https://www.cnblogs.com/linghu-java/p/8615702.html)

简单的概括一下，就是把多个dex文件塞入到app的classloader之中，但是android dex拆包方案中的类是没有重复的，如果classes.dex和classes1.dex中有重复的类，当用到这个重复的类的时候，系统会选择哪个类进行加载呢？

让我们来看看类加载的代码：

```java

#BaseDexClassLoader
@Override
protected Class<?> findClass(String name) throws ClassNotFoundException {
    //pathList是DexPathList
    Class clazz = pathList.findClass(name);
    if (clazz == null) {
        throw new ClassNotFoundException(name);
    }
    return clazz;
}

#DexPathList
public Class findClass(String name) {
        //每一个Element就是一个dex文件
        for (Element element : dexElements) {
            DexFile dex = element.dexFile;
            if (dex != null) {
                Class clazz = dex.loadClassBinaryName(name, definingContext);
                if (clazz != null) {
                    return clazz;
                }
            }
        }
        return null;
    }
```

一个ClassLoader可以包含多个dex文件，每个dex文件是一个Element，多个dex文件排列成一个有序的数组**dexElements**，当找类的时候，会按顺序遍历dex文件，然后从当前遍历的dex文件中找类，如果找类则返回，如果找不到从下一个dex文件继续查找。

理论上，如果在不同的dex中有相同的类存在，那么会优先选择排在前面的dex文件的类，如下图：

![](http://mmbiz.qpic.cn/mmbiz/0aYRVN1mAJwR6vqR4Yv6V3zIvjqmgdu7dVfrXN7XxhOjiaahHricl00hal4rjw1cQ2LRFKVGU7uUOO0Q5HSz7hKw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

在此基础上，我们构想了热补丁的方案，把有问题的类打包到一个dex（patch.dex）中去，然后把这个dex插入到Elements的最前面，如下图：

![](http://mmbiz.qpic.cn/mmbiz/0aYRVN1mAJwR6vqR4Yv6V3zIvjqmgdu7L1OTRicUdpKy3Txd5eancPZXSWKVic4S9A7rUticj9aKY5UhfbAhjpLjg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

好，该方案基于第二个拆分dex的方案，方案实现如果懂拆分dex的原理的话，大家应该很快就会实现该方案，如果没有拆分dex的项目的话，可以参考一下谷歌的multidex方案实现。然后在插入数组的时候，把补丁包插入到最前面去。

好，看似问题很简单，轻松的搞定了，让我们来试验一下，修改某个类，然后打包成dex，插入到classloader，当加载类的时候出现了（本例中是QzoneActivityManager要被替换）：

![](http://mmbiz.qpic.cn/mmbiz/0aYRVN1mAJwR6vqR4Yv6V3zIvjqmgdu7tC9UtEopH3nCy3WxLufdwribQYGesIeofBebMwHwbAicvOH1FTzicVMdQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

为什么会出现以上问题呢？

从log的意思上来讲，ModuleManager引用了QzoneActivityManager，但是发现这这两个类所在的dex不在一起，其中：

1. ModuleManager在classes.dex中

2. QzoneActivityManager在patch.dex中

结果发生了错误。

这里有个问题,拆分dex的很多类都不是在同一个dex内的,怎么没有问题?

![](http://mmbiz.qpic.cn/mmbiz/0aYRVN1mAJwR6vqR4Yv6V3zIvjqmgdu7OwicJpMnQicGxNCs2NWFTNuTjknjCoaCuvBMEUYrxvKJmMBVty3icB1Rw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

从代码上来看，如果两个相关联的类在不同的dex中就会报错，但是拆分dex没有报错这是为什么，原来这个校验的前提是：

![](http://mmbiz.qpic.cn/mmbiz/0aYRVN1mAJwR6vqR4Yv6V3zIvjqmgdu7N1uaUdav2cvKA5H1KtVZOcQVN2uCkiaQOJVJH3yicice5q3VPtHLMB5OA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

如果引用者（也就是ModuleManager）这个类被打上了**CLASS_ISPREVERIFIED**标志，那么就会进行dex的校验。那么这个标志是什么时候被打上去的？让我们在继续搜索一下代码，嘿咻嘿咻~~，在DexPrepare.cpp找到了一下代码：