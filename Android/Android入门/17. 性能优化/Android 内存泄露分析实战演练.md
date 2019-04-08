> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/_s88Xjti0YwO4rayKvF5Dg

<section class="" style="font-size: 15px;color: rgb(84, 84, 84);margin-left: 6px;margin-right: 6px;line-height: 1.6;letter-spacing: 1px;word-break: break-all;font-family: PingFangSC-Regular, &quot;Helvetica Neue&quot;, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;">

> 本文由**玉刚说写作平台**提供写作赞助
> 原作者：**四月葡萄**
> 赞助金额：**200 元**

### 1\. 内存泄露简介

内存泄露，即 Memory Leak，指程序中不再使用到的对象因某种原因从而无法被 GC 正常回收。发生内存泄露，会导致一些不再使用到的对象没有及时释放，这些对象占用了宝贵的内存空间，很容易导致后续需要分配内存的时候，内存空间不足而出现 OOM（内存溢出）。无用对象占据的内存空间越多，那么可用的空闲空间也就越少，GC 就会更容易被触发，GC 进行时会停止其他线程的工作，因此有可能会造成界面卡顿等情况。

为什么不再使用到的对象无法被 GC 正常回收呢？这是因为还有其他对象持有无用对象的引用。为什么其他对象会持有无用对象的引用呢？这通常是我们意外地引进了无用对象的引用。从而导致无用对象无法给正常回收。

**常见的内存泄露点**

1.  静态变量

2.  非静态内部类（匿名类）

3.  集合类

4.  使用资源对象后未关闭

后面会对这些内存泄露点逐一分析。

## 2\. 常见内存泄露例子及解决方案

### 2.1 静态变量内存泄露

> 说明：静态变量的生命周期跟整个程序的生命周期一致。只要静态变量没有被销毁也没有置 null，其对象就一直被保持引用，也就不会被垃圾回收, 从而出现内存泄露。

像`static Context`这种，`lint`直接就报警告了，所以建议就别使用了，如下图:

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WzdfWEvNfibBFGVe3iaddtqTSfAz7oEibrWE39327qCaXadq0CwW3gY7DInAmMRFTrLJxXchoeQzC2rg/640?wx_fmt=png)静态 Context.png

来看个比较隐蔽的例子

```
public class MainActivity extends Activity {    public static Test sTest;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_main);        sTest = new Test(this);    }}//外部Test类public class Test {    Test(Context context) {    }}
```

`sTest`作为静态变量，并且持有`Activity`的引用，`sTest`的生命周期肯定比`Activity`长。因此当`Activity`退出后，由于`Activity`仍然被`sTest`引用到，所以`Activity`就不能被回收，造成了内存泄露。

`Activity`这种占用内存非常多的对象，内存泄露的话影响非常大。

**解决方案**：

*   针对静态变量
    在不用静态变量时置为空，如：

```
sTest = null;
```

*   针对`Context`
    如果用到`Context`，尽量去使用`Applicaiton`的`Context`，避免直接传递`Activity`，比如：

```
sTest = new Test(getApplicationContext());
```

*   针对`Activity`
    若一定要使用`Activity`，建议使用弱引用或者软引入来代替强引用。如下：

```
//弱引用WeakReference<Activity> weakReference = new WeakReference<Activity>(this);Activity activity = weakReference.get();//软引用SoftReference<Activity> softReference=new SoftReference<Activity>(this);Activity activity1 = softReference.get();
```

> 注意：单例模式其生命周期跟应用一样，所以使用单例模式时传入的参数需要注意一下，避免传入`Activity`等对象造成内存泄露。

### 2.2 非静态内部类（匿名类）内存泄露

> 说明：非静态内部类 （匿名类）默认就持有外部类的引用，当非静态内部类（匿名类）对象的生命周期比外部类对象的生命周期长时，就会导致内存泄露。

#### 2.2.1 Handler 内存泄露

一般我们都是使用内部类来实现`Handler`，然后`lint`就直接飘黄警告了, 如下：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WzdfWEvNfibBFGVe3iaddtqTSmf3ePkCHXApNNaspyXzt7k7Bj4T2hdfuGicRkQPBov3zoiaQzWia8Mibwg/640?wx_fmt=png)非静态内部类. png

这里会涉及到`Handler`的原理，如果还不懂`Handler`原理的话，建议先去看下。

如果`Handler`中有延迟的任务或者是等待执行的任务队列过长，都有可能因为`Handler`继续执行而导致`Activity`发生泄漏。

> 1\. 首先，非静态的`Handler`类会默认持有外部类的引用，包含`Activity`等。
> 2\. 然后，还未处理完的消息（`Message`）中会持有`Handler`的引用。
> 3\. 还未处理完的消息会处于消息队列中，即消息队列`MessageQueue`会持有`Message`的引用。
> 4\. 消息队列`MessageQueue`位于`Looper`中，`Looper`的生命周期跟应用一致。

因此，此时的引用关系链是`Looper` -> `MessageQueue` -> `Message` -> `Handler` -> `Activity`。所以，这时退出`Activity`的话，由于存在上述的引用关系，垃圾回收器将无法回收`Activity`，从而造成内存泄漏。

**解决方法**：

*   静态内部类 + 弱引用
    静态内部类默认不持有外部类的引用, 所以改成静态内部类即可。同时，这里采用弱引用来持有`Activity`的引用。

```
    private static class MyHalder extends Handler {        private WeakReference<Activity> mWeakReference;        public MyHalder(Activity activity) {            mWeakReference = new WeakReference<Activity>(activity);        }        @Override        public void handleMessage(Message msg) {            super.handleMessage(msg);            //...        }    }
```

*   `Activity`退出时，移除所有信息
    移除信息后，`Handler`将会跟`Activity`生命周期同步。

```
    @Override    protected void onDestroy() {        super.onDestroy();        mHandler.removeCallbacksAndMessages(null);    }
```

#### 2.2.2 多线程引起的内存泄露

我们一般使用匿名类等来启动一个线程，如下：

```
        new Thread(new Runnable() {            @Override            public void run() {            }        }).start();
```

同样，匿名`Thread`类里持有了外部类的引用。当`Activity`退出时，`Thread`有可能还在后台执行，这时就会发生了内存泄露。

**解决方法**：

*   静态内部类
    静态内部类不持有外部类的引用，如下：

```
private static class MyThread extends Thread{        //...      }
```

*   `Activity`退出时，结束线程
    同样，这里也是让线程的生命周期跟`Activity`一致。

其他非静态内部类（匿名类），都可以按照这个套路来：一个是改成静态内部类，另外一个就是内部类的生命周期不要超过外部类。

### 2.3 集合类内存泄露

> 说明：集合类添加元素后，将会持有元素对象的引用，导致该元素对象不能被垃圾回收，从而发生内存泄漏。

举个例子:

```
        List<Object> objectList = new ArrayList<>();        for (int i = 0; i < 10; i++) {            Object o = new Object();            objectList.add(o);            o = null;        }
```

虽然`o`已经被置空了，但是集合里还是持有`Object`的引用。

**解决方法**：

*   清空集合对象
    如下：

```
    objectList.clear();    objectList=null;
```

### 2.4 未关闭资源对象内存泄露

> 说明：一些资源对象需要在不再使用的时候主动去关闭或者注销掉，否则的话，他们不会被垃圾回收，从而造成内存泄露。

以下是一些常见的需要主动关闭的资源对象：

*   1\. 注销广播
    如果广播在`Activity`销毁后不取消注册，那么这个广播会一直存在系统中，由于广播持有了`Activity`的引用，因此会导致内存泄露。

```
    unregisterReceiver(receiver);
```

*   2\. 关闭输入输出流等
    在使用 IO、File 流等资源时要及时关闭。这些资源在进行读写操作时通常都使用了缓冲，如果不及时关闭，这些缓冲对象就会一直被占用而得不到释放，以致发生内存泄露。因此我们在不需要使用它们的时候就应该及时关闭，以便缓冲能得到释放，从而避免内存泄露。

```
    InputStream.close();    OutputStream.close();
```

*   3\. 回收 Bitmap
    `Bitmap`对象比较占内存，当它不再被使用的时候，最好调用`Bitmap.recycle()`方法主动进行回收。

```
    Bitmap.recycle()；    Bitmap = null;
```

*   4\. 停止动画
    属性动画中有一类无限动画，如果`Activity`退出时不停止动画的话，动画会一直执行下去。因为动画会持有`View`的引用，`View`又持有`Activity`, 最终`Activity`就不能给回收掉。只要我们在`Activity`退出把动画停掉即可。

```
    animation.cancel();
```

*   5\. 销毁 WebView
    `WebView`在加载网页后会长期占用内存而不能被释放，因此我们在`Activity`销毁后要调用它的`destory()`方法来销毁它以释放内存。此外，`WebView`在 Android  5.1 上也会出现其他的内存泄露。具体可以看下这篇文章：WebView 内存泄漏解决方法。
    所以，要防止`WebView`内存泄露还是比较复杂的。代码如下：

```
@Overrideprotected void onDestroy() {    if( mWebView!=null) {        ViewParent parent = mWebView.getParent();        if (parent != null) {            ((ViewGroup) parent).removeView(mWebView);        }        mWebView.stopLoading();        // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错        mWebView.getSettings().setJavaScriptEnabled(false);        mWebView.clearHistory();        mWebView.clearView();        mWebView.removeAllViews();        mWebView.destroy();    }    super.on Destroy();}
```

所以，总的来说，该关的对象一律都主动去关掉，留着也没啥用。

## 3\. 常用内存泄露检测工具介绍

### 3.1  lint

`lint`是一个静态代码分析工具，同样也可以用来检测部分会出现内存泄露的代码，平时写码注意`lint`飘出来的各种黄色警告即可。如：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WzdfWEvNfibBFGVe3iaddtqTSmf3ePkCHXApNNaspyXzt7k7Bj4T2hdfuGicRkQPBov3zoiaQzWia8Mibwg/640?wx_fmt=png)非静态内部类. png

### 3.2 leakcanary

`leakcanary`是 square 开源的一个库，能够自动检测发现内存泄露，其使用也很简单：
在`build.gradle`中添加依赖：

```
dependencies {  debugImplementation 'com.squareup.leakcanary:leakcanary-android:1.6.1'  releaseImplementation 'com.squareup.leakcanary:leakcanary-android-no-op:1.6.1'  //可选项，如果使用了support包中的fragments  debugImplementation 'com.squareup.leakcanary:leakcanary-support-fragment:1.6.1'}
```

如果遇到下面这个问题：

```
Failed to resolve: com.squareup.leakcanary:leakcanary-android
```

根目录下的`build.gradle`添加`mavenCentral()`即可，如下：

```
allprojects {    repositories {        google()        jcenter()        mavenCentral()    }}
```

然后在自定义的`Application`中调用以下代码就可以了。

```
public class MyApplication extends Application {    @Override    public void onCreate() {        super.onCreate();        if (LeakCanary.isInAnalyzerProcess(this)) {            return;        }        LeakCanary.install(this);        //正常初始化代码    }}
```

好了，完事。然后我们写一个内存泄露的例子，来测试一下:

```
public class MainActivity extends Activity {    public static Context sContext;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_main);        sContext = this;    }}
```

例子够简单了吧，运行起来，然后退出`Activity`。

如果检测到有内存泄漏，通知栏会有提示，如下图；如果没有内存泄漏，则没有提示。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WzdfWEvNfibBFGVe3iaddtqTSSHSV291FggHOhNXkkhEiaTicVjT5LVTicMmic6Kna1lYJicicUSZGt2CdARQ/640?wx_fmt=png)leakcanary-1.png
点击通知栏或者点击 Leaks 那个图标，可以得到内存泄露的信息，如下图所示，然后就可以知道是哪里出现了内存泄漏。
![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WzdfWEvNfibBFGVe3iaddtqTSia4e7ZKxqYN3UV1SbAnh7CwjicLCjZkiaqP2lxZ50xo6Mbo5NzaIZVqiaQ/640?wx_fmt=png)leakcanary-2.png

### 3.3 Memory Profiler

`Memory Profiler` 是 `Android Profiler` 中的一个组件，可以帮助你分析应用卡顿，崩溃和内存泄露等等问题。

打开 `Memory Profiler`后即可看到一个类似下图的视图。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WzdfWEvNfibBFGVe3iaddtqTSVPaTXIqas4lbicRwicB13rpMhDEEsb3icFzSVUcw7U9TJVpeRzdRB1Zgg/640?wx_fmt=png)memory-profiler-callouts_2x.png
上面的红色数字含义如下：

> 1\. 用于强制执行垃圾回收事件的按钮。
> 2\. 用于捕获堆转储的按钮。
> 3\. 用于记录内存分配情况的按钮。 此按钮仅在连接至运行 Android 7.1 或更低版本的设备时才会显示。
> 4\. 用于放大 / 缩小 / 还原时间线的按钮。
> 5\. 用于跳转至实时内存数据的按钮。
> 6.Event 时间线，其显示 `Activity` 状态、用户输入 Event 和屏幕旋转 Event。
> 7\. 内存使用量时间线，其包含以下内容：
> 
> *   一个显示每个内存类别使用多少内存的堆叠图表，如左侧的 y 轴以及顶部的彩色键所示。
>     
>     
> *   虚线表示分配的对象数，如右侧的 y 轴所示。
>     
>     
> *   用于表示每个垃圾回收事件的图标。

如何使用 Memory Profiler 分析内存泄露呢？按以下步骤来即可：
1\. 使用`Memory Profiler`监听要分析的应用进程
2\. 旋转几次要分析的`Activity`。（这是因为旋转`Activity`后会重新创建）
3\. 点击捕获堆转储按钮去捕获堆转储
4\. 在捕获结果中搜索要分析的类。（这里是`MainActivity`）
5\. 点击要分析的类，右边会显示这个类创建对象的数量。

如下图所示：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WzdfWEvNfibBFGVe3iaddtqTSqjjstoWeBJLBJ7mn8nxs9OjTljre5QBwdSS4pDEqa8Wia9OicGRGaUJw/640?wx_fmt=png)使用 Memory Profiler 分析内存泄露. png
可以看到，这里创建了多个`MainActivity`，毫无疑问，这里内存泄露了。

关于`Memory Profiler`的更多使用细节，可以查看官方文档：使用 Memory Profiler 查看 Java 堆和内存分配。

**使用 Memory Profiler 分析内存的技巧**：

使用 `Memory Profiler` 时，您应对应用代码施加压力并尝试强制内存泄漏。 在应用中引发内存泄漏的一种方式是，先让其运行一段时间，然后再检查堆。泄漏在堆中可能逐渐汇聚到分配顶部。 不过，泄漏越小，您越需要运行更长时间的应用才能看到泄漏。

您还可以通过以下方式之一触发内存泄漏：

1.  将设备从纵向旋转为横向，然后在不同的 `Activity` 状态下反复操作多次。 旋转设备经常会导致应用泄漏 `Activity`、`Context` 或 `View` 对象，因为系统会重新创建`Activity`，而如果您的应用在其他地方保持对这些对象之一的引用，系统将无法对其进行垃圾回收。

2.  处于不同的 `Activity` 状态时，在您的应用与另一个应用之间切换（导航到主屏幕，然后返回到您的应用）。

### 3.4 MAT(Memory Analysis Tools)

一个 Eclipse 的 Java Heap 内存分析工具, 使用 Android Studio 进行开发的需要另外单独下载它。关于 MAT 的使用，可以查看《Android 开发艺术探索》上面的介绍，也可以网上查看相关资料。这里就不细说了。

### 3.5 Memory Monitor、Allocation Tracker 和 Heap Dump

Memory Monitor 和 Heap Dump 可以用来观察内存的使用情况，Allocation Tracker 则可以用来来跟踪内存分配的情况。

这三款工具都是位于`Android Device Monitor`。但是在 Android Studio 3.0 之后，Android Device Monitor 已经不集成到 Android Studio 中了。虽然我们还可以单独打开 Android Device Monitor 来使用，但其实 Android Studio 3.0 之后提供的 Memory Profiler 等功能足于能够替代它。所以，这里也就不多说了，有兴趣的可以自行查找资料去了解。

</section>

— — — END — — —

**推荐阅读**

[又来给大家送福利了](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649493148&idx=1&sn=b2eb05b9b28d738fb25396f95377e216&chksm=8eec8563b99b0c75dd838972fb891b476e1dbe859ecbabadd3056976c17687dc0a25d79bfe2f&scene=21#wechat_redirect) [Android Webview H5 秒开方案实现](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649493130&idx=1&sn=f8c9fe91de36c736c2dd863140baefa9&chksm=8eec8575b99b0c637652f5d59e31b032992c0547cefb700ad9c6b5a3d47853ae9fab89fc0394&scene=21#wechat_redirect)
[谈谈我开知识星球的感想](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649493123&idx=1&sn=6bafdbbaa24addd070b8b600d32b7a47&chksm=8eec857cb99b0c6a8f0d6de089e384f0b698976ff62a261a247e427653711adbe6bee8d30c89&scene=21#wechat_redirect)

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WxEibQVO9fRJVxibanVicVCVL9oZ6Nh6ibZDuVbEwIKMibC6ba9nnM3FTpHka2SYLyDtSwvBhzwCIecdbQ/640?wx_fmt=png)