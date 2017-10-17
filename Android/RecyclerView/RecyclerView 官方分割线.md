# DividerItemDecoration 官方的分割线

> 今天忽然看到一个好东西,给大家分享一下.在API 25之后,Google添加了一个DividerItemDecoration用作RecyclerView的分割线,目前只支持LinearLayoutManager,垂直和水平都是支持的.

用法: 

	mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
             mLayoutManager.getOrientation());
     recyclerView.addItemDecoration(mDividerItemDecoration);


简单吧,哈哈,官方的东西就是简单好用啊.感谢Google.
