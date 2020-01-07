> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/_CahiMe8A6m40TI-iiP9kw

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5u4qlRDV7JCPTlhLruwgCE7IwG1NBPXaJxfvDKvhCWSDe0PMuVbEb1pavYAiaAjbELXcNC1UPzbJw/640?wx_fmt=jpeg)

/   今日科技快讯   /

6 月 12 日，在特斯拉 2019 年年度股东大会召开之际，正值这家电动汽车制造商处于关键性的历史时刻。对于特斯拉的投资者来说，过去的一年就像坐过山车一样，特斯拉股价最高升至每股 387.46 美元，最低跌至每股 176.99 美元。特斯拉首款大众型电动汽车 Model 3 就处于这些剧烈波动的中心。

/   作者简介   /

本篇文章来自 GitLqr 的投稿，分享了如何利用 gradle 来实现多渠道配置，相信会对大家有所帮助！同时也感谢作者贡献的精彩文章。

GitLqr 的博客地址：

> https://juejin.im/user/58a53faf5c497d005fa78737

/   新增渠道   /

使用 AndroidStudio 配合 gradle，可以很方便的输出多个渠道包，只需要在 app Module 下的 build.gradle 中，对 productFlavors 领域进行配置即可，假设我当前开发的项目，需要上线不同的地区，一个是国内版，一个美国版，还有一个免费版，那么 gradle 可以这么配：

```
android {
    productFlavors {
        china { // 中国版
        }
        america { // 美国版
        }
        free { // 免费版
        }
    }
}


```

以上多渠道配置完成后，在 Android Studio 的 Build Variants 标签中，就会有不同渠道变体供我们选择了。当我们想使用 AS 直接运行某个渠道的 app 时，就需要先在 Build Variants 标签中选择好变体，再点击 "运行" 按钮运行项目。

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwibV6eibcV5ibYADd8iafUWT5RA8QaYXHcCw6AJ93kkpfsez2lMWia14sJfQ/640?wx_fmt=png)

在 productFlavors 中还可以配置包名（applicationId）、版本号（versionCode）、版本名（versionName）、icon、应用名 等等，举个例子：

```
free {
    applicationId 'com.lqr.demo.free'
    versionCode 32
    versionName '1.3.2'
    manifestPlaceholders = [
            app_icon: "@drawable/ic_launcher",
            app_name: "菜鸡【免费版】",
    ]
}


```

注意：

这里配置的包名是 applicationId，而不是清单文件里的 packageName，applicationId 与 packageName 是不一样的。

我们常说，一部 Android 设备上不能同时安装 2 个相同包名的 app，指的是 applicationId 不能一样。

applicationId 与 packageName 的区别可查阅《ApplicationId versus PackageName》：

> https://link.juejin.im/?target=http%3A%2F%2Ftools.android.com%2Ftech-docs%2Fnew-build-system%2Fapplicationid-vs-packagename

如果工程要求不同渠道共存，或者对版本号、icon、应用名等有定制需求的话，那么这个多渠道配置就显得非常有用了。其中，app_icon、app_name 是放在 manifestPlaceholders 的，这个其实是在对 AndroidManifest.xml 中的占位符进行变量修改，也就是说，要定制 icon 或者应用名的话，还需要对清单文件做些小修改才行（增加一些占位符），如：

```
<application
    xmlns:tools="http://schemas.android.com/tools"
    android:icon="${app_icon}"
    android:label="${app_name}"
    android:theme="@style/AppTheme"
    android:largeHeap="true"
    tools:replace="android:label">
    ...
</application>


```

/   生成渠道变量   /

在新增渠道之后，我们可以对这些渠道进行一起更多的配置，假设项目代码需要根据不同的渠道，赋予不同的数据，当然你可以选择在 java 代码中通过判断当前渠道名，配合 switch 来设置静态常量，但其实不用那么烦琐，而且有些静态数据通过类似 config.gradle 或 config.properties 这类配置文件来配置有比较好，那么 gradle 中的 applicationVariants 完全可以帮助到我们，以下面的配置 Demo 为例进行说明：

```
// 多渠道相关设置
applicationVariants.all { variant ->
    buildConfigField("String", "PROUDCT", "\"newapp\"")
    buildConfigField("String[]", "DNSS", "{\"http://119.29.29.29\",\"http://8.8.8.8\",\"http://114.114.114.114\"}")
    if (variant.flavorName == 'china') {
        buildConfigField("String", "DNS", "\"http://119.29.29.29\"")
    } else if (variant.flavorName == 'america') {
        buildConfigField("String", "DNS", "\"http://8.8.8.8\"")
    } else if (variant.flavorName == 'free') {
        buildConfigField("String", "DNS", "\"http://114.114.114.114\"")
    }
}


```

通过 gradle 中提供的 buildConfigField()，AndroidStudio 会在执行脚本初始化时，根据当前所选变体将对于的配置转变为 BuildConfig.java 中的一个个静态常量：  

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwTib1ySpQshEDn4VibjHXfIPfojsdPibJ6yq4XhPM4yjuDg7zaCs4YpuNg/640?wx_fmt=png)

当我切换其他变体时，BuildConfig 中的 DNS 也会跟着一起改变，这样，我们在工程代码中，就不需要去判断当前渠道名来为某些静态常量赋值了。这里只是举例了使用 buildConfigField() 来生成 String 和 String[] 常量，当然也可以用来生成其它类型的常量数据，有兴趣的话，可以百度了解下。

/   变体的使用   /

上面提到了变体，那么变体是什么？可以这样理解，变体是由【Build Type】和【Product Flavor】组合而成的，组合情况有【Build Type】*【Product Flavor】种，举个例子，有如下 2 种构建类型，并配置了 2 种渠道：

```
Build Type：release debug
Product Flavor：china free


```

那么最终会有四种 Build Variant 组成：

```
chinaRelease chinaDebug freeRelease freeDebug


```

变体在复杂多渠道工程中是相当有用的，可以做到资源文件合并以及代码整合，这里的合并与整合怎么理解？我们使用 Android Studio 进行项目开发时，会把代码文件与资源文件都存放在 app/src 目录下，通常是 main 下会有 java、res、assets 来区分存放代码文件和资源文件，你可以把 main 看作是默认渠道工程文件目录，也就是说 main 下存放在代码文件和资源文件对所有渠道来说都是共同持有的。

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwnTjmZvuqGLhpGR9VycZ4qia9thuW96F498VCt12BWpSv9SS60maWKyA/640?wx_fmt=png)

那么，一旦出来了某些代码文件或者资源文件是个别渠道专属时，应该怎么办呢？因为 main 是共有的，所以理想状态下，我们并不会把这类 "不通用" 的文件放在 main 下（这样做不会出错，但是做法很 low，会增大 apk 包体积），Android Studio 为变体做了很好的支持，我们可以在 app/src 下，创建一个以渠道名命名的目录，用于存放这类个别渠道专属的代码文件和资源文件，如：

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwrAmqNSj4F3Juv5nfNXPficBWG0h2QbyvrV371U7ic5o0oIwJVib4SeLUw/640?wx_fmt=png)

可以看到，当我选择 freeDebug 变体时，app/src/free 下的目录高亮了，说明它们被 Android Studio 识别，在运行工程时，Android Studio 会将 free 和 main 下的所有资源文件进行合并，将代码文件进行整合。同理，如果我选择的是 chinaDebug 变体，那么 app/src/china 下目录就会高亮。知道如何创建变体目录后，下面就开始进行资源合并与代码整合了。

资源合并

资源文件有哪些？我们可以这样认为：

```
资源文件 = res下的所有文件 + AndroidManifest.xml 


```

变体的资源合并功能简直是 "神器" 一般的存在，可以解决很多业务需求，如不同渠道显示的 icon 不同，应用名不同等等。Android Studio 在对变体目录和 main 目录进行资源合并时，会遵守这样的规则，假设当前选中的变体是 freeDebug：

*   某资源在 free 下有，在 main 中没有，那么在打包时，会将该资源直接合并到 main 资源中。
    
*   某资源在 free 下有，在 main 中也有，那么在打包时，会以 free 为主，将 free 中资源替换掉 main 中资源。
    

针对上述 2 个规则，这里以 string.xml 为例进行说明，main 下的 string.xml 是：

```
<resources>
    <string >Demo</string>
    <string >Lin</string>
</resources>


```

free 下的 string.xml 是：

```
<resources>
    <string >发生错误</string>
    <string >Lqr</string>
</resources>


```

那么最终打出的 apk 包里的 string.xml 是：

```
<resources>
    <string >Demo</string>
    <string >发生错误</string>
    <string >Lqr</string>
</resources>


```

除了字符串合并外，还有图片（drawable、mipmap）、布局（layout）、清单文件（AndroidManifest.xml）的合并，具体可以自己尝试一下。其中，清单文件的合并需要提醒一点，如果渠道目录下的 AndroidManifest.xml 与 main 下的 AndroidManifest.xml 拥有相同的节点属性，但属性值不同时，那么就需要对 main 下的 AndroidManifest.xml 进行修改了，具体修改要根据编译时报错来处理，所以，报错时不要慌，根据错误提示修改就是了。

注意：布局（layout）文件的合并是对整个文件进行替换的~。

代码整合

代码文件，顾名思义就是指 java 目录下的. java 文件了，为什么代码叫整合，而资源却是合并呢？因为代码文件是没办法合并的，只能是整合，整合是什么意思？假设当前选中的变体是 freeDebug，有一个 java 文件是 Test.java，这个 Test.java 要么只存在 free/java 下，要么只存在于 main/java 下，如：

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwVxRESuYoH6XMS7Via0dAyAu6CaeiaE6L0xJJp3ChUiblkAfB4GzYT4GJg/640?wx_fmt=png)

可以看到，一切正常，Test.java 被 AndroidStudio 识别，但如果此时在 main/java 下也存在 Test.java，那么 Android Studio 就会报错了：

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwPuloR7oumAb91XSwLTbmlX6he2dfbFgRlPR0vHcYxW1JAMRqiaprF7A/640?wx_fmt=png)

代码整合是一个比较头痛的事，因为如果你是在渠道目录 free 下去引用 main 下的类，那么是完全没有问题的，但如果反过来，在 main 下去引用 free 下的专属类时，情况就会变得很糟糕，当你切换其他变体时（如，切换成 chinaDebug），这时工程就会报错了，因为变体切换，Test.java 是 free 专属的，在 chinaDebug 变体下，free 不会被识别，于是 main 就找不到对应的类了。

选择 freeDebug 变体时，正常引用 Test.java：

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwYnyASPEZISprDMqsaYkbs48TjibWlZ1dibsJQYhAQjNP7d2KreoJQGOQ/640?wx_fmt=png)

选择 chinaDebug 变体时，找不到 Test.java（只找到 junit 下的 Test.java）：  

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwe4MVyQbWhG6YJYyr9FrnyEqToCKYJ5C0a4Tt5Zq2K36xB5rbIg87FA/640?wx_fmt=png)

所以，对于代码整合，需要我们在开发过程中慎重考虑，多想想如何将渠道目录与 main 目录进行解耦。比如可以使用 Arouter 来解耦 main 与渠道目录下所有的 Activity、Fragment，将类引用转换为字符串引用，全部将由 Arouter 来管理，又或者通过反射来处理，等等，这里顺带记录一下，我项目中使用 ARouter 来判断 Activity、Fragment 是否存在，和获取的相关方法：

```
/**
 * 获取到目标Delegate（仅仅支持Fragment）
 */
public <T extends CommonDelegate> T getTargetDelegate(String path) {
    return (T) ARouter.getInstance().build(path).navigation();
}

/**
 * 获取到目标类class（支持Activity、Fragment）
 */
public Class<?> getTargetClass(String path) {
    Postcard postcard = ARouter.getInstance().build(path);
    LogisticsCenter.completion(postcard);
    return postcard.getDestination();
}


```

其他

前面只说到了 res 和 java 这 2 个目录，那么 assets 呢，它是属于哪种？很可惜，assets 虽然是资源，但它不是合并，而是整合，也就是说，assets 文件的处理方式跟 java 文件的处理方式是一样的，不能在渠道目录和 main 目录下同时存在相同的 assets 文件，这将对某些需求实现造成阻碍，举个例子，假设 china 与 free 使用的 assets 资源是一样的，而 america 单独使用自己的 assets 资源，并且这些 assets 资源文件名都是一样的，那这时要怎么办呢？给每个渠道都放一份各自的 assets 资源吗？这种做法可行，但很 low，原因如下：

1.  复用性差：都说了 china 与 free 使用的资源是一样的，从整个工程的角度来看，一个工程里放了 2 份一模一样的 assets 资源文件，如果我有 10 个渠道，其中 9 个渠道使用的 assets 资源是一样的要怎么办，copy9 次？
    
2.  维护成本高：在开发行业里，需求变动是很常见的事，产品经理会时不时改下需求，所以，叫你改 assets 资源文件也是很有可能的，如果你采用每个渠道都放一份，那么当 assets 资源需要修改时，你就需要将每个渠道的 assets 目录资源替换一遍。记得，是每次修改都要替换一遍。
    

正确的解决方案是使用 sourceSets，对于 sourceSets 的使用，放到下一节去说明。

/   sourceSets   /

强大的 gradle，通过 sourceSets 可以让开发者能够自定义项目结构，如自定义 assets 目录、java 目录、res 目录，而且还可以是多个，但要知道的是，sourceSets 并不会破坏变体的合并规则，它们是分开的，sourceSets 只是起到了 “扩充” 的作用。这里先摆一下 sourceSets 的常规使用：

```
sourceSets {
    main {
        manifest.srcFile 'AndroidManifest.xml'
        java.srcDirs = ['src']
        aidl.srcDirs = ['src']
        renderscript.srcDirs = ['src']
        res.srcDirs = ['res']
        assets.srcDirs = ['assets']
    }
}


```

复用 assets 资源
------------

对于多渠道共用同一套 assets 资源文件这个问题，结合 sourceSets，我们可以这么处理，步骤如下：

1.  把共用的 assets 资源存放到一个渠道目录下，如 free/assets。
    
2.  修改 sourceSets 规则，强制指定 china 渠道的 assets 目录为 free/assets。
    

```
sourceSets {
    china {
        sourceSet.assets.srcDirs = ['src/free/assets']
    }
}


```

这样配置以后，如果下次需要统一修改 china 与 free 的 assets 资源文件时，你就只需要把 free/assets 目录下的资源文件替换掉就好了。虽然这种写法已经满足前面说的需求了，但是还不够，还可以再优化一下，假设你有 20 个渠道，都使用同一套 assets 资源的话，按前面的写法你就要写 19 遍 sourceSets 配置了。

```
sourceSets {
    china {
        sourceSet.assets.srcDirs = ['src/free/assets']
    }
    a{
        sourceSet.assets.srcDirs = ['src/free/assets']
    }
    b{
        sourceSet.assets.srcDirs = ['src/free/assets']
    }
    ...
}


```

可以想像，在这个 gradle 文件中，光 sourceSets 配置就会有多长，你可能会说，一个项目怎么会有这么多渠道，不好意思，本人所处公司的业务需求就有 20 + 个渠道的情况，话不多说，下面就来看看怎么优化好这段配置，如果你有学习过 gradle，就应该知道，gradle 是一种脚本，脚本是可以像写代码一样写逻辑的，那么上面的配置就可以转化为一个 if-else 代码片段：

```
sourceSets {
    sourceSets.all { sourceSet ->
    // println("sourceSet.name = ${sourceSet.name}")
    if (sourceSet.name.contains('Debug') || sourceSet.name.contains('Release')) {
        if (sourceSet.name.contains("china") 
                || sourceSet.name.contains("a")
                || sourceSet.name.contains("b")
                || ...) {
                sourceSet.assets.srcDirs = ['src/free/assets']
            }
        }
    }
}


```

现在你可能会觉得这样写好像精简不了多少，不过一旦你的业务复杂起来，像这样用代码的逻辑思维来处理配置，相信这会是一种不错的选择。

有兴趣的可以打印下 sourceSet.name；if 的写法不一定要用 contains()，也可以用其他的判断方式，具体看开发者自己决定。

修改程序主入口
-------

对于 sourceSets 的使用，除了针对修改 assets 以外，java 文件、res 资源文件、清单文件等等都是可以用同样的方式进行 “扩充” 的，比如不同渠道共用一套 java 代码逻辑，那么我们可以把这套代码单独抽取出来存放在一个其他目录下，然后使用 sourceSets 对其进行添加。这里就以我亲身经历来说明，我是如何通过 sourceSets 对于 java 和清单文件进行指定，并且完美解决此类 "变态" 需求的。

### **背景**

新的 app 项目开发完成，现在需要将项目定制化后上线，项目整体采用 1 个 Activity + n 个 Fragment 架构，这个 Activity 便是程序主入口，因为我们产品是做机顶盒 app 开发，产品开发完成后，需要上线到盒子运营商（局方）的应用商店，然后通过盒子推荐位（EPG）启动我们开发的 app，因此上线后，需要提供 app 的包名和类名给到局方，假设新 app 的包名和类名分别如下：

```
包名：com.lqr.newapp
类名：com.lqr.newapp.MainActivity


```

### **需求**

把新 app 的包名和类名改成跟旧 app 的一样，因为局方那边不想换~~ 假设旧 app 的包名和类名如下：

```
包名：com.lqr.oldapp
类名：com.lqr.oldapp.MainActivity


```

### **问题**

修改包名很简单，但是修改入口类名就很麻烦了，如果我在该渠道目录下新增一个 com.lqr.oldapp.MainActivity，并在其清单文件中进行注册，那么，在打包时，渠道目录下的 AndroidManifest.xml 会与 main 目录下的 AndroidManifest.xml 进行合并。

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNw0kDCrJiaKCQ2dP85gBcgO0BUxGaIRvrcOVFWic1kJNnczRJBoP4DlOPw/640?wx_fmt=png)

而 main 目录下的 AndroidManifest.xml 中已经注册了 com.lqr.newapp.MainActivity，这样就会导致，最终输出 apk 包中的清单文件会有 2 个入口类。

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNwZAJZflIN2L7tv4PX6GKuAzNV9RreZGdmYxibQF884QRdm6YVx4meNnA/640?wx_fmt=png)

是的，这样的产品交付出去，确实也可以应付掉局方的需求，但是，一旦盒子安装了这个 app，那么盒子 Launcher 上可能会同时出现 2 个入口 icon，到时又是一顿折腾，毕竟 app 上线流程比较麻烦，我们最好是保证产品就一个入口。

### **分析**

因为变体的资源合并规则，只要渠道目录和 main 目录下都存在 AndroidManifest.xml，那么最终 apk 包里的清单文件合并出来的就会是 2 个文件的融合，所以，不能在这 2 个清单文件中分别注册入口。可以抽出 2 个不同入口的 AndroidManifest.xml 存放到其他目录，main 下的 AndroidManifest.xml 只注册通用组件即可。

### **操作**

#### a. 抽离 MainActivity（oldapp）

在 app 目录下，创建一个 support/entry 目录（名字随意），用于存放入口相关功能的代码及资源文件，将 com.lqr.oldapp.MainActivity 放到 support/entry/java 目录下。

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNw1Xmely74m8KtkDthhBoMKBSvQXIaGMbFUuicR0QP8hTuur5xEa8NkKg/640?wx_fmt=png)

#### b. 抽离 AndroidManifest.xml

在 support 目录下，创建 manifest（名字随意），用于存放各渠道对应的 AndroidManifest.xml，如：

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5tx9ib7QfyWfZxcLv5zIjNw4ib93upTFGnQBuIJrryHXzPHsALbN2T5992MmqtFntYol5bkEk0dbcQ/640?wx_fmt=png)

其中 newapp 目录下的 AndroidManifest.xml：

```
<application>
    <activity android:>
        <intent-filter>
            <action android:/>

            <category android:/>
        </intent-filter>
    </activity>
</application>


```

oldapp 目录下的 AndroidManifest.xml：

```
<application>
    <activity android:>
        <intent-filter>
            <action android:/>
            <category android:/>
        </intent-filter>
    </activity>
</application>


```

#### c. 配置 sourceSets

经过上面 2 步后，oldapp 的 MainActivity 与各自的主入口注册清单文件就被抽离出去了，接下来就是使用 sourceSets，根据不同的渠道名，指定 java 与清单文件即可：

```
sourceSets {
    sourceSets.all { sourceSet ->
        // project.logger.log(LogLevel.ERROR, "sourceSet.name = " + sourceSet.name)
        if (sourceSet.name.contains('Debug') || sourceSet.name.contains('Release')) {
            if (sourceSet.name.contains("china")) {
                sourceSet.java.srcDirs = ['support/entry/java']
                sourceSet.manifest.srcFile 'support/manifest/oldapp/AndroidManifest.xml'
            } else {
                sourceSet.manifest.srcFile 'support/manifest/newapp/AndroidManifest.xml'
            }
        }
    }
}


```

至此，最终打出的 apk 包中的 AndroidManifest.xml 中就只会保留一个主入口了，完美解决了局方要求。

解疑
--

### Q：为什么要把 oldapp 的 MainActivity 也抽出去？

A：因为 oldapp 的 MainActivity 不单只是 china 这个渠道需要用到，后续还会被其它渠道使用，为了后续复用考虑，于是就把 MainActivity 抽离出来。

### Q：为什么 sourceSets 中要判断 sourceSet.name 是否包含 Debug 或 Release？

A：如果你有打印过 sourceSet.name 的话，你一定会发现输出的结果不单单只是那几个变体名，还有 androidTest、test、main 等等这些，但我们仅仅只是想对工程变体（chinaDebug、chinaRelease、freeDebug、freeRelease）指定 java 目录和清单文件而已，如果对 test、main 这类 “东西” 也指定的话，结果并不是我们想要的，所以，一定要确保 source 配置的是我们想要指定的变体，而非其他。

### Q：sourceSets 与变体合并的关系究竟如何？

A：以 java 源码目录为例，默认 AS 工程的 java 源码目录是【src/main/java】，在 gradle 中通过 sourceSets 指定了另一个目录，比如【support/entry/java】，那么打包时，AS 会认为这 2 个目录均是有效的 java 目录，所以，sourceSets 指定的 java 目录仅仅只是对原来的扩充，而非替换。还是以 java 源码目录为例，如果你的项目配置了多渠道，在不考虑 sourceSets 的情况下，项目在打包时，因为变体合并的特性，有效的 java 目录也是有 2 个，分别是【src/main/java】和【src / 渠道名 / java】，变体的合并规则不会因为 sourceSets 的配置而改变，如果将上述 2 种情况一起考虑上的话，那么最终打包时，有效的 java 目录则是 3 个，分别是【src/main/java】、【src / 渠道名 / java】、 【support/entry/java】。

 /   渠道依赖   /

我们知道，要在 gradle 中添加第三方库依赖的话，需要在 dependencies 领域进行配置，常见的 configuration 有 provided（compileOnly）、compile（api）、implementation 等等，它们的区别请自行百度查阅了解，针对【Build Type】、【Product Flavor】、【Build Variant】，这些 configuration 也会出现一些组合，如：

### a. 构建类型组合

```
debugCompile    // 所有的debug变体都依赖
releaseCompile    // 所有的release变体都依赖


```

### b. 多渠道组合

```
chinaCompile    // china渠道依赖
americaCompile    // america渠道依赖
freeCompile        // free渠道依赖


```

### c. 变体组合

```
chinaDebugCompile        // chinaDebug变体依赖
chinaReleaseCompile        // chinaRelease变体依赖
americaDebugCompile        // americaDebug变体依赖
americaReleaseCompile    // americaRelease变体依赖
freeDebugCompile        // freeDebug变体依赖
freeReleaseCompile        // freeRelease变体依赖


```

常规方式配置渠道依赖
----------

通过上述组合就可以轻松配置好各种情况下的依赖了，如：

```
// autofittextview
compile 'me.grantland:autofittextview:0.2.+'
// leakcanary
debugCompile "com.squareup.leakcanary:leakcanary-android:1.6.1"
debugCompile "com.squareup.leakcanary:leakcanary-support-fragment:1.6.1"
releaseCompile "com.squareup.leakcanary:leakcanary-android-no-op:1.6.1"
// gson
chinaCompile 'com.google.code.gson:gson:2.6.2'
americaCompile 'com.google.code.gson:gson:2.6.2'
freeCompile 'com.google.code.gson:gson:2.5.2'


```

代码方式配置渠道依赖
----------

虽然官方给出的多种组合依赖可以解决几乎所有的依赖问题，但实际上，当渠道有很多很多时，整个 gradle 文件将变得冗长臃肿，你能想像 20 多个渠道中只有 1 个渠道依赖的 gson 版本不同的情况吗？所以，这时候就需要考虑一下，充分利用好 gradle 作为脚本的特性，使用代码方式来进行渠道依赖：

```
dependencies {
    gradle.startParameter.getTaskNames().each { task ->
        // project.logger.log(LogLevel.ERROR, "lqr print task : " + task)
        if (task.contains('free')) {
            compile 'com.google.code.gson:gson:2.5.2'
        } else {
            compile 'com.google.code.gson:gson:2.6.2'
        }
    }
}


```

另外，还有一种方式是我之前项目中使用过的，但这种方式不支持依赖远程仓库组件，这里也记录一下：

```
dependencies {
    // 配置 插件化库 依赖
    applicationVariants.all { variant ->
        if (variant.flavorName == 'china')
                ||variant.flavorName == 'america') {
            dependencies.add("${variant.flavorName}Compile", project(':DroidPluginFix'))
        } else {
            dependencies.add("${variant.flavorName}Compile", project(':DroidPlugin'))
        }
    }
}


```

要知道，以下写法是正确的，但就是不生效：

```
dependencies.add("${variant.flavorName}Compile", 'com.google.code.gson:gson:2.6.2')


```

DroidPluginFix 是最新官方适配了 Android7、8 的 DroidPlugin，而 DroidPlugin 则没有适配，因为历史原因，需要对不同的渠道依赖不同的 DroidPlugin 版本。

/   总结   /

因为公司业务的特殊性，对 gradle 以及多渠道的掌握要求比较高，所以，这也是我这段时间来重点学习的一部分，但是毕竟是单独学习这些知识，可能也会存在一些掌握不到位的情况，所以上面提到的知识如果有误或有更好的处理方式，欢迎各位指出和分享，thx~

推荐阅读：

[干货分享，组件化开发到底存在哪些优势？](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650246273&idx=1&sn=bd6d0ed90bf1526a5b04d3ba4d829c64&chksm=88637deebf14f4f83223fb15af5285b793f512d485434b963792d88077f2f0bfaa2e63a32601&scene=21#wechat_redirect)  

[Android 和 H5 那些不可描述的事情...](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650246240&idx=1&sn=8ff8a6ad4de43d91883ebc397258296a&chksm=88637d0fbf14f419dec0958586d74327f0c0de88223b0360551c6f539ea464cece3106d9bba0&scene=21#wechat_redirect)  

[Canvas 可以画出任何你想要的效果](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650246061&idx=1&sn=b0db1da7e3b302d40543b3ad73e08db9&chksm=88637ac2bf14f3d4e92f30e51c1fcfe02fdf15260a32c7382226298c22286f3d1cecf2b0e228&scene=21#wechat_redirect)  

欢迎关注我的公众号

学习技术或投稿

![](https://mmbiz.qpic.cn/mmbiz/wyice8kFQhf4Mm0CFWFnXy6KtFpy8UlvN0DOM3fqc64fjEj9tw23yYSqujQjSQoU1rC0vicL9Mf0X6EMR4gFluJw/640.png?)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt6FSn51QbdP1ic92cjsQM7LkBCfnaJMtcibMw9vYtdQ6QQM3CcFFbGqMoNucFlBRJw9E6VQWYk30ficw/640?wx_fmt=jpeg)

长按上图，识别图中二维码即可关注