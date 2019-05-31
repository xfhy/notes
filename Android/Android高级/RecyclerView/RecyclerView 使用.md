

#### 1. 给RecyclerView的item添加点击效果

- 方式1:`android:foreground="?android:attr/selectableItemBackground"`
- 方式2: 

```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android" >
    <item android:state_pressed="true" android:drawable="@color/color_item_press"></item>
    <item android:drawable="@color/color_item_normal"></item>
</selector>
```
