> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/guolin_blog/article/details/17045157 版权声明：本文出自郭霖的博客，转载必须注明出处。 https://blog.csdn.net/sinyu890807/article/details/17045157 <link rel="stylesheet" href="https://csdnimg.cn/release/phoenix/template/css/ck_htmledit_views-f57960eb32.css"> <link rel="stylesheet" href="https://csdnimg.cn/release/phoenix/template/css/ck_htmledit_views-f57960eb32.css">

转载请注明出处：[http://blog.csdn.net/guolin_blog/article/details/17045157](http://blog.csdn.net/guolin_blog/article/details/17045157)

在前面一篇文章中，我带着大家一起从源码的层面上分析了视图的绘制流程，了解了视图绘制流程中 onMeasure、onLayout、onDraw 这三个最重要步骤的工作原理，那么今天我们将继续对 View 进行深入探究，学习一下视图状态以及重绘方面的知识。如果你还没有看过我前面一篇文章，可以先去阅读 [Android 视图绘制流程完全解析，带你一步步深入了解 View(二)](http://blog.csdn.net/guolin_blog/article/details/16330267) 。

相信大家在平时使用 View 的时候都会发现它是有状态的，比如说有一个按钮，普通状态下是一种效果，但是当手指按下的时候就会变成另外一种效果，这样才会给人产生一种点击了按钮的感觉。当然了，这种效果相信几乎所有的 Android 程序员都知道该如何实现，但是我们既然是深入了解 View，那么自然也应该知道它背后的实现原理应该是什么样的，今天就让我们来一起探究一下吧。

## <a></a>一、视图状态

视图状态的种类非常多，一共有十几种类型，不过多数情况下我们只会使用到其中的几种，因此这里我们也就只去分析最常用的几种视图状态。

**1\. enabled**

表示当前视图是否可用。可以调用 setEnable() 方法来改变视图的可用状态，传入 true 表示可用，传入 false 表示不可用。它们之间最大的区别在于，不可用的视图是无法响应 onTouch 事件的。

**2\. focused**

表示当前视图是否获得到焦点。通常情况下有两种方法可以让视图获得焦点，即通过键盘的上下左右键切换视图，以及调用 requestFocus() 方法。而现在的 Android 手机几乎都没有键盘了，因此基本上只可以使用 requestFocus() 这个办法来让视图获得焦点了。而 requestFocus() 方法也不能保证一定可以让视图获得焦点，它会有一个布尔值的返回值，如果返回 true 说明获得焦点成功，返回 false 说明获得焦点失败。一般只有视图在 focusable 和 focusable in touch mode 同时成立的情况下才能成功获取焦点，比如说 EditText。

**3\. window_focused**

表示当前视图是否处于正在交互的窗口中，这个值由系统自动决定，应用程序不能进行改变。

**4\. selected**

表示当前视图是否处于选中状态。一个界面当中可以有多个视图处于选中状态，调用 setSelected() 方法能够改变视图的选中状态，传入 true 表示选中，传入 false 表示未选中。

**5\. pressed**

表示当前视图是否处于按下状态。可以调用 setPressed() 方法来对这一状态进行改变，传入 true 表示按下，传入 false 表示未按下。通常情况下这个状态都是由系统自动赋值的，但开发者也可以自己调用这个方法来进行改变。

我们可以在项目的 drawable 目录下创建一个 selector 文件，在这里配置每种状态下视图对应的背景图片。比如创建一个 compose_bg.xml 文件，在里面编写如下代码：

```
<selector xmlns:android="http://schemas.android.com/apk/res/android">     <item android:drawable="@drawable/compose_pressed" android:state_pressed="true"></item>    <item android:drawable="@drawable/compose_pressed" android:state_focused="true"></item>    <item android:drawable="@drawable/compose_normal"></item> </selector>
```

这段代码就表示，当视图处于正常状态的时候就显示 compose_normal 这张背景图，当视图获得到焦点或者被按下的时候就显示 compose_pressed 这张背景图。

创建好了这个 selector 文件后，我们就可以在布局或代码中使用它了，比如将它设置为某个按钮的背景图，如下所示：

```
<?xml version="1.0" encoding="utf-8"?><LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"    android:layout_width="match_parent"    android:layout_height="match_parent"    android:orientation="vertical" >    	<Button 	    android:id="@+id/compose"	    android:layout_width="60dp"	    android:layout_height="40dp"	    android:layout_gravity="center_horizontal"	    android:background="@drawable/compose_bg"	    />    </LinearLayout>
```

现在运行一下程序，这个按钮在普通状态和按下状态的时候就会显示不同的背景图片，如下图所示：

![](https://img-blog.csdn.net/20140105203957906?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ3VvbGluX2Jsb2c=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

这样我们就用一个非常简单的方法实现了按钮按下的效果，但是它的背景原理到底是怎样的呢？这就又要从源码的层次上进行分析了。

我们都知道，当手指按在视图上的时候，视图的状态就已经发生了变化，此时视图的 pressed 状态是 true。每当视图的状态有发生改变的时候，就会回调 View 的 drawableStateChanged() 方法，代码如下所示：

```
protected void drawableStateChanged() {    Drawable d = mBGDrawable;    if (d != null && d.isStateful()) {        d.setState(getDrawableState());    }}
```

在这里的第一步，首先是将 mBGDrawable 赋值给一个 Drawable 对象，那么这个 mBGDrawable 是什么呢？观察 setBackgroundResource() 方法中的代码，如下所示：

```
public void setBackgroundResource(int resid) {    if (resid != 0 && resid == mBackgroundResource) {        return;    }    Drawable d= null;    if (resid != 0) {        d = mResources.getDrawable(resid);    }    setBackgroundDrawable(d);    mBackgroundResource = resid;}
```

可以看到，在第 7 行调用了 Resource 的 getDrawable() 方法将 resid 转换成了一个 Drawable 对象，然后调用了 setBackgroundDrawable() 方法并将这个 Drawable 对象传入，在 setBackgroundDrawable() 方法中会将传入的 Drawable 对象赋值给 mBGDrawable。

而我们在布局文件中通过 android:background 属性指定的 selector 文件，效果等同于调用 setBackgroundResource() 方法。也就是说 drawableStateChanged() 方法中的 mBGDrawable 对象其实就是我们指定的 selector 文件。

接下来在 drawableStateChanged() 方法的第 4 行调用了 getDrawableState() 方法来获取视图状态，代码如下所示：

```
public final int[] getDrawableState() {    if ((mDrawableState != null) && ((mPrivateFlags & DRAWABLE_STATE_DIRTY) == 0)) {        return mDrawableState;    } else {        mDrawableState = onCreateDrawableState(0);        mPrivateFlags &= ~DRAWABLE_STATE_DIRTY;        return mDrawableState;    }}
```

在这里首先会判断当前视图的状态是否发生了改变，如果没有改变就直接返回当前的视图状态，如果发生了改变就调用 onCreateDrawableState() 方法来获取最新的视图状态。视图的所有状态会以一个整型数组的形式返回。

在得到了视图状态的数组之后，就会调用 Drawable 的 setState() 方法来对状态进行更新，代码如下所示：

```
public boolean setState(final int[] stateSet) {    if (!Arrays.equals(mStateSet, stateSet)) {        mStateSet = stateSet;        return onStateChange(stateSet);    }    return false;}
```

这里会调用 Arrays.equals() 方法来判断视图状态的数组是否发生了变化，如果发生了变化则调用 onStateChange() 方法，否则就直接返回 false。但你会发现，Drawable 的 onStateChange() 方法中其实就只是简单返回了一个 false，并没有任何的逻辑处理，这是为什么呢？这主要是因为 mBGDrawable 对象是通过一个 selector 文件创建出来的，而通过这种文件创建出来的 Drawable 对象其实都是一个 StateListDrawable 实例，因此这里调用的 onStateChange() 方法实际上调用的是 StateListDrawable 中的 onStateChange() 方法，那么我们赶快看一下吧：

```
@Overrideprotected boolean onStateChange(int[] stateSet) {    int idx = mStateListState.indexOfStateSet(stateSet);    if (DEBUG) android.util.Log.i(TAG, "onStateChange " + this + " states "            + Arrays.toString(stateSet) + " found " + idx);    if (idx < 0) {        idx = mStateListState.indexOfStateSet(StateSet.WILD_CARD);    }    if (selectDrawable(idx)) {        return true;    }    return super.onStateChange(stateSet);}
```

可以看到，这里会先调用 indexOfStateSet() 方法来找到当前视图状态所对应的 Drawable 资源下标，然后在第 9 行调用 selectDrawable() 方法并将下标传入，在这个方法中就会将视图的背景图设置为当前视图状态所对应的那张图片了。

那你可能会有疑问，在前面一篇文章中我们说到，任何一个视图的显示都要经过非常科学的绘制流程的，很显然，背景图的绘制是在 draw() 方法中完成的，那么为什么 selectDrawable() 方法能够控制背景图的改变呢？这就要研究一下视图重绘的流程了。

## <a></a>二、视图重绘

虽然视图会在 Activity 加载完成之后自动绘制到屏幕上，但是我们完全有理由在与 Activity 进行交互的时候要求动态更新视图，比如改变视图的状态、以及显示或隐藏某个控件等。那在这个时候，之前绘制出的视图其实就已经过期了，此时我们就应该对视图进行重绘。

调用视图的 setVisibility()、setEnabled()、setSelected() 等方法时都会导致视图重绘，而如果我们想要手动地强制让视图进行重绘，可以调用 invalidate() 方法来实现。当然了，setVisibility()、setEnabled()、setSelected() 等方法的内部其实也是通过调用 invalidate() 方法来实现的，那么就让我们来看一看 invalidate() 方法的代码是什么样的吧。

View 的源码中会有数个 invalidate() 方法的重载和一个 invalidateDrawable() 方法，当然它们的原理都是相同的，因此我们只分析其中一种，代码如下所示：

```
void invalidate(boolean invalidateCache) {    if (ViewDebug.TRACE_HIERARCHY) {        ViewDebug.trace(this, ViewDebug.HierarchyTraceType.INVALIDATE);    }    if (skipInvalidate()) {        return;    }    if ((mPrivateFlags & (DRAWN | HAS_BOUNDS)) == (DRAWN | HAS_BOUNDS) ||            (invalidateCache && (mPrivateFlags & DRAWING_CACHE_VALID) == DRAWING_CACHE_VALID) ||            (mPrivateFlags & INVALIDATED) != INVALIDATED || isOpaque() != mLastIsOpaque) {        mLastIsOpaque = isOpaque();        mPrivateFlags &= ~DRAWN;        mPrivateFlags |= DIRTY;        if (invalidateCache) {            mPrivateFlags |= INVALIDATED;            mPrivateFlags &= ~DRAWING_CACHE_VALID;        }        final AttachInfo ai = mAttachInfo;        final ViewParent p = mParent;        if (!HardwareRenderer.RENDER_DIRTY_REGIONS) {            if (p != null && ai != null && ai.mHardwareAccelerated) {                p.invalidateChild(this, null);                return;            }        }        if (p != null && ai != null) {            final Rect r = ai.mTmpInvalRect;            r.set(0, 0, mRight - mLeft, mBottom - mTop);            p.invalidateChild(this, r);        }    }}
```

在这个方法中首先会调用 skipInvalidate() 方法来判断当前 View 是否需要重绘，判断的逻辑也比较简单，如果 View 是不可见的且没有执行任何动画，就认为不需要重绘了。之后会进行透明度的判断，并给 View 添加一些标记位，然后在第 22 和 29 行调用 ViewParent 的 invalidateChild() 方法，这里的 ViewParent 其实就是当前视图的父视图，因此会调用到 ViewGroup 的 invalidateChild() 方法中，代码如下所示：

```
public final void invalidateChild(View child, final Rect dirty) {    ViewParent parent = this;    final AttachInfo attachInfo = mAttachInfo;    if (attachInfo != null) {        final boolean drawAnimation = (child.mPrivateFlags & DRAW_ANIMATION) == DRAW_ANIMATION;        if (dirty == null) {            ......        } else {            ......            do {                View view = null;                if (parent instanceof View) {                    view = (View) parent;                    if (view.mLayerType != LAYER_TYPE_NONE &&                            view.getParent() instanceof View) {                        final View grandParent = (View) view.getParent();                        grandParent.mPrivateFlags |= INVALIDATED;                        grandParent.mPrivateFlags &= ~DRAWING_CACHE_VALID;                    }                }                if (drawAnimation) {                    if (view != null) {                        view.mPrivateFlags |= DRAW_ANIMATION;                    } else if (parent instanceof ViewRootImpl) {                        ((ViewRootImpl) parent).mIsAnimating = true;                    }                }                if (view != null) {                    if ((view.mViewFlags & FADING_EDGE_MASK) != 0 &&                            view.getSolidColor() == 0) {                        opaqueFlag = DIRTY;                    }                    if ((view.mPrivateFlags & DIRTY_MASK) != DIRTY) {                        view.mPrivateFlags = (view.mPrivateFlags & ~DIRTY_MASK) | opaqueFlag;                    }                }                parent = parent.invalidateChildInParent(location, dirty);                if (view != null) {                    Matrix m = view.getMatrix();                    if (!m.isIdentity()) {                        RectF boundingRect = attachInfo.mTmpTransformRect;                        boundingRect.set(dirty);                        m.mapRect(boundingRect);                        dirty.set((int) boundingRect.left, (int) boundingRect.top,                                (int) (boundingRect.right + 0.5f),                                (int) (boundingRect.bottom + 0.5f));                    }                }            } while (parent != null);        }    }}
```

可以看到，这里在第 10 行进入了一个 while 循环，当 ViewParent 不等于空的时候就会一直循环下去。在这个 while 循环当中会不断地获取当前布局的父布局，并调用它的 invalidateChildInParent() 方法，在 ViewGroup 的 invalidateChildInParent() 方法中主要是来计算需要重绘的矩形区域，这里我们先不管它，当循环到最外层的根布局后，就会调用 ViewRoot 的 invalidateChildInParent() 方法了，代码如下所示：

```
    public ViewParent invalidateChildInParent(final int[] location, final Rect dirty) {        invalidateChild(null, dirty);        return null;    }
```

这里的代码非常简单，仅仅是去调用了 invalidateChild() 方法而已，那我们再跟进去瞧一瞧吧：

```
public void invalidateChild(View child, Rect dirty) {    checkThread();    if (LOCAL_LOGV) Log.v(TAG, "Invalidate child: " + dirty);    mDirty.union(dirty);    if (!mWillDrawSoon) {        scheduleTraversals();    }}
```

这个方法也不长，它在第 6 行又调用了 scheduleTraversals() 这个方法，那么我们继续跟进：

```
public void scheduleTraversals() {    if (!mTraversalScheduled) {        mTraversalScheduled = true;        sendEmptyMessage(DO_TRAVERSAL);    }}
```

可以看到，这里调用了 sendEmptyMessage() 方法，并传入了一个 DO_TRAVERSAL 参数。了解 Android 异步消息处理机制的朋友们都会知道，任何一个 Handler 都可以调用 sendEmptyMessage() 方法来发送消息，并且在 handleMessage() 方法中接收消息，而如果你看一下 ViewRoot 的类定义就会发现，它是继承自 Handler 的，也就是说这里调用 sendEmptyMessage() 方法出的消息，会在 ViewRoot 的 handleMessage() 方法中接收到。那么赶快看一下 handleMessage() 方法的代码吧，如下所示：

```
public void handleMessage(Message msg) {    switch (msg.what) {    case DO_TRAVERSAL:        if (mProfile) {            Debug.startMethodTracing("ViewRoot");        }        performTraversals();        if (mProfile) {            Debug.stopMethodTracing();            mProfile = false;        }        break;    ......}
```

熟悉的代码出现了！这里在第 7 行调用了 performTraversals() 方法，这不就是我们在前面一篇文章中学到的视图绘制的入口吗？虽然经过了很多辗转的调用，但是可以确定的是，调用视图的 invalidate() 方法后确实会走到 performTraversals() 方法中，然后重新执行绘制流程。之后的流程就不需要再进行描述了吧，可以参考 [Android 视图绘制流程完全解析，带你一步步深入了解 View(二)](http://blog.csdn.net/guolin_blog/article/details/16330267) 这一篇文章。

了解了这些之后，我们再回过头来看看刚才的 selectDrawable() 方法中到底做了什么才能够控制背景图的改变，代码如下所示：

```
public boolean selectDrawable(int idx) {    if (idx == mCurIndex) {        return false;    }    final long now = SystemClock.uptimeMillis();    if (mDrawableContainerState.mExitFadeDuration > 0) {        if (mLastDrawable != null) {            mLastDrawable.setVisible(false, false);        }        if (mCurrDrawable != null) {            mLastDrawable = mCurrDrawable;            mExitAnimationEnd = now + mDrawableContainerState.mExitFadeDuration;        } else {            mLastDrawable = null;            mExitAnimationEnd = 0;        }    } else if (mCurrDrawable != null) {        mCurrDrawable.setVisible(false, false);    }    if (idx >= 0 && idx < mDrawableContainerState.mNumChildren) {        Drawable d = mDrawableContainerState.mDrawables[idx];        mCurrDrawable = d;        mCurIndex = idx;        if (d != null) {            if (mDrawableContainerState.mEnterFadeDuration > 0) {                mEnterAnimationEnd = now + mDrawableContainerState.mEnterFadeDuration;            } else {                d.setAlpha(mAlpha);            }            d.setVisible(isVisible(), true);            d.setDither(mDrawableContainerState.mDither);            d.setColorFilter(mColorFilter);            d.setState(getState());            d.setLevel(getLevel());            d.setBounds(getBounds());        }    } else {        mCurrDrawable = null;        mCurIndex = -1;    }    if (mEnterAnimationEnd != 0 || mExitAnimationEnd != 0) {        if (mAnimationRunnable == null) {            mAnimationRunnable = new Runnable() {                @Override public void run() {                    animate(true);                    invalidateSelf();                }            };        } else {            unscheduleSelf(mAnimationRunnable);        }        animate(true);    }    invalidateSelf();    return true;}
```

这里前面的代码我们可以都不管，关键是要看到在第 54 行一定会调用 invalidateSelf() 方法，这个方法中的代码如下所示：

```
public void invalidateSelf() {    final Callback callback = getCallback();    if (callback != null) {        callback.invalidateDrawable(this);    }}
```

可以看到，这里会先调用 getCallback() 方法获取 Callback 接口的回调实例，然后再去调用回调实例的 invalidateDrawable() 方法。那么这里的回调实例又是什么呢？观察一下 View 的类定义其实你就知道了，如下所示：

```
public class View implements Drawable.Callback, Drawable.Callback2, KeyEvent.Callback,AccessibilityEventSource {    ......}
```

View 类正是实现了 Callback 接口，所以刚才其实调用的就是 View 中的 invalidateDrawable() 方法，之后就会按照我们前面分析的流程执行重绘逻辑，所以视图的背景图才能够得到改变的。

另外需要注意的是，invalidate() 方法虽然最终会调用到 performTraversals() 方法中，但这时 measure 和 layout 流程是不会重新执行的，因为视图没有强制重新测量的标志位，而且大小也没有发生过变化，所以这时只有 draw 流程可以得到执行。而如果你希望视图的绘制流程可以完完整整地重新走一遍，就不能使用 invalidate() 方法，而应该调用 requestLayout() 了。这个方法中的流程比 invalidate() 方法要简单一些，但中心思想是差不多的，这里也就不再详细进行分析了。

这样的话，我们就将视图状态以及重绘的工作原理都搞清楚了，相信大家对 View 的理解变得更加深刻了。感兴趣的朋友可以继续阅读 [Android 自定义 View 的实现方法，带你一步步深入了解 View(四)](http://blog.csdn.net/guolin_blog/article/details/17357967) 。

> 关注我的技术公众号，每天都有优质技术文章推送。关注我的娱乐公众号，工作、学习累了的时候放松一下自己。
> 
> 微信扫一扫下方二维码即可关注：
> 
> ![](https://img-blog.csdn.net/20160507110203928)         ![](https://img-blog.csdn.net/20161011100137978)