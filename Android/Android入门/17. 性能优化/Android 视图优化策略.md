> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5b9e61c7e51d450e41153cdd

> 本文首发于微信公众号「玉刚说」
> 
> 原文链接：[那些 Android 程序员必会的视图优化策略](https://link.juejin.im?target=https%3A%2F%2Fmp.weixin.qq.com%2Fs%2Fep-Assy2j_EOUW8uWUQfSQ)

## 1\. 概述

现在的 APP 一些视觉效果都很炫，往往在一个界面上堆叠了很多视图，这很容易出现一些性能的问题，严重的话甚至会造成卡顿。因此，我们在开发时必须要平衡好设计效果和性能的问题。

本文主要讲解如何对视图和布局进行优化：包括如何避免过度绘制，如何减少布局的层级，如何使用 ConstraintLayout 等等。

## 2\. 过度绘制 (Overdraw)

### 2.1 什么是过度绘制？

过度绘制 (Overdraw) 指的是屏幕上的某个像素在同一帧的时间内被绘制了多次。

举个例子: 在多层次的 UI 结构里面，如果不可见的 UI 也进行绘制操作，那么就会造成某些像素区域被绘制了多次。这会浪费大量的 CPU 以及 GPU 资源。这是我们需要避免的。

### 2.2 如何检测过度绘制

Android 手机上面的开发者选项提供了工具来检测过度绘制，可以按如下步骤来打开：

> 开发者选项 -> 调试 GPU 过度绘制 -> 显示过度绘制区域

如下图所示：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adc71d9e4d2?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)显示过度绘制区域. png

可以看到，界面上出现了一堆红绿蓝的区域，我们来看下这些区域代表什么意思：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adc66d3676d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)overdraw.png

需要注意的是，有些过度绘制是无法避免的。因此在优化界面时，应该尽量让大部分的界面显示为真彩色（即无过度绘制）或者为蓝色（仅有 1 次过度绘制）。尽量避免出现粉色或者红色。

### 2.3 过度绘制优化

可以采取以下方案来减少过度绘制：

> 1\. 移除布局中不需要的背景
> 2\. 将 layout 层级扁平化
> 3\. 减少透明度的使用

#### 2.3.1 移除布局中不需要的背景

一些布局中的背景由于被该视图上所绘制的内容完全覆盖掉，因此这个背景实际上多余的，如果没有移除这个背景的话，将会产生过度绘制。我们可以使用以下方案来移除布局中不需要的背景：

> 1\. 移除 Window 默认的 Background
> 2\. 移除控件中不需要的背景

##### 2.3.1.1 移除 Window 默认的 Background

通常，我们使用的`theme`都会包含了一个`windowBackground`，比如某个`theme`的如下：

```
 <item >@color/background_material_light</item>复制代码
```

然后，我们一般会给布局一个背景，比如:

```
<android.support.constraint.ConstraintLayout    ...    android:background="@mipmap/bg">复制代码
```

这就导致了整个页面都会多了一次绘制。

那么其解决办法也很简单，把`windowBackground`移除掉就可以了，有以下两种方法来解决, 随便使用其中一种即可：

1\. 在`theme`中设置

```
    <style >        <item >@null</item>    </style>复制代码
```

2\. 在`Activity`的`onCreate()`方法中添加：

```
    getWindow().setBackgroundDrawable(null);复制代码
```

直接来看下优化前后的对比图：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adc74a191f7?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)移除 Window 默认的 Background.png

优化前，由于需要绘制 windowBackground 以及布局的 background, 即有 1 次过度绘制，因此整个界面是蓝色的，同时 hello world 文字部分再进行了一次绘制，所以变绿了。

优化后，由于不需要绘制 windowBackground，仅仅只需要绘制布局的 background，因此整个界面显示的是原本的真彩色。文字部分再进行一次绘制，也只是蓝色而已。

##### 2.3.1.2 移除控件中不需要的背景

下面先来看个例子，根布局`LinearLayout`设置了一个背景，然后它的子控件 3 个`TextView`中有两个设置了同样的背景，布局如下所示：

```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"              android:layout_width="match_parent"              android:layout_height="match_parent"              android:background="#ffffffff"              android:orientation="vertical">    <TextView        android:layout_width="wrap_content"        android:layout_height="wrap_content"        android:background="#ffffffff"        android:text="test0"/>    <TextView        android:layout_width="wrap_content"        android:layout_height="wrap_content"        android:text="test1"/>    <TextView        android:layout_width="wrap_content"        android:layout_height="wrap_content"        android:background="#ffffffff"        android:text="test2"/></LinearLayout>复制代码
```

其显示结果如下：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adc67da0ee9?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)移除控件中不需要的背景. png

可以看到，2 个使用了跟父布局同样背景的 TextView 会导致了一次过度绘制。

那么，我们平时只需要遵循以下两个原则就可以减少次过度绘制：

> 1\. 对于子控件，如果其背景颜色跟父布局一致，那么就不用再给子控件添加背景了。
> 2\. 如果子控件背景五颜六色，且能够完全覆盖父布局，那么父布局就可以不用添加背景了。

#### 2.3.2 将 layout 层级扁平化

往往我们在写界面的时候都会使用基本布局来实现，这可能会出现一些性能问题。比如：使用嵌套的`LinearLayout`可能会导致布局的层次结构变得过深。另外，如果在`LinearLayout`中使用了`layout_weight`的话，那么他的每一个子 `view`都需要测量两次。特别是用在 `ListView` 和 `GridView` 时，他们会被反复测量。

布局嵌套过多的话会导致过度绘制，从而降低性能，因此我们需要将布局的层次结构尽量扁平化。

##### 2.3.2.1 使用 Layout Inspector 去查看 layout 的层次结构

之前的 Android SDK 工具包含了一个名为`Hierarchy Viewer`的工具，可以在应用运行时分析布局。但是在 Android Studio 3.1 之后，`Hierarchy Viewer`就给移除掉了。并且 Android 的团队表示不再开发`Hierarchy Viewer`。所以这里就不介绍`Hierarchy Viewer`。

这里使用 Android 推荐的`Layout Inspector`来查看 layout 的层次结构。

在 Android Studio 中点击`Tools > Android > Layout Inspector`。然后在出现的 `Choose Process` 对话框中，选择想要检查的应用进程即可。

`Layout Inspector`会自动捕获快照，然后会显示以下内容：

*   View Tree：视图在布局中的层次结构。
*   Screenshot：每个视图可视边界的设备屏幕截图。
*   Properties Table：选定视图的布局属性。

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adc82a58198?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)layout-inspector.png

通过左侧`View Tree`即可看到布局中的层次结构。

> 偷偷提一句，`Layout Inspector`也可以用来分析别人 APP 的布局。

##### 2.3.2.2 使用嵌套少的布局

假如要实现以下布局：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adc80ca0afe?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)layout-listitem.png
我们可以使用`LinearLayout`和`RelativeLayout`来完成。但是`LinearLayout`相比于`RelativeLayout`，就多了一层，所以`RelativeLayout`明显是一个更优的选择。如下图所示：
![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adc8963fdc9?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)过度嵌套 LinearLayout.png

所以，合理选择不同的布局能够减少嵌套。

##### 2.3.2.3 使用 merge 标签减少嵌套

通过`<include>`标签能够复用布局。

比如，我们要复用如下的一个布局, 一个垂直的线性布局包含一个`ImageView`和`TextView`，其布局文件`layout_include.xml`如下：

```
<LinearLayout    xmlns:android="http://schemas.android.com/apk/res/android"    android:layout_width="wrap_content"    android:layout_height="wrap_content"    android:orientation="vertical">    <ImageView        ...        />    <TextView        ...        /></LinearLayout>复制代码
```

然后我们就可以通过`<include>`来复用这个布局了，其布局文件`activity_include.xml`如下：

```
<LinearLayout    xmlns:android="http://schemas.android.com/apk/res/android"    android:layout_width="match_parent"    android:layout_height="match_parent"    android:background="#fff"    android:orientation="vertical">    <include layout="@layout/layout_include"/>    <include layout="@layout/layout_include"/>    <include layout="@layout/layout_include"/></LinearLayout>复制代码
```

但是上面这个例子会有个问题：其父布局是垂直的线性布局，`include`进来的也是垂直的线性布局，这就会造成了布局嵌套，而且这种嵌套是没必要的，那么就可以使用`<merge>`标签来减少这种嵌套。将`layout_include.xml`改成以下即可：

```
<merge    xmlns:android="http://schemas.android.com/apk/res/android"    android:layout_width="wrap_content"    android:layout_height="wrap_content">    <ImageView        ...        />    <TextView        ...        /></merge>复制代码
```

我们可以用`Layout Inspector`来看下使用`<merge>`标签优化前后的布局层次结构：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adceee3c6a7?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)使用 merge 标签减少嵌套. png

##### 2.3.2.4 使用 lint 来优化布局的层次结构

`lint`是一个静态代码分析工具，可以用来协助优化布局的性能。要使用`lint`，点击`Analyze`> `Inspect Code`即可，如下图所示：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adc8998bd56?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)lint-inspect-code.png
布局性能方面的信息位于`Android`> `Lint`> `Performance`下，我们可以点开它来看下一些优化建议。
![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adca8d1f88a?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)lint-display.png

下面是 lint 的一些优化技巧：

1.  使用复合图片
    如果一个线性布局中包含一个 `ImageView` 和一个 `TextView`，可以使用复合图片来替换掉

2.  合并根节点
    如果一个`FrameLayout` 是整个布局的根节点，并且也没有提供背景、留白等等，那么可以使用`<merge>`标签来替换掉，因为`DecorView`本身就是一个`FrameLayout`。

3.  移除布局中无用的叶子
    布局是一个树形的结构，如果一个布局没有子 `View` 或者背景，那么可以把它移除掉（这布局本身就不可见了）。

4.  移除无用的父布局
    如果一个布局没有兄弟，也不是`ScrollView` 或者根 `View`，并且也没有背景，那么可以把这个父布局移除掉，然后把它的子`view`移到它的父布局下。

5.  避免过深的层次结构
    过多的布局嵌套不利于性能，可以使用更扁平化的布局，如`RelativeLayout`、`GridLayout`、`ConstraintLayout`等布局来提高性能。布局默认的最大深度为 10。

> `lint`的功能其实很强大，可以用来检测优化各个方面，平时我们遇到 lint 的一些警告，能修复优化的话就尽量去完善掉。

#### 2.3.3 减少透明度的使用

对于不透明的`view`，只需要渲染一次即可把它显示出来。但是如果这个`view`设置了`alpha`值，则至少需要渲染两次。这是因为使用了`alpha`的`view`需要先知道混合`view`的下一层元素是什么，然后再结合上层的`view`进行 Blend 混色处理。透明动画、淡入淡出和阴影等效果都涉及到某种透明度，这就会造成了过度绘制。可以通过减少渲染这些透明对象来改善过度绘制。比如：在`TextView`上设置带透明度`alpha`值的黑色文本可以实现灰色的效果。但是，直接通过设置灰色的话能够获得更好的性能。

#### 2.3.4 减少自定义 View 的过度绘制, 使用 clipRect()

下面我们自定义一个 View 用来显示多张重叠的表情包，效果图如下：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adcc966e075?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)自定义 view_1.png
其`onDraw()`方法也很简单，就是遍历所有表情包，然后绘制出来：

```
    @Override    protected void onDraw(Canvas canvas) {        super.onDraw(canvas);        for (int i = 0; i < imgs.length; i++) {            canvas.drawBitmap(imgs[i], i * 100, 0, mPaint);        }    }复制代码
```

显示过度绘制区域：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adccbe41121?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)自定义 view_2.png
五颜六色的，过度绘制比较严重，那么如何解决？

我们先来分析一下为什么会出现过度绘制：以第一张图为例，上面的代码会把整张图都绘制出来了，第二张在第一张上面继续绘制，这就造成了过度绘制。

那么，解决办法也很简单，对于前面的 n-1 张图，我们只需要绘制一部分即可，对于最后一张才绘制完整的。

`Canvas`中的`clipRect()`方法能够设置一个裁剪矩形，只在这个矩形区域内的内容才能够绘制出来。

优化后的代码如下：

```
    protected void onDraw(Canvas canvas) {        super.onDraw(canvas);        for (int i = 0; i < imgs.length; i++) {            canvas.save();            if (i < imgs.length - 1) {                //前面的n-1张图，只裁剪一部分                canvas.clipRect(i * 100, 0, (i + 1) * 100, imgs[i].getHeight());            } else if (i == imgs.length - 1) {                //最后一张，完整的                canvas.clipRect(i * 100, 0, i * 100 + imgs[i].getWidth(), imgs[i].getHeight());            }            canvas.drawBitmap(imgs[i], i * 100, 0, mPaint);            canvas.restore();        }复制代码
```

优化后的效果图如下：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adcd78bd7d0?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)自定义 view_3.png
所有区域都是蓝色的，即只有 1 次过度绘制。

`Canvas`除了`clipRect()`方法外，还有`clipPath()`等方法，优化时选择合理的方法去裁剪即可。

## 3\. 一些布局优化技巧

除了避免过度绘制之外，还有一些其他的优化技巧能够帮我们提升性能。这里简单介绍一下一些比较常用的技巧。

### 3.1 使用性能更优的布局

1.  在无嵌套布局的情况下，`FrameLayout`和`LinearLayout`的性能比`RelativeLayout`更好。因为`RelativeLayout`会测量每个子节点两次。
2.  `ConstraintLayout`的性能比`RelativeLayout`更好，推荐使用`ConstraintLayout`。后面会介绍`ConstraintLayout`的使用。

### 3.2 使用 include 标签提高布局的复用性

使用`<include>`标签提取布局的公用部分，能够提高布局的复用性。具体例子这里就不写了，可以回头看看`<merge>`标签那一小节的例子。

### 3.3 使用 ViewStub 标签延迟加载

在项目中，有些复杂的布局很少使用到，比如进度指示器等等。那么我们可以通过`<ViewStub>`标签来实现在需要时才加载布局。使用`<ViewStub>`能够减少内存的使用并且加快渲染速度。

`ViewStub`是一个轻量级的视图，它没有尺寸，也不会绘制任何内容和参与布局。下面是一个`ViewStub`的例子：

```
<ViewStub    android:id="@+id/stub_import"    android:inflatedId="@+id/panel_import"    android:layout="@layout/progress_overlay"    android:layout_width="fill_parent"    android:layout_height="wrap_content"    android:layout_gravity="bottom" />复制代码
```

这里的`panel_import`就是具体要加载的布局 ID。

通过以下代码即可在需要时加载布局：

```
findViewById(R.id.stub_import)).setVisibility(View.VISIBLE);或者View importPanel = ((ViewStub) findViewById(R.id.stub_import)).inflate();复制代码
```

一旦布局加载后，`ViewStub`就不再是原来布局的一部分了，它会被新加载进来的布局替换掉。需要注意的是，`ViewStub`不支持`<merge>`标签。

### 3.4 onDraw() 中不要创建新的局部变量以及不要做耗时操作

1.  `onDraw()`中不要创建新的局部变量，因为`onDraw()`方法可能会被频繁调用，大量的临时对象会导致内存抖动，会造成频繁的 GC，从而使 UI 线程被频繁阻塞，导致画面卡顿。
2.  Android 要求每帧的绘制时间不超过 16ms, 在`onDraw()`进化耗时操作的话，轻则掉帧，严重的话会造成卡顿。

### 3.5 使用 GPU 呈现模式分析工具来分析渲染速度

点击开发者模式 -> 监控 ->GPU 呈现模式，然后选择 在屏幕上显示为条形图 即可以看到一个图表。

如下图所示：

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adcf3678085?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)GPU 呈现模式分析. png
上图中，主要包含了以下信息：

> 1\. 沿水平轴的每个竖条都代表一个帧，每个竖条的高度表示渲染该帧所花的时间（单位：毫秒）。
> 2\. 水平绿线表示 16 毫秒。 要实现每秒 60 帧，代表每个帧的竖条需要保持在此线以下。 当竖条超出此线时，可能会使动画出现暂停。

再来看下每个竖条的颜色代表什么意思：
注意：这是在 Android6.0 以上才有的颜色，6.0 以下只有 3、4 种，所以建议使用 6.0 以上的设备来查看。

![](https://user-gold-cdn.xitu.io/2018/9/16/165e2adcd790c925?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)用 GPU 呈现模式分析 - 区段说明. png

如果存在一大段的竖条都超过了绿线，则我们可以去分析是哪个阶段的时间花费比较多，然后针对性的去优化。

## 4\. 使用 ConstraintLayout

ConstraintLayout 是 Android 新推出的一个布局，其性能更好，连官方的 hello world 都用 ConstraintLayout 来写了。所以极力推荐使用 ConstraintLayout 来编写布局。

ConstraintLayout，可以翻译为约束布局，在 2016 年 Google I/O 大会上发布。我们知道，当布局嵌套过多时会出现一些性能问题。之前我们可以去通过 RelativeLayout 或者 GridLayout 来减少这种布局嵌套的问题。现在，我们可以改用 ConstraintLayout 来减少布局的层级结构。ConstraintLayout 相比 RelativeLayout，其性能更好，也更容易使用，结合 Android Studio 的布局编辑器可以实现拖拽控件来编写布局等等。

![](https://user-gold-cdn.xitu.io/2018/6/6/163d3319859594d4?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)欢迎关注我的微信公众号「玉刚说」，接收第一手技术干货