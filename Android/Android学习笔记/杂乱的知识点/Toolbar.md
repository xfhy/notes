# Toolbar

## 1. 使用

	setSupportActionBar(mToolbar);

	  ActionBar actionBar = getSupportActionBar();
	  if (actionBar != null) {
	      //设置显示Home键
	      actionBar.setDisplayHomeAsUpEnabled(true);
	  }
## 2.添加了这个之后左上角的图标就变了

	//添加抽屉监听 ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 
	mToolbar, R.string.open_drawer, R.string .close_drawer); 
	mDrawerLayout.addDrawerListener(actionBarDrawerToggle); 
	//同步状态 
	actionBarDrawerToggle.syncState();
