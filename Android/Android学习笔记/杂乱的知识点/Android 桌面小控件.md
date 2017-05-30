# Android 桌面小控件

> 比如系统自带的日历,桌面时间等,都是桌面小控件,App Widget.

![](http://olg7c0d2n.bkt.clouddn.com/17-5-21/88058701-file_1495329738925_13d3b.png)

# 1.生成桌面小部件

1.来到[官方文档](https://developer.android.com/guide/topics/appwidgets/index.html#Manifest),查看API指南
`App Components`->`APP Widgets`模块.

2.可以看到,首先叫我们定义一个AppWidgetProvider class,

	public class ExampleAppWidgetProvider extends AppWidgetProvider {
	
	}

3.然后在AndroidManifest.xml文件中配置

	<receiver android:name="ExampleAppWidgetProvider" >
	    <intent-filter>
	        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
	    </intent-filter>
	    <meta-data android:name="android.appwidget.provider"
	               android:resource="@xml/example_appwidget_info" />
	</receiver>

4.然后需要在`res/xml/`下创建一个文件`example_appwidget_info.xml`,内容如下:

	<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
	    android:minWidth="40dp"
	    android:minHeight="40dp"
	    android:updatePeriodMillis="86400000"
	    android:previewImage="@drawable/preview"
	    android:initialLayout="@layout/example_appwidget"
	    android:configure="com.example.android.ExampleAppWidgetConfigure"
	    android:resizeMode="horizontal|vertical"
	    android:widgetCategory="home_screen">
	</appwidget-provider>

参数的意思:

- android:minHeight="72dp"//能被调整的最小宽高，若大于minWidth minHeight 则忽略    
- android:updatePeriodMillis="86400000"//更新周期,毫秒,最短默认半小时    
- android:previewImage="@drawable/preview"//选择部件时 展示的图像,3.0以上使用,默认是ic_launcher    
- android:initialLayout="@layout/example_appwidget"//布局文件
- android:configure="com.example.android.ExampleAppWidgetConfigure"//添加widget之前,先跳转到配置的activity进行相关参数配置,这个我们暂时用不到       
- android:resizeMode="horizontal|vertical"//widget可以被拉伸的方向。horizontal表示可以水平拉伸，vertical表示可以竖直拉伸
- android:widgetCategory="home_screen|keyguard"//分别在屏幕主页和锁屏状态也能显示(4.2+系统才支持)
- android:initialKeyguardLayout="@layout/example_keyguard"//锁屏状态显示的样式(4.2+系统才支持)

# 2.桌面小部件生命周期

	public class MyAppWidgetProvider extends AppWidgetProvider {

	    private static final String TAG = "MyAppWidgetProvider";
	
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        //调用此方法意味着将调用到AppWidgetProvider上的各种其他方法。
	        LogUtil.d(TAG, "onReceive ");
	        super.onReceive(context, intent);
	    }
	
	    @Override
	    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
	        //当这个AppWidget提供者的实例已经从备份还原时，响应ACTION_APPWIDGET_RESTORED广播而调用。
	        LogUtil.d(TAG, "onRestored");
	        super.onRestored(context, oldWidgetIds, newWidgetIds);
	    }
	
	    @Override
	    public void onEnabled(Context context) {
	        /*
	        * 当该提供程序的AppWidget被实例化时，响应ACTION_APPWIDGET_ENABLED广播而被调用。
	        * 覆盖此方法来实现您自己的AppWidget功能。
	
	        当该提供者的最后一个AppWidget被删除时，ACTION_APPWIDGET_DISABLED由AppWidget管理器发送，
	        并且onDisabled（Context）被调用。
	        如果之后，再次创建此提供程序的AppWidget，将再次调用onEnabled（）。
	        * */
	        LogUtil.d(TAG, "onEnabled   第一个小部件被创建");
	        super.onEnabled(context);
	    }
	
	    @Override
	    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	        //当这个AppWidget提供程序被要求为一组AppWidget提供RemoteView时，
	        // 调用响应ACTION_APPWIDGET_UPDATE和ACTION_APPWIDGET_RESTORED广播。
	        LogUtil.d(TAG, "onUpdate   新添加了一个小部件");
	        super.onUpdate(context, appWidgetManager, appWidgetIds);
	    }
	
	    @Override
	    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int
	            appWidgetId, Bundle newOptions) {
	        //当响应ACTION_APPWIDGET_OPTIONS CHANGED广播时调用此窗口小部件已经以新的大小布局。
	        // 部件大小被改变  添加部件时也会被调用,大小从0到有
	        LogUtil.d(TAG, "onAppWidgetOptionsChanged   部件大小被改变");
	        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	    }
	
	    @Override
	    public void onDeleted(Context context, int[] appWidgetIds) {
	        //当一个或多个AppWidget实例被删除时，响应于ACTION_APPWIDGET_DELETED广播而被调用。
	        LogUtil.d(TAG, "onDeleted   删除了一个部件");
	        super.onDeleted(context, appWidgetIds);
	    }
	
	    @Override
	    public void onDisabled(Context context) {
	        //调用响应ACTION_APPWIDGET_DISABLED广播，当该提供者的最后一个AppWidget实例被删除时发送。
	        LogUtil.d(TAG, "onDisabled   当最后一个部件被删除了,则会调用此方法");
	        super.onDisabled(context);
	    }
	}

### 1.当创建第一个部件的时候,会依次调用

1. onReceive 
- onEnabled   第一个小部件被创建
- onReceive 
- onUpdate   新添加了一个小部件
- onReceive 
- onAppWidgetOptionsChanged   部件大小被改变

### 2.接着上面,当再次添加一个部件

1. onReceive 
2. onUpdate   新添加了一个小部件
3. onReceive 
4. onAppWidgetOptionsChanged   部件大小被改变

### 3. 接着上面,删除了一个部件

1. onReceive 
2. onDeleted   删除了一个部件

### 4. 接着上面,再删除一个部件

1. onReceive 
2. onDeleted   删除了一个部件
3. onReceive 
4. onDisabled   当最后一个部件被删除了,则会调用此方法
