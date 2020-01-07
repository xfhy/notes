

> 本文基于RxJava 2.2.10 进行分析

## 1. 最基本的订阅流程

下面先举一个很简单的例子,示例一:

```java
Observable.create(new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        emitter.onNext("1111");
        emitter.onNext("2222");
        emitter.onNext("3333");
        emitter.onComplete();
    }
}).subscribe(new Observer<String>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.e(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(String s) {
        Log.e(TAG, "onNext: " + s);
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: " + e.getMessage());
    }

    @Override
    public void onComplete() {
        Log.e(TAG, "onComplete: ");
    }
});
```

在上面的例子中首先调用Observable.create方法创建一个Observable.

```java
public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
    return new ObservableCreate<T>(source);
}
```

所以这里最终创建出来的是ObservableCreate.然后关键是subscribe()这个方法开始的订阅,而subscribe这个方法是Observable的一个方法.我们看一下subscribe方法是如何实现的

```java
public final void subscribe(Observer<? super T> observer) {
    ...
    subscribeActual(observer);
    ...
}

protected abstract void subscribeActual(Observer<? super T> observer);

```
subscribe的实现非常简单,抹去一些其他检查代码,就只是调用了一下subscribeActual方法.而subscribeActual方法是Observable里面的抽象方法.这个subscribeActual抽象方法是在上面创建出来的ObservableCreate里面实现的

```java
@Override
protected void subscribeActual(Observer<? super T> observer) {
    //创建一个发射器
    CreateEmitter<T> parent = new CreateEmitter<T>(observer);
    //调用观察者的onSubscribe方法   即上面示例中的 Log.e(TAG, "onSubscribe: ");
    observer.onSubscribe(parent);
    
    try {
        //这里的source是我们传入的ObservableOnSubscribe,即调用它的subscribe方法,并传入一个发射器
        source.subscribe(parent);
    } catch (Throwable ex) {
        Exceptions.throwIfFatal(ex);
        如果这里出错了,那么就调用观察者的onError方法   这里捕获的异常有点大,Throwable
        parent.onError(ex);
    }
    
}
```
需要注意的是用CreateEmitter包装了一下Observer.然后才是执行观察者(Observer)的onSubscribe方法.
调用ObservableOnSubscribe的subscribe方法,即走到了我们的如下代码:

```java
//这个emitter是上面包装了观察者(Observer)的CreateEmitter
emitter.onNext("1111");
emitter.onNext("2222");
emitter.onNext("3333");
emitter.onComplete();
```

这个emitter是上面包装了观察者(Observer)的CreateEmitter,它实现了ObservableEmitter接口,ObservableEmitter接口是继承了Emitter接口.一看就是一个发射器装置.

相当于是开始发射点东西了,被观察者发射一点东西,给观察者.发射,当然是通过CreateEmitter(里面有observer)的onNext方法,我们进去看一下:

```java
@Override
public void onNext(T t) {
    if (!isDisposed()) {
        //直接调用了观察者的onNext方法
        observer.onNext(t);
    }
}
```

可以看到,直接调用了观察者的onNext方法,也就是我们上面的这部分代码

```java
Log.e("TAG", "onNext():  " + s);
```

然后我们就是我们在示例中调用了`emitter.onComplete();`,看一下源码

```java
@Override
public void onComplete() {
    if (!isDisposed()) {
        try {
            observer.onComplete();
        } finally {
            dispose();
        }
    }
}
```

在里面直接调用了观察者的onComplete方法,然后调用dispose切断.不知不觉中,订阅流程居然分析完了....

感觉分析RxJava源码,很绕.这里我们简单回顾一下,构建了一个ObservableCreate,然后调用其subscribe方法,然后在subscribe方法里面调用onNext方法,然后在onNext方法中其实就是调用了观察者的onNext方法.

借用大佬的一句话:在RxJava中的每个操作符都会在内部创建一个Observable和Observer,但是在`Observable#subscribeActual(Observer)`中都有自己特定的实现,这些实现基本上就是做两个操作,一个是将下游传过来的Observer根据需求进行二次封装,二个就是让上游的Observable订阅(subscribe)该Observer.

基本的流程就是如此,没了.这里,我建议大家跟着源码走几遍(何止几遍,我走了至少15遍......很绕)

## 2. 复杂一点的订阅流程

上面的订阅流程略微有点简单,下面来点复杂点的

```java
Observable.create(new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        emitter.onNext("1111");
        emitter.onNext("2222");
        emitter.onNext("3333");
        emitter.onComplete();
    }
})
.flatMap(new Function<String, ObservableSource<String>>() {
    @Override
    public ObservableSource<String> apply(String s) throws Exception {
        return Observable.just(s + s);
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
        Log.e("TAG", "onError():  ");
    }

    @Override
    public void onComplete() {
        Log.e("TAG", "onComplete():  ");
    }
});
```

1. 首先Observable.create创建的是ObservableCreate,ObservableCreate是一个Observable.
2. 然后flapMap创建的是ObservableFlatMap,也是一个Observable. 

上面这2个都继承了Observable,实现了自己的subscribeActual方法.

然后RxJava的订阅是从subscribe方法开始,所以我们从这里进入.看看上面这些Observable是如何串起来的

```java
Observable#subscribe
public final void subscribe(Observer<? super T> observer) {
    ......
    subscribeActual(observer);
}
```
这里其实就是ObservableFlatMap,然后调用了ObservableFlatMap的subscribeActual方法

```java
@Override
public void subscribeActual(Observer<? super U> t) {
    //这里先是创建了一个MergeObserver,先放着,待会儿看
    //这里的source是构建ObservableFlatMap时传入的ObservableCreate(就是Observable.create构建出来的对象),调用的是上一个Observable的subscribe方法,然后在里面又会调用上一个的subscribeActual方法. 逐级向上传递Observer.  
    source.subscribe(new MergeObserver<T, U>(t, mapper, delayErrors, maxConcurrency, bufferSize));
}
```

这里包装了一个观察者Observer,然后调用了上游Observable(即示例中的ObservableCreate)的subscribe接口.在ObservableCreate里面会调用它的subscribeActual方法.

```java
@Override
protected void subscribeActual(Observer<? super T> observer) {
    //这里传入的observer是ObservableFlatMap里面包装过的MergeObserver
    CreateEmitter<T> parent = new CreateEmitter<T>(observer);
    //所以这里是调用的MergeObserver的onSubscribe方法.然后就会调用MergeObserver的下游onSubscribe 即就是我们自己写的那个Observer了   Log.e("TAG", "onSubscribe():  ");
    observer.onSubscribe(parent);

    try {
        //这个source是我们创建的ObservableOnSubscribe
        source.subscribe(parent);
    } catch (Throwable ex) {
        Exceptions.throwIfFatal(ex);
        parent.onError(ex);
    }
}

```

每一个Observable的subscribeActual实现中都会调用`source.subscribe(parent);`,相当于从下游不断地将观察者往上游传递,直到传递到第一个Observable.

我们在第一个Observable中写了如下代码

```java
emitter.onNext("1111");
emitter.onNext("2222");
emitter.onNext("3333");
emitter.onComplete();
```

这个onNext方法会来到ObservableCreate的onNext,然后又传递给ObservableFlatMap(也是一个Observable)的onNext方法,在ObservableFlatMap的onNext方法中调用了我们实现的apply转换方法.最后ObservableFlatMap调用了我们的Observer的onNext

```java
Log.e(TAG, "onNext: " + s);
```

从第一个Observable开始发送事件,通知这个Observable的下游,然后这个下游继续通知下游,中间可能会存在一些转换,筛选等操作,最终一级一级的传递,就会将事件传递给我们的观察者.这个流程就是订阅流程.


从上游开始创建Observable,每个操作符都会创建一个Observable,创建下游Observable的时候会将上游的Observable引用传递过来.然后到了subscribe订阅处,最下游的Observable就开始将观察者包装起来,然后将观察者向上游传递.直到传递到最上层Observable.

![image](87952AF8B2594DC18EF69FBC624C95A7)

## 3. 线程切换

### 3.1 observeOn原理

先上代码,来个示例

```java
Observable.create(new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        emitter.onNext("1111");
        emitter.onNext("2222");
        emitter.onNext("3333");
        emitter.onComplete();
    }
})
    .observeOn(Schedulers.io())
    .subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.e(TAG, "onSubscribe: ");
        }

        @Override
        public void onNext(String s) {
            Log.e(TAG, "onNext: " + s);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: " + e.getMessage());
        }

        @Override
        public void onComplete() {
            Log.e(TAG, "onComplete: ");
        }
    });
```

先拿出一句话: observeOn方法是切换下游的Observer的线程的.

先点进去observeOn方法,它是Observable的方法,最终都会调用这个3个参数的方法

```java
public final Observable<T> observeOn(Scheduler scheduler, boolean delayError, int bufferSize) {
    ......
    return new ObservableObserveOn<T>(this, scheduler, delayError, bufferSize);
}
```

就是创建了一个ObservableObserveOn,而ObservableObserveOn肯定是一个Observable(每个操作符都会创建一个Observable). 所以我们直奔主题,来到ObservableObserveOn的subscribeActual方法(上面分析过,订阅的时候必经过这里)

```java
@Override
protected void subscribeActual(Observer<? super T> observer) {
    //通过传入的scheduler创建worker
    Scheduler.Worker w = scheduler.createWorker();
    //将观察者包装成一个ObserveOnObserver
    source.subscribe(new ObserveOnObserver<T>(observer, w, delayError, bufferSize));
}
```

ObserveOnObserver是一个观察者,实现了Observer和Runnable接口.来看看经过这个观察者的时候会发生什么

```java
@Override
public void onNext(T t) {
    //加入队列
    queue.offer(t);
    //执行当前Runnable(因为当前观察者是实现了Runnable接口的)
    schedule();
}

void schedule() {
    //执行当前Runnable(因为当前观察者是实现了Runnable接口的)
    worker.schedule(this);
}

@Override
public void run() {
    if (outputFused) {
        drainFused();
    } else {
        drainNormal();
    }
}

void drainNormal() {

    final SimpleQueue<T> q = queue;
    final Observer<? super T> a = downstream;
    
    for (;;) {
        boolean d = done;
        T v;
        
        v = q.poll();

        a.onNext(v);
    }
    
}

```

根据worker去执行当前这个Runnable(当前观察者),在执行到最后的时候看到是调用了onNext方法,也就是说下一层的Observer会在指定的线程中执行,也就达到了切换线程的目的.

因为示例中是`Schedulers.io()`,所以上面的`worker.schedule(this);`里面的worker实际上是一个IoScheduler的内部类EventLoopWorker,它最终会执行

```java
if (delayTime <= 0) {
    f = executor.submit((Callable<Object>)sr);
} else {
    f = executor.schedule((Callable<Object>)sr, delayTime, unit);
}
```
而这里的executor实际上是

```java
Executors.newScheduledThreadPool(1, factory) 
```
也就是说,我们下游的Observer的onNext会被执行到一个子线程(示例是这样的)中.如果说是指定的其他线程,那么肯定就执行在不同的线程中.比如指定的是`Schedulers.single()`那肯定worker的实现就不太一样了,但是原理是类似的,都是将下游的Observer切换到自己指定的线程中执行.

### 3.2 subscribeOn原理

国际惯例,先来段代码

```java
Observable
    .create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            emitter.onNext("1111");
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
```

输出如下
```
onSubscribe: main
观察者所在线程为 RxCachedThreadScheduler-1
被观察者所在的线程 RxCachedThreadScheduler-1
```

subscribeOn是用来切换subscribe线程,我们轻车熟路的点进subscribeOn方法

```java
public final Observable<T> subscribeOn(Scheduler scheduler) {
    return new ObservableSubscribeOn<T>(this, scheduler);
}
```

subscribeOn操作符的Observable是ObservableSubscribeOn,我们看看它的重点方法subscribeActual

```java
@Override
public void subscribeActual(final Observer<? super T> observer) {
    final SubscribeOnObserver<T> parent = new SubscribeOnObserver<T>(observer);

    observer.onSubscribe(parent);

    parent.setDisposable(scheduler.scheduleDirect(new SubscribeTask(parent)));
}
```

其中scheduler.scheduleDirect是和上面的observeOn是差不多的原理,也是搞到一个线程中执行,SubscribeTask(只需要搞懂这个的run方法在搞什么,就知道这个线程切过去是要搞啥)是一个Runnable,它的run方法如下

```java
@Override
public void run() {
    source.subscribe(parent);
}
```

可以看到,就是将subscribe过程放到了一个指定的线程中执行去了,所以订阅和发送事件都是在这个线程中执行的.

## 4. 总结

> 到这里相当于把订阅和线程切换理通了.这篇文章写得不是很好....

observeOn可以多次切换下游的执行环境(onNext,onError,onComplete),调用一次就包装一次,然后将onNext这种扔进去执行;而subscribeOn却只有第一次的有效,因为它是控制订阅那里的环境的,始终是第一个subscribeOn指定的线程subscribe对起作用.

参考:

- https://juejin.im/post/5a209c876fb9a0452577e830
