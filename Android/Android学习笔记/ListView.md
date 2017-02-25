# ListView


# 1. listview 入门
> ListView 是一个控件,一个在垂直滚动的列表中显示条目的一个控件，这些条目的内容来自于一个ListAdapter 。EditText Button TextView ImageView Checkbox 五大布局。


1. 布局添加Listview
		
2. 找到listview

3. 创建一个Adapter适配器继承BaseAdapter，封装4个方法，其中getcount,getview必须封装

			getcount:告诉listview要显示的条目数
			getview：告诉listview每个条目显示的内容。

4. 创建Adapter的一个对象，设置给listview。
				listview.setAdapter(ListAdapter adapter);
	
# 2. listview优化 

> adapter中getview方法会传进来一个convertView，convertView是指曾经使用过的view对象，可以被重复使用，但是在使用前需要判断是否为空，不为空直接复用，并作为getview方法的返回对象。

			TextView view = null;
			if(convertView != null){//判断converView是否为空，不为空重新使用
				view = (TextView) convertView;
			}else{
				view = new TextView(mContext);//创建一个textView对象
			}
			return view；

****************第一行代码   书里面的

		public View getView(int position, View convertView, ViewGroup parent) {
		Fruit fruit = getItem(position); // 首先获得当前项的Fruit实例
		// 加载传入的布局
		View view; 
		ViewHolder viewHolder;
		if (convertView == null) {  //如果先前没有加载,则就没有缓存View,则需要加载一下
			//加载传入的布局
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			//获得一个选项布局中的控件id
			viewHolder.fruitimage = (ImageView) view.findViewById(R.id.fruit_image);
			viewHolder.fruitname = (TextView) view.findViewById(R.id.fruit_name);
			view.setTag(viewHolder);  //将这个内部类存到view里面
		} else {  //第二次加载,则只需要加载之前的缓存数据
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		//设置布局文件中控件的数据
		viewHolder.fruitimage.setImageResource(fruit.getImageId()); // 设置图片和文字
		viewHolder.fruitname.setText(fruit.getName());
		return view;
	}
	
	//这是一个内部类,用于缓存已经加载过了的布局上的id
	class ViewHolder{
		ImageView fruitimage;
		TextView fruitname;
	}

# 3. listview---老虎机
	
	javaweb mvc
	m....mode....javabean
	v....view....jsp
	c....control...servlet
	
	listview mvc
	m....mode....Bean
	v....view....listview
	c....control...adapter
 	

# 4. listview显示原理 (了解)

1. 要考虑listview显示的条目数    getcount
2. 考虑listview每个条目显示的内容   getview
3. 考虑每个item的高度，因为屏幕的多样化
4. 还要考虑listview的滑动，监听一个旧的条目消失，一个新的条目显示。


# 5. 复杂listview界面显示 ,新闻案例（***********重要***********）

1. 布局写listview

2. 找到listview

3. 获取新闻数据封装到list集合中(才用模拟数据)，作为adapter的显示数据,怎么将获取的新闻数据给adapter???

4. 创建一个adapter继承BaseAdapter，实现4个方法
		getcount: 有多少条新闻数据，就有多少个条目。
		getView:将返回一个复杂的布局作为条目的内容展示；并且显示的数据是新闻的信息。 ？？？？？
		
	public View getView(int position, View convertView, ViewGroup parent) {
		//1.获取position位置条目对应的list集合中的新闻,Bean对象
		NewsBean newsBean = list.get(position);
		
		View view = null;
		
		ViewHolder viewHolder = null;   //用来缓存view上的数据
		
		//2.复用convertView优化listview,创建一个view作为getview的返回值用来显示一个条目
		if(convertView != null){
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();   //将之前缓存的数据  恢复
		} else {
			//加载布局
			view = View.inflate(context, R.layout.item_news_layout, null);
			viewHolder = new ViewHolder();
			
			//3.获取view上的子控件   存到  viewHolder里面
			viewHolder.item_img_icon = (ImageView)view.findViewById(R.id.item_img_icon);
			viewHolder.item_tv_title = (TextView)view.findViewById(R.id.item_tv_title);
			viewHolder.item_tv_des = (TextView)view.findViewById(R.id.item_tv_des);
			
			view.setTag(viewHolder);   //将这个内部类(缓存数据的类)存到view里面
			
		}
		//4.将数据设置给这些子控件显示
		 //view子控件的id在ViewHolder里面,直接可以设置里面的数据,就等于在设置view里面的子控件的数据
		viewHolder.item_img_icon.setImageDrawable(newsBean.news_icon);   
		viewHolder.item_tv_title.setText(newsBean.title);
		viewHolder.item_tv_des.setText(newsBean.des);
		
		return view;
	}

	//用来缓存 item条目上的所有的控件对象
	class ViewHolder{
		ImageView item_img_icon;
		TextView item_tv_title;
		TextView item_tv_des;
	}
		
5. 创建一个adapter对象设置给listview

6. 设置listview的条目的点击事件，并封装点击事件,去查看新闻详情。 ?????????
		//设置listview条目的点击事件
		lv_news.setOnItemClickListener(this);
	
			//listview的条目点击时会调用该方法 parent:代表listviw  view:点击的条目上的那个view对象   position:条目的位置  id： 条目的id

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		//需要获取条目上bean对象中url做跳转
		NewsBean bean = (NewsBean) parent.getItemAtPosition(position);
		
		String url = bean.news_url;
		
		//跳转浏览器
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

		

	1.布局写listview ok

	2.找到listview ok 
	
	3.封装新闻数据到list集合中 ，目的是为adapter提供数据展示。 ok 

	4.封装一个Adapter类继承BaseAdatper，写一个构造方法接受list集合数据，复写四个方法
		a.创建一个构造方法  ok 
		b.封装getCount方法   ok 
		c.getView方法：   不ok
			1.复用convertview，模板代码,如果不都能空，需要将一个布局文件转换为view对象作为getview的返回对象。
				view = View.inflater(Context context, int resuorceId,ViewGroup root)
			2.找到view上的这些子控件，目的是将list集合中的bean数据一一对应设置给这些子控件

			3.从list集合中获取postion条目上要显示的数据Bean
			
			4.将获取的bean中的数据设置给这些子控件
		d.getItem方法：将list集合中指定postion上的bean对象返回
		e.getItemId,直接返回postion

	5.创建一个封装的Adapter对象，设置给listview   ok
	6.设置listview条目的点击事件  ok
		listview.setOnItem....

	7.复写OnItemClicklistener方法，获取相应条目上的bean对象，最终获取到url，做Intent跳转;  不ok


# 6. 常用获取inflate的写法 

			1.
			//context:上下文, resource:要转换成view对象的layout的id, root:将layout用root(ViewGroup)包一层作为codify的返回值,一般传null
				//view = View.inflate(context, R.layout.item_news_layout, null);//将一个布局文件转换成一个view对象

			2.
			//通过LayoutInflater将布局转换成view对象
			//view =  LayoutInflater.from(context).inflate(R.layout.item_news_layout, null);
			
			3.
			//通过context获取系统服务得到一个LayoutInflater，通过LayoutInflater将一个布局转换为view对象
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.item_news_layout, null);
	
# 7. arrayadapter  (不用看，知道有这个玩意就行)
		//找到控件
		ListView lv_array = (ListView) findViewById(R.id.lv_array);
		ListView lv_simple = (ListView) findViewById(R.id.lv_simple);
		
		//创建一个arrayAdapter
	//context  , resource:布局id, textViewResourceId：条目布局中 textview控件的id, objects:条目上texitview显示的内容
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_listview_layout, R.id.item_tv_class, classz);
		lv_array.setAdapter(arrayAdapter);
		
		
# 8. simpleadapter   (不用看，知道有这个玩意就行)


		//创建一个simpleAdapter,封装simpleAdapter的数据
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String,String>>();
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("class", "C++");
		arrayList.add(hashMap);
		
		HashMap<String, String> hashMap1 = new HashMap<String, String>();
		hashMap1.put("class", "android");
		arrayList.add(hashMap1);
		
		
		HashMap<String, String> hashMap2 = new HashMap<String, String>();
		hashMap2.put("class", "javaEE");
		arrayList.add(hashMap2);
		
		//context, data:显示的数据, resource:item布局id, from: map中的key, to:布局中的控件id
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.item_listview_layout, new String[]{"class"}, new int[]{R.id.item_tv_class});
		
		lv_simple.setAdapter(simpleAdapter);

