# 自定义AlertDialog

先上效果图

![图片](https://i.loli.net/2017/08/29/59a52e366b257.jpg)

> 这是我自定义的背景透明的,可全局使用的,有动画的AlertDialog

使用方法:

- 定义:loadingProgressDialog = new LoadingProgressDialog(this, "正在加载中...", R.drawable.animation_loading);
- 显示:loadingProgressDialog.show();
- 隐藏:loadingProgressDialog.dismiss();

源码如下:

``` java


	public class LoadingProgressDialog extends AlertDialog {
	    /**
	     * 动画的资源文件
	     */
	    private int mResId;
	    /**
	     * 对话框的标题
	     */
	    private String mLoadingTitle;
	    private Context mContext;
	    /**
	     * 显示图片
	     */
	    private ImageView mImage;
	    /**
	     * 显示标题
	     */
	    private TextView mContent;
	    /**
	     * 动画
	     */
	    private AnimationDrawable mAnimation;
	
	    public LoadingProgressDialog(Context context) {
	        super(context);
	    }
	
	    public LoadingProgressDialog(Context context, int theme) {
	        super(context, theme);
	    }
	
	    public LoadingProgressDialog(@NonNull Context context, boolean cancelable, @Nullable
	            OnCancelListener cancelListener) {
	        super(context, cancelable, cancelListener);
	    }
	
	    /**
	     * 构造方法
	     *
	     * @param context Context
	     * @param content 标题
	     * @param resId   动画资源
	     */
	    public LoadingProgressDialog(Context context, String content, int resId) {
	        super(context,R.style.loadingDialog);
	        this.mContext = context;
	        this.mLoadingTitle = content;
	        this.mResId = resId;
	    }
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	
	        initView();
	        initData();
	    }
	
	    private void initView() {
	        setContentView(R.layout.layout_loading);
	        mImage = (ImageView) findViewById(R.id.iv_loading);
	        mContent = (TextView) findViewById(R.id.tv_loading);
	
	        //点击其他地方不能取消
	        setCanceledOnTouchOutside(false);
	
	        //设置对话框的宽度为屏幕宽度的一般
	        Window window = getWindow();
	        if (window != null) {
	            WindowManager.LayoutParams attributes = window.getAttributes();
	            attributes.width = DevicesUtil.getDeviceWidth(mContext).widthPixels / 2;
	            window.setAttributes(attributes);
	        }
	    }
	
	    private void initData() {
	        //设置图片的资源
	        mImage.setBackgroundResource(mResId);
	        //获取图片背景
	        mAnimation = (AnimationDrawable) mImage.getBackground();
	        mImage.post(new Runnable() {
	            @Override
	            public void run() {
	                mAnimation.start();
	            }
	        });
	        //设置标题
	        mContent.setText(mLoadingTitle);
	    }
	}


```

## 自定义的步骤

1. 设置布局
2. 设置宽高
3. 设置控件上的内容
4. 设置图片的动画

**附动画源码:**

``` xml

	<?xml version="1.0" encoding="utf-8"?>
	<animation-list xmlns:android="http://schemas.android.com/apk/res/android">
	
	    <item android:drawable="@drawable/loading1"
	          android:duration="100"/>
	    <item android:drawable="@drawable/loading2"
	          android:duration="100"/>
	    <item android:drawable="@drawable/loading3"
	          android:duration="100"/>
	    <item android:drawable="@drawable/loading4"
	          android:duration="100"/>
	
	</animation-list>
```
