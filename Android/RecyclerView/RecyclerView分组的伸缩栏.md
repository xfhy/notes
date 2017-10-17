# RecyclerView分组的伸缩栏

> 利用第三方库BaseRecyclerViewAdapterHelper,地址:https://github.com/CymChad/BaseRecyclerViewAdapterHelper

1.layout_commonnum_group_item.xml文件

	<?xml version="1.0" encoding="utf-8"?>
	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
	                android:layout_width="match_parent"
	                android:layout_height="wrap_content"
	                android:padding="10dp">
	
	    <!--常用号码分组条目-->
	
	    <TextView
	        android:id="@+id/tv_commonnum_title"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_alignParentStart="true"
	        android:textColor="@android:color/black"
	        android:textSize="18sp"
	        android:textStyle="bold"/>
	
	    <ImageView
	        android:id="@+id/iv_expand"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_gravity="right|center"
	        android:layout_alignParentEnd="true"
	        android:src="@drawable/arrow_r"/>
	
	</RelativeLayout>

2.layout_commonnum_child_item.xml文件

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	              android:layout_width="match_parent"
	              android:layout_height="wrap_content"
	              android:orientation="vertical"
	              android:padding="10dp">
	
	    <!--常用号码的分组的子项条目布局-->
	
	    <TextView
	        android:id="@+id/tv_commonnum_address"
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:textColor="@color/bright_foreground_light_disabled"
	        android:textSize="18sp"
	        />
	
	    <TextView
	        android:id="@+id/tv_commonnum_number"
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:textColor="@color/bright_foreground_light_disabled"
	        android:textSize="18sp"
	        />
	
	</LinearLayout>

3.CommonnumDao.java

	/**
	 * Created by xfhy on 2017/5/20.
	 * 常用号码数据库的Dao
	 */
	
	public class CommonnumDao {

	    private static final String DB_PATH = "data/data/com.xfhy.mobilesafe/files/commonnum.db";
	    private static final String GROUP_TABLE_NAME = "classlist";
	    private static final String GROUP_TABLE_NAME_COLUMN = "name";
	    private static final String GROUP_TABLE_IDX_COLUMN = "idx";
	    private static final String CHILD_TABLE_ID_COLUMN = "_id";
	    private static final String CHILD_TABLE_NUMBER_COLUMN = "number";
	    private static final String CHILD_TABLE_NAME_COLUMN = "name";
	
	    public List<MultiItemEntity> getComNumGroup() {
	        //1, 打开数据库  以只读方式打开
	        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase
	                .OPEN_READONLY);
	        //2, 查询分组资料
	        Cursor cursor = db.query(GROUP_TABLE_NAME, new String[]{GROUP_TABLE_NAME_COLUMN,
	                        GROUP_TABLE_IDX_COLUMN}, null, null, null,
	                null, null);
	        //3, 将查询到的数据封装到集合中
	        List<MultiItemEntity> comnumGroupList = new ArrayList<>();
	        if (cursor != null && cursor.moveToFirst()) {
	            do {
	                ComNumGroup comNumGroup = new ComNumGroup();
	                comNumGroup.name = cursor.getString(cursor.getColumnIndex(GROUP_TABLE_NAME_COLUMN));
	                comNumGroup.idx = cursor.getString(cursor.getColumnIndex(GROUP_TABLE_IDX_COLUMN));
	                getComNumChild(comNumGroup,comNumGroup.idx);
	                comnumGroupList.add(comNumGroup);
	            } while (cursor.moveToNext());
	
	        }
	        //4, 记得关闭资源
	        cursor.close();
	        db.close();
	
	        return comnumGroupList;
	    }
	
	    public void getComNumChild(ComNumGroup comNumGroup,String idx) {
	        //1, 打开数据库  以只读方式打开
	        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase
	                .OPEN_READONLY);
	        //2, 查询数据库中table1,2,3,...表
	        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
	        if (cursor != null && cursor.moveToFirst()) {
	            do {
	                ComNumChild comnumChild = new ComNumChild();
	                comnumChild._id = cursor.getString(cursor.getColumnIndex(CHILD_TABLE_ID_COLUMN));
	                comnumChild.name = cursor.getString(cursor.getColumnIndex(CHILD_TABLE_NAME_COLUMN));
	                comnumChild.number = cursor.getString(cursor.getColumnIndex
	                        (CHILD_TABLE_NUMBER_COLUMN));
	                comNumGroup.addSubItem(comnumChild);
	            } while (cursor.moveToNext());
	
	        }
	        //4, 记得关闭资源
	        cursor.close();
	        db.close();
	    }
	
	    /**
	     * 常用电话的分组表的数据
	     */
	    public class ComNumGroup extends AbstractExpandableItem implements MultiItemEntity {
	        public String name;
	        public String idx;
	
	        @Override
	        public int getLevel() {
	            return 0;
	        }
	
	        @Override
	        public int getItemType() {
	            return 0;
	        }
	    }
	
	    /**
	     * 常用电话的分组表对应的子表
	     */
	    public class ComNumChild extends AbstractExpandableItem implements MultiItemEntity{
	        public String _id;
	        public String number;
	        public String name;
	
	        @Override
	        public int getLevel() {
	            return 1;
	        }
	
	        @Override
	        public int getItemType() {
	            return 1;
	        }
	    }
	
	}

4.CommonNumberQueryActivity.java

		public class CommonNumberQueryActivity extends BaseActivity {
	    private static final String TAG = "CommonNumberQueryActivity";
	
	    @BindView(R.id.rv_commonnum)
	    RecyclerView rvCommonnum;
	    private CommonnumDao mDao;
	    /**
	     * 常用号码数据
	     */
	    private List<MultiItemEntity> mComNumGroupList = new ArrayList<>();
	    private static final int LOAD_DATA_FINISHED = 1000;
	    private Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case LOAD_DATA_FINISHED:
	                    mAdapter = new ExpandableItemAdapter
	                            (mComNumGroupList);
	                    rvCommonnum.setAdapter(mAdapter);
	                    break;
	            }
	        }
	    };
	    private ExpandableItemAdapter mAdapter;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_common_number_query);
	        ButterKnife.bind(this);
	
	        initUI();
	        initData();
	    }
	
	    /**
	     * 初始化UI
	     */
	    private void initUI() {
	        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
	        rvCommonnum.setLayoutManager(linearLayoutManager);
	    }
	
	    /**
	     * 初始化数据
	     */
	    private void initData() {
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                //1, 创建dao
	                mDao = new CommonnumDao();
	                //2, 获取数据,从数据库
	                mComNumGroupList = mDao.getComNumGroup();
	
	                //3, 加载完成 发送给主线程消息
	                Message msg = Message.obtain();
	                msg.what = LOAD_DATA_FINISHED;
	                mHandler.sendMessage(msg);
	            }
	        }).start();
	
	    }
	
	    /**
	     * 条目适配器
	     */
	    class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
	        /**
	         * 扩展的类型
	         */
	        public static final int TYPE_LEVEL_GROUP = 0;
	        public static final int TYPE_LEVEL_CHILD = 1;
	
	        public ExpandableItemAdapter(List<MultiItemEntity> data) {
	            super(data);
	            //需要加载的布局   各种类型
	            addItemType(TYPE_LEVEL_GROUP, R.layout.layout_commonnum_group_item);
	            addItemType(TYPE_LEVEL_CHILD, R.layout.layout_commonnum_child_item);
	        }
	
	        //设置数据和点击事件等
	        @Override
	        protected void convert(final BaseViewHolder holder, final MultiItemEntity item) {
	            switch (holder.getItemViewType()) {
	                case TYPE_LEVEL_GROUP:
	                    final CommonnumDao.ComNumGroup lv0 = (CommonnumDao.ComNumGroup) item;
	                    holder.setText(R.id.tv_commonnum_title, lv0.name)
	                            .setImageResource(R.id.iv_expand, lv0.isExpanded() ? R.drawable
	                                    .arrow_b : R
	                                    .drawable.arrow_r);
	                    //set view content
	                    holder.itemView.setOnClickListener(new View.OnClickListener() {
	                        @Override
	                        public void onClick(View v) {
	                            int pos = holder.getAdapterPosition();
	                            if (lv0.isExpanded()) {
	                                collapse(pos);
	                            } else {
	                                expand(pos);
	                            }
	                        }
	                    });
	                    break;
	                case TYPE_LEVEL_CHILD:
	                    // similar with level 0
	                    final CommonnumDao.ComNumChild lv1 = (CommonnumDao.ComNumChild) item;
	                    holder.setText(R.id.tv_commonnum_address, lv1.name)
	                            .setText(R.id.tv_commonnum_number, lv1.number);
	                    //set view content
	                    holder.itemView.setOnClickListener(new View.OnClickListener() {
	                        @Override
	                        public void onClick(View v) {
	                            //拨打电话
	                            String number = lv1.number;
	                            LogUtil.d(TAG, "电话:" + number);
	                        }
	                    });
	                    break;
	            }
	        }
	    }
	
	}

