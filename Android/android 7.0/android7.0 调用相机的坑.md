# Android 7.0调用系统相机

1.在 res 目录下新建文件夹 xml 然后创建资源文件 filepaths(随意名字)
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <external-path
        name="images"
        path="test/"/>
</resources>
```

其中
```xml
<files-path/> //代表的根目录： Context.getFilesDir()
<external-path/> //代表的根目录: Environment.getExternalStorageDirectory()
<cache-path/> //代表的根目录: getCacheDir()
```

2.在manifest中添加provider
```xml
<application
   
   ......
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="com.xykj.customview.fileprovider" 
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/filepaths"/>
    </provider>
</application>
```

3. 3.在java代码中

```java
/**
 * 使用相机
 */
private void useCamera() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/test/" + System.currentTimeMillis() + ".jpg");
    file.getParentFile().mkdirs();
    
    //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
    Uri uri = FileProvider.getUriForFile(this, "com.xykj.customview.fileprovider", file);
    //添加权限
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    startActivityForResult(intent, REQUEST_CAMERA);
}
```

调用相机拍照，图片得存储吧，存储图片又需要权限，因此动态申请权限
AndroidManifest.xml文件中
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

java代码中
```java
public void applyWritePermission() {

    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    if (Build.VERSION.SDK_INT >= 23) {
        int check = ContextCompat.checkSelfPermission(this, permissions[0]);
        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
        if (check == PackageManager.PERMISSION_GRANTED) {
            //调用相机
            useCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    } else {
        useCamera();
    }
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        useCamera();
    } else {
        // 没有获取 到权限，从新请求，或者关闭app
        Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
    }
}
```

然后在ImageView点击事件中调用applyWritePermission()方法 并在onActivityResult中编写显示图片的代码
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1 && resultCode == RESULT_OK) {
        Log.e("TAG", "---------" + FileProvider.getUriForFile(this, "com.xykj.customview.fileprovider", file));
        imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
    }
}
```
完成，看下FileProvider.getUriFile方法得到的Uri结果
content://com.xykj.customview.fileprovider/images/1494663973508.jpg
可以发现 name为临时的文件夹名 path为自己定义路径的文件夹名
```xml
<resources>
    <external-path name="images" path="test/"/>
</resources>
```

4.最后发现此方法相机拍照的图片并没有显示在手机图库中
想要在手机相册图库中显示刚拍照的图片可以采用发送广播的方式
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1 && resultCode == RESULT_OK) {
        headImageView.setImageURI(Uri.fromFile(file));

        //在手机相册中显示刚拍摄的图片
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
}
```
