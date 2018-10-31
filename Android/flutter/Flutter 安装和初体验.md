> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492521&idx=1&sn=723658efc8221f15f6a6a93e1a3f08c8&chksm=8eec87d6b99b0ec0fef71ee2a29f03c9997124fd91c031d350500df973e648954435e823b04f&scene=21#wechat_redirect

> 在前面的文章中，我们初步了解了 Flutter。本文将介绍 Flutter 的安装过程和开发工具，为了大家能更好地理解 Flutter，本文还找了一个小例子。

### Flutter 安装指南

关于 Flutter 的安装，参考官方文档中的步骤即可。本文以 macOS 为例，介绍 Flutter 在 macOS 上的安装细节。

官方文档地址：https://flutter.io/get-started/install/

首先，下载 Flutter 的源码，按照官方建议，我们选择 beta 分支，如下所示：

```
git clone -b beta https://github.com/flutter/flutter.git
```

代码下载后，目录结构如下：

```
flutter renyugang$ tree -L 2 -d.├── bin│   ├── cache│   └── internal├── dev│   ├── automated_tests│   ├── benchmarks│   ├── bots│   ├── devicelab│   ├── docs│   ├── integration_tests│   ├── manual_tests│   ├── missing_dependency_tests│   └── tools├── examples│   ├── catalog│   ├── flutter_gallery│   ├── flutter_view│   ├── hello_world│   ├── layers│   ├── platform_channel│   ├── platform_channel_swift│   ├── platform_view│   └── stocks└── packages    ├── flutter    ├── flutter_driver    ├── flutter_localizations    ├── flutter_test    └── flutter_tools29 directories
```

接着，为了方便后续使用，需要将项目根目录下 bin 路径加入环境变量 PATH 中，打开~/.bash_profile 文件，修改环境变量即可，如下：

```
# 注意：Users/didi/google/flutter需要替换为你本地Flutter项目的路径export PATH=$PATH:/Users/didi/google/flutter/bin
```

> 务必注意：如果你不能科学上网，那么在继续下面的步骤之前，需要做一些额外的事情。声明 PUB_HOSTED_URL 和 FLUTTER_STORAGE_BASE_URL 两个环境变量，在当前 shell 窗口执行如下两行命令即可，此举是为了让 Flutter 在安装过程中使用国内的镜像。
> export PUB_HOSTED_URL=https://pub.flutter-io.cn
> export FLUTTER_STORAGE_BASE_URL=https://storage.flutter-io.cn

然后，我们就可以通过`flutter doctor`命令来执行 Flutter 的安装程序了，经过一段时间的下载和安装，Flutter 会输出安装结果：

```
flutter renyugang$ flutter doctorDoctor summary (to see all details, run flutter doctor -v):[✓] Flutter (Channel beta, v0.1.5, on Mac OS X 10.11.1 15B42, locale zh-Hans)[✓] Android toolchain - develop for Android devices (Android SDK 26.0.2)[!] iOS toolchain - develop for iOS devices (Xcode 7.3.1)    ✗ Flutter requires a minimum Xcode version of 9.0.0.      Download the latest version or update via the Mac App Store.    ✗ libimobiledevice and ideviceinstaller are not installed. To install, run:        brew install --HEAD libimobiledevice        brew install ideviceinstaller    ✗ ios-deploy not installed. To install:        brew install ios-deploy    ✗ CocoaPods not installed.        CocoaPods is used to retrieve the iOS platform side's plugin code that responds to your plugin usage on the Dart side.        Without resolving iOS dependencies with CocoaPods, plugins will not work on iOS.        For more info, see https://flutter.io/platform-plugins      To install:        brew install cocoapods        pod setup[✓] Android Studio (version 3.0)[✓] IntelliJ IDEA Community Edition (version 2017.3.1)[✓] Connected devices (1 available)! Doctor found issues in 1 category.
```

从上面的诊断信息可以看出如下关键信息：

*   Flutter 的版本和渠道

*   Flutter 运行所需的 Android 工具链 OK

*   Flutter 运行所需的 iOS 工具链    不 OK

*   Android Studio 和 IntelliJ 都安装了，还有一个已连接的手机

如果大家想完善 iOS 工具链，那么就按照✗号的提示安装相应工具即可。对于我来说，我本机的 xcode 版本太低，我需要升级 xcode 并安装一堆工具，我就懒得操作了，大家根据需要来吧。

### 如何运行 Flutter 程序

运行 Flutter 程序有两种方式。

第一种方式，这里假设有个 Flutter 程序，它的目录名为 FlutterDemo，那么我们只需要在 FlutterDemo 目录下执行`flutter run`即可，当然必须通过 USB 连接设备才可以，这里可以选择 iOS 和 Android 手机，当然也可以选用模拟器。

第二种运行方式就是通过 IDE 来运行程序，比如 AndroidStudio 和 IntelliJ 的`Run`按钮。

### 如何开发 Flutter 程序

Flutter 可以使用如下三个 IDE 来开发程序

*   Android Studio

*   IntelliJ

*   Visual Studio Code

但是很奇怪，看来 Google 并没有打算支持 xcode，这或许对 iOS 用户不太友好。

对于 Android 同学来说，肯定是选择 Android Studio 或者 IntelliJ 来开发程序了。本文选择 IntelliJ 做演示，其实 Android Studio 和 IntelliJ 很类似。

为了使用 IntelliJ 来开发和调试 Flutter 程序，首先需要安装如下两个插件：

*   Flutter 插件：提供程序的运行、调试和热重载等功能

*   Dart：提供 Dart 语言的支持

安装方式为：选择 Preferences 中的 Plugins 选项，然后搜索 Flutter 安装即可，安装 Flutter 插件会自动安装 Dart 插件，如下：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwrGxkBoXFhzGCfyLgEDAb1YeVqu5SGickjicLDe4PicWZnNGM6dXrWzJ3McfD8PWLXfzicbJGUFBrSkg/640?wx_fmt=png)

安装完成后，重启 IntelliJ，就可以通过 IntelliJ 来新建和开发程序了。

### 例子展示

为了让大家更加直观地了解 Flutter 的运行效果，本文找了一个官方 demo 并在 IntelliJ 中打开并运行，这个例子的路径位于 flutter/examples/flutter_gallery 目录下，使用 IntelliJ 打开后如下所示：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwrGxkBoXFhzGCfyLgEDAb1ib9Z9vNUXKD6fnRtxKV4dRF0FMwxL5Ae3gdzbticKypVHOiaWPTYZCibFQ/640?wx_fmt=png)

可以看到，在连接设备那栏有三个选项，分别是 Android 手机、iOS 模拟器和 Android 模拟器，这意味着：同一套代码，可以在不同的设备中运行。

在 Android 手机中的运行效果如下：

![](https://mmbiz.qpic.cn/mmbiz_gif/zKFJDM5V3WwrGxkBoXFhzGCfyLgEDAb19fiarUguIZDIAux1RTF2TfHaTr5Qh4Ta3ptonD0NUOYmakMYs1CFmNQ/640?wx_fmt=gif)

> 长按识别下方的二维码关注我，接收更多技术推送

![](https://mmbiz.qpic.cn/mmbiz_jpg/zKFJDM5V3WzzNpnqOGq3mMO64mFVSicAIkzUSiam08j6DetjnjeujRjEAZRe7PqmPGqow3GWxSk4gas6r7BA4k6A/640?wx_fmt=jpeg)