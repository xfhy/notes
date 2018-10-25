> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/d2b1689a23bf

Android Material Design Library 推出了很长时间，越来越多的 APP 使用了符合 Library 包的控件，DrawerLayout 绝对是热门之一，Material Design 定义了一个抽屉导航应该有何种外观和感受，统一了侧滑菜单和样式。在 Android 原生手机上对 DrawerLayout+NavigationView 更是使用到了极致，如 Gmail,Google Map

关于 DrawerLayout 和 NavigationView 的使用介绍博客有很多，这里主要是实现一些使用上的介绍, 如让 NavigationView 在 Toolbar 下方, 不显示 Toolbar 左侧按钮等。

下面开始看下 DrawerLayout 的如何使用，首先在 build.gradle 中引入 Design 包

> compile 'com.android.support:design:24.2.1'

#### (一)、基本使用

新建一个 Activity, 这里我们选择使用 Android Studio 提供的模板, 选择 NavgationDrawer Activity

![](https://upload-images.jianshu.io/upload_images/3485428-c2e12e24ec1854d3.jpg)

查看下界面的 xml 文件

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.v4.widget.DrawerLayout
    android:id="@+id/drawer_layout"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start">

    <include
        layout="@layout/app_bar_drawer_layout__one"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <android.support.design.widget.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:fitsSystemWindows="true"
        app:headerLayout="@layout/nav_header_drawer_layout__one"
        app:menu="@menu/activity_drawer_layout__one_drawer"/>

</android.support.v4.widget.DrawerLayout>

```

可以看到我们的最外层是 DrawerLayout，包含了两个内容：include 为显示内容区域，NavigationView 为侧边抽屉栏。

NavigationView 有两个 app 属性，分别为 app:headerLayout 和 app:menu，eaderLayout 用于显示头部的布局（可选），menu 用于建立 MenuItem 选项的菜单。

headerLayout 就是正常的 layout 布局文件, 我们查看下 menu.xml

```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <group android:checkableBehavior="single">
        <item
            android:id="@+id/nav_camera"
            android:icon="@drawable/ic_menu_camera"
            android:title="Import"/>
        <item
            android:id="@+id/nav_gallery"
            android:icon="@drawable/ic_menu_gallery"
            android:title="Gallery"/>
        <item
            android:id="@+id/nav_slideshow"
            android:icon="@drawable/ic_menu_slideshow"
            android:title="Slideshow"/>
        <item
            android:id="@+id/nav_manage"
            android:icon="@drawable/ic_menu_manage"
            android:title="Tools"/>
    </group>

    <item android:title="Communicate">
        <menu>
            <item
                android:id="@+id/nav_share"
                android:icon="@drawable/ic_menu_share"
                android:title="Share"/>
            <item
                android:id="@+id/nav_send"
                android:icon="@drawable/ic_menu_send"
                android:title="Send"/>
        </menu>
    </item>
</menu>

```

menu 可以分组，group 的 android:checkableBehavior 属性设置为 single 可以设置该组为单选

Activity 主题必须设置先这两个属性

```
    <style >
        <item >false</item>
        <item >true</item>
    </style>

```

未设置 Activity 主题会爆出错误信息:

```
vCaused by: java.lang.IllegalStateException: This Activity 
already has an action bar supplied by the window decor. 
Do not request Window.FEATURE_SUPPORT_ACTION_BAR 
and set windowActionBar to false in your theme to use a Toolbar instead.

```

设置主题为 android:theme="@style/AppTheme.NoActionBar"

最后 java 代码

```
Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
setSupportActionBar(toolbar);

DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
drawer.setDrawerListener(toggle);
toggle.syncState();

NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
navigationView.setNavigationItemSelectedListener(this);

```

效果图：

![](https://upload-images.jianshu.io/upload_images/3485428-f448bae6615efa4c.gif)

#### (二)、监听和关闭 NavigationView

NavigationView 监听通过 navigationView.setNavigationItemSelectedListener(this) 方法去监听 menu 的点击事件

```
@SuppressWarnings("StatementWithEmptyBody")
@Override
public boolean onNavigationItemSelected(MenuItem item)
{
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
}

```

每次点击一个 Menu 关闭 DrawerLayout，方法为 drawer.closeDrawer(GravityCompat.START);

通过 onBackPressed 方法, 当点击返回按钮的时候, 如果 DrawerLayout 是打开状态则关闭

```
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

```

#### （三）、NavigationView 在 Toolbar 下方

大多数的 APP 都是使用 NavigationView 都是全屏的，当我们想让 NavigationView 在 Toolbar 下方的时候应该怎么做呢
xml 布局如下图，DrawerLayout 在 Toolbar 的下方

```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:app="http://schemas.android.com/apk/res-auto"
              android:id="@+id/sample_main_layout"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:orientation="vertical">

    <android.support.v7.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        app:theme="@style/ThemeOverlay.AppCompat.Dark" />

    <android.support.v4.widget.DrawerLayout
        android:id="@+id/drawer_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <FrameLayout
            android:id="@+id/container"
            android:layout_width="match_parent"
            android:layout_height="match_parent" >

            <TextView
                android:padding="16dp"
                android:text="NavigationView在Toolbar下方"
                android:gravity="center"
                android:layout_width="match_parent"
                android:layout_height="match_parent" />
        </FrameLayout>

        <android.support.design.widget.NavigationView
            android:id="@+id/nav_view"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:layout_gravity="start"
            android:fitsSystemWindows="true"
            app:headerLayout="@layout/nav_header_drawer_layout_one"
            app:menu="@menu/activity_drawer_layout_one_drawer"/>

    </android.support.v4.widget.DrawerLayout>
</LinearLayout>

```

效果如图:

![](https://upload-images.jianshu.io/upload_images/3485428-fb9b326a661904ab.gif)

#### （四）、Toolbar 上不显示 Home 旋转开关按钮

上图可以看到我们点击 Home 旋转开关按钮, 显示和隐藏了侧滑菜单。那么如果我们想要不通过按钮点击, 只能右划拉出菜单需要怎么做呢。
我们先看下带 Home 旋转开关按钮的代码是如何写的：

```
DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//这是带Home旋转开关按钮
ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, 
    toolbar, 
    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
drawer.setDrawerListener(toggle);
toggle.syncState();

```

这个 Home 旋转开关按钮实际上是通过 ActionBarDrawerToggle 代码绑定到 toolbar 上的，ActionBarDrawerToggle 是和 DrawerLayout 搭配使用的，它可以改变 android.R.id.home 返回图标，监听 drawer 的显示和隐藏。ActionBarDrawerToggle 的 syncState() 方法会和 Toolbar 关联，将图标放入到 Toolbar 上。
进入 ActionBarDrawerToggle 构造器可以看到一个不传 Toolbar 参数的构造器

```
public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout,
        @StringRes int openDrawerContentDescRes,
        @StringRes int closeDrawerContentDescRes) {
    this(activity, null, drawerLayout, null, openDrawerContentDescRes,
            closeDrawerContentDescRes);
}

```

那么不带 Home 旋转开关按钮的代码如下

```
//这是不带Home旋转开关按钮
ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
  R.string.navigation_drawer_open, R.string.navigation_drawer_close);

```

> 当然我们把上面带 Home 旋转开关按钮的代码删除也是可以的。

效果如图:

![](https://upload-images.jianshu.io/upload_images/3485428-37f3e8d70491ff22.gif)

#### （五）、不使用 NavigationView，使用 DrawerLayout + 其他布局

APP 实际开发中往往不能完全按照 Materialdesign 的规则来, 如网易云音乐的侧滑, 底部还有两个按钮。这时候我们可以通过 + 其他布局来实现特殊的侧滑布局。

我们可以参考鸿杨大神的博客
[Android 自己实现 NavigationView [Design Support Library(1)]](https://link.jianshu.com?t=http://blog.csdn.net/lmj623565791/article/details/46405409)

我们自己实现个简单的, DrawerLayout 包裹了一个 FrameLayout 和一个 RelativeLayout,FrameLayout 是我们的显示内容区域, RelativeLayout 是我们的侧边栏布局。

```
<android.support.v4.widget.DrawerLayout
    android:id="@+id/drawer_layout"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start">

    <FrameLayout
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/AppTheme.PopupOverlay"/>

        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:padding="16dp"
            android:text="@string/title_activity_drawer_layout_other"/>
    </FrameLayout>

    <RelativeLayout
        android:id="@+id/nav_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@android:color/white"
        android:fitsSystemWindows="true">

        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="这是顶部按钮"/>

        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:text="这是中间的按钮"/>

        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_alignParentBottom="true"
            android:text="这是底部按钮"/>
    </RelativeLayout>
</android.support.v4.widget.DrawerLayout>

```

如果需要监听 DrawerLayout 的侧滑状态监听, 那么代码如下:

```
mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /**
         * 也可以使用DrawerListener的子类SimpleDrawerListener,
         * 或者是ActionBarDrawerToggle这个子类
         */
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        });

```

效果图如下:

![](https://upload-images.jianshu.io/upload_images/3485428-54ea163c547c1cc6.gif)

最后上 github 地址

> [https://github.com/itdais/MaterialDesignDing](https://link.jianshu.com?t=https://github.com/itdais/MaterialDesignDing)