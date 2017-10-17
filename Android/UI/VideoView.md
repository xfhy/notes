# VideoView

## 1.VideoView的Demo,简单使用,播放网络视频和本地视频

	 //1, 设置播放地址     支持本地和网络的    如果是网络的,记得加权限
        //mVideoView.setVideoPath(VIDEO_PATH);   //这是网络播放

        //这是本地播放
        mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw
                .play));

        //设置一个媒体控制器     控制器默认在父布局的地步
        //可以自己实现控制器布局
        // mVideoView.setMediaController(new MediaController(this));

        //设置准备好了才进行播放
        mVideoView.setOnPreparedListener(this);
        //设置视频播放完成的监听
        mVideoView.setOnCompletionListener(this);
        
## 2.自定义View   实现全屏播放视频

	public class MyVideoView extends VideoView {

	    private static final String TAG = "MyVideoView";
	
	    public MyVideoView(Context context) {
	        this(context, null);
	    }
	
	    public MyVideoView(Context context, AttributeSet attrs) {
	        this(context, attrs, 0);
	    }
	
	    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
	    }
	
	    //widthMeasureSpec : 期望的宽度（可以理解为布局文件的宽度）
	    //heightMeasureSpec : 期望的高度（可以理解为布局文件的高度）
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        Log.e(TAG, "onMeasure: widthMeasureSpec--" + widthMeasureSpec + "   heightMeasureSpec:" +
	                heightMeasureSpec);
	        //获取控件的宽度，手动进行测量
	        //获取被父控件约束的宽度或者是高度
	        //参数1：默认控件的宽/高
	        //参数2：父控件约束的宽/高
	        int width = getDefaultSize(0, widthMeasureSpec);
	        int height = getDefaultSize(0, heightMeasureSpec);
	
	        this.setMeasuredDimension(width, height);
	    }
	
	}
	
	
## 3.拖动进度条,播放指定位置的视频

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    if (fromUser) {
	        //如果是用户改变的,那么将视频进度移动到指定位置进行播放
	        mVideoView.seekTo(progress);
	    }
	}

## 4.切换全屏,取消全屏

	/**
	 * 切换全屏,取消全屏
	 *
	 * @param isChecked
	 */
	private void switchFullScreen(boolean isChecked) {
	    if (isChecked) {
	        //切换到全屏模式
	        //添加一个全屏的标记
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        //请求横屏
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	
	        //设置视频播放控件的布局的高度是match_parent
	        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVideoRootView.getLayoutParams();
	        //将默认的高度缓存下来
	        mVideoHeight = layoutParams.height;
	        layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
	        mVideoRootView.setLayoutParams(layoutParams);
	    } else {
	        //切换到默认模式
	        //清除全屏标记
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        //请求纵屏
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	
	        //设置视频播放控件的布局的高度是200
	        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVideoRootView.getLayoutParams();
	        layoutParams.height = mVideoHeight;  //这里的单位是px
	        mVideoRootView.setLayoutParams(layoutParams);
	    }
	}

**横竖屏切换时的生命周期总结：**

1、不设置Activity的android:configChanges时，切屏会重新调用各个生命周期，切横屏时会执行一次，切竖屏时会执行两次

2、设置Activity的android:configChanges="orientation"时，切屏还是会重新调用各个生命周期，切横、竖屏时只会执行一次

3、设置Activity的android:configChanges="orientation|keyboardHidden"时，切屏不会重新调用各个生命周期，只会执行onConfigurationChanged方