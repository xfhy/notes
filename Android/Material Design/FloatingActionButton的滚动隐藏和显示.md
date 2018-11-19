
        
# FloatingActionButton的滚动隐藏和显示


## 概述

FloatingActionButton（FAB）其实就是遵循了 `Material Design` 设计规范的并拥有特定动作行为的 `ImageButton`，基本用法参照 `ImageButton` 即可。

比如说，可以在 `RecyclerView` 向下滚动的时候隐藏 `FAB`, 向上滚动的时候显示 `FAB`:<br>


![](https://upload-images.jianshu.io/upload_images/448665-e1519fa4b969dc4f.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/319/format/webp)

那么，如何实现上面 FAB 的滚动隐藏和显示动画呢？

## 实现

我们需要通过导入 `Design Support Library` 才能使用 FAB, 所以，需要在 `build.gradle` 中加入 `compile 'com.android.support:design:X.X.X'` 其中 X 代表 Support Library 的版本。

一般来说，我们需要把 FAB 放在 `CoordinatorLayout` 布局中， `CoordinatorLayout` 可以看做为 `FrameLayout`，其特殊之处在于可以协调子控件的交互，其中就包括FAB的滚动隐藏和显示的动画。

那么，演示动画中的布局为：

```java
<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <android.support.design.widget.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:src="@drawable/ic_add_white"
        app:layout_behavior="com.xfhy.fab.FabScrollBehavior"
        app:elevation="4dp">

</android.support.design.widget.CoordinatorLayout>

```

`app:elevation` 属性表示 `FloatingActionButton` 悬浮的高度，高度越大，投影范围越大，投影效果越淡；高度越小，投影范围越小，投影效果越明显。

`app:layout_behavior` 属性表示 `FloatingActionButton` 所实现的 [CoordinatorLayout Behavior](https://link.jianshu.com?t=https://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.Behavior.html), 这个属性指定的 `Behavior` 用于定义 `FloatingActionButton` 与在同一个 `CoordinatorLayout` 布局下的其他控件的交互方式。如果不指定自定义的 `Behavior`，那么`FloatingActionButton` 默认的 `Behavior` 是为 `Snackbar` 留出空间，详见 [演示视频](https://link.jianshu.com?t=http://omx3hkcsx.bkt.clouddn.com/static/media/fab_default_behavior.webm)

显然， FAB 默认的 Behavior 不能满足我们的需要，那么就需要自定义 Behavior 来实现 FAB 的滚动显示和隐藏动画：

```java

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class FabScrollBehavior extends FloatingActionButton.Behavior {

    // 因为需要在布局xml中引用，所以必须实现该构造方法
    public FabScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // 确保滚动方向为垂直方向
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed &gt; 0) { // 向下滑动
            animateOut(child);
        } else if (dyConsumed &lt; 0) { // 向上滑动
            animateIn(child);
        }
    }

    // FAB移出屏幕动画（隐藏动画）
    private void animateOut(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        int bottomMargin = layoutParams.bottomMargin;
        fab.animate().translationY(fab.getHeight() + bottomMargin).setInterpolator(new LinearInterpolator()).start();
    }

    // FAB移入屏幕动画（显示动画）
    private void animateIn(FloatingActionButton fab) {
        fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }
}

```

上述代码的关键就是找到 “隐藏动画” 和 “显示动画” 触发的时机，然后利用 “属性动画” 来实现对应的动画效果即可。
