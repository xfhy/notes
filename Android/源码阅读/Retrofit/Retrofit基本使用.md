
> Retrofit底层是基于OkHttp实现的.与其他网络框架不同的是,它更多使用使用了运行时注解的方式提供功能.

### Retrofit的基本用法

引入 
```
api 'com.squareup.retrofit2:retrofit:2.4.0' 

//为了增加支持返回值为 Gson 类型数据所需要添加的依赖包
api 'com.squareup.retrofit2:converter-gson:2.4.0' 
```

### Retrofit的注解分类

Retrofit的注解分为3大类


**HTTP请求方法**

- **GET**
- **POST** 向指定资源提交数据进行处理请求（例如提交表单或者上传文件）。数据被包含在请求体中。POST请求可能会导致新的资源的建立和/或已有资源的修改。
- PUT  从客户端向服务器传送的数据取代指定的文档的内容。
- DELETE  请求服务器删除指定的页面。
- HEAD  类似于get请求，只不过返回的响应中没有具体的内容，用于获取报头
- PATCH 该请求是对put请求的补充，用于更新局部资源
- OPTION 允许客户端查看服务器的性能。
- HTTP 通用注解, 可以替换以上所有的注解，其拥有三个属性：method，path，hasBody

**标注类注解**

- **FormURLEncoded**
- Multipard
- Streaming

**参数类注解**

- **Header** 作为方法的参数传入，用于添加不固定值的Header，该注解会更新已有的请求头
- Headers  用于添加固定请求头，可以同时添加多个。通过该注解添加的请求头不会相互覆盖，而是共同存在
- Body  多用于post请求发送非表单数据, 比如想要以post方式传递json格式数据
- **Path**  用于url中的占位符,{占位符}和PATH只用在URL的path部分，url中的参数使用Query和QueryMap代替，保证接口定义的简洁
- **Field**   用于post请求中表单字段, Filed和FieldMap需要FormUrlEncoded结合使用
- FieldMap  和@Filed作用一致，用于不确定表单参数
- Part   用于表单字段, Part和PartMap与Multipart注解结合使用, 适合文件上传的情况
- PartMap  用于表单字段, 默认接受的类型是Map<String,REquestBody>，可用于实现多文件上传
- **Query**  用于Get中指定参数
- QueryMap  和Query使用类似
- ...