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
>QQ ,微信,默默等

1. data表    data1列表里存的是所有联系人的所有信息(包含姓名,地址,邮箱等)    
    raw_contact_id 列是用来区分一共有几条联系人信息
    mimetype_id 列是用来区分数据类型(姓名,地址,或者邮箱)
2. raw_contacts 表中contact_id就是data表的 raw_contact_id

查询联系人步骤:

1. 先查询raw_contacts表的contact_id列,就知道有几条联系人
2. 我根据contact_id去查询data表,查询data1列和mimetype列
3. view_data是由data表和mimetype表的组合

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
