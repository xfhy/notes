> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/27c1554b7fee

> 前言：对于 _OkHttp_ 我接触的时间其实不太长，一直都是使用 Retrofit + OkHttp 来做网络请求的，但是有同学说面试的时候可能会问框架源码，这样光是会用是不够的，于是便萌生了通一通 OkHttp 源码的念头。经过大约一周的时间，源码看了个大概（说来惭愧，也就知道里面的原理），这里变向大家介绍一下我的所得，希望对大家能有所帮助。这里推荐两篇博文：[](https://link.jianshu.com?t=http://gold.xitu.io/entry/5728441d128fe1006058b6b9)[OkHttp 官方教程解析 - 彻底入门 OkHttp 使用  
> ](https://link.jianshu.com?t=http://blog.csdn.net/mynameishuangshuai/article/details/51303446)和[拆轮子系列：拆 OkHttp](https://link.jianshu.com?t=http://blog.piasy.com/2016/07/11/Understand-OkHttp/) 前者能够让你入门 OkHttp, 后者能让你明白 OkHttp 的原理，我就是看的后者去看的源码，如果看我的不太懂，大家可以去看看上面的。同时，欢迎大家交流，提出意见，谢谢！

总体流程
====

下面的流程图是由上面的文章抄来的（自己画的图，用的 visio）  
_整个流程是，通过`OkHttpClient`将构建的`Request`转换为 Call，然后在 RealCall 中进行异步或同步任务，最后通过一些的拦截器`interceptor`发出网络请求和得到返回的`response`。_  
将流程大概是这么个流程，大家可以有个大概的印象，继续向下看：  

![](http://upload-images.jianshu.io/upload_images/1916953-fc6439af2bfefddc.jpg) OkHttp 流程图. jpg

为了让大家有更深的印象，我准备追踪一个`GET`网络请求的具体流程，来介绍在源码中发生了什么。

GET 请求过程
========

这是利用`OkHttp`写一个 Get 请求步骤，这里是一个同步的请求, 异步的下面也会说：

```
   //HTTP GET
    public String get(String url) throws IOException {
        //新建OKHttpClient客户端
        OkHttpClient client = new OkHttpClient();
        //新建一个Request对象
        Request request = new Request.Builder()
                .url(url)
                .build();
        //Response为OKHttp中的响应
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }else{
            throw new IOException("Unexpected code " + response);
        }
    }


```

OKHttpClient：流程的总控制者
====================

![](http://upload-images.jianshu.io/upload_images/1916953-1f3fe0ee98ff2c26.png) OkHttpClient 的类设计图

使用 OkHttp 的时候我们都会创建一个 OkHttpClient 对象:  
`OkHttpClient client = new OkHttpClient();`  
这是做什么的呢？看下 builder 里面的参数：

```
     final Dispatcher dispatcher;  //分发器
    final Proxy proxy;  //代理
    final List<Protocol> protocols; //协议
    final List<ConnectionSpec> connectionSpecs; //传输层版本和连接协议
    final List<Interceptor> interceptors; //拦截器
    final List<Interceptor> networkInterceptors; //网络拦截器
    final ProxySelector proxySelector; //代理选择
    final CookieJar cookieJar; //cookie
    final Cache cache; //缓存
    final InternalCache internalCache;  //内部缓存
    final SocketFactory socketFactory;  //socket 工厂
    final SSLSocketFactory sslSocketFactory; //安全套接层socket 工厂，用于HTTPS
    final CertificateChainCleaner certificateChainCleaner; // 验证确认响应证书 适用 HTTPS 请求连接的主机名。
    final HostnameVerifier hostnameVerifier;    //  主机名字确认
    final CertificatePinner certificatePinner;  //  证书链
    final Authenticator proxyAuthenticator;     //代理身份验证
    final Authenticator authenticator;      // 本地身份验证
    final ConnectionPool connectionPool;    //连接池,复用连接
    final Dns dns;  //域名
    final boolean followSslRedirects;  //安全套接层重定向
    final boolean followRedirects;  //本地重定向
    final boolean retryOnConnectionFailure; //重试连接失败
    final int connectTimeout;    //连接超时
    final int readTimeout; //read 超时
    final int writeTimeout; //write 超时 


```

在这些声明的对象中可以看出来，几乎所有用到的类都和`OkHttpClient`有关系。事实上，你能够通过它来设置改变一些参数，因为他是通过`建造者模式`实现的，因此你可以通过`builder()`来设置。如果不进行设置，在`Builder`中就会使用默认的设置：

```
            dispatcher = new Dispatcher();
            protocols = DEFAULT_PROTOCOLS;
            connectionSpecs = DEFAULT_CONNECTION_SPECS;
            proxySelector = ProxySelector.getDefault();
            cookieJar = CookieJar.NO_COOKIES;
            socketFactory = SocketFactory.getDefault();
            hostnameVerifier = OkHostnameVerifier.INSTANCE;
            certificatePinner = CertificatePinner.DEFAULT;
            proxyAuthenticator = Authenticator.NONE;
            authenticator = Authenticator.NONE;
            connectionPool = new ConnectionPool();
            dns = Dns.SYSTEM;
            followSslRedirects = true;
            followRedirects = true;
            retryOnConnectionFailure = true;
            connectTimeout = 10_000;
            readTimeout = 10_000;
            writeTimeout = 10_000;


```

看到这，如果你还不明白的话，也没关系，在`OkHttp`中只是设置用的的各个东西。真正的流程要从里面的`newCall()`方法中说起：

```
       /**
        *  Prepares the {@code request} to be executed at some point in the future.
        *  准备将要被执行的request
        */
        @Override
        public Call newCall(Request request) {
            return new RealCall(this, request);
        }


```

当通过`建造者模式`创建了`Request`之后（这个没什么好说），紧接着就通过下面的代码来获得`Response`  
大家还记得上面做`GET`请求时的这句代码吧：  
`Response response = client.newCall(request).execute();`这就代码就开启了整个 GET 请求的流程：

RealCall：真正的请求执行者。
==================

先看一下他的构造方法：

```
protected RealCall(OkHttpClient client, Request originalRequest) {  
    this.client = client;    
    this.originalRequest = originalRequest;    
    this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client);
}


```

可以看到他传过来一个`OkHttpClient`对象和一个`originalRequest`（我们创建的`Request`）。  
接下来看它的`execute()`方法：

```
 @Override
    public Response execute() throws IOException {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed"); //(1)
            executed = true;
        }
        try {
            client.dispatcher.executed(this);//(2)
            Response result = getResponseWithInterceptorChain();//(3)
            if (result == null) throw new IOException("Canceled");
            return result;
        }finally {
            client.dispatcher.finished(this);//(4)
        }
    }


```

1.  检查这个 `call`是否已经被执行了，每个 `call` 只能被执行一次，如果想要一个完全一样的 `call`，可以利用 `all#clone` 方法进行克隆。
2.  利用 `client.dispatcher().executed(this)` 来进行实际执行，`dispatcher` 是刚才看到的 `OkHttpClient.Builder` 的成员之一，它的文档说自己是异步 `HTTP`请求的执行策略，现在看来，同步请求它也有掺和。
3.  调用 `getResponseWithInterceptorChain()` 函数获取 `HTTP` 返回结果，从函数名可以看出，这一步还会进行一系列 “拦截” 操作。
4.  最后还要通知 `dispatcher` 自己已经执行完毕。  
    `dispatcher` 这里我们不过度关注，在同步执行的流程中，涉及到 `dispatcher` 的内容只不过是告知它我们的执行状态，比如开始执行了（调用 `executed`），比如执行完毕了（调用 `finished`），在异步执行流程中它会有更多的参与。  
    真正发出网络请求，解析返回结果的，还是 `getResponseWithInterceptorChain`：

```
//拦截器的责任链。
    private Response getResponseWithInterceptorChain() throws IOException {
        // Build a full stack of interceptors.
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());     //(1)
        interceptors.add(retryAndFollowUpInterceptor);    //(2)
        interceptors.add(new BridgeInterceptor(client.cookieJar()));    //(3)
        interceptors.add(new CacheInterceptor(client.internalCache()));    //(4)
        interceptors.add(new ConnectInterceptor(client));    //(5)
        if (!retryAndFollowUpInterceptor.isForWebSocket()) {
            interceptors.addAll(client.networkInterceptors());    //(6)
        }
        interceptors.add(new CallServerInterceptor(
                retryAndFollowUpInterceptor.isForWebSocket()));     //(7)

        Interceptor.Chain chain = new RealInterceptorChain(
                interceptors, null, null, null, 0, originalRequest);
        return chain.proceed(originalRequest); //  <<=========开始链式调用
    }


```

1.  在配置 `OkHttpClient` 时设置的 `interceptors`；
2.  负责失败重试以及重定向的 `RetryAndFollowUpInterceptor`；
3.  负责把用户构造的请求转换为发送到服务器的请求、把服务器返回的响应转换为用户友好的响应的 `BridgeInterceptor`；
4.  负责读取缓存直接返回、更新缓存的 `CacheInterceptor`；
5.  负责和服务器建立连接的 `ConnectInterceptor`；
6.  配置 `OkHttpClient` 时设置的 `networkInterceptors`；
7.  负责向服务器发送请求数据、从服务器读取响应数据的 `CallServerInterceptor`。
8.  在`return chain.proceed(originalRequest);`中开启链式调用：

RealInterceptorChain
====================

```
 public Response proceed(Request request, StreamAllocation streamAllocation, HttpCodec httpCodec,
      Connection connection) throws IOException {
    if (index >= interceptors.size()) throw new AssertionError();

    calls++;

    // If we already have a stream, confirm that the incoming request will use it.
    //如果我们已经有一个stream。确定即将到来的request会使用它
    if (this.httpCodec != null && !sameConnection(request.url())) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must retain the same host and port");
    }

    // If we already have a stream, confirm that this is the only call to chain.proceed().
    //如果我们已经有一个stream， 确定chain.proceed()唯一的call
    if (this.httpCodec != null && calls > 1) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must call proceed() exactly once");
    }

    // Call the next interceptor in the chain.
    //调用链的下一个拦截器
    RealInterceptorChain next = new RealInterceptorChain(
        interceptors, streamAllocation, httpCodec, connection, index + 1, request);
    Interceptor interceptor = interceptors.get(index);
    Response response = interceptor.intercept(next);

    // Confirm that the next interceptor made its required call to chain.proceed().
    if (httpCodec != null && index + 1 < interceptors.size() && next.calls != 1) {
      throw new IllegalStateException("network interceptor " + interceptor
          + " must call proceed() exactly once");
    }

    // Confirm that the intercepted response isn't null.
    if (response == null) {
      throw new NullPointerException("interceptor " + interceptor + " returned null");
    }

    return response;
  }


```

代码很多，但是主要是进行一些判断，主要的代码在这：

```
// Call the next interceptor in the chain.
    //调用链的下一个拦截器
    RealInterceptorChain next = new RealInterceptorChain(
        interceptors, streamAllocation, httpCodec, connection, index + 1, request);    //(1)
    Interceptor interceptor = interceptors.get(index);     //(2)
    Response response = interceptor.intercept(next);    //(3)


```

1.  实例化下一个拦截器对应的`RealIterceptorChain`对象，这个对象会在传递给当前的拦截器
2.  得到当前的拦截器：`interceptors`是存放拦截器的`ArryList`
3.  调用当前拦截器的`intercept()`方法，并将下一个拦截器的`RealIterceptorChain`对象传递下去  
    ** 除了在 client 中自己设置的`interceptor`, 第一个调用的就是`retryAndFollowUpInterceptor` **

RetryAndFollowUpInterceptor: 负责失败重试以及重定向
========================================

直接上代码

```
@Override 
public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        streamAllocation = new StreamAllocation(
                client.connectionPool(), createAddress(request.url()));
        int followUpCount = 0;
        Response priorResponse = null;
        while (true) {
            if (canceled) {
                streamAllocation.release();
                throw new IOException("Canceled");
            }

            Response response = null;
            boolean releaseConnection = true;
            try {
                response = ((RealInterceptorChain) chain).proceed(request, streamAllocation, null, null);    //(1)
                releaseConnection = false;
            } catch (RouteException e) {
                // The attempt to connect via a route failed. The request will not have been sent.
                //通过路线连接失败，请求将不会再发送
                if (!recover(e.getLastConnectException(), true, request)) throw e.getLastConnectException();
                releaseConnection = false;
                continue;
            } catch (IOException e) {
                // An attempt to communicate with a server failed. The request may have been sent.
                // 与服务器尝试通信失败，请求不会再发送。
                if (!recover(e, false, request)) throw e;
                releaseConnection = false;
                continue;
            } finally {
                // We're throwing an unchecked exception. Release any resources.
                //抛出未检查的异常，释放资源
                if (releaseConnection) {
                    streamAllocation.streamFailed(null);
                    streamAllocation.release();
                }
            }

            // Attach the prior response if it exists. Such responses never have a body.
            // 附加上先前存在的response。这样的response从来没有body
            // TODO: 2016/8/23 这里没赋值，岂不是一直为空？
            if (priorResponse != null) { //  (2)
                response = response.newBuilder()
                        .priorResponse(priorResponse.newBuilder()
                                .body(null)
                                .build())
                        .build();
            }

            Request followUp = followUpRequest(response); //判断状态码 (3)
            if (followUp == null){
                if (!forWebSocket) {
                    streamAllocation.release();
                }
                return response;
            }

            closeQuietly(response.body());

            if (++followUpCount > MAX_FOLLOW_UPS) {
                streamAllocation.release();
                throw new ProtocolException("Too many follow-up requests: " + followUpCount);
            }

            if (followUp.body() instanceof UnrepeatableRequestBody) {
                throw new HttpRetryException("Cannot retry streamed HTTP body", response.code());
            }

            if (!sameConnection(response, followUp.url())) {
                streamAllocation.release();
                streamAllocation = new StreamAllocation(
                        client.connectionPool(), createAddress(followUp.url()));
            } else if (streamAllocation.codec() != null) {
                throw new IllegalStateException("Closing the body of " + response
                        + " didn't close its backing stream. Bad interceptor?");
            }

            request = followUp;
            priorResponse = response;
        }
    }



```

1.  这里是最关键的代码，可以看出在`response = ((RealInterceptorChain) chain).proceed(request, streamAllocation, null, null);`中直接调用了下一个拦截器，然后捕获可能的异常来进行操作
2.  这里没看太懂，有点坑，以后补
3.  这里对于返回的 response 的状态码进行判断，然后进行处理

BridgeInterceptor：
==================

负责把用户构造的请求转换为发送到服务器的请求、把服务器返回的响应转换为用户友好的响应的 。

```
@Override 
public Response intercept(Chain chain) throws IOException {
    Request userRequest = chain.request();
    Request.Builder requestBuilder = userRequest.newBuilder();

    //检查request。将用户的request转换为发送到server的请求
    RequestBody body = userRequest.body();     //(1)
    if (body != null) {
      MediaType contentType = body.contentType();
      if (contentType != null) {
        requestBuilder.header("Content-Type", contentType.toString());
      }

      long contentLength = body.contentLength();
      if (contentLength != -1) {
        requestBuilder.header("Content-Length", Long.toString(contentLength));
        requestBuilder.removeHeader("Transfer-Encoding");
      } else {
        requestBuilder.header("Transfer-Encoding", "chunked");
        requestBuilder.removeHeader("Content-Length");
      }
    }

    if (userRequest.header("Host") == null) {
      requestBuilder.header("Host", hostHeader(userRequest.url(), false));
    }

    if (userRequest.header("Connection") == null) {
      requestBuilder.header("Connection", "Keep-Alive");
    }
      // If we add an "Accept-Encoding: gzip" header field we're responsible for also decompressing
    // the transfer stream.
    //GZIP压缩
    boolean transparentGzip = false;
    if (userRequest.header("Accept-Encoding") == null) {
      transparentGzip = true;
      requestBuilder.header("Accept-Encoding", "gzip");
    }

    List<Cookie> cookies = cookieJar.loadForRequest(userRequest.url());
    if (!cookies.isEmpty()) {
      requestBuilder.header("Cookie", cookieHeader(cookies));
    }

    if (userRequest.header("User-Agent") == null) {
      requestBuilder.header("User-Agent", Version.userAgent());
    }

    Response networkResponse = chain.proceed(requestBuilder.build());   //(2)

    HttpHeaders.receiveHeaders(cookieJar, userRequest.url(), networkResponse.headers()); //(3)

    Response.Builder responseBuilder = networkResponse.newBuilder()
        .request(userRequest);

    if (transparentGzip
        && "gzip".equalsIgnoreCase(networkResponse.header("Content-Encoding"))
        && HttpHeaders.hasBody(networkResponse)) {
      GzipSource responseBody = new GzipSource(networkResponse.body().source());
      Headers strippedHeaders = networkResponse.headers().newBuilder()
          .removeAll("Content-Encoding")
          .removeAll("Content-Length")
          .build();
      responseBuilder.headers(strippedHeaders);
      responseBuilder.body(new RealResponseBody(strippedHeaders, Okio.buffer(responseBody)));
    }

    return responseBuilder.build();
  }


```

1.  在（1）和（2）之间，`BridgeInterceptor`对于`request`的格式进行检查，让构建了一个新的`request`
2.  调用下一个`interceptor`来得到 response
3.  （3）下面就是对得到的 response 进行一些判断操作，最后将结果返回。

```
@Override 
public Response intercept(Chain chain) throws IOException {
    Response cacheCandidate = cache != null        //=============(1)
        ? cache.get(chain.request()) //通过request得到缓存
        : null;

    long now = System.currentTimeMillis();

    CacheStrategy strategy = new CacheStrategy.Factory(now, chain.request(), cacheCandidate).get(); //根据request来得到缓存策略===========(2)
    Request networkRequest = strategy.networkRequest;
    Response cacheResponse = strategy.cacheResponse;

    if (cache != null) {
      cache.trackResponse(strategy);
    }

    if (cacheCandidate != null && cacheResponse == null) { //存在缓存的response，但是不允许缓存
      closeQuietly(cacheCandidate.body()); // The cache candidate wasn't applicable. Close it. 缓存不适合，关闭
    }

    // If we're forbidden from using the network and the cache is insufficient, fail.
      //如果我们禁止使用网络，且缓存为null，失败
    if (networkRequest == null && cacheResponse == null) {
      return new Response.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(504)
          .message("Unsatisfiable Request (only-if-cached)")
          .body(EMPTY_BODY)
          .sentRequestAtMillis(-1L)
          .receivedResponseAtMillis(System.currentTimeMillis())
          .build();
    }

    // If we don't need the network, we're done.
    if (networkRequest == null) {  //没有网络请求，跳过网络，返回缓存
      return cacheResponse.newBuilder()
          .cacheResponse(stripBody(cacheResponse))
          .build();
    }

    Response networkResponse = null;
    try {
      networkResponse = chain.proceed(networkRequest);//网络请求拦截器    //======(3)
    } finally {
      // If we're crashing on I/O or otherwise, don't leak the cache body.
        //如果我们因为I/O或其他原因崩溃，不要泄漏缓存体
      if (networkResponse == null && cacheCandidate != null) {
        closeQuietly(cacheCandidate.body());
      }
    }

    // If we have a cache response too, then we're doing a conditional get.========(4)
      //如果我们有一个缓存的response，然后我们正在做一个条件GET
    if (cacheResponse != null) {
      if (validate(cacheResponse, networkResponse)) { //比较确定缓存response可用
        Response response = cacheResponse.newBuilder()
            .headers(combine(cacheResponse.headers(), networkResponse.headers()))
            .cacheResponse(stripBody(cacheResponse))
            .networkResponse(stripBody(networkResponse))
            .build();
        networkResponse.body().close();

        // Update the cache after combining headers but before stripping the
        // Content-Encoding header (as performed by initContentStream()).
          //更新缓存，在剥离content-Encoding之前
        cache.trackConditionalCacheHit();
        cache.update(cacheResponse, response);
        return response;
      } else {
        closeQuietly(cacheResponse.body());
      }
    }

    Response response = networkResponse.newBuilder()
        .cacheResponse(stripBody(cacheResponse))
        .networkResponse(stripBody(networkResponse))
        .build();

    if (HttpHeaders.hasBody(response)) {    // =========(5)
      CacheRequest cacheRequest = maybeCache(response, networkResponse.request(), cache);
      response = cacheWritingResponse(cacheRequest, response);
    }

    return response;
  }



```

1.  首先，根据`request`来判断`cache`中是否有缓存的`response`，如果有，得到这个`response`，然后进行判断当前`response`是否有效，没有将`cacheCandate`赋值为空。
2.  根据 request 判断缓存的策略，是否要使用了网络，缓存 或两者都使用
3.  调用下一个拦截器，决定从网络上来得到`response`
4.  如果本地已经存在`cacheResponse`，那么让它和网络得到的`networkResponse`做比较，决定是否来更新缓存的`cacheResponse`
5.  缓存未经缓存过的`response`

ConnectInterceptor: 建立连接
========================

```
 @Override 
public Response intercept(Chain chain) throws IOException {
       RealInterceptorChain realChain = (RealInterceptorChain) chain;
       Request request = realChain.request();
       StreamAllocation streamAllocation = realChain.streamAllocation();

       // We need the network to satisfy this request. Possibly for validating a conditional GET.
       boolean doExtensiveHealthChecks = !request.method().equals("GET");
       HttpCodec httpCodec = streamAllocation.newStream(client, doExtensiveHealthChecks);
       RealConnection connection = streamAllocation.connection();

       return realChain.proceed(request, streamAllocation, httpCodec, connection);
  }


```

实际上建立连接就是创建了一个`HttpCodec`对象，它将在后面的步骤中被使用，那它又是何方神圣呢？它是对 `HTTP` 协议操作的抽象，有两个实现：`Http1Codec`和`Http2Codec`，顾名思义，它们分别对应 `HTTP/1.1` 和 `HTTP/2` 版本的实现。

在 Http1Codec 中，它利用 [Okio](https://link.jianshu.com?t=https://github.com/square/okio/) 对 Socket 的读写操作进行封装，`Okio` 以后有机会再进行分析，现在让我们对它们保持一个简单地认识：它对 java.io 和 java.nio 进行了封装，让我们更便捷高效的进行 IO 操作。

而创建`HttpCodec`对象的过程涉及到`StreamAllocation、RealConnection`，代码较长，这里就不展开，这个过程概括来说，就是找到一个可用的`RealConnection`，再利用`RealConnection`的输入输出（`BufferedSource`和`BufferedSink`）创建`HttpCodec`对象，供后续步骤使用。

NetworkInterceptors
===================

配置 OkHttpClient 时设置的 NetworkInterceptors。

CallServerInterceptor：发送和接收数据
=============================

```
 @Override public Response intercept(Chain chain) throws IOException {
    HttpCodec httpCodec = ((RealInterceptorChain) chain).httpStream();
    StreamAllocation streamAllocation = ((RealInterceptorChain) chain).streamAllocation();
    Request request = chain.request();

    long sentRequestMillis = System.currentTimeMillis();
    httpCodec.writeRequestHeaders(request);

    if (HttpMethod.permitsRequestBody(request.method()) && request.body() != null) {   //===(1)
      Sink requestBodyOut = httpCodec.createRequestBody(request, request.body().contentLength());
      BufferedSink bufferedRequestBody = Okio.buffer(requestBodyOut);
      request.body().writeTo(bufferedRequestBody);
      bufferedRequestBody.close();
    }

    httpCodec.finishRequest();

    Response response = httpCodec.readResponseHeaders()     //====(2)
        .request(request)
        .handshake(streamAllocation.connection().handshake())
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .build();

    if (!forWebSocket || response.code() != 101) {
      response = response.newBuilder()
          .body(httpCodec.openResponseBody(response))
          .build();
    }

    if ("close".equalsIgnoreCase(response.request().header("Connection"))
        || "close".equalsIgnoreCase(response.header("Connection"))) {
      streamAllocation.noNewStreams();
    }

    int code = response.code();
    if ((code == 204 || code == 205) && response.body().contentLength() > 0) {
      throw new ProtocolException(
          "HTTP " + code + " had non-zero Content-Length: " + response.body().contentLength());
    }

    return response;
  }


```

1.  检查请求方法，用`Httpcodec`处理`request`
2.  进行网络请求得到`response`
3.  返回 r`esponse`

### 总结

前面说了拦截器用了`责任链设计模式`, 它将请求一层一层向下传，知道有一层能够得到 Resposne 就停止向下传递，然后将`response`向上面的拦截器传递，然后各个拦截器会对`respone`进行一些处理，最后会传到`RealCall`类中通过`execute`来得到`esponse`。

异步请求的流程：
========

异步 get 请求示例如下：

```
private final OkHttpClient client = new OkHttpClient();

  public void run() throws Exception {
    Request request = new Request.Builder()
        .url("http://publicobject.com/helloworld.txt")
        .build();

    client.newCall(request).enqueue(new Callback() {
      @Override 
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override 
      public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
          System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        System.out.println(response.body().string());
      }
    });
  }


```

由代码中`client.newCall(request).enqueue(Callback)`，开始我们知道`client.newCall(request)`方法返回的是`RealCall`对象，接下来继续向下看`enqueue()`方法:

```
   //异步任务使用
    @Override 
    public void enqueue(Callback responseCallback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        client.dispatcher().enqueue(new AsyncCall(responseCallback));
    }


```

调用了上面我们没有详细说的`Dispatcher`类中的`enqueue(Call )`方法. 接着继续看：

```
synchronized void enqueue(AsyncCall call) {
        if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
            runningAsyncCalls.add(call);
            executorService().execute(call);
        } else {
            readyAsyncCalls.add(call);
        }
    }


```

如果中的`runningAsynCalls`不满，且`call`占用的`host`小于最大数量，则将`call`加入到`runningAsyncCalls`中执行，同时利用线程池执行`call`；否者将`call`加入到`readyAsyncCalls`中。`runningAsyncCalls`和`readyAsyncCalls`是什么呢？看下面：

```
/** Ready async calls in the order they'll be run. */
private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>(); //正在准备中的异步请求队列

/** Running asynchronous calls. Includes canceled calls that haven't finished yet. */
private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>(); //运行中的异步请求

/** Running synchronous calls. Includes canceled calls that haven't finished yet. */
private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>(); //同步请求


```

`call`加入到线程池中执行了。现在再看`AsynCall的`代码，它是`RealCall`中的内部类:

```
//异步请求
    final class AsyncCall extends NamedRunnable {
        private final Callback responseCallback;

        private AsyncCall(Callback responseCallback) {
            super("OkHttp %s", redactedUrl());
            this.responseCallback = responseCallback;
        }

        String host() {
            return originalRequest.url().host();
        }

        Request request() {
            return originalRequest;
        }

        RealCall get() {
            return RealCall.this;
        }

        @Override protected void execute() {
            boolean signalledCallback = false;
            try {
                Response response = getResponseWithInterceptorChain();
                if (retryAndFollowUpInterceptor.isCanceled()) {
                    signalledCallback = true;
                    responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
                } else {
                    signalledCallback = true;
                    responseCallback.onResponse(RealCall.this, response);
                }
            } catch (IOException e) {
                if (signalledCallback) {
                    // Do not signal the callback twice!
                    Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
                } else {
                    responseCallback.onFailure(RealCall.this, e);
                }
            } finally {
                client.dispatcher().finished(this);
            }
        }
    }


```

`AysncCall`中的`execute()`中的方法，同样是通过`Response response = getResponseWithInterceptorChain();`来获得 response，这样异步任务也同样通过了 interceptor，剩下的流程就和上面一样了。

### 结语：

看到这，不知道你是否明白了 OkHttp 的请求过程，如果有什么问题或意见，欢迎私信。

#### 参考

1.  [OkHttp 官方教程解析 - 彻底入门 OkHttp 使用](https://link.jianshu.com?t=http://blog.csdn.net/mynameishuangshuai/article/details/51303446)
2.  [拆轮子系列：拆 OkHttp](https://link.jianshu.com?t=http://blog.piasy.com/2016/07/11/Understand-OkHttp/)