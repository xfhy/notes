# TabLayout的简单使用

> [官网 API地址](https://developer.android.com/reference/android/support/design/widget/TabLayout.html)

效果如下:
![TabLayout.gif](https://ooo.0o0.ooo/2017/06/06/59364aa659eaf.gif)

1.一般TabLayout是和ViewPager一起使用的.所有布局如下:

	<android.support.design.widget.TabLayout
        android:id="@+id/sliding_tabs"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:tabGravity="fill"
        app:tabMode="scrollable"
        app:tabBackground="@color/colorPrimary"
        app:tabTextColor="@color/whiteTransparent"
        app:tabSelectedTextColor="@color/white"/>


    <android.support.v4.view.ViewPager
        android:id="@+id/vp_news_pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

2.在代码中如果只是简单地添加tab.如果这样写了之后,再设置`mTabLayout.setupWithViewPager(mNewsPager);`会出现tab文字不显示的bug,特尴尬...
	
	TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
	 tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
	 tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
	 tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));

3.当然,上面的问题有解决办法.

查看源码可知:

	public void setupWithViewPager(@NonNull ViewPager viewPager) {
	    final PagerAdapter adapter = viewPager.getAdapter();
	    if (adapter == null) {
	        throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
	    }
	
	    // First we'll add Tabs, using the adapter's page titles
	    setTabsFromPagerAdapter(adapter);
	
	    // Now we'll add our page change listener to the ViewPager
	    viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(this));
	
	    // Now we'll add a tab selected listener to set ViewPager's current item
	    setOnTabSelectedListener(new ViewPagerOnTabSelectedListener(viewPager));
	
	    // Make sure we reflect the currently set ViewPager item
	    if (adapter.getCount() > 0) {
	        final int curItem = viewPager.getCurrentItem();
	        if (getSelectedTabPosition() != curItem) {
	            selectTab(getTabAt(curItem));
	        }
	    }
	}

	public void setTabsFromPagerAdapter(@NonNull PagerAdapter adapter) {
	    removeAllTabs();
	    for (int i = 0, count = adapter.getCount(); i < count; i++) {
	        addTab(newTab().setText(adapter.getPageTitle(i)));
	    }
	}

**请注意**:在setupWithViewPager()里面会调用setTabsFromPagerAdapter()方法,而第二个方法里面会调用removeAllTabs();
啊!!!!!!!!什么鬼????居然把所有的tab移除了,,,,于是我们之前`tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));`这样添加的tab就没有了,当然就不显示文字了..还看到里面调用了`addTab(newTab().setText(adapter.getPageTitle(i)));`,所以我推断可以重写adapter里面的getPageTitle()方法解决此问题.后来,果然成功啦!

**解决办法**:

必须自己重写ViewPager的PagerAdapter的getPageTitle()方法,返回正确的tab文字.
