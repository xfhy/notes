package com.xfhy.mobilesafe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xfhy.mobilesafe.utils.LogUtil;

/**
 * Created by xfhy on 2017/5/22.
 * "密码输入框"
 * <p>
 * 测量			 摆放		绘制
 * measure	->	layout	->	draw
 * | 		  |			 |
 * onMeasure -> onLayout -> onDraw 重写这些方法, 实现自定义控件
 * <p>
 * 都在onResume()之后执行
 * <p>
 * View流程
 * onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)
 * <p>
 * ViewGroup流程
 * onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)
 */

public class CirclePsdView extends View {

    private static final String TAG = "CirclePsdView";
    private Paint mBGPaint;  //背景画笔
    private Paint mFGPaint;  //前景画笔
    private int mRadius;   //View的半径
    private static final int SMALL_CIRCLE_RADIUS = 10;  //内圆半径

    public CirclePsdView(Context context) {
        super(context);
        init();
    }

    public CirclePsdView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclePsdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化设置
     */
    private void init() {
        mBGPaint = new Paint();
        mFGPaint = new Paint();
        mBGPaint.setARGB(255, 239, 239, 239);//设置背景画笔颜色
        mFGPaint.setColor(Color.TRANSPARENT); //设置前景画笔颜色   默认是没有密码,是透明的
    }

    //测量自己的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //因为是圆形,所以应该让宽高保持一致
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        //该方法必须由onMeasure（int，int）调用来存储测量的宽度和测量高度。 如果没有这样做，将在测量时间触发异常。
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //1, 获取当前控件的宽高,并设置半径的大小
        int w = this.getMeasuredWidth();
        int h = this.getMeasuredHeight();
        mRadius = w < h ? w / 2 : h / 2;

        //2, 首先画背景  #EFEFEF
        canvas.drawCircle(mRadius, mRadius, mRadius, mBGPaint);

        //3, 其次画中间的颜色深一点的圆圈   #B6B6B6
        //画圆形，指定好中心点坐标、半径、画笔
        canvas.drawCircle(mRadius, mRadius, SMALL_CIRCLE_RADIUS, mFGPaint);
    }

    //设置是否存在密码
    public void setHadPass(boolean exist) {
        LogUtil.d(TAG, "密码输入框切换状态");
        //1, 判断是否存在密码    设置前景色
        if (exist) {
            mFGPaint.setARGB(255, 182, 182, 182);//设置画笔颜色
        } else {
            mFGPaint.setColor(Color.TRANSPARENT);   //设置为透明
        }
        //2, 重绘界面
        invalidate();
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
            result = 45;
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
            result = 45;   //给一个默认的大小
            if (specMode == MeasureSpec.AT_MOST) {   //如果是wrap_content
                result = Math.min(result, specSize);   //取最小的  适合控件大小的
            }
        }

        return result;
    }

}
