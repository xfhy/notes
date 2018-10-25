
![](http://olg7c0d2n.bkt.clouddn.com/18-10-22/4224381.jpg)

微信的这个菜单是个经典案例(我猜的...)

<img src="http://olg7c0d2n.bkt.clouddn.com/18-10-22/64927458.jpg" width=300px/>

#### 看一个demo

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">

    <LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="doclick"
        android:orientation="vertical">

        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@drawable/shape_fragment_custom_bottom_sheet_dialog"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="#0f0"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="#00f"/>

    </LinearLayout>
</androidx.coordinatorlayout.widget.CoordinatorLayout>

```

代码如下:
```java
public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static CustomBottomSheetDialogFragment newInstance() {

        Bundle args = new Bundle();

        CustomBottomSheetDialogFragment fragment = new CustomBottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BottomSheetBehavior mBehavior;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_custom_bottom_sheet_dialog, null);
        dialog.setContentView(view);

        //设置背景透明   这样才好实现圆角
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        /*
         设置高度
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.9); layoutParams.height = height;
        view.setLayoutParams(layoutParams);*/
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //默认全屏展开
        //mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void doclick(View v) {
        //点击任意布局关闭
        //mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

}
```