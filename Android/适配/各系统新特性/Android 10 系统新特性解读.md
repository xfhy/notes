> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5c7ca69e51882561ad32b744#heading-2

概述
==

和往年一样，将于今年 5 月 7 日举办的谷歌 I/O 19 大会上，谷歌将发布首版 Android Q（即 Android 10）系统。按照惯例，Android Q Beta 并非首个开发者预览版，可能是第二或者第三版，Beta 阶段更大意义在于非谷歌系的 OEM 品牌手机加入，便于测试和用户尝鲜。至于正式版什么时候发布，按照惯例，会在 8 月或者 9 月发布稳定的版本供用户使用。

根据目前流出的关于 Android Q 新闻，特别是随着 Android Q 内测系统的泄露，以及对 AOSP 代码、System UI APK 的挖掘，Android Q 的很多特性已经曝光。可以预测的是，WPA3 加密和 5G/5G + 肯定会在这次版本中出现，另外一些 AOSP 代码还涉及：

1.  **`限制程序访问剪贴板`**：安卓系统此前毫无限制的剪贴板功能在 Android Q 中将纳入监管，目前的代码暗示只有 OEM 厂商签名的程序才有访问权。
2.  **`允许应用程序降级`**：当对商店更新后的版本后悔时，可以 “回到过去” 即回滚到旧版。
3.  **`限制外部存储访问权`**：授予程序 SD 卡读写权限后就可以任意操作卡内的任意文件，Android Q 将加以限制，避免用户数据误删或隐私泄露。
4.  **`后台程序位置访问功能`**：此前后台程序不被允许访问位置信息，虽然保护了隐私但对某些程序也造成了困扰，Android Q 将加以甄别恢复。

新特性
===

下面的这些内容来自 XDA Developer。他们通过 2019 年 2 月的安全补丁，获得了 Android Q 早起版本相关信息。

1、 暗黑模式
-------

Android Q 的暗黑模式和 Android Pie 的暗黑模式不同，在 Android Q 中，暗黑模式适用于任何地方，如果应用不支持暗黑模式，那么系统将自动设置一个暗黑模式。

![](https://user-gold-cdn.xitu.io/2019/3/4/16946ead2d383816?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

2、桌面模式
------

Android Q 将支持桌面模式，类似三星 Dex 和华为的投影模式。它提供类似一个类似于 PC 的体验，但是远远不能代替 PC。

![](https://user-gold-cdn.xitu.io/2019/3/4/16946ead2d14a80c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

3、 隐私增强
-------

Android Q 还将更多地使用 Android Pie 中推出的隐私功能。 在 Android Q 中，您可以选择应用程序在后台运行时是否可以访问该位置，感觉跟 iOS 学的。

![](https://user-gold-cdn.xitu.io/2019/3/4/16946ead2d44a106?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

此外，当应用程序使用您的位置数据、麦克风或摄像头时，您将在通知栏中看到相应的图标， 它会告诉你哪个应用程序正在使用该权限。

![](https://user-gold-cdn.xitu.io/2019/3/4/16946ead2f6a31b9?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

Android Q 中还有一个新的专用隐私页面，它可以显示了您的联系人、短信和其他敏感信息的应用程序的确切数量。

4、 超级锁定模式
---------

目前，Android Pie 版本已经有一个锁定模式，可以禁用指纹传感器，可以猜 想的是 Android Q 将会有某种超级锁定模式。

![](https://user-gold-cdn.xitu.io/2019/3/4/16946ead2d2807ef?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

泄漏的信息中包括一个 “传感器关闭” 切换按钮，该按钮将设备置于飞机模式，并禁用手机上的所有传感器。

5、 屏幕录制
-------

Android Q 支持屏幕录制，就像 iOS 一样。透过泄漏信息我们发现，Android Q 的录屏功能还不完善，可能需要在后面的 beta 版本中进行修复，可以通过长时间按下 “电源” 菜单中的 “屏幕快照” 来开启。

![](https://user-gold-cdn.xitu.io/2019/3/4/16946ead2f66e2dc?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

6、 移除 Android Beam
------------------

Android Beam 用于在设备之间共享文件，是 Android 4.0 版本推出的一个功能，不过这个功能基本上没有什么人用，移除了很多人也没什么感觉吧。

![](https://user-gold-cdn.xitu.io/2019/3/4/16946ead63a0be4e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

7、 运营商锁定
--------

如果你从运营商那里购买锁定的 Android Q 设备，他们将有能力阻止你使用其他特定运营商的 SIM 卡。

8、 面部识别
-------

XDA 团队发现了一串字符串，这些字符串表明 Android 10 将具有内部面部识别功能。 这意味着谷歌官方支持面部解锁系统。

9、 限制程序访问剪贴板
------------

Android Q 包含了名为 “READ_CLIPBOARD_IN_BACKGROUND” 的新权限。 顾名思义，新的权限将阻止随机的后台应用程序访问剪贴板内容。

10、 应用程序降级
----------

当对商店更新后的版本后悔时，可以 “回到过去” 即回滚到旧版。

11、新字体、图标形状和提示颜色
----------------

Android Pie 的一个特点是能够改变背景主题，泄露的 Android 信息中展示了新的两种新字体，图标形状，如正方形、松鼠、TearDrop，新的提示颜色：黑色、绿色和蓝色。

![](https://user-gold-cdn.xitu.io/2019/3/4/16946ead610293b1?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

目前 Android Q 还没有正式发布，John Wu 实现 Root 所使用的 ROM 应该是一个早期的内测版本系统。在后续的开发测试过程中，这个系统应该还会得到一定的修补和更新，让我们期待 Android Q 的到来吧。