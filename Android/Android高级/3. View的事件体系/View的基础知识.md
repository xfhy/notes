

### 1.View基础知识

- getX()/getY()返回的是相对于当前View左上角的x和y坐标,而getRawX()/getRawY()返回的是相对于手机屏幕左上角的x和y坐标.
- TouchSlop: TouchSlop是系统所能识别出的被认为是滑动的最小距离,这是一个常量,和设备有关.可以通过如下方式获取该常量:`ViewConfiguration. get(getContext()).getScaledTouchSlop()`
- VelocityTracker: 速度追踪,用于追踪手指在滑动过程中的速度,包括水平和竖直方向的速度.

```java
VelocityTracker velocityTracker = VelocityTracker.obtain();
velocityTracker.addMovement(event);
//获取速度之前,需要先计算速度  这里的1000表示时间间隔,1000ms
velocityTracker.computeCurrentVelocity(1000);
int xVelocity = (int) velocityTracker.getXVelocity();
int yVelocity = (int) velocityTracker.getYVelocity();

最后，当不需要使用它的时候，需要调用clear方法来重置并回收内存：
velocityTracker.clear();
velocityTracker.recycle();
```

- GestureDetector: 手势检测,用于辅助检测用户的单击、滑动、长按、双击等行为。

```java
GestureDetector mGestureDetector = new GestureDetector(this);
//解决长按屏幕后无法拖动的现象
mGestureDetector.setIsLongpressEnabled(false);

//接着，接管目标View的onTouchEvent方法，在待监听View的onTouchEvent方法中添
加如下实现
boolean consume = mGestureDetector.onTouchEvent(event);
return consume;
```

做完了上面两步，我们就可以有选择地实现OnGestureListener和OnDoubleTapListener
中的方法了，这两个接口中的方法介绍如表3-1所示。

![image](271D8424E81142638D3E9D5112F8B2CB)

这里有一个建议供读者参考：如果只是监听滑动相关的，建议自己
在onTouchEvent中实现，如果要监听双击这种行为的话，那么就使用GestureDetector。

- Scroller: 使用它可以轻松实现弹性滑动.

如何使用Scroller呢？它的典型
代码是固定的，如下所示
```java
Scroller scroller = new Scroller(mContext);
// 缓慢滚动到指定位置
private void smoothScrollTo(int destX,int destY) {
    int scrollX = getScrollX();
    int delta = destX -scrollX;
    // 1000ms内滑向destX，效果就是慢慢滑动
    mScroller.startScroll(scrollX,0,delta,0,1000);
    invalidate();
}
@Override
public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
        scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
        postInvalidate();
    }
}
```

### 2.View的滑动

- 通过三种方式可以实现View的滑动
    - 通过View本身提供的scrollTo/scrollBy方法来实现滑动
    - 第二种是通过动画给View施加平移效果来实现滑动
    - 第三种是通过改变View的LayoutParams使得View重新布局从而实现滑动

#### 2.1 使用scrollTo/scrollBy

- scrollTo和scrollBy只能改变View内容的位置而不能改变View在布局中的位置

![image](A76B9D5779974F5B90FE7FC63A32324A)

#### 2.2 使用动画

> 主要是操作View的translationX和translationY属性.

```java
//View动画
val loadAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.animation_scroll)
btn_animation_scroll.startAnimation(loadAnimation)

//属性动画
btn_animation_scroll.animate().translationX(200f).translationY(200f).start()
```

#### 2.3 LayoutParams

```java
val layoutParams = btn_layout_params.layoutParams as? FrameLayout.LayoutParams
layoutParams?.leftMargin = layoutParams?.leftMargin?.plus(100)
//或者btn_layout_params.setLayoutParams(params)
btn_layout_params.requestLayout()
```

#### 2.4 View滑动总结

- scrollTo/scrollBy：操作简单，适合对View内容的滑动；
- 动画：操作简单，主要适用于没有交互的View和实现复杂的动画效果；
- 改变布局参数：操作稍微复杂，适用于有交互的View。


### 3. View的弹性滑动

#### 3.1 使用Scroller

原理: 调用startScroll()方法,其实里面并没有进行滑动,而是因为紧接着调用的invalidate(),导致View重绘.在View的draw方法里面又会去调用computeScroll方法.computeScroll是需要自己去实现的,computeScroll会去向Scroller获取当前的scrollX和scrollY(Scroller内部是根据时间去计算当前的scrollX和scrollY的值),然后又调用scrollTo,又invalidate(),又要重绘,如此反复.实现了弹性滑动.

View不断重绘,不断通过时间流逝来计算新的滑动位置,小幅度的滑动,最终形成了弹性滑动.

#### 3.2 通过动画

- 动画天生就自带弹性的属性,哈哈,自然没啥问题
- 说点利用动画来实现一些动画不能实现的效果->动画可以不作用于任何View上,而是通过它在具体的时间内完成了整个动画过程,我们可以获取这个值,从而干一些操作.比如通过ValueAnimator在1000ms内从0-100,然后我们可以根据这个来画一个进度条,在1000ms内到达100%.


模仿Scroller来实现View的弹性滑动
```java
final int startX = 0;
final int deltaX = 100;
ValueAnimator animator = ValueAnimator.ofInt(0,1).setDuration(1000);
animator.addUpdateListener(new AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        float fraction = animator.getAnimatedFraction();
        mButton1.scrollTo(startX + (int) (deltaX * fraction),0);
    }
});
animator.start();
```

#### 3.3 Handler.postDelayed

不断发生延时消息,不断scrollTo.发送消息无法精确地定时,系统的消息调度是需要时间的,并且所需时间不定.

#### 3.4 Thread#sleep

### 4.事件分发

> 主要是3个方法: dispatchTouchEvent(),onInterceptTouchEvent(),onTouchEvent()

- 从Activity的dispatchTouchEvent开始,然后传到PhoneWindow的superDispatchTouchEvent(),再传入DecorView的superDispatchTouchEvent(),即ViewGroup的dispatchTouchEvent.
- 在ViewGroup的dispatchTouchEvent()中,先调用onInterceptTouchEvent(),判断是否需要拦截事件.默认是不拦截的,继续向子View传递事件,找到被点击的响应子View控件,调用该View的dispatchTouchEvent.至此,完成了从ViewGroup向子View事件传递的过程,子View也可能是ViewGroup,但是过程是一样的,类似的递归下去. 当ViewGroup拦截了事件时,自己处理事件,调用自身的onTouch()->onTouchEvent()->performClick()->onClick()
- 当事件来到View这里,调用dispatchTouchEvent,调用View.onTouch(),如果这个方法返回true,那么事件已经被消费了,不用再继续往下传递了.如果返回false,那么消费事件,调用onTouchEvent().调用performClick(),调用onClick()

三者关系伪代码
```java
public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean consume = false;
    if (onInterceptTouchEvent(ev)) {
        consume = onTouchEvent(ev);
    } else {
        consume = child.dispatchTouchEvent(ev);
    }
    return consume;
}
```

### 5. 解决滑动冲突

这里借用刚哥 安卓开发艺术探索中的图叙述一下

![image](E6E1143324584FCFA131C79A91C41BDC)

对于场景1,当用户左右滑动时,让外部的View拦截点击事件,当用户上下滑动时让内部的View拦截点击事件. 如何判断用户是上下还是左右滑动: 用2个按下点与抬起点的距离差判断,如果横向多,那么就是横向滑动,如果竖向多,就是竖向滑动.

对于场景2和3,需要根据具体的业务场景来判断当时应该让谁拦截事件.

抛开具体的场景,滑动冲突其实有一种通用的解决方式.

#### 5.1  外部拦截法

外部拦截法,就是指点击事件都先经过父控件的处理,如果父控件需要这个事件就拦截,然后自己处理.不需要就不拦截,传递给子控件.

它的伪代码如下所示:

```java
public boolean onInterceptTouchEvent(MotionEvent event) {
    boolean intercepted = false;
    int x = (int) event.getX();
    int y = (int) event.getY();
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN: {
        intercepted = false;
        break;
    }
    case MotionEvent.ACTION_MOVE: {
        if (父容器需要当前点击事件) {
            intercepted = true;
        } else {
            intercepted = false;
        }
        break;
    }
    case MotionEvent.ACTION_UP: {
        intercepted = false;
        break;
    }
    default:
        break;
    }
    mLastXIntercept = x;
    mLastYIntercept = y;
    return intercepted;
}
```

