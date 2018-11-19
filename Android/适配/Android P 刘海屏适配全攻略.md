
# Android P 刘海屏适配全攻略

# 1.前言

先吐槽一下，刘海屏真丑。然而作为苦逼的开发者，还是要去适配刘海屏的。好了，吐槽完毕，进入正题。

# 2.Android P中的刘海屏适配

# 2.1 Google对刘海屏的支持介绍

Google将刘海屏命名为屏幕缺口了，这一小节内容摘自Android官方介绍：
[屏幕缺口支持](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Fpreview%2Ffeatures%23cutout)。

Android P 支持最新的全面屏以及为摄像头和扬声器预留空间的凹口屏幕。 通过全新的 `DisplayCutout` 类，可以确定非功能区域的位置和形状，这些区域不应显示内容。 要确定这些凹口屏幕区域是否存在及其位置，请使用 `getDisplayCutout()` 函数。

全新的窗口布局属性 `layoutInDisplayCutoutMode` 让您的应用可以为设备凹口屏幕周围的内容进行布局。 您可以将此属性设为下列值之一：

- `LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT`
- `LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS`
- `LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER`

您可以按如下方法在任何运行 Android P 的设备或模拟器上模拟屏幕缺口：

1. 启用开发者选项。
1. 在 **Developer options（开发者选项）** 屏幕中，向下滚动至 **Drawing（绘图）** 部分并选择 **Simulate a display with a cutout（模拟具有凹口的显示屏）**。
1. 选择凹口屏幕的大小。

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656cc1e6157?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

## 2.2 Android P提供提供的刘海屏适配方案

1. 对于有状态栏的页面，不会受到刘海屏特性的影响，因为刘海屏包含在状态栏中了；
1. 全屏显示的页面，系统刘海屏方案会对应用界面做下移处理，避开刘海区显示，这时会看到刘海区域变成一条黑边，完全看不到刘海了；
1. 已经适配Android P应用的全屏页面可以通过谷歌提供的适配方案使用刘海区，真正做到全屏显示。

## 2.3 Android P中支持的凹口屏幕类型

目前Android支持了三类凹口屏幕类型：**边角显示屏凹口（斜刘海）**、**双显示屏凹口（刘海+胡子）**、**长型显示屏凹口（刘海）**，如下图所示：

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656cc56197e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656cc8fa9ad?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656cc72a057?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

目前的手机主要还是长型显示屏凹口，即刘海屏。其他斜刘海和胡子手机应该还没有实物吧？反正是亮瞎了狗眼了。

## 2.4 刘海屏布局及安全区域说明

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656cc458f42?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

## 2.5 Android P中凹口屏幕相关接口

注意，以下接口都是要`Build.VERSION.SDK_INT &gt;= 28`才能调用到。

### 2.5.1 DisplayCutout类接口

主要用于获取凹口位置和安全区域的位置等。主要接口如下所示：

|方法|接口说明
|------
|getBoundingRects()|返回Rects的列表，每个Rects都是显示屏上非功能区域的边界矩形。
|getSafeInsetLeft ()|返回安全区域距离屏幕左边的距离，单位是px。
|getSafeInsetRight ()|返回安全区域距离屏幕右边的距离，单位是px。
|getSafeInsetTop ()|返回安全区域距离屏幕顶部的距离，单位是px。
|getSafeInsetBottom()|返回安全区域距离屏幕底部的距离，单位是px。

来看下例子。
这里将**开发者选项**中的**模拟具有凹口的显示屏**选项改为**双显示屏凹口**，即这里应当有两个刘海，然后，直接上代码：

```kotlin
public class NotchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //开局就一张背景图
        setContentView(R.layout.notch);

        getNotchParams();
    }

    @TargetApi(28)
    public void getNotchParams() {
        final View decorView = getWindow().getDecorView();

        decorView.post(new Runnable() {
            @Override
            public void run() {
                DisplayCutout displayCutout = decorView.getRootWindowInsets().getDisplayCutout();
                Log.e("TAG", "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
                Log.e("TAG", "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
                Log.e("TAG", "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
                Log.e("TAG", "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());
                
                List&lt;Rect&gt; rects = displayCutout.getBoundingRects();
                if (rects == null || rects.size() == 0) {
                    Log.e("TAG", "不是刘海屏");
                } else {
                    Log.e("TAG", "刘海屏数量:" + rects.size());
                    for (Rect rect : rects) {
                        Log.e("TAG", "刘海屏区域：" + rect);
                    }
                }
            }
        });
    }
}

```

输出结果为：

```
06-04 21:57:10.120 5698-5698/? E/TAG: 安全区域距离屏幕左边的距离 SafeInsetLeft:0
06-04 21:57:10.120 5698-5698/? E/TAG: 安全区域距离屏幕右部的距离 SafeInsetRight:0
06-04 21:57:10.120 5698-5698/? E/TAG: 安全区域距离屏幕顶部的距离 SafeInsetTop:112
06-04 21:57:10.120 5698-5698/? E/TAG: 安全区域距离屏幕底部的距离 SafeInsetBottom:112
06-04 21:57:10.120 5698-5698/? E/TAG: 刘海屏数量:2
06-04 21:57:10.120 5698-5698/? E/TAG: 刘海屏区域：Rect(468, 0 - 972, 112)
06-04 21:57:10.120 5698-5698/? E/TAG: 刘海屏区域：Rect(468, 2448 - 972, 2560)

```

可以看到，即距离顶部和底部各112px的区域就是安全区域了。

### 2.5.2 设置凹口屏幕显示模式

使用例子：

```
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
    getWindow().setAttributes(lp);

```

Android P中新增了一个布局参数属性`layoutInDisplayCutoutMode`，包含了三种不同的模式，如下所示：

|模式|模式说明
|------
|LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT|只有当DisplayCutout完全包含在系统栏中时，才允许窗口延伸到DisplayCutout区域。 否则，窗口布局不与DisplayCutout区域重叠。
|LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER|该窗口决不允许与DisplayCutout区域重叠。
|LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES|该窗口始终允许延伸到屏幕短边上的DisplayCutout区域。

下面我们来写个Demo看下这三种模式的显示效果：
Demo很简单，就是显示一张背景图，相关背景布局就不贴了，来看下主要的代码：

```
public class NotchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //开局就一张背景图
        setContentView(R.layout.notch);

        //全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        
        //下面图1
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        //下面图2
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        //下面图3
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        getWindow().setAttributes(lp);
    }
}

```

这里设置为全屏的显示效果，三种模式的结果如下图所示：

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656e814ce82?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656ee28fc4f?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656e814ce82?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

可以看到：

> 
<ol>
- `LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES`模式会让屏幕到延申刘海区域中。
- `LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER`模式不会让屏幕到延申刘海区域中，会留出一片黑色区域。
- `LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT`模式在全屏显示下跟`LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER`一样。
</ol>


我们再来看看`LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT`模式在沉浸式状态栏下的效果，代码如下：

```kotlin
public class NotchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //开局就一张背景图
        setContentView(R.layout.notch);

        //全屏显示
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;

        getWindow().setAttributes(lp);

    }
}

```

如下图所示：

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656f48dd8c5?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)


可以看到：

> 
当刘海区域完全在系统的状态栏时，`LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT`的显示效果与`LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES`一致。


所以，当我们进行刘海屏的适配时，请根据实际情况去使用不同的`layoutInDisplayCutoutMode`。

### 2.6 那么刘海屏该如何适配呢？

#### 2.6.1 如果页面存在状态栏

- 那么很简单，不用适配，因为刘海区域会包含在状态栏中了。
- 如果不想看到刘海区域，可以使用`LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER`将刘海区域变成一条黑色边。

#### 2.6.2 如果页面是全屏显示

- 不适配的话将会留出一条黑色边。
- 要做到真正全屏的话，那么就先要获取到刘海的区域（危险区域），内容部分（操作按钮等）应当避开危险区域，保证在安全区域中展示。横屏的话两边都需要注意避开刘海（危险区域）。

# 3.Android P之前的刘海屏适配

上面是Android P才有的解决方案，在P之前呢，上面的代码通通都没用。然而我们伟大的国产厂商在Android P之前（基本都是Android O）就用上了高档大气上档次的刘海屏，所以，这也造就了各大厂商在Android P之前的解决方案百花齐放。下面，我们来看下主流厂商：华为、vivo、OPPO、小米等所提供的方案。

注：相关的代码都已封装好，可以直接拷贝使用。

## 3.1 华为

### 3.1.1 使用刘海区显示

使用新增的`meta-data`属性`android.notch_support`。
在应用的`AndroidManifest.xml`中增加`meta-data`属性，此属性不仅可以针对`Application`生效，也可以对`Activity`配置生效。
如下所示：

```
&lt;meta-data android:name="android.notch_support" android:value="true"/&gt;

```

- 对`Application`生效，意味着该应用的所有页面，系统都不会做竖屏场景的特殊下移或者是横屏场景的右移特殊处理。
- 对`Activity`生效，意味着可以针对单个页面进行刘海屏适配，设置了该属性的`Activity`系统将不会做特殊处理。

实际上还有一种代码实现的方式，不过代码比较多，这里就不贴了，有兴趣的话可以在文末的链接中点进去看看。

### 3.1.2 是否有刘海屏

通过以下代码即可知道华为手机上是否有刘海屏了，`true`为有刘海，`false`则没有。

```kotlin
    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtHuawei Exception");
        } finally {
            return ret;
        }
    }

```

### 3.1.3 刘海尺寸

华为提供了接口获取刘海的尺寸，如下：

```kotlin
    //获取刘海尺寸：width、height
    //int[0]值为刘海宽度 int[1]值为刘海高度
    public static int[] getNotchSizeAtHuawei(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "getNotchSizeAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "getNotchSizeAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "getNotchSizeAtHuawei Exception");
        } finally {
            return ret;
        }
    }

```

## 3.2 vivo

vivo在**设置**--**显示与亮度**--**第三方应用显示比例**中可以切换是否全屏显示还是安全区域显示。

![](https://user-gold-cdn.xitu.io/2018/6/7/163da656faf6f32a?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

### 3.2.1 是否有刘海屏

```kotlin
    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    public static final int VIVO_FILLET = 0x00000008;//是否有圆角

    public static boolean hasNotchAtVoio(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtVoio ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtVoio NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtVoio Exception");
        } finally {
            return ret;
        }
    }

```

### 3.2.2 刘海尺寸

vivo不提供接口获取刘海尺寸，目前vivo的刘海宽为100dp,高为27dp。




## 3.3 OPPO

OPPO目前在设置 -- 显示 -- 应用全屏显示 -- 凹形区域显示控制，里面有关闭凹形区域开关。

### 3.3.1 是否有刘海屏

```kotlin
    public static boolean hasNotchInScreenAtOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

```


### 3.3.2 刘海尺寸

OPPO不提供接口获取刘海尺寸，目前其有刘海屏的机型尺寸规格都是统一的。不排除以后机型会有变化。
其显示屏宽度为1080px，高度为2280px。刘海区域则都是宽度为324px,  高度为80px。


![](https://user-gold-cdn.xitu.io/2018/6/7/163da657057e3062?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)


## 3.4 小米

### 3.4.1 是否有刘海屏

系统增加了 property `ro.miui.notch`，值为1时则是 Notch 屏手机。

手头上没有小米8的手机，暂时没法验证，这里就不贴代码了，免得误导大家。后面测试过再放出来。

### 3.4.2 刘海尺寸

小米的状态栏高度会略高于刘海屏的高度，因此可以通过获取状态栏的高度来间接避开刘海屏，获取状态栏的高度代码如下：

```
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId &gt; 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

```

其他手机也可以通过这个方法来间接避开刘海屏，但是有可能有些手机的刘海屏高度会高于状态栏的高度，所以这个方法获取到的结果并不一定安全。

## 3.5 其他厂商

如果要适配其他厂商的刘海屏，可以去找下他们的开发者文档，一般都会有提供的，这里就不详述了。

# 4 参考资料：

[1.Android P 功能和 API](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Fpreview%2Ffeatures%23cutout)

[2.华为刘海屏手机安卓O版本适配指导](https://link.juejin.im?target=https%3A%2F%2Fdevcenter-test.huawei.com%2Fconsumer%2Fcn%2Fdevservice%2Fdoc%2F50114)

[3.vivo全面屏应用适配指南](https://link.juejin.im?target=https%3A%2F%2Fdev.vivo.com.cn%2Fdoc%2Fdocument%2Finfo%3Fid%3D103)

[4.OPPO凹形屏适配说明](https://link.juejin.im?target=https%3A%2F%2Fopen.oppomobile.com%2Fservice%2Fmessage%2Fdetail%3Fid%3D61876)

[5.MIUI Notch 屏适配说明](https://link.juejin.im?target=https%3A%2F%2Fdev.mi.com%2Fconsole%2Fdoc%2Fdetail%3FpId%3D1293)