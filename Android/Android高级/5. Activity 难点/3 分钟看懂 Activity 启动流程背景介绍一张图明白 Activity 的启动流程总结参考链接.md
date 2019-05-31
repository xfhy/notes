> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/entry/58f5b68e61ff4b005807ab47 ![](http://upload-images.jianshu.io/upload_images/1869462-1ee9c9600c8ac2cd.png?imageMogr2/auto-orient/strip%7CimageView2/2)
_Android_

# 背景介绍

从事开发到了一定阶段，想要提高就必须搞明白系统的一些工作原理。为什么？因为只有明白了这些，你才能针对平台的特性写出优质的代码。当遇到棘手的问题时，你才能更快速的结合系统原理去寻找最优解决方案。底层基础决定上层建筑。这个原理在开发中同样适用。我是提倡 **回归基础** 的。高级的功能总是由最基本的元件构成，就好比为数不多的元素构成了我们难以想象的丰富的物质世界一样。**只有掌握了最根本的内容，才能促使你爆发出难以想象的创造力来！**

重视基础，回归基础。回到最初，去探寻灵感。 愿与君共勉✌️！

# 一张图明白 Activity 的启动流程

![](http://upload-images.jianshu.io/upload_images/1869462-882b8e0470adf85a.jpg?imageMogr2/auto-orient/strip%7CimageView2/2)
_Activity 启动流程_

本篇主要讲的是从一个 App 启动，到 Activity 执行 onCreate() 的流程。后面关于 Activity 的生命周期相信大家基本都耳熟能详了。

上图中我把涉及到的类名方法命均列出来了，你可以看着流程，打开源码跟着过一遍。相信在过完一遍之后，在今后的开发中你会更加自信！

上图乍一看可能感觉有些眼花缭乱，但请不要惧怕。其实根本就没什么东西😂，你只需要从蓝色箭头开始看下去，会发现一下就看完了。在结合下面简要的分析，3 分钟内你就能搞明白 Activity 的启动流程。

关于 Activity 的启动，我在[【惊天秘密！从 Thread 开始，揭露 Android 线程通讯的诡计和主线程的阴谋】http://www.jianshu.com/p/8862bd2b6a29](https://link.juejin.im?target=http%3A%2F%2Fwww.jianshu.com%2Fp%2F8862bd2b6a29) 一文中有提到过。这篇文章主要讲的是 Thread 线程到底是个什么东西，以及 Android 中的消息机制。感兴趣可以点链接看一看。

## 一切从 main() 方法开始

Android 中，一个应用程序的开始可以说就是从 **ActivityThread.java** 中的 main() 方法开始的。都是学过 Java 的人，想必也都知道 Java 的程序入口就是 main() 方法。Android 其实也就是一个 Java 程序而已。

从上图可以看到，main() 方法中主要做的事情有：

1.  初始化主线程的 Looper、主 Handler。并使主线程进入等待接收 Message 消息的无限循环状态。关于 Android 的 Handler 机制，可以参考一下我上面提到的文章：
    [【惊天秘密！从 Thread 开始，揭露 Android 线程通讯的诡计和主线程的阴谋】http://www.jianshu.com/p/8862bd2b6a29](https://link.juejin.im?target=http%3A%2F%2Fwww.jianshu.com%2Fp%2F8862bd2b6a29)
    下面是 main() 方法中比较关键的代码：

<pre>public static void main(String[] args){
    ...
    Looper.prepareMainLooper(); 
    //初始化Looper
    ...
    ActivityThread thread = new ActivityThread();
    //实例化一个ActivityThread
    thread.attach(false);
    //这个方法最后就是为了发送出创建Application的消息
    ... 
    Looper.loop();
    //主线程进入无限循环状态，等待接收消息
}
</pre>

2\. 调用 attach() 方法，主要就是为了发送出初始化 Application 的消息。这个流程说长不长，说短不短。下文会再捋一捋。

## 创建 Application 的消息是如何发送的呢？

上面提到过，ActivityThread 的 attach() 方法最终的目的是发送出一条创建 Application 的消息——H.BIND_APPLICATION，到主线程的主 Handler 中。那我们来看看 attach() 方法干了啥。
attach() 关键代码：

<pre>public void attach(boolean system){
    ...
    final IActivityManager mgr = ActivityManagerNative.getDefault();  
    //获得IActivityManager实例，下面会看看它是个啥
    try {
        mgr.attachApplication(mAppThread);
         //看见没？关键啊。mAppThread这个参数下面也会说一下
    } catch (RemoteException ex) {
        throw ex.rethrowFromSystemServer();
    }
    ...
}
</pre>

莫慌莫慌，下面看看上面出现的两个对象是个啥。

### IActivityManager mgr 是个啥？

从上图也可以看到，IActivityManager 是一个接口，当我们调用`ActivityManagerNative.getDefault()`获得的实际是一个代理类的实例——**ActivityManagerProxy**，这个东西实现了 IActivityManager 接口。打开源码你会发现，**ActivityManagerProxy** 是 ActivityManagerNative 的一个内部类。可以看出，Android 团队在设计的过程中是实践了**最小惊异原则**的，就是把相关的东西尽量放在一起。那么既然是个代理类，它究竟代理了谁？代码里看看喽😏。
下面这个代码稍微有点绕啊！老哥，稳住！

1.  先看 ActivityManagerProxy 的构造函数：

    <pre>public ActivityManagerProxy(IBinder remote) {
         mRemote = remote;
    }
    </pre>

    这个构造函数非常的简单。首先它需要一个 IBinder 参数，然后赋值给 **mRemote** 变量。这个 **mRemote** 显然是 ActivityManagerNative 的成员变量。但对它的操作是由 ActivityManagerProxy 来代理间接进行的。这样设计的好处是保护了 mRemote，并且能够在操作 mRemote 前执行一些别的事务，并且我们是以 IActivityManager 的身份来进行这些操作的！这就非常巧妙了👍。

2.  那么这个构造函数是在那调用的呢？

    <pre>static public IActivityManager asInterface(IBinder obj) {
     if (obj == null) {
         return null;
     }
     IActivityManager in =
         (IActivityManager)obj.queryLocalInterface(descriptor);
     //先检查一下有没有
     if (in != null) {
         return in;
     }
     ...
     return new ActivityManagerProxy(obj);
     //这个地方调用了构造函数
    }
    </pre>

    上面这个方法是 ActivityManagerNative 中的一个静态方法，它会调用到 ActivityManagerProxy 的构造方法。然而，这个静态方法也需要一个 IBinder 作为参数! 老夫被绕晕了😂。但是不怕，咱们继续往找！

3.  getDefault() 获取到的静态常量 gDefault

    <pre>private static final Singleton<IActivityManager> gDefault = 
    new Singleton<IActivityManager>() {
     protected IActivityManager create() {
        IBinder b = ServiceManager.getService("activity");
        //重点啊！IBinder实例就是在这里获得的。
         ...
         IActivityManager am = asInterface(b);
         //调用了上面的方法。
         ...
         return am;
     }
    };
    </pre>

    这是 ActivityManagerNative 的静态常量，它是一个单例。在其中终于获得了前面一直在用的 IBinder 实例。

    <pre>IBinder b = ServiceManager.getService("activity");
    </pre>

    试着在上图中找到对应位置。

这里是通过 **ServiceManager** 获取到 **IBinder** 实例的。如果你以前了解 **AIDL** 通讯流程的话。这可能比较好理解一点，这只是通过另一种方式获取 **IBinder** 实例罢了。获取 **IBinder** 的目的就是为了通过这个 **IBinder** 和 **ActivityManager** 进行通讯，进而 **ActivityManager** 会调度发送 **H.BIND_APPLICATION** 即初始化 Application 的 Message 消息。如果之前没接触过 **Binder** 机制的话，只需知道这个目的就行了。我后面会写一篇专门介绍 Android 中 Binder 机制的文章。当然，你也可以参考一下罗大的系列文章，写的很详细，非常的很赞！ [【Android 系统进程间通信 Binder 机制在应用程序框架层的 Java 接口源代码分析
】http://m.blog.csdn.net/article/details?id=6642463](https://link.juejin.im?target=http%3A%2F%2Fm.blog.csdn.net%2Farticle%2Fdetails%3Fid%3D6642463)。

1.  再来看看 attachApplication(mAppThread) 方法。

    <pre>public void attachApplication(IApplicationThread app){
    ...
    mRemote.transact(ATTACH_APPLICATION_TRANSACTION, data, reply, 0);  
    ...
    }
    </pre>

    这个方法我在上图中也体现出来了。

这个方法中上面这一句是关键。调用了 IBinder 实例的 tansact() 方法，并且把参数 app(这个参数稍后就会提到) 放到了 data 中，最终传递给 ActivityManager。

现在，我们已经基本知道了 IActivityManager 是个什么东东了。其实最重要的就是它的一个实现类 **ActivityManagerProxy**，它主要代理了内核中与 **ActivityManager** 通讯的 **Binder** 实例。下面再看看 **ApplicationThread mAppThread**。

### ApplicationThread mAppThread 又是个啥？

1.  在 ActivityThread 的成员变量中，你能够发现：

    <pre>final ApplicationThread mAppThread = new ApplicationThread();
    </pre>

    ApplicationThread 是作为 ActivityThread 中的一个常量出现的。这表明系统不希望这个变量中途被修改，可见这个变量具有特定而十分重要的作用。
2.  我们看看他是啥。

    <pre>private class ApplicationThread extends ApplicationThreadNative{
     ...
    }
    </pre>

    ApplicationThread 是 ActivityThread 中的一个内部类，为什么没有单独出来写在别的地方呢？我觉得这也是对最小惊异原则的实践。因为 ApplicationThread 是专门真对这里使用的对象。
3.  它继承自 ApplicationThreadNative，我们再看看它是个啥。

    <pre>public abstract class ApplicationThreadNative extends Binder 
     implements IApplicationThread{
     ...
     //无参构造函数
     public ApplicationThreadNative() {
         //这是Binder的
         attachInterface(this, descriptor);
     }
     ...
    }
    </pre>

    那么很明显，ApplicationThread 最终也是一个 Binder！同时，由于实现了 IApplicationThread 接口，所以它也是一个 IApplicationThread。以上这系对应关系你都可以在上图中找到。

我们在 ActivityThread 中看到的 ApplicationThread 使用的构造函数是无参的，所以看上面无参构造函数都干了啥！

Binder 的 attachInterface(IInterface owner, String descriptor) 方法没什么特别的，就是赋值了。

<pre>public void attachInterface(IInterface owner, String descriptor) {
    mOwner = owner;
    mDescriptor = descriptor;
}
</pre>

4\. 那么 IApplicationThread 又是啥？老铁，走着！我们继续挖。

<pre>public interface IApplicationThread extends IInterface {
    ...
    String descriptor = "android.app.IApplicationThread"; 
    //留意下这个参数
    ...
}
</pre>

好吧，这在上图中没有，挖的有点什么了。但是学习嘛，咱就看看喽。

IApplicationThread 是继承了 IInterface 的一个接口，我们需要关注一下里面的 descriptor 参数。后面会用它，它是一个标识，查询的时候很重要。

好，我们终于知道 attach() 方法中出现的两个对象是啥了。ApplicationThread 作为 IApplicationThread 的一个实例，承担了最后发送 Activity 生命周期、及其它一些消息的任务。也就是说，前面绕了一大圈，最后还是回到这个地方来发送消息。我擦！

也许你会想，既然在 ActivityThread 中我们已经创建出了 ApllicationThread 的了，为什么还要绕这么弯路？😄，当然是为了让系统根据情况来控制这个过程喽，不然为什么要把 ApplicationThread 传到 ActivityManager 中呢？

### ActivityManagerService 调度发送初始化消息

经过上面的辗转，ApplicationThread 终于到了 ActivityManagerService 中了。请在上图中找到对应位置！

从上图中可以看到，ActivityManagerService 中有一这样的方法：

<pre>private final boolean attachApplicationLocked(IApplicationThread thread
, int pid) {
    ...
    thread.bindApplication();
    //注意啦！
    ...
}
</pre>

ApplicationThread 以 IApplicationThread 的身份到了 ActivityManagerService 中，经过一系列的操作，最终被调用了自己的 bindApplication() 方法，发出初始化 Applicationd 的消息。

<pre>public final void bindApplication(String processName, 
    ApplicationInfo appInfo,
    List<ProviderInfo> providers, 
    ComponentName instrumentationName,
    ProfilerInfo profilerInfo, 
    Bundle instrumentationArgs,
    IInstrumentationWatcher instrumentationWatcher,
    IUiAutomationConnection instrumentationUiConnection, 
    int debugMode,
    boolean enableBinderTracking, 
    boolean trackAllocation,
    boolean isRestrictedBackupMode, 
    boolean persistent, 
    Configuration config,
    CompatibilityInfo compatInfo, 
    Map<String, IBinder> services, 
    Bundle coreSettings){

    ...
    sendMessage(H.BIND_APPLICATION, data);
}
</pre>

吓屎老纸！这么多参数。这明明很违反参数尽量要少的原则嘛！所以说，有的时候，开发过程中还是很难避免一些参数堆积的情况的。也不能一概而论。

但是，这个地方，我们只要知道最后发了一条 **H.BIND_APPLICATION** 消息，接着程序开始了。

## 收到初始化消息之后的世界

上面我们已经找到初始化 Applicaitond 的消息是在哪发送的了。现在，需要看一看收到消息后都发生了些什么。

现在上图的 H 下面找到第一个消息：**H.BIND_APPLICATION**。一旦接收到这个消息就开始创建 Application 了。这个过程是在 handleBindApplication() 中完成的。看看这个方法。在上图中可以看到对应的方法。

<pre>private void handleBindApplication(AppBindData data) {
    ...
    mInstrumentation = (Instrumentation)
        cl.loadClass(data.instrumentationName.getClassName())
        .newInstance();
    //通过反射初始化一个Instrumentation仪表。后面会介绍。
    ...
    Application app = data.info.makeApplication(data.restrictedBackupMode, null);
    //通过LoadedApp命令创建Application实例
    mInitialApplication = app;
    ...
    mInstrumentation.callApplicationOnCreate(app);
    //让仪器调用Application的onCreate()方法
    ...
}
</pre>

handleBindApplication() 是一个很长的方法，但是我为各位看官精选出了上面这几句代码。对于本篇的主题来说，他们是至关重要的。上面短短的代码中出现了几个新对象。下面我会一一道来。

### Instrumentation 仪表，什么鬼？

1\. 这个叫 Instrumentation 仪表的东西十分诡异，姑且翻译为仪器吧。字面上看不出任何它是干什么的线索。但是，我们可以打开文档看看喽。

> Instrumentation 会在应用程序的任何代码运行之前被实例化，它能够允许你监视应用程序和系统的所有交互。

大概就这个意思啦。

2\. 但是，从上面的代码我们可以看出，Instrumentation 确实是在 Application 初始化之前就被创建了。那么它是如何实现监视应用程序和系统交互的呢？

打开这个类你可以发现，最终 Apllication 的创建，Activity 的创建，以及生命周期都会经过这个对象去执行。简单点说，就是把这些操作包装了一层。通过操作 Instrumentation 进而实现上述的功能。

3\. 那么这样做究竟有什么好处呢？仔细想想。Instrumentation 作为抽象，当我们约定好需要实现的功能之后，我们只需要给 Instrumentation 仪表添加这些抽象功能，然后调用就好。剩下的，不管怎么实现这些功能，都交给 Instrumentation 仪器的实现对象就好。啊！这是多态的运用。啊！这是依赖抽象，不依赖具体的实践。啊！这是上层提出需求，底层定义接口，即依赖倒置原则的践行。呵！抽象不过如此。

从代码中可以看到，这里实例化 Instrumentation 的方法是反射！而反射的 ClassName 是来自于从 ActivityManagerService 中传过来的 Binder 的。套路太深！就是为了隐藏具体的实现对象。但是这样耦合性会很低。

4\. 好了，不瞎扯了。既然在说 Instrumentation，那就看看最后调的 callApplicationOnCreate() 方法。

<pre>public void callApplicationOnCreate(Application app) {
    app.onCreate();
}
</pre>

你没看错，它啥也没干。只是调用了一下 Application 的 onCreate() 方法。这就是为什么它能够起到监控的作用。

在上图中你能够看到 Instrumentation，以及它的交互过程。

### LoadedApk 就是 data.info 哦！

关于它是怎么来的本篇就不说了，以后可能会介绍下。本篇就看流程就好。所以直接进去看它的 makeApplication() 干了啥，就把 Application 给创建了。

<pre>public Application makeApplication(boolean forceDefaultAppClass,
    Instrumentation instrumentation) {
    ...
    String appClass = mApplicationInfo.className;
    //Application的类名。明显是要用反射了。
    ...
    ContextImpl appContext = ContextImpl.createAppContext(mActivityThread
        , this);
    //留意下Context
    app = mActivityThread.mInstrumentation
        .newApplication( cl, appClass, appContext);
    //通过仪表创建Application
    ...
}
</pre>

在这个方法中，我们需要知道的就是，在取得 Application 的实际类名之后，最终的创建工作还是交由 Instrumentation 去完成，就像前面所说的一样。

值得留意的是，就像上图所标注的一样，当需要第二次获取 Application 时，同样只需要调用这个方法就好。“真是方便！”

### 现在把目光移回 Instrumentation

看看 newApplication() 中是如何完成 Application 的创建的。

<pre>static public Application newApplication(Class<?> clazz
    , Context context) throws InstantiationException
    , IllegalAccessException
    , ClassNotFoundException {
        Application app = (Application)clazz.newInstance();
        //反射创建，简单粗暴
        app.attach(context);
        //关注下这里，Application被创建后第一个调用的方法。
        //目的是为了绑定Context。
        return app;
    }
</pre>

我的天，绕了这么多，这 Application 可算是创建出来了。快给自己一个小红花吧😊！

## LaunchActivity

当 Application 初始化完成后，系统会更具 Manifests 中的配置的启动 Activity 发送一个 Intent 去启动相应的 Activity。这个过程本篇先不提，下次再说。主要看流程！

1.  直接的，H 就收到了一条 LAUNCH_ACTIVITY 的消息。然后开始初始化 Activity 之旅。收到消息后，真正处理是在 ActivityThread 中的 handleLaunchActivity() 中进行的。是不是迫不及待的想要知道发生了啥？快在上图中找到对应的步骤吧！

    <pre>private void handleLaunchActivity(ActivityClientRecord r
     , Intent customIntent
     , String reason) {
     ...
     Activity a = performLaunchActivity(r, customIntent);
     //妈蛋！又封装到另一个方法中创建了。
     ...
     if (a != null) {
         ...
         handleResumeActivity(r.token
         , false
         , r.isForward
         ,!r.activity.mFinished && !r.startsNotResumed
         , r.lastProcessedSeq, reason);
         //Activity创建成功就往onResume()走了！
         ...
     }
    }
    </pre>

    从上面的代码中可以看出... 好吧，什么都看不出来！
2.  再走一个方法。

    <pre>private Activity performLaunchActivity(ActivityClientRecord r
     , Intent customIntent) {
     ...
     activity = mInstrumentation.newActivity(
          cl, component.getClassName(), r.intent);
     //通过仪表来创建Activity
     ...
      Application app = r.packageInfo.makeApplication(false
      , mInstrumentation);
      //前面说过，是在获取Application
     ...
     activity.attach(appContext
         , this
         , getInstrumentation()
         , r.token
         ,.ident
         , app
         , r.intent
         , r.activityInfo
         , title
         , r.parent
         , r.embeddedID
         , r.lastNonConfigurationInstances
         , config
         ,r.referrer
         , r.voiceInteractor
         , window);
     //方法怪出现！
     ...
     if (r.isPersistable()) {
         mInstrumentation.callActivityOnCreate(
           activity, r.state, r.persistentState);
     } else {
         mInstrumentation.callActivityOnCreate(activity, r.state);
     }
     //根据是否可持久化选择onCreate()方法。
     ...
    }
    </pre>

    这个方法内容较多，我们一个个看。
3.  <pre>activity = mInstrumentation.newActivity(
          cl, component.getClassName(), r.intent);
    </pre>

    正如前面所说，Activity、Application 的创建及生命周期都被承包给 Instrumentation 仪表了。所以由它来负责。看看 Instrumentation 干了啥。

    <pre>public Activity newActivity(ClassLoader cl, String className,
             Intent intent)
             throws InstantiationException
             , IllegalAccessException,
             ClassNotFoundException {
         return (Activity)cl.loadClass(className).newInstance();
         //真的没干啥。反射实例化Activity而已
     }
    </pre>

    就是反射出一个 Activity 而已。

4.  <pre>if (r.isPersistable()) {
         mInstrumentation.callActivityOnCreate(
           activity, r.state, r.persistentState);
     } else {
         mInstrumentation.callActivityOnCreate(activity, r.state);
     }
    </pre>

    根据是否可持久化选择 Activity 的 onCreate() 方法。同样是通过 Instrumentation 仪表来执行 onCreate() 的。它两分别对应的 onCreate() 方法为：

    <pre>onCreate(icicle, persistentState);
    //可获得持久化数据
    </pre>

    和

    <pre>onCreate(icicle);
    //平时重写的最多的。
    </pre>

中间两个方法留意一下就好，就不在解释的，感兴趣的点源码看看。

到此，Activity 就跑起来了！怎么样？是不是并不复杂。

# 总结

本篇就到此结束了。本篇主要流程是从 Application 创建开始，到第一个 Activity onCreate() 结束的。这了流程也不算长，关键是结合上面的图来看。重点环节我都用不同的颜色标记出来了。

看到这里的同学奖励自己一包辣条吧！😄

# 参考链接

1.  [【Android 系统进程间通信 Binder 机制在应用程序框架层的 Java 接口源代码分析
    】http://m.blog.csdn.net/article/details?id=6642463](https://link.juejin.im?target=http%3A%2F%2Fm.blog.csdn.net%2Farticle%2Fdetails%3Fid%3D6642463)

2.  [【Instrumentation API】https://developer.android.com/reference/android/app/Instrumentation.html](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Freference%2Fandroid%2Fapp%2FInstrumentation.html)

3.  [【Activity 启动流程（下）】http://www.jianshu.com/p/3cf90c633bb1](https://link.juejin.im?target=http%3A%2F%2Fwww.jianshu.com%2Fp%2F3cf90c633bb1)
4.  [【Android 学习——ActivityManager 与 Proxy 模式的运用】http://www.cnblogs.com/bastard/archive/2012/05/25/2517522.html](https://link.juejin.im?target=http%3A%2F%2Fwww.cnblogs.com%2Fbastard%2Farchive%2F2012%2F05%2F25%2F2517522.html)
5.  [【ActivityManager API】https://developer.android.com/reference/android/app/ActivityManager.html](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Freference%2Fandroid%2Fapp%2FActivityManager.html)

**感谢你的阅读。如果你觉得对你有用的话，记得给 CoorChice 点个赞，添加下关注哦，谢谢😘！**