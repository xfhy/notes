# Android O 8.0 新功能预览

> [Android官方API](https://developer.android.com/preview/index.html)已经更新,各位Android的开发者赶快去看看啊,又有新的功能,API和限制等等出台啦.在下在这里总结了以下几点,比较重要的功能,大神勿喷..

Android官方原文https://android-developers.googleblog.com/2017/03/first-preview-of-android-o.html,我只是写下一些简单的认识,哈哈.

## 1.后台限制(Background limits)

在Android 7.0的基础上,Android O提高了用户的电池寿命和设备的交互性能一大重点。其中,这些应用程序可以在后台做，在三个主要领域：隐式广播，后台服务和位置更新。这些变化会更容易地创建具有用户的设备和电池的影响最小的应用程序。[后台执行限制](https://developer.android.com/preview/features/background.html)和[后台位置限制](https://developer.android.com/preview/features/background-location-limits.html)到这里去看看细节.

## 2.通知的渠道(Notification channels)

Android O可以单独控制一个应用程序的通知,在状态栏上.而不是像之前一样把所有的应用通知都屏蔽掉.

## 3.自动填充的API(Autofill APIs)

程序员可以利用这些API很容易地实现自动填充信息和重复信息,而且Google官方还对这些数据做了保护措施,可以用于保护用户数据,如地址,用户名,甚至是密码都行.[自动填充的API在这里看](https://developer.android.com/preview/features/autofill.html)

## 4.PIP手机和新的窗口功能(PIP for handsets and new windowing features)

画中画（PIP）显示，现在手机和平板电脑都可以可用了，这样用户就可以继续观看视频，当他们在聊天或者打车。应用程序可以把自己在画中画模式从恢复或暂停状态的系统支持它-你可以指定长宽比和一套定制的互动（如播放/暂停）的. 所以当写视频播放类的软件时,应该将暂停写到onStop()中而不是onPause()中,我个人认为.

## 5.支持在XML中定义字体(Font resources in XML)

## 6.自适应图标(Adaptive icons)

现在可以创建 自适应图标,来在不同的系统环境下,显示不同的样子.

## 7.针对应用宽色域的颜色(Wide-gamut color for apps)

## 8.连接(Connectivity)

Android O现在还支持高品质的蓝牙音频编解码器，如LDAC编解码器。增加了新的Wi-Fi功能，以及像 wifi网络感知，以前称为邻居感知联网（NAN）。在与相应的硬件设备，应用和附近的设备可以发现和传递通过Wi-Fi没有互联网接入点。

## 9.键盘导航(Keyboard navigation)

## 10.WebView中的增强功能(WebView enhancements)

在Android 7.0，我们引入了一个可选的多进程模式的WebView感动的网页内容处理成一个独立的进程。在Android O中，我们在默认情况下启用多进程模式和加法的API，让你的应用程序处理错误和崩溃，以提高安全性和提高应用的稳定性。也比以前更安全了.

## 11.Java 8 的API和运行时优化(Java 8 Language APIs and runtime optimizations)

Android现在支持几种新的Java语言的API，包括新java.time API。另外，Android的运行时间比以往更快，最高为一些应用程序基准测试2倍的改进。
