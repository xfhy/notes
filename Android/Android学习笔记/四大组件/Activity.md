# Activity

[TOC]

#1.安卓四大组件:Activity  广播接收者(BroadCastReceiver)   服务(Service)  内容提供者(ContentProvider)
>都需要在清单文件中配置
#2.Intent

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
		3.然后需要在调用者中写到
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// 如果是用户按下的确定之后才返回到这里 而不是按返回键到这里
			if (resultCode == Activity.RESULT_OK) {
				switch (requestCode) {
				case SECOND_ACTIVITY_CODE:
					tv_second_info.setText(data.getStringExtra("info"));
					break;
				default:
					break;
				}
			}
		}

#3.Activity的生命周期 (必须掌握)

  Activity类中定义了七个回调方法，覆盖了活动生命周期的每一个环节，下面我来一一介绍下这七个方法。

### 1.onCreate()

这个方法你已经看到过很多次了，每个活动中我们都重写了这个方法，它会在活动第一次被创建的时候调用。你应该在这个方法中完成活动的初始化操作，比如说加载布局、绑定事件等。

### 2.onStart()

这个方法在活动由不可见变为可见的时候调用。

### 3.onResume()

这个方法在活动准备好和用户进行交互的时候调用。此时的活动一定位于返回栈的栈顶，并且处于运行状态。

### 4.onPause()

这个方法在系统准备去启动或者恢复另一个活动的时候调用。我们通常会在这个方法中将一些消耗CPU的资源释放掉，以及保存一些关键数据，但这个方法的执行速度一定要快，不然会影响到新的栈顶活动的使用。

### 5.onStop()

这个方法在活动完全不可见的时候调用。它和onPause()方法的主要区别在于，如果启动的新活动是一个对话框式的活动，那么onPause()方法会得到执行，而onStop()方法并不会执行。

### 6.onDestroy()

这个方法在活动被销毁之前调用，之后活动的状态将变为销毁状态。

### 7.onRestart()

这个方法在活动由停止状态变为运行状态之前调用，也就是活动被重新启动了。

以上七个方法中除了onRestart()方法，其他都是两两相对的，从而又可以将活动分为三种生存期。
![生命周期图](http://images2015.cnblogs.com/blog/15207/201512/15207-20151230134402026-2097191680.jpg)

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
				activities.clear();
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


# 7. 保存临时数据(onSaveInstanceState()方法)

Activity中还提供了一个onSaveInstanceState()回调方法.这个方法可以保证在活动被回收之前一定会被调用,因此我们可以通过这个方法来
解决活动被回收时临时数据得不到保存的问题.然后在onCreate()中通过Bundle对象来取数据,注意判断是否为null.

# 官方API介绍

# 8. 一般一个Activity需要重写onCreate()和onPause()方法,onPause()是您处理用户离开你的活动。最重要的是，用户所做的任何更改应该在这一点上（通常在致力于 ContentProvider保持数据）。
