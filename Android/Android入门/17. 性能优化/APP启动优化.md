> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5baa3eb76fb9a05cdb103c74

> 本文首发于微信公众号「玉刚说」
> 
> 原文链接：[你的 APP 为何启动那么慢？](https://link.juejin.im?target=https%3A%2F%2Fmp.weixin.qq.com%2Fs%2Fi0Qkp8rZ_IfmVEoWSxvpdw)

### App 启动方式

* * *

##### 冷启动（Cold start）

冷启动是指 APP 在手机启动后第一次运行，或者 APP 进程被 kill 掉后在再次启动。
可见冷启动的必要条件是该 APP 进程不存在，这就意味着系统需要创建进程，APP 需要初始化。在这三种启动方式中，冷启动耗时最长，对于冷启动的优化也是最具挑战的。因此本文重点谈论的是对冷启动相关的优化。

##### 温启动 (Warm start)

App 进程存在，当时 Activity 可能因为内存不足被回收。这时候启动 App 不需要重新创建进程，但是 Activity 的 onCrate 还是需要重新执行的。场景类似打开淘宝逛了一圈然后切到微信去聊天去了，过了半小时再次回到淘宝。这时候淘宝的进程存在，但是 Activity 可能被回收，这时候只需要重新加载 Activity 即可。

##### 热启动 (Hot start)

App 进程存在，并且 Activity 对象仍然存在内存中没有被回收。可以重复避免对象初始化，布局解析绘制。
场景就类似你打开微信聊了一会天这时候出去看了下日历 在打开微信 微信这时候启动就属于热启动。

##### 在最近任务给 App 加锁和启动方式有什么关系

某些厂商为了用户体验提供了给 APP 上锁的功能，目的就是让用户自己做主是上锁的 APP 不被杀，启动的时候不会处于冷启动方式，但是加锁也不是万能的, Low memory killer 在内存极度吃紧的情况下也会杀死加锁 APP, 在此启动时也将以冷启动方式运行。

##### AI 和启动方式有什么关系

AI 在进程管理方面可谓是大有可为。MIUI10 发布了进程 AI 唤醒功能，使 APP 启动速度远超友商。这其中的道理简单说就是学习用户的使用习惯，提前将 App 进程创建好，当用户打开 APP 时不会出现冷启动。比如你是微信重度用户你发现用了 MIUI10 就再也见不到微信启动页面的那个地球了，这就是 AI 唤醒的功劳。

### 从点击 APP 图标到主页显示出现需要经过的步骤

* * *

这里我们来讨论冷启动的过程，进程启动原则上有四种途径，也就是通过其他进程对该 APP 的四大组件的调用来实现。

![](https://user-gold-cdn.xitu.io/2018/9/25/1661104e95868118?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

这里我们重点讨论用户点击桌面后的 APP 启动，通过 startActivity 方式的启动。
调用 startActivity, 该方法经过层层调用, 最终会调用 ActivityStackSupervisor.java 中的 startSpecificActivityLocked, 当 activity 所属进程还没启动的情况下, 则需要创建相应的进程.

```
void startSpecificActivityLocked(...) {    ProcessRecord app = mService.getProcessRecordLocked(r.processName,            r.info.applicationInfo.uid, true);    if (app != null && app.thread != null) {         ...//进程已创建        return    }    //创建进程    mService.startProcessLocked(r.processName, r.info.applicationInfo, true, 0,                "activity", r.intent.getComponent(), false, false, true);}复制代码
```

最终进程由 Zygote Fork 进程:

![](https://user-gold-cdn.xitu.io/2018/9/25/1661104e95ec717f?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

进程启动后系统还有一个工作就是：进程启动后立即显示应用程序的空白启动窗口。

一旦系统创建应用程序进程，应用程序进程就会负责下一阶段。这些阶段是：
1\. 创建应用程序对象
2\. 启动主线程
3\. 创建主要 Activity
4\. 绘制视图（View）
5\. 布局屏幕
6\. 执行初始化绘制

而一旦 App 进程完成了第一次绘制，系统进程就会用 Main Activity 替换已经展示的 Background Window，此时用户就可以使用 App 了。

![](https://user-gold-cdn.xitu.io/2018/9/25/1661104e95944f10?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)image.png

这里很明显有两个优化点：

1.Application OnCrate() 优化
当 APP 启动时，空白的启动窗口将保留在屏幕上，直到系统首次完成绘制应用程序。此时，系统进程会交换应用程序的启动窗口，允许用户开始与应用程序进行交互。如果应用程序中重载了 Application.onCreate()，系统会调用 onCreate() 方法。之后，应用程序会生成主线程（也称为 UI 线程），并通过创建 MainActivity 来执行任务。

2.Activity onCreate() 优化
onCreate() 方法对加载时间的影响最大，因为它以最高的开销执行工作：加载并绘制视图，以及初始化 Activity 运行所需的对象。

### 启动速度优化

* * *

##### 如何对启动时间进行量化？

1\. 目前为止见过最最牛逼的是使用机械手和高速相机测试，手机开机后使用机械手点击应用桌面图标，高速相机记录启动过程，后续通过程序分析视频，从机械手点击图标到 Activity 显示出来使用了多少时间。这种方式是最直观和精确的，但是成本也很高。

2\. 通过 shell 命令

```
adb shell am start -W [packageName]/[packageName.MainActivity]复制代码
```

执行成功后将返回三个测量到的时间：

ThisTime: 一般和 TotalTime 时间一样，除非在应用启动时开了一个透明的 Activity 预先处理一些事再显示出主 Activity，这样将比 TotalTime 小。

TotalTime: 应用的启动时间，包括创建进程 + Application 初始化 + Activity 初始化到界面显示。

WaitTime: 一般比 TotalTime 大点，包括系统影响的耗时。

3\. 可以通过在代码中增加 log 来计算启动时间

4\. 使用 systrace

##### Application OnCrate() 优化

1\. 第三方 SDK 初始化的处理
Application 是程序的主入口，很多三方 SDK 示例程序中都要求自己在 Application OnCreate 时做初始化操作。这就是增加 Application OnCreate 时间的主要元凶，所以需要尽量避免在 Application onCreate 时同步做初始化操作。比较好的解决方案就是对三方 SDK 就行懒加载，不在 Application OnCreate() 时初始化，在真正用到的时候再去加载。
下面实例对比下 ImageLoader 在采用懒加载后启动速度优化。

一般我们在使用 imageLoader 时都会在 Application onCreate() 时在主线程加载：

```
public class MyApplication extends Application {    @Override    public void onCreate() {        super.onCreate();        ImageLoaderConfiguration.Builder config =                new ImageLoaderConfiguration.Builder(this);        ImageLoader.getInstance().init(config.build());    }}复制代码
```

此时使用 adb shell am start -W [packageName]/[packageName.MainActivity] 检测应用启动时间，每次执行命令时需要杀死进程。

```
Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.luozhanwei.myapplication/.MainActivity }Status: okActivity: com.luozhanwei.myapplication/.MainActivityThisTime: 423TotalTime: 423WaitTime: 441复制代码
```

Total time 在 423ms 之间，下面是封装了一个懒加载 ImageLoader 的工具类示例：

```
public class ImageUtil {    private static boolean sInit;    private synchronized static void ensureInit() {        if (sInit) {            return;        }        ImageLoaderConfiguration.Builder config =                new ImageLoaderConfiguration.Builder(SecurityCoreApplication.getInstance());      ....        // Initialize ImageLoader with configuration.        ImageLoader.getInstance().init(config.build());        sInit = true;    }public static void display(String uri, ImageView imageView, boolean cacheOnDisk) {        imageView.setImageResource(R.drawable.icon_app_default);        ensureInit();        ImageLoader loader = ImageLoader.getInstance();        if (cacheOnDisk) {            loader.displayImage(uri, imageView);        } else {            loader.displayImage(uri, imageView, OPTIONS_NO_CACHE_DISK);        }    }复制代码
```

使用这种方案后的启动时间：

```
Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.luozhanwei.myapplication/.MainActivity }Status: okActivity: com.luozhanwei.myapplication/.MainActivityThisTime: 389TotalTime: 389WaitTime: 405复制代码
```

看到 TotalTime 比之前减少了 34ms（给出的数据为 10 次检测平均值）。

所以 Application OnCreate 避免在主线程做大量耗时操作，例如和 IO 相关的逻辑，这样都会影响到应用启动速度。如果必须要做需要放到子线程中。

##### Activity onCreate（）优化

减少 LaunchActivity 的 View 层级，减少 View 测量绘制时间。
避免主线程做耗时操作

##### 用户体验优化

消除启动时的白屏 / 黑屏

![](https://user-gold-cdn.xitu.io/2018/9/25/1661104e95b67c3f?imageslim)冷启动白屏. gif

为什么启动时会出现短暂黑屏或白屏的现象？当用户点击你的 app 那一刻到系统调用 Activity.onCreate() 之间的这个时间段内，WindowManager 会先加载 app 主题样式中的 windowBackground 做为 app 的预览元素，然后再真正去加载 activity 的 layout 布局。

很显然，如果你的 application 或 activity 启动的过程太慢，导致系统的 BackgroundWindow 没有及时被替换，就会出现启动时白屏或黑屏的情况（取决于你的主题是 Dark 还是 Light）。

![](https://user-gold-cdn.xitu.io/2018/9/25/1661104e9747f24c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

##### 解决方案

1\. 甩锅给系统

使用透明主题:

```
<item >true</item>复制代码
```

Activity.onCreate() 之前 App 不做显示，这样用户误以为是手机慢了，这种瞒天过海的方案大家还是不要用了。

```
<resources>    <!-- Base application theme. -->    <style >        <!-- Customize your theme here. -->        <item >@color/colorPrimary</item>        <item >@color/colorPrimaryDark</item>        <item >@color/colorAccent</item>        <item >true</item>    </style></resources>复制代码
```

效果如下：

![](https://user-gold-cdn.xitu.io/2018/9/25/1661104e95d4aacf?imageslim)甩锅 .gif

2\. 主题替换
我们在 style 中自定义一个样式 Lancher，在其中放一张背景图片，或是广告图片之类的

```
<style >        <item >@drawable/bg</item>    </style>复制代码
```

把这个样式设置给启动的 Activity

```
<activity            android:            android:screenOrientation="portrait"            android:theme="@style/AppTheme.Launcher"            >复制代码
```

然后在 Activity 的 onCreate 方法，把 Activity 设置回原来的主题

```
@Override    protected void onCreate(Bundle savedInstanceState) {        //替换为原来的主题，在onCreate之前调用        setTheme(R.style.AppTheme);        super.onCreate(savedInstanceState);    }复制代码
```

这样在启动时就通过给用户看一张图片或是广告来防止黑白屏的尴尬。

![](https://user-gold-cdn.xitu.io/2018/9/25/1661104f24ccf7cf?imageslim)![](https://user-gold-cdn.xitu.io/2018/6/6/163d3319859594d4?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)欢迎关注我的微信公众号「玉刚说」，接收第一手技术干货