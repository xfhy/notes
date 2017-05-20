# BaseRecyclerViewAdapterHelper使用

# 1. 添加分组

1.首先bean对象需要基础自SectionEntity

	public class AppInfo extends SectionEntity {

	    private String name;   //名称
	    private String packageName;  //包名
	    private Drawable icon;    //图标
	    private boolean isSdcard; //手机应用,用户装的
	    private boolean isSystem; //是否为系统应用
	
	    /**
	     * 构造方法
	     * @param isHeader   指定这一项是否是header
	     * @param header     
	     */
	    public AppInfo(boolean isHeader, String header) {
	        super(isHeader, header);
	    }
	
	    public AppInfo(boolean isHeader, String header, String name, String packageName, Drawable
	            icon, boolean isSdcard, boolean isSystem) {
	        super(isHeader, header);
	        this.name = name;
	        this.packageName = packageName;
	        this.icon = icon;
	        this.isSdcard = isSdcard;
	        this.isSystem = isSystem;
	    }
	}

2.然后写适配器时这样写

	/**
     * app信息的适配器    extends第三方的Adapter
     */
    public class MyAppInfoAdapter extends BaseSectionQuickAdapter<AppInfo, BaseViewHolder> {
        //构造方法   参数:普通条目的布局,header的布局,集合数据
        public MyAppInfoAdapter(int layoutResId, int sectionHeadResId, List<AppInfo> data) {
            super(layoutResId, sectionHeadResId, data);
        }

        //convert方法里面加载item数据
        @Override
        protected void convert(BaseViewHolder viewHolder, AppInfo item) {
            viewHolder.setImageDrawable(R.id.iv_app_icon, item.getIcon())
                    .setText(R.id.tv_app_name, item.getName());
            if (item.isSdcard()) {
                viewHolder.setText(R.id.tv_app_path, "SD卡应用");
            } else {
                viewHolder.setText(R.id.tv_app_path, "手机应用");
            }
        }

        //在convertHead方法里面加载head数据
        @Override
        protected void convertHead(BaseViewHolder helper, AppInfo item) {
            //将头布局里面的tv_app_group_name设置文字为item.header
            helper.setText(R.id.tv_app_group_name, item.header);
        }
    }
