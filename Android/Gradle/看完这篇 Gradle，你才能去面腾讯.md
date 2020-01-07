> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/niFYe3MCUeoiDeQGg-_YCQ

![](https://mmbiz.qpic.cn/mmbiz_gif/y5HvXaQmpqmhOhoo8ptlxbzTcuV2uVDuCwXz5IRCaiaQa4w09iapxbiaehnWd4lia9ce1BVPIJ4GYwZzeL2YEuoULQ/640?wx_fmt=gif)

码个蛋 (codeegg) 第 652 次推文

作者：厘米姑娘

原文：https://www.jianshu.com/p/1274c1f1b6a4

上次的 [Gradle 这么差还来面腾讯？](http://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA==&mid=2247493120&idx=1&sn=e3e32fd0d6c1bccdbbb7234ae09ae392&chksm=96ce474da1b9ce5bee69a45419a70c9429529d74ba9cf895aba2bc30ca4782a0641bd5d96e83&scene=21#wechat_redirect)还有印象没？由于文章干货太满，限于公众号的篇幅留了个关子。今天就把下面部分奉献给大家。老鼠拉大锨 -- **大头在后台**！

六. Android Gradle 插件

**1. 概述**

Android Gradle 插件继承于 Java 插件，具有 Java 插件的所有特性，也有自己的特性，看下官方介绍：

*   可以很容易地重用代码和资源
    
*   可以很容易地创建应用的衍生版本
    
*   可以很容易地配置、扩展以及自定义构建过程
    
*   和 IDE 无缝整合
    

**2. 插件分类**

*   App 应用工程：生成可运行 apk 应用；id: com.android.application
    
*   Library 库工程：生成 aar 包给其他的 App 工程公用；id: com.android.library
    
*   Test 测试工程：对 App 应用工程或 Library 库工程进行单元测试；id: com.android.test
    

**3. 项目结构**

```
|-example
| |-build.gradle
| |-example.iml
| |-libs
| |-proguard-rules.pro 混淆配置文件
| |-src
|  |-androidTest
|    |-java Android单元测试代码
|  |-main
|    |-java App主代码
|    |-res 资源文件
|    |-AndroidManifest.xml 配置文件
| |-test
|  |-java 普通单元测试代码

```

**4. 内置任务**

<一> Java 插件内置任务：如 build、assemble、check 等

<二> Android 特有的常用任务：

*   connectedCheck 任务：在所有连接的设备或者模拟器上运行 check 检查
    
*   deviceCheck 任务：通过 API 连接远程设备运行 checks
    
*   lint 任务：在所有 ProductFlavor 上运行 lint 检查
    
*   install、uninstall 任务：在已连接的设备上安装或者卸载 App
    
*   signingReport 任务：打印 App 签名
    
*   androidDependencies 任务：打印 Android 依赖
    

**5. 应用实例**

```
//应用插件，Android Gradle属于Android发布的第三方插件
buildscript{
repositories{
 jcenter()
}
dependencies{
 classpath 'com.android.tcols.build:gradle:1.5.0'
}
}
apply plugin:'com.android.application'
//自定义配置入口，后续详解
android{
compileSdkVersion 23 //编译Android工程的SDK版本
buildToolsVersion "23.0.1" //构建Android工程所用的构建工具版本

defaultConfig{
 applicationId "org.minmin.app.example"
 minSdkVersion 14
 targetSdkVersion 23
 versionCode 1
 versionName "1.0"
}
buildTypes{
release{
 minifyEnabled false
 proguardFiles getDefaultPraguardFile('proguard-andrcid.txt'), 'proguard-rules.pro'
 }
}
}
//配置第三方依赖
dependencies{
 compile fileTree(dir:'libs', include:['*.jar'])
 testCompile 'junit:junit:4.12'
 compile 'com.android.support:appcorpat-v7:23.1.1'
 compile 'com.android.support:design:23.1.1'
}

```

**a.defaultConfig**

*   作用：用于定义所有的默认配置，是一个 ProductFlavor，若 ProductFlavor 没有被特殊定义，默认使用 defaultConfig 块指定的配置
    
*   常用配置：
    

![](https://mmbiz.qpic.cn/mmbiz_png/y5HvXaQmpqlk6bCnhlwt8BCN1IJdt4vLQ3ylkGaicvnftpzKcia1J14l56xvsU3l0Z9K8q1MMfG8utE5fGrq0XbQ/640?wx_fmt=png)

**b.buildTypes**

*   作用：是构建类型，在 Android Gradle 中内置了 debug 和 release 两个构建类型，差别在于能否在设备上调试和签名不同
    
*   每一个 BuildType 都会生成一个 SourceSet 以及相应的 assemble<BuildTypeName> 任务
    
*   常用配置：
    

![](https://mmbiz.qpic.cn/mmbiz_png/y5HvXaQmpqlk6bCnhlwt8BCN1IJdt4vLYicKcrJuk9HLMIJj9Cm5uRB0Cb6pGibe3R5kVib5DAhPh3yC21rrq5aTw/640?wx_fmt=png)

**c.signingConfigs**

*   作用：配置签名设置，标记 App 唯一性、保护 App
    
*   可以对不同构建类型采用不同签名方式：debug 模式用于开发调试，可以直接使用 Android SDK 提供的默认 debug 签名证书；release 模式用于发布，需要手动配置
    
*   常用配置：
    

![](https://mmbiz.qpic.cn/mmbiz_png/y5HvXaQmpqnP8H7tH8t8HjibIia0jPia9zZPBwUjjX08bqML24Yqu16zHWZGuRwadSLG6Ep1Srqwx7gXOc9icx8xsg/640?wx_fmt=png)

```
android {
 signingConfigs {
 release{
  storeFile file('myFile.keystore')
  storePassword 'psw'
  keyAlias 'myKey'
  keyPassword 'psw'
 }
 }
}

```

**d.productFlavors**

*   作用：添加不同的渠道、并对其做不同的处理
    
*   常用配置：  
    

![](https://mmbiz.qpic.cn/mmbiz_png/y5HvXaQmpqnP8H7tH8t8HjibIia0jPia9zZecs5eMibJmC8BOWectq2k93pnl866p3g82dNibnI2ugowRBg2EIMqagQ/640?wx_fmt=png)

```
//定义baidu和google两个渠道，并声明两个维度，优先级为abi>version>defaultConfig
android{
 flavorDimensions "abi", "version"
 productFlavors{
  google{
  dimension "abi"
 }
 baidu{ 
 dimension "version"
 } 
}

```

**e.buildConfigFiled**

**作用**：在 buildTypes、ProductFlavor 自定义字段等配置

**方法**：buildConfigField(String type,String name,String value)

*   type：字段类型
    
*   name：字段常量名
    
*   value：字段常量值
    

```
android{
 buildTypes{
 debug{
  buildConfigField "boolean", "LOG_DEBUG", "true"
  buildConfigField "String", "URL", ' "http://www.ecjtu.jx.cn/" '
 }
 }
}

```

**6. 多项目构建**

和 Java Grdle 多项目构建一样的，通过 settings.gradle 配置管理多项目；在每个项目都有一个 build.gradle，采用项目依赖就能实现多项目协作。

项目直接依赖一般适用于关联较紧密、不可复用的项目，如果想让项目被其他项目所复用，比如公共组件库、工具库等，可以单独发布出去。

**7. 多渠道构建**

**a.** 基本原理

*   构建变体（Build Variant）= 构建类型（Build Type）+ 构建渠道（Product Flavor）
    

> Build Type 有 release、debug 两种构建类型 
> 
> Product Flavor 有 baidu、google 两种构建渠道 
> 
> Build Variant 有 baiduRelease、baiduDebug、googleRelease、googleDebug 四种构件产出

*   构建渠道（Product Flavor）还可以通过 dimension 进一步细化分组
    

*   assemble 开头的负责生成构件产物 (Apk)
    

> assembleBaidu：运行后会生成 baidu 渠道的 release 和 debug 包  
> assembleRelease：运行后会生成所有渠道的 release 包  
> assembleBaiduRelease：运行后只会生成 baidu 的 release 包

**b.** 构建方式：

通过占位符 manifestPlaceholders 实现：

```
//AndroidManifest
<meta-data 
 android: value="Channel ID" 
 android:/>
 //build.gradle
 android{
 productFlavors{
 google{
  manifestPlaceholders.put("UMENG_ CHANNEL", "google")
 }
 baidu{
  manifestPlaceholders.put("UMENG_ CHANEL", "baidu")
 }
}

```

```
//改进：通过productFlavors批量修改
android{
 productFlavors{
  google{
 }
 baidu{
 }
 ProductFlavors.all{ flavor->
  manifestPlaceholders.put("UMENG_ CHANEL", name) 
 } 
}

```

**8. 高级应用**

**a.** 使用共享库

*   android sdk 库：系统会自动链接
    
*   共享库：独立库，不会被系统自动链接，使用时需要在 AndroidManifest 通过 <uses-library> 指定
    

```
//声明需要使用maps共享库，true表示如果手机系统不满足将不能安装该应用
<uses-library
 android:
 android:required="true" 
/>

```

*   add-ons 库：存于 add-ons 目录下，大部分由第三方厂商或公司开发，会被自动解析添加到 classpath
    
*   optional 可选库：位于 platforms/android-xx/optional 目录下，通常为了兼容旧版本的 API，使用时需要手动添加到 classpath
    

**b.** 批量修改生成的 apk 文件名

**<一>** 类型：

*   applicationVariants ：仅仅适用于 Android 应用 Gradle 插件
    
*   libraryVariants ：仅仅适用于 Android 库 Gradle 插件
    
*   testVariants ：以上两种 Gradle 插件都使用
    

**<二>** 示例：

![](https://mmbiz.qpic.cn/mmbiz_png/y5HvXaQmpqnP8H7tH8t8HjibIia0jPia9zZakFiacng4yicyROWmOILA2SlxdHWib0ickcd8VB0bTaZle76HwlSqwwKxg/640?wx_fmt=png)

> applicationVariants 是一个 DomainObjectCollection 集合，通过 all 方法遍历每一个 ApplicationVariant，这里有 googleRelease 和 googleDebug 两个变体；然后判断名字是否以. apk 结尾，如果是就修改其文件名。示例中共有。

**c.** 动态生成版本信息

*   原始方式：由 defaultConfig 中的 versionName 指定
    
*   分模块方式：把版本号等配置抽出放在单独的文件里，并用 ext{} 括起来，通过 apply from 将其引入到 build.gradle，版本信息就被当作扩展属性直接使用了
    
*   从 git 的 tag 中获取
    
*   从属性文件中动态获取和递增
    

**d.** 隐藏签名文件信息

**<一>** 必要性：为保证签名信息安全，最好直接放在项目中，而是放在服务器上

**<二>** 一种思路：

*   服务器：配置好环境变量，打包时直接使用
    
*   本地：直接使用 android 提供的 debug 签名
    
*   在 signingConfigs 加入以下判断
    

```
signingConfigs {
 if (System.env.KEYSTORE_PATH != null) {
  //打包服务器走这个逻辑
  storeFile file(System.env.KEYSTORE_PATH)
  keyAlias System.env.ALIAS
  keyPassword System.env.KEYPASS
  storePassword System.env.STOREPASS
 } else {
  //当不能从环境变量取到签名信息时，使用本地debug签名
  storeFile file('debug.keystore')
  storePassword 'android'
  keyAlias 'androiddebugkey'
  keyPassword 'android'
 }
}

```

**e. 动态添加自定义的资源**

**<一>** 针对 res/values 中的资源，除了使用 xml 定义，还可以通过 Android Gradle 定义

**<二>** 方法：resValue(String type, String name, String value)

*   type：资源类型，如有 string、id、bool
    
*   name：资源名称，以便在工程中引用
    
*   value：资源值
    

```
productFlavors{
 google{
  resValue 'string', 'channel_tips', 'google渠道欢迎你'
 }
}

```

以 google 为例，在 debug 模式下，资源文件保存目录：build/generated/res/resValues/google/debug/values/generated.xml

**f.**Java 编译选项

通过 compileOptions{} 闭包进行编译配置，可配置项：

*   encoding：配置源文件的编码
    
*   sourceCompatibility：配置 Java 源代码的编译级别
    
*   targetCompatibility：配置生成 Java 字节码的版本
    

```
android{
 compileOptions{
  encoding = 'utf-8'
  sourceCompatibility = JavaVersion.VERSI0N_ 1_ 6
  targetCompatibility = JavaVersion.VERSION_ 1_ 6
 }
}

```

**g.** adb 选项配置

通过 adbOptions{} 闭包进行 adb 配置，可配置项：

**<一>**timeOutInMs：设置执行 adb 命令的超时时间，单位毫秒

**<二>**installOptions：设置 adb install 安装设置项

*   -l：锁定该应用程序
    
*   -r：替换已存在的应用程序，即强制安装
    
*   -t：允许测试包
    
*   -s：把应用程序安装到 SD 卡上
    
*   -d：允许进行降级安装，即安装版本比手机自带的低
    
*   -g：为该应用授予所有运行时的权限
    

```
android{
 adbOptions{
  timeOutInMs = 5*1000
  installOptions '-r', '-s'
 }
}

```

**h.**DEX 选项配置

通过 dexOptions {} 闭包进行 dex 配置，可配置项：  

*   incremental：配置是否启用 dx 的增量模式，默认值为 false
    
*   javaMaxHeapSize：配置执行 dx 命令时为其分配的最大堆内存
    
*   jumboMode：配置是否开启 jumbo 模式
    
*   preDexLibraries：配置是否预 dex Libraries 库工程，默认值为 true，开启后会提高增量构建的速度
    
*   threadCount：配置 Android Gradle 运行 dx 命令时使用的线程数量
    

**近期文章：**

*   [谁的 Bug 指给了我？害我损失 5W 奖金！](http://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA==&mid=2247493470&idx=1&sn=64fbf58692dae20e06b0fefb487e07f9&chksm=96ce4613a1b9cf05e4c0c949268c9806fdcbaa11f145e498af218634e828b03bb48846430cbe&scene=21#wechat_redirect)  
    
*   [码妞：Java 那么多锁，能锁住灭霸吗？](http://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA==&mid=2247493379&idx=1&sn=5e62f81bdba537207bacd81746c4cbbc&chksm=96ce464ea1b9cf58844f19834a043bfba34d600c36ec39594ea17cdc7d2858079c0f3a9e61c7&scene=21#wechat_redirect)  
    
*   [谷歌遭反垄断调查；5G 牌照正式发放；IBM 大裁员；百度又一高管离职](http://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA==&mid=2247493301&idx=1&sn=fd2de6cea2139c1a7aa33e9a8af14a94&chksm=96ce47f8a1b9ceee854584061f67b5062f862ca995c6624d63e058b3acd470d5b2c636fa24eb&scene=21#wechat_redirect)  
    

**今日问题：**

腾讯大佬弥补了你的 Gradle 漏洞了吧？

![](https://mmbiz.qpic.cn/mmbiz_jpg/y5HvXaQmpqmabhASQxVGwlUcdAa4atCNMpcbQnPIReWb2YOAUa09pUMRL01UWOticgpaR6ktKMgIA73cy2tfv7g/640?wx_fmt=jpeg)

快来码仔社群解锁新姿势吧！[社群升级：Max 你的学习效率](http://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA==&mid=2247492550&idx=2&sn=516d9e3b892e391fe1949d19876a0def&chksm=96ce428ba1b9cb9d412961b605feed313d14f8849d9a9330e928bbe5ed7f0db4a502649d04fb&scene=21#wechat_redirect)  

![](https://mmbiz.qpic.cn/mmbiz_gif/y5HvXaQmpqmf7bkHkVgK5L4SicMQxPn7stXT6zIYSV77emjDPBgXXVoBQnVKQmooYJL8xwoib2yY7j5pq8VZlaew/640?wx_fmt=gif)