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


# 坑点

### 加载布局
最好是像下面这样加载,这样加载之后,会整个子项占满一行.

	View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item,parent,false);
