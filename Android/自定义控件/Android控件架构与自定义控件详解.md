# Android控件架构与自定义控件详解

## 1. Android界面的架构图

![](http://olg7c0d2n.bkt.clouddn.com/17-3-25/17122039-file_1490441531165_161c4.png)

![](http://olg7c0d2n.bkt.clouddn.com/17-3-25/69323537-file_1490441546651_13ab7.png)

## 2. View的测量

> Android系统在绘制View前，也必须对View进行测量，告诉系统该画一个多大的View.这个过程在onMeasure方法中进行

测量的模式可以分为以下三种

- EXACTLY
精确值模式：layout_width与layout_heigth或者具体值时：系统使用的是EXACTLY

- **AT_MOST** ：最大值模式 ，当layout_width或layout_height属性为wrap_content控件大小一般随着控件的子空间或内容的变化而变化。

- **UNSPECIFIED**：不指定大小模式
View类默认的onMeasure方法只支持EXACTLY模式，所以如果在自定义控件的时候不重写onMeasure方法的话，就只能使用EXACTLY模式。控件可以响应你指定的具体宽高值或者match_parent属性。**如果要让自定义的View支持wrap_content属性，那么就必须重写onMeasure方法来指定wrap_content时的大小**

演示如何进行View 的测量。

	@Override
	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
	      super.onMeasure(widthMeasureSpec,heigthMeasureSpec);
	}

重写onMeasure方法后，最终要做的工作就是把测量后的宽高值作为参数设置给setaMeasureDimension()

	@Override
	proteced void onMeasure(int widthMeasureSpec,int heigthMeasureSpec){
	        setMeasureDimension(measureWidth(widthMeasureSpec),measureHeigth(heigthMeasureSpec));
	}
	private int measureWidth(int measureSpec){
	      int result=0;
	      int specMode=MeasureSpec.getMode(measureSpec); 
	      if(specMode==MeasureSpec.EXACTLY)
	       {
	              result=specSize;
	       }else{
	              reslut=200;
	              if(spec==MeasureSpec.AT_MOST){
	                    result==Math.min(result,specSize);
	              }
	        }
	      return result;
	}
	private int measureHeigth(int measureSpec){
	      int result=0;
	      int specMode=MeasureSpec.getMode(measureSpec); 
	      if(specMode==MeasureSpec.EXACTLY)
	       {
	              result=specSize;
	       }else{
	              reslut=200;
	              if(spec==MeasureSpec.AT_MOST){
	                    result==Math.min(result,specSize);
	              }
	        }
	      return result;
	}

**所以,重写onMeasure()方法的目的,就是为了能够给View一个wrap_content属性下的默认大小**

下面是效果    

- 宽高属性是match_parent
![](http://olg7c0d2n.bkt.clouddn.com/17-3-25/81971271-file_1490444476710_b9c3.png)

- 宽高是240dp

![](http://olg7c0d2n.bkt.clouddn.com/17-3-25/77579595-file_1490444576290_b044.png)

- 宽高是wrap_content

![](http://olg7c0d2n.bkt.clouddn.com/17-3-25/55438708-file_1490444620392_10c8c.png)

## 3. View的绘制

> Canvas就像是一个画板,使用Paint就可以在上面作画了.

当测量好一个View之后,我们就可以简单地在上面重写onDraw()方法,并在Canvas对象上绘制所需要的图形.

**这里我们虽然也使用了Canvas的绘制API,但其实并没有将图形直接绘制在onDraw()方法指定的那块画布上,而是通过改变bitmap,然后让view重绘,从而显示改变之后的bitmap.**

## 4. ViewGroup的测量

> ViewGroup会去管理其子View,其中一个管理项目就是负责子View的显示大小.

当ViewGroup的大小为**wrap_content**时,ViewGroup就需要对子View进行遍历,以便获得所有子View的大小,而在其他模式下则会通过具体的指定值来设置自身的大小。

VIewGroup在测量时会通过遍历所有子View，从而调用子View的Measure方法来获得每个子View的测量结果，前面所说的对View的测量，就是在这里进行的。

当子View测量完毕后，就需要将子View放到合适的位置，这个过程就是View的Layout过程。ViewGroup在执行Layout过程时，同样是遍历来调用子View的Layout方法，并指定其具体显示的位置，从而来决定其布局位置。

在自定义ViewGroup时，通常会去重写onLayout()方法来控制其子View显示位置的逻辑。同样，如果需要支持wrap_content属性，那么它必须要还要重写onMeasure()方法，这点与View是相同的。

## 5. 自定义View

有三种方法来实现自定义控件

- 对现有控件进行扩展
- 通过组合来实现新的控件
- 重写View来实现全新的控件。

在View通常有以下一些比较重要的回调方法

- onFinishInfalte():从XML加载组建中回调
- onSizeChanged: 组件大小改变时回调
- onMeasure:回调该方法来进行测量
- onLayout: 回调该方法来确定显示的位置
- onTouchEvent :监听到触摸事件时回调
### 5.1 对现有控件进行拓展

比如拓展TextView,需要一个类继承TextView,然后重写onDraw()方法

	@Override
    protected void onDraw(Canvas canvas) {
        //在回调方法前,实现自己的逻辑,对TextView来说既是在绘制文本内容前

        mPaint1 = new Paint();
        mPaint1.setColor(getResources().getColor(android.R.color.holo_blue_light));
        mPaint1.setStyle(Paint.Style.FILL);
        mPaint2 = new Paint();
        mPaint2.setColor(Color.YELLOW);
        mPaint2.setStyle(Paint.Style.FILL);

        //绘制外层矩形
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint1);
        //绘制内层矩形
        canvas.drawRect(10,10,getMeasuredWidth()-10,getMeasuredHeight()-10,mPaint2);

        canvas.save();
        //绘制文字前平移10像素
        canvas.translate(10,0);

        //父类完成的方法   即绘制文本
        super.onDraw(canvas);
        //在回调方法后,实现自己的逻辑,对TextView来说是在绘制文本内容后
        canvas.restore();   //回滚
    }

### 5.2 闪动文字效果

	public class FlashingTextView extends TextView {

	  private int mViewWidth = 0;
	  private Paint mPaint;
	  private LinearGradient mLinearGradient;
	  private Matrix mGradientMatrix;
	  private int mTranslate;

	  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	    if (mViewWidth == 0) {
	      mViewWidth = getMeasuredWidth();
	      if (mViewWidth > 0) {
	        mPaint = getPaint();
	        mLinearGradient = new LinearGradient(
	            0,
	            0,
	            mViewWidth,
	            0,
	            new int[] {
	                Color.BLUE, 0xffffffff,
	                Color.BLUE
	            },
	            null,
	            Shader.TileMode.CLAMP);
	        mPaint.setShader(mLinearGradient);
	        mGradientMatrix = new Matrix();
	      }
	    }
	  }
	
	  @Override protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    if (mGradientMatrix != null) {
	      mTranslate += mViewWidth/5;
	      if(mTranslate>2*mViewWidth){
	        mTranslate = -mViewWidth;
	      }
	      mGradientMatrix.setTranslate(mTranslate,0);
	      mLinearGradient.setLocalMatrix(mGradientMatrix);
	      postInvalidateDelayed(300);
	    }
	  }
	}

## 6. 创建复合控件

> 创建复合控件可以很好地创建出具有重用功能的控件集合。这种方式通常需要继承一个合适的ViewGroup，再给它添加指定功能的控件，从而组合成新的复合控件。通过这种方式创建的控件，我们一般会给它指定一些可配置的属性，让它具有更强的拓展性。下面就以一个TopBar为示例，讲解如何创建复合控件。

### 6.1 定义属性

为一个View提供可自定义的属性非常简单，只需要在res资源目录的values目录下创建一个attrs.xml的属性定义文件，并在该文件中通过如下代码定义相应的属性即可。

	<?xml version="1.0" encoding="utf-8"?>
	<resources>
	
	  <declare-styleable name="TopBar">
	    <!--fornat属性用来指定属性的类型
	      dimension表示大小
	      reference引用属性
	      color是颜色
	      -->
	    <attr name="_title" format="string"/>
	    <attr name="_titleTextSize" format="dimension"/>
	    <attr name="_titleTextColor" format="color"/>
	    <attr name="leftTextColor" format="color"/>
	    <attr name="leftBackground" format="reference|color"/>
	    <attr name="leftText" format="string"/>
	    <attr name="rightTextColor" format="color"/>
	    <attr name="rightBackground" format="reference|color"/>
	    <attr name="rightText" format="string"/>
	  </declare-styleable>
	
	</resources>

我们在代码中通过标签声明了使用自定义属性，并通过name属性来确定引用的名称。最后，通过标签来声明具体的自定义属性，比如在这里定义了标题文字的字体、大小、颜色，左边按钮的文字颜色、背景、字体.

在确定好属性后，就可以创建一个自定义控件—-TopBar，并让它继承自ViewGroup，从而组合一些需要的控件。这里为了简单，我们继承RelativeLayout。在构造方法中，通过如下所示代码来获取XML布局文件中自定义的那些属性，即与我们使用系统提供的那些属性一样.

	// 通过这个方法，将你在attrs.xml中定义的declare-styleable的所有属性值存储到TypedArray中
	TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
	// 从TypedArray中取出对应的值来为要设置的属性赋值
	mLeftTextColor = ta.getColor(R.styleable.TopBar_leftTextColor, 0);
	mLeftBackground = ta.getDrawable(R.styleable.TopBar_leftBackground);
	mLeftText = ta.getString(R.styleable.TopBar_leftText);
	mRightTextColor = ta.getColor(R.styleable.TopBar_rightTextColor, 0);
	mRightBackground = ta.getDrawable(R.styleable.TopBar_rightBackground);
	mRightText = ta.getString(R.styleable.TopBar_rightText);
	mTitleTextSize = ta.getDimension(R.styleable.TopBar__titleTextSize, 10);
	mTitleTextColor = ta.getColor(R.styleable.TopBar__titleTextColor, 0);
	mTitle = ta.getString(R.styleable.TopBar__title);
	// 获取完TypedArray的值后，一般要调用recycle方法来避免重新创建时的错误
	ta.recycle();

## 6.2 组合控件

> 接下来，我们就可以开始组合控件了。UI模板TopBar实际上由三个控件组成，即左边的点击按钮mLeftButton，右边的点击按钮mRightButton和中间的标题栏mTitleView。通过动态添加控件的方式，使用addView()方法将这三个控件加入到定义的TopBar模板中，并给它们设置我们前面所获取到的具体的属性值，比如标题的文字颜色、大小等，代码如下所示。

下面是代码,放在构造方法里面的,上面的代码之后.

	// 为创建的组件元素赋值
	// 值就来源于我们在引用的xml文件中给对应属性的赋值
	mLeftButton.setTextColor(mLeftTextColor);
	mLeftButton.setBackground(mLeftBackground);
	mLeftButton.setText(mLeftText);
	 
	mRightButton.setTextColor(mRightTextColor);
	mRightButton.setBackground(mRightBackground);
	mRightButton.setText(mRightText);
	 
	mTitleView.setText(mTitle);
	mTitleView.setTextColor(mTitleTextColor);
	mTitleView.setTextSize(mTitleTextSize);
	mTitleView.setGravity(Gravity.CENTER);
	 
	// 为组件元素设置相应的布局元素
	mLeftLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	mLeftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
	// 添加到ViewGroup
	addView(mLeftButton, mLeftLayoutParams);
	 
	mRightLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	mRightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
	addView(mRightButton, mRightLayoutParams);
	 
	mTitleLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	mTitleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
	addView(mTitleView, mTitleLayoutParams);

**定义接口**

在UI模板类中定义一个左右按钮点击的接口，并创建两个方法，分别用于左边按钮的点击和右边按钮的点击，代码如下所示。

**暴露接口给调用者**

在模板方法中，为左、右按钮增加点击事件，但不去实现具体的逻辑，而是调用接口中相应的点击方法，代码如下所示。

	// 按钮的点击事件，不需要具体的实现，
	// 只需调用接口的方法，回调的时候，会有具体的实现
	mRightButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	        mListener.rightClick();
	    }
	});
	mLeftButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	        mListener.leftClick();
	    }
	});
	 
	// 暴露一个方法给调用者来注册接口回调
	// 通过接口来获得回调者对接口方法的实现
	public void setOnTopbarClickListener(topbarClickListener mListener) {
	    this.mListener = mListener;
	}

**实现接口回调**

在调用者的代码中，调用者需要实现这样一个接口，并完成接口中的方法，确定具体的实现逻辑，并使用第二步中暴露的方法，将接口的对象传递进去，从而完成回调。通常情况下，可以使用匿名内部类的形式来实现接口中的方法，代码如下所示。

	mTopBar.setOnTopbarClickListener(new MyTopBar.topbarClickListener() {
	    @Override
	    public void leftClick() {
	        Toast.makeText(MainActivity.this,
	                "left", Toast.LENGTH_SHORT)
	                .show();
	    }
	    @Override
	    public void rightClick() {
	        Toast.makeText(MainActivity.this,
	                "right", Toast.LENGTH_SHORT)
	                .show();
	    }
	});

这里为了简单演示，只显示两个Toast来区分不同的按钮点击事件。除了通过接口回调的方式来实现动态的控制UI模板，同样可以使用公共方法来动态地修改UI模板中的UI，这样就进一步提高了模板的可定制性，代码如下所示。

	/**
	* 设置按钮的显示与否 通过id区分按钮，flag区分是否显示
	*
	* @param id   id
	* @param flag 是否显示
	*/
	public void setButtonVisable(int id, boolean flag) {
	    if (flag) {
	        if (id == 0) {
	            mLeftButton.setVisibility(View.VISIBLE);
	        } else {
	            mRightButton.setVisibility(View.VISIBLE);
	        }
	    } else {
	        if (id == 0) {
	            mLeftButton.setVisibility(View.GONE);
	        } else {
	            mRightButton.setVisibility(View.GONE);
	        }
	    }
	}

**引用UI模板**

最后一步，自然是在需要使用的地方引用UI模板，在引用前，需要指定引用第三方控件的名字空间。

	xmlns:custom="http://schemas.android.com/apk/res-auto"

如果将这个UI模板写到一个布局文件中，代码如下所示。

	<com.xfhy.viewmeasuretest.TopBar
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    app:rightTextColor="@color/colorPrimary"
	    app:rightBackground="@color/colorXfhy"
	    app:rightText="编辑"
	    app:leftTextColor="@color/colorPrimary"
	    app:leftBackground="@color/colorXfhy"
	    app:leftText="返回"
	    app:_titleTextSize="20sp"
	    app:_titleTextColor="@color/colorPrimary"
	    android:id="@+id/tb_bar"
	    android:layout_below="@id/ft"
	    android:layout_marginTop="10dp"
	    app:_title="标题"
	    android:layout_width="match_parent"
	    android:layout_height="50dp"
	    android:background="@color/colorAccent">
	
	</com.xfhy.viewmeasuretest.TopBar>

通过如上所示的代码，我们就可以在其他的布局文件中，直接通过标签来引用这个UI模板View，代码如下所示。

	<include layout="@layout/widget_topbar"/>

## 7. 实现弧线展示图



	public class ArcDisplay extends View {

	  /**
	   * 绘制内部圆需要的成员变量
	   */
	  private Paint mCirclePaint;
	  private float mCenter;
	  private float mInsideRadius;
	  /**
	   * 绘制外面弧度所需要的成员变量
	   */
	  private Paint mArcPaint;
	  private RectF mArcRectF;
	  private float mSweepAngle = 90;
	  /**
	   * 绘制中间Text
	   */
	  private Paint mTextPaint;
	  private String mShowText;
	  private float mShowTextSize;
	  private int mWidth;
	  private int mHeight;
	
	  @Override protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    // 绘制圆
	    canvas.drawCircle(mCenter, mCenter, mInsideRadius, mCirclePaint);
	    // 绘制弧线
	    /**
	     * oval：用于定义圆弧形状和大小的椭圆形边界
	     startAngle float：弧开始的起始角度（以度为单位）
	     sweepAngle float：顺时针测量的扫描角度（以度为单位）
	     useCenter boolean：如果为true，请在圆弧中包含椭圆的中心，如果正在进行描边，则将其关闭。 这会画一个楔子
	     paint：油漆用来画弧
	     */
	    canvas.drawArc(mArcRectF, -90, mSweepAngle, false, mArcPaint);
	    // 绘制文字
	    canvas.drawText(mShowText, 0, mShowText.length(),
	        mCenter, mCenter + (mShowTextSize / 4), mTextPaint);
	  }
	
	  /**
	   * 当这个视图的大小改变时，这在布局期间被调用。 如果您刚刚添加到视图层次结构中，则调用旧值0。
	   * @param w
	   * @param h
	   * @param oldw
	   * @param oldh
	   */
	  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	    mWidth = getMeasuredWidth();
	    mHeight = getMeasuredHeight();
	    this.initView();
	  }
	
	  /**
	   * 初始化布局
	   */
	  private void initView() {
	    float length = Math.min(mWidth,mHeight);
	
	    mCenter = length/2;  //中间的圆的中点坐标
	    mInsideRadius = (float) (length*0.5/2);
	    mCirclePaint = new Paint();
	    mCirclePaint.setAntiAlias(true);
	    //getColor(int) api 23之后应该是被  int getColor (int id,Resources.Theme theme)代替
	    mCirclePaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
	
	    //绘制外层的弧
	    mArcRectF = new RectF(
	        (float) (length * 0.1), (float) (length * 0.1),
	        (float) (length * 0.9), (float) (length * 0.9));
	
	    mArcPaint = new Paint();
	    mArcPaint.setAntiAlias(true);
	    mArcPaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
	    mArcPaint.setStrokeWidth((float) (length * 0.05));
	    mArcPaint.setStyle(Paint.Style.STROKE);
	
	        /*中间显示的文字*/
	    mShowText = setShowText();
	    mShowTextSize = setShowTextSize();
	    mTextPaint = new Paint();
	    mTextPaint.setTextSize(mShowTextSize);
	    mTextPaint.setTextAlign(Paint.Align.CENTER);
	
	  }
	
	  private float setShowTextSize() {
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());
	  }
	
	  private String setShowText() {
	    return "Android Skill";
	  }
	
	  /**
	   * 通知View进行重绘
	   */
	  public void forceInvalidate() {
	    this.invalidate();
	  }
	
	  /**
	   * 设置顺时针测量的扫描角度（以度为单位）
	   * @param sweepValue
	   */
	  public void setSweepValue(float sweepValue) {
	    if (sweepValue >= 0) {
	      mSweepAngle = sweepValue;
	    } else {
	      mSweepAngle = 90;
	    }
	    this.invalidate();
	  }
	
	}

## 8. 音频条形图


