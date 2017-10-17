# Android自定义View——仿华为手机管家的病毒扫描动画效果

直接上图:

![](http://olg7c0d2n.bkt.clouddn.com/17-5-25/97919453.jpg)


> 最近要做杀毒界面的设计,所以想把这个自定义View自己实现一下.

## 分析

1.那个扫描状态的自定义View,其实就是2个圆,2根线,一个扇形(只不过扇形在动而已),一个进度的文字,再加一个`%`符号.

2.根据自定义View的宽和高,可以把2个圆按照比例画出来,那中间的十字交叉线也是一样的.

3.那个扇形有点难实现,首先需要一个矩形来包裹这个扇形,其次每隔一定的事件,扇形需要旋转(这个可以用重新绘制,然后让扇形的角度+1).矩形的4边必须是相等的,其次中心必须在圆形的中心,这样一来,矩形的4条边就很好决定了,矩形左侧的X坐标:mWidth * 0.1,矩形顶部的Y坐标:mWidth * 0.1,矩形右侧的X坐标:mWidth * 0.9,矩形底部的Y坐标:mWidth * 0.9 .

4.有了扇形,其次就是让进度文字居中显示的难题.首先,必须要拿到文字的宽度和高度,然后根据圆心来决定文字的起始x坐标和起始y坐标.

	//拿到字符串的宽度
	mTextWidth = mTextPaint.measureText(str);
	//文字的x轴坐标
	mTextX = (getWidth() - mTextWidth) / 2;
	//文字的y轴坐标
	//描述给定文本大小的字体的各种指标的类。
	// 记住，Y值增加下降，所以这些值将是正的，测量距离上升的值将为负。 这个类由getFontMetrics（）返回。
	Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
	mTextY = getHeight() / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
	//画字
     canvas.drawText(text, mTextX, mTextY, mTextPaint);

5.最后就是进度文字旁边的那个`%`符号了,这个只要进度文字放置好了,只需要把这个放在进度文字的右边就可以了.

# 源码

## 自定义View `VirusKilling.java`

		public class VirusKilling extends View {
	
		    private static final String TAG = "VirusKilling";
		
		    //2个圆(外圆和内圆)  2根线(十字交叉)   一行文字(中间的显示扫描进度的文字)    2个扇形(扫描 渐变)
		    /**
		     * 控件宽度
		     */
		    private float mWidth;
		    /**
		     * 控件高度
		     */
		    private float mHeight;
		    /**
		     * 2圆的画笔
		     */
		    private Paint mCirclePaint;
		    /**
		     * 2根线的画笔
		     */
		    private Paint mLinePaint;
		    /**
		     * 中间进度文字的画笔
		     */
		    private Paint mTextPaint;
		    /**
		     * 中间的进度旁边的 % 符号
		     */
		    private Paint mPercentSignPaint;
		    /**
		     * 扇形的画笔
		     */
		    private Paint mArcPaint;
		    /**
		     * 扇形的画笔
		     */
		    private Paint mArcPaint2;
		    /**
		     * 扇形外面的矩形
		     */
		    private RectF mRectF;
		    /**
		     * 内圆半径
		     */
		    private float radiusInside;
		    /**
		     * 外圆半径
		     */
		    private float radiusExt;
		    /**
		     * 扇形旋转的开始的角度
		     */
		    private float startAngle = 360;
		    /**
		     * 是否在旋转
		     */
		    private boolean running = true;
		    /**
		     * 开一个子线程,去旋转  扇形
		     */
		    private Thread mThread;
		    /**
		     * 更新UI了
		     */
		    private static final int UPDATE_UI = 1000;
		    /**
		     * 进度
		     */
		    private int schedule = 0;
		    private Handler mHandler = new Handler() {
		        @Override
		        public void handleMessage(Message msg) {
		            switch (msg.what) {
		                case UPDATE_UI:
		                    synchronized (this) {
		                        if (startAngle < 1) {
		                            startAngle = 360;
		                        } else {
		                            startAngle--;
		                            invalidate();
		                        }
		                    }
		                    break;
		            }
		        }
		    };
		    /**
		     * 中间的进度文字的宽度
		     */
		    private float mTextWidth;
		    /**
		     * 中间的进度的文字的X
		     */
		    private float mTextX;
		    /**
		     * 中间的进度的文字的Y
		     */
		    private float mTextY;
		    /**
		     * 默认扫描速度  20毫秒前进1次  1次1°C
		     */
		    private int mScanSpeed = 20;
		
		
		    public VirusKilling(Context context) {
		        super(context);
		        initData();
		    }
		
		
		    public VirusKilling(Context context, @Nullable AttributeSet attrs) {
		        super(context, attrs);
		        initData();
		    }
		
		    public VirusKilling(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		        super(context, attrs, defStyleAttr);
		        initData();
		    }
		
		    /**
		     * 初始化数据
		     */
		    private void initData() {
		        //圆的画笔
		        mCirclePaint = new Paint();
		        mCirclePaint.setStrokeWidth(4);  //设置画线的宽度
		        mCirclePaint.setAntiAlias(true); //
		        mCirclePaint.setStyle(Paint.Style.STROKE);
		        mCirclePaint.setColor(Color.parseColor("#E3F0FC"));
		
		        //2根线的画笔
		        mLinePaint = new Paint();
		        mLinePaint.setStrokeWidth(4);  //设置画线的宽度
		        mLinePaint.setAntiAlias(true); //
		        mLinePaint.setStyle(Paint.Style.STROKE);
		        mLinePaint.setColor(Color.parseColor("#E3F0FC"));
		
		        //中间的进度文字
		        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        mTextPaint.setTextSize(90f);
		        mTextPaint.setColor(Color.parseColor("#000000"));
		
		        //进度旁边的 %  符号
		        mPercentSignPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        mPercentSignPaint.setTextSize(30f);
		        mPercentSignPaint.setColor(Color.parseColor("#000000"));
		
		        //扇形的画笔
		        mArcPaint = new Paint();
		        mArcPaint.setStrokeWidth(1);  //设置画线的宽度
		        mArcPaint.setColor(Color.parseColor("#2E93FE"));
		
		        //引导  扇形 的画笔
		        mArcPaint2 = new Paint();
		        mArcPaint2.setStrokeWidth(1);  //设置画线的宽度
		        mArcPaint2.setColor(Color.parseColor("#3B9BFE"));
		    }
		
		    //测量自己的宽高
		    @Override
		    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		        //因为是圆形,所以应该让宽高保持一致
		        int width = measureWidth(widthMeasureSpec);
		        int height = measureHeight(heightMeasureSpec);
		        int suitSize = Math.min(width,height);  //必须是正方形,不然扇形中心不在圆形中心
		
		        //该方法必须由onMeasure（int，int）调用来存储测量的宽度和测量高度。
		        // 如果没有这样做，将在测量时间触发异常。
		        setMeasuredDimension(suitSize, suitSize);
		    }
		
		    /**
		     * 测量View的长度
		     */
		    private int measureHeight(int measureSpec) {
		        int result = 0;
		
		        //获取具体的测量模式和大小
		        int specMode = MeasureSpec.getMode(measureSpec);
		        int specSize = MeasureSpec.getSize(measureSpec);
		
		        if (specMode == MeasureSpec.EXACTLY) { //默认的大小(指定了具体的值  比如android:layout_width=100dp)
		            result = measureSpec;
		        } else {
		            result = 400;
		            if (specMode == MeasureSpec.AT_MOST) { //wrap_content
		                result = Math.min(result, specSize);
		            }
		        }
		
		        return result;
		    }
		
		    /**
		     * 测量View的宽度
		     */
		    private int measureWidth(int measureSpec) {
		        int result = 0;
		        int specMode = MeasureSpec.getMode(measureSpec);   //获取具体的测量模式(EXACTLY,AT_MOST,UNSPECIFIED)
		        int specSize = MeasureSpec.getSize(measureSpec); //获取具体的测量大小
		
		        if (specMode == MeasureSpec.EXACTLY) {   //默认模式
		            result = measureSpec;
		        } else {
		            result = 400;   //给一个默认的大小
		            if (specMode == MeasureSpec.AT_MOST) {   //如果是wrap_content
		                result = Math.min(result, specSize);   //取最小的  适合控件大小的
		            }
		        }
		
		        return result;
		    }
		
		    /*
		    * 这个是系统回调方法，是系统调用的，它的方法名已经告诉我们了，这个方法会在这个view的大小发生改变是被系统调用，
		    * 我们要记住的就是view
		    * 大小变化，这个方法就被执行就可以了。最主要的是，它还在onDraw方法之前调用。
		    * */
		    @Override
		    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		        super.onSizeChanged(w, h, oldw, oldh);
		        mWidth = getWidth();
		        mHeight = getHeight();
		        //扇形外围的矩形
		        /*
		        首先需要一个矩形来包裹这个扇形,其次每隔一定的事件,扇形需要旋转(这个可以用重新绘制,然后让扇形的角度+1).
		        矩形的4边必须是相等的,其次中心必须在圆形的中心,这样一来,
		        矩形的4条边就很好决定了,矩形左侧的X坐标:mWidth * 0.1,矩形顶部的Y坐标:mWidth * 0.1,
		        矩形右侧的X坐标:mWidth * 0.9,
		        矩形底部的Y坐标:mWidth * 0.9
		        * 参数:left float：矩形左侧的X坐标 top float：矩形顶部的Y坐标
		        * right float：矩形右侧的X坐标 bottom float：矩形底部的Y坐标
		        * */
		        mRectF = new RectF((float) (mWidth * 0.1), (float) (mWidth * 0.1),
		                (float) (mWidth * 0.9), (float) (mWidth * 0.9));
		        // 绘制渐变效果
		        LinearGradient gradient = new LinearGradient((float) (mWidth * 0.3),
		                (float) (mWidth * 0.9), (float) (mWidth * 0.1),
		                (float) (mWidth * 0.5),
		                new int[]{Color.parseColor("#B1D6FD"), Color.TRANSPARENT},
		                null, Shader.TileMode.CLAMP);
		        mArcPaint.setShader(gradient);
		
		        // 2个圆的半径
		        radiusExt = (float) (mWidth * 0.4);
		        radiusInside = (float) (mWidth * 0.25);
		    }
		
		    @Override
		    protected void onDraw(Canvas canvas) {
		        super.onDraw(canvas);
		        canvasArc(canvas);    //话扇形
		        canvasArc2(canvas);   //画扇形  引导
		        canvasLines(canvas);  //画线
		        canvasCircle(canvas); //画圆
		        canvasSche(canvas);  //画进度
		        canvasPercent(canvas); //画 %
		
		    }
		
		    /**
		     * 画 % 符号
		     *
		     * @param canvas
		     */
		    private void canvasPercent(Canvas canvas) {
		        //符号的X
		        float perSignX = mTextX + mTextWidth + 5;
		
		        //符号的Y
		        float perSignY = getHeight() / 2 + 20;
		
		        //画字
		        canvas.drawText("%", perSignX, perSignY, mPercentSignPaint);
		    }
		
		    /**
		     * 画文字  进度
		     *
		     * @param canvas
		     */
		    private void canvasSche(Canvas canvas) {
		        String text = String.valueOf(schedule);
		        //拿到字符串的宽度
		        mTextWidth = mTextPaint.measureText(text);
		        //文字的x轴坐标
		        mTextX = (getWidth() - mTextWidth) / 2;
		        //文字的y轴坐标
		        //描述给定文本大小的字体的各种指标的类。
		        // 记住，Y值增加下降，所以这些值将是正的，测量距离上升的值将为负。 这个类由getFontMetrics（）返回。
		        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		        mTextY = getHeight() / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
		        //画字
		        canvas.drawText(text, mTextX, mTextY, mTextPaint);
		    }
		
		    /**
		     * 绘制2根线
		     *
		     * @param canvas
		     */
		    private void canvasLines(Canvas canvas) {
		        //横线
		        canvas.drawLine((float) (mWidth * 0.1), (float) (mHeight * 0.5),
		                (float) (mWidth * 0.9), (float) (mHeight * 0.5), mLinePaint);
		        //竖线
		        canvas.drawLine((float) (mWidth * 0.5), (float) (mHeight * 0.1),
		                (float) (mWidth * 0.5), (float) (mHeight * 0.9), mLinePaint);
		    }
		
		    /**
		     * 绘制旋转的扇形
		     *
		     * @param canvas
		     */
		    private void canvasArc(Canvas canvas) {
		        canvas.drawArc(mRectF, startAngle, 100, true, mArcPaint);
		    }
		
		    /**
		     * 绘制旋转的扇形
		     *
		     * @param canvas
		     */
		    private void canvasArc2(Canvas canvas) {
		        //第二个的扇形的角度是1，为什么是1呢，原来我这里只是想要它旋转角度的效果，并不需要它有多宽，
		        // 所以在具体实现自定义View的时候，也要学会活学活用。
		        canvas.drawArc(mRectF, startAngle, 2, true, mArcPaint2);
		    }
		
		    /**
		     * 绘制四个圆
		     *
		     * @param canvas
		     */
		    private void canvasCircle(Canvas canvas) {
		        canvas.drawCircle(mWidth / 2, mHeight / 2, radiusInside, mCirclePaint);
		        canvas.drawCircle(mWidth / 2, mHeight / 2, radiusExt, mCirclePaint);
		    }
		
		    /**
		     * 开始扫描 动画
		     */
		    public void startScanning() {
		        //开一个子线程
		        mThread = new Thread(new Runnable() {
		            @Override
		            public void run() {
		                while (true) {
		                    try {
		                        Thread.sleep(mScanSpeed);
		                    } catch (InterruptedException e) {
		                        e.printStackTrace();
		                    }
		
		                    //如果正在执行动画,则发送消息,叫UI线程更新UI
		                    if (running) {
		                        mHandler.sendEmptyMessage(UPDATE_UI);
		                    } else {
		                        break;
		                    }
		                }
		            }
		        });
		        mThread.start();
		
		    }
		
		    /**
		     * 重新开启动画
		     */
		    public void restartScan() {
		        running = true;
		    }
		
		    /**
		     * 暂停动画
		     */
		    public void stopScan() {
		        running = false;
		    }
		
		    /**
		     * 是否在转动
		     *
		     * @return
		     */
		    public boolean isRunning() {
		        return running;
		    }
		
		    /**
		     * 获取当前进度
		     *
		     * @return 当前进度
		     */
		    public int getSchedule() {
		        return schedule;
		    }
		
		    /**
		     * 设置进度
		     *
		     * @param schedule 进度
		     */
		    public void setSchedule(int schedule) {
		        if (schedule >= 0 && schedule <= 100) {
		            this.schedule = schedule;
		            this.postInvalidate();  //非UI线程更新UI
		        }
		    }
		
		    /**
		     * 设置扫描速度   1000~20   数字越小扫描速度越快
		     *
		     * @param mScanSpeed 速度  1000~20
		     */
		    public void setmScanSpeed(int mScanSpeed) {
		        //检查参数
		        if (mScanSpeed < 20 || mScanSpeed > 1000) {
		            mScanSpeed = 50;  //设置为速度默认值
		        } else {
		            this.mScanSpeed = mScanSpeed;
		        }
		    }
		
		    /**
		     * 获取当前扫描速度
		     *
		     * @return
		     */
		    public int getmScanSpeed() {
		        return mScanSpeed;
		    }
		}


## 然后在布局中的使用  `activity_antivirusi.xml`

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:orientation="vertical"
	    tools:context="com.xfhy.mobilesafe.activity.AntivirusiActivity">
	
	    <!--病毒查杀界面-->
	
	    <TextView
	        style="@style/TitleTheme"
	        android:text="病毒查杀"/>
	
	    <LinearLayout
	        android:layout_width="match_parent"
	        android:layout_height="0dp"
	        android:layout_weight="2"
	        android:orientation="vertical">
	
	        <!--自定义View 扫描病毒的View-->
	        <com.xfhy.mobilesafe.view.VirusKilling
	            android:id="@+id/vk_scan_virus"
	            android:layout_width="200dp"
	            android:layout_height="200dp"
	            android:layout_gravity="center_horizontal"
	            />
	
	        <TextView
	            android:id="@+id/tv_scan_app_package"
	            android:layout_width="wrap_content"
	            android:layout_height="wrap_content"
	            android:layout_gravity="center_horizontal"
	            android:text="正在扫描:com.xfhy.hehe"
	            android:textColor="@color/bright_foreground_light_disabled"
	            android:textSize="18sp"/>
	    </LinearLayout>
	
	    <LinearLayout
	        android:layout_width="match_parent"
	        android:layout_height="0dp"
	        android:layout_weight="2"
	        android:orientation="vertical"
	        android:paddingBottom="10dp"
	        android:paddingEnd="20dp"
	        android:paddingStart="20dp"
	        android:paddingTop="10dp">
	
	        <RelativeLayout
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content"
	            android:layout_weight="1">
	
	            <TextView
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:layout_alignParentStart="true"
	                android:gravity="center_vertical"
	                android:text="病毒"
	                android:textColor="@color/colorBlack"
	                android:textSize="22sp"
	                />
	
	            <TextView
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:layout_alignParentEnd="true"
	                android:gravity="center_vertical"
	                android:text="正在扫描..."
	                android:textColor="@color/bright_foreground_light_disabled"
	                android:textSize="18sp"
	                />
	
	        </RelativeLayout>
	
	        <RelativeLayout
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content"
	            android:layout_weight="1">
	
	            <TextView
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:layout_alignParentStart="true"
	                android:gravity="center_vertical"
	                android:text="风险软件"
	                android:textColor="@color/colorBlack"
	                android:textSize="22sp"
	                />
	
	            <TextView
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:layout_alignParentEnd="true"
	                android:gravity="center_vertical"
	                android:text="正在扫描..."
	                android:textColor="@color/bright_foreground_light_disabled"
	                android:textSize="18sp"
	                />
	        </RelativeLayout>
	
	        <RelativeLayout
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content"
	            android:layout_weight="1">
	
	            <TextView
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:layout_alignParentStart="true"
	                android:gravity="center_vertical"
	                android:text="非官方证书"
	                android:textColor="@color/colorBlack"
	                android:textSize="22sp"
	                />
	
	            <TextView
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:layout_alignParentEnd="true"
	                android:gravity="center_vertical"
	                android:text="正在扫描..."
	                android:textColor="@color/bright_foreground_light_disabled"
	                android:textSize="18sp"
	                />
	
	        </RelativeLayout>
	
	        <RelativeLayout
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content"
	            android:layout_weight="1">
	
	            <TextView
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:layout_alignParentStart="true"
	                android:gravity="center_vertical"
	                android:text="云查杀"
	                android:textColor="@color/colorBlack"
	                android:textSize="22sp"
	                />
	
	            <TextView
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:layout_alignParentEnd="true"
	                android:gravity="center_vertical"
	                android:text="正在扫描..."
	                android:textColor="@color/bright_foreground_light_disabled"
	                android:textSize="18sp"
	                />
	        </RelativeLayout>
	
	    </LinearLayout>
	
	    <Button
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:layout_margin="20dp"
	        android:background="@drawable/selector_button_scan_bg"
	        android:text="停止扫描"
	        android:textColor="#017EFF"
	        android:textSize="20sp"/>
	
	</LinearLayout>

## 最后在Activity中的使用 `AntivirusiActivity.java`

	public class AntivirusiActivity extends BaseActivity {

	    @BindView(R.id.vk_scan_virus)
	    VirusKilling vkScanVirus;
	    private int sce;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_antivirusi);
	        ButterKnife.bind(this);
	
	        vkScanVirus.startScanning();
	        sce = 0;
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                while (true) {
	                    if (sce > 100) {
	                        sce = 0;
	                    }
	                    try {
	                        Thread.sleep(200);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                    vkScanVirus.setSchedule(sce++);
	                }
	
	            }
	        }).start();
	    }
	}


由于源码中注释非常详细,在中间就不再过多的叙述啦.
好东西要大家一起分享.

本文参考

`http://www.jianshu.com/p/1728b725b4a6`
`http://blog.csdn.net/qq_25193681/article/details/51891117`
