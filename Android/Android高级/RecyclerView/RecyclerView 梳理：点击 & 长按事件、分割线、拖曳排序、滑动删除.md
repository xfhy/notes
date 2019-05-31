> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5a320ffcf265da43200342a3![](http://upload-images.jianshu.io/upload_images/2625875-1151dc0f8de04f31.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这次主要是把 RecyclerView 比较常用的基本的点，在这里集中整理一下。从这篇文章主要梳理以下几点：

*   优雅的实现：item 点击事件 & item 长点击事件
*   RecyclerView 添加 divider 的标准姿势
*   RecyclerView 实现 item 的拖曳排序和滑动删除
*   拖曳排序时，限制首个 item 固定的实现

先看一下最终的效果图：

![](http://upload-images.jianshu.io/upload_images/2625875-eccdcd5760024dd9.gif?imageMogr2/auto-orient/strip) ![](http://upload-images.jianshu.io/upload_images/2625875-fb57c4ceb07885e1.gif?imageMogr2/auto-orient/strip)

自从 RecyclerView 发布以来，由于其高度的可交互性被广泛使用。相信大家肯定对它的使用方法已经非常熟练了，今天主要是为大家总结一下较正常用法更加优雅的方式。

> 如果你想再回顾一下 RecyclerView 的基本使用方法，推荐鸿洋的这篇文章：
> [Android RecyclerView 使用完全解析 体验艺术般的控件](https://link.juejin.im?target=http%3A%2F%2Fblog.csdn.net%2Flmj623565791%2Farticle%2Fdetails%2F45059587)

## 优雅的实现：item 点击事件 & item 长点击事件

### 使用方式

RecyclerView 的 api 虽然没有提供 onItemClickListener 但是提供了 addOnItemTouchListener() 方法，既然可以添加触摸监听，那么我们完全可以获取触摸手势来识别点击事件，然后通过触摸坐标来判断点击的是哪一个 item。

```
mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                //TODO item 点击事件
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder viewHolder) {
                //TODO item 长按事件
            }
        });
复制代码
```

其中 OnRecyclerItemClickListener 是自定义的一个触摸监听器，代码如下：

```
public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener{
    private GestureDetectorCompat mGestureDetectorCompat;//手势探测器
    private RecyclerView mRecyclerView;

    public OnRecyclerItemClickListener(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mGestureDetectorCompat = new GestureDetectorCompat(mRecyclerView.getContext(),
                new ItemTouchHelperGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public abstract void onItemClick(RecyclerView.ViewHolder viewHolder);
    public abstract void onLongClick(RecyclerView.ViewHolder viewHolder);
}
复制代码
```

GestureDetectorCompat 中传入了一个 ItemTouchHelperGestureListener，代码如下：

```
private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener{
			//一次单独的轻触抬起手指操作，就是普通的点击事件
	        @Override
	        public boolean onSingleTapUp(MotionEvent e) {
	            View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
	            if (childViewUnder != null) {
	                RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(childViewUnder);
	                onItemClick(childViewHolder);
	            }
	            return true;
	        }

			//长按屏幕超过一定时长，就会触发，就是长按事件
	        @Override
	        public void onLongPress(MotionEvent e) {
	            View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
	            if (childViewUnder != null) {
	                RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(childViewUnder);
	                onLongClick(childViewHolder);
	            }
	        }
	    }
复制代码
```

### 原理分析

上面的代码很简单没什么复杂的地方，就是通过一个手势探测器 GestureDetectorCompat 来探测屏幕事件，然后通过手势监听器 SimpleOnGestureListener 来识别手势事件的种类，然后调用我们设置的对应的回调方法。这里值得说的是：当获取到了 RecyclerView 的点击事件和触摸事件数据 MotionEvent，那么如何才能知道点击的是哪一个 item 呢？

RecyclerView 已经为我们提供了这样的方法：`findChildViewUnder()`。

我们可以通过这个方法获得点击的 item ，同时我们调用 RecyclerView 的另一个方法 `getChildViewHolder()`，可以获得该 item 的 ViewHolder，最后再回调我们定义的虚方法 onItemClick() 就 ok 了，这样我们就可以在外部实现该方法来获得 item 的点击事件了。

## RecyclerView 添加 divider 的标准姿势

当你想给条目间添加 divider 时，你可能自然而然的去尝试这种方式：

```
<android.support.v7.widget.RecyclerView
    android:divider="#ffff0000"
    android:dividerHeight="10dp"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
复制代码
```

其实 RecyclerView 是没有这两个属性的，就算你写上也不会有任何效果。
当然你还可以通过给 item 的最外层布局设置一个 margin 值，甚至你还可以专门在 item 布局中的适当地方添加一个高度 / 宽度为 1 的带背景的 View 作为 divider，这两种方法呢，确实有效果，但是不够优雅，有时还可能带来一些想不到的问题。

其实官方还是为我们提供了为 RecyclerView 添加分割线的方式的，那就是方法： `mRecyclerView.addItemDecoration()` 。该方法的参数为 RecyclerView.ItemDecoration，该类为抽象类，且官方目前并没有提供默认的实现类，我们只能自己来实现。

### 使用方式

**列表布局的分割线实例：**

```
public class DividerListItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;

    private int mOrientation;

    public DividerListItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public DividerListItemDecoration(Context context, int orientation, int drawableId) {
        mDivider = ContextCompat.getDrawable(context, drawableId);
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

	//画线 > 就是画出你想要的分割线样式
    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            android.support.v7.widget.RecyclerView v = new android.support.v7.widget.RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

	//设置条目周边的偏移量
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}
复制代码
```

**网格布局分割线实例：**

```
public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;
    private int lineWidth = 1;

    public DividerGridItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    public DividerGridItemDecoration(int color) {
        mDivider = new ColorDrawable(color);
    }

    public DividerGridItemDecoration() {
        this(Color.parseColor("#cccccc"));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + lineWidth;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + lineWidth;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + lineWidth;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        boolean b = state.willRunPredictiveAnimations();
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
//        if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
//        {
//            outRect.set(0, 0, lineWidth, 0);
//        }
//        else if (isLastColum(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
//        {
////            if (b){
////                outRect.set(0, 0, lineWidth, lineWidth);
////            }else {
//                outRect.set(0, 0, 0, lineWidth);
////            }
//        }
//        else {
        outRect.set(0, 0, lineWidth, lineWidth);
//        }
    }
}
复制代码
```

### 使用说明

上面给出的两个实例都是最简单的一条线的分割。这里的分割线你是可以自由的去自定义它的，具体如何实现也不是太复杂，这里不再做详细介绍了，推荐一篇文章：

> [RecyclerView 之 ItemDecoration 讲解及高级特性实践](https://link.juejin.im?target=http%3A%2F%2Fwww.10tiao.com%2Fhtml%2F227%2F201705%2F2650239745%2F1.html)

## RecyclerView 实现 item 的拖曳排序和滑动删除

下面就主要为大家梳理一下拖曳排序和滑动删除的实现，具体实现效果看文章首部效果图，这里就不再重复放图了。

### 实现方式

主要就要使用到 ItemTouchHelper，ItemTouchHelper 一个帮助开发人员处理拖拽和滑动删除的实现类，它能够让你非常容易实现侧滑删除、拖拽的功能。（ItemTouchHelper 的使用并不仅仅局限于 RecyclerView 的滑动删除，你同意可以用在其他需要拖曳滑动的地方。当然，今天我们不涉及其他地方的使用）

实现的代码并关联到 RecyclerView 非常简单，代码如下：

```
ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback());
itemTouchHelper.attachToRecyclerView(mRecyclerView);
复制代码
```

代码很简单，没什么好说的。需要我们关注的是创建 ItemTouchHelper 时传入的参数 ItemTouchHelper.Callback() 。ItemTouchHelper 会在拖拽的时候回调 Callback 中相应的方法，我们只需在 Callback 中实现自己的逻辑。

自定义一个类继承实现 ItemTouchHelper.Callback 接口，需要实现以下方法：

```
//通过返回值来设置是否处理某次拖曳或者滑动事件
public abstract int getMovementFlags(RecyclerView recyclerView,
                ViewHolder viewHolder);

//当长按并进入拖曳状态时，拖曳的过程中不断的回调此方法
public abstract boolean onMove(RecyclerView recyclerView,
                ViewHolder viewHolder, ViewHolder target);

//滑动删除的回调
public abstract void onSwiped(ViewHolder viewHolder, int direction);
复制代码
```

getMovementFlags() 用于设置是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向，有以下两种情况：

*   如果是列表类型的 RecyclerView，拖拽只有 UP、DOWN 两个方向
*   如果是网格类型的则有 UP、DOWN、LEFT、RIGHT 四个方向

该方法需要编写的代码如下：

```
@Override
public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    } else {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
}
复制代码
```

> dragFlags 是拖拽标志，
> swipeFlags 是滑动标志，
> swipeFlags 都设置为 0，暂时不考虑滑动相关操作。

如果设置了相关的 dragFlags，那么当长按 item 的时候就会进入拖拽并在拖拽过程中不断回调 onMove() 方法，我们就在这个方法里获取当前拖拽的 item 和已经被拖拽到所处位置的 item 的 ViewHolder，有了这 2 个 ViewHolder，我们就可以交换他们的数据集并调用 Adapter 的 notifyItemMoved 方法来刷新 item。

```
@Override
public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
	//拖动的 item 的下标
    int fromPosition = viewHolder.getAdapterPosition();
	//目标 item 的下标，目标 item 就是当拖曳过程中，不断和拖动的 item 做位置交换的条目。
    int toPosition = target.getAdapterPosition();
    if (fromPosition < toPosition) {
        for (int i = fromPosition; i < toPosition; i++) {
            Collections.swap(((RecyAdapter) mAdapter).getDataList(), i, i + 1);
        }
    } else {
        for (int i = fromPosition; i > toPosition; i--) {
            Collections.swap(((RecyAdapter) mAdapter).getDataList(), i, i - 1);
        }
    }
    mAdapter.notifyItemMoved(fromPosition, toPosition);
    return true;
}
复制代码
```

只要重写完上面这两个方法，RecyclerView 就能实现拖曳的效果了。是不是很简单？但是虽然拖曳是没什么问题了，但是并不能达到下图的效果，因为你正在拖曳的 item 并没有阴影效果。

![](http://upload-images.jianshu.io/upload_images/2625875-31e0271cfef33e6d.gif?imageMogr2/auto-orient/strip)

那怎么才能实现被拖曳的 item 有背景颜色加深起到强调的视觉效果呢？这是需要重写下面两个方法：

```
//当长按 item 刚开始拖曳的时候调用
@Override
public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
		//给被拖曳的 item 设置一个深颜色背景
        viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
    }
    super.onSelectedChanged(viewHolder, actionState);
}

//当完成拖曳手指松开的时候调用
@Override
public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);
	//给已经完成拖曳的 item 恢复开始的背景。
	//这里我们设置的颜色尽量和你 item 在 xml 中设置的颜色保持一致
    viewHolder.itemView.setBackgroundColor(Color.WHITE);
}
复制代码
```

这样就能完全达到上面图片的效果了。

**滑动删除**

如何实现滑动删除呢？我们只需要实现第三个方法 onSwipe() 就行了。代码如下：

```
@Override
public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    int adapterPosition = viewHolder.getAdapterPosition();
    mAdapter.notifyItemRemoved(adapterPosition);
    ((RecyAdapter) mAdapter).getDataList().remove(adapterPosition);
}
复制代码
```

同时也不要忘了修改一下 getMovementFlags() 方法，以便能够相应滑动事件：

```
@Override
public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    } else {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
		//注意：和拖曳的区别就是在这里
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
}
复制代码
```

那目前你就能完美的实现拖曳排序和滑动删除了。

**拖曳排序，首个固定**

有时我们希望首个 item 不能被拖曳排序。比如我们在新闻 App 中常见当我们进行新闻分类时，“热门” 新闻这个分类总是第一个且不能被拖曳修改，类似下面的效果：

![](http://upload-images.jianshu.io/upload_images/2625875-fb57c4ceb07885e1.gif?imageMogr2/auto-orient/strip)

那么怎么才能达到上面的效果呢？在上面我们的 Callback 类中有一个方法：

```
public boolean isLongPressDragEnabled() {
	return true;
}
复制代码
```

这个方法是为了告诉 ItemTouchHelper 是否需要 RecyclerView 支持长按拖拽，默认返回是 ture，理所当然我们要支持，所以我们没有重写，因为默认 true。但是这样做是默认全部的 item 都可以拖拽，怎么实现部分 item 拖拽呢，在 isLongPressDragEnabled 方法的源码中有提示说，如果想自定义拖曳 view, 那么就使用 startDrag(ViewHolder) 方法。

** 第一步：** 那么我们就先重写 isLongPressDragEnabled() 方法，返回 false 让它控制所有的 item 都不能拖曳。

```
public boolean isLongPressDragEnabled() {
	return false;
}
复制代码
```

** 第二步：** 我们给 RecyclerView 设置 item 的长按监听事件，然后判断这个 item 是不是第一个（或者最后一个，如果你不想让最后一个被拖曳的话），如果不是我们就手动调用 startDrag(ViewHolder) 让 item 开始被拖曳。
结合上面我们提供的给 item 设置点击和长按事件的方法，我们可以这样：

```
mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        //TODO:点击事件
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder viewHolder) {
		//当 item 被长按且不是第一个时，开始拖曳这个 item
        if (viewHolder.getLayoutPosition() != 0) {
            itemTouchHelper.startDrag(viewHolder);
        }
    }
});
复制代码
```

** 第三步：** 如果你以为上面两步你就达到首个 item 固定不被拖曳的话，恭喜你，答对了！首个 item 确实固定不能被拖曳了，可是看看下图，就会令你大跌眼睛：

![](http://upload-images.jianshu.io/upload_images/2625875-885c339e7bba4b41.gif?imageMogr2/auto-orient/strip)

虽然我们通过上面两步控制了首个 item 不能被长按拖曳，但是我们并没有处理，别的 item 被拖曳到首个 item 的情况。那么如何才能让首个 item 不被挤掉呢，这个也很简单，只需要在 Callback 的 onMove() 方法中处理首个 item 被当着目标 item 的情况就行了。

```
@Override
public boolean onMove（...) {
    int fromPosition = viewHolder.getAdapterPosition();
    int toPosition = target.getAdapterPosition();
	//其他地方代码都和上面的一样，这个就直接省略了
	//这里判断如果目标 item 是首个 item，那么就直接返回false，表示不响应此次拖曳移动
    if (toPosition == 0) {
        return false;
    }
    ...
    return true;
}
复制代码
```

好了，到这里就大功告成了。

本文源代码地址：[github.com/OCNYang/Rec…](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FOCNYang%2FRecyclerViewEvent)

> 参考文章：
> [chuansong.me/n/400690551…](https://link.juejin.im?target=http%3A%2F%2Fchuansong.me%2Fn%2F400690551872)
> [chuansong.me/n/400690851…](https://link.juejin.im?target=http%3A%2F%2Fchuansong.me%2Fn%2F400690851058)
> [www.10tiao.com/html/227/20…](https://link.juejin.im?target=http%3A%2F%2Fwww.10tiao.com%2Fhtml%2F227%2F201705%2F2650239745%2F1.html)