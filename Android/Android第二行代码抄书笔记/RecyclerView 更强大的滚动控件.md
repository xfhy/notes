# RecyclerView 更强大的滚动控件

[TOC]

## 1. 基本用法

1. 打开app/build.gradle文件,在dependencies闭包中添加如下内容:
`compile 'com.android.support:recyclerview-v7:24.2.1'`

2. 关于xml布局文件写法

		<android.support.v7.widget.RecyclerView
	        android:id="@+id/rv_fruit"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"/>

3. 写一个bean对象

	public class Fruit {
	
	    private String name;   //水果名
	    private int imageId;   //水果图片
	
	    public Fruit(String name, int imageId) {
	        this.name = name;
	        this.imageId = imageId;
	    }
	
	    public String getName() {
	        return name;
	    }
	
	    public int getImageId() {
	        return imageId;
	    }
	}

4. 写一个Adapter,继承自RecyclerView.Adapter,泛型是自己Adapter里面的内部静态类

		public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder>{
		
		    private List<Fruit> mFruitList; //存放数据的集合
		
		    //用于缓存
		    static class ViewHolder extends RecyclerView.ViewHolder{
		
		        ImageView fruitImage;
		        TextView fruitName;
		
		        //构造方法
		        public ViewHolder(View itemView) {
		            super(itemView);
		            fruitImage = (ImageView)itemView.findViewById(R.id.iv_fruit_image);
		            fruitName = (TextView)itemView.findViewById(R.id.tv_fruit_name);
		        }
		    }
		
		    //构造方法  初始化数据
		    public FruitAdapter(List<Fruit> fruitList){
		        mFruitList = fruitList;
		    }
		
		    //创建ViewHolder实例
		    @Override
		    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		        //加载布局
		        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item,parent,false);
		        ViewHolder viewHolder = new ViewHolder(view);
		        return viewHolder;   //创建ViewHolder对象并返回
		    }
		
		    //子项数据进行赋值,当滚动到屏幕内时执行
		    @Override
		    public void onBindViewHolder(ViewHolder holder, int position) {
		        Fruit fruit = mFruitList.get(position);
		        holder.fruitName.setText(fruit.getName());
		        holder.fruitImage.setImageResource(fruit.getImageId());
		    }
		
		    //多少个子项
		    @Override
		    public int getItemCount() {
		        return mFruitList.size();
		    }
		}

5. 最后一步,将RecyclerView显示到界面上

		public class MainActivity extends AppCompatActivity {
	
	    private List<Fruit> fruitList = new ArrayList<>();
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	
	        initFruits();  //1. 初始化水果数据
	        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_fruit);
	
	        //2. 创建LayoutManager对象
	        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
	        //3. 设置LayoutManager对象  LayoutManager用于指定RecyclerView的布局方式,这里使用的LinearLayoutManager
        	//是线性布局的意思    可以实现和ListView类似的效果
	        recyclerView.setLayoutManager(layoutManager);
	        //4. 创建Adapter对象
	        FruitAdapter fruitAdapter = new FruitAdapter(fruitList);
	        //5. 设置Adapter
	        recyclerView.setAdapter(fruitAdapter);
	
	    }
	
	    private void initFruits() {
	        for (int i = 0; i < 2; i++) {
	            Fruit apple = new Fruit("Apple", R.drawable.apple_pic);
	            fruitList.add(apple);
	            Fruit banana = new Fruit("Banana", R.drawable.banana_pic);
	            fruitList.add(banana);
	            Fruit orange = new Fruit("Orange", R.drawable.orange_pic);
	            fruitList.add(orange);
	            Fruit watermelon = new Fruit("Watermelon", R.drawable.watermelon_pic);
	            fruitList.add(watermelon);
	        }
	    }
	}

## 2. RecyclerView.LayoutManager

这是一个抽象类，系统提供了3个实现类：

 - LinearLayoutManager 现行管理器，支持横向、纵向。

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);  //设置水平排列

 - GridLayoutManager 网格布局管理器

		recyclerView.setLayoutManager(new GridLayoutManager(this,2));
	
 - StaggeredGridLayoutManager 瀑布流式布局管理器

		//创建StaggeredGridLayoutManager实例,   参数:列数,排列方向
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL);

  		下面是Adapter  随机设置子项的高度

		public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder>{

	    private List<Fruit> mFruitList; //存放数据的集合
	    private List<Integer> mHeights;
	
	    //用于缓存
	    static class ViewHolder extends RecyclerView.ViewHolder{
	
	        ImageView fruitImage;
	        TextView fruitName;
	
	        //构造方法
	        public ViewHolder(View itemView) {
	            super(itemView);
	            fruitImage = (ImageView)itemView.findViewById(R.id.iv_fruit_image);
	            fruitName = (TextView)itemView.findViewById(R.id.tv_fruit_name);
	        }
	    }
	
	    //构造方法  初始化数据
	    public FruitAdapter(List<Fruit> fruitList){
	        mFruitList = fruitList;
	
	        //随机生成子项的高度
	        mHeights = new ArrayList<>();
	        for (int i = 0; i < mFruitList.size(); i++) {
	            mHeights.add((int)(100+Math.random()*300));
	        }
	    }
	
	    //创建ViewHolder实例
	    @Override
	    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        //加载布局
	        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item,parent,false);
	        ViewHolder viewHolder = new ViewHolder(view);
	        return viewHolder;   //创建ViewHolder对象并返回
	    }
	
	    //子项数据进行赋值,当滚动到屏幕内时执行
	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	        //设置子项图片的高度
	        ViewGroup.LayoutParams layoutParams = holder.fruitImage.getLayoutParams();
	        layoutParams.height = mHeights.get(position);
	        holder.fruitImage.setLayoutParams(layoutParams);
	
	        Fruit fruit = mFruitList.get(position);
	        holder.fruitName.setText(fruit.getName());
	        holder.fruitImage.setImageResource(fruit.getImageId());
	    }
	
	    //多少个子项
	    @Override
	    public int getItemCount() {
	        return mFruitList.size();
	    }
	}



## 3. RecyclerView的点击事件

> RecyclerView的强大之处也在这里,它可以轻松实现子项中任意控件或布局的点击事件.

	public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder>{
	
	    private List<Fruit> mFruitList; //存放数据的集合
	    private List<Integer> mHeights;
	
	    //用于缓存
	    static class ViewHolder extends RecyclerView.ViewHolder{
	        View fruitView;  //保存子项最外层布局的实例
	        ImageView fruitImage;
	        TextView fruitName;
	
	        //构造方法
	        public ViewHolder(View itemView) {
	            super(itemView);
	            fruitView = itemView;
	            fruitImage = (ImageView)itemView.findViewById(R.id.iv_fruit_image);
	            fruitName = (TextView)itemView.findViewById(R.id.tv_fruit_name);
	        }
	    }
	
	    //构造方法  初始化数据
	    public FruitAdapter(List<Fruit> fruitList){
	        mFruitList = fruitList;
	
	        //随机生成子项的高度
	        mHeights = new ArrayList<>();
	        for (int i = 0; i < mFruitList.size(); i++) {
	            mHeights.add((int)(100+Math.random()*300));
	        }
	    }
	
	    //创建ViewHolder实例
	    @Override
	    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        //加载布局
	        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item,parent,false);
	        final ViewHolder viewHolder = new ViewHolder(view);
	
	        //设置子项的点击事件
	        viewHolder.fruitView.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                //返回此ViewHolder表示的项目的适配器位置
	                int adapterPosition = viewHolder.getAdapterPosition();
	                Fruit fruit = mFruitList.get(adapterPosition);
	                Toast.makeText(v.getContext(),"you clicked view "+fruit.getName(),Toast.LENGTH_SHORT).show();
	            }
	        });
	
	        viewHolder.fruitImage.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                int adapterPosition = viewHolder.getAdapterPosition();
	                Fruit fruit = mFruitList.get(adapterPosition);
	                Toast.makeText(v.getContext(), "you clicked image "+fruit.getName(), Toast.LENGTH_SHORT).show();
	            }
	        });
	
	        return viewHolder;   //创建ViewHolder对象并返回
	    }
	
	    //子项数据进行赋值,当滚动到屏幕内时执行
	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	        //设置子项图片的高度
	        ViewGroup.LayoutParams layoutParams = holder.fruitImage.getLayoutParams();
	        layoutParams.height = mHeights.get(position);
	        holder.fruitImage.setLayoutParams(layoutParams);
	
	        Fruit fruit = mFruitList.get(position);
	        holder.fruitName.setText(fruit.getName());
	        holder.fruitImage.setImageResource(fruit.getImageId());
	    }
	
	    //多少个子项
	    @Override
	    public int getItemCount() {
	        return mFruitList.size();
	    }
	}


# 4. 数据更新

		//当有新消息时  刷新RecyclerView中的显示
         adapter.notifyItemInserted(msgList.size()-1);   //这个的效果比下面那个效果好(有动画)
          adapter.notifyDataSetChanged();

# 5. 添加分割线

1. 先到`drawable`下创建`divider_bg.xml`文件,写入如下代码:

		<?xml version="1.0" encoding="utf-8"?>
		<shape xmlns:android="http://schemas.android.com/apk/res/android"
		    android:shape="rectangle" >
		
		    <solid android:color="#F0F0F0"/>
		    <size android:height="2dp"/>
		
		</shape>

2. 再到`values.xml->styles.xml`下,在`<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">`中添加`<item name="android:listDivider">@drawable/divider_bg</item>`

3. 写一个实现类，新建DividerItemDecoration

		public class DividerItemDecoration extends RecyclerView.ItemDecoration {
	
	    //使用系统自带的listDivider
	    private static final int[] ATTRS = new int[]{
	        android.R.attr.listDivider
	    };
	
	    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
	    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
	
	    private Drawable mDivider;
	    private int mOrientation;
	
	    public DividerItemDecoration(Context context,int orientation){
	        //使用TypeArray加载该系统资源
	        final TypedArray ta = context.obtainStyledAttributes(ATTRS);
	        mDivider = ta.getDrawable(0);
	        //缓存
	        ta.recycle();
	        setOrientation(orientation);
	    }
	
	    public void setOrientation(int orientation){
	        if(orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST){
	            throw new IllegalArgumentException("invalid orientation");
	        }
	        mOrientation = orientation;
	    }
	    @Override
	    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
	        if(mOrientation == VERTICAL_LIST){
	            drawVertical(c,parent);
	        }else{
	            drawHorizontal(c,parent);
	        }
	    }
	
	    public void drawVertical(Canvas c,RecyclerView parent){
	        //获取分割线的左边距，即RecyclerView的padding值
	        final int left = parent.getPaddingLeft();
	        //分割线右边距
	        final int right = parent.getWidth() - parent.getPaddingRight();
	        final int childCount = parent.getChildCount();
	        //遍历所有item view，为它们的下方绘制分割线
	        for(int i=0;i<childCount;i++){
	            final View child = parent.getChildAt(i);
	            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
	            final int top = child.getBottom() + params.bottomMargin;
	            final int bottom = top + mDivider.getIntrinsicHeight();
	            mDivider.setBounds(left,top,right,bottom);
	            mDivider.draw(c);
	        }
	    }
	
	    public void drawHorizontal(Canvas c, RecyclerView parent) {
	        final int top = parent.getPaddingTop();
	        final int bottom = parent.getHeight() - parent.getPaddingBottom();
	
	        final int childCount = parent.getChildCount();
	        for (int i = 0; i < childCount; i++) {
	            final View child = parent.getChildAt(i);
	            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
	                    .getLayoutParams();
	            final int left = child.getRight() + params.rightMargin;
	            final int right = left + mDivider.getIntrinsicHeight();
	            mDivider.setBounds(left, top, right, bottom);
	            mDivider.draw(c);
	        }
	    }
	
	    @Override
	    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
	        if(mOrientation == VERTICAL_LIST){
	            //设置偏移的高度是mDivider.getIntrinsicHeight，该高度正是分割线的高度
	            outRect.set(0,0,0,mDivider.getIntrinsicHeight());
	        }else{
	            outRect.set(0,0,mDivider.getIntrinsicWidth(),0);
	        }
	    }
	}

4. 接着在MainActivity.java添加如下代码：

	mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
	DividerItemDecoration.VERTICAL_LIST));

# 6.滑动加载更多

		//创建LayoutManager对象,并设置给RecyclerView
        mLayoutManager = new LinearLayoutManager(this);
        rv_blacknumber.setLayoutManager(mLayoutManager);

		/*
           创建滑动监听器
        * 对于RecyclerView的滚动监听
        * */
        mbBlNumScrollListener = new BlNumScrollListener();
        rv_blacknumber.addOnScrollListener(mbBlNumScrollListener);
	
	/**
     * RecyclerView滑动状态监听器
     */
    class BlNumScrollListener extends RecyclerView.OnScrollListener {
        //滑动状态发生改变
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            /*
                newState - 所有的RecyclerView的滑动状态
             *   SCROLL_STATE_IDLE       空闲状态
             *   SCROLL_STATE_DRAGGING   正在被手指拖动滑动
             *   SCROLL_STATE_SETTLING.  飞速滑动,手指飞速滑了一下,然后列表还在飞速的滚动中,这时候手指已离开屏幕
             */

            //1, 判断是否合法   如果列表为空,下面的都没有执行的意义了
            if (mBlackNumberList == null) {
                return;
            }

            /*
            * 2, 判断当前用户是否需要加载数据:
                * a, 是否处于空闲状态
                * b, 当前RecyclerView的最后一项是否可见
                * c, 当前是否处于正在加载的状态(防止重复加载)
            *
            * 如果当前正在加载mIsLoad就会为true,本次加载完毕后,再将isLoad改为false
						如果下一次加载需要去做执行的时候,会判断上诉mIsLoad变量,是否为false,如果为true,
						就需要等待上一次加载完成,将其值
						改为false后再去加载
            * */
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && mLayoutManager.findLastVisibleItemPosition() >= (mBlackNumberList.size() - 1)
                    && !isLoad) {

                //3, 获取数据库中的黑名单数量 知道是否还可以加载更多
                // 如果数据库中的数目大于当前集合的条目则去加载
                if (mCount > mBlackNumberList.size()) {
                    isLoad = true;
                    ToastUtil.show("加载更多");

                    //4, 开一个子线程,去数据库查询黑名单数据   部分查询
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //2, 获取到DAO
                            mDao = BlackNumberDao.getInstance(mContext);
                            //3, 查询黑名单部分数据
                            List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList
                                    .size());
                            mBlackNumberList.addAll(moreData);  //将这次查询的数据加到集合末尾

                            //4, 发送给主线程一个  查找黑名单  完成的消息
                            Message msg = Message.obtain();
                            msg.what = FIND_NUMBER_FINISHED;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                }

            }
        }

        //正在滑动
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

# 坑点

### 加载布局
最好是像下面这样加载,这样加载之后,会整个子项占满一行.

	View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item,parent,false);
