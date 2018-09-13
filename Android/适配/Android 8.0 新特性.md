
1. 通知

在 Android O 中，我们已重新设计通知，以便为管理通知行为和设置提供更轻松和更统一的方式。这些变更包括： 
　　通知渠道：Android O 引入了通知渠道，其允许您为要显示的每种通知类型创建用户可自定义的渠道。用户界面将通知渠道称之为通知类别。要了解如何实现通知渠道的信息，请参阅通知渠道指南。 
　　休眠：用户可以将通知置于休眠状态，以便稍后重新显示它。重新显示时通知的重要程度与首次显示时相同。应用可以移除或更新已休眠的通知，但更新休眠的通知并不会使其重新显示。 
　　通知超时：现在，使用 Notification.Builder.setTimeout() 创建通知时您可以设置超时。您可以使用此方法指定一个持续时间，过了该持续时间后取消通知。如果需要，您可以在指定的超时持续时间之前取消通知。 
　　通知清除：系统现在可区分通知是由用户清除，还是由应用移除。要查看清除通知的方式，您应实现 NotificationListenerService 类的新 onNotificationRemoved() 方法。 
　　背景颜色：您现在可以设置和启用通知的背景颜色。只能在用户必须一眼就能看到的持续任务的通知中使用此功能。例如，您可以为与驾车路线或正在进行的通话有关的通知设置背景颜色。您还可以使用 Notification.Builder.setColor() 设置所需的背景颜色。这样做将允许您使用 Notification.Builder.setColorized() 启用通知的背景颜色设置。 
　　消息样式：现在，使用 MessagingStyle 类的通知可在其折叠形式中显示更多内容。对于与消息有关的通知，您应使用 MessagingStyle 类。您还可以使用新的 addHistoricMessage() 方法，通过向与消息相关的通知添加历史消息为会话提供上下文。

关于通知适配可查看
- 郭霖8.0通知适配 https://blog.csdn.net/guolin_blog/article/details/79854070
- Notification用法 https://www.jianshu.com/p/8cec293cfa9a

2. 画中画

画中画（Picture in Picture），简称 PIP。画中画就是一个画面中浮动着另外一个画面，其实就是画面的层次感。画中画最早用于 Android TV，从 Android 8.0 开始，API 开放给所有 Android 设备。其实在 Android 的旧版本，也可以利用 Android 窗口设计框架，在其它应用上面绘制 UI 的功能，实现类似的功能。国内的某信的视频通话就是基于 Android 这一特点。当然，Android 推出画中画功能，会更加便捷的实现同样的功能。读者可以阅读笔者的文章《Android窗口和视图》了解更多 Android 视图的知识。

![](http://olg7c0d2n.bkt.clouddn.com/18-8-29/11078992.jpg)

Android 的画中画是基于整个 Activity 的，并不是单纯的一个 View，因此，这里就会设计到 Activity 的生命周期的问题，所以画中画也会和 Activity 的生命周期绑定在一起，形成画中画生命周期。画中画的生命周期和 Android 7.0 推出的分屏模式的生命周期是一样的，只有当前和用户交互的 Activity 是最上层的 Activity，其它的可见的 Activity 都是 paused 状态。所以在处理画中画或者分屏模式需要注意 Activity 的生命周期。

实现方法
在项目的 manifest 中，给对应的 Activity 加上

android:supportsPictureInPicture=ture
1
调用接口

Activity.enterPictureInPictureMode(PictureInPictureParams args)
1
进入画中画模式。PictureInPictureParams 可以携带画中画的配置参数，如在屏幕中显示多大比例等，如果参数为空，系统将会使用默认配置参数。后续也可以通过接口

Activity.setPictureInPictureParams()


3. 通知标记

Notification dots，这个功能不知说什么好，在 Android 7.0 的 pixel 手机上已经有这个功能框架，Android 8.0 新增了其它的功能点。这个功能和某果的手机的 3D touch 功能是类似到了，只是 Android 不是通过压力感应来实现，而是通过长按，这些都不重要，重要的是需要 Launcher 配合，OO，所以除开 pixel 的手机，目前没有其它厂商的 Luancher 开发了这个功能。读者可以阅读笔者的文章了解 Launcher 《Launcher的启动过程》《Launcher界面结构》《Launcher拖拽框架》《Launcher 记录自定义桌面》。

![](https://img-blog.csdn.net/20171106204944045?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbXlmcmllbmQw/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


4. 通知延时提醒
Snoozing，Android 8.0 支持用户可以延时再次提醒该通知。这个功能应用本身无法设置，欲详细了解该功能，读者可以阅读 Android 8.0 的 SystemUI。

5. 通知超时
Notification timeouts，设定多久时间通知自动消失。

Notification.Builder setTimeoutAfter (long durationMs)

6. 通知设置
应用可以添加通知配置入口，让用户可以点击快速调转到应用的通知设置界面，即上文中的通知渠道界面。

Notification.Builder setSettingsText (CharSequence text)
//相关 Intent，应用可以不关注
Notification.INTENT_CATEGORY_NOTIFICATION_PREFERENCES

7. 自动填充
自动填充功能在互联网上很多地方已经有应用，如度娘的搜索框，在输入搜索内容的一部分时，会弹出一个列表选择。在 Android AutoCompleteTextView 控件，也是实现类似的功能，只是 AutoCompleteTextView 的限制，谁用过谁知道，这个自动填充实在是太 low 了。自动填出不是什么新鲜的事物，Android 此处引入了功能强大的自动填充框架，也是很有必要。毕竟，自动填充可以带来两点优势

减少输入时间，在手机上的输入速度实在也是慢
减少输入错误，比如登录之类的，完全可以规避输入错误的问题

![](https://img-blog.csdn.net/20171106205020257)

8. 更换Autofill Service
9. 
Android 设备上的 Autofill service 可以由用户切换，路径是：Settings > System > Languages & input > Advanced > Input assistance > Autofill service。

打开自动填充
```
AutofillManager afm = context.getSystemService(AutofillManager.class);
if (afm != null) {
    afm.requestAutofill();
}
```

10 .下载字体
Android 8.0 提供字体下载，App 开发业都可以方便提供各种字体。对于强大的汉语的字体，不知会支持到什么程度了。字体下载详情可以看官方文档。

11. XML定义字体
其实这个早已经有很多玩法，既然 Android 官方提供支持，也将会成为一种标准。[详情进入](https://developer.android.google.cn/guide/topics/ui/look-and-feel/fonts-in-xml.html)。


12. 自动缩放文本视图
Autosizing TextView，就是会根据 TextView 在不同屏幕上的大小，来自动调整 TextView 内容的大小，对于 TextView 内容较多，TextView 内容变化频繁的场景非常实用。希望此举有望解决 Android TextView 内容经常显示异常导致界面超级难看的问题。

对于 TextView 的内容大小调整，Android 提供了三种方式

- Default
- Granularity
- Preset Sizes

例子
```
<?xml version="1.0" encoding="utf-8"?>
<TextView
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:autoSizeTextType="uniform"
    android:autoSizeMinTextSize="12sp"
    android:autoSizeMaxTextSize="100sp"
    android:autoSizeStepGranularity="2sp" />
```

13. 自适应桌面图标

https://developer.android.google.cn/guide/practices/ui_guidelines/icon_design_adaptive.html

14. 定时作业调度
JobScheduler improvements，JobScheduler 在很早的版本就已经集成了，在 Anroid 8.0 做了些更新，主要还是因为 Android 8.0 中的后台限制，很多后台任务，后续版本需要迁移到 JobScheduler 上来。

这次 JobScheduler 更新了作业队列，可以添加一系列作业，当 JobScheduler 运行时，所有作业将会被执行。

JobScheduler.enqueue()

Android 8.0 引入了新的 JobIntentService，功能上类似 IntentService，Android 8.0 中在后台无法运行的 Service，可以用新的 JobIntentService 代替。

15. 后台限制

PS：此章节主要摘自官网

每次在后台运行时，应用都会消耗一部分有限的设备资源，例如 RAM。 这可能会影响用户体验，如果用户正在使用占用大量资源的应用（例如玩游戏或观看视频），影响尤为明显。

为了提升用户体验，Android 8.0 对应用在后台运行时可以执行的操作施加了限制。Android 终于着手解决多年困扰 Android 用户的第一诟病了，真的是非常具有前进性的一步。

同时运行的应用越多，对系统造成的负担越大。 如果还有应用或服务在后台运行，这会对系统造成更大负担，进而可能导致用户体验下降；例如，音乐应用可能会突然关闭。为了降低发生这些问题的几率，Android 8.0 对应用在用户不与其直接交互时可以执行的操作施加了限制。

应用在两个方面受到限制：

后台服务限制：处于空闲状态时，应用可以使用的后台服务存在限制。 这些限制不适用于前台服务，因为前台服务更容易引起用户注意。

广播限制：除了有限的例外情况，应用无法使用清单注册隐式广播。 它们仍然可以在运行时注册这些广播，并且可以使用清单注册专门针对它们的显式广播。

注：默认情况下，这些限制仅适用于针对 O 的应用。 不过，用户可以从 Settings 屏幕为任意应用启用这些限制，即使应用并不是以 O 为目标平台。

后台执行限制
系统可以区分 前台 和 后台 应用。 （用于服务限制目的的后台定义与内存管理使用的定义不同；一个应用按照内存管理的定义可能处于后台，但按照能够启动服务的定义又处于前台。）如果满足以下任意条件，应用将被视为处于前台：

具有可见 Activity（不管该 Activity 已启动还是已暂停）。
具有前台服务。
另一个前台应用已关联到该应用（不管是通过绑定到其中一个服务，还是通过使用其中一个内容提供程序）。 例如，如果另一个应用绑定到该应用的服务，那么该应用处于前台：

1. IME
2. 壁纸服务
3. 通知侦听器
4. 语音或文本服务

如果以上条件均不满足，应用将被视为处于后台。

处于前台时，应用可以自由创建和运行前台服务与后台服务。 进入后台时，在一个持续数分钟的时间窗内，应用仍可以创建和使用服务。

在 Android 8.0 之前，创建前台服务的方式通常是先创建一个后台服务，然后将该服务推到前台。

Android 8.0 有一项复杂功能；系统不允许后台应用创建后台服务。 因此，Android 8.0 引入了一种全新的方法，即 Context.startForegroundService()，以在前台启动新服务。

在系统创建服务后，应用有五秒的时间来调用该服务的 startForeground() 方法以显示新服务的用户可见通知。

如果应用在此时间限制内未调用 startForeground()，则系统将停止服务并声明此应用为 ANR。

在很多情况下，您的应用都可以使用 JobScheduler 作业替换后台服务。


广播限制
Android 8.0 广播限制更为严格。

针对 Android 8.0 的应用无法继续在其清单中为隐式广播注册广播接收器。 隐式广播是一种不专门针对该应用的广播。 例如，ACTION_PACKAGE_REPLACED 就是一种隐式广播，因为它将发送到注册的所有侦听器，让后者知道设备上的某些软件包已被替换。
不过，ACTION_MY_PACKAGE_REPLACED 不是隐式广播，因为不管已为该广播注册侦听器的其他应用有多少，它都会只发送到软件包已被替换的应用。

应用可以继续在它们的清单中注册显式广播。

应用可以在运行时使用 Context.registerReceiver() 为任意广播（不管是隐式还是显式）注册接收器。

需要签名权限的广播不受此限制所限，因为这些广播只会发送到使用相同证书签名的应用，而不是发送到设备上的所有应用。

在许多情况下，之前注册隐式广播的应用使用 JobScheduler 作业可以获得类似的功能。

例如，一款社交照片应用可能需要不时地执行数据清理，并且倾向于在设备连接到充电器时执行此操作。

之前，应用已经在清单中为 ACTION_POWER_CONNECTED 注册了一个接收器；当应用接收到该广播时，它会检查清理是否必要。 为了迁移到 Android 8.0，应用将该接收器从其清单中移除。

应用将清理作业安排在设备处于空闲状态和充电时运行。

注：很多隐式广播当前均已不受此限制所限。 应用可以继续在其清单中为这些广播注册接收器，不管应用针对哪个 API 级别。 有关已豁免广播的列表，请参阅隐式广播例外。


17. 后台位置限制
PS：此章节内容主要摘自官网

为降低功耗，无论应用的目标 SDK 版本为何，Android 8.0 都会对后台应用检索用户当前位置的频率进行限制。

重要说明：作为起点，我们只允许后台应用每小时接收几次位置更新。我们将在整个预览版阶段继续根据系统影响和开发者的反馈优化位置更新间隔。

前台应用行为得到保留
如果应用在运行 Android 8.0 的设备上处于前台，其位置更新行为将与 Android 7.1.1（API 级别 25）及更低版本上相同。

优化应用的位置行为
考虑在您的应用接收位置更新不频繁的情况下其后台运行用例是否根本无法成功。如果属于这种情况，您可以通过执行下列操作之一提高位置更新的检索频率：

将您的应用转至前台。
使用应用中的某个前台服务。激活此服务时，您的应用必须在通知区显示一个持续性的通知。
使用 Geofencing API 的元素（例如 GeofencingApi 接口），这些元素针对最大限度减少耗电进行了专门优化。
使用被动位置侦听器，它可以在后台应用加快位置请求频率时提高位置更新的接收频率。
受影响的 API
对后台应用位置检索行为的更改影响下列 API：

Fused Location Provider (FLP)

如果您的应用运行在后台，位置系统服务只会根据 Android 8.0 行为变更中定义的间隔，按每小时几次的频率为其计算新位置。即使您的应用请求进行更频繁的位置更新，也仍是如此。
如果您的应用运行在前台，与 Android 7.1.1（API 级别 25）相比，在位置采样率上不会有任何变化。
Geofencing

后台应用可以高于接收 Fused Location Provider 更新的频率接收地理围栏转换事件。
地理围栏事件的平均响应时间是大约每两分钟一次。
GNSS Measurements 和 GNSS Navigation Messages

当您的应用位于后台时，注册用于接收 GnssMeasurement 和 GnssNavigationMessage 输出的回调会停止执行。
Location Manager

提供给后台应用的位置更新只会根据 Android 8.0 行为变更中定义的间隔，按每小时几次的频率提供。

注：如果运行您的应用的设备安装了 Google Play 服务，强烈建议您改用 Fused Location Provider (FLP)。

WLAN 管理器 
startScan() 方法对后台应用执行完整扫描的频率仅为每小时数次。如果不久之后后台应用再次调用此方法， WifiManager 类将提供上次扫描所缓存的结果。