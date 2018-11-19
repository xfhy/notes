> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5be4e4a2f265da614e2b9562?utm_source=gold_browser_extension

就在前几天，跟一同事车回家，他用的是iOS版高德，每次发车前，重力长按高德icon，弹出shortcuts，很方便就进入回家的导航，也就是iOS 3D Touch功能。如下面这张图，截图来自647 iPhone X 。

![](https://user-gold-cdn.xitu.io/2018/11/9/166f61d167871745?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

今天得空研究了一下，Android 在Android 7.1(API 25) 添加了App快捷方式的新功能，由ShortcutManager类来管理，这样开发者可以随意定义快速进入到指定的Activity或打开指定网页。目前有很多App已经有了这个特性，接了个图如下：

![](https://user-gold-cdn.xitu.io/2018/11/9/166f61d1677eba16?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

下面我们就详细探讨一下这个特性。

### 实现Shortcuts的两种形式

#### 静态Shortcuts

所谓的静态就是在工程中配置，利用xml写死。在APK中包含一个资源文件来描述Shortcut，目录res/xml/shortcuts.xml。这种方法做不到热更新，需要从新发布App才可。

```
<shortcuts xmlns:android="http://schemas.android.com/apk/res/android">
    <shortcut
        android:shortcutId="static shortcut"
        android:enabled="true"
        android:icon="@mipmap/ic_launcher"
        android:shortcutShortLabel="@string/shortcut_short_name"
        android:shortcutLongLabel="@string/shortcut_long_name"
        android:shortcutDisabledMessage="@string/shortcut_disable_msg">
        <intent
            android:action="android.intent.action.VIEW"
            android:targetPackage="d.shortcuts"
            android:targetClass="d.shortcuts.MainActivity" />
        <categories android:/>
    </shortcut>
</shortcuts>
复制代码
```

```
<application ... 
             ...>
    <activity android:>
        <intent-filter>
            <action android: />
            <category android: />
        </intent-filter>
        <meta-data
            android:
            android:resource="@xml/shortcuts"/>
    </activity>
</application>
复制代码
```

这种方式适合百年不变的场景，不然真是不够灵活。

#### 动态Shortcuts

动态Shortcuts在运行时，通过ShortcutManager API来进行注册。用这种方式可以在运行时，动态的发布、更新和删除shortcut。官方给出了几个场景可以作为shortcut的例子，比如：

*   在地图类app中，指导用户到特定的位置；
*   在社交类app中，发送消息给一个朋友；
*   在媒体类app中，播放视频的下一片段；
*   在游戏类app中，下载最后保存的要点；

#### 动态安装

给Activity页面构建shortcut

```
private void setupShortcutsForActivity() {
    mShortcutManager = getSystemService(ShortcutManager.class);
    List<ShortcutInfo> infos = new ArrayList<>();
    for (int i = 0; i < mShortcutManager.getMaxShortcutCountPerActivity(); i++) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("info", "this is info!");
        ShortcutInfo info = new ShortcutInfo.Builder(this, "ID:" + i)
                .setShortLabel("short label")
                .setLongLabel("long label")
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(intent)
                .build();
        infos.add(info);
    }
    mShortcutManager.setDynamicShortcuts(infos);
}
复制代码
```

使用URL构建shortcut，打开默认浏览器，如下

```
private ShortcutInfo createShortcutForUrl(String urlAsString) {
      final ShortcutInfo.Builder b = new ShortcutInfo.Builder(mContext, urlAsString);
      final Uri uri = Uri.parse(urlAsString);
      b.setIntent(new Intent(Intent.ACTION_VIEW, uri));
      setSiteInformation(b, uri)
      setExtras(b);
      return b.build();
}

private ShortcutInfo.Builder setSiteInformation(ShortcutInfo.Builder b, Uri uri) {
        b.setShortLabel(uri.getHost());
        b.setLongLabel(uri.toString());
        Bitmap bmp = fetchFavicon(uri);
        if (bmp != null) {
            b.setIcon(Icon.createWithBitmap(bmp));
        } else {
            b.setIcon(Icon.createWithResource(mContext, R.drawable.link));
        }
        return b;
 }

private ShortcutInfo.Builder setExtras(ShortcutInfo.Builder b) {
        final PersistableBundle extras = new PersistableBundle();
        extras.putLong(EXTRA_LAST_REFRESH, System.currentTimeMillis());
        b.setExtras(extras);
        return b;
}

//注意要异步Task执行
private Bitmap fetchFavicon(Uri uri) {
        final Uri iconUri = uri.buildUpon().path("favicon.ico").build();
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(iconUri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            return null;
        }
    }
复制代码
```

![](https://user-gold-cdn.xitu.io/2018/11/9/166f61d167655d7f?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

动态删除

```
public void removeShortcut(ShortcutInfo shortcut) { 
    mShortcutManager.removeDynamicShortcuts(Arrays.asList(shortcut.getId()));
}
复制代码
```

动态停用

```
public void disableShortcut(ShortcutInfo shortcut) {
    mShortcutManager.disableShortcuts(Arrays.asList(shortcut.getId()));
}
复制代码
```

动态开启

```
public void enableShortcut(ShortcutInfo shortcut) {
    mShortcutManager.enableShortcuts(Arrays.asList(shortcut.getId()));
}
复制代码
```

### Pinning Shortcut

在动态里面还有一个Pinning Shortcuts概念，相当于app的另外一种快捷方式，只允许用户添加与删除它。

用isRequestPinShortcutSupported() 判断当前设备是否支持PinShort 。在Android 8.0 (API level 26) 以及以上的版本上支持创建pinned shortcuts。

```
ShortcutManager mShortcutManager = context.getSystemService(ShortcutManager.class);
if (mShortcutManager.isRequestPinShortcutSupported()) {
    // Assumes there's already a shortcut with the ID "my-shortcut".
    // The shortcut must be enabled.
    ShortcutInfo pinShortcutInfo =
            new ShortcutInfo.Builder(context, "my-shortcut").build();
    // Create the PendingIntent object only if your app needs to be notified
    // that the user allowed the shortcut to be pinned. Note that, if the
    // pinning operation fails, your app isn't notified. We assume here that the
    // app has implemented a method called createShortcutResultIntent() that
    // returns a broadcast intent.
    Intent pinnedShortcutCallbackIntent =
            mShortcutManager.createShortcutResultIntent(pinShortcutInfo);

    // Configure the intent so that your app's broadcast receiver gets
    // the callback successfully.For details, see PendingIntent.getBroadcast().
    PendingIntent successCallback = PendingIntent.getBroadcast(context, /* request code */ 0,
            pinnedShortcutCallbackIntent, /* flags */ 0);

    mShortcutManager.requestPinShortcut(pinShortcutInfo,
            successCallback.getIntentSender());
}
复制代码
```

### shortcuts 最佳实践

1、不管是静态形式还是动态形式，每个应用最多可以注册4个Shortcuts。

2、 "short description" 限制在 10 个字符内

3、"long description" 限制在 25 个字符内

4、改变 dynamic and pinned shortcuts时，调用方法`updateShortcuts()`

5、重复创建shortcut时，

*   动态的 shortcuts使用方法: `addDynamicShortcuts()` 或 `setDynamicShortcuts()`.
*   Pinned shortcuts使用方法: `requestPinShortcut()`.

6、在每次启动与需要重新发布动态shortcut时，推荐检查方法`getDynamicShortcuts()` 返回的数量。

```
public class MainActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        if (shortcutManager.getDynamicShortcuts().size() == 0) {
            // Application restored. Need to re-publish dynamic shortcuts.
            if (shortcutManager.getPinnedShortcuts().size() > 0) {
                // Pinned shortcuts have been restored. Use
                // updateShortcuts() to make sure they contain
                // up-to-date information.
            }
        }
    }
    // ...
}
复制代码
```

本文demo地址:
[github.com/donald99/sh…](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fdonald99%2Fshortcuts.git)

推荐Google demo
[github.com/googlesampl…](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fgooglesamples%2Fandroid-AppShortcuts%2F)

## 关注微信公众号，最新技术干货实时推送

![](https://user-gold-cdn.xitu.io/2018/11/9/166f620d3a7c714d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)