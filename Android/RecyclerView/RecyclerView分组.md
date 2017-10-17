# RecyclerView分组

这里用了很笨的方法,通过设置不同的viewType,知道条目的类型,然后加载不同的布局.header和normal条目用的是不同的布局.

简单实现如下:
	
public class AppManagerActivity extends BaseActivity {


	    /**
	     * 读取所有应用信息完成
	     */
	    private static final int LOAD_APPINFO_FINISHED = 1001;
	
	    private Context mContext;
	    private static final String TAG = "AppManagerActivity";
	    /**
	     * app列表
	     */
	    private RecyclerView rv_app_list;
	    /**
	     * 所有app信息集合
	     */
	    private List<AppInfo> appInfoList;
	    /**
	     * app信息集合适配器
	     */
	    private MyAppInfoAdapter mAdapter;
	
	    private Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case LOAD_APPINFO_FINISHED:
	                    mAdapter = new MyAppInfoAdapter();
	                    rv_app_list.setAdapter(mAdapter);
	                    mAppLayout.onDone();  //加载完成
	                    break;
	            }
	        }
	    };
	    private List<AppInfo> mSystemList;
	    private List<AppInfo> mCustomerList;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_app_manager);
	        mContext = this;
	
	        initAppList();     //初始化App列表
	    }
	
	    /**
	     * 初始化App列表
	     */
	    private void initAppList() {
	        //找到控件
	        rv_app_list = (RecyclerView) findViewById(R.id.rv_app_list);
	        //设置RecyclerView的LayoutManager
	        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
	        rv_app_list.setLayoutManager(layoutManager);
	
	        //开启子线程获取所有的app信息
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                //读取所有应用信息
	                appInfoList = AppInfoProvider.getAppInfoList(mContext);
	                mSystemList = new ArrayList<>();
	                mCustomerList = new ArrayList<>();
	                for (AppInfo appInfo : appInfoList) {
	                    if (appInfo.isSystem()) {
	                        //系统应用
	                        mSystemList.add(appInfo);
	                    } else {
	                        //用户应用
	                        mCustomerList.add(appInfo);
	                    }
	                }
	                //最后统一放到一个集合中,比较方便管理
	                appInfoList.clear();
	                appInfoList.addAll(mCustomerList);
	                appInfoList.addAll(mSystemList);
	
	                //读取完成  向主线程发送完成消息  更新UI
	                Message msg = Message.obtain();
	                msg.what = LOAD_APPINFO_FINISHED;
	                mHandler.sendMessage(msg);
	            }
	        }).start();
	    }
	
	
	    /**
	     * app列表适配器
	     */
	    class MyAppInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	
	        //头布局的ViewHolder
	        class HeaderViewHolder extends RecyclerView.ViewHolder {
	
	            TextView tv_app_group_name; //组名
	
	            public HeaderViewHolder(View itemView) {
	                super(itemView);
	                tv_app_group_name = (TextView) itemView.findViewById(R.id.tv_app_group_name);
	            }
	        }
	
	        //用于缓存数据
	        class NormalViewHolder extends RecyclerView.ViewHolder {
	
	            ImageView appIcon;  //应用图标
	            TextView appName;  //应用名称
	            TextView appPath;  //应用路径
	
	            public NormalViewHolder(View itemView) {
	                super(itemView);
	
	                //找到控件
	                appIcon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
	                appName = (TextView) itemView.findViewById(R.id.tv_app_name);
	                appPath = (TextView) itemView.findViewById(R.id.tv_app_path);
	            }
	        }
	
	        /**
	         * 条目的类型
	         */
	        class ItemType {
	            public static final int HEADER = 0;   //头部
	            public static final int NORMAL = 1;   //正常条目
	        }
	
	        //创建ViewHolder实例
	        //onCreateViewHolder方法的第二个参数viewType，我们可以根据viewType的值，来加载不同的布局。
	        @Override
	        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	            //加载子项布局
	            View view;
	            if (viewType == ItemType.HEADER) {
	                view = LayoutInflater.from(mContext).inflate(R.layout.layout_appinfo_item_header,
	                        parent, false);
	                return new HeaderViewHolder(view);
	            } else if (viewType == ItemType.NORMAL) {
	                view = LayoutInflater.from(mContext).inflate(R.layout.layout_appinfo_item_normal,
	                        parent, false);
	                //创建ViewHolder对象并返回
	                return new NormalViewHolder(view);
	            }
	            return null;
	        }
	
	        //子项数据进行赋值,当滚动到屏幕内时执行此方法
	        @Override
	        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
	            //非法判断
	            if (appInfoList == null) {
	                mAppLayout.onEmpty();
	                return;
	            }
	
	            //判断当前的条目是否是header
	            if (holder instanceof HeaderViewHolder) {
	                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
	                if (position == 0) {
	                    headerViewHolder.tv_app_group_name.setText("用户应用");
	                } else if (position == (mCustomerList.size() + 1)) {
	                    headerViewHolder.tv_app_group_name.setText("系统应用");
	                }
	            } else if (holder instanceof NormalViewHolder) {
	                //正常ViewHolder
	                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
	
	                AppInfo appInfo = null;
	                if (position < (mCustomerList.size() + 1)) {  //手机应用区域
	                    appInfo = appInfoList.get(position - 1);
	                } else if (position > (mCustomerList.size() + 1)) {  //系统应用区域
	                    appInfo = appInfoList.get(position - 2);
	                }
	
	                if (appInfo != null) {
	                    normalViewHolder.appIcon.setImageDrawable(appInfo.getIcon());
	                    normalViewHolder.appName.setText(appInfo.getName());
	                    if (appInfo.isSdcard()) {
	                        normalViewHolder.appPath.setText("SD卡应用");
	                    } else {
	                        normalViewHolder.appPath.setText("手机应用");
	                    }
	                }
	
	            }
	        }
	
	        //子项数目
	        @Override
	        public int getItemCount() {
	            //这里多加了2个header,所以子项数目+2
	            return appInfoList == null ? 0 : (appInfoList.size() + 2);
	        }
	
	        //子项类型
	        @Override
	        public int getItemViewType(int position) {
	            if (position == 0 || position == (mCustomerList.size() + 1)) {
	                //header
	                return ItemType.HEADER;
	            } else {
	                //normal
	                return ItemType.NORMAL;
	            }
	        }
	    }
	
	}

