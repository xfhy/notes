# ContentProvider 内容提供者

[TOC]

#1. 为什么需要内容提供者
>ContentProvider(内容提供者)可以把私有的数据库内容暴露出来.在一个程序里写好了
ContentProvider则在其他的应用程序也可以根据标准来进行第一个应用的数据库的访问.

#2. 实现ContentProvider步骤(一般开发中用不到,因为都想隐藏数据)
1. 定义内容提供者,定义一个类继承自ContentProvider
2. 在清单文件中配置一下,类似于

            <!-- 配置内容提供者,android:authorities为该内容提供者取名作为在本应用中的唯一标识 -->
            <provider android:name="com.itheima.provider.AccoutProvider"
                android:authorities="com.itheima.provider"
                android:exported="true">
            </provider>

3. 定义一个UriMatcher

        private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

4. 写一个静态代码块,添加匹配规则

        static { // 静态代码块 将期望匹配的内容传递进去
            uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI("com.example.app.provider", "table1", TABLE1_DIR);
            uriMatcher.addURI("com.example.app.provider", "table1/#", TABLE1_ITEM);
            uriMatcher.addURI("com.example.app.provider", "table2", TABLE2_DIR);
            uriMatcher.addURI("com.example.app.provider", "table2/#", TABLE2_ITEM);
        }

5. 按照我们添加的匹配规则,暴露想要暴露的方法    实现query(),insert()方法等
6. __只要是通过内容提供者暴露出来的数据,其他应用访问的方式都是一样的,就是通过内容解析者__

		ContentResolver contentResolver = mContext.getContentResolver();
		        Uri uri = Uri.parse("content://com.itheima.provider/query");
		        Cursor cursor = contentResolver.query(uri, new String[] { "name",
		                "money" }, null, null, null);
		        if (cursor != null && cursor.moveToFirst()) {
		            do {
		                
		                String name = cursor.getString(cursor.getColumnIndex("name"));
		                String money = cursor.getString(cursor.getColumnIndex("money"));
		                
		                Log.d("xfhy", "程序二:  name:"+name+"  money:"+money);
		                
		            } while (cursor.moveToNext());
		        } else {
		            Toast.makeText(mContext, "未查询到数据", Toast.LENGTH_SHORT).show();
		        }

# 3. 读取联系人案例
>QQ ,微信,陌陌等

1. data表    data1列表里存的是所有联系人的所有信息(包含姓名,地址,邮箱等)    
    raw_contact_id 列是用来区分一共有几条联系人信息
    mimetype_id 列是用来区分数据类型(姓名,地址,或者邮箱)
2. raw_contacts 表中contact_id就是data表的 raw_contact_id

查询联系人步骤:

1. 先查询raw_contacts表的contact_id列,就知道有几条联系人
2. 我根据contact_id去查询data表,查询data1列和mimetype列
3. view_data是由data表和mimetype表的组合

**Android 第一行代码  书中示例**

		ArrayAdapter<String> adapter;
	    List<String> contactList = new ArrayList<>();
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	
	        ListView lv_contact = (ListView) findViewById(R.id.lv_contact);
	
	        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactList);
	        lv_contact.setAdapter(adapter);
	
	        //检查用户是否已经授权了读取联系人的权限     如果相等则授权了
	        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
	                != PackageManager.PERMISSION_GRANTED) {
	            //不相等   则申请权限
	            ActivityCompat.requestPermissions(this,
	                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
	        } else {
	            readContacts();
	        }
	    }
	
	    /**
	     * 读取联系人
	     */
	    private void readContacts() {
	        Cursor cursor = null;
	
	        try{
	            //查询联系人数据   得到Cursor对象
	            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	                    null,null,null,null);
	            if(cursor != null){
	                while(cursor.moveToNext()){
	                    //获取联系人姓名
	                    String name = cursor.getString(cursor.getColumnIndex(
	                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	                    //获取联系人号码
	                    String phone = cursor.getString(cursor.getColumnIndex(
	                            ContactsContract.CommonDataKinds.Phone.NUMBER));
	                    contactList.add(name+"\n"+phone);
	                }
	                adapter.notifyDataSetChanged();  //刷新一下ListView
	            }
	        } catch (Exception e){
	            e.printStackTrace();
	        } finally {
	            //最后一定要记得关闭cursor
	            if(cursor != null){
	                cursor.close();
	            }
	        }
	
	    }
	
	    //每申请一此危险权限,这个方法就会被调一次
	    @Override
	    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                           @NonNull int[] grantResults) {
	        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	        //根据申请码 进行判断
	        switch (requestCode) {
	            case 1:
	                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
	                    readContacts();
	                } else {
	                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
	                }
	
	                break;
	            default:
	                break;
	        }
	    }

**自己写的完整的读取联系人,并且排序**

		public class ContactListActivity extends BaseActivity {

	    private ListView lvContact;
	    private final static String TAG = "ContactListActivity";
	    /**
	     * 封装联系人数据
	     */
	    private List<HashMap<String, String>> contactList = new ArrayList<>();
	    /**
	     * 联系人数据准备就绪
	     */
	    private final static int DATA_IS_READY = 10001;
	    /**
	     * 封装联系人数据的集合HashMap中的key
	     */
	    private final static String CONTACT_NAME_KEY = "contact_name";
	    private final static String CONTACT_PHONE_KEY = "contact_phone";
	    /**
	     * 适配器
	     */
	    private MyContactAdapter mContactAdapter;
	
	
	    private Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case DATA_IS_READY:
	                    //初始化适配器
	                    mContactAdapter = new MyContactAdapter();
	                    //设置ListView的adapter
	                    lvContact.setAdapter(mContactAdapter);
	                    break;
	                default:
	                    break;
	            }
	        }
	    };
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_contact_list);
	
	        initUI();
	        requestReadContactPermission();
	    }
	
	    /**
	     * 申请联系人权限
	     */
	    private void requestReadContactPermission() {
	        //先申请读取联系人权限
	        //检查用户是否已经给我们授权了权限,相等则已经授权,不等则没授权
	        if (ContextCompat.checkSelfPermission(this, Manifest.permission
	                .READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
	            //参数:Context上下文,权限数组,申请码(申请码只要唯一就行)
	            ActivityCompat.requestPermissions(this, new String[]{Manifest
	                    .permission.READ_CONTACTS}, ConstantValue.MY_PERMISSIONS_READ_CONTACTS);
	        } else {
	            //如果已经有权限
	            initData();
	        }
	    }
	
	    /**
	     * 初始化数据
	     */
	    private void initData() {
	        //1, 读取联系人    因为可能联系人比较多,可能会读取很久,不想阻塞主线程,所以放到子线程中读取联系人
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                //2, 获取ContentResolver对象   通过该对象可以查询系统联系人数据库
	                ContentResolver contentResolver = getContentResolver();
	
	                String sortOrder = "sort_key COLLATE LOCALIZED ASC";
	                /*
	                 * Cursor query (Uri uri,
	                 String[] projection,
	                 String selection,
	                 String[] selectionArgs,
	                 String sortOrder)
	                 */
	                //3, 查询联系人数据
	                Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone
	                                .CONTENT_URI
	                        , null, null, null, sortOrder);
	
	                //4, 如果有数据   就循环查询里面的Cursor数据
	                if (cursor != null && cursor.moveToFirst()) {
	                    contactList.clear();
	                    LogUtil.d(TAG, "有联系人数据");
	                    do {
	                        //联系人姓名
	                        String contactName = cursor.getString(cursor.getColumnIndex
	                                (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	                        //获取联系人号码
	                        String contactPhone = cursor.getString(cursor.getColumnIndex(
	                                ContactsContract.CommonDataKinds.Phone.NUMBER));
	                        LogUtil.d(TAG, contactName + "----> " + contactPhone);
	
	                        //5, 判断联系人数据是否为空  如果姓名或者电话有任何一项是空的,那么就不保存
	                        if (!TextUtils.isEmpty(contactName) && !TextUtils.isEmpty(contactPhone)) {
	                            HashMap<String, String> contact = new HashMap<>();
	                            contactPhone = contactPhone.replace("-", "");
	                            contact.put(CONTACT_NAME_KEY, contactName);
	                            contact.put(CONTACT_PHONE_KEY, contactPhone);
	                            contactList.add(contact);
	                        }
	
	                    } while (cursor.moveToNext());
	
	                    //6, 用完记得关闭
	                    cursor.close();
	
	                    //7, 联系人数据准备完毕,发送给主线程,更新UI
	                    Message msg = Message.obtain();
	                    msg.what = DATA_IS_READY;
	                    mHandler.sendMessage(msg);
	
	                }
	            }
	        }).start();
	    }
	
	    /**
	     * 初始化UI
	     */
	    private void initUI() {
	        lvContact = (ListView) findViewById(R.id.lv_contact);
	    }
	
	    @Override
	    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                           @NonNull int[] grantResults) {
	        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	        switch (requestCode) {
	            case ConstantValue.MY_PERMISSIONS_READ_CONTACTS:
	                if (grantResults.length > 0 && grantResults[0] == PackageManager
	                        .PERMISSION_GRANTED) {
	                    //申请权限成功
	                    LogUtil.d(TAG, "申请联系人权限成功");
	                    initData();
	                } else {
	                    //申请权限失败
	                    ToastUtil.showWarning("亲~未授权读取联系人权限就无法读取联系人哦");
	                    backToSetup3Activity("");
	                }
	                break;
	            default:
	                break;
	        }
	    }
	
	    /**
	     * 返回Setup3Activity
	     */
	    private void backToSetup3Activity(String number) {
	        Intent intent = new Intent();
	        intent.putExtra(ConstantValue.EXTRA_FOR_CONTACT_NUMBER, number);
	        setResult(REQUEST_CODE, intent);
	        finish();   //关闭当前Activity
	    }
	
	    /**
	     * ListView的适配器
	     */
	    class MyContactAdapter extends BaseAdapter {
	        @Override
	        public int getCount() {
	            return contactList != null ? contactList.size() : 0;
	        }
	
	        @Override
	        public Object getItem(int position) {
	            return contactList.get(position);
	        }
	
	        @Override
	        public long getItemId(int position) {
	            return position;
	        }
	
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            //1,  首先获得当前项的数据
	            HashMap<String, String> contactModel = (HashMap<String, String>) getItem(position);
	            View view;
	            ViewHolder viewHolder;
	
	            if (convertView == null) {
	                //2, 如果先前没有加载,则就没有缓存View,则需要加载一下
	                view = View.inflate(MyApplication.getContext(), R.layout.item_contact_layout,
	                        null);
	                viewHolder = new ViewHolder();
	
	                //3, 获得一个选项布局中的控件id
	                viewHolder.tvContactName = (TextView) view.findViewById(R.id.tv_contact_name);
	                viewHolder.tvContactPhone = (TextView) view.findViewById(R.id.tv_contact_phone);
	
	                //4, 将这个内部类(缓存的数据类)保存到view中(进行缓存)
	                view.setTag(viewHolder);
	            } else {
	                //5, 第二次加载,则只需要加载之前的缓存数据
	                view = convertView;
	                viewHolder = (ViewHolder) view.getTag();
	            }
	
	            //6, 设置布局文件中控件的数据
	            viewHolder.tvContactName.setText(contactModel.get(CONTACT_NAME_KEY));
	            viewHolder.tvContactPhone.setText(contactModel.get(CONTACT_PHONE_KEY));
	
	            //7, 将这个view返回回去作为该子项的布局
	            return view;
	        }
	
	        /**
	         * 用来缓存 item条目上的所有的控件对象
	         */
	        class ViewHolder {
	            TextView tvContactName;
	            TextView tvContactPhone;
	        }
	
	    }
	
	}

# 4. 内容观察者
1. 内容观察者不是四大组件,它不需要在清单文件中配置

		// 1.注册内容观察者
		Uri uri = Uri.parse("content://sms/");
		getContentResolver().registerContentObserver(uri, true,
				new MyContentObserver(new Handler()));
2. 定义内容观察者

		// 2.定义一个内容观察者
		class MyContentObserver extends ContentObserver {
	
			public MyContentObserver(Handler handler) {
				super(handler);
			}
	
			// 被监听的数据库,当数据库发送变化的时候调用
			@Override
			public void onChange(boolean selfChange) {
				Log.d("xfhy", "短信数据库内容发送变化");
				super.onChange(selfChange);
			}
	
		}
3. 应用场景:短信监听器

# 应用:读取系统通话记录数据库

当需要读取通话记录的时候,需要用到系统的数据库,现在需要去查看源码,看到

	<provider android:name="CallLogProvider"
	            android:authorities="call_log"
	            android:syncable="false" android:multiprocess="false"
	            android:exported="true"
	            android:readPermission="android.permission.READ_CALL_LOG"
	            android:writePermission="android.permission.WRITE_CALL_LOG">
	        </provider>

可以看到,里面的android:authorities="call_log",所以访问数据库中calls表的Uri地址: `content://call_log/calls`
