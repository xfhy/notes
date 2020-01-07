> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/YFMkJZXSqmc0OOuyAnc4mA

点击上方 “**刘望舒**”，马上关注，早上 **8:42** 推送  

技术进阶关注 “**刘望舒**” 就对了

作者 :  刘望舒  |  来源 ：刘望舒的博客

地址：http://liuwangshu.cn/application/android-gradle/1-gradle-plug-in.html

### 前言

老读者都知道，我的技术博客从 2016 年开始就没写过不成系列的文章，这些系列文章组成了目前 Android 领域最全面深入的原创知识体系，更恐怖的是这个体系还在不断的成长，关于这个知识体系可以点击 阅读原文 了解。

今天我给大家带来的是 Android Gradle 系列文章的开篇，实际上在 Android Gradle 系列之前，我已经写了 Gradle 核心思想系列，这个系列我尽量避免了 Gradle 和 Android 之间的关联，这是因为在了解 Gradle 的核心思想后，可以更好的理解 Android Gradle，因此这里强烈建议先阅读 Gradle 核心思想系列。

[为什么现在要用 Gradle？](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649842700&idx=1&sn=3ea966869a96c36836aba920c774f0c3&chksm=83bf6d57b4c8e44110a7ce046a1a95d9847e2ccf3e7c0a1fac95b889ae2a55ace725e2630694&scene=21#wechat_redirect)

[Gradle 入门前奏](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649842877&idx=1&sn=618a4926808b1225b3fc6fc1803f7f99&chksm=83bf6de6b4c8e4f0c5d0956797a5698f70213c75d5ab175f2dcf36d4f42691378f4772832e30&scene=21#wechat_redirect)  

[Groovy 快速入门看这篇就够了](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649842919&idx=1&sn=212eac39bf76c4340b165a558572ce7f&chksm=83bf6dbcb4c8e4aaa82d57ad4667fbfa6c35102b0da5fd1d97f297fd33bbcd62be4e119c4eb7&scene=21#wechat_redirect)

[看似无用，实则重要的 Gradle Wrapper](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649843617&idx=1&sn=de27f38a63a95368baa773cda463130d&chksm=83bf6efab4c8e7ec9aa85b397f5cc211940da51b68342962ce7f466bfa921a759bde32524166&scene=21#wechat_redirect)  

[通俗易懂的 Gradle 插件讲解](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649843752&idx=1&sn=b3b01ea8e56a3397de90b385a8b50c66&chksm=83bf6173b4c8e865f29a759328dc7160cee05cdc0198bc64db29814596d4e4c0c8762d62114e&scene=21#wechat_redirect)  

[通俗易懂的自定义 Gradle 插件讲解](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649843781&idx=1&sn=a7ee811d2bf739adf019c6cef405c8ef&chksm=83bf611eb4c8e808d817f8b4d96af97a13055a6c238a09bdd6206a44bb552388a7e15d37bec9&scene=21#wechat_redirect)

### 1. 什么是 Gradle 的 Android 插件

在[通俗易懂的 Gradle 插件讲解](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649843752&idx=1&sn=b3b01ea8e56a3397de90b385a8b50c66&chksm=83bf6173b4c8e865f29a759328dc7160cee05cdc0198bc64db29814596d4e4c0c8762d62114e&scene=21#wechat_redirect)这篇文章中我们知道，Gradle 有很多插件，为了支持 Android 项目的构建，谷歌为 Gradle 编写了 Android 插件，新的 Android 构建系统就是由 Gradle 的 Android 插件组成的，Gradle 是一个高级构建工具包，它管理依赖项并允许开发者自定义构建逻辑。Android Studio 使用 Gradle wrapper 来集成 Gradle 的 Android 插件。需要注意的是，Gradle 的 Android 插件也可以独立于 AndroidStudio 运行。  
在 Android 的官方网站提到了新的 Android 构建系统主要有以下几个特点：

*   代码和资源易于重用
    
*   无论是针对多个 apk 发行版还是针对不同风格的应用程序，都可以很容易创建应用程序的多个不同版本。
    
*   易于配置、扩展和自定义构建过程
    
*   良好的 IDE 集成
    

Gradle 的 Android 插件结合 Android Studio 成为了目前最为流行的 Android 构建系统。

### 2. Android Studio 的模块类型和项目视图

Android Studio 中的每个项目包含一个或多个含有源代码文件和资源文件的模块，这些模块可以独立构建、测试或调试，一个 Android Studio 的模块类型可以有以下几种：

**Android 应用程序模块**  
 Android 应用程序模块可能依赖于库模块，尽管许多 Android 应用程序只包含一个应用程序模块，构建系统会将其生成一个 APK。

**Android 库模块**  
Android 库模块包含可重用的特定于 Android 的代码和资源，构建系统会将其生成一个 AAR。

**App 引擎模块**  
包含应用程序引擎集成的代码和资源。

**Java 库模块**  
包含可重用的代码，构建系统会将其生成一个 JAR 包。

Android Studio3.3.2 中的 Android 项目视图如下所示。  

![](https://mmbiz.qpic.cn/mmbiz_png/nk8ic4xzfuQ92fGz4RmicItCic0Loeo1XYlTyg9kUMGByNt7LbicNVfbxKUDB4pNRFRyNgTZw5Z0eUNxhRAicquT8jw/640?wx_fmt=png)1.png  
所有构建文件在 Gradle Scripts 层级下显示，大概介绍下这些文件的用处。

  

*   项目 build.gradle：配置项目的整体属性，比如指定使用的代码仓库、依赖的 Gradle 插件版本等等。
    
*   模块 build.gradle：配置当前 Module 的编译参数。
    
*   gradle-wrapper.properites：配置 Gradle Wrapper，可以查看 Gradle 核心思想（四）看似无用，实则重要的 Gradle Wrapper 这篇文章。
    
*   gradle.properties：配置 Gradle 的编译参数。具体配置见 Gradle 官方文档
    
*   settings.gradle：配置 Gradle 的多项目管理。
    
*   local.properties：一般用来存放该 Android 项目的私有属性配置，比如 Android 项目的 SDK 路径。
    

这篇文章主要介绍项目 build.gradle 和模块 build.gradle。

### 3. 项目 build.gradle

我们新建一个 Android 项目，它的项目 build.gradle 的内容如下：

```
buildscript {
    repositories {
        google()
        jcenter()   
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.3.2' //1
    }
}

allprojects {
    repositories {
        google()
        jcenter()  
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}


```

注释 1 处配置依赖的 Gradle 插件版本，Gradle 插件属于第三方插件，因此这里在 buildscrip 块中配置谷歌的 Maven 库和 JCenter 库，这样 Gradle 系统才能找到对应的 Gradle 插件。  
如果使用`google()`报`not found: 'google()'`错误，可以用如下代码替代：

```
maven { url 'https://maven.google.com' }


```

如果你还不理解 Gradle 插件，可以查看 Gradle 核心思想（五）通俗易懂的 Gradle 插件讲解这篇文章。

### 4. 模块 build.gradle

新建一个 Android 项目，它的模块 build.gradle 的内容如下：

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.example.myapplication"
        minSdkVersion 15
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
}


```

#### 4.1 Gradle 的 Android 插件类型

apply 引入的插件 id 为 com.android.application，说明当前模块是一个应用程序模块，Gradle 的 Android 插件有多个类型分别为：

*   应用程序插件，插件 id 为 com.android.application，会生成一个 APK。
    
*   库插件，插件 id 为 com.android.library，会生成一个 AAR，提供给其他应用程序模块用。
    
*   测试插件，插件 id 为 com.android.test，用于测试其他的模块。
    
*   feature 插件，插件 id 为 com.android.feature，创建 Android Instant App 时需要用到的插件。
    
*   Instant App 插件，插件 id 为 com.android.instantapp，是 Android Instant App 的入口。
    

#### 4.2 Android 块

Android 块用于描述该 Module 构建过程中所用到的所有参数。

*   compileSdkVersion：配置编译该模块的 SDK 版本
    
*   buildToolsVersion：Android 构建工具的版本
    

##### 4.2.1 defaultConfig 块

Android 块中的 defaultConfig 块用于默认配置，常用的配置如下所示。

![](https://mmbiz.qpic.cn/mmbiz_png/nk8ic4xzfuQ92fGz4RmicItCic0Loeo1XYllFvegCK72YclCyqbHL0eFTpdiaWXLZPPl2WiaiaSCOaZYqRlhuWQ8Nc1Q/640?wx_fmt=png)

#####   
buildTypes 块用于配置构建不同类型的 APK。4.2.2 buildTypes 块

当我们新建一个项目时，在 Android 块已经默认配置了 buildTypes 块：

```
 buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }


```

在 AS 的 Terminal 中执行 gradlew.bat build 命令，会在该模块的 build/outputs/apk 目录中生成 release 和 debug 的 APK，虽然只配置了 release ，但 release 和 debug 是默认配置，即使我们不配置也会生成。也可以修改默认的 release 和 debug，甚至可以自定义构建类型，比如：

```
 buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        debug {
            debuggable true
        }
        privitedebug{
            applicationIdSuffix ""
        }
    }


```

这时会在 build/outputs/apk 目录中生成 release、debug、privitedebug 的 APK。  
buildTypes 块还可以配置很多属性，常用的配置如下所示。

![](https://mmbiz.qpic.cn/mmbiz_png/nk8ic4xzfuQ92fGz4RmicItCic0Loeo1XYlibMqAfWzHBbOKHcoO0bicbjWtSEaiao1w70R1xeBgDLEicjfGcyxtvz2DA/640?wx_fmt=png)

##### 4.2.3 signingConfigs 块

用于配置签名设置，一般用来配置 release 模式。

![](https://mmbiz.qpic.cn/mmbiz_png/nk8ic4xzfuQ92fGz4RmicItCic0Loeo1XYl6v9WjtT7ibSKficJNVPbSJPKmf42D6EtGyp8U606cjlaBM01hevrib7WA/640?wx_fmt=png)

```
signingConfigs {
        release {
            storeFile file('C:/Users/liuwangshu/.android/release.keystore')
            storePassword 'android'
            keyAlias 'androidreleasekey'
            keyPassword 'android'

        }


```

##### 4.2.4 其他配置块

android 块中除了前面讲的 defaultConfig 块、buildTypes 块、signingConfigs 块还有其他的配置块，这里列举一些。

![](https://mmbiz.qpic.cn/mmbiz_png/nk8ic4xzfuQ92fGz4RmicItCic0Loeo1XYllXuxeeOV92XuOJOtELibXhcKOCrkic5vLHN1Dod4HLTNP0znSTjNqGTQ/640?wx_fmt=png)

更多的配置块请参考官方文档。

##### 4.2.4 全局配置

如果有多个 module 的配置是一样的，可以将这些配置提取出来，也就是使用全局配置。全局配置有多种方式，这里介绍其中的两种。  
**1. 使用 ext 块配置**  
 在项目 build.gradle 中使用 ext 块，如下所示。

```
ext{
    compileSdkVersion =28
    buildToolsVersion ="28.0.3"
    minSdkVersion =15
    targetSdkVersion =28
}


```

在某个 module 的 build.gradle 中使用配置：

```
apply plugin: 'com.android.application'
android {
    compileSdkVersion rootProject.ext.compileSdkVersion
    buildToolsVersion rootProject.ext.buildToolsVersion
    defaultConfig {
        applicationId "com.example.liuwangshu.hookinstrumentation"
        minSdkVersion rootProject.ext.minSdkVersion
        targetSdkVersion rootProject.ext.targetSdkVersion
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
  ...
}
...


```

**2. 使用 config.gradle 配置**  
首先在根目录下创建 config.gradle 文件来进行配置。  
**config.gradle**

```
ext{
    android=[
            applicationId:"com.example.liuwangshu.hookinstrumentation",
            compileSdkVersion :28,
            buildToolsVersion :"28.0.3",
            minSdkVersion : 15,
            targetSdkVersion : 28,
    ]

    dependencies =[
            "appcompat-v7" : "com.android.support:appcompat-v7:28.0.0",
            "constraint"  : "com.android.support.constraint:constraint-layout:1.1.3",
    ]
}


```

接着在项目 build.gradle 中添加`apply from: "config.gradle"`，这样项目的所有 module 都能用 config.gradle 中定义的参数。  
最后在 module 的 build.gradle 中使用配置：

```
apply plugin: 'com.android.application'
android {
    compileSdkVersion rootProject.ext.android.compileSdkVersion
    buildToolsVersion rootProject.ext.android.buildToolsVersion
    defaultConfig {
        applicationId rootProject.ext.android.applicationId
        minSdkVersion rootProject.ext.android.minSdkVersion
        targetSdkVersion rootProject.ext.android.targetSdkVersion
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
   ...
dependencies {
    implementation rootProject.ext.dependencies["constraint"]
    implementation rootProject.ext.dependencies["appcompat-v7"]
    ...
}


```

#### 4.2 dependencies 块

dependencies 块用于配置该 module 构建过程中所依赖的所有库。Gradle 插件 3.4 版本新增了 api 和 implementation 来代替 compile 配置依赖，其中 api 和此前的 compile 是一样的。dependencies 和 api 主要以下的区别：

*   implementation 可以让 module 在编译时隐藏自己使用的依赖，但是在运行时这个依赖对所有模块是可见的。而 api 与 compile 一样，无法隐藏自己使用的依赖。
    
*   如果使用 api，一个 module 发生变化，这条依赖链上所有的 module 都需要重新编译，而使用 implemention，只有直接依赖这个 module 需要重新编译。
    

感谢  
https://jeroenmols.com/blog/2017/06/14/androidstudio3/  
http://google.github.io/android-gradle-dsl/current/  
http://www.androiddocs.com/tools/building/plugin-for-gradle.html  
https://www.jianshu.com/p/8962d6ba936e  
https://www.jianshu.com/p/b6744e1e4f7c  
《Android 群英传 神兵利器》

--------  END  ---------

**推荐阅读**

[今天，我需要你的支持！](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649844554&idx=1&sn=ccd7d38662f732a22cf9309533238187&chksm=83bf6211b4c8eb0762d88c518e487ebb44bf71f37ac9ae72761ac33350628e866c5cf73f4402&scene=21#wechat_redirect)

[Android 系统服务（一）解析 ActivityManagerService(AMS)](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649841686&idx=1&sn=38565aab03c212dde0e8647d13197bad&chksm=83bf694db4c8e05b97dc5ca9e5eea58679c764aac8405c44f46eb4581742500101399d845290&scene=21#wechat_redirect)  

[为什么阿里不建议在 for 循环中使用 "+" 进行字符串拼接](http://mp.weixin.qq.com/s?__biz=MzAxMTg2MjA2OA==&mid=2649843636&idx=1&sn=ff63fd15c4830e58a9f3f63dbed7a948&chksm=83bf6eefb4c8e7f9f5ac7a8518447fd06de1669f0399bf57d4d45b2263bf6e0a17381c3433aa&scene=21#wechat_redirect)  

**你好，我是刘望舒，****十年经验的资深架构师，著有两本业界知名的技术畅销书，多个知名技术大会的特邀演讲嘉宾。**

如果你喜欢我的文章，就给公众号加个星标吧，方便阅读。

![](https://mmbiz.qpic.cn/mmbiz_jpg/nk8ic4xzfuQ9RM3USPLDqBo9wJqSXic3hf6pFF6y7wYtpLFpNgMLsLPF04flU5kPxG4IGLorFiaoXnvautHxSFzEw/640?wx_fmt=jpeg)

听说有人不敢点这里 👇