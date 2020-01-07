
### View坐标系

搞懂下图,即可

![](http://olg7c0d2n.bkt.clouddn.com/18-8-29/4561639.jpg)

### View的滑动

在这里主要讲解6种滑动方法，分别是layout（）、offsetLeftAndRight（）与
offsetTopAndBottom（）、LayoutParams、动画、scollTo 与 scollBy，以及Scroller。

**layout()方法**

动态计算view应该放到哪个位置,然后进行调用layout方法进行放置view

**offsetLeftAndRight() 与 offsetTopAndBottom()**

这两种方法和layout()方法差不多

**LayoutParams()改变布局参数**

LayoutParams主要保存了一个View的布局参数,我们可以通过改变leftMargin或者topMargin等来改变View的位置

**动画**

动画可以移动View,使用属性动画比较方便

```
ObjectAnimator.ofFloat(view,"translationX",0,300).setDuration(1000).start();
```

**scrollTo()与scollBy()**

都表示移动.scrollTo(x,y)表示移动到一个具体的坐标点.而scrollBy(dx,dy)则表示移动的增量为dx、dy.
其中，scollBy最终也是要调用scollTo的.

**Scroller**

有过度效果的滑动,它需要与View的computeScroll（）方法配合才能实现弹性滑动的效果。
接下来重写computeScroll（）方法，系统会在绘制View的时候在draw（）方法中调用该方法。在这个
方法中，我们调用父类的scrollTo（）方法并通过Scroller来不断获取当前的滚动值，每滑动一小段距离我们
就调用invalidate（）方法不断地进行重绘，重绘就会调用computeScroll（）方法，这样我们通过不断地移动
一个小的距离并连贯起来就实现了平滑移动的效果。
```java
@Override
public void computeScroll(){
    super.computeScroll();
    if(mScroller.computeScrollOffset()){
        ((View)getParent()).scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
        invalidate();
    }
}
```
我们在 CustomView 中写一个 smoothScrollTo 方法，调用 Scroller 的 startScroll（）方法，在2000ms内
沿X轴平移delta像素，代码如下所示：
```java
public void smoothScrollTo(int destX,int destY){
    int scrollX = getScrollX();
    int delta = destX-scrollX;
    mScroller.startScroll(scrollX,0,delta,0,2000);
    invalidate();
}
```
最后我们在ViewSlideActivity.java中调用CustomView的smoothScrollTo（）方法。这里我们设定
CustomView沿着X轴向右平移400像素。
mCustomView.smoothScrollTo(-400,0);

### 属性动画

3.0之后推出属性动画,为了弥补View动画的缺陷(其不具有交互性).在
Animator框架中使用最多的就是AnimatorSet和ObjectAnimator配合：使用 ObjectAnimator 进行更精细化的控
制，控制一个对象和一个属性值，而使用多个ObjectAnimator组合到AnimatorSet形成一个动画。属性动画通
过调用属性get、set方法来真实地控制一个View的属性值，因此，强大的属性动画框架基本可以实现所有的
动画效果。

**1. ObjectAnimator**

ObjectAnimator 是属性动画最重要的类，创建一个 ObjectAnimator 只需通过其静态工厂类直接返还一个
ObjectAnimator对象。参数包括一个对象和对象的属性名字，但这个属性必须有get和set方法，其内部会通
过Java反射机制来调用set方法修改对象的属性值。下面看看平移动画是如何实现的，代码如下所示：

```java
ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(view,"translationX",200);
mObjectAnimator.setDuration(300);
mObjectAnimator.start();
```

常用属性值:
- translationX和translationY：用来沿着X轴或者Y轴进行平移。
- rotation、rotationX、rotationY：用来围绕View的支点进行旋转。
- PrivotX和PrivotY：控制View对象的支点位置，围绕这个支点进行旋转和缩放变换处理。默认该支点
位置就是View对象的中心点。
- alpha：透明度，默认是1（不透明），0代表完全透明。
- x和y：描述View对象在其容器中的最终位置。

**属性必须有get和set方法,不然ObjectAnimator就无法生效.**

如果一个属性没有get、set方法，也可以通过自定义一个属性类或包装类来间
接地给这个属性增加get和set方法。现在来看看如何通过包装类的方法给一个属性增加get和set方法，代码如
下所示：
```java
private static class MyView{
    private View mTarget;
    private MyView(View target){
        this.mTarget = target;
    }
    public int getWidth(){
        return mTarget.getLayoutParams().width;
    }
    public void setWidth(int width){
        mTarget.getLayoutParams().width = width;
        mTarget.requestLayout();
    }
}
```

使用时只需要操作包类就可以调用get、set方法了.
```java
MyView myView = MyView(mButton);
ObjectAnimator.onInt(myView,"width",500).setDuration(1000).start();
```

**2. ValueAnimator**

ValueAnimator不提供任何动画效果，它更像一个数值发生器，用来产生有一定规律的数字，从而让调
用者控制动画的实现过程。通常情况下，在ValueAnimator的AnimatorUpdateListener中监听数值的变化，从
而完成动画的变换，代码如下所示：

**3. 组合动画 AnimatorSet**

AnimatorSet  类提供了一个  play（）方法，如果我们向这个方法中传入一个  Animator  对象
（ValueAnimator或ObjectAnimator），将会返回一个AnimatorSet.Builder的实例。

**4. 组合动画——PropertyValuesHolder**

除了上面的AnimatorSet类，还可以使用PropertyValuesHolder类来实现组合动画。不过这个组合动画就
没有上面的丰富了，使用PropertyValuesHolder类只能是多个动画一起执行。当然我们得结合
ObjectAnimator.ofPropertyValuesHolder（Object target，PropertyValuesHolder…values）；方法来使用。其第
一个参数是动画的目标对象；之后的参数是PropertyValuesHolder类的实例，可以有多个这样的实例

**5. 在xml中使用属性动画**

和View动画一样，属性动画也可以直接写在XML中。在res文件中新建animator文件.