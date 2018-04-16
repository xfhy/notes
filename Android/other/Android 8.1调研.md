# Android 8.1调研

## 主要新功能点
- 1、神经网络API（NNAPI），提供机器学习的硬件加速。
- 2、通知：通知消息现在每秒仅能发出一次提示音。
- 3、改善2G以下内存设备的表现
- 4、自动填充功能针对APP进行优化，提供验证器判断是否响应
- 5、文本编辑更新
- 6、程序性的安全浏览行为：允许APP对浏览行为进行安全检测、规避威胁
- 7、视频缩略图生成更精准
- 8、共享内存API：Android 8.1(API级别27)引入了一个新的SharedMemory类。这个类允许您创建、映射和管理匿名共享内存,被多个进程或应用程序使用。
- 9、壁纸色彩管理API

## 神经网络API

Neural Networks API为设备上的机器学习框架（如TensorFlow Lite -Google的移动平台ML库以及Caffe2等）提供了加速的计算和推理。访问TensorFlow Lite 开源回购下载和文档。TensorFlow Lite可与Neural Networks API 协同工作，在移动设备上高效运行 MobileNets， Inception v3和 Smart Reply等模型。

## 自动填充框架更新
Android 8.1（API等级27）对自动填充框架进行了一些改进，您可以将其纳入应用程序。

该BaseAdapter 级现在包括setAutofillOptions() 方法，它允许您提供值的字符串表示在一个适配器。这对于 在其适配器中动态生成值的微调控件非常有用。例如，您可以使用该setAutofillOptions()方法提供用户可以选择作为信用卡过期日期一部分的年份列表的字符串表示形式。自动填充服务可以使用字符串表示来适当填写需要数据的视图。

此外，AutofillManager 该类还包含notifyViewVisibilityChanged(View, int, boolean)可调用的方法，以通知框架有关虚拟结构中视图可见性更改的内容。非虚拟结构的方法也有重载。但是，非虚拟结构通常不要求您明确通知框架，因为该方法已由View 该类调用 。

Android 8.1还为Autofill服务提供了更多的能力，通过添加对CustomDescription and Validator 内部的支持来定制保存UI可供性SaveInfo。

自定义描述对于帮助自动填充服务澄清正在保存的内容非常有用; 例如，当屏幕包含信用卡时，它可以显示信用卡银行的标识，信用卡号码的最后四位数字以及其到期号码。要了解更多信息，请参阅 CustomDescription 课程。

Validator 对象用于避免在验证条件不满足时显示自动填充保存UI。要了解更多信息，请参阅 Validator类及其子类 LuhnChecksumValidator和RegexValidator。

## 通知
Android 8.1包含对通知的以下更改：

应用程序现在只能每秒发出一次通知警报声。超过此速率的警报声音不会排队并丢失。此更改不会影响通知行为的其他方面，并且通知消息仍按预期发布。
NotificationListenerService和ConditionProviderService在调用ActivityManager.isLowRamDevice()时不支持返回true

## EditText更新
从API级别27开始，该EditText.getText()方法返回一个Editable; 以前它返回一个CharSequence。作为Editable实现 ，此更改是向后兼容的CharSequence。

该Editable界面提供了宝贵的附加功能。例如，因为Editable还实现了Spannable接口，所以可以将标记应用于实例中的内容EditText。

## 程序化安全浏览操作

通过使用安全浏览API 的实施，您的应用可以检测何时尝试导航到Google已被归类为已知威胁的URL 的实例。默认情况下，会 显示一个插页式广告，警告用户已知的威胁。该屏幕允许用户选择加载URL，或返回到安全的上一页。

在Android 8.1中，您可以通过编程定义应用程序对已知威胁的响应情况：

- 您可以控制您的应用是否将已知威胁报告给安全浏览。
- 您可以让自己的应用程序自动执行特定操作（例如回到安全状态），每次遇到安全浏览会将其分类为已知威胁的网址时。

注意：为了最大限度地防范已知威胁，请等到您在调用WebView对象的loadUrl()方法之前初始化安全浏览 。

以下代码片段显示了如何指示您的应用程序的实例 WebView在遇到已知威胁后始终返回安全状态：

`AndroidManifest.xml`
```xml
<manifest>
    <application>
        ...
        <meta-data android:name="android.webkit.WebView.EnableSafeBrowsing"
                   android:value="true" />
    </application>
</manifest>
```

MyWebActivity.java
```java
private WebView mSuperSafeWebView;
private boolean mSafeBrowsingIsInitialized;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mSuperSafeWebView = new WebView(this);
    mSuperSafeWebView.setWebViewClient(new MyWebViewClient());
    mSafeBrowsingIsInitialized = false;

    mSuperSafeWebView.startSafeBrowsing(this, new ValueCallback<Boolean>() {
        @Override
        public void onReceiveValue(Boolean success) {
            mSafeBrowsingIsInitialized = true;
            if (!success) {
                Log.e("MY_APP_TAG", "Unable to initialize Safe Browsing!");
            }
        }
    });
}
```

MyWebViewClient.java
```java
public class MyWebViewClient extends WebViewClient {
    // Automatically go "back to safety" when attempting to load a website that
    // Safe Browsing has identified as a known threat. An instance of WebView
    // calls this method only after Safe Browsing is initialized, so there's no
    // conditional logic needed here.
    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request,
            int threatType, SafeBrowsingResponse callback) {
        // The "true" argument indicates that your app reports incidents like
        // this one to Safe Browsing.
        callback.backToSafety(true);
        Toast.makeText(view.getContext(), "Unsafe web page blocked.",
                Toast.LENGTH_LONG).show();
    }
}
```

## 视频缩略图提取器
`MediaMetadataRetriever`类有一个新的方法，`getScaledFrameAtTime()`即找到邻近给定的时间位置的帧，并返回具有相同的纵横比作为源帧的位图，而且缩放以符合给定宽度和高度的矩形。这对于从视频生成缩略图图像很有用。

我们建议使用此方法而不是getFrameAtTime()浪费内存，因为它会返回与源视频具有相同分辨率的位图。例如，来自4K视频的帧将是一张16MB的位图，比您需要的缩略图大得多。

## 共享内存API

Android 8.1（API等级27）引入了一个新的`SharedMemory API`。这个类允许你创建，映射和管理一个匿名 `SharedMemory` 实例。您可以将`SharedMemory` 对象的内存保护设置 为读取和/或写入，并且由于 `SharedMemory` 对象是Parcelable，因此可以通过AIDL轻松地将其传递给另一个进程。

该`SharedMemory` API与`ASharedMemoryNDK`中的设施互操作 。 ASharedMemory可以访问文件描述符，然后可以将其映射为读取和写入。这是在应用程序之间或单个应用程序内的多个进程之间共享大量数据的好方法。

## WallpaperColors API
Android 8.1（API等级27）允许您的动态壁纸为系统UI提供颜色信息。您可以通过bitmap,drawable,颜色选择器创建WallpaperColors。您也可以检索这种颜色信息。

要创建一个WallpaperColors 对象，请执行以下任一操作：

- 要WallpaperColors 使用三种颜色创建对象，请WallpaperColors 通过传递主要颜色，辅助颜色和第三级颜色来创建类的实例。主要颜色不能为空。
- 要从WallpaperColors 位图创建对象，请fromBitmap() 通过传递位图源作为参数来调用该方法。
- 要从WallpaperColors drawable 创建对象，请fromDrawable() 通过传递可绘制源作为参数来调用该方法。

要从墙纸检索主要，次要或第三个颜色细节，请调用以下方法：

- getPrimaryColor() 返回壁纸最具视觉效果的颜色。
- getSecondaryColor() 返回壁纸的第二个最显着的颜色。
- getTertiaryColor() 方法返回壁纸的第三个最显着的颜色。
要通知系统有关活动壁纸中的任何重大颜色变化，请调用该notifyColorsChanged() 方法。此方法onComputeColors()在您有机会提供新WallpaperColors 对象的情况下触发生命周期事件。

要为颜色更改添加侦听器，您可以调用该addOnColorsChangedListener()方法。您也可以调用该getWallpaperColors()方法来检索墙纸的主要颜色。

## 指纹更新

该FingerprintManager类先后引进了以下错误代码：

FINGERPRINT_ERROR_LOCKOUT_PERMANENT - 用户尝试使用指纹读取器解锁设备的次数过多。
FINGERPRINT_ERROR_VENDOR - 出现特定于供应商的指纹识别器错误

## 加密更新

使用Android 8.1进行了多项密码更改：

- 新算法已在Conscrypt中实施。Conscrypt实现优先用于现有的Bouncy Castle实现。新算法包括：
  * AlgorithmParameters:GCM
  * KeyGenerator:AES
  * KeyGenerator:DESEDE
  * KeyGenerator:HMACMD5
  * KeyGenerator:HMACSHA1
  * KeyGenerator:HMACSHA224
  * KeyGenerator:HMACSHA256
  * KeyGenerator:HMACSHA384
  * KeyGenerator:HMACSHA512
  * SecretKeyFactory:DESEDE
  * Signature:NONEWITHECDSA
- Cipher.getParameters().getParameterSpec(IvParameterSpec.class)不再适用于使用GCM的算法。相反，使用 getParameterSpec(GCMParameterSpec.class)。
- 许多与TLS相关的内部加密类都被重构了。由于开发人员有时会反射性地访问这些内容，所以垫片已经放置以支持以前的使用，但一些细节已经改变。例如，先前的套接字是类型的OpenSSLSocketImpl，但现在它们是类型的， ConscryptFileDescriptorSocket或者 ConscryptEngineSocket两者都扩展 OpenSSLSocketImpl。
- SSLSessionIllegalArgumentException传递null引用时抛出的方法 ，现在抛出NullPointerException。
- RSA KeyFactory不再允许从字节数组中生成大于编码密钥的密钥。调用 generatePrivate()并 generatePublic()提供一个 KeySpec关键结构不填充整个缓冲区的地方将导致一个InvalidKeySpecException。
- 当套接字读取被关闭的套接字中断时，Conscrypt用于从读取返回-1。现在阅读抛出 SocketException。
- 根CA证书集已更改，主要是删除大量废弃证书，但也删除了WoSign和StartCom的根证书。有关此决定的更多信息，请参阅Google安全博客文章， WoSign和StartCom证书中的最终信任删除。
