> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/811ba49d0748

前面一篇文章讲了一下 Retrofit+ RxJava 请求网络的一些基本用法，还没有看过的可以去看一下 [Retrofit + RxJava ＋ OkHttp 让网络请求变的简单 - 基础篇](https://www.jianshu.com/p/5bc866b9cbb9), 正如标题所说的，Retrofit+RxJava 是让我们的网络请求变得简单，代码精简。通过前一篇文章，我们感觉写一个请求还是有点麻烦，作为程序员，我们的目标就是 “偷懒”，绝不重复搬砖。因此我们还需要封装一下，来简化我们使用，接下来进入正题。

### 一，创建一个统一生成接口实例的管理类 RetrofitServiceManager

我们知道，每一个请求，都需要一个接口，里面定义了请求方法和请求参数等等，而获取接口实例需要通过一个 Retrofit 实例, 这一步都是相同的，因此，我们可以把这些相同的部分抽取出来，代码如下：

```
/*
* 
* Created by zhouwei on 16/11/9\. 
*/
public class RetrofitServiceManager { 
   private static final int DEFAULT_TIME_OUT = 5;//超时时间 5s    
  private static final int DEFAULT_READ_TIME_OUT = 10;    
  private Retrofit mRetrofit;   
  private RetrofitServiceManager(){  
      // 创建 OKHttpClient      
  OkHttpClient.Builder builder = new OkHttpClient.Builder();      
  builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间        builder.writeTimeout(DEFAULT_READ_TIME_OUT,TimeUnit.SECONDS);//写操作 超时时间        
  builder.readTimeout(DEFAULT_READ_TIME_OUT,TimeUnit.SECONDS);//读操作超时时间  
      // 添加公共参数拦截器        
HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder() 
               .addHeaderParams("paltform","android") 
               .addHeaderParams("userToken","1234343434dfdfd3434") 
               .addHeaderParams("userId","123445")      
               .build();        
builder.addInterceptor(commonInterceptor);    
    // 创建Retrofit        
mRetrofit = new Retrofit.Builder() 
               .client(builder.build())  
              .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) 
               .addConverterFactory(GsonConverterFactory.create()) 
               .baseUrl(ApiConfig.BASE_URL)   
               .build();   
 } 
   private static class SingletonHolder{
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }
    /**
     * 获取RetrofitServiceManager
     * @return
     */   
 public static RetrofitServiceManager getInstance(){  
      return SingletonHolder.INSTANCE; 
   }  
  /** 
    * 获取对应的Service 
    * @param service Service 的 class     
    * @param <T>    
    * @return  
    */  
  public <T> T create(Class<T> service){ 
       return mRetrofit.create(service);    
}
}

```

> 说明：创建了一个 RetrofitServiceManager 类，该类采用单例模式，在私有的构造方法中，生成了 Retrofit 实例，并配置了 OkHttpClient 和一些公共配置。提供了一个 create（）方法，生成接口实例，接收 Class 范型，因此项目中所有的接口实例 Service 都可以用这个来生成，代码如下：

```
mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);

```

通过 create() 方法生成了一个 MovieService

### 二，创建接口，通过第一步获取实例

上面已经有了可以获取接口实例的方法因此我们需要创建一个接口，代码如下：

```
public interface MovieService{  
  //获取豆瓣Top250 榜单   
 @GET("top250")    
Observable<MovieSubject> getTop250(@Query("start") int start, @Query("count")int count);   

 @FormUrlEncoded    
@POST("/x3/weather")   
 Call<String> getWeather(@Field("cityId") String cityId, @Field("key") String key);
}

```

好了，有了接口我们就可以获取到接口实例了 mMovieService

### 三，创建一个业务 Loader ，如 XXXLoder, 获取 Observable 并处理相关业务

解释一下为什么会出现 Loader ，我看其他相关文章说，每一个 Api 都写一个接口，我觉得这样很麻烦，因此就把请求逻辑封装在在一个业务 Loader 里面，一个 Loader 里面可以处理多个 Api 接口。代码如下：

```
/*
 *
 * Created by zhouwei on 16/11/10\. 
 */
public class MovieLoader extends ObjectLoader { 
   private MovieService mMovieService; 
   public MovieLoader(){  
      mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);
    }  
  /** 
    * 获取电影列表 
    * @param start  
    * @param count    
    * @return    
    */  
  public Observable<List<Movie>> getMovie(int start, int count){  
      return observe(mMovieService.getTop250(start,count)) 
               .map(new Func1<MovieSubject, List<Movie>>() {   
         @Override 
           public List<Movie> call(MovieSubject movieSubject) {   
             return movieSubject.subjects;     
       }   
     }); 
   }   

public Observable<String> getWeatherList(String cityId,String key){    
        return observe(mMovieService.getWeather(cityId,key))
       .map(new Func1<String, String>() {     
       @Override      
       public String call(String s) {
           //可以处理对应的逻辑后在返回
            return s;    
       } 
     });
}

 public interface MovieService{   
      //获取豆瓣Top250 榜单  
      @GET("top250")       
     Observable<MovieSubject> getTop250(@Query("start") int start, @Query("count")int count);   

     @FormUrlEncoded   
     @POST("/x3/weather")    
    Call<String> getWeather(@Field("cityId") String cityId, @Field("key") String key);   
 }
}

```

创建一个 MovieLoader, 构造方法中生成了 mMovieService, 而 Service 中可以定义和业务相关的多个 api, 比如：例子中的 MovieService 中，
可以定义和电影相关的多个 api, 获取电影列表、获取电影详情、搜索电影等 api，就不用定义多个接口了。

上面的代码中，MovieLoader 是从 ObjectLoader 中继承下来的，ObjectLoader 提取了一些公共的操作。代码如下：

```
/** 
 *
 * 将一些重复的操作提出来，放到父类以免Loader 里每个接口都有重复代码 
 * Created by zhouwei on 16/11/10.
 * 
 */
public class ObjectLoader {   
 /**
   * 
   * @param observable     
   * @param <T>   
   * @return    
   */   
 protected  <T> Observable<T> observe(Observable<T> observable){    
    return observable
      .subscribeOn(Schedulers.io())          
      .unsubscribeOn(Schedulers.io())  
      .observeOn(AndroidSchedulers.mainThread());  
  }
}

```

相当于一个公共方法，其实也可以放在一个工具类里面，后面做缓存的时候会用到这个父类，所以就把这个方法放到父类里面。

### 四，Activity/Fragment 中的调用

创建 Loader 实例

```
mMovieLoader = new MovieLoader();

```

通过 Loader 调用方法获取结果, 代码如下：

```
/*
 *
 * 获取电影列表 
 */
private void getMovieList(){ 
   mMovieLoader.getMovie(0,10).subscribe(new Action1<List<Movie>>() {   
     @Override   
     public void call(List<Movie> movies) {   
         mMovieAdapter.setMovies(movies);        
         mMovieAdapter.notifyDataSetChanged();      
        } 
   }, new Action1<Throwable>() {    
    @Override       
    public void call(Throwable throwable) {    
        Log.e("TAG","error message:"+throwable.getMessage());     
      }  
  });
}

```

以上就完成请求过程的封装，现在添加一个新的请求，只需要添加一个业务 Loader 类，然后通过 Loader 调用方法获取结果就行了，是不是方便了很多？但是在实际项目中这样是不够的，还能做进一步简化。

### 五，统一处理结果和错误

1, 统一处理请求结果

现实项目中，所有接口的返回结果都是同一格式，如：

```
 {
 "status": 200,
 "message": "成功",
 "data": {}
}

```

我们在请求 api 接口的时候，只关心我们想要的数据，也就上面的 data, 其他的东西我们不太关心，请求失败的时候可以根据 status 判断进行错误处理，所以我们需要包装一下。首先需要根据服务端定义的 JSON 结构创建一个 BaseResponse 类，代码如下：

```
/*
*
* 
* 网络请求结果 基类 
* Created by zhouwei on 16/11/10\. 
*/
public class BaseResponse<T> {   
  public int status;  
  public String message;    
  public T data;    
public boolean isSuccess(){   
     return status == 200;  
  }
}

```

有了统一的格式数据后，我们需要剥离出 data 返回给上层调用者，创建一个 PayLoad 类，代码如下：

```
/*
* 
*
* 剥离 最终数据 
* Created by zhouwei on 16/11/10\. 
*/
public class PayLoad<T> implements Func1<BaseResponse<T>,T>{    
@Override 
   public T call(BaseResponse<T> tBaseResponse) {//获取数据失败时，包装一个Fault 抛给上层处理错误
        if(!tBaseResponse.isSuccess()){ 
           throw new Fault(tBaseResponse.status,tBaseResponse.message);  
      }    
    return tBaseResponse.data;  
  }
}

```

PayLoad 继承自 Func1, 接收一个 BaseResponse<T> , 就是接口返回的 JSON 数据结构，返回的是 T, 就是 data, 判断是否请求成功，请求成功返回 Data, 请求失败包装成一个 Fault 返回给上层统一处理错误。在 Loader 类里面获取结果后，通过 map 操作符剥离数据。代码如下：

```
public Observable<List<Movie>> getMovie(int start, int count){ 
 return observe(mMovieService.getTop250(start,count))        
  .map(new PayLoad<BaseResponse<List<Movie>>>());
}

```

2，统一处理错误

在 PayLoad 类里面，请求失败时，抛出了一个 Fault 异常给上层，我在 Activity/Fragment 中拿到这个异常，然后判断错误码，进行异常处理。在 onError () 中添加代码如下：

```
public void call(Throwable throwable) {  
  Log.e("TAG","error message:"+throwable.getMessage());  
  if(throwable instanceof Fault){     
   Fault fault = (Fault) throwable;    
    if(fault.getErrorCode() == 404){     
       //错误处理 
       }else if(fault.getErrorCode() == 500){   
         //错误处理  
      }else if(fault.getErrorCode() == 501){      
      //错误处理   
     }  
  }
}

```

以上就可以对应错误码处理相应的错误了。

### 六，添加公共参数

在实际项目中，每个接口都有一些基本的相同的参数，我们称之为公共参数，比如：userId、userToken、userName,deviceId 等等，我们不必要，每个接口都去写，这样就太麻烦了，因此我们可以写一个拦截器，在拦截器里面拦截请求，为每个请求都添加相同的公共参数。拦截器代码如下：

```
/*
 *
 * 拦截器
 *
 * 向请求头里添加公共参数 
 * Created by zhouwei on 16/11/10\. 
 */
public class HttpCommonInterceptor implements Interceptor {    
private Map<String,String> mHeaderParamsMap = new HashMap<>();  
  public HttpCommonInterceptor() {   
 }    
@Override
    public Response intercept(Chain chain) throws IOException {    
    Log.d("HttpCommonInterceptor","add common params");     
   Request oldRequest = chain.request();    
    // 添加新的参数，添加到url 中  
     /* HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()                .newBuilder()       
         .scheme(oldRequest.url().scheme())   
             .host(oldRequest.url().host());*/ 
       // 新的请求   
     Request.Builder requestBuilder =  oldRequest.newBuilder(); 
       requestBuilder.method(oldRequest.method(), 
oldRequest.body()); 

       //添加公共参数,添加到header中        
if(mHeaderParamsMap.size() > 0){       
     for(Map.Entry<String,String> params:mHeaderParamsMap.entrySet()){  
              requestBuilder.header(params.getKey(),params.getValue());       
         }    
  }    
    Request newRequest = requestBuilder.build();   
     return chain.proceed(newRequest);  
  }  
  public static class Builder{      
  HttpCommonInterceptor mHttpCommonInterceptor;    
    public Builder(){      
      mHttpCommonInterceptor = new HttpCommonInterceptor();     
   }     
   public Builder addHeaderParams(String key, String value){      
       mHttpCommonInterceptor.mHeaderParamsMap.put(key,value);   
       return this;   
    }       
 public Builder  addHeaderParams(String key, int value){   
         return addHeaderParams(key, String.valueOf(value)); 
       }        
public Builder  addHeaderParams(String key, float value){ 
           return addHeaderParams(key, String.valueOf(value));  
      }      
  public Builder  addHeaderParams(String key, long value){  
          return addHeaderParams(key, String.valueOf(value));      
  }    
    public Builder  addHeaderParams(String key, double value){    
        return addHeaderParams(key, String.valueOf(value));    
    }    
    public HttpCommonInterceptor build(){ 
           return mHttpCommonInterceptor;     
   } 
   }
}

```

以上就是添加公共参数的拦截器，在 RetrofitServiceManager 类里面加入 OkHttpClient 配置就好了。代码如下：

```
// 添加公共参数拦截器
HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder()     
       .addHeaderParams("paltform","android")   
       .addHeaderParams("userToken","1234343434dfdfd3434") 
       .addHeaderParams("userId","123445")      
       .build();
builder.addInterceptor(commonInterceptor);

```

这样每个请求都添加了公共参数。

** 好了，以上一个简易的网络请求库就封装得差不多了，完整代码请戳 [Retrofit + RxJava +OkHttp 简易封装](https://link.jianshu.com?t=https://github.com/pinguo-zhouwei/RetrofitRxJavaDemo)基本上能满足项目中的网络请求，由于项目中暂时没有文件上传下载的需求，这一块还没有添加，后面有时间会补充这一块的东西。**

封装的类放在 http 包下：

![](https://upload-images.jianshu.io/upload_images/3513995-5619a67dc391a996.png)

最后放几张 Demo 示例的效果图：（数据来自干货集中营）
重点是看妹纸！！！（滑稽脸）

![](https://upload-images.jianshu.io/upload_images/3513995-357256326a146a08.png)

电影列表：（数据来自豆瓣）

![](https://upload-images.jianshu.io/upload_images/3513995-542e3f7caa70d624.png)

** 以上就是封装的全部内容，还没有用 Retrofit 的，赶快用上它来改造你想网络请求库吧！！！ **

参考博客：
[RxJava 与 Retrofit 结合的最佳实践](https://link.jianshu.com?t=http://gank.io/post/56e80c2c677659311bed9841)