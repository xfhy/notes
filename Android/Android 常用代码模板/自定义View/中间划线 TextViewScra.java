package com.xfhy.textviewscratches;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * author feiyang
 * create at 2017/9/4 19:01
 * description：
 */
public class TextViewScra extends TextView {

    private static final String TAG = "TextViewScra";
    private Paint paint;

    public TextViewScra(Context context) {
        super(context);

        initView();
    }

    public TextViewScra(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public TextViewScra(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2.3f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        Log.e(TAG,"measuredWidth:"+measuredWidth);
        Log.e(TAG,"measuredHeight:"+measuredHeight);
        int centerHeight = measuredHeight / 2;

        canvas.drawLine(0,centerHeight,measuredWidth,centerHeight,paint);

        super.onDraw(canvas);
    }
}
