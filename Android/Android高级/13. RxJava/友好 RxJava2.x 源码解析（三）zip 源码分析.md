> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5ac16a2d6fb9a028b617a82a

系列文章：

*   [**友好 RxJava2.x 源码解析（一）基本订阅流程**](https://juejin.im/post/5a209c876fb9a0452577e830)
*   [**友好 RxJava2.x 源码解析（二）线程切换**](https://juejin.im/post/5a248206f265da432153ddbc)
*   [**友好 RxJava2.x 源码解析（三）zip 源码分析**](https://juejin.im/post/5ac16a2d6fb9a028b617a82a)

本文基于 RxJava 2.1.9

*   [前言](#pre)
*   [示例代码](#code)
*   [源码解析](#source_code)
*   [可视化](#visi)
*   [后记](#after)

前言
--

距离前两篇文章已经过去三个月之久了，终于补上第三篇了。第三篇预期就是针对某一个操作符的源码进行解析，选择了 `Observable.zip` 的原因一是司里这块用的比较多，再一个笔者觉得这个操作符十分强大，想去探索一番 zip 操作符是如何实现这样的骚操作，如果读者还不了解 zip 操作符，建议查看文档并上手一番，文档地址：[Zip · ReactiveX 文档中文翻译](https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Zip.html)

![](https://user-gold-cdn.xitu.io/2018/4/2/1628386bb887ea63?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

示例代码
----

```
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;

public class Test {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        Observable.zip(first(), second(), zipper())
                .subscribe(System.out::println);
    }

    private static ObservableSource<String> first() {
        return Observable.create(emitter -> {
                    Thread.sleep(1000);
                    emitter.onNext("11");
                    emitter.onNext("12");
                    emitter.onNext("13");
                }
        );
    }

    private static ObservableSource<String> second() {
        return Observable.create(emitter -> {
                    emitter.onNext("21");
                    Thread.sleep(2000);
                    emitter.onNext("22");
                    Thread.sleep(3000);
                    emitter.onNext("23");
                }
        );
    }

    private static BiFunction<String, String, String> zipper() {
        return (s1, s2) -> s1 + "，" + s2;
    }
}
复制代码

```

> hello world 级别的代码就是为了 hello world. —— 鲁迅

如上所示，操作过 zip 操作符的读者们应该都知道，会在一秒后输出【11，21】，紧接着两秒后输出【12，22】，再紧接着三秒后输出【13，23】。

源码解析
----

经过前两篇文章的阅读，笔者相信读者们能很快地找到 `ObservableZip` 这个类，这个类就是实现具体 zip 操作的核心类，同样地，直接针对该类的 `subscribeActual(Observer)` 解析，简化后源码如下：

```
public void subscribeActual(Observer<? super R> s) {
    // sources 是上游 ObservableSource 数组
    // 在本案例中也就是上面 first() 和 second() 方法传回的 ObservableSource
    ObservableSource<? extends T>[] sources = this.sources;
    
    ZipCoordinator<T, R> zc = new ZipCoordinator<T, R>(s, zipper, count, delayError);
    zc.subscribe(sources, bufferSize);
}
复制代码

```

简化后可以看到还是很简单的，所以下步就是了解 `ZipCoordinator` 类和其 `subscribe()` 方法的实现了，`ZipCoordinator` 构造函数和 `ZipCoordinator#subscribe()` 代码简化如下 ——

```
    ZipCoordinator(Observer<? super R> actual, int count) {
        this.actual = actual;
        this.observers = new ZipObserver[count];
        this.row = (T[])new Object[count];
    }

    public void subscribe(ObservableSource<? extends T>[] sources, int bufferSize) {
        ZipObserver<T, R>[] s = observers;
        int len = s.length;
        for (int i = 0; i < len; i++) {
            s[i] = new ZipObserver<T, R>(this, bufferSize);
        }
        actual.onSubscribe(this);
        for (int i = 0; i < len; i++) {
            sources[i].subscribe(s[i]);
        }
    }
复制代码

```

大致做了以下几件事：

*   构造函数中初始化了一个和上游 ObservableSource 一样数量大小（在本案例中是 2） 的 ZipObserver 数组和 T 类型的数组。
*   `ZipCoordinator#subscribe()` 中初始化了 ZipObserver 数组并让上游 ObservableSource 分别订阅了对应的 ZipObserver。

经过前面的文章分析我们知道，上游的 `onNext(T)` 方法会触发下游的 `onNext(T)` 方法，所以下一步来看看 ZipObserver 的 `onNext(T)` 方法实现 ——

```
@Override
public void onNext(T t) {
    queue.offer(t);
    parent.drain();
}
复制代码

```

可以看到，源码十分的简单，一是**入队**，二是调用 `ZipCoordinator#drain()` 方法，精简如下 ——

```
public void drain() {
    final ZipObserver<T, R>[] zs = observers;
    final Observer<? super R> a = actual;
    // row 在我们前面提到过
    final T[] os = row;


    for (; ; ) {
        int i = 0;
        int emptyCount = 0;
        for (ZipObserver<T, R> z : zs) {
            if (os[i] == null) {
                boolean d = z.done;
                T v = z.queue.poll();
                boolean empty = v == null;

                if (!empty) {
                    os[i] = v;
                } else {
                    emptyCount++;
                }
            } else {
                // ...
            }
            i++;
        }

        if (emptyCount != 0) {
            break;
        }

        R v = zipper.apply(os.clone();

        a.onNext(v);

        Arrays.fill(os, null);
    }
}
复制代码

```

先从实际场景解析流程，再来总结 ——

![](https://user-gold-cdn.xitu.io/2018/4/2/1628386bb8b9e3f6?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

第一个事件应该是上游 `first()` 返回的 ObservableSource 中发射的【11】，最终在 `ZipObserver#onNext(T)` 方法中，该事件首先被塞入队列，再触发上述的 `ZipCoordinator#drain()`，在 `drain()` 方法中会进入 ZipObserver 的遍历 ——

*   第一次：【11】作为第一个事件，此时 os 中所有元素应该都是 null，所以会走入上面的分支，接着从第一个 ZipObserver 的队列中 poll 一个值，这时队列中有且只有刚刚塞入的【11】事件，它将被填入 os[0] 的位置中。
*   第二次：os[1] 为 null，同样会走入上分支，此时试图从第二 ZipObserver 中 poll 一个值，但是此时第二个 ZipObserver 中队列中肯定是没有值的，因为【21】这个事件 1000 毫秒后才会被发射出来，所以 emptyCount++。

for 循环跳出后，由于 emptyCount 不为 0，死循环结束。

第二个事件也是由 `first()` 发射过来的（【12】）， 当第二个事件发射过来的时候——

*   第一次：os[0] 不为 null，走下分支，然而下分支在大部分情况下并不会执行什么逻辑，所以笔者在此处省略了。
*   第二次：os[1] 为 null，接着 emptyCount++，结束死循环。

同样地，第三个事件（【13】）发射过来的时候，走同样的逻辑。

但是 1000 毫秒后，第「四」个事件由 **`second()`** 发射（也就是【21】）的时候，事情就不一样了——

*   第一次：os[0] 不为 null，走下分支，忽略。
*   第二次：os[1] 为 null，走上分支，此时试图从第二个 ZipObserver 中 poll 一个值，此时有值吗？有——【21】此时出队并被塞入 os[1] 中。

for 循环跳出后，经过 zipper 操作合并后两个事件被传输给下游 Observer 的 `onNext(T)` 中，此时打印台就输出了【11，21】了。当然，最后还会将 os 数组中元素全部填充为 null，为下一次数据填充做准备。

所以实际上 zip 操作符的原理在于就是依靠**队列 + 数组**，当一个事件被发射过来的时候，首先进入队列，再去查看数组的每个元素是否为空 ——

*   如果为空，就去**指定**队列中 poll
    *   如果 poll 出来 null，说明该队列中还没有事件被发射过来，emptyCount++。
    *   如果不为 null 则填充到数组的指定位置。
*   如果不为空，则跳过此次循环。

直到最后，判定 emptyCount 是否不为 0，不为 0 则意味着数组没有被填满，某些队列中还没有值，所以只能结束此次操作，等待下一次上游发射事件了。而如果 emptyCount 为 0，那么说明数组中的值被填满了，这意味着符合触发下游 `Observer#onNext(T)` 的要求了，当然，不要忘了将数组内部元素置 null，为下次数据填充做准备。

妈个鸡，是不是还没懂？笔者也觉得挺难懂的，谁要跟我这么说我也听不懂啊！画图吧 ——

![](https://user-gold-cdn.xitu.io/2018/4/2/1628386bb8a22b17?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

可视化
---

第一次事件由「第一个」事件源发出：

![](https://user-gold-cdn.xitu.io/2018/4/2/1628386bba4b5a80?imageslim)

当【11】入队后，数组开始遍历，数组 0 的位置试图将第一个队列 poll 的值填入，此时为【11】；数组 1 的位置试图将第二个队列 poll 的值填入，但是此时为 null，所以最终结束操作，等待下一次上游的事件发射。

第二次事件仍然是由「第一个」事件源发出的 ——

![](https://user-gold-cdn.xitu.io/2018/4/2/1628386bb87f3302?imageslim)

当【12】入队后，数组开始遍历，数组 0 位置已经被填入值，数组 1 的位置试图将第二个队列 poll 的值填入，但是此时为 null，结束操作。

另一种情况则是第二次事件是由「第二个」事件源发出：

![](https://user-gold-cdn.xitu.io/2018/4/2/1628386bb8635da4?imageslim)

当【21】入队后，数组开始遍历，数组 0 位置已经被填入值，数组 1 的位置试图将第二个队列 poll 的值填入，此时为【21】。循环结束后，emptyCount 依旧为 0，符合条件，触发下游 `Observer#onNext(T)`，然后将数组中元素置 null，为下一次数据填充做准备。

后记
--

ZipCoordinator 为了应对高并发引入了 CAS，同时也利用 CAS 优化 `ZipCoordinator#drain()` 实现，另外如果各位读者对 rxjava 有一定的了解，一定知道有一些和 zip 一类的操作符被称为组合操作符，而里面的 concat 操作符的实现，和 zip 操作符的实现有着异曲同工之妙，感兴趣的读者可以去自行去源码中一探究竟，感受下 rxjava 的魅力。

![](https://user-gold-cdn.xitu.io/2018/4/2/1628386bd89352ba?imageslim)