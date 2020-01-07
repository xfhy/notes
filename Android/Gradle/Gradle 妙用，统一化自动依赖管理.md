> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/Lhae-ou5gEcXjUFnuVCwaw

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6FSn51QbdP1ic92cjsQM7LkE1CHaFMjMuwuvk5aHV43DjI5QRia5rU0DDUrXldEI1t6d6VjDg0mThw/640?wx_fmt=png)  

今日科技快讯

小米集团公告，1 月 18 日斥资约 1 亿港元回购 984.96 万 B 类普通股；小米于 17 日回购 614 万股 B 类股，耗资约 6000 万港元。在一年的限售期到期之后，更多的股票将在今年 7 月份解锁出售，而小米的一些控股股东（包括雷军）本月表示，他们将继续持有一年的股份。

作者简介

本篇来自 **Young 方远** 的投稿文章。文章对高效的 dle variant 使用知识进行了不错的讲解，希望对大家有所帮助。

**Young 方远 **的博客地址：

> https://blog.csdn.net/qxf5777404

正文

如果你也在做着同一套代码，构建多个项目的需求，那么一定要浏览下，或许会带给你启发. 清晰化的目录结构，统一化的自动依赖管理。

入坑以来一直和 variant 打着交道，最初 15 年还是 eclipse 开发，那是还没 variant 概念。当时的项目是企业级 app 开发，简而言之就是一套代码针对不同企业构建其对应请求地址的包，当然不同企业也会有差异化的需求但大致的业务流程都差不多。(ps: 如果是你，你会怎么做？一家企业一套代码？这种想法最初就被否了，因为企业多了，以后增加 / 修改公共需求都是问题，绝对让你崩溃!)。所以当时的处理只是从代码层面来区分的，当然劣势也有，可能会增加包的大小。再往后推，谷歌爸爸推出了 AS 正式版，果断拥抱。再后来，了解到了 Gradle 的 Flavor 配置。可以根据 Flavor 从项目结构上选择依赖文件。这不就完美从项目配置上满足了我的需求么。随后就是更改重构的过程~ 以下只是我的经验心得，献给可能需要的你。

**重要性**

无论是从项目的健壮性还是可维护性来说好的开发工具搭配项目架构能让你事半功倍。

统一化管理：我一直有统一配置的习惯, 无论是代码的配置还是 gradle 的依赖。这样在项目变动的时候，更改一处即可。想想，假如不这么做，漏该了一处，什么结果...

**项目结构**

> productFlavors: 相信大家都了解，用于构建变体。我们可以用来打多渠道包，也可以做一些资源 / 代码差异化变更。这里我们将充分的依靠它完成后续工作！

*   **差异化的实现有两种方式**
    

首先我创建了两个 Flavor:

```
 productFlavors {
      variant_a {
          applicationId "com.joe.variant_a"
      }
      variant_b {
          applicationId "com.joe.variant_b"
      }
  }


```

**第一种**

直接在 app-module 的 src 下建立对应的 flavor 名相同的文件夹，并创建相应的 java/res 文件即可。

如图: 

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvK6Zv0tD4wWlvAwurO7Oe8H70icBibPQKxuXdPyYZicOlgX7DsTSJBibXGg/640?wx_fmt=png)

这种是常见的目录结构。也满足了大多的需求，差异化的主题色 / 代码等都满足了。但是对于想大做文章的就不一定满足需求了，比如:

*   依赖性差异
    

项目 A 需要添加实现一个扫码功能，而 B 则不需要。见多识广的可能说，创建扫码的 module 可以用 aImplementation 来选择性依赖，不错可以解决。起初我也这么做的。那么问题又来了: B 需要依赖支付组件而 A 又不需要，行呗，接着 bImplementation 呗，有没有想过如果你的 Flavor 不止这两个有 N 多个，而差异化的依赖又有 N 项... 再这么在 dependencies {...} 中写下去，看着多痛苦啊。

*   业务性差异
    

同一个模块，A 和 B 却是不同的业务那你会怎么做? if else 来判断? 然后在 app 中处理? 那么问题来了: 这种需求贼多，而且不小 (一个方法搞不定) 也不大(用组件化区分又没必要)。那怎么办? 我的想法: 运用设计模块，区分不同的 Flavor，接口抽象业务处理方法。app 层差异性业务触发接口。Flavor 层来各自处理。(ps: 由于时间关系，Sample 中未做此方案，后续会添加。但我觉得大家都能理解，也可能有更好的解决方法。

**第二种**

将 Flavor 作为 module 来处理

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgv3cmYrsyHNHzW7qLm04yqn3PvRZXG0nSZCega3QufcqoiciaeQRd7F4EA/640?wx_fmt=png)

看图可以清晰的大致看出: app 负责主要的业务包装职能。base: 基准的依赖。extralib: 三方的依赖。variants: 我们所需的变体项目。每个变体依赖各自所需的组件。并处理各自的业务。接下来就是要让它能够统一化自动依赖的管理了。

**大功臣 Groovy**

首先思考一下我们的理想效果: 在 vatiants 中进行各自的配置后。无论是 debug 还是 release 都能根据当前编译的 flavor 来自动将其添加为 app 的依赖，并找到对应的配置文件和 keystore 来编译，也就是我们首先就是要知道如何才能拿到当前编译的 flavor。很遗憾 Gradle 并没有提供该方法，但感谢 stackoverflow 上的大神提供思路，我们可以通过其他方法获得。

*   **拿到 currentFlavor**
    

```
  我们在项目的gradle中写下该语句:
  Gradle gradle = getGradle()
  String taskName = gradle.getStartParameter().getTaskNames()[0]
  println "taskName : " + taskName


```

运行后我们可以看到日志:

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvTkuIOXLkicmu0tgvIyGmMLIqqhDuEWv0bgvdaQAicic9Xbic4gMycsR4mA/640?wx_fmt=png)

然后我们，针对当前编译的 taskName 处理下。就拿到了 currentFlavor:variant_a.

运行。这里要说下运行如何看打印的日志。

debug 情况下 (也就是直接运行项目):

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvFTKicpicTWv0Y0C4FkKWyiahEVNKUFqbsv5cJRjG55CriaGfktibzPSTjFQ/640?wx_fmt=png)

assemble 情况下 (命令行选择构建情况下): 可以直接看到  

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvT8mtcAhZ1ibdyU1S2zdYSusLWbWKEJG6Os3rpM5nibHVAibqAUSZek9EQ/640?wx_fmt=png)

为了能够根据 currentFlavor 自动化获取配置路径 先看下 Flavor 中的配置:

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvIiaxZ01MlLBLwfGTdVAH7RJIicVPWHvhiaBib08nFMSloATuxppbHqiaVpw/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvExkh2U9Ns1zEI7UcguyAuUjia9hRZrEJia2iakLEib518ibiaJuwGWtk6Rsg/640?wx_fmt=png)

可以看到，我们在每个 Flavor 中都设置了其私有的 appConfig 文件。在里面定义了其对应的 app 信息。和三方依赖不得不在 gradle 中定义的参数 (不必要的直接在 java 文件. AppConfig 中定义)。这里说下如何引用 appConfig. 很简单，直接在对应的 build.gradle 中直接 apply from: 即可。但我们要实现自动化，所以就要动态化路径。

```
//获取对应variant目录下的私有特殊appConfig.gradle文件
apply from: String.format('../variants/%1$s/appConfig.gradle', rootProject.ext.currentFlavor)

```

这样我们就可以动态的拿到当前编译的 Flavor 的 appConfig。这样就可以愉快的配置了。

*   ### **动态配置项目参数**
    

### ![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvsxC4JAK85guH2puDSMF2tNgzB3sIVeFzicVhg9zKV5opDrxZKqjicW9w/640?wx_fmt=png)

### ![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvTIRkGNpgW11Tqmb1UtZo6YjFG30eHzNGEsEcGibK8wAYhiaNnuL0EQcA/640?wx_fmt=png)

### 这里简单解释一下: rootProject.ext 获取的是根 module 下 gradle 内的 ext 中定义的参数 this.ext 则指的是引用的 appConfig。而且大家也可以看到我很省事儿的直接将应该在 Flavor 中定义的 versionCode.versionName.SERVER_URL 都直接写在了 defaultConfig 中了。只有 applicationId 还在 Flavor 中定义 (ps.applicationId 不能在 defaultConfig 中, debug 编译有可能会出现先后编译错误. 可在项目中查看)。

*   ### **动态化依赖 Flavor**
    

很关键的一步: 直接在 dependencies 中即可，以后随便你加多少 Flavor 都不需要再维护。

```
 implementation project(':variants:' + rootProject.ext.currentFlavor)


```

### 动态化签名文件配置

### ![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvnbJDiaUnKW9lhCoaDR7FKxib65ee85QVdqSgCTbNfSnQmNNCHp9krSwA/640?wx_fmt=png)

### ![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvZj6y9ugp6yvJcwzJuTlpAITfU1fckoF3mCKL2m7R2lZicQJVibQx7vww/640?wx_fmt=png)

每个 Flavor 有自己的签名。这样才能...(你懂得) 下面配置 Flavor 自己的签名 (比较重要，请自行睁大眼查看~ ) 首先，让 gradle 可以自动引入对应 Flavor 的签名：  

```
  //获取当前编译flavor对应路径下的sign
  final String signPath = String.format('../variants/%1$s/keystore/signing.properties', rootProject.ext.currentFlavor)
  final String keyPath = String.format('../variants/%1$s/keystore/key', rootProject.ext.currentFlavor)


```

然后，需要为其配置独立的签名验证：

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvNqT3tsA22mwmmNjicSSkt2LU8JNmVMkRNJLk8A90rrMV3gBt6hZLg7g/640?wx_fmt=png)

*   **三方依赖的特殊处理**
    

普通的依赖，我们正常操作就可以了。但是有些特殊的三方需要在 Manifest 内定义 appId(比如: QQ 分享).。好，我们看下如何处理: 前面可以看到，我们在 appConfig.gradle 中已经设置了 QQ_APP_ID，现在我们在 tencent 的 module 中获取到。

```
//获取对应variant目录下的私有特殊appConfig.gradle文件
apply from: String.format('../../variants/%1$s/appConfig.gradle', rootProject.ext.currentFlavor)

//然后給Manifest设置占位符
manifestPlaceholders = [QQ_APP_ID: this.ext.tencent.QQ_APP_ID]


```

然后在 tencent 的 Manifest 中: 

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvsUW406ln1uXxMB5VFbAtkQUESGtXwt3hhvMN87GBfX4zWS3pPypEug/640?wx_fmt=png)

这样就依旧可以保证全局只依赖 appConfig 中的 QQ_APP_ID。

*   ### **创建 Flavor 的 AppConfig 文件**
    

现在基本完成了，配置工作。就差 Flavor 数据的设置了。剩下的使用我想就不用我说了吧~(ps: 自行查看路径)

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvIgGibgyGnh0V3VUTQTX15heFMpffz8Wg5BSVQCBQZx9YmgeymH1Z3EA/640?wx_fmt=png)

Tip

1.  构建: 必须构建单一 Flavor 才行。直接 assembleRelease 是不行的，拿不到 currentFlavor(如有大神知道欢迎告知)。
    
2.  虽然是完工了，但是一路走下来坑还是略多。加班花了个通宵才完工。你需要一些 Groovy 的基本语法，比如闭包的特性，以及路径的获取 .\: 当前目录之下, ..\: 上级目录之下。
    
3.  在使用的时候也有很多需要注意的地方。比如 flavor 的命名和 Flavor 的包名应一样等一些问题。
    
4.  Sample 中我创建了完整的项目，后续也会更新维护。里面也详细了注释。欢迎查看，建议。
    

项目地址为:

> https://github.com/Young-Joe/VariantsSample

附图为我实现的 Sample 来打出的两个差异性 Release 包:

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvlgE6micmQRFdfltQqNCXaxkYucTiaRXgUSdDq3bQkUvl5rZOCAnr3PcQ/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt4PqLcxOTQk9PX5PhOBuTgvZM0vU0TjyE81Kd7j3MjSD1l8MHcrz30aPNkNbt5zfibg2LicEYWA7IcQ/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt7MfKcpYNP8NWww77qGsiaVyItO9rBWQQ4adYibkvh6pibg2icoic2OAeyribR5nPlibuBWgsY4P1PcwntFA/640?wx_fmt=jpeg)