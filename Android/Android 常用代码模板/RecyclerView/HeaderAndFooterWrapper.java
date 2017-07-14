package com.xfhy.recyclerviewrefresh.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xfhy on 2017/7/10.
 * 该adapter可以在不修改原始adapter的基础上添加头布局和尾布局
 */

public class HeaderAndFooterWrapper<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    /**
     * 此集合用来盛放头布局
     * 此集合有点儿类似于Map,但是里面的key只是int类型的,将int映射到对象时比HashMap更快
     */
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    /**
     * 适配器
     */
    private RecyclerView.Adapter mInnerAdapter;

    /**
     * 构造函数
     *
     * @param mInnerAdapter 将适配器传入
     */
    public HeaderAndFooterWrapper(RecyclerView.Adapter mInnerAdapter) {
        this.mInnerAdapter = mInnerAdapter;
    }

    /**
     * 判断该位置是否是头布局
     *
     * @param position 索引
     * @return
     */
    private boolean isHeaderViewPos(int position) {
        return position < mHeaderViews.size();
    }

    /**
     * 判断该位置是否是尾布局
     *
     * @param position
     * @return
     */
    private boolean isFootViewPos(int position) {
        return position >= (mHeaderViews.size() + getRealItemCount());
    }

    /**
     * 添加头布局
     *
     * @param view
     */
    public void addHeaderView(View view) {
        //将头布局添加到集合中,key就是集合的大小加上type的值,这样可以避免key重复,value是该布局
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    /**
     * 添加尾布局
     *
     * @param view
     */
    public void addFootView(View view) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    /**
     * 获取头布局的个数
     *
     * @return
     */
    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    /**
     * 获取尾布局的个数
     *
     * @return
     */
    public int getFootersCount() {
        return mFootViews.size();
    }

    private int getRealItemCount() {
        return mInnerAdapter.getItemCount();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //判断该type是否是头布局
        if (mHeaderViews.get(viewType) != null) {
            //mHeaderViews.get(viewType)获取该type的布局
            ViewHolder holder = ViewHolder.createViewHolder(parent.getContext(), mHeaderViews
                    .get(viewType));
            return holder;
        } else if (mFootViews.get(viewType) != null) {
            //判断该type是否是尾布局
            ViewHolder holder = ViewHolder.createViewHolder(parent.getContext(), mFootViews
                    .get(viewType));
            return holder;
        }

        //如果不是头布局和尾布局,则是正常布局,则交给adapter去处理
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFootViewPos(position)) {
            return;
        }

        mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemViewType(int position) {
        //判断是否是头尾部布局
        if (isHeaderViewPos(position)) {
            //返回该位置的key  即 type
            return mHeaderViews.keyAt(position);
        } else if (isFootViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }

        //正常位置   记得减去header的个数
        return mInnerAdapter.getItemViewType(position - getHeadersCount());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getRealItemCount() + getFootersCount();
    }
}
