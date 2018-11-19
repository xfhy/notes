
## 坑

#### 1. Android 9.0 适配

> Bugly官方文档上最高是适配的8.x,刚开始的时候我拿Android P进行测试也是无效果.后来发现log中有一句`Cleartext HTTP traffic to android.bugly.qq.com not permitted`. 拿起就是一阵Google,发现原来是Android P需要进行适配(限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉).  当我发现这个的时候,仿佛看到了一丝光明,抱着试一试的心态,搞了一下,果然可行.(测试机型为Pixel 2,Android 9.0)

Android 9.0上会报以下错误,联网会失败:
```
2018-10-10 16:39:21.312 31611-31646/com.xfhy.tinkerfirmdemo W/CrashReport: java.io.IOException: Cleartext HTTP traffic to android.bugly.qq.com not permitted
        at com.android.okhttp.HttpHandler$CleartextURLFilter.checkURLPermitted(HttpHandler.java:115)
        at com.android.okhttp.internal.huc.HttpURLConnectionImpl.execute(HttpURLConnectionImpl.java:458)
        at com.android.okhttp.internal.huc.HttpURLConnectionImpl.connect(HttpURLConnectionImpl.java:127)
        at com.android.okhttp.internal.huc.HttpURLConnectionImpl.getOutputStream(HttpURLConnectionImpl.java:258)
        at com.tencent.bugly.proguard.ai.a(BUGLY:265)
        at com.tencent.bugly.proguard.ai.a(BUGLY:114)
        at com.tencent.bugly.proguard.al.run(BUGLY:355)
        at com.tencent.bugly.proguard.ak$1.run(BUGLY:723)
        at java.lang.Thread.run(Thread.java:764)
2018-10-10 16:39:21.312 31611-31646/com.xfhy.tinkerfirmdemo E/CrashReport: Failed to upload, please check your network.
2018-10-10 16:39:21.312 31611-31646/com.xfhy.tinkerfirmdemo D/CrashReport: Failed to execute post.
2018-10-10 16:39:21.312 31611-31646/com.xfhy.tinkerfirmdemo E/CrashReport: [Upload] Failed to upload(1): Failed to upload for no response!
2018-10-10 16:39:21.313 31611-31646/com.xfhy.tinkerfirmdemo E/CrashReport: [Upload] Failed to upload(1) userinfo: failed after many attempts
```

**解决办法**

- 具体原因:Android P - CLEARTEXT communication not permitted by network security policy  [详细介绍](http://www.douevencode.com/articles/2018-07/cleartext-communication-not-permitted/)

在资源文件新建xml目录，新建文件
`network_security_config.xml`
```
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">android.bugly.qq.com</domain>
    </domain-config>
</network-security-config>
```

然后在清单文件中application下加入`android:networkSecurityConfig="@xml/network_security_config"`即可