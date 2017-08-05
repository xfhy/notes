# 给Android开发者的RxJava详解

> 本文只是本人在看原著时的笔记,想要看原文的小伙伴可以[点击进去这里](http://gank.io/post/560e15be2dca930e00da1083#toc_1)查看原文.

## RxJava的基本实现主要有三点

### Observer 

> 即观察者,它决定事件触发的时候将有怎样的行为.

### Observable 

> 即被观察者,它决定什么时候触发事件以及触发怎样的事件.

### Subscribe

> 即订阅.创建了Observable和Observer之后,再用subscribe()方法将它们联结起来,整条链子就可以工作了.

放图(图片来自给Android开发者的RxJava详解)

![](http://ww3.sinaimg.cn/mw1024/52eb2279jw1f2rx4ay0hrg20ig08wk4q.gif)

## 线程控制(一)

> 如果需要切换线程,就需要用到Scheduler(调度器)

- Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
- Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
- Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
- Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
- 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。

有了这几个 Scheduler ，就可以使用 subscribeOn() 和 observeOn() 两个方法来对线程进行控制了。 * subscribeOn(): 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。 * observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。

	Observable.just(1, 2, 3, 4)
    .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
    .subscribe(new Action1<Integer>() {
        @Override
        public void call(Integer number) {
            Log.d(tag, "number:" + number);
        }
    });

## 线程控制(二)

换句话说，observeOn() 指定的是它之后的操作所在的线程。因此如果有多次切换线程的需求，只要在每个想要切换线程的位置调用一次 observeOn() 即可。上代码：

	Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.newThread())
    .map(mapOperator) // 新线程，由 observeOn() 指定
    .observeOn(Schedulers.io())
    .map(mapOperator2) // IO 线程，由 observeOn() 指定
    .observeOn(AndroidSchedulers.mainThread) 
    .subscribe(subscriber);  // Android 主线程，由 observeOn() 指定

如上，通过 observeOn() 的多次调用，程序实现了线程的多次切换。

不过，不同于 observeOn() ， subscribeOn() 的位置放在哪里都可以，但它是只能调用一次的。
