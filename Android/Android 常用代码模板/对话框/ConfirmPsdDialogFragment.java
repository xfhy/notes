package com.xfhy.mobilesafe.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.xfhy.mobilesafe.R;
import com.xfhy.mobilesafe.listener.ConfirmPsdInputListener;
import com.xfhy.mobilesafe.utils.ToastUtil;

/**
 * Created by xfhy on 2017/4/16.
 * 手机防盗模块第一次进入时需要设置密码的对话框
 */

public class ConfirmPsdDialogFragment extends DialogFragment {

    /**
     * 确认密码输入框
     */
    private EditText mConfirmPsd;
    /**
     * 确认按钮
     */
    private Button mSubmitPsd;
    /**
     * 取消按钮
     */
    private Button mCancelPsd;
    /**
     * 对话框的监听器    确认和取消
     */
    private ConfirmPsdInputListener mPsdInputListener = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);   //设置为无标题

        //加载对话框的布局
        View view = inflater.inflate(R.layout.dialog_confirm_psd, container);

        mConfirmPsd = (EditText) view.findViewById(R.id.et_confirm_psd);
        mSubmitPsd = (Button) view.findViewById(R.id.bt_submit_psd);
        mCancelPsd = (Button) view.findViewById(R.id.bt_cancel_psd);

        /**
         * 确认按钮监听器
         */
        mSubmitPsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的数据
                String confirmPassword = mConfirmPsd.getText().toString().trim();

                if (TextUtils.isEmpty(confirmPassword)) {
                    ToastUtil.show("密码不能为空哦~");
                    return;
                } else {
                    //如果用户输入的密码不为空 则将用户输入的数据  送回调用者处
                    if (mPsdInputListener != null) {
                        //回调确认输入的接口函数
                        mPsdInputListener.confirmInputPsd(confirmPassword);
                    }
                }

            }
        });

        /**
         * 取消按钮监听器
         */
        mCancelPsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPsdInputListener != null) {
                    //回调取消输入的接口函数
                    mPsdInputListener.cancelInputPsd();
                    ConfirmPsdDialogFragment.this.dismiss();  //隐藏对话框
                }
            }
        });

        return view;
    }

    /**
     * 设置对话框监听器
     *
     * @param listener
     */
    public void setOnConfirmPsdInputListener(ConfirmPsdInputListener listener) {
        this.mPsdInputListener = listener;
    }

}
