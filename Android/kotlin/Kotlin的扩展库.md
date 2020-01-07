
kotlin引入协程
```
//                                       👇 依赖协程核心库
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1"
    //                                       👇 依赖当前平台所对应的平台库
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.1"
```

### 1. 协程基本概念

- 通过提升CPU利用率,减少线程切换,进而提升程序运行效率
- 可控制性:协程能做到可被控制的发起子任务
- 轻量级:协程非常小,占用资源比线程还少
- 语法糖:使多任务或多线程切换不再使用回调语法

### 2. 启动协程

- runBlocking:T //用于执行协程任务
- launch:Job //用于执行协程任务
- async/await: Deferred //用于执行协程任务,并得到执行结果

### 3. 用协程简单请求网络

2-3句代码 ok

```
GlobalScope.launch {

    //发起网络请求
    val result = mOkHttpClient.newCall(mRequest).execute().body?.string()

    //这里是子线程哦
    Log.e("xfhy", "ThreadName = ${Thread.currentThread().name} time = ${System.currentTimeMillis()}")

    //阻塞的时候让出CPU 不会阻塞主线程
    withContext(Dispatchers.Main) {
        //回到主线程
        mContentTv.text = result
    }
}
```

### 4. suspend

被suspend修饰的函数只能被有suspend修饰的函数调用,因为suspend修饰的函数(或lambda)被编译之后会多一个参数类型叫Continuation.协程的异步调用本质上就是一次回调.

### 5. KTX

kotlin协助Android开发的扩展库,现已加入Jetpack

