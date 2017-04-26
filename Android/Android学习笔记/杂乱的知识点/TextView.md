# TextView

## 1.选择器  TextView的坑

默认的TextView是不可点击的,必须设置setOnClickListener()才行.
比如下面的selector,如果没有设置setOnClickListener,则选中时的颜色是不会变的.

	<selector xmlns:android="http://schemas.android.com/apk/res/android">

	    <!--选中的背景-->
	    <item android:state_pressed="true" android:drawable="@android:color/darker_gray"/>
	    <!--默认是透明的-->
	    <item android:drawable="@color/colorPink"/>
	
	</selector>


