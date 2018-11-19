> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/JMMepbKWAf9XrhtTbWtSLg

<section class="" style="font-size: 16px;color: rgb(84, 84, 84);margin-left: 6px;margin-right: 6px;line-height: 1.6;letter-spacing: 1px;word-break: break-all;font-family: &quot;Helvetica Neue&quot;, PingFangSC-Regular, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;text-align: center;">

> 本文由**玉刚说写作平**台提供写作赞助
> 原作者：**水晶虾饺**

这是个系列文章，后面还有很多篇，希望对大家能有帮助。

Flutter 是 Google 推出的移动端跨平台开发框架，使用的编程语言是 Dart。从 React Native 到 Flutter，开发者对跨平台解决方案的探索从未停止，毕竟，它可以让我们节省移动端一半的人力。本篇文章中，我们就通过编写一个简单的 Flutter 来了解它的开发流程。

这里我们要开发的 demo 很简单，只是在屏幕中间放一个按钮，点击的时候，模拟摇两个骰子并弹窗显示结果。

### 创建项目

我们这里假定读者已经安装好 Flutter，并且使用安装了 Flutter 插件的 Android Studio 进行开发。如果你还没有配置好开发环境，可以参考 [这篇文章](https://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492521&idx=1&sn=723658efc8221f15f6a6a93e1a3f08c8&scene=21#wechat_redirect)。

下面我们开始创建项目：

1.  选择 File > New > New Flutter project…

2.  在接下来弹出的选择面板里，选择 Flutter Application

3.  这里填应用的基本信息。Project name 我们就写 flutter_demo 好了。这里要注意的是，Project name 必须是一个合法的 Dart 包名（小写 + 下划线，可以有数字）。填好以后点击 next，然后 finish。

第一次创建项目时，由于要下载 gradle，时间会稍微长一些。

### 编写代码（1）

在上一小节里我们所创建的项目，已经有了一些代码，感兴趣的读者可以跑到自己手机上看一看，相关的代码在 lib/main.dart 里面。

为了体验从头开发一个应用的过程，这里我们先把 lib/main.dart 里的内容都删除。

首先，创建一个 `main` 函数。跟其他语言一样，`main` 函数是应用的入口：

```
void main() {}
```

下面我们编写一个 `Widget` 作为我们的 app。在 Flutter 里，所有的东西都是 `Widget`。

```
import 'package:flutter/material.dart';void main() {  // 创建一个 MyApp  runApp(MyApp());}/// 这个 widget 作用这个应用的顶层 widget.////// 这个 widget 是无状态的，所以我们继承的是 [StatelessWidget]./// 对应的，有状态的 widget 可以继承 [StatefulWidget]class MyApp extends StatelessWidget {  @override  Widget build(BuildContext context) {    // 创建内容  }}
```

现在我们进入正题，实现一个按钮，在点击的时候弹框显示结果：

```
@overrideWidget build(BuildContext context) {  // 我们想使用 material 风格的应用，所以这里用 MaterialApp  return MaterialApp(    // 移动设备使用这个 title 来表示我们的应用。具体一点说，在 Android 设备里，我们点击    // recent 按钮打开最近应用列表的时候，显示的就是这个 title。    title: 'Our first Flutter app',    // 应用的“主页”    home: Scaffold(      appBar: AppBar(        title: Text('Flutter rolling demo'),      ),      // 我们知道，Flutter 里所有的东西都是 widget。为了把按钮放在屏幕的中间，      // 这里使用了 Center（它是一个 widget）。      body: Center(        child: RaisedButton(          // 用户点击时候调用          onPressed: _onPressed,          child: Text('roll'),        ),      ),    ),  );}void _onPressed() {  // TODO}
```

### 安装、调试（1）

现在，点击 Run，把我们的第一个 Flutter 应用跑起来吧。没有意外的话，你会看到下面这个页面：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3Wxu5ekiayC0saR7p8wI1gXef3FDEK2TMoz83t2WxxmeK21x6hicEqAgUHgYib66ucw191tiamt3U6hYrQ/640?wx_fmt=png)Screenshot with a button in the center

如果你遇到了什么困难，可以查看 tag first_app_step1 的代码：

```
git clone https://github.com/Jekton/flutter_demo.gitcd flutter_demogit checkout first_app_step1
```

由于是第一次写 Flutter 应用，我们对上面的代码是否能够按照预期执行还不是那么有信心，所以我们先打个 log 确认一下，点击按钮后是不是真的会执行 `onPress`。

打 log 可以使用 Dart 提供的 `print`，但在日志比较多的时候，`print` 的输出可能会被 Android 丢弃，这个时候 `debugPrint` 会是更好的选择。对应的日志信息可以在 Dart Console 里查看（View -> Tool Windows -> Run 或者 Mac 上使用 Command+4 打开）。

```
void _onPressed() {  debugPrint('_onPressed');}
```

保存后（会自动 Hot Reload），我们再次点击按钮，在我的设备上，打印出了下面这样的信息：

```
I/flutter (11297): _onPressedV/AudioManager(11297): playSoundEffect   effectType: 0V/AudioManager(11297): querySoundEffectsEnabled...
```

这里的第一行，就是我们打的。现在我们有足够的自信说，点击按钮后，会执行 `_onPressed` 方法了。

### 编写代码（2）

软件开发通常是一个螺旋式上升的过程，不可能通过一次编码、调试就完成。现在，我们开始第二轮迭代。

接下来要做的，便是在 `_onPressed` 里面弹一个框：

```
// context 这里使用的是 MyApp.build 的参数void _onPressed(BuildContext context) {  debugPrint('_onPressed');  showDialog(    context: context,    builder: (_) {      return AlertDialog(        content: Text('AlertDialog'),      );    }  );}
```

遗憾的是，这一次并不那么顺利。Dialog 没有弹出来，而且报了下面这问题：

```
I/flutter (11297): Navigator operation requested with a context that does not include a Navigator.I/flutter (11297): The context used to push or pop routes from the Navigator must be that of a widget that is aI/flutter (11297): descendant of a Navigator widget.
```

原因在于，stateless 的 `widget` 只能用于显示信息，不能有其他动作。所以，该让 `StatefulWidget` 上场了。

```
class RollingButton extends StatefulWidget {  // StatefulWidget 需要实现这个方法，返回一个 State  @override  State createState() {    return _RollingState();  }}// 可能看起来有点恶心，这里的泛型参数居然是 RollingButtonclass _RollingState extends State<RollingButton> {  @override  Widget build(BuildContext context) {    return RaisedButton(      child: Text('Roll'),      onPressed: _onPressed,    );  }  void _onPressed() {    debugPrint('_RollingState._onPressed');    showDialog(        // 第一个 context 是参数名，第二个 context 是 State 的成员变量        context: context,        builder: (_) {          return AlertDialog(            content: Text('AlertDialog'),          );        }    );  }}
```

要实现一个 stateful 的 `widget`，可以继承 `StatefulWidget` 并在 `createState` 方法中返回一个 `State`。除了这一部分，代码跟我们之前写的并没有太大的区别。

剩下的，就是替换 `MyApp` 里面使用的按钮，修改后的代码如下：

```
class MyApp extends StatelessWidget {  @override  Widget build(BuildContext context) {    return MaterialApp(      title: 'Our first Flutter app',      home: Scaffold(        appBar: AppBar(          title: Text('Flutter rolling demo'),        ),        body: Center(          child: RollingButton(),        ),      ),    );  }}
```

再次运行，点击按钮后，我们将看到梦寐以求的 dialog。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3Wxu5ekiayC0saR7p8wI1gXef6rM3vaDq9fI1oAPVrZmhUSVgLlq5buDOciahC7Zkiawul5FiaCIkoAg1Q/640?wx_fmt=png)Screenshot with a dialog

如果你遇到了麻烦，可以查看 tag first_app_step2 的代码。

最后，我们来实现 “roll”：

```
import 'dart:math';class _RollingState extends State<RollingButton> {  final _random = Random();  // ...  List<int> _roll() {    final roll1 = _random.nextInt(6) + 1;    final roll2 = _random.nextInt(6) + 1;    return [roll1, roll1];  }  void _onPressed() {    debugPrint('_RollingState._onPressed');    final rollResults = _roll();    showDialog(        // 第一个 context 是参数名，第二个 context 是 State 的成员变量        context: context,        builder: (_) {          return AlertDialog(            content: Text('Roll result: (${rollResults[0]}, ${rollResults[1]})'),          );        }    );  }}
```

### 安装、调试（2）

还是一样，重新运行后，我们就能够看到每次点击按钮的结果随机地出现 [1, 6] 中的数…… 慢着，怎么弹出的消息里的两个号码总是一样的！好吧，肯定是哪里出错了。

这次，我们不采用打 log 的方法，改用 debugger 来调试。

1.  在 `final rollResults = _roll()` 这一行打个断点

2.  然后点击 Debug main.dart 开始调试

3.  点击 APP 里的 Roll 按钮

现在，应用停在了我们所打的断点处：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3Wxu5ekiayC0saR7p8wI1gXefwfumvbaiaDQuSzLFyLkspZxCg1RgibaSy3RT5K3pFN8vyhQfEdmnwiaDA/640?wx_fmt=png)debug step1

接下来：

1.  Step Into 进入 `_roll` 方法

2.  进入 `_roll` 后，Step Over 一行一行执行。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3Wxu5ekiayC0saR7p8wI1gXefGrYpibpuibHKmLpfXosh3ttHnCXe4d9PeOiamiaLiczeU4yeHTsNJVQDd2Q/640?wx_fmt=png)

debug step2</section>

<section class="" style="font-size: 16px;color: rgb(84, 84, 84);margin-left: 6px;margin-right: 6px;line-height: 1.6;letter-spacing: 1px;word-break: break-all;font-family: &quot;Helvetica Neue&quot;, PingFangSC-Regular, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;">这里我们看到，两次 random 确实产生了不同的结果。我们继续：

1.  还是 Step Over，这个时候 `_roll` 就返回了

2.  切换到 Variables 这个选项卡，查看 `rollResults` 的值

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3Wxu5ekiayC0saR7p8wI1gXefwk4eZZCrSDwZgqGxibXSlJicJ21GpaTZBGNEB9ku6VRO2ItLCtdPFA9A/640?wx_fmt=png)debug step3

可以发现，两个结果居然变成一样的了。再往回查看一下代码，我们写的是 `return [roll1, roll1]`。修改后一个为 `roll2`，程序就能够按预期的正常执行了。

最终的代码，可以看 tag first_app_done。

### 调试总结

本篇文章其实介绍了两种调试方法：打 log 和 debugger。虽然现在 Flutter 提供的 log 工具比较简陋，可以预期未来还会进一步完善。

使用打 log 的方式，好处在于不会对执行流程产生较大的影响，在多线程环境尤为有用。它的速度也比较快，不需要我们去单步执行。不足之处在于，如果原先没有对应的 log，我们只能修改代码重新运行，才能查看相应的状态。对于线上的应用，我们也只能够通过分析 log 来定位问题。

debugger 跟打 log 方式是互补的。使用 debugger 时，我们可以随意查看我们需要知道的变量的值，一步一步近距离观察代码的运行状态。坏处当然就是太慢了。在什么时候使用什么方法，需要一些经验；但有时候就全凭个人喜好了，没有优劣之分。

更多的调试方法，读者可以根据需要查看 https://flutter.io/debugging / 进一步学习。

### 打包

编写完应用后，就得打包 apk 分发给用户使用了。在这一小节，我们来看看怎么给 Flutter 项目打包。

在项目的根目录，有一个 android 文件夹，下面我们将主要对这个目录的文件进行修改。

1.  查看 AndroidManifest.xml。这是一个按模板生成的文件，有些东西可能需要修改一下

2.  build.gradle，这里面也可能有你需要修改的地方。对我们的应用来说，目前都先维持原样

3.  如果有需要，更新 res/mipmap 里的应用启动图标，这里我们不改

4.  签名，前面略微复杂一些，下面详细展开一下。

1) 生成签名的 key（如果你已经有了，跳过这一步）, 为了让读者也可以编译，这里我把 key 也放到了项目中。

```
keytool -genkey -v -keystore ~/key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias key
```

2) 添加一个 android/key.properties，内容如下：

```
storePassword=123456keyPassword=123456keyAlias=keystoreFile=../key.jks
```

3) 更新 build.gradle 里的签名配置

```
def keystorePropertiesFile = rootProject.file("key.properties")def keystoreProperties = new Properties()keystoreProperties.load(new FileInputStream(keystorePropertiesFile))android {    // ...    signingConfigs {        release {            keyAlias keystoreProperties['keyAlias']            keyPassword keystoreProperties['keyPassword']            storeFile file(keystoreProperties['storeFile'])            storePassword keystoreProperties['storePassword']        }    }    buildTypes {        release {            signingConfig signingConfigs.release            minifyEnabled true            useProguard true            // proguard 文件我们在下一步添加            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'        }    }}
```

签名信息配置完毕后，下面进行第 5 步。

1.  添加 android/app/proguard-rules.pro

    ```
    #Flutter Wrapper-keep class io.flutter.app.** { *; }-keep class io.flutter.plugin.**  { *; }-keep class io.flutter.util.**  { *; }-keep class io.flutter.view.**  { *; }-keep class io.flutter.**  { *; }-keep class io.flutter.plugins.**  { *; }
    ```

2.  编译 apk。在项目的根目录，执行 `flutter build apk`， 编译后的应用在 build/app/outputs/apk/release/app-release.apk。

3.  还是在根目录下，执行 `flutter install` 就可以安装这个 apk 了。

对于 iOS，读者可以看 https://flutter.io/ios-release/，这里就不再演示了。 查看最终的项目，可以 checkout 到 tag first_app_signing。恭喜你，第一个 Flutter 应用完成啦。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WxEibQVO9fRJVxibanVicVCVL9oZ6Nh6ibZDuVbEwIKMibC6ba9nnM3FTpHka2SYLyDtSwvBhzwCIecdbQ/640?wx_fmt=png)

</section>