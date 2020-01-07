> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/xyh269/article/details/52728861 版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/xyh269/article/details/52728861

Android 的 UI 访问是没有加锁的，这样在多个线程访问 UI 是不安全的。所以 Android 中规定只能在 UI 线程中访问 UI。

但是有没有极端的情况？使得我们在子线程中访问 UI 也可以使程序跑起来呢？接下来我们用一个例子去证实一下。

新建一个工程，activity_main.xml 布局如下所示：

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

    <TextView
        android:id="@+id/main_tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="18sp"
        android:layout_centerInParent="true"
        />

</RelativeLayout>


```

很简单，只是添加了一个居中的 TextView

MainActivity 代码如下所示：

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
public class MainActivity extends AppCompatActivity {

    private TextView main_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_tv = (TextView) findViewById(R.id.main_tv);

        new Thread(new Runnable() {

            @Override
            public void run() {
                main_tv.setText("子线程中访问");
            }
        }).start();

    }

}


```

也是很简单的几行，在 onCreate 方法中创建了一个子线程，并进行 UI 访问操作。

点击运行。你会发现即使在子线程中访问 UI，程序一样能跑起来。结果如下所示：  
![](https://img-blog.csdn.net/20161003174416786)

咦，那为嘛以前在子线程中更新 UI 会报错呢？难道真的可以在子线程中访问 UI？

先不急，这是一个极端的情况，修改 MainActivity 如下：

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
public class MainActivity extends AppCompatActivity {

    private TextView main_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_tv = (TextView) findViewById(R.id.main_tv);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                main_tv.setText("子线程中访问");
            }
        }).start();

    }

}


```

让子线程睡眠 200 毫秒，醒来后再进行 UI 访问。

结果你会发现，程序崩了。这才是正常的现象嘛。抛出了如下很熟悉的异常：

android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.  
at android.view.ViewRootImpl.checkThread(ViewRootImpl.java:6581)  
at android.view.ViewRootImpl.requestLayout(ViewRootImpl.java:924)

……

作为一名开发者，我们应该认真阅读一下这些异常信息，是可以根据这些异常信息来找到为什么一开始的那种情况可以访问 UI 的。那我们分析一下异常信息：

首先，从以下异常信息可以知道

```
1
at android.view.ViewRootImpl.checkThread(ViewRootImpl.java:6581)

```

这个异常是从 android.view.ViewRootImpl 的 checkThread 方法抛出的。

那现在跟进 ViewRootImpl 的 checkThread 方法瞧瞧，源码如下：

```
1
2
3
4
5
6
7
void checkThread() {
    if (mThread != Thread.currentThread()) {
        throw new CalledFromWrongThreadException(
                "Only the original thread that created a view hierarchy can touch its views.");
    }
}


```

只有那么几行代码而已的，而 mThread 是主线程，在应用程序启动的时候，就已经被初始化了。

由此我们可以得出结论：  
在访问 UI 的时候，ViewRootImpl 会去检查当前是哪个线程访问的 UI，如果不是主线程，那就会抛出如下异常：

```
1
Only the original thread that created a view hierarchy can touch its views

```

这好像并不能解释什么？继续看到异常信息

```
1
at android.view.ViewRootImpl.requestLayout(ViewRootImpl.java:924)

```

那现在就看看 requestLayout 方法，

```
1
2
3
4
5
6
7
8
9
@Override
public void requestLayout() {
    if (!mHandlingLayoutInLayoutRequest) {
        checkThread();
        mLayoutRequested = true;
        scheduleTraversals();
    }
}


```

这里也是调用了 checkThread() 方法来检查当前线程，咦？除了检查线程好像没有什么信息。那再点进 scheduleTraversals() 方法看看

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
void scheduleTraversals() {
    if (!mTraversalScheduled) {
        mTraversalScheduled = true;
        mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();
        mChoreographer.postCallback(
                Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
        if (!mUnbufferedInputDispatch) {
            scheduleConsumeBatchedInput();
        }
        notifyRendererOfFramePending();
        pokeDrawLockIfNeeded();
    }
}


```

注意到 postCallback 方法的的第二个参数传入了很像是一个后台任务。那再点进去

```
1
2
3
4
5
6
7
final class TraversalRunnable implements Runnable {
    @Override
    public void run() {
        doTraversal();
    }
}


```

找到了，那么继续跟进 doTraversal() 方法。

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
void doTraversal() {
    if (mTraversalScheduled) {
        mTraversalScheduled = false;
        mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);

        if (mProfile) {
            Debug.startMethodTracing("ViewAncestor");
        }

        performTraversals();

        if (mProfile) {
            Debug.stopMethodTracing();
            mProfile = false;
        }
    }
}


```

可以看到里面调用了一个 performTraversals() 方法，View 的绘制过程就是从这个 performTraversals 方法开始的。PerformTraversals 方法的代码有点长就不贴出来了，如果继续跟进去就是学习 View 的绘制了。而我们现在知道了，每一次访问了 UI，Android 都会重新绘制 View。这个是很好理解的。

分析到了这里，其实异常信息对我们帮助也不大了，它只告诉了我们子线程中访问 UI 在哪里抛出异常。  
而我们会思考：当访问 UI 时，ViewRootImpl 会调用 checkThread 方法去检查当前访问 UI 的线程是哪个，如果不是 UI 线程则会抛出异常，这是没问题的。但是为什么一开始在 MainActivity 的 onCreate 方法中创建一个子线程访问 UI，程序还是正常能跑起来呢？？  
唯一的解释就是执行 onCreate 方法的那个时候 ViewRootImpl 还没创建，无法去检查当前线程。

那么就可以这样深入进去。寻找 ViewRootImpl 是在哪里，是什么时候创建的。好，继续前进

在 ActivityThread 中，我们找到 handleResumeActivity 方法，如下：

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
final void handleResumeActivity(IBinder token,
        boolean clearHide, boolean isForward, boolean reallyResume) {
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    unscheduleGcIdler();
    mSomeActivitiesChanged = true;

    // TODO Push resumeArgs into the activity for consideration
    ActivityClientRecord r = performResumeActivity(token, clearHide);

    if (r != null) {
        final Activity a = r.activity;

        //代码省略

            r.activity.mVisibleFromServer = true;
            mNumVisibleActivities++;
            if (r.activity.mVisibleFromClient) {
                r.activity.makeVisible();
            }
        }

      //代码省略    
}


```

可以看到内部调用了 performResumeActivity 方法，这个方法看名字肯定是回调 onResume 方法的入口的，那么我们还是跟进去瞧瞧。

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
public final ActivityClientRecord performResumeActivity(IBinder token,
        boolean clearHide) {
    ActivityClientRecord r = mActivities.get(token);
    if (localLOGV) Slog.v(TAG, "Performing resume of " + r
            + " finished=" + r.activity.mFinished);
    if (r != null && !r.activity.mFinished) {
    //代码省略
            r.activity.performResume();

    //代码省略

    return r;
}


```

可以看到 r.activity.performResume() 这行代码，跟进 performResume 方法，如下：

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
final void performResume() {
    performRestart();

    mFragments.execPendingActions();

    mLastNonConfigurationInstances = null;

    mCalled = false;
    // mResumed is set by the instrumentation
    mInstrumentation.callActivityOnResume(this);

    //代码省略

}


```

Instrumentation 调用了 callActivityOnResume 方法，callActivityOnResume 源码如下：

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
public void callActivityOnResume(Activity activity) {
    activity.mResumed = true;
    activity.onResume();

    if (mActivityMonitors != null) {
        synchronized (mSync) {
            final int N = mActivityMonitors.size();
            for (int i=0; i<N; i++) {
                final ActivityMonitor am = mActivityMonitors.get(i);
                am.match(activity, activity, activity.getIntent());
            }
        }
    }
}


```

找到了，activity.onResume()。这也证实了，performResumeActivity 方法确实是回调 onResume 方法的入口。

那么现在我们看回来 handleResumeActivity 方法，执行完 performResumeActivity 方法回调了 onResume 方法后，  
会来到这一块代码：

```
1
2
3
4
5
6
r.activity.mVisibleFromServer = true;
mNumVisibleActivities++;
if (r.activity.mVisibleFromClient) {
    r.activity.makeVisible();
}


```

activity 调用了 makeVisible 方法，这应该是让什么显示的吧，跟进去探探。

```
1
2
3
4
5
6
7
8
9
void makeVisible() {
    if (!mWindowAdded) {
        ViewManager wm = getWindowManager();
        wm.addView(mDecor, getWindow().getAttributes());
        mWindowAdded = true;
    }
    mDecor.setVisibility(View.VISIBLE);
}


```

往 WindowManager 中添加 DecorView，那现在应该关注的就是 WindowManager 的 addView 方法了。而 WindowManager 是一个接口来的，我们应该找到 WindowManager 的实现类才行，而 WindowManager 的实现类是 WindowManagerImpl。

找到了 WindowManagerImpl 的 addView 方法，如下：

```
1
2
3
4
5
6
@Override
public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
    applyDefaultToken(params);
    mGlobal.addView(view, params, mDisplay, mParentWindow);
}


```

里面调用了 WindowManagerGlobal 的 addView 方法，那现在就锁定  
WindowManagerGlobal 的 addView 方法：

```
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
public void addView(View view, ViewGroup.LayoutParams params,
        Display display, Window parentWindow) {

    //代码省略  


    ViewRootImpl root;
    View panelParentView = null;

    //代码省略

        root = new ViewRootImpl(view.getContext(), display);

        view.setLayoutParams(wparams);

        mViews.add(view);
        mRoots.add(root);
        mParams.add(wparams);
    }

    // do this last because it fires off messages to start doing things
    try {
        root.setView(view, wparams, panelParentView);
    } catch (RuntimeException e) {
        // BadTokenException or InvalidDisplayException, clean up.
        synchronized (mLock) {
            final int index = findViewLocked(view, false);
            if (index >= 0) {
                removeViewLocked(index, true);
            }
        }
        throw e;
    }
}


```

终于击破，ViewRootImpl 是在 WindowManagerGlobal 的 addView 方法中创建的。

回顾前面的分析，总结一下：  
ViewRootImpl 的创建在 onResume 方法回调之后，而我们一开篇是在 onCreate 方法中创建了子线程并访问 UI，在那个时刻，ViewRootImpl 是没有创建的，无法检测当前线程是否是 UI 线程，所以程序没有崩溃一样能跑起来，而之后修改了程序，让线程休眠了 200 毫秒后，程序就崩了。很明显 200 毫秒后 ViewRootImpl 已经创建了，可以执行 checkThread 方法检查当前线程。

这篇博客的分析如题目一样，Android 中子线程真的不能更新 UI 吗？在 onCreate 方法中创建的子线程访问 UI 是一种极端的情况，这个不仔细分析源码是不知道的。我是最近看了一个面试题，才发现这个。

从中我也学习到了从异常信息中跟进源码寻找答案，你呢？