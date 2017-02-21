# Android Studio项目 目录含义

[TOC]

## 1. .gradle和.idea

这两个目录下放置的都是Android Studio自动生成的一些文件，我们无须关心，也不要去手动编辑。

## 2.app

 项目中的代码、资源等内容几乎都是放置在这个目录下的，我们后面的开发工作也基本都是在这个目录下进行的，待会儿还会对这个目录单独展开进行讲解。

## 3. build

这个目录你也不需要过多关心，它主要包含了一些在编译时自动生成的文件。

## 4、gradle

 这个目录下包含了gradle wrapper的配置文件，使用gradle wrapper的方式不需要提前将gradle下载好，而是会自动根据本地的缓存情况决定是否需要联网下载gradle。Android Studio默认没有启动gradle wrapper的方式，如果需要打开，可以点击Android Studio导航栏 --> File --> Settings --> Build，Execution，Deployment --> Gradle，进行配置更改。

## 5、.gitignore

这个文件是用来将指定的目录或文件排除在版本控制之外的。

## 6、build.gradle

这是项目全局的gradle构建脚本，通常这个文件中的内容是不需要修改的。下面会详细分析gradle构建脚本中的具体内容。

## 7、gradle.properties

这个文件是全局的gradle配置文件，在这里配置的属性将会影响到项目中所有的gradle编译脚本。

## 8、gradlew和gradlew.bat

这两个文件是用来在命令行界面中执行gradle命令的，其中gradlew是在Linux或Mac系统中使用的，gradlew.bat是在Windows系统中使用的。

## 9、HelloWorld.iml

iml文件是所有IntelliJ IDEA项目都会自动生成的一个文件(Android Studio是基于IntelliJ IDEA开发的)，用于标识这是一个IntelliJ IDEA项目，我们不需要修改这个文件中的任何内容。

## 10、local.properties

这个文件用于指定本机中的Android SDK路径，通常内容都是自动生成的，我们并不需要修改。除非你本机中的Android SDK位置发生了变化，那么就将这个文件中的路径改成新的位置即可。

## 11、settings.gradle

这个文件用于指定项目中所有引入的模块。由于HelloWorld项目中就只有一个app模块，因此该文件中也就只引入了app这一个模块。通常情况下模块的引入都是自动完成的，需要我们手动去修改这个文件的场景可能比较少。


# app目录结构

>现在整个项目的外层目录结构已经介绍完了。你会发现，除了app目录之外，大多数的文件和目录都是自动生成的，我们并不需要进行修改。想必你已经猜到了，app目录下的内容才是我们以后的工作重点，展开之后结构如下：

![](http://img.blog.csdn.net/20170119141854262?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGhieGhic3E=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

那么下面我们就来对app目录下的内容进行更为详细的分析。

## 1、build

这个目录和外层的build目录类似，主要也是包含了一些在编译时自动生成的文件，不过它里面的内容会更多更杂，我们不需要过多关系。

## 2、libs

如果你的项目中使用到了第三方jar包，就需要把这些jar包都放在libs目录下，放在这个目录下的jar包都会被自动添加到构建路径里去。

## 3、AndroidTest

此处是用来编写Android Test测试用例的，可以对项目进行一些自动化测试。

## 4、java

毫无疑问，java目录是放置我们所有java代码的地方，展开该目录，你将看到我们刚才创建的HelloWorldActivity文件就在里面。

## 5、res

这个目录下的内容就有点多了。简单点说，就是你在项目中使用到的所有图片，布局，字符串等资源都要存放在这个目录下。当然这个目录下还有很多子目录，图片放在drawable目录下，布局放在layout目录下，字符串放在values目录下，所以你不用担心会把整个res目录弄得乱糟糟的。

## 6、AndroidManifest.xml

这是你整个Android项目的配置文件，你在程序中定义的所以四大组件都需要在这个文件里注册，另外还可以在这个文件中给应用程序添加权限声明。

## 7、test

此处是用来编写Unit Test测试用例的，是对项目进行自动化测试的另一种方式。

## 8、.gitignore

这个文件用于将app模块内的指定的目录或文件排除在版本控制之外，作用和外层的.gitignore文件类似。

## 9、app.iml

IntelliJ IDEA项目自动生成的文件，我们不需要关心或修改这个文件中的内容。

## 10、build.gradle

这是app模块的gradle构建脚本，这个文件中会指定很多项目构建相关的配置。

## 11、proguard-rules.pro

这个文件用于指定项目代码的混淆规则，当代码开发完成后打成安装包文件，如果不希望代码被别人破解，通常会将代码混淆，从而让破解者难以阅读。


# res目录

## 1. drawable

所有以drawable开头的文件夹都是用来存放图片的

## 2. mipmap

所有以drawable开头的文件夹都是用来存放应用图标的

## 3. values

所有以drawable开头的文件夹都是用来存放字符串,样式,颜色等配置的

## 4. layout

存放布局文件的

# 详解build.gradle文件

## 1.外部的build.gradle文件

	buildscript 
	{
	    repositories 
	    {
	        jcenter()
	    }
	    dependencies 
	    {
			//这是插件
	        classpath 'com.android.tools.build:gradle:2.1.2'
	
	        // NOTE: Do not place your application dependencies here; they belong
	        // in the individual module build.gradle files
	    }
	}
	
	allprojects 
	{
	    repositories 
	    {
	        jcenter()
	    }
	}

两处repositories的闭包中都声明了jcenter（）这行配置，其实他是一个代码托管仓库，很多Android开源项目都会选择将代码托管到jcenter上，声明了这行配置之后，我们就可以在项目中轻松引用任何jcenter上的开源项目了。 dependencies闭包中使用classpath声明了一个Gradle插件，我们用它来构建Android项目，后面是版本号。通常情况下你并不需要修改这个文件中的内容，除非你想添加一些全局的项目构建配置。

## 2.app目录下的build.gradle文件

	apply plugin: 'com.android.application'

	android 
	{
	    compileSdkVersion 23  //编译版本
	    buildToolsVersion "25.0.2"  指定项目构建工具的版本
	
	    defaultConfig 
	    {
	        applicationId "com.example.myapplication"  //包名
	        minSdkVersion 15  //最低兼容
	        targetSdkVersion 23  //充分测试
	        versionCode 1 //版本号
	        versionName "1.0"  //版本名
	    }
	    buildTypes 
	    {
	        release 
	        {
	            minifyEnabled false //是否混淆代码
				//指定混淆时使用的规则文件
	            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
	        }
	    }
	}
	
	dependencies  //指定当前项目所有的依赖关系
	{
	    compile fileTree(dir: 'libs', include: ['*.jar'])
	    testCompile 'junit:junit:4.12'
	    compile 'com.android.support:appcompat-v7:23.4.0'
	}

apply plugin: 'com.android.application

应用了一个插件，一般有两种值可选：com.android.application表示这是一个应用程序模块，com.android.library表示这是一个库模块。应用程序模块和库模块的最大区别在于，一个是可以直接运行的，一个只能作为代码库依附于别的应用程序模块来运行。

接下来是一个大的**Android闭包**，在这个闭包中我们可以配置项目构建的各种属性，其中compileSdkVersion用于指定项目的编译版本，24表示使用Android7.0系统的SDK编译，buildToolsVersion用于指定项目构建工具的版本，目前的版本是25.0.2。

里面嵌套的的**defaultConfig闭包**， applicationId用于指定项目的包名，我们在创建项目的时候已经指定过包名，如果你想在后面对其进行修改，那么就是在这里修改的， minSdkVersion用于指定项目最低兼容的Android系统版本，15代表兼容到Android4.0系统，targetSdkVersion指定的值表示你在该目标版本上已经做过了充分的测试，系统将会为你的应用程序启用一些最新的功能和特性，如果你将targetSdkVersion指定成23或者更高的话,系统就会为你的程序启用运行时权限功能，如果设置成22，22代表Android5.1系统，运行时权限就不会自然就不会启用。versionCode用于指定项目的版本号，versionName用于指定项目的版本名。这两个属性在生成安装文件的时候非常重要。

**buildTypes闭包**用于指定生成安装文件的相关配置，通常只会有两个子闭包，一个是debug,一个是release。debug闭包用于指定生成测试版安装文件的配置，release闭包用于指定生成正式版安装文件的配置，debug闭包是可以忽略不写的。

**release闭包**
minifyEnabled用于指定是否对项目的代码进行混淆，true表示混淆，false表示不混淆。proguardFiles用于指定混淆时使用的规则文件，这里指定了两个文件，第一个proguard-android.txt是在AndroidSDK目录下的，里面是所有项目通用的混淆规则，第二个proguard-rules.pro是在当前项目的根目录下的，里面可以编写当前项目特有的混淆规则.需要注意的是，通过Android Studio直接运行项目，生成的都是测试版安装文件。

**dependencies闭包**
这个闭包的功能非常强大，它可以指定当前项目所有的依赖关系，通常Android Studio项目一共有三种依赖方式：本地依赖，库依赖和远程依赖。本地依赖可以对本地的jar包或目录添加依赖关系，库依赖可以对项目中的库模块添加依赖关系，远程依赖则可以对jcenter库上的开源项目添加依赖关系，观察一下dependencies闭包中的配置：

compile fileTree就是一个本地依赖声明，他表示将libs目录下所有.jar后缀的文件都添加到项目的构建路径当中。

compile 'com.android.support:appcompat-v7:23.4.0'是远程依赖声明，其中com.android.support是域名部分，用于和其他公司的库作区分，appcompat-v7是组名称，用于和同一个公司中不同的库作区分，24.2.1是版本号，用于和同一个库不同的版本最区分。加上这句声明后，Gradle在构建项目时会首先检查一下本地是否已经有这个库的缓存，如果没有的话则会去自动联网下载，然后再添加到项目的构建路径当中。