package com.xfhy.daily.ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by xfhy on 2017/8/13.
 * 自定义RadioButton
 * 可实现在checked的时候,通过属性动画将view变大
 * 在未选中时,缩小
 */

public class VariableLargeRadioButton extends AppCompatRadioButton {

    public VariableLargeRadioButton(Context context) {
        super(context);
    }

    public VariableLargeRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VariableLargeRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = null;
        ObjectAnimator scaleY = null;
        if (checked) {  //选中时则将自身变大   未选中则变小
            scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1, 1.1f);
            scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1, 1.1f);
        } else {
            scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1, 0.9f);
            scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1, 0.9f);
        }
        animatorSet.setDuration(500);
        //设置插值器  其速率迅速变大,再减速
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.start();

    }
}
