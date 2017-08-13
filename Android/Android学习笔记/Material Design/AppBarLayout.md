# AppBarLayout

## 1.demo

	<?xml version="1.0" encoding="utf-8"?>
	<android.support.design.widget.CoordinatorLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    tools:context="com.xfhy.design.MainActivity">
	
	    <!--
	        - 协调者布局 CoordinatorLayout ,内部被划分成2个区域
	    - AppBar区域
	    - 动作产生区域(滚动区域)
	
	    -->
	
	    <android.support.design.widget.AppBarLayout
	        android:id="@+id/design_app_bar"
	        android:layout_width="match_parent"
	        android:layout_height="250dp">
	
	        <ImageView
	            android:layout_width="match_parent"
	            android:layout_height="200dp"
	            android:scaleType="centerCrop"
	            android:src="@drawable/timg"
	            app:layout_scrollFlags="scroll|snap"/>
	
	        <!--
	            appbar区域使用:
	            app:layout_scrollFlags="scroll|exitUntilCollapsed"添加标记,来配合动作app:layout_behavior
	        -->
	        <android.support.design.widget.TabLayout
	            android:id="@+id/design_tablayout"
	            android:layout_width="match_parent"
	            android:layout_height="50dp"/>
	
	    </android.support.design.widget.AppBarLayout>
	
	    <!--
	        滚动区域可以直接使用的控件
	          - NestedScrollView
	          - RecyclerView
	    -->
	    <android.support.v4.widget.NestedScrollView
	        android:id="@+id/design_scroll_view"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        app:layout_behavior="@string/appbar_scrolling_view_behavior">
	
	        <TextView
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            android:layout_margin="10dp"
	            android:text="@string/text_scorll_view"
	            android:textColor="#000000"
	            android:textSize="18sp"/>
	
	    </android.support.v4.widget.NestedScrollView>
	
	    <!--
	        android:elevation="10dp" 海拔,阴影
	         app:fabSize="normal"  大小
	    -->
	    <android.support.design.widget.FloatingActionButton
	        android:id="@+id/design_fab"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_gravity="end|bottom"
	        android:layout_marginBottom="30dp"
	        android:layout_marginEnd="30dp"
	        app:fabSize="normal"
	        app:srcCompat="@android:drawable/ic_input_add"
	        android:elevation="10dp"
	        />
	
	</android.support.design.widget.CoordinatorLayout>


