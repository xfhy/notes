package com.xfhy.recyclerviewrefresh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by xfhy on 2017/7/10.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    /**
     * 用来存放该子项上的所有的子View
     */
    private SparseArray<View> mViews;
    /**
     * 该子项的布局
     */
    private View mConvertView;
    private Context mContext;


    public ViewHolder(Context mContext, View itemView) {
        super(itemView);
        this.mContext = mContext;
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    /**
     * 构建ViewHolder
     *
     * @param context
     * @param itemView 布局
     * @return
     */
    public static ViewHolder createViewHolder(Context context, View itemView) {
        ViewHolder holder = new ViewHolder(context, itemView);
        return holder;
    }

    /**
     * 构建ViewHolder
     *
     * @param context
     * @param parent   ViewGroup  parent
     * @param layoutId 布局id
     * @return
     */
    public static ViewHolder createViewHolder(Context context,
                                              ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        ViewHolder holder = new ViewHolder(context, itemView);
        return holder;
    }

    /**
     * 通过id获取该View
     *
     * @param viewId
     * @param <T>
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
     * 获取该子项布局
     *
     * @return
     */
    public View getConvertView() {
        return mConvertView;
    }


    /*---------------以下为辅助方法--------------*/
    public ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

}
