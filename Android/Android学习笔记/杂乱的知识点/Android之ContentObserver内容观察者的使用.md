# Android之ContentObserver内容观察者的使用

转载:原博客地址:http://blog.csdn.net/jason0539/article/details/22581297

ContentObserver——内容观察者，目的是观察(捕捉)特定Uri引起的数据库的变化，继而做一些相应的处理，它类似于

   数据库技术中的触发器(Trigger)，当ContentObserver所观察的Uri发生变化时，便会触发它。

（1）注册：

    public final void  registerContentObserver(Uri uri, boolean notifyForDescendents, ContentObserver observer)。

     功能：为指定的Uri注册一个ContentObserver派生类实例，当给定的Uri发生改变时，回调该实例对象去处理。
 

（2）卸载：      public final void  unregisterContentObserver(ContentObserver observer)

      功能：取消对给定Uri的观察

下面是一个监听收信箱的Demo

首先是一个监听类：

	package jason.observer;

	import android.content.Context;
	import android.database.ContentObserver;
	import android.database.Cursor;
	import android.net.Uri;
	import android.os.Handler;
	
	public class SMSContentObserver extends ContentObserver {
	
		Context context;
		Handler handler;
	
		public SMSContentObserver(Context c, Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
			this.context = c;
			this.handler = handler;
		}
	
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			Uri outMMS = Uri.parse("content://sms/inbox");
			//desc 降序	 asc 升序
			Cursor cursor = context.getContentResolver().query(outMMS, null, null, null, "date ASC");
			if(cursor != null){
				System.out.println("the number is " + cursor.getCount());
				StringBuilder  builder = new StringBuilder();
				while(cursor.moveToNext()){
					builder.append("发件人信息:" + cursor.getString(cursor.getColumnIndex("address")));
					builder.append("信息内容："+cursor.getString(cursor.getColumnIndex("body"))+"\n");
				}
				cursor.close();
				String builder2 = builder.toString();
				handler.obtainMessage(1, builder2).sendToTarget();
			}
		}
	
	}

（2）注册监听类的activity

	package jason.observer;

	import android.app.Activity;
	import android.net.Uri;
	import android.os.Bundle;
	import android.os.Handler;
	import android.widget.TextView;
	
	public class ObserverActivity extends Activity {
		SMSContentObserver contentObserver;
		TextView tv_number;
		TextView tv_content;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_observer);
			tv_content = (TextView) findViewById(R.id.tv_content);
			tv_number = (TextView) findViewById(R.id.tv_number);
			contentObserver = new SMSContentObserver(this, handler);
			Uri uri = Uri.parse("content://sms");
			getContentResolver().registerContentObserver(uri, true, contentObserver);
		}
	
		Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 1:
					String sb = (String) msg.obj;
					tv_content.setText(sb);
					break;
	
				default:
					break;
				}
			};	
		};
	
	}

最后别忘记了加入 读取消息的权限

   <uses-permission Android:name="android.permission.READ_SMS"/>