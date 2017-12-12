package com.na517.hotel.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.na517.hotel.R;

/**
 * Created by feiyang on 2017/12/4 12:31
 * Description : 带删除按钮的自定义布局(FrameLayout)
 */
public class EditTextWithDelete extends FrameLayout implements View.OnClickListener, TextWatcher,
        TextView.OnEditorActionListener, View.OnFocusChangeListener {

    private EditText mInputKeyEt;
    private ImageView mDeleteIv;
    private OnFocusEnterListener mOnFocusEnterListener;

    public EditTextWithDelete(Context context) {
        this(context, null);
    }

    public EditTextWithDelete(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextWithDelete(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.hotel_layout_edittext_with_del, this,
                true);
        mInputKeyEt = findViewById(R.id.et_input_search_key);
        mDeleteIv = findViewById(R.id.iv_input_search_key_del);

        mDeleteIv.setOnClickListener(this);
        mInputKeyEt.addTextChangedListener(this);
        mInputKeyEt.setOnEditorActionListener(this);
        mInputKeyEt.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.iv_input_search_key_del) {
            //删除按钮
            mInputKeyEt.setText("");
        }
    }

    /**
     * 设置文本内容
     */
    public void setText(String text) {
        mInputKeyEt.setText(text);
    }

    /**
     * 清空输入的值
     */
    public void clearText() {
        mInputKeyEt.setText("");
    }

    /**
     * 获取输入的内容
     */
    public String getText() {
        return mInputKeyEt.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String inputText = mInputKeyEt.getText().toString();
        if (inputText.length() > 0) {
            mDeleteIv.setVisibility(VISIBLE);
        } else {
            mDeleteIv.setVisibility(GONE);
        }
        mOnFocusEnterListener.onTextChange(mInputKeyEt.getText().toString());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //当actionId == XX_SEND 或者 XX_DONE时都触发
        //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
        //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent
                .ACTION_DOWN == event.getAction())) {
            //处理事件  回调用户按下enter键时EditText上已输入的值
            if (mOnFocusEnterListener != null) {
                mOnFocusEnterListener.onEnterClick(mInputKeyEt.getText().toString());
            }
            return true;
        }
        return false;
    }

    /**
     * 设置监听器  用于监听用户在虚拟键盘上按下enter键
     *
     * @param onFocusEnterListener 监听器
     */
    public void setOnFocusEnterListener(OnFocusEnterListener onFocusEnterListener) {
        this.mOnFocusEnterListener = onFocusEnterListener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mOnFocusEnterListener != null) {
            mOnFocusEnterListener.onTopBarFocusChange(v, hasFocus);
        }
    }

    public void clearFocus() {
        mInputKeyEt.clearFocus();
    }

    /**
     * 监听用户按下enter键,监听EditText获取焦点
     */
    public interface OnFocusEnterListener {
        /**
         * 回调用户按下enter键时EditText上已输入的值
         *
         * @param key EditText上已输入的值
         */
        void onEnterClick(String key);

        /**
         * EditText焦点发生变化
         *
         * @param view     焦点发生变化的view
         * @param hasFocus true:有焦点  false:无焦点
         */
        void onTopBarFocusChange(View view, boolean hasFocus);

        /**
         * 输入的文字发生改变
         */
        void onTextChange(String textContent);

    }

}
