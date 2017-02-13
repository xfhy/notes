# Activity

[TOC]

#1.安卓四大组件:Activity  广播接收者(BroadCastReceiver)   服务(Service)  内容提供者(ContentProvider)
>都需要在清单文件中配置
#2.Intent
```java
		1.传递数据putExtra("name",name);   在另一个Activity读取数据:Intent intent = getIntent();
			String name = intent.getStringExtra("name");
			String sex = intent.getStringExtra("sex");
		2.如果想要取开启的Activity的界面的数据 用 startActivityForResult();然后在自己的这个Activity中
		覆写onActivityResult()方法获取,在另一个界面调用finish()的时候这个方法会被调用.在另一个界面只需要
		像下面这样既可传递数据:
			    //将数据放到Intent对象中
				Intent intent = new Intent();
				intent.putExtra("number", number);
				//设置返回的验证码  和 数据
				setResult(10, intent);
				//关闭当前页面  
				finish();
```
#3.Activity的生命周期 (必须掌握)
- onCreate() 方法 当Activity第一次启动的时候调用
- onDestroy() 方法 当Activity销毁的时候调用
- onStart() 方法 当Activity变成可见的时候调用 
- onStop() 方法 当activity 不可见的时候调用
- onResume()方法 当activity可以获取焦点的时候  当界面的按钮可以被点击了
- onPause()方法 当失去焦点的时候调用 当按钮不了可以被点击的时候调用
- onRestart()当界面重新启动的时候调用   调完这个方法接着调用onStart()方法

		当横竖屏切换的时候会重新创建Activity界面(onCreate()->onStart()->OnResume())
		在android配置文件中当前Activity的里面写入如下:
		android:screenOrientation="portrait"可将界面锁定成竖屏
		android:screenOrientation="landscape"可将界面锁定成横屏
#4.任务栈
		栈:先进后出 
		  队列:先进先出 
		  Task 打开一个Activity叫进栈  关闭一个activit出栈   
		  任务栈是用来维护Activity的 是用来维护用户的操作体验
		  我们操作的Activity永远是任务栈的栈顶的Activity
		  说应用程序退出了 实际上任务栈清空了
#5.Activity四种启动模式(面试时经常问到,区别和应用场景)
* standard(默认):系统不会在乎这个Activity是否已经在栈中存在,每次启动都会创建一个该Activity的一个新的实例.
* singleTop:在启动Activity时如果发现返回栈的栈顶已经是该Activity,则认为可以直接使用它,
	不会再创建新的Activity实例.不过,当Activity并未处于栈顶位置时,这时再启动Activity,还是会创建新的实例.
	应用场景：浏览器的书签
* singleTask:每次启动该Activity时系统首先会在返回栈中检查是否存在该Activity的实例,如果发现已经存在则直接使用该
	实例,并把在这之上的所有活动通通出栈,如果没有发现就会创建一个新的Activity实例.
	应用场景：浏览器的activity
* singleInstance:会启用一个新的返回栈来管理这个Activity(其实如果singleTask模式指定了不同的taskAffinity,也会
	启动一个新的返回栈).     应用:其他程序可以和我们的实例可以共享这个Activity的实例    来电页面
	
#6.Activity的最佳实践
##6.1知晓当前是在哪一个Activity
		根据当前的界面即可判断出是哪一个Activity,首先新建一个BaseActivity,代码如下:
			public class BaseActivity extends Activity {
				@Override
				protected void onCreate(Bundle savedInstanceState) {
					super.onCreate(savedInstanceState);
					Log.i("xfhy",getClass().getSimpleName());  //获取当前类的名称
				}
			}
		然后让BaseActivity成为当前项目中所有Activity的父类.现在每当我们进入一个Activity的界面,该活动的类名就会被打印出来,
		这样就可以时时刻刻知晓当前界面对应的是哪一个活动.
##6.2随时随地退出程序
		只需要一个专门的集合类对所有的活动进行管理即可,新建一个ActivityCollector类作为活动管理器.
		public class ActivityCollector {
			// 将所有的活动添加到这里来
			public static List<Activity> activities = new ArrayList<Activity>();
		
			public static void addActivity(Activity activity) {
				activities.add(activity);
			}
		
			public static void removeActivity(Activity activity) {
				activities.remove(activities);
			}
		
			public static void finishAll() // 销毁所有活动
			{
				for (Activity activity : activities) {
					if (!activity.isFinishing()) {
						activity.finish();
					}
				}
			}
		}
		接下来修改BaseActivity中的代码,onCreate()方法中加入ActivityCollector.addActivity(this);在onDestroy()中加入
		ActivityCollector.removeActivity(this);
##6.3启动活动的最佳写法
		在Activity中加入如下代码:
		public static void acionStart(Context context,String data1,String data2){
			Intent intent = new Intent(context,SecondActivity.class);
			intent.putExtra("param1",data1);
			intent.putExtra("param2",data2);
			context.startActivity(intent);
		}
		使用方法:SecondActivity.actionStart(FirstActvity.this,"data1","data2");
