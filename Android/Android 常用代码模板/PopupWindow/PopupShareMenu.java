package com.xfhy.vmovie.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.xfhy.vmovie.R;

/**
 * Created by xfhy on 2017/6/20.
 * 分享的工具类
 */

public class PopupShareMenu implements PopupWindow.OnDismissListener, View.OnClickListener {

    private static final String TAG = "PopupShareMenu";
    /**
     * 分享PopupWindow的高度
     */
    private static final int SHARE_MENU_HEIGHT = 400;

    /**
     * 对应的AppCompatActivity
     */
    private Context context;

    /**
     * 根布局   需要现在分享布局在其之上的
     */
    private View mRootView;

    /**
     * 分享的布局  R.layout.layout_share
     */
    private int mLayout;
    private ImageView mShareToWeChat;
    private ImageView mShareToCircle;
    private ImageView mShareToQQ;
    private ImageView mShareToQzone;
    private ImageView mShareToVblog;
    private ImageView mCancel;
    private PopupWindow mPopupWindow;

    public PopupShareMenu(Context context, View mRootView, int mLayout) {
        this.context = context;
        this.mRootView = mRootView;
        this.mLayout = mLayout;
    }

    /**
     * 显示分享布局  在底部弹出PopupWindow
     */
    public PopupWindow showSharePopupWindow() {
        //1, 将弹出窗口需要展示的布局加载进来
        View popupView = View.inflate(context, mLayout, null);

        //2, 获取屏幕的宽度
        DisplayMetrics displayMetrics = AppUtils.getAppWidth();

        //3, 创建popupWindow
        //参数:view,宽度,高度,是否能获取焦点
        mPopupWindow = new PopupWindow(popupView,
                displayMetrics.widthPixels,
                SHARE_MENU_HEIGHT, true);

        //查找控件
        mShareToWeChat = (ImageView) popupView.findViewById(R.id.iv_share_wechat);
        mShareToCircle = (ImageView) popupView.findViewById(R.id.iv_share_circle);
        mShareToQQ = (ImageView) popupView.findViewById(R.id.iv_share_qq);
        mShareToQzone = (ImageView) popupView.findViewById(R.id.iv_share_qzone);
        mShareToVblog = (ImageView) popupView.findViewById(R.id.iv_share_vblog);
        mCancel = (ImageView) popupView.findViewById(R.id.iv_share_cancel);

        //设置控件点击事件
        mShareToWeChat.setOnClickListener(this);
        mShareToCircle.setOnClickListener(this);
        mShareToQQ.setOnClickListener(this);
        mShareToQzone.setOnClickListener(this);
        mShareToVblog.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        //设置显示隐藏的动画
        mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);

        //4, 在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //5, 点击空白处时，隐藏掉pop窗口
        mPopupWindow.setFocusable(true);

        //6, 隐藏PopupWindow下面的背景透明度
        setBackgroundAlpha(0.2f);

        //7, 设置popupWindow的背景颜色  这里需要设置成透明的  这里设置成透明的
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());

        //8, 添加pop窗口关闭事件
        mPopupWindow.setOnDismissListener(this);

        //9, 设置PopupWindow的位置
        mPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);

        return mPopupWindow;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 0.0-1.0
     */
    public void setBackgroundAlpha(float bgAlpha) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_wechat:
                Log.e(TAG, "onClick: 分享到微信");
                //弹出一个提示.....
                Snackbar.make(mRootView, "分享到微信成功", Snackbar.LENGTH_LONG)
                        .setAction("取消", this)
                        .show();
                break;
            case R.id.iv_share_circle:
                Log.e(TAG, "onClick: 分享到朋友圈");
                //弹出一个提示.....
                Snackbar.make(mRootView, "分享到朋友圈成功", Snackbar.LENGTH_LONG)
                        .setAction("取消", this)
                        .show();
                break;
            case R.id.iv_share_qq:
                Log.e(TAG, "onClick: 分享到QQ");
                //弹出一个提示.....
                Snackbar.make(mRootView, "分享到QQ成功", Snackbar.LENGTH_LONG)
                        .setAction("取消", this)
                        .show();
                break;
            case R.id.iv_share_qzone:
                Log.e(TAG, "onClick: 分享到QQ空间");
                //弹出一个提示.....
                Snackbar.make(mRootView, "分享到QQ空间成功", Snackbar.LENGTH_LONG)
                        .setAction("取消", this)
                        .show();
                break;
            case R.id.iv_share_vblog:
                Log.e(TAG, "onClick: 分享到新浪");
                //弹出一个提示.....
                Snackbar.make(mRootView, "分享到新浪成功", Snackbar.LENGTH_LONG)
                        .setAction("取消", this)
                        .show();
                break;
            case R.id.iv_share_cancel:
                Log.e(TAG, "onClick: 取消");
                break;
            default:
                Toast.makeText(context, "呵呵,你以为取消有用吗?", Toast.LENGTH_SHORT).show();
                break;
        }

        //关闭该分享PopupWindow
        mPopupWindow.dismiss();
    }
}
