
# 编译多个APP,且不同签名,包名,界面,字段等

首先在项目里面新建一个需要打包成其他样子(不同签名,包名,界面,字段)的app目录  类似下面这样  需要改什么就放进去什么,我这里是做测试,所以把所有东西都放进去了.比如如果只是某个布局不一样的话,直接把那个布局放进去就行了(注意res目录结构)

下面我新建了一个 小李(与主工程不一样,大概是备胎吧) 

![](http://olg7c0d2n.bkt.clouddn.com/18-2-11/38158483.jpg)

我们在 app的gradle下 添加如下代码
```gradle
android {
    compileSdkVersion 26
    buildToolsVersion "26.0.2"
    defaultConfig {
        applicationId "com.persons.zhang"
        minSdkVersion 15
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
    }
    ...
  flavorDimensions "app"

    //定义不同的产品
    productFlavors {
        li {
            dimension "app"
            applicationId "com.xfhy.li"   //包名
            versionCode 2
            versionName "1.0.2"

            // assets.srcDir('li/assets')       指定assets目录
            // manifest.srcFile('li/AndroidManifest.xml')   指定manifest目录

            // 签名配置注意看这里---------------------------------------------
            signingConfig signingConfigs.li

            //配置AndroidManifest的占位
            manifestPlaceholders {
                MOB_APP_KEY: "5345645645456"
                QQ_APP_KEY: "d4das654d56as456d"
            }
        }
        zhang {
            dimension "app"
            applicationId "com.xfhy.zhang"
            versionCode 1
            versionName "1.0.3"

            signingConfig signingConfigs.zhang

            //配置AndroidManifest的占位
            manifestPlaceholders {
                MOB_APP_KEY: "4d53as456das456"
                QQ_APP_KEY: "d45asf4a5dasfa"
            }
        }
    }

    //指定资源目录
    sourceSets {
        li {
            java.srcDir('li/java')
            res.srcDir('li/res')
        }
    }

    signingConfigs {
        // 只是用来做演示，没有特地生成签名文件
        li {
            storeFile file("src/li/liKeystore.jks")
            storePassword "li123"
            keyAlias "li"
            keyPassword "li1234"
            // 开启 V2 签名
            v2SigningEnabled true
        }
        zhang {
            storeFile file("src/zhang/liKeystore.jks")
            storePassword "zhang123"
            keyAlias "zhang"
            keyPassword "zhang1234"
            v2SigningEnabled true
        }
    }
}
```

现在我们可以通过修改string.xml和layout改变布局那些了,也可以修改不同的逻辑代码了,当然资源也可以修改为不一样的.通过上面的注释应该能看懂了.

### 批量打包

> 点一下就行了,如下图

- 单独打包
- 打包所有产品(上面配置的productFlavors)

![](http://olg7c0d2n.bkt.clouddn.com/18-2-11/82429949.jpg)

### Manifest value 值设置占位符

在 Manifest 中占位
添加如下代码:
```xml
<application
        android:allowBackup="true">
        ...
        <meta-data
            android:name="Mob-AppKey"
            android:value="${MOB_APP_KEY}"/>
        <meta-data
            android:name="QQ-AppKey"
            android:value="${QQ_APP_KEY}"/>
            ...
</manifest>       
```     

然后在gradle中配置

```gradle
//定义不同的产品
    productFlavors {
        li {
            dimension "app"
            ...
            //配置AndroidManifest的占位
            manifestPlaceholders {
                MOB_APP_KEY: "5345645645456"
                QQ_APP_KEY: "d4das654d56as456d"
            }
        }
        zhang {
            dimension "app"
            ...

            //配置AndroidManifest的占位
            manifestPlaceholders {
                MOB_APP_KEY: "4d53as456das456"
                QQ_APP_KEY: "d45asf4a5dasfa"
            }
        }
    }
```