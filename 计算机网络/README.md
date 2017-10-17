## 高频

* 常见的http状态码的含义
* 304与200的区别
* HTTP, UDP, TCP
* 三次握手, 四次挥手
* https, http2
* GET与POST区别
* tcp原理
* 304缓存的原理
* 域名收敛是什么
* restful method的解释
* socket套接字是在协议中的哪一层?
* accept是什么，怎么用
* 301, 302, 303, 307区别
* tcp与udp特点与区别
* tcp的可靠传输以及流量控制是如何实现的
* ip协议需要知道端口吗
* https的加密在哪一层实现
* CDN路由回溯定向
* 正常情况下，如果浏览器已经登录了百度账号，再另外打开一个tab页是会自动保持登录状态的，问怎样杜绝这个事情，使得每一次打开都是重新登录，给出实现方案。

## get和post的区别

* HTTP规范指出，get从服务器上获取资源，post是向服务器上传资源
* get传输的数据量小，post传输的数据量相对大一些。
* get数据在url中可见，post数据不会显示在url中。
* post安全性更高，因为参数不会被保存在浏览器历史中以及服务器日历中。
* get请求可以被缓存以及存入到书签里，post不可以。
* 编码类型不一样，get: `application/x-www-form-urlencoded`, post: `application/x-www-form-urlencoded`或`multipart/form-data`。
