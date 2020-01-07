> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.zhihu.com/question/34652589 ![](https://pic1.zhimg.com/59e145e8b0d37770ef831324a77ba088_xs.jpg)Gityuan要完全彻底理解这个问题，需要准备以下 4 方面的知识：Process/Thread，Android Binder IPC，Handler/Looper/MessageQueue 消息机制，Linux pipe/epoll 机制。

**主线程的消息循环背后，一切皆是消息，消息机制和 Binder 是 Android 系统的两大核心机制，屏幕触摸消息、键盘消息、四大组件的启动等均是由消息驱动。**

**总结一下楼主主要有 3 个疑惑：**

1.Android 中为什么主线程不会因为 Looper.loop() 里的死循环卡死？

2. 没看见哪里有相关代码为这个死循环准备了一个新线程去运转？

3.Activity 的生命周期这些方法这些都是在主线程里执行的吧，那这些生命周期方法是怎么实现在死循环体外能够执行起来的？

--------------------------------------------------------------------------------------------------------------------------------------
针对这些疑惑，

@hi 大头鬼 hi@Rocko@陈昱全 大家回答都比较精炼，接下来我再更进一步详细地一一解答楼主的疑惑：

**(1) Android 中为什么主线程不会因为 Looper.loop() 里的死循环卡死？**

这里涉及线程，先说说说进程 / 线程，**进程：**每个 app 运行时前首先创建一个进程，该进程是由 Zygote fork 出来的，用于承载 App 上运行的各种 Activity/Service 等组件。进程对于上层应用来说是完全透明的，这也是 google 有意为之，让 App 程序都是运行在 Android Runtime。大多数情况一个 App 就运行在一个进程中，除非在 AndroidManifest.xml 中配置 Android:process 属性，或通过 native 代码 fork 进程。

**线程：**线程对应用来说非常常见，比如每次 new Thread().start 都会创建一个新的线程。该线程与 App 所在进程之间资源共享，从 Linux 角度来说进程与线程除了是否共享资源外，并没有本质的区别，都是一个 task_struct 结构体**，在 CPU 看来进程或线程无非就是一段可执行的代码，CPU 采用 CFS 调度算法，保证每个 task 都尽可能公平的享有 CPU 时间片**。

有了这么准备，再说说死循环问题：

对于线程既然是一段可执行的代码，当可执行代码执行完成后，线程生命周期便该终止了，线程退出。而对于主线程，我们是绝不希望会被运行一段时间，自己就退出，那么如何保证能一直存活呢？**简单做法就是可执行代码是能一直执行下去的，死循环便能保证不会被退出，**例如，binder 线程也是采用死循环的方法，通过循环方式不同与 Binder 驱动进行读写操作，当然并非简单地死循环，无消息时会休眠。但这里可能又引发了另一个问题，既然是死循环又如何去处理其他事务呢？通过创建新线程的方式。

真正会卡死主线程的操作是在回调方法 onCreate/onStart/onResume 等操作时间过长，会导致掉帧，甚至发生 ANR，looper.loop 本身不会导致应用卡死。

  

**(2) 没看见哪里有相关代码为这个死循环准备了一个新线程去运转？**

事实上，会在进入死循环之前便创建了新 binder 线程，在代码 ActivityThread.main() 中：  

```
public static void main(String[] args) {
        ....

        //创建Looper和MessageQueue对象，用于处理主线程的消息
        Looper.prepareMainLooper();

        //创建ActivityThread对象
        ActivityThread thread = new ActivityThread(); 

        //建立Binder通道 (创建新线程)
        thread.attach(false);

        Looper.loop(); //消息循环运行
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }

```

**thread.attach(false)；便会创建一个 Binder 线程（具体是指 ApplicationThread，Binder 的服务端，用于接收系统服务 AMS 发送来的事件），该 Binder 线程通过 Handler 将 Message 发送给主线程**，具体过程可查看 [startService 流程分析](https://link.zhihu.com/?target=http%3A//gityuan.com/2016/03/06/start-service/)，这里不展开说，简单说 Binder 用于进程间通信，采用 C/S 架构。关于 binder 感兴趣的朋友，可查看我回答的另一个知乎问题：  
[为什么 Android 要采用 Binder 作为 IPC 机制？ - Gityuan 的回答](https://www.zhihu.com/question/39440766/answer/89210950)

另外，**ActivityThread 实际上并非线程**，不像 HandlerThread 类，ActivityThread 并没有真正继承 Thread 类，只是往往运行在主线程，该人以线程的感觉，其实承载 ActivityThread 的主线程就是由 Zygote fork 而创建的进程。

**主线程的死循环一直运行是不是特别消耗 CPU 资源呢？** 其实不然，这里就涉及到 **Linux pipe/e****poll 机制**，简单说就是在主线程的 MessageQueue 没有消息时，便阻塞在 loop 的 queue.next() 中的 nativePollOnce() 方法里，详情见 [Android 消息机制 1-Handler(Java 层)](https://link.zhihu.com/?target=http%3A//www.yuanhh.com/2015/12/26/handler-message-framework/%23next)，此时主线程会释放 CPU 资源进入休眠状态，直到下个消息到达或者有事务发生，通过往 pipe 管道写端写入数据来唤醒主线程工作。这里采用的 epoll 机制，是一种 IO 多路复用机制，可以同时监控多个描述符，当某个描述符就绪 (读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步 I/O，即读写是阻塞的。 **所以说，主线程大多数时候都是处于休眠状态，并不会消耗大量 CPU 资源。**

  

**(3) Activity 的生命周期是怎么实现在死循环体外能够执行起来的？**

ActivityThread 的内部类 H 继承于 Handler，通过 handler 消息机制，简单说 Handler 机制用于同一个进程的线程间通信。

**Activity 的生命周期都是依靠主线程的 Looper.loop，当收到不同 Message 时则采用相应措施：**  
在 H.handleMessage(msg) 方法中，根据接收到不同的 msg，执行相应的生命周期。

比如收到 msg=H.LAUNCH_ACTIVITY，则调用 ActivityThread.handleLaunchActivity() 方法，最终会通过反射机制，创建 Activity 实例，然后再执行 Activity.onCreate() 等方法；  
再比如收到 msg=H.PAUSE_ACTIVITY，则调用 ActivityThread.handlePauseActivity() 方法，最终会执行 Activity.onPause() 等方法。 上述过程，我只挑核心逻辑讲，真正该过程远比这复杂。

**主线程的消息又是哪来的呢？**当然是 App 进程中的其他线程通过 Handler 发送给主线程，请看接下来的内容：

  

--------------------------------------------------------------------------------------------------------------------------------------
**最后，从进程与线程间通信的角度，****通过一张图****加深大家对 App 运行过程的理解：**  

![](https://pic4.zhimg.com/50/7fb8728164975ac86a2b0b886de2b872_hd.jpg)![](https://pic4.zhimg.com/7fb8728164975ac86a2b0b886de2b872_r.jpg)  
**system_server 进程是系统进程**，java framework 框架的核心载体，里面运行了大量的系统服务，比如这里提供 ApplicationThreadProxy（简称 ATP），ActivityManagerService（简称 AMS），这个两个服务都运行在 system_server 进程的不同线程中，由于 ATP 和 AMS 都是基于 IBinder 接口，都是 binder 线程，binder 线程的创建与销毁都是由 binder 驱动来决定的。

**App 进程则是我们常说的应用程序**，主线程主要负责 Activity/Service 等组件的生命周期以及 UI 相关操作都运行在这个线程； 另外，每个 App 进程中至少会有两个 binder 线程 ApplicationThread(简称 AT) 和 ActivityManagerProxy（简称 AMP），除了图中画的线程，其中还有很多线程，比如 signal catcher 线程等，这里就不一一列举。

Binder 用于不同进程之间通信，由一个进程的 Binder 客户端向另一个进程的服务端发送事务，比如图中线程 2 向线程 4 发送事务；而 handler 用于同一个进程中不同线程的通信，比如图中线程 4 向主线程发送消息。

**结合图说说 Activity 生命周期，比如暂停 Activity，流程如下：**  

1.  线程 1 的 AMS 中调用线程 2 的 ATP；（由于同一个进程的线程间资源共享，可以相互直接调用，但需要注意多线程并发问题）  
    
2.  线程 2 通过 binder 传输到 App 进程的线程 4；  
    
3.  线程 4 通过 handler 消息机制，将暂停 Activity 的消息发送给主线程；  
    
4.  主线程在 looper.loop() 中循环遍历消息，当收到暂停 Activity 的消息时，便将消息分发给 ActivityThread.H.handleMessage() 方法，再经过方法的调用，最后便会调用到 Activity.onPause()，当 onPause() 处理完后，继续循环 loop 下去。  
    

 
