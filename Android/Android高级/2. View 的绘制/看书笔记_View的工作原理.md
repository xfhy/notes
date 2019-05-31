
> 复习刚哥的安卓开发艺术探索,又有了新的感受

[TOC]

### 初识ViewRoot和DecorView

1. measure->layout->draw
2. View的三大流程是通过ViewRoot来完成的
3. ActivityThread中创建完Activity后,会将DecorView添加到Window中,同时创建ViewRootImpl对象,并将ViewRootImpl对象和DecorView建立关联.过程参见源码:
4. 
```java
//WindowManagerGlobal  ->  addView

//创建ViewRootImpl，并且将view与之绑定
root = new ViewRootImpl(view.getContext(),display);
//通过ViewRootImpl的setView方法，完成view的绘制流程，并添加到window上。
root.setView(view,wparams,panelParentView);
```
5. DecorView是一个FrameLayout,一般情况下它内部会包含一个竖直方向的
LinearLayout,一般分为上下2部分(具体情况和Android版本及主体有关),中间的内容栏,id为android.R.id.content
6. 每个Activity都会持有一个Window(实际上是一个PhoneWindow)引用,Window是一个抽象类,只有一个实现类就是PhoneWindow.
7. PhoneWindow中含有一个DecorView,是Window中的顶级View.
8. 通过Activity中的setContentView(),会在PhoneWindow中将该id加载到mContentParent中,mContentParent可以是DecorView也可以是DecorView的子View,反正就是内容区域.
9. PhoneWindow是在Activity中的attach()中实例化的,PhoneWindow在实例化的时候传入了Activity的引用,PhoneWindow中的mContext就是当前所关联的Activity.
10. PhoneWindow其实是Activity中的,setContentView()其实是将View设置到PhoneWindow中的DecorView中.
11. Activity的启动过程很复杂，最终会由ActivityThread中的handleLaunchActivity()来完成整个启动过程。 
在这个方法中会通过performLaunchActivity()方法创建Activity，performLaunchActivity()内部通过类加载器创建Activity的实例对象，并调用其attach()方法为其关联运行过程中所依赖的一系列上下文环境变量以及创建与绑定窗口。 

### MeasureSpec

1. 决定着View的宽高,很大程度上会受父容器的影响
2. 高2位,代表SpecMode,测量模式;低30位代表SpecSize.

```
UNSPECIFIED  - 00000000 00000000 00000000 00000000 父容器不对子布局有任何限制，要多大给多大（如: scrollview）
EXACTLY  - 01000000 00000000 00000000 00000000 父容器已经测量出子布局大小。
AT_MOST - 10000000 00000000 00000000 00000000 父窗口限定了一个最大值给子子布局。

低30位用来封装size.
```

3. UNSPECIFIED: 父容器对View没有任何限制,要多大给多大,这种情况一般用于系统内部,表示一种测量的状态
4. EXACTLY: 父容器已经检测出View所需要的精确大小,这个时候View的最终大小就是SpecSize所指定的值.它对应于LayoutParams中的`match_parent`和具体的数值这两种模式.
5. `AT_MOST`: 父容器指定了可用大小即SpecSize,View的大小不能大于这个值,具体是什么值要看不同的View的具体实现.它对应于LayoutParams中的`wrap_content`.

![image](93D5B023E19D4B5989EA0D5E0D50B9B2)

6. 对于普通View，其MeasureSpec由父
容器的MeasureSpec和自身的LayoutParams来共同决定

### View的工作流程

1. View的measure过程:在measure()方法中回调onMeasure()方法,一般需要为AT_MOST的情况单独处理一下,设置一个默认的宽高值.
2. ViewGroup除了自己的measure过程以外,还会遍历调用所有的子元素的measure()方法,各个子元素再递归去执行这个过程.ViewGroup是一个抽象类....我擦,,惊了.ViewGroup没有具体的测量过程,没有实现onMeasure(),需要其子类(比如LinearLayout,FrameLayout等)去实现.
3. 如果想在自定义View中获取宽高,比较好的方式是在onLayout()中去获取View的最终宽高.
4. 获取View宽高,在测量完毕后
    - Activity/View#onWindowFocusChanged(),这个方法的含义是:View已经初始化完成了,这个时候获取宽高是没有问题的.这个方法会被调用多次.
    - view.post(runnable) 通过post可以将Runnable投递到消息队列的尾部,然后等待Looper调用此Runnable的时候,View也已经初始化好了.
    -  ViewTreeObserver,使用ViewTreeObserver的众多回调可以完成这个功能，比如使用
    OnGlobalLayoutListener这个接口，当View树的状态发生改变或者View树内部的View的可
    见性发现改变时，onGlobalLayout方法将被回调，因此这是获取View的宽/高一个很好的时
    机。
5. View的测量宽/高和最终/宽高有什么区
别？这个问题可以具体为：View的getMeasuredWidth和getWidth这两个方法有什么区别?

```java
public final int getWidth() {
    return mRight -mLeft;
}
```
在View
的默认实现中，View的测量宽/高和最终宽/高是相等的，只不过测量宽/高形成于View的
measure过程，而最终宽/高形成于View的layout过程，即两者的赋值时机不同，测量宽/高
的赋值时机稍微早一些。但是,也有骚操作.

```java
public void layout(int l,int t,int r,int b) {
    super.layout(l,t,r + 100,b + 100);
}
```
上述代码会导致在任何情况下View的最终宽/高总是比测量宽/高大100px，虽然这样
做会导致View显示不正常并且也没有实际意义，但是这证明了测量宽/高的确可以不等于
最终宽/高。另外一种情况是在某些情况下，View需要多次measure才能确定自己的测量
宽/高，在前几次的测量过程中，其得出的测量宽/高有可能和最终宽/高不一致，但最终来
说，测量宽/高还是和最终宽/高相同。

6. layout的作用是ViewGroup用来确定子元素的文字,当ViewGroup的位置被确定后,它会在onLayout()中遍历所有子元素并调用其layout()方法.在layout()方法中onLayout()方法又会被调用(如果是ViewGroup,那么继续layout它的子元素).layout()方法确定View本身的位置,而onLayout()方法则会确定所有子元素的位置.
7. draw过程:
    1. 绘制背景background.draw(canvas)
    2. 绘制自己onDraw()
    3. 绘制children(dispatchDraw)
    4. 绘制装饰(onDrawScrollBars)

### 自定义View

> 看hencoder的教程,perfect

1. View中如果有线程或者动画,需要及时停止,参考View#onDetachedFromWindow


