
Vector Drawable相对于普通的Drawable来说，有以下几个好处：


- Vector图像可以自动进行适配，不需要通过分辨率来设置不同的图片
- Vector图像可以大幅减少图像的体积，同样一张图，用Vector来实现，可能只有PNG的几十分之一
- 使用简单，很多设计工具，都可以直接导出SVG图像，从而转换成Vector图像
- 功能强大，不用写很多代码就可以实现非常复杂的动画
- 成熟、稳定，前端已经非常广泛的进行使用了

Vector图像刚发布的时候，是只支持Android 5.0+的，对于Android pre-L的系统来说，并不能使用，所以，可以说那时候的Vector并没有什么卵用。不过自从AppCompat 23.2之后，Google对p-View的Android系统也进行了兼容，也就是说，Vector可以使用于Android 2.1以上的所有系统，只需要引用com.android.support:appcompat-v7:23.2.0以上的版本就可以了，这时候，Vector应该算是迎来了它的春天。

## 获取Vector图像

1. 阿里巴巴矢量图标库: http://www.iconfont.cn/

2. 如图,下载svg资源
![](http://olg7c0d2n.bkt.clouddn.com/17-12-12/51081656.jpg)

3. 使用android studio 右键,创建

![](http://olg7c0d2n.bkt.clouddn.com/17-12-12/12762614.jpg)

4. 添加生成vector
- 本地SVG,PSD文件
- Name:名称
- Path:本地文件路径
- Size:可以自行调节大小
- Opacity:不透明性

![](http://olg7c0d2n.bkt.clouddn.com/17-12-12/23228562.jpg)

添加进来之后,类似于下面的代码

- android:viewportHeight和android:viewportWidth属性，这个是画布宽高

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
        android:width="50dp"
        android:height="50dp"
        android:viewportHeight="1024.0"
        android:viewportWidth="1024.0">
    <path
        android:fillColor="#BECFDB"
        android:pathData="M0,853.3c0,93.9 76.8,170.7 170.7,170.7h102.4v-85.3H187.7c-56.3,0 -102.4,-47.4 -102.4,-105.4V750.9H0v102.4zM85.3,190.7C85.3,132.7 131.4,85.3 187.7,85.3h85.3V0H170.7C76.8,0 0,76.8 0,170.7v102.4h85.3V190.7zM1024,273.1V170.7c0,-93.9 -76.8,-170.7 -170.7,-170.7h-102.4v85.3h85.3c56.3,0 102.4,47.4 102.4,105.4V273.1h85.3zM938.7,833.3c0,58 -46.1,105.4 -102.4,105.4h-85.3v85.3h102.4c93.9,0 170.7,-76.8 170.7,-170.7v-102.4h-85.3v82.3z"/>
    <path
        android:fillColor="#3E96D2"
        android:pathData="M699.7,443.7h-119.5V324.3c0,-37.5 -30.7,-68.3 -68.3,-68.3s-68.3,30.7 -68.3,68.3v119.5H324.3c-37.5,0 -68.3,30.7 -68.3,68.3s30.7,68.3 68.3,68.3h119.5v119.5c0,37.5 30.7,68.3 68.3,68.3s68.3,-30.7 68.3,-68.3v-119.5h119.5c37.5,0 68.3,-30.7 68.3,-68.3s-30.7,-68.3 -68.3,-68.3z"/>
</vector>

```

5. 在布局中使用

**注意:使用前需要在app build.gradle中添加android下面的defaultConfig下面添加vectorDrawables.useSupportLibrary = true**

```gradle
android {
    defaultConfig {
        vectorDrawables.useSupportLibrary = true
    }
}

//还需要添加依赖
compile 'com.android.support:support-vector-drawable:25.3.1'
```

6. 在Application的onCreate()中加入如下代码,兼容:
`AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);`

这样一来，我们就可以在5.0以下的设备上使用Vector了。

7. 需要注意的是，如果我们在EditText中使用 android:drawableLeft 和 android:drawableStart 时，有可能还会报错。解决办法。 
首先将你的svg资源用selector标签包裹起来。 
例如：

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item  android:drawable="@drawable/ic_user"></item>

</selector>
```

8. 一定要用app:srcCompat才行

直接在ImageView的background中引入该资源即可.
比如下面的代码:
```xml
<TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:drawableStart="@drawable/ic_add"/>
<ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:srcCompat="@drawable/ic_add"/>
```


6. 动态修改SVG颜色

```kotlin
//大于等于API 21 就可以使用VectorDrawable
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	val vectorDrawable = mImg.drawable as VectorDrawable
	vectorDrawable.setTint(Color.parseColor("#FF4081"))
} else {
	//小于API 21则需要用VectorDrawableCompat  才行
	val vectorDrawableCompat = VectorDrawableCompat.create(resources, R.drawable.ic_beach_access_black_24dp, theme)
	vectorDrawableCompat?.setTint(Color.parseColor("#FF4081"))
	mImg.setImageDrawable(vectorDrawableCompat)
}
```