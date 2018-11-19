# Android O行为变更--隐式广播限制

Android O做了一系列变更，其中在清单文件中注册（静态注册）的隐式广播（除例外情况）运行在Android O上已经不起任何作用，所以除了个别例外的隐式广播外，其他所有隐式广播都需要动态注册。至于为什么要做这一变更，还是为了节省电量，提升续航，增强性能，提高用户体验。

至于限制隐式广播为何能有这么大的效果，举个例子：现在的手机中少则几十个应用多则上百，如果每个应用都注册监听CONNECIVITY_ACTION广播，想象一下，每当WiFi，数据流量网络状态发生变化时系统就会发出广播，此时所有的应用都被唤醒并执行任务，即使这些应用不在前台甚至没有运行，所以可想这是多么的耗费资源，特别是电量。

## 限制

1. 针对 Android O 的应用无法继续在其清单中为隐式广播注册广播接收器。 隐式广播是一种不专门针对该应用的广播。 例如，`ACTION_PACKAGE_REPLACED` 就是一种隐式广播，因为它将发送到注册的所有侦听器，让后者知道设备上的某些软件包已被替换。不过，`ACTION_MY_PACKAGE_REPLACED` 不是隐式广播，因为不管已为该广播注册侦听器的其他应用有多少，它都会只发送到软件包已被替换的应用。
2. 应用可以继续在它们的清单中注册显式广播。
3. 应用可以在运行时使用 Context.registerReceiver() 为任意广播（不管是隐式还是显式）注册接收器。
4. 需要签名权限的广播不受此限制所限，因为这些广播只会发送到使用相同证书签名的应用，而不是发送到设备上的所有应用

## 隐式广播例外情况

有几种广播目前不会受这些限制。，无论应用目标平台的 API 级别为何，都可以继续注册下列广播的侦听器。 
```
1. ACTION_LOCKED_BOOT_COMPLETED、ACTION_BOOT_COMPLETED：原因：这些广播只在首次启动时发送一次，并且许多应用都需要接收此广播以便进行作业、闹铃等事项的安排。 
2. ACTION_USER_INITIALIZE、”android.intent.action.USER_ADDED”、”android.intent.action.USER_REMOVED”：原因：这些广播受特权保护，因此大多数正常应用无论如何都无法接收它们。 
3. “android.intent.action.TIME_SET”、ACTION_TIMEZONE_CHANGED：原因：时钟应用可能需要接收这些广播，以便在时间或时区变化时更新闹铃。 
4. ACTION_LOCALE_CHANGED：原因：只在语言区域发生变化时发送，并不频繁。 应用可能需要在语言区域发生变化时更新其数据。 
5.ACTION_USB_ACCESSORY_ATTACHED、ACTION_USB_ACCESSORY_DETACHED、ACTION_USB_DEVICE_ATTACHED、ACTION_USB_DEVICE_DETACHED：原因：如果应用需要了解这些 USB 相关事件的信息，目前尚未找到能够替代注册广播的可行方案。 
6. ACTION_HEADSET_PLUG：原因：由于此广播只在用户进行插头的物理连接或拔出时发送，因此不太可能会在应用响应此广播时影响用户体验。 
7. ACTION_CONNECTION_STATE_CHANGED、ACTION_CONNECTION_STATE_CHANGED：原因：与 ACTION_HEADSET_PLUG 类似，应用接收这些蓝牙事件的广播时不太可能会影响用户体验。 
8. ACTION_CARRIER_CONFIG_CHANGED, TelephonyIntents.ACTION_*_SUBSCRIPTION_CHANGED、”TelephonyIntents.SECRET_CODE_ACTION”：原因：原始设备制造商 (OEM) 电话应用可能需要接收这些广播。 
9. LOGIN_ACCOUNTS_CHANGED_ACTION：原因：一些应用需要了解登录帐号的变化，以便为新帐号和变化的帐号设置计划操作。 
10. ACTION_PACKAGE_DATA_CLEARED：原因：只在用户显式地从 Settings 清除其数据时发送，因此广播接收器不太可能严重影响用户体验。 
11. ACTION_PACKAGE_FULLY_REMOVED：原因：一些应用可能需要在另一软件包被移除时更新其存储的数据；对于这些应用，尚未找到能够替代注册此广播的可行方案。 
12. ACTION_NEW_OUTGOING_CALL：原因：执行操作来响应用户打电话行为的应用需要接收此广播。 
13. ACTION_DEVICE_OWNER_CHANGED：原因：此广播发送得不是很频繁；一些应用需要接收它，以便知晓设备的安装状态发生了变化。 
14. ACTION_EVENT_REMINDER：原因：由日历提供程序发送，用于向日历应用发布事件提醒。因为日历提供程序不清楚日历应用是什么，所以此广播必须是隐式广播。 
15. ACTION_MEDIA_MOUNTED、ACTION_MEDIA_CHECKING、ACTION_MEDIA_UNMOUNTED、ACTION_MEDIA_EJECT、 ACTION_MEDIA_UNMOUNTABLE、ACTION_MEDIA_REMOVED、ACTION_MEDIA_BAD_REMOVAL：原因：这些广播是作为用户与设备进行物理交互的结果（安装或移除存储卷）或启动初始化（作为已装载的可用卷）的一部分发送的，因此它们不是很常见，并且通常是在用户的掌控下。 
16. SMS_RECEIVED_ACTION、WAP_PUSH_RECEIVED_ACTION：原因：这些广播依赖于短信接收应用。
```

## 解决办法

用动态注册,注册方式：在代码中调用Context.registerReceiver（）方法
```java
// 选择在Activity生命周期方法中的onResume()中注册
@Override
  protected void onResume(){
      super.onResume();

    // 1. 实例化BroadcastReceiver子类 &  IntentFilter
     mBroadcastReceiver mBroadcastReceiver = new mBroadcastReceiver();
     IntentFilter intentFilter = new IntentFilter();

    // 2. 设置接收广播的类型
    intentFilter.addAction(android.net.conn.CONNECTIVITY_CHANGE);

    // 3. 动态注册：调用Context的registerReceiver（）方法
     registerReceiver(mBroadcastReceiver, intentFilter);
 }


// 注册广播后，要在相应位置记得销毁广播
// 即在onPause() 中unregisterReceiver(mBroadcastReceiver)
// 当此Activity实例化时，会动态将MyBroadcastReceiver注册到系统中
// 当此Activity销毁时，动态注册的MyBroadcastReceiver将不再接收到相应的广播。
 @Override
 protected void onPause() {
     super.onPause();
      //销毁在onResume()方法中的广播
     unregisterReceiver(mBroadcastReceiver);
     }
}
```
