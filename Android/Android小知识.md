# Android小知识 #
<font size="5"><b>
1. 安卓全屏代码<br/>

		//安卓全屏代码
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
<br/>
2. 在Activity代码中设置背景图片.代码如下:<br/>

	Drawable drawable = ContextCompat.getDrawable(RandomSelectionActivity.this,
                            R.drawable.hubian);
    activity_random_selection.setBackground(drawable);
3. Android中在代码中布局的一些基础:

	    //主布局
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        mainLayout.setOrientation(LinearLayout.VERTICAL);   //设置方向是垂直

        //创建RelativeLayout对象
        RelativeLayout topRelativeLayout = new RelativeLayout(this);
        RelativeLayout bottomRelativeLayout = new RelativeLayout(this);

        // 建立布局样式宽和高，对应xml布局中：
        // android:layout_width="match_parent"
        // android:layout_height="0"
        //layout_weight=1
        topRelativeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,0,1
                ));
        bottomRelativeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,0,1
        ));

        //在代码中获取color
        //getColor(int id)在API版本23时(Android 6.0)已然过时    这里没办法,我的是android4.4
        topRelativeLayout.setBackgroundColor(getResources().getColor(R.color.colorBlueXfhy));
        bottomRelativeLayout.setBackgroundColor(getResources().getColor(R.color.colorEye));

        TextView player1TextView = new TextView(this);
        player1TextView.setId(generateViewId());   //直接使用 generateViewId() 获得ID，且可以得到不重复的ID
        player1TextView.setText("哈哈");
        player1TextView.setTextSize((float)30);   //文字大小
        player1TextView.setRotation((float)180);  //旋转角度
        RelativeLayout.LayoutParams player1TextViewLayoutParams = new RelativeLayout.LayoutParams(
          RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        player1TextViewLayoutParams.addRule(ALIGN_PARENT_BOTTOM);    //父布局底部
        player1TextViewLayoutParams.bottomMargin = 40;       //距离底部40dp
        player1TextViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);  //水平居中

        // 在父类布局中添加它，及布局样式
        topRelativeLayout.addView(player1TextView,player1TextViewLayoutParams);
        mainLayout.addView(topRelativeLayout);
        mainLayout.addView(bottomRelativeLayout);
		return mainLayout;
这里的重写AppCompatActivity是为了更好为我们编写代码布局服务,需要的时候直接集继承自BaseActivity,然后就可以动态的加载布局了:

		    public abstract class BaseActivity extends AppCompatActivity {
		    public Handler handler;
		
		    /** 初始化数据 */
		    protected abstract void initData();
		
		    /** 初始化资源 */
		    protected abstract void initRecourse();
		
		    /** 初始化界面 */
		    protected abstract View initView();
		
		    /** 处理handler回传的信息 */
		    public void dispatchMessage(Message msg) {
		    }
		
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		
		        requestWindowFeature(Window.FEATURE_NO_TITLE);
		        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
		
		        initData();
		
		        initRecourse();
		
		        setContentView(initView());
		
		        handler = new Handler() {
		            public void dispatchMessage(Message msg) {
		                BaseActivity.this.dispatchMessage(msg);
		            }
		        };
    		}
	    }

<br/>
</b></font>