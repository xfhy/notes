# TabLayout和ViewPager+Fragment做滑动栏

1.在Activity中需要配置一下

	private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tl_guide_title);
        mViewPager = (ViewPager) findViewById(R.id.vp_guide_content);

        //设置ViewPager适配器
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager()
                , getData());
        mViewPager.setAdapter(mAdapter);

        //绑定ViewPager
        mTabLayout.setupWithViewPager(mViewPager);

        //表示每个标签都保持自身宽度，一旦标签过多，给标题栏提供支持横向滑动的功能
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mTabLayout.setBackgroundColor(Color.parseColor("#303F9F"));
    }

    /**
     * 创造数据的方法(自己造的  其实本来这里应该从网络获取)
     * @return
     */
    private List<TabData> getData() {
        List<TabData> dataList = new ArrayList<>();
        dataList.add(new TabData(0,"社会"));
        dataList.add(new TabData(1,"科技"));
        dataList.add(new TabData(2,"头条"));
        dataList.add(new TabData(3,"IT"));
        dataList.add(new TabData(4,"华为"));
        dataList.add(new TabData(5,"最新"));
        dataList.add(new TabData(6,"上热评"));
        dataList.add(new TabData(7,"国内"));
        dataList.add(new TabData(8,"国际"));
        return dataList;
    }

2.布局

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:orientation="vertical"
	    tools:context="com.xfhy.day03.activity.HomeActivity">
	
	    <android.support.design.widget.TabLayout
	        android:id="@+id/tl_guide_title"
	        android:layout_width="match_parent"
	        app:tabTextColor="#7dffffff"
	        app:tabSelectedTextColor="#ffffff"
	        android:layout_height="55dp"/>
	
	    <android.support.v4.view.ViewPager
	        android:id="@+id/vp_guide_content"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"/>
	
	</LinearLayout>

3.adapter

	public class HomeSwitchPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> dataList;

		public HomeSwitchPagerAdapter(FragmentManager fm, List<Fragment> dataList) {
			super(fm);
			this.dataList = dataList;
		}

		@Override
		public Fragment getItem(int position) {
			if (dataList != null && dataList.size() > position) {
				Fragment fragment = dataList.get(position);
				return fragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return dataList != null ? dataList.size() : 0;
		}
	}

# 小东西

- TabLayout 小技巧 //表示每个标签都保持自身宽度，一旦标签过多，给标题栏提供支持横向滑动的功能 mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);