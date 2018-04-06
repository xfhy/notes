

# 一、Http方法

## GET
> 获取资源

当前网络请求中，绝大部分使用的都是GET方法。

## HEAD
> 获取报文首部

和GET方法一样，但是不返回报文实体主体部分。主要用于确认URL的有效性以及资源更新的日期时间等。

## POST
> 传输实体主体

POST主要用于传输数据，而GET主要用来获取资源。（详情见后面GET和POST的区别）

## PACTH
>对资源进行部分修改

PUT也可以用于修改资源，但是只能完全替代原始资源，PATCH允许部分修改。

## DELETE
> 删除文件

与PUT功能相反，并且同样不带验证机制。

## OPTIONS
>查询支持的方法

会返回Allow:GET、POST、HEAD、OPTIONS这样的内容

## CONNECT
> 要求用隧道协议连接代理

要求在与代理服务器听信时建立隧道，使用SSL（安全套接层）和TLS（传输安全）协议把通信内容加密后经网络隧道传输。

## TRACE
> 追踪路径
 服务器会将通信路径返回给客户端。发送请求时，在Max-Forwards首部字段中填入数值，每经过一个服务器就会减1，当数值为0时就停止传输。通常不会使用TRACE，并且它容易受到XST（跨站追踪）攻击，因为更不会去使用它。

 # 二、HTTP状态码

>服务器返回的 响应报文 中第一行为状态行，包含了状态码以及原因短语，用来告知客户端请求的结果。

| 状态码 | 类别 | 原因短语 |
| :---: | :---: | :---: |
| 1XX | Informational（信息性状态码） | 接收的请求正在处理 |
| 2XX | Success（成功状态码） | 请求正常处理完毕 |
| 3XX | Redirection（重定向状态码） | 需要进行附加操作以完成请求 |
| 4XX | Client Error（客户端错误状态码） | 服务器无法处理请求 |
| 5XX | Server Error（服务器错误状态码） | 服务器处理请求出错 |

## 1XX信息

- 100 Continue ：表明到目前为止都很正常，客户端可以继续发送请求或者忽略这个响应。

## 2XX成功

- 200 OK
- 204 No Content ：请求已经成功处理，但是返回的响应报文不包含实体的主体部分。一般在只需要从客户端往服务器发送信息，而不需要返回数据时使用。
- 206 Partial Content ：表示客户端进行了范围请求。响应报文包含由 Content-Range 指定范围的实体内容。

## 3XX重定向

- 301 Moved Permanently ：永久性重定向
- 302 Found ：临时性重定向
- 303 See Other ：和 302 有着相同的功能，但是 303 明确要求客户端应该采用 GET 方法获取资源。
- 304 Not Modified ：如果请求报文首部包含一些条件，例如：If-Match，If-ModifiedSince，If-None-Match，If-Range，If-Unmodified-Since，如果不满足条件，则服务器会返回 304 状态码。
- 307 Temporary Redirect ：临时重定向，与 302 的含义类似，但是 307 要求浏览器不会把重定向请求的 POST 方法改成 GET 方法。

## 4XX客户端错误

- 400 Bad Request ：请求报文中存在语法错误
- 401 Unauthorized ：该状态码表示发送的请求需要有认证信息（BASIC 认证、DIGEST 认证）。如果之前已进行过一次请求，则表示用户认证失败。
- 403 Forbidden ：请求被拒绝，服务器端没有必要给出拒绝的详细理由。
- 404 Not Found

## 5XX 服务器错误

- 500 Internal Server Error ：服务器正在执行请求时发生错误。
- 503  Service Unavilable ：服务器暂时处于超负载或正在进行停机维护，现在无法处理请求。

# 三、具体应用

## Cookie
HTTP 协议是无状态的，主要是为了让 HTTP 协议尽可能简单，使得它能够处理大量事务。HTTP/1.1 引入 Cookie 来保存状态信息。

Cookie 是服务器发送给客户端的数据，该数据会被保存在浏览器中，并且客户端的下一次请求报文会包含该数据。通过 Cookie 可以让服务器知道两个请求是否来自于同一个客户端，从而实现保持登录状态等功能。

### 1.创建过程
服务器发送的响应报文包含 Set-Cookie 字段，客户端得到响应报文后把 Cookie 内容保存到浏览器中。

```
HTTP/1.0 200 OK
Content-type: text/html
Set-Cookie: yummy_cookie=choco
Set-Cookie: tasty_cookie=strawberry

[page content]
```

客户端之后发生请求时，会从浏览器中读出Cookie值，在请求报文中包含Cookie字段。

```
GET /sample_page.html HTTP/1.1
Host: www.example.org
Cookie: yummy_cookie=choco; tasty_cookie=strawberry
```

### 2.分类

- 会话期Cookie：浏览器关闭之后它会被自动删除，也就是说它仅在会话期内有效。
- 持久性 Cookie：指定一个特定的过期时间（Expires）或有效期（Max-Age）之后就成为了持久性的 Cookie。

`Set-Cookie: id=a3fWa; Expires=Wed, 21 Oct 2015 07:28:00 GMT;`

### 3.Session和Cookie区别

Session 是服务器用来跟踪用户的一种手段，每个 Session 都有一个唯一标识：Session ID。当服务器创建了一个 Session 时，给客户端发送的响应报文包含了 Set-Cookie 字段，其中有一个名为 sid 的键值对，这个键值对就是 Session ID。客户端收到后就把 Cookie 保存在浏览器中，并且之后发送的请求报文都包含 Session ID。HTTP 就是通过 Session 和 Cookie 这两种方式一起合作来实现跟踪用户状态的，Session 用于服务器端，Cookie 用于客户端。

## 缓存

### 1.优点

1.降低服务器的负担；
2.提高响应速度（缓存资源比服务器上的资源离客户端更近）。

### 2.Cache-Control字段

HTTP通过Cache-Control首部字段来控制缓存
```
Cache-Control: private, max-age=0, no-cache
```
