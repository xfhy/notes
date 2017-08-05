package com.a517na.feiyang.edittexttest.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.a517na.feiyang.edittexttest.R;

/**
 * description：
 * author feiyang
 * create at 2017/8/1 17:04
 */
public class EditTextWithDe extends EditText {
    private static final String TAG = "EditTextWithDe";

    private Context mContext;
    private Drawable imgDrawable;

    public EditTextWithDe(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EditTextWithDe(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public EditTextWithDe(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        imgDrawable = mContext.getResources().getDrawable(R.drawable.delete);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setDrawable();
            }
        });
        setDrawable();
    }

    /**
     * 设置图标
     */
    private void setDrawable() {
        if (length() < 1) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgDrawable, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgDrawable != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            setText("");
        }
        return super.onTouchEvent(event);
    }
}
