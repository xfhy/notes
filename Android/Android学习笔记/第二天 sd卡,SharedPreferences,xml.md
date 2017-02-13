
[TOC]

#1.测试的相关内容
	1.adb shell 下面的 monkey -p pagename	1000,在手机上的某个应用狂点1000下.

	2.百度云测  在云端测试该应用

#2.logcat日志猫工具的使用
	需要logcat的时候,需要创建一个包com.xx.logcat.utils,里面需要创建一个类LogUtils,在这里面控制打印的信息.(必须这样,否则就是不合格的程序员).
	在公司开发中一般打印日志用Log类,通常会封装一个LogUtils,通过开关来控制日志信息的打印.
#3.把数据存储到文件(login案例)  android 下的数据存储 
	1.写布局
		LinearLayout + RelativeLayout
	2.写业务逻辑
		a.找到相应控件

		b.设置按钮的点击事件

		c.在onclick方法中，获取用户输入的用户名密码和是否记住密码

		d.判断用户名密码是否为空，不为空请求服务器（省略，默认请求成功）

		e.判断是否记住密码，如果记住，将用户名密码保存本地。???? 

		f.回显用户名密码 ??

		//通过context对象获取私有目录，/data/data/packagename/filse
		context.getFileDir().getPath()
	3.必须清楚业务逻辑才能写代码
	4.在xml文件中需要使用string资源来显示文字
	5.在Activity中定义一个属性private Context mContext;然后在
	setContentView之后将这个属性初始化(设置为当前的Activity.this),很多地
	方需要用到Context对象(比如Toast等),这样很方便.在公司,有经验的人都这样
	写,so...
	6.ctrl+1 可将临时变量快速转变为类的属性
	7.通过context对象获取私有目录，/data/data/packagename/files
		context.getFileDir().getPath()
#4.存储到SD卡,获取SD的大小及可用空间  （重点）

	使用sdcard注意事项

	1.权限问题
		<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	2.硬性编码问题:通过Environment.getExternalStorageDirectory().getPath()
	获取sd卡路径

	3.使用前,需要判断SD卡状态	getExternalStorageState()  MEDIA_MOUNTED->可读写

	4.需要判断SD卡的剩余空间
			//判断sdcard存储空间是否满足文件的存储
				File sdcard_filedir = Environment.getExternalStorageDirectory();//得到sdcard的目录作为一个文件对象
				long usableSpace = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
				long totalSpace = sdcard_filedir.getTotalSpace();
				//将一个long类型的文件大小格式化成用户可以看懂的M，G字符串
				String usableSpace_str = Formatter.formatFileSize(mContext, usableSpace);
				String totalSpace_str = Formatter.formatFileSize(mContext, totalSpace);
				if(usableSpace < 1024 * 1024 * 200){//判断剩余空间是否小于200M
					Toast.makeText(mContext, "sdcard剩余空间不足,无法满足下载；剩余空间为："+usableSpace_str, Toast.LENGTH_SHORT).show();
					return ;	
				}
			
		/data/data: context.getFileDir().getPath();
					是一个应用程序的私有目录，只有当前应用程序有权限访问读写，其他应用无权限访问。一些安全性要求比较高的数据存放在该目录，一般用来存放size比较小的数据。
		/sdcard:  Enviroment.getExternalStorageDirectory().getPath();
					是一个外部存储目录，只用应用声明了<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>的一个权限，就可以访问读写sdcard目录；所以一般用来存放一些安全性不高的数据，文件size比较大的数据。
#5.文件的权限概念 (了解)

	//通过context对象获取一个私有目录的文件读取流  /data/data/packagename/files/userinfoi.txt
	FileInputStream fileInputStream = context.openFileInput("userinfo.txt");

	//通过context对象得到私有目录下一个文件写入流； name : 私有目录文件的名称    mode： 文件的操作模式， 私有，追加，全局读，全局写
		FileOutputStream fileOutputStream = context.openFileOutput("userinfo.txt", Context.MODE_PRIVATE);	

		linux下一个文件的权限由10位标示：
	1位：文件的类型，d：文件夹 l:快捷方式  -:文件
	2-4： 该文件所属用户对本文件的权限 ， rwx ：用二进制标示，如果不是-就用1标示，是-用0标示；chmod指令赋权限。
	5-7：该文件所属用户组对本文件的权限
	8-10：其他用户对该文件的权限。
#6.SharedPreferences介绍  (重点) 用来做数据存储

		sharedPreferences是通过xml文件来做数据存储的。
		一般用来存放一些标记性的数据，一些设置信息。


		*********使用sharedPreferences存储数据

				
			1.通过Context对象创建一个SharedPreference对象
				//name:sharedpreference文件的名称    mode:文件的操作模式
				SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
			2.通过sharedPreferences对象获取一个Editor对象
				Editor editor = sharedPreferences.edit();
			3.往Editor中添加数据
				editor.putString("username", username);
				editor.putString("password", password);
			4.提交Editor对象
				editor.commit();

		*********使用sharedPreferences读取数据

			1.通过Context对象创建一个SharedPreference对象
				SharedPreferences sharedPreferences = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
				
			2.通过sharedPreference获取存放的数据
				//key:存放数据时的key   defValue: 默认值,根据业务需求来写
				String username = sharedPreferences.getString("username", "");
				String password = sharedPreferences.getString("password", "");
				


		通过PreferenceManager可以获取一个默认的sharepreferences对象		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
#7.经验之谈
		1.谷歌官方推荐一个Android下的一个Bean类(Model),比如Student,SmsBean等.里面的属性不要用设置为private,然后get,set,尽量用public,因为get,set需要反射,反射需要占内存,android内存有限,所以用public.
		2.工具类需要封装到util包下面.
		3.数据库的增删改查放到dao包下面.
#8.生成xml的2种方式 
		1.写布局

	2.业务逻辑
		a.备份
			1.封装短信数据到list中
			2.将list中的数据写到xml文件中。
		b.恢复
			1.解析xml文件中短信数据，封装到list集合中
			2.将解析数据打印。


	XmlSerializer
	


	//使用XmlSerializer来序列化xml文件
	public static boolean backupSms_android(Context context){
		
		try{
			
			//0.获取短信数据
			ArrayList<SmsBean> allSms = SmsDao.getAllSms();
			//1.通过Xml获取一个XmlSerializer对象
			XmlSerializer xs = Xml.newSerializer();
			//2.设置XmlSerializer的一些参数，比如：设置xml写入到哪个文件中
			//os:xml文件写入流   encoding：流的编码
			xs.setOutput(context.openFileOutput("backupsms2.xml", Context.MODE_PRIVATE), "utf-8");
			//3.序列化一个xml的声明头
			//encoding:xml文件的编码  standalone:是否独立
			xs.startDocument("utf-8", true);
			//4.序列化一个根节点的开始节点
			//namespace:命名空间  name： 标签的名称
			xs.startTag(null, "Smss");
			//5.循环遍历list集合序列化一条条短信
			
				for (SmsBean smsBean : allSms) {
					xs.startTag(null, "Sms");
					//name:属性的名称  value：属性值
					xs.attribute(null, "id", smsBean.id+"");
					
					xs.startTag(null, "num");
					//写一个标签的内容
					xs.text(smsBean.num);
					xs.endTag(null, "num");
					
					
					xs.startTag(null, "msg");
					xs.text(smsBean.msg);
					xs.endTag(null, "msg");
					
					
					xs.startTag(null, "date");
					xs.text(smsBean.date);
					xs.endTag(null, "date");
					
					xs.endTag(null, "Sms");
				}

			//6.序列化一个根节点的结束节点
				xs.endTag(null, "Smss");
			//7.将xml写入到文件中，完成xml的序列化
				xs.endDocument();
				return true;

		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}