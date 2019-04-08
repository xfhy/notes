> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/b117c974deaf

# 概述

在 Android 开发的过程中，View 的变化是很常见的，如果 View 变化的过程没有动画来过渡而是瞬间完成，会让用户感觉很不友好，因此学习好 Android 系统中的动画框架是很重要的。
Android 系统提供了两个动画框架：属性动画框架和 View 动画框架。 两个动画框架都是可行的选项，但是属性动画框架通常是首选的使用方法，因为它更灵活，并提供更多的功能。 除了这两个框架，还可以使用 Drawable 动画（即逐帧动画，AnimationDrawable），它允许你加载 Drawable 资源并逐帧地显示它们。
1> View 动画框架
View 动画框架是旧的框架，只能用于 Views。 比较容易设置和能满足许多应用程序的需要。View 动画框架中一共提供了 AlphaAnimation（透明度动画）、RotateAnimation（旋转动画）、ScaleAnimation（缩放动画）、TranslateAnimation（平移动画）四种类型的补间动画；并且 View 动画框架还提供了动画集合类（AnimationSet），通过动画集合类（AnimationSet）可以将多个补间动画以组合的形式显示出来。View 动画框架中动画相关类的继承关系如下图所示：

![](https://upload-images.jianshu.io/upload_images/2171639-131f06130ad5a61b.png)

2> 属性动画框架
与属性动画相比 View 动画存在一个缺陷，View 动画改变的只是 View 的显示，而没有改变 View 的响应区域，并且 View 动画只能对 View 做四种类型的补间动画。因此 Google 在 Android3.0（API 级别 11）及其后续版本中添加了属性动画框架，从名称中就可以知道只要某个类具有属性（即该类含有某个字段的 set 和 get 方法），那么属性动画框架就可以对该类的对象进行**动画操作**（其实就是通过反射技术来获取和执行属性的 get，set 方法），同样属性动画框架还提供了动画集合类（AnimatorSet），通过动画集合类（AnimatorSet）可以将多个属性动画以组合的形式显示出来。属性动画框架中动画相关类的继承关系如下图所示：

![](https://upload-images.jianshu.io/upload_images/2171639-664294987b728f1f.png)

3> Drawable 动画
可绘制动画通过一个接一个地加载一系列 Drawable 资源来创建动画。 这是一个传统的动画，它是用一系列不同的图像创建的，按顺序播放，就像一卷电影；逐帧动画中动画相关类的继承关系如下图所示：

![](https://upload-images.jianshu.io/upload_images/2171639-eedb69268babded4.png)

# 预备知识

## 1\. 时间插值器

对于补间动画：时间插值器（TimeInterpolator）的作用是根据时间流逝的百分比计算出动画进度的百分比。有了动画进度的百分比，就可以很容易的计算出动画开始的关键帧与将要显示的帧之间的差异（通过 Transformation 类的对象表示），下面展示 TranslateAnimation 类中如何根据动画进度的百分比计算出动画开始的关键帧与将要显示的帧之间的差异：

```
@Override
protected void applyTransformation(float interpolatedTime, Transformation t) {
    float dx = mFromXDelta;
    float dy = mFromYDelta;
    if (mFromXDelta != mToXDelta) {
        dx = mFromXDelta + ((mToXDelta - mFromXDelta) * interpolatedTime);
    }
    if (mFromYDelta != mToYDelta) {
        dy = mFromYDelta + ((mToYDelta - mFromYDelta) * interpolatedTime);
    }
    t.getMatrix().setTranslate(dx, dy);
}

```

上面源码中 applyTransformation 方法的第一个参数就是通过时间插值器（TimeInterpolator）获取的动画进度的百分比。
然后根据帧之间的差异绘制出将要显示的帧，以此类推从而形成动画的效果。

对于属性动画：时间插值器（TimeInterpolator）的作用是根据时间流逝的百分比计算出动画进度的百分比（即属性值改变的百分比）。

Android 提供了常用的时间插值器如下表所示：

| Interpolator class | Resource ID |
| --- | --- |
| [AccelerateDecelerateInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/AccelerateDecelerateInterpolator.html) | @android:anim/accelerate_decelerate_interpolator |
| [AccelerateInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/AccelerateInterpolator.html) | @android:anim/accelerate_interpolator |
| [AnticipateInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/AnticipateInterpolator.html) | @android:anim/anticipate_interpolator |
| [AnticipateOvershootInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/AnticipateOvershootInterpolator.html) | @android:anim/anticipate_overshoot_interpolator |
| [BounceInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/BounceInterpolator.html) | @android:anim/bounce_interpolator |
| [CycleInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/CycleInterpolator.html) | @android:anim/cycle_interpolator |
| [DecelerateInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/DecelerateInterpolator.html) | @android:anim/decelerate_interpolator |
| [LinearInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/LinearInterpolator.html) | @android:anim/linear_interpolator |
| [OvershootInterpolator](https://link.jianshu.com?t=https://developer.android.com/reference/android/view/animation/OvershootInterpolator.html) | @android:anim/overshoot_interpolator |

上面列举的常用的时间插值器对应的类与时间插值器（TimeInterpolator）之间的继承关系如下图所示：

![](https://upload-images.jianshu.io/upload_images/2171639-d304b3caad058c72.png)

为了比较直观的感受到 Android 提供的常用时间插值器的效果，我通过程序绘制出了动画进度的百分比随着时间流逝的百分比变化的波形图（波形图的横向代表时间流逝的百分比，纵向代表动画进度的百分比，时间流逝的百分比我一共选取了 11 个值，分别为 0、0.1、以此类推一直到 1），如下所示：

![](https://upload-images.jianshu.io/upload_images/2171639-001557cf6e57345e.png) ![](https://upload-images.jianshu.io/upload_images/2171639-4a2e4b57fd715740.png) ![](https://upload-images.jianshu.io/upload_images/2171639-e9b529092e85e30c.png) ![](https://upload-images.jianshu.io/upload_images/2171639-5acc8e7945ed6216.png) ![](https://upload-images.jianshu.io/upload_images/2171639-f923854cb7fe9884.png) ![](https://upload-images.jianshu.io/upload_images/2171639-83f2b64352e8220c.png) ![](https://upload-images.jianshu.io/upload_images/2171639-d3b0a331e2d29d8a.png) ![](https://upload-images.jianshu.io/upload_images/2171639-096ccec677f49cba.png) ![](https://upload-images.jianshu.io/upload_images/2171639-05bd8dadbc8236d0.png)

## 2\. 类型估值器

类型估值器（TypeEvaluator）是针对于属性动画框架的，对于 View 动画框架是不需要类型估值器（TypeEvaluator）的。
类型估值器（TypeEvaluator）的作用是根据属性值改变的百分比计算出改变后的属性值。由于属性动画实际上作用的是对象的属性，而属性的类型是不同的，因此 Android 内置了一些常用的类型估值器来操作不同类型的属性，如下图所示：

![](https://upload-images.jianshu.io/upload_images/2171639-8dcada4d76df48bc.png)

# View 动画框架

使用 View 动画框架可以在 Views 上执行补间动画。 补间动画是指只要指定动画的开始、动画结束的 "关键帧"，而动画变化的 "中间帧" 由系统计算并补齐；无论动画怎样改变 View 的显示区域（移动或者改变大小），持有该动画的 View 的边界不会自动调整来适应 View 的显示区域， 即使如此，该动画仍将被绘制在超出其视图边界并且不会被剪裁， 但是，如果动画超过父视图的边界，则会出现裁剪。在 Android 中的 View 动画框架中一共提供了 AlphaAnimation（透明度动画）、RotateAnimation（旋转动画）、ScaleAnimation（缩放动画）、TranslateAnimation（平移动画）四种类型的补间动画。

```
FILE LOCATION:
    res/anim/filename.xml
    The filename will be used as the resource ID.
COMPILED RESOURCE DATATYPE:
    Resource pointer to an Animation.
RESOURCE REFERENCE:
    In Java: R.anim.filename
    In XML: @[package:]anim/filename

```

语法：

```
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@[package:]anim/interpolator_resource"
    android:shareInterpolator=["true" | "false"] >
    <alpha
        android:fromAlpha="float"
        android:toAlpha="float" />
    <scale
        android:fromXScale="float"
        android:toXScale="float"
        android:fromYScale="float"
        android:toYScale="float"
        android:pivotX="float"
        android:pivotY="float" />
    <translate
        android:fromXDelta="float"
        android:toXDelta="float"
        android:fromYDelta="float"
        android:toYDelta="float" />
    <rotate
        android:fromDegrees="float"
        android:toDegrees="float"
        android:pivotX="float"
        android:pivotY="float" />
    <set>
        ...
    </set>
</set>

```

<set> 标签表示补间动画的集合，对应于 AnimationSet 类，所以上面语法中的 < set > 标签可以包含多个补间动画的标签；并且补间动画的集合中还可以包含补间动画的集合。
<set> 标签的相关属性如下所示：

```
android:interpolator
Interpolator resource. 设置动画集合所采用的插值器，默认值为@android:anim/accelerate_decelerate_interpolator

android:shareInterpolator
Boolean. 表示集合中的动画是否共享集合的插值器。当值为true且集合没有设置插值器，
此时集合中的动画就会使用默认的插值器@android:anim/accelerate_decelerate_interpolator，
但是你也可以为集合中的动画单独指定所需的插值器。

```

### AlphaAnimation（透明度动画）

上面语法中的 <alpha> 标签代表的就是透明度动画，顾名思义透明度动画就是通过不断改变 View 的透明度实现动画的效果。
<alpha> 标签相关属性如下所示：

```
android:fromAlpha
Float. 设置透明度的初始值，其中0.0是透明，1.0是不透明的。

android:toAlpha
Float. 设置透明度的结束值，其中0.0是透明，1.0是不透明的。

```

举例如下：
首先展示一下动画效果：

![](https://upload-images.jianshu.io/upload_images/2171639-f327119ec22bdb90.gif)

实现代码如下：
首先通过 xml 定义一个透明度动画（AlphaAnimation），代码如下：

```
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android" >
    <alpha
        android:duration="2000"
        android:fromAlpha="1.0"
        android:toAlpha="0.0" />
</set>

```

接着将上面定义的透明度动画（AlphaAnimation）设置为 ImageView 的 src，然后通过 startAnimation 播放动画，代码如下：

```
<ImageView
    android:id="@+id/image"
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:layout_gravity="center"
    android:src="@drawable/second_pic"
    android:scaleType="centerCrop"
    android:alpha="1.0" >
</ImageView>

loadAnimation = AnimationUtils.loadAnimation(getActivity(),
        R.anim.base_animation_alpha);
loadAnimation.setFillAfter(true);
image.startAnimation(loadAnimation);

```

### ScaleAnimation（缩放动画）

上面语法中的 <scale> 标签代表的就是缩放动画，顾名思义缩放动画就是通过不断缩放 View 的宽高实现动画的效果。
<scale> 标签相关属性如下所示：

```
android:fromXScale
Float. 水平方向缩放比例的初始值，其中1.0是没有任何变化。
android:toXScale
Float. 水平方向缩放比例的结束值，其中1.0是没有任何变化。
android:fromYScale
Float. 竖直方向缩放比例的初始值，其中1.0是没有任何变化。
android:toYScale
Float. 竖直方向缩放比例的结束值，其中1.0是没有任何变化。
android:pivotX
Float. 缩放中心点的x坐标
android:pivotY
Float. 缩放中心点的y坐标

```

举例如下：
首先展示一下动画效果：

![](https://upload-images.jianshu.io/upload_images/2171639-77fa233e5a41d7f0.gif)

实现代码如下：
首先通过 xml 定义一个缩放动画（ScaleAnimation），代码如下：

```
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android" >
    <scale
        android:duration="5000"
        android:fromXScale="1.0"
        android:fromYScale="1.0"
        android:interpolator="@android:anim/accelerate_interpolator"
        android:pivotX="50%"
        android:pivotY="50%"
        android:toXScale="0.0"
        android:toYScale="0.0" />
</set>

```

接着将上面定义的缩放动画（ScaleAnimation）设置为 ImageView 的 src，然后通过 startAnimation 播放动画，代码如下：

```
ImageView对应的layout代码同上，这里就不赘叙了。

loadAnimation = AnimationUtils.loadAnimation(getActivity(),
        R.anim.base_animation_scale);
loadAnimation.setFillAfter(true);
image.startAnimation(loadAnimation);

```

### TranslateAnimation（平移动画）

上面语法中的 <translate> 标签代表的就是平移动画，顾名思义平移动画就是通过不断移动 View 的位置实现动画的效果。
<translate> 标签相关属性如下所示：

```
android:fromXDelta
Float or percentage. 移动起始点的x坐标. 表示形式有三种：
1 相对于自己的左边界的距离，单位像素值。（例如 "5"）
2 相对于自己的左边界的距离与自身宽度的百分比。（例如  "5%"）
3 相对于父View的左边界的距离与父View宽度的百分比。（例如 "5%p"）
android:toXDelta
Float or percentage. 移动结束点的x坐标. 表现形式同上
android:fromYDelta
Float or percentage. 移动起始点的y坐标. 表示形式有三种：
1 相对于自己的上边界的距离，单位像素值。（例如 "5"）
2 相对于自己的上边界的距离与自身高度的百分比。（例如  "5%"）
3 相对于父View的上边界的距离与父View高度的百分比。（例如 "5%p"）
android:toYDelta
Float or percentage. 移动结束点的y坐标. 表现形式同上

```

举例如下：
首先展示一下动画效果：

![](https://upload-images.jianshu.io/upload_images/2171639-e94e22c25cdce724.gif)

实现代码如下：
首先通过 xml 定义一个平移动画（TranslateAnimation），代码如下：

```
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android" >
    <translate
        android:duration="2000"
        android:fromXDelta="20"
        android:fromYDelta="20"
        android:toXDelta="100"
        android:toYDelta="100" />
</set>

```

接着将上面定义的平移动画（TranslateAnimation）设置为 ImageView 的 src，然后通过 startAnimation 播放动画，代码如下：

```
ImageView对应的layout代码同上，这里就不赘叙了。

loadAnimation = AnimationUtils.loadAnimation(getActivity(),
        R.anim.base_aniamtion_translate);
loadAnimation.setFillAfter(true);
image.startAnimation(loadAnimation);

```

### RotateAnimation（旋转动画）

上面语法中的 <rotate> 标签代表的就是旋转动画，顾名思义旋转动画就是通过不断旋转 View 实现动画的效果。
<rotate> 标签相关属性如下所示：

```
android:fromDegrees
Float. 旋转初始的角度。
android:toDegrees
Float. 旋转结束的角度。
android:pivotX
Float or percentage. 旋转中心点x坐标，表示形式有三种：
1 相对于自己的左边界的距离，单位像素值。（例如 "5"）
2 相对于自己的左边界的距离与自身宽度的百分比。（例如 "5%"）
3 相对于父View的左边界的距离与父View宽度的百分比。（例如 "5%p"）
android:pivotY
Float or percentage. 旋转中心点y坐标，表示形式有三种：
1 相对于自己的上边界的距离，单位像素值。（例如 "5"）
2 相对于自己的上边界的距离与自身宽度的百分比。（例如 "5%"）
3 相对于父View的上边界的距离与父View高度的百分比。（例如 "5%p"）

```

举例如下：
首先展示一下动画效果：

![](https://upload-images.jianshu.io/upload_images/2171639-221adf19ed80a0e7.gif)

实现代码如下：
首先通过 xml 定义一个旋转动画（RotateAnimation），代码如下：

```
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android" >
    <rotate
        android:duration="1000"
        android:fromDegrees="0"
        android:interpolator="@android:anim/accelerate_decelerate_interpolator"
        android:pivotX="50%"
        android:pivotY="50%"
        android:toDegrees="+360" />
</set>

```

接着将上面定义的旋转动画（RotateAnimation）设置为 ImageView 的 src，然后通过 startAnimation 播放动画，代码如下：

```
ImageView对应的layout代码同上，这里就不赘叙了。

loadAnimation = AnimationUtils.loadAnimation(getActivity(),
        R.anim.base_anmation_rotate);
image.startAnimation(loadAnimation);

```

### 补间动画（Tween animation）两个常用的特殊场景

1 通过布局动画（LayoutAnimation）给 ViewGroup 的子 View 指定入场动画。
举例如下：
首先展示一下动画效果：

![](https://upload-images.jianshu.io/upload_images/2171639-43828b21d7706a65.gif)

实现代码如下：
首先通过 xml 定义 ViewGroup 的子 View 入场动画，代码如下：

```
res/anim/zoom_in.xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/decelerate_interpolator" >
  <scale
        android:duration="500"
        android:fromXScale="0.1"
        android:fromYScale="0.1"
        android:pivotX="50%"
        android:pivotY="50%"
        android:toXScale="1.0"
        android:toYScale="1.0" />
  <alpha
        android:duration="500"
        android:fromAlpha="0"
        android:toAlpha="1.0" />
</set>

```

接着为 ListView 设置布局动画（LayoutAnimation），代码如下所示：

```
LayoutAnimationController lac=new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
lac.setDelay(0.5f);
lac.setOrder(LayoutAnimationController.ORDER_RANDOM);
mListView.setLayoutAnimation(lac);
//mListView.startLayoutAnimation(); //可以通过该方法控制动画在何时播放。

```

上面是通过 java 代码来为 ListView 设置布局动画（LayoutAnimation）的，其实通过 xml 也可以为 ListView 设置布局动画（LayoutAnimation），代码如下所示：

```
<ListView
    android:id="@+id/listView"
    android:layoutAnimation="@anim/anim_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >
</ListView>

res/anim/anim_layout.xml
<?xml version="1.0" encoding="utf-8"?>
<layoutAnimation xmlns:android="http://schemas.android.com/apk/res/android"
    android:delay="0.5"
    android:animationOrder="random"
    android:animation="@anim/zoom_in"/>

```

2 通过补间动画（Tween animation）为 Activity 自定义切换动画
Android 系统为 Activity 设置了默认的切换动画，这个动画我们是可以进行自定义的。通过调用 Activity 类的 overridePendingTransition(int enterAnim, int exitAnim) 方法可以实现自定义 Activity 的切换动画，注意这个方法必须在 startActivity 和 finish 调用之后被调用，否者没有效果。

# Drawable 动画（逐帧动画）

逐帧动画是用来逐帧显示预先定义好的一组图片，类似于电影播放。对应于 AnimationDrawable 类。

```
FILE LOCATION:
    res/drawable/filename.xml
    The filename will be used as the resource ID.
COMPILED RESOURCE DATATYPE:
    Resource pointer to an AnimationDrawable.
RESOURCE REFERENCE:
    In Java: R.drawable.filename
    In XML: @[package:]drawable.filename

```

不同于补间动画，逐帧动画资源文件放在 drawable 文件夹下。
语法：

```
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot=["true" | "false"] >
    <item
        android:drawable="@[package:]drawable/drawable_resource_name"
        android:duration="integer" />
</animation-list>

```

<animation-list> 标签用来包含逐帧动画的每一帧。
<animation-list> 标签的相关属性如下所示：

```
android:oneshot
Boolean. 当设置为true时动画只会播放一次，否者会循环播放。

```

<item> 用来表示逐帧动画的每一帧图片。
<item> 标签的相关属性如下所示：

```
android:drawable
Drawable resource. 设置当前帧对应的Drawable 资源。
android:duration
Integer. 设置显示该帧的时间, 单位为毫秒（milliseconds）。

```

举例如下：
首先展示一下动画效果：

![](https://upload-images.jianshu.io/upload_images/2171639-d9b4f6a90fbdc563.gif)

实现代码如下：
首先通过 xml 定义一个逐帧动画（AnimationDrawable），代码如下：

```
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="true" >
    <item
        android:drawable="@drawable/first_pic"
        android:duration="1000"/>
    <item
        android:drawable="@drawable/second_pic"
        android:duration="1000"/>
    <item
        android:drawable="@drawable/third_pic"
        android:duration="1000"/>
    <item
        android:drawable="@drawable/fourth_pic"
        android:duration="1000"/>
    <item
        android:drawable="@drawable/fifth_pic"
        android:duration="1000"/>
    <item
        android:drawable="@drawable/sixth_pic"
        android:duration="1000"/>
</animation-list>

```

然后将上面定义的 AnimationDrawable 作为 View 的背景并且通过 AnimationDrawable 来播放动画，代码如下：

```
image.setImageResource(R.drawable.anim_list);
AnimationDrawable animationDrawable = (AnimationDrawable) image.getDrawable();
animationDrawable.start();
//animationDrawable.stop(); //如果oneshot为false，必要时要停止动画

```

上面是通过 xml 定义一个逐帧动画，也可以完全通过代码来实现和上面相同效果，如下所示：

```
//代码定义、创建、执行动画
AnimationDrawable animationDrawable = new AnimationDrawable();
animationDrawable.addFrame(getResources().getDrawable(R.drawable.first_pic), 1000);
animationDrawable.addFrame(getResources().getDrawable(R.drawable.second_pic), 1000);
animationDrawable.addFrame(getResources().getDrawable(R.drawable.third_pic), 1000);
animationDrawable.addFrame(getResources().getDrawable(R.drawable.fourth_pic), 1000);
animationDrawable.addFrame(getResources().getDrawable(R.drawable.fifth_pic), 1000);
animationDrawable.addFrame(getResources().getDrawable(R.drawable.sixth_pic), 1000);
animationDrawable.setOneShot(true);
image.setImageDrawable(animationDrawable);
animationDrawable.start();

```

注意：重要的是要注意，在 Activity 的 onCreate（）方法中不能调用 AnimationDrawable 的 start（）方法，因为 AnimationDrawable 尚未完全附加到窗口。 如果你想立即播放动画，而不需要交互，那么你可能想从 Activity 的 onWindowFocusChanged（）方法调用它，当 Android 将你的窗口聚焦时，它会被调用。

# 属性动画框架

与属性动画相比 View 动画存在一个缺陷，View 动画改变的只是 View 的显示，而没有改变 View 的响应区域，并且 View 动画只能对 View 做四种类型的补间动画，因此 Google 在 Android3.0 及其后续版本中添加了属性动画框架。同样属性动画框架还提供了动画集合类（AnimatorSet），通过动画集合类（AnimatorSet）可以将多个属性动画以组合的形式显示出来。

下面是我关于属性动画框架工作原理的总结：
顾名思义只要某个类具有属性（即该类含有某个字段的 set 和 get 方法），那么属性动画框架就可以对该类的对象进行**动画操作**（其实就是通过反射技术来获取和执行属性的 get，set 方法），因此属性动画框架可以实现 View 动画框架的所有动画效果并且还能实现 View 动画框架无法实现的动画效果。属性动画框架工作原理可以总结为如下三步：
1 在创建属性动画时如果没有设置属性的初始值，此时 Android 系统就会通过该属性的 get 方法获取初始值，所以在没有设置属性的初始值时，必须提供该属性的 get 方法，否者程序会 Crash。
2 在动画播放的过程中，属性动画框架会利用时间流逝的百分比获取属性值改变的百分比（即通过时间插值器），接着利用获取的属性值改变的百分比获取改变后的属性值（即通过类型估值器）。
3 通过该属性的 set 方法将改变后的属性值设置到对象中。

还要注意一点，虽然通过 set 方法改变了对象的属性值，但是还要将这种改变用动画的形式表现出来，否者就不会有动画效果，所以属性动画框架本身只是不断的改变对象的属性值并没有实现动画效果。

```
FILE LOCATION:
    res/animator/filename.xml
    The filename will be used as the resource ID.
COMPILED RESOURCE DATATYPE:
    Resource pointer to a ValueAnimator, ObjectAnimator, or AnimatorSet.
RESOURCE REFERENCE:
    In Java: R.animator.filename
    In XML: @[package:]animator/filename

```

语法：

```
<set
  android:ordering=["together" | "sequentially"]>

    <objectAnimator
        android:property
        android:duration="int"
        android:valueFrom="float | int | color"
        android:valueTo="float | int | color"
        android:startOffset="int"
        android:repeatCount="int"
        android:repeatMode=["repeat" | "reverse"]
        android:valueType=["intType" | "floatType"]/>

    <animator
        android:duration="int"
        android:valueFrom="float | int | color"
        android:valueTo="float | int | color"
        android:startOffset="int"
        android:repeatCount="int"
        android:repeatMode=["repeat" | "reverse"]
        android:valueType=["intType" | "floatType"]/>

    <set>
        ...
    </set>
</set>

```

在上面的语法中，<set> 对应 AnimatorSet（属性动画集合）类，<objectAnimator > 对应 ObjectAnimator 类，<animator > 标签对应 ValueAnimator 类；并且属性动画集合还可以包含属性动画集合。
<set> 标签相关属性如下所示：

```
android:ordering
该属性有如下两个可选值：
together：表示动画集合中的子动画同时播放。
sequentially：表示动画集合中的子动画按照书写的先后顺序依次播放。
该属性的默认值是together。

```

## 通过 ObjectAnimator 实现属性动画

<objectAnimator> 标签相关属性如下所示：

```
android:propertyName
String. Required. 属性动画作用的属性名称 
android:duration
int. 表示动画的周期，默认值为300毫秒
android:valueFrom
float, int, or color. 表示属性的初始值
android:valueTo
float, int, or color. Required. 表示属性的结束值
android:startOffset
int. 表示调用start方法后延迟多少毫秒开始播放动画
android:repeatCount
int. 表示动画的重复次数，-1代表无限循环，默认值为0，表示动画只播放一次。
android:repeatMode
表示动画的重复模式，该属性有如下两个可选值：
repeat：表示连续重复
reverse：表示逆向重复
android:valueType
Keyword.  表示android:propertyName所指定属性的类型，有intType和floatType两个可选项，
分别代表属性的类型为整型和浮点型，另外如果属性是颜色值，那么就不需要指定
android:valueType属性，Android系统会自动对颜色类型的属性做处理。
默认值为floatType。

```

利用 ObjectAnimator 实现与补间动画中 4 个实例相同的动画效果，代码如下：

```
//利用ObjectAnimator实现透明度动画
ObjectAnimator.ofFloat(mImageView, "alpha", 1, 0, 1)
        .setDuration(2000).start();
//利用AnimatorSet和ObjectAnimator实现缩放动画
final AnimatorSet animatorSet = new AnimatorSet();
image.setPivotX(image.getWidth()/2);
image.setPivotY(image.getHeight()/2);
animatorSet.playTogether(
        ObjectAnimator.ofFloat(image, "scaleX", 1, 0).setDuration(5000),
        ObjectAnimator.ofFloat(image, "scaleY", 1, 0).setDuration(5000));
animatorSet.start();
//利用AnimatorSet和ObjectAnimator实现平移动画
AnimatorSet animatorSet = new AnimatorSet();
animatorSet.playTogether(
        ObjectAnimator.ofFloat(image, "translationX", 20, 100).setDuration(2000),
        ObjectAnimator.ofFloat(image, "translationY", 20, 100).setDuration(2000));
animatorSet.start();
//利用ObjectAnimator实现旋转动画
image.setPivotX(image.getWidth()/2);
image.setPivotY(image.getHeight()/2);
ObjectAnimator.ofFloat(image, "rotation", 0, 360)
        .setDuration(1000).start();

```

上面是通过代码的形式实现的属性动画，对于通过 xml 定义属性动画的方式不是很常用，因为属性的起始和结束值大多是在程序运行的时候动态获取的。大家可以根据上面的语法自己尝试一下。

## 通过 ValueAnimator 实现属性动画

<animator> 标签相关属性如下所示：

```
与<objectAnimator>标签相比，除了没有android:propertyName属性和valueFrom 属性是Required的之外，
其他的属性都相同并且属性的作用也一样。

```

其实 ValueAnimator 类就是一个数值生成器，也就是没有上面关于**属性动画框架工作原理的第 1 步和第 3 步**，ObjectAnimator 作为 ValueAnimator 的子类，实现了这两步。你只要给 ValueAnimator 提供一个初始值、结束值和周期时间，ValueAnimator 就会按照**属性动画框架工作原理的第 2 步**中的步骤生成具有一定规则的数字。

## 无法使用属性动画或者属性动画不起作用的情况和解决方法

无法使用属性动画或者属性动画不起作用的情况如下：
1 该字段没有没有 set 和 get 方法
2 该属性的 set 方法仅仅改变了对象的属性值，但是没有将这种改变用动画的形式表现出来
解决方法如下：
1 如果你又权限的话，给这个字段添加 get 和 set 方法，比如在自定义 View 中。
2 使用一个包装类来封装该字段对应的类，间接为该字段提供 get 和 set 方法。
例如使 ImageView 从当前高度变化到 600，代码如下所示：

```
ViewWrapper viewWrapper = new ViewWrapper(mImageView);
ObjectAnimator.ofInt(viewWrapper, "height", 600).setDuration(5000).start();

public class ViewWrapper {

    private View view;

    public ViewWrapper(View view) {
        this.view = view;
    }

    public int getHeight(){
        return view.getLayoutParams().height;
    }

    public void setHeight(int height){
        view.getLayoutParams().height = height;
        view.requestLayout();
    }
}

```

3 通过 ValueAnimator 实现动画效果
举例如下：
首先展示一下运行的效果：

![](https://upload-images.jianshu.io/upload_images/2171639-cabbf66cc2ae5f89.gif)

实现代码如下：

```
if (mBtHiddenView.getVisibility() == View.GONE) {
    animateOPen(mBtHiddenView);
} else {
    animateClose(mBtHiddenView);
}

private void animateOPen(final View view) {
    view.setVisibility(View.VISIBLE);
    ValueAnimator valueAnimator = createDropAnimator(view, 0, mHiddenViewHeight);
    valueAnimator.setInterpolator(new AccelerateInterpolator());
    valueAnimator.setDuration(1000).start();
}

private void animateClose(final View view) {
    ValueAnimator valueAnimator = createDropAnimator(view, mHiddenViewHeight, 0);
    valueAnimator.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            // TODO Auto-generated method stub
            super.onAnimationEnd(animation);
            view.setVisibility(View.GONE);
        }
    });
    valueAnimator.setInterpolator(new DecelerateInterpolator());
    valueAnimator.setDuration(1000).start();
}

private ValueAnimator createDropAnimator(final View view, int start, int end) {
    ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // TODO Auto-generated method stub
            LayoutParams params = view.getLayoutParams();
            params.height = (int) animation.getAnimatedValue();
            view.setLayoutParams(params);
        }
    });
    return valueAnimator;
}

```

## 属性动画常用的特殊场景

1> 属性动画可以为 ViewGroup 的子 View 的显示和隐藏设置过渡动画。Android 系统中已经提供了默认过渡动画（在 layout 文件中将 ViewGroup 的 animateLayoutChanges 属性打开就可以使用系统提供的默认过渡动画）。Android 系统中一共提供了如下所示的 4 种类型的过渡动画：

```
APPEARING 
当通过 设置子View的可见性为VISIBLE或者通过addView方法添加子View 来显示子View时，
子View就会执行该类型的动画。
该类型动画的周期为300毫秒，默认延迟为300毫秒。
DISAPPEARING
当通过 设置子View的可见性为GONE或者通过removeView方法移除子View 来隐藏子View时，
子View就会执行该类型的动画。
该类型动画的周期为300毫秒，默认延迟为0毫秒。
CHANGE_APPEARING
当显示子View时，所有的兄弟View就会立即依次执行该类型动画并且兄弟View之间执行动画的间隙默认为0毫秒，然后才会执行显示子View的动画。
该类型动画的周期为300毫秒，默认延迟为0毫秒。
CHANGE_DISAPPEARING
当隐藏子View的动画执行完毕后，所有的兄弟View就会依次执行该类型动画并且兄弟View之间执行动画的间隙默认为0毫秒。
该类型动画的周期为300毫秒，默认延迟为300毫秒。

注意 上面描述的都是系统默认的行为，我们可以做适当的改变。

```

如果感觉 Android 系统提供的过渡动画不够炫，你也可以自定义过渡动画，下面就举例说明一下如何自定义过渡动画，首先展示一下效果图：

![](https://upload-images.jianshu.io/upload_images/2171639-069e9e05620c422e.gif)

布局相关代码如下所示：

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:orientation="vertical">
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">
    <Button
        android:id="@+id/btn_add_image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="添加图片" />
    <Button
        android:id="@+id/btn_remove_image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="移除图片" />
</LinearLayout>
<LinearLayout
    android:id="@+id/ll_image"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="24dp"
    android:gravity="center_horizontal"
    android:animateLayoutChanges="true"
    android:orientation="vertical"/>
</LinearLayout>

```

在 java 代码中为 id 为 ll_image 的 LinearLayout 设置自定义的过渡动画，如下所示：

```
llImageView = (LinearLayout) root.findViewById(R.id.ll_image);

LayoutTransition transition = new LayoutTransition();

transition.setStagger(LayoutTransition.CHANGE_APPEARING, 30);
transition.setDuration(LayoutTransition.CHANGE_APPEARING, transition.getDuration(LayoutTransition.CHANGE_APPEARING));
transition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);

ObjectAnimator appearingAnimator = ObjectAnimator
        .ofPropertyValuesHolder(
                (Object) null,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("alpha", 0, 1.0f));
transition.setAnimator(LayoutTransition.APPEARING, appearingAnimator);
transition.setDuration(LayoutTransition.APPEARING, transition.getDuration(LayoutTransition.APPEARING));
transition.setStartDelay(LayoutTransition.APPEARING, transition.getDuration(LayoutTransition.CHANGE_APPEARING));

ObjectAnimator disappearingAnimator = ObjectAnimator
        .ofPropertyValuesHolder(
                (Object) null,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f),
                PropertyValuesHolder.ofFloat("alpha", 1.0f, 0));
transition.setAnimator(LayoutTransition.DISAPPEARING, disappearingAnimator);
transition.setDuration(LayoutTransition.DISAPPEARING, transition.getDuration(LayoutTransition.DISAPPEARING));
transition.setStartDelay(LayoutTransition.DISAPPEARING, 0);

transition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 30);
transition.setDuration(LayoutTransition.CHANGE_DISAPPEARING, transition.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, transition.getDuration(LayoutTransition.DISAPPEARING));

llImageView.setLayoutTransition(transition);

```

上面代码依次对 CHANGE_APPEARING、APPEARING、DISAPPEARING 和 CHANGE_DISAPPEARING 类型的过渡动画进行了设置，下面就来分析常用的设置方法：

```
setStagger方法
当多个子View要执行同一个类型的动画时，就可以通过该方法来设置子View之间执行动画的间隙，
默认为0毫秒。

setAnimator方法
为指定类型的过渡动画设置自定义的属性动画。

setDuration方法
为指定类型的过渡动画设置执行动画的周期，默认为300毫秒。

setStartDelay方法
为指定类型的过渡动画设置延迟执行的时间，默认与过渡动画的类型相关，上面已经说过。

setLayoutTransition方法
为ViewGroup设置过渡动画。

```

经过上面代码的设置后，LinearLayout 显示或者隐藏子 View 时就会执行相关的过渡动画，显示或者隐藏子 View 的代码如下所示：

```
@Override
public void onClick(View v) {
    switch (v.getId()) {
        case R.id.btn_add_image:{
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.second_pic);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200,200);
            llImageView.addView(imageView, 0, layoutParams);
        }
            break;
        case R.id.btn_remove_image:{
            int count = llImageView.getChildCount();
            if (count > 0) {
                llImageView.removeViewAt(0);
            }
        }
            break;
    }
}

```

2> Vector(矢量图) 中的动画
可以参考我的另外一篇 blog [Android 中的矢量图](https://www.jianshu.com/p/677da5076115)