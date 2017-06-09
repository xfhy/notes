# ViewPager详解

> 本文总结的是源自:http://blog.csdn.net/harvic880925/article/details/38453725  大师写的博客

## 1. 基本入门

看看效果图:

![](http://olg7c0d2n.bkt.clouddn.com/17-5-30/61334413.jpg)

> 首先让大家有个全局的认识，直接上个项目，看看仅仅通过这几行代码，竟然就能完成如此强悍的功能。下篇再结合API仔细讲讲为什么要这么写。

1.新建项目，引入ViewPager控件,ViewPager。它是google SDk中自带的一个附加包的一个类，可以用来实现屏幕间的切换。在主布局文件里加入

	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"  
	    xmlns:tools="http://schemas.android.com/tools"  
	    android:layout_width="fill_parent"  
	    android:layout_height="fill_parent"  
	    tools:context="com.example.testviewpage_1.MainActivity" >  
	  
	<android.support.v4.view.ViewPager  
	    android:id="@+id/viewpager"  
	    android:layout_width="wrap_content"  
	    android:layout_height="wrap_content"  
	    android:layout_gravity="center" />  
	  
	</RelativeLayout>  
2.新建三个layout，用于滑动切换的视图

布局代码分别如下：

layout1.xml

	<?xml version="1.0" encoding="utf-8"?>  
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"  
	    android:layout_width="match_parent"  
	    android:layout_height="match_parent"  
	    android:background="#ffffff"  
	    android:orientation="vertical" >  
	      
	  
	</LinearLayout>  

layout2.xml

	<?xml version="1.0" encoding="utf-8"?>  
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"  
	    android:layout_width="match_parent"  
	    android:layout_height="match_parent"  
	    android:background="#ffff00"  
	    android:orientation="vertical" >  
	      
	  
	</LinearLayout>  

layout3.xml

	<?xml version="1.0" encoding="utf-8"?>  
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"  
	    android:layout_width="match_parent"  
	    android:layout_height="match_parent"  
	    android:background="#ff00ff"  
	    android:orientation="vertical" >  
	      
	  
	</LinearLayout><span style="color:#660000;">  

3.代码实战

	public class MainActivity extends Activity {  
  
	    private View view1, view2, view3;  
	    private ViewPager viewPager;  //对应的viewPager  
	      
	    private List<View> viewList;//view数组  
	     
	     
	    @Override  
	    protected void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.activity_main);  
	          
	        viewPager = (ViewPager) findViewById(R.id.viewpager);  
	        LayoutInflater inflater=getLayoutInflater();  
	        view1 = inflater.inflate(R.layout.layout1, null);  
	        view2 = inflater.inflate(R.layout.layout2,null);  
	        view3 = inflater.inflate(R.layout.layout3, null);  
	          
	        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中  
	        viewList.add(view1);  
	        viewList.add(view2);  
	        viewList.add(view3);  
	          
	          
	        PagerAdapter pagerAdapter = new PagerAdapter() {  
	              
	            @Override  
	            public boolean isViewFromObject(View arg0, Object arg1) {  
	                // TODO Auto-generated method stub  
	                return arg0 == arg1;  
	            }  
	              
	            @Override  
	            public int getCount() {  
	                // TODO Auto-generated method stub  
	                return viewList.size();  
	            }  
	              
	            @Override  
	            public void destroyItem(ViewGroup container, int position,  
	                    Object object) {  
	                // TODO Auto-generated method stub  
	                container.removeView(viewList.get(position));  
	            }  
	              
	            @Override  
	            public Object instantiateItem(ViewGroup container, int position) {  
	                // TODO Auto-generated method stub  
	                container.addView(viewList.get(position));  
	                  
	                  
	                return viewList.get(position);  
	            }  
	        };  
	          
	        viewPager.setAdapter(pagerAdapter);  
	    }  
	}  

4.PageAdapter——PageView的适配器

适配器这个东东想必大家都不莫生，在ListView中也有适配器，listView通过重写GetView（）函数来获取当前要加载的Item。而PageAdapter不太相同，毕竟PageAdapter是单个VIew的合集。

PageAdapter 必须重写的四个函数：

- boolean isViewFromObject(View arg0, Object arg1)

	功能：该函数用来判断instantiateItem(ViewGroup, int)函数所返回来的Key与一个页面视图是否是代表的同一个视图(即它俩是否是对应的，对应的表示同一个View)
返回值：如果对应的是同一个View，返回True，否则返回False。

- int getCount() getCount():返回要滑动的VIew的个数


- void destroyItem(ViewGroup container, int position,Object object) 从当前container中删除指定位置（position）的View;


- Object instantiateItem(ViewGroup container, int position) 做了两件事，第一：将当前视图添加到container中，第二：返回当前View

		这个函数的实现的功能是创建指定位置的页面视图。适配器有责任增加即将创建的View视图到这里给定的container中，
		这是为了确保在finishUpdate(viewGroup)返回时this is be done!
		返回值：返回一个代表新增视图页面的Object（Key），这里没必要非要返回视图本身，也可以这个页面的其它容器。
		其实我的理解是可以代表当前页面的任意值，只要你可以与你增加的View一一对应即可，
		比如position变量也可以做为Key（最后我们举个例子试试可不可行）
		
		心得 ：
		
		1、从说明中可以看到，在代码中，我们的责任是将指定position的视图添加到conatiner中
		
		2、Key的问题：从这个函数就可以看出，该函数返回值就是我们根据参数position增加到
		conatiner里的View的所对应的Key！！！！！！！
		
		3、“it only must ensure this is done by the time it returns fromfinishUpdate(ViewGroup).”
		这句话在destroyItem（）的函数说明中同样出现过，这说明在 finishUpdate(viewGroup)执行完后，有两个操作，
		一个是原视图的移除（不再显示的视图），另一个是新增显示视图（即将显示的视图）
