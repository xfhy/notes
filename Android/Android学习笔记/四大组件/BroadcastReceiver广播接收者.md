# BroadcastReceiver广播接收者

[TOC]

#1.简单介绍
		1.Android系统内部相当于已经有一个电台 定义了好多的广播事件  
			比如外拨电话 短信到来 sd卡状态  电池电量变化....
		2.谷歌工程师给我们定义了一个组件专门用来接收这些事件的
		3.谷歌工程师为什么要设计这样一个组件  目的就是为了方便开发者进行开发 
		4.当在应用程序中注册了一个广播监听器,即使是退出了程序,当广播事件发生时,该程序的进程会被创建,
			执行onReceive()方法.
#2.使用
		1.创建一个类,类名类似于xxReceiver,继承自BroadcastReceiver.覆写onReceive()方法
		2.需要在清单文件中配置
		3.可以监听很多事件,这些事件可以通过在清单文件中配置<intent-filter>中的action,可以配置多个action,一个
			action就是一个广播事件,可以监听多个广播.然后在类中,监听到了广播则调用onReceive()方法
		4.getResultData();Retrieve the current result data, as set by the previous receiver. Often this is null.
		5.setResultData("17951"+currentNumber);Change the current result data of this broadcast; 
		6.在onReceive()判断是哪个广播事件
			//[1]获取到当前广播的事件类型 
			String action = intent.getAction();
			//[2]对action做一个判断 
			if("android.intent.action.MEDIA_UNMOUNTED".equals(action)){
				System.out.println("说明sd卡 卸载了");
				
			}else if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
				
				System.out.println("说明sd卡挂载了");
			}
#3.开启Activity
		1.如果在广播里面开启Activity 要设置一个任务栈环境
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
#4.自定义广播

##4.1无序广播
		1.类似新闻联播:广播不可以被终止  数据不可以被修改 
		2.发送无序广播:
			//1.创建Intent对象
			Intent intent = new Intent();
			//2.设置action
			intent.setAction("com.itheima.news");
			//3.设置需要传送的数据
			intent.putExtra("name", "新闻联播来啦!!!");
			//4.发送无序广播
			//Broadcast the given intent to all interested BroadcastReceivers. 
			sendBroadcast(intent);
		3.接收无序广播:
			//在intent-filter中这样声明
			 <action android:name="com.itheima.news"/>

			//1.取出广播携带的数据
			String content = intent.getStringExtra("name");
		
##4.2有序广播
		1.类似中央发送的红头文件  按照优先级一级一级的接收 有序广播可以被终止 数据可以被修改 
		2.发送有序广播:
			//发送一条有序广播
			Intent intent = new Intent();
			//设置一个action
			intent.setAction("com.itheima.rice");
			
			/*
			 * Parameters
				intent 所有匹配这个意图的都将接收到这条广播
				receiverPermission 	接收的权限
				resultReceiver 	最终的接收者    这个不需要在清单文件中配置
				scheduler 	A custom Handler with which to schedule the resultReceiver callback; if null it will be scheduled in the Context's main thread.
				initialCode 	An initial value for the result code. Often Activity.RESULT_OK.
				initialData 	An initial value for the result data. Often null.
				initialExtras 	An initial value for the result extras. Often null.
			 * */
			sendOrderedBroadcast(intent, null, new FinalReceiver(), null, Activity.RESULT_OK, "习大大给每个村民发了1000斤大米", null);
		3.接收有序广播:
			首先需要到清单文件中配置先后顺序,eg:android:priority="1000",数值是-1000~1000(其实写个10000也无所谓),
			数值越大优先级越高(数值越大的就先接收到).
			<receiver android:name="com.itheima.receiverice.receiver.ProvinceReceiver">
	            <intent-filter android:priority="1000">
	                <action android:name="com.itheima.rice"/>
	            </intent-filter>
	        </receiver>
	        <receiver android:name="com.itheima.receiverice.receiver.CityReceiver">
	            <intent-filter android:priority="500">
	                <action android:name="com.itheima.rice"/>
	            </intent-filter>
	        </receiver>
	        <receiver android:name="com.itheima.receiverice.receiver.CountryReceiver">
	            <intent-filter android:priority="200">
	                <action android:name="com.itheima.rice"/>
	            </intent-filter>
	        </receiver>
			
			然后在onReceive()方法中接收数据:
				// [1]获取到发送广播携带的数据
				String content = getResultData();
				// [2]展示到Toast上
				Toast.makeText(context, "省:" + content, Toast.LENGTH_SHORT).show();
				// [3]修改数据 (扣留大米)
				setResultData("习大大给每个村民发了500斤大米");
	    4.终止有序广播:abortBroadcast();

#5.特殊广播接收者

##动态注册广播
		比如操作特别频繁的广播事件 屏幕的锁屏和解锁 电池电量的变化 这样的广播接收者在清单文件里面注册无效
		,这时候需要动态注册.注册代码如下:
			//1.动态的去注册屏幕解锁 和锁屏的广播
			screenReceiver = new ScreenReceiver();
			//2.创建IntentFilter对象
			IntentFilter intentFilter = new IntentFilter();
			//3.添加要注册的action
			intentFilter.addAction("android.intent.action.SCREEN_OFF");
			intentFilter.addAction("android.intent.action.SCREEN_ON");
			
			//注册广播接收者
			this.registerReceiver(screenReceiver, intentFilter);
		然后在Activity的onDestroy()中需要取消注册:this.unregisterReceiver(screenReceiver);

#特殊
		1.当监听sd卡的卸载挂载事件时
			    <action android:name="android.intent.action.MEDIA_MOUNTED" />
                <action android:name="android.intent.action.MEDIA_UNMOUNTED" />
                
                <!--想让上面的这2个事件生效 必须的加上这样的一个data   -->
                <data android:scheme="file"/>
		2.当监听安装或者卸载应用时
				<action android:name="android.intent.action.PACKAGE_INSTALL" />
                <action android:name="android.intent.action.PACKAGE_ADDED" />
                <action android:name="android.intent.action.PACKAGE_REMOVED" />

				<!--想让上面的这3个事件生效 必须的加上这样的一个data   -->
                <data android:scheme="package" />
		