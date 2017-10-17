# android设置圆形 带selector状态按钮

> 源博客地址: http://blog.csdn.net/new_abc/article/details/50115745

	<?xml version="1.0" encoding="utf-8"?>
	<selector xmlns:android="http://schemas.android.com/apk/res/android">
	
	    <!--九宫格界面的子项点击时的selector-->
	    <item  android:state_focused="true"
	           android:state_enabled="true"
	           android:state_pressed="false">
	        <shape
	            xmlns:android="http://schemas.android.com/apk/res/android"
	            android:shape="oval">
	            <solid android:color="@color/colorGray"/>
	            <size
	                android:width="80dp"
	                android:height="80dp"/>
	
	        </shape>
	    </item>
	    <item android:state_enabled="true"
	          android:state_pressed="true" >
	        <shape
	            xmlns:android="http://schemas.android.com/apk/res/android"
	            android:shape="oval">
	            <solid android:color="@color/colorGray"/>
	            <size
	                android:width="80dp"
	                android:height="80dp"/>
	
	        </shape>
	    </item>
	    <item android:state_enabled="true"
	          android:state_checked="true">
	        <shape
	            xmlns:android="http://schemas.android.com/apk/res/android"
	            android:shape="oval">
	            <solid android:color="@color/colorGray"/>
	            <size
	                android:width="80dp"
	                android:height="80dp"/>
	
	        </shape>
	    </item>
	    <item android:state_focused="false"
	          android:state_enabled="true"
	          android:state_pressed="true" >
	        <shape
	            xmlns:android="http://schemas.android.com/apk/res/android"
	            android:shape="oval">
	            <solid android:color="@color/colorGray"/>
	            <size
	                android:width="80dp"
	                android:height="80dp"/>
	
	        </shape>
	    </item>
	    <item android:state_enabled="true" android:state_focused="true"
	          android:state_pressed="false">
	        <shape
	            xmlns:android="http://schemas.android.com/apk/res/android"
	            android:shape="oval">
	            <solid android:color="@color/colorGray"/>
	            <size
	                android:width="80dp"
	                android:height="80dp"/>
	
	        </shape>
	    </item>
	    <item android:color="@color/colorTransparent" >
	        <shape
	            xmlns:android="http://schemas.android.com/apk/res/android"
	            android:shape="oval">
	            <solid android:color="@color/colorTransparent"/>
	            <size
	                android:width="80dp"
	                android:height="80dp"/>
	
	        </shape>
	    </item>
	
	</selector>

## 2. selector color

selector还可以使用在color上,类似于下面这样.然后在button 上使用:`android:textColor="@color/selector_txt_guide"`即可(需要建立res/color文件夹).

	<?xml version="1.0" encoding="utf-8"?>
	<selector xmlns:android="http://schemas.android.com/apk/res/android">
	
	    <item android:color="@color/black" android:state_pressed="true"/>
	    <item android:color="@color/white"/>
	
	</selector>

