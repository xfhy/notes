# Toolbar

## 1. 使用

	<android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:fitsSystemWindows="true">

        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/blueStatus"
            android:minHeight="?attr/actionBarSize"
            app:layout_scrollFlags="scroll|enterAlways"
            app:navigationIcon="?attr/homeAsUpIndicator"
            app:theme="@style/Theme.AppCompat.NoActionBar"/>

    </android.support.design.widget.AppBarLayout>

	setSupportActionBar(mToolbar);

	  ActionBar actionBar = getSupportActionBar();
	  if (actionBar != null) {
	      //设置显示Home键
	      actionBar.setDisplayHomeAsUpEnabled(true);
	  }
	  
	
监听Toolbar左上角按钮的点击事件
覆写Activity的onOptionsItemSelected()方法,那个按钮的id一直是android.R.id.home

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //Toolbar左上角的按钮
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
## 2.添加了这个之后左上角的图标就变了

	//添加抽屉监听 ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 
	mToolbar, R.string.open_drawer, R.string .close_drawer); 
	mDrawerLayout.addDrawerListener(actionBarDrawerToggle); 
	//同步状态 
	actionBarDrawerToggle.syncState();
