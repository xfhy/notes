# Material Design

> 2014年Google I/O大会重磅推出了一套全新的界面设计语言---Material Design

> 2015年Google I/O大会推出了一个Design Support库,这个库将Material Design中最具代表性的一些控件和效果进行了封装.


# 1. Toolbar

> ActionBar 由于其设计的原因,被限定只能位于活动的顶部,从而不能实现一些Material Design的效果.因此官方已经不再建议使用ActionBar了.Toolbar的强大之处在于,它不仅继承了ActionBar的所有功能,而且灵活性高,可以配合其他控件来完成一些Material Design的效果.

## 使用方法

1. 首先修改主题:

		<style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
	        <!-- Customize your theme here. -->
	        <item name="colorPrimary">@color/colorPrimary</item>
	        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
	        <item name="colorAccent">@color/colorAccent</item>
    	</style>

2. 然后在布局中配置

		<?xml version="1.0" encoding="utf-8"?>
		<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
		    xmlns:app="http://schemas.android.com/apk/res-auto"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent">
		
		    <!--
		        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"    深色主题
		        app:popupTheme="@style/ThemeOverlay.AppCompat.Light"  popupTheme5.0系统才有的,需要兼容之前的系统
		    -->
		    <android.support.v7.widget.Toolbar
		        android:id="@+id/toolbar"
		        android:layout_width="match_parent"
		        android:layout_height="?attr/actionBarSize"
		        android:background="?attr/colorPrimary"
		        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
		        app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
		
		</FrameLayout>

3. 在Activity中写:

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

## 添加action按钮

效果如下:

![](http://olg7c0d2n.bkt.clouddn.com/17-3-4/68615899-file_1488612474654_24a4.png)

步骤:

1. 右击res目录->new->directory,创建一个menu文件夹.然后新建Menu resource file,创建一个toolbar.xml文件,并编写如下代码:

 - 这里引入app命名空间,是为了兼容性.
 - 指定的app:showAsAction用来指定按钮的显示位置,有以下几种值可选,always表示永远显示在Toolbar中,如果屏幕空间不够,则不显示;ifRoom表示屏幕空间足够的情况下显示在Toolbar中,不够的话就显示在菜单中;never则表示永远显示在菜单中.

			<?xml version="1.0" encoding="utf-8"?>
			<menu xmlns:android="http://schemas.android.com/apk/res/android"
		    xmlns:app="http://schemas.android.com/apk/res-auto">
		
		    <item
		        android:id="@+id/backup"
		        android:icon="@drawable/ic_backup"
		        android:title="Backup"
		        app:showAsAction="always" />
		
		    <item
		        android:id="@+id/delete"
		        android:icon="@drawable/ic_delete"
		        android:title="Delete"
		        app:showAsAction="ifRoom" />
		
		    <item
		        android:id="@+id/setting"
		        android:title="Setting"
		        app:showAsAction="never"
		        android:icon="@drawable/ic_settings" />
		
		</menu>

2. 然后在Activity中重写如下函数

		@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.toolbar,menu);    //加载菜单文件
	        return true;
	    }

		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()){
	            case R.id.backup:
	                Toast.makeText(this, "你点击了备份", Toast.LENGTH_SHORT).show();
	                break;
	            case R.id.delete:
	                Toast.makeText(this, "你点击了删除", Toast.LENGTH_SHORT).show();
	                break;
	            case R.id.setting:
	                Toast.makeText(this, "你点击了设置", Toast.LENGTH_SHORT).show();
	                break;
	            default:
	                break;
	        }
	        return true;
	    }

# 2. 滑动菜单(QQ5.0那种)

> 首先介绍以下DrawerLayout的用法吧.首先它是一个布局,在布局中允许放入两个直接子控件,第一个子控件是主屏幕中显示的内容,第二个子控件是滑动菜单中显示的内容.

## DrawerLayout

1. 布局写法示例

		<?xml version="1.0" encoding="utf-8"?>
		<android.support.v4.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:app="http://schemas.android.com/apk/res-auto"
    	android:id="@+id/drawer_layout"
    	android:layout_width="match_parent"
    	android:layout_height="match_parent">

    	<FrameLayout
	        android:layout_width="match_parent"
	        android:layout_height="match_parent">
	
	        <!--
	            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"    深色主题
	            app:popupTheme="@style/ThemeOverlay.AppCompat.Light"  popupTheme5.0系统才有的,需要兼容之前的系统
	        -->
	        <android.support.v7.widget.Toolbar
	            android:id="@+id/toolbar"
	            android:layout_width="match_parent"
	            android:layout_height="?attr/actionBarSize"
	            android:background="?attr/colorPrimary"
	            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
	            app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
	
	    </FrameLayout>
	
	    <!--
	        android:layout_gravity="start"  这个属性是必须指定的,意思是让系统指定滑动菜单在左边还是右边.这里为start是指让系统根据当前语言环境自动选择.
	    -->
	    <TextView
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:text="This is Menu"
	        android:id="@+id/tv_menu"
	        android:textSize="30sp"
	        android:background="#FFF"
	        android:layout_gravity="start"/>
	
		</android.support.v4.widget.DrawerLayout>

2. 然后在Activity中写入如下:

		public class MainActivity extends AppCompatActivity {

		    private DrawerLayout mDrawerLayout;
		
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_main);
		
		        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		        setSupportActionBar(toolbar);
		
		        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		        ActionBar actionBar = getSupportActionBar();   //具体实现是Toobar来完成的
		        if (actionBar != null) {
		            actionBar.setDisplayHomeAsUpEnabled(true);  //让导航按钮显示出来
		            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);  //设置导航按钮图标
		        }
		    }
		
		    @Override
		    public boolean onCreateOptionsMenu(Menu menu) {
		        getMenuInflater().inflate(R.menu.toolbar, menu);    //加载菜单文件
		        return true;
		    }
		
		    @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		        switch (item.getItemId()) {
		
		            //这个是HomeAsUp按钮的id永远都是android.R.id.home
		            case android.R.id.home:
		                mDrawerLayout.openDrawer(GravityCompat.START);   //将滑动菜单显示出来
		                break;
		            
		            case R.id.backup:
		                Toast.makeText(this, "你点击了备份", Toast.LENGTH_SHORT).show();
		                break;
		            case R.id.delete:
		                Toast.makeText(this, "你点击了删除", Toast.LENGTH_SHORT).show();
		                break;
		            case R.id.setting:
		                Toast.makeText(this, "你点击了设置", Toast.LENGTH_SHORT).show();
		                break;
		            default:
		                break;
		        }
		        return true;
		    }
		}

## NavigationView

> 你可以在滑动菜单页面定制任意的布局,不过谷歌给我提供了一种更好的方法----使用NavigationView.NavigationView是Design Support库中提供的一个控件.

效果如下:
![](http://olg7c0d2n.bkt.clouddn.com/17-3-4/64264576-file_1488638537722_1334b.jpg)

## 使用方法

1. 引入

		compile 'com.android.support:design:24.2.1'  
    	compile 'de.hdodenhof:circleimageview:2.1.0'

第一个是Design Support->NavigationView
第二个是图片圆形化的一个开源库

2. 在开始使用NavigationView之前,我们还需要准备好两个东西,menu和headerLayout.menu是用来在NavigationView中显示具体的菜单项的,headerLayout则是用来在NavigationView中显示头部布局的.

新建menu文件夹->new->Menu resource file

**menu**

	<?xml version="1.0" encoding="utf-8"?>
	<menu xmlns:android="http://schemas.android.com/apk/res/android">

	    <!--这是滑动菜单的菜单项-->
	
	    <!--android:checkableBehavior="single"表示所有菜单只能单选-->
	    <group android:checkableBehavior="single">
	
	        <item
	            android:id="@+id/nav_call"
	            android:icon="@drawable/nav_call"
	            android:title="Call" />
	        <item
	            android:id="@+id/nav_friends"
	            android:icon="@drawable/nav_friends"
	            android:title="Friends" />
	        <item
	            android:id="@+id/nav_location"
	            android:icon="@drawable/nav_location"
	            android:title="Location" />
	        <item
	            android:id="@+id/nav_mail"
	            android:icon="@drawable/nav_mail"
	            android:title="Mail" />
	        <item
	            android:id="@+id/nav_task"
	            android:icon="@drawable/nav_task"
	            android:title="Tasks" />
	
	    </group>

	</menu>

layout文件夹->new->layout resource file

**headerLayout**

	<?xml version="1.0" encoding="utf-8"?>
		<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    android:layout_width="match_parent"
	    android:layout_height="180dp"
	    android:background="@color/colorPrimary"
	    android:padding="10dp">
	
	    <!--
	        这是headerLayout布局
	    -->
	
	    <!--头像  圆形的-->
	    <de.hdodenhof.circleimageview.CircleImageView
	        android:id="@+id/icon_image"
	        android:layout_width="70dp"
	        android:layout_height="70dp"
	        android:layout_centerInParent="true"
	        android:src="@drawable/nav_icon" />
	
	    <!--邮箱-->
	    <TextView
	        android:id="@+id/tv_mail"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_alignParentBottom="true"
	        android:textColor="#FFF"
	        android:textSize="14sp"
	        android:text="xfhy@gmail.com"/>
	
	    <!--用户名-->
	    <TextView
	        android:id="@+id/tv_username"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_above="@id/tv_mail"
	        android:textSize="14sp"
	        android:textColor="#FFF"
	        android:text="Tony Gonm"/>

	</RelativeLayout>

高度设为180dp,这是一个NavigationView比较适合的高度.

3.然后将布局中滑动菜单布局替换一下,具体实现:

		<?xml version="1.0" encoding="utf-8"?>
		<android.support.v4.widget.DrawerLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:id="@+id/drawer_layout"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent">
	
	    <FrameLayout
	        android:layout_width="match_parent"
	        android:layout_height="match_parent">
	
	        <!--
	            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"    深色主题
	            app:popupTheme="@style/ThemeOverlay.AppCompat.Light"  popupTheme5.0系统才有的,需要兼容之前的系统
	        -->
	        <android.support.v7.widget.Toolbar
	            android:id="@+id/toolbar"
	            android:layout_width="match_parent"
	            android:layout_height="?attr/actionBarSize"
	            android:background="?attr/colorPrimary"
	            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
	            app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
	
	    </FrameLayout>
	
	    <!--
	        android:layout_gravity="start"  这个属性是必须指定的,意思是让系统指定滑动菜单在左边还是右边
	        app:menu="@menu/nav_menu"   指定菜单
	        app:headerLayout="@layout/nav_header"   指定headerLayout
	    -->
	    <android.support.design.widget.NavigationView
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:id="@+id/nav_view"
	        android:layout_gravity="start"
	        app:menu="@menu/nav_menu"
	        app:headerLayout="@layout/nav_header"/>
	
		</android.support.v4.widget.DrawerLayout>

4.在Activity中找到NavigationView控件

	navView.setCheckedItem(R.id.nav_call);   //设置默认选中call选项

	//设置NavigationView菜单选中事件的监听器
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //这里可以根据item的getItemId()来判断  具体点击了哪个选项

                mDrawerLayout.closeDrawers();   //关闭滑动菜单
                return true;
            }
        });

# 3. 悬浮按钮和可交互提示

## 3.1 FloatingActionButton

> FloatingActionButton是Design Support 库中提供的一个控件,这个控件可以帮助我们轻松地实现悬浮按钮的效果.

1.首先,在上面的基础上进行修改activity_main.xml布局.将下面这段代码放到FrameLayout中.

	<android.support.design.widget.FloatingActionButton
            android:id="@+id/fab_done"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom|end"
            android:layout_margin="16dp"
            android:src="@drawable/ic_done" />

2.设置点击事件,和普通按钮一样的用法.

	FloatingActionButton fab_done = (FloatingActionButton) findViewById(R.id.fab_done);
        //设置点击事件   和普通按钮的点击事件是一样的
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "你点我了(Done)", Toast.LENGTH_SHORT).show();
            }
        });

## 3.2 Snackbar

> Design Support库中提供的更加先进的提示工具----Snackbar

> 首先要明确,Snackbar并不是Toast的替代品,它们两者之间有着不同的应用场景.

> Snackbar允许在提示当中加入一个可交互按钮,当用户点击按钮的时候可以执行一些额外的逻辑操作.

> 过一段时间会自动消失

效果如下:

![](http://olg7c0d2n.bkt.clouddn.com/17-3-5/53082739-file_1488678751375_17271.png)

用法如下:

	//需要传入View对象  这个view只要是当前界面布局的任意一个View都可以,Snackbar会根据这个View来
                //自动查找最外层的布局,用来展示Snackbar.
                Snackbar.make(view,"Data Deleted",Snackbar.LENGTH_SHORT)
                        .setAction("Undo",new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
存在的问题:这个Snackbar竟然将我们的悬浮按钮给遮挡住了.


## 3.3 CoordinatorLayout

> 上面遗留的问题用CoordinatorLayout就可以轻松解决.CoordinatorLayout可以说是一个加强版的FrameLayout,这个布局也是由Design Support库提供的.

> 事实上,CoordinatorLayout可以监听其所有子控件的各种事件,然后自动帮助我们做出最合理的响应.

如下:

![](http://olg7c0d2n.bkt.clouddn.com/17-3-6/4783486-file_1488768414760_e5eb.png)


**用法**

直接替换用`android.support.design.widget.CoordinatorLayout`替换掉上面的FrameLayout

# 4.4 卡片式布局


