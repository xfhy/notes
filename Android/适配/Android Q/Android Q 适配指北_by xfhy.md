
## 1. 权限相关

首先是一些权限

![image](E1391B71CBC343F78928ABDD7D141CE4)

差旅壹号受影响的如下:

- 存储权限
- 定位权限
- 设备标识

### 1.1 存储权限

Android Q,每个APP存储的文件都在该APP对应的沙盒内,其他APP无法访问该沙盒内的文件.

APP访问自己沙盒内的文件无需申请权限,谷歌推荐是存储在Context.getExternalFilesDir()下面.

- 访问自己的文件:Q中用更精细的媒体特定权限替换并取消了 `READ_EXTERNAL_STORAGE` 和 `WRITE_EXTERNAL_STORAGE`权限，并且无需特定权限，应用即可访问自己沙盒中的文件。
- 访问系统媒体文件：Q中引入了一个新定义媒体文件的共享集合，如果要访问沙盒外的媒体共享文件，比如照片，音乐，视频等，需要申请新的媒体权限:`READ_MEDIA_IMAGES`,`READ_MEDIA_VIDEO`,`READ_MEDIA_AUDIO`,申请方法同原来的存储权限。

![image](51A8DC2930E540F5A852B725CE514BA3)

### 1.2 定位权限

为了让用户更好地控制应用对位置信息的访问权限，Android Q 引入了新的位置权限 ACCESS_BACKGROUND_LOCATION。


与现有的 ACCESS_FINE_LOCATION 和 ACCESS_COARSE_LOCATION 权限不同，新权限仅会影响应用在后台运行时对位置信息的访问权。除非应用的某个 Activity 可见或应用正在运行前台服务，否则应用将被视为在后台运行。

targetSDK <= P 应用如果请求了`ACCESS_FINE_LOCATION` 或 `ACCESS_COARSE_LOCATION`权限，Q设备会自动帮你申请`ACCESS_BACKGROUND_LOCATION`权限。

### 1.3 设备唯一标识符

从 Android Q 开始，应用必须具有 `READ_PRIVILEGED_PHONE_STATE` 签名权限才能访问设备的不可重置标识符（包含 IMEI 和序列号）。

设备唯一标识符需要特别注意，原来的READ_PHONE_STATE权限已经不能获得IMEI和序列号.

谷歌官方给予了设备唯一ID最佳做法，但是此方法给出的ID可变，可以按照具体需求具体解决。

本文给出一个不变和基本不重复的UUID方法。

```java
public static String getUUID() {

String serial = null;

String m_szDevIDShort = "35" +
        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

        Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

        Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

        Build.USER.length() % 10; //13 位

try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        serial = android.os.Build.getSerial();
    } else {
        serial = Build.SERIAL;
    }
    //API>=9 使用serial号
    return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
} catch (Exception exception) {
    //serial需要一个初始化
    serial = "serial"; // 随便一个初始化
}
    //使用硬件信息拼凑出来的15位号码
    return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
}


```

## 2. Q 行为变更

### 2.1 非 SDK 接口限制

![image](023E5B981BDC4BE2B25BBBE9B2647766)

非SDK接口查找

如果您不确定自己的应用是否使用了非 SDK 接口，则可以测试该应用进行确认。

当你调用了非SDK接口时，会有类似Accessing hidden XXX的日志:

```
Accessing hidden field Landroid/os/Message;->flags:I (light greylist, JNI)
```

但是一个大项目到底哪里使用了这些方法，靠review代码和看日志肯定是不现实的，谷歌官方也提供了官方检查器veridex用来检测一个apk中哪里使用了非SDK接口。veridex下载。

https://android.googlesource.com/platform/prebuilts/runtime/+/master/appcompat

下载解压后命令行cd到veridex目录下使用./appcompat.sh --dex-file=Q.apk即可自动扫描。Q.apk为包的绝对路径，如果包与veridex在相同目录下直接输入包文件名即可。


扫描结果分为两部分，一部分为被调用的非SDK接口的位置，另一部分为非SDK接口数量统计，

1. greylist: 灰名单，即当前版本仍能使用的非SDK接口，但在下一版本中可能变成被限制的非SDK接口
1. blacklist：黑名单，使用了就会报错。也是我们项目中必须解决的非SDK接口
1. greylist-max-o：在targetSDK<=O中能使用，但是在targetSDK>=P中被限制的非SDK接口
1. greylist-max-p：在targetSDK<=P中能使用，但是在targetSDK>=Q中被限制的非SDK接口

除了greylist我们可以暂时不解决以外，其余三种类型的非SDK接口需要我们进行适配。

### 2.2 非SDK接口适配

如果您的应用依赖于非 SDK 接口，则应该开始计划迁移到 SDK 替代方案。如果您无法为应用中的某项功能找到使用非 SDK 接口的替代方案，则应该请求新的公共 API。


官方要求targetSDK>=P的应用不使用这些方法，并寻找其他的公共API去替代这些非SDK接口，如果找不到，则可以向谷歌申请，请求一个新的公共API https://developer.android.com/distribute/best-practices/develop/restrictions-non-sdk-interfaces#feature-request (一般不需要)。


就我个人扫描并定位的结果来看，项目中使用非SDK接口大概率有以下两种情况：

在自定义View的过程中为了方便，使用反射修改某个参数。

三方SDK中使用了非SDK接口(这种情况比较多)。


第一种是好解决的，毕竟是我们自己写的代码。

第二种就头疼了，只能更新到最新的三方SDK版本，或者提工单、换库(也是整个适配过程中工作量最庞大的部分)。

## 最后

无论targetSDK是否为Q,必须对应用存储权限进行适配.不适配的后果

![image](DFF06C170E9D4AC3B4821306DCF649D7)