# Android 中需要掌握的高级技巧

![](http://upload-images.jianshu.io/upload_images/3532835-d1e86aaff5276287.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

# 1. 获取全局Context

> Android提供了一个Application类,每当应用程序启动的时候,系统就会自动将这个类进入初始化.而我们可以定制一个自己的Application类,以便于管理应用程序内一些全局的状态信息,比如说全局Context;

1.代码如下

	public class MyApplication extends Application {

	    private static Context context;
	
	    @Override
	    public void onCreate() {
	        context = getApplicationContext();
	    }
	    
	    public static Context getContext() {
	        return context;
	    }
	
	}

2.接下来需要告知系统,当程序启动的时候应该初始化MyApplication类.
需要在清单文件中写一下:(一定要写完整包名)

	<application
        android:name="com.xfhy.materialtest.MyApplication"
		......
	</application>

3.任何一个项目都只能配置一个Application,对于这种情况(当需要用LitePal的时候,需要在清单文件中配置android:name="org.litepal.LitePalApplication"),LitePal提供了很简单的解决方案,那就是在我们自己的Application中去调用LitePal的初始化方法就可以了.在MyApplication中的onCreate()方法中加入如下代码`LitePal.initialize(context);`

# 2. 使用Intent传递对象

> putExtra()方法中所支持的数据类型是有限的,虽然常用的数据类型是支持的,但是在传递一些自定义对象时,就会发现无从下手.

## 2.1 Serializable 方式 

> Serializable是序列化的意思,表示将一个对象转换成可存储或可传输的状态.至于序列化的方法也很简单,只需要让一个类去实现Serializable这个接口就可以了.

1.比如需要序列化一个Person类

	public class Person implements Serializable{

	    private String name;
	    private int age;
	
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	        this.name = name;
	    }
	
	    public int getAge() {
	        return age;
	    }
	
	    public void setAge(int age) {
	        this.age = age;
	    }
	}

2.传递时这样写
	
	Person person = new Person();
	Intent intent = new Intent(mContext,SecondActivity.class);
	intent.putExtra("person_data",person);

3.接收数据时这样写

	Person person = (Person)getIntent().getSerializableExtra("person_data");

## 2.2 Parcelable 方式



1.写一个Person类,继承自Parcelable.需要实现describeContents()方法和writeToParcel()方法;还需要写一个CREATOR常量.

	public class Person implements Parcelable{

	    private String name;
	    private int age;
	
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	        this.name = name;
	    }
	
	    public int getAge() {
	        return age;
	    }
	
	    public void setAge(int age) {
	        this.age = age;
	    }
	
	    //直接返回0即可
	    @Override
	    public int describeContents() {
	        return 0;
	    }
	
	    //这里需要调用Parcel的writeXXXX方法将Person类中的字段一一写出
	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        dest.writeString(name);   //写出name
	        dest.writeInt(age);       //写出age
	    }
	
	    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>(){
	        @Override
	        public Person createFromParcel(Parcel source) {
	            Person person = new Person();
	
	            //这里读取的顺序一定要与上面的写入顺序一致
	            person.name = source.readString();    //读取name
	            person.age = source.readInt();         //读取age
	            return person;
	        }
	
	        @Override
	        public Person[] newArray(int size) {
	            return new Person[size];
	        }
	    };
	
	}

2.传递时这样写
	
	Person person = new Person();
	Intent intent = new Intent(mContext,SecondActivity.class);
	intent.putExtra("person_data",person);

3.接收数据时这样写

	Person person = (Person)getIntent().getParcelableExtra("person_data");

# 3.定制自己的日志工具

> 虽然 Android 中自带的日志工具功
能非常强大，但也不能说是完全没有缺点，例如在打印日志的控制方面就做得不够好。
打个比方，你正在编写一个比较庞大的项目，期间为了方便调试，在代码的很多地方都
打印了大量的日志。最近项目已经基本完成了，但是却有一个非常让人头疼的问题，之前用
于调试的那些日志，在项目正式上线之后仍然会照常打印，这样不仅会降低程序的运行效率，
还有可能将一些机密性的数据泄露出去。
那该怎么办呢，难道要一行一行把所有打印日志的代码都删掉？显然这不是什么好点
子，不仅费时费力，而且以后你继续维护这个项目的时候可能还会需要这些日志。因此，最
理想的情况是能够自由地控制日志的打印，当程序处于开发阶段就让日志打印出来，当程序
上线了之后就把日志屏蔽掉。

		//只需要控制LEVEL的大小就可以控制输出的等级
		public class LogUtil {
	    public static final int VERBOSE = 1;//啰嗦，等级最低的
	    public static final int DEBUG = 2;//调试
	    public static final int INFO = 3;//信息
	    public static final int WARN = 4;//警告
	    public static final int ERROR = 5;//错误
	    public static final int NOTHING = 6;//什么也不打印出来
	    public static final int LEVEL = VERBOSE;//LEVEL:标准
	    public static void v(String tag,String msg){
	        if(LEVEL<=VERBOSE){//如果大于或者等于定义的标准就打印出来
	            Log.v(tag, msg);
	        }
	    }
	    public static void d(String tag,String msg){
	        if(LEVEL<=DEBUG){
	            Log.d(tag, msg);
	        }
	    }
	    public static void i(String tag,String msg){
	        if(LEVEL<=INFO){
	            Log.i(tag, msg);
	        }
	    }
	    public static void w(String tag,String msg){
	        if(LEVEL<=WARN){
	            Log.w(tag, msg);
	        }
	    }
	    public static void e(String tag,String msg){
	        if(LEVEL<=ERROR){
	            Log.e(tag, msg);
	        }
	    }
	}

# 4. 调试Android程序

> 在调试模式下,程序的运行效率会大大地降低,如果你的断点加在一个比较靠后的位置,需要执行很多的操作才能运行到这个断点,那么前面这些操作就都会有一些卡顿的感觉。Android还提供了另外一种调试的方式，可以让程序随时进入到调试模式。

> 这次不需要选择调试模式来启动程序了，就使用正常的方式来启动程序。把账号和密码输入好，然后点击Android Studio顶部工具栏的Attach debugger to Android process按钮


同志,就是下面这个按钮:

![](http://olg7c0d2n.bkt.clouddn.com/17-3-7/27149673-file_1488861104485_10805.png)

此时会让弹出一个进程选择提示框

![](http://upload-images.jianshu.io/upload_images/3532835-10155841a7aa0f35.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这里目前只列出了一个进程，也就是我们当前程序的进程，选中这个进程，然后点击Ok按钮，就会让这个进程进入到调试模式了。

# 5. 创建定时任务

> Android中的定时任务一般有两种实现方式，一种是使用Java API里提供的Timer类，一种是使用Android的Alarm机制。这两种方式在多数情况下都能实现类似的效果，但Timer有一个明显的短板，它并不太适用于那些需要长期在后台运行的定时任务。我们都知道为了能让电池更加耐用，每种手机都会有自己的休眠策略，Android手机就会在长时间不操作的情况下自动让CPU进入到睡眠状态，这就有可能导致Timer中的定时任务无法正常运行。而Alarm则具有唤醒CPU的功能，它可以保证在大多数情况下需要执行定时任务的时候CPU都能正常工作。需要注意: 这里唤醒CPU和唤醒屏幕完全不是一个概念，千万不要产生混淆。

## 5.1 Alarm机制

1.如果我们要实现一个长时间在后台定时运行的服务，首先新建一个普通的服务，起名叫LongRunningService，然后将触发定时任务的代码写到onStartCommand()方法中。

	public class LongRunningService extends Service
	{
	
	    @Override
	    public IBinder onBind(Intent intent)
	    {
	        return null;
	    }
	
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId)
	    {
	        new Thread(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                //执行具体的逻辑操作
	            }
	        }).start();
	
			//获取AlarmManager实例
	        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	        int anHour = 60 * 60 * 1000; //这是一小时的毫秒数
	        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
	        Intent i = new Intent(this,LongRunningService.class);
	        PendingIntent pi = PendingIntent.getService(this,0,i,0);
	        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
	        return super.onStartCommand(intent, flags, startId);
	    }
	}
我们先是在onStartCommand()方法中开启了一个子线程，这样就可以在这里执行具体的逻辑操作了。之所以要在子线程里执行逻辑操作，是因为逻辑操作也是需要耗时的，如果放在主线程里执行可能会对定时任务的准确性造成轻微的影响。

这样我们就将一个长时间在后台定时运行的服务成功实现了。因为一旦启动了LongRunningService，就会在onStartCommand()方法中设定一个定时任务，这样一小时后将会再次启动LongRunningService，从而也就形成了一个永久的循环，保证LongRunningService的onStartCommand()方法可以每隔一小时就执行一次。

2.最后，只需要在你想要启动定式服务的时候调用如下代码即可：

	Intent intent = new Intent(context,LongRunningService.class);
	context.startService(intent);

**如果你要求Alarm任务的执行时间必须准确无误，Android仍然提供了解决方案。使用AlarmManager的setExact()方法来替代set()方法，就基本可以保证任务能够准时执行了。**

## 5.2 Doze模式

> 于是在Android6.0系统中，谷歌加入了一个全新的Doze模式，从而可以极大幅度地延长电池的使用寿命。首先看一下到底什么是Doze模式。当用户的设备是Android6.0或以上系统时，如果该设备未插接电源，处于静止状态（Android7.0中删除了这一条），且屏幕关闭了一段时间之后，就会进入到Doze模式。在Doze模式下，系统会对CPU，网络，Alarm等活动进行限制，从而延长了电池的使用寿命。

> 当然，系统并不会一直处于Doze模式，而是会间歇性地退出Doze模式一小段时间，在这段时间中，应用就可以去完成它们的同步操作，Alaem任务，等等

官方Doze的解释:

![](http://upload-images.jianshu.io/upload_images/3532835-337a3c4e9d8fc15a.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

左边是未插电源，设备静止，屏幕关闭，接着是短暂退出Doze模式

可以看到，随着设备进入Doze模式的时间越长，间歇性地退出Doze模式的时间间隔也会越长。因为如果设备长时间不使用的话是没必要频繁退出Doze模式来执行同步等操作的，Android在这些细节上的把控是的电池寿命进一步得到了延长。

接下来我们具体看一看Doze模式下有哪些功能会受到限制吧。

- 网络访问本禁止
- 系统忽略唤醒CPU或者屏幕操作
- 系统不再执行WIFI扫描
- 系统不再执行同步服务
- Alarm任务将会在下次退出Doze模式的时候执行

注意其中的最后一条，也就是说，在Doze模式下，我们的Alarm任务将会变得不准时。当然，这在大多数情况下都是合理的，因为只有当用户长时间不使用手机的时候才会进入Doze模式，通常在这种情况下对Alarm任务的准时性要求并没有那么高。

不过，如果你真的有非常特殊的需求，要求Alarm任务即使在Doze模式下也必须正常执行，Android还是提供了解决方案。调用AlarmManager的setAndAllowWhileIdle()或setExactAndAllowWhileIdle()方法就能让定时任务即使在Doze模式下也能正常执行了，这两个方法之间的区别和set(),setExact()方法之间的区别是一样的。

## 5.3 使用Handler的postDelayed(Runnable, long)方法

1.

	Handler handler=new Handler();
	Runnable runnable=new Runnable(){
		@Override
		public void run() {
		// TODO Auto-generated method stub
		//要做的事情
		handler.postDelayed(this, 2000);
		}
	};


2.启动计时器：
`handler.postDelayed(runnable, 2000);//每两秒执行一次runnable.`

3.停止计时器：
`handler.removeCallbacks(runnable);`

# 6. 多窗口模式编程

> Android 7.0 系统中却引入了一个非常有特殊的功能,多窗口模式,它允许我们在同一个屏幕中打开两个应用程序.对于手机屏幕越来越大的今天,这个功能确实是越发重要了.

> 在多窗口模式下,整个应用的界面会缩小很多,那么编写程序时就应该多考虑使用match_parent属性,RecyclerView,ListView,ScrollView等控件,来让应用的界面能够更好地适配各种不同尺寸的屏幕,尽量不要出现屏幕尺寸过大时界面就无法正常显示的情况.

## 6.1 多窗口模式下的生命周期

> 其实多窗口模式并不会改变活动原有的生命周期,只是会将用户最近交互过的那个活动设置为运行状态,而将多窗口模式下另外一个可见的活动设置为暂停状态.如果这时用户又去和暂停的活动进行交互,那么该活动就变成运行状态,之前处于运行状态的活动变成暂停状态.

我们选择MaterialTest项目和LBSTest项目。

先启动MaterialTest项目：

	MaterialTest: onCreate
	MaterialTest: onStart
	MaterialTest: onResume

然后长按Overview按钮，进入多窗口模式：

	MaterialTest: onPause
	MaterialTest: onStop
	MaterialTest: onDestory
	MaterialTest: onCreate
	MaterialTest: onStart
	MaterialTest: onResume
	MaterialTest: onPause

可以看到MaterialTest经历了一个重建的过程。其实这是个正常现象，因为进入到多窗口模式后活动的大小发生了比较大的变化，此时默认是会重新创建活动的。进入多窗口模式后，MaterialTest变成了暂停状态。

接着在Overview列表界面选中LBSTest程序

	LBSTest: onCreate
	LBSTest: onStart
	LBSTest: onResume

现在LBSTset变成了运行状态。

我们随意操作一下MaterialTest程序：

	LBSTest: onPause
	MaterialTest: onResume

在多窗口模式下，用户仍然可以看到处于暂停状态的应用，那么像视频播放器之类的应用在此时就应该能继续播放视频才对。因此，我们最好不要再活动的onPause()方法中去处理视频播放的暂停逻辑，而是应该在onStop()方法中去处理，并且在onStart()方法恢复视频的播放。

另外，针对于进入多窗口模式时活动会被重新创建，如果你想改变这一默认行为，可以在AndroidManifest.xml活动中进行如下配置。

	<activity
	   android:name= ".MainActivity"
	   android:label= "Fruits"
	   android:configChanges="orientation|keyboardHidden|screenSize|screenLayout">
	</activity>

## 6.2 禁用多窗口模式

只需要在AndroidManifest.xml的<application>或<activity>标签中加入如下属性即可：

	android:resizeableActivity=["true" | "false"];

其中，true表示应用支持多窗口模式，false表示应用不支持多窗口模式，如果不配置这个属性，那么默认值为true。

虽说android:resizeableActivity这个属性的用法很简单，但是它还存在着一个问题，就是这个属性只有当项目的targetSdkVersion指定成24或者更高的时候才会有用，否则这个属性是无效的。那么比如说我们将项目的targetSdkVersion指定成23，这个时候尝试进入多窗口模式,会发现告知我们此应用在多窗口模式可能无法正常工作.

针对这个情况，还有一种解决方案，Android规定，如果项目指定的targetSdkVersion低于24，并且活动是不允许横竖屏切换的，那么该应用也将不支持多窗口模式。

默认情况下，我们的应用都是可以随着手机的旋转自由地横竖屏切换，如果想要应用不允许横竖屏切换，那么就需要在AndroidManifest.xml的<activity>标签中加入如下属性即可：

	android:screenOrientation=["portrail" | "landscape"];

portrail表示活动只允许竖屏，landscape表示活动只允许横屏，当然android:screenOrientation还有很多的可选值，"portrail" | "landscape"是最常用的。

# 7. Lambda表达式

> Java 8 引入了一些新特性,如Lambda表达式,stream API,接口默认实现等等.

但是目前能用的就只有Lambda表达式,因为stream API和接口默认实现等特性都只支持Android 7.0及以上的系统.而Lambda表达式却最低兼容到Android 2.3系统.

Lambda表达式本质上是一种匿名方法，它既没有方法名，也没有访问修饰符和返回值类型，使用它来编写代码将会更加简洁，也更加易读。

如果想要在Android项目中使用Lambda表达式或者Java8的其他新特性，首先我们需要在app/build.gradle中添加如下配置。

	android {
	    ·············
		defaultConfig {
	        jackOptions.enabled = true
	    }
	    compileOptions {
	        sourceCompatibility JavaVersion.VERSION_1_8
	        targetCompatibility JavaVersion.VERSION_1_8
	    }
	}

比如说传统情况下开启子线程

	new Thread(new Runnable()
    {
        @Override
        public void run()
        {
            //处理具体的逻辑
        }
    }).start();

而使用Lambda表达式则可以这样使用：

		new Thread(() ->
        {
            //处理具体的逻辑
        }).start();
因为Thread类的构造函数接收的参数是一个Runnable接口，并且该接口中只有一个待实现方法。

**凡是这种只有一个待实现方法的接口，都可以使用Lambda表达式的写法。**

接下啦我们尝试自定义一个接口，然后再使用Lambda表达式的方式进行实现：

	public interface MyListener
	{
	    String doSomething(String a,int b);
	}

MyListener接口中也只有一个待实现方法，这和Runnable接口的结构是基本一致的。唯一不同的是，MyListener中的doSomething()方法是有参数并且有返回值的。

另外，Java还可以根据上下文自动推断出Lambda表达式中的参数类型，上面的也可以简化为：

	 MyListener listener1 = (a,b) ->
        {
            String result = a + b;
            return result;
        };

**在Android中的应用：**

	 button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               //处理点击事件
            }
        });
使用Lambda后表达式后为：

	button = (Button) findViewById(R.id.button);
        button.setOnClickListener((v) ->
        {
            //处理点击事件
        });
另外，当接口的待实现方法有且只有一个参数的时候，还可以进一步简化

	 button = (Button) findViewById(R.id.button);
        button.setOnClickListener(v ->
        {
            //处理点击事件
        });

# 8. 使用Gradle生成正式签名的APK

1.在Android Studio 根目录下有一个gradle.properties文件,它是用来专门配置全局键值对数据的,在该文件中添加如下数据:

	KEY_PATH=C:User/xfhy.jks
	KEY_PASS=123456
	ALIAS_NAME=xfhy
	ALIAS_PASS=123456

2.在app/build.gradle中的android闭包中添加如下内容:

	signingConfigs {
		config {
			storeFile file(KEY_PATH)
			storePassword KEY_PASS
			keyAlias ALIAS_NAME
			keyPassword ALIAS_PASS
		}
	}
	buildTypes {
		release {
			...
			signingConfig signingConfigs.config
		}
	}

3.点击右侧工程栏的Gradle->项目名->:app->Tasks->build.在生成APK之前,先要双击clean这个Task来清理一下当前项目,
然后双击assembleRelease.

**需要将gradle.properties文件保护好,比如说将它从Git版本库中排除**

# 9. for循环简单优化

	int length = list.size();
	for(int i=0; i<length; i++){
		syso("你好");
	}
