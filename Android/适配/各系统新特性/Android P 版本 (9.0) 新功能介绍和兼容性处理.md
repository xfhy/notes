> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/u011200604/article/details/82116320 [](http://creativecommons.org/licenses/by-sa/4.0/)版权声明：本文为博主原创文章，遵循 [CC 4.0 BY-SA](http://creativecommons.org/licenses/by-sa/4.0/) 版权协议，转载请附上原文出处链接和本声明。 本文链接：[https://blog.csdn.net/u011200604/article/details/82116320](https://blog.csdn.net/u011200604/article/details/82116320)

P 新特性
=====

Android P 在 Google IO2018 正式发版，全新的手势操作选项。底部虚拟键将由小白点和一颗返回键取代。通过轻触回到主页、长按呼出语音助手。新的特性主要有：

*   利用 Wi-Fi RTT 进行室内定位
    
*   刘海平 API 支持
    
*   通知栏功能增强
    
*   多摄像头支持和摄像头更新
    
*   HDR VP9 视频、HEIF 图像压缩和 Media API
    

详细可参考 [Google 官方文档](https://developer.android.google.cn/preview/features)介绍。

P 兼容优化
======

根据官方的 API 迁移指南，对应用比较影响的有如下几点：

non-SDK 接口的使用
-------------

一般来说，SDK 接口是指在 Android 框架[软件包索引](https://developer.android.google.cn/reference/packages)中记录的接口。 对非 SDK 接口的处理是 API 抽象化的实现细节；其会随时更改，恕不另行通。

Android P 引入了针对非 SDK 接口的新使用限制，无论是直接使用还是通过反射或 JNI 间接使用。 无论应用是引用非 SDK 接口还是尝试使用反射或 JNI 获取其句柄，均适用这些限制。

### 名单分类

*   Light grey list: targetSDK>=P 时，警告；
    
*   Dark grey list: targetSDK<P 时，警告；>=p 时，不允许调用；
    
*   Black list: 三方应用不允许调用；
    

[名单查看](https://android.googlesource.com/platform/frameworks/base/+/master/config/)

### 具体影响

<table><tbody><tr><td><p>访问方式</p></td><td><p>结果</p></td></tr><tr><td><p>Dalvik 指令引用字段</p></td><td><p>引发 NoSuchFieldError</p></td></tr><tr><td><p>Dalvik 指令引用函数</p></td><td><p>引发 NoSuchMethodError</p></td></tr><tr><td><p>通过 Class.getDeclaredField() 或 Class.getField() 反射</p></td><td><p>引发 NoSuchFieldException</p></td></tr><tr><td><p>通过 Class.getDeclaredMethod() 或 Class.getMethod() 反射</p></td><td><p>引发 NoSuchMethodException</p></td></tr><tr><td><p>通过 Class.getDeclaredFields() 或 Class.getFields() 反射</p></td><td><p>结果中未出现非 SDK 成员</p></td></tr><tr><td><p>通过 Class.getDeclaredMethods() 或 Class.getMethods() 反射</p></td><td><p>结果中未出现非 SDK 成员</p></td></tr><tr><td><p>通过 env-&gt;GetFieldID() 调用 JNI</p></td><td><p>返回 NULL，引发 NoSuchFieldError</p></td></tr><tr><td><p>通过 env-&gt;GetMethodID() 调用 JNI</p></td><td><p>返回 NULL，引发 NoSuchMethodError</p></td></tr></tbody></table>

挖孔屏适配
-----

谷歌 P 版本提供了统一的挖孔屏方案和三方适配挖孔屏方案：

*   对于有状态栏的页面，不会受到挖孔屏特性的影响；
    
*   全屏显示的页面，系统挖孔屏方案会对应用界面做下移避开挖孔区显示；
    
*   已经适配的 P 的应用的全屏页面可以通过谷歌提供的适配方案使用挖孔区，真正做到全屏显示
    

总的来说，就是 P 版本已经坐了兼容，全屏显示和状态栏显示，都会避开挖空区域显示。

但注意对于沉浸式的显示要注意，避免挖空挡住 UI 布局，需要做好适配。

关于 P 版本全面屏适配请参考上篇: 

[全面屏 / 刘海屏及虚拟键适配 -- 总结版](https://blog.csdn.net/u011200604/article/details/81698725)

Battery Improvements
--------------------

谷歌在 P 版本之前没有一个完整的功耗解决方案，OEM 厂商分别开发各自的功耗方案，管控手段都包括了清理应用，功耗得到优化，但是同时也影响了三方应用的一些功能正常使用，谷歌为了解决这个问题在 P 版本提出了自己的功耗解决方案。

### 主要方案：

*   AAB（Auto Awesome Battery)：
    

1、通过 ML 算法将应用进行分类，不同类型的应用功耗管控策略不一样

2、 Firebase Cloud Messaging (FCM): 管控三方消息接收的频率

3、谷歌提供了统一的应用的管控方法：Forced App Standby (FAS)，谷歌不会通过清理应用来优化功耗

*   Extreme Battery Saver（EBS）谷歌超级省电模式；
    
*   Smart screen brightness：屏幕亮度调节优化算法。
    

### 影响

谷歌功耗方案对三方应用各种管控，存在导致应用后台功能无法正常使用的可能，特别是：IM、邮箱、闹钟、音乐（直播）、地图导航、运动健康、下载、日历等应用影响比较大。目前通过谷歌提供的调试命令验证：所有的应用都有可能会被分到管控的类型，对三方的后台功能是有影响的。

不允许共享 WebView 数据目录
------------------

应用程序不能再跨进程共享单个 WebView 数据目录。如果您的应用有多个使用 WebView，CookieManager 或 android.webkit 包中的其他 API 的进程，则当第二个进程调用 WebView 方法时，您的应用将崩溃。

该特性只影响已经适配 P 的应用，也就是 targetSDK Version>=P。

移除对 Build.serial 的直接访问
----------------------

现在，需要 Build.serial 标识符的应用必须请求 READ_PHONE_STATE 权限，然后使用 Android P 中新增的新 Build.getSerial() 函数

SELinux 禁止访问应用的数据目录
-------------------

系统强制每个应用的 SELinux 沙盒对每个应用的私有数据目录强制执行逐个应用的 SELinux 限制。现在，不允许直接通过路径访问其他应用的数据目录。应用可以继续使用进程间通信 (IPC) 机制（包括通过传递 FD）共享数据

项目升级遇到问题
--------

这里注意我们应用是直接在 24 升级到 28.

1、java.lang.SecurityException: Failed to find provider null for user 0; expected to find a valid ContentProvider for this authority

问题原因：项目使用了 ActiveAndroid，在 8.0 或 8.1 系统上使用 26 或以上的版本的 SDK 时，调用 ContentResolver 的 notifyChange 方法通知数据更新，或者调用 ContentResolver 的 registerContentObserver 方法监听数据变化时，会出现上述异常。

解决方案：

在清单文件配置

```
<provider
android:
android:authorities="com.ylmf.androidclient"
android:enabled="true"
android:exported="false">
</provider>
<provider
 
android:
 
android:authorities="com.ylmf.androidclient"
 
android:enabled="true"
 
android:exported="false">
 
</provider>

```

2、CLEARTEXT communication to [life.115.com](http://life.115.com/) not permitted by network security policy

问题原因： Android P 限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉

解决方案：

在资源文件新建 xml 目录，新建文件

```
<?xml version="1.0" encoding="utf-8"?><network-security-config>
<base-config cleartextTrafficPermitted="true" /></network-security-config>
清单文件配置：android:networkSecurityConfig="@xml/network_security_config"
<?xml version="1.0" encoding="utf-8"?><network-security-config>
 
<base-config cleartextTrafficPermitted="true" /></network-security-config>
 
清单文件配置：android:networkSecurityConfig="@xml/network_security_config"

```

但还是建议都使用 https 进行传输

3、8.0，静态广播无法正常接收

问题原因： Android 8.0 引入了新的广播接收器限制，因此您应该移除所有为隐式广播 Intent 注册的广播接收器

解决方案：

使用动态广播代替静态广播

4、Caused by: java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation

问题原因： Android 8.0 非全屏透明页面不允许设置方向

解决方案：android:windowIsTranslucent 设置为 false

总结
==

注意事项
----

1、随着 Google 新的版本发布，项目应尽快跟进兼容。否则后面有多个版本的迭代，遗留的坑会比较多

2、项目可在独立分支升级兼容，然后进行业务功能测试，发现问题进行针对性处理。可先解决一些闪退，功能不正常的问题。

3、多参考 Google 官方的版本适配介绍，有详细的技术指导

参考:

[Google 官方文档](https://developer.android.google.cn/preview/) [https://developer.android.google.cn/about/versions/pie/](https://developer.android.google.cn/about/versions/pie/)

[Android P 版本应用兼容性适配技术指导](https://devcenter.huawei.com/consumer/cn/devservice/doc/50115) [https://devcenter.huawei.com/consumer/cn/devservice/doc/50115](https://devcenter.huawei.com/consumer/cn/devservice/doc/50115)

[Android P 版本 新功能介绍和兼容性处理](https://blog.csdn.net/yi_master/article/month/2018/04) [https://blog.csdn.net/yi_master/article/month/2018/04](https://blog.csdn.net/yi_master/article/month/2018/04)