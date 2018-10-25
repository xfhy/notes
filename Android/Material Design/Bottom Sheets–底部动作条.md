Bottom Sheets–底部动作条

底部动作条(Bottom Sheets)是一个从屏幕底部边缘向上滑出的一个面板，使用这种方式向用户呈现一组功能。底部动作条呈现了简单、清晰、无需额外解释的一组操作。

在一个标准的列表样式的底部动作条(Bottom Sheets)中，每一个操作应该有一句描述和一个左对齐的 icon。如果需要的话，也可以使用分隔符对这些操作进行逻辑分组，也可以为分组添加标题或者副标题。

![](http://wiki.jikexueyuan.com/project/material-design/images/components-bottomsheet-for-mobile-1b_large_mdpi.png)

#### 举个例子

<img src="http://olg7c0d2n.bkt.clouddn.com/18-10-22/550317.jpg" width=250px></img>

<img src="http://olg7c0d2n.bkt.clouddn.com/18-10-22/69714740.jpg" width=250px></img>

下面是布局
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".BottomSheetsActivity">

    <!--
        peekHeight是当Bottom Sheets关闭的时候，底部下表我们能看到的高度，
        hideable 是当我们拖拽下拉的时候，bottom sheet是否能全部隐藏。
        如果你需要监听Bottom Sheets回调的状态，可以通过setBottomSheetCallback来实现，
        onSlide方法是拖拽中的回调，根据slideOffset可以做一些动画 onStateChanged方法可以监听到状态的改变,总共有5种
            STATE_COLLAPSED: 关闭Bottom Sheets,显示peekHeight的高度，默认是0

            STATE_DRAGGING: 用户拖拽Bottom Sheets时的状态

            STATE_SETTLING: 当Bottom Sheets view释放时记录的状态。

            STATE_EXPANDED: 当Bottom Sheets 展开的状态

            STATE_HIDDEN: 当Bottom Sheets 隐藏的状态
    -->

    <LinearLayout
        android:id="@+id/layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#EFEFEF"
        android:orientation="vertical"
        android:paddingLeft="15dp"
        android:paddingRight="15dp"
        app:behavior_hideable="true"
        app:behavior_peekHeight="0dp"
        app:layout_behavior="@string/bottom_sheet_behavior">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:minHeight="30dp"
            android:text="选取方式"
            android:textColor="?attr/colorAccent"/>

        <Button
            android:id="@+id/photo"
            style="@style/Widget.AppCompat.Button.Borderless.Colored"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:minHeight="48dp"
            android:text="照片"
            android:textColor="@android:color/darker_gray"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="@android:color/darker_gray"/>

        <Button
            android:id="@+id/camera"
            style="@style/Widget.AppCompat.Button.Borderless.Colored"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:minHeight="48dp"
            android:text="相机"
            android:textColor="@android:color/darker_gray"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="@android:color/darker_gray"/>

        <Button
            android:id="@+id/tv_dialog_cancel"
            style="@style/Widget.AppCompat.Button.Borderless.Colored"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:text="取消"
            android:textColor="?attr/colorAccent"/>
    </LinearLayout>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        android:onClick="fab"
        android:src="@android:drawable/ic_dialog_email"
        app:layout_anchor="@id/layout"
        app:layout_anchorGravity="right|top"
        app:pressedTranslationZ="10dp"/>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

下面是代码:
```java
public class BottomSheetsActivity extends AppCompatActivity {

    private BottomSheetBehavior mBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheets);
        mBehavior = BottomSheetBehavior.from(findViewById(R.id.layout));
    }

    public void fab(View view) {

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            //折叠
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

}
```

其实就是用了一个behavior,然后上面的LinearLayout用什么View都行,一般是选择LinearLayout、RecyclerView、ScrollView
