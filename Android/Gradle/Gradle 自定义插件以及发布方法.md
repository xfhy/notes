> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/d1d7fd48ff0b

前言
--

我们平常在进行 Android 开发时，都会使用 [Gradle](https://gradle.org/guides/) 来进行项目配置，通常在对应的 module:app 的`build.gradle`中，在最上面的一句话一般都为:

```
apply plugin: 'com.android.application'


```

这句话就是用来加载 gradle 的`android`开发插件，然后，我们就可以使用该插件提供的配置方法进行 Android 项目的配置了，即如下所示:

```
android {
    compileSdkVersion 26
    buildToolsVersion "26.0.1"
    defaultConfig {
        applicationId "com.yn.gradleplugintest"
        minSdkVersion 23
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}


```

更多 `android` 插件配置详情，请查看：[Android Plugin DSL Reference](http://google.github.io/android-gradle-dsl/current/)

简单来讲，Gradle 插件允许我们做一些额外的扩展工作，比如我想在 module 每次 build 完成后，把生成的 jar/aar 移动到另一个项目的 libs 文件内，相当于动态更新库文件 ······  
其实像上面这种操作用 [Gradle](https://gradle.org/guides/) 的 `task` 就可以完成，但是使用 `task` 的一个弊端就是没办法做到复用，而使用 [Gradle](https://gradle.org/guides/) 你就能在任何项目，任何模块中使用同一套逻辑功能，甚至于你还能对不同的模块进行动态化的个性配置，只要插件代码支持即可。

更多 [Gradle](https://gradle.org/guides/) 使用方法，请查看官网：[Gradle User Guide](https://docs.gradle.org/4.1/userguide/userguide.html)

自定义插件
-----

上面我们说了自定义插件的诸多好处，那么，究竟该如何进行 [Gradle](https://gradle.org/guides/) 自定义插件的编写呢 ?  
其实，自定义插件基于源码放置可以分为 3 种：

*   第一种：**Build script**  
    这种插件脚本的源码放置在模块内的 `build.gradle` 中，好处就是插件脚本会被自动编译并添加进模块的 `classpath` 中，我们完全不用做任何事情。但是，这种插件脚本只能在声明的这个 `build.gradle` 中使用，其他模块是没办法复用这个插件的。

```
//app build.gradle
apply plugin: BuildScriptPlugin

class BuildScriptPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('hello') {
            group = "test"
            description = "gradle build script demo,shares only in this build.gradle"
            doLast {
                println "Hello from the BuildScriptPlugin"
            }
        }
    }
}


```

如上面这个插件 `GreetingPlugin`, 我们是在 `app` 的 `build.gradle` 中声明定义的，然后，在控制台运行下: `gradle app:tasks`，或者直接看 Android Studio 的 Gradle 窗口，可以看到如下结果：

![](http://upload-images.jianshu.io/upload_images/2222997-9c604f19a72893fa.png) Build script

可以看到，只有我们声明编写的 `app` 中成功添加了一个 `task:hello`（根目录也会同时添加进这个插件功能），然后，你在工程其他 `build.gradle` 中 `apply plugin: GreetingPlugin` 是无法成功的，因为这种插件对其他模块是不可见的。

所以，**Build script** 这种插件其实跟直接定义一个 `task` 没有多大区别。

*   第二种：**buildSrc project**  
    这种插件脚本要求源码放置在 `rootProjectDir/buildSrc/src/main/groovy`目录内（也就是工程根目录下创建 buildSrc 目录），然后 [Gralde] 就会自动编译和测试这个插件，同时，这种方法创建的插件对工程内的所有模块都是可以使用的。

![](http://upload-images.jianshu.io/upload_images/2222997-6e577bcddf5c831c.png) buildSrc project

从上图可以看到，我们在模块

`app`

中成功加载了插件，所以，使用 buildSrc project 这种插件脚本方法就使得我们创建了一个工程插件。

在你工程只需扩展本工程额外功能，不需与其他工程或者其他开发者进行共用时，buildSrc project 这种插件开发或许是个不错的选择。

*   第三种：**Standalone project**  
    这种方法就是使用单独的一个工程 / 模块创建我们的 [Gradle](https://gradle.org/guides/) 插件，这种方法会构建和发表一个 JAR 文件，可以提供给多工程构建和其他开发者共同使用。通常来说，JAR 文件内可能包含有一些自定义的插件脚本，或者是由一些相关的 `task` 类组合成的一个库，获取前面两者的结合 ······

下面我们来讲下 **Standalone project** 插件脚本编写方法，主要有以下几大步骤：

1.  在 Android Studio 中新建一个 project，然后建立一个 Android Module，然后删除掉目录下除了 `src/main`和 `build.gradle` 之外的其他内容，把 `build.gradle` 内容清空。
2.  在 `src/main/` 目录下创建一个 `groovy` 目录，用于存放 [Gradle](https://gradle.org/guides/) 插件代码。
3.  在 `build.gradle` 中添加 `gradle sdk`和 `groovy` 语言支持

```
apply plugin: 'groovy'

dependencies {
    //gradle sdk
    compile gradleApi()
    //groovy sdk
    compile localGroovy()
}


```

4.  现在，我们就可以进行 [Gradle](https://gradle.org/guides/) 插件代码的具体编写了，编写方法跟 java 基本一致，这里，我就给出一个简单的 `Demo`:

```
package com.yn.test

import org.gradle.api.Plugin
import org.gradle.api.Project

class StandAlonePlugin implements Plugin<Project> {
    void apply(Project project) {
        note()
        //create an extension object:Whyn,so others can config via Whyn
        project.extensions.create("whyn", YNExtension)
        project.task('whyn'){
            group = "test"
            description = "gradle Standalone project demo,shares everywhere"
            doLast{
                println '**************************************'
                println "$project.whyn.description"
                println '**************************************'
            }

        }
    }

    private void note(){
        println '------------------------'
        println 'apply StandAlonePlugin'
        println '------------------------'
    }
}

class YNExtension {
    String description = 'default description'
}


```

这里，我们提供了扩展属性的功能，方便我们在其他地方使用扩展属性，让我们的插件能够接收传递信息。

5.  代码写完后，为了让 [Gradle](https://gradle.org/guides/) 能够找到我们插件的实现类，我们还需要提供一个 properties 文件，具体做法如下：  
    在 `main` 目录下新建 `resources` 目录，然后在 `resources` 目录里面再新建 `META-INF` 目录，再在 `META-INF` 里面新建 `gradle-plugins`目录，最后在 `gradle-plugins` 里面新建一个 properties 文件（**注：**该 properties 的命名就是最后别人 `apply plugin:`时使用的名称），最后在该 properties 文件内写入插件完整包名：`implementation-class=com.yn.test.StandAlonePlugin`。  
    最后的目录结构如下图所示：

![](http://upload-images.jianshu.io/upload_images/2222997-e6b5e5ffae2f29c8.png) StandAlone project

6.  最后，**Standalone project** 创建的插件需要先进行发布:`Publish`，才能被其他工程所使用。  
    发布方法如下：

*   在 `build.gradle` 中添加如下内容：

```
apply plugin: 'maven-publish'

publishing {
    publications {
        mavenJava(MavenPublication) {

            groupId 'com.whyn.plugin'
            artifactId 'ynplugin'
            version '1.0.0'

            from components.java

        }
    }
}

publishing {
    repositories {
        maven {
            // change to point to your repo, e.g. http://my.org/repo
            url uri('D:/Android/repos')
        }
    }
}


```

从代码中可以看到，我这里是把插件发布到我本地路径: `D:/Android/repos` 中，如果把这个路径换成网络地址，那就是发布到网络上。

*   现在就可以打开控制台窗口，输入 `gradlew publish` 进行插件发布。  
    发布成功后，你就可以在本地路径中看到如下结果：

![](http://upload-images.jianshu.io/upload_images/2222997-fe24cf0b834cd0f1.png) publish local

更多插件发布内容，请查看官网：

[Maven Publishing](https://docs.gradle.org/4.1/userguide/publishing_maven.html)

7.  到此，我们自定义的插件已经完成了编写和发布过程了，最后要做的就是，在其他功能模块中使用我们这个插件，具体方法如下：

*   在工程的根目录的 `build.gradle` 中添加如下内容：

```
buildscript {
    
    repositories {
        google()
        jcenter()
        maven {//local maven repo path
            url uri('D:/Android/repos')
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.0-alpha8'
        //group:artifactId:version
        classpath 'com.whyn.plugin:ynplugin:1.0.0'
//        classpath group: 'com.whyn.plugin', name: 'ynplugin', version: '1.0.0'            
    }
}


```

*   然后，在 Module 的 `build.gradle` 中 `apply` 这个插件:

```
//app build.gradle
apply plugin: 'com.whyn.plugin.StandAlone'


```

`sync` 一下，然后在控制台输入 `gradlew whyn`，你就可以看到以下输出：  

![](http://upload-images.jianshu.io/upload_images/2222997-9cf470fdc20d756c.png) whyn_plugin_default_extension

可以看到，我们成功地输出了插件中

`description`

的默认值，如果我们想改变这个值，那就再加载这个插件的

`build.gradle`

中配置一下我们插件提供的

`extension`

：

```
//app build.gradle
apply plugin: 'com.whyn.plugin.StandAlone'

whyn {
    description 'description in app build.gradle'
}


```

然后再 `sync`，再执行 `gradlew whyn`，就可以看到我们输出了自定义配置的内容了：

![](http://upload-images.jianshu.io/upload_images/2222997-afd58d1fd3bee0b9.png) whyn_plugin_custom_extension

顺便在说一下，插件中 `apply()` 执行的时序是在我们 `apply plugin:` 的时候就会被调用执行的，也就是说，我们的 `build.gradle` 中录入 `apply plugin:'com.whyn.plugin.StandAlone'`后，`sycn` 的时候，`apply plugin:'com.whyn.plugin.StandAlone'` 就会被执行，从而插件的 `apply(Project project)` 就会被调用执行，所以我们每次在 `sync` 的时候，都可以在 `Gradle Console` 窗口中看到 `apply(Project project)` 中的输出信息：

![](http://upload-images.jianshu.io/upload_images/2222997-808926ac82c2c45a.png) sync

更多的自定义插件编写详情，请查看官网：[Writing Custom Plugins](https://docs.gradle.org/4.1/userguide/custom_plugins.html#sec:custom_plugins_standalone_project)