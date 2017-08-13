# DrawerLayout 


## 1. 介绍

- 这个布局可以直接拥有2个或3个ChildView
- 默认加入的ChildView就是主界面,使用layout_gravity修饰的就是侧滑界面

## 2.demo

	<?xml version="1.0" encoding="utf-8"?>
	<android.support.v4.widget.DrawerLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    tools:context="com.xfhy.design.ThreeActivity">
	
	    <!--
	        抽屉布局,侧滑布局
	
	        这个布局可以
	    -->
	
	    <FrameLayout
	        android:id="@+id/main_content"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:background="#3f5cc4">
	
	        <TextView
	            android:layout_width="wrap_content"
	            android:layout_height="wrap_content"
	            android:layout_gravity="center"
	            android:text="这是主界面"/>
	
	    </FrameLayout>
	
	    <!--
	
	        Design 中推出的一个侧滑布局
	
	    -->
	    <android.support.design.widget.NavigationView
	        android:id="@+id/left_content"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:layout_gravity="start"
	        app:headerLayout="@layout/header"
	        app:itemIconTint="@color/colorAccent"
	        app:menu="@menu/menu_navi"/>
	
	</android.support.v4.widget.DrawerLayout>

## 3.NavigationView

> Design 中推出的一个侧滑布局

给侧滑菜单中的ImageView添加点击事件

	int headerCount = mNavigationView.getHeaderCount();
        if (headerCount>0) {
            //获取header布局
            View headerView = mNavigationView.getHeaderView(0);
            //header中的
            headerIcon = (ImageView) headerView.findViewById(R.id.header_icon);
        }