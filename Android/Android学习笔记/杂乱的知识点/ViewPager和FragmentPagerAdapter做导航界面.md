# ViewPager和FragmentPagerAdapter做导航界面

1.先创建几个fragment,作为导航界面的几个fragment,也将fragment布局写好.

2.GlideActivity中写入ViewPager,然后将adapter写好

	public class GuideAdapter extends FragmentPagerAdapter {

	    private List<Fragment> dataList;
	
	    public GuideAdapter(FragmentManager fm, List<Fragment> dataList) {
	        super(fm);
	        this.dataList = dataList;
	    }
	
	    /*
	    返回与指定位置相关联的Fragment
	    前提是拥有所有Fragment的一个集合
	     */
	    @Override
	    public Fragment getItem(int position) {
	        if (dataList != null && dataList.size() > position) {
	            return dataList.get(position);
	        }
	        return null;
	    }
	
	    //个数
	    @Override
	    public int getCount() {
	        return dataList == null ? 0 : dataList.size();
	    }
	}

3.然后写下面的小点点,其实就是一个RadioGroup,然后给ViewPager添加滑动监听,滑动到那里的时候切换状态.

	
