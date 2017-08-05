
[TOC]

#1.adb命令
	adb devices 显示所有设备

	adb -s 设备名 push 电脑端文件路径 手机端需要放置的文件路径   :把电脑上的文件放到手机上

	adb -s 设备名 pull 手机端文件 电脑端需要存放的文件路径     :把手机上的文件存到电脑端

	adb install [-r] apkpath(apk文件路径) : 安装apk到手机 -r:强制安装

	adb uninstall packagename  :卸载手机上的app

	adb kill-server :结束adb服务的连接
	adb start-server :开启adb服务的连接
	netstat -oan :查看端口

	adb shell + ls -l ：查看当前设备的目录结构    su:获取root权限
	adb shell+ logcat :查看系统运行中的日志信息
	
	dumpsys activity | grep "mFocusedActivity"  查看当前显示的Activity
	
#2.eclipse快捷键 
	Ctrl+2+L:快速将返回的变量定义成变量
	alt+shift+r :快速重命名变量
#3.android studio快捷键
	alt+shift+m 快速将选中的代码导出成方法
	alt+shift+l 快速将返回的变量定义成变量
#4.按钮点击

		1.匿名内部类
		2.创建一个内部类,实现OnClickListener接口
		3.该类实现OnClickListener接口(不需要额外创建引用),这种方式适合按钮比较多的情况,一般在公司采用该方式(无论多少个按钮,扩展比较方便).
		4.在xml布局中写一个属性android:onClick="方法名",然后在该布局所对应的Activity中写一个与之方法名对应的方法( public void 方法名(View v) )即可.(一般不这样用).比较适合做简单的测试.
	
#5.调试技巧

如果程序发生错误,一般先看LogCat中的Caused by,这个一般就是出错的原因;如果没有这个再去从上往下看其他的信息.

#6.Android中常用布局(多练习) div+css
		
### 1.线性布局	LinearLayout

- gravity:对齐方式,子控件相对于当前控件的对齐方式
- layout_gravity:当前控件相对于父控件的对齐方式
- margin:当前控件相对于四周的间距
- padding:当前控件内部的边距

### 2.相对布局	RelativeLayout 都是从左上角开始布局,要控制位置,需要设置每个控件相对于其他控件的位置

- layout_below:位于哪个控件的下方
- layout_above:位于哪个控件的上方
- layout_toRightOf:指定当前控件位于某个控件的右方
- layout_alignParentRight:当前控件基于父窗体的对齐方式
- layout_centerInParent:基于父窗体的居中

### 3.帧布局	FrameLayout

帧布局中的子控件都是一层一层的向上叠加
	
### 4.百分比布局(一种全新的布局方式)

百分比布局只为FrameLayout和RelativeLayout进行了功能扩展,提供了PercentFrameLayout和
PercentRelativeLayout这两个全新的布局.

打开app/build.gradle文件,在dependencies闭包中添加如下内容

		compile 'com.android.support:percent:24.2.1'

然后在布局中这样写
		
	<?xml version="1.0" encoding="utf-8"?>
	<android.support.percent.PercentFrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:id="@+id/activity_main"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent">
	
	    <Button
	        android:id="@+id/bt_btn1"
	        android:text="Button1"
	        android:layout_gravity="left|top"
	        app:layout_widthPercent="50%"
	        app:layout_heightPercent="50%"
	        />
	
	    <Button
	        android:id="@+id/bt_btn2"
	        android:text="Button2"
	        android:layout_gravity="right|top"
	        app:layout_widthPercent="50%"
	        app:layout_heightPercent="50%"
	        />
	
	    <Button
	        android:id="@+id/bt_btn3"
	        android:text="Button3"
	        android:layout_gravity="left|bottom"
	        app:layout_widthPercent="50%"
	        app:layout_heightPercent="50%"
	        />
	
	    <Button
	        android:id="@+id/bt_btn4"
	        android:text="Button4"
	        android:layout_gravity="right|bottom"
	        app:layout_widthPercent="50%"
	        app:layout_heightPercent="50%"
	        />
	
	</android.support.percent.PercentFrameLayout>


### 5.表格布局	TableLayout
		基本不用,在公司.前3个已经够了.

### 6.绝对布局(被抛弃) AbsoluteLayout
