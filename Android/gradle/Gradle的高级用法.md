# gradle的高级用法

### 基础知识

首先我们要知道gradle中有一个功能叫做变体「productflavors」，这是来为APP设置不同的打包配置，以实现多渠道打包的一种方案。

```gradle
android {
  ...
  buildTypes {
     debug {
          ...
        }
        qa {
          ...
        }
        release {
          ...
         }
  }
  productFlavors {
      baidu{}
      _360{}
      yingyongbao{}
  }
```
这样的话最后打包的时候就可以生成9种包：
```
· baiduDebug
· baiduQa
· baiduRelease
· _360Debug
· _360Qa
· _360Release
· yingyongbaoDebug
· yingyongbaoQa
· yingyongbaoRelease
```

在Android Studio左下角可以找到并在每次build的时候选择不同种类的包
![](http://olg7c0d2n.bkt.clouddn.com/18-2-11/81618902.jpg)

注意名称不能用数字，所以我这里没有用360。

### 实现分渠道配置

在gradle中有一个功能叫「buildConfigField」，可以在系统的buildconfig中设置一个值。如下：
`buildConfigField 'boolean', 'ISB', 'true'`

这样就可以在app中使用boolean类型的变量BuildConfig.ISB=true
这里也可以建立一个string值，如下：

`buildConfigField 'String', 'val', '"content"'`

### 配置manifest变量

很多第三方sdk喜欢在manifest中配置appkey等，可以在gradle中使用：
```
 manifestPlaceholders = [UMENG_CHANNEL: "0",
                UMENG_APPKEY : "123456789"]
```
然后在manifest中配置
```xml
<meta-data
    android:name="UMENG_APPKEY"
    android:value="${UMENG_APPKEY}" />
<meta-data
    android:name="UMENG_CHANNEL"
    android:value="${UMENG_CHANNEL}" />
```
就可以实现。

### 配置包名

在gradle中包名用applicationId代表。还可以使用applicationIdSuffix在后面加一个后缀。
比如原包名是
```
applicationId "com.a.b"
```
使用
```
applicationIdSuffix ".c"
```
最终打包以后的包名就是com.a.b.c。

### 配置版本号

是的，versionCode和versionName也可以自己配置，毕竟他们是一个变量~

## 实战

前面说了这么多可配置的项，其实我还有一点没有说，那就是这一切都是可以在productFlavors和buildTypes配置的！

所以我们可以利用这点实现更高级的需求。

### 日志开关

```gradle
    debug {
        buildConfigField 'boolean', 'DEBUG', 'true'
    }
    releaseTest {
         buildConfigField 'boolean', 'DEBUG', 'true'
    }
    release {
         buildConfigField 'boolean', 'DEBUG', 'false'
    }
```
在buildTypes中配置，release配置为false 其他为true。便于调试

### api环境地址

一般情况下后端的api地址区别就在于域名，所以在debug releaseTest release可以使用不同的域名
```gradle
    debug {
        buildConfigField 'String', 'API_HOST', '"debug.com"'
    }
    releaseTest {
         buildConfigField 'String', 'API_HOST', '"releaseTest.com"'
    }
    release {
         buildConfigField 'String', 'API_HOST', '"release.com"'
    }
```

### 不同的包名 版本号

这些都是可以使用自带的属性进行配置。在不同的buildTypes中赋值不同即可。

```gradle
productFlavors {
        li {
            applicationId "com.xfhy.li"   //包名
            versionCode 2
            versionName "1.0.2"
        }
        zhang {
            applicationId "com.xfhy.zhang"
            versionCode 1
            versionName "1.0.3"
        }
```
