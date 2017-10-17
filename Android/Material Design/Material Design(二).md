# Material Design(二)

> 下面的这些东西都是接着Material Design(一)做的.

# 4. 卡片式布局

## 4.1 CardView

> CardView是用于实现卡片式布局效果的重要控件,由appcompat-v7库提供.实际上,CardView就是一个FrameLayout,只是额外提供了圆角和阴影等效果,看上去会有立体的效果.   下面就将使用CardView作为Recycler的子项来使用,达到下面图片上的效果.

效果如下:

![](http://olg7c0d2n.bkt.clouddn.com/17-3-6/58834961-file_1488776264124_11bb6.png)

1.首先需要往app/build.gradle文件中声明这些库的依赖才行

`compile 'com.android.support:cardview-v7:24.2.1'     //CardView`
` compile 'com.android.support:recyclerview-v7:24.2.1' //RecyclerView`
`compile 'com.github.bumptech.glide:glide:3.7.0'   //强大的图片加载库`

2.然后每个RecyclerView的子项就是用CardView来实现的.具体代码:

	<?xml version="1.0" encoding="utf-8"?>
	<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="5dp"
    app:cardCornerRadius="4dp">

    <!--
        这是RecyclerView的子项布局
        cardCornerRadius  圆角弧度
    -->

	    <LinearLayout
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:orientation="vertical">
	
	        <!--
	            scaleType:可以指定图片的缩放模式
	            这里使用了centerCrop模式,它可以让图片保持原有比例填充满ImageView
	            并将超出的部分裁剪掉.
	        -->
	        <ImageView
	            android:id="@+id/fruit_image"
	            android:layout_width="match_parent"
	            android:layout_height="100dp"
	            android:scaleType="centerCrop" />
	
	        <TextView
	            android:id="@+id/fruit_name"
	            android:layout_width="wrap_content"
	            android:layout_height="wrap_content"
	            android:layout_gravity="center_horizontal"
	            android:layout_margin="5dp"
	            android:textSize="16sp" />
	
	    </LinearLayout>

	</android.support.v7.widget.CardView>

3.然后直接将RecyclerView显示出来即可   就可以看到CardView的效果了.
上面的那种2列的效果是配合
`GridLayoutManager layoutManager = new GridLayoutManager(this,2);    //2列
recyclerView.setLayoutManager(layoutManager);`
这个使用的.

待解决问题:RecyclerView把Toolbar遮盖住了.

## 4.2 AppBarLayout

> AppBarLayout是Design Support提供的另一个工具,AppBarLayout实际上是一个垂直方向的LinearLayout,它在内部做了很多滚动事件的封装,并应用了一些Material Design的设计理念.

> AppBarLayout必须是CoordinatorLayout的子布局

> 可以实现的效果:当往下滑的时候标题栏自动隐藏,往上滑的时候标题栏用重新出现.


1. 首先将Toolbar用AppBarLayout包起来:

		<android.support.design.widget.AppBarLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
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
                app:layout_scrollFlags="scroll|enterAlways|snap"
                app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
        </android.support.design.widget.AppBarLayout>

2. 然后将布局中的RecyclerView加一个属性,这个属性可以指定布局的行为.

	 	app:layout_behavior="@string/appbar_scrolling_view_behavior"

3. 现在往Toolbar中添加一个`app:layout_scrollFlags="scroll|enterAlways|snap"`属性,并将这个属性的值指定成了scroll|enterAlways|snap.其中,scroll表示当RecyclerView向上滚动的时候,Toolbar会跟着一起向上滚动并实现隐藏;enterAlways 表示当RecyclerView向下滚动的时候Toolbar会跟着一起向下滚动并重新显示.snap表示当Toolbar还没有完全隐藏或显示的时候,会根据当前滚动的距离,自动选择是隐藏还是显示.

## 4.3 下拉刷新

> SwipeRefreshLayout就是用于实现下拉刷新功能的核心类,它是由support-v4库提供的.我们把想要实现下拉刷新功能的控件放置到SwipeRefreshLayout中,就可以迅速让这个控件支持下拉刷新.

效果如下:

![](http://olg7c0d2n.bkt.clouddn.com/17-3-6/10124305-file_1488788229008_138a3.png)


**使用方法**

1. 在布局中这样用
	
		<!--
            SwipeRefreshLayout,嵌套在里面的话就有下拉刷新功能
            app:layout_behavior="@string/appbar_scrolling_view_behavior"   指定布局行为
        -->
        <android.support.v4.widget.SwipeRefreshLayout
            android:id="@+id/swipe_refresh"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layout_behavior="@string/appbar_scrolling_view_behavior">

            <!--
                layout_behavior:指定布局行为
            -->
            <android.support.v7.widget.RecyclerView
                android:id="@+id/recycler_view"
                android:layout_width="match_parent"
                android:layout_height="match_parent" />
        </android.support.v4.widget.SwipeRefreshLayout>

2. 在Activity中,可以设置监听器等

		private SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);   //设置下拉刷新进度条的颜色

        //设置下拉刷新的监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();    //进行刷新操作
            }
        });

# 5. 可折叠式标题栏

## 5.1 CollapsingToolbarLayout

> 实现一个可折叠式标题栏的效果,需要借助CollapsingToolbarLayout这个工具.

效果如下:
页面上有三部分,水果标题栏,水果内容详情和悬浮按钮

![](http://olg7c0d2n.bkt.clouddn.com/17-3-6/72475250-file_1488805365087_17a3d.png)
![](http://olg7c0d2n.bkt.clouddn.com/17-3-6/24630050-file_1488805436941_a776.png)
![](http://olg7c0d2n.bkt.clouddn.com/17-3-6/33602930-file_1488805495756_11d75.png)

1. 在布局中这样写:

		<?xml version="1.0" encoding="utf-8"?>
		<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:id="@+id/activity_fruit"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    tools:context="com.xfhy.materialtest.FruitActivity">
	
	    <!--
	        垂直的LinearLayout
	    -->
	    <android.support.design.widget.AppBarLayout
	        android:id="@+id/appBar"
	        android:layout_width="match_parent"
	        android:layout_height="250dp">
	
	        <!--
	            app:contentScrim="?attr/colorPrimary":  指定CollapsingToolbarLayout在趋于折叠状态以及
	            折叠之后的背景色
	
	            app:layout_scrollFlags="scroll|exitUntilCollapsed" : scroll表示CollapsingToolbarLayout
	            会随着水果内容详情的滚动一起滚动,exitUntilCollapsed表示当CollapsingToolbarLayout随着滚动完
	            成折叠之后就保留在界面上,不再移出屏幕.
	        -->
	        <android.support.design.widget.CollapsingToolbarLayout
	            android:id="@+id/collapsing_toolbar"
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
	            app:contentScrim="?attr/colorPrimary"
	            app:layout_scrollFlags="scroll|exitUntilCollapsed">
	
	            <!--
	                android:scaleType="centerCrop" : 图片的缩放模式  这种模式是表示图片等比例放大,占满这个控件
	
	                app:layout_collapseMode="parallax" : 用于指定当前控件在CollapsingToolbarLayout折叠过程中
	                的折叠模式,其中Toolbar指定成pin,表示在折叠的过程中位置始终保持不变,ImageView指定成parallax,
	                表示会在折叠过程中产生一定的错位偏移,这种模式的视觉效果会非常好.
	
	                这个ImageView是用于显示顶部的图片的
	            -->
	            <ImageView
	                android:id="@+id/fruit_image_view"
	                android:layout_width="match_parent"
	                android:layout_height="match_parent"
	                android:scaleType="centerCrop"
	                app:layout_collapseMode="parallax" />
	
	            <android.support.v7.widget.Toolbar
	                android:id="@+id/toolbar"
	                android:layout_width="match_parent"
	                android:layout_height="?attr/actionBarSize"
	                app:layout_collapseMode="pin">
	
	            </android.support.v7.widget.Toolbar>
	
	        </android.support.design.widget.CollapsingToolbarLayout>
	
	    </android.support.design.widget.AppBarLayout>
	
	    <!--
	        NestedScrollView 和ScrollView差不多,只不过NestedScrollView在ScrollView的基础上海增加了嵌套响应
	        滚动事件的功能.
	
	        app:layout_behavior="@string/appbar_scrolling_view_behavior" : 指定布局行为
	
	        这个滚动控件是用于放置下方的水果介绍的那些文字用的   可滚动
	    -->
	    <android.support.v4.widget.NestedScrollView
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        app:layout_behavior="@string/appbar_scrolling_view_behavior">
	
	        <!--
	            不管是ScrollView还是NestedScrollView,它们的内部都只允许存在一个直接子布局.因此,
	            如果我们想要在里面放入很多东西的话,通常都会先嵌套一个LinearLayout,然后再在LinearLayout放入具体的内容
	        -->
	        <LinearLayout
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content"
	            android:orientation="vertical">
	
	            <!--
	                app:cardCornerRadius="4dp" : 圆角弧度
	            -->
	            <android.support.v7.widget.CardView
	                android:layout_width="match_parent"
	                android:layout_height="wrap_content"
	                android:layout_marginBottom="15dp"
	                android:layout_marginLeft="15dp"
	                android:layout_marginRight="15dp"
	                android:layout_marginTop="34dp"
	                app:cardCornerRadius="4dp">
	
	                <TextView
	                    android:id="@+id/fruit_content_text"
	                    android:layout_width="wrap_content"
	                    android:layout_height="wrap_content"
	                    android:layout_margin="10dp" />
	
	            </android.support.v7.widget.CardView>
	
	        </LinearLayout>
	
	    </android.support.v4.widget.NestedScrollView>
	
	    <!--
	        app:layout_anchor="@id/appBar" : 设置描点,即以哪个控件为参照点设置位置
	        app:layout_anchorGravity="bottom|end" : 设置按钮在底部和右边
	    -->
	    <android.support.design.widget.FloatingActionButton
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_margin="16dp"
	        android:src="@drawable/ic_comment"
	        app:layout_anchor="@id/appBar"
	        app:layout_anchorGravity="bottom|end" />
	
		</android.support.design.widget.CoordinatorLayout>

2. 然后需要在Activity中让导航图标可见,并且生成水果介绍的数据.

		package com.xfhy.materialtest;
		public class FruitActivity extends AppCompatActivity {
	
		    public static final String FRUIT_NAME = "fruit_name";
		    public static final String FRUIT_IMAGE_ID = "fruit_image_id";
		
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_fruit);
		
		        Intent intent = getIntent();
		        String fruitName = intent.getStringExtra(FRUIT_NAME);
		        int fruitImageId = intent.getIntExtra(FRUIT_IMAGE_ID, 0);
		        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
		                findViewById(R.id.collapsing_toolbar);
		        ImageView fruit_image_view = (ImageView) findViewById(R.id.fruit_image_view);
		        TextView fruit_content_text = (TextView) findViewById(R.id.fruit_content_text);
		
		        setSupportActionBar(toolbar);   //设置标题栏
		        ActionBar actionBar = getSupportActionBar();
		        if(actionBar != null){
		            actionBar.setDisplayHomeAsUpEnabled(true);    //设置导航图标可见
		        }
		        collapsingToolbar.setTitle(fruitName);   //设置标题栏  标题
		        Glide.with(this).load(fruitImageId).into(fruit_image_view);   //设置显示水果图片
		
		        String fruitContent = generateFruitContent(fruitName);
		        fruit_content_text.setText(fruitContent);   //设置水果介绍需要显示的文字
		    }
		
		    /**
		     * 生成水果的介绍
		     * @param fruitName
		     * @return
		     */
		    private String generateFruitContent(String fruitName) {
		        StringBuilder sb = new StringBuilder();
		        for (int i = 0; i < 500; i++) {
		            sb.append(fruitName);
		        }
		        return sb.toString();
		    }
		
		    @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		        switch (item.getItemId()){
		            case android.R.id.home:   //这是导航按钮的id(固定值)
		                finish();
		                return true;
		        }
		        return super.onOptionsItemSelected(item);
		    }
		}

3. 当然,如果需要设置卡片的点击事件,点击后跳转到当前的水果介绍页面则需要在FruitAdapter中设置点击事件

		//创建ViewHolder实例
	    @Override
	    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        if (mContext == null) {
	            mContext = parent.getContext();
	        }
	        //加载布局到view
	        View view = LayoutInflater.from(mContext).inflate(R.layout.fruit_item, parent, false);
	        final ViewHolder viewHolder = new ViewHolder(view);
	
	        //设置每个卡片的点击事件
	        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                int adapterPosition = viewHolder.getAdapterPosition();
	                Fruit fruit = mFruitList.get(adapterPosition);
	                Intent intent = new Intent(mContext, FruitActivity.class);
	                intent.putExtra(FruitActivity.FRUIT_NAME, fruit.getName());
	                intent.putExtra(FruitActivity.FRUIT_IMAGE_ID, fruit.getImageId());
	                mContext.startActivity(intent);
	            }
	        });
	
	        return viewHolder;   //创建ViewHolder实例
	    }

## 5.2 充分利用系统状态栏空间(感觉像是沉浸式状态栏)

> 上面的效果还有个缺陷,背景图片和系统的状态栏总有一些不搭的感觉.只不过可惜的是,在Android 5.0 之前,我们是无法对状态栏的背景或颜色进行操作的,那个时候也还没有Material Design的概念.

> 想要让背景图能够和状态栏融合,需要借助android:fitsSystemWindows这个属性来实现.

效果如下:

![](http://olg7c0d2n.bkt.clouddn.com/17-3-6/17739900-file_1488808457958_117a2.png)

1.在CoordinatorLayout,AppBarLayout,CollapsingToolbarLayout,ImageView,这种嵌套结构的布局中,将android:fitsSystemWindows属性指定成true;

2.还需要在程序的主题中将状态栏的颜色指定成透明色才行.指定成透明色的方法:在主题中将android:statusBarColor属性的值指定成@android:color/transparent就可以了.     但是有个问题,android:statusBarColor属性是从API 21 开始的,也就是Android 5.0 系统才开始有的.那么,系统差异型的功能实现就要从这里开始了.

在res下创建目录values-21,然后在该目录下创建values resources file ,写入如下代码:

	<?xml version="1.0" encoding="utf-8"?>
	<resources>
	
	    <!--
	        values-v21 是android 5.0以上系统才回去读取的
	        这个主题是专门为FruitActivity使用的
	    -->
	    <style name="FruitActivityTheme" parent="AppTheme">
	        <item name="android:statusBarColor">@android:color/transparent</item>
	    </style>
	
	</resources>


但是,在5.0 之前的系统无法识别FruitActivityTheme这个主题,所以需要在res->values->styles.xml添加2行代码,中间什么都没有.

	<style name="FruitActivityTheme" parent="AppTheme">
    </style>

3.最后在清单文件中设置FruitActivity的主题时,这样设置`android:theme="@style/FruitActivityTheme"`;

即可实现状态栏沉浸式效果.