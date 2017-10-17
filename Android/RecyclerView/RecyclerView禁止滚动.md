1, LinearLayoutManager不能多个RecyclerView公用

2,代码如下:

	 LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext) {
				@Override
				public boolean canScrollVertically() {
					return false;
				}
			};
			recyclerView.setLayoutManager(mLinearLayoutManager);