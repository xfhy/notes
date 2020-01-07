> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5a209c876fb9a0452577e830

系列文章：

*   [**友好 RxJava2.x 源码解析（一）基本订阅流程**](https://juejin.im/post/5a209c876fb9a0452577e830)
*   [**友好 RxJava2.x 源码解析（二）线程切换**](https://juejin.im/post/5a248206f265da432153ddbc)
*   [**友好 RxJava2.x 源码解析（三）zip 源码分析**](https://juejin.im/post/5ac16a2d6fb9a028b617a82a)

本文 csdn 地址：[友好 RxJava2.x 源码解析（一）基本订阅流程](https://link.juejin.im?target=http%3A%2F%2Fblog.csdn.net%2Fziwang_%2Farticle%2Fdetails%2F78618976)

本文基于 RxJava 2.1.3

*   [前言](#pre)
*   [示例代码](#code)
*   [订阅流程源码解析](#analyse)
*   [订阅流程](#flow)
    *   [Observable#subscribe(Observer) 流程](#sub_flow)
    *   [Observer#onSubscribe(Disposable) 流程](#on_flow)
    *   [Observer#onNext(T) 流程](#next_flow)

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
                    emitter.onNext("1");
                    emitter.onNext("2");
                    emitter.onNext("3");
                    emitter.onComplete();
                }
            })
            .subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.e("TAG", "onSubscribe():  ");
                }

                @Override
                public void onNext(String s) {
                    Log.e("TAG", "onNext():  " + s);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    Log.e("TAG", "onComplete():  ");
                }
            });
复制代码

```

输出结果：

```
E/TAG: onSubscribe():  
E/TAG: onNext():  1
E/TAG: onNext():  2
E/TAG: onNext():  3
E/TAG: onComplete():  
复制代码

```

订阅流程解析
------

我们知道 `subscribe()` 方法是 Observable 和 Observer 的连接点，所以首先戳进 `subscribe(Observer observer)` 中，可以发现该方法是 Observable 类的方法，传入了一个 Observer 对象，那首先我们需要弄明白这里的 Observable 和 Observer 分别是什么，观察上方示例代码我们可以知道 Observer 是 new 出来的，所以我们只需要知道 Observable 是什么，当然，这里也很清晰，Observable 就是我们调用 `Observable.create(ObservableOnSubscribe)` 所创建出来的 Observable，来一张图 ——

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f6368a7bb82c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

Observable 和 Observer 我们都弄清楚了，接下来就是查看 `subscribe(Observer)` 具体的实现了，如下 ——

```
@Override
public final void subscribe(Observer<? super T> observer) {
	// 略去其他源码
    subscribeActual(observer);
	// 略去其他源码
}
复制代码

```

略去非关键源码后我们发现它只做了一件事，就是调用 `Observable#subscribeActual(observer)`，而在 Observable 中该方法是一个抽象方法：

```
protected abstract void subscribeActual(Observer<? super T> observer);
复制代码

```

这意味着我们需要去找它的子类，我们要看看它的 `subscribeActual(Observer)` 方法，那我们就得从 `create(ObservableOnSubscribe)` 着手，看它是如何将一个 ObservableOnSubscribe 对象转换成一个 Observable 对象的——

```
public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
	// 略去其他源码
    return new ObservableCreate<T>(source);
}
复制代码

```

同样的，删除非关键源码之后，我们就剩下这么一行代码，这也就是意味着我们需要从 `ObservableCreate` 这个类中去寻找 `subscribeActual(Observer)` 的实现了，这里笔者需要提及两点——

1.  从上述方法可以看出 ObservableCreate 是 Observable 的一个子类
    
2.  我们自定义的 ObservableOnSubscribe 作为一个名为 `source` 字段被传入了。事实上在 Observable 的子类实现中，它们都有一个名为 `source` 的字段，指代上游 Observable（实际上是 ObservableOnSubscribe，但是我们不妨理解成就是 Observable）。
    

`ObservableCreate#subscribeActual()` 实现如下：

```
@Override
protected void subscribeActual(Observer<? super T> observer) {
    CreateEmitter<T> parent = new CreateEmitter<T>(observer);
    // 触发 Observer#onSubscribe(Disposable)
    observer.onSubscribe(parent);

    try {
        // 发射事件
        source.subscribe(parent);
    } catch (Throwable ex) {
        Exceptions.throwIfFatal(ex);
        parent.onError(ex);
    }
}
复制代码

```

第 5 行调用了 `Observer#onSubscribe(Disposable)` ，所以我们可以知道 `Observer#onSubscribe(Disposable)` 是先被调用的，而此时 Observable 甚至还没有开始发射事件！接下来就是调用了 `source.subscribe(ObservableEmitter)`，这个方法是交由开发者去实现的，在示例代码是如下所写 ——

```
@Override
public void subscribe(ObservableEmitter<String> emitter) throws Exception {
      emitter.onNext("1");
      emitter.onNext("2");
      emitter.onNext("3");
      emitter.onComplete();
  }
复制代码

```

在代码中我们调用了 CreateEmitter 对象的 `onNext()` 方法，所以我们需要戳入 CreateEmitter 类中看一下 `onNext(T)` 的具体实现（当然 `onComplete()` 方法等同，此处就不做扩展了），源码如下：

```
    @Override
    public void onNext(T t) {
		// 略去其他源码
        if (!isDisposed()) {
            observer.onNext(t);
        }
    }
复制代码

```

一目了然，当当前对象并不处于 `DISPOSED` 状态时，那么就将会调用下游 Observer 的 `onNext(T)` 方法，而下游 Observer 的 `onNext(T)` 方法也就是我们上面示例代码中所写的——

```
public void onNext(String s) {
    Log.e("TAG", "onNext():  ");
}
复制代码

```

至此，基本订阅流程我们就理清楚了。我们从 `Observable#subscribe(Observer)` 开始，将 Observer 传给 Observable，而 Observable 又会在 `onNext(T)` 方法中激活 Observer 的 `onNext(T)` 方法。我们在示例只涉及了少量的 Observable/Observer，事实上，我们在 RxJava 中运用的操作符都会在内部创建一个 Observable 和 Observer，虽然在 `Observable#subscribeActual(Observer)` 中都有自己特定的实现，但是它们大部分都是做两个操作，一是将「下游」传来的 Observer 根据需求进行封装；二就是让「上游」的 Observable `subscribe()` 该 Observer。

订阅流程
----

经过了如上的分析后，笔者希望读者能够理解 RxJava2.x 的基本订阅流程是从 `Observable#subscribe(Observer)` 开始的，而该方法会触发「上游」 Observable 的 `Observable#subscribeActual(Observer)` 方法，而在该「上游」 Observable 中又会触发「上游的上游」Observable 的 `Observable#subscribeActual(Observer)` 方法。我们不妨用以下述源码举例：

```
Observable
    .create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            emitter.onNext("1");
        }
    })
    .flatMap(new Function<String, ObservableSource<String>>() {
        @Override
        public ObservableSource<String> apply(String s) throws Exception {
            return Observable.just(s);
        }
    })
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
    });
复制代码

```

另附一张图，图中标明了后面讲到的「第一个 Observable」、「第二个 Observable」等名词：

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f6368bf574ea?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

接下来用图展示整个订阅的流程——

### Observable#subscribe(Observer) 流程

在 `Observable#subscribe(Observer)` 之前：

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f6368930a147?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

准备触发 `Observable#subscribe(Observer)`：

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f63687f61f3f?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

`Observable#subscribe(Observer)` 将会导致其上游 Observable 的 `subscribe(Observer)` 方法被调用：

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f6368a0f0ee2?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

上游 Observable 的 `subscribe(Observer)` 方法内部又会调用上游的上游 Observable 的 `subscribe(Observer)`：

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f6368c613100?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

> `Observable#subscribe(Observer)` 会调用 `Observable#subscribeActual(Observer)` ，该方法是一个抽象方法，由子类覆写，所以展现了 Observable 的多态性，而且如何激活上游 Observable 的 `subscribe(Observer)`/`subscribeActual(Observer)` 方法的关键点也在此。实现方式就在于 `Observable#subscribeActual(Observer)` 方法虽然是一个抽象方法，但是它的子类实现中都包含有一句 `source.subscribe(Observer)`，其中 source 就是上游 Observable（实际上是 ObservableSource，但是我们此处不妨就理解成 Observable，毕竟我们对这个对象更熟悉一些，Observable 是 ObservableSource 接口的实现），所以就可以理解**在每一个 Observable 的 `subscribeActual(Observer)` 方法中它都会调用上游的 `subscribe(Observer)/subscribeActual(Observer)` 方法**，直至到达第一个 Observable 的 `subscribe(Observer)/subscribeActual(Observer)` 中。

### Observer#onSubscribe(Disposable) 流程

订阅的关系链理清了，但是还没有发射事件的流程还没出来啊，我们继续往下走——

到达顶部 Observable 的时候，已经不能再往上走了，就要准备搞事情（准备发射事件了），此处我们就以示例代码中的 Observable 为例，它的 `subscribeActual(Observer)` 中——

```
@Override
protected void subscribeActual(Observer<? super T> observer) {
    CreateEmitter<T> parent = new CreateEmitter<T>(observer);
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

它首先封装了一个 Disposable，接下来将调用 `Observer#onSubscribe(Disposable)` 将 Disposable 作为参数传给下一层 Observer。

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f636c9c46d0d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

到了下一层的 Observer 的 `onSubscribe(Disposable)` 中，该方法中针对上一层 Disposable 做一些操作（判断、封装等），然后再封装一个 Disposable 作为参数传递给 `Observer#onSubscribe(Disposable)`。

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f636ccde9d51?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

而此时的 Observer 就是我们所自定义的 Observer——

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f636cb446f92?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

### Observer#onNext(T) 流程

在 `Observer#onSubscribe(Disposable)` 流程结束后，就执行到第 7 行代码 `Observeable.subscribe(Observer)`，实质上也就是——

```
new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        emitter.onNext("1");
    }
})
复制代码

```

> ps：为了方便起见，此处只分析 `onNext()` 执行流程。

在 `ObservableEmitter#onNext(T)` 的内部实际上会触发 Observer 的 `onNext(T)` 方法——

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f636cd346049?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

再向下触发就是我们所自定义的最底层的 Observer 了——

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f636cef76eec?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

以示例代码来说，顶游 Observable 会触发 `ObservableEmitter#onNext(T)` 方法，在该方法的内部又触发了「下游」 Observer 的 `onNext(T)` 方法，而在该方法内部又会触发「下游的下游」 Observer 的 `onNext(T)` 方法，直至最底层的 Observer —— 我们所自定义的 Observer ——

![](https://user-gold-cdn.xitu.io/2017/12/1/1600f636d08fb282?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

到此，一套订阅流程就执行完毕了。