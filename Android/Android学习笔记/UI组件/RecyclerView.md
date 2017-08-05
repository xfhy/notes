# RecyclerView

## 1.RecyclerView基本使用:

1,添加依赖;

2,布局:android.support.v7.widget.RecyclerView;

3,找控件,初始化;

4,设置LayoutManager;

5,设置适配器RecyclerView.Adapter

## 2.RecyclerView的Adapter的设计

		public class BehindArticleAdapter extends RecyclerView.Adapter<BehindArticleAdapter.ViewHolder>
	        implements View.OnClickListener {
	
	    private static final String TAG = "BehindArticleAdapter";
	    /**
	     * 文章列表
	     */
	    private List<ArticleBean> articleBeanList;
	    private Context context;
	    private RecyclerView mRecyclerView;
	    LayoutInflater inflater;
	
	    public BehindArticleAdapter(Context context, List<ArticleBean> articleBeanList) {
	        this.context = context;
	        inflater = LayoutInflater.from(context);
	        this.articleBeanList = articleBeanList;
	    }
	
	    @Override
	    public void onClick(View v) {
	        //条目点击事件
	        int childAdapterPosition = mRecyclerView.getChildAdapterPosition(v);
	
	        LogUtil.e(TAG, "onClick: 点击事件触发 位置: " + childAdapterPosition);
	
	        //开启文章详情页面
	        ArticleBean articleBean = articleBeanList.get(childAdapterPosition);
	        ArticleDetailsActivity.acionStart(context, articleBean
	                .getPostId(), articleBean.getLikeNum(), articleBean.getShareNum());
	    }
	
	    static class ViewHolder extends RecyclerView.ViewHolder {
	
	        ImageView articleImg;  //文章图片
	        TextView articleTitle; //文章标题
	        TextView shareNum;     //文章分享数量
	        TextView likeNum;      //文章喜欢数量
	
	        public ViewHolder(View itemView) {
	            super(itemView);
	
	            articleImg = (ImageView) itemView.findViewById(R.id.iv_behind_article_img);
	            articleTitle = (TextView) itemView.findViewById(R.id.tv_article_title);
	            shareNum = (TextView) itemView.findViewById(R.id.tv_share_num);
	            likeNum = (TextView) itemView.findViewById(R.id.tv_like_num);
	        }
	    }
	
	    @Override
	    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
	        this.mRecyclerView = recyclerView;
	        super.onAttachedToRecyclerView(recyclerView);
	    }
	
	    @Override
	    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        View view = inflater.inflate(R.layout.item_behind_article_layout, parent, false);
	        view.setOnClickListener(this);
	        return new ViewHolder(view);
	    }
	
	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	        if (articleBeanList != null && articleBeanList.size() > position) {
	            ArticleBean articleBean = articleBeanList.get(position);
	
	            //用Glide加载图片
	            Glide.with(context)
	                    .load(articleBean.getImageUrl())
	                    .into(holder.articleImg);
	
	            //标题
	            holder.articleTitle.setText(articleBean.getTitle());
	            //分享数
	            holder.shareNum.setText(articleBean.getShareNum());
	            //喜欢数
	            holder.likeNum.setText(articleBean.getLikeNum());
	        }
	    }
	
	    @Override
	    public int getItemCount() {
	        return articleBeanList == null ? 0 : articleBeanList.size();
	    }
	
	    /**
	     * 刷新数据源  更新Header的数据
	     *
	     * @param data 最新的数据
	     */
	    public void updateData(List<ArticleBean> data) {
	        if (data == null) {
	            return;
	        }
	        if (articleBeanList == null) {
	            articleBeanList = new ArrayList<>();
	        }
	        articleBeanList.clear();
	        articleBeanList.addAll(data);
	        LogUtil.e(TAG, "updateData: " + articleBeanList.toString());
	        //更新适配器
	        this.notifyDataSetChanged();
	    }
	
	    /**
	     * 刷新数据源  添加数据
	     *
	     * @param data 最新的数据
	     */
	    public void addData(List<ArticleBean> data) {
	        if (data == null) {
	            return;
	        }
	        if (articleBeanList == null) {
	            articleBeanList = new ArrayList<>();
	        }
	        articleBeanList.addAll(data);
	        LogUtil.e(TAG, "addData: " + articleBeanList.toString());
	        //更新适配器
	        this.notifyDataSetChanged();
	    }
	
	}

## 3.RecyclerView添加滑动条

	android:scrollbars="vertical" 表示滑动时有垂直的滑动条
	
## 4.设置RecyclerView显示样式

`mRecycler.setLayoutManager(new GridLayoutManager(this,2));`


## 5.设置点击事件

> 给RecyclerView设置点击事件,重写onAttachedToRecyclerView拿到recyclerView的布局.

	//recyclerView开始使用该adapter时调用此方法
	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
	    this.recyclerView = recyclerView;
	    super.onAttachedToRecyclerView(recyclerView);
	}
	
	@Override
	public void onClick(View v) {
	    //条目点击事件
	    int childAdapterPosition = recyclerView.getChildAdapterPosition(v);
	
	    Log.e(TAG, "onClick: 点击事件触发 位置: " + childAdapterPosition);
	
	    //开启电源详情页面
	    Intent intent = new Intent(context, MovieDetailActivity.class);
	    //获取postid
	    intent.putExtra(MovieDetailActivity.POST_ID, dataList.get(childAdapterPosition)
	            .getPostid());
	    context.startActivity(intent);
	}

## 6.RecyclerView的滚动监听,实现上拉加载更多

> 写一个类继承自RecyclerView.OnScrollListener,然后当判断需要加载的时候调用onLoadMore()--这是自定义的方法

	public class EndLessOnScrollListener extends RecyclerView.OnScrollListener implements HttpCallbackListener {
	    //声明一个LinearLayoutManager
	    private LinearLayoutManager mLinearLayoutManager;
	
	    //已经加载出来的Item的数量
	    private int totalItemCount;
	
	    //当前页，从0开始
	    private int currentPage = 0;
	
	    //主要用来存储上一个totalItemCount
	    private int previousTotal = 0;
	
	    //在屏幕上可见的item数量
	    private int visibleItemCount;
	
	    //在屏幕可见的Item中的第一个
	    private int firstVisibleItem;
	
	    //是否正在上拉数据
	    private boolean loading = true;
	
	    /**
	     * 需要请求的url
	     */
	    private String requestUrl;
	    /**
	     * 网络访问回调接口
	     */
	    private HttpCallbackListener listener;
	    public static final int LOAD_MORE = 9999;
	    public EndLessOnScrollListener(LinearLayoutManager mLinearLayoutManager, String requestUrl,
	                                   HttpCallbackListener listener) {
	        this.mLinearLayoutManager = mLinearLayoutManager;
	        this.requestUrl = requestUrl;
	        this.listener = listener;
	    }
	
	    public EndLessOnScrollListener(LinearLayoutManager linearLayoutManager) {
	        this.mLinearLayoutManager = linearLayoutManager;
	    }
	
	    @Override
	    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
	        super.onScrolled(recyclerView, dx, dy);
	
	        visibleItemCount = recyclerView.getChildCount();
	        totalItemCount = mLinearLayoutManager.getItemCount();
	        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
	        if (loading) {
	
	            if (totalItemCount > previousTotal) {
	                //说明数据已经加载结束
	                loading = false;
	                previousTotal = totalItemCount;
	            }
	        }
	        //这里需要好好理解  总条目数-可见条目数 <= 第一个可见条目
	        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem) {
	            currentPage++;
	            onLoadMore();
	            loading = true;
	        }
	    }
	
	    /**
	     * 加载更多
	     */
	    public void onLoadMore(){
	        currentPage++;
	        //请求网络数据 并通过接口回调回去
	        HttpUtils.requestGet(requestUrl+currentPage,LOAD_MORE,this);
	    }
	
	    @Override
	    public void onFinish(int from, String response) {
	        if (listener != null) {
	            listener.onFinish(from,response);
	        }
	    }
	
	    @Override
	    public void onError(Exception e) {
	        if (listener != null) {
	            listener.onError(e);
	        }
	    }
	}

## 7.中间有间隔(GridLayoutManager)

在写频道的时候,发现RecyclerView中间始终有一定的间隔,很不爽

RecyclerView没有可以直接设置间距的属性，但可以用ItemDecoration来装饰一个item，所以继承重写ItemDecoration就可以实现间距了

啊,好吧,网上就这一种方式....然而我的却不起任何作用....尴尬

解决方式:老师说官方不建议设置ItemDecoration,然后需要动态设置子项的宽度和高度,根据屏幕的宽度来设置就行

