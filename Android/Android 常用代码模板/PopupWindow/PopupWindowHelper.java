package com.na517.hotel.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by feiyang on 2017/12/1 12:48
 * Description : 通用的弹出PopupWindow
 */
public class PopupWindowHelper implements PopupWindow.OnDismissListener {

    /**
     * PopupWindow放在view左边
     */
    public static final int LEFT = 1000;
    /**
     * PopupWindow放在view右边
     */
    public static final int RIGHT = 1001;
    /**
     * PopupWindow放在view上边
     */
    public static final int TOP = 1002;
    /**
     * PopupWindow放在view下边
     */
    public static final int BOTTOM = 1003;
    /**
     * PopupWindow放在屏幕底部
     */
    public static final int SCREEN_BOTTOM = 1004;
    /**
     * PopupWindow放在屏幕顶部
     */
    public static final int SCREEN_TOP = 1005;

    /**
     * popupWindow位置类型   替代Java中的枚举类型
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEFT, RIGHT, TOP, BOTTOM, SCREEN_BOTTOM, SCREEN_TOP})
    private @interface PopupPosition {
    }

    private Context mContext;
    private View mAnchor;
    private View mView;
    private int mLayoutId = -1;
    private int mWidth;
    private int mHeight;
    private int mStyle;
    private boolean mFocusable;
    private float mAlpha;
    private Drawable mBackgroundDrawable;
    private int mPopupPosition;

    private PopupWindow mPopupWindow;

    private PopupWindowHelper(Builder builder) {
        this.mContext = builder.context;
        this.mAnchor = builder.anchor;
        this.mView = builder.showView;
        this.mLayoutId = builder.layoutId;
        this.mWidth = builder.width;
        this.mHeight = builder.height;
        this.mStyle = builder.style;
        this.mFocusable = builder.focusable;
        this.mAlpha = builder.alpha;
        this.mBackgroundDrawable = builder.backgroundDrawable;
        this.mPopupPosition = builder.popupPosition;
    }

    /**
     * PopupWindow dismiss
     */
    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 显示PopupWindow
     */
    public PopupWindow show() {
        //1, 将弹出窗口需要展示的布局加载进来
        //View popupView = View.inflate(mContext, mLayoutId, null);

        //2, 创建popupWindow
        //参数:view,宽度,高度,是否能获取焦点
        if (mView != null) {
            mPopupWindow = new PopupWindow(mView, mWidth, mHeight, true);
        } else if (mLayoutId != -1) {
            mView = View.inflate(mContext, mLayoutId, null);
            mPopupWindow = new PopupWindow(mView, mWidth, mHeight, true);
        }

        //3, 设置显示隐藏的动画
        mPopupWindow.setAnimationStyle(mStyle);

        //4, 在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //5, 点击空白处时，隐藏掉pop窗口
        mPopupWindow.setFocusable(mFocusable);

        //6, 隐藏PopupWindow下面的背景透明度
        setBackgroundAlpha(mAlpha);

        //7, 设置popupWindow的背景颜色  这里需要设置成透明的  这里设置成透明的
        mPopupWindow.setBackgroundDrawable(mBackgroundDrawable);

        //8, 添加pop窗口关闭事件
        mPopupWindow.setOnDismissListener(this);

        //9, 位置
        switch (mPopupPosition) {
            case LEFT:
                mPopupWindow.showAsDropDown(mAnchor, -mPopupWindow.getWidth(), -mPopupWindow
                        .getHeight()
                        / 2 - mAnchor.getHeight() / 2);
                break;
            case RIGHT:
                mPopupWindow.showAsDropDown(mAnchor, mAnchor.getWidth(), (-mPopupWindow
                        .getHeight()
                        >> 1) - (mAnchor.getHeight() >> 1));
                break;
            case TOP:
                mPopupWindow.showAsDropDown(mAnchor,
                        Math.abs(mAnchor.getWidth() / 2 - mPopupWindow.getWidth() / 2),
                        -(mPopupWindow.getHeight() + mAnchor.getMeasuredHeight()));
                break;
            case BOTTOM:
                mPopupWindow.showAsDropDown(mAnchor,
                        Math.abs(mAnchor.getWidth() / 2 - mPopupWindow.getWidth() / 2), 0);
                break;
            case SCREEN_BOTTOM:
                mPopupWindow.showAtLocation(mAnchor, Gravity.BOTTOM, 0, 0);
                break;
            case SCREEN_TOP:
                mPopupWindow.showAtLocation(mAnchor, Gravity.TOP, 0, 0);
                break;
            default:
                mPopupWindow.showAtLocation(mAnchor, Gravity.BOTTOM, 0, 0);
                break;
        }

        return mPopupWindow;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 0.0-1.0
     */
    public void setBackgroundAlpha(float bgAlpha) {
        if (mContext instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) mContext;
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            activity.getWindow().setAttributes(lp);
        }

    }

    @Override
    public void onDismiss() {
        //把背景设置回来
        setBackgroundAlpha(1f);
    }

    public static class Builder {
        private Context context;
        private View anchor;
        private int layoutId = -1;
        private View showView;
        private int width;
        private int height;
        private int style;
        private boolean focusable;
        private float alpha;
        private Drawable backgroundDrawable;
        private int popupPosition;

        /**
         * Context (必填)
         */
        public Builder context(@NonNull Context context) {
            this.context = context;
            return this;
        }

        /**
         * 锚  (必填)
         */
        public Builder anchor(@NonNull View view) {
            this.anchor = view;
            return this;
        }

        /**
         * 布局id (布局id和view选填1个)
         */
        public Builder layout(int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        /**
         * 需要显示的View (布局id和view选填1个)
         */
        public Builder view(@NonNull View view) {
            this.showView = view;
            return this;
        }

        /**
         * PopupWindow宽度
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * PopupWindow高度
         */
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * PopupWindow style
         */
        public Builder style(int style) {
            this.style = style;
            return this;
        }

        /**
         * PopupWindow点击外部是否取消
         */
        public Builder focusable(boolean focusable) {
            this.focusable = focusable;
            return this;
        }

        /**
         * 透明度
         */
        public Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * 背景
         */
        public Builder backgroundDrawable(Drawable backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
            return this;
        }

        /**
         * PopupWindow弹出位置
         *
         * @param popupPosition 可选:LEFT, RIGHT, TOP, BOTTOM, SCREEN_BOTTOM, SCREEN_TOP
         */
        public Builder position(@PopupPosition int popupPosition) {
            this.popupPosition = popupPosition;
            return this;
        }

        /**
         * 构建PopupSelectPriceStar   记得调用PopupSelectPriceStar.show()
         */
        public PopupWindowHelper build() {

            if (context == null) {
                throw new IllegalArgumentException("Context can't be null!");
            }
            if (anchor == null) {
                throw new IllegalArgumentException("anchor View can't be null!");
            }
            if (showView == null && layoutId == -1) {
                throw new IllegalArgumentException("showView can't be null!");
            }
            if (backgroundDrawable == null) {
                backgroundDrawable = new ColorDrawable();
            }

            return new PopupWindowHelper(this);
        }

    }

}
