> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5a248206f265da432153ddbc

系列文章：

*   [**友好 RxJava2.x 源码解析（一）基本订阅流程**](https://juejin.im/post/5a209c876fb9a0452577e830)
*   [**友好 RxJava2.x 源码解析（二）线程切换**](https://juejin.im/post/5a248206f265da432153ddbc)
*   [**友好 RxJava2.x 源码解析（三）zip 源码分析**](https://juejin.im/post/5ac16a2d6fb9a028b617a82a)

本文 csdn 地址：[友好 RxJava2.x 源码解析（二）线程切换](http://blog.csdn.net/ziwang_/article/details/78619533)

本文基于 RxJava 2.1.3

*   [前言](#pre)
*   [示例代码](#code)
*   [源码解析](#analyse)
    *   [Observer#onSubscribe(Dispose)](#onsub)
    *   [Observable#observeOn(Scheduler)](#obs)
        *   [不作用上游 Observable](#upstream)
        *   [作用下游 Observer](#downstream)
    *   [Observable#subscribeOn(Scheduler)](#sub)
        *   [切换 subscribe 线程](#up_down)
        *   [第一次有效原理](#first)
    *   [Observable#observeOn(Scheduler) 和 Observable#subscribeOn(Scheduler)](#diff)

前言
--

本文基于读者会使用 RxJava 2.x 而讲解，基本原理不涉及，示例只纯粹为示例而示例。

示例代码
----

示例源码：

```
Observable
        .create(new ObservableOnSubscribe<String>() {
            @Override
                public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                    Log.e("TAG", "subscribe(): 所在线程为 " + Thread.currentThread().getName());
                    emitter.onNext("1");
                    emitter.onComplete();
                }
            })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("TAG", "onSubscribe(): 所在线程为 " + Thread.currentThread().getName());
            }

            @Override
            public void onNext(String s) {
                Log.e("TAG", "onNext(): 所在线程为 " + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                Log.e("TAG", "onComplete(): 所在线程为 " + Thread.currentThread().getName());
            }
        });
复制代码

```

输出结果：

```
E/TAG: onSubscribe(): 所在线程为 main
E/TAG: subscribe(): 所在线程为 RxCachedThreadScheduler-1
E/TAG: onNext(): 所在线程为 main
E/TAG: onComplete(): 所在线程为 main
复制代码

```

源码解析
----

我们可以发现，除了 Observable 的 `subscribe(ObservableEmitter)` 方法执行在 io 线程，Observer 的方法都是执行在 main 线程的，接下来就请各位读者跟着笔者来分析了。

### Observer#onSubscribe(Dispose)

看到标题部分读者就疑惑了，明明是说线程切换，跟 `Observer#onSubscribe()` 方法有什么关系呢？前方的 log 中展示 `Observer#onSubscribe()` 方法在主线程执行的，但是这个主线程是由 `.observeOn(AndroidSchedulers.mainThread())` 所导致的吗？为了解决这个疑惑，我们可以在外面套一个子线程，然后去执行该逻辑，代码如下：

```
new Thread() {
    @Override
    public void run() {
	    Log.e("TAG", "run: 所在线程为 " + Thread.currentThread().getName());
        // 添加示例代码
    }
}.start();
复制代码

```

打印结果：

```
run: 所在线程为 Thread-554
onSubscribe(): 所在线程为 Thread-554
subscribe(): 所在线程为 RxCachedThreadScheduler-1
onNext(): 所在线程为 main
onComplete(): 所在线程为 main
复制代码

```

所以实际上 **`Observer#onSubscribe()` 的执行线程是当前线程**，它并不受 `subscribe(Scheduler)` 或 `observeOn(Scheduler)` 所影响 (因为笔者这段代码写在了 Android 主线程当中，所以当前线程是主线程)。本文不在此扩展原因，具体源码追溯和查看前一篇文章，简而言之—— `subscribe(Observer)` -> `subscribeActual(Observer)` -> `Observer#onSubscribe()`，我们可以看到 `subscribe(Observer)` 的执行线程是当前线程，而在上面所述的数据流中也不存在数据切换的过程，所以 `onSubscribe()` 执行的线程也是当前线程。

### Observable#observeOn(Scheduler)

此小节针对 `Observable#observeOn(Scheduler)` 讲解，所以将示例代码更改如下：

```
new Thread() {
    @Override
    public void run() {
        Log.e("TAG", "run: 当前默认执行环境为 " + Thread.currentThread().getName());
        Observable
            .create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                    emitter.onNext("1");
                }
            })
            // 仅保留 observeOn(Scheduler)
            .observeOn(Schedulers.io())
            .subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(String s) {
                    Log.e("TAG", "onNext(): 所在线程为 " + Thread.currentThread().getName());
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            });
    }
} .start();
复制代码

```

输出结果：

```
E/TAG: run: 当前默认执行线程为 Thread-610
E/TAG: onNext(): 所在线程为 RxCachedThreadScheduler-1
复制代码

```

#### 不作用上游 Observable

同样的，直接先进入 `Observable#observeOn(Scheduler)` 源码查看一下，发现其最终会调用 `Observable#的observeOn(Scheduler, boolean, int)` 方法，该方法将会返回一个 Observable 对象。那么老问题来了，是哪个 Observable 对象调用的 `observeOn()` 方法，又返回了一个怎样的 Observable 对象？

第一个问题很简单，是 `Observable.create(ObservableOnSubscribe)` 对象返回的一个 Observable，而且这个 Observable 是一个 ObservableCreate 对象（这里不理解的可以查看第一篇文章）。但是 `Observable#observeOn(Scheduler, boolean, int)` 是没有被任何子类重写的，这意味着它的子类都是调用它的该方法。

第二个问题来了，返回了一个怎样的 Observable 对象呢？实际上这里的分析流程和第一篇文章中所阐述的流程是一模一样的，我们戳进 `Observable#observeOn(Scheduler, boolean, int)` 源码，发现它最终会返回一个 `new ObservableObserveOn<T>(this, scheduler, delayError, bufferSize)` 对象，这里我们只关注前两个对象，第一个参数 `this` 是指上游的 Observable 对象，也就是我们第一个问题中所涉及到的 Observable 对象，第二个参数 `scheduler` 毋庸置疑就是我们所传入的 Scheduler 对象了，在此也就是我们的 `AndroidSchedulers.mainThread()`。

通过第一篇的学习，我们应该会轻车熟路地打开 ObservableObserveOn 类并查看它的核心 `subscribeActual()` 方法以及构造函数——

```
final Scheduler scheduler;
final boolean delayError;
final int bufferSize;

public ObservableObserveOn(ObservableSource<T> source, Scheduler scheduler, boolean delayError, int bufferSize) {
    super(source);
    this.scheduler = scheduler;
    this.delayError = delayError;
    this.bufferSize = bufferSize;
}

@Override
protected void subscribeActual(Observer<? super T> observer) {
    // 如果传入的 scheduler 是 Scheduler.trampoline() 的情况
    // 该线程的意义是传入当前线程，也就是不做任何线程切换操作
    if (scheduler instanceof TrampolineScheduler) {
        source.subscribe(observer);
    } else {
        Scheduler.Worker w = scheduler.createWorker();
        source.subscribe(new ObserveOnObserver<T>(observer, w, delayError, bufferSize));
    }
}
复制代码

```

直接进入第二个 case，首先先略去第 19 行代码，看到第 20 行代码，`source`（上游 Observable） 和 `Observable#subscribe()` 操作都没有任何变化，唯一改变的地方就是将 Observer 进行了封装，所以我们可以因此得出结论， `Observable#observeOn(Scheduler)` 并不会对上游线程执行环境有任何影响。（如果看到这里不能够理解的话，后文中会有通俗易懂的伪代码辅助理解）

#### 作用下游 Observer

经过上文[友好 RxJava2.x 源码解析（一）基本订阅流程](http://blog.csdn.net/ziwang_/article/details/78618976)一文的分析我们知道 ObservableEmitter 的 `onNext(T)` 方法会触发「下游」 Observer 的 `onNext(T)` 方法，而此时的「下游」 Observer 对象是经过 `Observable#observeOn(Scheduler)` 封装的 ObserveOnObserver 对象，所以我们不妨打开 ObserveOnObserver 的 `onNext(T)` 方法——

```
@Override
public void onNext(T t) {
    // 删除无关源码
    queue.offer(t);
    schedule();
}
复制代码

```

可以看到 `onNext(T)` 方法做了两件事——一是将当前方法传入的对象添加进队列；另一是执行 `schedule()` 方法，打开 `schedule()` 方法源码——

```
void schedule() {
    // 删除无关源码
    worker.schedule(this);
}
复制代码

```

所以将会执行 `worker.schedule(Runnable)`，可向下继续追溯到 `schedule(Runnable, long, TimeUnit )` ，该方法是一个抽象方法，所以我们可以想到，调度器们就是通过实现该方法来创建各色各样的线程的。所以我们继续追溯到 IoScheduler 的 `schedule(Runnable, long, TimeUnit)` 中，源码如下：

```
    public Disposable schedule(@NonNull Runnable action, long delayTime, @NonNull TimeUnit unit) {
        // 删除无关源码
        return threadWorker.scheduleActual(action, delayTime, unit, tasks);
    }
复制代码

```

继续追溯下去——

```
@NonNull
public ScheduledRunnable scheduleActual(final Runnable run, long delayTime, @NonNull TimeUnit unit, @Nullable DisposableContainer parent) {
    Future<?> f;
    if (delayTime <= 0) {
        f = executor.submit((Callable<Object>)sr);
    } else {
        f = executor.schedule((Callable<Object>)sr, delayTime, unit);
    }
    sr.setFuture(f);

    return sr;
}
复制代码

```

executor 是一个 ScheduledExecutorService 对象，而 ScheduledExecutorService 的父接口是我们所熟悉的 ExecutorService 接口，所以很清晰 ScheduledExecutorService 具有创建和调度线程的能力，而其具体的实现在此就不讨论了。

最后，我们不妨将上述所提到的几段源代码整体抽象结合一下：

```
@Override
public void onNext(T t) {
    // 删除无关源码
    if (delayTime <= 0) {
        f = executor.submit((Callable<Object>)this);
    } else {
        f = executor.schedule((Callable<Object>)this, delayTime, unit);
    }
}
复制代码

```

总结一下：`onNext(T)` 方法会触发 Scheduler 对象的 `schedule(Runnable, long, TimeUnit)` ，该方法是一个抽象方法，由子类实现，所以才有了多元多样的 `Schedulers.io()/Schedulers.computation()/Schedulers.trampoline()` 等调度器，具体调度器的内部会使用相关的线程来 `submit()` 或者 `schedule()` 任务。解决完调度器的问题，那么接下来就是看看 `Runnable#run()` 里面的逻辑是什么样的，回到 ObserveOnObserver 中——

```
@Override
public void run() {
    drainNormal();
}
复制代码

```

`drainNormal()` 源码如下：

```
void drainNormal() {
    final SimpleQueue<T> q = queue;
    final Observer<? super T> a = actual;

    for (;;) {
        T v;

        try {
            v = q.poll();
        } catch (Throwable ex) {
        }
        boolean empty = v == null;

        if (empty) {
            break;
        }

        a.onNext(v);
    }
}
复制代码

```

可以看到实际上最后一行执行了 `Observer#onNext(T)` 方法，也就是意味着「ObserveOnObserver 中触发下一层 Observer 的 `onNext(T)` 操作」在指定线程执行，也就达到了切换线程的目的了。

来个复杂的例子——

![](https://user-gold-cdn.xitu.io/2017/12/4/1601e9c01e04a58f?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

经过[友好 RxJava2.x 源码解析（一）基本订阅流程](http://blog.csdn.net/ziwang_/article/details/78618976)一文我们知道，Observer 的传递是由下往上的，从源头开始，我们自定义的 Observer 向上传递的时候到达第六个 Observable 的时候被线程封装了一层，我们不妨使用伪代码演示一下——

```
public class Observer {
    Observer oldObserver;

    public Observer(Observer observer) {
        oldObserver = observer;
    }

    public void onNext(T t) {
        // 一些其他操作
        new Thread("Android mainThread") {
            @Override
            public void run() {
                oldObserver.onNext(t);
            }
        } .start();
    }

    public void onError(Throwable e) {
        // 一些其他操作
        new Thread("Android mainThread") {
            @Override
            public void run() {
                oldObserver.onError(e);
            }
        } .start();
    }

    public void onComplete() {
        // 一些其他操作
        new Thread("Android mainThread") {
            @Override
            public void run() {
                oldObserver.onComplete();
            }
        } .start();
    }
}
复制代码

```

Observer 继续向上被传递，`Observable#map()` 中并未对 Observer 进行线程切换；再向上走，到达第四个 `observeOn(Scheduler)` 的时候，被 computation 线程嵌套了一层——

```
public class Observer {
    Observer oldObserver;

    public Observer(Observer observer) {
        oldObserver = observer;
    }

    public void onNext(T t) {
        // 一些其他操作
        new Thread("computation") {
            @Override
            public void run() {
                oldObserver.onNext(t);
            }
        } .start();
    }

    public void onError(Throwable e) {
        // 一些其他操作
        new Thread("computation") {
            @Override
            public void run() {
                oldObserver.onError(e);
            }
        } .start();
    }

    public void onComplete() {
        // 一些其他操作
        new Thread("computation") {
            @Override
            public void run() {
                oldObserver.onComplete();
            }
        } .start();
    }
}
复制代码

```

当然，继续向上直到顶端 Observable——

```
public class Observer {
    Observer oldObserver;

    public Observer(Observer observer) {
        oldObserver = observer;
    }

    public void onNext(T t) {
        // 一些其他操作
        new Thread("io") {
            @Override
            public void run() {
                oldObserver.onNext(t);
            }
        } .start();
    }

    public void onError(Throwable e) {
        // 一些其他操作
        new Thread("io") {
            @Override
            public void run() {
                oldObserver.onError(e);
            }
        } .start();
    }

    public void onComplete() {
        // 一些其他操作
        new Thread("io") {
            @Override
            public void run() {
                oldObserver.onComplete();
            }
        } .start();
    }
}
复制代码

```

甚至更精简的操作如下：

```
new Thread("Scheduler io") {
    @Override
    public void run() {
        // flatMap() 操作
        flatMap();
        System.out.println("flatMap 操作符执行线程：" + Thread.currentThread().getName());
        System.out.println("第二个 observeOn() 执行线程：" + Thread.currentThread().getName());
        // 第二个 observeOn() 操作
        new Thread("Scheduler computation") {
            @Override
            public void run() {
                // map() 操作
                map();
                System.out.println("map 操作符执行线程：" + Thread.currentThread().getName());
                System.out.println("第三个 observeOn() 执行线程：" + Thread.currentThread().getName());
                // 第三个 observeOn() 操作
                new Thread("Android mainThread") {
                    @Override
                    public void run() {
                        // Observer#onNext(T)/onComplete()/onError() 执行线程
                        System.out.println("Observer#onNext(T)/onComplete()/onError() 执行线程：" +
                                           Thread.currentThread().getName());
                    }
                } .start();
            }
        } .start();
    }
} .start();
复制代码

```

输出结果：

```
flatMap 操作符执行线程：Scheduler io
第二个 observeOn() 执行线程：Scheduler io
map 操作符执行线程：Scheduler computation
第三个 observeOn() 执行线程：Scheduler computation
Observer#onNext(T)/onComplete()/onError() 执行线程：Android mainThread
复制代码

```

由此便将 `Observable#observeOn(Scheduler)` 是如何将下游 Observer 置于指定线程执行的流程分析完了。简而言之 `Observable#observeOn(Scheduler)` 的实现原理在于**将目标 Observer 的 `onNext(T)/onError(Throwable)/onComplete()` 置于指定线程中运行**。

> 这里特别要注意的一点是——【线程操作符切换的是其他的流，自身这条流是不会受到影响的。】看过知乎前一段时间的 [rx 分享](https://github.com/zhihu/zhihu-rxjava-meetup)视频的小伙伴应该有注意到杨凡前辈的 PPT 中有这么一图：
> 
> ![](https://user-gold-cdn.xitu.io/2017/12/4/1601e9c01e5afb72?imageView2/0/w/1280/h/960/format/webp/ignore-error/1) 想要提出两点——`observeOn(Schedulers.io())` 所对应的 Observable 应该是受到了 `subscribeOn(AndroidSchedulers.mainThread())` 影响，所以它创建的这条流应该执行于主线程；而 `subscribeOn(AndroidSchedulers.mainThread())` 所对应的 Observable 则受到了 `subscribeOn(Schedulers.computation)` 影响，所以它创建的这条流应该执行于 computation 线程。

### Observable#subscribeOn(Scheduler)

#### 切换 subscribe 线程

示例代码：

```
Observable
    .create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            emitter.onNext("1");
            Log.e("TAG", "被观察者所在的线程 " + Thread.currentThread().getName());
        }
    })
    .subscribeOn(Schedulers.io())
    .subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.e("TAG", "onSubscribe: " + Thread.currentThread().getName());
        }

        @Override
        public void onNext(String s) {
            Log.e("TAG", "观察者所在线程为 " + Thread.currentThread().getName());
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
    });
复制代码

```

输出结果：

```
E/TAG: onSubscribe: main
E/TAG: 观察者所在线程为 RxCachedThreadScheduler-1
E/TAG: 被观察者所在的线程 RxCachedThreadScheduler-1
复制代码

```

同样地，戳进 `Observable#subscirbeOn(Scheduler)` 源码，点进 ObservableSubscribeOn 查看 `subscribeActual(Observer)` 的具体实现，相信这对于各位读者来说已经轻车熟路了——

```
@Override
public void subscribeActual(final Observer<? super T> s) {
    final SubscribeOnObserver<T> parent = new SubscribeOnObserver<T>(s);
    s.onSubscribe(parent);
    Disposeable disposable = scheduler.scheduleDirect(new SubscribeTask(parent));
    parent.setDisposable(disposable);
}
复制代码

```

第一行老套路，对下游 Observer 进行了一层封装；第二行因为它不涉及线程切换所以此处也不做扩展；第三行就是我们的关键了 `Scheduler#scheduleDirect(Runnable)` 方法可以追溯到 `Scheduler#schedule(Runnable, long, TimeUnit)`，这部分在前面已经阐述过了，就不做扩展了。SubscribeTask 是一个 Runnable，它的 `run()` 核心方法——

```
@Override
public void run() {
	source.subscribe(parent);
}
复制代码

```

至此谜团解开了，`Observable#subscribeOn(Scheduler)` 将 `Observable#subscribe(Observer)` 的执行过程移到了指定线程（在上述中也就是 io 线程），同时 Observable 和 Observer 中**并未做新的线程切换处理**，所以它们的订阅、发射等操作就执行在了 io 线程。

#### 第一次有效原理

示例代码：

```
Observable
    .create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            emitter.onNext("1");
            Log.e("TAG", "被观察者所在的线程 " + Thread.currentThread().getName());
        }
    })
    .subscribeOn(Schedulers.io())
    .subscribeOn(Schedulers.computation())
    .subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.e("TAG", "onSubscribe: " + Thread.currentThread().getName());
        }

        @Override
        public void onNext(String s) {
            Log.e("TAG", "观察者所在线程为 " + Thread.currentThread().getName());
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
    });
复制代码

```

打印结果：

```
onSubscribe: main
观察者所在线程为 RxCachedThreadScheduler-1
被观察者所在的线程 RxCachedThreadScheduler-1
复制代码

```

我们知道，只有第一个 `Observable#subscribeOn(Scheduler)` 操作才有用，而后续的 `Observable#subscribeOn(Scheduler)` 并不会影响整个流程中 Observerable 。同样的，来张图——

![](https://user-gold-cdn.xitu.io/2017/12/4/1601e9c01e80af84?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

前面我们分析到，`Observable#subscribeOn(Scheduler)` 实际上是将 `Observable#subscribe(Observer)` 的操作放在了指定线程，而通过[友好 RxJava2.x 源码解析（一）基本订阅流程](http://blog.csdn.net/ziwang_/article/details/78618976)一文我们知道了 `subscribe` 的过程是由下往上的。所以首先是第三个 Observable 调用 `Observable#subscribe(Observer)` 启动订阅，在其内部会激活第二个 Observable 的 `Observable#subscribe(Observer)` 方法，但是此时该方法外部被套入了一个 `Schedulers.computation()` 线程，于是这个订阅的过程就被运行在了该线程中。同样的，我们不妨用伪代码演示一下——

```
public class Observable {
    // 第「二」个 Observable
    Observable source;
    Observer observer;

    public Observable(Observable source, Observer observer) {
        this.source = source;
        this.observer = observer;
    }

    public void subscribe(Observer Observer) {
        new Thread("computation") {
            @Override
            public void run() {
                // 第「二」个 Observable 订阅
                source.subscribe(observer);
            }
        }
    }
}
复制代码

```

再往上走，第二个 Observable 订阅内部会激活第一个 Observable 的 `Observable#subscribe(Observer)` 方法，同样的，该方法被套在了 `Schedulers.io()` 线程中，如下——

```
public class Observable {
    // 第「一」个 Observable
    Observable source;
    Observer observer;

    public Observable(Observable source, Observer observer) {
        this.source = source;
        this.observer = observer;
    }

    public void subscribe(Observer Observer) {
        new Thread("io") {
            @Override
            public void run() {
                // 第「一」个 Observable 订阅
                source.subscribe(observer);
            }
        }
    }
}
复制代码

```

此时到达第一个 Observable 了之后就要开始发射事件了，此时的执行线程很明显是 io 线程。还可以换成 Thread 伪代码来表示 ——

```
new Thread("computation") {
    @Override
    public void run() {
        // 第二个 Observable.subscribe(Observer) 的实质
        // 就是切换线程，效果类似如下
        new Thread("io") {
            @Override
            public void run() {
                // 第一个 Observable.subscribe(Observer) 的实质
                // 就是发射事件
                System.out.println("onNext(T)/onError(Throwable)/onComplete() 的执行线程是： " + Thread
                                   .currentThread().getName());
            }
        } .start();
    }
} .start();
复制代码

```

输出结果：

```
onNext(T)/onError(Throwable)/onComplete() 的执行线程是： io
复制代码

```

### Observable#observeOn(Scheduler) 和 Observable#subscribeOn(Scheduler)

如果针对前面的内容你已经懂了，那么后续的内容可以直接跳过啦，本文就结束了~ 如果你还没懂，笔者再汇总一次。

经过[友好 RxJava2.x 源码解析（一）基本订阅流程](http://blog.csdn.net/ziwang_/article/details/78618976)一文我们知道，`Observable#subscribe(Observer)` 的顺序是由下往上的，**本游会将 Observer 进行「封装」，然后「激活上游 Observable 订阅这个 Observer」**。

我们不妨抽象一个 Observer，如下：

```
public class Observer<T> {
    public void onNext(T t){}
    public void onCompelete(){}
    public void onError(Throwable t){}
}
复制代码

```

对于 `Observable#observeOn(Schedulers.computation())` 操作来说，它对 Observer 进行了怎样的封装呢？

```
public class NewObserver<T> {
    // 下游 Observer
    Observer downStreamObserver;
    public NewObserver(Observer observer) {
        downStreamObserver = observer;
    }

    public void onNext(T t) {
        new Thread("computation") {
            downStreamObserver.onNext(t);
        }
    }

    public void onError(Throwable e) {
        new Thread("computation") {
            downStreamObserver.onError(e);
        }
    }

    public void onComplete() {
        new Thread("computation") {
            downStreamObserver.onComplete();
        }
    }
}
复制代码

```

在 `Observable#observeOn(Scheduler)` 内部，其对下游的 Observer 进行了类似如上的封装，这就导致了其「下游」 Observer 在指定线程内执行。所以 `Observable#observeOn(Scheduler)` 是可以多次调用并有效的。

而对于 `Observable#subscribe(Scheduler)` 来说，它并未对下游 Observer 进行封装，但是对于「激活上游 Observable 订阅这个 Observer」这个操作它做了一点小小的手脚，也就是切换线程，我们抽象如下——

```
public class ComputationObservable {
    public void subscribe(observer) {
        new Thread("computation") {
            // upstreamObservable 是上游 Observable，我们不妨假设是下文中所提到的 IOObservable
            upstreamObservable.subscribe(observer);
        }
    }
}
复制代码

```

而当它在往上遇到了一个新的 `Observable#subscribe(Scheduler)` 操作的时候——

```
public class IOObservable {
    public void subscribe(observer) {
        new Thread("io") {
            // upstreamObservable 是上游 Observable，我们不妨下文中所提到的 TopObservable
            upstreamObservable.subscribe(observer);
        }
    }
}
复制代码

```

我们不妨假设此时已经到达了最顶端开始发射事件了——

```
public class TopObservable {
    public void subscribe(observer) {
        observer.onNext(t);
    }
}
复制代码

```

此时的 `Observer#onNext(t)` 的执行环境当然就是由最后一个 `subscribeOn(Scheduler)` 操作符（此处的最后一个是指订阅流程中的最后一个，它与实际写代码的顺序相反，也就是我们代码中的第一个 `subscribeOn(Scheduler)` 操作符）所决定的了，在上述伪代码中也就是 io 线程，伪代码对应的源码如下——

```
Observable
    .create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            emitter.onNext("1");
        }
    })
    .subscribeOn(Schedulers.io())
    .subscribeOn(Schedulers.computation())
    .subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(String s) {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
    });复制代码

```