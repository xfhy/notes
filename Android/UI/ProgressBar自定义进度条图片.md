# ProgressBar自定义进度条图片

> 进度条有3种状态

## 1. 布局中使用

	<!-- 自定义进度条图片(三种类型) -->
    <ProgressBar 
        android:id="@+id/pb_bar"
        android:progressDrawable="@drawable/progress_bg"
        style="@android:style/Widget.ProgressBar.Horizontal"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

## 2. 背景

security_progress_bg是 背景
secondaryProgress 是   前景色

	<?xml version="1.0" encoding="utf-8"?>
	<layer-list xmlns:android="http://schemas.android.com/apk/res/android" >
		<!-- 进度条为0%,使用的图片效果 -->
	    <item android:id="@android:id/background"	android:drawable="@drawable/security_progress_bg"/>
		<!-- 进度条为0%-100%之间,使用的图片效果 -->
		<item android:id="@android:id/secondaryProgress" android:drawable="@drawable/security_progress"/>
		<!-- 进度条为100%,使用的图片效果 -->
		<item android:id="@android:id/progress" android:drawable="@drawable/security_progress"/>
	</layer-list>
