> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/5bc866b9cbb9

最近因为手头上的工作做完了，比较闲，想着做一些优化。看到以前用的那一套网络框架添加一个请求比较麻烦，并且比较难用，所以想改造一下网络框架。现在 Android 市面上很火的当然是 Retrofit＋RxJava + OkHttp, 功能强大，简单易用，因此选用这套方案来改造网络库。本篇文章是对 Retrofit 的基本使用方法做一些简单的介绍。后面会再写一篇 Retrofit + RxJava + OkHttp 的封装过程。以下是正文。

##### 简介：

Retrofit: Retrofit 是 Square 公司开发的一款正对 Android 网络请求的框架。底层基于 OkHttp 实现，OkHttp 已经得到了 google 官方的认可。[Retrofit 官网](https://link.jianshu.com?t=http://square.github.io/retrofit/)

OkHttp: 也是 Square 开源的网络请求库

RxJava:RxJava 在 GitHub 主页上的自我介绍是 "a library for composing asynchronous and event-based programs using observable sequences for the Java VM"（一个在 Java VM 上使用可观测的序列来组成异步的、基于事件的程序的库）。这就是 RxJava ，概括得非常精准。总之就是让异步操作变得非常简单。

各自的职责：Retrofit 负责请求的数据和请求的结果，使用接口的方式呈现，OkHttp 负责请求的过程，RxJava 负责异步，各种线程之间的切换。

RxJava + Retrofit + okHttp 已成为当前 Android 网络请求最流行的方式。

##### 一，Retrofit 写一个网络请求

以获取豆瓣 Top250 榜单为例，地址：[https://api.douban.com/v2/movie/](https://link.jianshu.com?t=https://api.douban.com/v2/movie/)

1，首先，要使用 Retrofit , 你肯定需要把它的包引入，在你的 build.gradle 文件中添加如下配置：

```
 compile 'com.squareup.retrofit2:retrofit:2.1.0'//retrofit 
 compile 'com.google.code.gson:gson:2.6.2'//Gson 库 
//下面两个是RxJava 和RxAndroid 
compile 'io.reactivex:rxjava:1.1.0' 
compile 'io.reactivex:rxandroid:1.1.0'  
compile 'com.squareup.retrofit2:converter-gson:2.1.0'//转换器，请求结果转换成Model 
compile 'com.squareup.retrofit2:adapter-rxjava:2.1.0'//配合Rxjava 使用

```

2, 创建一个 Retrofit 实例，并且完成相关的配置

```
public static final String BASE_URL = "https://api.douban.com/v2/movie/";
Retrofit retrofit = new Retrofit.Builder() 
       .baseUrl(BASE_URL) 
       .addConverterFactory(GsonConverterFactory.create())
       .build();

```

> 说明：配置了接口的 baseUrl 和一个 converter,GsonConverterFactory 是默认提供的 Gson 转换器，Retrofit 也支持其他的一些转换器，详情请看官网 [Retrofit 官网](https://link.jianshu.com?t=http://square.github.io/retrofit/)

3，创建一个 接口 ，代码如下：

```
public interface MovieService { 

 //获取豆瓣Top250 榜单 
 @GET("top250")
 Call<MovieSubject> getTop250(@Query("start") int start,@Query("count")int count);

}

```

> 说明：定义了一个方法 getTop250, 使用 get 请求方式，加上 @GET 标签，标签后面是这个接口的 尾址 top250, 完整的地址应该是 baseUrl + 尾址 ，参数 使用 @Query 标签，如果参数多的话可以用 @QueryMap 标签，接收一个 Map

4，用 Retrofit 创建 接口实例 MoiveService, 并且调用接口中的方法进行网络请求，代码如下：

```
//获取接口实例
MovieService MovieService movieService = retrofit.create(MovieService.class); 
//调用方法得到一个Call 
Call<MovieSubject> call = movieService.getTop250(0,20);
 //进行网络请求 
call.enqueue(new Callback<MovieSubject>() {
       @Override 
       public void onResponse(Call<MovieSubject> call, Response<MovieSubject> response) { 
            mMovieAdapter.setMovies(response.body().subjects);     
            mMovieAdapter.notifyDataSetChanged(); 
       } 
      @Override 
      public void onFailure(Call<MovieSubject> call, Throwable t) { 
         t.printStackTrace(); 
      } 
});

```

以上是异步方式请求，还有同步方式 execute(), 返回一个 Response, 代码如下：

```
Response<MovieSubject> response = call.execute();

```

以上就是用 Retrofit 完成了一个网络请求，获取豆瓣 top250 榜单电影，效果图如下：

![](https://upload-images.jianshu.io/upload_images/3513995-a52d026b75265774.png)

以上示例是用 get 方式完成，如果要使用 post 方式，我们只需要修改一下接口中的方法定义，如下：

```
public interface MovieService { 
        //获取豆瓣Top250 榜单 
       @FormUrlEncoded
       @POST("top250") 
       Call<MovieSubject> getTop250(@Field("start") int start, @Field("count") int count);
}

```

> 说明：使用 POST 请求方式时，只需要更改方法定义的标签，用 @POST 标签，参数标签用 @Field 或者 @Body 或者 FieldMap，**注意：使用 POST 方式时注意 2 点，1，必须加上 @FormUrlEncoded 标签，否则会抛异常。2，使用 POST 方式时，必须要有参数，否则会抛异常,** 源码抛异常的地方如下：

```
if (isFormEncoded && !gotField) { 
      throw methodError("Form-encoded method must contain at least one @Field."); 
}

```

**以上就是一个使用 Retrofit 完成一个网络请求的完整示例，其他标签使用方式请看官网 [Retrofit 官网](https://link.jianshu.com?t=http://square.github.io/retrofit/)，官网用法也介绍的比较详细，此外，发现了一篇博客也介绍得比较详细，[Retrofit 用法详解](https://link.jianshu.com?t=http://duanyytop.github.io/2016/08/06/Retrofit%E7%94%A8%E6%B3%95%E8%AF%A6%E8%A7%A3/)**

##### 二，配合 RxJava 使用

1, 更改定义的接口，返回值不再是一个 Call , 而是返回的一个 Observble.

```
public interface MovieService { 
    //获取豆瓣Top250 榜单  
    @GET("top250") 
    Observable<MovieSubject> getTop250(@Query("start") int start, @Query("count")int count);
 }

```

2, 创建 Retrofit 的时候添加如下代码

```
addCallAdapterFactory(RxJavaCallAdapterFactory.create())

```

3, 添加转换器 Converter(将 json 转为 JavaBean)

```
addConverterFactory(GsonConverterFactory.create())

```

4,Activity 或者 Fragment 中传入 Subscriber 建立订阅关系

```
Subscription subscription = movieService.getTop250(0,20) 
.subscribeOn(Schedulers.io()) 
.observeOn(AndroidSchedulers.mainThread())
.subscribe(new Subscriber<MovieSubject>() { 
@Override
 public void onCompleted() { 

 } 
@Override 
public void onError(Throwable e) { 

} 
@Override
 public void onNext(MovieSubject movieSubject) { 
        mMovieAdapter.setMovies(movieSubject.subjects); 
        mMovieAdapter.notifyDataSetChanged(); 
   } 
});

```

以上是加入 RxJava 后的网络请求，返回不再是一个 Call , 而是一个 Observable, 在 Activity / Fragment 中传入一个 Subscriber 建立订阅关系，就可以在 onNext 中处理结果了，RxJava 的好处是帮我处理线程之间的切换，我们可以在指定订阅的在哪个线程，观察在哪个线程。我们可以通过操作符进行数据变换。整个过程都是链式的，简化逻辑。其中 FlatMap 操作符 还可以解除多层嵌套的问题。总之，RxJava 很强大，能帮我处理很多复杂的场景，如果熟练使用的话，那么能提升我们的开发效率。这里不打算讲 RxJava 的内容，如果还不了解 RxJava , 或者还对 RxJava 不熟悉的话，推荐几篇写很优秀的博客。

1，RxJava 的经典文章, 扔物线的 [给 Android 开发者的 RxJava 详解](https://link.jianshu.com?t=http://gank.io/post/560e15be2dca930e00da1083)
2，[关于 RxJava 友好的文章](https://link.jianshu.com?t=http://gold.xitu.io/post/580103f20e3dd90057fc3e6d)
3，[关于 RxJava 友好的文章－进阶](https://link.jianshu.com?t=http://gold.xitu.io/post/5818777f67f356005871ef2c)

##### 三，加入 OkHttp 配置

通过 OkHttpClient 可以配置很多东西，比如链接超时时间，缓存，拦截器等等。代码如下：

```
// 创建 OKHttpClient 
OkHttpClient.Builder builder = new OkHttpClient.Builder(); 
     builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间 
     builder.writeTimeout(DEFAULT_TIME_OUT,TimeUnit.SECONDS);//写操作 超时时间 
     builder.readTimeout(DEFAULT_TIME_OUT,TimeUnit.SECONDS);//读操作超时时间 

  // 添加公共参数拦截器 
BasicParamsInterceptor basicParamsInterceptor = new BasicParamsInterceptor.Builder() 
    .addHeaderParam("userName","")//添加公共参数 
    .addHeaderParam("device","") 
    .build(); 

 builder.addInterceptor(basicParamsInterceptor); 

// 创建Retrofit
 mRetrofit = new Retrofit.Builder() 
     .client(builder.build()) 
     .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) 
     .addConverterFactory(GsonConverterFactory.create()) 
     .baseUrl(ApiConfig.BASE_URL) 
     .build();

```

以上只是配置了一些简单的项，如，连接超时时间，实际项目中，我们可能有一些公共的参数，如 ，设备信息，渠道，Token 之类的，每个接口都需要用，我们可以写一个拦截器，然后配置到 OKHttpClient 里，通过 builder.addInterceptor(basicParamsInterceptor) 添加，这样我们就不用每个接口都添加这些参数了。缓存也可以通过写一个拦截器来实现（后面文章再讲）。

**以上就是 Retrofit+RxJava＋OkHttp 实现网络请求的简单演示，如果每个接口都这么写的话，代码量太多，而且不优雅。所以还需要我们封装一下，由于篇幅有限，封装放到下一篇文章。**

** Retrofit + RxJava + OkHttp 封装已更新，请看 [Retrofit + RxJava ＋ OkHttp 让网络请求变的简单 - 封装篇](https://www.jianshu.com/p/811ba49d0748) **

参考博客：
1，[Retrofit 用法详解](https://link.jianshu.com?t=http://duanyytop.github.io/2016/08/06/Retrofit%E7%94%A8%E6%B3%95%E8%AF%A6%E8%A7%A3/)
2，[基于 Retrofit、OkHttp、Gson 封装通用网络框架](https://www.jianshu.com/p/e736c15ce2f8)
3, [RxJava 与 Retrofit 结合的最佳实践](https://link.jianshu.com?t=http://gank.io/post/56e80c2c677659311bed9841)