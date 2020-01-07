> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5dfaeccbf265da33910a441d?utm_source=gold_browser_extension

保活现状
====

我们知道，Android 系统会存在杀后台进程的情况，并且随着系统版本的更新，杀进程的力度还有越来越大的趋势。系统这种做法本身出发点是好的，因为可以节省内存，降低功耗，也避免了一些流氓行为。

但有一部分应用，应用本身的使用场景就需要在后台运行，用户也是愿意让它在后台运行的，比如跑步类应用。一方面流氓软件用各种流氓手段进行保活，另一方面系统加大杀后台的力度，导致我们一些真正需要在后台运行的应用被误杀，苦不堪言。

优雅保活？
=====

为了做到保活，出现了不少「黑科技」，比如 1 个像素的 Activity，播放无声音频，双进程互相守护等。这些做法可以说是很流氓了，甚至破坏了 Android 的生态，好在随着 Android 系统版本的更新，这些非常规的保活手段很多都已失效了。

对于那些确实需要在后台运行的应用，我们如何做到优雅的保活呢？

后台运行白名单
=======

从 Android 6.0 开始，系统为了省电增加了休眠模式，系统待机一段时间后，会杀死后台正在运行的进程。但系统会有一个后台运行白名单，白名单里的应用将不会受到影响，在原生系统下，通过「设置」 - 「电池」 - 「电池优化」 - 「未优化应用」，可以看到这个白名单，通常会看到下面这两位：

![](https://user-gold-cdn.xitu.io/2019/12/19/16f1c2cf6cb87170?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

下次被产品说「 XXX 都可以保活，为什么我们不行！」的时候，你就知道怎么怼回去了。大厂通过和手机厂商的合作，将自己的应用默认加入到白名单中。如果你在一个能谈成这种合作的大厂，也就不用往下看了。

好在系统还没有抛弃我们，允许我们申请把应用加入白名单。

首先，在 AndroidManifest.xml 文件中配置一下权限：

```
<uses-permission android: />
复制代码

```

可以通过以下方法，判断我们的应用是否在白名单中：

```
@RequiresApi(api = Build.VERSION_CODES.M)
private boolean isIgnoringBatteryOptimizations() {
    boolean isIgnoring = false;
    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    if (powerManager != null) {
        isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
    }
    return isIgnoring;
}
复制代码

```

如果不在白名单中，可以通过以下代码申请加入白名单：

```
@RequiresApi(api = Build.VERSION_CODES.M)
public void requestIgnoreBatteryOptimizations() {
    try {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
复制代码

```

申请时，应用上会出现这样一个窗口：

![](https://user-gold-cdn.xitu.io/2019/12/19/16f1c2cf6d02d1b9?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

可以看到，这个系统弹窗会有影响电池续航的提醒，所以如果想让用户点允许，必须要有相关的说明。如果要判断用户是否点击了允许，可以在申请的时候调用 startActivityForResult，在 onActivityResult 里再判断一次是否在白名单中。

厂商后台管理
======

Android 开发的一个难点在于，各大手机厂商对原生系统进行了不同的定制，导致我们需要进行不同的适配，后台管理就是一个很好的体现。几乎各个厂商都有自己的后台管理，就算应用加入了后台运行白名单，仍然可能会被厂商自己的后台管理干掉。

如果能把应用加入厂商系统的后台管理白名单，可以进一步降低进程被杀的概率。不同的厂商在不同的地方进行设置，一般是在各自的「手机管家」，但更难的是，就算同一个厂商的系统，不同的版本也可能是在不同地方设置。

最理想的做法是，我们根据不同手机，甚至是不同的系统版本，给用户呈现一个图文操作步骤，并且提供一个按钮，直接跳转到指定页面进行设置。但需要对每个厂商每个版本进行适配，工作量是比较大的。我使用真机测试了大部分主流 Android 厂商的手机后，整理出了部分手机的相关资料。

首先我们可以定义这样两个方法：

```
/**
 * 跳转到指定应用的首页
 */
private void showActivity(@NonNull String packageName) {
    Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
    startActivity(intent);
}

/**
 * 跳转到指定应用的指定页面
 */
private void showActivity(@NonNull String packageName, @NonNull String activityDir) {
    Intent intent = new Intent();
    intent.setComponent(new ComponentName(packageName, activityDir));
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
}
复制代码

```

以下是部分手机的厂商判断，跳转方法及对应设置步骤，跳转方法不保证在所有版本上都能成功跳转，都需要加 try catch。

华为
--

厂商判断：

```
public boolean isHuawei() {
    if (Build.BRAND == null) {
        return false;
    } else {
        return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
    }
}
复制代码

```

跳转华为手机管家的启动管理页：

```
private void goHuaweiSetting() {
    try {
        showActivity("com.huawei.systemmanager",
            "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
    } catch (Exception e) {
        showActivity("com.huawei.systemmanager",
            "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
    }
}
复制代码

```

操作步骤：应用启动管理 -> 关闭应用开关 -> 打开允许自启动

小米
--

厂商判断：

```
public static boolean isXiaomi() {
    return Build.BRAND != null && Build.BRAND.toLowerCase().equals("xiaomi");
}
复制代码

```

跳转小米安全中心的自启动管理页面：

```
private void goXiaomiSetting() {
    showActivity("com.miui.securitycenter",
        "com.miui.permcenter.autostart.AutoStartManagementActivity");
}
复制代码

```

操作步骤：授权管理 -> 自启动管理 -> 允许应用自启动

OPPO
----

厂商判断：

```
public static boolean isOPPO() {
    return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oppo");
}
复制代码

```

跳转 OPPO 手机管家：

```
private void goOPPOSetting() {
    try {
        showActivity("com.coloros.phonemanager");
    } catch (Exception e1) {
        try {
            showActivity("com.oppo.safe");
        } catch (Exception e2) {
            try {
                showActivity("com.coloros.oppoguardelf");
            } catch (Exception e3) {
                showActivity("com.coloros.safecenter");
            }
        }
    }
}
复制代码

```

操作步骤：权限隐私 -> 自启动管理 -> 允许应用自启动

VIVO
----

厂商判断：

```
public static boolean isVIVO() {
    return Build.BRAND != null && Build.BRAND.toLowerCase().equals("vivo");
}
复制代码

```

跳转 VIVO 手机管家：

```
private void goVIVOSetting() {
    showActivity("com.iqoo.secure");
}
复制代码

```

操作步骤：权限管理 -> 自启动 -> 允许应用自启动

魅族
--

厂商判断：

```
public static boolean isMeizu() {
    return Build.BRAND != null && Build.BRAND.toLowerCase().equals("meizu");
}
复制代码

```

跳转魅族手机管家：

```
private void goMeizuSetting() {
    showActivity("com.meizu.safe");
}
复制代码

```

操作步骤：权限管理 -> 后台管理 -> 点击应用 -> 允许后台运行

三星
--

厂商判断：

```
public static boolean isSamsung() {
    return Build.BRAND != null && Build.BRAND.toLowerCase().equals("samsung");
}
复制代码

```

跳转三星智能管理器：

```
private void goSamsungSetting() {
    try {
        showActivity("com.samsung.android.sm_cn");
    } catch (Exception e) {
        showActivity("com.samsung.android.sm");
    }
}
复制代码

```

操作步骤：自动运行应用程序 -> 打开应用开关 -> 电池管理 -> 未监视的应用程序 -> 添加应用

乐视
--

厂商判断：

```
public static boolean isLeTV() {
    return Build.BRAND != null && Build.BRAND.toLowerCase().equals("letv");
}
复制代码

```

跳转乐视手机管家：

```
private void goLetvSetting() {
    showActivity("com.letv.android.letvsafe", 
        "com.letv.android.letvsafe.AutobootManageActivity");
}
复制代码

```

操作步骤：自启动管理 -> 允许应用自启动

锤子
--

厂商判断：

```
    public static boolean isSmartisan() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("smartisan");
    }
复制代码

```

跳转手机管理：

```
private void goSmartisanSetting() {
    showActivity("com.smartisanos.security");
}
复制代码

```

操作步骤：权限管理 -> 自启动权限管理 -> 点击应用 -> 允许被系统启动

友商致敬？
=====

在之前做的跑步应用中，我在设置里增加了一个权限设置页面，将上面提到的设置放在这里面。最近发现友商某咚也跟进了，图 1 是我们做的，图 2 是某咚做的：

![](https://user-gold-cdn.xitu.io/2019/12/19/16f1c455ba6132e0?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

某咚从设计、从我写的不够好的文案，甚至是我从十几台手机上一张一张截下来的图，进行了全方位的致敬。感谢某咚的认可，但最近在某个发布会上听到这么一句话：在致敬的同时，能不能说一句谢谢？

某咚的致敬，一方面说明了目前确实存在进程容易被杀，保活难度大的问题，另一方面也说明了这种引导用户进行白名单设置的手段是有效的。