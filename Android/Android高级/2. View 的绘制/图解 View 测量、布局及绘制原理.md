> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/3d2c49315d68

> Android 中自定义 View 一直是一个高级的技能，入门比较难，看起来很高大上。想要学会自定义 View, 当然要理解 View 的测量、布局及绘制原理，本篇文章将以图表的形式讲解 View 的测量、布局及绘制原理。

### 一、View 绘制的流程框架

![](https://upload-images.jianshu.io/upload_images/3985563-5f3c64af676d9aee.png) View 的绘制是从上往下一层层迭代下来的。DecorView-->ViewGroup（--->ViewGroup）-->View ，按照这个流程从上往下，依次 measure(测量),layout(布局),draw(绘制)。关于 DecorView，可以看[这篇文章](https://www.jianshu.com/p/8766babc40e0)。![](https://upload-images.jianshu.io/upload_images/3985563-a7ace6f9221c9d79.png)

### 二、Measure 流程

顾名思义，就是测量每个控件的大小。

调用 measure() 方法，进行一些逻辑处理，然后调用 onMeasure() 方法，在其中调用 setMeasuredDimension() 设定 View 的宽高信息，完成 View 的测量操作。

```
public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
}

```

measure() 方法中，传入了两个参数 widthMeasureSpec, heightMeasureSpec 表示 View 的宽高的一些信息。

```
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

```

由上述流程来看 Measure 流程很简单，关键点是在于 widthMeasureSpec, heightMeasureSpec 这两个参数信息怎么获得？

如果有了 widthMeasureSpec, heightMeasureSpec，通过一定的处理 (**可以重写，自定义处理步骤**)，从中获取 View 的宽 / 高，调用 setMeasuredDimension() 方法，指定 View 的宽高，完成测量工作。

##### MeasureSpec 的确定

先介绍下什么是 MeasureSpec？

![](https://upload-images.jianshu.io/upload_images/3985563-d3bf0905aeb8719b.png)

MeasureSpec 由两部分组成，一部分是测量模式，另一部分是测量的尺寸大小。

其中，Mode 模式共分为三类

UNSPECIFIED ：不对 View 进行任何限制，要多大给多大，一般用于系统内部

EXACTLY：对应 LayoutParams 中的 match_parent 和具体数值这两种模式。检测到 View 所需要的精确大小，这时候 View 的最终大小就是 SpecSize 所指定的值，

AT_MOST ：对应 LayoutParams 中的 wrap_content。View 的大小不能大于父容器的大小。

**那么 MeasureSpec 又是如何确定的？**

对于 DecorView，其确定是通过屏幕的大小，和自身的布局参数 LayoutParams。

这部分很简单，根据 LayoutParams 的布局格式（match_parent，wrap_content 或指定大小），将自身大小，和屏幕大小相比，设置一个不超过屏幕大小的宽高，以及对应模式。

对于其他 View（包括 ViewGroup），其确定是通过父布局的 MeasureSpec 和自身的布局参数 LayoutParams。

这部分比较复杂。以下列图表表示不同的情况：

![](https://upload-images.jianshu.io/upload_images/3985563-e3f20c6662effb7b.png) **当子 View 的 LayoutParams 的布局格式是 wrap_content，可以看到子 View 的大小是父 View 的剩余尺寸，和设置成 match_parent 时，子 View 的大小没有区别。为了显示区别，一般在自定义 View 时，需要重写 onMeasure 方法，处理 wrap_content 时的情况，进行特别指定。**

**从这里看出 MeasureSpec 的指定也是从顶层布局开始一层层往下去，父布局影响子布局。**

可能关于 MeasureSpec 如何确定 View 大小还有些模糊，篇幅有限，没详细具体展开介绍，可以看[这篇文章](https://www.jianshu.com/p/1dab927b2f36)

View 的测量流程：

![](https://upload-images.jianshu.io/upload_images/3985563-d1a57294428ff668.png)

### 三、Layout 流程

测量完 View 大小后，就需要将 View 布局在 Window 中，View 的布局主要通过确定上下左右四个点来确定的。

**其中布局也是自上而下，不同的是 ViewGroup 先在 layout() 中确定自己的布局，然后在 onLayout() 方法中再调用子 View 的 layout() 方法，让子 View 布局。在 Measure 过程中，ViewGroup 一般是先测量子 View 的大小，然后再确定自身的大小。**

```
public void layout(int l, int t, int r, int b) {  

    // 当前视图的四个顶点
    int oldL = mLeft;  
    int oldT = mTop;  
    int oldB = mBottom;  
    int oldR = mRight;  

    // setFrame（） / setOpticalFrame（）：确定View自身的位置
    // 即初始化四个顶点的值，然后判断当前View大小和位置是否发生了变化并返回  
 boolean changed = isLayoutModeOptical(mParent) ?
            setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);

    //如果视图的大小和位置发生变化，会调用onLayout（）
    if (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {  

        // onLayout（）：确定该View所有的子View在父容器的位置     
        onLayout(changed, l, t, r, b);      
  ...

}  

```

上面看出通过 setFrame（） / setOpticalFrame（）：确定 View 自身的位置，通过 onLayout() 确定子 View 的布局。
setOpticalFrame（）内部也是调用了 setFrame（），所以具体看 setFrame（）怎么确定自身的位置布局。

```
protected boolean setFrame(int left, int top, int right, int bottom) {
    ...
// 通过以下赋值语句记录下了视图的位置信息，即确定View的四个顶点
// 即确定了视图的位置
    mLeft = left;
    mTop = top;
    mRight = right;
    mBottom = bottom;

    mRenderNode.setLeftTopRightBottom(mLeft, mTop, mRight, mBottom);
}

```

确定了自身的位置后，就要通过 onLayout() 确定子 View 的布局。onLayout() 是一个可继承的空方法。

```
protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

```

**如果当前 View 就是一个单一的 View，那么没有子 View，就不需要实现该方法。**

**如果当前 View 是一个 ViewGroup，就需要实现 onLayout 方法，该方法的实现个自定义 ViewGroup 时其特性有关，必须自己实现。**

由此便完成了一层层的的布局工作。

View 的布局流程：

![](https://upload-images.jianshu.io/upload_images/3985563-8aefac42b3912539.png)

### 四、Draw 过程

View 的绘制过程遵循如下几步：
①绘制背景 background.draw(canvas)

②绘制自己（onDraw）

③绘制 Children(dispatchDraw)

④绘制装饰（onDrawScrollBars）

从源码中可以清楚地看出绘制的顺序。

```
public void draw(Canvas canvas) {
// 所有的视图最终都是调用 View 的 draw （）绘制视图（ ViewGroup 没有复写此方法）
// 在自定义View时，不应该复写该方法，而是复写 onDraw(Canvas) 方法进行绘制。
// 如果自定义的视图确实要复写该方法，那么需要先调用 super.draw(canvas)完成系统的绘制，然后再进行自定义的绘制。
    ...
    int saveCount;
    if (!dirtyOpaque) {
          // 步骤1： 绘制本身View背景
        drawBackground(canvas);
    }

        // 如果有必要，就保存图层（还有一个复原图层）
        // 优化技巧：
        // 当不需要绘制 Layer 时，“保存图层“和“复原图层“这两步会跳过
        // 因此在绘制的时候，节省 layer 可以提高绘制效率
        final int viewFlags = mViewFlags;
        if (!verticalEdges && !horizontalEdges) {

        if (!dirtyOpaque) 
             // 步骤2：绘制本身View内容  默认为空实现，  自定义View时需要进行复写
            onDraw(canvas);

        ......
        // 步骤3：绘制子View   默认为空实现 单一View中不需要实现，ViewGroup中已经实现该方法
        dispatchDraw(canvas);

        ........

        // 步骤4：绘制滑动条和前景色等等
        onDrawScrollBars(canvas);

       ..........
        return;
    }
    ...    
}

```

**无论是 ViewGroup 还是单一的 View，都需要实现这套流程，不同的是，在 ViewGroup 中，实现了 dispatchDraw() 方法，而在单一子 View 中不需要实现该方法。自定义 View 一般要重写 onDraw() 方法，在其中绘制不同的样式。**

View 绘制流程：

![](https://upload-images.jianshu.io/upload_images/3985563-594f6b3cde8762c7.png)

### 五、总结

从 View 的测量、布局和绘制原理来看，要实现自定义 View，根据自定义 View 的种类不同，可能分别要自定义实现不同的方法。但是这些方法不外乎：**onMeasure() 方法，onLayout() 方法，onDraw() 方法。**

**onMeasure() 方法**：单一 View，一般重写此方法，针对 wrap_content 情况，规定 View 默认的大小值，避免于 match_parent 情况一致。ViewGroup，若不重写，就会执行和单子 View 中相同逻辑，不会测量子 View。一般会重写 onMeasure() 方法，循环测量子 View。

**onLayout() 方法:** 单一 View，不需要实现该方法。ViewGroup 必须实现，该方法是个抽象方法，实现该方法，来对子 View 进行布局。

**onDraw() 方法：**无论单一 View，或者 ViewGroup 都需要实现该方法，因其是个空方法