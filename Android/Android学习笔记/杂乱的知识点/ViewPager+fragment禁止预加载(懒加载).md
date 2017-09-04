# Android中ViewPager+Fragment取消(禁止)预加载延迟加载(懒加载)问题解决方案

[转]原作:http://blog.csdn.net/linglongxin24/article/details/53205878

> 在Android中我们经常会用到ViewPager+Fragment组合。然而，有一个很让人头疼的问题就是，我们去加载数据的时候由于ViewPager的内部机制所限制，所以它会默认至少预加载一个。这让人很郁闷，所以，我就想到要封装一个Fragment来解决这个问题。 

# 解决方案

	public abstract class BaseFragment extends Fragment {

	    /**
	     * 该fragment所对应的布局
	     */
	    private View mRootView;
	
	    /**
	     * 当前fragment所依附的Activity
	     */
	    protected FragmentActivity mActivity;
	    /**
	     * 视图是否已经初始化
	     */
	    protected boolean isInit = false;
	    /**
	     * 数据是否已经加载
	     */
	    protected boolean isLoad = false;
	
	    @Override
	    public void onAttach(Context context) {
	        super.onAttach(context);
	        initPresenter();
	    }
	
	    @Nullable
	    @Override
	    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
	            Bundle savedInstanceState) {
	        //需要返回页面布局   所有子类需要返回view
	        mRootView = inflater.inflate(getLayoutResId(), container, false);
	        mActivity = getActivity();
	
	        isInit = true;  //视图已加载
	        isCanloadData(); //初始化的时候去加载数据
	        initArguments();
	        initView();
	        initViewEvent();
	        initData();
	        return mRootView;
	    }
	
	    /**
	     * 视图是否已经对用户可见,系统的方法
	     *
	     * @param isVisibleToUser
	     */
	    @Override
	    public void setUserVisibleHint(boolean isVisibleToUser) {
	        super.setUserVisibleHint(isVisibleToUser);
	        //去看看是否可以加载数据了
	        isCanloadData();
	    }
	
	    /**
	     * 是否可以加载数据
	     */
	    private void isCanloadData() {
	        if (!isInit) {
	            return;
	        }
	
	        //如果用户可见并且之前没有加载过数据  则去加载数据
	        if (getUserVisibleHint() && !isLoad) {
	            lazyLoad();
	        } else {
	            if (isLoad) {
	                stopLoad();
	            }
	        }
	    }
	
	    /**
	     * 初始化presenter
	     */
	    protected void initPresenter() {
	
	    }
	
	    /**
	     * 方便获取传递过来的参数
	     */
	    protected void initArguments() {
	
	    }
	
	    /**
	     * 初始化view的点击事件
	     */
	    protected abstract void initViewEvent();
	
	    /**
	     * 设置布局数据
	     */
	    protected void initData() {
	
	    }
	
	    /**
	     * 设置布局的id
	     *
	     * @return 返回子类布局id
	     */
	    protected abstract int getLayoutResId();
	
	    /**
	     * 初始化View
	     */
	    protected abstract void initView();
	
	    /**
	     * 当视图初始化并且对用户可见的时候去真正的加载数据  在加载完数据之后需要把isLoad置true
	     */
	    protected abstract void lazyLoad();
	
	    /**
	     * 当视图已经对用户不可见并且加载过数据,如果需要在切换到其他页面时停止加载,则可以重写该方法
	     */
	    protected void stopLoad() {
	    }
	
	}

# 子fragment去继承上面的BaseFragment

在lazyLoad()中去请求网络数据,并把isLoad置为true,表示已经加载完数据.
