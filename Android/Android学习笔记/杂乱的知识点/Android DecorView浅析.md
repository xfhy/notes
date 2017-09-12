# Android DecorView浅析

> 原作:http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2013/0322/1054.html

一、DecorView为整个Window界面的最顶层View。

二、DecorView只有一个子元素为LinearLayout。代表整个Window界面，包含通知栏，标题栏，内容显示栏三块区域。

三、LinearLayout里有两个FrameLayout子元素。

  (20)为标题栏显示界面。只有一个TextView显示应用的名称。也可以自定义标题栏，载入后的自定义标题栏View将加入FrameLayout中。

  (21)为内容栏显示界面。就是setContentView()方法载入的布局界面，加入其中。

设置自定义的TItleBar

	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	setContentView(R.layout.custom_title);
	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
