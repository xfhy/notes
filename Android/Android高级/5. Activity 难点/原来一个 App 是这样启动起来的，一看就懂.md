> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/hOnc_06jkQRlYOE1CQZvEg

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5u4qlRDV7JCPTlhLruwgCE7IwG1NBPXaJxfvDKvhCWSDe0PMuVbEb1pavYAiaAjbELXcNC1UPzbJw/640?wx_fmt=jpeg)

/   今日科技快讯   /

世界卫生组织 (WHO) 近日全票通过了《国际疾病分类》第十一次修订本 (ICD-11)、正式将“游戏成瘾(障碍)” 列为精神类疾病，这一指导方针将于 2022 年开始正式生效。此消息一出便遭到韩国游戏行业和韩国文化体育观光部的公开反对。他们认为 WHO 此举缺乏科学依据，贸然给游戏扣上 “疾病” 的帽子很不妥，将阻碍韩国游戏产业的发展。

/   作者简介   /

本篇文章来自黄林晴的投稿，分享了他对 APP 启动流程的理解，相信会对大家有所帮助！同时也感谢作者贡献的精彩文章。

黄林晴的博客地址：

> https://blog.csdn.net/huangliniqng

/   前言   /

当我们点击手机屏幕上的软件图标时，就可以打开这个软件，看似很简单的过程其实包含了许多的底层交互，看了还不明白，欢迎来打我。

/   启动流程简介  /

首先要知道的是，手机屏幕其实就是一个 Activity，我们专业点将其称为 Launcher，相信做过车载设备开发的朋友肯定不会陌生，Launcher 是手机厂商提供的，不同的手机厂商比拼的就是 Launcher 的设计。当然我们自己也可以去编写 Launcher，运行在手机上使用自己的桌面风格，当然这里我们不去讲如何去编写一个 Launcher，如果你感兴趣欢迎关注我。

写过 AndroidDemo 的朋友肯定都知道，我们必须在 AndroidManifest 配置文件中配置默认启动的 Activity。

<section>

<pre><intent-filter>
    <action android: />

    <category android: />
</intent-filter>

</pre>

</section>

其实就是告诉 Launcher 该启动这个 App 的哪个页面。这样当系统启动的时候，PackageManger-Service 就可以从配置文件中读取到该启动哪个 Activity。

其次要知道的是，Launch 和其他 APP，运行在不同的进程中，所以他们之间的通信是通过 Binder 去完成的，所以 AMS 是必不可少的。下面我们以启动微信为例，看看启动流程是怎样的。

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwLAb0alumEia8Y4xUjRjpNibHw34rILzXv8bbe1kjTWyQQheouqictnRTw/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwJLic6PPX7ibDZ8zhxiaJjA41bT9JoMsLUiaKSAAiaOJqg7KvqQuekG8Lskw/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwsmSJ5QiaSoLWoxS4RqOaA5s9LEsXAKXsy7DiaxodVb5zmdOGEL3KD5Og/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwJeALYmZPlpDfGW7icxvWgkmYoxS0qNqHEWHMbQWiaTg2bibaDzyXtgfXA/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwxvjicibXxv4oIUCjZ9T8PUDheffQLqiaib0CtyVAIn03Bic0IlZcKF6wLnw/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_gif/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZw3QkmC6QnWLKLMy5AiaVkRyXHcGO4ysvhVW5IRHXpHdapW2164m6Yp9Q/640?wx_fmt=gif)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwicSribAaGnda92L41T2AItM1TzwicWysP1swFBgicZ82IOrcEDZ6nPBdHA/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwC5FfjUt25V11MicZuLQR3ibI4H1d3ibfJaL78gwza79DCbAL5bHO06e7A/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_gif/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwKtURpVDzc3Gsic1IxnpmJibdzHT6RRW3Yu8AEoR8ljvdOB1tacgRelQw/640?wx_fmt=gif)

简单概括启动微信的流程就是：

1.Launcher 通知 AMS 要启动微信了，并且告诉 AMS 要启动的是哪个页面也就是首页是哪个页面

2.AMS 收到消息告诉 Launcher 知道了，并且把要启动的页面记下来

3.Launcher 进入 Paused 状态，告诉 AMS，你去找微信吧

上述就是 Launcher 和 AMS 的交互过程

4.AMS 检查微信是否已经启动了也就是是否在后台运行，如果是在后台运行就直接启动，如果不是，AMS 会在新的进程中创建一个 ActivityThread 对象，并启动其中的 main 函数。

5\. 微信启动后告诉 AMS，启动好了

6.AMS 通过之前的记录找出微信的首页，告诉微信应该启动哪个页面

7\. 微信按照 AMS 通知的页面去启动就启动成功了。

上述阶段是微信和 AMS 的交互过程。那么接下来我们分析下具体过程

/   启动流程分析   /

点击 Launcher 上的微信图标时，会调用 startActivitySafely 方法，intent 中携带微信的关键信息也就是我们在配置文件中配置的默认启动页信息，其实在微信安装的时候，Launcher 已经将启动信息记录下来了，图标只是一个快捷方式的入口。

startActivitySafely 的方法最终还是会调用 Activity 的 startActivity 方法

<section>

<pre>@Override
public void startActivity(Intent intent, @Nullable Bundle options) {
    if (options != null) {
        startActivityForResult(intent, -1, options);
    } else {
        // Note we want to go through this call for compatibility with
        // applications that may have overridden the method.
        startActivityForResult(intent, -1);
    }
}

</pre>

</section>

而 startActivity 方法最终又会回到 startActivityForResult 方法，这里 startActivityForResult 的方法中 code 为 - 1，表示 startActivity 并不关心是否启动成功。startActivityForResult 部分方法如下所示：

<section>

<pre>public void startActivityForResult(@RequiresPermission Intent intent, int requestCode,
        @Nullable Bundle options) {
    if (mParent == null) {
        options = transferSpringboardActivityOptions(options);
        Instrumentation.ActivityResult ar =
            mInstrumentation.execStartActivity(
                this, mMainThread.getApplicationThread(), mToken, this,
                intent, requestCode, options);
        if (ar != null) {
            mMainThread.sendActivityResult(
                mToken, mEmbeddedID, requestCode, ar.getResultCode(),
                ar.getResultData());
        }
        if (requestCode >= 0) {

</pre>

</section>

startActivityForResult 方法中又会调用 mInstrumentation.execStartActivity 方法，我们看到其中有个参数是

<section>

<pre>mMainThread.getApplicationThread()

</pre>

</section>

关于 ActivityThread 曾在 深入理解 Android 消息机制文章中提到过，ActivityThread 是在启动 APP 的时候创建的，ActivityThread 代表应用程序，而我们开发中常用的 Application 其实是 ActivityThread 的上下文，在开发中我们经常使用，但在 Android 系统中其实地位很低的。

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwBJsds5xFAMicwMCXSNKiaDOrLs8Ff2oL8ItxyiaEeBrhLKXmw8ibKjxxXg/640?wx_fmt=jpeg)

Android 的 main 函数就在 ActivityThread 中

<section>

<pre>public static void main(String[] args) {
    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "ActivityThreadMain");
    SamplingProfilerIntegration.start();

    // CloseGuard defaults to true and can be quite spammy.  We
    // disable it here, but selectively enable it later (via
    // StrictMode) on debug builds, but using DropBox, not logs.
    CloseGuard.setEnabled(false);

    Environment.initForCurrentUser();

</pre>

</section>

再回到上面方法

<section>

<pre>mMainThread.getApplicationThread()

</pre>

</section>

得到的是一个 Binder 对象，代表 Launcher 所在的 App 的进程，mToken 实际也是一个 Binder 对象，代表 Launcher 所在的 Activity 通过 Instrumentation 传给 AMS，这样 AMS 就知道是谁发起的请求。

mInstrumentation.execStartActivity

instrumentation 在测试的时候经常用到，instrumentation 的官方文档：

> http://developer.android.com/intl/zh-cn/reference/android/app/Instrumentation.html

这里不对 instrumentation 进行详细介绍了，我们主要接着看 mInstrumentation.execStartActivity 方法

<section>

<pre>public Instrumentation.ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) {
    IApplicationThread whoThread = (IApplicationThread)contextThread;
    if(this.mActivityMonitors != null) {
        Object e = this.mSync;
        synchronized(this.mSync) {
            int N = this.mActivityMonitors.size();

            for(int i = 0; i < N; ++i) {
                Instrumentation.ActivityMonitor am = (Instrumentation.ActivityMonitor)this.mActivityMonitors.get(i);
                if(am.match(who, (Activity)null, intent)) {
                    ++am.mHits;
                    if(am.isBlocking()) {
                        return requestCode >= 0?am.getResult():null;
                    }
                    break;
                }
            }
        }
    }

    try {
        intent.setAllowFds(false);
        intent.migrateExtraStreamToClipData();
        int var16 = ActivityManagerNative.getDefault().startActivity(whoThread, intent, intent.resolveTypeIfNeeded(who.getContentResolver()), token, target != null?target.mEmbeddedID:null, requestCode, 0, (String)null, (ParcelFileDescriptor)null, options);
        checkStartActivityResult(var16, intent);
    } catch (RemoteException var14) {
        ;
    }

    return null;
}
</pre>

</section>

其实这个类是一个 Binder 通信类，相当于 IPowerManager.java 就是实现了 IPowerManager.aidl，我们再来看看 getDefault 这个函数

<section>

<pre>public static IActivityManager getDefault() {
    return (IActivityManager)gDefault.get();
}

</pre>

</section>

getDefault 方法得到一个 IActivityManager，它是一个实现了 IInterface 的接口，里面定义了四大组件的生命周期

<section>

<pre>public static IActivityManager asInterface(IBinder obj) {
    if(obj == null) {
        return null;
    } else {
        IActivityManager in = (IActivityManager)obj.queryLocalInterface("android.app.IActivityManager");
        return (IActivityManager)(in != null?in:new ActivityManagerProxy(obj));
    }
}

</pre>

</section>

最终返回一个 ActivityManagerProxy 对象也就是 AMP，AMP 就是 AMS 的代理对象，说到代理其实就是代理模式，关于什么是代理模式以及动态代理和静态代理的使用可以持续关注我，后面会单独写篇文章进行介绍。

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwmn0S1F7bdwu9gI3d5FugWwCPjRkESx8mttBmebBwjib3MG6y0Jq1W8Q/640?wx_fmt=jpeg)

AMP 的 startActivity 方法

<section>

<pre>public int startActivity(IApplicationThread caller, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, String profileFile, ParcelFileDescriptor profileFd, Bundle options) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    data.writeInterfaceToken("android.app.IActivityManager");
    data.writeStrongBinder(caller != null?caller.asBinder():null);
    intent.writeToParcel(data, 0);
    data.writeString(resolvedType);
    data.writeStrongBinder(resultTo);
    data.writeString(resultWho);
    data.writeInt(requestCode);
    data.writeInt(startFlags);
    data.writeString(profileFile);
    if(profileFd != null) {
        data.writeInt(1);
        profileFd.writeToParcel(data, 1);
    } else {
        data.writeInt(0);
    }

</pre>

</section>

主要就是将数据写入 AMS 进程，等待 AMS 的返回结果，这个过程是比较枯燥的，因为我们做插件化的时候只能对客户端 Hook，而不能对服务端操作，所以我们只能静静的看着。（温馨提示：如果文章到这儿你已经有点头晕了，那就对了, 研究源码主要就是梳理整个流程，千万不要纠结源码细节，那样会无法自拔）。

AMS 处理 Launcher 的信息

AMS 告诉 Launcher 我知道了，那么 AMS 如何告诉 Launcher 呢？

Binder 的通信是平等的，谁发消息谁就是客户端，接收的一方就是服务端，前面已经将 Launcher 所在的进程传过来了，AMS 将其保存为一个 ActivityRecord 对象，这个对象中有一个 ApplicationThreadProxy 即 Binder 的代理对象，AMS 通 ApplicationTreadProxy 发送消息，App 通过 ApplicationThread 来接收这个消息。

Launcher 收到消息后，再告诉 AMS，好的我知道了，那我走了，ApplicationThread 调用 ActivityThread 的 sendMessage 方法给 Launcher 主线程发送一个消息。这个时候 AMS 去启动一个新的进程，并且创建 ActivityThread，指定 main 函数入口。

启动新进程的时候为进程创建了 ActivityThread 对象，这个就是 UI 线程，进入 main 函数后，创建一个 Looper，也就是 mainLooper，并且创建 Application，所以说 Application 只是对开发人员来说重要而已。创建好后告诉 AMS 微信启动好了，AMS 就记录了这个 APP 的登记信息，以后 AMS 通过这个 ActivityThread 向 APP 发送消息。

这个时候 AMS 根据之前的记录告诉微信应该启动哪个 Activity，微信就可以启动了。

<section>

<pre>public void handleMessage(Message msg) {
    ActivityThread.ActivityClientRecord data;
    switch(msg.what) {
    case 100:
        Trace.traceBegin(64L, "activityStart");
        data = (ActivityThread.ActivityClientRecord)msg.obj;
        data.packageInfo = ActivityThread.this.getPackageInfoNoCheck(data.activityInfo.applicationInfo, data.compatInfo);
        ActivityThread.this.handleLaunchActivity(data, (Intent)null);
        Trace.traceEnd(64L);

</pre>

</section>

<section>

<pre>ActivityThread.ActivityClientRecord

</pre>

</section>

就是 AMS 传过来的 Activity

<section>

<pre>data.activityInfo.applicationInfo

</pre>

</section>

/   总结  /

所得到的属性我们称之为 LoadedApk，可以提取到 apk 中的所有资源，那么 APP 内部是如何页面跳转的呢，比如我们从 ActivityA 跳转到 ActivityB，我们可以将 Activity 看作 Launcher，唯一不同的就是，在正常情况下 ActivityB 和 ActivityA 所在同一进程，所以不会去创建新的进程。

APP 的启动流程就是这样了，点击阅读原文可以查看我的更多文章。

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5FqhyzIcib3mITekUQddhZwiaEOHUAqTO2SQIBsbV5zjQiczk1n8fnLrpSqBwLKMkXfXDax57eKmXiaw/640?wx_fmt=jpeg)

推荐阅读：

[](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650245904&idx=1&sn=7837f96ee4bfe1faf8d68b47346384e9&chksm=88637a7fbf14f369a0c73e073a7eb56b82822b4515dc57d03a4a0a1d6fdc0dda61053f3fd129&scene=21#wechat_redirect)[每位开发者都应该读的 9 本技术书，本本都是经典](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650246095&idx=1&sn=6e44498a7271619e73e0ce1626c65809&chksm=88637aa0bf14f3b622144c494767ab184a6fb4abe64e7c63bd44b5145cb1655f901429909257&scene=21#wechat_redirect)

[](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650245764&idx=1&sn=290222a279504514837eb39951e65132&chksm=88637bebbf14f2fdac8979203982d871fe7db8a83cc3469fe8a7bb17c254bf8056a2967a597a&scene=21#wechat_redirect)[使用 Flutter 仿写抖音的手势交互](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650245904&idx=1&sn=7837f96ee4bfe1faf8d68b47346384e9&chksm=88637a7fbf14f369a0c73e073a7eb56b82822b4515dc57d03a4a0a1d6fdc0dda61053f3fd129&scene=21#wechat_redirect)

[](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650245825&idx=1&sn=e99e8c4975cf842b7ccf74700bf8ba87&chksm=88637baebf14f2b8b1910d8a11c44a7b1cf52bfc1f8402f5f457bb7799898116f09ccad903a5&scene=21#wechat_redirect)[一篇文章带你看遍 Google I/O 2019 大会](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650245876&idx=1&sn=5eb193f4bea7c5b3b2db529b1ba10c35&chksm=88637b9bbf14f28d9f97368c511283cd761e29ed9820e5c5d256266a8a4ac7fab515bc07edda&scene=21#wechat_redirect)

欢迎关注我的公众号

学习技术或投稿

![](https://mmbiz.qpic.cn/mmbiz/wyice8kFQhf4Mm0CFWFnXy6KtFpy8UlvN0DOM3fqc64fjEj9tw23yYSqujQjSQoU1rC0vicL9Mf0X6EMR4gFluJw/640.png?)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt6FSn51QbdP1ic92cjsQM7LkBCfnaJMtcibMw9vYtdQ6QQM3CcFFbGqMoNucFlBRJw9E6VQWYk30ficw/640?wx_fmt=jpeg)

长按上图，识别图中二维码即可关注