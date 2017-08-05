# 使用OkHttp3遇到的一些坑(一)

> 其实这个我相信很多人都见过....我之前遇到了好几次,也见有人问,在这里简单总结一下我遇到的坑

## 1.调用了2次response.body().string()


代码如下(这里是Kotlin,Java和这个差不多):

	//使用okhttp3访问网络
    val builder = Request.Builder()
    val request = builder.url(NEWS_URL).get().build()
    val response = client.newCall(request).execute()
    val responseBody = response.body()
    val result = responseBody?.string()
    //这里的.string()只能用一次  如果下面那一句不注释的话就会报错
    //val result2 = responseBody?.string()

.string()调用2次就会引来下面错误

	08-04 09:11:32.059 10176-10176/com.xfhy.baseadapterpackage E/AndroidRuntime: FATAL EXCEPTION: main
	   Process: com.xfhy.baseadapterpackage, PID: 10176
	   io.reactivex.exceptions.OnErrorNotImplementedException: closed
		   at io.reactivex.internal.functions.Functions$OnErrorMissingConsumer.accept(Functions.java:704)
		   at io.reactivex.internal.functions.Functions$OnErrorMissingConsumer.accept(Functions.java:701)
		   at io.reactivex.internal.observers.LambdaObserver.onError(LambdaObserver.java:74)
		   at io.reactivex.internal.operators.observable.ObservableObserveOn$ObserveOnObserver.checkTerminated(ObservableObserveOn.java:276)
		   at io.reactivex.internal.operators.observable.ObservableObserveOn$ObserveOnObserver.drainNormal(ObservableObserveOn.java:172)
		   at io.reactivex.internal.operators.observable.ObservableObserveOn$ObserveOnObserver.run(ObservableObserveOn.java:252)
		   at io.reactivex.android.schedulers.HandlerScheduler$ScheduledRunnable.run(HandlerScheduler.java:109)
		   at android.os.Handler.handleCallback(Handler.java:739)
		   at android.os.Handler.dispatchMessage(Handler.java:95)
		   at android.os.Looper.loop(Looper.java:179)
		   at android.app.ActivityThread.main(ActivityThread.java:5739)
		   at java.lang.reflect.Method.invoke(Native Method)
		   at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:784)
		   at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:674)
		Caused by: java.lang.IllegalStateException: closed
		   at okio.RealBufferedSource.rangeEquals(RealBufferedSource.java:398)
		   at okio.RealBufferedSource.rangeEquals(RealBufferedSource.java:392)
		   at okhttp3.internal.Util.bomAwareCharset(Util.java:431)
		   at okhttp3.ResponseBody.string(ResponseBody.java:174)
		   at com.xfhy.baseadapterpackage.MainActivity$initData$1.subscribe(MainActivity.kt:50)
		   at io.reactivex.internal.operators.observable.ObservableCreate.subscribeActual(ObservableCreate.java:40)
		   at io.reactivex.Observable.subscribe(Observable.java:10901)
		   at io.reactivex.internal.operators.observable.ObservableSubscribeOn$SubscribeTask.run(ObservableSubscribeOn.java:96)
		   at io.reactivex.Scheduler$DisposeTask.run(Scheduler.java:452)
		   at io.reactivex.internal.schedulers.ScheduledRunnable.run(ScheduledRunnable.java:61)
		   at io.reactivex.internal.schedulers.ScheduledRunnable.call(ScheduledRunnable.java:52)
		   at java.util.concurrent.FutureTask.run(FutureTask.java:237)
		   at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:154)
		   at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:269)
		   at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1113)
		   at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:588)
		   at java.lang.Thread.run(Thread.java:818)
	
可以看到是Caused by: java.lang.IllegalStateException: closed,可能是在第一次调用之后就关闭了资源.然后我去查看源码:

	public final String string() throws IOException {
	    BufferedSource source = source();
	    try {
	      Charset charset = Util.bomAwareCharset(source, charset());
	      return source.readString(charset);
	    } finally {
	      Util.closeQuietly(source);
	    }
	  }

果然不出我所料,在finally里有一个Util.closeQuietly(source);关闭资源.....怪不得只能调一次呢...下次可要记住啦!!!

## 2.当在无网络的时候用OkHttp3访问网络会抛异常

> 情节还是比较严重的,反正我之前写的时候还没发现,因为之前写的时候判断了当前的网络状态的,昨晚写一个小demo时没有判断,直接就崩了....情况严重

大概的异常信息如下:

	08-04 09:25:17.995 11031-11031/com.xfhy.baseadapterpackage W/System.err: io.reactivex.exceptions.OnErrorNotImplementedException: Unable to resolve host "xfhy-json.oss-cn-shanghai.aliyuncs.com": No address associated with hostname
	08-04 09:25:17.995 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.functions.Functions$OnErrorMissingConsumer.accept(Functions.java:704)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.functions.Functions$OnErrorMissingConsumer.accept(Functions.java:701)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.observers.LambdaObserver.onError(LambdaObserver.java:74)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.operators.observable.ObservableObserveOn$ObserveOnObserver.checkTerminated(ObservableObserveOn.java:276)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.operators.observable.ObservableObserveOn$ObserveOnObserver.drainNormal(ObservableObserveOn.java:172)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.operators.observable.ObservableObserveOn$ObserveOnObserver.run(ObservableObserveOn.java:252)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.android.schedulers.HandlerScheduler$ScheduledRunnable.run(HandlerScheduler.java:109)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at android.os.Handler.handleCallback(Handler.java:739)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at android.os.Handler.dispatchMessage(Handler.java:95)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at android.os.Looper.loop(Looper.java:179)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at android.app.ActivityThread.main(ActivityThread.java:5739)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.lang.reflect.Method.invoke(Native Method)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:784)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:674)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err: Caused by: java.net.UnknownHostException: Unable to resolve host "xfhy-json.oss-cn-shanghai.aliyuncs.com": No address associated with hostname
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.net.InetAddress.lookupHostByName(InetAddress.java:470)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.net.InetAddress.getAllByNameImpl(InetAddress.java:252)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.net.InetAddress.getAllByName(InetAddress.java:215)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.Dns$1.lookup(Dns.java:39)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.connection.RouteSelector.resetNextInetSocketAddress(RouteSelector.java:171)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.connection.RouteSelector.nextProxy(RouteSelector.java:137)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.connection.RouteSelector.next(RouteSelector.java:82)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.connection.StreamAllocation.findConnection(StreamAllocation.java:171)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.connection.StreamAllocation.findHealthyConnection(StreamAllocation.java:121)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.connection.StreamAllocation.newStream(StreamAllocation.java:100)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.connection.ConnectInterceptor.intercept(ConnectInterceptor.java:42)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.cache.CacheInterceptor.intercept(CacheInterceptor.java:93)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.BridgeInterceptor.intercept(BridgeInterceptor.java:93)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.RetryAndFollowUpInterceptor.intercept(RetryAndFollowUpInterceptor.java:120)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.RealCall.getResponseWithInterceptorChain(RealCall.java:185)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at okhttp3.RealCall.execute(RealCall.java:69)
	08-04 09:25:17.996 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at com.xfhy.baseadapterpackage.MainActivity$initData$1.subscribe(MainActivity.kt:47)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.operators.observable.ObservableCreate.subscribeActual(ObservableCreate.java:40)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.Observable.subscribe(Observable.java:10901)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.operators.observable.ObservableSubscribeOn$SubscribeTask.run(ObservableSubscribeOn.java:96)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.Scheduler$DisposeTask.run(Scheduler.java:452)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.schedulers.ScheduledRunnable.run(ScheduledRunnable.java:61)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at io.reactivex.internal.schedulers.ScheduledRunnable.call(ScheduledRunnable.java:52)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.util.concurrent.FutureTask.run(FutureTask.java:237)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:154)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:269)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1113)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:588)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.lang.Thread.run(Thread.java:818)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err: Caused by: android.system.GaiException: android_getaddrinfo failed: EAI_NODATA (No address associated with hostname)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at libcore.io.Posix.android_getaddrinfo(Native Method)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at libcore.io.ForwardingOs.android_getaddrinfo(ForwardingOs.java:55)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err:     at java.net.InetAddress.lookupHostByName(InetAddress.java:451)
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage W/System.err: 	... 35 more
	08-04 09:25:17.997 11031-11031/com.xfhy.baseadapterpackage E/AndroidRuntime: FATAL EXCEPTION: main
	                                                                                       Process: com.xfhy.baseadapterpackage, PID: 11031

所以,在使用OkHttp3访问网络的时候还是判断一下当前的网络状态比较好.

判断网络状态的代码如下:


