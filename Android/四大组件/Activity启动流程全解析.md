

### 主要对象功能介绍

- ActivityManagerServices，简称AMS，服务端对象，负责系统中所有Activity的生命周期
- ActivityThread，App的真正入口。当开启App之后，会调用main()开始运行，开启消息循环队列，这就是传说中的UI线程或者叫主线程。与ActivityManagerServices配合，一起完成Activity的管理工作
- ApplicationThread，用来实现ActivityManagerService与ActivityThread之间的交互。在ActivityManagerService需要管理相关Application中的Activity的生命周期时，通过ApplicationThread的代理对象与ActivityThread通讯。
- ApplicationThreadProxy，是ApplicationThread在服务器端的代理，负责和客户端的ApplicationThread通讯。AMS就是通过该代理与ActivityThread进行通信的。
Instrumentation，每一个应用程序只有一个Instrumentation对象，每个Activity内都有一个对该对象的引用。Instrumentation可以理解为应用进程的管家，- ActivityThread要创建或暂停某个Activity时，都需要通过Instrumentation来进行具体的操作。
- ActivityStack，Activity在AMS的栈管理，用来记录已经启动的Activity的先后关系，状态信息等。通过ActivityStack决定是否需要启动新的进程。
- ActivityRecord，ActivityStack的管理对象，每个Activity在AMS对应一个ActivityRecord，来记录Activity的状态以及其他的管理信息。其实就是服务器端的Activity对象的映像。
- TaskRecord，AMS抽象出来的一个“任务”的概念，是记录ActivityRecord的栈，一个“Task”包含若干个ActivityRecord。AMS用TaskRecord确保Activity启动和退出的顺序。如果你清楚Activity的4种launchMode，那么对这个概念应该不陌生。

### zygote是什么？有什么作用？

在Android系统里面，zygote是一个进程的名字。Android是基于Linux System的，当你的手机开机的时候，Linux的内核加载完成之后就会启动一个叫“init“的进程。在Linux System里面，所有的进程都是由init进程fork出来的，我们的zygote进程也不例外。

我们都知道，每一个App其实都是

- 一个单独的dalvik虚拟机
- 一个单独的进程

所以当系统里面的第一个zygote进程运行之后，在这之后再开启App，就相当于开启一个新的进程。而为了实现资源共用和更快的启动速度，Android系统开启新进程的方式，是通过fork第一个zygote进程实现的。所以说，除了第一个zygote进程，其他应用所在的进程都是zygote的子进程，这下你明白为什么这个进程叫“受精卵”了吧？因为就像是一个受精卵一样，它能快速的分裂，并且产生遗传物质一样的细胞！

### SystemServer是什么？有什么作用？它与zygote的关系是什么？
首先,SystemServer也是一个进程,而且是由zygote进程fork出来的.
为什么说SystemServer非常重要呢？因为系统里面重要的服务都是在这个进程里面开启的，比如
ActivityManagerService、PackageManagerService、WindowManagerService等等，看着是不是都挺眼熟的？
在zygote开启的时候，会调用ZygoteInit.main()进行初始化
```java
public static void main(String argv[]) {
    
     ...ignore some code...
    
    //在加载首个zygote的时候，会传入初始化参数，使得startSystemServer = true
     boolean startSystemServer = false;
     for (int i = 1; i < argv.length; i++) {
                if ("start-system-server".equals(argv[i])) {
                    startSystemServer = true;
                } else if (argv[i].startsWith(ABI_LIST_ARG)) {
                    abiList = argv[i].substring(ABI_LIST_ARG.length());
                } else if (argv[i].startsWith(SOCKET_NAME_ARG)) {
                    socketName = argv[i].substring(SOCKET_NAME_ARG.length());
                } else {
                    throw new RuntimeException("Unknown command line argument: " + argv[i]);
                }
            }
            
            ...ignore some code...
            
         //开始fork我们的SystemServer进程
     if (startSystemServer) {
                startSystemServer(abiList, socketName);
         }

     ...ignore some code...

}
```
我们看下startSystemServer()做了些什么
```java
/**留着这个注释，就是为了说明SystemServer确实是被fork出来的
     * Prepare the arguments and fork for the system server process.
     */
    private static boolean startSystemServer(String abiList, String socketName)
            throws MethodAndArgsCaller, RuntimeException {
        
         ...ignore some code...
        
        //留着这段注释，就是为了说明上面ZygoteInit.main(String argv[])里面的argv就是通过这种方式传递进来的
        /* Hardcoded command line to start the system server */
        String args[] = {
            "--setuid=1000",
            "--setgid=1000",
            "--setgroups=1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1018,1032,3001,3002,3003,3006,3007",
            "--capabilities=" + capabilities + "," + capabilities,
            "--runtime-init",
            "--nice-name=system_server",
            "com.android.server.SystemServer",
        };

        int pid;
        try {
            parsedArgs = new ZygoteConnection.Arguments(args);
            ZygoteConnection.applyDebuggerSystemProperty(parsedArgs);
            ZygoteConnection.applyInvokeWithSystemProperty(parsedArgs);

        //确实是fuck出来的吧，我没骗你吧~不对，是fork出来的 -_-|||
            /* Request to fork the system server process */
            pid = Zygote.forkSystemServer(
                    parsedArgs.uid, parsedArgs.gid,
                    parsedArgs.gids,
                    parsedArgs.debugFlags,
                    null,
                    parsedArgs.permittedCapabilities,
                    parsedArgs.effectiveCapabilities);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }

        /* For child process */
        if (pid == 0) {
            if (hasSecondZygote(abiList)) {
                waitForSecondaryZygote(socketName);
            }

            handleSystemServerProcess(parsedArgs);
        }

        return true;
    }
```

### ActivityManagerService是什么？什么时候初始化的？有什么作用？

**ActivityManagerService，简称AMS，服务端对象，负责系统中所有Activity的生命周期。**
ActivityManagerService进行初始化的时机很明确，就是在SystemServer进程开启的时候，就会初始化ActivityManagerService。从下面的代码中可以看到
```java
public final class SystemServer {

    //zygote的主入口
    public static void main(String[] args) {
        new SystemServer().run();
    }

    public SystemServer() {
        // Check for factory test mode.
        mFactoryTestMode = FactoryTest.getMode();
    }
    
    private void run() {
        
        ...ignore some code...
        
        //加载本地系统服务库，并进行初始化 
        System.loadLibrary("android_servers");
        nativeInit();
        
        // 创建系统上下文
        createSystemContext();
        
        //初始化SystemServiceManager对象，下面的系统服务开启都需要调用SystemServiceManager.startService(Class<T>)，这个方法通过反射来启动对应的服务
        mSystemServiceManager = new SystemServiceManager(mSystemContext);
        
        //开启服务
        try {
            startBootstrapServices();
            startCoreServices();
            startOtherServices();
        } catch (Throwable ex) {
            Slog.e("System", "******************************************");
            Slog.e("System", "************ Failure starting system services", ex);
            throw ex;
        }
       
        ...ignore some code...
    
    }

    //初始化系统上下文对象mSystemContext，并设置默认的主题,mSystemContext实际上是一个ContextImpl对象。调用ActivityThread.systemMain()的时候，会调用ActivityThread.attach(true)，而在attach()里面，则创建了Application对象，并调用了Application.onCreate()。
    private void createSystemContext() {
        ActivityThread activityThread = ActivityThread.systemMain();
        mSystemContext = activityThread.getSystemContext();
        mSystemContext.setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
    }

    //在这里开启了几个核心的服务，因为这些服务之间相互依赖，所以都放在了这个方法里面。
    private void startBootstrapServices() {
        
        ...ignore some code...
        
        //初始化ActivityManagerService
        mActivityManagerService = mSystemServiceManager.startService(
                ActivityManagerService.Lifecycle.class).getService();
        mActivityManagerService.setSystemServiceManager(mSystemServiceManager);
        
        //初始化PowerManagerService，因为其他服务需要依赖这个Service，因此需要尽快的初始化
        mPowerManagerService = mSystemServiceManager.startService(PowerManagerService.class);

        // 现在电源管理已经开启，ActivityManagerService负责电源管理功能
        mActivityManagerService.initPowerManagement();

        // 初始化DisplayManagerService
        mDisplayManagerService = mSystemServiceManager.startService(DisplayManagerService.class);
    
    //初始化PackageManagerService
    mPackageManagerService = PackageManagerService.main(mSystemContext, mInstaller,
       mFactoryTestMode != FactoryTest.FACTORY_TEST_OFF, mOnlyCore);
    
    ...ignore some code...
    
    }

}
```
经过上面这些步骤，我们的ActivityManagerService对象已经创建好了，并且完成了成员变量初始化。而且在这之前，调用createSystemContext()创建系统上下文的时候，也已经完成了mSystemContext和ActivityThread的创建。注意，这是系统进程开启时的流程，在这之后，会开启系统的Launcher程序，完成系统界面的加载与显示。
你是否会好奇，我为什么说AMS是服务端对象？下面我给你介绍下Android系统里面的服务器和客户端的概念。

其实服务器客户端的概念不仅仅存在于Web开发中，在Android的框架设计中，使用的也是这一种模式。服务器端指的就是所有App共用的系统服务，比如我们这里提到的ActivityManagerService，和前面提到的PackageManagerService、WindowManagerService等等，这些基础的系统服务是被所有的App公用的，当某个App想实现某个操作的时候，要告诉这些系统服务，比如你想打开一个App，那么我们知道了包名和MainActivity类名之后就可以打开
```java
Intent intent = new Intent(Intent.ACTION_MAIN);  
intent.addCategory(Intent.CATEGORY_LAUNCHER);              
ComponentName cn = new ComponentName(packageName, className);              
intent.setComponent(cn);  
startActivity(intent); 
```
但是，我们的App通过调用startActivity()并不能直接打开另外一个App，这个方法会通过一系列的调用，最后还是告诉AMS说：“我要打开这个App，我知道他的住址和名字，你帮我打开吧！”所以是AMS来通知zygote进程来fork一个新进程，来开启我们的目标App的。这就像是浏览器想要打开一个超链接一样，浏览器把网页地址发送给服务器，然后还是服务器把需要的资源文件发送给客户端的。

知道了Android Framework的客户端服务器架构之后，我们还需要了解一件事情，那就是我们的App和AMS(SystemServer进程)还有zygote进程分属于三个独立的进程，他们之间如何通信呢？
App与AMS通过Binder进行IPC通信，AMS(SystemServer进程)与zygote通过Socket进行IPC通信。

那么AMS有什么用呢？在前面我们知道了，**如果想打开一个App的话，需要AMS去通知zygote进程，除此之外，其实所有的Activity的开启、暂停、关闭都需要AMS来控制，所以我们说，AMS负责系统中所有Activity的生命周期。**

### Launcher是什么？什么时候启动的？
当我们点击手机桌面上的图标的时候，App就由Launcher开始启动了。但是，你有没有思考过Launcher到底是一个什么东西？

Launcher本质上也是一个应用程序，和我们的App一样，也是继承自Activity

packages/apps/Launcher2/src/com/android/launcher2/Launcher.java
```java
public final class Launcher extends Activity
        implements View.OnClickListener, OnLongClickListener, LauncherModel.Callbacks,
                   View.OnTouchListener {
                   }
```
Launcher实现了点击、长按等回调接口，来接收用户的输入。既然是普通的App，那么我们的开发经验在这里就仍然适用，比如，**我们点击图标的时候，是怎么开启的应用呢？**如果让你，你怎么做这个功能呢？**捕捉图标点击事件，然后startActivity()发送对应的Intent请求呗！**是的，Launcher也是这么做的，就是这么easy！

那么到底是处理的哪个对象的点击事件呢？既然Launcher是App，并且有界面，那么肯定有布局文件呀，是的，我找到了布局文件launcher.xml
```xml
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:launcher="http://schemas.android.com/apk/res/com.android.launcher"
    android:id="@+id/launcher">

    <com.android.launcher2.DragLayer
        android:id="@+id/drag_layer"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:fitsSystemWindows="true">

        <!-- Keep these behind the workspace so that they are not visible when
             we go into AllApps -->
        <include
            android:id="@+id/dock_divider"
            layout="@layout/workspace_divider"
            android:layout_marginBottom="@dimen/button_bar_height"
            android:layout_gravity="bottom" />

        <include
            android:id="@+id/paged_view_indicator"
            layout="@layout/scroll_indicator"
            android:layout_gravity="bottom"
            android:layout_marginBottom="@dimen/button_bar_height" />

        <!-- The workspace contains 5 screens of cells -->
        <com.android.launcher2.Workspace
            android:id="@+id/workspace"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:paddingStart="@dimen/workspace_left_padding"
            android:paddingEnd="@dimen/workspace_right_padding"
            android:paddingTop="@dimen/workspace_top_padding"
            android:paddingBottom="@dimen/workspace_bottom_padding"
            launcher:defaultScreen="2"
            launcher:cellCountX="@integer/cell_count_x"
            launcher:cellCountY="@integer/cell_count_y"
            launcher:pageSpacing="@dimen/workspace_page_spacing"
            launcher:scrollIndicatorPaddingLeft="@dimen/workspace_divider_padding_left"
            launcher:scrollIndicatorPaddingRight="@dimen/workspace_divider_padding_right">

            <include android:id="@+id/cell1" layout="@layout/workspace_screen" />
            <include android:id="@+id/cell2" layout="@layout/workspace_screen" />
            <include android:id="@+id/cell3" layout="@layout/workspace_screen" />
            <include android:id="@+id/cell4" layout="@layout/workspace_screen" />
            <include android:id="@+id/cell5" layout="@layout/workspace_screen" />
        </com.android.launcher2.Workspace>

    ...ignore some code...

    </com.android.launcher2.DragLayer>
</FrameLayout>
```
为了方便查看，我删除了很多代码，从上面这些我们应该可以看出一些东西来：Launcher大量使用<include/>标签来实现界面的复用，而且定义了很多的自定义控件实现界面效果，dock_divider从布局的参数声明上可以猜出，是底部操作栏和上面图标布局的分割线，而paged_view_indicator则是页面指示器，和App首次进入的引导页下面的界面引导是一样的道理。当然，我们最关心的是Workspace这个布局，因为注释里面说在这里面包含了5个屏幕的单元格，想必你也猜到了，这个就是在首页存放我们图标的那五个界面(不同的ROM会做不同的DIY，数量不固定)。

接下来，我们应该打开workspace_screen布局，看看里面有什么东东。

workspace_screen.xml

```java
<com.android.launcher2.CellLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:launcher="http://schemas.android.com/apk/res/com.android.launcher"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:paddingStart="@dimen/cell_layout_left_padding"
    android:paddingEnd="@dimen/cell_layout_right_padding"
    android:paddingTop="@dimen/cell_layout_top_padding"
    android:paddingBottom="@dimen/cell_layout_bottom_padding"
    android:hapticFeedbackEnabled="false"
    launcher:cellWidth="@dimen/workspace_cell_width"
    launcher:cellHeight="@dimen/workspace_cell_height"
    launcher:widthGap="@dimen/workspace_width_gap"
    launcher:heightGap="@dimen/workspace_height_gap"
    launcher:maxGap="@dimen/workspace_max_gap" />
```

里面就一个CellLayout，也是一个自定义布局，那么我们就可以猜到了，既然可以存放图标，那么这个自定义的布局很有可能是继承自ViewGroup或者是其子类，实际上，CellLayout确实是继承自ViewGroup。在CellLayout里面，只放了一个子View，那就是ShortcutAndWidgetContainer。从名字也可以看出来，ShortcutAndWidgetContainer这个类就是用来存放快捷图标和Widget小部件的，那么里面放的是什么对象呢？

在桌面上的图标，使用的是BubbleTextView对象，这个对象在TextView的基础之上，添加了一些特效，比如你长按移动图标的时候，图标位置会出现一个背景(不同版本的效果不同)，所以我们找到BubbleTextView对象的点击事件，就可以找到Launcher如何开启一个App了。

除了在桌面上有图标之外，在程序列表中点击图标，也可以开启对应的程序。这里的图标使用的不是BubbleTextView对象，而是PagedViewIcon对象，我们如果找到它的点击事件，就也可以找到Launcher如何开启一个App。

BubbleTextView的点击事件
```java
/**
* Launches the intent referred by the clicked shortcut
*/
public void onClick(View v) {

        ...ignore some code...
        
        Object tag = v.getTag();
    if (tag instanceof ShortcutInfo) {
        // Open shortcut
        final Intent intent = ((ShortcutInfo) tag).intent;
        int[] pos = new int[2];
        v.getLocationOnScreen(pos);
        intent.setSourceBounds(new Rect(pos[0], pos[1],
                pos[0] + v.getWidth(), pos[1] + v.getHeight()));
    //开始开启Activity咯~
        boolean success = startActivitySafely(v, intent, tag);

        if (success && v instanceof BubbleTextView) {
            mWaitingForResume = (BubbleTextView) v;
            mWaitingForResume.setStayPressed(true);
        }
    } else if (tag instanceof FolderInfo) {
        //如果点击的是图标文件夹，就打开文件夹
        if (v instanceof FolderIcon) {
            FolderIcon fi = (FolderIcon) v;
            handleFolderClick(fi);
        }
    } else if (v == mAllAppsButton) {
    ...ignore some code...
    }
}

```
下面我们就可以一步步的来看一下Launcher.startActivitySafely()到底做了什么事情。
```java
boolean startActivitySafely(View v, Intent intent, Object tag) {
        boolean success = false;
        try {
            success = startActivity(v, intent, tag);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
        }
        return success;
    }
```
调用了startActivity(v, intent, tag)
```java
boolean startActivity(View v, Intent intent, Object tag) {
        //新的Task栈
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            boolean useLaunchAnimation = (v != null) &&
                    !intent.hasExtra(INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION);
            
            if (useLaunchAnimation) {
                if (user == null || user.equals(android.os.Process.myUserHandle())) {
                    startActivity(intent, opts.toBundle());
                } else {
                    launcherApps.startMainActivity(intent.getComponent(), user,
                            intent.getSourceBounds(),
                            opts.toBundle());
                }
            } else {
                if (user == null || user.equals(android.os.Process.myUserHandle())) {
                    startActivity(intent);
                } else {
                    launcherApps.startMainActivity(intent.getComponent(), user,
                            intent.getSourceBounds(), null);
                }
            }
            return true;
        } catch (SecurityException e) {
        ...
        }
        return false;
    }
```
所以这个Activity会添加到一个新的Task栈中，而且，startActivity()调用的其实是startActivityForResult()这个方法。
```java
@Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        if (options != null) {
            startActivityForResult(intent, -1, options);
        } else {
            // Note we want to go through this call for compatibility with
            // applications that may have overridden the method.
            startActivityForResult(intent, -1);
        }
    }
```
所以我们现在明确了，Launcher中开启一个App，其实和我们在Activity中直接startActivity()基本一样，都是调用了Activity.startActivityForResult()。

### Instrumentation是什么？和ActivityThread是什么关系？
还记得前面说过的Instrumentation对象吗？每个Activity都持有Instrumentation对象的一个引用，但是整个进程只会存在一个Instrumentation对象。当startActivityForResult()调用之后，实际上还是调用了mInstrumentation.execStartActivity()

```java
public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (mParent == null) {
            Instrumentation.ActivityResult ar =
                mInstrumentation.execStartActivity(
                    this, mMainThread.getApplicationThread(), mToken, this,
                    intent, requestCode, options);
            if (ar != null) {
                mMainThread.sendActivityResult(
                    mToken, mEmbeddedID, requestCode, ar.getResultCode(),
                    ar.getResultData());
            }
            ...ignore some code...    
        } else {
            if (options != null) {
                 //当现在的Activity有父Activity的时候会调用，但是在startActivityFromChild()内部实际还是调用的mInstrumentation.execStartActivity()
                mParent.startActivityFromChild(this, intent, requestCode, options);
            } else {
                mParent.startActivityFromChild(this, intent, requestCode);
            }
        }
         ...ignore some code...    
    }

public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        IApplicationThread whoThread = (IApplicationThread) contextThread;
            ...ignore some code...
      try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess();
            int result = ActivityManagerNative.getDefault()
                .startActivity(whoThread, who.getBasePackageName(), intent,
                        intent.resolveTypeIfNeeded(who.getContentResolver()),
                        token, target != null ? target.mEmbeddedID : null,
                        requestCode, 0, null, options);
            checkStartActivityResult(result, intent);
        } catch (RemoteException e) {
        }
        return null;
    }
```
所以当我们在程序中调用startActivity()的 时候，实际上调用的是Instrumentation的相关的方法。Instrumentation这个类里面的方法大多数和Application和Activity有关，是的，这个类就是完成对Application和Activity初始化和生命周期的工具类。

ActivityThread你都没听说过？那你肯定听说过传说中的UI线程吧？是的，这就是UI线程。我们前面说过，App和AMS是通过Binder传递信息的，那么ActivityThread就是专门与AMS的外交工作的。

AMS说：“ActivityThread，你给我暂停一个Activity！”
ActivityThread就说：“没问题！”然后转身和Instrumentation说：“老婆，AMS让暂停一个Activity，我这里忙着呢，你快去帮我把这事办了把~”
于是，Instrumentation就去把事儿搞定了。

所以说，AMS是董事会，负责指挥和调度的，ActivityThread是老板，虽然说家里的事自己说了算，但是需要听从AMS的指挥，而Instrumentation则是老板娘，负责家里的大事小事，但是一般不抛头露面，听一家之主ActivityThread的安排。

### 如何理解AMS和ActivityThread之间的Binder通信？

前面我们说到，在调用startActivity()的时候，实际上调用的是
```
mInstrumentation.execStartActivity()
```
但是到这里还没完呢！里面又调用了下面的方法
```java
ActivityManagerNative.getDefault()
                .startActivity
```
这里的ActivityManagerNative.getDefault返回的就是ActivityManagerService的远程接口，即ActivityManagerProxy。

怎么知道的呢？往下看
```java
public abstract class ActivityManagerNative extends Binder implements IActivityManager
{

//从类声明上，我们可以看到ActivityManagerNative是Binder的一个子类，而且实现了IActivityManager接口
 static public IActivityManager getDefault() {
        return gDefault.get();
    }

 //通过单例模式获取一个IActivityManager对象，这个对象通过asInterface(b)获得
 private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() {
        protected IActivityManager create() {
            IBinder b = ServiceManager.getService("activity");
            if (false) {
                Log.v("ActivityManager", "default service binder = " + b);
            }
            IActivityManager am = asInterface(b);
            if (false) {
                Log.v("ActivityManager", "default service = " + am);
            }
            return am;
        }
    };
}


//最终返回的还是一个ActivityManagerProxy对象
static public IActivityManager asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IActivityManager in =
            (IActivityManager)obj.queryLocalInterface(descriptor);
        if (in != null) {
            return in;
        }
    
     //这里面的Binder类型的obj参数会作为ActivityManagerProxy的成员变量保存为mRemote成员变量，负责进行IPC通信
        return new ActivityManagerProxy(obj);
    }
}

```
再看ActivityManagerProxy.startActivity()，在这里面做的事情就是IPC通信，利用Binder对象，调用transact()，把所有需要的参数封装成Parcel对象，向AMS发送数据进行通信。
```java
public int startActivity(IApplicationThread caller, String callingPackage, Intent intent,
            String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(callingPackage);
        intent.writeToParcel(data, 0);
        data.writeString(resolvedType);
        data.writeStrongBinder(resultTo);
        data.writeString(resultWho);
        data.writeInt(requestCode);
        data.writeInt(startFlags);
        if (profilerInfo != null) {
            data.writeInt(1);
            profilerInfo.writeToParcel(data, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        } else {
            data.writeInt(0);
        }
        if (options != null) {
            data.writeInt(1);
            options.writeToParcel(data, 0);
        } else {
            data.writeInt(0);
        }
        mRemote.transact(START_ACTIVITY_TRANSACTION, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }
```
Binder本质上只是一种底层通信方式，和具体服务没有关系。为了提供具体服务，Server必须提供一套接口函数以便Client通过远程访问使用各种服务。这时通常采用Proxy设计模式：将接口函数定义在一个抽象类中，Server和Client都会以该抽象类为基类实现所有接口函数，所不同的是Server端是真正的功能实现，而Client端是对这些函数远程调用请求的包装。

为了更方便的说明客户端和服务器之间的Binder通信，下面以ActivityManagerServices和他在客户端的代理类ActivityManagerProxy为例。

ActivityManagerServices和ActivityManagerProxy都实现了同一个接口——IActivityManager。
```java
class ActivityManagerProxy implements IActivityManager{}

public final class ActivityManagerService extends ActivityManagerNative{}

public abstract class ActivityManagerNative extends Binder implements IActivityManager{}
```
虽然都实现了同一个接口，但是代理对象ActivityManagerProxy并不会对这些方法进行真正地实现，ActivityManagerProxy只是通过这种方式对方法的参数进行打包(因为都实现了相同接口，所以可以保证同一个方法有相同的参数，即对要传输给服务器的数据进行打包)，真正实现的是ActivityManagerService。

但是这个地方并不是直接由客户端传递给服务器，而是通过Binder驱动进行中转。其实我对Binder驱动并不熟悉，我们就把他当做一个中转站就OK，客户端调用ActivityManagerProxy接口里面的方法，把数据传送给Binder驱动，然后Binder驱动就会把这些东西转发给服务器的ActivityManagerServices，由ActivityManagerServices去真正的实施具体的操作。

但是Binder只能传递数据，并不知道是要调用ActivityManagerServices的哪个方法，所以在数据中会添加方法的唯一标识码，比如前面的startActivity()方法：
```java
public int startActivity(IApplicationThread caller, String callingPackage, Intent intent,
            String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
       
        ...ignore some code...
       
        mRemote.transact(START_ACTIVITY_TRANSACTION, data, reply, 0);
        reply.readException();
        int result = reply.readInt();
        reply.recycle();
        data.recycle();
        return result;
    }
```
上面的`START_ACTIVITY_TRANSACTION`就是方法标示，data是要传输给Binder驱动的数据，reply则接受操作的返回值。

即

客户端：ActivityManagerProxy =====>Binder驱动=====> ActivityManagerService：服务器

而且由于继承了同样的公共接口类，ActivityManagerProxy提供了与ActivityManagerService一样的函数原型，使用户感觉不出Server是运行在本地还是远端，从而可以更加方便的调用这些重要的系统服务。

但是！**这里Binder通信是单方向的，即从ActivityManagerProxy指向ActivityManagerService的**，如果AMS想要通知ActivityThread做一些事情，应该咋办呢？

还是通过Binder通信，不过是换了另外一对，换成了ApplicationThread和ApplicationThreadProxy。

客户端：ApplicationThread <=====Binder驱动<===== ApplicationThreadProxy：服务器

他们也都实现了相同的接口IApplicationThread
```java
  private class ApplicationThread extends ApplicationThreadNative {}
  
  public abstract class ApplicationThreadNative extends Binder implements IApplicationThread{}
  
  class ApplicationThreadProxy implements IApplicationThread {}
```
剩下的就不必多说了吧，和前面一样。

### AMS接收到客户端的请求之后，会如何开启一个Activity？

OK，至此，点击桌面图标调用startActivity()，终于把数据和要开启Activity的请求发送到了AMS了。说了这么多，其实这些都在一瞬间完成了，下面咱们研究下AMS到底做了什么。

注：前方有高能的方法调用链，如果你现在累了，请先喝杯咖啡或者是上趟厕所休息下

AMS收到startActivity的请求之后，会按照如下的方法链进行调用
调用startActivity()
```java
@Override
    public final int startActivity(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options) {
        return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo,
            resultWho, requestCode, startFlags, profilerInfo, options,
            UserHandle.getCallingUserId());
    }

@Override
    public final int startActivityAsUser(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int startFlags, ProfilerInfo profilerInfo, Bundle options, int userId) {
            
            ...ignore some code...
            
        return mStackSupervisor.startActivityMayWait(caller, -1, callingPackage, intent,
                resolvedType, null, null, resultTo, resultWho, requestCode, startFlags,
                profilerInfo, null, null, options, userId, null, null);
    }
```

在这里又出现了一个新对象ActivityStackSupervisor，通过这个类可以实现对ActivityStack的部分操作。

```java
final int startActivityMayWait(IApplicationThread caller, int callingUid,
            String callingPackage, Intent intent, String resolvedType,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            IBinder resultTo, String resultWho, int requestCode, int startFlags,
            ProfilerInfo profilerInfo, WaitResult outResult, Configuration config,
            Bundle options, int userId, IActivityContainer iContainer, TaskRecord inTask) {
            
            ...ignore some code...
            
              int res = startActivityLocked(caller, intent, resolvedType, aInfo,
                    voiceSession, voiceInteractor, resultTo, resultWho,
                    requestCode, callingPid, callingUid, callingPackage,
                    realCallingPid, realCallingUid, startFlags, options,
                    componentSpecified, null, container, inTask);
            
            ...ignore some code...
            
            }

final int startActivityLocked(IApplicationThread caller,
            Intent intent, String resolvedType, ActivityInfo aInfo,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            IBinder resultTo, String resultWho, int requestCode,
            int callingPid, int callingUid, String callingPackage,
            int realCallingPid, int realCallingUid, int startFlags, Bundle options,
            boolean componentSpecified, ActivityRecord[] outActivity, ActivityContainer container,
            TaskRecord inTask) {
         
              err = startActivityUncheckedLocked(r, sourceRecord, voiceSession, voiceInteractor,
              startFlags, true, options, inTask);
        if (err < 0) {
            notifyActivityDrawnForKeyguard();
        }
        return err;
    }
```

调用startActivityUncheckedLocked(),此时要启动的Activity已经通过检验，被认为是一个正当的启动请求。

终于，在这里调用到了ActivityStack的startActivityLocked(ActivityRecord r, boolean newTask,boolean doResume, boolean keepCurTransition, Bundle options)。

ActivityRecord代表的就是要开启的Activity对象，里面分装了很多信息，比如所在的ActivityTask等，如果这是首次打开应用，那么这个Activity会被放到ActivityTask的栈顶，

```java
final int startActivityUncheckedLocked(ActivityRecord r, ActivityRecord sourceRecord,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, int startFlags,
            boolean doResume, Bundle options, TaskRecord inTask) {
            
            ...ignore some code...
            
            targetStack.startActivityLocked(r, newTask, doResume, keepCurTransition, options);
            
            ...ignore some code...
            
             return ActivityManager.START_SUCCESS;
            }
```

调用的是ActivityStack.startActivityLocked()

```java
final void startActivityLocked(ActivityRecord r, boolean newTask,
            boolean doResume, boolean keepCurTransition, Bundle options) {
        
        //ActivityRecord中存储的TaskRecord信息
        TaskRecord rTask = r.task;
      
         ...ignore some code...
      
        //如果不是在新的ActivityTask(也就是TaskRecord)中的话，就找出要运行在的TaskRecord对象
     TaskRecord task = null;
        if (!newTask) {
            boolean startIt = true;
            for (int taskNdx = mTaskHistory.size() - 1; taskNdx >= 0; --taskNdx) {
                task = mTaskHistory.get(taskNdx);
                if (task.getTopActivity() == null) {
                    // task中的所有Activity都结束了
                    continue;
                }
                if (task == r.task) {
                    // 找到了
                    if (!startIt) {
                        task.addActivityToTop(r);
                        r.putInHistory();
                        mWindowManager.addAppToken(task.mActivities.indexOf(r), r.appToken,
                                r.task.taskId, mStackId, r.info.screenOrientation, r.fullscreen,
                                (r.info.flags & ActivityInfo.FLAG_SHOW_ON_LOCK_SCREEN) != 0,
                                r.userId, r.info.configChanges, task.voiceSession != null,
                                r.mLaunchTaskBehind);
                        if (VALIDATE_TOKENS) {
                            validateAppTokensLocked();
                        }
                        ActivityOptions.abort(options);
                        return;
                    }
                    break;
                } else if (task.numFullscreen > 0) {
                    startIt = false;
                }
            }
        }

      ...ignore some code...

        // Place a new activity at top of stack, so it is next to interact
        // with the user.
        task = r.task;
        task.addActivityToTop(r);
        task.setFrontOfTask();

        ...ignore some code...

         if (doResume) {
            mStackSupervisor.resumeTopActivitiesLocked(this, r, options);
        }
    }
```
靠！这来回折腾什么呢！从ActivityStackSupervisor到ActivityStack，又调回ActivityStackSupervisor，这到底是在折腾什么玩意啊！！！

淡定...淡定...我知道你也在心里骂娘，世界如此美妙，你却如此暴躁，这样不好，不好...

来来来，咱们继续哈，刚才说到哪里了？哦，对，咱们一起看下StackSupervisor.resumeTopActivitiesLocked(this, r, options)
```java
boolean resumeTopActivitiesLocked(ActivityStack targetStack, ActivityRecord target,
            Bundle targetOptions) {
        if (targetStack == null) {
            targetStack = getFocusedStack();
        }
        // Do targetStack first.
        boolean result = false;
        if (isFrontStack(targetStack)) {
            result = targetStack.resumeTopActivityLocked(target, targetOptions);
        }
        
          ...ignore some code...
        
        return result;
    }

```
我...已无力吐槽了，又调回ActivityStack去了...
ActivityStack.resumeTopActivityLocked()

```java
final boolean resumeTopActivityLocked(ActivityRecord prev, Bundle options) {
        if (inResumeTopActivity) {
            // Don't even start recursing.
            return false;
        }

        boolean result = false;
        try {
            // Protect against recursion.
            inResumeTopActivity = true;
            result = resumeTopActivityInnerLocked(prev, options);
        } finally {
            inResumeTopActivity = false;
        }
        return result;
    }

final boolean resumeTopActivityInnerLocked(ActivityRecord prev, Bundle options) {
  
          ...ignore some code...
      //找出还没结束的首个ActivityRecord
     ActivityRecord next = topRunningActivityLocked(null);
    
    //如果一个没结束的Activity都没有，就开启Launcher程序
    if (next == null) {
            ActivityOptions.abort(options);
            if (DEBUG_STATES) Slog.d(TAG, "resumeTopActivityLocked: No more activities go home");
            if (DEBUG_STACK) mStackSupervisor.validateTopActivitiesLocked();
            // Only resume home if on home display
            final int returnTaskType = prevTask == null || !prevTask.isOverHomeStack() ?
                    HOME_ACTIVITY_TYPE : prevTask.getTaskToReturnTo();
            return isOnHomeDisplay() &&
                    mStackSupervisor.resumeHomeStackTask(returnTaskType, prev);
        }
        
        //先需要暂停当前的Activity。因为我们是在Lancher中启动mainActivity，所以当前mResumedActivity！=null，调用startPausingLocked()使得Launcher进入Pausing状态
          if (mResumedActivity != null) {
            pausing |= startPausingLocked(userLeaving, false, true, dontWaitForPause);
            if (DEBUG_STATES) Slog.d(TAG, "resumeTopActivityLocked: Pausing " + mResumedActivity);
        }
  
  }
```
在这个方法里，prev.app为记录启动Lancher进程的ProcessRecord，prev.app.thread为Lancher进程的远程调用接口IApplicationThead，所以可以调用prev.app.thread.schedulePauseActivity，到Lancher进程暂停指定Activity。
```java
final boolean startPausingLocked(boolean userLeaving, boolean uiSleeping, boolean resuming,
            boolean dontWait) {
        if (mPausingActivity != null) {
            completePauseLocked(false);
        }
       
       ...ignore some code...    

        if (prev.app != null && prev.app.thread != null) 
          try {
                mService.updateUsageStats(prev, false);
                prev.app.thread.schedulePauseActivity(prev.appToken, prev.finishing,
                        userLeaving, prev.configChangeFlags, dontWait);
            } catch (Exception e) {
                mPausingActivity = null;
                mLastPausedActivity = null;
                mLastNoHistoryActivity = null;
            }
        } else {
            mPausingActivity = null;
            mLastPausedActivity = null;
            mLastNoHistoryActivity = null;
        }

      ...ignore some code...  

 }
```
在Lancher进程中消息传递，调用ActivityThread.handlePauseActivity()，最终调用ActivityThread.performPauseActivity()暂停指定Activity。接着通过前面所说的Binder通信，通知AMS已经完成暂停的操作。
```
ActivityManagerNative.getDefault().activityPaused(token).
```
上面这些调用过程非常复杂，源码中各种条件判断让人眼花缭乱，所以说如果你没记住也没关系，你只要记住这个流程，理解了Android在控制Activity生命周期时是如何操作，以及是通过哪几个关键的类进行操作的就可以了，以后遇到相关的问题知道从哪块下手即可，这些过程我虽然也是撸了一遍，但还是记不清。

### 一个App的程序入口到底是什么？

是ActivityThread.main()。

### 整个App的主线程的消息循环是在哪里创建的？

是在ActivityThread初始化的时候，就已经创建消息循环了，所以在主线程里面创建Handler不需要指定Looper，而如果在其他线程使用Handler，则需要单独使用Looper.prepare()和Looper.loop()创建消息循环。
```java
public static void main(String[] args) {
 
          ...ignore some code...    

      Looper.prepareMainLooper();

        ActivityThread thread = new ActivityThread();
        thread.attach(false);

        if (sMainThreadHandler == null) {
            sMainThreadHandler = thread.getHandler();
        }

        AsyncTask.init();

        if (false) {
            Looper.myLooper().setMessageLogging(new
                    LogPrinter(Log.DEBUG, "ActivityThread"));
        }

        Looper.loop();
        
          ...ignore some code...    
        
 }
```

### Application是在什么时候创建的？onCreate()什么时候调用的？

也是在ActivityThread.main()的时候，再具体点呢，就是在thread.attach(false)的时候。

看你的表情，不信是吧！凯子哥带你溜溜~

我们先看一下ActivityThread.attach()
```java
private void attach(boolean system) {
        sCurrentActivityThread = this;
        mSystemThread = system;
        //普通App进这里
        if (!system) {
        
            ...ignore some code...    
        
            RuntimeInit.setApplicationObject(mAppThread.asBinder());
            final IActivityManager mgr = ActivityManagerNative.getDefault();
            try {
                mgr.attachApplication(mAppThread);
            } catch (RemoteException ex) {
                // Ignore
            }
           } else {
             //这个分支在SystemServer加载的时候会进入，通过调用
             // private void createSystemContext() {
             //    ActivityThread activityThread = ActivityThread.systemMain()；
             //} 
             
             // public static ActivityThread systemMain() {
        //        if (!ActivityManager.isHighEndGfx()) {
        //            HardwareRenderer.disable(true);
        //        } else {
        //            HardwareRenderer.enableForegroundTrimming();
        //        }
        //        ActivityThread thread = new ActivityThread();
        //        thread.attach(true);
        //        return thread;
        //    }       
           }
    }

这里需要关注的就是mgr.attachApplication(mAppThread)，这个就会通过Binder调用到AMS里面对应的方法

@Override
    public final void attachApplication(IApplicationThread thread) {
        synchronized (this) {
            int callingPid = Binder.getCallingPid();
            final long origId = Binder.clearCallingIdentity();
            attachApplicationLocked(thread, callingPid);
            Binder.restoreCallingIdentity(origId);
        }
    }

然后就是

 private final boolean attachApplicationLocked(IApplicationThread thread,
            int pid) {
            
            
             thread.bindApplication(processName, appInfo, providers, app.instrumentationClass,
                    profilerInfo, app.instrumentationArguments, app.instrumentationWatcher,
                    app.instrumentationUiAutomationConnection, testMode, enableOpenGlTrace,
                    isRestrictedBackupMode || !normalMode, app.persistent,
                    new Configuration(mConfiguration), app.compat, getCommonServicesLocked(),
                    mCoreSettingsObserver.getCoreSettingsLocked());
            
            
            }

thread是IApplicationThread，实际上就是ApplicationThread在服务端的代理类ApplicationThreadProxy，然后又通过IPC就会调用到ApplicationThread的对应方法

private class ApplicationThread extends ApplicationThreadNative {

  public final void bindApplication(String processName, ApplicationInfo appInfo,
                List<ProviderInfo> providers, ComponentName instrumentationName,
                ProfilerInfo profilerInfo, Bundle instrumentationArgs,
                IInstrumentationWatcher instrumentationWatcher,
                IUiAutomationConnection instrumentationUiConnection, int debugMode,
                boolean enableOpenGlTrace, boolean isRestrictedBackupMode, boolean persistent,
                Configuration config, CompatibilityInfo compatInfo, Map<String, IBinder> services,
                Bundle coreSettings) {
                
                 ...ignore some code...    
                
             AppBindData data = new AppBindData();
            data.processName = processName;
            data.appInfo = appInfo;
            data.providers = providers;
            data.instrumentationName = instrumentationName;
            data.instrumentationArgs = instrumentationArgs;
            data.instrumentationWatcher = instrumentationWatcher;
            data.instrumentationUiAutomationConnection = instrumentationUiConnection;
            data.debugMode = debugMode;
            data.enableOpenGlTrace = enableOpenGlTrace;
            data.restrictedBackupMode = isRestrictedBackupMode;
            data.persistent = persistent;
            data.config = config;
            data.compatInfo = compatInfo;
            data.initProfilerInfo = profilerInfo;
            sendMessage(H.BIND_APPLICATION, data);
                
           }

}

我们需要关注的其实就是最后的sendMessage()，里面有函数的编号H.BIND_APPLICATION，然后这个Messge会被H这个Handler处理

private class H extends Handler {
 
      ...ignore some code... 
 
     public static final int BIND_APPLICATION        = 110;
 
    ...ignore some code... 
 
     public void handleMessage(Message msg) {
          switch (msg.what) {
        ...ignore some code... 
         case BIND_APPLICATION:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "bindApplication");
                    AppBindData data = (AppBindData)msg.obj;
                    handleBindApplication(data);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
        ...ignore some code... 
        }
 }

最后就在下面这个方法中，完成了实例化，拨那个企鹅通过mInstrumentation.callApplicationOnCreate实现了onCreate()的调用。

private void handleBindApplication(AppBindData data) {
 
 try {
           
           ...ignore some code... 
           
            Application app = data.info.makeApplication(data.restrictedBackupMode, null);
            mInitialApplication = app;

           ...ignore some code... 
           
            try {
                mInstrumentation.onCreate(data.instrumentationArgs);
            }
            catch (Exception e) {
            }
            try {
                mInstrumentation.callApplicationOnCreate(app);
            } catch (Exception e) {            }
        } finally {
            StrictMode.setThreadPolicy(savedPolicy);
        }
 }

data.info是一个LoadeApk对象。
LoadeApk.data.info.makeApplication()

public Application makeApplication(boolean forceDefaultAppClass,
            Instrumentation instrumentation) {
        if (mApplication != null) {
            return mApplication;
        }

        Application app = null;

        String appClass = mApplicationInfo.className;
        if (forceDefaultAppClass || (appClass == null)) {
            appClass = "android.app.Application";
        }

        try {
            java.lang.ClassLoader cl = getClassLoader();
            if (!mPackageName.equals("android")) {
                initializeJavaContextClassLoader();
            }
            ContextImpl appContext = ContextImpl.createAppContext(mActivityThread, this);
            app = mActivityThread.mInstrumentation.newApplication(
                    cl, appClass, appContext);
            appContext.setOuterContext(app);
        } catch (Exception e) {        }
        mActivityThread.mAllApplications.add(app);
        mApplication = app;

    //传进来的是null，所以这里不会执行，onCreate在上一层执行
        if (instrumentation != null) {
            try {
                instrumentation.callApplicationOnCreate(app);
            } catch (Exception e) {
               
            }
        }
        ...ignore some code... 
              
       }

        return app;
    }

```
所以最后还是通过Instrumentation.makeApplication()实例化的，这个老板娘真的很厉害呀！
```java
static public Application newApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException, 
            ClassNotFoundException {
        Application app = (Application)clazz.newInstance();
        app.attach(context);
        return app;
    }
```
而且通过反射拿到Application对象之后，直接调用attach()，所以attach()调用是在onCreate()之前的。

## 参考文章

下面的这些文章都是这方面比较精品的，希望你抽出时间研究，这可能需要花费很长时间，但是如果你想进阶为中高级开发者，这一步是必须的。

再次感谢下面这些文章的作者的分享精神。

### Binder

[Android Bander设计与实现 - 设计篇](https://link.jianshu.com/?t=http://blog.csdn.net/universus/article/details/6211589)

### zygote

[Android系统进程Zygote启动过程的源代码分析](https://link.jianshu.com/?t=http://blog.csdn.net/luoshengyang/article/details/6768304)
[Android 之 zygote 与进程创建](https://link.jianshu.com/?t=http://blog.csdn.net/xieqibao/article/details/6581975)
[Zygote浅谈](https://link.jianshu.com/?t=http://www.th7.cn/Program/Android/201404/187670.shtml)

### ActivityThread、Instrumentation、AMS

[Android Activity.startActivity流程简介](https://link.jianshu.com/?t=http://blog.csdn.net/myarrow/article/details/14224273)
[Android应用程序进程启动过程的源代码分析](https://link.jianshu.com/?t=http://blog.csdn.net/luoshengyang/article/details/6747696#comments)
[框架层理解Activity生命周期(APP启动过程)](https://link.jianshu.com/?t=http://laokaddk.blog.51cto.com/368606/1206840)
[Android应用程序窗口设计框架介绍](https://link.jianshu.com/?t=http://blog.csdn.net/yangwen123/article/details/35987609)
[ActivityManagerService分析一：AMS的启动](https://link.jianshu.com/?t=http://www.xuebuyuan.com/2172927.html)
[Android应用程序窗口设计框架介绍](https://link.jianshu.com/?t=http://blog.csdn.net/yangwen123/article/details/35987609)

### Launcher

[Android 4.0 Launcher源码分析系列(一)](https://link.jianshu.com/?t=http://mobile.51cto.com/hot-312129.htm)
[Android Launcher分析和修改9——Launcher启动APP流程](https://link.jianshu.com/?t=http://www.cnblogs.com/mythou/p/3187881.html)

## 结语

OK，到这里，这篇文章算是告一段落了，我们再回头看看一开始的几个问题，你还困惑吗？

- 一个App是怎么启动起来的？
- App的程序入口到底是哪里？
- Launcher到底是什么神奇的东西？
- 听说还有个AMS的东西，它是做什么的？
- Binder是什么？他是如何进行IPC通信的？
- Activity生命周期到底是什么时候调用的？被谁调用的？

再回过头来看看这些类，你还迷惑吗？

- ActivityManagerServices，简称AMS，服务端对象，负责系统中所有Activity的生命周期
ActivityThread，App的真正入口。当开启App之后，会调用main()开始运行，开启消息循环队列，这就是传说中的UI线程或者叫主线程。与ActivityManagerServices配合，一起完成Activity的管理工作
- ApplicationThread，用来实现ActivityManagerService与ActivityThread之间的交互。在ActivityManagerService需要管理相关Application中的Activity的生命周期时，通过ApplicationThread的代理对象与ActivityThread通讯。
- ApplicationThreadProxy，是ApplicationThread在服务器端的代理，负责和客户端的ApplicationThread通讯。AMS就是通过该代理与ActivityThread进行通信的。
- Instrumentation，每一个应用程序只有一个Instrumentation对象，每个Activity内都有一个对该对象的引用。Instrumentation可以理解为应用进程的管家，ActivityThread要创建或暂停某个Activity时，都需要通过Instrumentation来进行具体的操作。
- ActivityStack，Activity在AMS的栈管理，用来记录已经启动的Activity的先后关系，状态信息等。通过ActivityStack决定是否需要启动新的进程。
- ActivityRecord，ActivityStack的管理对象，每个Activity在AMS对应一个ActivityRecord，来记录Activity的状态以及其他的管理信息。其实就是服务器端的Activity对象的映像。
- TaskRecord，AMS抽象出来的一个“任务”的概念，是记录ActivityRecord的栈，一个“Task”包含若干个ActivityRecord。AMS用TaskRecord确保Activity启动和退出的顺序。如果你清楚Activity的4种launchMode，那么对这个概念应该不陌生。
如果你还感到迷惑的话，就把这篇文章多读几遍吧，信息量可能比较多，需要慢慢消化~

作者：裸奔的凯子哥
链接：https://www.jianshu.com/p/6037f6fda285
來源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。