# Bundle

# 1. 使用Bundle传递对象

1.让对象实现Serializable
2.数据封装

	Bundle bundle = new Bundle();
	TabData tabData = new TabData();
    //将对象封装到Bundle对象中
    bundle.putSerializable(CONTENT_DATA_KEY,tabData);

3.取出数据

	Bundle bundle = getArguments();
    if (bundle != null) {
        // 从bundle数据包中取出数据
        TabData tabData = (TabData) bundle.getSerializable(CONTENT_DATA_KEY);
    }
