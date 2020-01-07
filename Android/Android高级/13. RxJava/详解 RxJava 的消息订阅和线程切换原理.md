> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5b1fbd796fb9a01e8c5fd847

> 本文由[`玉刚说写作平台`](https://link.juejin.im?target=http%3A%2F%2Frenyugang.io%2Fpost%2F75)提供写作赞助
> 
> 原作者：[`四月葡萄`](https://link.juejin.im?target=https%3A%2F%2Fblog.csdn.net%2Fu011810352 "点击跳转")
> 
> 版权声明：本文版权归微信公众号 **玉刚说** 所有，未经许可，不得以任何形式转载

1. 前言
-----

本文主要是对 RxJava 的消息订阅和线程切换进行源码分析，相关的使用方式等不作详细介绍。

本文源码基于`rxjava:2.1.14`。

2. RxJava 简介
------------

> RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences.
> 
> It extends the observer pattern to support sequences of data/events and adds operators that allow you to compose sequences together declaratively while abstracting away concerns about things like low-level threading, synchronization, thread-safety and concurrent data structures.

上面这段话来自于 RxJava 在 github 上面的官方介绍。翻译成中文的大概意思就是：

> RxJava 是一个在 Java 虚拟机上的响应式扩展，通过使用可观察的序列将异步和基于事件的程序组合起来的一个库。
> 
> 它扩展了观察者模式来支持数据 / 事件序列，并且添加了操作符，这些操作符允许你声明性地组合序列，同时抽象出要关注的问题：比如低级线程、同步、线程安全和并发数据结构等。

简单点来说， RxJava 就是一个使用了观察者模式，能够异步的库。

3. 观察者模式
--------

上面说到，RxJava 扩展了观察者模式，那么什么是观察模式呢？我们先来了解一下。

举个例子，以微信公众号为例，一个微信公众号会不断产生新的内容，如果我们读者对这个微信公众号的内容感兴趣，就会订阅这个公众号，当公众号有新内容时，就会推送给我们。我们收到新内容时，如果是我们感兴趣的，就会点进去看下; 如果是广告的话，就可能直接忽略掉。这就是我们生活中遇到的典型的观察者模式。

在上面的例子中，微信公众号就是一个被观察者 (`Observable`)，不断的产生内容（事件），而我们读者就是一个观察者 (`Observer`) ，通过订阅（`subscribe`）就能够接受到微信公众号（被观察者）推送的内容（事件），根据不同的内容（事件）做出不同的操作。

### 3.1 Rxjava 角色说明

RxJava 的扩展观察者模式中就是存在这么 4 种角色：

| 角色 | 角色功能 |
| --- | --- |
| 被观察者（`Observable`） | 产生事件 |
| 观察者（`Observer`） | 响应事件并做出处理 |
| 事件（`Event`） | 被观察者和观察者的消息载体 |
| 订阅（`Subscribe`） | 连接被观察者和观察者 |

### 3.2 RxJava 事件类型

RxJava 中的事件分为三种类型：`Next`事件、`Complete`事件和`Error`事件。具体如下：

| 事件类型 | 含义 | 说明 |
| --- | --- | --- |
| `Next` | 常规事件 | 被观察者可以发送无数个 Next 事件，观察者也可以接受无数个 Next 事件 |
| `Complete` | 结束事件 | 被观察者发送 Complete 事件后可以继续发送事件，观察者收到 Complete 事件后将不会接受其他任何事件 |
| `Error` | 异常事件 | 被观察者发送 Error 事件后，其他事件将被终止发送，观察者收到 Error 事件后将不会接受其他任何事件 |

4.RxJava 的消息订阅
--------------

在分析 RxJava 消息订阅原理前，我们还是先来看下它的简单使用步骤。这里为了方便讲解，就不用链式代码来举例了，而是采用分步骤的方式来逐一说明（平时写代码的话还是建议使用链式代码来调用，因为更加简洁）。其使用步骤如下：

> 1.  创建被观察者 (`Observable`), 定义要发送的事件。
> 2.  创建观察者 (`Observer`)，接受事件并做出响应操作。
> 3.  观察者通过订阅（`subscribe`）被观察者把它们连接到一起。

### 4.1 RxJava 的消息订阅例子

这里我们就根据上面的步骤来实现这个例子，如下：

```
        //步骤1. 创建被观察者(Observable),定义要发送的事件。
        Observable observable = Observable.create(
        new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter)
            throws Exception {
                emitter.onNext("文章1");
                emitter.onNext("文章2");
                emitter.onNext("文章3");
                emitter.onComplete();
            }
        });
       
        //步骤2. 创建观察者(Observer)，接受事件并做出响应操作。
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext : " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError : " + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
       
        //步骤3. 观察者通过订阅（subscribe）被观察者把它们连接到一起。
        observable.subscribe(observer);
复制代码

```

其输出结果为：

```
onSubscribe
onNext : 文章1
onNext : 文章2
onNext : 文章3
onComplete
复制代码

```

### 4.2 源码分析

下面我们对消息订阅过程中的源码进行分析，分为两部分：创建被观察者过程和订阅过程。

#### 4.2.1 创建被观察者过程

首先来看下创建被观察者 (`Observable`) 的过程，上面的例子中我们是直接使用`Observable.create()`来创建`Observable`，我们点进去这个方法看下。

##### 4.2.1.1 Observable 类的 create()

```
    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        ObjectHelper.requireNonNull(source, "source is null");
        return RxJavaPlugins.onAssembly(new ObservableCreate<T>(source));
    }
复制代码

```

可以看到，`create()`方法中也没做什么，就是创建一个`ObservableCreate`对象出来，然后把我们自定义的`ObservableOnSubscribe`作为参数传到`ObservableCreate`中去，最后就是调用 `RxJavaPlugins.onAssembly()`方法。

我们先来看看`ObservableCreate`类：

##### 4.2.1.2 ObservableCreate 类

```
public final class ObservableCreate<T> extends Observable<T> {//继承自Observable
    public ObservableCreate(ObservableOnSubscribe<T> source) {
        this.source = source;//把我们创建的ObservableOnSubscribe对象赋值给source。
    }
}
复制代码

```

可以看到，`ObservableCreate`是继承自`Observable`的，并且会把`ObservableOnSubscribe`对象给存起来。

再看下`RxJavaPlugins.onAssembly()`方法

##### 4.2.1.3 RxJavaPlugins 类的 onAssembly()

```
    public static <T> Observable<T> onAssembly(@NonNull Observable<T> source) {
        //省略无关代码
        return source;
    }
复制代码

```

很简单，就是把上面创建的`ObservableCreate`给返回。

##### 4.2.1.4 简单总结

所以`Observable.create()`中就是把我们自定义的`ObservableOnSubscribe`对象重新包装成一个`ObservableCreate`对象，然后返回这个`ObservableCreate`对象。 注意，这种重新包装新对象的用法在 RxJava 中会频繁用到，后面的分析中我们还会多次遇到。 放个图好理解，包起来哈～

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc55f017ac4?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

##### 4.2.1.5 时序图

`Observable.create()`的时序图如下所示：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc55ef0d5ef?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 4.2.2 订阅过程

接下来我们就看下订阅过程的代码，同样，点进去`Observable.subscribe()`：

##### 4.2.2.1 Observable 类的 subscribe()

```
    public final void subscribe(Observer<? super T> observer) {
            //省略无关代码
           
            observer = RxJavaPlugins.onSubscribe(this, observer);

            subscribeActual(observer);
           
            //省略无关代码
    }
复制代码

```

可以看到，实际上其核心的代码也就两句，我们分开来看下：

##### 4.2.2.2 RxJavaPlugins 类的 onSubscribe()

```
    public static <T> Observer<? super T> onSubscribe(
    @NonNull Observable<T> source, @NonNull Observer<? super T> observer) {
        //省略无关代码
       
        return observer;
    }
复制代码

```

跟之前代码一样，这里同样也是把原来的`observer`返回而已。 再来看下`subscribeActual()`方法。

##### 4.2.2.3 Observable 类的 subscribeActual()

```
    protected abstract void subscribeActual(Observer<? super T> observer);
复制代码

```

`Observable`类的`subscribeActual()`中的方法是一个抽象方法，那么其具体实现在哪呢？还记得我们前面创建被观察者的过程吗，最终会返回一个`ObservableCreate`对象，这个`ObservableCreate`就是`Observable`的子类，我们点进去看下：

##### 4.2.2.4 ObservableCreate 类的 subscribeActual()

```
    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        CreateEmitter<T> parent = new CreateEmitter<T>(observer);
        //触发我们自定义的Observer的onSubscribe(Disposable)方法
        observer.onSubscribe(parent);

        try {
            source.subscribe(parent);
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            parent.onError(ex);
        }
    }
复制代码

```

可以看到，`subscribeActual()`方法中首先会创建一个`CreateEmitter`对象，然后把我们自定义的观察者`observer`作为参数给传进去。这里同样也是包装起来，放个图：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc55edb6124?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

这个

`CreateEmitter`

实现了

`ObservableEmitter`

接口和

`Disposable`

接口，如下：

```
    static final class CreateEmitter<T>
    extends AtomicReference<Disposable>
    implements ObservableEmitter<T>, Disposable {
        //代码省略
    }
复制代码

```

然后就是调用了`observer.onSubscribe(parent)`，实际上就是调用观察者的`onSubscribe()`方法，即告诉观察者已经成功订阅到了被观察者。

继续往下看，`subscribeActual()`方法中会继续调用`source.subscribe(parent)`，这里的`source`就是`ObservableOnSubscribe`对象，即这里会调用`ObservableOnSubscribe`的`subscribe()`方法。 我们具体定义的`subscribe()`方法如下：

```
        Observable observable = Observable.create(
        new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter)
            throws Exception {
                emitter.onNext("文章1");
                emitter.onNext("文章2");
                emitter.onNext("文章3");
                emitter.onComplete();
            }
        });
复制代码

```

`ObservableEmitter`，顾名思义，就是被观察者发射器。 所以，`subscribe()`里面的三个`onNext()`方法和一个`onComplete()`会逐一被调用。 这里的`ObservableEmitter`接口其具体实现为`CreateEmitter`，我们看看`CreateEmitte`类的`onNext()`方法和`onComplete()`的实现：

##### 4.2.2.5 CreateEmitter 类的 onNext() 和 onComplete() 等

```
        //省略其他代码
       
        @Override
        public void onNext(T t) {
            //省略无关代码
            if (!isDisposed()) {
                //调用观察者的onNext()
                observer.onNext(t);
            }
        }
       
        @Override
        public void onComplete() {
            if (!isDisposed()) {
                try {
                    //调用观察者的onComplete()
                    observer.onComplete();
                } finally {
                    dispose();
                }
            }
        }
复制代码

```

可以看到，最终就是会调用到观察者的`onNext()`和`onComplete()`方法。至此，一个完整的消息订阅流程就完成了。 另外，可以看到，上面有个`isDisposed()`方法能控制消息的走向，即能够切断消息的传递，这个后面再来说。

##### 4.2.2.6 简单总结

`Observable`(被观察者) 和`Observer`(观察者)建立连接 (订阅) 之后，会创建出一个发射器`CreateEmitter`，发射器会把被观察者中产生的事件发送到观察者中去，观察者对发射器中发出的事件做出响应处理。可以看到，是订阅之后，`Observable`(被观察者) 才会开始发送事件。

放张事件流的传递图：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc55f54e2be?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

##### 4.2.2.7 时序流程图

再来看下订阅过程的时序流程图：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc55f4f9e71?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

### 4.3 切断消息

之前有提到过切断消息的传递，我们先来看下如何使用：

#### 4.3.1 切断消息

```
        Observable observable = Observable.create(
        new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter)
            throws Exception {
                emitter.onNext("文章1");
                emitter.onNext("文章2");
                emitter.onNext("文章3");
                emitter.onComplete();
            }
        });
       
        Observer<String> observer = new Observer<String>() {
            private Disposable mDisposable;
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe : " + d);
                mDisposable=d;
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext : " + s);
                mDisposable.dispose();
                Log.d(TAG, "切断观察者与被观察者的连接");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError : " + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
       
        observable.subscribe(observer);
复制代码

```

输出结果为：

```
onSubscribe : null
onNext : 文章1
切断观察者与被观察者的连接
复制代码

```

可以看到，要切断消息的传递很简单，调用下`Disposable`的`dispose()`方法即可。调用`dispose()`之后，被观察者虽然能继续发送消息，但是观察者却收不到消息了。 另外有一点需要注意，上面`onSubscribe`输出的`Disposable`值是`"null"`, 并不是空引用`null`。

#### 4.3.2 切断消息源码分析

我们这里来看看下`dispose()`的实现。`Disposable`是一个接口，可以理解`Disposable`为一个连接器，调用`dispose()`后，这个连接器将会中断。其具体实现在`CreateEmitter`类，之前也有提到过。我们来看下`CreateEmitter`的`dispose()`方法：

##### 4.3.2.1 CreateEmitter 的 dispose()

```
        @Override
        public void dispose() {
            DisposableHelper.dispose(this);
        }
复制代码

```

就是调用`DisposableHelper.dispose(this)`而已。

##### 4.3.2.2 DisposableHelper 类

```
public enum DisposableHelper implements Disposable {

    DISPOSED
    ;
   
    //其他代码省略

    public static boolean isDisposed(Disposable d) {
        //判断Disposable类型的变量的引用是否等于DISPOSED
        //即判断该连接器是否被中断
        return d == DISPOSED;
    }
   
    public static boolean dispose(AtomicReference<Disposable> field) {
        Disposable current = field.get();
        Disposable d = DISPOSED;
        if (current != d) {
            //这里会把field给设为DISPOSED
            current = field.getAndSet(d);
            if (current != d) {
                if (current != null) {
                    current.dispose();
                }
                return true;
            }
        }
        return false;
    }
}
复制代码

```

可以看到`DisposableHelper`是一个枚举类，并且只有一个值:`DISPOSED`。`dispose()`方法中会把一个原子引用`field`设为`DISPOSED`，即标记为中断状态。因此后面通过`isDisposed()`方法即可以判断连接器是否被中断。

##### 4.3.2.3 CreateEmitter 类中的方法

再回头看看`CreateEmitter`类中的方法：

```
        @Override
        public void onNext(T t) {
            //省略无关代码
           
            if (!isDisposed()) {
                //如果没有dispose()，才会调用onNext()
                observer.onNext(t);
            }
        }

        @Override
        public void onError(Throwable t) {
            if (!tryOnError(t)) {
                //如果dispose()了，会调用到这里，即最终会崩溃
                RxJavaPlugins.onError(t);
            }
        }

        @Override
        public boolean tryOnError(Throwable t) {
            //省略无关代码
            if (!isDisposed()) {
                try {
                    //如果没有dispose()，才会调用onError()
                    observer.onError(t);
                } finally {
                    //onError()之后会dispose()
                    dispose();
                }
                //如果没有dispose()，返回true
                return true;
            }
            //如果dispose()了，返回false
            return false;
        }

        @Override
        public void onComplete() {
            if (!isDisposed()) {
                try {
                    //如果没有dispose()，才会调用onComplete()
                    observer.onComplete();
                } finally {
                    //onComplete()之后会dispose()
                    dispose();
                }
            }
        }
复制代码

```

从上面的代码可以看到：

> 1.  如果没有`dispose`，`observer.onNext()`才会被调用到。
> 2.  `onError()`和`onComplete()`互斥，只能其中一个被调用到，因为调用了他们的任意一个之后都会调用`dispose()`。
> 3.  先`onError()`后`onComplete()`，`onComplete()`不会被调用到。反过来，则会崩溃，因为`onError()`中抛出了异常：`RxJavaPlugins.onError(t)`。实际上是`dispose`后继续调用`onError()`都会炸。

5.RxJava 的线程切换
--------------

上面的例子和分析都是在同一个线程中进行，这中间也没涉及到线程切换的相关问题。但是在实际开发中，我们通常需要在一个子线程中去进行一些数据获取操作，然后要在主线程中去更新 UI，这就涉及到线程切换的问题了，通过 RxJava 我们也可以把线程切换写得还简洁。

### 5.1 线程切换例子

关于 RxJava 如何使用线程切换，这里就不详细讲了。 我们直接来看一个例子，并分别打印 RxJava 在运行过程中各个角色所在的线程。

```
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "Thread run() 所在线程为 :" + Thread.currentThread().getName());
                Observable
                        .create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                Log.d(TAG, "Observable subscribe() 所在线程为 :" + Thread.currentThread().getName());
                                emitter.onNext("文章1");
                                emitter.onNext("文章2");
                                emitter.onComplete();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "Observer onSubscribe() 所在线程为 :" + Thread.currentThread().getName());
                            }

                            @Override
                            public void onNext(String s) {
                                Log.d(TAG, "Observer onNext() 所在线程为 :" + Thread.currentThread().getName());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "Observer onError() 所在线程为 :" + Thread.currentThread().getName());
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "Observer onComplete() 所在线程为 :" + Thread.currentThread().getName());
                            }
                        });
            }
        }.start();
复制代码

```

输出结果为：

```
Thread run() 所在线程为 :Thread-2
Observer onSubscribe() 所在线程为 :Thread-2
Observable subscribe() 所在线程为 :RxCachedThreadScheduler-1
Observer onNext() 所在线程为 :main
Observer onNext() 所在线程为 :main
Observer onComplete() 所在线程为 :main
复制代码

```

从上面的例子可以看到：

> 1.  `Observer`（观察者）的`onSubscribe()`方法运行在当前线程中。
> 2.  `Observable`（被观察者）中的`subscribe()`运行在`subscribeOn()`指定的线程中。
> 3.  `Observer`（观察者）的`onNext()`和`onComplete()`等方法运行在`observeOn()`指定的线程中。

### 5.2 源码分析

下面我们对线程切换的源码进行一下分析，分为两部分：`subscribeOn()`和`observeOn()`。

#### 5.2.1 subscribeOn() 源码分析

首先来看下`subscribeOn()`, 我们的例子中是这么个使用的：

```
    .subscribeOn(Schedulers.io())
复制代码

```

`subscribeOn()`方法要传入一个`Scheduler`类对象作为参数，`Scheduler`是一个调度类，能够延时或周期性地去执行一个任务。

##### 5.2.1.1 Scheduler 类型

通过`Schedulers`类我们可以获取到各种`Scheduler`的子类。RxJava 提供了以下这些线程调度类供我们使用：

| Scheduler 类型 | 使用方式 | 含义 | 使用场景 |
| --- | --- | --- | --- |
| IoScheduler | `Schedulers.io()` | io 操作线程 | 读写 SD 卡文件，查询数据库，访问网络等 IO 密集型操作 |
| NewThreadScheduler | `Schedulers.newThread()` | 创建新线程 | 耗时操作等 |
| SingleScheduler | `Schedulers.single()` | 单例线程 | 只需一个单例线程时 |
| ComputationScheduler | `Schedulers.computation()` | CPU 计算操作线程 | 图片压缩取样、xml,json 解析等 CPU 密集型计算 |
| TrampolineScheduler | `Schedulers.trampoline()` | 当前线程 | 需要在当前线程立即执行任务时 |
| HandlerScheduler | `AndroidSchedulers.mainThread()` | Android 主线程 | 更新 UI 等 |

##### 5.2.1.2 Schedulers 类的 io()

下面我们来看下`Schedulers.io()`的代码，其他的`Scheduler`子类都差不多，就不逐以分析了，有兴趣的请自行查看哈～

```
    @NonNull
    static final Scheduler IO;
   
    @NonNull
    public static Scheduler io() {
        //1.直接返回一个名为IO的Scheduler对象
        return RxJavaPlugins.onIoScheduler(IO);
    }
   
    static {
        //省略无关代码
       
        //2.IO对象是在静态代码块中实例化的，这里会创建按一个IOTask()
        IO = RxJavaPlugins.initIoScheduler(new IOTask());
    }
   
    static final class IOTask implements Callable<Scheduler> {
        @Override
        public Scheduler call() throws Exception {
            //3.IOTask中会返回一个IoHolder对象
            return IoHolder.DEFAULT;
        }
    }
   
    static final class IoHolder {
        //4.IoHolder中会就是new一个IoScheduler对象出来
        static final Scheduler DEFAULT = new IoScheduler();
    }
复制代码

```

可以看到，`Schedulers.io()`中使用了静态内部类的方式来创建出了一个单例`IoScheduler`对象出来，这个`IoScheduler`是继承自 Scheduler 的。这里 mark 一发，后面会用到这个`IoScheduler`的。

##### 5.2.1.3 Observable 类的 subscribeOn()

然后，我们就来看下 subscribeOn() 的代码：

```
    public final Observable<T> subscribeOn(Scheduler scheduler) {
        //省略无关代码
        return RxJavaPlugins.onAssembly(new ObservableSubscribeOn<T>(this, scheduler));
    }
复制代码

```

可以看到，首先会将当前的`Observable`（其具体实现为`ObservableCreate`）包装成一个新的`ObservableSubscribeOn`对象。 放个图：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc55f2f3309?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

跟前面一样，`RxJavaPlugins.onAssembly()`也是将`ObservableSubscribeOn`对象原样返回而已，这里就不看了。 可以看下`ObservableSubscribeOn`的构造方法：

##### 5.2.1.4 ObservableSubscribeOn 类的构造方法

```
    public ObservableSubscribeOn(ObservableSource<T> source, Scheduler scheduler) {
        super(source);
        this.scheduler = scheduler;
    }
复制代码

```

也就是把`source`和`scheduler`这两个保存一下，后面会用到。

然后`subscribeOn()`方法就完了。好像也没做什么，就是重新包装一下对象而已，然后将新对象返回。即将一个旧的被观察者包装成一个新的被观察者。

##### 5.2.1.5 ObservableSubscribeOn 类的 subscribeActual()

接下来我们回到订阅过程，为什么要回到订阅过程呢？因为事件的发送是从订阅过程开始的啊。 虽然我们这里用到了线程切换，但是呢，其订阅过程前面的内容跟上一节分析的是一样的，我们这里就不重复了，直接从不一样的地方开始。还记得订阅过程中`Observable`类的`subscribeActual()`是个抽象方法吗？因此要看其子类的具体实现。在上一节订阅过程中，其具体实现是在`ObservableCreate`类。但是由于我们调用`subscribeOn()`之后，`ObservableCreate`对象被包装成了一个新的`ObservableSubscribeOn`对象了。因此我们就来看看`ObservableSubscribeOn`类中的`subscribeActual()`方法：

```
    @Override
    public void subscribeActual(final Observer<? super T> s) {
        final SubscribeOnObserver<T> parent = new SubscribeOnObserver<T>(s);

        s.onSubscribe(parent);

        parent.setDisposable(scheduler.scheduleDirect(new SubscribeTask(parent)));
    }
复制代码

```

`subscribeActual()`中同样也将我们自定义的`Observer`给包装成了一个新的`SubscribeOnObserver`对象。同样，放张图：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc58e48cfed?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

然后就是调用

`Observer`

的

`onSubscribe()`

方法，可以看到，到目前为止，还没出现过任何线程相关的东西，所以

`Observer`

的

`onSubscribe()`

方法就是运行在当前线程中。 然后我们重点看下最后一行代码，首先创建一个

`SubscribeTask`

对象，然后就是调用

`scheduler.scheduleDirect()`

.。 我们先来看下

`SubscribeTask`

类：

##### 5.2.1.6 SubscribeTask 类

```
    //SubscribeTask是ObservableSubscribeOn的内部类
    final class SubscribeTask implements Runnable {
        private final SubscribeOnObserver<T> parent;

        SubscribeTask(SubscribeOnObserver<T> parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            //这里的source就是我们自定义的Observable对象，即ObservableCreate
            source.subscribe(parent);
        }
    }
复制代码

```

很简单的一个类，就是实现了`Runnable`接口，然后`run()`中调用`Observer.subscribe()`。

##### 5.2.1.7 Scheduler 类的 scheduleDirect()

再来看下`scheduler.scheduleDirect()`方法

```
    public Disposable scheduleDirect(@NonNull Runnable run) {
        return scheduleDirect(run, 0L, TimeUnit.NANOSECONDS);
    }
复制代码

```

往下看：

```
    public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {

        //createWorker()在Scheduler类中是个抽象方法，所以其具体实现在其子类中
        //因此这里的createWorker()应当是在IoScheduler中实现的。
        //Worker中可以执行Runnable
        final Worker w = createWorker();
       
        //实际上decoratedRun还是这个run对象，即SubscribeTask
        final Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
       
        //将Runnable和Worker包装成一个DisposeTask
        DisposeTask task = new DisposeTask(decoratedRun, w);
       
        //Worker执行这个task
        w.schedule(task, delay, unit);

        return task;
    }
复制代码

```

我们来看下创建`Worker`和`Worker`执行任务的过程。

##### 5.2.1.8 IoScheduler 的 createWorker() 和 schedule()

```
    final AtomicReference<CachedWorkerPool> pool;
   
    public Worker createWorker() {
        //就是new一个EventLoopWorker，并且传一个Worker缓存池进去
        return new EventLoopWorker(pool.get());
    }
   
    static final class EventLoopWorker extends Scheduler.Worker {
        private final CompositeDisposable tasks;
        private final CachedWorkerPool pool;
        private final ThreadWorker threadWorker;

        final AtomicBoolean once = new AtomicBoolean();
       
        //构造方法
        EventLoopWorker(CachedWorkerPool pool) {
            this.pool = pool;
            this.tasks = new CompositeDisposable();
            //从缓存Worker池中取一个Worker出来
            this.threadWorker = pool.get();
        }

        @NonNull
        @Override
        public Disposable schedule(@NonNull Runnable action, long delayTime, @NonNull TimeUnit unit) {
            //省略无关代码
           
            //Runnable交给threadWorker去执行
            return threadWorker.scheduleActual(action, delayTime, unit, tasks);
        }
    }
复制代码

```

注意，不同的`Scheduler`类会有不同的`Worker`实现，因为`Scheduler`类最终是交到`Worker`中去执行调度的。

我们来看下`Worker`缓存池的操作：

##### 5.2.1.9 CachedWorkerPool 的 get()

```
    static final class CachedWorkerPool implements Runnable {
        ThreadWorker get() {
            if (allWorkers.isDisposed()) {
                return SHUTDOWN_THREAD_WORKER;
            }
            while (!expiringWorkerQueue.isEmpty()) {
                //如果缓冲池不为空，就从缓存池中取threadWorker
                ThreadWorker threadWorker = expiringWorkerQueue.poll();
                if (threadWorker != null) {
                    return threadWorker;
                }
            }

            //如果缓冲池中为空，就创建一个并返回。
            ThreadWorker w = new ThreadWorker(threadFactory);
            allWorkers.add(w);
            return w;
        }
    }
复制代码

```

##### 5.2.1.10 NewThreadWorker 的 scheduleActual()

我们再来看下`threadWorker.scheduleActual()`。 `ThreadWorker`类没有实现`scheduleActual()`方法，其父类`NewThreadWorker`实现了该方法，我们点进去看下：

```
public class NewThreadWorker extends Scheduler.Worker implements Disposable {
    private final ScheduledExecutorService executor;

    volatile boolean disposed;

    public NewThreadWorker(ThreadFactory threadFactory) {
        //构造方法中创建一个ScheduledExecutorService对象，可以通过ScheduledExecutorService来使用线程池
        executor = SchedulerPoolFactory.create(threadFactory);
    }
   
    public ScheduledRunnable scheduleActual(final Runnable run, long delayTime, @NonNull TimeUnit unit, @Nullable DisposableContainer parent) {
        //这里的decoratedRun实际还是run对象
        Runnable decoratedRun = RxJavaPlugins.onSchedule(run);
        //将decoratedRun包装成一个新对象ScheduledRunnable
        ScheduledRunnable sr = new ScheduledRunnable(decoratedRun, parent);

        //省略无关代码
       
        if (delayTime <= 0) {
            //线程池中立即执行ScheduledRunnable
            f = executor.submit((Callable<Object>)sr);
        } else {
            //线程池中延迟执行ScheduledRunnable
            f = executor.schedule((Callable<Object>)sr, delayTime, unit);
        }
           
        //省略无关代码

        return sr;
    }
}
复制代码

```

这里的`executor`就是使用线程池去执行任务，最终`SubscribeTask`的`run()`方法会在线程池中被执行，即`Observable`的`subscribe()`方法会在 IO 线程中被调用。这与上面例子中的输出结果符合：

```
Observable subscribe() 所在线程为 :RxCachedThreadScheduler-1
复制代码

```

##### 5.2.1.11 简单总结

> 1.  `Observer`（观察者）的`onSubscribe()`方法运行在当前线程中，因为在这之前都没涉及到线程切换。
> 2.  如果设置了`subscribeOn(指定线程)`，那么`Observable`（被观察者）中`subscribe()`方法将会运行在这个指定线程中去。

##### 5.2.1.12 时序图

来张总的`subscribeOn()`切换线程时序图

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc597417a8c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

##### 5.2.1.13 多次设置 subscribeOn() 的问题

如果我们多次设置`subscribeOn()`，那么其执行线程是在哪一个呢？先来看下例子

```
        //省略前后代码，看重点部分
        .subscribeOn(Schedulers.io())//第一次
        .subscribeOn(Schedulers.newThread())//第二次
        .subscribeOn(AndroidSchedulers.mainThread())//第三次
复制代码

```

其输出结果为：

```
Observable subscribe() 所在线程为 :RxCachedThreadScheduler-1
复制代码

```

即只有第一次的`subscribeOn()`起作用了。这是为什么呢？ 我们知道，每调用一次`subscribeOn()`就会把旧的被观察者包装成一个新的被观察者，经过了三次调用之后，就变成了下面这个样子：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc58e582d9b?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

同时，我们知道，被观察者被订阅时是从最外面的一层通知到里面的一层，那么当传到上图第三层时，也就是

`ObservableSubscribeOn`

（第一次）那一层时，管你之前是在哪个线程，

`subscribeOn(Schedulers.io())`

都会把线程切到 IO 线程中去执行，所以多次设置

`subscribeOn()`

时，只有第一次生效。

#### 5.2.2 observeOn()

我们再来看下`observeOn()`，还是先来回顾一下我们例子中的设置：

```
    //指定在Android主线程中执行
    .observeOn(AndroidSchedulers.mainThread())
复制代码

```

##### 5.2.2.1 Observable 类的 observeOn()

```
    public final Observable<T> observeOn(Scheduler scheduler) {
        return observeOn(scheduler, false, bufferSize());
    }

    public final Observable<T> observeOn(Scheduler scheduler, boolean delayError, int bufferSize) {
        //省略无关代码
        return RxJavaPlugins.onAssembly(new ObservableObserveOn<T>(this, scheduler, delayError, bufferSize));
    }
复制代码

```

同样，这里也是新包装一个`ObservableObserveOn`对象，注意，这里包装的旧被观察者是`ObservableSubscribeOn`对象了，因为之前调用过`subscribeOn()`包装了一层了，所以现在是如下图所示：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc59a40251e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

`RxJavaPlugins.onAssembly()`也是原样返回。

我们看看`ObservableObserveOn`的构造方法。

##### 5.2.2.2 ObservableObserveOn 类的构造方法

```
    public ObservableObserveOn(ObservableSource<T> source, Scheduler scheduler, boolean delayError, int bufferSize) {
        super(source);
        this.scheduler = scheduler;
        this.delayError = delayError;
        this.bufferSize = bufferSize;
    }
复制代码

```

里面就是一些变量赋值而已。

##### 5.2.2.3 ObservableObserveOn 的 subscribeActual()

和`subscribeOn()`差不多，我们就直接来看`ObservableObserveOn`的`subscribeActual()`方法了。

```
    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        //判断是否当前线程
        if (scheduler instanceof TrampolineScheduler) {
            //是当前线程的话，直接调用里面一层的subscribe()方法
            //即调用ObservableSubscribeOn的subscribe()方法
            source.subscribe(observer);
        } else {
            //创建Worker
            //本例子中的scheduler为AndroidSchedulers.mainThread()
            Scheduler.Worker w = scheduler.createWorker();
            //这里会将Worker包装到ObserveOnObserver对象中去
            //注意：source.subscribe没有涉及到Worker，所以还是在之前设置的线程中去执行
            //本例子中source.subscribe就是在IO线程中执行。
            source.subscribe(new ObserveOnObserver<T>(observer, w, delayError, bufferSize));
        }
    }
复制代码

```

同样，这里也将`observer`给包装了一层，如下图所示：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc5afc59f7d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

`source.subscribe()`中将会把事件逐一发送出去，我们这里只看下`ObserveOnObserver`中的`onNext()`方法的处理，`onComplete()`等就不看了，实际上都差不多。

##### 5.2.2.4 ObserveOnObserver 的 onNext()

```
        @Override
        public void onNext(T t) {
            //省略无关代码
            if (sourceMode != QueueDisposable.ASYNC) {
                //将信息存入队列中
                queue.offer(t);
            }
            schedule();
        }
复制代码

```

就是调用`schedule()`而已。

##### 5.2.2.5 ObserveOnObserver 的 schedule()

```
        void schedule() {
            if (getAndIncrement() == 0) {
                //ObserveOnObserver同样实现了Runnable接口，所以就把它自己交给worker去调度了
                worker.schedule(this);
            }
        }
复制代码

```

Android 主线程调度器里面的代码就不分析了，里面实际上是用`handler`来发送`Message`去实现的，感兴趣的可以看下。 既然`ObserveOnObserver`实现了`Runnable`接口，那么就是其`run()`方法会在主线程中被调用。 我们来看下`ObserveOnObserver`的`run()`方法：

##### 5.2.2.6 ObserveOnObserver 的 run()

```
        @Override
        public void run() {
            //outputFused默认是false
            if (outputFused) {
                drainFused();
            } else {
                drainNormal();
            }
        }
复制代码

```

这里会走到`drainNormal()`方法。

##### 5.2.2.7 ObserveOnObserver 的 drainNormal()

```
        void drainNormal() {
            int missed = 1;
            //存储消息的队列
            final SimpleQueue<T> q = queue;
            //这里的actual实际上是SubscribeOnObserver
            final Observer<? super T> a = actual;

            //省略无关代码
           
            //从队列中取出消息
            v = q.poll();
           
            //...
           
            //这里调用的是里面一层的onNext()方法
            //在本例子中，就是调用SubscribeOnObserver.onNext()
            a.onNext(v);
           
            //...
        }
复制代码

```

至于`SubscribeOnObserver.onNext()`，里面也没切换线程的逻辑，就是调用里面一层的`onNext()`，所以最终会调用到我们自定义的`Observer`中的`onNext()`方法。因此，`Observer`的`onNext()`方法就在`observeOn()`中指定的线程中给调用了，在本例中，就是在 Android 主线程中给调用。

##### 5.2.2.8 简单总结

> 1.  如果设置了`observeOn(指定线程)`，那么`Observer`（观察者）中的`onNext()`、`onComplete()`等方法将会运行在这个指定线程中去。
> 2.  `subscribeOn()`设置的线程不会影响到`observeOn()`。

##### 5.2.2.9 时序图

最后，来张 observeOn() 时序图：

![](https://user-gold-cdn.xitu.io/2018/6/12/163f3fc5bcad8321?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

6. 其他
-----

因本人水平有限，如有错误，欢迎指出并交流~ [四月葡萄的博客](https://link.juejin.im?target=https%3A%2F%2Fblog.csdn.net%2Fu011810352 "点击跳转")

![](https://user-gold-cdn.xitu.io/2018/6/6/163d3319859594d4?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)欢迎关注微信公众号，接收第一手技术干货