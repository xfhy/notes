package com.feiyang.complexlistlayout;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.feiyang.complexlistlayout.adapter.SectionedRecyclerViewAdapter;
import com.feiyang.complexlistlayout.adapter.SectionedSpanSizeLookup;
import com.feiyang.complexlistlayout.entity.TestEntity;
import com.feiyang.complexlistlayout.listener.LoadMoreListener;
import com.feiyang.complexlistlayout.util.DatasUtil;
import com.feiyang.complexlistlayout.widgets.SectionedGridDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现复杂列表
 *
 * @author xfhy
 *         create at 2017/8/10 13:37
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout
        .OnRefreshListener {

    private static final String TAG = "MainActivity";
    private TestAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private SectionedGridDivider mDivider;
    private List<TestEntity.BodyBean.EListBean> mDatas = new ArrayList<>();
    private boolean isPull = true;//是否下拉刷新

    private LoadMoreListener loadMoreListener;
    private RecyclerView rv;
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        initAdapter();
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip_root);

    }

    private void initAdapter() {
        mAdapter = new TestAdapter(mDatas, this);

        //设置一个Child的点击事件
        mAdapter.setOnChildClickListener(new SectionedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(int position, int viewType) {
                Toast.makeText(MainActivity.this, "点击头像位置:" + position, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onChildClick: position:" + position + "   viewType:" + viewType);
            }
        });

        mAdapter.setOnItemClickListener(new SectionedRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int section, int position) {
                Toast.makeText(MainActivity.this, "点击美女的位置为:" + position, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onItemClick: section:"+section+"  position:"+position);
            }
        });

        mAdapter.setOnItemLongClickListener(new SectionedRecyclerViewAdapter.OnItemLongClickListener() {


            @Override
            public void onItemLongClick(int section, int position) {
                Toast.makeText(MainActivity.this, "长按  美女的位置为:" + position, Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "onItemLongClick: section:"+section+"  position:"+position);
            }
        });

        mGridLayoutManager = new GridLayoutManager(this, 3);
        mGridLayoutManager.setSpanSizeLookup(new SectionedSpanSizeLookup(mAdapter,
                mGridLayoutManager));
        rv.setLayoutManager(mGridLayoutManager);
        rv.setAdapter(mAdapter);
        mDivider = new SectionedGridDivider(this, 50, Color.parseColor("#F5F5F5"));
        rv.addItemDecoration(mDivider);

        loadMoreListener = new LoadMoreListener(mGridLayoutManager) {
            @Override
            public void onLoadMore() {
                isPull = false;
                isLoading = true;
                mAdapter.changeMoreStatus(SectionedRecyclerViewAdapter.LOADING_MORE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //mPresenter.loadData(1);
                        List<TestEntity.BodyBean.EListBean> datas = DatasUtil.createDatas();
                        mAdapter.addMoreData(datas);

                        mAdapter.changeMoreStatus(SectionedRecyclerViewAdapter.PULLUP_LOAD_MORE);
                        isLoading = false;
                    }
                }, 1000);
            }
        };
        rv.addOnScrollListener(loadMoreListener);

        refreshLayout.setOnRefreshListener(this);

        List<TestEntity.BodyBean.EListBean> datas = DatasUtil.createDatas();
        mAdapter.addMoreData(datas);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<TestEntity.BodyBean.EListBean> datas = DatasUtil.createDatas();
                mAdapter.addMoreData(datas);

                //关闭正在加载的 刷新
                refreshLayout.setRefreshing(false);
            }
        }, 1000);
    }


}
