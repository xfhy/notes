> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/KCpl0CNgwMv0CgvbadNK6A

> 本文由`玉刚说写作平台`提供写作赞助
> 
> 赞助金额：`300元`  
> 原作者：`竹千代`  
> 版权声明：未经本公众号许可，不得转载

Gradle 中插件可以分为两类：脚本插件和对象插件。

### 脚本插件

> 脚本插件就是一个普通的 gradle 构建脚本，通过在一个 foo.gradle 脚本中定义一系列的 task，另一个构建脚本 bar.gradle 通过`apply from:'foo.gradle'`即可引用这个脚本插件。

首先在项目根目录下新建一个 config.gradle 文件，在该文件中定义所需的 task。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwnI5aOewRSYiaOXhM9UEPxN0NSNoydcw6TniciaUypDDB92wyuGgFRXyJOZQfibThtcBmI3yJd0cFm8A/640?wx_fmt=png)

```
//config.gradle
project.task("showConfig") {
     doLast {
        println("$project.name:showConfig")
    }
}


```

然后在需要引用的 module 的构建脚本中引用`config.gradle`, 例如在`app.gradle`中, 由于`config.gradle`建立在根目录下，与 app 这个模块平级，所以需要注意路径问题`../config.gradle`

```
//app.gradle
apply from: '../config.gradle'


```

就是这么简单，此时运行 gradle 构建即可执行 showConfig 这个 task，

```
garretdeMacBook-Pro:CustomPlugin garretwei$ ./gradlew showConfig
> Task :app:showConfig
app:showConfig


```

### 对象插件

> 对象插件是指实现了 org.gradle.api.Plugin 接口的类。Plugin 接口需要实现`void apply(T target)`这个方法。该方法中的泛型指的是此 Plugin 可以应用到的对象，而我们通常是将其应用到 Project 对象上。

编写对象插件主要有三种方式：

1.  直接在 gradle 脚本文件中
    
2.  在 buildSrc 目录下
    
3.  在独立的项目下
    

#### 在 gradle 脚本文件中

直接在 gradle 脚本中编写这个方式是最为简单的。打开`app.gradle`文件，在其中编写一个类实现 Plugin 接口。

```
//app.gradle
class CustomPluginInBuildGradle implements Plugin<Project> {
    @Override
    void apply(Project target) {
       target.task('showCustomPluginInBuildGradle'){
            doLast {
                println("task in CustomPluginInBuildGradle")
            }
        }
    }
}


```

然后通过插件类名引用它

```
//app.gradle
apply plugin: CustomPluginInBuildGradle


```

执行插件中定义的 task

```
garretdeMacBook-Pro:CustomPlugin garretwei$ ./gradlew -q showCustomPluginInBuildGradle
task in CustomPluginInBuildGradle


```

#### 在 buildSrc 目录下

除了直接写在某个模块的构建脚本中，我们还可以将插件写在工程根目录下的 buildSrc 目录下，这样可以在多个模块之间复用该插件。

虽然 buildSrc 是 Gradle 在项目中配置自定义插件的默认目录，但它并不是标准的 Android 工程目录，所以使用这种方式需要我们事先手动创建一个 buildSrc 目录。目录结构如下：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwnI5aOewRSYiaOXhM9UEPxNja8mT8IZevk2orLMyWGcYm3cGyYWXID2a9icW2ErsQCVDjAic47iclmZA/640?wx_fmt=png)

buildSrc 目录结构  

在 buildSrc/src/main/groovy 目录下创建自定义 plugin，并在 build.gradle 中引用 groovy 插件

```
//  buildSrc/build.gradle
apply plugin: 'groovy'
dependencies {
    compile gradleApi()
    compile localGroovy()
}


```

然后编写 plugin 代码

```
class CustomPluginInBuildSrc implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.task('showCustomPluginInBuildSrc') {
            doLast {
                println('task in CustomPluginInBuildSrc')
            }
        }
    }
}


```

由于 buildSrc 目录是 gradle 默认的目录之一，该目录下的代码会在构建是自动编译打包，并被添加到 buildScript 中的 classpath 下，所以不需要任何额外的配置，就可以直接被其他模块的构建脚本所引用。

注意这里引用的方式可以是通过类名引用，也可以通过给插件映射一个 id，然后通过 id 引用。

通过类名引用插件的需要使用全限定名，也就是需要带上包名，或者可以先导入这个插件类，如下

```
apply plugin: com.gary.plugin.CustomPluginInBuildSrc


```

或者

```
import com.gary.plugin.CustomPluginInBuildSrc
apply plugin: CustomPluginInBuildSrc


```

通过简单的 id 的方式，我们可以隐藏类名等细节，使的引用更加容易。映射方式很简单，在 buildSrc 目录下创建 resources/META-INF/gradle-plugins/xxx.properties, 这里的 xxx 也就是所映射的 id，这里我们假设取名 myplugin。具体结构可参考上文 buildSrc 目录结构。

myplugin.properties 文件中配置该 id 所对应的 plugin 实现类

```
implementation-class=com.gary.plugin.CustomPluginInBuildSrc


```

此时就可以通过 id 来引用对于的插件了

```
//app.gradle
apply plugin: 'myplugin'


```

#### 在独立工程下

在 buildSrc 下创建的 plugin 只能在该工程下的多个模块之间复用代码。如果想要在多个项目之间复用这个插件，我们就需要在一个单独的工程中编写插件，将编译后的 jar 包上传 maven 仓库。

这里为了不增加复杂度，我们还是在该工程下创建一个 standaloneplugin 模块。只需要明白我们完全可以在一个独立的工程下来编写插件。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwnI5aOewRSYiaOXhM9UEPxNgyXJBZ7iaPF0VPFpkvZQwvfNZJmLf7ib8hdpZibbmwMNbXs0ibRUMybibZQ/640?wx_fmt=png)

standAlonePlugin 目录结构  

从目录结构来看，和 buildSrc 目录是一致的。区别在于 buildSrc 下的代码在构建时会自动编译并被引用。而我们在独立项目中编写的插件如果要能正确的被引用到，需要上传到 maven 仓库中，然后显式地在需要引用的项目中的 buildSrcipt 中添加对该构件的依赖。

插件代码

```
class StandAlonePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.task('showStandAlonePlugin') {
            doLast {
                println('task in StandAlonePlugin')
            }
        }
    }
}


```

插件项目构建脚本

```
apply plugin: 'groovy'
apply plugin: 'maven'
dependencies {
    compile gradleApi()
    compile localGroovy()
}
group = 'com.gary.plugin'
version = '1.0.0'
uploadArchives {
    repositories {
        mavenDeployer {
            repository(url: uri('../repo'))
        }
    }
}


```

这里与 buildSrc 不同的是，我们引用了`apply plugin 'maven'`，通过 maven 插件，我们可以轻松的配置 group，version 以及 uploadArchives 的相关属性，然后执行`./gradlew uploadArchives`这个任务，就可以将构件打包后上传到 maven 仓库了。同样为了示例简单，我们上传到一个本地仓库`repository(url: uri('../repo'))`中。

上传之后就可以在项目根目录下找到`repo`这个目录了。最后我们通过给根目录下的 build.gradle 配置 buildScript 的 classpath，就可以引用这个插件了。注意，classpath 的格式为`group:artifact:version`

```
buildscript {
    repositories {
        maven {
            url uri('repo')
        }
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
        classpath 'com.gary.plugin:standaloneplugin:1.0.0'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}


```

引用插件

```
//app.gradle
apply plugin: 'standAlonePlugin'


```

执行 StandAlonePlugin 中定义的任务

```
./gradlew -q showStandAlonePlugin
task in StandAlonePlugin


```

### 总结

*   插件分为脚本插件和对象插件。
    
*   脚本插件通过`apply from: 'foo.gradle'`引用。
    
*   对象插件可以在当前构建脚本下直接编写，也可以在 buildSrc 目录下编写，还可以在完全独立的项目中编写，通过插件类名或是 id 引用。`apply plugin: ClassName`或者`apply plugin:'pluginid'`
    

本文所编写的插件代码只是起到简单说明，主要介绍插件编写方式。实际插件编写过程中需要用到的 Extension，以及 Project 和 Gradle 相关的生命周期方法等 api 并未涉及。相关知识点可以参考本系列文章中的【实战，从 0 到 1 完成一款 Gradle 插件】。

欢迎长按下图 -> 识别图中二维码  
或者 扫一扫 关注我的公众号

![](https://mmbiz.qpic.cn/mmbiz_jpg/zKFJDM5V3WzPPAU3mhjBXh3pjqyS21MrTwMTGbjwlb2FpyUjdAqYh8hQU6OAWaeibqVCrQmqqgoNHOlHK0Aaib7w/640?wx_fmt=jpeg)

也想获得写作赞助？点击下面的 **阅读原文 **来加入`玉刚说写作平台`吧！