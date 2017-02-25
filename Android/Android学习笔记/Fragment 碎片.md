# Fragment 碎片

[TOC]

#1. fragment初步使用  入门 

1. 在activity布局中定义fragment布局,这个布局的外层必须是ViewGroup的子类.命名一般是fragment_xx;
2. 在需要显示fragment的布局里面定义fragment,比如下面的activity_main

		    <?xml version="1.0" encoding="utf-8"?>
	    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	        android:orientation="horizontal"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent">
			
			<!--name:属性来显示指明要添加的碎片类名,注意一定要将包名也加上-->
	        <fragment android:name="com.itheima.fragment.Fragment1"
	                android:id="@+id/list"
	                android:layout_weight="1"
	                android:layout_width="0dp"
	                android:layout_height="match_parent" />
	        <fragment android:name="com.itheima.fragment.Fragment2"
	                android:id="@+id/viewer"
	                android:layout_weight="1"
	                android:layout_width="0dp"
	                android:layout_height="match_parent" />
	    </LinearLayout>

3. 然后需要声明Fragment,需要定义一个类继承自Fragment,必须重写onCreateView()方法

		    //定义一个Fragment 
	    public class Fragment1 extends Fragment {
	    	//当用户第一次画ui的时候调用  要显示Fragment自己的内容  setContentView(R.layout.activity_main);
	    	@Override
	    	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    			Bundle savedInstanceState) {
	    		//[1]通过打气筒把一个布局转换成view对象 
	    		View view = inflater.inflate(R.layout.fragment1, null);
	    		
	    		
	    		return view;
	    	}
	    }

4. name属性 要指定我们自己定义的fragment

**注意:Fragment有两个不同包下的Fragment供你选择,一个是系统内置的android.app.Fragment,一个是support-v4库中的android.support.v4.app.Fragment.
强烈建议使用support-v4库中的Fragment,因为它可以让碎片在所有的Android系统版本中保持功能一致性.如果需要兼容**

# 2. 动态替换fragment
		//3. 获取FragmentManager实例
		FragmentManager fragmentManager = getFragmentManager();
		//3.1 开启事务    要么同时成功    要么同时失败
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		//3.2 添加fragment
		FirstFragment firstFragment = new FirstFragment();
		transaction.add(android.R.id.content, firstFragment);
			
		//4. 最后一定要记得  提交
		transaction.commit();

#3. 使用fragment创建一个选项卡页面(类似于微信主界面)
		
		//1. 获取FragmentManager
		FragmentManager fragmentManager = getFragmentManager();
		
		//2. 开启事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		
		switch (v.getId()) {
		case R.id.bt_wx:
			//3. 替换布局
			transaction.replace(R.id.ll_up, new WxFragment());
			break;
		case R.id.bt_contact:
			transaction.replace(R.id.ll_up, new ContactFragment());
			break;
		case R.id.bt_discover:
			transaction.replace(R.id.ll_up, new DiscoverFragment());
			break;
		case R.id.bt_me:
			transaction.replace(R.id.ll_up, new MeFragment());
			break;
		default:
			break;
		}
		
		//4. 记得提交   事务
		transaction.commit();

# 4. 使用fragment兼容低版本的写法
1. 定义fragment继承V4包中的Fragment 
2. 定义的activity要继承v4包中的FragmentActivity
3. 通过这个方法getSupportFragmentManager  获取Fragment的管理者

# 5. fragment的生命周期
1. 使用Fragment必须重写onCreateView方法
2. 还可以重写一个ondestroy方法 做一些收尾的工作

# 6.fragment之间的通信
>Fragment有一个公共的桥梁 Activity

# 7. 模拟返回栈

> FragmentTransaction中提供了一个addToBackStack()方法,可以用于将一个事务添加到返回栈中.

	private void replaceFragment(Fragment fragment) {
	        FragmentManager fragmentManager = getSupportFragmentManager();
	        FragmentTransaction transaction = fragmentManager.beginTransaction();
	        transaction.replace(R.id.right_layout, fragment);
	        transaction.addToBackStack(null);
	        transaction.commit();
	    }

# 8. 动态加载布局的技巧

### 8.1 使用限定符

写两份activity_main,一份放在layout下面.一份放在layout-large文件夹下面.  其中large就是一个限定符,那些屏幕被认为是large的设备就会自动加载layout-large文件夹下面的布局,而小屏幕的设备则还是会加载layout文件夹的布局.

Android中 常见限定符:
![Android中 常见限定符](http://img.blog.csdn.net/20160311130101005)

### 8.2 使用最小宽度限定符
>有时候我们希望可以更加灵活地为不同设备加载布局,不管它们是不是被系统认定为`large`,这时就可以使用最小宽度限定符

在`res`目录下新建`layout-sw600dp`文件夹,然后在这个文件夹下新建`activity_main`布局.当程序运行在屏幕宽度大于600dp的设备上时,
会加载`layout-sw600dp/activity_main`布局,当程序运行在屏幕宽度小于600dp的设备上时,则仍然加载默认的`layout/activity_main`布局



