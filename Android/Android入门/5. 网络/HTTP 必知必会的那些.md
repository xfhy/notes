> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/Fazx13maQfPJItfkOqk9FQ

<section class="" style="font-size: 15px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

> 本文由`玉刚说写作平台`提供写作赞助
> 赞助金额：`200元`
> 原作者：`竹千代`
> 版权声明：本文版权归微信公众号`玉刚说`所有，未经许可，不得以任何形式转载

Http 是我们经常打交道的网络应用层协议，它的重要性可能不需要再强调。但是实际上很多人，包括我自己可能对 http 了解的并不够深。本文就我自己的学习心得，分享一下我认为需要知道的缓存所涉及到的相关知识点。

### Http 报文

* * *

首先我们来点基础的，看看 http 报文具体的格式。http 报文可以分为请求报文和响应报文，格式大同小异。主要分为三个部分：

1.  起始行

2.  首部

3.  主体

请求报文格式：

```
<method> <request-url> <version><headers><entity-body>
```

响应报文格式

```
<version> <status> <reason-phrase><headers><entity-body>
```

从请求报文格式和响应报文格式可以看出，两者主要在起始行上有差异。这里稍微解释一下各个标签：

```
<method> 指请求方法，常用的主要是Get、 Post、Head 还有其他一些我们这里就不说了，有兴趣的可以自己查阅一下<version> 指协议版本，现在通常都是Http/1.1了<request-url> 请求地址<status> 指响应状态码， 我们熟悉的200、404等等<reason-phrase> 原因短语，200 OK 、404 Not Found 这种后面的描述就是原因短语，通常不必太关注。
```

#### method

我们知道请求方法最常用的有 Get 和 Post 两种，面试时也常常会问到这两者有什么区别，通常什么情况下使用。这里我们来简单说一说。

两个方法之间在传输形式上有一些区别，通过 Get 方法发起请求时，会将请求参数拼接在 request-url 尾部，格式是 url?param1=xxx&param2=xxx&[…]。

我们需要知道，这样传输参数会使得参数都暴露在地址栏中。并且由于 url 是 ASCII 编码的，所以参数中如果有 Unicode 编码的字符，例如汉字，都会编码之后传输。另外值得注意的是，虽然 http 协议并没有对 url 长度做限制，但是一些浏览器和服务器可能会有限制，所以通过 GET 方法发起的请求参数不能够太长。而通过 POST 方法发起的请求是将参数放在请求体中的，所以不会有 GET 参数的这些问题。

另外一点差别就是方法本身的语义上的。GET 方法通常是指从服务器获取某个 URL 资源，其行为可以看作是一个读操作，对同一个 URL 进行多次 GET 并不会对服务器产生什么影响。而 POST 方法通常是对某个 URL 进行添加、修改，例如一个表单提交，通常会往服务器插入一条记录。多次 POST 请求可能导致服务器的数据库中添加了多条记录。所以从语义上来讲，两者也是不能混为一谈的。

#### 状态码

常见的状态码主要有  
200 OK  请求成功，实体包含请求的资源  
301 Moved Permanent 请求的 URL 被移除了，通常会在 Location 首部中包含新的 URL 用于重定向。  
304 Not Modified    条件请求进行再验证，资源未改变。  
404 Not Found       资源不存在  
206 Partial Content 成功执行一个部分请求。这个在用于断点续传时会涉及到。

#### header

在请求报文和响应报文中都可以携带一些信息，通过与其他部分配合，能够实现各种强大的功能。这些信息位于起始行之下与请求实体之间，以键值对的形式，称之为首部。每条首部以回车换行符结尾，最后一个首部额外多一个换行，与实体分隔开。

这里我们重点关注一下  
Date  
Cache-Control  
Last-Modified  
Etag  
Expires  
If-Modified-Since  
If-None-Match  
If-Unmodified-Since  
If-Range  
If-Match

Http 的首部还有很多，但限于篇幅我们不一一讨论。这些首部都是 Http 缓存会涉及到的，在下文中我们会来说说各自的作用。

#### 实体

请求发送的资源，或是响应返回的资源。

### Http 缓存

* * *

当我们发起一个 http 请求后，服务器返回所请求的资源，这时我们可以将该资源的副本存储在本地，这样当再次对该 url 资源发起请求时，我们能快速的从本地存储设备中获取到该 url 资源，这就是所谓的缓存。缓存既可以节约不必要的网络带宽，又能迅速对 http 请求做出响应。

> 先摆出几个概念：
> 
> 1.  新鲜度检测
>     
>     
> 2.  再验证
>     
>     
> 3.  再验证命中

我们知道，有些 url 所对应的资源并不是一成不变的，服务器中该 url 的资源可能在一定时间之后会被修改。这时本地缓存中的资源将与服务器一侧的资源有差异。

既然在一定时间之后可能资源会改变，那么在某个时间之前我们可以认为这个资源没有改变，从而放心大胆的使用缓存资源，当请求时间超过来该时间，我们认为这个缓存资源可能不再与服务器端一致了。所以当我们发起一个请求时，我们需要先对缓存的资源进行判断，看看究竟我们是否可以直接使用该缓存资源，这个就叫做`新鲜度检测`。即每个资源就像一个食品一样，拥有一个过期时间，我们吃之前需要先看看有没有过期。

如果发现该缓存资源已经超过了一定的时间，我们再次发起请求时不会直接将缓存资源返回，而是先去服务器查看该资源是否已经改变，这个就叫做`再验证`。如果服务器发现对应的 url 资源并没有发生变化，则会返回`304 Not Modified`，并且不再返回对应的实体。这称之为`再验证命中`。相反如果再验证未命中，则返回`200 OK`，并将改变后的 url 资源返回，此时缓存可以更新以待之后请求。

我们看看具体的实现方式：

> 1.  新鲜度检测  
>     我们需要通过检测资源是否超过一定的时间，来判断缓存资源是否新鲜可用。那么这个一定的时间怎么决定呢？其实是由服务器通过在响应报文中增加`Cache-Control:max-age`，或是`Expire`这两个首部来实现的。值得注意的是 Cache-Control 是 http1.1 的协议规范，通常是接相对的时间，即多少秒以后，需要结合`last-modified`这个首部计算出绝对时间。而 Expire 是 http1.0 的规范，后面接一个绝对时间。
>     
>     
> 2.  再验证  
>     如果通过新鲜度检测发现需要请求服务器进行再验证，那么我们至少需要告诉服务器，我们已经缓存了一个什么样的资源了，然后服务器来判断这个缓存资源到底是不是与当前的资源一致。逻辑是这样没错。那怎么告诉服务器我当前已经有一个备用的缓存资源了呢？我们可以采用一种称之为`条件请求`的方式实现再验证。
>     
>     
> 3.  Http 定义了 5 个首部用于条件请求:  
>     If-Modified-Since  
>     If-None-Match  
>     If-Unmodified-Since  
>     If-Range  
>     If-Match

If-Modified-Since 可以结合`Last-Modified`这个服务器返回的响应首部使用，当我们发起条件请求时，将 Last-Modified 首部的值作为 If-Modified-Since 首部的值传递到服务器，意思是查询服务器的资源自从我们上一次缓存之后是否有修改。

If-None-Match 需要结合另一个`Etag`的服务器返回的响应首部使用。Etag 首部实际上可以认为是服务器对文档资源定义的一个版本号。有时候一个文档被修改了，可能所做的修改极为微小，并不需要所有的缓存都重新下载数据。或者说某一个文档的修改周期极为频繁，以至于以秒为时间粒度的判断已经无法满足需求。这个时候可能就需要 Etag 这个首部来表明这个文档的版号了。发起条件请求时可将缓存时保存下来的 Etag 的值作为 If-None-Match 首部的值发送至服务器，如果服务器的资源的 Etag 与当前条件请求的 Etag 一致，表明这次再验证命中。  

其他三个与断点续传涉及到的相关知识有关，本文暂时不讨论。待我之后写一篇文章来讲讲断点续传。

### OkHttp 的缓存

* * *

缓存的 Http 理论知识大致就是这么些。我们从 OkHttp 的源码来看看，这些知名的开源库是如何利用 Http 协议实现缓存的。这里我们假设读者对 OkHttp 的请求执行流程有了大致的了解，并且只讨论缓存相关的部分。对于 OkHttp 代码不熟悉的同学，建议先看看相关代码或是其他文章。

我们知道 OkHttp 的请求在发送到服务器之前会经过一系列的 Interceptor，其中有一个 CacheInterceptor 即是我们需要分析的代码。

```
 final InternalCache cache;@Override public Response intercept(Chain chain) throws IOException {    Response cacheCandidate = cache != null        ? cache.get(chain.request())        : null;    long now = System.currentTimeMillis();    CacheStrategy strategy = new CacheStrategy.Factory(now, chain.request(), cacheCandidate).get();    Request networkRequest = strategy.networkRequest;    Response cacheResponse = strategy.cacheResponse;    ...... }
```

方法首先通过 InternalCache 获取到对应请求的缓存。这里我们不展开讨论这个类的具体实现，只需要知道，如果之前缓存了该请求 url 的资源，那么通过 request 对象可以查找到这个缓存响应。

将获取到的缓存响应，当前时间戳和请求传入 CacheStrategy，然后通过执行 get 方法执行一些逻辑最终可以获取到 strategy.networkRequest,strategy.cacheResponse。如果通过 CacheStrategy 的判断之后，我们发现这次请求无法直接使用缓存数据，需要向服务器发起请求，那么我们就通过 CacheStrategy 为我们构造的 networkRequest 来发起这次请求。我们先来看看 CacheStrategy 做了哪些事情。

```
CacheStrategy.Factory.javapublic Factory(long nowMillis, Request request, Response cacheResponse) {      this.nowMillis = nowMillis;      this.request = request;      this.cacheResponse = cacheResponse;      if (cacheResponse != null) {        this.sentRequestMillis = cacheResponse.sentRequestAtMillis();        this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();        Headers headers = cacheResponse.headers();        for (int i = 0, size = headers.size(); i < size; i++) {          String fieldName = headers.name(i);          String value = headers.value(i);          if ("Date".equalsIgnoreCase(fieldName)) {            servedDate = HttpDate.parse(value);            servedDateString = value;          } else if ("Expires".equalsIgnoreCase(fieldName)) {            expires = HttpDate.parse(value);          } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {            lastModified = HttpDate.parse(value);            lastModifiedString = value;          } else if ("ETag".equalsIgnoreCase(fieldName)) {            etag = value;          } else if ("Age".equalsIgnoreCase(fieldName)) {            ageSeconds = HttpHeaders.parseSeconds(value, -1);          }        }      }    }
```

CacheStrategy.Factory 的构造方法首先保存了传入的参数，并将缓存响应的相关首部解析保存下来。之后调用的 get 方法如下

```
    public CacheStrategy get() {      CacheStrategy candidate = getCandidate();      if (candidate.networkRequest != null && request.cacheControl().onlyIfCached()) {        // We're forbidden from using the network and the cache is insufficient.        return new CacheStrategy(null, null);      }      return candidate;    }
```

get 方法很简单，主要逻辑在 getCandidate 中，这里的逻辑是如果返回的 candidate 所持有的 networkRequest 不为空，表示我们这次请求需要发到服务器，此时如果请求的 cacheControl 要求本次请求只使用缓存数据。那么这次请求恐怕只能以失败告终了，这点我们等会儿回到 CacheInterceptor 中可以看到。接着我们看看主要 getCandidate 的主要逻辑。

```
    private CacheStrategy getCandidate() {      // No cached response.      if (cacheResponse == null) {        return new CacheStrategy(request, null);      }      // Drop the cached response if it's missing a required handshake.      if (request.isHttps() && cacheResponse.handshake() == null) {        return new CacheStrategy(request, null);      }      // If this response shouldn't have been stored, it should never be used      // as a response source. This check should be redundant as long as the      // persistence store is well-behaved and the rules are constant.      if (!isCacheable(cacheResponse, request)) {        return new CacheStrategy(request, null);      }      CacheControl requestCaching = request.cacheControl();      if (requestCaching.noCache() || hasConditions(request)) {        return new CacheStrategy(request, null);      }        ......    }
```

上面这段代码主要列出四种情况下需要忽略缓存，直接想服务器发起请求的情况：

1.  缓存本身不存在

2.  请求是采用 https 并且缓存没有进行握手的数据。

3.  缓存本身不应该不保存下来。可能是缓存本身实现有问题，把一些不应该缓存的数据保留了下来。

4.  如果请求本身添加了 Cache-Control: No-Cache，或是一些条件请求首部，说明请求不希望使用缓存数据。

这些情况下直接构造一个包含 networkRequest，但是 cacheResponse 为空的 CacheStrategy 对象返回。

```
    private CacheStrategy getCandidate() {      ......      CacheControl responseCaching = cacheResponse.cacheControl();      if (responseCaching.immutable()) {        return new CacheStrategy(null, cacheResponse);      }      long ageMillis = cacheResponseAge();      long freshMillis = computeFreshnessLifetime();      if (requestCaching.maxAgeSeconds() != -1) {        freshMillis = Math.min(freshMillis, SECONDS.toMillis(requestCaching.maxAgeSeconds()));      }      long minFreshMillis = 0;      if (requestCaching.minFreshSeconds() != -1) {        minFreshMillis = SECONDS.toMillis(requestCaching.minFreshSeconds());      }      long maxStaleMillis = 0;      if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {        maxStaleMillis = SECONDS.toMillis(requestCaching.maxStaleSeconds());      }      if (!responseCaching.noCache() && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {        Response.Builder builder = cacheResponse.newBuilder();        if (ageMillis + minFreshMillis >= freshMillis) {          builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");        }        long oneDayMillis = 24 * 60 * 60 * 1000L;        if (ageMillis > oneDayMillis && isFreshnessLifetimeHeuristic()) {          builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");        }        return new CacheStrategy(null, builder.build());      }        ......         }
```

如果缓存响应的 Cache-Control 首部包含 immutable, 那么说明该资源不会改变。客户端可以直接使用缓存结果。值得注意的是 immutable 并不属于 http 协议的一部分，而是由 facebook 提出的扩展属性。

之后分别计算 ageMills、freshMills、minFreshMills、maxStaleMills 这四个值。 
如果响应缓存没有通过 Cache-Control:No-Cache 来禁止客户端使用缓存，并且

```
ageMillis + minFreshMillis < freshMillis + maxStaleMillis
```

这个不等式成立，那么我们进入条件代码块之后最终会返回 networkRequest 为空，并且使用当前缓存值构造的 CacheStrtegy。

这个不等式究竟是什么含义呢？我们看看这四个值分别代表什么。  

**ageMills** 指这个缓存资源自响应报文在源服务器中产生或者过期验证的那一刻起，到现在为止所经过的时间。用食品的保质期来比喻的话，好比当前时间距离生产日期已经过去了多久了。  

**freshMills** 表示这个资源在多少时间内是新鲜的。也就是假设保质期 18 个月，那么这个 18 个月就是 freshMills。 

**minFreshMills** 表示我希望这个缓存至少在多久之后依然是新鲜的。好比我是一个比较讲究的人，如果某个食品只有一个月就过期了，虽然并没有真的过期，但我依然觉得食品不新鲜从而不想再吃了。  

**maxStaleMills **好比我是一个不那么讲究的人，即使食品已经过期了，只要不是过期很久了，比如 2 个月，那我觉得问题不大，还可以吃。

minFreshMills 和 maxStatleMills 都是由请求首部取出的，请求可以根据自己的需要，通过设置

```
Cache-Control:min-fresh=xxx、Cache-Control:max-statle=xxx
```

来控制缓存，以达到对缓存使用严格性的收紧与放松。

```
    private CacheStrategy getCandidate() {        ......      // Find a condition to add to the request. If the condition is satisfied, the response body      // will not be transmitted.      String conditionName;      String conditionValue;      if (etag != null) {        conditionName = "If-None-Match";        conditionValue = etag;      } else if (lastModified != null) {        conditionName = "If-Modified-Since";        conditionValue = lastModifiedString;      } else if (servedDate != null) {        conditionName = "If-Modified-Since";        conditionValue = servedDateString;      } else {        return new CacheStrategy(request, null); // No condition! Make a regular request.      }      Headers.Builder conditionalRequestHeaders = request.headers().newBuilder();      Internal.instance.addLenient(conditionalRequestHeaders, conditionName, conditionValue);      Request conditionalRequest = request.newBuilder()          .headers(conditionalRequestHeaders.build())          .build();      return new CacheStrategy(conditionalRequest, cacheResponse);    }
```

如果之前的条件不满足，说明我们的缓存响应已经过期了，这时我们需要通过一个条件请求对服务器进行再验证操作。接下来的代码比较清晰来，就是通过从缓存响应中取出的`Last-Modified`,`Etag`,`Date`首部构造一个条件请求并返回。

接下来我们返回 CacheInterceptor

```
    // If we're forbidden from using the network and the cache is insufficient, fail.    if (networkRequest == null && cacheResponse == null) {      return new Response.Builder()          .request(chain.request())          .protocol(Protocol.HTTP_1_1)          .code(504)          .message("Unsatisfiable Request (only-if-cached)")          .body(Util.EMPTY_RESPONSE)          .sentRequestAtMillis(-1L)          .receivedResponseAtMillis(System.currentTimeMillis())          .build();    }
```

可以看到，如果我们返回的`networkRequest`和`cacheResponse`都为空，说明我们即没有可用的缓存，同时请求通过`Cache-Control:only-if-cached`只允许我们使用当前的缓存数据。这个时候我们只能返回一个 504 的响应。接着往下看，

```
    // If we don't need the network, we're done.    if (networkRequest == null) {      return cacheResponse.newBuilder()          .cacheResponse(stripBody(cacheResponse))          .build();    }
```

如果 networkRequest 为空，说明我们不需要进行再验证了，直接将 cacheResponse 作为请求结果返回。

```
Response networkResponse = null;    try {      networkResponse = chain.proceed(networkRequest);    } finally {      // If we're crashing on I/O or otherwise, don't leak the cache body.      if (networkResponse == null && cacheCandidate != null) {        closeQuietly(cacheCandidate.body());      }    }    // If we have a cache response too, then we're doing a conditional get.    if (cacheResponse != null) {      if (networkResponse.code() == HTTP_NOT_MODIFIED) {        Response response = cacheResponse.newBuilder()            .headers(combine(cacheResponse.headers(), networkResponse.headers()))            .sentRequestAtMillis(networkResponse.sentRequestAtMillis())            .receivedResponseAtMillis(networkResponse.receivedResponseAtMillis())            .cacheResponse(stripBody(cacheResponse))            .networkResponse(stripBody(networkResponse))            .build();        networkResponse.body().close();        // Update the cache after combining headers but before stripping the        // Content-Encoding header (as performed by initContentStream()).        cache.trackConditionalCacheHit();        cache.update(cacheResponse, response);        return response;      } else {        closeQuietly(cacheResponse.body());      }    }     Response response = networkResponse.newBuilder()        .cacheResponse(stripBody(cacheResponse))        .networkResponse(stripBody(networkResponse))        .build();    if (cache != null) {      if (HttpHeaders.hasBody(response) && CacheStrategy.isCacheable(response, networkRequest)) {        // Offer this request to the cache.        CacheRequest cacheRequest = cache.put(response);        return cacheWritingResponse(cacheRequest, response);      }      if (HttpMethod.invalidatesCache(networkRequest.method())) {        try {          cache.remove(networkRequest);        } catch (IOException ignored) {          // The cache cannot be written.        }      }    }    return response;
```

如果 networkRequest 存在不为空，说明这次请求是需要发到服务器的。此时有两种情况，一种 cacheResponse 不存在，说明我们没有一个可用的缓存，这次请求只是一个普通的请求。如果 cacheResponse 存在，说明我们有一个可能过期了的缓存，此时 networkRequest 是一个用来进行再验证的条件请求。

不管哪种情况，我们都需要通过 networkResponse=chain.proceed(networkRequest) 获取到服务器的一个响应。不同的只是如果有缓存数据，那么在获取到再验证的响应之后，需要 cache.update(cacheResponse, response) 去更新当前缓存中的数据。如果没有缓存数据，那么判断此次请求是否可以被缓存。在满足缓存的条件下，将响应缓存下来，并返回。

OkHttp 缓存大致的流程就是这样，我们从中看出，整个流程是遵循了 Http 的缓存流程的。最后我们总结一下缓存的流程：

1.  从接收到的请求中，解析出 Url 和各个首部。

2.  查询本地是否有缓存副本可以使用。

3.  如果有缓存，则进行新鲜度检测，如果缓存足够新鲜，则使用缓存作为响应返回，如果不够新鲜了，则构造条件请求，发往服务器再验证。如果没有缓存，就直接将请求发往服务器。

4.  把从服务器返回的响应，更新或是新增到缓存中。

### OAuth

* * *

OAuth 是一个用于授权第三方获取相应资源的协议。与以往的授权方式不同的是，OAuth 的授权能避免用户暴露自己的用户密码给第三方，从而更加的安全。OAuth 协议通过设置一个授权层，以区分用户和第三方应用。用户本身可以通过用户密码登陆服务提供商，获取到账户所有的资源。而第三方应用只能通过向用户请求授权，获取到一个 Access Token，用以登陆授权层，从而在指定时间内获取到用户授权访问的部分资源。

OAuth 定义的几个角色：

| Role | Description |
| --- | --- |
| Resource Owner | 可以授权访问某些受保护资源的实体，通常就是指用户 |
| Client | 可以通过用户的授权访问受保护资源的应用, 也就是第三方应用 |
| Authorization server | 在认证用户之后给第三方下发 Access Token 的服务器 |
| Resource Server | 拥有受保护资源的服务器，可以通过 Access Token 响应资源请求 |

```
     +--------+                               +---------------+     |        |--(A)- Authorization Request ->|   Resource    |     |        |                               |     Owner     |     |        |<-(B)-- Authorization Grant ---|               |     |        |                               +---------------+     |        |     |        |                               +---------------+     |        |--(C)-- Authorization Grant -->| Authorization |     | Client |                               |     Server    |     |        |<-(D)----- Access Token -------|               |     |        |                               +---------------+     |        |     |        |                               +---------------+     |        |--(E)----- Access Token ------>|    Resource   |     |        |                               |     Server    |     |        |<-(F)--- Protected Resource ---|               |     +--------+                               +---------------+
```

从上图可以看出，一个 OAuth 授权的流程主要可以分为 6 步：

1.  客户端向用户申请授权。

2.  用户同意授权。

3.  客户端通过获取的授权，向认证服务器申请 Access Token。

4.  认证服务器通过授权认证后，下发 Access Token。

5.  客户端通过获取的到 Access Token 向资源服务器发起请求。

6.  资源服务器核对 Access Token 后下发请求资源。

### Https

* * *

> 简单的说 Http + 加密 + 认证 + 完整性保护 = Https

传统的 Http 协议是一种应用层的传输协议，Http 直接与 TCP 协议通信。其本身存在一些缺点：

1.  Http 协议使用明文传输，容易遭到窃听。

2.  Http 对于通信双方都没有进行身份验证，通信的双方无法确认对方是否是伪装的客户端或者服务端。

3.  Http 对于传输内容的完整性没有确认的办法，往往容易在传输过程中被劫持篡改。

因此，在一些需要保证安全性的场景下，比如涉及到银行账户的请求时，Http 无法抵御这些攻击。  
Https 则可以通过增加的 SSL\TLS，支持对于通信内容的加密，以及对通信双方的身份进行验证。

#### Https 的加密

近代密码学中加密的方式主要有两类：

1.  对称秘钥加密

2.  非对称秘钥加密

对称秘钥加密是指加密与解密过程使用同一把秘钥。这种方式的优点是处理速度快，但是如何安全的从一方将秘钥传递到通信的另一方是一个问题。

非对称秘钥加密是指加密与解密使用两把不同的秘钥。这两把秘钥，一把叫公开秘钥，可以随意对外公开。一把叫私有秘钥，只用于本身持有。得到公开秘钥的客户端可以使用公开秘钥对传输内容进行加密，而只有私有秘钥持有者本身可以对公开秘钥加密的内容进行解密。这种方式克服了秘钥交换的问题，但是相对于对称秘钥加密的方式，处理速度较慢。

SSL\TLS 的加密方式则是结合了两种加密方式的优点。首先采用非对称秘钥加密，将一个对称秘钥使用公开秘钥加密后传输到对方。对方使用私有秘钥解密，得到传输的对称秘钥。之后双方再使用对称秘钥进行通信。这样即解决了对称秘钥加密的秘钥传输问题，又利用了对称秘钥的高效率来进行通信内容的加密与解密。

#### Https 的认证

SSL\TLS 采用的混合加密的方式还是存在一个问题，即怎么样确保用于加密的公开秘钥确实是所期望的服务器所分发的呢？也许在收到公开秘钥时，这个公开秘钥已经被别人篡改了。因此，我们还需要对这个秘钥进行认证的能力，以确保我们通信的对方是我们所期望的对象。

目前的做法是使用由数字证书认证机构颁发的公开秘钥证书。服务器的运营人员可以向认证机构提出公开秘钥申请。认证机构在审核之后，会将公开秘钥与共钥证书绑定。服务器就可以将这个共钥证书下发给客户端，客户端在收到证书后，使用认证机构的公开秘钥进行验证。一旦验证成功，即可知道这个秘钥是可以信任的秘钥。

**总结**
Https 的通信流程：

1.  Client 发起请求

2.  Server 端响应请求，并在之后将证书发送至 Client

3.  Client 使用认证机构的共钥认证证书，并从证书中取出 Server 端共钥。

4.  Client 使用共钥加密一个随机秘钥，并传到 Server

5.  Server 使用私钥解密出随机秘钥

6.  通信双方使用随机秘钥最为对称秘钥进行加密解密。

<section class="" style="max-width: 100%;color: rgb(51, 51, 51);">

— — — END — — —

**近期文章回顾**

*   [如何通俗理解设计模式及其思想？](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492895&idx=1&sn=1bb90c14d5e9693e819d3b0366f28da4&chksm=8eec8660b99b0f760790dfead84f6ab4095207d60e0ad5739c0f0724519ad1dc41dc738166a6&scene=21#wechat_redirect)

*   [Android 官方架构组件 Paging：分页库的设计美学](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492903&idx=1&sn=6040b030d2a8125f38b7c9e7bd8f3054&chksm=8eec8658b99b0f4e07cf1c550096b87c5551a6da124906a67911dcae4a0656814a6e5cc13c84&scene=21#wechat_redirect)

*   <pangu></pangu>[MVC、MVP、MVVM，我到底该怎么选？](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492883&idx=1&sn=2c206702fe1dd357ed65052bb9080488&chksm=8eec866cb99b0f7aabe917b584eee71dea51a57b22d54fac96cfbd420a0f53340350ae978321&scene=21#wechat_redirect) 

</section>

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WxEibQVO9fRJVxibanVicVCVL9oZ6Nh6ibZDuVbEwIKMibC6ba9nnM3FTpHka2SYLyDtSwvBhzwCIecdbQ/640?wx_fmt=png)

</section>