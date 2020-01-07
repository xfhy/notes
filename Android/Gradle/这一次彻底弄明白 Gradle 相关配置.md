> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/1UHcYOudViMhpUYeREZzGA

本文作者

作者：**19snow93**

链接：

https://www.jianshu.com/p/8b8a550246bd

本文由作者授权发布。

当毕业不够两年，身边的朋友慢慢得有车有房有女朋友周末有自己的节目，而我觉得很多美好的事情我都遥不可及，找不到可以让自己开心的事情做，心情很不好。但是同时我觉得沉淀需要时间、努力、耐心和自律，所以我总相信艰难的时候是总会过去，迎接自己的美好生活总到来的，给自己喊一句：“加油！”。

好了，牢骚发完了，最近我看了《Android Gradle 权威指南》这本书，虽然书上写的内容可能比较简单，但是对于 Android 开发人员来说应该还是比较够用的了。所以，今天我打算结合书上的知识和自己开发项目作为例子来总结一篇关于 Gradle 的知识基础要点。

_1_

初识 Gradle

Gradle 是一个基于 Apache Ant 和 Apache Maven 概念的项目自动化建构工具。它使用一种基于 Groovy 的特定领域语言来声明项目设置，而不是传统的 XML。当前其支持的语言限于 Java、Groovy 和 Scala，计划未来将支持更多的语言。

怎么看上面都是一段很官方的解释，对于入门的人来说简直是一个噩梦般的解释（包括以前的我）。那下面我就用通俗一点语言说说我的理解。

Gradle 就是工程的管理，帮我们做了依赖, 打包, 部署, 发布, 各种渠道的差异管理等工作。举个例子形容，如果我是一个做大事的少爷平时管不了这么多小事情，那 Gradle 就是一个贴心的秘书或者管家，把一些杂七杂八的小事情都帮我们做好了，让我们可以安心的打代码，其他事情可以交给管家管。

那有人会问，既然工作都可以交给他做，为什么还要我们去了解。我想我们要管家做事，也要下达我们的命令，我们必须知道这些命令和管家的喜好才能跟他相处和谐，不然你不知道它的脾性下错命令，那后果可是很严重的。

在以前实习的时候，我还用 eclipse，那是导入一个网上的下载的 module 还需要一步步的 import。但自从用了 Android Studio 后，Gradle 很贴心的帮我完成了这个繁杂的工作，而且往往只需要添加一句话，这太神奇了，当时我是这样想的，下面我们也会说到这个。

_2_

分析

下面我就用自己项目中用到的 Gradle 慢慢分析：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsUL3GJOBtBbkN4dZnQX8B7XVGs5t32sk1QurMrLyNIwialCiaz2vy7WHQ/640?wx_fmt=png)

我们看到，每个 Module 都会对应有一个 Gradle 文件，另外还有一个主 Project 的 Gradle 文件管理全局。下面我们先看看那个叫 gradle-wrapper.properties 的文件：  

_3_

gradle-wrapper

Wrapper 是对 Gradle 的一层包装，便于在团队开发过程中统一 Gradle 构建的版本号，这样大家都可以使用统一的 Gradle 版本进行构建。

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrssDjArdZZiaL5mdQJGicYcvLbDDLzPs0NTu4rMTsXt5eOp69CibsVTG5ow/640?wx_fmt=png)

上面我们看到的图就是 Gradle 提供内置的 Wrapper task 帮助我们自动生成 Wrapper 所需的目录文件。再看看我们 Android 项目里面自动生成的文件

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrs5UtBbUSYHt7Hh0P2AW6OicicX5AZI0gujic8MjtSRphPgknqYLZf30qpw/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsxWozBFb3k1NiaHxB2GUjBlC41gXa1CEFshx6klOhDo0y64bu94lcXicg/640?wx_fmt=png)

终于，我们知道这几个自动生成的文件原来是 Gradle Wrapper 创建出来的。

那下面我们看看 gradle-wrapper.properties 这个文件的作用

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrs0krjNmznbEhHZR6owFDCrqnnHV6o4ZCPgANztYxV04m8V2Yh47sTSA/640?wx_fmt=png)

看到项目里面的各个属性，下面再看看每个属性的作用

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsaFS8a1JbHmmcKhfLeI2B4LAiaMKtpUYOtsFuXZP9GKClptFu6R53FGQ/640?wx_fmt=png)

我们其实最关心的应该是 distributionUrl 这个属性，他是下载 Gradle 的路径，它下载的东西会出现在以下的文件夹中  

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsYR1GGUqU68FFptYrFlQGZ2G8FC0ZBR81WY6K1fVFNicFCB75L5WCYHQ/640?wx_fmt=png)

看到了吧，这个文件夹包含了各个版本你下载的 Gradle。

当我是初学者的时候老是会遇到一个问题，那就是下图：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsEAibhAtMFogB3cMQMglwNdeTY4uWV0ficIPG9ibkgvAomjGuCARVXGeMg/640?wx_fmt=png)

导入项目的时候一直会停留在这个界面，这是为什么？其实原因很简单，就是你常用项目的 Gradle 版本跟你新导入项目的 Gradle 版本不一致造成的，那怎么解决？我本人自己是这么做的：

1.  在能访问的情况下 ，由它自己去下载，不过下载时间有长有短，不能保证。
    
2.  当你在公司被限网速的时候，当然也是我最常用的，就是把你最近常用项目的 gradle-wrapper.properties 文件替换掉你要导入项目的该文件，基本上我是这样解决的，当然有时候也会遇到替换掉报错的情况，不过比较少。
    

_4_

settings.gradle

下面我们讲讲 settings.gradle 文件，它其实是用于初始化以及工程树的配置的，放在根工程目录下。

设置文件大多数的作用都是为了配置自工程。在 Gradle 众多工程是通过工程树表示的，相当于我们在 Android Studio 看到的 Project 和 Module 概念一样。根工程相当于 Android Studio 的 Project，一个根工程可以有很多自工程，也就是很多 Module，这样就和 Android Studio 定义的 Module 概念对应上了。

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsCKzEkB287ymbsiaPTOVzhPWkExAFR9M1JGCxBxiayRleNK2cgudsVMBw/640?wx_fmt=png)

我们可以看到这个项目我们添加了 7 个 module，一一对应，如果你的项目添加了项目依赖，那就会出现在这个文件当中。

好了，我们说完 settings.gradle 文件之后就慢慢进入其他文件了，但是首先我们要解释一下什么是 Groovy：

**Groovy**
----------

Groovy 是基于 JVM 虚拟机的一种动态语言，它的语法和 Java 非常相似，由 Java 入门学习 Groovy 基本没有障碍。Groovy 完全兼容 Java，又在此基础上增加了很多动态类型和灵活的特性，比如支持密保，支持 DSL，可以说它就是一门非常灵活的动态脚本语言。

一开始我总把 Gradle 和 Groovy 搞混了，现在我总把他们的关系弄清楚了。Gradle 像是一个软件，而 Groovy 就是写这个软件的语言，这就很简单明了吧。那下面我们说到的内容都是用 Groovy 语法写的，但是这个知识点我就暂时不科普了，有兴趣的小伙伴可以去了解一下更深入的 Groovy 语法。

_5_

build.gradle（Project）

下面我们就来讲讲主的 build.gradle 文件：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsTXUzfiaziczC5B9licfbeVagwKaSwsJtD16tCEa39K0qAD9lJonyviaU8g/640?wx_fmt=png)

我们这里，分为四个标签来讲：

### 1.buildscript

buildscript 中的声明是 gradle 脚本自身需要使用的资源。可以声明的资源包括依赖项、第三方插件、maven 仓库地址等

### 2.ext

ext 是自定义属性，现在很多人都喜欢把所有关于版本的信息都利用 ext 放在另一个自己新建的 gradle 文件中集中管理，下面我介绍一下 ext 是怎么用的：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsu8gCcs6I8yqicAtaCsBK6WACjnib0PXJOxxZPZmFL2GxGJmNRbickUu2A/640?wx_fmt=png)

1. 首先我们新建两个文件，分别叫 build.gradle 和 version.gradle

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsoqpY4mmBf6fKJ7vATtNQRvDeCJicHReyOupSiaDxZHK47cibHLSBM43RA/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsYaKYmT1tttbtm3tI7AicErlpE67cOiarGBzgjOINY8766mgdVw716kKQ/640?wx_fmt=png)

2. 然后分别在两个文件中打上相应的代码

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrs1evRPu89ic8zD6yibtu4HStZe0apAribbF1rAr0wceYicoHnoAeVQawJibQ/640?wx_fmt=png)

3. 最后在 Android Studio 的 Terminal 移动到相应的文件夹中运行 task。

我们可以很神奇的发现，当我们在 build.gradle 文件中输入了 apply from:'version.gradle'这句话，我们就可以读取到该文件下 ext 的信息。

现在在项目中我也是这种方法统一管理所有第三方插件的版本号的，有兴趣的朋友也可以试试。

### 3.repositories

顾名思义就是仓库的意思啦，而 jcenter()、maven() 和 google() 就是托管第三方插件的平台

### 4.dependencies

当然配置了仓库还不够，我们还需要在 dependencies{} 里面的配置里，把需要配置的依赖用 classpath 配置上，因为这个 dependencies 在 buildscript{} 里面，所以代表的是 Gradle 需要的插件。

下面我们再看看 build.gradle（Project）的另一部分代码

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsHkXYkwO2NzxLIjgfRG1byNcrWQ6Bbg2NjeWtlcoxibeHH5nzedYvyjg/640?wx_fmt=png)

### allprojects

allprojects 块的 repositories 用于多项目构建，为所有项目提供共同所需依赖包。而子项目可以配置自己的 repositories 以获取自己独需的依赖包。

奇怪，有人会问，为什么同一个 build.gradle（Project）文件中 buildscript 和 allprojects 里面的内容基本上是一样的呢，他们的区别在哪？

### **buildscript 和 allprojects 的作用和区别**

buildscript 中的声明是 gradle 脚本自身需要使用的资源，就是说他是管家自己需要的资源，跟你这个大少爷其实并没有什么关系。而 allprojects 声明的却是你所有 module 所需要使用的资源，就是说如果大少爷你的每个 module 都需要用同一个第三库的时候，你可以在 allprojects 里面声明。这下解释应该可以明白了吧。

好了，下面该说说 build.gradle（Project）文件的最后一个一段代码了

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrs3VzAl3Hhwwiajal7Lomh4j0pNAAd0tjq9EGMdhp7ibqCkiba4EqCBsWicA/640?wx_fmt=png)

运行 gradle clean 时，执行此处定义的 task。该任务继承自 Delete，删除根目录中的 build 目录。相当于执行 Delete.delete(rootProject.buildDir)。其实这个任务的执行就是可以删除生成的 Build 文件的，跟 Android Studio 的 clean 是一个道理。

_6_

build.gradle（Module）

讲完 Project 的 build 文件，就来讲讲最后也是内容最多的文件了。

### **apply plugin**

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrs1ntLv9ykpodcDJzfXXnQCSdOSAvibOicndByeVWy9trADS74SKAgM4ng/640?wx_fmt=png)

#### 首先要说下 apply plugin：'×××'

这种叫做引入 Gradle 插件，而 Gradle 插件大致分为分为两种：

1. apply plugin：'×××'：叫做二进制插件，二进制插件一般都是被打包在一个 jar 里独立发布的，比如我们自定义的插件，再发布的时候我们也可以为其指定 plugin id，这个 plugin id 最好是一个全限定名称，就像你的包名一样；

2. apply from：'×××'：叫做应用脚本插件，其实这不能算一个插件，它只是一个脚

本。应用脚本插件，其实就是把这个脚本加载进来，和二进制插件不同的是它使用的是 from 关键字. 后面紧跟的坫一个脚本文件，可以是本地的，也可以是网络存在的，如果是网络上的话要使用 HTTP URL.

  
虽然它不是一个真正的插件，但是不能忽视它的作用. 它是脚本文件模块化的基础，我们可以把庞大的脚本文件. 进行分块、分段整理. 拆分成一个个共用、职责分明的文件，然后使用 apply from 来引用它们，比如我们可以把常用的函数放在一个 Utils.gradle 脚本里，供其他脚本文件引用。示例中我们把 App 的版本名称和版本号单独放在一个脚本文件里，清晰、简单、方便、快捷. 我们也可以使用自动化对该文件自动处理，生成版本。

#### **说说 Gradle 插件的作用**

把插件应用到你的项目中，插件会扩展项目的功能，帮助你在项目的构建过程中做很多事情。

1. 可以添加任务到你的项目中，帮你完成一些亊情，比如测试、编译、打包。

2. 可以添加依赖配置到你的项目中，我们可以通过它们配置我们项目在构建过程中需要的依赖. 比 如我们编译的时候依赖的第三方库等。

3. 可以向项目中现有的对象类型添加新的扩展属性、 方法等，让你可以使用它们帮助我们配置、优化构建，比如 android{} 这个配置块就是 Android Gradle 插件为 Project 对象添加的一个扩展。

4. 可以对项目进行一些约定，比如应用 Java 插 件之后，约定 src/main/java 目录下是我们的源代码存放位置，在编译的时候也是编译这个目录下的 Java 源代码文件。

#### **然后我们说说'com.android.application'**

Android Gradle 插件的分类其实是根据 Android 工程的属性分类的。在 Andriod 中有 3 类工程，一类是 App 应用工程，它可以生成一个可运行的 apk 应用：一类是 Library 库工程，它可以生成 AAR 包给其他的 App 工程公用，就和我们的 Jar 一样，但是它包含了 Android 的资源等信息，是一个特殊的 Jar 包；最后一类是 Test 测试工程，用于对 App 工程或者 Library 库工程进行单元测试。

1.  App 插件 id：com.android.application.
    
2.  Library 插件 id：com.android.library.
    
3.  Test 插件 id：com.android.test.
    

一般一个项目只会设置一个 App 插件，而 module 一般是会设置为 Library 插件。

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsSqnKwvjVkueMbkuU9t8fgkspR1iaHXSY8aQA9f4SUoOmUMZ45cevJyA/640?wx_fmt=png)

#### android{}

是 Android 插件提供的一个扩展类型，可以让我们自定义 Android Gradle 工程，是 Android Gradle 工程配置的唯一入口。

#### compileSdkVersion

是编译所依赖的 Android SDK 的版本，这里是 API Level。

#### buildToolsVersion

是构建该 Android 工程所用构建工具的版本。

#### defaultConfig{}

defaultConfig 是默认的配置，它是一个 ProductFlavor。ProductFlavor 允许我们根据不同的情况同时生成多个不同的 apk 包。

#### applicationId

配置我们的包名，包名是 app 的唯一标识，其实他跟 AndroidManifest 里面的 package 是可以不同的，他们之间并没有直接的关系。

package 指的是代码目录下路径；applicationId 指的是 app 对外发布的唯一标识，会在签名、申请第三方库、发布时候用到。

#### minSdkVersion

是支持的 Android 系统的 api level，这里是 15，也就是说低于 Android 15 版本的机型不能使用这个 app。

#### targetSdkVersion

表明我们是基于哪个 Android 版本开发的，这里是 22。

#### versionCode

表明我们的 app 应用内部版本号，一般用于控制 app 升级，当然我在使用的 bugly 自动升级能不能接受到升级推送就是基于这个。

#### versionName

表明我们的 app 应用的版本名称，一般是发布的时候写在 app 上告诉用户的，这样当你修复了一个 bug 并更新了版本，别人却发现说怎么你这个 bug 还在，你这时候就可以自信的告诉他自己看下 app 的版本号。（亲身经历在撕逼的时候可以从容的应对）

#### multiDexEnabled

用于配置该 BuildType 是否启用自动拆分多个 Dex 的功能。一般用程序中代码太多，超过了 65535 个方法的时候。

#### ndk{}

多平台编译，生成有 so 包的时候使用，包括四个平台'armeabi', 'x86', 'armeabi-v7a', 'mips'。一般使用第三方提供的 SDK 的时候，可能会附带 so 库。

#### sourceSets

源代码集合，是 Java 插件用来描述和管理源代码及资源的一个抽象概念，是一个 Java 源代码文件和资源文件的集合，我们可以通过 sourceSets 更改源集的 Java 目录或者资源目录等。

譬如像上图，我通过 sourceSets 告诉了 Gradle 我的关于 jni so 包的存放路径就在 app/libs 上了，叫他编译的时候自己去找。

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsLsHibuY5CHIO7vm7zh4FeZ9PK1DObxj1xwuwx0UodKWQVoUnTicdSZfQ/640?wx_fmt=png)

```
name：build type的名字
applicationIdSuffix：应用id后缀
versionNameSuffix：版本名称后缀
debuggable：是否生成一个debug的apk
minifyEnabled：是否混淆
proguardFiles：混淆文件
signingConfig：签名配置
manifestPlaceholders：清单占位符
shrinkResources：是否去除未利用的资源，默认false，表示不去除。
zipAlignEnable：是否使用zipalign工具压缩。
multiDexEnabled：是否拆成多个Dex
multiDexKeepFile：指定文本文件编译进主Dex文件中
multiDexKeepProguard：指定混淆文件编译进主Dex文件中


```

buildType  

构建类型，在 Android Gradle 工程中，它已经帮我们内置了 debug 和 release 两个构建类型，两种模式主要车别在于，能否在设备上调试以及签名不一样，其他代码和文件资源都是一样的。一般用在代码混淆，而指定的混淆文件在下图的目录上，minifyEnabled=true 就会开启混淆：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsHT4mD8JpWt4xs65L50bWEWQZfglVDwn24yZcdPx7xPyvuLjwle0pLA/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrs2Mvwu2sgV6EMgbf6vGSpTW8xpIcxUSrSvvTicqYfcGmSUokClPibt2YA/640?wx_fmt=png)

#### signingConfigs

签名配置，一个 app 只有在签名之后才能被发布、安装、使用，签名是保护 app 的方式，标记该 app 的唯一性。如果 app 被恶意删改，签名就不一样了，无法升级安装，一定程度保护了我们的 app。而 signingConfigs 就很方便为我们提供这个签名的配置。storeFile 签名文件，storePassword 签名证书文件的密码，storeType 签名证书类型，keyAlias 签名证书中秘钥别名，keyPassword 签名证书中改密钥的密码。

默认情况下，debug 模式的签名已经被配置好了，使用的就是 Android SDK 自动生成的 debug 证书，它一般位于 $HOME/.android/debug.keystore, 其 key 和密码是已经知道的，一般情况下我们不需要单独配置 debug 模式的签名信息。

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsWU4Wc4VVb9fHuFsfV7xAia7iaQrHONJRGnYx6uDrEH7feCnx7iawdxHicQ/640?wx_fmt=png)

#### productFlavors

在我看来他就是 Gradle 的多渠道打包，你可以在不同的包定义不同的变量，实现自己的定制化版本的需求。

#### manifestPlaceholders

占位符，我们可以通过它动态配置 AndroidManifest 文件一些内容，譬如 app 的名字：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsDm8OwwBS51RUAdc2Fh4YB4Q61MlzxP1raDpzv8PqmWE6LtoVrnXpwg/640?wx_fmt=png)

看看上图，我们就能发现我们在 productFlavors 中定义 manifestPlaceholders = [APP_NAME: "(测试)"] 之后，在 AndroidManifest 的 label 加上 "${APP_NAME}", 我们就能控制每个包打出来的名字是我们想要不同的名字，譬如测试服务器和生产服务器的包应该名字不一样。

#### buildConfigField

他是 BuildConfig 文件的一个函数，而 BuildConfig 这个类是 Android Gradle 构建脚本在编译后生成的。而 buildConfigField 就是其中的自定义函数变量，看下图我们分别定义了三个常量：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsF9AqMStkHKsAMdyGicdFQuzMAicm1BWX8BujUtaJVyAj5MUM32dtOBFw/640?wx_fmt=png)

我们可以在 BuildConfig 文件中看到我们声明的三个变量

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsuO3QvyTaiaR8yofa9WwIbPnpp4nMspibJhLuyL1amMibQ7yevy49p8ACQ/640?wx_fmt=png)

然后我们就可以在代码中用这些变量控制不同版本的代码：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsusib5M0OubZvcdRWnZjpmVHMdp15YsNxCferqIZLdhCKBk2yL6QCQoQ/640?wx_fmt=png)

我们这样加个 if，就可以轻轻松松的控制测试和生产版本付费的问题了，再也不用手动的改来改去了，那问题来了，我怎么去选择不同的版本呢，看下图：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrskpJqPdPV485oYqKvRpzBp5GQBezMiaW4Tl8q6ictosOQHe8eyZ7rq6TQ/640?wx_fmt=png)

如果你是 Android Studio，找到 Build Variants 就可以选择你当前要编译的版本啦。

#### flavorDimensions

顾名思义就是维度，Gradle3.0 以后要用 flavorDimensions 的变量必须在 defaultConfig{} 中定义才能使用，不然会报错：

```
Error:All flavors must now belong to a named flavor dimension.
The flavor 'flavor_name' is not assigned to a flavor dimension.


```

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrs05coyvc1cmeQT3ZsBCbEIicAUFdHmrvpaK6ttBhyy7N6UT9FicYFRDGw/640?wx_fmt=png)  

这样我们就可以在不同的包中形成不同的 applicationId 和 versionName 了。

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsmcw0f7XUGoufYbtOEn59IvuTSib0RUKicKM4jticGBSXk2dNhPFnv1rlw/640?wx_fmt=png)

#### dexOptions{}

我们知道，Android 中的 Java 源代码被编译成 class 字节码后，在打包成 apk 的时候  
被 dx 命令优化成 Android 虚拟机可执行的 DEX 文件。

DEX 文件比较紧凑，Android 费尽心思做了这个 DEX 格式，就是为了能使我们的程序在 Android 中平台上运行快一些。对于这些生成 DEX 文件的过程和处理，Android Gradle 插件都帮我们处理好了，Android Gradle 插件会调用 SDK 中的 dx 命令进行处理。

但是有的时候可能会遇到提示内存不足的错误，大致提示异常是

java,lang.OutOfMemoryError: GC overhead limit exceeded, 为什么会提示内存不足呢？

 其实这个 dx 命令只是一个脚本，它调用的还是 Java 编写的 dx.jar 库，是 Java 程序处理的，所以当内存不足的时候，我们会看到这个 Java 异常信息. 默认情况下给 dx 分配的内存是一个 G8, 也就是 1024MB。

所以我们只需要把内存设置大一点，就可以解决这个问题，上图我的项目就把内存设置为 4g。

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsGDwwUnoGXKmRArx3Hxux5ZXI4RmmpEo4WtkArudsaiaaiaHlSTIZKPyg/640?wx_fmt=png)

### dependencies{}

我们平时用的最多的大概就这个了，

1. 首先第一句 compile fileTree(include: ['.jar'], dir: 'libs')*，这样配置之后本地 libs 文件夹下的扩展名为 jar 的都会被依赖，非常方便。

2. 如果你要引入某个本地 module 的话，那么需要用 compile project('×××')。

3. 如果要引入网上仓库里面的依赖，我们需要这样写 compile group：'com.squareup.okhttp3',name:'okhttp',version:'3.0.1', 当然这样是最完整的版本，缩写就把 group、name、version 去掉，然后以 ":" 分割即可。  
compile 'com.squareup.okhttp3:okhttp:3.0.1'

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsuwGcAT0bvUXroJV0kbDbSsyW73iay33hBKlwQ9wjdM5icmKWtzcMwoEg/640?wx_fmt=png)

但是到了 gradle3.0 以后 build.gradle 中的依赖默认为 implementation，而不是  
之前的 compile。另外，还有依赖指令 api。

  
那么下面我们就来说说：

### **gradle 3.0 中依赖 implementation、api 的区别：**

其实 api 跟以前的 compile 没什么区别，将 compile 全部改成 api 是不会错的；  
而 implementation 指令依赖是不会传递的，也就是说当前引用的第三方库仅限于本 module 内使用，其他 module 需要重新添加依赖才能用，下面用两个图说明：

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrs0dw4Fzk2Q0qSKSUT4Jn05A7ATv5SlAxjcsClNBgeSSTYebh4Uvhkyw/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOicnicP5NDOk5rdQLo4lRmrsh3MII2Y2SLnknpXaZW0uBuDd6vibos27T1YmB9kFk4MY1cZiaZA8f9rQ/640?wx_fmt=png)

相信看过图的人都会一目明了。

  
好了，这期内容写得差不多了，如果上面内容有什么错漏的话欢迎大家给我提出。虽然才讲了 gradle 几个文件，但是感觉有些小众内容还没写出来，如果后面有需要，我会补上的。今天就讲这么多了，下期再见！

我的掘金

_https://juejin.im/user/594e8e9a5188250d7b4cd875_

推荐阅读：  

[阿里春招 Android 面经](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650825487&idx=1&sn=ac0ef79172cc9054dea7a281e3fb8a15&chksm=80b7b791b7c03e87992b1da000ab0e1ddddd7c55ad3895e0a3efbc985adce49cf63a6f0c5183&scene=21#wechat_redirect)  

[我们要不要上线个人 app？](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650825468&idx=1&sn=107a3089d4ea74693c212d2320724559&chksm=80b7b662b7c03f74bbc8936e9f3a113c2a65f87762137add226938a87e3421d1c92f16b32ebd&scene=21#wechat_redirect)  

![](https://mmbiz.qpic.cn/mmbiz_jpg/MOu2ZNAwZwP4yDt9RiaN89t9lxTz0vZWZy9sYR54YefTFFBPmPLwnAN9PNicI0rZznIYt4r2Q40DbAAiatTS1MlVw/640?wx_fmt=jpeg)

如果你想要跟大家分享你的文章，欢迎投稿~