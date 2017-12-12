# 自定义控件:带删除按钮的EditText

> 一个带删除按钮的EditText.虽然比较简单,但还是记录一下.

## 自定义布局xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
             android:id="@+id/fl_edittext_with_del"
             android:layout_width="match_parent"
             android:layout_height="match_parent"
             android:focusable="true"
             android:focusableInTouchMode="true"
             android:background="@drawable/shape_e9e9e9">

    <!--带删除按钮的EditText布局-->

    <EditText
        android:id="@+id/et_input_search_key"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center_vertical"
        android:layout_marginEnd="@dimen/dimen_35"
        android:background="@null"
        android:drawablePadding="@dimen/dimen_5"
        android:drawableStart="@drawable/icon_address_search_img_test"
        android:ellipsize="end"
        android:gravity="center_vertical"
        android:hint="关键字/位置/品牌/酒店名"
        android:imeOptions="actionSearch"
        android:inputType="text"
        android:maxLines="1"
        android:paddingStart="@dimen/dimen_10"
        android:textColor="@color/color_1e1e1e"
        android:textColorHint="@color/color_949494"
        android:textCursorDrawable="@null"
        android:textSize="@dimen/font_size_14"/>

    <!--删除按钮-->
    <ImageView
        android:id="@+id/iv_input_search_key_del"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical|end"
        android:layout_marginEnd="@dimen/dimen_10"
        android:layout_marginStart="@dimen/dimen_10"
        android:scaleType="centerCrop"
        android:src="@drawable/icon_delete_img_test"
        android:visibility="gone"/>

</FrameLayout>
```
里面有一些颜色啊,shape啊什么的,暂时就不拿出来了,比较多,那些也不影响大局.

## 创建自定义EditTextWithDelete控件,继承FrameLayout

将布局加载进来,初始化
```java
LayoutInflater.from(getContext()).inflate(R.layout.hotel_layout_edittext_with_del, this,
                true);
mInputKeyEt = findViewById(R.id.et_input_search_key);
mDeleteIv = findViewById(R.id.iv_input_search_key_del);
```
## 添加点击事件
```java
//删除按钮点击事件
mDeleteIv.setOnClickListener(this);
//用户输入的内容变化事件
mInputKeyEt.addTextChangedListener(this);
//监听软键盘按下回车事件
mInputKeyEt.setOnEditorActionListener(this);
//EditText焦点变化事件
mInputKeyEt.setOnFocusChangeListener(this);
```

## 对监听事件处理
```java

//删除按钮点击事件
@Override
public void onClick(View v) {
    int viewId = v.getId();
    if (viewId == R.id.iv_input_search_key_del) {
        //删除按钮
        mInputKeyEt.setText("");
    }
}

//输入内容变化之后
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

//用户按下回车键
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

```

## 定义控件内容的一些事件可供外部监听

外部可能会对该控件的一些状态,事件比较感兴趣,可以监听一下,得到数据和状态.

```java

/**
* 设置监听器  用于监听控件的一些事件
*
* @param onEditTextListener 监听器
*/
public void setOnEditTextListener(OnEditTextListener onEditTextListener) {
    this.mOnEditTextListener = onEditTextListener;
}

/**
* 监听用户按下enter键,监听EditText获取焦点
*/
public interface OnEditTextListener {
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
    *
    * @param textContent 输入的内容
    */
    void onTextChange(String textContent);

}
```

## 提供一些其他方法给外部
```java
/**
* 清除焦点
*/
public void clearFocus() {
    mInputKeyEt.clearFocus();
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

```

## 完整代码

```java
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

/**
 * Created by feiyang on 2017/12/4 12:31
 * Description : 带删除按钮的自定义布局(FrameLayout)
 */
public class EditTextWithDelete extends FrameLayout implements View.OnClickListener, TextWatcher,
        TextView.OnEditorActionListener, View.OnFocusChangeListener {

    private EditText mInputKeyEt;
    private ImageView mDeleteIv;
    private OnEditTextListener mOnEditTextListener;

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
        mOnEditTextListener.onTextChange(mInputKeyEt.getText().toString());
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
            if (mOnEditTextListener != null) {
                mOnEditTextListener.onEnterClick(mInputKeyEt.getText().toString());
            }
            return true;
        }
        return false;
    }

    /**
     * 设置监听器  用于监听控件的一些事件
     *
     * @param onEditTextListener 监听器
     */
    public void setOnEditTextListener(OnEditTextListener onEditTextListener) {
        this.mOnEditTextListener = onEditTextListener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mOnEditTextListener != null) {
            mOnEditTextListener.onTopBarFocusChange(v, hasFocus);
        }
    }

    /**
     * 清除焦点
     */
    public void clearFocus() {
        mInputKeyEt.clearFocus();
    }

    /**
     * 监听用户按下enter键,监听EditText获取焦点
     */
    public interface OnEditTextListener {
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
         *
         * @param textContent 输入的内容
         */
        void onTextChange(String textContent);

    }

}

```

## 总结

虽然比较简单,但是每次都去重复的写的话,还是比较麻烦,记录一波.有需要的也可以参考参考.
