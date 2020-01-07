> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5d0c46246fb9a07ec9561573?utm_source=gold_browser_extension

## ConstraintHelper

ConstraintLayout 在 1.0 的时候提供了 GuideLine 辅助布局，在 1.1 时提供了 Group 和 Barrier，在 2.0 时候提供了 Layer 以及放开了限制，开发者可以自定义 Helper 了。

### Group (Added in 1.1)

Group 可以用来控制一组 view 的可见性

<pre>    <android.support.constraint.Group
              android:id="@+id/group"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:visibility="visible"
              app:constraint_referenced_ids="button4,button9" />
复制代码
</pre>

可以通过控制 group 的 hide/show 来直接控制一组 view(button4,button9) 的可见性。

### Barrier (Added in 1.1)

来看一个场景，下面是一个表单，Email 和 Password 左对齐 中间的虚线为 GuideLine，具体字段都和 GuideLine 左对齐。

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b1b495483?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

现在如果需要做多语言，翻译为德文后变成了下面的效果

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b1b5f6c14?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

这时候就需要 Barrier 出场了，Barrier 是栅栏的意思，可以理解为挡着不让超过。

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b1c7a4a19?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

改进方法

*   把中间的虚线 GuideLine 换成 Barrier
*   把①和②加入到 Barrier 的 referenced_ids 中
*   指定 barrierDirection 为 right（右侧不超过）
*   把③和④左边对齐到 Barrier 的右边

这样 Email 和 Password 就不会超出 Barrier，大致代码如下 (有删减，[完整代码参考这里](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Ffeng0403%2FConstraintLayoutSamples%2Fblob%2Fmaster%2Fapp%2Fsrc%2Fmain%2Fres%2Flayout%2Factivity_main3.xml))

<pre><?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout>
    <TextView
        android:id="@+id/tv_email"
        app:layout_constraintBottom_toTopOf="@+id/tv_password"
        app:layout_constraintStart_toStartOf="@+id/tv_password"
        app:layout_constraintTop_toTopOf="parent"
        tools:text="E-mail Addresse" />

    <EditText
        android:id="@+id/et_email"
        android:text="me@gmail.com"
        app:layout_constraintBaseline_toBaselineOf="@+id/tv_email"
        app:layout_constraintStart_toEndOf="@+id/barrier" />

    <TextView
        android:id="@+id/tv_password"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/tv_email" />

    <EditText
        android:id="@+id/et_password"
        android:inputType="textPassword"
        android:text="2321321"
        app:layout_constraintBaseline_toBaselineOf="@+id/tv_password"
        app:layout_constraintStart_toEndOf="@+id/barrier" />

    <android.support.constraint.Barrier
        android:id="@+id/barrier"
        app:barrierDirection="right"
        app:constraint_referenced_ids="tv_email,tv_password" />

</android.support.constraint.ConstraintLayout>
复制代码
</pre>

### Layer (Added in 2.0)

Layer 可以看作是它引用的 view 的边界（可以理解为包含这些 view 的一个 ViewGroup，但是 Layer 并不是 ViewGroup，Layer 并不会增加 view 的层级）。另外 Layer 支持对里面的 view 一起做变换。

考虑这么一个场景，如果一个页面里面有部分 view 需要加个背景，使用 Layer 引用这几个 view，然后给 Layer 设置背景就可以了。如果不用 Layer，只能另外加个 ViewGroup 包住这几个 View 了，这样会增加 view 的层级，不利于性能。

看一个示例（[完整代码](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Ffeng0403%2FConstraintLayoutSamples%2Fblob%2Fmaster%2Fapp%2Fsrc%2Fmain%2Fjava%2Fcn%2Ffeng%2Fconstraintlayoutsample%2Factivity%2FLayerActivity.kt)）:

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b1df0f985?imageslim)

图中 Layer 包住了中间的 6 个按钮，绿色边线白色填充是通过 Layer 设置背景完成的。另外对 Layer 里面的所有按钮一起做动画，出来的效果就是这样子

ConstraintLayout2.0 除了提供几个默认实现的 ConstraintHelper 外，还提供开发者自定义 ConstraintHelper 的方式。

### 自定义 Helper

为什么需要自定义?

*   保持 view 的层级不变，不像 ViewGroup 会增加 view 的层级
*   封装一些特定的行为，方便复用
*   一个 View 可以被多个 Helper 引用，可以很方便组合出一些复杂的效果出来

如何自定义?

*   Helper 持有 view 的引用，所以可以获取 view (getViews) 然后操作 view
*   提供了 onLayout 前后的 callback（updatePreLayout/updatePreLayout）
*   Helper 继承了 view，所以 Helper 本身也是 view

#### CircularRevealHelper

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b1e037ba5?imageslim)

对一张图片作出 CircularReveal 的效果 ViewAnimationUtils 给我们提供了 createCircularReveal 这个函数

<pre>public static Animator createCircularReveal(View view,
            int centerX,  int centerY, float startRadius, float endRadius) 
复制代码
</pre>

借助这个函数只需要计算出中心点（centerX,centerY）和 endRadius（半径）就可以很方便实现 CircularReveal 的效果

<pre>class CircularRevealHelper @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintHelper(context, attrs, defStyleAttr) {

    override fun updatePostLayout(container: ConstraintLayout) {
        super.updatePostLayout(container)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val views = getViews(container)
            for (view in views) {
                val anim = ViewAnimationUtils.createCircularReveal(view, view.width / 2,
                        view.height / 2, 0f,
                        Math.hypot((view.height / 2).toDouble(), (view.width / 2).toDouble()).toFloat())
                anim.duration = 3000
                anim.start()
            }
        }
    }
}
复制代码
</pre>

updatePostLayout 会在 onLayout 之后调用，在这里做动画就可以。

有了 CircularRevealHelper 之后可以直接在 xml 里面使用, 在 CircularRevealHelper 的 constraint_referenced_ids 里面指定需要做动画 view。

<pre><?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/img_mario"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:srcCompat="@drawable/mario" />

    <cn.feng.constraintLayout2.helps.CircularRevealHelper
        android:id="@+id/helper"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:constraint_referenced_ids="img_mario"
        tools:ignore="MissingConstraints" />

</android.support.constraint.ConstraintLayout>
复制代码
</pre>

后面如果要对 view 做 CircularReveal 直接在 xml 里面指定就可以了，做到了很好的复用。

#### FlyinHelper

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b1d3586bd?imageslim)

再来看看这个 Flyin 的飞入效果，view 从四周飞入到各自的位置。

这个动画的关键在于计算出每个 view 该从什么方向飞入。

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b50a10938?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

红色边框的位置可以借助前面介绍的的 Layer 找到（当然也可以[不借助 Layer，自己算](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Ffeng0403%2FConstraintLayoutSamples%2Fblob%2Fmaster%2Fapp%2Fsrc%2Fmain%2Fjava%2Fcn%2Ffeng%2Fconstraintlayoutsample%2Fhelps%2FFlyinHelper.kt)，稍显复杂），从而计算出红色框框部分的中间点位置， 再和图中每个 view 的中间点比较（图中每个白点的位置）从而得出每个 view 该从哪个方向飞入。

计算每个 view 的初始位置代码如下，借助上面的图形应该很好理解。

<pre>    for (view in views) {

            val viewCenterX = (view.left + view.right) / 2
            val viewCenterY = (view.top + view.bottom) / 2

            val startTranslationX = if (viewCenterX < centerPoint.x) -2000f else 2000f
            val startTranslationY = if (viewCenterY < centerPoint.y) -2000f else 2000f

            view.translationX = (1 - animatedFraction) * startTranslationX
            view.translationY = (1 - animatedFraction) * startTranslationY
        }
复制代码
</pre>

FlyinHelper 的完整代码[参考这里](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Ffeng0403%2FConstraintLayoutSamples%2Fblob%2Fmaster%2Fapp%2Fsrc%2Fmain%2Fjava%2Fcn%2Ffeng%2Fconstraintlayoutsample%2Fhelps%2FFlyin2Helper.kt)

#### ComposeMultipleHelper

每个 view 不但可以接受一个 ConstraintHelper，还可以同时接受多个 ConstraintHelper。

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b540a1cf8?imageslim)

左边的四个 ImageView 和右下的 FloatingActionButton 都有 Flyin 的效果，同时左边的四个 ImageView 还在绕 Y 轴做 3D 旋转。上方的 Seekbar 的背景在做 CircularReveal 的效果。有了前面编写的 CircularRevealHelper 以及 FlyInHelper 我们可以很方便做到这样的效果。

[代码参考这里](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Ffeng0403%2FConstraintLayoutSamples%2Fblob%2Fmaster%2Fapp%2Fsrc%2Fmain%2Fres%2Flayout%2Flayout_multiple_helper.xml)

## Flow (VirtualLayout)

Flow 是 VirtualLayout，Flow 可以像 Chain 那样帮助快速横向 / 纵向布局 constraint_referenced_ids 里面的元素。 通过 flow_wrapMode 可以指定具体的排列方式, 有三种模式

*   wrap none : 简单地把 constraint_referenced_ids 里面的元素组成 chain, 即使空间不够

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b5693d337?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

*   wrap chain : 根据空间的大小和元素的大小组成一条或者多条 chain

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b5a39cb1d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

*   wrap aligned : wrap chain 类似，但是会对齐

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b6c30dca9?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

下面看下如何实现这个计算器布局：

![](https://user-gold-cdn.xitu.io/2019/6/20/16b7364b601f1a6c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

<pre><?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".activity.MainActivity">

    <android.support.constraint.helper.Flow
        android:id="@+id/flow"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#FFC107"
        android:padding="20dp"
        app:constraint_referenced_ids="tv_num_7,tv_num_8,tv_num_9,tv_num_4,tv_num_5,tv_num_6,tv_num_1,tv_num_2,tv_num_3,tv_num_0,tv_operator_div,tv_dot,tv_operator_times"
        app:flow_horizontalGap="10dp"
        app:flow_maxElementsWrap="3"
        app:flow_verticalGap="10dp"
        app:flow_wrapMode="aligned"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <TextView
        android:id="@+id/tv_num_7"
        style="@style/text_view_style"
        android:text="7" />

    <TextView
        android:id="@+id/tv_num_8"
        style="@style/text_view_style"
        android:text="8" />

    <TextView
        android:id="@+id/tv_num_9"
        style="@style/text_view_style"
        android:text="9" />

    <TextView
        android:id="@+id/tv_num_4"
        style="@style/text_view_style"
        android:text="4" />

    <TextView
        android:id="@+id/tv_num_5"
        style="@style/text_view_style"
        android:text="5" />

    <TextView
        android:id="@+id/tv_num_6"
        style="@style/text_view_style"
        android:text="6" />

    <TextView
        android:id="@+id/tv_num_1"
        style="@style/text_view_style"
        android:text="1" />

    <TextView
        android:id="@+id/tv_num_2"
        style="@style/text_view_style"
        android:text="2" />

    <TextView
        android:id="@+id/tv_num_3"
        style="@style/text_view_style"
        android:text="3" />

    <TextView
        android:id="@+id/tv_num_0"
        style="@style/text_view_style"
        android:text="0" />

    <TextView
        android:id="@+id/tv_operator_div"
        style="@style/text_view_style"
        android:text="/"
        tools:layout_editor_absoluteX="156dp"
        tools:layout_editor_absoluteY="501dp" />

    <TextView
        android:id="@+id/tv_operator_times"
        style="@style/text_view_style"
        android:text="*" />

    <TextView
        android:id="@+id/tv_dot"
        style="@style/text_view_style"
        android:text="."
        tools:layout_editor_absoluteX="278dp"
        tools:layout_editor_absoluteY="501dp" />

    <TextView
        android:id="@+id/KE"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="#00BCD4"
        android:gravity="center"
        android:text="Compute"
        android:textColor="@android:color/white"
        android:textSize="24sp"
        app:layout_constraintBottom_toBottomOf="@+id/tv_operator_times"
        app:layout_constraintEnd_toEndOf="@+id/tv_dot"
        app:layout_constraintHorizontal_bias="1.0"
        app:layout_constraintStart_toStartOf="@+id/tv_operator_div"
        app:layout_constraintTop_toTopOf="@+id/tv_operator_times" />

    <TextView
        android:id="@+id/KR"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="#03A9F4"
        android:gravity="right|center_vertical"
        android:paddingEnd="16dp"
        android:text="0"
        android:textColor="@android:color/white"
        android:textSize="58sp"
        app:layout_constraintBottom_toTopOf="@+id/flow"
        app:layout_constraintEnd_toEndOf="@+id/flow"
        app:layout_constraintStart_toStartOf="@+id/flow"
        app:layout_constraintTop_toTopOf="parent" />

</android.support.constraint.ConstraintLayout>
复制代码
</pre>

借助 flow 很快可以布局出来，这里 flow_wrapMode 使用的是 aligned，id 为 KE 的 TextView 可以对齐到 Flow 里面的 view，id 为 KR 的 TextView 可以对齐到 Flow，另外 Flow 也是 ConstraintHelper, 所以 Flow 也是个 View，可以设置背景，padding 等元素。 那么这样布局有什么优势? 这样的布局 view 都在一个层级，不使用 ViewGroup，减少层级。

### 流式 APIs

1.1 之前需要这样修改属性

<pre>    val set = ConstraintSet()
        set.clone(constraintLayout)
        set.setTranslationZ(R.id.image, 32f)
        set.setMargin(R.id.image, ConstraintSet.START, 43)
        set.applyTo(constraintLayout)
复制代码
</pre>

2.0 提供了 ConstraintProperties 可以使用流式 API 修改属性

<pre> val properties = ConstraintProperties(findViewById(R.id.image))
        properties.translationZ(32f)
                .margin(ConstraintSet.START, 43)
                .apply()
复制代码
</pre>

### MotionLayout

关于 MotionLayout 可以参考 ConstraintLayout 开发者 Nicolas Roard 写的系列文章，

[Introduction to MotionLayout (part I)](https://link.juejin.im?target=https%3A%2F%2Fmedium.com%2Fgoogle-developers%2Fintroduction-to-motionlayout-part-i-29208674b10d)

[Introduction to MotionLayout (part II)](https://link.juejin.im?target=https%3A%2F%2Fmedium.com%2Fgoogle-developers%2Fintroduction-to-motionlayout-part-ii-a31acc084f59)

[Introduction to MotionLayout (part III)](https://link.juejin.im?target=https%3A%2F%2Fmedium.com%2Fgoogle-developers%2Fintroduction-to-motionlayout-part-iii-47cd64d51a5)

[Defining motion paths in MotionLayout](https://link.juejin.im?target=https%3A%2F%2Fmedium.com%2Fgoogle-developers%2Fdefining-motion-paths-in-motionlayout-6095b874d37)

完整代码参考 [Github](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Ffeng0403%2FConstraintLayoutSamples)，喜欢的话 star 哦

参考资料

[ConstraintLayout Deep Dive (Android Dev Summit '18)](https://link.juejin.im?target=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DP9Zstbk0lPw)

[ConstraintLayout 2.0 by Nicolas Roard and John Hoford, Google EN](https://link.juejin.im?target=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DW39H7972buY)

[What's New in ConstraintLayout (Google I/O'19)](https://link.juejin.im?target=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3D29gLA90m6Gk%26list%3DPLWz5rJ2EKKc9FfSQIRXEWyWpHD6TtwxMM%26index%3D37)