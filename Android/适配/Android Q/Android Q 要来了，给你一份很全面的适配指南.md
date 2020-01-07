> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/aiDMyAfAZvaYIHuIMLAlcg

![](https://mmbiz.qpic.cn/mmbiz_jpg/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wmftUlnyRWp2kMuiaTIYbHooq4iccG9iaKAthNeOpe7YOgdp84XnXQZhPQ/640?wx_fmt=jpeg)

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSwIgGwqImEF6tKibiclPDdFDicIpvrhAQrJtzFE7icuZ88csZ6dhYtmXiaeXTRiaEa8d4MrcphYWT6aEXMg/640?wx_fmt=png)

Android Q 越来越近了，最近 Google 又发布了 Android Q Beta 的第五个版本，眼瞅着这进度，在今年 Q3 季度，Android Q 就正式和用户见面了，在此之前，开发者必然又是面临的一波让人头疼的适配。  

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wMmHCiaAXj065fHLQX92HCG5SYrPsM4loGEibBW6qLGyBWRKfVBgwyibww/640?wx_fmt=png)

了解新特性，首推应该去看官方文档，官方已经给出了一份完整的新特性文档，在发布的这段时间，也一直在保持同步的更新。而作为开发者，我们更关心的是如何解决在我们现有的 App 上，保证 Android Q 的兼容性问题。

今天就给推荐给大家一份适配文档，以开发者的角度列一份适配清单，在 Android Q 还没来之前，先了解需要做什么，以及怎么做，到时候才不至于措手不及。

这份文档的出自 OPPO 开放平台，可能有人会觉得是 KPI 工程，但是你想想这些厂商每年耗巨资研发的旗舰机，用着最新的硬件，当然要搭配最新的系统，而用户在旗舰机上的体验，也是他们最关心的，所以每次 Android 发布新系统，这些厂商也在推进自己应用市场上 App 的适配工作。

你只需要想想他们做这件事的动机，就能知道这份文档肯定是花了心思的。文档我看过一遍，从场景出发来分析原因，并附上解决方案，很有参考意义。

文档比较长，大家可以先收藏，再跳跃阅读看自己关注的点。

* * *

一. 背景说明
-------

本文档是基于谷歌安卓 Q 的 beta4 版本的变更输出的兼容性整改指导，如果后续 beta 版本有新的变更和新的特性，我们也会刷新文档的相关章节内容，请开发者持续关注。

二. 存储空间限制
---------

### 2.1 背景

为了让用户更好地控制自己的文件，并限制文件混乱的情况，Android Q 修改了 APP 访问外部存储中文件的方法。外部存储的新特性被称为 Scoped Storage。

Android Q 仍然使用 READ_EXTERNAL_STORAGE 和 WRITE_EXTERNAL_STORAGE 作为面向用户的存储相关运行时权限，但现在即使获取了这些权限，访问外部存储也受到了限制。APP 需要这些运行时权限的情景发生了变化，且各种情况下外部存储对 APP 的可见性也发生了变化。

在 Scoped Storage 新特性中，外部存储空间被分为两部分：

**●** **公共目录**：Downloads、Documents、Pictures 、DCIM、Movies、Music、Ringtones 等

*   公共目录下的文件在 APP 卸载后，不会删除。
    
*   APP 可以通过 SAF(System Access Framework)、MediaStore 接口访问其中的文件。
    

**●** **App-specific 目录**

*   APP 卸载后，数据会清除。
    
*   APP 的私密目录，APP 访问自己的 App-specific 目录时无需任何权限。
    

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wBrXffMxUAiafWuvtuMFxbTaQMTaAGPK4scdib0eiceWl2NpUmtNekUAYg/640?wx_fmt=png)

Android Q 规定了 APP 有两种外部存储空间视图模式：Legacy View、Filtered View。  

**●** **Filtered View**

*   App 可以直接访问 App-specific 目录，但不能直接访问 App-specific 外的文件。访问公共目录或其他 APP 的 App-specific 目录，只能通过 MediaStore、SAF、或者其他 APP 提供的 ContentProvider、FileProvider 等访问。
    

**●** **Legacy View**

*   兼容模式。与 Android Q 以前一样，申请权限后 App 可访问外部存储，拥有完整的访问权限。
    

在 Android Q 上，target SDK 大于或等于 29 的 APP 默认被赋予 Filtered View，反之则默认被赋予 Legacy View。APP 可以在 `AndroidManifest.xml` 中设置新属性 `requestLegacyExternalStorage` 来修改外部存储空间视图模式，true 为 Legacy View，false 为 Filtered View。可以使用 `Environment.isExternalStorageLegacy()` 这个 API 来检查 APP 的运行模式。APP 开启 Filtered View 后，Scoped Storage 新特性对 APP 生效。

Android Q 除了划分外部存储和定义 Filtered View，还在查询、读写文件的一些细节上做了改进或限制，例如图片文件中的地理位置信息将不再默认提供、查询 MediaProvider 获得的 DATA 字段不再可靠、新增了文件的 Pending 状态等等。这些细节的具体内容请参考适配方案章节。

### 2.2  兼容性影响

Scoped Storage 对于 APP 访问外部存储方式、APP 数据存放以及 APP 间数据共享，都产生很大影响。请开发者注意以下的兼容性影响事项。

**2.2.1 无法新建文件**

问题原因：直接使用自身 App-specific 目录以外的路径新建文件。

问题分析：在 Android Q 上，APP 只允许在自身 App-specific 目录以内通过路径生成的文件。

解决方案：APP 自身 App-specific 目录下新建文件的方法与文件路径，请参见 2.3.1；如果要在公共目录下新建文件，使用 MediaStore 接口，请参见 2.3.2；如果要在任意目录下新建文件，需要使用 SAF，请参见 2.3.3。

**2.2.2 无法访问存储设备上的文件**

问题原因 1：直接使用路径访问公共目录文件。

问题分析 1：在 Android Q 上，APP 默认只能访问外部存储设备上的 App-specific 目录。

解决方法 1：参见 2.3.2 和 2.3.3，使用 MediaStore 接口访问公共目录中的多媒体文件，或者使用 SAF 访问公共目录中的任意文件。注意：从 MediaStore 接口中查询到的 DATA 字段将在 Android Q 开始废弃，不应该利用它来访问文件或者判断文件是否存在；从 MediaStore 接口或者 SAF 获取到文件 Uri 后，请利用 Uri 打开 FD 或者输入输出流，而不要转换成文件路径去访问。

问题原因 2：使用 MediaStore 接口访问非多媒体文件。

问题分析 2：在 Android Q 上，使用 MediaStore 接口只能访问公共目录中的多媒体文件。

解决方法 2：使用 SAF 向用户申请文件或目录的读写权限，请参见 2.3.3。

**2.2.3 无法正确分享文件**

问题原因：APP 将 App-specific 目录中的私有文件分享给其他 APP 时，使用了 `file://` 类型的 Uri。

问题分析：在 Android Q 上，由于 App-specific 目录中的文件是私有受保护的，其他 APP 无法通过文件路径访问。

解决方案：参见 2.3.4，使用 FileProvider，将 `content://` 类型的 Uri 分享给其他 APP。

**2.2.4 无法修改存储设备上的文件**

问题原因 1：直接使用路径访问公共目录文件。

问题分析 1：同 2.2.2。

解决方案 1：同 2.2.2，请使用正确的公共目录文件访问方式。

问题原因 2：使用 MediaStore 接口获取公共目录多媒体文件的 Uri 后，直接使用该 Uri 打开 OutputStream 或文件描述符。

问题分析 2：在 Android Q 上，修改公共目录文件，需要用户授权。

解决方案 2：从 MediaStore 接口获取公共目录多媒体文件 Uri 后，打开 OutputStream 或 FD 时，注意 catch RecoverableSecurityException，然后向用户申请该多媒体文件的删改权限，请参见 2.3.2.6；使用 SAF 获取到文件或目录的 Uri 时，用户已经授权读写，可以直接使用，但要注意 Uri 权限的时效，请参见 2.3.3.6。

**2.2.5 应用卸载后文件意外删除**

问题原因：将想要保留的文件保存在外部存储的 App-specific 目录下。

问题分析：在 Android Q 上，卸载 APP 默认删除 App-specific 目录下的数据。

解决方案：APP 应该将想要保留的文件通过 MediaStore 接口保存到公共目录下，请参见 2.3.2。默认情况下，MediaStore 接口会将非媒体类文件保存到 Downloads 目录下，推荐 APP 指定一级目录为 Documents。如果 APP 想要在卸载时保留 App-specific 目录下的数据，要在 AndroidManifest.xml 中声明 android:hasFragileUserData="true"，这样在 APP 卸载时就会有弹出框提示用户是否保留应用数据。

**2.2.6 无法访问图片文件中的地理位置数据**

问题原因：直接从图片文件输入流中解析地理位置数据。

问题分析：由于图片的地理位置信息涉及用户隐私，Android Q 上默认不向 APP 提供该数据。

解决方案：申请 ACCESS_MEDIA_LOCATION 权限，并使用 MediaStore.setRequireOriginal() 接口更新文件 Uri，请参见 2.3.5.1 。

**2.2.7 Fota 升级问题**

问题原因：Fota 升级后，APP 被卸载，重新安装后无法访问到 APP 数据。

问题分析：Scoped Storage 新特性只对 Android Q 上新安装的 APP 生效。设备从 Android Q 之前的版本升级到 Android Q，已安装的 APP 获得 Legacy View 视图。这些 APP 如果直接通过路径的方式将文件保存到了外部存储上，例如外部存储的根目录，那么 APP 被卸载后重新安装，新的 APP 获得 Filtered View 视图，无法直接通过路径访问到旧数据，导致数据丢失。

解决方案：APP 应该修改保存文件的方式，不再使用路径的方式直接保存，而是采用 MediaStore 接口将文件保存到对应的公共目录下。在 Fota 升级前，可以将 APP 的用户历史数据通过 MediaStore 接口迁移到公共目录下。此外，APP 应当改变访问 App-specific 目录以外的文件的方式，请使用 MediaStore 接口或者 SAF。

### 2.3 适配指导

Android Q Scoped Storage 新特性谷歌官方适配文档：

https://developer.android.google.cn/preview/privacy/scoped-storage

OPPO 适配指导如下，分为：访问 APP 自身 App-specific 目录文件、使用 MediaStore 访问公共目录、使用 SAF 访问指定文件和目录、分享 App-specific 目录下文件和其他细节适配。

**2.3.1 访问 APP 自身 App-specific 目录文件**

无需任何权限，APP 即可直接使用文件路径来读写自身 App-specific 目录下的文件。获取 App-specific 目录路径的接口如下表所示。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wArS090VXMrgqWibF1JjjlrAC6Te7OYYWXVLoZHL9GE63Kn7yrbYicQxw/640?wx_fmt=png)

如下，以新建并写入文件为例。

```
// set "Documents" as subDir
final File[] dirs = getExternalFilesDirs("Documents");
File primaryDir = null;
if (dirs != null && dirs.length > 0) {
    primaryDir = dirs[0];
}
if (primaryDir == null) {
    return;
}
File newFile = new File(primaryDir.getAbsolutePath(), "MyTestDocument");
OutputStream fileOS = null;
try {
    fileOS = new FileOutputStream(newFile);
    if (fileOS != null) {
        fileOS.write("file is created".getBytes(StandardCharsets.UTF_8));
        fileOS.flush();
    }
} catch (IOException e) {
    LogUtil.log("create file fail");
} finally {
    try {
        if (fileOS != null) {
            fileOS.close();
        }
    } catch (IOException e1) {
        LogUtil.log("close stream fail");
    }
}


```

**2.3.2 使用 MediaStore 访问公共目录**

APP 无法直接访问公共目录下的文件。MediaStore 为 APP 提供了访问公共目录下媒体文件的接口。APP 在有适当权限时，可以通过 MediaStore 查询到公共目录文件的 Uri，然后通过 Uri 读写文件。

MediaStore 相关的 Google 官方文档：

https://developer.android.google.cn/reference/android/provider/MediaStore

**2.3.2.1 MediaStore 的 Uri 和路径对照表**

MediaStore 提供了下列几种类型的访问 Uri，通过查询对应 Uri 数据（在 MediaProvider 中），达到访问的目的。

下列每种类型又分为三种 Uri：Internal、External、可移动存储。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wgL8cCibOicte4Y8frkLVkaBjV7TLlPdibm2vIeZqNQj6JmeXcjBHricdWQ/640?wx_fmt=png)

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wNv9QAPicUkOcw0Y8tia52xmL8Y5G3GAfyZyjm1PORGcDQcMxXGJsQx6w/640?wx_fmt=png)

在 Android Q 上，所有的外部存储设备，包括内置卡、SD 卡等，都会被命名，即设备的 Volume Name。MediaStore 可以通过 Volume Name 获取对应存储设备的 Uri。

```
for (String volumeName : MediaStore.getExternalVolumeNames(this)){
    MediaStore.Images.Media.getContentUri(volumeName);
}


```

MediaProvider 对于 APP 新建到公共目录的文件，通过 ContentResolver.insert 方法中的 Uri 来确定具体存放目录。其中下表中

content://media//>

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5woFTEuvIT7Kvwialat4QpkX3UBuicbdKddKJWwuJpUyf8hicich1ib2FHLSA/640?wx_fmt=png)![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wUmVex5EXwZmgibMU8phJjCseX5a2zkxbaGYb2xZ6tvdjXaIM7fKf7Lw/640?wx_fmt=png)

**2.3.2.2 APP 通过 MediaStore 访问文件所需要的权限**

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wXtMBbx11Tia5QS9zccpSEFrZeT9IyAVSjVojtPW5oYKw7ExlhmV5vLA/640?wx_fmt=png)

通过 MediaStore 提供的 Uri，使用 ContentResolver 的 insert 接口，将文件保存到公共目录下。不同的 Uri，可以保存到不同的公共目录中，详见 2.3.2.1。

```
ContentValues values = new ContentValues();
values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
values.put(MediaStore.Images.Media.DISPLAY_NAME, "Image.png");
values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
values.put(MediaStore.Images.Media.TITLE, "Image.png");
values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/test");

Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
ContentResolver resolver = context.getContentResolver();

Uri insertUri = resolver.insert(external, values);
LogUtil.log("insertUri: " + insertUri);

OutputStream os = null;
try {
    if (insertUri != null) {
        os = resolver.openOutputStream(insertUri);
    }
    if (os != null) {
        final Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
        // write what you want
    }
} catch (IOException e) {
    LogUtil.log("fail: " + e.getCause());
} finally {
    try {
        if (os != null) {
            os.close();
        }
    } catch (IOException e) {
        LogUtil.log("fail in close: " + e.getCause());
    }
}


```

**2.3.2.4 使用 MediaStore 查询文件**

用 MediaStore 提供的 Uri 指定设备，selection 参数指定过滤条件，通过 ContentResolver.query 接口查询文件 Uri。

```
Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

ContentResolver resolver = context.getContentResolver();

String selection = MediaStore.Images.Media.TITLE + "=?";
String[] args = new String[] {"Image"};
String[] projection = new String[] {MediaStore.Images.Media._ID};
Cursor cursor = resolver.query(external, projection, selection, args, null);
Uri imageUri = null;

if (cursor != null && cursor.moveToFirst()) {
    imageUri = ContentUris.withAppendedId(external, cursor.getLong(0));
    cursor.close();
}


```

**2.3.2.5 使用 MediaStore 读取文件**

通过以上查询方式得到 Uri 之后，通过以下方式读取文件：

1）通过 ContentResolver openFileDescriptor 接口，选择对应的打开方式。例如”r” 表示读，”w” 表示写，返回 ParcelFileDescriptor 类型的文件描述符。

```
ParcelFileDescriptor pfd = null;
if (imageUri != null) {
    try {
        pfd = context.getContentResolver().openFileDescriptor(imageUri, "r");
        if (pfd != null) {
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
            // show the bitmap, or do something else.
        }
    } catch (IOException e) {
        LogUtil.log("fail: " + e.getCause());
    } finally {
        try {
            if (pfd != null) {
                pfd.close();
            }
        } catch (IOException e) {
            LogUtil.log("fail in close: " + e.getCause());
        }
    }
}


```

2）访问 Thumbnail，使用 ContentResolver.loadThumbnail 接口。通过传入 size 参数，MediaProvider 返回指定大小的 Thumbnail。

3）Native 代码访问文件

如果 Native 代码需要访问文件，可以参考下面方式：

*   通过 openFileDescriptor 返回 ParcelFileDescriptor
    
*   通过 ParcelFileDescriptor.detachFd() 读取 FD
    
*   将 FD 传递给 Native 层代码
    
*   通过 close 接口关闭 FD
    

```
String fileOpenMode = "r";
ParcelFileDescriptor parcelFd = resolver.openFileDescriptor(uri, fileOpenMode);
if (parcelFd != null) {
    int fd = parcelFd.detachFd();
    // Pass the integer value "fd" into your native code. Remember to call
    // close(2) on the file descriptor when you're done using it.
}


```

**2.3.2.6 使用 MediaStore 修改文件**

根据查询得到的文件 Uri，使用 MediaStore 修改其他 APP 新建的多媒体文件，需要 catch `RecoverableSecurityException` ，由 MediaProvider 弹出弹框给用户选择是否允许 APP 修改或删除图片 / 视频 / 音频文件。用户操作的结果，将通过 onActivityResult 回调返回到 APP。如果用户允许，APP 将获得该 Uri 的修改权限，直到设备下一次重启。

根据文件 Uri，通过下列接口，获取需要修改文件的 FD 或者 OutputStream：

1）getContentResolver().openOutputStream(contentUri)

获取对应文件的 OutputStream。

2）getContentResolver().openFile 或者 getContentResolver().openFileDescriptor

通过 openFile 或者 openFileDescriptor 打开文件，需要选择 Mode 为”w”，表示写权限。这些接口返回一个 ParcelFileDescriptor。

```
OutputStream os = null;
try {
    if (imageUri != null) {
        os = resolver.openOutputStream(imageUri);
    }
} catch (IOException e) {
    LogUtil.log("open image fail");
} catch (RecoverableSecurityException e1) {
    LogUtil.log("get RecoverableSecurityException");
    try {
        ((Activity) context).startIntentSenderForResult(
                e1.getUserAction().getActionIntent().getIntentSender(),
                100, null, 0, 0, 0);
    } catch (IntentSender.SendIntentException e2) {
        LogUtil.log("startIntentSender fail");
    }
}


```

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wjeHtU2TYbQ35GtA1WOjuuicdTtT9njcczoOTcDNOiczhQre5I9HicRiaog/640?wx_fmt=png)

**2.3.2.7 使用 MediaStore 删除文件**

删除其他 APP 新建的媒体文件，与修改类似，需要用户授权。删除文件使用 ContentResolver.delete 接口。

```
getContentResolver().delete(imageUri, null, null);


```

**2.3.3 使用 SAF 访问指定文件和目录**

SAF，即 Storage Access Framework。根据当前系统中存在的 DocumentsProvider，让用户选择特定的文件或文件夹，使调用 SAF 的 APP 获取它们的读写权限。APP 通过 SAF 获得文件或目录的读写权限，无需申请任何存储相关的运行时权限。

SAF 相关的 Google 官方文档：

https://developer.android.com/guide/topics/providers/document-provider

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wM5cMBUQdrV2zQfXibqShktTUJJMOzNXRS2ws253PWric9vN2LtVUB2PQ/640?wx_fmt=png)

使用 SAF 获取文件或目录权限的过程：

APP 通过特定 Intent 调起 DocumentUI -> 用户在 DocumentUI 界面上选择要授权的文件或目录  -> APP 在回调中解析文件或目录的 Uri，最后根据这一 Uri 可进行读写删操作。

**2.3.3.1 使用 SAF 选择单个文件**

使用 Intent.ACTION_OPEN_DOCUMENT 调起 DocumentUI 的文件选择页面，用户可以选择一个文件，将它的读写权限授予 APP。

```
Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
// you can set type to filter files to show
intent.setType("*/*");
startActivityForResult(intent, REQUEST_CODE_FOR_SINGLE_FILE);


```

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5w7Gib7obeE1b4ict4rw5tvMHeWUjNKfJfp6wN57eCgcrmNSznL2PiaEvsQ/640?wx_fmt=png)

**2.3.3.2 使用 SAF 修改文件**

通过 2.3.3.1 的方式，用户选择文件授权给 APP 后，在 APP 的 onActivityResult 回调中收到返回结果，解析出对应文件的 Uri。然后使用该 Uri，用户可以获取可写的 ParcelFileDescriptor 或者打开 OutputStream 进行修改。

```
if (requestCode == REQUEST_CODE_FOR_SINGLE_FILE && resultCode == Activity.RESULT_OK) {
    Uri fileUri = null;
    if (data != null) {
        fileUri = data.getData();
    }
    if (fileUri != null) {
        OutputStream os = null;
        try {
            os = getContentResolver().openOutputStream(fileUri);
            os.write("something".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LogUtil.log("modify document fail");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    LogUtil.log("close fail");
                }
            }
        }
    }
}


```

**2.3.3.3 使用 SAF 删除文件**

类似修改文件，在回调中解析出文件 Uri，然后使用 DocumentsContract.deleteDocument 接口进行删除操作。

```
if (requestCode == REQUEST_CODE_FOR_SINGLE_FILE && resultCode == Activity.RESULT_OK) {
    Uri fileUri = null;
    if (data != null) {
        fileUri = data.getData();
    }
    if (fileUri != null) {
        try {
            DocumentsContract.deleteDocument(getContentResolver(), fileUri);
        } catch (FileNotFoundException e) {
            LogUtil.log("delete document fail");
        }
    }
}


```

**2.3.3.4 使用 SAF 新建文件**

APP 通过 Intent.ACTION_CREATE_DOCUMENT 调起 DocumentUI 界面，由用户决定文件命名，以及存放位置。

```
Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
intent.addCategory(Intent.CATEGORY_OPENABLE);
// you can set file mimetype
intent.setType("*/*");
// default file name
intent.putExtra(Intent.EXTRA_TITLE, "myFileName");
startActivityForResult(intent, REQUEST_CODE_FOR_CREATE_FILE);


```

  

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5w4udFcF9gdQTF2L4BrPMXxVVM5Tia2yCrOwsS3cSeDVnianGFZHAic61Hg/640?wx_fmt=png)

在用户确定后，操作结果将返回到 APP 的 onActivityResult 回调中，APP 解析出文件 Uri，之后就可以利用这一 Uri 对文件进行读写删操作。

```
if (requestCode == REQUEST_CODE_FOR_CREATE_FILE && resultCode == Activity.RESULT_OK) {
    Uri fileUri = null;
    if (data != null) {
        fileUri = data.getData();
    }
    // read/update/delete by the uri got here.
    LogUtil.log("uri: " + fileUri);
}


```

**2.3.3.5 使用 SAF 选择目录**

通过 Intent.ACTION_OPEN_DOCUMENT_TREE 调起 DocumentUI 界面，用户可以选择任意文件夹，将它及其子文件夹的读写权限授予 APP。

```
Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
startActivityForResult(intent, REQUEST_CODE_FOR_DIR);


```

在右上角的菜单中选择 show internal storage，可以在左侧菜单中选择内置存储设备，接着用户可以选择内置存储设备中的任意文件夹。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wXhAO2KEC8kaLsTwlqlqX9aja7SNtokeMUkuWp1AticKKnicUujxKDprg/640?wx_fmt=png)

在用户确定后，APP 的 onActivityResult 回调收到操作结果，解析出被选文件夹的 uriTree。根据这一 uriTree ，进一步可以生成表示被选文件夹的 DocumentFile，利用 DocumentFile 提供的 API 可以对目录下的文件进行各种操作。

```
if (requestCode == REQUEST_CODE_FOR_DIR && resultCode == Activity.RESULT_OK) {
    Uri uriTree = null;
    if (data != null) {
        uriTree = data.getData();
    }
    if (uriTree != null) {
        // create DocumentFile which represents the selected directory
        DocumentFile root = DocumentFile.fromTreeUri(this, uriTree);
        // list all sub dirs of root
        DocumentFile[] files = root.listFiles();
        // do anything you want with APIs provided by DocumentFile
        // ...
    }
}


```

**2.3.3.6 永久保存获取的目录权限**

在 2.3.3.5 中，通过 SAF 获取了用户指定目录的读写权限，直至设备下一次重启。APP 可以通过 takePersistableUriPermission 接口获取该 uriTree 的永久权限，并将 uriTree 以 SharedPreferences 等形式持久化保存，以备之后随时使用。

```
if (uriTree != null) {
    // get persistable uri permission
    final int takeFlags = data.getFlags()
            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    getContentResolver().takePersistableUriPermission(uriTree, takeFlags);

    // save uriTree to sharedPreference
    SharedPreferences sp = getSharedPreferences("DirPermission", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString("uriTree", uriTree.toString());
    editor.commit();
}


```

在使用保存的 uriTree 时，首先检查是否顺利从 SharedPreferences 中获取到 uriTree，然后通过 takePersistableUriPermission 接口是否抛异常来判断权限是否仍存在。如果权限不存在，则重新通过 SAF 申请权限。

```
SharedPreferences sp = getSharedPreferences("DirPermission", Context.MODE_PRIVATE);
String uriTree = sp.getString("uriTree", "");
if (TextUtils.isEmpty(uriTree)) {
    startSafForDirPermission();
} else {
    try {
        Uri uri = Uri.parse(uriTree);
        final int takeFlags = getIntent().getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getContentResolver().takePersistableUriPermission(uri, takeFlags);
        // uri tree permission is granted, do what you want with this uri
        LogUtil.log("uri is granted");
        DocumentFile root = DocumentFile.fromTreeUri(this, uri);
    } catch (SecurityException e) {
        LogUtil.log("uri is not granted");
        startSafForDirPermission();
    }
}


```

APP 申请到目录的永久权限后，用户可以在该 APP 的设置页面取消目录的访问权限，即点击如下图的 “Clear access” 按钮。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wqJWxNfia946t2bfUHqLycwB8ttrCsbcx8V18406Vr4IlV57sverN5ibg/640?wx_fmt=png)

**2.3.4 分享 App-specific 目录下文件**

APP 可以选择以下的方式，将自身 App-specific 目录下的文件分享给其他 APP 读写。

**2.3.4.1 使用 FileProvider**

APP 可以使用 FileProvider 将私有文件的读写权限赋给其他 APP。这种方式十分适用于 APP 主动发起事件的情况，例如从 APP 将某个私有文件分享给其他 APP。

FileProvider 相关的 Google 官方文档：

*   https://developer.android.google.cn/reference/androidx/core/content/FileProvider
    
*   https://developer.android.com/training/secure-file-sharing/setup-sharing
    

自定义 FileProvider 及使用的基本步骤：

1）在 AndroidManifest.xml 中声明 App 的 FileProvider

```
    android:authorities="com.oppo.whoops.fileprovider"
    android:
    android:grantUriPermissions="true"
    android:exported="false">
            android:
        android:resource="@xml/filepaths"/>


```

2）根据 FileProvider 声明中的 meta data，在 res/xml 中新建 filepaths.xml ，用于定义分享的路径。

```
name represents what other apps see in the shared uri as subdir. -->


```

3）在 APP 逻辑代码中生成要分享的 uri，设置权限，然后发送 uri。

```
String filePath = getExternalFilesDir("Documents") + "/MyTestImage.PNG";
Uri uri = FileProvider.getUriForFile(this, "com.oppo.whoops.fileprovider", new File(filePath));
Intent intent = new Intent(Intent.ACTION_SEND);
intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
intent.setDataAndType(uri, getContentResolver().getType(uri));
startActivity(Intent.createChooser(intent, "File Provider share"));


```

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wmiavc1ia2uUAnZxylTJiaKcnIDSgLp3kmykwptadWQbAmRx5XFcBbcib4A/640?wx_fmt=png)

4）接收方 APP 的组件设置对应的 intent-filter。

5）接收方 APP 的组件收到 intent，解析获得 uri，通过 uri 获取文件的 FD。

```
Uri uri = getIntent().getData();
ParcelFileDescriptor pdf = null;
try {
    if (uri != null) {
        LogUtil.log("Uri: " + uri);
        pdf = getContentResolver().openFileDescriptor(uri, "r ");
        LogUtil.log("Pdf: " + pdf);
    }
} catch (FileNotFoundException e) {
    LogUtil.log("open file fail
                        ");
} finally {
    try {
        if (pdf != null) {
            pdf.close();
        }
    } catch (IOException e1) {
        LogUtil.log("close fd fail ");
    }
}


```

**2.3.4.2 使用 ContentProvider**

APP 可以实现自定义 ContentProvider 来向外提供 APP 私有文件。这种方式十分适用于内部文件分享，不希望有 UI 交互的情况。

ContentProvider 相关的 Google 官方文档：

https://developer.android.google.cn/guide/topics/providers/content-providers

**2.3.4.3 使用 DocumentsProvider**

Android 默认提供的 ExternalStorageProvider、DownloadStorageProivder 和 MediaDocumentsProvider 会显示在 SAF 调起的 DocumentUI 界面中。ExternalStorageProvider 展示了所有外部存储设备的所有目录及文件，包括 App-specific 目录，所以 App-specific 目录下的文件也可以通过 SAF 授权给其他 APP。

APP 也可以自定义 DocumentsProvider 来提供向外授权。自定义的 DocumentsProivder 将作为第三方 DocumentsProvider 展示在 SAF 调起的界面中。DocumentsProvider 的使用方法请参考官方文档。

DocumentsProvider 相关的 Google 官方文档：

https://developer.android.google.cn/reference/kotlin/android/provider/DocumentsProvider

**2.3.5 细节适配**

**2.3.5.1 图片的地理位置信息**

Android Q 上，默认情况下 APP 不能获取图片的地理位置信息。如果 APP 需要访问图片上的 Exif Metadata，需要完成以下步骤：

1）申请 ACCESS_MEDIA_LOCATION 权限。

2）通过 MediaStore.setRequireOriginal 返回新 Uri。

```
Uri photoUri = Uri.withAppendedPath(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        cursor.getString(idColumnIndex));
final double[] latLong;
// Get location data from the ExifInterface class.
photoUri = MediaStore.setRequireOriginal(photoUri);
InputStream stream = getContentResolver().openInputStream(photoUri);
if (stream != null) {
    ExifInterface exifInterface = new ExifInterface(stream);
    double[] returnedLatLong = exifInterface.getLatLong();

    // If lat/long is null, fall back to the coordinates (0, 0).
    latLong = returnedLatLong != null ? returnedLatLong : new double[2];

    // Don't reuse the stream associated with the instance of "ExifInterface".
stream.close();

} else {

// Failed to load the stream, so return the coordinates (0, 0).

latLong = new double[2];

}


```

**2.3.5.2 DATA 字段数据不再可靠**

MediaStore 中，DATA（即_data）字段，在 Android Q 中开始废弃，不再表示文件的真实路径。读写文件或判断文件是否存在，不应该使用 DATA 字段，而要使用 openFileDescriptor。

同时也无法直接使用路径访问公共目录的文件。

**2.3.5.3 MediaStore.Files 接口自过滤**

通过 MediaStore.Files 接口访问文件时，只展示多媒体文件（图片、视频、音频）。其他文件，例如 PDF 文件，无法访问到。

**2.3.5.4 文件的 Pending 状态**

Android Q 上，MediaStore 中添加了一个 IS_PENDING Flag，用于标记当前文件是 Pending 状态。

其他 APP 通过 MediaStore 查询文件，如果没有设置 setIncludePending 接口，就查询不到设置为 Pending 状态的文件，这就能使 APP 专享此文件。

这个 flag 在一些应用场景下可以使用，例如在下载文件的时候：下载中，文件设置为 Pending 状态；下载完成，把文件 Pending 状态置为 0。

```
ContentValues values = new ContentValues();
values.put(MediaStore.Images.Media.DISPLAY_NAME, "myImage.PNG");
values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
values.put(MediaStore.Images.Media.IS_PENDING, 1);

ContentResolver resolver = context.getContentResolver();
Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
Uri item = resolver.insert(uri, values);

try {
    ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w", null);
    // write data into the pending image.
} catch (IOException e) {
    LogUtil.log("write image fail");
}

// clear IS_PENDING flag after writing finished.
values.clear();
values.put(MediaStore.Images.Media.IS_PENDING, 0);
resolver.update(item, values, null, null);


```

**2.3.5.5 使用 MediaStore 接口定义好的 Columns**

在使用 MediaStore 接口时，如果用到 Projection，Column Name 要使用在 MediaStore 中定义好的。如果 APP 引用的库使用了其他 Column Name，需要由 APP 做好 Column Name 映射。

**2.3.5.6 设置相对路径**

Android Q 上，通过 MediaStore 存储到公共目录的文件，除了 Uri 跟公共目录关系中规定的每一个存储空间的一级目录外，可以通过 MediaColumns.RELATIVE_PATH 来指定存储的次级目录，这个目录可以使多级，具体方法如下：

1）ContentResolver insert 方法

通过 values.put(Media.RELATIVE_PATH, "Pictures/album/family") 指定存储目录。其中，Pictures 是一级目录，album/family 是子目录。

2）ContentResolver update 方法

通过 values.put(Media.RELATIVE_PATH, "Pictures/album/family") 指定存储目录。通过 update 方法，可以移动文件的存储地方。

2.3.5.7 卸载应用

如果 APP 在 AndroidManifest.xml 中声明：android:hasFragileUserData="true"，卸载应用会有提示是否保留 APP 数据。默认应用卸载时 App-specific 目录下的数据被删除，但用户可以选择保留。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wNdbTvQ5tV2ZJhhCSNwXyibxQWBIRB52dWfg43pKHdUDicqnGia4efmFzA/640?wx_fmt=png)

**2.3.5.8 新建虚拟可移动存储**

APP 适配时，如果一个设备没有可移动存储，可以使用下面的方法新建虚拟存储设备：

1）adb shell sm set-virtual-disk true

2）在设置 -> 存储 -> Virtual SD，进行初始化

三. 禁止应用读取 Device ID
-------------------

### 3.1 背景

Android Q 对设备标识 (Device Identifier) 做了访问限制。App 必须拥有系统签名权限：READ_PRIVILEGED_PHONE_STATE，才能访 Device ID，包括 IMEI、Serial Number，这意味着第三方应用无法获取 Device ID。

### 3.2 兼容性影响

（1）TargetSdkVersion 并且没有申请 READ_PHONE_STATE 权限，或者 TargetSdkVersion>=Q，获取 device id 会抛异常 SecurityException；

（2）TargetSdkVersion 并且申请了 READ_PHONE_STATE，通过 getDeviceId 接口读取的值为 Null；

（3）无法获取到 device id，会对应用依赖 device id 的功能产生影响。

### 3.3 适配指导

● 谷歌提供的适配指导文档:

唯一标识符最佳做法：https://developer.android.google.cn/training/articles/user-data-ids

官方文档：

● 避免使用硬件 ID

● 使用 Advertising, ID 表示用户资料或者广告用途需要依赖于 GMS 包里面的 AdvertisingIdClient

● 使用 Google FirebaseInstanceId，但是会在下列情况下不同：

*   App 删除 Instance ID
    
*   App 在新设备恢复
    
*   用户卸载或者重新安装 App
    
*   用户清除数据
    

● 使用 IDs 跟 GUIDs

```
String uniqueID = UUID.randomUUID().toString();


```

● 使用 Android ID，恢复出厂，这个值会改变

```
Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


```

● 接入 OPPO OPEN_ID

目前方案正在开发中，开发完成后再补充适配信息

四. Mac 地址随机化
------------

### 4.1 背景

为了进一步保护用户的隐私，Android Q 在连接 Wi-Fi 时，默认启用了 Mac 地址随机化的特性，如果 APP 不进行适配，使用原来方式获取到的 Mac 地址可能是随机生成的，并不是真实的 Mac 地址。

### 4.2 兼容性影响

如果您的 APP 需要使用 Mac 地址作为设备的标识，无论您的 Target SDK 是否设置为 Q，只要运行在 Android Q 上，您就需要进行适配。

### 4.3 适配 指导

请参考谷歌适配指导：

https://developer.android.com/preview/privacy/data-identifiers#randomized-mac-addresses

五. 禁止后台应用启动 Activity
--------------------

### 5.1 背景

安卓 Q 版本限制了应用后台启动 Activity，该变更的目的是最大限度减少后台应用弹界面对用户的打扰，在 Android Q 上运行的应用只有在满足以下一个或多个条件时才能启动 Activity

1.  应用处于前台；
    
2.  桌面 widget 点击启动 Activity；
    
3.  由桌面点击启动应用；
    
4.  由 Recent 点击启动应用；
    
5.  前台应用启动后台应用；
    
6.  临时白名单机制，不拦截通过通知拉起的应用。
    

（a）应用通过通知，在 pendingintent 中启动 activity；

（b）应用通过通知，在 pendingintent 中发送广播，接收广播后启动 activity，加入临时白名单不拦截。

（c）应用通过通知，在 pendingintent 中启动 service，在 service 中启动 activity，加入临时白名单不拦截。

### 5.2 兼容性 影响

影响所有应用的后台启动 Activity，需全面排查及整改。

### 5.3 适配 指导

请参考谷歌适配指导：

https://developer.android.com/preview/privacy/background-activity-starts

谷歌在 Q 的 beta 版本并未真正打开该管控限制，但是如果应用的页面存在被管控的场景，系统会通过一个 Toast 告警提示，提示开发者需要整改，否则应用的某些页面在谷歌的后续版本会被拦截，具体的告警文字内容：

This background activity start from "packageName" will be blocked in future Q builds. See g.co/dev/bgblock.

App 需要测试这个特性，需要在打开这个限制，使用下面任一步骤开启即可：

● Settings > Developer options > Allow background activity starts 设置为关闭

● adb shell settings put global background_activity_starts_enabled 0

六. 后台应用地理位置权限
-------------

### 6.1 背景

Android Q 针对位置信息新增了 ACCESS_BACKGROUND_LOCATION 权限，以管控应用是否可以在后台访问位置信息。原有的 ACCESS_COARSE_LOCATION 和 ACCESS_FINE_LOCATION 权限用于管控应用在前台是否可以获取位置信息。

### 6.2 兼容性影响

地图类应用在后台获取位置信息时将受到影响。

### 6.3 适配指导

Google 官方适配指导链接：

https://developer.android.com/preview/privacy/device-location

**1、Target Sdk Version 兼容**

当应用的 Target Sdk Version < Q

● 请求 ACCESS_COARSE_LOCATION 或者 ACCESS_FINE_LOCATION 权限，系统会自动同时请求 Q 新增的 ACCESS_BACKGROUND_LOCATION(图  6-3-1)。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wXgvg1TEAoEs6DA9FiaLCWwX0BZNWwAMETw4hawrSX2M00k6Xq3NgBgQ/640?wx_fmt=png)

当应用的 Target Sdk Version >= Q

● 只请求 ACCESS_COARSE_LOCATION 权限或 ACCESS_FINE_LOCATION 权限，系统将不会提供 “始终允许” 的选择按钮，应用只能在使用时获取位置信息（图 6-3-2）。

Google 建议：如果应用不需要在后台获取位置信息，不要请求 ACCESS_BACKGROUND_LOCATION 权限。

● 请求 ACCESS_COARSE_LOCATION 权限或 ACCESS_FINE_LOCATION 权限，并同时请求 ACCESS_BACKGROUND_LOCATION 权限，则系统提供 “仅在使用该应用期间允许” 选择按钮（图 6-3-3 ）。

● Android Q 不允许在没有请求 ACCESS_COARSE_LOCATION 或 ACCESS_FINE_LOCATION 权限（或者 ACCESS_COARSE_LOCATION 或 ACCESS_FINE_LOCATION 没有被授权）的情况下单独请求 ACCESS_BACKGROUND_LOCATION 权限。

● 在 ACCESS_COARSE_LOCATION 或 ACCESS_FINE_LOCATION 已经授权的情况下，请求 ACCESS_BACKGROUND_LOCATION 权限，则弹出如图 6-3-4 的权限说明框。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wQP7ZOCv7pu0iaR0andAicqiaWSYHXxdG41b0mlYc0AePEgu0wds6MCuhg/640?wx_fmt=png)

**2、仅在使用该应用时允许**

在 Q 上，选择 “仅在使用该应用时允许”，应用只有在可见或者使用前台服务情况下才能获取到位置信息。

使用前台服务，应用需要参考以下步骤：

a. 在应用的 Manifest 中的对应 service 中添加值为 location 的 foregroundServiceType：

```
    android:
    android:foregroundServiceType="location" ... >
    ...


```

b. 启动前台服务前检查是否具有获取位置信息的权限：

```
boolean permissionAccessCoarseLocationApproved =
    ActivityCompat.checkSelfPermission(this,
        permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED;

if (permissionAccessCoarseLocationApproved) {
    // App has permission to access location in the foreground. Start your
    // foreground service that has a foreground service type of "location".
} else {
   // Make a request for foreground-only location access.
   ActivityCompat.requestPermissions(this, new String[] {
        Manifest.permission.ACCESS_COARSE_LOCATION},
       your-permission-request-code);
}


```

**3、始终允许**

在某些场景下，比如共享位置信息，应用需要始终获取位置信息。

当用户选择了 “始终允许”，应用无论在前台还是后台都可以获取到位置信息。但需要注意的是，用户选择 “始终允许” 后可以手动撤销掉后台访问的权限！（当用户选择 “始终允许” 后，系统会周期在通知栏提示用户选择了 “始终允许”，点击通知会进入位置权限详情界面，用户可以重新选择位置权限的授权）。因此，应用在进入后台获取位置信息前需要判断当前是否依然具有权限，参考以下步骤：

a. 定义后台服务，无需添加 location 的 type

```
     access to the device's location "all the time" in order to run successfully.-->
    android: ... >
    ...


```

b. 进入后台前判断是否具有后台访问位置信息的权限

```
boolean permissionAccessCoarseLocationApproved =
    ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED;

if (permissionAccessCoarseLocationApproved) {
   boolean backgroundLocationPermissionApproved =
           ActivityCompat.checkSelfPermission(this,
               permission.ACCESS_BACKGROUND_LOCATION)
               == PackageManager.PERMISSION_GRANTED;

   if (backgroundLocationPermissionApproved) {
       // App can access location both in the foreground and in the background.
       // Start your service that doesn't have a foreground service type
       // defined.
   } else {
       // App can only access location in the foreground. Display a dialog
       // warning the user that your app must have all-the-time access to
       // location in order to function properly. Then, request background
       // location.
       ActivityCompat.requestPermissions(this, new String[] {
           Manifest.permission.ACCESS_BACKGROUND_LOCATION},
           your-permission-request-code);
   }
} else {
   // App doesn't have access to the device's location at all. Make full request
   // for permission.
   ActivityCompat.requestPermissions(this, new String[] {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
        },
        your-permission-request-code);
}


```

如果应用进入后台前，发现后台访问位置信息的权限被撤销即 backgroundLocationPermissionApproved 为 false，需要重新申请 ACCESS_BACKGROUND_LOCATION 权限，这时会弹出如图 6-3-4 的权限说明框，提示用户重新选择。

7. 非 SDK 接口管控
-------------

### 7.1 背景

Google 认为非公开接口可能在不同版本之间进行变动从而导致应用兼容性问题，因此从 Android P 开始强制约定三方应用只能使用 Android SDK 公开的类和接口；对于非公开的 API，Google 按照不同名单类型进行不同程度的限制使用。

a) 从原生的 android.jar 中能够看到的就是 SDK 接口，也可以从 Google 的开发者网站 https://developer.android.com/reference/packages 进行查询。

b) 非公开 API 的分类和限制

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wE2XnicjJzibk5CrgsSBsu9jNTI6mqNmibzbXWSBJG97P4j5RjuDAkfpBA/640?wx_fmt=png)

### 7.2 兼容性影响

所有三方应用都可能会受到影响，Android Q 版本由于名单生成规则变化了，导致增加很多黑名单接口；同时有很多非 SDK 接口被删除，这些都会导致应用出现兼容性问题。

由于非公开接口的管控是在运行时进行管控的，因此用使用反射、JNI 调用和正常的空实现封装这些深灰和黑名单的接口都不会绕过 Google 的限制，应用会出现异常。

具体异常信息如下：

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wMzeKDn3pGyEUrLbicEaZRVglAeoibDteYEib81p8GvDibvgeb7mh52R8SQ/640?wx_fmt=png)

### 7.3  适配指导

Google 的官方文档中的适配指导。

https://developer.android.com/distribute/best-practices/develop/restrictions-non-sdk-interfaces

https://android-developers.googleblog.com/2018/06/an-update-on-non-sdk-restrictions-in.html

a) Google 原生最新的名单见下面的链接

https://android.googlesource.com/platform/prebuilts/runtime/+/refs/heads/master/appcompat/

hiddenapi-flags.csv 文件包含了所有的类和接口，第二列显示的就是名单类型。

三方应用要重点关注 max-o、max-p 和 blacklist 接口（private 类型的接口会是 blacklist）。这些接口不可用。

b) 静态扫描 APK 文件，获得应用使用非公开接口的情况。

使用下面链接的 veridex 工具，建议在 linux 环境下使用

https://android.googlesource.com/platform/prebuilts/runtime/+/refs/heads/master/appcompat/

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wU84iceImsglFwgNicRg7VCXk6libNxujDBq0cWOFRPWMYyTic1BI29tXibA/640?wx_fmt=png)

README.txt 给出了详细的使用步骤

备注：该方法不能检测出使用反射方式调用黑名单接口的情况。

c) 通过运行应用，获得应用使用非公开接口的情况。

通过执行 adb shell settings put global hidden_api_policy  1

将系统的 Hidden API 强制策略禁用掉，这样应用就可以在 AndroidQ 设备上运行。查看日志是否有如下关键日志信息。

log 中会有关键字打印：

Accessing hidden field Landroid/content/pm/PackageParser;->mParseError:I (blacklist, reflection)

应用行为异常有报错，有类似如下的报错:

java.lang.NoSuchMethodError: No virtual method getActivityIconCache(Landroid/content/ComponentName;)Landroid/graphics/drawable/Drawable; in class Landroid/content/pm/PackageManager; or its super classes (declaration of 'android.content.pm.PackageManager' appears in /system/framework/framework.jar)

当没有发现 Accessing hidden 日志信息时，需要确认调用的类或者方法是否由于 Android 版本升级有改变了。

建议开发者采用上述方法进行应用的非公开 SDK 接口的检查。

d) 总的适配原则是针对 max-o 、max-p 和 blacklist 接口, 尝试在公开的 SDK 中找替代方案。 例如 android.util.FloatMath 的 sqrt 方法，可用 java.lang.Math 类 return (float) Math.sqrt(value); 方式来替换。

e) 若应用无法找到可替代的 SDK 接口，但是又要使用这个非 SDK 接口，建议开发者直接给谷歌反馈，申请新的公共 API，申请链接：https://partnerissuetracker.corp.google.com/issues/new?component=328403&template=1027267

八. API Level 要求
---------------

### 8.1 背景

1.  增加对于上架谷歌 Play 商店应用要求：
    

a) 新开发的应用：2019-8-1 之后，上架谷歌 Play 商店要求应用的 TargetSdkVersion>=28 ；

b) 更新的应用：2019-11-1 之后，上架谷歌 Play 商店要求应用的 TargetSdkVersion>=28 。

2.  最小 TargetSdkVersion 要求：当用户首次运行 API 低级低于  23 (Android Marshmallow) 的应用时，会受到来自 Android Q 的警告信息。
    

8.2 兼容性 影响

应用升级 TargetSdkVersion 之后，和应用的 TargetSdkVersion 相关的变更就会影响，不适配很可能导致应用出现兼容性问题或者功能问题。

8.3 适配 指导

谷歌适配指导链接：https://developer.android.google.cn/about/versions/pie/android-9.0-changes-28

重点关注以下特性变化：

1.  非 SDK 接口管控，需要重点排查是否使用 P 版本的深灰名单接口和 Q 版本的 max-o 名单接口，请参考 7 章节进行适配
    
2.  针对 Android 9 或更高版本并使用前台服务的应用必须请求  FOREGROUND_SERVICE 权限，否则会引发 SecurityException，这是普通权限，因此，系统会自动为请求权限的应用授予此权限。
    
3.  后台执行限制，请参考谷歌适配指导：https://developer.android.google.cn/about/versions/oreo/background.html
    

九. 64 位兼容
---------

### 9.1 背景

从 2019 年 8 月 1 日开始，在 Google Play 上发布的应用必须支持 64 位架构

### 9.2 兼容性影响

应用需要自查是否具有 so 库（native 代码）。如果没有则已支持 64 位架构。如果有则需要自检是否支持并采取对应措施，否则将无法正常运行。

### 9.3 适配 指导

Google 官方适配指导链接：

https://developer.android.google.cn/distribute/best-practices/develop/64-bit

1、查找应用中的 so 文件

a. 通过 Analyze Apk 查找，参考：

https://developer.android.google.cn/distribute/best-practices/develop/64-bit#look_for_native_libraries_using_apk_analyzer

b. 通过解压 apk 文件查找，参考：

https://developer.android.google.cn/distribute/best-practices/develop/64-bit#look_for_native_libraries_by_unzipping_apks

2、确认是否支持 64 位架构

a. 如果没有 so 文件，则已支持 64 位架构

b. 根据 ABI，so 文件对应不同的目录（图 10-3-1 ），如果每个目录下都存在 so 文件，则支持 64 位架构：

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5w4hC97dGgZrYQes2h0xdDHAUQia4JoP7th1IT8OyH1aps8lZsfzjUC6w/640?wx_fmt=png)

3、构建 64 位架构的 app

a. 应用的 so 文件自己开发，参考：

https://developer.android.google.cn/distribute/best-practices/develop/64-bit#build_your_app_with_64-bit_libraries

b. Game 游戏开发者，需要使用支持 64 位的引擎：

Unreal since 2015

Cocos2d since 2015

Unity since 2018

c. Unity 开发者，参考：

https://developer.android.google.cn/distribute/best-practices/develop/64-bit#unity_developers

d. 应用使用三方的 sdk 库文件，需通知三方开发者适配支持，然后应用重新集成

10. 其他权限变更
----------

### 10.1 USB 序列化

**10.1.1 背景**

Android Q 上获取 USB 序列号需要 android.permission.MANAGE_USB 权限

**10.1.2 兼容性影响**

三方应用无法获取 USB 序列号

**10.1.3 适配指导**

Google 官方适配指导链接：https://developer.android.com/preview/privacy/data-identifiers

由于 android.permission.MANAGE_USB 是 signature|privileged 级别的权限，三方应用无法获取该权限，因此无法获取 USB 序列号。

**10.2 电话、WiFi 、蓝牙 API 所需的精确位置权限**

**10.2.1 背景**

为了管理 APP 对一些关键 API 的调用，Android Q 对电话、 Wi-Fi 以及蓝牙的相关 API 增加了 ACCESS_FINE_LOCATION 权限的限制。

**10.2.2 兼容性影响**

具体影响的各模块接口如下：

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wM9LnScDkoN7ARLSr31KmDwicPB9yF6F3ibs4amfib9nNQj1bZic6l5sSkA/640?wx_fmt=png)![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5w0rr9VnHx8mdkpsQs0WG6TCgRQicdWKVyyxAicHgQ4UM3N7cfuGSjSlrg/640?wx_fmt=png)![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5whhUiccIp0L8O38JgRhwV1wh8FdkSRMPLNOQUbX1M2b46nLD1OAy4z7Q/640?wx_fmt=png)

**10.2.3 适配指导**

参考 Google 官方适配指导链接: https://developer.android.com/preview/privacy/camera-connectivity#fine-location-telephony-wifi-bt

如果应用的 targetSdkVersion = Q，那么必须申请 ACCESS_FINE_LOCATION 权限，否则当 APP 运行在 Android Q 平台时，将无法正常使用受影响的 API。

### 10.3 应用无法后台访问剪切板数据

**10.3.1 背景**

Android P 非 Instant 应用可以任何时刻获取剪贴板内容。

Android Q 上新增 READ_CLIPBOARD_IN_BACKGROUND 权限限制应用后台获取剪贴板内容。

**10.3.2 兼容性影响**

除非应用是默认输入法编辑器（IME）或具有焦点的应用程序，否则无法获取剪贴板内容

**10.3.3 适配指导**

Google 官方适配指导链接：https://developer.android.com/preview/privacy/data-identifiers

由于 READ_CLIPBOARD_IN_BACKGROUND 权限是签名 signature 级别的权限，因此三方应用无法通过授权该权限在后台获取剪贴板内容。应用只有在具有焦点时才能获取剪贴板内容。

### 10.4 访问相机信息所需权限

**10.4.1 背景**

为了进一步保护用户的隐私，从 Android Q 开始，google 更改了默认情况下 getCameraCharacteristics()  方法返回的设备信息的粒度，部分属性将受到权限限制，这些属性可能用户运动跟踪等涉及用户隐私的领域。因此，需要特别注意的是，当您的应用需要尝试获取以下列出的包含设备特定信息的元数据时，您的应用必须具有 android.permission.CAMERA 权限才能获取此 Key 对应的返回值， 否则将返回 null。

关于如何获取 android.permission.CAMERA 权限，请访问 https://developer.android.google.cn/training/permissions/requesting

如果您的应用没有 CAMERA 权限，则无法访问以下字段：

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wuflc0IRhia8Esc2YELXVN7r02OEOaLIAwadvT6D4ibt2CuyickFcB5TCQ/640?wx_fmt=png)![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wYMHmOExeOPqPictycKasibUUK80bPlibRvad4Y53R36qA09EGTIXBHwxg/640?wx_fmt=png)

**10.4.2  兼容性影响**

即使您的应用的 target 在 Android 9（API 级别 28）或更低级别，如果没有 CAMERA 权限，在 Q 版本的 Android 系统上运行您的应用，通过 cameraCharacteristics.get(CameraCharacteristics.LENS_POSE_ROTATION) 获取对应属性时，返回值仍将为 null。

**10.4.3 适配指导**

要获取以上的属性，请动态申请 android.permission.CAMERA 的权限，参照：https://developer.android.google.cn/training/permissions/requesting

### 10.5 限制 SMS/Call Log 访问

**10.5.1 背景**

Google Play Store 中限制一些高危、高敏感的权限，包括 SMS、Call Log 权限。如果 App 没有满足 Google Play Store 的要求，会从 Google Play 移除。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wyYGaEPUCIjBGWOGdHqHzmwicDM5oG5x9FTDJyXOyzf8RStVO1mnQydA/640?wx_fmt=png)

**10.5.2 兼容性影响**

在 Google Play 上架的 App，需要注意影响。

https://support.google.com/googleplay/android-developer/answer/9047303

**10.5.3 适配指导**

适配指导请参考：https://play.google.com/about/privacy-security-deception/permissions/

● 如果应用不具备默认短信、电话或辅助处理程序功能，就不得在清单中（包括清单中的占位文本）声明需要使用上述权限。

● 只有在用户主动将应用注册为默认短信、电话或辅助处理程序的情况下，应用才能提示用户接受上述任何权限请求；当应用不再是默认处理程序时，则必须立即停止使用相应权限。

● 应用只能将权限（及其衍生数据）用于提供已获批准的关键核心应用功能（例如应用说明中记录并宣传的应用现有关键功能）。您绝不能出售此类数据。您只能基于提供应用关键核心功能或服务的目的，转移、分享或许可使用此类数据，不能将此类数据用于任何其他用途（例如改进其他应用或服务、投放广告或营销）。您不得使用其他方法（包括其他权限、API 或第三方来源）从上述权限中衍生数据。

● 通话记录和短信默认处理程序限制的例外情

上述限制是为了保护用户隐私。如果应用不是默认处理程序，但符合以上所有要求，并清楚明确地提供极具吸引力的功能或关键功能，而该功能目前只有在获得相关权限后才能实现，则我们可能会允许少数特例。我们会评估相应功能在隐私权或安全性方面对用户可能造成的影响。这类特例十分少见，并不适用于所有开发者。

11. WIFI 相关接口变更
---------------

### 11.1 背景

Android Q 为了更好的保护用户的隐私，让用户知晓应用对 Wi-Fi 配置的改动，其限制了应用对 WifiManager 重要接口的调用，三方应用将无法正常使用这些接口。此外，针对 Wi-Fi Direct 相关的广播以及接口也做了调整。

### 11.2 兼容性影响

**11.2.1 WifiManager 相关接口变更**

如下接口，应用需要进行适配。

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wwJJMOgupmGNNyCicaIiawrfMpesAs91WkXt9Sg9lugNfyHibgRic4Y9xNw/640?wx_fmt=png)

**11.2.2 Wi-Fi Direct 相关变更**

在 Android Q 中，以下与 Wi-Fi Direct 相关的广播不再具有黏性。如果您的应用依赖于在注册的时候接收这些广播，需要进行适配。

● WIFI_P2P_CONNECTION_CHANGED_ACTION

● WIFI_P2P_THIS_DEVICE_CHANGED_ACTION

### 11.3 适配指导

兼容如上接口变更，可以参照：

https://developer.android.com/preview/privacy/camera-connectivity#wifi-network-config-restrictions

https://developer.android.com/preview/behavior-changes-all#wifi-direct-broadcasts

**11.3.1 开关或关闭 Wi-Fi**

Android Q 提供了 Pannel 的方式打开或者关闭 Wi-Fi，若只希望开关 Wi-Fi，可以通过如下方式。

```
Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
startActivity(panelIntent);


```

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wPPsqd8s7ZMFricibegDH4tcdibicndFjVSibewuyaa7Qg8XsrjOicSZW6auQ/640?wx_fmt=png)

若希望是连接互联网，可以通过如下方式。

```
Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
startActivity(panelIntent);


```

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5w3iaicWfbGzGg2GnggPhGJicjQTLL8eXJSn0b9F6NVnKkUxTsfZhupxkSA/640?wx_fmt=png)

**11.3.2 使用 NetworkRequest 连接网络**

Android Q 限制应用使用连接或断开网络的接口，并推荐使用 NetworkRequest 的方式连接网络。应用通过配置 NetworkSpecifier 规则，并通过 NetworkRequst 下发到 Framework，Framework 会弹出选择框并触发一次 Wi-Fi 扫描。扫描接收后，系统会根据 NetworkSpecifier 规则将过滤后的扫描结果呈现在弹框中供用户选择。

```
PatternMatcher ssidMatcher = new PatternMatcher("OPPO", PatternMatcher.PATTERN_PREFIX);
WifiNetworkSpecifier.Builder buidler = new WifiNetworkSpecifier.Builder();
buidler.setSsidPattern(ssidMatcher);

NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
networkRequestBuilder.setNetworkSpecifier(buidler.build());
mConnectivityManager.requestNetwork(networkRequestBuilder.build(), mNetworkCallback);


```

![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5w6WEve6LNvF6HdFbsibIaaicnq7Wxic1hib0I1G1wM23w8OKG3qFj0XOTAQ/640?wx_fmt=png)

**11.3.3 使用 WifiNetworkSuggestion 连接网络**

应用可以创建 NetworkSuggestions，将其添加到 Framework。待 Framework 收到扫描结果后，会首先从系统已保存的网络中选网。如果没有可选的网络，就会从添加的 NetworkSuggestions 中选网。若 Framework 连接上本应用添加的 NetworkSuggestions 后，会发送 WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION 广播到应用内。

```
WifiNetworkSuggestion.Builder networkSuggestionBuilder = new WifiNetworkSuggestion.Builder();
networkSuggestionBuilder.setSsid("TEST");

List networkSuggestionList = new ArrayList();
networkSuggestionList.add(networkSuggestionBuilder.build());
if (mWifiManager.addNetworkSuggestions(networkSuggestionList) == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
    Log.d(TAG, "Successfully add network suggestion.");
}


```

**11.3.4 Wi-Fi Direct 广播适配**

```
原依赖于注册 WIFI_P2P_CONNECTION_CHANGED_ACTION 时接收广播内容的逻辑，可以使用 WifiP2pManager.requestNetworkInfo() 接口查询，具体使用方式如下：


```

```
p2pManager.requestNetworkInfo(p2pChannel, new WifiP2pManager.NetworkInfoListener() {
    @Override
    public void onNetworkInfoAvailable(NetworkInfo networkInfo) {
        Log.d(TAG, "Receive network information " + networkInfo);
    }
});


```

原依赖于注册 WIFI_P2P_THIS_DEVICE_CHANGED_ACTION 时接收广播内容的逻辑，可以使用 WifiP2pManager.requestDeviceInfo() 接口查询，注意该方法需要申请 ACCESS_FINE_LOCATION 权限，具体使用方式如下:

```
p2pManager.requestDeviceInfo(p2pChannel, new WifiP2pManager.DeviceInfoListener() {
    @Override
    public void onDeviceInfoAvailable(WifiP2pDevice wifiP2pDevice) {
        Log.d(TAG, "Receive deivce information " + wifiP2pDevice);
    }
}


```

十二 电话 API 重要变更
--------------

### 12.1 背景

TelephonyManager.java 的 endCall()、answerRingingCall ()、silenceRinger () 方法已失效。

### 12.2 兼容性影响

调用如上方法将无法生效，不会有任何响应。

### 12.3 适配指导

如需挂断电话：

使用 android.telecom.TelecomManager#endCall()，该接口需申请权限 Manifest.permission.ANSWER_PHONE_CALLS ，但谷歌已不建议使用，

不久的将来存在废弃的风险。

如需接听电话：

使用 android.telecom.TelecomManager# acceptRingingCall ()，该接口需申请权限 Manifest.permission.ANSWER_PHONE_CALLS ，但谷歌已不建议使用，

不久的将来存在废弃的风险。

如需实现来电静音：

请使用 android.telecom.TelecomManager# silenceRinger () 方法，但只能被用户设为默认拨号应用的前提下使用，否则将会抛出权限异常。

* * *

Android Q 的适配文档就到这里，本文档是以 Beta 4 版本为基础编写，有些地方后续应该还有一些微调，大家可以持续关注 Android Q 各方面的更新消息。

后续我也会分享一些具有针对性的 Android Q 新特性和兼容问题，有兴趣就持续关注吧。

* * *

「联机圆桌」👈推荐我的知识星球，一年 50 个优质问题，上桌联机学习。

> 公众号后台回复成长『**成长**』，将会得到我准备的学习资料，也能回复『**加群**』，一起学习进步。

[![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wfbaCtC6FI0j0opUz8UzwHgbCq1SqBU8uQibe6qbKBfbzFmqwNL39LSA/640?wx_fmt=png)](http://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247486375&idx=1&sn=427f560fd40a2dfc0fcaa89bd79f4a29&chksm=97851286a0f29b907d02029128b3d0f93b7e2dccd3f5b1569552aca5379202fcdc1f7b8beba7&scene=21#wechat_redirect)

[![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSwIgGwqImEF6tKibiclPDdFDiced2NunN3r6R8xcicz61cxBdCgLZmj1ibx0fHRlkLNl5yvsuIxFXmtlWQ/640?wx_fmt=png)](http://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247486341&idx=1&sn=2f412b301fcd509cda17a6233b3adf1b&chksm=978512a4a0f29bb27033b22e01bff1beec77ddb0a736224c0fb0bd7f186d1e75c83a1b046837&scene=21#wechat_redirect)

[![](https://mmbiz.qpic.cn/mmbiz_png/liaczD18OicSwJeXpDXYVhv4zEweIavic59qml1oUvES3OlkicqV2GvsCVHaKPaOTs66zTA5zbCdxJvlibxeA2foCWw/640?wx_fmt=png)](http://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247486331&idx=1&sn=27c86b9036be6f0e876ff8e36fc4f07c&chksm=9785125aa0f29b4cc1134a873630095367c6f7d49841a3ce6619499c39d5615201a1be46f406&scene=21#wechat_redirect)

![](https://mmbiz.qpic.cn/mmbiz_jpg/liaczD18OicSzSQtKEciaWiaJgvsgfx89V5wRWRHlvTwtC7clkaRG9tlFIUM7hTmRhvianhGuTUQwOG2eo7NAoaDia4w/640?wx_fmt=jpeg)