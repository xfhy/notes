# SwipeRefreshLayout 刷新控件

## 1. 使用方式

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:orientation="vertical"
	    tools:context="com.xfhy.day06.MainActivity">
	
	    <android.support.v4.widget.SwipeRefreshLayout
	        android:id="@+id/srl_main_content"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:scrollbars="vertical">
	
	        <android.support.v7.widget.RecyclerView
	            android:id="@+id/rv_main_content_movie_list"
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            app:layoutManager="LinearLayoutManager"/>
	    </android.support.v4.widget.SwipeRefreshLayout>
	
	
	</LinearLayout>

## 2.简单处理

	setOnRefreshListener(OnRefreshListener):添加下拉刷新监听器
	setRefreshing(boolean):显示或者隐藏刷新进度条
	isRefreshing():检查是否处于刷新状态
	setColorSchemeResources():设置进度条的颜色主题，最多设置四种，以前的setColorScheme()方法已经弃用了。
