# 自定义Toast显示(不限时+在其他应用之上显示)

# 一.首先写好自定义Toast的布局

toast_view.xml

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	              android:layout_width="match_parent"
	              android:layout_height="match_parent"
	              android:orientation="vertical">
	
	    <TextView
	        android:id="@+id/tv_toast_number"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:background="@drawable/call_locate_white"
	        android:drawableStart="@android:drawable/ic_menu_call"
	        android:gravity="center"
	        android:text="来电话啦"/>
	
	</LinearLayout>

# 二.查看源码看看官方是怎么写Toast的

进入Toast源码中,看到

	TN() {
            // XXX This should be changed to use a Dialog, with a Theme.Toast
            // defined that sets up the layout params appropriately.
            final WindowManager.LayoutParams params = mParams;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = com.android.internal.R.style.Animation_Toast;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.setTitle("Toast");
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }

# 三.下面开始使用Toast搞点事情

	/**
	 * Created by xfhy
	 * 打电话时的归属地悬浮窗由这个服务去管理
	 */
	public class AddressService extends Service {
	
	    private TelephonyManager mTm;
	    private static final String TAG = "AddressService";
	    /**
	     * 监听电话的监听器
	     */
	    private MyPhoneStateListener mPhoneStateListener;
	    /**
	     * Toast的规则
	     */
	    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	    /**
	     * WindowManager管理者对象
	     */
	    private WindowManager mWM;
	    /**
	     * Toast上的View
	     */
	    private View mToastView;
	
	    @Override
	    public void onCreate() {
	        //第一次开启服务之后,就需要去管理Toast的显示
	
	        //电话状态的监听(服务开启的时候,需要去做监听,关闭的时候电话状态就不需要监听了)
	        //1, 电话管理者对象
	        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	        //2, 监听电话状态
	        mPhoneStateListener = new MyPhoneStateListener();
	        mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	
	        //3, 获取窗体对象
	        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
	        super.onCreate();
	    }
	
	    @Override
	    public IBinder onBind(Intent intent) {
	        throw new UnsupportedOperationException("Not yet implemented");
	    }
	
	    @Override
	    public void onDestroy() {
	        //取消对电话状态的监听   如果不取消监听的话,则即使停止了Service,还是在监听着的
	        if (mTm != null && mPhoneStateListener != null) {
	            mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	        }
	        super.onDestroy();
	    }
	
	    /**
	     * 显示Toast
	     *
	     * @param incomingNumber
	     */
	    private void showToast(String incomingNumber) {
	        //Toast.makeText(MyApplication.getContext(), incomingNumber, Toast.LENGTH_LONG).show();
	
	        //宽高
	        final WindowManager.LayoutParams params = mParams;
	        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
	        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
	
	        params.format = PixelFormat.TRANSLUCENT;
	        //在响铃的时候显示吐司,和电话类型一致
	        params.type = WindowManager.LayoutParams.TYPE_PHONE;
	        params.setTitle("Toast");
	        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
	//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE   默认是不可以触摸的
	                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
	
	        //指定Toast所在位置
	        params.gravity = Gravity.LEFT + Gravity.TOP;
	
	        //吐司显示效果(吐司布局文件) ,xml->view(吐司),将吐司挂在windowManager窗体上
	        mToastView = View.inflate(this, R.layout.toast_view, null);
	        mWM.addView(mToastView, mParams);
	    }
	
	    /**
	     * 电话状态的监听
	     * 监听器类，用于监视设备上特定电话状态的变化，包括服务状态，信号强度，消息等待指示符（语音信箱）等。
	     * 覆盖您希望接收更新的状态的方法，
	     * 并将您的PhoneStateListener对象与按位或LISTEN_标志一起传递给TelephonyManager.listen（）。
	     * 请注意，对某些电话信息的访问权限受到保护。 您的应用程序将不会收到受保护信息的更新，
	     * 除非它的清单文件中声明了相应的权限。 在适用权限的情况下，它们会在相应的LISTEN_标志中注明。
	     */
	    class MyPhoneStateListener extends PhoneStateListener {
	        @Override
	        public void onCallStateChanged(int state, String incomingNumber) {
	            switch (state) {
	                case TelephonyManager.CALL_STATE_IDLE:
	                    //无任何状态时    空闲状态
	                    LogUtil.d(TAG, "空闲状态");
	                    //空闲状态的时候需要移除Toast显示
	                    if (mWM != null && mToastView != null) {
	                        //最开始的时候是空闲状态的,那个时候mToastView是null的,需要判断非空
	                        //挂断电话的时候也是空闲状态,也需要移除Toast
	                        mWM.removeView(mToastView);
	                    }
	                    break;
	                case TelephonyManager.CALL_STATE_OFFHOOK:
	                    //接起电话时   摘机
	                    LogUtil.d(TAG, "摘机状态");
	                    break;
	                case TelephonyManager.CALL_STATE_RINGING:
	                    //电话进来时   响铃
	                    LogUtil.d(TAG, "响铃状态");
	                    //showToast(incomingNumber);
	                    requestPermission(incomingNumber);
	                    break;
	            }
	        }
	    }
	
	    /**
	     * 请求显示在其他应用之上的权限
	     * <p>
	     * 当运行在23之上的时候,不仅需要在清单文件中写入
	     * <uses-permission android:name="android.permission
	     * .SYSTEM_ALERT_WINDOW"/>
	     * 但是仅仅这样还不行,还会报下面的错
	     * Unable to add window android.view.ViewRootImpl$W@18a6ff4 --
	     * permission denied for window type 2002
	     * 现在需要像下面一样判断一下,如果没有权限,则需要用户跳转到相应的界面去给权限才行
	     *
	     * @param incomingNumber 需要显示的内容
	     */
	    public void requestPermission(String incomingNumber) {
	        if (Build.VERSION.SDK_INT >= 23) {
	            if (!Settings.canDrawOverlays(this)) {
	                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
	                startActivity(intent);
	                return;
	            } else {
	                //Android6.0以上
	                showToast(incomingNumber);
	            }
	        } else {
	            //Android6.0以下，不用动态声明权限
	            showToast(incomingNumber);
	        }
	    }
	
	}

模仿Toast里面的写法,写出上面的showToast()方法,用于显示自定义的Toast.

# 四.自定义Toast支持拖动

	//触摸监听
		mRocketView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: // 按下
					// 1, 获取控件开始时的xy
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE: // 移动
					// 2, 获取控件移动过程中的xy值
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					// 3, 当前位置和初始位置的X距离和Y距离
					int disX = moveX - startX;
					int disY = moveY - startY;

					// 4, 控件的位置需要移动到移动之后的那个位置
					mParams.x = mParams.x + disX;
					mParams.y = mParams.y + disY;

					// 5, 容错,不允许Toast拖拽出屏幕区域
					if (mParams.x < 0) { // 左边区域 最小就为0
						mParams.x = 0;
					}

					if (mParams.y < 0) {
						mParams.y = 0;
					}

					if (mParams.x > mScreenWidth - mRocketView.getWidth()) {
						// 右边,最多能拖到屏幕宽度-控件宽度的位置
						mParams.x = mScreenWidth - mRocketView.getWidth();
					}

					if (mParams.y > mScreenHeight - 22 - mRocketView.getHeight()) {
						// 下边,最多能拖到屏幕高度-状态栏高度-控件高度的位置
						mParams.y = mScreenHeight - 22 - mRocketView.getHeight();
					}

					// 6, 更新控件 刷新显示
					mWM.updateViewLayout(mRocketView, mParams);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
				case MotionEvent.ACTION_UP: // 抬起
					break;
				default:
					break;
				}

				// 8, 消费了事件,则返回true
				return true;
			}
		});

# 五.遇到的坑
当运行在23之上的时候,不仅需要在清单文件中写入
`<uses-permission android:name="android.permission
.SYSTEM_ALERT_WINDOW"/>`但是仅仅这样还不行,还会报下面的错
`Unable to add window android.view.ViewRootImpl$W@18a6ff4 --
permission denied for window type 2002`
现在需要像下面一样判断一下,如果没有权限,则需要用户跳转到相应的界面去给权限才行

# 六.总结

上面的Service是用于监听当电话打入时,显示一个自定义的Toast,当电话挂断时,自定义Toast就取消显示.相当于是Toast可以显示很久很久(只要是电话没挂),而且还会显示在Toast之上.
