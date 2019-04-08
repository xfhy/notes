> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5c74b64a6fb9a049be5e22fc?utm_source=gold_browser_extension

<a></a>

## 0\. 前言

做 Android 开发肯定离不开跟 Handler 打交道，它通常被我们用来做主线程与子线程之间的通信工具，而 Handler 作为 Android 中消息机制的重要一员也确实给我们的开发带来了极大的便利。

可以说**只要有异步线程与主线程通信的地方就一定会有 Handler**。

那么，Handler 的通信机制的背后的原理是什么？

本文带你揭晓。

**注意：本文所展示的系统源码基于 Android-27 ，并有所删减。**

<a></a>

## 1\. 重识 Handler

我们可以使用 Handler **发送并处理**与一个线程关联的 Message 和 Runnable 。（注意：**Runnable 会被封装进一个 Message，所以它本质上还是一个 Message** ）

每个 Handler 都会跟一个线程绑定，并与该线程的 MessageQueue 关联在一起，从而实现消息的管理以及线程间通信。

<a></a>

### 1.1 Handler 的基本用法

```
android.os.Handler handler = new Handler(){
  @Override
  public void handleMessage(final Message msg) {
    //这里接受并处理消息
  }
};
//发送消息
handler.sendMessage(message);
handler.post(runnable);
复制代码
```

实例化一个 Handler 重写 `handleMessage` 方法 ，然后在需要的时候调用它的 `send` 以及 `post` **系列方法**就可以了，非常简单易用，并且支持延时消息。（更多方法可查询 API 文档）

但是奇怪，**我们并没有看到任何 MessageQueue 的身影，也没看到它与线程绑定的逻辑，这是怎么回事**？

<a></a>

## 2\. Handler 原理解析

相信大家早就听说过了 Looper 以及 MessageQueue 了，我就不多绕弯子了。

不过在开始分析原理之前，先**明确我们的问题**：

1.  **Handler 是如何与线程关联的？**
2.  **Handler 发出去的消息是谁管理的？**
3.  **消息又是怎么回到 handleMessage() 方法的？**
4.  **线程的切换是怎么回事？**

<a></a>

### 2.1 Handler 与 Looper 的关联

实际上我们在实例化 Handler 的时候 Handler 会去检查当前线程的 Looper 是否存在，如果不存在则会报异常，也就是说**在创建 Handler 之前一定需要先创建 Looper** 。

代码如下：

```
public Handler(Callback callback, boolean async) {
        //检查当前的线程是否有 Looper
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException(
                "Can't create handler inside thread that has not called Looper.prepare()");
        }
        //Looper 持有一个 MessageQueue
        mQueue = mLooper.mQueue;
}
复制代码
```

这个异常相信很多同学遇到过，而我们平时直接使用感受不到这个异常是因为主线程已经为我们创建好了 Looper，先记住，后面会讲。（见【3.2】）

一个完整的 Handler 使用例子其实是这样的：

```
class LooperThread extends Thread {
    public Handler mHandler;
    public void run() {
        Looper.prepare();
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // process incoming messages here
            }
        };
        Looper.loop();
    }
}
复制代码
```

Looper.prepare() :

```
//Looper
private static void prepare(boolean quitAllowed) {
  if (sThreadLocal.get() != null) {
    throw new RuntimeException("Only one Looper may be created per thread");
  }
  sThreadLocal.set(new Looper(quitAllowed));
}
复制代码
```

Looper 提供了 `Looper.prepare()`  方法来创建 Looper ，并且会**借助 ThreadLocal 来实现与当前线程的绑定**功能。**Looper.loop() 则会开始不断尝试从 MessageQueue 中获取 Message , 并分发给对应的 Handler（见【2.3】）**。

**也就是说 Handler 跟线程的关联是靠 Looper 来实现的。**

<a></a>

### 2.2 Message 的存储与管理

Handler 提供了一些列的方法让我们来发送消息，如 send() 系列 post() 系列 。

不过不管我们调用什么方法，最终都会走到 `MessageQueue.enqueueMessage(Message,long)` 方法。

以 `sendEmptyMessage(int)`  方法为例：

```
//Handler
sendEmptyMessage(int)
  -> sendEmptyMessageDelayed(int,int)
    -> sendMessageAtTime(Message,long)
      -> enqueueMessage(MessageQueue,Message,long)
  			-> queue.enqueueMessage(Message, long);
复制代码
```

**到了这里，消息的管理者 MessageQueue 也就露出了水面**。
MessageQueue 顾明思议，就是个队列，负责消息的入队出队。

<a></a>

### 2.3 Message 的分发与处理

了解清楚 Message 的发送与存储管理后，就该揭开分发与处理的面纱了。

前面说到了 `Looper.loop()`  负责对消息的分发，本章节进行分析。

先来看看所涉及到的方法：

```
//Looper
public static void loop() {
    final Looper me = myLooper();
    if (me == null) {
        throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
    }
    final MessageQueue queue = me.mQueue;
    //...
    for (;;) {
       // 不断从 MessageQueue 获取 消息
        Message msg = queue.next(); // might block
        //退出 Looper 
        if (msg == null) {
            // No message indicates that the message queue is quitting.
            return;
        }
        //...
        try {
            msg.target.dispatchMessage(msg);
            end = (slowDispatchThresholdMs == 0) ? 0 : SystemClock.uptimeMillis();
        } finally {
            //...
        }
        //...
				//回收 message, 见【3.5】
        msg.recycleUnchecked();
    }
}
复制代码
```

`loop()` 里调用了 `MessageQueue.next()` :

```
//MessageQueue
Message next() {
    //...
    for (;;) {
        //...
        nativePollOnce(ptr, nextPollTimeoutMillis);

        synchronized (this) {
            // Try to retrieve the next message.  Return if found.
            final long now = SystemClock.uptimeMillis();
            Message prevMsg = null;
            Message msg = mMessages;
            //...
            if (msg != null) {
                if (now < msg.when) {
                    // Next message is not ready.  Set a timeout to wake up when it is ready.
                    nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                } else {
                    // Got a message.
                    mBlocked = false;
                    if (prevMsg != null) {
                        prevMsg.next = msg.next;
                    } else {
                        mMessages = msg.next;
                    }
                    msg.next = null;
                    return msg;
                }
            } else {
                // No more messages.
                nextPollTimeoutMillis = -1;
            }

            // Process the quit message now that all pending messages have been handled.
            if (mQuitting) {
                dispose();
                return null;
            }
        }

        // Run the idle handlers. 关于 IdleHandler 自行了解
        //...
    }
}
复制代码
```

还调用了 `msg.target.dispatchMessage(msg)` ，msg.target 就是发送该消息的 Handler，这样就回调到了 Handler 那边去了:

```
//Handler
public void dispatchMessage(Message msg) {
  //msg.callback 是 Runnable ，如果是 post方法则会走这个 if
  if (msg.callback != null) {
    handleCallback(msg);
  } else {
    //callback 见【3.4】
    if (mCallback != null) {
      if (mCallback.handleMessage(msg)) {
        return;
      }
    }
    //回调到 Handler 的 handleMessage 方法
    handleMessage(msg);
  }
}
复制代码
```

**注意：dispatchMessage() 方法针对 Runnable 的方法做了特殊处理，如果是 ，则会直接执行 `Runnable.run()` 。**

**分析：**Looper.loop() 是个死循环，会**不断调用 MessageQueue.next() 获取 Message ，并调用 `msg.target.dispatchMessage(msg)` 回到了 Handler 来分发消息，以此来完成消息的回调**。

**注意：loop() 方法并不会卡死主线程，见【6】。**

那么**线程的切换又是怎么回事**呢？
很多人搞不懂这个原理，但是其实非常简单，我们将所涉及的方法调用栈画出来，如下：

```
Thread.foo(){
	Looper.loop()
	 -> MessageQueue.next()
 	  -> Message.target.dispatchMessage()
 	   -> Handler.handleMessage()
}
复制代码
```

**显而易见，Handler.handleMessage() 所在的线程最终由调用 Looper.loop() 的线程所决定。**

平时我们用的时候从异步线程发送消息到 Handler，这个 Handler 的 `handleMessage()` 方法是在主线程调用的，所以消息就从异步线程切换到了主线程。

<a></a>

### 2.3 图解原理

文字版的原理解析到这里就结束了，如果你看到这里还是没有懂，没关系，我特意给你们准备了些图，配合着前面几个章节，再多看几遍，一定可以吃透。

![](https://user-gold-cdn.xitu.io/2019/2/26/16927e6098cf257b?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)![](https://user-gold-cdn.xitu.io/2019/2/26/16927e6099e1d48c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)
图片来源见【6】

<a></a>

### 2.4 小结

Handler 的背后有着 Looper 以及 MessageQueue 的协助，三者通力合作，分工明确。

尝试小结一下它们的职责，如下：

*   Looper ：**负责关联线程以及消息的分发**在该线程下 ** 从 MessageQueue 获取 Message，分发给 Handler ；
*   MessageQueue ：**是个队列，负责消息的存储与管理**，负责管理由 Handler 发送过来的 Message ；
*   Handler : **负责发送并处理消息**，面向开发者，提供 API，并隐藏背后实现的细节。

对【2】章节提出的问题用一句话总结：

**Handler 发送的消息由 MessageQueue 存储管理，并由 Loopler 负责回调消息到 handleMessage()。**

**线程的转换由 Looper 完成，handleMessage() 所在线程由 Looper.loop() 调用者所在线程决定。**

<a></a>

## 3\. Handler 的延伸

Handler 虽然简单易用，但是要用好它还是需要注意一点，另外 Handler 相关 还有些鲜为人知的知识技巧，比如 IdleHandler。

由于 Handler 的特性，它在 Android 里的应用非常广泛，比如： AsyncTask、HandlerThread、Messenger、IdleHandler 和 IntentService 等等。

这些我会讲解一些，我没讲到的可以自行搜索相关内容进行了解。

<a></a>

### 3.1 Handler 引起的内存泄露原因以及最佳解决方案

Handler 允许我们发送**延时消息**，如果在延时期间用户关闭了 Activity，那么该 Activity 会泄露。

这个泄露是因为 Message 会持有 Handler，而又因为 **Java 的特性，内部类会持有外部类**，使得 Activity 会被 Handler 持有，这样最终就导致 Activity 泄露。

解决该问题的最有效的方法是：**将 Handler 定义成静态的内部类，在内部持有 Activity 的弱引用，并及时移除所有消息**。

示例代码如下：

```
private static class SafeHandler extends Handler {

    private WeakReference<HandlerActivity> ref;

    public SafeHandler(HandlerActivity activity) {
        this.ref = new WeakReference(activity);
    }

    @Override
    public void handleMessage(final Message msg) {
        HandlerActivity activity = ref.get();
        if (activity != null) {
            activity.handleMessage(msg);
        }
    }
}
复制代码
```

并且再在 `Activity.onDestroy()` 前移除消息，加一层保障：

```
@Override
protected void onDestroy() {
  safeHandler.removeCallbacksAndMessages(null);
  super.onDestroy();
}
复制代码
```

这样双重保障，就能完全避免内存泄露了。

**注意：单纯的在 `onDestroy` 移除消息并不保险，因为 `onDestroy` 并不一定执行。**

<a></a>

### 3.2 为什么我们能在主线程直接使用 Handler，而不需要创建 Looper ？

前面我们提到了每个 Handler 的线程都有一个 Looper ，主线程当然也不例外，但是我们不曾准备过主线程的 Looper 而可以直接使用，这是为何？

**注意：通常我们认为 ActivityThread 就是主线程。事实上它并不是一个线程，而是主线程操作的管理者，所以吧，我觉得把 ActivityThread 认为就是主线程无可厚非，另外主线程也可以说成 UI 线程。**

在 ActivityThread.main() 方法中有如下代码：

```
//android.app.ActivityThread
public static void main(String[] args) {
  //...
  Looper.prepareMainLooper();

  ActivityThread thread = new ActivityThread();
  thread.attach(false);

  if (sMainThreadHandler == null) {
    sMainThreadHandler = thread.getHandler();
  }
  //...
  Looper.loop();

  throw new RuntimeException("Main thread loop unexpectedly exited");
}
复制代码
```

Looper.prepareMainLooper(); 代码如下：

```
/**
 * Initialize the current thread as a looper, marking it as an
 * application's main looper. The main looper for your application
 * is created by the Android environment, so you should never need
 * to call this function yourself.  See also: {@link #prepare()}
 */
public static void prepareMainLooper() {
    prepare(false);
    synchronized (Looper.class) {
        if (sMainLooper != null) {
            throw new IllegalStateException("The main Looper has already been prepared.");
        }
        sMainLooper = myLooper();
    }
}
复制代码
```

可以看到**在 ActivityThread 里 调用了 Looper.prepareMainLooper() 方法创建了 主线程的 Looper , 并且调用了 loop() 方法**，所以我们就可以直接使用 Handler 了。

**注意：`Looper.loop()` 是个死循环，后面的代码正常情况不会执行。**

<a></a>

### 3.3 主线程的 Looper 不允许退出

如果你尝试退出 Looper ，你会得到以下错误信息：

```
Caused by: java.lang.IllegalStateException: Main thread not allowed to quit.
  at android.os.MessageQueue.quit(MessageQueue.java:415)
  at android.os.Looper.quit(Looper.java:240)
复制代码
```

why? 其实原因很简单，**主线程不允许退出**，退出就意味 APP 要挂。

<a></a>

### 3.4 Handler 里藏着的 Callback 能干什么？

在 Handler 的构造方法中有几个 要求传入 Callback ，那它是什么，又能做什么呢？

来看看 `Handler.dispatchMessage(msg)`  方法：

```
public void dispatchMessage(Message msg) {
  //这里的 callback 是 Runnable
  if (msg.callback != null) {
    handleCallback(msg);
  } else {
    //如果 callback 处理了该 msg 并且返回 true， 就不会再回调 handleMessage
    if (mCallback != null) {
      if (mCallback.handleMessage(msg)) {
        return;
      }
    }
    handleMessage(msg);
  }
}
复制代码
```

可以看到 Handler.Callback 有**优先处理消息的权利** ，当一条消息被 Callback 处理**并拦截（返回 true）**，那么 Handler 的 `handleMessage(msg)` 方法就不会被调用了；如果 Callback 处理了消息，但是并没有拦截，那么就意味着**一个消息可以同时被 Callback 以及 Handler 处理**。

这个就很有意思了，这有什么作用呢？

**我们可以利用 Callback 这个拦截机制来拦截 Handler 的消息！**

场景：Hook [ActivityThread.mH](https://link.juejin.im?target=http%3A%2F%2FActivityThread.mH) ， 在 ActivityThread 中有个成员变量 `mH` ，它是个 Handler，又是个极其重要的类，几乎所有的插件化框架都使用了这个方法。

<a></a>

### 3.5 创建 Message 实例的最佳方式

由于 Handler 极为常用，所以为了节省开销，Android 给 Message 设计了回收机制，所以我们在使用的时候尽量复用 Message ，减少内存消耗。

方法有二：

1.  通过 Message 的静态方法 `Message.obtain();`   获取；
2.  通过 Handler 的公有方法 `handler.obtainMessage();` 。

<a></a>

### 3.6 子线程里弹 Toast 的正确姿势

当我们尝试在子线程里直接去弹 Toast 的时候，会 crash ：

```
java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
复制代码
```

**本质上是因为 Toast 的实现依赖于 Handler**，按子线程使用 Handler 的要求修改即可（见【2.1】），同理的还有 Dialog。

正确示例代码如下：

```
new Thread(new Runnable() {
  @Override
  public void run() {
    Looper.prepare();
    Toast.makeText(HandlerActivity.this, "不会崩溃啦！", Toast.LENGTH_SHORT).show();
    Looper.loop();
  }
}).start();
复制代码
```

<a></a>

### 3.7 妙用 Looper 机制

我们可以利用 Looper 的机制来帮助我们做一些事情：

1.  将 Runnable post 到主线程执行；
2.  利用 Looper 判断当前线程是否是主线程。

完整示例代码如下：

```
public final class MainThread {

    private MainThread() {
    }

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void run(@NonNull Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        }else{
            HANDLER.post(runnable);
        }
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

}
复制代码
```

能够省去不少样板代码。

<a></a>

## 4\. 知识点汇总

由前文可得出一些知识点，汇总一下，方便记忆。

1.  Handler 的背后有 Looper、MessageQueue 支撑，Looper 负责消息分发，MessageQueue 负责消息管理；
2.  在创建 Handler 之前一定需要先创建 Looper；
3.  Looper 有退出的功能，但是主线程的 Looper 不允许退出；
4.  异步线程的 Looper 需要自己调用 `Looper.myLooper().quit();` 退出；
5.  Runnable 被封装进了 Message，可以说是一个特殊的 Message；
6.  `Handler.handleMessage()` 所在的线程是 Looper.loop() 方法被调用的线程，也可以说成 Looper 所在的线程，并不是创建 Handler 的线程；
7.  使用内部类的方式使用 Handler 可能会导致内存泄露，即便在 Activity.onDestroy 里移除延时消息，必须要写成静态内部类；

<a></a>

## 5\. 总结

Handler 简单易用的背后藏着工程师大量的智慧，要努力向他们学习。

看完并理解本文可以说你对 Handler 有了一个非常深入且全面的了解，应对面试肯定是绰绰有余了。

<a></a>

![](https://user-gold-cdn.xitu.io/2019/2/26/16927e9e50008ba7?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

## 6\. 参考和推荐

[Handler](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Freference%2Fandroid%2Fos%2FHandler)
[what-is-the-relationship-between-looper-handler-and-messagequeue-in-android](https://link.juejin.im?target=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F12877944%2Fwhat-is-the-relationship-between-looper-handler-and-messagequeue-in-android)
[Android 消息机制 1-Handler（Java 层）](https://link.juejin.im?target=http%3A%2F%2Fgityuan.com%2F2015%2F12%2F26%2Fhandler-message-framework%2F)
[Android 中为什么主线程不会卡死](https://link.juejin.im?target=https%3A%2F%2Fwww.zhihu.com%2Fquestion%2F34652589%2Fanswer%2F90344494)