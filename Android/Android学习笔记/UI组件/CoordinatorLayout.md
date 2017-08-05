# CoordinatorLayout 

## 1.内部被划分成2个区域

- AppBar区域
- 动作产生区域(滚动区域)

## 2.滚动区域可以直接使用的控件

- NestedScrollView
- RecyclerView
- 滚动区域使用app:layout_behavior="@string/appbar_scrolling_view_behavior"发布动作
- appbar区域使用: app:layout_scrollFlags="scroll|exitUntilCollapsed"添加标记,来配合动作app:layout_behavior
- 想让View随着滚动,可以做出动作,对顶部的View添加标记 ,没有添加标记的View就会出现悬浮特性

添加标记:类似: app:layout_scrollFlags="scroll|exitUntilCollapsed"

## 3.demo

	<?xml version="1.0" encoding="utf-8"?>
	<android.support.design.widget.CoordinatorLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:fitsSystemWindows="true"
	    tools:context="com.xfhy.design.ScrollingActivity">
	
	    <!--这其实就是一个LinearLayout-->
	    <android.support.design.widget.AppBarLayout
	        android:id="@+id/app_bar"
	        android:layout_width="match_parent"
	        android:layout_height="@dimen/app_bar_height"
	        android:fitsSystemWindows="true"
	        android:theme="@style/AppTheme.AppBarOverlay">
	
	        <!--折叠的布局面就是一个FrameLayout
	                app:layout_scrollFlags="scroll|exitUntilCollapsed"  根据滚动区域,做出响应动作的一个标示
	                只要想关注滚动动作,必须添加scroll
	                exitUntilCollapsed:下滑时,applayout就悬停于顶部
	                                    上滑时,滑到最上面,applayout才显示出来
	                snap:表示,如果折叠区域显示超过一半,那么最后将全部显示
	                          折叠区域显示小于一半,那么将完全折叠
	                enterAlways:下滑时,applayout就消失了
	                            上滑时(即使没滑到最上面),appLayout也会马上显示出来
	                enterAlwaysCollapsed:下滑时,applayout就消失了
	                            上滑时(只有滑动到最上面),appLayout才会马上显示出来
	
	            app:contentScrim="?attr/colorPrimary" 表示折叠时上面的覆盖色 问号表示如果没指定则直接用colorPrimary
	        -->
	        <android.support.design.widget.CollapsingToolbarLayout
	            android:id="@+id/toolbar_layout"
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            android:fitsSystemWindows="true"
	            app:contentScrim="@color/colorAccent"
	            app:layout_scrollFlags="scroll|exitUntilCollapsed">
	
	            <android.support.v7.widget.Toolbar
	                android:id="@+id/toolbar"
	                android:layout_width="match_parent"
	                android:layout_height="?attr/actionBarSize"
	                app:layout_collapseMode="pin"
	                app:popupTheme="@style/AppTheme.PopupOverlay"/>
	
	        </android.support.design.widget.CollapsingToolbarLayout>
	    </android.support.design.widget.AppBarLayout>
	
	    <include layout="@layout/content_scrolling"/>
	
	    <!--
	        app:layout_anchor="@id/app_bar"   锚点,指的是挂在哪一个View上面
	        app:layout_anchorGravity="bottom|end" 锚点的对其方式
	    -->
	    <android.support.design.widget.FloatingActionButton
	        android:id="@+id/fab"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_margin="@dimen/fab_margin"
	        app:layout_anchor="@id/app_bar"
	        app:layout_anchorGravity="bottom|end"
	        app:srcCompat="@android:drawable/ic_dialog_email"/>
	
	</android.support.design.widget.CoordinatorLayout>
