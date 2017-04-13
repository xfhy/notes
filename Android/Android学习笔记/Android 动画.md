# Android 动画

# 1.  Android 属性动画（Property Animation）

## 1.1 ObjectAnimator实现动画

> 缩放、反转等都有中心点或者轴，默认中心缩放，和中间对称线为反转线

	/**
	 * 多个效果放在一起    然后用ObjectAnimator执行动画
	 * @param view
	 */
	public void propertyValuesHolder(View view)  
    {  
		//该类保存有关属性的信息以及该动画中该属性应该占用的值。 
		//PropertyValuesHolder对象可以用于使用ValueAnimator或ObjectAnimator来创建动画，并在多个不同的属性上进行操作。
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,  
                0f, 1f);    //透明度      从1->0->1
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,     //缩放
                0, 1f);     //X轴缩放比例   
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,  
                0, 1f);  
        //实现依次执行动画,顺序是pvhX,pvhY,pvhZ
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY,pvhZ).setDuration(1000).start();  
    }  

	/**
	 * 多个效果放在一起    然后用ObjectAnimator执行动画
	 * @param v
	 */
	public void rotateyAnimRun(final View v){
		//ofFloat设置动画作用的元素,作用的属性,动画开始,结束,以及中间的任意值 
		//rotation:旋转      X轴旋转360°      时间是1000ms
		//ObjectAnimator.ofFloat(v, "rotationX", 0.0F,360F).setDuration(1000).start();
		
		ObjectAnimator animator = ObjectAnimator
				.ofFloat(v, "xfhy", 1.0F,0.0F)
				.setDuration(1000);
		animator.start();
		
		/**
		 * 此接口的实现可以添加自己为更新听众的ValueAnimator实例能够接收每一个动画帧上的回调，
		 * 当前帧的值已经计算出该ValueAnimator后。
		 */
		animator.addUpdateListener(new AnimatorUpdateListener() {
			
			//通知动画的另一帧的出现。
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				//ValueAnimator    该类提供了一个简单的计时引擎，用于运行动画，计算动画值并将其设置在目标对象上。
				
				//当ValueAnimator只有一个属性是动画时计算的最新值。 这个值只有在动画运行时才有意义。 
				//此只读属性的主要目的是在调用onAnimationUpdate（ValueAnimator）时从ValueAnimator中检索值
				//，该值在每个动画帧之间调用，紧随该值计算。
				float cVal = (float) animation.getAnimatedValue();
				v.setAlpha(cVal);
				v.setScaleX(cVal);
				v.setScaleY(cVal);
			}
		});
		
	}

## 1.2 ValueAnimator实现动画

	/**
	 * 自由落体
	 * 
	 * @param view
	 * 
	 */
	public void verticalRun(View view) {
		ValueAnimator animator = ValueAnimator.ofFloat(0,
				mScreenHeight - ball.getHeight());
		animator.setTarget(ball); // 设置动画的执行目标
		animator.setDuration(1000).start(); // 设置开始执行动画
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// 设置此视图相对于其顶部位置的垂直位置。 除了对象的布局放置之外，这有效地定位了对象后布局。
				// 设置的位置根据animation动画的值来决定
				ball.setTranslationY((float) animation.getAnimatedValue());
			}
		});
	}

	/**
	 * 抛物线
	 * 
	 * @param view
	 * 
	 *            如果我希望小球抛物线运动【实现抛物线的效果，水平方向100px/s，垂直方向加速度200px/s*s 】，
	 *            分析一下，貌似只和时间有关系，但是根据时间的变化，横向和纵向的移动速率是不同的，
	 *            我们该咋实现呢？此时就要重写TypeValue的时候了，因为我们在时间变化的同时，
	 *            需要返回给对象两个值，x当前位置，y当前位置：
	 * 
	 *            ValueAnimator valueAnimator = new ValueAnimator();
	 *            valueAnimator.setDuration(3000);
	 *            valueAnimator.setObjectValues(new PointF(0, 0));
	 *            valueAnimator.setInterpolator(new LinearInterpolator());
	 *            valueAnimator.setEvaluator(new TypeEvaluator<PointF>() { //
	 *            fraction = t / duration
	 * @Override public PointF evaluate(float fraction, PointF startValue,
	 *           PointF endValue) { Log.e(TAG, fraction * 3 + ""); // x方向200px/s
	 *           ，则y方向0.5 * 10 * t PointF point = new PointF(); point.x = 200 *
	 *           fraction * 3; point.y = 0.5f * 200 * (fraction * 3) * (fraction
	 *           * 3); return point; } });
	 */
	public void parabola(View view) {
		ValueAnimator valueAnimator = new ValueAnimator();
		valueAnimator.setDuration(3000);
		valueAnimator.setObjectValues(new PointF(0, 0));
		//用于计算此动画的经过分数的时间插值器。 内插器确定动画是否以线性或非线性运动（如加速和减速）运行。
		//默认值为android.view.animation.AccelerateDecelerateInterpolator
		valueAnimator.setInterpolator(new LinearInterpolator());
		
		//计算动画值时使用的类型评估器。 系统将根据构造函数中的startValue和endValue的类型自动分配一个float或int评估器
		//自定义TypeEvaluator传入的泛型可以根据自己的需求，自己设计个Bean。
		valueAnimator.setEvaluator(new TypeEvaluator<PointF>() {
			// fraction = t / duration
			@Override
			public PointF evaluate(float fraction, PointF startValue,
					PointF endValue) {
				// x方向200px/s ，则y方向0.5 * 10 * t
				PointF point = new PointF();
				point.x = 200 * fraction * 3;
				point.y = 0.5f * 200 * (fraction * 3) * (fraction * 3);
				return point;
			}
		});

		valueAnimator.start();
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				PointF point = (PointF) animation.getAnimatedValue();
				ball.setX(point.x);
				ball.setY(point.y);

			}
		});
	}

	/**
	 * 淡出且删除
	 * @param view
	 */
	public void fadeOut(View view){
		/**
		 * 构造并返回一个在浮点值之间进行动画化的ObjectAnimator。 单个值意味着该值是动画化的值，在这种情况下，
		 * 起始值将从动画属性派生，并在第一次调用start（）时引用目标对象。 
		 * 两个值意味着开始和结束值。 两个以上的值意味着一个起始值，
		 * 一路上动画的值和一个结束值（这些值将在动画的持续时间内均匀分布）。
		 */
		ObjectAnimator animator = ObjectAnimator.ofFloat(ball, "alpha", 0.5f);
		
		//这样就可以监听动画的开始、结束、被取消、重复等事件~但是有时候会觉得，
		//我只要知道结束就行了，这么长的代码我不能接收，那你可以使用AnimatorListenerAdapter
		//AnimatorListenerAdapter继承了AnimatorListener接口，然后空实现了所有的方法~
		//animator还有cancel()和end()方法：cancel动画立即停止，停在当前的位置；end动画直接到最终状态。
		animator.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(android.animation.Animator animation) {
				Log.d("xfhy", "onAnimationEnd");
				ViewGroup parentGroup = (ViewGroup) ball.getParent();
				if(parentGroup != null){
					parentGroup.removeView(ball);
				}
			};
		});
		animator.start();    //最后记得开始执行动画
	}

## 1.3 AnimatorSet的使用

	/**
	 * 多个动画一起执行
	 * @param view
	 * 实现X轴和Y轴方向的同时放大到2倍
	 */
	public void togetherRun(View view) {
		                                         //设置该View的scaleX   X轴的比例从1.0->2.0
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(mBall,"scaleX", 1.0f,2.0f);
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(mBall, "scaleY", 1.0f,2.0f);
		AnimatorSet animSet = new AnimatorSet();
		animSet.setDuration(2000);
		animSet.setInterpolator(new LinearInterpolator());
		animSet.playTogether(anim1,anim2);   //多个动画同时执行
		animSet.start();  //最后记得开始执行动画
	}

	/**
	 * 多个动画一起执行    多个动画顺序执行
	 * @param view
	 */
	public void playWithAfter(View view)  {
		float cx = mBall.getX();   //获取该mBall的X坐标
		
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(mBall, "alpha", 1.0f,0.5f);  //从1.0不透明到0.5半透明
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(mBall, "scaleX", 1.0f,2.0f); //X比例 变到2倍
		ObjectAnimator anim3 = ObjectAnimator.ofFloat(mBall, "scaleY", 1.0f,2.0f);
		ObjectAnimator anim4 = ObjectAnimator.ofFloat(mBall, "x", cx,0f);  //x  从当前位置到0
		ObjectAnimator anim5 = ObjectAnimator.ofFloat(mBall, "x", cx);  //x  位置到cx    恢复初始位置
		
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(anim1).with(anim2);   //anim1-anim4  一起执行
		animatorSet.play(anim2).with(anim3);
		animatorSet.play(anim3).with(anim4);  
		animatorSet.play(anim5).after(anim4);  //anim5在anim4执行之后才执行
		
		animatorSet.setDuration(1000);
		animatorSet.start();  //开始执行
	}

## 1.4  如何使用xml文件来创建属性动画

> 大家肯定都清楚，View Animator 、Drawable Animator都可以在anim文件夹下创建动画，然后在程序中使用，甚至在Theme中设置为属性值。当然了，属性动画其实也可以在文件中声明：

1.首先在res下建立animator文件夹，然后建立res/animator/scalex.xml

	<?xml version="1.0" encoding="utf-8"?>  
	<objectAnimator xmlns:android="http://schemas.android.com/apk/res/android"  
	    android:duration="1000"  
	    android:propertyName="scaleX"  
	    android:valueFrom="1.0"  
	    android:valueTo="2.0"  
	    android:valueType="floatType" >  
	</objectAnimator> 

2.如果需要一起执行的动画(或者是顺序执行的动画),则需要在`res/animator/ `下建立一个xml文件

	<?xml version="1.0" encoding="utf-8"?>
	<set xmlns:android="http://schemas.android.com/apk/res/android"
	    android:ordering="together" >
	
	    <!-- 同时使控件横向和纵向都缩放 
	    	使用set标签，有一个orderring属性设置为together,【还有另一个值：sequentially（表示一个接一个执行）】。
	    	android:propertyName="scaleX"   是表示什么属性变化
	    -->
	
	    <objectAnimator
	        android:duration="1000"
	        android:propertyName="scaleX"
	        android:valueFrom="1.0"
	        android:valueTo="2.0"
	        android:valueType="floatType" >
	    </objectAnimator>
	    <objectAnimator
	        android:duration="1000"
	        android:propertyName="scaleY"
	        android:valueFrom="1.0"
	        android:valueTo="2.0"
	        android:valueType="floatType" >
	    </objectAnimator>
	
	</set>

3.在java代码中的调用

	/**
	 * 单独执行一个X的动画
	 * @param view
	 */
	public void scaleX(View view)  
    {  
        // 加载动画  
		//使用AnimatorInflater加载动画的资源文件，然后设置目标，就ok~~是不是很简单，这只是单纯横向的放大一倍~
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.scalex);  
        anim.setTarget(mBall);  //目标
        anim.start();     //开始执行动画
    }  
	
	/**
	 * 一起执行的动画
	 * @param view
	 */
	public void together(View view) {
		//加载动画
		Animator animator = AnimatorInflater.loadAnimator(this, R.animator.scalex_and_y);
		
		//缩放、反转等都有中心点或者轴，默认中心缩放，和中间对称线为反转线，所以我决定这个横向，纵向缩小以左上角为中心点：
        //设置旋转和缩放视图的点的x位置。 默认情况下，枢轴点以对象为中心。 设置此属性将禁用此行为，并导致视图仅使用显式设置的pivotX和pivotY值。
		//很简单，直接给View设置pivotX和pivotY，然后调用一下invalidate，就ok了。
        mBall.setPivotX(0);
        mBall.setPivotY(0);
        mBall.invalidate();   //显示地调用invalidate
		
		animator.setTarget(mBall);   //设置目标
		animator.start();   //设置动画开始执行
	}
**通过写xml声明动画，使用set嵌套set，结合orderring属性，也基本可以实现任何动画~~上面也演示了pivot的设置。**

## 1.5布局动画(Layout Animations)

> 在Android中，最简单的动画就是补间动画了。通过补间动画，可以对一个控件进行位移、缩放、旋转、改变透明度等动画。但是补间动画只能对一个控件使用，如果要对某一组控件播放一样的动画的话，可以考虑layout-animation。
> 
> LayoutAnimationController用于为一个layout里面的控件，或者是一个ViewGroup里面的控件设置动画效果，可以在XML文件中设置，亦可以在Java代码中设置。
>主要使用LayoutTransition为布局的容器设置动画，当容器中的视图层次发生变化时存在过渡的动画效果。

过渡的类型一共有四种：

- LayoutTransition.APPEARING 当一个View在ViewGroup中出现时，对此View设置的动画
- LayoutTransition.CHANGE_APPEARING 当一个View在ViewGroup中出现时，对此View对其他View位置造成影响，对其他View设置的动画
- LayoutTransition.DISAPPEARING  当一个View在ViewGroup中消失时，对此View设置的动画
- LayoutTransition.CHANGE_DISAPPEARING 当一个View在ViewGroup中消失时，对此View对其他View位置造成影响，对其他View设置的动画
- LayoutTransition.CHANGE 不是由于View出现或消失造成对其他View位置造成影响，然后对其他View设置的动画。

注意动画到底设置在谁身上，此View还是其他View。

1.布局文件

	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"  
	    xmlns:tools="http://schemas.android.com/tools"  
	    android:id="@+id/id_container"  
	    android:layout_width="match_parent"  
	    android:layout_height="match_parent"  
	    android:orientation="vertical" >  
	  
	    <Button  
	        android:layout_width="wrap_content"  
	        android:layout_height="wrap_content"
	        android:onClick="addBtn"    
	        android:text="addBtns" />  
	  
	    <CheckBox  
	        android:id="@+id/id_appear"  
	        android:layout_width="wrap_content"  
	        android:layout_height="wrap_content"  
	        android:checked="true"  
	        android:text="APPEARING" />  
	  
	    <CheckBox  
	        android:id="@+id/id_change_appear"  
	        android:layout_width="wrap_content"  
	        android:layout_height="wrap_content"  
	        android:checked="true"  
	        android:text="CHANGE_APPEARING" />  
	  
	    <CheckBox  
	        android:id="@+id/id_disappear"  
	        android:layout_width="wrap_content"  
	        android:layout_height="wrap_content"  
	        android:checked="true"  
	        android:text="DISAPPEARING" />  
	  
	    <CheckBox  
	          android:id="@+id/id_change_disappear"  
	        android:layout_width="wrap_content"  
	        android:layout_height="wrap_content"  
	        android:checked="true"  
	        android:text="CHANGE_DISAPPEARING " />  
	  
	</LinearLayout>  

2.代码

	public class MainActivity extends Activity implements OnCheckedChangeListener {

	/**
	 * 这是最外层的LinearLayout
	 */
	private ViewGroup viewGroup;
	private GridLayout mGridLayout;
	private int mVal;
	private LayoutTransition mTransition;

	private CheckBox mAppear, mChangeAppear, mDisAppear, mChangeDisAppear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		viewGroup = (ViewGroup) findViewById(R.id.id_container);

		mAppear = (CheckBox) findViewById(R.id.id_appear);
		mChangeAppear = (CheckBox) findViewById(R.id.id_change_appear);
		mDisAppear = (CheckBox) findViewById(R.id.id_disappear);
		mChangeDisAppear = (CheckBox) findViewById(R.id.id_change_disappear);

		mAppear.setOnCheckedChangeListener(this);
		mChangeAppear.setOnCheckedChangeListener(this);
		mDisAppear.setOnCheckedChangeListener(this);
		mChangeDisAppear.setOnCheckedChangeListener(this);

		// 创建一个GridLayout
		mGridLayout = new GridLayout(this);
		// 设置每列5个按钮
		mGridLayout.setColumnCount(5);
		// 添加到布局中
		viewGroup.addView(mGridLayout);
		// 默认动画全部开启
		mTransition = new LayoutTransition();
		mGridLayout.setLayoutTransition(mTransition);

	}

	/**
	 * 添加按钮
	 * 
	 * @param view
	 */
	public void addBtn(View view) {
		// 新建一个Button并添加到GridLayout中,添加到第二个按钮的位置
		final Button button = new Button(this);
		button.setText((++mVal) + "");
		mGridLayout.addView(button, Math.min(1, mGridLayout.getChildCount()));
		// 给按钮设置点击事件,点击则移除
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mGridLayout.removeView(button);
			}
		});
	}

	/**
	 * 如果复选框状态发生改变 则重新设置LayoutTransition
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mTransition = new LayoutTransition();   //重新new一个LayoutTransition
		
		//当一个View在ViewGroup中出现时，对此View设置的动画
		mTransition.setAnimator(
				LayoutTransition.APPEARING,
				(mAppear.isChecked() ? mTransition
						.getAnimator(LayoutTransition.APPEARING) : null));
		
		//当一个View在ViewGroup中出现时，对此View对其他View位置造成影响，对其他View设置的动画
		mTransition
				.setAnimator(
						LayoutTransition.CHANGE_APPEARING,
						(mChangeAppear.isChecked() ? mTransition
								.getAnimator(LayoutTransition.CHANGE_APPEARING)
								: null));
		
		//当一个View在ViewGroup中消失时，对此View设置的动画
		mTransition.setAnimator(
				LayoutTransition.DISAPPEARING,
				(mDisAppear.isChecked() ? mTransition
						.getAnimator(LayoutTransition.DISAPPEARING) : null));
		
		//当一个View在ViewGroup中消失时，对此View对其他View位置造成影响，对其他View设置的动画
		mTransition.setAnimator(
				LayoutTransition.CHANGE_DISAPPEARING,
				(mChangeDisAppear.isChecked() ? mTransition
						.getAnimator(LayoutTransition.CHANGE_DISAPPEARING)
						: null));
		
		/*
		 * 当然了动画支持自定义，还支持设置时间，比如我们修改下，添加的动画为：
		 * mTransition.setAnimator(LayoutTransition.APPEARING, (mAppear  
                .isChecked() ? ObjectAnimator.ofFloat(this, "scaleX", 0, 1)  
                : null));  
		 * */
		
		mGridLayout.setLayoutTransition(mTransition);   //重新设置LayoutTransition
	}

## 1.6 View的anim方法

> 在SDK11的时候，给View添加了animate方法，更加方便的实现动画效果。

1.布局文件：

	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent" 
	    >
	
	    <ImageView
	        android:id="@+id/id_ball"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:src="@drawable/ball" />
	
	    <LinearLayout
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:layout_alignParentBottom="true"
	        android:orientation="horizontal" >
	
	        <Button
	            android:layout_width="wrap_content"
	            android:layout_height="wrap_content"
	            android:onClick="viewAnim"
	            android:text="View Anim" />
	
	        <Button
	            android:layout_width="wrap_content"
	            android:layout_height="wrap_content"
	            android:onClick="propertyValuesHolder"
	            android:text="PropertyValuesHolder " />
	        
	
	    </LinearLayout>
	
	</RelativeLayout>

代码:

	public class MainActivity extends Activity {
		protected static final String TAG = "MainActivity";
	
		private ImageView mBall;
		private float mScreenHeight;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
	
			DisplayMetrics outMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
			mScreenHeight = outMetrics.heightPixels;
			mBall = (ImageView) findViewById(R.id.id_ball);
	
		}
	
		/**
		 * 用View的animate()方法实现图片的简单下落
		 */
		public void viewAnim(View view) {
	
			// 简单的使用mBlueBall.animate().alpha(0).y(mScreenHeight /
			// 2).setDuration(1000).start()就能实现动画~~不过需要SDK11，
			// 此后在SDK12，SDK16又分别添加了withStartAction和withEndAction用于在动画前
			// ，和动画后执行一些操作。当然也可以.setListener(listener)等操作。
			mBall.animate().alpha(0) // 设置透明的逐渐减为0
					.y(mScreenHeight / 2).setDuration(1000) // 设置它的y坐标一直从0增大刀屏幕高度的一半
															// 时间是1秒
					.withStartAction(new Runnable() {
	
						@Override
						public void run() {
							Log.d("xfhy", "START");
						}
					}).withEndAction(new Runnable() { // 结束时
	
								@Override
								public void run() {
									Log.d("xfhy", "END");
									runOnUiThread(new Runnable() {
	
										@Override
										public void run() { // 结束时将Y的坐标设置为0,并且将透明的设置为1
											mBall.setY(0);
											mBall.setAlpha(1.0f);
										}
									});
								}
							}).start(); // 最后记得开始执行动画
		}
	
		/**
		 * 使用ObjectAnimator实现上面的变化，我们可以使用：PropertyValueHolder
		 */
		public void propertyValuesHolder(View view) {
			//搞一个PropertyValuesHolder实例    
			PropertyValuesHolder pvh1 = PropertyValuesHolder.ofFloat("alpha", 1.0f,0f,1.0f); //透明度:1->0->1
			PropertyValuesHolder pvh2 = PropertyValuesHolder.ofFloat("y", 0,mScreenHeight/2,0); //y值:0->下降->0
			//设置动画
			ObjectAnimator.ofPropertyValuesHolder(mBall,pvh1,pvh2).setDuration(2000).start();
			
		}
	
	}

![](http://img.blog.csdn.net/20140725000504359)

# 2. 补间动画
1. 透明

		//1.0意味着着完全不透明 0.0意味着完全透明
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
		aa.setDuration(2000); //设置动画执行的时间
		aa.setRepeatCount(1); //设置重复的次数
		aa.setRepeatMode(Animation.REVERSE);//设置动画执行的模式
		//iv开始执行动画 
		iv.startAnimation(aa);
2. 旋转

		//fromDegrees 开始角度   toDegrees 结束角度
		//		RotateAnimation  ra = new RotateAnimation(0, 360);	
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(2000); //设置动画执行的时间
		ra.setRepeatCount(1); //设置重复的次数
		ra.setRepeatMode(Animation.REVERSE);//设置动画执行的模式
		//iv开始执行动画 
		iv.startAnimation(ra);
3. 缩放

		ScaleAnimation sa = new ScaleAnimation(1.0f,2.0f, 1.0f, 2.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(2000); //设置动画执行的时间
		sa.setRepeatCount(1); //设置重复的次数
		sa.setRepeatMode(Animation.REVERSE);//设置动画执行的模式
		//iv开始执行动画 
		iv.startAnimation(sa);
4. 位移

		TranslateAnimation ta = new TranslateAnimation
		(Animation.RELATIVE_TO_PARENT, 0, 
		Animation.RELATIVE_TO_PARENT, 0, 
		Animation.RELATIVE_TO_PARENT, 0, 
		Animation.RELATIVE_TO_PARENT, 0.2f);
		ta.setDuration(2000); //设置动画执行的时间
		ta.setFillAfter(true);//当动画结束后 动画停留在结束位置
		
		//开始动画
		iv.startAnimation(ta);

**总结:补间动画不会改变控件真实的坐标**


# 3.帧动画(Frame animation)

用来播放gif的,类似,一帧一帧的播放动画.

1.girl_anim.xml

	<?xml version="1.0" encoding="utf-8"?>
	<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
	    android:oneshot="true" >
	    <!-- android:oneshot="true"表示一直重复 -->
	    <!-- android:duration="200"   时间 -->
	    <item android:drawable="@drawable/girl_1" android:duration="200" />
	    <item android:drawable="@drawable/girl_2" android:duration="200" />
	    <item android:drawable="@drawable/girl_3" android:duration="200" />
	    <item android:drawable="@drawable/girl_4" android:duration="200" />
	    <item android:drawable="@drawable/girl_5" android:duration="200" />
	    <item android:drawable="@drawable/girl_5" android:duration="200" />
	
	</animation-list>

2.调用

	// [1]找到ImageView控件 用来显示动画效果
		ImageView rocketImage  = (ImageView) findViewById(R.id.iv_img);
		// [2]设置背景资源
		rocketImage.setBackgroundResource(R.drawable.girl_anim);
		// [3]获取AnimationDrawable 类型
		animationDrawable = (AnimationDrawable) rocketImage.getBackground();
	animationDrawable.start();
