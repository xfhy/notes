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
