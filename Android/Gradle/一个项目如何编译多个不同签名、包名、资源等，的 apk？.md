> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/OQtAVhQVPNVxo9zJc3NG9w

本文作者

作者：**徐佳吉**

链接：

https://blog.xujiaji.com/post/android-project-one-for-more

本文由作者授权发布。

这类文章实操指数非常高，非常建议跟着作者一起操作一波，便于巩固记忆，遇到问题也可以参考作者的完整配置，单纯的阅读忘得很快。

_1_

简介

如题所示！本篇文章就是为了解决这种问题。方便打包和运行的时候能做到无需手动替换配置，即可打包想要的 apk。打包的时候，只需选一下想打哪种配置的 apk 就 OK 啦。 (^o^)/~
--------------------------------------------------------------------------------------------

> 先来看，有需求如下：

1.  同一个项目
    
2.  不同的 apk 图标
    
3.  不同的服务器域名
    
4.  不同的包名
    
5.  不同的名称
    
6.  不同的签名
    
7.  不同的第三方 key
    
8.  不同的版本名版本号
    

**解决思路**

1.  当然最直接的方式不过于每次打不同包的时候都去替换对应的配置，这种方式的麻烦之处不言而喻。
    
2.  将所有配置，资源等都配置入项目中，打包的时候，根据选择渠道打包不同配置的 apk。（本篇文章就是要讲怎么这么做的）
    
3.  相信还有其他的。。。
    

_2_

相关的几个要点

**1. 首先我们需要知道 productFlavors 来配置渠道，这里我将渠道用来表示哪种 apk，如下我需要配置四种应用:**

```
productFlavors {
  userquhua {}
  quhua {}
  cuntuba {}
  xemh {}
}


```

**2. 如果我们选择了某一个渠道，那么运行打包的时候会根据渠道名选择资源文件（可结合第 6 点一起看）**

  
![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwMkRvxqEvwNwsFw5IDKiaFjY1wHfiacqTMW1XHecpfEwoTQWIOVrNwUIbNicdm2eZ6w1rHCgMibkIvb6g/640?wx_fmt=png)

**3. 签名可在 signingConfigs 中配置多个（我将所有签名文件放在了项目跟目录的 key 文件夹中），这样我们就可以通过 signingConfigs 指定预制好的签名配置。**

```
signingConfigs {
    userquhuaRelease {
        storeFile file("../key/xxx1.keystore")
        storePassword "xxxxxx"
        keyAlias "alias"
        keyPassword "xxxxxx"
    }

    quhuaRelease {
        storeFile file("../key/xxx2.keystore")
        storePassword "xxxxxx"
        keyAlias "alias"
        keyPassword "xxxxxx"
    }

    cuntubaRelease {
        storeFile file("../key/xxx3.keystore")
        storePassword "xxxxxx"
        keyAlias "alias"
        keyPassword "xxxxxx"
    }

    xemhRelease {
        storeFile file("../key/xxx4.keystore")
        storePassword "xxxxxx"
        keyAlias "alias"
        keyPassword "xxxxxx"
    }
}


```

**4. 可在 build.gradle 中配置动态配置 java 代码调用的常量数据（如：通过该方式我们可根据不同渠道动态配置第三方 appid，或其他需要根据渠道而改变的数据）**

比如：我们在 defaultConfig {} 中定义了:

```
buildConfigField "String", "SERVER_URL", '"http://xx.xxxx.com/"'


```

此时，您看一下清单文件中 manifest 标签里的，package 的值，假如是：  

```
com.xxx.xx


```

那么，您就可以在 java 代码中通过导入文件：

```
import com.xxx.xx.BuildConfig;


```

然后调用  

```
BuildConfig.SERVER_URL


```

它的值就是上边配置的字符串：http://xx.xxxx.com/。  

你可以进入 BuildConfig 看一看，里面还包含了一些当前的包名版本号等信息。

  
![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwMkRvxqEvwNwsFw5IDKiaFjYt81ooyGmWNSJloXWv7IFxCb57dfslyhjBrkvxCta5k8dgWnfU5uGGA/640?wx_fmt=png)  

**5. 在渠道配置那里可以配置对应的包名版本名签名等等  
**  

如下所示：

```
// 省略其他配置...
android {
  // 省略其他配置...
  productFlavors {
      userquhua {
          applicationId "com.xxx.xx"
          versionCode 1
          versionName "1.0.0"
          signingConfig signingConfigs.userquhuaRelease // 配置签名

          String qq_id = '"xxxxxxxxx"' //配置qq appid
          buildConfigField "String",           "QQ_ID", qq_id
          buildConfigField "String",           "WX_ID", '"wxxxxxxxxxxxxxxxxx"' // 配置微信appid
          manifestPlaceholders = [
            qq_id: qq_id,
            JPUSH_PKGNAME : applicationId,
            JPUSH_APPKEY : "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx", //JPush 上注册的包名对应的 Appkey.
            JPUSH_CHANNEL : "developer-default",
          ]
      }
  }

  buildTypes {
    release {
      // 省略其他配置...
        signingConfig null  // 置空
    }

    debug {
      // 省略其他配置...
        signingConfig null // 置空
    }
  }
}

```

这样，如果我们打包 userquhua 这个渠道，看第 2 点中介绍选择 userquhuaDebug。

然后，最好 clean 一下项目、然后我们运行项目。

该 app 的包名就是 com.xxx.xx，版本号为 1，版本名为 1.0.0。

通过 BuildConfig 调用 QQ_ID 静态常量，就是该渠道里配置的值，WX_ID 同理。  

manifestPlaceholders 配置也可以这样配置。  

签名问题经过个人反复尝试（然后半天就过去了￣へ￣），最终签名如上配置。

需要注意 buildTypes 中的签名配置 signingConfig 如果不设置为 null，那么打包的是有还是以内置的签名打包。

**6. 资源文件替换  
**  

再看到第 2 点的介绍，我们选择运行渠道后，会默认匹配对应渠道下的资源。下面我将 xemh 渠道的资源目录全部展开一下。

  
![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwMkRvxqEvwNwsFw5IDKiaFjY6uqeicl1zZLemovhr3MCMl9x1c3Vp0FLWvcxGYvqhJnEMWhbz4DON7g/640?wx_fmt=png)

如上图这样，只需要资源名字和 app 目录对应的文件名字一样即可替换。

strings.xml 里的应用名，只需要将对应 app_name 修改既可替换 app 下 strings 的 app_name，其他不用替换的不用写就行。

**7. 打正式包的时候选好渠道，就可以打包不同配置的 apk，当然您也可以使用命令的方式。**

![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwMkRvxqEvwNwsFw5IDKiaFjY2JhBibWlVDL52Nib1zVqWhKu4Gfry115KS0DHKldrIZ2K60cHW3uA99g/640?wx_fmt=png)

_3_

其他配置记录

获取当前时间

```
static def releaseTime() {
    return new Date().format("yyyy-MM-dd-HH.mm", TimeZone.getTimeZone("GMT+8"))
}


```

打包的时候，修改文件名，以方便区别渠道和版本打包时间

```
applicationVariants.all {
    variant ->
        variant.outputs.all {
            outputFileName = "${variant.productFlavors[0].name}-v${variant.productFlavors[0].versionName}-${releaseTime()}.apk"
        }
}


```

*   ${variant.productFlavors[0].name} 当前渠道名  
    
*   ${variant.productFlavors[0].versionName} 当前版本名
    
*   ${releaseTime()} 当前时间
    

_4_

其他需要注意的事项

如果您在清单文件 AndroidManifest.xml 中，有那种以包名开头命名的那种。因为如果包名都改了，有些也需要动态的改变。可以用 ${applicationId} 代替。在打包的时候，会自动替换成当前包名。

比如，类似下配置：

```
<permission
    android:
    android:protectionLevel="signature" />
<uses-permission android: />
<receiver
    android:
    android:enabled="true"
    android:exported="false" >
    <intent-filter>
        <action android: />
        <category android: />
    </intent-filter>
</receiver>
<provider
    android:
    android:authorities="com.xxx.xx.provider"
    android:exported="false"
    tools:replace="android:authorities"
    android:grantUriPermissions="true">
    <meta-data
        android:
        android:resource="@xml/file_paths" />
</provider>


```

可改为：

```
<permission
    android:name="${applicationId}.permission.JPUSH_MESSAGE"
    android:protectionLevel="signature" />
<uses-permission android:name="${applicationId}.permission.JPUSH_MESSAGE" />
<receiver
    android:
    android:enabled="true"
    android:exported="false" >
    <intent-filter>
        <action android: />
        <category android:name="${applicationId}" />
    </intent-filter>
</receiver>
<provider
    android:
    android:authorities="${applicationId}.provider"
    android:exported="false"
    tools:replace="android:authorities"
    android:grantUriPermissions="true">
    <meta-data
        android:
        android:resource="@xml/file_paths" />
</provider>


```

当然值得注意的是，在代码中我们也不能把包名写死了，可通过 BuildConfig 得到当前包名

_5_

我的完整配置，供参考

> 有关隐私信息的都用 xxx 替换了

1. 项目根目录的 build.gradle

```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.0'
        classpath "io.github.prototypez:save-state:0.1.7"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
        maven { url 'http://oss.jfrog.org/artifactory/oss-snapshot-local/' }
        flatDir {
            dirs 'libs'
        }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}

ext{
    minSdkVersion               = 16
    targetSdkVersion            = 27
    compileSdkVersion           = 27
    buildToolsVersion           = '27.1.1'

    supportLibraryVersion       = '27.1.1'
    xmvpVersion                 = '1.2.2'
    retrofit2Version            = '2.3.0'
    okhttp3Version              = '3.8.1'
    butterknifeVersion          = '8.6.0'
    rx2Version                  = '2.0.2'
    CircleProgressDialogVersion = '1.0.2'
    smarttabVersion             = '1.6.1@aar'
    adapterHelperVersion        = '2.9.41'
    glideVersion                = '4.7.1'
    roundedimageviewVersion     = '2.3.0'
    eventbusVersion             = '3.0.0'
    dispatcherVersion           = '2.4.0'
    picture_libraryVersion      = 'v2.2.3'
    statusbarutilVersion        = '1.5.1'
    okhttpUtilsVersion          = '3.8.0'
    constraintVersion           = '1.1.3'
    flexboxVersion              = '1.0.0'
}


```

2. app 目录下的 build.gradle

```
apply plugin: 'com.android.application'
apply plugin: 'save.state'

static def releaseTime() {
    return new Date().format("yyyy-MM-dd-HH.mm", TimeZone.getTimeZone("GMT+8"))
}

android {
    compileSdkVersion rootProject.compileSdkVersion
//    buildToolsVersion rootProject.buildToolsVersion
    defaultConfig {
        minSdkVersion rootProject.minSdkVersion
        targetSdkVersion rootProject.targetSdkVersion

        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        multiDexEnabled true
        // config the JSON processing library
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ serializer : "gson" ]
            }
        }

        ndk {
            abiFilters "armeabi-v7a"
        }
        renderscriptTargetApi 25
        renderscriptSupportModeEnabled true

    }
    signingConfigs {
        userquhuaRelease {
            storeFile file("../key/xxx.keystore")
            storePassword "xxxxxx"
            keyAlias "xxx"
            keyPassword "xxxxxx"
        }

        quhuaRelease {
            storeFile file("../key/xxx.keystore")
            storePassword "xxxxxxx"
            keyAlias "xxx"
            keyPassword "xxxxxxx"
        }

        cuntubaRelease {
            storeFile file("../key/xxx.keystore")
            storePassword "xxxxxxx"
            keyAlias "xxx"
            keyPassword "xxxxxxx"
        }

        xemhRelease {
            storeFile file("../key/xxx.keystore")
            storePassword "xxxxxxx"
            keyAlias "xxx"
            keyPassword "xxxxxxx"
        }
    }
    flavorDimensions "default"
    productFlavors {
        userquhua {
            applicationId "com.xxx.xx"
            versionCode 22
            versionName "1.7.5"
            signingConfig = signingConfigs.userquhuaRelease

            String qq_id = '"xxxxxx"'
            buildConfigField "String",           "QQ_ID", qq_id // qq appId
            buildConfigField "String",         "SINA_ID", '"xxxxxx"' // 新浪appId
            buildConfigField "String",           "WX_ID", '"xxxxxx"' // 微信 appId
            buildConfigField "String",           "UM_ID", '"xxxxxx"' // 友盟
            buildConfigField "String",       "WX_SECRET", '"xxxxxx"' // 微信 secret
            buildConfigField "String",   "SINA_REDIRECT", '"http://open.weibo.com/apps/xxxxxx/privilege/oauth"' // 新浪

            buildConfigField "String",   "ADHUB_INIT_ID", '"xxxxxx"' // 广告sdk初始化id
            buildConfigField "String", "ADHUB_SPLASH_ID", '"xxxxxx"' // 开屏广告id
            buildConfigField "String", "ADHUB_BANNER_ID", '"xxxxxx"' // banner广告id

            buildConfigField "String",      "SERVER_URL", '"http://xxx.xxx.com/"'
            buildConfigField "String",        "LOGO_URL", '"http://file.xxx.com/img/xxx.png"'

            manifestPlaceholders = [
                    qq_id: qq_id,
                    JPUSH_PKGNAME : applicationId,
                    JPUSH_APPKEY : "xxxxxx", //JPush 上注册的包名对应的 Appkey.
                    JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
            ]
        }

        quhua {
            applicationId "com.xxx.xx"
            versionCode 1
            versionName "1.0.0"
            signingConfig = signingConfigs.quhuaRelease

            String qq_id = '"xxxxxx"'
            buildConfigField "String",           "QQ_ID", qq_id
            buildConfigField "String",         "SINA_ID", '"xxxxxx"'
            buildConfigField "String",           "WX_ID", '"xxxxxx"'
            buildConfigField "String",           "UM_ID", '"xxxxxx"'
            buildConfigField "String",       "WX_SECRET", '"xxxxxx"'
            buildConfigField "String",   "SINA_REDIRECT", '"http://open.weibo.com/apps/xxxxxx/privilege/oauth"'

            buildConfigField "String",   "ADHUB_INIT_ID", '"xxxxxx"' // 广告sdk初始化id
            buildConfigField "String", "ADHUB_SPLASH_ID", '"xxxxxx"' // 开屏广告id
            buildConfigField "String", "ADHUB_BANNER_ID", '"xxxxxx"' // banner广告id

            buildConfigField "String",      "SERVER_URL", '"http://xx.xxx.com/"'
            buildConfigField "String",        "LOGO_URL", '"http://file.xxx.com/img/xxx.png"'

            manifestPlaceholders = [
                    qq_id: qq_id,
                    JPUSH_PKGNAME : applicationId,
                    JPUSH_APPKEY : "xxxxxx", //JPush 上注册的包名对应的 Appkey.
                    JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
            ]
        }

        cuntuba {
            applicationId "com.xxx.xx"
            versionCode 1
            versionName "1.0.0"
            signingConfig = signingConfigs.cuntubaRelease

            String qq_id = '"xxxxxx"'
            buildConfigField "String",           "QQ_ID", qq_id
            buildConfigField "String",         "SINA_ID", '"xxxxxx"'
            buildConfigField "String",           "WX_ID", '"xxxxxx"'
            buildConfigField "String",           "UM_ID", '"xxxxxx"'
            buildConfigField "String",       "WX_SECRET", '"xxxxxx"'
            buildConfigField "String",   "SINA_REDIRECT", '"http://open.weibo.com/apps/xxxxxx/privilege/oauth"'

            buildConfigField "String",   "ADHUB_INIT_ID", '"xxxxxx"' // 广告sdk初始化id
            buildConfigField "String", "ADHUB_SPLASH_ID", '"xxxxxx"' // 开屏广告id
            buildConfigField "String", "ADHUB_BANNER_ID", '"xxxxxx"' // banner广告id

            buildConfigField "String",      "SERVER_URL", '"http://xxx.xxxx.com/"'
            buildConfigField "String",        "LOGO_URL", '"http://file.xxx.com/img/xxx.png"'

            manifestPlaceholders = [
                    qq_id: qq_id,
                    JPUSH_PKGNAME : applicationId,
                    JPUSH_APPKEY : "xxxxxx", //JPush 上注册的包名对应的 Appkey.
                    JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
            ]
        }

        xemh {
            applicationId "com.xxx.xx"
            versionCode 1
            versionName "1.0.0"
            signingConfig = signingConfigs.xemhRelease

            String qq_id = '"xxxxxx"'
            buildConfigField "String",           "QQ_ID", qq_id
            buildConfigField "String",         "SINA_ID", '"xxxxxx"'
            buildConfigField "String",           "WX_ID", '"xxxxxx"'
            buildConfigField "String",           "UM_ID", '"xxxxxx"'
            buildConfigField "String",       "WX_SECRET", '"xxxxxx"'
            buildConfigField "String",   "SINA_REDIRECT", '"xxxxxx"'

            buildConfigField "String",   "ADHUB_INIT_ID", '"xxxxxx"' // 广告sdk初始化id
            buildConfigField "String", "ADHUB_SPLASH_ID", '"xxxxxx"' // 开屏广告id
            buildConfigField "String", "ADHUB_BANNER_ID", '"xxxxxx"' // banner广告id

            buildConfigField "String",      "SERVER_URL", '"http://xx.xxx.com/"'
            buildConfigField "String",        "LOGO_URL", '"http://file.xxxxxx.com/img/xxxxxx.png"'

            manifestPlaceholders = [
                    qq_id: qq_id,
                    JPUSH_PKGNAME : applicationId,
                    JPUSH_APPKEY : "xxxxxx", //JPush 上注册的包名对应的 Appkey.
                    JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
            ]
        }
    }

    applicationVariants.all {
        variant ->
            variant.outputs.all {
                outputFileName = "${variant.productFlavors[0].name}-v${variant.productFlavors[0].versionName}-${releaseTime()}.apk"
            }
    }

    buildTypes {
        release {
            // 不显示Log
            buildConfigField "boolean", "LOG_DEBUG", "false"
            signingConfig null
            minifyEnabled true
            zipAlignEnabled true
            // 移除无用的resource文件
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }

        debug {
            // 显示Log
            buildConfigField "boolean", "LOG_DEBUG", "true"
            signingConfig null
            minifyEnabled false
            zipAlignEnabled false
            shrinkResources false
        }
    }
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/NOTICE.txt'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/LICENSE.txt'
    }
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }

    dexOptions {

        javaMaxHeapSize "4g" //此处可根据电脑本身配置 数值越大 当然越快

        preDexLibraries = false

    }
}

repositories {
    flatDir {
        dirs 'libs', '../adpoymer/libs'
    }
}

dependencies {
    // 省略
}


```

**结束**  

就这样就可以解放大量劳动力啦！每次项目打包各种软件，选一下就 ojbk，哈哈哈~

  
如果有些配置在其他渠道没有的，也可通过 BuildConfig 在 java 中判断如果是某某渠道那么屏蔽。

推荐阅读：  

[Toast 不显示了？](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650826650&idx=1&sn=71f1ebddf0f62e51a43343968a54cbc4&chksm=80b7b304b7c03a12c3b08c186007648210d3e50174052ff185f0cca8f799ecc005f90434c228&scene=21#wechat_redirect)  

[但愿人长久，搬砖不再有](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650826616&idx=1&sn=e215938fa515c8c055db1e30e49e401c&chksm=80b7b3e6b7c03af00d3918461942323822d2337c837d68f712788fa81793ded2490ea6419be0&scene=21#wechat_redirect)  

![](https://mmbiz.qpic.cn/mmbiz_jpg/MOu2ZNAwZwP4yDt9RiaN89t9lxTz0vZWZy9sYR54YefTFFBPmPLwnAN9PNicI0rZznIYt4r2Q40DbAAiatTS1MlVw/640?wx_fmt=jpeg)

**扫一扫** 关注我的公众号

如果你想要跟大家分享你的文章，欢迎投稿~

┏(＾0＾)┛明天见！