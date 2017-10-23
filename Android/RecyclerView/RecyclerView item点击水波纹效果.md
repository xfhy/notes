哇,相见恨晚,其实只需要在RecyclerView的itemView的最外层布局加一句
android:foreground="?android:attr/selectableItemBackground"

在这里意思是指定有界的波纹

这里是API 21以上效果是比较好的,虽然API 21以下也能用,只是效果没有那么好看.