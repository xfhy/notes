
import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.na517.project.library.widget.CircularImage;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseViewHolder {
    
    private final SparseArray<View> mViews;
    
    private   int mPosition;
    
    private View mConvertView;
    
    private BaseViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        // setTag
        mConvertView.setTag(this);
    }
    
    /**
     * 拿到一个ViewHolder对象
     * 
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static BaseViewHolder getInstance(Context context,
                                             View convertView,
                                             ViewGroup parent,
                                             int layoutId,
                                             int position) {
        if (convertView == null) {
            return new BaseViewHolder(context, parent, layoutId, position);
        }
        else {
            ((BaseViewHolder) convertView.getTag()).mPosition = position;
            return (BaseViewHolder) convertView.getTag();
        }

    }
    
    public View getConvertView() {
        return mConvertView;
    }
    
    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     * 
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
    
    /**
     * 为TextView设置字符串
     * 
     * @param viewId
     * @param text
     * @return
     */
    public BaseViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }
    
    /**
     * 为ImageView设置图片
     * 
     * @param viewId
     * @param drawableId
     * @return
     */
    public BaseViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        
        return this;
    }
    
    /**
     * View设置背景
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public BaseViewHolder setBackgroudResource(int viewId, int drawableId) {
        getView(viewId).setBackgroundResource(drawableId);
        
        return this;
    }
    
    /**
     * @param viewId
     * @param color
     * @return
     */
    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        ImageView imageView = getView(viewId);
        imageView.setBackgroundColor(color);
        return this;
    }
    
    /**
     * 为ImageView设置图片
     * 
     * @param viewId
     * @return
     */
    public BaseViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }
    
    public BaseViewHolder setHeadImageView(int viewId, String imageUrl) {
        CircularImage view = getView(viewId);
        ImageLoader.getInstance().displayImage(imageUrl, view);
        return this;
    }
    
    public int getPosition() {
        return mPosition;
    }
    
    /**
     * 设置字体着色
     * 
     * @param res
     * @return
     */
    public BaseViewHolder setTextColor(int viewId, int res) {
        TextView textView = getView(viewId);
        textView.setTextColor(res);
        return this;
    }
    
    /**
     * 设置textView值
     * 
     * @param viewId
     * @param spnStr
     * @return
     */
    public BaseViewHolder setText(int viewId, SpannableString spnStr) {
        TextView textView = getView(viewId);
        textView.setText(spnStr);
        return this;
    }
    
    public BaseViewHolder setText(int viewId, SpannableStringBuilder spnStr) {
        TextView textView = getView(viewId);
        textView.setText(spnStr);
        return this;
    }
    
    public BaseViewHolder setVisibility(int viewId, int visible) {
        getView(viewId).setVisibility(visible);
        return this;
    }
    
    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        getView(viewId).setOnClickListener(listener);
        return this;
    }
    
}
