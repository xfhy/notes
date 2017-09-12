package com.feiyang.complexlistlayout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.feiyang.complexlistlayout.holder.EmptyViewHolder;
import com.feiyang.complexlistlayout.holder.FooterHolder;

/**
 * description：分组适配器的基类
 * author xfhy
 * create at 2017/8/10 14:19
 */
public abstract class SectionedRecyclerViewAdapter<RH extends RecyclerView.ViewHolder, H extends
        RecyclerView.ViewHolder, VH extends RecyclerView.ViewHolder, F extends RecyclerView
        .ViewHolder, FO extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView
        .ViewHolder> {

    //RH 是整个列表的Header
    //H 是分组的header
    //VH 是分组内的item
    //F 是分组内的footer
    //FO 是整个列表的footer

    /**
     * 每个分组的header
     */
    protected static final int TYPE_SECTION_HEADER = -1;
    /**
     * 每个分组的footer
     */
    protected static final int TYPE_SECTION_FOOTER = -2;
    /**
     * 每个分组的内容
     */
    protected static final int TYPE_ITEM = -3;
    /**
     * 整个列表的header
     */
    protected static final int TYPE_HEADER = 0;
    /**
     * 整个列表的footer
     */
    protected static final int TYPE_FOOTER = 1;

    /**
     * 上拉加载更多
     */
    public static final int PULLUP_LOAD_MORE = 0;
    /**
     * 正在加载中
     */
    public static final int LOADING_MORE = 1;
    /**
     * 加载完成
     */
    public static final int LOADING_FINISH = 2;

    /**
     * 空类型
     */
    public static final int TYPE_EMPTY = -4;

    /**
     * 上拉加载默认状态,默认为-1
     */
    public int load_more_status = -1;

    /**
     * 用来保存分组section位置 那个索引处属于哪个组 元素个数为item的总数(未包含整个列表的header和footer)
     */
    private int[] sectionForPosition = null;
    /**
     * 用来保存分组内的每项的position位置 那个索引处在组内的相对位置(比如是组内的第2个,那么positionWithInSection[index]=1) 元素个数为item的总数
     * (未包含整个列表的header和footer)
     */
    private int[] positionWithInSection = null;
    /**
     * 用来记录每个位置是否是一个组内Header  元素个数为item的总数(未包含整个列表的header和footer)
     */
    private boolean[] isHeader = null;
    /**
     * 用来记录每个位置是否是一个组内Footer  元素个数为item的总数(未包含整个列表的header和footer)
     */
    private boolean[] isFooter = null;

    /**
     * item的总数  注意,是总数,包含所有项
     */
    private int count = 0;

    //以下接口对应各个item的点击事件   可以在自己的adapter中在适当的位置调用
    protected OnChildClickListener onChildClickListener;
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;
    protected OnSectionHeaderClickListener onSectionHeaderClickListener;
    protected OnSectionFooterClickListener onSectionFooterClickListener;

    /**
     * 空布局
     */
    private View emptyView;
    /**
     * 空布局是否可见
     */
    private boolean emptyViewVisible;

    /**
     * 构造方法
     */
    public SectionedRecyclerViewAdapter() {
        super();

        //RecyclerView采用观察者(Observer)模式,
        // 对外提供了registerDataSetObserver和unregisterDataSetObserver两个方法,用来监控数据集的变化
        registerAdapterDataObserver(new SectionDataObserver());
    }

    /**
     * 定义一个内部类,每当数据集合发生改变时,设置控件的位置信息
     */
    private class SectionDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            setupPosition();
            checkEmpty();  //检查数据是否为空,设置空布局
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkEmpty();//检查数据是否为空,设置空布局
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkEmpty();//检查数据是否为空,设置空布局
        }
    }

    /**
     * 检查数据是否为空,设置空布局
     */
    private void checkEmpty() {
        if (emptyView != null) {
            //判断整个列表是否有头布局
            if (hasHeader()) {
                emptyViewVisible = getItemCount() == 2;
            } else {
                emptyViewVisible = getItemCount() == 1;
            }
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 返回item总数（包含顶部Header、底部Footer、分组header和分组footer以及分组item内容）
     */
    @Override
    public int getItemCount() {
        //如果有头布局 则 +2:加了整个列表的header和footer
        if (hasHeader()) {
            return count + 2;
        } else {
            //如果没有头布局 则+1:加了整个列表的footer
            return count + 1;
        }
    }

    /**
     * 初始化那些位置信息等
     */
    private void setupPosition() {
        count = countItems();  //计算出item的总数量

        //得到item的总数量后,初始化几个数组:初始化与position相对于的section数组,初始化与section相对于的position的数组,
        // 初始化当前位置是否是一个Header的数组，初始化当前位置是否是一个Footer的数组
        setupArrays(count);

        //通过计算每个item的位置信息，将上一步初始化后的数组填充数据，最终这几个数组保存了每个位置的item
        // 的状态信息，即：是否是header，是否是footer，所在的position是多少，所在的section是多少
        calculatePositions();
    }

    private int countItems() {
        int count = 0;

        //获取组的数量
        int sections = getSectionCount();

        for (int i = 0; i < sections; i++) {
            count += 1;  //组头
            count += getItemCountForSection(i); //那个组内的item个数
            count += (hasFooterInSection(i) ? 1 : 0); //首先判断那个组内是否有footer,如果有则+1,没有则不加
        }
        return count;
    }

    /**
     * 通过item的总数量，初始化几个数组:初始化与position相对应的section数组，
     * 初始化与section相对应的position的数组，初始化当前位置是否是一个Header的数组，
     * 初始化当前位置是否是一个Footer的数组
     *
     * @param count item的总数
     */
    private void setupArrays(int count) {
        sectionForPosition = new int[count];
        positionWithInSection = new int[count];
        isHeader = new boolean[count];
        isFooter = new boolean[count];
    }

    /**
     * 通过计算每个item的位置信息，将上一步初始化后的数组填充数据，
     * 最终这几个数组保存了每个位置的item的状态信息，即：是否是header，是否是footer，
     * 所在的position是多少，所在的section是多少
     */
    private void calculatePositions() {
        //获取分组的个数
        int sections = getSectionCount();
        int index = 0;

        //
        for (int i = 0; i < sections; i++) {
            //每个组的组头  肯定是header咯
            setupItems(index, true, false, i, 0);
            index++;

            //组内的每个item
            for (int j = 0; j < getItemCountForSection(i); j++) {
                setupItems(index, false, false, i, j);
                index++;
            }

            //判断是否含有 footer
            if (hasFooterInSection(i)) {
                //footer的位置(position)无所谓啦
                setupItems(index, false, true, i, 0);
                index++;
            }

        }

    }

    /**
     * 保存/设置 每个位置的数据信息
     *
     * @param index    每个item对于的索引(包括header和footer等) 从0开始的每个最小单位所在的位置，从0开始，到count结束
     * @param isHeader 所在index位置的item是否是header
     * @param isFooter 所在index位置的item是否是footer
     * @param section  所在index位置的item对应的section(所在index位置的item对应的组的位置)
     * @param position 所在index位置的item对应的position
     */
    private void setupItems(int index, boolean isHeader, boolean isFooter, int section, int
            position) {
        this.isHeader[index] = isHeader;
        this.isFooter[index] = isFooter;
        sectionForPosition[index] = section;
        positionWithInSection[index] = position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_EMPTY) {
            viewHolder = new EmptyViewHolder(emptyView);
        } else {
            if (isSectionHeaderViewType(viewType)) {
                //类型是组头  组header布局
                viewHolder = onCreateSectionHeaderViewHolder(parent, viewType);
            } else if (isSectionFooterViewType(viewType)) {
                //类型是组footer
                viewHolder = onCreateSectionFooterViewHolder(parent, viewType);
            } else if (isHeaderViewType(viewType)) {
                //如果是整个列表的头布局 header
                viewHolder = onCreateHeaderViewHolder(parent, viewType);
            } else if (isFooterViewType(viewType)) {
                //如果是整个列表的尾布局
                viewHolder = onCreateFooterViewHolder(parent, viewType);
            } else {
                //如果是组内的某个布局
                viewHolder = onCreateItemViewHolder(parent, viewType);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (emptyViewVisible) {
            //此时数据集为空,需要设置空布局
        } else {
            setViewHolder(holder, position);
        }
    }

    private void setViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (hasHeader()) {  //如果整个列表有header
            if (position == 0) {
                //这是整个列表的header
                onBindHeaderViewHolder((RH) holder);
            } else if (position + 1 < getItemCount()) {
                //这是列表中间的东西

                //这是当前位置的分组位置
                // 这里-1是因为:sectionForPosition的总数没有算列表的header和footer,所以这里需要-1,因为前面是有列表的header
                final int groupPosition = sectionForPosition[position - 1];
                //这是当前位置的相对于组内的位置
                final int childPosition = positionWithInSection[position - 1];
                if (isSectionHeaderPosition(position - 1)) {
                    //如果当前的索引处是某个组的header
                    //那么去绑定组头header的数据
                    onBindSectionHeaderViewHolder((H) holder, groupPosition);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSectionHeaderClickListener.onSectionHeaderClick(groupPosition);
                        }
                    });
                } else if (isSectionFooterPosition(position - 1)) {
                    //如果当前的索引处是某个组的footer
                    //那么去绑定组尾footer的数据
                    onBindSectionFooterViewHolder((F) holder, groupPosition);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSectionFooterClickListener.onSectionFooterClick(groupPosition);
                        }
                    });
                } else {
                    //如果当前的索引处是组内的item
                    //那么去绑定这个组内item的数据
                    onBindItemViewHolder((VH) holder, groupPosition, childPosition);

                    //设置item点击事件
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //通过接口回调
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(groupPosition, position - 1);
                            }
                        }
                    });

                    //设置item长按事件
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (onItemLongClickListener != null) {
                                onItemLongClickListener.onItemLongClick(groupPosition, position -
                                        1);
                            }
                            return true;
                        }
                    });

                }
            } else {
                //当前位置是整个列表的footer
                onBindFooterViewHolder((FO) holder);
            }
        } else {//整个列表没有Header
            if (position + 1 < getItemCount()) {
                final int section = sectionForPosition[position];
                int index = positionWithInSection[position];
                if (isSectionHeaderPosition(position)) {//当前位置是分组Header
                    onBindSectionHeaderViewHolder((H) holder, section);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSectionHeaderClickListener != null) {
                                onSectionHeaderClickListener.onSectionHeaderClick(section);
                            }
                        }
                    });

                } else if (isSectionFooterPosition(position)) {//当前位置是分组footer

                    onBindSectionFooterViewHolder((F) holder, section);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSectionFooterClickListener != null) {
                                onSectionFooterClickListener.onSectionFooterClick(section);
                            }
                        }
                    });

                } else {//当前位置是分组的item
                    onBindItemViewHolder((VH) holder, section, index);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(section, position);
                            }
                        }
                    });

                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (onItemLongClickListener != null) {
                                onItemLongClickListener.onItemLongClick(section, position);
                            }
                            return true;
                        }
                    });

                }
            } else {//当前位置是整个列表的footer
                onBindFooterViewHolder((FO) holder);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (sectionForPosition == null) {
            setupPosition();
        }
        if (emptyViewVisible) {
            return TYPE_EMPTY;
        } else {
            if (hasHeader()) {
                if (position == 0) {
                    return getHeaderViewType();
                } else if (position + 1 < getItemCount()) {
                    int section = sectionForPosition[position - 1];
                    int index = positionWithInSection[position - 1];
                    if (isSectionHeaderPosition(position - 1)) {
                        return getSectionHeaderViewType(section);
                    } else if (isSectionFooterPosition(position - 1)) {
                        return getSectionFooterViewType(section);
                    } else {
                        return getSectionItemViewType(section, index);
                    }
                }
                return getFooterViewType();
            } else {
                if (position + 1 < getItemCount()) {
                    int section = sectionForPosition[position];
                    int index = positionWithInSection[position];
                    if (isSectionHeaderPosition(position)) {
                        return getSectionHeaderViewType(section);
                    } else if (isSectionFooterPosition(position)) {
                        return getSectionFooterViewType(section);
                    } else {
                        return getSectionItemViewType(section, index);
                    }
                }
                return getFooterViewType();
            }
        }
    }

    /**
     * 对应位置是否是一个分组header
     */
    public boolean isSectionHeaderPosition(int position) {
        if (isHeader == null) {
            setupPosition();
        }
        return isHeader[position];
    }

    /**
     * 对应位置是否是一个分组footer
     */
    public boolean isSectionFooterPosition(int position) {
        if (isFooter == null) {
            setupPosition();
        }
        return isFooter[position];
    }

    /**
     * 改变当前的列表状态   是否在加载更多
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    /**
     * 获取空布局
     */
    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置空布局
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * 是否是分组header
     *
     */
    protected boolean isSectionHeaderViewType(int viewType) {
        return viewType == TYPE_SECTION_HEADER;
    }

    /**
     * 是否是分组footer
     *
     */
    protected boolean isSectionFooterViewType(int viewType) {
        return viewType == TYPE_SECTION_FOOTER;
    }

    /**
     * 是否是列表的Header
     */
    protected boolean isHeaderViewType(int viewType) {
        return viewType == TYPE_HEADER;
    }

    /**
     * 是否是列表的footer
     *
     */
    protected boolean isFooterViewType(int viewType) {
        return viewType == TYPE_FOOTER;
    }

    protected int getSectionHeaderViewType(int section) {
        return TYPE_SECTION_HEADER;
    }

    protected int getSectionFooterViewType(int section) {
        return TYPE_SECTION_FOOTER;
    }

    protected int getHeaderViewType() {
        return TYPE_HEADER;
    }

    protected int getFooterViewType() {
        return TYPE_FOOTER;
    }

    protected int getSectionItemViewType(int section, int position) {
        return TYPE_ITEM;
    }

    /**
     * 整个列表是否有Header
     */
    protected abstract boolean hasHeader();

    /**
     * 返回分组的数量
     */
    protected abstract int getSectionCount();

    /**
     * 返回当前分组的item数量
     *
     * @param section 组头的位置
     */
    protected abstract int getItemCountForSection(int section);

    /**
     * 当前分组是否有footer
     *
     * @param section 组头的位置
     */
    protected abstract boolean hasFooterInSection(int section);

    /**
     * 为分组header创建一个类型为H的ViewHolder
     */
    protected abstract H onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType);

    /**
     * 为分组footer创建一个类型为F的ViewHolder
     *
     */
    protected abstract F onCreateSectionFooterViewHolder(ViewGroup parent, int viewType);

    /**
     * 为分组内容创建一个类型为VH的ViewHolder
     *
     */
    protected abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

    /**
     * 为整个列表创建一个类型为RH的ViewHolder
     *
     */
    protected abstract RH onCreateHeaderViewHolder(ViewGroup parent, int viewType);

    /**
     * 为整个列表创建一个类型为FO的ViewHolder
     *
     */
    protected abstract FO onCreateFooterViewHolder(ViewGroup parent, int viewType);

    /**
     * 绑定分组的Header数据
     */
    protected abstract void onBindSectionHeaderViewHolder(H holder, int section);

    /**
     * 绑定分组数据
     *
     */
    protected abstract void onBindItemViewHolder(VH holder, int section, int position);

    /**
     * 绑定整个列表的Header数据
     *
     */
    protected abstract void onBindHeaderViewHolder(RH holder);

    /**
     * 绑定分组的footer数据
     *
     */
    protected abstract void onBindSectionFooterViewHolder(F holder, int section);

    /**
     * 绑定上拉加载footer(整个RecyclerView的footer)数据
     *
     */
    protected void onBindFooterViewHolder(FO holder) {
        if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footerHolder.tvFooter.setVisibility(View.VISIBLE);
                    footerHolder.tvFooter.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerHolder.tvFooter.setVisibility(View.VISIBLE);
                    footerHolder.tvFooter.setText("正在加载数据...");
                    break;
                case LOADING_FINISH:
                    footerHolder.tvFooter.setVisibility(View.VISIBLE);
                    footerHolder.tvFooter.setText("没有更多数据");
                    break;
                default:
                    footerHolder.tvFooter.setVisibility(View.GONE);
                    break;
            }
        } else {
            //当footer不是上拉刷新时，复写此方法，如：点击查看更多或者更复杂的布局等
            onBindFooterOtherViewHolder(holder);
        }
    }

    /**
     * 当footer不是上拉刷新时，复写此方法，如：点击查看更多或者更复杂的布局等
     *
     */
    protected abstract void onBindFooterOtherViewHolder(FO holder);

    /**
     * 分组内子View点击事件回调,多了一个viewType,用以区分同一个item的不同的点击事件
     * 根据需求,需要时可以实现此接口
     */
    public interface OnChildClickListener {
        /**
         * @param position 分组内子View点击事件回调
         * @param viewType 点击的view的类型,调用时根据不同的view传入不同的值加以区分,如viewType=0表示进入下一级页面,viewType=1表示查看大图等
         */
        void onChildClick(int position, int viewType);
    }

    /**
     * 分组内的item点击回调
     */
    public interface OnItemClickListener {
        /**
         * 分组内的item点击回调
         *
         * @param section  组的位置
         * @param position item在整个列表中的索引
         */
        void onItemClick(int section, int position);
    }

    /**
     * item长按回调
     */
    public interface OnItemLongClickListener {
        /**
         * item长按回调
         *
         * @param section  组的位置
         * @param position item在整个列表中的索引
         */
        void onItemLongClick(int section, int position);
    }

    /**
     * section的header的点击回调 每个组的组header的点击回调
     */
    public interface OnSectionHeaderClickListener {
        /**
         * section的header的点击回调 每个组的组header的点击回调
         *
         * @param section 组的位置
         */
        void onSectionHeaderClick(int section);
    }

    /**
     * section的Footer的点击回调 每个组的组footer的点击回调
     */
    public interface OnSectionFooterClickListener {
        /**
         * section的Footer的点击回调 每个组的组footer的点击回调
         *
         * @param section 组的位置
         */
        void onSectionFooterClick(int section);
    }

    /**
     * 设置分组内子View点击事件回调
     *
     */
    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    /**
     * 设置分组内的item点击回调
     *
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * item长按回调
     *
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * section的Header的点击回调
     *
     */
    public void setOnSectionHeaderClickListener(OnSectionHeaderClickListener
                                                        onSectionHeaderClickListener) {
        this.onSectionHeaderClickListener = onSectionHeaderClickListener;
    }

    /**
     * section的Footer的点击回调
     *
     */
    public void setOnSectionFooterClickListener(OnSectionFooterClickListener
                                                        onSectionFooterClickListener) {
        this.onSectionFooterClickListener = onSectionFooterClickListener;
    }


}
