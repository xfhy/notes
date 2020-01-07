> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://zhuanlan.zhihu.com/p/35121326

文章修改了一下，最近把源码撸了好几遍，感觉现在这样写才是思路最简单的，欢迎大家找茬。

> retrofit 已经流行很久了，它是 Square 开源的一款优秀的网络框架，这个框架对 okhttp 进行了封装，让我们使用 okhttp 做网路请求更加简单。但是光学会使用只是让我们多了一个技能，学习其源码才能让我们更好的成长。

**本篇文章是在分析 retrofit 的源码流程，有大量的代码，读者最好把源码下载下来导入 IDE，然后跟着一起看，效果会更好**

[square/retrofit​github.com![](https://pic3.zhimg.com/v2-4906f047c6d2f8eeab97ca536b904bfa_ipico.jpg)](https://github.com/square/retrofit)

retrofit 入门
-----------

*   定义网络请求的 API 接口：

```
interface GithubApiService {
        @GET("users/{name}/repos")
        Call<ResponseBody> searchRepoInfo(@Path("name") String name);
    }

```

使用了注解表明请求方式，和参数类型，这是 retrofit 的特性，也正是简化了我们的网络请求过程的地方！

*   初始化一个 retrofit 的实例：

```
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();

```

retrofit 的实例化很简单，采用链式调用的设计，把需要的参数传进去即可，复杂的参数我们这里就不举例了。

*   生成接口实现类：

```
GithubApiService githubService = retrofit.create(service)
Call<ResponseBody> call = githubService.searchRepoInfo("changmu175");

```

我们调用 retrofit 的`create`方法就可以把我们定义的接口转化成实现类，我们可以直接调用我们定义的方法进行网络请求，但是我们只定义了一个接口方法，也没有方法体，请求方式和参数类型都是注解，`create`是如何帮我们整理参数，实现方法体的呢？一会我们通过源码解析再去了解。

*   发起网络请求

```
//同步请求方式
 call.request();
 //异步请求方式
 call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //请求成功回调
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //请求与失败回调
            }
        });

```

至此，retrofit 的一次网络请求示例已经结束，基于对 okhttp 的封装，让网络请求已经简化了很多。当然 retrofit 最适合的还是 REST API 类型的接口，方便简洁。

下面我们就看看 retrofit 的核心工作是如何完成的！

retrofit 初始化
------------

retrofit 的初始化采用了链式调用的设计

```
Retrofit retrofit = new Retrofit.Builder()
                       .baseUrl("https://api.github.com/")
                       .build();

```

很明显这个方法是在传一些需要的参数，我们简单的跟踪一下：

首先看看`Builder()`的源码：
-------------------

```
public Builder() {
      this(Platform.get());
    }

```

这句代码很简单就是调用了自己的另一个构造函数：

```
Builder(Platform platform) {
      this.platform = platform;
    }

```

这个构造函数也很简单，就是一个赋值，我们把之前的`Platform.get()`点开，看看里面做在什么：

```
private static final Platform PLATFORM = findPlatform();

static Platform get() {
    return PLATFORM;
  }

```

我们发现这里使用使用了一个饿汉式单例，使用`Platform.get()`返回一个实例，这样写的好处是简单，线程安全，效率高，不会生成多个实例！

我们再看看`findPlatform()` 里做了什么：

```
private static Platform findPlatform() {
    try {
      Class.forName("android.os.Build");
      if (Build.VERSION.SDK_INT != 0) {
        return new Android();
      }
    } catch (ClassNotFoundException ignored) {
    }

    ....省略部分代码...
 }

```

所以是判断了一下系统，然后根据系统实例化一个对象。这里面应该做了一些和 Android 平台相关的事情，属于细节，我们追究，感兴趣的可以只看看。

再看看`baseUrl(url)`的源码
--------------------

```
public Builder baseUrl(String baseUrl) {
      checkNotNull(baseUrl, "baseUrl == null");
      HttpUrl httpUrl = HttpUrl.parse(baseUrl);
      ....
      return baseUrl(httpUrl);
    }

public Builder baseUrl(HttpUrl baseUrl) {
      checkNotNull(baseUrl, "baseUrl == null");
      ....
      this.baseUrl = baseUrl;
      return this;
    }

```

这两段代码也很简单，校验 URL，生成`httpUrl`对象，然后赋值给`baseUrl`

看看`build()` 方法在做什么
------------------

参数基本设置完了，最后就要看看`build()` 这个方法在做什么：

```
public Retrofit build() {
      if (baseUrl == null) {
        throw new IllegalStateException("Base URL required.");
      }

      okhttp3.Call.Factory callFactory = this.callFactory;
      if (callFactory == null) {
        callFactory = new OkHttpClient();
      }
      ....

      return new Retrofit(callFactory, baseUrl, unmodifiableList(converterFactories),
          unmodifiableList(callAdapterFactories), callbackExecutor, validateEagerly);
    }
  }
}

```

代码中有大量的参数校验，有些复杂的参数我们没有传，所以我就把那些代码删除了。简单看一下也能知道，这段代码就是做一些参数校验，`baseUrl`不能为空否则会抛异常，至于其他的参数如果为`null`则会创建默认的对象。其中`callFactory`就是 okhttp 的工厂实例，用于网络请求的。  
最后我们看到，这个方法最终返回的是一个 Retrofit 的对象，初始化完成。

生成接口实现类
-------

刚才我们就讲过`retrofit.create`这个方法很重要，它帮我们生成了接口实现类，并完成了方法体的创建，省去了我们很多工作量。那我们来看看它是如何帮我们实现接口的。

```
public <T> T create(final Class<T> service) {

    ...

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
            return serviceMethod.adapt(okHttpCall);
          }
        });
  }

```

这段代码实际上是使用了**动态代理**的设计模式，而且这个方法封装的非常好，我们只需要调用 方法就可以获得我们需要的实现类，遵循了迪米特法则（最少知道原则）。

了解动态代理的人都知道我们要重写`Object invoke(Object proxy, Method method, @Nullable Object[] args)` 方法，这个方法会传入我们需要的实现的方法，和参数，并返回我们需要的返回值。  
retrofit 在重写这个方法的时候做了三件事：

*   1、先判断了这个方法的类是不是一个`Object.class)`，就直接返回方法原有的返回值。
*   2、判断这个方法是不是`DefaultMethod`，大家都知道这个方法是 Java 8 出来的新属性，表示接口的方法体。
*   3、构建一个`ServiceMethod<Object, Object>`对象和`OkHttpCall<Object>`对象，并调用 `serviceMethod.adapt(okHttpCall)`方法将二者绑定。

我们看看这个方法的源码：

```
T adapt(Call<R> call) {
    return callAdapter.adapt(call);
  }

```

这个`callAdapter`我们在初始化 retrofit 的时候没有使用： `addCallAdapterFactory(CallAdapterFactory)`传值，所以这里是默认的`DefaultCallAdapterFactory`  
那我们再看看`DefaultCallAdapterFactory`里的`adapt(call)`方法：

```
@Override public Call<Object> adapt(Call<Object> call) {
        return call;
      }

```

直接返回参数，也就是`OkHttpCall<Object>`的对象。所以如果没有自定义`callAdapter`的时候，我们定义接口的时候返回值类型应该是个`Call`类型的。  
那么，至此这个`create`方法已经帮我们实现了我们定义的接口，并返回我们需要的值。

请求参数整理
------

我们定义的接口已经被实现，但是我们还是不知道我们注解的请求方式，参数类型等是如何发起网络请求的呢？  
这时我们可能应该关注一下`ServiceMethod<Object, Object>`对象的构建了：

```
ServiceMethod<Object, Object> serviceMethod =
                (ServiceMethod<Object, Object>) loadServiceMethod(method);

```

主要的逻辑都在这个`loadServiceMethod(method)`里面，我们看看方法体：

```
ServiceMethod<?, ?> loadServiceMethod(Method method) {
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

逻辑很简单，就是先从一个 `serviceMethodCache`中取`ServiceMethod<?, ?>`对象，如果没有，则构建`ServiceMethod<?, ?>`对象，然后放进去`serviceMethodCache`中，这个`serviceMethodCache`是一个`HashMap`:

```
private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap<>();

```

所以构建`ServiceMethod<?, ?>`对象的主要逻辑还不在这个方法里，应该在`new ServiceMethod.Builder<>(this, method).build();`里面。这也是个链式调用，一般都是参数赋值，我们先看看`Builder<>(this, method)`方法：

```
Builder(Retrofit retrofit, Method method) {
      this.retrofit = retrofit;
      this.method = method;
      this.methodAnnotations = method.getAnnotations();
      this.parameterTypes = method.getGenericParameterTypes();
      this.parameterAnnotationsArray = method.getParameterAnnotations();
    }

```

果然，这里获取了几个重要的参数：

*   `retrofit`实例
*   `method`，接口方法
*   接口方法的注解`methodAnnotations`，在`retrofit`里一般为请求方式
*   参数类型`parameterTypes`
*   参数注解数组`parameterAnnotationsArray`，一个参数可能有多个注解

我们再看看`build()`的方法：

```
public ServiceMethod build() {
      callAdapter = createCallAdapter();
      responseType = callAdapter.responseType();
      responseConverter = createResponseConverter();

      for (Annotation annotation : methodAnnotations) {
        parseMethodAnnotation(annotation);
      }

      if (httpMethod == null) {
        throw methodError("HTTP method annotation is required (e.g., @GET, @POST, etc.).");
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

      return new ServiceMethod<>(this);
    }

```

这个方法挺长的，删了些无关紧要的代码还是很长。首先一开始先获取几个重要对象：`callAdapter`、`responseType`和`responseConverter`，这三个对象都跟最后的结果有关，我们先不管。

看到一个`for`循环，遍历方法的注解，然后解析：

```
for (Annotation annotation : methodAnnotations) {
        parseMethodAnnotation(annotation);
      }
private void parseMethodAnnotation(Annotation annotation) {
      if (annotation instanceof DELETE) {
        parseHttpMethodAndPath("DELETE", ((DELETE) annotation).value(), false);
      } else if (annotation instanceof GET) {
        parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
      } 

        ....

```

这个方法的方法体我删掉了后面的一部分，因为逻辑都是一样，根据不同的方法注解作不同的解析，得到网络请求的方式`httpMethod`。但是主要的方法体还是`if`里面的方法：

```
private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {

      ....

      // Get the relative URL path and existing query string, if present.
      int question = value.indexOf('?');
      if (question != -1 && question < value.length() - 1) {
        // Ensure the query string does not have any named parameters.
        String queryParams = value.substring(question + 1);
        Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
        if (queryParamMatcher.find()) {
          throw methodError("URL query string \"%s\" must not have replace block. "
              + "For dynamic query parameters use @Query.", queryParams);
        }
      }

      this.relativeUrl = value;
      this.relativeUrlParamNames = parsePathParameters(value);
    }

```

逻辑不复杂，就是校验这个`value`的值 是否合法，规则就是不能有 “？” 如果有则需要使用`@Query`注解。最后`this.relativeUrl = value;`。这个`relativeUrl`就相当于省略域名的 URL，一般走到这里我们能得到的是：`users/{name}/repos`这样的。里面的 “{name}” 是一会我们需要赋值的变量。

我们继续看刚才的`build()`方法：  
解析完方法的注解之后，需要解析参数的注解数组，这里实例化了一个一维数组：

```
parameterHandlers = new ParameterHandler<?>[parameterCount];

```

然后遍历取出参数的类型：

```
Type parameterType = parameterTypes[p];

```

取出参数注解：

```
Annotation[] parameterAnnotations = parameterAnnotationsArray[p];

```

然后把参数类型、参数注解都放在一起进行解析，解析的结果放到刚才实例化的数组`parameterHandlers`里面：

```
parameterHandlers[p] = parseParameter(p, parameterType, parameterAnnotations);

```

那我们再看看这个方法里做了什么：

```
private ParameterHandler<?> parseParameter(int p, Type parameterType, Annotation[] annotations) {
      ParameterHandler<?> result = null;
      for (Annotation annotation : annotations) {
        ParameterHandler<?> annotationAction = parseParameterAnnotation(
            p, parameterType, annotations, annotation);
      }

    }

```

这个方法的主要代码也很简单，解析参数注解，得到一个`ParameterHandler<?> annotationAction`对象。  
那我继续看方法里面的代码。当我们点进`parseParameterAnnotation( p, parameterType, annotations, annotation);`的源码里面去之后发现这个方法的代码接近 500 行！但是大部分逻辑类似，都是通过`if else`判断参数的注解，我们取一段我们刚才的例子相关的代码出来：

```
if (annotation instanceof Path) {
        if (gotQuery) {
          throw parameterError(p, "A @Path parameter must not come after a @Query.");
        }
        if (gotUrl) {
          throw parameterError(p, "@Path parameters may not be used with @Url.");
        }
        if (relativeUrl == null) {
          throw parameterError(p, "@Path can only be used with relative url on @%s", httpMethod);
        }
        gotPath = true;

        Path path = (Path) annotation;
        String name = path.value();
        validatePathName(p, name);

        Converter<?, String> converter = retrofit.stringConverter(type, annotations);
        return new ParameterHandler.Path<>(name, converter, path.encoded());

      }

```

前面做了一些校验，后面取出注解的名字：`name`，然后用正则表达校验这个`name`是否合法。然后构建一个`Converter<?, String>对象`：

```
Converter<?, String> converter = retrofit.stringConverter(type, annotations);

```

点击去看看：

```
public <T> Converter<T, String> stringConverter(Type type, Annotation[] annotations) {
      ....
    for (int i = 0, count = converterFactories.size(); i < count; i++) {
      Converter<?, String> converter =
          converterFactories.get(i).stringConverter(type, annotations, this);
      if (converter != null) {
        //noinspection unchecked
        return (Converter<T, String>) converter;
      }
    }
    return (Converter<T, String>) BuiltInConverters.ToStringConverter.INSTANCE;
  }

```

看到核心代码是`converter`的`stringConverter(type, annotations, this)`方法：  
因为我们刚才的示例中被没有通过：`addConverterFactory(ConverterFactory)`添加一个`ConverterFactory`，所以这里会返回一个空：

```
public @Nullable Converter<?, String> stringConverter(Type type, Annotation[] annotations,
        Retrofit retrofit) {
      return null;
    }

```

所以最后会执行最后一句代码： `return (Converter<T, String>) BuiltInConverters.ToStringConverter.INSTANCE;`  
我们点进去看看这个`INSTANCE`：

```
static final ToStringConverter INSTANCE = new ToStringConverter();

```

是`BuiltInConverters`内的内部类`ToStringConverter`的单例。所以这里我们得到的就  
是`BuiltInConverters.ToStringConverter`的实例。

最后用这个对象构建一个`Path`（因为示例中的参数类型是 path，所以我们看这个代码）：

```
new ParameterHandler.Path<>(name, converter, path.encoded());

```

我们看看这个`Path`类的构造函数：

```
Path(String name, Converter<T, String> valueConverter, boolean encoded) {
      this.name = checkNotNull(name, "name == null");
      this.valueConverter = valueConverter;
      this.encoded = encoded;
    }

```

只是赋值，并且我们看到这个类继承自：`ParameterHandler<T>`，所以我们回到刚才的`build()`方法，发现把参数类型，参数注解放在一起解析之后存储到了这个`ParameterHandler<T>`数组中，中间主要做了多种合法性校验，并根据注解的类型，生成不同的 `ParameterHandler<T>`子类，如注解是`Url`则生成`ParameterHandler.RelativeUrl()`对象，如果注解是`Path`，则生成： `ParameterHandler.Path<>(name, converter, path.encoded())`对象等等。  
我们查看了`ParameterHandler<T>`类，发现它有一个抽象方法：

```
abstract void apply(RequestBuilder builder, @Nullable T value) throws IOException;

```

这个方法每个子类都必须复写，那我们看看`Path`里面怎么复写的：

```
@Override 
    void apply(RequestBuilder builder, @Nullable T value) throws IOException {
         builder.addPathParam(name, valueConverter.convert(value), encoded);
    }

```

就是把`value`被添加到`RequestBuilder`中，我们看一下这个`addPathParam`方法：

```
void addPathParam(String name, String value, boolean encoded) {

    relativeUrl = relativeUrl.replace("{" + name + "}", canonicalizeForPath(value, encoded));
  }

```

这个方法把我们传进来的值`value`按照编码格式转换，然后替换`relativeUrl`中的`{name}`，构成一个有效的省略域名的 URL。至此，URL 的拼接已经完成！

**总结：Retrofit 使用动态代理模式实现我们定义的网络请求接口，在重写 invoke 方法的时候构建了一个 ServiceMethod 对象，在构建这个对象的过程中进行了方法的注解解析得到网络请求方式`httpMethod`，以及参数的注解分析，拼接成一个省略域名的 URL**

Retrofit 网络请求
-------------

我们刚才解析了`apply`方法，我们看看 apply 方法是谁调用的呢？跟踪一下就发先只有`toCall(args);`方法：

```
okhttp3.Call toCall(@Nullable Object... args) throws IOException {
    RequestBuilder requestBuilder = new RequestBuilder(httpMethod, baseUrl, relativeUrl, headers,
        contentType, hasBody, isFormEncoded, isMultipart);

    @SuppressWarnings("unchecked") // It is an error to invoke a method with the wrong arg types.
    ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) parameterHandlers;

    int argumentCount = args != null ? args.length : 0;
    if (argumentCount != handlers.length) {
      throw new IllegalArgumentException("Argument count (" + argumentCount
          + ") doesn't match expected count (" + handlers.length + ")");
    }

    for (int p = 0; p < argumentCount; p++) {
      handlers[p].apply(requestBuilder, args[p]);
    }

    return callFactory.newCall(requestBuilder.build());
  }

```

这个方法一开始就构建了`RequestBuilder`，传进去的参数包含： `httpMethod，baseUrl，relativeUrl，headers，contentType，hasBody，isFormEncoded，isMultipart`！

然后获取了`parameterHandlers`，我们上边分析的时候，知道这个数组是存参数注解的解析结果的，并对其进行遍历调用了如下方法：

```
for (int p = 0; p < argumentCount; p++) {
      handlers[p].apply(requestBuilder, args[p]);
    }

```

把参数值传进`RequestBuilder`中。  
最后调用`callFactory.newCall(requestBuilder.build())`生成一个`okhttp3.Call`。  
我们看一下这个`build`方法：

```
Request build() {
    HttpUrl url;
    HttpUrl.Builder urlBuilder = this.urlBuilder;
    if (urlBuilder != null) {
      url = urlBuilder.build();
    } else {
      // No query parameters triggered builder creation, just combine the relative URL and base URL.
      //noinspection ConstantConditions Non-null if urlBuilder is null.
      url = baseUrl.resolve(relativeUrl);
      if (url == null) {
        throw new IllegalArgumentException(
            "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
      }
    }

    RequestBody body = this.body;
    if (body == null) {
      // Try to pull from one of the builders.
      if (formBuilder != null) {
        body = formBuilder.build();
      } else if (multipartBuilder != null) {
        body = multipartBuilder.build();
      } else if (hasBody) {
        // Body is absent, make an empty body.
        body = RequestBody.create(null, new byte[0]);
      }
    }

    MediaType contentType = this.contentType;
    if (contentType != null) {
      if (body != null) {
        body = new ContentTypeOverridingRequestBody(body, contentType);
      } else {
        requestBuilder.addHeader("Content-Type", contentType.toString());
      }
    }

    return requestBuilder
        .url(url)
        .method(method, body)
        .build();
  }

```

可以看到 okhttp 的请求体在这里构建，当所有的参数满足的时候，则调用了

```
Request.Builder requestBuilder
        .url(url)
        .method(method, body)
        .build();

```

这是发起 okhttp 的网络请求 。  
那这个`toCall(args);`谁调用的呢？继续往回跟！

```
private okhttp3.Call createRawCall() throws IOException {
    okhttp3.Call call = serviceMethod.toCall(args);
    return call;
  }

```

那谁调用了`createRawCall()`呢？继续看谁调用了！于是发现调用方有三个地方，并且都是`OkHttpCall`里面！我们一个一个看吧：

1.  Request request() 方法：
2.  enqueue(final Callback callback) 方法
3.  Response execute() 的方法

很明显上面三个方法都是 retrofit 的发起网络请求的方式，分别是同步请求和异步请求。我们的示例中在最后一步就是调用了`request`方法和`enqueue`方法发起网络请求。至此我们已经疏通了 retrofit 是如何进行网络请求的了。

**总结：当我们调用 Retrofit 的网络请求方式的时候，就会调用 okhttp 的网络请求方式，参数使用的是实现接口的方法的时候拿到的信息构建的`RequestBuilder`对象，然后在`build`方法中构建 okhttp 的`Request`，最终发起网络请求**

总结
--

至此 retrofit 的流程讲完了，文章很长，代码很多，读者最好下载代码导入 IDE，跟着文章一起看代码。

**Retrofit 主要是在`create`方法中采用动态代理模式实现接口方法，这个过程构建了一个 ServiceMethod 对象，根据方法注解获取请求方式，参数类型和参数注解拼接请求的链接，当一切都准备好之后会把数据添加到 Retrofit 的`RequestBuilder`中。然后当我们主动发起网络请求的时候会调用 okhttp 发起网络请求，okhttp 的配置包括请求方式，URL 等在 Retrofit 的`RequestBuilder`的`build()`方法中实现，并发起真正的网络请求。**

Retrofit 封装了 okhttp 框架，让我们的网络请求更加简洁，同时也能有更高的扩展性。当然我们只是窥探了 Retrofit 源码的一部分，他还有更复杂更强大的地方等待我们去探索包括返回值转换工厂，拦截器等，这些都属于比较难的地方，我们需要循序渐进的去学习，当我们一点一点的看透框架的本质之后，我们使用起来才会熟能生巧。大神的代码，对于 Android 想要进阶的同学来说很有好处，不仅教会我们如何设计代码更多的是解决思想。

最后大家支持欢迎关注公众号：码老板

[http://weixin.qq.com/r/liqYgFfEBQp6rRPj93_E](http://weixin.qq.com/r/liqYgFfEBQp6rRPj93_E) (二维码自动识别)

写下你的评论...  

想问一下怎么往回倒源码，在分析 build 那块。

toCall 方法后面讲有点断