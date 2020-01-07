> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/singwhatiwanna/article/details/78797506 [](http://creativecommons.org/licenses/by-sa/4.0/)版权声明：本文为博主原创文章，遵循 [CC 4.0 BY-SA](http://creativecommons.org/licenses/by-sa/4.0/) 版权协议，转载请附上原文出处链接和本声明。 本文链接：[https://blog.csdn.net/singwhatiwanna/article/details/78797506](https://blog.csdn.net/singwhatiwanna/article/details/78797506)

### 什么是 Gradle？

一个像 Ant 一样的非常灵活的通用构建工具  
一种可切换的, 像 maven 一样的基于合约构建的框架  
支持强大的多工程构建  
支持强大的依赖管理 (基于 ApacheIvy)  
支持已有的 maven 和 ivy 仓库  
支持传递性依赖管理, 而不需要远程仓库或者 pom.xml 或者 ivy 配置文件  
优先支持 Ant 式的任务和构建  
基于 groovy 的构建脚本  
有丰富的领域模型来描述你的构建

### 如何学习 Gradle？

*   学习 Groovy（[http://docs.groovy-lang.org/](http://docs.groovy-lang.org/)）
*   学习 Gradle DSL（[https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html)）
*   学习 Android DSL 和 Task（[http://google.github.io/android-gradle-dsl/current/index.html](http://google.github.io/android-gradle-dsl/current/index.html)）

### 使用 Gradle wrapper

如果你本地安装了 Gradle，那么你就可以使用 gradle 命令来直接构建。如果本地没有安装，那么可以通过 gradle wrapper 来构建，Linux 和 MAC 使用./gradlew，而 Windows 上面则使用 gradlew，还可以在 gradle/gradle-wrapper.properties 中配置 Gradle 版本。

### Gradle 脚本的执行时序

Gradle 脚本的执行分为三个过程：

*   初始化  
    分析有哪些 module 将要被构建，为每个 module 创建对应的 project 实例。这个时候 settings.gradle 文件会被解析。
    
*   配置：处理所有的模块的 build 脚本，处理依赖，属性等。这个时候每个模块的 build.gradle 文件会被解析并配置，这个时候会构建整个 task 的链表（这里的链表仅仅指存在依赖关系的 task 的集合，不是数据结构的链表）。
    
*   执行：根据 task 链表来执行某一个特定的 task，这个 task 所依赖的其他 task 都将会被提前执行。
    

下面我们根据一个实际的例子来详细说明。这里我们仍然采用 VirtualAPK 这个开源项目来做演示，它的地址是：[https://github.com/didi/VirtualAPK](https://github.com/didi/VirtualAPK)。

我们以它的宿主端为例，宿主端有如下几个模块：  
![](https://img-blog.csdn.net/20171213214401054?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2luZ3doYXRpd2FubmE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)  
其实 buildSrc 是 virtualapk-gradle-plugin，为了便于调试我将其重命名为 buildSrc。他们的依赖关系如下：

![](https://img-blog.csdn.net/20171213215106177?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2luZ3doYXRpd2FubmE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

解释一下，app 模块依赖 CoreLibrary 和 buildSrc，CoreLibrary 又依赖 AndroidStub。为了大家更好理解，下面加一下 log。

```
1
2
3
4
5
6
7
/***** Settings.gradle *****/

println "settings start"
include ':app'
include ':CoreLibrary'
include ':AndroidStub'
println "settings end"

```

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
/***** VirtualAPK.gradle *****/

println "virtualapk start"

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
println "virtualapk end"

```

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
/***** app.gradle *****/

println "app config start"

apply plugin: 'com.android.application'
apply plugin: 'com.didi.virtualapk.host'

dependencies {
    compile project (":CoreLibrary")
}

project.afterEvaluate {
    println "app evaluate start"
    println "app evaluate end"
}

println "app config end"

```

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
/***** CoreLibrary.gradle *****/

apply plugin: 'com.android.library'
println "corelib config start"

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:22.2.0'

    provided project(':AndroidStub')
}

apply from: 'upload.gradle'
println "corelib config end"

```

```
1
2
3
4
5
6
7
8
9
/***** AndroidStub.gradle *****/

println "androidstub config start"

dependencies {
    compile 'com.android.support:support-annotations:22.2.0'
}

println "androidstub config end"

```

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
/***** buildSrc *****/

public class VAHostPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        println "VAHostPlugin config start"

        project.afterEvaluate {
            println "VAHostPlugin evaluate start"

            project.android.applicationVariants.each { ApplicationVariant variant ->
                generateDependencies(variant)
                backupHostR(variant)
                backupProguardMapping(variant)
            }
            println "VAHostPlugin evaluate end"

        }

        println "VAHostPlugin config end"

    }
}

```

现在随便执行一个 task，比如`./gradlew clean`，那么将会输出如下日志，大家对比着日志，应该能明白 Gradle 脚本的执行顺序了吧。

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
VirtualAPK renyugang$ ./gradlew clean
settings start
settings end
virtualapk start
virtualapk end
androidstub config start
androidstub config end
Incremental java compilation is an incubating feature.
corelib config start
corelib config end
app config start
VAHostPlugin config start
VAHostPlugin config end
app config end
VAHostPlugin evaluate start
VAHostPlugin evaluate end
app evaluate start
app evaluate end
:clean
:AndroidStub:clean
:CoreLibrary:clean
:app:clean

BUILD SUCCESSFUL

Total time: 12.381 secs


```

可以看到，Gradle 执行的时候遵循如下顺序：  
1. 首先解析 settings.gradle 来获取模块信息，这是初始化阶段；  
2. 然后配置每个模块，配置的时候并不会执行 task；  
3. 配置完了以后，有一个重要的回调`project.afterEvaluate`，它表示所有的模块都已经配置完了，可以准备执行 task 了；  
4. 执行指定的 task。

备注：如果注册了多个`project.afterEvaluate`回调，那么执行顺序等同于注册顺序。在上面的例子中，由于 buildSrc 中的回调注册较早，所以它也先执行。

本公众号聚焦于『Android 开发前沿、AI 技术、职业发展、生活感悟、妹子图』，欢迎大家关注：  
![](https://img-my.csdn.net/uploads/201707/23/1500824251_3475.jpg)