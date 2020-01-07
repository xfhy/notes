> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5b02113a5188254289190671

> 欢迎关注本人公众号，扫描下方二维码或搜索公众号 id: mxszgg
> 
> ![](https://user-gold-cdn.xitu.io/2018/6/10/163e7d8e2bbaab51?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

本文基于 Android Gradle plugin 3.0.1

前言
--

在[前文](https://juejin.im/post/5afa06466fb9a07aaa1163f1)中笔者阐述道 task 就相当于函数，那么这篇文章所要介绍的 plugin 就相当于函数库了。毕竟在 `build.gradle` 文件中撰写大量的 task 是肯定不好维护的，所以可以将 tasks 做成 plugin 然后直接 apply 就好了。

> 就像在 `app/build.gradle` 中 `apply plugin: 'com.android.application'` 这样 appProject 就可以使用该 plugin 中的 task 了。

准备工作
----

1.  新建一个 Android 项目。
2.  新建一个 java library module，该 module 必须[命名为 `buildSrc`](https://docs.gradle.org/4.4/userguide/custom_plugins.html#sec:packaging_a_plugin)。
3.  将 `src/main/java` 改成 `src/main/groovy`

基本实现
----

1.  新建一个 xxxPlugin.groovy 并实现 Plugin 接口，例如：
    
    ```
    import org.gradle.api.Plugin
    import org.gradle.api.Project
    
    class TestPlugin implements Plugin<Project> {
      @Override
      void apply(Project project) {
        project.task('pluginTest') {
          doLast {
            println 'Hello World'
          }
        }
      }
    }
    复制代码
    
    ```
    

可以看到，上述 plugin 仅是在 `apply()` 方法内部创建了一个名为 `pluginTest` 的 task。

> 由于 Kotlin/Java 与 groovy 的兼容，所以并非一定要创建 groovy 文件，也可以是 xxxPlugin.java/xxxPlugin.kotlin。

2.  既然 plugin 已经就这么简单的实现了，那么如何应用到实际项目中呢？在 `build.gradle` 文件中添加如下信息：

> apply plugin: TestPlugin

至此之后，不妨在命令行调用 `pluginTest` task 看看是否有效果——

> ./gradlew pluginTest
> 
> > Task :app:testPlugin
> 
> Hello from the TestPlugin

### 扩展

随着项目的急速发展，有朝一日发现有时候不想输出 `Hello World` 而是希望这个 `pluginTest` task 可以根据开发者的需求进行配置。

1.  创建一个 xxxExtension.groovy 文件（当然，也可以用 Java/Kotlin 来写），实际上就是和 JavaBean 差不多的类，类似如下：
    
    ```
    class TestPluginExtension {
      String message = 'Hello World'
    }
    复制代码
    
    ```
    
2.  在 Plugin 类中获取闭包信息，并输出：
    
    ```
    class TestPlugin implements Plugin<Project> {
        void apply(Project project) {
            // Add the 'testExtension' extension object
            def extension = project.extensions.create('testExtension', TestPluginExtension)
            project.task('pluginTest') {
                doLast {
                    println extension.message
                }
            }
        }
    }
    复制代码
    
    ```
    
    第四行通过 `project.extensions.create(String name, Class<T> type, Object... constructionArguments)` 来获取 `testExtension` 闭包中的内容并通过反射将闭包的内容转换成一个 TestPluginExtension 对象。
    
3.  在 `build.gradle` 中添加一个 `testExtension` 闭包：
    
    ```
    testExtension {
     message 'Hello Gradle'
    }
    复制代码
    
    ```
    
4.  在命令行键入以下信息：
    

> ./gradlew pluginTest

将会看到输出结果——

> > Task :app:pluginTest
> 
> Hello Gradle

### 项目化

到目前为止谈及到的东西都还是一个普通的、**不可以发布到仓库**的插件，如果想要将插件发布出去供他人和自己在项目中 apply，需要进行以下步骤将插件变成一个 Project——

1.  更改 `build.gradle` 文件内容：
    
    ```
    apply plugin: 'groovy'
    
    dependencies {
        compile gradleApi()
        compile localGroovy()
    }
    复制代码
    
    ```
    
    > 此时可以观察到 External Libraries 中多出了 gradle-api/gradle-installation-beacon/groovy 库。其中，gradle 的版本是基于项目下 gradle wrapper 中配置的版本——
    > 
    > ![](https://user-gold-cdn.xitu.io/2018/5/21/1638013660923c1e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1) ![](https://user-gold-cdn.xitu.io/2018/5/21/1638013660af02df?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)
    
2.  创建 `src/main/resources/META-INF/gradle-plugins/插件名.properties`，例如 `src/main/resources/META-INF/gradle-plugins/com.sample.test.properties`，然后将 properities 文件内容改为 `implementation-class=Plugin 路径`，例如 `implementation-class=com.sample.test.TestPlugin`。
    
3.  在 `build.gradle` 文件中通过 `apply plugin: '插件名'` 引入插件 —— `apply plugin: 'com.sample.test'`。
    
4.  在命令行键入以下信息：
    

> ./gradlew pluginTest

将会看到输出结果——

> > Task :app:pluginTest
> 
> Hello Gradle

> 当然，以上仅是告诉各位读者如何将 plugin 项目化，并未涉及到如何将 plugin 提交到仓库中，关于 jcenter 仓库提交方式可借鉴[手摸手教你如何把项目提交到 jcenter](https://blog.csdn.net/ziwang_/article/details/76556621)，其他仓库提交方式读者可自行搜索。

实战
--

Android 打包过程中，一个 task 接着一个 task 的执行，每个 task 都会执行一段特定的事情（例如[第一篇文章](https://juejin.im/post/5af4f117f265da0b9f405221)中提到的几个 task），所以在 Gradle 插件的开发中，如果是针对打包流程的更改，实际上大部分都是 hook 某一个 task 来达到目的——例如我司的 [mess](https://github.com/eleme/Mess) 通过 hook `transformClassesAndResourcesWithProguardForDebug` task （Gradle v2.0+ task）来实现对四大组件以及 View 的混淆的；美丽说的 [ThinRPlugin](https://github.com/meili/ThinRPlugin) 是通过 hook `transformClassesWithDexForDebug`（Gradle v2.0+ task）来实现精简 R.class/R2.class 的。

因为 Android 现有的 task 已经很完善了，所以如果想要达到目的，只需要了解相应的 task 并在其之前或之后做一些操作即可。

为了示例而示例的简单例子实在不多，笔者只能拿起[上篇文章](https://juejin.im/post/5afa06466fb9a07aaa1163f1#heading-8)中的示例——在 app 目录下创建 pic 文件夹，并添加一个名为 test 的 png 图片，hook apk 打包流程将该图片添加入 apk 的 `assets` 文件夹。

> 尽管这看起来真的很没有卵用。

这次为了符合实际开发要求，不妨提升一定的难度——仅在 release 包中向 `assets` 添加图片，而 debug 包不向 `assets` 中添加图片。在实际开发中有很多这样的需求，例如前文提到的 [mess](https://github.com/eleme/mess) 是对 apk 源码进行混淆的，那么日常开发者运行的 debug 包有必要执行该 task 么？显然并不需要，应该仅在发布的时候打 release 包的时候执行该 task 就好了。

那么如何知道当前 task 是为 release 服务的呢？简单的寻找到 name 为 `packageRelease` 的 task 是肯定不行的，日常开发中项目时常有很多种变体，例如在 `app/build.gradle` 中输入以下代码：

```
android {
	...
	flavorDimensions "api", "mode"
	
	productFlavors {
   		demo {
      		dimension "mode"
    	}

    	full {
      		dimension "mode"
    	}

    	minApi23 {
      		dimension "api"
      		minSdkVersion '23'
    	}

    	minApi21 {
      		dimension "api"
      		minSdkVersion '21'
    	}
  }
复制代码

```

此时的变种共有 3 (debug、release、androidTest) * 2（demo、full） * 2（minApi23、minApi21）共计 12 种，截图如下：

![](https://user-gold-cdn.xitu.io/2018/5/21/163801366127f70f?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

那么如何为以上所有的 release 变种包的 `assets` 中都填入图片呢？

根据[官方文档](https://google.github.io/android-gradle-dsl/3.1/com.android.build.gradle.AppExtension.html#com.android.build.gradle.AppExtension:applicationVariants)可以知道开发者可以通过 `android.applicationVariants.all` 获取到当前所有的 [apk 变体](https://developer.android.com/studio/build/build-variants)，该变体的类型为 `ApplicationVariant`，其父类 `BaseVariantOutput` 中含 name 字段，该字段实际上就是当前变体的名字，那么其实只需要判断该 name 字段是否包含 release 关键字即可。

创建 plugin 的基本流程已经在前文中阐述过了，直接进行核心 plugin 的撰写，`HookAssetsPlugin` 源码如下：

```
import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.tasks.PackageApplication
import org.gradle.api.Plugin
import org.gradle.api.Project

class HookAssetsPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    project.afterEvaluate {
      project.plugins.withId('com.android.application') {
        project.android.applicationVariants.all { ApplicationVariant variant ->
          variant.outputs.each { ApkVariantOutput variantOutput ->
            if (variantOutput.name.equalsIgnoreCase("release")) {
              variantOutput.packageApplication.doFirst { PackageApplication task ->
                project.copy {
                  from "${project.projectDir.absolutePath}/pic/test.png"
                  into "${task.assets.asPath}"
                }
              }
            }
          }
        }
      }
    }
  }
}
复制代码

```

1.  在[第一篇文中](https://juejin.im/post/5af4f117f265da0b9f405221)就阐述过，只能在 `project.afterEvaluate` 闭包中才能获取到当前 project 中的所有 task 。
    
2.  通过 `project.plugins.withId('com.android.application')` 确保当前 project 是 Android app project 而不是 Android library project，以此来避免无效操作，毕竟 package task 是 `com.android.application` 中的 task。
    
3.  通过 `project.android.applicationVariants.all` 获取所有变体信息。
    
4.  通过观察 `ApplicationVariant` 类的父类 `BaseVariant` 中 outputs 字段可知道该字段代表着当前变体的输出信息（DomainObjectCollection 类型），`BaseVariantOutput` 的子类 `ApkVariantOutput` 中的 `packageApplication` 即为上一篇文章中所说的 `PackageAndroidArtifact` task 了。
    
5.  判断当前变体是否是 release 的变体。（通过 `variantOutput.name.equalsIgnoreCase("release")/variant.name.equalsIgnoreCase("release")` 都是可以的。）
    
6.  hook 步骤 4 中所说的 `PackageAndroidArtifact` task，将图片复制到 `assets` 中。
    

> 实际上，在日常开发中寻找 task 的方式可能更多的是使用 `project.tasks.findByName(name)/project.tasks.getByName(name)`，这样也更加方便，笔者在 demo 中附带了此种写法，[源码戳我](https://github.com/jokermonn/GradlePlugin)。

后续
--

除了上面提到的 [mess](https://github.com/eleme/Mess)（[mess 源码解析](http://www.wangyuwei.me/2017/02/09/Mess%E8%AF%A6%E8%A7%A3%EF%BC%88%E5%85%B3%E4%BA%8EActivity%E3%80%81%E8%87%AA%E5%AE%9A%E4%B9%89View%E7%AD%89%E7%9A%84%E6%B7%B7%E6%B7%86%EF%BC%89/)） 和 [ThinRPlugin](https://github.com/meili/ThinRPlugin) （笔者将会在后续的文章中对 [ThinRPlugin](https://github.com/meili/ThinRPlugin) 的源码进行解析）以外，笔者了解到的还有一些以下知名的 Gradle 插件可供读者学习：

*   [tinker-patch-gradle-plugin](https://github.com/Tencent/tinker/tree/master/tinker-build/tinker-patch-gradle-plugin)（ [Android 热修复 Tinker Gradle Plugin 解析](https://blog.csdn.net/lmj623565791/article/details/72667669)）
*   [butterknife-gradle-plugin](https://github.com/JakeWharton/butterknife/tree/master/butterknife-gradle-plugin)
*   [build-time-tracker-plugin](https://github.com/passy/build-time-tracker-plugin)（[检测使用 Gradle 构建项目时 每一个 Task 的耗时](https://juejin.im/entry/56e92135efa6310054477a40) ）
*   [gradle-small-plugin](https://github.com/wequick/Small/tree/master/Android/DevSample/buildSrc)（[轻量级插件化框架——Small](https://www.jianshu.com/p/7990714d10cb)）

当然，前面提到的几个 plugin 有些重量级，轻量级的笔者没有了解多少，只能推荐 [mess 源码解析](http://www.wangyuwei.me/2017/02/09/Mess%E8%AF%A6%E8%A7%A3%EF%BC%88%E5%85%B3%E4%BA%8EActivity%E3%80%81%E8%87%AA%E5%AE%9A%E4%B9%89View%E7%AD%89%E7%9A%84%E6%B7%B7%E6%B7%86%EF%BC%89/)作者的[一个快速生成 R2.java 中 fields 的插件](http://www.wangyuwei.me/2017/12/02/%E4%B8%80%E4%B8%AA%E5%BF%AB%E9%80%9F%E7%94%9F%E6%88%90R2-java%E4%B8%ADfields%E7%9A%84%E6%8F%92%E4%BB%B6/)和[一个快速将指定 class 打入 maindex 的插件](http://www.wangyuwei.me/2017/11/27/%E4%B8%80%E4%B8%AA%E5%BF%AB%E9%80%9F%E5%B0%86%E6%8C%87%E5%AE%9Aclass%E6%89%93%E5%85%A5maindex%E7%9A%84%E6%8F%92%E4%BB%B6/)，对新手了解 Gradle plugin 还是很友好的。

本文实战模块的源码链接：[请戳我](https://github.com/jokermonn/GradlePlugin)。

> 笔者新建了微信群，如果读者有问题或者对笔者感兴趣，欢迎加入，由于满了 100 人，需要先加笔者的微信，微信备注：入群。
> 
> ![](https://user-gold-cdn.xitu.io/2018/5/21/16381f87ed6e9a08?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)