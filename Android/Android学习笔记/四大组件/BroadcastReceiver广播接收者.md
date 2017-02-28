# BroadcastReceiver广播接收者

[TOC]

#1.简单介绍

1. Android系统内部相当于已经有一个电台 定义了好多的广播事件  
比如外拨电话 短信到来 sd卡状态  电池电量变化....

2. 谷歌工程师给我们定义了一个组件专门用来接收这些事件的

3. 谷歌工程师为什么要设计这样一个组件  目的就是为了方便开发者进行开发 

4. 当在应用程序中注册了一个广播监听器,即使是退出了程序,当广播事件发生时,该程序的进程会被创建,
执行onReceive()方法.

#2.使用(静态广播)

1. 创建一个类,类名类似于xxReceiver,继承自BroadcastReceiver.覆写onReceive()方法

2. 需要在清单文件中配置

3. 可以监听很多事件,这些事件可以通过在清单文件中配置<intent-filter>中的action,可以配置多个action,一个
action就是一个广播事件,可以监听多个广播.然后在类中,监听到了广播则调用onReceive()方法,比如像下面这样

		<receiver android:name=".broadcast.SmsBroadcast">
            <intent-filter android:priority="1000">
                <action android:name="android.provider.Telephony.SMS_RECEIVED"/>
            </intent-filter>
	    </receiver>

4. getResultData();Retrieve the current result data, as set by the previous receiver. Often this is null.

5. setResultData("17951"+currentNumber);Change the current result data of this broadcast; 

6. 在onReceive()判断是哪个广播事件

   //[1]获取到当前广播的事件类型 

	`String action = intent.getAction();`

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


# 6. 本地广播
> Android引入了一套本地广播机制,使用这个机制发出的广播只能够在应用程序的内部进行传递,并且广播接收器也只能接收
来自本程序发出的广播,这样所有的安全性问题就都不存在了.主要是使用了一个LocalBroadcastManager来对广播进行管理.

	public class MainActivity extends AppCompatActivity {

	    private IntentFilter intentFilter;
	    private LocalReceiver localReceiver;
	    private LocalBroadcastManager localBroadcastManager;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	
	        //1. 获取LocalBroadcastManager实例
	        localBroadcastManager = LocalBroadcastManager.getInstance(this);
	
	        Button bt_send_broadcast = (Button) findViewById(R.id.bt_send_broadcast);
	        bt_send_broadcast.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                //2. 发送本地广播
	                Intent intent = new Intent();
	                intent.setAction("com.xfhy.localbroadcast");
	                intent.putExtra("name","xfhy");   //封装一条数据到Intent对象中
	                localBroadcastManager.sendBroadcast(intent);
	            }
	        });
	        //3. 创建广播接收者    注册本地广播监听器
	        localReceiver = new LocalReceiver();
	        intentFilter = new IntentFilter();
	        intentFilter.addAction("com.xfhy.localbroadcast");
	        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
	    }
	
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        //4. 取消注册广播
	        localBroadcastManager.unregisterReceiver(localReceiver);
	    }
	}

本地广播是无法通过静态注册的方式来接收的.
使用本地广播的几点优势:

- 可以明确地知道正在发生的广播不会离开我们的程序,因此不必担心机密数据泄露
- 其他的程序无法将广播发送到我们程序的内部,因此不需要担心会有安全漏洞的隐患
- 发生本地广播比发送系统全局广播将会更加高效



## 注意
不要在onReceive()方法中添加过多的逻辑或者进行任何的耗时操作,因为在广播接收器中是不允许开启线程的,当onReceive()方法运行了较长时间而没有结束时,程序就会报错.因此,广播接收器更多的是扮演一种打开程序其他组件的角色,比如创建一条状态栏通知,或者启动一个服务等.