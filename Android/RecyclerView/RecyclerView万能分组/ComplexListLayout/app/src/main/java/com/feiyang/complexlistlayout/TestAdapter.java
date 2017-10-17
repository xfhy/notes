package com.feiyang.complexlistlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.feiyang.complexlistlayout.adapter.SectionedRecyclerViewAdapter;
import com.feiyang.complexlistlayout.entity.TestEntity;
import com.feiyang.complexlistlayout.holder.FooterHolder;
import com.feiyang.complexlistlayout.holder.TestSectionBodyHolder;
import com.feiyang.complexlistlayout.holder.TestSectionFooterHolder;
import com.feiyang.complexlistlayout.holder.TestSectionHeaderHolder;
import com.feiyang.complexlistlayout.util.DeviceInforUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * package: com.easyandroid.sectionadapter.TestAdapter
 * author: gyc
 * description:
 * time: create at 2017/7/8 2:59
 */

public class TestAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder,
        TestSectionHeaderHolder, TestSectionBodyHolder,
        TestSectionFooterHolder, FooterHolder> {

    private List<TestEntity.BodyBean.EListBean> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    public TestAdapter(List<TestEntity.BodyBean.EListBean> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<TestEntity.BodyBean.EListBean> mDatas) {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        if (this.mDatas == null) {
            this.mDatas = new ArrayList<>();
        }
        this.mDatas.clear();
        this.mDatas.addAll(mDatas);
        notifyDataSetChanged();
    }

    public List<TestEntity.BodyBean.EListBean> getData() {
        return mDatas;
    }

    public void addMoreData(List<TestEntity.BodyBean.EListBean> newDatas) {
        if (newDatas == null || newDatas.size() == 0) {
            return;
        }
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.addAll(0, newDatas);
        notifyDataSetChanged();
    }

    @Override
    protected boolean hasHeader() {
        return false;
    }

    @Override
    protected int getSectionCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    protected int getItemCountForSection(int section) {
        return mDatas.get(section).getEPicture() == null ? 0 : mDatas.get(section)
                .getEPicture().size();

    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return true;
    }

    @Override
    protected TestSectionHeaderHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int
            viewType) {
        TestSectionHeaderHolder testSectionHeaderHolder = new TestSectionHeaderHolder(mInflater
                .inflate(R.layout
                        .item_section_header, parent, false));
        return testSectionHeaderHolder;
    }

    @Override
    protected TestSectionFooterHolder onCreateSectionFooterViewHolder(ViewGroup parent, int
            viewType) {
        return new TestSectionFooterHolder(mInflater.inflate(R.layout
                .item_section_footer, parent, false));

    }

    @Override
    protected TestSectionBodyHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new TestSectionBodyHolder(mInflater.inflate(R.layout.item_section_body,
                parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected FooterHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FooterHolder(mInflater.inflate(R.layout.layout_footer, parent, false));
    }

    @Override
    protected void onBindSectionHeaderViewHolder(TestSectionHeaderHolder holder, final int
            section) {
        Glide.with(mContext).load(mDatas.get(section).getPicture()).into(holder.imgHead);
        holder.imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChildClickListener.onChildClick(section, 0);
            }
        });
        holder.tvNike.setText(mDatas.get(section).getUserName());
        holder.tvDate.setText(mDatas.get(section).getTime());
        holder.tvEvaluate.setText(mDatas.get(section).getContent());
    }

    @Override
    protected void onBindItemViewHolder(TestSectionBodyHolder holder, final int section, final
    int position) {
        DisplayMetrics screenInfor = DeviceInforUtils.getScreenInfor(mContext);
        int screenWidth = screenInfor.widthPixels;
        int imgWidth = (screenWidth - 85) / 3;
        ViewGroup.MarginLayoutParams params = null;
        if (holder.llRoot.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            params = (ViewGroup.MarginLayoutParams) holder.llRoot.getLayoutParams();
        } else {
            params = new ViewGroup.MarginLayoutParams(holder.llRoot.getLayoutParams());
        }
        params.width = imgWidth;
        params.height = imgWidth;

        //这里左右边距不相同，左边距与评论文字相同，加上头像的大小，为55dp，左边距为55dp，右边距为10dp，图片间距为10dp
        if (position % 3 == 0) {
            params.leftMargin = 55;
        } else if (position % 3 == 1) {
            params.leftMargin = 35;
        } else {
            params.leftMargin = 14;
        }
        params.bottomMargin = 8;
        holder.llRoot.setLayoutParams(params);
        Glide.with(mContext).load(mDatas.get(section).getEPicture().get(position)).into(holder
                .imgEvaluate);

        holder.imgEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(section, position);
            }
        });
        holder.imgEvaluate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onItemLongClick(section, position);
                return true;
            }
        });
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {

    }

    @Override
    protected void onBindSectionFooterViewHolder(TestSectionFooterHolder holder, int section) {
        holder.tvLookNum.setText(mContext.getString(R.string.item_section_footer, mDatas.get
                (section).getBrowser()));
    }

    @Override
    protected void onBindFooterOtherViewHolder(FooterHolder holder) {

    }


}
