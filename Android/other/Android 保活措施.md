> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5df24da36fb9a0165c711807

> 这个文章只是 Android 历史保活方案总结，没有什么特别的参考意义，Android 已经到 10 了，100% 保活本身就已经不复存在，文章中所有的方案，都是有可能有用，毕竟 4.4 还有人用，至于要不用可以自己参考，毕竟当 PM 就是让你应用不死，你能不写代码吗？

保活通常分为 2 种方案，一种为提高进程优先级，防止被杀，另一种为进程被杀死拉活

1. 进程优先级
--------

Android 系统会尽可能的保持应用进程，但是当需要建立新的进程或者运行更重要的进程，便会回收优先级低一些的进程, 这个就是 lowmemorykiller 的工作。而进程的优先级其实就是 /proc/pid/oom_adj

**进程的优先级排序**

1.  前台进程（Foreground Process）
2.  可见进程 (Visible Process)
3.  服务进程 (Service Process)
4.  后台进程 (Background Process)
5.  空进程 (Empty Process)

**前台进程**

1.  拥有 用户正在交互的 Activity(正处于 onResume 中)
2.  拥有 Service 绑定到正处于 onResume 的 Activity
3.  拥有 Service 调用 startForeground 成为前台服务
4.  拥有 Service 正在执行生命周期回调（onCreate、onStart、onDestroy）
5.  拥有 BroadcastReceiver 正在执行 onReceive

**可见进程**

1.  拥有 Activity 处于 onPause ，此时可见但是不可操作
2.  拥有 Service 绑定到正处于 onPause 的 Activity

**服务进程**

1.  仅通过 startService 启动的 Service

**后台进程**

1.  拥有 Activity 处于 onStop

**空进程**

1.  不拥有任何活动的组件进程

2. 回收策略
-------

从 Zygote fork 出来的进程都会被储存在 ActivityManagerService.mLruProcesses 列表中，由 ActivityManagerService 进行统一管理。ActivityManagerService 会根据进程状态去更新进程所对应的 oom_adj 的值，当内存达到一定的阈值会触发清理 oom_adj 高的进程。

[参考博客](https://user-gold-cdn.xitu.io/2019/12/14/16f0387f426b5d34)

![](https://user-gold-cdn.xitu.io/2019/12/14/16f03873886484ad?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

3. 保活方案
-------

### 3.1 提高进程优先级

#### 3.1.1 利用 Activity

`1像素Activy`，监控手机解锁屏事件，解锁时将 Activity 销毁，锁屏时启动，并且要无感知，在 RecentTask 里移除

#### 3.1.2 前台服务 + Notification

Service 通过 `startForegroundService` 启动 ，低版本时可以通过特殊方式对 Notification 进行隐藏，高版本无法规避，此方案为通过需求正向解决

#### 3.1.3 引导用户打开电池管理，允许应用后台运行

目前市面上的手机，或多或少都有对进程管理有优化，可能会有允许应用后台允许的功能，但是每款手机的入口均不相同，而且相同厂商的不同版本也会不同

具体做法，找到手机的电池管理或者系统的后台管理，针对不同的手机做文字书面的提醒，提醒用户开启此功能，暴力一点可以想办法拿到此 Activity 的具体类名 包名等信息，进行反射调用。

此方案一般应用不要使用，工作量巨大，而且仅仅针对提醒类应用使用，比如吃药提醒，起床闹钟，这些对保活要求非常高的应用才适合

### 3.2 进程死后拉活

#### 3.2.1 监听系统静态广播

低版本时，静态广播可以唤醒应用进程，所以监听系统广播，例如开机，锁屏，解锁等可以做到，但是高版本不能通过静态广播监听系统广播了

#### 3.2.2 监听三方静态广播

与上个方案类似，都是运用静态广播可以拉活应用为基础，只是发送方不是系统，而且三方应用。所以此方案可行，但是很不稳定，海外和国内用户群体不同，手机使用的 APK 也会不同，而且需要大量反编译三方应用，投成本也很高

#### 3.2.3 利用系统 Service 机制拉活

Service 的 onStartCommand 返回值，当返回值为 `START_STICKY` 和 `START_REDELIVER_INTENT` 时，服务会自动重启，但是 Service 在短时间内被杀死 5 次，则不再拉起

#### 3.2.4 利用 JobScheduler

JobScheduler 为 Android 5.0 之后引入的，本质是系统定时任务，如果进程被杀，任务仍然会被执行，在 7.0 后 JobScheduler 添加了限制，最低间隔为 15 分钟。但是还是有概率出现存在进程死亡后，不触发的情况。

#### 3.2.5 利用 AlarmManager

本质上也是通过设置定时任务，如果进程被杀，任务也仍然会被执行，此时就可以拉活进程。Doze 模式会影响 AlarmManager 不被触发，此时要用`setAlarmClock`来设置。同样有概率出现存在进程死亡后，不触发的情况。

而且 Android 9.0 的谷歌原生手机，多了一个功能，就是`显示手机下一个的闹钟时间是几点`，如果用到了这种保活方式，用户也注意到了这个功能，那么闹钟上的时间会暴露有应用在明目张胆的保活

#### 3.2.6 利用账号同步机制

Android 系统的账号同步机制会定期同步账号进行，该方案目的在于利用同步机制进行进程的拉活。添加账号和设置同步周期的代码即可，谷歌商店会查这种保活方案，后果不知，建议慎用

[代码参考链接](https://blog.csdn.net/lyz_zyx/article/details/73571927)

#### 3.2.7 利用 Native 进程拉活

利用 Linux 中的 fork 机制创建 Native 进程，在 Native 进程中监控主进程的存活，当主进程挂掉后，在 Native 进程中立即对主进程进行拉活。

感知主进程死亡：在主进程中创建一个监控文件，并且在主进程中持有文件锁。在拉活进程启动后申请文件锁将会被堵塞，一旦可以成功获取到锁，说明主进程挂掉，即可进行拉活。

拉活主进程：通过 Native 进程拉活主进程的部分代码如下，即通过 am 命令进行拉活。通过指定 “–include-stopped-packages” 参数来拉活主进程处于 forestop 状态的情况。

但是 Android5.0 以上手机 会依次杀死所有进程，也会将 Native 进程杀死

#### 3.2.8 利用双进程拉活

启动两个 Service A 和 B，处于不同进程，然后在 A 的 onStartCommand 中绑定 B，B 也在 A 的 onStartCommand 中绑定 A，通过 ServiceConnection 的回调 onServiceDisconnected ，当绑定断开时，说明另一个进程死亡，于是重新启动死亡的进程（Service），6.0 之后保活效果也开始有限，与 Natvie 进程遇到的问题相似，只有在依次杀死进程的间隔中，有几率拉活

### 3.3 其他拉活方式

#### 3.3.1 利用系统官方的服务，或者三方服务

1.  国外可以使用 Firebase 的云端推送
2.  国内可以使用极光推送等服务

主要还是依靠，自己应用与其他应用使用相同 SDK，然后相同的 SDK 里面内置了相互唤醒功能，具体保活的效果也是依赖三方 SDK 的能力