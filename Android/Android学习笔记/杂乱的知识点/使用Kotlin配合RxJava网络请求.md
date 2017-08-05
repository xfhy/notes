# 使用 Kotlin 配合 RxJava 进行网络请求

## 1.首先需要配置Kotlin在项目中

- Android Studio 3.0是自带的,可以在创建项目的时候勾选include kotlin support
- Android Studio 2.0+是需要自己配置的,首先需要安装Kotlin插件,然后在下图这里配置一下,Configure Kotlin in Project

![](http://olg7c0d2n.bkt.clouddn.com/17-8-4/72052612.jpg)

如果在配置了Kotlin,然后gradle 构建时下载慢,可以看看这里 [解决Android Studio配置完Kotlin下载慢的问题](http://blog.csdn.net/xfhy_/article/details/76628292)

## 2.引入第3方库

> 我这里使用的是 OkHttp3,RxJava2,RxAndroid

引入如下(在我使用时,这是最新的版本):

- implementation 'com.squareup.okhttp3:okhttp:3.8.1'
- implementation 'io.reactivex.rxjava2:rxjava:2.1.2'
- implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
- implementation 'com.google.code.gson:gson:2.8.1'
- implementation 'com.github.bumptech.glide:glide:4.0.0'

## 3.正式开始写代码

在MainActivity中写入如下代码

		//使用RxJava处理
        Observable.create(ObservableOnSubscribe<String> {
            e ->

            //使用okhttp3访问网络
            val builder = Request.Builder()
            val request = builder.url(NEWS_URL).get().build()
            val response = client.newCall(request).execute()
            val responseBody = response.body()
            val result = responseBody?.string()
            //这里的.string()只能用一次  如果下面那一句不注释的话就会报错
            //val result2 = responseBody?.string()

            Log.e(TAG, result)

            //这里其实形参是String类型,然而实参是String?类型,如果直接传result会报错,在后面加!!即可解决
            //发射(这里是被观察者,被观察者发射事件)
            e.onNext(result!!)
			
			//上面那句代码可以这样写
			//e.onNext(result as String)
        }).subscribeOn(Schedulers.io())  //io线程  被观察者
                .observeOn(AndroidSchedulers.mainThread())  //主线程 观察者
                .subscribe({

                    //这里接收刚刚被观察者发射的事件
					//这个response就是io线程发射过来的result
                    response ->
                    Log.e(TAG, response)
                })

## 简简单单的总结

看似代码极少的demo,可是我却遇到了很多的挫折,,搞了好久好久才写好,主要是遇到错误的话,这种东西网上不好找解决方案....
