# Android系统自带的常用的数据库

## 1. 联系人数据

	data/data/com.android.providers.contacts/databases/contacts2.db

1.当需要读取通话记录的时候,需要用到系统的数据库,现在需要去查看源码Android清单文件(路径:`android-7.0.0_r1\packages\providers\ContactsProvider\AndroidManifest`)中,看到

	<provider android:name="CallLogProvider"
	            android:authorities="call_log"
	            android:syncable="false" android:multiprocess="false"
	            android:exported="true"
	            android:readPermission="android.permission.READ_CALL_LOG"
	            android:writePermission="android.permission.WRITE_CALL_LOG">
	        </provider>

2.可以看到,里面的android:authorities="call_log",所以访问数据库中calls表的Uri地址: `content://call_log/calls`.

- 中间的那个call_log是根据源码中的android:authorities="call_log"来的
- 最后那个calls是表名,需要去源码 
`android-7.0.0_r1\packages\providers\ContactsProvider\src\com\android\providers\contacts\CallLogProvider.java中`查看

		static {
		        sURIMatcher.addURI(CallLog.AUTHORITY, "calls", CALLS);
		        ...
		    }
3.从上面的源码中可以看到,写url的时候只需要在authorities后面加上calls即可访问通话记录这张表.访问其他的数据库也是同样的道理.

## 2.短信数据

	data/data/com.android.providers.telephony/databases/mmssms.db/sms

获取其相应的四个字段(address 电话号码	date 时间	type:接收,发送	body:短信内容)
