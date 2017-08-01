
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源为Object的Adapter基类   ListView的基类adapter
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {
    
    protected Context mContext;
    
    protected LayoutInflater mInflater;
    
    protected List<T> mDatas = new ArrayList<T>();
    
    protected int mLayoutId;
    
    public BaseListAdapter(Context context, List<T> datas, int layoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mLayoutId = layoutId;
        if (datas != null) {
            mDatas = datas;
        }
    }
    
    public void notityAdapter(List<T> mLists) {
        mDatas = mLists;
        notifyDataSetChanged();
    }
    
    public void addItem(T t) {
        mDatas.add(t);
        notifyDataSetChanged();
    }
    
    public void removeItem(T t) {
        if (mDatas.contains(t)) {
            mDatas.remove(t);
        }
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }
    
    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder holder = BaseViewHolder.getInstance(mContext, convertView, parent,
                mLayoutId, position);
        getView(holder, getItem(position));
        getView(holder, getItem(position), position);
        return holder.getConvertView();
    }
    
    public abstract void getView(BaseViewHolder holder, T t);
    
    public void getView(BaseViewHolder holder, T t, int position) {
    }
    
    public List<T> getDatas() {
        return mDatas;
    }
}
