####  Android P - CLEARTEXT communication not permitted by network security policy

问题原因： Android P 限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉

解决方案：

在资源文件新建xml目录，新建文件


```
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">android.bugly.qq.com</domain>
    </domain-config>
</network-security-config>
 
清单文件配置：android:networkSecurityConfig="@xml/network_security_config"

```

但还是建议都使用https进行传输