> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/51aaa65d5d25

## Android 四大组件 --- Activity

### Activity 生命周期

> 生命周期：`onCreate()` -> `onStart()` - > `onResume()` -> `onPause()` -> `onStop()` -> `onDestroy()`

![](https://upload-images.jianshu.io/upload_images/682504-1405607172778d9b.gif)

*   启动`activity`：系统先调用`onCreate()`，然后调用`onStart()`，最后调用`onResume()`方法，`activity`进入运行状态。

*   `activity`被**其他 activity 覆盖其上（DialogActivity）**或者**锁屏**：系统会调用`onPause()`方法，暂停当前`activity`的执行。

*   当前`activity`由被`覆盖`状态回到前台或者`解锁屏`：系统会调用`onResume()`方法，再次进入运行状态。

*   当前`Activity`转到新的`Activity`界面或按`Home`键回到主屏，自身退居后台：系统会先调用`onPause`方法，然后调用`onStop`方法，进入停滞状态。

*   用户后退回到此`Activity`：系统会先调用`onRestart`方法，然后调用`onStart`方法，最后调用`onResume`方法，再次进入运行状态。

*   当前`Activity`处于被覆盖状态或者后台不可见状态，即第 2 步和第 4 步，系统内存不足，杀死当前`Activity`，而后用户退回当前`Activity`：再次调用`onCreate`方法、`onStart`方法、`onResume`方法，进入运行状态。

*   用户退出当前`Activity`：系统先调用`onPause`方法，然后调用`onStop`方法，最后调用`onDestory`方法，结束当前`Activity`。

*   `onRestart()`：表示`activity`正在重新启动 ，一般情况下，当前`activity`从`不可见`重新`变成可见`状态时，`onRestart()`就会被调用，这种情形一般是用户行为所导致的，比如用户按`HOME`键切换到桌面然后重新打开`APP`或者按`back`键。

*   `onStart()`：`activity`可见了，但是还没有出现在前台，还**无法和用户交互**。

*   `onPause()`：表示`activity`正在停止，此时可以做一些存储数据，停止动画等工作，注意不能太耗时，因为这会影响到新`activity`的显示，`onPause`必须先执行完，新的`activity`的`onResume`才会执行。

*   从`activity`是否可见来说，`onstart()`和`onStop()`是配对的，从`activity`是否在前台来说，`onResume()`和`onPause()`是配对的。

*   **旧`activity`先`onPause`，然后新`activity`在启动**

**注意**：当`activity`中弹出`dialog`对话框的时候，`activity不会回调onPause`。
然而当`activity`启动`dialog风格的activity`的时候，此`activity会回调onPause函数`。

**异常情况下的生命周期**：比如当系统资源配置发生改变以及系统内存不足时，`activity`就可能被杀死。

*   **情况 1**：资源相关的系统配置发生改变导致`activity`被杀死并重新创建
    比如说当前`activity`处于竖屏状态，如果突然旋转屏幕，由于系统配置发生了改变，在默认情况下，`activity`就会被销毁并且重新创建，当然我们也可以组织系统重新创建我们的`activity`。

![](https://upload-images.jianshu.io/upload_images/682504-c7df1f922a04d7c2.png) 系统配置发生改变以后，`activity`会销毁，其`onPause`，`onStop`，`onDestory`均会被调用，由于`activity`是在异常情况下终止的，系统会调用 **onSaveInstance** 来保存当前`activity`状态，这个方法的调用时机是在 **onStop 之前**。与`onPause`没有既定的时序关系，当`activity`重新创建后，系统会调用`onRestoreInstanceState`，并且把`activity`销毁时`onSaveInstanceState`方法保存的`Bundle`对象作为参数同时传递给 **onRestoreInstanceState 和 onCreate** 方法。**onRestoreInstanceState()onStart() 方法后回调。**

同时，在`onSaveInstanceState`和`onRestoreInstanceState`方法中，系统自动为我们做了一些恢复工作，如：文本框（`EditeText`）中用户输入的数据，`ListView`滚动的位置等，这些 **view 相关的状态系统都能够默认为我们恢复**。可以查看`view`源码，和`activity`一样，`每个view都有onSaveInstanceState方法和onRestoreInstanceState方法`。

生命周期日志打印：

```
04-11 09:44:57.350 11757-11757/cn.hotwoo.play:remote I/MainActivity: onCreate
04-11 09:44:57.354 11757-11757/cn.hotwoo.play:remote I/MainActivity: onStart
04-11 09:44:57.356 11757-11757/cn.hotwoo.play:remote I/MainActivity: onResume
04-11 09:44:57.425 11757-11757/cn.hotwoo.play:remote I/MainActivity: onCreateOptionsMenu
04-11 09:44:59.149 11757-11757/cn.hotwoo.play:remote I/MainActivity: onPause
04-11 09:44:59.151 11757-11757/cn.hotwoo.play:remote I/MainActivity: onSaveInstanceState
04-11 09:44:59.151 11757-11757/cn.hotwoo.play:remote I/MainActivity: onStop
04-11 09:44:59.151 11757-11757/cn.hotwoo.play:remote I/MainActivity: onDestroy
04-11 09:44:59.234 11757-11757/cn.hotwoo.play:remote I/MainActivity: onCreate
04-11 09:44:59.235 11757-11757/cn.hotwoo.play:remote I/MainActivity: onStart
04-11 09:44:59.236 11757-11757/cn.hotwoo.play:remote I/MainActivity: onRestoreInstanceState
04-11 09:44:59.237 11757-11757/cn.hotwoo.play:remote I/MainActivity: onResume
04-11 09:44:59.270 11757-11757/cn.hotwoo.play:remote I/MainActivity: onCreateOptionsMenu
04-11 10:02:32.320 11757-11757/cn.hotwoo.play:remote I/MainActivity: onPause
04-11 10:02:32.516 11757-11757/cn.hotwoo.play:remote I/MainActivity: onStop
04-11 10:02:32.516 11757-11757/cn.hotwoo.play:remote I/MainActivity: onDestroy

```

*   **情况 2**：资源内存不足导致低优先级的`activity`被杀死
    这里的情况和前面的情况 1 数据存储和恢复是完全一致的，`activity`按照优先级从高到低可以分为如下三种：
    （1）前台`activity`--- 正在和用户交互的`activity`，优先级最高
    （2）可见但非前台`activity`--- 比如`activity`中弹出了一个对话框，导致`activity`可见但是位于后台无法和用户直接交互。
    （3）后台`activity`--- 已经被暂停的`activity`，比如执行了`onStop`，优先级最低。

**防止重新创建 activity**：`activity`指定`configChange`属性来不让系统重新创建`activity`。
`android : configChanges = "orientation"`

### Activity 与 Fragment 生命周期关系

创建过程：

![](https://upload-images.jianshu.io/upload_images/682504-c954db2f48b7747a.png)

销毁过程：

![](https://upload-images.jianshu.io/upload_images/682504-b8a5a4c104f03dd5.png)

### Activity 与 menu 创建先后顺序

在`activity`创建完回调`onResume`后创建`menu`，回调`onCreateOptionsMenu`

> 04-05 00:35:03.452 2292-2292/cn.hotwoo.play:remote I/MainActivity: onCreate
> 04-05 00:35:03.453 2292-2292/cn.hotwoo.play:remote I/MainActivity: onStart
> 04-05 00:35:03.454 2292-2292/cn.hotwoo.play:remote I/MainActivity: onResume
> 04-05 00:35:03.482 2292-2292/cn.hotwoo.play:remote I/MainActivity: onCreateOptionsMenu

### Activity 的启动模式

> 有四种启动模式：`standard`，`singleTop`，`singleTask`，`singleInstance`

*   standard 模式：在这种模式下，activity 默认会进入**启动它的 activity 所属的任务栈中**。 **注意**：在非 activity 类型的 context（如 ApplicationContext）并没有所谓的任务栈，所以不能通过 ApplicationContext 去启动 standard 模式的 activity。
*   singleTop 模式：栈顶复用模式。如果新 activity 位于任务栈的栈顶的时候，activity 不会被重新创建，同时它的 **onNewIntent** 方法会被回调。 **注意**：这个 activity 的 onCreate，onStart，onResume 不会被回调，因为他们并没有发生改变。
*   singleTask 模式：栈内复用模式。只要 activity 在一个栈中存在，那么多次启动此 activity 不会被重新创建单例，系统会回调 **onNewIntent**。比如 activityA，系统首先会寻找是否存在 A 想要的任务栈，如果没有则创建一个新的任务栈，然后把 activityA 压入栈，如果存在任务栈，然后再看看有没有 activityA 的实例，如果实例存在，那么就会把 A 调到栈顶并调用它的 **onNewIntent** 方法，如果不存在则把它压入栈。
*   singleInstance 模式：单实例模式。这种模式的 **activity 只能单独地位于一个任务栈**中。由于站内复用特性，后续的请求均不会创建新的 activity 实例。

**注意**：默认情况下，所有 activity 所需的任务栈的名字为应用的包名，可以通过给 activity 指定 TaskAffinity 属性来指定任务栈，** 这个属性值不能和包名相同，否则就没有意义 ** 。

## Android 四大组件 --- Service

### 本地服务（LocalService）

> 调用者和 service 在同一个进程里，所以运行在主进程的 main 线程中。所以不能进行耗时操作，可以采用在 service 里面**创建一个 Thread** 来执行任务。service 影响的是**进程**的生命周期，讨论与 Thread 的区别没有意义。
> **任何 Activity 都可以控制同一 Service，而系统也只会创建一个对应 Service 的实例**。

**两种启动方式**

#### 第一种启动方式：

通过 start 方式开启服务.
使用 service 的步骤：

> 1，定义一个类继承 service
> 2，manifest.xml 文件中配置 service
> 3，使用 context 的 startService(Intent) 方法启动 service
> 4，不在使用时，调用 stopService(Intent) 方法停止服务

使用 start 方式启动的生命周期：

> onCreate() --> onStartCommand() -- > onDestory()
> **注意**：如果服务已经开启，不会重复回调 onCreate() 方法，如果再次调用 context.startService() 方法，service 而是会调用 onStart() 或者 onStartCommand() 方法。停止服务需要调用 context.stopService() 方法，服务停止的时候回调 onDestory 被销毁。

**特点**：
一旦服务开启就跟调用者（开启者）没有任何关系了。开启者退出了，开启者挂了，服务还在后台长期的运行，开启者不能调用服务里面的方法。

#### 第二种启动方式

采用 bind 的方式开启服务
使用 service 的步骤：

> 1，定义一个类继承 Service
> 2，在 manifest.xml 文件中注册 service
> 3，使用 context 的 bindService(Intent,ServiceConnection,int) 方法启动 service
> 4，不再使用时，调用 unbindService(ServiceConnection) 方法停止该服务

使用这种 bind 方式启动的 service 的生命周期如下：

> onCreate() --> onBind() --> onUnbind() -- > onDestory()

**注意**：绑定服务不会调用 onStart() 或者 onStartCommand() 方法

**特点**：bind 的方式开启服务，绑定服务，调用者挂了，服务也会跟着挂掉。绑定者可以调用服务里面的方法。

**示例**：
定义一个类继承 service

```
//本地service不涉及进程间通信
public class MyService extends Service {

    private String TAG = "MyService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG,"onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    //绑定服务时调用这个方法，返回一个IBinder对象
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind");
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

//    停止服务，通过调用Context.unbindService()，别忘了service也继承了Context类
//    @Override
//    public void unbindService(ServiceConnection conn) {
//        super.unbindService(conn);
//        Log.i(TAG,"unbindService");
//    }

    //服务挂了
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    public interface MyIBinder{
        void invokeMethodInMyService();
    }

    public class MyBinder extends Binder implements MyIBinder{

        public void stopService(ServiceConnection serviceConnection){
            unbindService(serviceConnection);
        }

        @Override
        public void invokeMethodInMyService() {
            for(int i =0; i < 20; i ++){
                System.out.println("service is opening");
            }
        }
    }

```

在 manifest.xml 文件中注册 service

```
        //Service 必须要注册
        <service android:
            android:exported="true">
            <intent-filter>
                <action android:/>
                <category android: />
            </intent-filter>
        </service>

```

绑定自定义的 service

```
public class CustomActivity extends AppCompatActivity {

    private Button startService, unbindService;
    private MyService.MyBinder myBinder;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        startService = (Button) findViewById(R.id.service_start);
        unbindService = (Button) findViewById(R.id.unbind_service);

        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startService(new Intent(CustomActivity.this, MyService.class));
                serviceConnection = new MyServiceConnection();
                bindService(new Intent(CustomActivity.this, MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });
        unbindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(serviceConnection);
            }
        });

    }

    class MyServiceConnection implements ServiceConnection {

        //这里的第二个参数IBinder就是Service中的onBind方法返回的
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("MyService", "onServiceConnected");
            myBinder = (MyService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("MyService", "onServiceDisconnected");
        }
    }
}

```

startService 输出日志：

> 04-01 19:56:09.846 22845-22845/cn.hotwoo.play I/MyService: onCreate
> 04-01 19:56:09.854 22845-22845/cn.hotwoo.play I/MyService: onStartCommand
> 04-01 19:56:09.854 22845-22845/cn.hotwoo.play I/MyService: onStart

bindService 输出日志：

> 04-01 19:53:21.459 14704-14704/cn.hotwoo.play I/MyService: onCreate
> 04-01 19:53:21.460 14704-14704/cn.hotwoo.play I/MyService: onBind
> 04-01 19:53:21.461 14704-14704/cn.hotwoo.play I/MyService: onServiceConnected
> 点击 back 键关闭 activity 或者调用 Context.unbindService() 方法后：
> 04-05 01:16:27.508 11427-11427/cn.hotwoo.play I/MyService: onUnbind
> 04-05 01:16:27.508 11427-11427/cn.hotwoo.play I/MyService: onDestroy

### 远程服务

> 调用者和 service 不在同一个进程中，service 在单独的进程中的 main 线程，是一种垮进程通信方式。[学习地址](https://www.jianshu.com/p/43f36e5ba122)

绑定远程服务的步骤：

> *   在服务的内部创建一个内部类，提供一个方法，可以间接调用服务的方法
> *   把暴露的接口文件的扩展名改为. aidl 文件 去掉访问修饰符
> *   实现服务的 onbind 方法，继承 Bander 和实现 aidl 定义的接口，提供给外界可调用的方法
> *   在 activity 中绑定服务。bindService()
> *   在服务成功绑定的时候会回调 onServiceConnected 方法 传递一个 IBinder 对象
> *   aidl 定义的接口. Stub.asInterface(binder) 调用接口里面的方法

### IntentService

> IntentService 是 Service 的子类，比普通的 Service 增加了额外的功能。先看 **Service 本身存在两个问题**：

*   Service 不会专门启动一条单独的进程，Service 与它所在应用位于同一个进程中；
*   Service 也不是专门一条新线程，因此不应该在 Service 中直接处理耗时的任务；

IntentService 特征:

*   会创建独立的 worker 线程来处理所有的 Intent 请求；
*   会创建独立的 worker 线程来处理 onHandleIntent() 方法实现的代码，无需处理多线程问题；
*   所有请求处理完成后，IntentService 会自动停止，无需调用 stopSelf() 方法停止 Service；
*   为 Service 的 onBind() 提供默认实现，返回 null；
*   为 Service 的 onStartCommand 提供默认实现，将请求 Intent 添加到队列中；

## Android 四大组件 --- BroadcastReceiver

> 广播被分为两种不同的类型：“**普通广播**（Normal broadcasts）” 和 “**有序广播**（Ordered broadcasts）”。普通广播是完全异步的，可以在同一时刻（逻辑上）被所有接收者接收到，消息传递的效率比较高，但缺点是：接收者不能将处理结果传递给下一个接收者，并且无法终止广播 Intent 的传播；然而有序广播是按照接收者声明的优先级别（声明在 intent-filter 元素的 android:priority 属性中，数越大优先级别越高, 取值范围:-1000 到 1000。也可以调用 IntentFilter 对象的 setPriority() 进行设置），被接收者依次接收广播。如：A 的级别高于 B,B 的级别高于 C, 那么，广播先传给 A，再传给 B，最后传给 C。A 得到广播后，可以往广播里存入数据，当广播传给 B 时, B 可以从广播中得到 A 存入的数据。

发送广播

```
Context.sendBroadcast()
发送的是普通广播，所有订阅者都有机会获得并进行处理。

```

```
Context.sendOrderedBroadcast()
发送的是有序广播，系统会根据接收者声明的优先级别按顺序逐个执行接收者，前面的接收者有权终止广播(BroadcastReceiver.abortBroadcast())，如果广播被前面的接收者终止，后面的接收者就再也无法获取到广播。对于有序广播，前面的接收者可以将处理结果通过setResultExtras(Bundle)方法存放进结果对象，然后传给下一个接收者，通过代码：Bundle bundle =getResultExtras(true))可以获取上一个接收者存入在结果对象中的数据。
系统收到短信，发出的广播属于有序广播。如果想阻止用户收到短信，可以通过设置优先级，让你们自定义的接收者先获取到广播，然后终止广播，这样用户就接收不到短信了。

```

**生命周期**：如果一个广播处理完 onReceive 那么系统将认定此对象将不再是一个活动的对象，也就会 finished 掉它。
至此，大家应该能明白 Android 的广播生命周期的原理。

![](https://upload-images.jianshu.io/upload_images/682504-b9e5dc6f4d64da7c.gif)

> 步骤：
> 1，自定义一个类继承 BroadcastReceiver
> 2，重写 onReceive 方法
> 3，在 manifest.xml 中注册

**注意** ：**BroadcastReceiver 生命周期很短**
如果需要在 onReceiver 完成一些耗时操作，应该考虑在 Service 中开启一个新线程处理耗时操作，**不应该在 BroadcastReceiver 中开启一个新的线程**，因为 BroadcastReceiver 生命周期很短，在执行完 onReceiver 以后就结束，如果开启一个新的线程，可能出现 BroadcastRecevier 退出以后线程还在，而如果 BroadcastReceiver 所在的进程结束了，该线程就会被标记为一个空线程，根据 Android 的内存管理策略，在系统内存紧张的时候，会按照优先级，结束优先级低的线程，而空线程无异是优先级最低的，这样就可能导致 BroadcastReceiver 启动的子线程不能执行完成。

**示例**

```
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("fuck","intent-action : " + intent.getAction());
        if(intent.getAction().equals("test")){
            Toast.makeText(context,"fuck",Toast.LENGTH_LONG).show();
        }
    }

}

```

注册

```
        //广播接收器
        <receiver android:>

            <intent-filter>
                <action android: />
                <action android:/>//这里自定义一个广播动作
            </intent-filter>

        </receiver>

```

广播还可以通过动态注册：

```
registerReceiver(new MyBroadcastReceiver(),new IntentFilter("test"));

```

一定要加上这个权限（坑）

```
<uses-permission android:/>

```

**注意**：xml 中注册的优先级高于动态注册广播。

发送广播

```
 Intent intent = new Intent("test");
                sendBroadcast(intent);

```

**静态注册和动态注册区别**

*   动态注册广播不是常驻型广播，也就是说广播跟随 activity 的生命周期。注意: 在 activity 结束前，移除广播接收器。
    静态注册是常驻型，也就是说当应用程序关闭后，如果有信息广播来，程序也会被系统调用自动运行。
*   当广播为有序广播时：
    1 优先级高的先接收
    2 同优先级的广播接收器，动态优先于静态
    3 同优先级的同类广播接收器，静态：先扫描的优先于后扫描的，动态：先注册的优先于后注册的。
*   当广播为普通广播时：
    1 无视优先级，动态广播接收器优先于静态广播接收器
    2 同优先级的同类广播接收器，静态：先扫描的优先于后扫描的，动态：先注册的优先于后注册的。

小结：

*   在 Android 中如果要发送一个广播必须使用 sendBroadCast 向系统发送对其感兴趣的广播接收器中。
*   使用广播必须要有一个 intent 对象必设置其 action 动作对象
*   使用广播必须在配置文件中显式的指明该广播对象
*   每次接收广播都会重新生成一个接收广播的对象
*   在 BroadCastReceiver 中尽量不要处理太多逻辑问题，建议复杂的逻辑交给 Activity 或者 Service 去处理
*   如果在 AndroidManifest.xml 中注册，当应用程序关闭的时候，也会接收到广播。在应用程序中注册就不产生这种情况了。

**注意**

> 当如果要进行的操作需要花费比较长的时间，则不适合放在 BroadcastReceiver 中进行处理。
> 引用网上找到的一段解释：
> 在 Android 中，程序的响应（ Responsive ）被活动管理器（ Activity Manager ）和窗口管理器（ Window Manager ）这两个系统服务所监视。当 BroadcastReceiver 在 10 秒内没有执行完毕，Android 会认为该程序无响应。所以在 BroadcastReceiver 里不能做一些比较耗时的操作，否侧会弹出 ANR （ Application No Response ）的对话框。如果需要完成一项比较耗时的工作，应该通过发送 Intent 给 Service ，由 Service 来完成。而不是使用子线程的方法来解决，因为 BroadcastReceiver 的生命周期很短（在 onReceive() 执行后 BroadcastReceiver 的实例就会被销毁），子线程可能还没有结束 BroadcastReceiver 就先结束了。如果 BroadcastReceiver 结束了，它的宿主进程还在运行，那么子线程还会继续执行。但宿主进程此时很容易在系统需要内存时被优先杀死，因为它属于空进程（没有任何活动组件的进程）。

## Android 四大组件 --- ContentProvider

> contentprovider 是 android 四大组件之一的内容提供器，它主要的作用就是将程序的内部的数据和外部进行共享，为数据提供外部访问接口，被访问的数据主要以数据库的形式存在，而且还可以选择共享哪一部分的数据。这样一来，对于程序当中的隐私数据可以不共享，从而更加安全。contentprovider 是 android 中一种跨程序共享数据的重要组件。

#### 使用系统的 ContentProvider

系统的 ContentProvider 有很多，如通话记录，短信，通讯录等等，都需要和第三方的 app 进行共享数据。既然是使用系统的，那么 contentprovider 的具体实现就不需要我们担心了，使用内容提供者的步骤如下

*   获取 ContentResolver 实例
*   确定 Uri 的内容，并解析为具体的 Uri 实例
*   通过 ContentResolver 实例来调用相应的方法，传递相应的参数，但是第一个参数总是 Uri，它制定了我们要操作的数据的具体地址

可以通过读取系统通讯录的联系人信息，显示在 Listview 中来实践这些知识。不要忘记在读取通讯录的时候，在清单文件中要加入相应的读取权限。

#### 自定义 ContentProvider

系统的 contentprovider 在与我们交互的时候，只接受了一个 Uri 的参数，然后根据我们的操作返回给我们结果。系统到底是如何根据一个 Uri 就能够提供给我们准确的结果呢？只有自己亲自实现一个看看了。

和之前提到的一样，想重新自定义自己程序中的四大组件，就必须重新实现一个类，重写这个类中的抽象方法，在清单文件中注册，最后才能够正常使用。

重新实现 ContentProvider 之后，发现我们重写了 6 个重要的抽象方法

*   oncreate
*   query
*   update
*   insert
*   delete
*   gettype

大部分的方法在数据库那里已经见过了，他们内部的逻辑可想而知都是对数据的增删改查操作，其中这些方法的第一个参数大多都是 Uri 实例。其中有两个方法比较特殊：

*   oncreate 方法应该是内容提供者创建的时候所执行的一个回调方法，负责数据库的创建和更新操作。这个方法只有我们在程序中获取 ContentResolver 实例之后准备访问共享数据的时候，才会被执行。
*   gettype 方法是获取我们通过参数传递进去的 Uri 的 MIME 类型，这个类型是什么，后面会有实例说明。

内容提供者首先要做的一个事情就是将我们传递过来的 Uri 解析出来，确定其他程序到底想访问哪些数据。Uri 的形式一般有两种：

1，以路径名为结尾，这种 Uri 请求的是整个表的数据，如: [content://com.demo.androiddemo.provider/tabl1](https://link.jianshu.com?t=content%3A%2F%2Fcom.demo.androiddemo.provider%2Ftabl1) 标识我们要访问 tabl1 表中所有的数据
2，以 id 列值结尾，这种 Uri 请求的是该表中和其提供的列值相等的单条数据。 [content://com.demo.androiddemo.provider/tabl1/1](https://link.jianshu.com?t=content%3A%2F%2Fcom.demo.androiddemo.provider%2Ftabl1%2F1) 标识我们要访问 tabl1 表中_id 列值为 1 的数据。

如果是内容提供器的设计者，那么我们肯定知道这个程序的数据库是什么样的，每一张表，或者每一张表中的_id 都应该有一个唯一的内容 Uri。我们可以将传递进来的 Uri 和我们存好的 Uri 进行匹配，匹配到了之后，就说明数据源已经找到，便可以进行相应的增删改查操作。

[ContentProvider 详解](https://link.jianshu.com?t=http%3A%2F%2Ffengzixu.net%2F2015%2F05%2F21%2FContentProvider-%25E8%25AF%25A6%25E8%25A7%25A3%2F)

## 五种布局

**RelativeLayout 实现平分父布局**

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">

        <View android:id="@+id/strut"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_centerHorizontal="true"/>
        <Button
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_alignRight="@id/strut"
            android:layout_alignParentLeft="true"
            android:text="Left"/>
        <Button
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_alignLeft="@id/strut"
            android:layout_alignParentRight="true"
            android:text="Right"/>

</RelativeLayout>

```

RelativeLayout 的子 view 的 layout_gravity 属性是没有效果的，而是通过

```
            android:layout_centerHorizontal="true"
            android:layout_alignParentBottom="true"
            android:layout_alignParentRight="true"
            android:layout_alignParentTop="true"

```

这样的一些属性来代替。