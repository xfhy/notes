
### 1.Retrofit的创建过程

```
retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(CustomerOkHttpClient.getClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
```

典型的建造者模式,这不用多说.来看看Builder()中干了什么.

```
public Builder() {
  this(Platform.get());
}
```
```
private static final Platform PLATFORM = findPlatform();

  static Platform get() {
    return PLATFORM;
  }

  private static Platform findPlatform() {
    try {
      Class.forName("android.os.Build");
      if (Build.VERSION.SDK_INT != 0) {
        return new Android();
      }
    } catch (ClassNotFoundException ignored) {
    }
    try {
      Class.forName("java.util.Optional");
      return new Java8();
    } catch (ClassNotFoundException ignored) {
    }
    return new Platform();
  }
```

最终调用的是findPlatform方法，根据不同的运行平台来提供不同的线程池。

接下来看看build()方法

```java
public Retrofit build() {
  //必须有baseurl
  if (baseUrl == null) {
    throw new IllegalStateException("Base URL required.");
  }

 //没有设置就直接搞了一个OkHttpClient
  okhttp3.Call.Factory callFactory = this.callFactory;
  if (callFactory == null) {
    callFactory = new OkHttpClient();
  }

 //用来将回调传递到UI线程
  Executor callbackExecutor = this.callbackExecutor;
  if (callbackExecutor == null) {
    callbackExecutor = platform.defaultCallbackExecutor();
  }

  // Make a defensive copy of the adapters and add the default Call adapter.
  //主要用于存储对Call进行转化的对象
  List<CallAdapter.Factory> adapterFactories = new ArrayList<>(this.adapterFactories);
  adapterFactories.add(platform.defaultCallAdapterFactory(callbackExecutor));

  // Make a defensive copy of the converters.
  //主要用于存储转化数据对象
  List<Converter.Factory> converterFactories = new ArrayList<>(this.converterFactories);

  return new Retrofit(callFactory, baseUrl, converterFactories, adapterFactories,
      callbackExecutor, validateEagerly);
}
```
这里的build()方法就和我们平时写的差不多嘛,但是,有一点需要注意一下,baseUrl不能为null(baseUrl都为null了还请求个毛啊).其他的非必要参数,当我们没有传入时都是取的默认值.

### 2. Call的创建过程

下面我们创建Retrofit的实例并调用如下代码来生成接口的动态代理对象:

```
IpService ipService=retrofit.create(IpService.class);
```
我们来看一下create()方法做了什么:

```
public <T> T create(final Class<T> service) {
    //1. 判断是否是interface
    Utils.validateServiceInterface(service);
    if (validateEagerly) {
      eagerlyValidateMethods(service);
    }
    //动态代理
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
          private final Platform platform = Platform.get();

          @Override public Object invoke(Object proxy, Method method, @Nullable Object[] args)
              throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            }
            if (platform.isDefaultMethod(method)) {
              return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            ServiceMethod<Object, Object> serviceMethod =
                (ServiceMethod<Object, Object>) loadServiceMethod(method);
            OkHttpCall<Object> okHttpCall = new OkHttpCall<>(serviceMethod, args);
            //adapt 方法会创建ExecutorCallbackCall，它会将call的回调转发至UI线程。
            return serviceMethod.callAdapter.adapt(okHttpCall);
          }
        });
  }
```

可以看到  create  方法返回了一个  Proxy.newProxyInstance  动态代理对象。当我们调用IpService的
getIpMsg方法时，最终会调用InvocationHandler的invoke方法。它有三个参数：第一个是代理对象，第二个
是调用的方法，第三个是方法的参数。在上面代码注释 1 处loadServiceMethod（method）中的method就是
我们定义的getIpMsg方法。下面查看loadServiceMethod方法里做了什么：

```
ServiceMethod<?, ?> loadServiceMethod(Method method) {
    //有缓存->取缓存
    ServiceMethod<?, ?> result = serviceMethodCache.get(method);
    if (result != null) return result;

    synchronized (serviceMethodCache) {
      result = serviceMethodCache.get(method);
      if (result == null) {
        result = new ServiceMethod.Builder<>(this, method).build();
        serviceMethodCache.put(method, result);
      }
    }
    return result;
  }
```
这里首先会从 serviceMethodCache 查询传入的方法是否有缓存。如果有，就用缓存的ServiceMethod；
如果没有，就创建一个，并加入 serviceMethodCache 缓存起来。下面看ServiceMethod是如何构建的，代码
如下所示：

```
public ServiceMethod build() {
     //get方法会得到CallAdapter对象，CallAdapter的responseType方法会返回数据的真实类型，比如 传入的是Call＜IpModel＞，responseType 方法就会返回 IpModel。
      callAdapter = createCallAdapter();
      responseType = callAdapter.responseType();
      if (responseType == Response.class || responseType == okhttp3.Response.class) {
        throw methodError("'"
            + Utils.getRawType(responseType).getName()
            + "' is not a valid response body type. Did you mean ResponseBody?");
      }
      
      /*createResponseConverter  方法来遍历converterFactories列表中存储的
        Converter.Factory，并返回一个合适的Converter用来转换对象。此前我们在构建Retrofit时调用了
        addConverterFactory（GsonConverterFactory.create（）），这段代码将
        GsonConverterFactory（Converter.Factory的子类）添加到converterFactories列表中，表示返回的数据支持转
        换为JSON对象。
        */
      responseConverter = createResponseConverter();

     //读取注解,进行注解解析
      for (Annotation annotation : methodAnnotations) {
        parseMethodAnnotation(annotation);
      }

      if (httpMethod == null) {
        throw methodError("HTTP method annotation is required (e.g., @GET, @POST, etc.).");
      }

      if (!hasBody) {
        if (isMultipart) {
          throw methodError(
              "Multipart can only be specified on HTTP methods with request body (e.g., @POST).");
        }
        if (isFormEncoded) {
          throw methodError("FormUrlEncoded can only be specified on HTTP methods with "
              + "request body (e.g., @POST).");
        }
      }

      int parameterCount = parameterAnnotationsArray.length;
      parameterHandlers = new ParameterHandler<?>[parameterCount];
      for (int p = 0; p < parameterCount; p++) {
        Type parameterType = parameterTypes[p];
        if (Utils.hasUnresolvableType(parameterType)) {
          throw parameterError(p, "Parameter type must not include a type variable or wildcard: %s",
              parameterType);
        }

        Annotation[] parameterAnnotations = parameterAnnotationsArray[p];
        if (parameterAnnotations == null) {
          throw parameterError(p, "No Retrofit annotation found.");
        }

        parameterHandlers[p] = parseParameter(p, parameterType, parameterAnnotations);
      }

      if (relativeUrl == null && !gotUrl) {
        throw methodError("Missing either @%s URL or @Url parameter.", httpMethod);
      }
      if (!isFormEncoded && !isMultipart && !hasBody && gotBody) {
        throw methodError("Non-body HTTP method cannot contain @Body.");
      }
      if (isFormEncoded && !gotField) {
        throw methodError("Form-encoded method must contain at least one @Field.");
      }
      if (isMultipart && !gotPart) {
        throw methodError("Multipart method must contain at least one @Part.");
      }

      return new ServiceMethod<>(this);
    }
```

在build()方法里面做了一些初始化,回调、返回值、注解解析等.

接下来回过头来查看Retrofit的create方法（在第264页），在调用了loadServiceMethod方法后会创建
OkHttpCall，OkHttpCall  的构造方法只是进行了赋值操作。紧接着调用
serviceMethod.callAdapter.adapt（okHttpCall）。callAdapter  的  adapt  方法前面讲过，它会创建
ExecutorCallbackCall，并传入OkHttpCall。ExecutorCallbackCall的部分代码如下所示：

```
 static final class ExecutorCallbackCall<T> implements Call<T> {
    final Executor callbackExecutor;
    final Call<T> delegate;

    ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
      this.callbackExecutor = callbackExecutor;
      this.delegate = delegate;
    }

    @Override public void enqueue(final Callback<T> callback) {
      checkNotNull(callback, "callback == null");

      delegate.enqueue(new Callback<T>() {
        @Override public void onResponse(Call<T> call, final Response<T> response) {
          callbackExecutor.execute(new Runnable() {
            @Override public void run() {
              if (delegate.isCanceled()) {
                // Emulate OkHttp's behavior of throwing/delivering an IOException on cancellation.
                callback.onFailure(ExecutorCallbackCall.this, new IOException("Canceled"));
              } else {
                callback.onResponse(ExecutorCallbackCall.this, response);
              }
            }
          });
        }

        @Override public void onFailure(Call<T> call, final Throwable t) {
          callbackExecutor.execute(new Runnable() {
            @Override public void run() {
              callback.onFailure(ExecutorCallbackCall.this, t);
            }
          });
        }
      });
    }

    @Override public boolean isExecuted() {
      return delegate.isExecuted();
    }

    @Override public Response<T> execute() throws IOException {
      return delegate.execute();
    }

    @Override public void cancel() {
      delegate.cancel();
    }

    @Override public boolean isCanceled() {
      return delegate.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
    @Override public Call<T> clone() {
      return new ExecutorCallbackCall<>(callbackExecutor, delegate.clone());
    }

    @Override public Request request() {
      return delegate.request();
    }
  }
```

可以看出ExecutorCallbackCall是对Call的封装，它主要添加了通过callbackExecutor将请求回调到 UI 线
程。当我们得到 Call 对象后会调用它的 enqueue 方法，其实调用的是ExecutorCallbackCall的enqueue方法。
而从上面代码注释1处可以看出ExecutorCallbackCall的enqueue方法最终调用的是delegate的enqueue方法。
delegate是传入的OkHttpCall。

### 3. Call的enqueue方法

下面我们就来查看OkHttpCall的enqueue方法，代码如下所示：

```
@Override public void enqueue(final Callback<T> callback) {
    checkNotNull(callback, "callback == null");

    okhttp3.Call call;
    ....

    call.enqueue(new okhttp3.Callback() {
      @Override public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse)
          throws IOException {
        Response<T> response;
        try {
          response = parseResponse(rawResponse);
        } catch (Throwable e) {
          callFailure(e);
          return;
        }
        callSuccess(response);
      }

      @Override public void onFailure(okhttp3.Call call, IOException e) {
        try {
          callback.onFailure(OkHttpCall.this, e);
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    .....
  }
```
主要是用OkHttp来请求网络,然后parseResponse()方法解析请求回来的值.来看看parseResponse()方法:

```
  Response<T> parseResponse(okhttp3.Response rawResponse) throws IOException {
    ResponseBody rawBody = rawResponse.body();

    // Remove the body's source (the only stateful object) so we can pass the response along.
    rawResponse = rawResponse.newBuilder()
        .body(new NoContentResponseBody(rawBody.contentType(), rawBody.contentLength()))
        .build();

    int code = rawResponse.code();
    if (code < 200 || code >= 300) {
      try {
        // Buffer the entire body to avoid future I/O.
        ResponseBody bufferedBody = Utils.buffer(rawBody);
        return Response.error(bufferedBody, rawResponse);
      } finally {
        rawBody.close();
      }
    }

    if (code == 204 || code == 205) {
      rawBody.close();
      return Response.success(null, rawResponse);
    }

    ExceptionCatchingRequestBody catchingBody = new ExceptionCatchingRequestBody(rawBody);
    try {
      T body = serviceMethod.toResponse(catchingBody);
      return Response.success(body, rawResponse);
    } catch (RuntimeException e) {
      // If the underlying source threw an exception, propagate that rather than indicating it was
      // a runtime exception.
      catchingBody.throwIfCaught();
      throw e;
    }
  }
```

此方法用于解析返回值,可以看到,还判断了状态码是否成功等等.来看看`serviceMethod.toResponse(catchingBody);`干了什么

```
R toResponse(ResponseBody body) throws IOException {
    return responseConverter.convert(body);
  }
```
这个responseConverter就是此前讲过在ServiceMethod的build方法调用createResponseConverter方法返回
的 Converter。在此前的例子中我们传入的是 GsonConverterFactory，因此可以查看GsonConverterFactory的
代码，如下所示：

```
 @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {
    TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
    return new GsonResponseBodyConverter<>(gson, adapter);
  }
```
在GsonConverterFactory中有一个方法responseBodyConverter，它最终会创建GsonResponse-
BodyConverter：
```
final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final Gson gson;
  private final TypeAdapter<T> adapter;

  GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
    this.gson = gson;
    this.adapter = adapter;
  }

  @Override public T convert(ResponseBody value) throws IOException {
    JsonReader jsonReader = gson.newJsonReader(value.charStream());
    try {
      return adapter.read(jsonReader);
    } finally {
      value.close();
    }
  }
}

```

在GsonResponseBodyConverter的convert方法里会将回调的数据转换为JSON格式。因此，我们也知道了
此前调用responseConverter.convert是为了转换为特定的数据格式。Call的enqueue方法主要做的就是用OkHttp
来请求网络，将返回的Response进行数据转换并回调给UI线程。Retrofit的源码就讲到这里了。