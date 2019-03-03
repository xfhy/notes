> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 http://www.gcssloop.com/customview/scalegesturedetector

<article class="post-container post-container--single" itemscope="" itemtype="http://schema.org/BlogPosting">

<header class="post-header"><time datetime="2018-08-09 00:00:00 +0800" itemprop="datePublished" class="post-meta__date date">2018-08-09</time> • Android,ScaleGestureDecetor • View 1348 times.

# 安卓自定义 View 进阶 - 缩放手势检测 (ScaleGestureDecetor)

</header>

<section class="post">

## 0\. 前言

Android 缩放手势检测，ScaleGestureDetector 相关内容的用法和注意事项，本文依旧属于事件处理这一体系，在大多数的情况下，缩放手势都不是单独存在的，需要配合其它的手势来使用，所以推荐配合 [手势检测 (GestureDetector)](http://www.gcssloop.com/customview/gestruedector) 一起观看。如果是用在自定义的控件上，则需要配合 Matrix 相关内容使用起来可能会更加方便，如果对 Matrix 不太熟悉也可以看本系列文章之前的内容来补充一下相关知识。如果你没看过之前的文章，可以到 [自定义 View 系列](http://www.gcssloop.com/customview/CustomViewIndex) 来查看这些内容。

缩放手势对于大部分 Android 工程师来说，需要用到的机会比较少，它最常见于以下的一些应用场景中，例如：图片浏览，图片编辑 (贴图效果)、网页缩放、地图、文本阅读(通过缩放手势调整文字大小) 等。应用场景相对比较狭窄，不过肯定也会有一些用武之地，它可以实现如下的效果：

![](http://www.gcssloop.com/assets/customview/scale-gesture-decetor/demo.gif)

## 2\. 缩放手势检测 (ScaleGestureDetector)

缩放手势检测同样是官方提供的手势检测工具，它的使用方式的 GentureDetector 类似，也是通过 Listener 进行监听用户的操作手势，它是对缩放手势进行了一次封装， 可以方便用户快速的完成缩放相关功能的开发。缩放手势相对比较简单，网络上也能查到不少非官方实现的缩放手势计算方案，但部分非官方的方案确实有所局限，例如只支持两个手指的计算，在出现超过两个手指时，只计算了前两个手指的移动，这样显然是不合理的。而官方的这种实现方案轻松的应对了多个手指的情况，下面我们就来看看它是如何实现的吧。

### 2.1 构造方法

它有两个构造方法，和 GestureDetector 类似，如下所示：

```
ScaleGestureDetector(Context context, ScaleGestureDetector.OnScaleGestureListener listener)

ScaleGestureDetector(Context context, ScaleGestureDetector.OnScaleGestureListener listener, Handler handler)

```

### 2.2 手势监听器

它只有两个监听器，但严格来说，这两个监听器是同一个，只不过一个是接口，另一个是空实现而已。

| 监听器 | 简介 |
| --- | --- |
| OnScaleGestureListener | 缩放手势检测器。 |
| SimpleOnScaleGestureListener | 缩放手势检测器的空实现。 |

#### OnScaleGestureListener

缩放手势监听器有 3 个方法：

| 方法 | 简介 |
| --- | --- |
| boolean [onScaleBegin](https://developer.android.com/reference/android/view/ScaleGestureDetector.OnScaleGestureListener.html#onScaleBegin(android.view.ScaleGestureDetector))([ScaleGestureDetector](https://developer.android.com/reference/android/view/ScaleGestureDetector.html) detector) | 缩放手势开始，当两个手指放在屏幕上的时候会调用该方法 (只调用一次)。如果返回 false 则表示不使用当前这次缩放手势。 |
| boolean [onScale](https://developer.android.com/reference/android/view/ScaleGestureDetector.OnScaleGestureListener.html#onScale(android.view.ScaleGestureDetector))([ScaleGestureDetector](https://developer.android.com/reference/android/view/ScaleGestureDetector.html) detector) | 缩放被触发 (会调用 0 次或者多次)，如果返回 true 则表示当前缩放事件已经被处理，检测器会重新积累缩放因子，返回 false 则会继续积累缩放因子。 |
| void [onScaleEnd](https://developer.android.com/reference/android/view/ScaleGestureDetector.OnScaleGestureListener.html#onScaleEnd(android.view.ScaleGestureDetector))([ScaleGestureDetector](https://developer.android.com/reference/android/view/ScaleGestureDetector.html) detector) | 缩放手势结束。 |

### 2.3 简单示例

这是使用 ScaleGestureDetector 的一个极简用例，当然，它没有实现任何功能，只是用日志的方式输出了几个我们比较关心的参数而已。

```
public class ScaleGestureDemoView extends View {
    private static final String TAG = "ScaleGestureDemoView";

    private ScaleGestureDetector mScaleGestureDetector;

    public ScaleGestureDemoView(Context context) {
        super(context);
    }

    public ScaleGestureDemoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initScaleGestureDetector();
    }

    private void initScaleGestureDetector() {
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.i(TAG, "focusX = " + detector.getFocusX());       // 缩放中心，x坐标
                Log.i(TAG, "focusY = " + detector.getFocusY());       // 缩放中心y坐标
                Log.i(TAG, "scale = " + detector.getScaleFactor());   // 缩放因子
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }
}

```

## 3\. 基本原理

由于缩放手势检测使用起来非常简单，没有什么复杂的内容，不仅如此，它的实现也非常简单，下面我就带大家简单分析一下它的基本原理。**在缩放手势中我们其实主要关心的只有两个参数而已，一个是缩放的中心点，另一个就是缩放比例了。** 下面我们就看看这两个参数是如何计算出来的.

### 3.1 计算缩放的中心点 (焦点)

如果只有两个手指的话，缩放的中心点自然是非常容易计算的，那就是两个手指坐标的中点，但是如果有多个手指该如何计算缩放的中心点呢？

**计算中心点的原理其实也非常简单，那就是将所有的坐标都加起来，然后除以数量即可。**

这是一个简单的数学原理，并不复杂，如果有不理解的，自己尝试计算一下也就能明白了。不过在实际运用中还是需要注意一下的， 用户的手指数量可能并不是固定的，用户可能随时抬起来或者按下手指，ScaleGestureDetector 中是这样实现的：

```
 final boolean anchoredScaleCancelled =
            mAnchoredScaleMode == ANCHORED_SCALE_MODE_STYLUS && !isStylusButtonDown;
    final boolean streamComplete = action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_CANCEL || anchoredScaleCancelled;

    // 注意这里
    if (action == MotionEvent.ACTION_DOWN || streamComplete) {
		//重置侦听器正在进行的任何缩放。
        //如果是ACTION_DOWN，我们正在开始一个新的事件流。
        //这意味着应用程序可能没有给我们所有的事件(事件被上层直接拦截了)。
        if (mInProgress) {
            mListener.onScaleEnd(this);
            mInProgress = false;
            mInitialSpan = 0;
            mAnchoredScaleMode = ANCHORED_SCALE_MODE_NONE;
        } else if (inAnchoredScaleMode() && streamComplete) {
            mInProgress = false;
            mInitialSpan = 0;
            mAnchoredScaleMode = ANCHORED_SCALE_MODE_NONE;
        }

        if (streamComplete) {
            return true;
        }
    }

```

可以看到，当触发 down 或者触发 up，cancel 时，如果之前处于缩放计算的状态，会将其状态重置， 并调用 onScaleEnd 方法。

> 当然， 你可能注意到了 mAnchoredScaleMode 等内容，这些是对触控笔等外设的支持，对于大部分工程师来说，用到的机会比较少， 不管即可。

**计算中心点：**

```
final boolean configChanged = action == MotionEvent.ACTION_DOWN ||
        action == MotionEvent.ACTION_POINTER_UP ||
        action == MotionEvent.ACTION_POINTER_DOWN || anchoredScaleCancelled;

// 注意这里
final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
final int skipIndex = pointerUp ? event.getActionIndex() : -1;

// 确定焦点
float sumX = 0, sumY = 0;
final int div = pointerUp ? count - 1 : count;
final float focusX;
final float focusY;
if (inAnchoredScaleMode()) {
    // 在锚定比例模式下，焦点始终是双击或按钮按下时手势开始的位置
    focusX = mAnchoredScaleStartX;
    focusY = mAnchoredScaleStartY;
    if (event.getY() < focusY) {
        mEventBeforeOrAboveStartingGestureEvent = true;
    } else {
        mEventBeforeOrAboveStartingGestureEvent = false;
    }
} else {
	// 注意这里， 最终计算得到焦点
    for (int i = 0; i < count; i++) {
        if (skipIndex == i) continue;
        sumX += event.getX(i);
        sumY += event.getY(i);
    }

    focusX = sumX / div;
    focusY = sumY / div;
}

```

### 3.2 计算缩放比例

**计算缩放比例也很简单，就是计算各个手指到焦点的平均距离，在用户手指移动后用新的平均距离除以旧的平均距离，并以此计算得出缩放比例。**

```
// 计算到焦点的平均距离
float devSumX = 0, devSumY = 0;
for (int i = 0; i < count; i++) {
    if (skipIndex == i) continue;
    devSumX += Math.abs(event.getX(i) - focusX);
    devSumY += Math.abs(event.getY(i) - focusY);
}
final float devX = devSumX / div;
final float devY = devSumY / div;

// 注意这里
final float spanX = devX * 2;
final float spanY = devY * 2;
final float span;
if (inAnchoredScaleMode()) {
    span = spanY;
} else {
    // 相当于 sqrt(x*x + y*y)
    span = (float) Math.hypot(spanX, spanY);
}

```

当用户移动的距离超过一定数值 (数值大小由系统定义) 后，会触发 onScaleBegin 方法，如果用户在 onScaleBegin 方法里面返回了 true，表示接受事件后，就会重置缩放相关数值，并且开始积累缩放因子。

```
// mSpanSlop 和 mMinSpan 都是从系统里面取得的预定义数值，该数值实际上影响的是缩放的灵敏度。
// 不过该参数并没有提供设置的方法，如果对灵敏度不满意的话，和通过直接之际复制一个 ScaleGestureDetector 到项目中， 并且修改其中的数值。
final int minSpan = inAnchoredScaleMode() ? mSpanSlop : mMinSpan;
if (!mInProgress && span >=  minSpan &&
        (wasInProgress || Math.abs(span - mInitialSpan) > mSpanSlop)) {
    mPrevSpanX = mCurrSpanX = spanX;
    mPrevSpanY = mCurrSpanY = spanY;
    mPrevSpan = mCurrSpan = span;
    mPrevTime = mCurrTime;
    mInProgress = mListener.onScaleBegin(this);
}

```

通知用户缩放：

```
if (action == MotionEvent.ACTION_MOVE) {
    mCurrSpanX = spanX;
    mCurrSpanY = spanY;
    mCurrSpan = span;

    boolean updatePrev = true;

    if (mInProgress) {
        // 注意这里，用户的返回值决定了是否重新计算缩放因子
        updatePrev = mListener.onScale(this);
    }

    // 如果用户返回了 true ，就会重新计算缩放因子
    if (updatePrev) {
        mPrevSpanX = mCurrSpanX;
        mPrevSpanY = mCurrSpanY;
        mPrevSpan = mCurrSpan;
        mPrevTime = mCurrTime;
    }
}

```

## 4\. 后记

由于缩放手势检测确实比较简单，没什么好讲的内容，如果看了上面的内容依旧有疑惑的话，推荐去看一下源码，源码的逻辑也非常简洁明了，相信看完之后也就能够彻底理解它的原理了。

最后附上文章一开始展示所用图片的源代码，点击下载项目获取源代码。

[**下载项目**](http://android.demo.gcssloop.com/ScaleGestureDemo.zip)

## About Me

### 作者微博: [@GcsSloop](http://weibo.com/GcsSloop)

[![](http://ww4.sinaimg.cn/large/005Xtdi2gw1f1qn89ihu3j315o0dwwjc.jpg)](http://www.gcssloop.com/info/about)

* * *

</section>

</article>