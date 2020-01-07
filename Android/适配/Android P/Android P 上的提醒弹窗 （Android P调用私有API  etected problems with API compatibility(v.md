> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/f87fe39caf1d?tdsourcetag=s_pctim_aiomsg

##### 在 MIUI 10 升级到 Android P 后 每次进入程序都会弹一个提醒弹窗

![](https://upload-images.jianshu.io/upload_images/5757771-be0e9cd9456e256f.png)

调研了一下，是 Android P 后谷歌限制了开发者调用非官方公开 API 方法或接口，也就是说，你用反射直接调用源码就会有这样的提示弹窗出现，非 SDK 接口指的是 Android 系统内部使用、并未提供在 SDK 中的接口，开发者可能通过 Java 反射、JNI 等技术来调用这些接口。但是，这么做是很危险的：非 SDK 接口没有任何公开文档，必须查看源代码才能理解其行为逻辑。
但是源码是 JAVA 写的，万物皆可反射，所以还是可以用反射干掉这个 每次启动都会弹出的提醒窗口

```
private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

```

将这个方法在 app 初始化时候调用一次，这个弹窗就不会出现了