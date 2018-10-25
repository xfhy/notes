>  原文地址 https://www.jianshu.com/p/bbc703a0015e

在许多 App 中看到, toolbar 有收缩和扩展的效果, 例如:

![](https://upload-images.jianshu.io/upload_images/9833282-31a22c4230401c35.gif) 要实现这样的效果, 需要用到:
**CoordinatorLayout** 和 **AppbarLayout** 的配合, 以及实现了 **NestedScrollView** 的布局或控件.
AppbarLayout 是一种支持响应滚动手势的 app bar 布局, CollapsingToolbarLayout 则是专门用来实现子布局内不同元素响应滚动细节的布局.

与 AppbarLayout 组合的滚动布局 (RecyclerView, NestedScrollView 等), 需要设置 app:layout_behavior = "@string/appbar_scrolling_view_behavior" . 没有设置的话, AppbarLayout 将不会响应滚动布局的滚动事件.

我们回到再前面一章 "Toolbar 的使用", 将布局改动如下:

```
 <?xml version="1.0" encoding="utf-8"?>

<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.truly.mytoolbar.MainActivity">
    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
           android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:layout_scrollFlags="scroll"
            app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
            app:title="Title" />
    </android.support.design.widget.AppBarLayout>
    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
        <TextView
            android:id="@+id/tv_content"
            android:layout_margin="16dp"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:lineSpacingMultiplier="2"
            android:text="@string/textContent" />
    </android.support.v4.widget.NestedScrollView>
</android.support.design.widget.CoordinatorLayout>

```

先看下效果再来解释为什么.

![](https://upload-images.jianshu.io/upload_images/9833282-6152ff329ca415cc.gif)

可以看到,

*   随着文本往上滚动, 顶部的 toolbar 也往上滚动, 直到消失.
*   随着文本往下滚动, 一直滚到文本的第一行露出来, toolbar 也逐渐露出来

**解释:**
从上面的布局中可以看到, 其实在整个父布局 CoordinatorLayout 下面, 是有 2 个子布局

*   AppbarLayout
*   NestedScrollView
    NestedScrollView 先放一放, 我们来看 AppbarLayout.

> AppBarLayout 继承自 LinearLayout，布局方向为垂直方向。所以你可以把它当成垂直布局的 LinearLayout 来使用。AppBarLayout 是在 LinearLayou 上加了一些材料设计的概念，它可以让你定制当某个可滚动 View 的滚动手势发生变化时，其内部的子 View 实现何种动作。

**注意:**

上面提到的 "某个可滚动 View", 可以理解为某个 ScrollView. 就是说，当某个 ScrollView 发生滚动时，你可以定制你的 “顶部栏” 应该执行哪些动作（如跟着一起滚动、保持不动等等）。

这里某个 ScrollView 就是 NestedScrollView 或者实现了 NestedScrollView 机制的其它控件, 如 RecyclerView. 它有一个布局行为 Layout_Behavior:

```
app:layout_behavior="@string/appbar_scrolling_view_behavior"

```

这是一个系统 behavior, 从字面意思就可以看到, 是为 appbar 设置滚动动作的一个 behavior. 没有这个属性的话, Appbar 就是死的, 有了它就有了灵魂.

我们可以通过给 Appbar 下的子 View 添加 **app:layout_scrollFlags** 来设置各子 View 执行的动作. scrollFlags 可以设置的动作如下:

> (1) scroll: 值设为 scroll 的 View 会跟随滚动事件一起发生移动。就是当指定的 ScrollView 发生滚动时，该 View 也跟随一起滚动，就好像这个 View 也是属于这个 ScrollView 一样。

上面这个效果就是设置了 scroll 之后的.

> (2) enterAlways: 值设为 enterAlways 的 View, 当任何时候 ScrollView 往下滚动时，该 View 会直接往下滚动。而不用考虑 ScrollView 是否在滚动到最顶部还是哪里.

我们把 layout_scrollFlags 改动如下:

```
app:layout_scrollFlags="scroll|enterAlways"

```

效果如下:

![](https://upload-images.jianshu.io/upload_images/9833282-5b52c0c52428e4b9.gif)

> (3) exitUntilCollapsed：值设为 exitUntilCollapsed 的 View，当这个 View 要往上逐渐 “消逝” 时，会一直往上滑动，直到剩下的的高度达到它的最小高度后，再响应 ScrollView 的内部滑动事件。

怎么理解呢？简单解释：在 ScrollView 往上滑动时，首先是 View 把滑动事件 “夺走”，由 View 去执行滑动，直到滑动最小高度后，把这个滑动事件“还” 回去，让 ScrollView 内部去上滑。

把属性改下再看效果

```
<android.support.v7.widget.Toolbar
    ...
    android:layout_height="?attr/actionBarSize"
    android:minHeight="20dp"
    app:layout_scrollFlags="scroll|exitUntilCollapsed"
/>

```

![](https://upload-images.jianshu.io/upload_images/9833282-05a487d1047a720d.gif)

> (4) enterAlwaysCollapsed：是 enterAlways 的附加选项，一般跟 enterAlways 一起使用，它是指，View 在往下 “出现” 的时候，首先是 enterAlways 效果，当 View 的高度达到最小高度时，View 就暂时不去往下滚动，直到 ScrollView 滑动到顶部不再滑动时，View 再继续往下滑动，直到滑到 View 的顶部结束

这个得把高度加大点才好实验. 来看:

```
<android.support.v7.widget.Toolbar
    ...
    android:layout_height="200dp"
    android:minHeight="?attr/actionBarSize"
    app:layout_scrollFlags="scroll|enterAlways|enterAlwaysCollapsed"
</android.support.design.widget.AppBarLayout>

```

![](https://upload-images.jianshu.io/upload_images/9833282-75f004f3fca8823e.gif)

**Attention:**

其实 toolbar 的默认最小高度 minHeight 就是 "?attr/actionBarSize" , 很多时候可以不用设置. 而且从图上可以看出, 其实这里有个缺陷, 就是 title 的位置和 toolbar 上的图标行脱离了, 即使在布局里添加了 android:gravity="bottom|start", 在 toolbar 滚动的时候, title 还在, 图标滚动到隐藏了.

![](https://upload-images.jianshu.io/upload_images/9833282-b3f03f55932a8c8c.png)

后面讲解的 CollapsingToolbarLayout 可以解决这个问题, 这里先丢出来.

> (5) snap：简单理解，就是 Child View 滚动比例的一个吸附效果。也就是说，Child View 不会存在局部显示的情况，滚动 Child View 的部分高度，当我们松开手指时，Child View 要么向上全部滚出屏幕，要么向下全部滚进屏幕，有点类似 ViewPager 的左右滑动

![](https://upload-images.jianshu.io/upload_images/9833282-f7f5ff4f8544353a.gif)

**引入 CollapsingToolbarLayout**

CollapsingToolbarLayout 是用来对 Toolbar 进行再次包装的 ViewGroup，主要是用于实现折叠（其实就是看起来像伸缩~）的 App Bar 效果。它需要放在 AppBarLayout 布局里面，并且作为 AppBarLayout 的直接子 View。CollapsingToolbarLayout 主要包括几个功能（参照了官方网站上内容，略加自己的理解进行解释）：

> (1) 折叠 Title（Collapsing title）：当布局内容全部显示出来时，title 是最大的，但是随着 View 逐步移出屏幕顶部，title 变得越来越小。你可以通过调用 setTitle 方法来设置 title。

> (2) 内容纱布（Content scrim）：根据滚动的位置是否到达一个阀值，来决定是否对 View“盖上纱布”。可以通过 setContentScrim(Drawable) 来设置纱布的图片. 默认 contentScrim 是 colorPrimary 的色值

> (3)状态栏纱布（Status bar scrim)：根据滚动位置是否到达一个阀值决定是否对状态栏 “盖上纱布”，你可以通过 setStatusBarScrim(Drawable) 来设置纱布图片，但是只能在 LOLLIPOP 设备上面有作用。默认 statusBarScrim 是 colorPrimaryDark 的色值.

> (4)视差滚动子 View(Parallax scrolling children): 子 View 可以选择在当前的布局当时是否以 “视差” 的方式来跟随滚动。（PS: 其实就是让这个 View 的滚动的速度比其他正常滚动的 View 速度稍微慢一点）。将布局参数 app:layout_collapseMode 设为 parallax

> (5) 将子 View 位置固定 (Pinned position children)：子 View 可以选择是否在全局空间上固定位置，这对于 Toolbar 来说非常有用，因为当布局在移动时，可以将 Toolbar 固定位置而不受移动的影响。 将 app:layout_collapseMode 设为 pin。

我们来更改一下布局:

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout 
    ...>

    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="150dp">

        <android.support.design.widget.CollapsingToolbarLayout
            android:id="@+id/collapsing_toolbar_layout"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">

            <android.support.v7.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                android:background="?attr/colorPrimary"
                app:layout_collapseMode="parallax"
                app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
                app:title="Title" />
        </android.support.design.widget.CollapsingToolbarLayout>

    </android.support.design.widget.AppBarLayout>

    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <TextView
            android:id="@+id/tv_content"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_margin="16dp"
            android:lineSpacingMultiplier="2"
            android:text="@string/textContent" />
    </android.support.v4.widget.NestedScrollView>

</android.support.design.widget.CoordinatorLayout>

```

可以看到, 我们把原本属于 toolbar 的几个属性移到了 CollapsingToolbarLayout 上. 分别是:

```
android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
app:layout_scrollFlags="scroll|exitUntilCollapsed"

```

同时给 toolbar 增加了一个折叠模式属性

```
app:layout_collapseMode="parallax"

```

我们来看下效果:

![](https://upload-images.jianshu.io/upload_images/9833282-8a6b4005bf634f9e.gif)

嗯嗯, 折叠模式不对, toolbar 的顶部图标没了. 我们改下折叠模式:

```
app:layout_collapseMode="pin"

```

再看效果:

![](https://upload-images.jianshu.io/upload_images/9833282-9bbdb99625649263.gif)

我们把 scrollFlags 属性改下, 看下对比:

```
app:layout_scrollFlags="scroll|enterAlways|enterAlwaysCollapsed"

```

![](https://upload-images.jianshu.io/upload_images/9833282-1d3dab9afc7a84b2.gif)

效果还是蛮不错的, 有了点 Google Material Design 的感觉了.

上面说 CollapsingToolbarLayout 是个 ViewGroup, 那么肯定还可以添加控件. 那么我们在里面添加一个 ImageView 来看看. 更改布局如下:

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout 
    ...>
    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="200dp">
        <android.support.design.widget.CollapsingToolbarLayout
            ...
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:layout_scrollFlags="scroll|enterAlways|enterAlwaysCollapsed">
            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                android:src="@drawable/darkbg"
                app:layout_collapseMode="parallax" />
            <android.support.v7.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                android:background="?attr/colorPrimary"
                app:layout_collapseMode="pin"
                app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
                app:title="Title" />
        </android.support.design.widget.CollapsingToolbarLayout>
    </android.support.design.widget.AppBarLayout>

    <android.support.v4.widget.NestedScrollView
        ...
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
        <TextView
            ... />
    </android.support.v4.widget.NestedScrollView>

</android.support.design.widget.CoordinatorLayout>

```

来看下效果:

![](https://upload-images.jianshu.io/upload_images/9833282-41f0884a89f987e5.gif)

嗯, 有了点意思, 但不美观, 上部的 toolbar 和图片不协调. toolbar 应该有默认的背景属性, 我们去掉它看看.

```
 <android.support.v7.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    app:layout_collapseMode="pin"
    app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
    app:title="Title" />

```

再看下效果:

![](https://upload-images.jianshu.io/upload_images/9833282-978faef12c54cb33.gif)

这次真的不错哦, 已经和很多大公司的 app 相像了. 但是为什么去掉 toolbar 的 background 就可以得到透明背景呢? 说句实话, 没找到原因.

不过我们没有给 CollapsingToolbarLayout 设置 contentScrim 属性哦, 给它加个属性看看.

```
<android.support.design.widget.CollapsingToolbarLayout
    ...
    app:contentScrim="?attr/colorPrimary"
    ...>

```

![](https://upload-images.jianshu.io/upload_images/9833282-5629801b3986aaba.gif)

嗯嗯, 好像还不如没设置这个属性好呢.

什么时候需要 contentScrim 属性呢?
因为这个布局里面给 CollapsingToolbarLayout 的 layout_scrollFlags 设置的是 "scroll|enterAlways|enterAlwaysCollapsed" , toolbar 会全部消失的, 所以感觉不是很美观. 如果将 layout_scrollFlags 属性改为 "scroll|exitUntilCollapsed" , 效果会好点, 适合 toolbar 还是需要展示的场合.

![](https://upload-images.jianshu.io/upload_images/9833282-808dbc8a8700bbfd.gif)

不管怎么样, 先去掉 contentScrim 属性吧.

目前有很多 APP 比较喜欢采用沉浸式设计, 简单点说就是将状态栏和导航栏都设置成透明或半透明的.

我们来把状态栏 statusBar 设置成透明. 在 style 主题中的 AppTheme 里增加一条:

```
<style >
    ...

    <item >@android:color/transparent</item>
</style>

```

在布局里面, 将 ImageView 和所有它上面的父 View 都添加 fitsSystemWindows 属性.

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout 
    ...
    android:fitsSystemWindows="true">
    <android.support.design.widget.AppBarLayout
        ...
        android:fitsSystemWindows="true">
        <android.support.design.widget.CollapsingToolbarLayout
            ...
            android:fitsSystemWindows="true">
            <ImageView
                ...
                android:fitsSystemWindows="true" />
            <android.support.v7.widget.Toolbar
                ... />
        </android.support.design.widget.CollapsingToolbarLayout>
    </android.support.design.widget.AppBarLayout>

    <android.support.v4.widget.NestedScrollView
        ...>
        <TextView
            ... />
    </android.support.v4.widget.NestedScrollView>

</android.support.design.widget.CoordinatorLayout>

```

最后来看下效果:

![](https://upload-images.jianshu.io/upload_images/9833282-05ffff7d8bdcfcd7.gif)

其实还可以在 CollapsingToolbarLayout 里设置 statusBarScrim 为透明色, 不过有点问题, 最顶部的 toolbar 没有完全隐藏, 还留了一点尾巴.

![](https://upload-images.jianshu.io/upload_images/9833282-0fa9ac2a9a132302.png)

难道就这个属性就没用吗? 我们把 layout_scrollFlags 改成 "scroll|exitUntilCollapsed" 看看:

![](https://upload-images.jianshu.io/upload_images/9833282-7ccc6dd333860f34.png)

这个时候 toolbar 不用隐藏, 所以还是美美的.

AppbarLayout 整个做成沉浸式之后, 状态栏的图标可能会受到封面图片颜色过浅的影响, 可以给其加一个渐变的不透明层.

**渐变遮罩**设置方法:

在 res/drawable 文件夹下新建一个名为 status_gradient 的 xml 资源文件, 代码如下:

```
 <?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
        android:angle="270"
        android:endColor="@android:color/transparent"
        android:startColor="#CC000000" />
        <!-- shape节点中, 可以通过android:shape来设置形状, 默认是矩形.
        gradient节点中angle的值270是从上到下，0是从左到右，90是从下到上。 
        此处的效果就是从下向上, 颜色逐渐由纯透明慢慢变成黑透色-->
</shape>

```

布局中, 在 ImageView 下面增加一个 View, 背景设为上面的渐变遮罩.

```
<!-- 在顶部增加一个渐变遮罩, 防止出现status bar 状态栏看不清 -->
<View
    android:layout_width="match_parent"
    android:layout_height="40dp"
    android:background="@drawable/status_gradient"
    app:layout_collapseMode="pin"
    android:fitsSystemWindows="true" />

```

给遮罩设置折叠模式: app:layout_collapseMode="pin" , 折叠到顶部后定住. 来看下效果.

![](https://upload-images.jianshu.io/upload_images/9833282-4c9ce9254313d319.png) ![](https://upload-images.jianshu.io/upload_images/9833282-69db4f6edee4151e.png)

上图是展开状态的对比, 后面的是没有添加遮罩的效果, 前面是添加了遮罩的效果. 下图是添加了遮罩折叠后的效果. 有点黑暗系影片的感觉哦.

**FloatingActionButton 再次表演**

作为 Google Material Design 的一个重要控件, FloatingActionButton 怎么可能不在 AppbarLayout 中起点作用呢. 我们在布局中加一个悬浮按钮, 让它的锚点挂载 Appbar 的右下角. 这样这个悬浮按钮就和 Appbar 关联起来了.

```
<android.support.design.widget.CoordinatorLayout
    ...>

    <android.support.design.widget.AppBarLayout
        ...
    </android.support.design.widget.AppBarLayout>

    <android.support.v4.widget.NestedScrollView
    ...
    </android.support.v4.widget.NestedScrollView>

    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        android:src="@drawable/ic_share_white_24dp"
        android:elevation="4dp"
        app:pressedTranslationZ="16dp"
        app:rippleColor="@android:color/white"
        app:layout_anchor="@id/appbar"
        app:layout_anchorGravity="bottom|end"/>

</android.support.design.widget.CoordinatorLayout>

```

我们来看下效果.

![](https://upload-images.jianshu.io/upload_images/9833282-1c4e2bdcb9373c3b.gif)

好吧, 美美的 Toolbar 完成了, 有点 Google Material Design 扑面而来的感觉了.

这篇文章已经很长了, 还有些内容就不放进来了, 后面陆续完善.

> 借鉴了很多资料, 写的时候忘了记录下来, 如对您有损, 请联系我进行删除或更改. 致歉!