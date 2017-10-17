# Android 运行时权限
> Android开发团队在Android 6.0系统中引用了运行时权限这个功能,从而更好的保护了用户的安全和隐私.

> 用户不需要在安装软件的时候一次性授权所有申请的权限,而是可以在软件的使用过程中再对某一项权限申请进行授权.

> Android现在将所有的权限归成了3类,一个是普通权限,一类是危险权限.第3类是特殊权限,不过这种权限使用的很少.

[TOC]

## 1. Android的危险权限图
> 每当要使用一个权限时,可以先到这张表中查一下,如果是属于这张表的权限,那么就需要进行运行时权限处理,如果不在这张表中,那么只需要在AndroidManifest.xml文件中添加一下权限声明就可以了.
> 表格中每个危险权限都属于一个权限组,我们在进行运行时权限处理时使用的是权限名,但是用户一旦统一授权了,那么该权限所对应的权限组中所有的其他权限也会同时被授权.

	CALENDAR:READ_CALENDAR,WRITE_CALENDAR

	CAMERA:CAMERA

	CONTACTS:READ_CONTACTS,WRITE_CONTACTS,GET_ACCOUNTS

	LOCATION:ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION

	MICROPHONE:RECORD_AUDIO

	PHONE:READ_PHONE_STATE,CALL_PHONE,READ_CALL_LOG,WRITE_CALL_LOG,ADD_VOICEMAIL,USE_SIP,PROCESS_OUTGOING_CALLS

	SENSORS:BODY_SENSORS

	SMS:SEND_SMS,RECEIVE_SMS,READ_SMS,RECEIVE_WAP_PUSH,RECEIVE_MMS

	STORAGE	READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE

![Android官网的危险权限图](http://olg7c0d2n.bkt.clouddn.com/17-2-25/27531759-file_1488010441042_153ea.png)

## 2. 申请危险权限示例

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_call = (Button) findViewById(R.id.bt_call);
        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 检查用户是否已经给我们授权了权限    相等则已经授权,不等则没授权
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //2. 申请权限
                    //参数:Context上下文,权限数组,申请码(申请码只要唯一就行)
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    call();
                }
            }
        });
    }

    /**
     * 打电话
     */
    private void call() {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:10086"));
            startActivity(intent);
        } catch (SecurityException e){   //这里要写SecurityException不然上面startActivity()会报红
            e.printStackTrace();
        }
    }

    //3. 判断是否申请成功 申请不管成功与否都会调用此方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                //用户授权的结果会封装在grantResults中
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //申请成功
                    call();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

# 3. 权限申请开源库
>虽然权限处理并不复杂，但是需要编写很多重复的代码，所以目前也有很多库对用法进行了封装.可以在github首页搜索:android permission.
>https://github.com/lovedise/PermissionGen这个库使用很简单.

1. app->build.gradle下加入`compile 'com.lovedise:permissiongen:0.0.6'`
2. 需要申请权限时这样写:

		PermissionGen.with(MainActivity.this)
	    .addRequestCode(100)
	    .permissions(
	        Manifest.permission.READ_CONTACTS,
	        Manifest.permission.RECEIVE_SMS,
	        Manifest.permission.WRITE_CONTACTS)
	    .request();

**or**

	PermissionGen.needPermission(ContactFragment.this, 100, 
	    new String[] {
	        Manifest.permission.READ_CONTACTS, 
	        Manifest.permission.RECEIVE_SMS,
	        Manifest.permission.WRITE_CONTACTS
	    }
	);

3. 覆盖onRequestPermissionsResult()方法在Activity或者Fragment,然后输入如下代码

		@Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
	      int[] grantResults) {
	    PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}

4. 当权限申请成功时调用这个方法,该方法需要实现

		@PermissionSuccess(requestCode = 100)
		public void doSomething(){
		    Toast.makeText(this, "Contact permission is granted", Toast.LENGTH_SHORT).show();
		}

5. 当权限申请失败时调用这个方法,该方法需要实现

		@PermissionFail(requestCode = 100)
		public void doFailSomething(){
		    Toast.makeText(this, "Contact permission is not granted", t.LENGTH_SHORT).show();
		}
