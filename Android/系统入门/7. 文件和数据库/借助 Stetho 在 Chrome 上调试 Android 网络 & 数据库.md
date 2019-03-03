> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/03da9f91f41f

## 先来谈谈我的数据库调试历程

### 第一阶段：

![](https://upload-images.jianshu.io/upload_images/1314924-0cbd87c45af65126.png)

这个熟悉的界面，记得那是 13 年初的时候，想要查看 sqlite 里面的数据都要通过这个 Android Device Monitor 找到 / data/data/com.xxx.xxx/databases 里面的 db 文件，然后导出到 PC 上，最后用 PC 上的数据库工具打开来查看。

> 还会遇到 data 文件夹死活打不开的情况（权限问题），说多了都是泪😭

### 第二阶段：

![](https://upload-images.jianshu.io/upload_images/1314924-0131b5d0a8a2cb4c.png)

后来手机上出现了一些资源查看的 App(需要 root 权限)，可以直接在手机上查看数据库啦~(上图为`RE文件管理器`的截图)

这比上面那种方式真是方便太多了，但也有很多不足之处，比如需要两个应用切换来切换去、当数据量比较大的时候会比较卡。

![](https://upload-images.jianshu.io/upload_images/1314924-06f4479f658ac815.png)

### 第三阶段：

下面就是本文的重点啦~ 通过 chrome 来查看 android 数据库。

主角：Facebook 推出的`Stetho`，[官网](https://link.jianshu.com?t=http://facebook.github.io/stetho/)

集成步骤：

1.  引入依赖包

    ```
    compile 'com.facebook.stetho:stetho:1.3.1'

    ```

2.  初始化一下

    ```
    public class MyApplication extends Application {
      public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
      }
    }

    ```

3.  运行 App, 打开 Chrome 输入`chrome://inspect/#devices`（别忘了用数据线把手机和电脑连起来哦）

![](https://upload-images.jianshu.io/upload_images/1314924-bdcd9467d4c5e2ed.png)

如上图，chrome 会检测到我们的 app，点击`inspect`进入查看页面

![](https://upload-images.jianshu.io/upload_images/1314924-a2c37f9c2afde97e.png)

注：有读者反馈第一次打开有遇到空白的情况，这时只要翻个墙就好了（20160817 更新）

做过 Web 前端开发的小伙伴们对这个界面应该再熟悉不过了，此时如果你的 app 中有数据库存在的话就可以在`Resources`下的`Web SQL`中找到你的数据库文件查看数据库中的内容啦~

> 如果想要修改的话，可以点击数据库文件名就进入 cmd 模式了，可以通过 sql 命令来增删改查啦~<sub>(≧▽≦)/</sub>

![](https://upload-images.jianshu.io/upload_images/1314924-2c174ada910693e6.png)

> 完整测试代码，将在文末给出 (<sup>o</sup>)/

## 再来谈谈我的网络调试历程

### 第一阶段：

用的最挫的方式，那就是想要看返回结果什么的时候，自己用 Log 打印出来看咯😓

> 打印对象的话，可以用下`FastJson：JSON.toJSONString(object);`

### 第二阶段：

一些第三方的网络请求库，都可以添加拦截器，然后就可以在拦截器中把

`Request Url`

`Request Headers`

`Request Body`

`Response Body`

`Response Code`

之类都打印出来看

如 OkHttp：

```
OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                LogUtils.i("url:" + request.url());
                LogUtils.i("request headers" + JSON.toJSONString(request.headers()));
                LogUtils.i("request body:" + bodyToString(request));
                long t1 = System.nanoTime();
                Response response = chain.proceed(request);
                long t2 = System.nanoTime();

                double time = (t2 - t1) / 1e6d;
                LogUtils.i("time:" + time);
                LogUtils.i("code:" + response.code());

                return response;
            }
        }).build();

    private String bodyToString(final Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

```

也用一些抓包工具，如 PC 上的 Fidder，Mac 上的 Charles。

但有时候只是想看看请求的数据和返回的数据，设置代理什么的感觉也挺麻烦的。

### 第三阶段

通过`Stetho`来实现，chrome 调试 Android 网络请求。

> 注：这里的例子是基于采用 okhttp 来发请求的，如果是`HttpURLConnection`可以到 [Stetho 官网](https://link.jianshu.com?t=http://facebook.github.io/stetho/)查看相关配置

步骤：

1.  引入依赖包

    ```
    compile 'com.facebook.stetho:stetho:1.3.1'
    compile 'com.facebook.stetho:stetho-okhttp3:1.3.1'
    compile 'com.squareup.retrofit2:retrofit:2.0.0-beta4'
    compile 'com.squareup.okhttp3:okhttp:3.2.0'
    compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta4'

    ```

2.  初始化一下

    ```
    public class MyApplication extends Application {
      public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
      }
    }

    ```

3.  添加拦截器

    ```
    OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

    ```

> 注：是 addNetworkInterceptor 不是 addInterceptor

1.  运行 App, 打开 Chrome 输入`chrome://inspect/#devices`（跟上文查看数据库内容的步骤一样）

    点击`inspect`进入查看页面，然后在 app 用 okhttp 发起一个请求，就可以在`Network`下拦截到请求的相关数据了。

    ![](https://upload-images.jianshu.io/upload_images/1314924-a77a9122966521f2.png)

    nice~

> 小贴士：用 Chrome 开发者工具抓包的时候，会发现如果页面跳转了，那么上一个页面的请求信息就没有了。这个只要勾选上`Preserve log`就不会了。

有个小问题, Response 返回的 json 数据没法自动格式化显示 =_=，有解决过这个问题的朋友还请留言告知，3Q~

![](https://upload-images.jianshu.io/upload_images/1314924-1910cb11589a73bf.png)

## One more thing

界面 UI 树状结构也能抓的到呀！！！

`Elements`下查看~

普通 text 信息还可以直接修改并在手机上预览效果。<sub>(≧▽≦)/</sub>

![](https://upload-images.jianshu.io/upload_images/1314924-93a5c9d21b1765ff.png)

全部测试代码下载地址:

[https://github.com/hellsam/StethoTest](https://link.jianshu.com?t=https://github.com/hellsam/StethoTest)

欢迎留言交流，如有描述不当或错误的地方还请留言告知！

觉得还不错的话，点个赞支持一下呗 O(∩_∩)O