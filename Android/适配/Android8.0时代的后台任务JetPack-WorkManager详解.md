
## Android8.0时代的后台任务JetPack-WorkManager详解

本篇来自 **微笑的江豚 **的投稿，对 Android 8.0 后的后台任务 JetPack-WorkManager 进行了详细解析。一起来看看！希望大家喜欢。

**<strong><strong> <strong style="orphans: 2;widows: 2;">微笑的江豚 **</strong></strong> </strong>的博客地址：

> https://my.oschina.net/JiangTun

<br  />

<br  />

以前我们在处理后台任务时，一般都是使用 Service(含 IntentService）或者线程/线程池，而 Service 不受页面生命周期影响，可以常驻后台，所以很适合做一些定时、延时任务，或者其他一些肉眼不可见的神秘勾当。 在处理一些复杂需求时，比如监听网络环境自动暂停重启后台上传下载这类变态任务，我们需要用 Service 结合 Broadcast 一起来做,非常的麻烦，再加上传输进度的回调，让人想疯！

当然大量的后台任务过度消耗了设备的电量，比如多种第三方推送的 service 都在后台常驻，不良 App 后台自动上传用户隐私也带来了隐私安全问题。

- 6.0 (API 级 23) 引入了 Doze 机制和应用程序待机。当屏幕关闭且设备静止时, 打盹模式会限制应用程序的行为。应用程序待机将未使用的应用程序置于限制其网络访问、作业和同步的特殊状态。

- Android 7.0 (API 级 24) 有限的隐性广播和 Doze-on-the-go.- Android 8.0 (API 级 26) 进一步限制了后台行为, 例如在后台获取位置并释放缓存的 wakelocks。

尤其在Android O（8.0）中，谷歌对于后台的限制几乎可以称之为变态：
- Android 8.0 有一项复杂功能；系统不允许后台应用创建后台服务。 因此，Android 8.0 引入了一种全新的方法，即 Context.startForegroundService()，以在前台启动新服务。 在系统创建服务后，应用有五秒的时间来调用该服务的 startForeground() 方法以显示新服务的用户可见通知。 如果应用在此时间限制内未调用 startForeground()，则系统将停止服务并声明此应用为 ANR。
而且加入了对静态广播的限制：

- Android 8.0 让这些限制更为严格。 针对 Android 8.0 的应用无法继续在其清单中为隐式广播注册广播接收器。 隐式广播是一种不专门针对该应用的广播。 例如，ACTION_PACKAGE_REPLACED 就是一种隐式广播，因为它将发送到注册的所有侦听器，让后者知道设备上的某些软件包已被替换。 不过，`ACTION_MY_PACKAGE_REPLACED` 不是隐式广播，因为不管已为该广播注册侦听器的其他应用有多少，它都会只发送到软件包已被替换的应用。 应用可以继续在它们的清单中注册显式广播。 应用可以在运行时使用 Context.registerReceiver() 为任意广播（不管是隐式还是显式）注册接收器。 需要签名权限的广播不受此限制所限，因为这些广播只会发送到使用相同证书签名的应用，而不是发送到设备上的所有应用。 在许多情况下，之前注册隐式广播的应用使用 JobScheduler 作业可以获得类似的功能。

于此同时，官方推荐用5.0推出的 JobScheduler 替换 Service + Broadcast 的方案。并且在 Android O，后台 Service 启动后的5秒内，如果不转为前台 Service 就会 ANR!

### 官方的推荐（qiangzhi）做法

![](http://olg7c0d2n.bkt.clouddn.com/18-8-15/47711682.jpg)

### WorkManager的推出

WorkManager 是一个 Android 库, 它在工作的触发器 (如适当的网络状态和电池条件) 满足时, 优雅地运行可推迟的后台工作。WorkManager 尽可能使用框架 JobScheduler , 以帮助优化电池寿命和批处理作业。在 Android 6.0 (API 级 23) 下面的设备上, 如果 WorkManager 已经包含了应用程序的依赖项, 则尝试使用 Firebase JobDispatcher 。否则, WorkManager 返回到自定义 AlarmManager 实现, 以优雅地处理您的后台工作。

也就是说，WorkManager 可以自动维护后台任务，同时可适应不同的条件，同时满足后台Service 和静态广播，内部维护着 JobScheduler，而在6.0以下系统版本则可自动切换为AlarmManager，好神奇！

### **引入**

```
implementation "android.arch.work:work-runtime:1.0.0-alpha06" // use -ktx for Kotlin/>
```

### **重要的类解析**
- **Worker**

Worker 是一个抽象类，用来指定需要执行的具体任务。我们需要继承 Worker 类，并实现它的 doWork 方法：

```kotlin
class MyWorker:Worker() {

    val tag = javaClass.simpleName

   override fun getExtras(): Extras {
       return Extras(...) //也可以把参数写死在这里
   }

   override fun onStopped(cancelled: Boolean) {
       super.onStopped(cancelled)
       //当任务结束时会回调这里
       ...
   }

    override fun doWork(): Result {

        Log.d(tag,"任务执行完毕！")
        return Worker.Result.SUCCESS
    }
}
```

##### **向任务添加参数**

在Request中传参

```kotlin
val data=Data.Builder()
        .putInt("A",1)
        .putString("B","2")
        .build()
val request2 = PeriodicWorkRequestBuilder<MyWorker>(24,TimeUnit.SECONDS)
        .setInputData(data)
        .build()
```

在 Worker 中使用

```kotlin
class MyWorker:Worker() {

    val tag = javaClass.simpleName

    override fun doWork(): Result {

        val A = inputData.getInt("A",0)
        val B = inputData.getString("B")
        return Worker.Result.SUCCESS
    }
}
```

当然除了上述代码中的方法之外，我们也可以重写父级的getExtras()，并在此方法中把参数写死再返回也是可以的。

这里WorkManager就有一个不是很人性的地方了，那就是WorkManager不支持序列化传值！这一点让我怎么说啊，intent和Bundle都支持序列化传值，为什么偏偏这货就不行？那么如果传一个复杂对象还要先拆解吗?

**任务的返回值**

很类似很类似的，任务的返回值也很简单：

```kotlin
override fun doWork(): Result {

    val A = inputData.getInt("A",0)
    val B = inputData.getString("B")

    val data = Data.Builder()
            .putBoolean("C",true)
            .putFloat("D",0f)
            .build()
    outputData = data//返回值
    return Worker.Result.SUCCESS
}
```

doWork 要求最后返回一个 Result，这个 Result 是一个枚举，它有几个固定的值：

- FAILURE 任务失败。- RETRY 遇到暂时性失败，此时可使用WorkRequest.Builder.setBackoffCriteria(BackoffPolicy, long, TimeUnit)来重试。- SUCCESS 任务成功。
看到这里我就很奇怪，官方不推荐我们使用枚举，但是自己却一直在用，什么意思？

- **WorkRequest**
也是一个抽象类，可以对 Work 进行包装，同时装裱上一系列的约束（Constraints），这些 Constraints 用来向系统指明什么条件下，或者什么时候开始执行任务。

WorkManager 向我们提供了 WorkRequest 的两个子类：
- OneTimeWorkRequest 单次任务。- PeriodicWorkRequest 周期任务。

```
val request1 = PeriodicWorkRequestBuilder<MyWorker>(60,TimeUnit.SECONDS).build()<br  /><br  />val request2 = OneTimeWorkRequestBuilder<MyWorker>().build()<br  />
```

从代码中可以看到，我们应该使用不同的构造器来创建对应的 WorkRequest。

接下来我们看看都有哪些约束：
- public boolean requiresBatteryNotLow ()：执行任务时电池电量不能偏低。
- public boolean requiresCharging ()：在设备充电时才能执行任务。
- public boolean requiresDeviceIdle ()：设备空闲时才能执行。
- public boolean requiresStorageNotLow ()：设备储存空间足够时才能执行。

##### **addContentUriTrigger**

```kotlin
@RequiresApi(24)
public @NonNull Builder addContentUriTrigger(Uri uri, boolean triggerForDescendants)
```

指定是否在(Uri 指定的)内容更新时执行本次任务（只能用于 Api24及以上版本）。瞄了一眼源码发现了一个 ContentUriTriggers，这什么东东？

```java
public final class ContentUriTriggers implements Iterable<ContentUriTriggers.Trigger> {

    private final Set<Trigger> mTriggers = new HashSet<>();
    ...

public static final class Trigger {
        private final @NonNull Uri mUri;
        private final boolean mTriggerForDescendants;

        Trigger(@NonNull Uri uri, boolean triggerForDescendants) {
            mUri = uri;
            mTriggerForDescendants = triggerForDescendants;
        }
```

特么惊呆了，居然是个HashSet，而HashSet的核心是个HashMap啊，谷歌声明不建议用HashMap，当然也就不建议用HashSet，可是官方自己在背地里面干的这些勾当啊...

##### **setRequiredNetworkType**

```java
public void setRequiredNetworkType (NetworkType requiredNetworkType)
```

指定任务执行时的网络状态。其中状态见下表：
![](http://olg7c0d2n.bkt.clouddn.com/18-8-15/24935054.jpg)

**setRequiresBatteryNotLow**<br  />

```java
public void setRequiresBatteryNotLow (boolean requiresBatteryNotLow)
```

指定设备电池电量低于阀值时是否启动任务，默认 false。

##### **setRequiresCharging**

```java
public void setRequiresCharging (boolean requiresCharging)
```

指定设备在充电时是否启动任务。

##### **setRequiresDeviceIdle**

```java
public void setRequiresDeviceIdle (boolean requiresDeviceIdle)
```

指明设备是否为空闲时是否启动任务。

##### **setRequiresStorageNotLow**

```java
public void setRequiresStorageNotLow (boolean requiresStorageNotLow)
```

指明设备储存空间低于阀值时是否启动任务。给任务加约束：

```kotlin
val myConstraints = Constraints.Builder()
        .setRequiresDeviceIdle(true)//指定{@link WorkRequest}运行时设备是否为空闲
        .setRequiresCharging(true)//指定要运行的{@link WorkRequest}是否应该插入设备
        .setRequiredNetworkType(NetworkType.NOT_ROAMING)
        .setRequiresBatteryNotLow(true)//指定设备电池是否不应低于临界阈值
        .setRequiresCharging(true)//网络状态
        .setRequiresDeviceIdle(true)//指定{@link WorkRequest}运行时设备是否为空闲
        .setRequiresStorageNotLow(true)//指定设备可用存储是否不应低于临界阈值
        .addContentUriTrigger(myUri,false)//指定内容{@link android.net.Uri}时是否应该运行{@link WorkRequest}更新
        .build()
val request = PeriodicWorkRequestBuilder<MyWorker>(24,TimeUnit.SECONDS)
        .setConstraints(myConstraints)//注意看这里！！！
        .build()
```

##### 给任务加标签分组

```kotlin
val request1 = OneTimeWorkRequestBuilder<MyWorker>()
                .addTag("A")//标签
                .build()
val request2 = OneTimeWorkRequestBuilder<MyWorker>()
                .addTag("A")//标签
                .build()
```

上述代码我给两个相同任务的request都加上了标签，使他们成为了一个组：A组。这样的好处是以后可以直接控制整个组就行了，组内的每个成员都会受到影响。

- **WorkManager**

经过上面的操作，相信我们已经能够成功创建 request 了，接下来我们就需要把任务放进任务队列，我们使用 WorkManager。

WorkManager 是个单例，它负责调度任务并且监听任务状态。

```java
WorkManager.getInstance().enqueue(request)
```

当我们的 request 入列后，WorkManager 会给它分配一个 work ID，之后我们可以使用这个work id 来取消或者停止任务：

```java
WorkManager.getInstance().cancelWorkById(request.id)
```

注意：WorkManager 并不一定能结束任务，因为任务有可能已经执行完毕了。

同时，WorkManager 还提供了其他结束任务的方法：
- cancelAllWork():取消所有任务。- cancelAllWorkByTag(tag:String):取消一组带有相同标签的任务。- cancelUniqueWork(uniqueWorkName:String):取消唯一任务。

- **WorkStatus**

当 WorkManager 把任务加入队列后，会为每个WorkRequest对象提供一个 LiveData（如果这个东东不了解的话赶紧去学）。 LiveData 持有 WorkStatus;通过观察该 LiveData, 我们可以确定任务的当前状态, 并在任务完成后获取所有返回的值。

```kotlin
val liveData: LiveData<WorkStatus> = WorkManager.getInstance().getStatusById(request.id)
```

我们来看这个 WorkStatus 到底都包涵什么，我们点进去看它的源码：

```java
public final class WorkStatus {    private @NonNull UUID mId;    private @NonNull State mState;    private @NonNull Data mOutputData;    private @NonNull Set<String> mTags;    public WorkStatus(
            @NonNull UUID id,
            @NonNull State state,
            @NonNull Data outputData,
            @NonNull List<String> tags) {
        mId = id;
        mState = state;
        mOutputData = outputData;
        mTags = new HashSet<>(tags);
    }
```

我们需要关注的只有 State 和 Data 这两个属性，首先看 State:

```java
public enum State {

    ENQUEUED,//已加入队列
    RUNNING,//运行中
    SUCCEEDED,//已成功
    FAILED,//已失败
    BLOCKED,//已刮起
    CANCELLED;//已取消

    public boolean isFinished() {        return (this == SUCCEEDED || this == FAILED || this == CANCELLED);
    }
}
```

这特么又一个枚举。看过代码之后，State 枚举其实就是用来给我们做最后的结果判断的。但是要注意其中有个已挂起 BLOCKED，这是啥子情况？通过看它的注释，我们得知，如果 WorkRequest 的约束没有通过，那么这个任务就会处于挂起状态。

接下来，Data 当然就是我们在任务中 doWork 的返回值了。看到这里，我感觉谷歌大佬的设计思维还是非常之强的，把状态和返回值同时输出，非常方便我们做判断的同时来取值，并且这样的设计就可以达到‘多次返回’的效果，有时间一定要去看一下源码，先立个 flag！

**任务链**

在很多场景中，我们需要把不同的任务弄成一个队列，比如在用户注册的时候，我们要先验证手机短信验证码，验证成功后再注册，注册成功后再调登陆接口实现自动登陆。类似这样相似的逻辑比比皆是，实话说笔者以前都是在 service 里面用 rxjava 来实现的。但是现在 service 在 Android8.0版本以上系统不能用了怎么办？当然还是用我们今天学到的 WorkManager 来实现，接下来我们就一起看一下 WorkManager 的任务链。

- **链式启动-并发**

```kotlin
val request1 = OneTimeWorkRequestBuilder<MyWorker1>().build()
val request2 = OneTimeWorkRequestBuilder<MyWorker2>().build()
val request3 = OneTimeWorkRequestBuilder<MyWorker3>().build()

WorkManager.getInstance().beginWith(request1,request2,request3)
.enqueue()
```

这样等同于 WorkManager 把一个个的 WorkRequest enqueue 进队列，但是这样写明显更整齐！同时队列中的任务是并行的。

- **then 操作符-串发**

```kotlin
val request1 = OneTimeWorkRequestBuilder<MyWorker>().build()
val request2 = OneTimeWorkRequestBuilder<MyWorker>().build()
val request3 = OneTimeWorkRequestBuilder<MyWorker>().build()

WorkManager.getInstance().beginWith(request1)
        .then(request2)
        .then(request3)
        .enqueue()
```

上述代码的意思就是先1，1成功后再2，2成功后再3，这期间如果有任何一个任务失败（返回 Worker.WorkerResult.FAILURE),则整个队列就会被中断。

在任务链的串行中，也就是两个任务使用了 then 操作符连接，那么上一个任务的返回值就会自动转为下一个任务的参数！

- **combine 操作符-组合**

现在我们有个复杂的需求：共有A、B、C、D、E这五个任务，要求 AB 串行，CD 串行，但两个串之间要并发，并且最后要把两个串的结果汇总到E。

我们看到这种复杂的业务逻辑，往往都会吓一跳，但是牛X的谷歌提供了combine操作符专门应对这种奇葩逻辑，不得不说：谷歌是我亲哥！

```kotlin
val chuan1 = WorkManager.getInstance()
    .beginWith(A)
    .then(B)
val chuan2 = WorkManager.getInstance()
    .beginWith(C)
    .then(D)
WorkContinuation
    .combine(chuan1, chuan2)
    .then(E)
    .enqueue()
```

### **唯一链**

什么是唯一链，就是同一时间内队列里不能存在相同名称的任务。

```kotlin
val request = OneTimeWorkRequestBuilder<MyWorker>().build()

WorkManager.getInstance().beginUniqueWork("tag"，ExistingWorkPolicy.REPLACE,request,request,request)
```

从上面代码我们可以看到，首先与之前不同的是，这次我们用的是 beginUniqueWork 方法，这个方法的最后一个参数是一个可变长度的数组，那就证明这一定是一根链条。然后我们看这个方法的第一个参数，要求输入一个名称，这个名称就是用来标识任务的唯一性。那如果两个不同的任务我们给了相同的名称也是可以的，但是这两个任务在队列中只能存活一个。最后我们再来看第二个参数 ExistingWorkPolicy,点进去果然又双叒是枚举：

```java
public enum ExistingWorkPolicy {

    REPLACE,
    KEEP,
    APPEND
}
```

- REPLACE：如果队列里面已经存在相同名称的任务，并且该任务处于挂起状态则替换之。
- KEEP：如果队列里面已经存在相同名称的任务，并且该任务处于挂起状态，则什么也不做。
- APPEND：如果队列里面已经存在相同名称的任务，并且该任务处于挂起状态，则会缓存新任务。当队列中所有任务执行完毕后，以这个新任务做为序列的第一个任务。


看到这里相信大家对于 WorkManager 的基本用法已经了解的差不多了吧！笔者对 WorkManager 的了解也还不够多，欢迎大家多多留言交流！

另外通过这次对 WorkManager 的学习，我们也看到官方在代码里面也仍旧在用一些他自己不推荐使用的东西，比如 HashMap、HashSet、Enum 等，只许州官放火不许百姓点灯？这很谷歌！其实不是的，所谓万事无绝对，只要你够自信，自己做好取舍，掌握平衡，用什么还是由你自己做主！
