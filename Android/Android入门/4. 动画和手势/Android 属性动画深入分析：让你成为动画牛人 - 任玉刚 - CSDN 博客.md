> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/singwhatiwanna/article/details/17841165 版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/singwhatiwanna/article/details/17841165 <link rel="stylesheet" href="https://csdnimg.cn/release/phoenix/template/css/ck_htmledit_views-2c6a5211c9.css"> 转载请注明出处：[http://blog.csdn.net/singwhatiwanna/article/details/17841165](http://blog.csdn.net/singwhatiwanna/article/details/17841165)

## <a></a>前言

感谢你阅读本文，我坚信读完本文肯定不会让你失望的。想要做动画牛人？想要精通动画？那么本文所讲的内容都是你必须要掌握的。关于动画，我已经写了两篇博文，但是还是没有将动画描述全面，于是我写了本文，另外，我后面还会再写一篇属性动画的源码分析，通过这四篇博文，你将真正地成为动画牛人。

**Android 动画系列：**

[android 动画简介](http://blog.csdn.net/singwhatiwanna/article/details/9270275)

[Android 动画进阶—使用开源动画库 nineoldandroids](http://blog.csdn.net/singwhatiwanna/article/details/17639987)

[Android 属性动画深入分析：让你成为动画牛人](http://blog.csdn.net/singwhatiwanna/article/details/17841165)

[Android 源码分析—属性动画的工作原理](http://blog.csdn.net/singwhatiwanna/article/details/17853275)

## <a></a>我为什么要写这篇博文？

是分享精神，我对动画从了解到熟悉是经历了一个过程，而这一个过程是要花费时间的，也许是几天，也许是几个小时，总之没有至少若干小时的时间投入，你是无法熟悉动画的全部的。我花了大量时间来弄懂动画的整个逻辑，深知其中的辛苦，所以，我不想大家再像我这样，我想大家能够更快地熟悉并精通动画。通过本文，你将会深入了解 Android 动画并且从此没有动画再能难得了你。确切来说本文是深入分析属性动画，因为 View 动画和帧动画的功能有限也比较简单，没有太多值得分析的东西。

## <a></a>开篇

像设计模式一样，我们也提出一个问题来引出我们的内容。

#### 问题：

给 Button 加一个动画，让这个 Button 的宽度从当前宽度增加到 500px。

也许你会说，这很简单，用渐变动画就可以搞定，我们可以来试试，你能写出来吗？很快你就会恍然大悟，原来渐变动画根本不支持对宽度进行动画啊，没错，渐变动画只支持四种类型：平移（Translate）、旋转（Rotate）、缩放（Scale）、不透明度（Alpha）。当然你用 x 方向缩放（scaleX）可以让 Button 在 x 方向放大，看起来好像是宽度增加了，实际上不是，只是 Button 被放大了而已，而且由于只在 x 方向被放大，这个时候 Button 的背景以及上面的文本都被拉伸了，甚至有可能 Button 会超出屏幕。下面是效果图

![](https://img-blog.csdn.net/20140104145552937?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2luZ3doYXRpd2FubmE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

上述效果显然是很差的，而且也不是真正地对宽度做动画，不过，所幸我们还有属性动画，我们用属性动画试试

看 demo

```
    private void performAnimate() {        ObjectAnimator.ofInt(mButton, "width", 500).setDuration(5000).start();    }     @Override    public void onClick(View v) {        if (v == mButton) {            performAnimate();        }    }
```

上述代码运行一下发现没效果，其实没效果是对的，如果你随便传递一个属性过去，轻则没动画效果，重则程序直接 Crash。

#### 下面分析下属性动画的原理：

属性动画要求动画作用的对象提供该属性的 get 和 set 方法，属性动画根据你传递的该熟悉的初始值和最终值，以动画的效果多次去调用 set 方法，每次传递给 set 方法的值都不一样，确切来说是随着时间的推移，所传递的值越来越接近最终值。总结一下，你对 object 的属性 xxx 做动画，如果想让动画生效，要同时满足两个条件：

**1\. object 必须要提供 setXxx 方法，如果动画的时候没有传递初始值，那么还要提供 getXxx 方法，因为系统要去拿 xxx 属性的初始值（如果这条不满足，程序直接 Crash）**

**2\. object 的 setXxx 对属性 xxx 所做的改变必须能够通过某种方法反映出来，比如会带来 ui 的改变啥的（如果这条不满足，动画无效果但不会 Crash）**

以上条件缺一不可

那么为什么我们对 Button 的 width 属性做动画没有效果？这是因为 Button 内部虽然提供了 getWidth 和 setWidth 方法，但是这个 setWidth 方法并不是改变视图的大小，它是 TextView 新添加的方法，View 是没有这个 setWidth 方法的，由于 Button 继承了 TextView，所以 Button 也就有了 setWidth 方法。下面看一下这个 getWidth 和 setWidth 方法的源码：

```
    /**     * Makes the TextView exactly this many pixels wide.     * You could do the same thing by specifying this number in the     * LayoutParams.     *     * @see #setMaxWidth(int)     * @see #setMinWidth(int)     * @see #getMinWidth()     * @see #getMaxWidth()     *     * @attr ref android.R.styleable#TextView_width     */    @android.view.RemotableViewMethod    public void setWidth(int pixels) {        mMaxWidth = mMinWidth = pixels;        mMaxWidthMode = mMinWidthMode = PIXELS;         requestLayout();        invalidate();    }     /**     * Return the width of the your view.     *     * @return The width of your view, in pixels.     */    @ViewDebug.ExportedProperty(category = "layout")    public final int getWidth() {        return mRight - mLeft;    }
```

从源码可以看出，getWidth 的确是获取 View 的宽度的，而 setWidth 是 TextView 和其子类的专属方法，它的作用不是设置 View 的宽度，而是设置 TextView 的最大宽度和最小宽度的，这个和 TextView 的宽度不是一个东西，具体来说，TextView 的宽度对应 Xml 中的 android:layout_width 属性，而 TextView 还有一个属性 android:width，这个 android:width 属性就对应了 TextView 的 setWidth 方法。好吧，我承认我的这段描述有点混乱，但事情的确是这个样子的，而且我目前还没发现这个 android:width 属性有啥重要的用途，感觉好像没用似的，这里就不深究了，不然就偏离主题了。总之，TextView 和 Button 的 setWidth 和 getWidth 干的不是同一件事情，通过 setWidth 无法改变控件的宽度，所以对 width 做属性动画没有效果，对应于属性动画的两个条件来说，本例中动画不生效的原因是只满足了条件 1 未满足条件 2。

针对上述问题，Google 告诉我们有 3 中解决方法：

**1\. 给你的对象加上 get 和 set 方法，如果你有权限的话**

**2\. 用一个类来包装原始对象，间接为其提供 get 和 set 方法**

**3\. 采用 ValueAnimator，监听动画过程，自己实现属性的改变**

看起来有点抽象，不过不用担心，下面我会一一介绍。

## <a></a>对任何属性做动画

针对上面提出的三种解决方法，这里会给出具体的介绍：

#### 给你的对象加上 get 和 set 方法，如果你有权限的话

这个的意思很好理解，如果你有权限的话，加上 get 和 set 就搞定了，但是很多时候我们没权限去这么做，比如本文开头所提到的问题，你无法给 Button 加上一个合乎要求的 setWidth 方法，因为这是 Android SDK 内部实现的。这个方法最简单，但是往往是不可行的，这里就不对其进行更多分析了。

#### 用一个类来包装原始对象，间接为其提供 get 和 set 方法

这是一个很有用的解决方法，是我最喜欢用的，因为用起来很方便，也很好理解，下面将通过一个具体的例子来介绍它

```
    private void performAnimate() {        ViewWrapper wrapper = new ViewWrapper(mButton);        ObjectAnimator.ofInt(wrapper, "width", 500).setDuration(5000).start();    }     @Override    public void onClick(View v) {        if (v == mButton) {            performAnimate();        }    }     private static class ViewWrapper {        private View mTarget;         public ViewWrapper(View target) {            mTarget = target;        }         public int getWidth() {            return mTarget.getLayoutParams().width;        }         public void setWidth(int width) {            mTarget.getLayoutParams().width = width;            mTarget.requestLayout();        }    }
```

上述代码 5s 内让 Button 的宽度增加到 500px，为了达到这个效果，我们提供了 ViewWrapper 类专门用于包装 View，具体到本例是包装 Button，然后我们对 ViewWrapper 的 width 熟悉做动画，并且在 setWidth 方法中修改其内部的 target 的宽度，而 target 实际上就是我们包装的 Button，这样一个间接属性动画就搞定了。上述代码同样适用于一个对象的其他属性。下面看效果

![](https://img-blog.csdn.net/20140104165651984?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2luZ3doYXRpd2FubmE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

 ok，效果达到了，真正实现了对宽度做动画。

#### 采用 ValueAnimator，监听动画过程，自己实现属性的改变

首先说说啥是 ValueAnimator，ValueAnimator 本身不作用于任何对象，也就是说直接使用它没有任何动画效果。它可以对一个值做动画，然后我们可以监听其动画过程，在动画过程中修改我们的对象的属性值，这样也就相当于我们的对象做了动画。还是不太明白？没关系，下面用例子说明

```
    private void performAnimate(final View target, final int start, final int end) {        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);         valueAnimator.addUpdateListener(new AnimatorUpdateListener() {             //持有一个IntEvaluator对象，方便下面估值的时候使用            private IntEvaluator mEvaluator = new IntEvaluator();             @Override            public void onAnimationUpdate(ValueAnimator animator) {                //获得当前动画的进度值，整型，1-100之间                int currentValue = (Integer)animator.getAnimatedValue();                Log.d(TAG, "current value: " + currentValue);                 //计算当前进度占整个动画过程的比例，浮点型，0-1之间                float fraction = currentValue / 100f;                 //这里我偷懒了，不过有现成的干吗不用呢                //直接调用整型估值器通过比例计算出宽度，然后再设给Button                target.getLayoutParams().width = mEvaluator.evaluate(fraction, start, end);                target.requestLayout();            }        });         valueAnimator.setDuration(5000).start();    }     @Override    public void onClick(View v) {        if (v == mButton) {            performAnimate(mButton, mButton.getWidth(), 500);        }    }
```

上述代码的动画效果图和采用 ViewWrapper 是一样的，请参看上图。关于这个 ValueAnimator 我要再说一下，拿上例来说，它会在 5000ms 内将一个数从 1 变到 100，然后动画的每一帧会回调 onAnimationUpdate 方法，在这个方法里，我们可以获取当前的值（1-100），根据当前值所占的比例（当前值 / 100），我们可以计算出 Button 现在的宽度应该是多少，比如时间过了一半，当前值是 50，比例为 0.5，假设 Button 的起始宽度是 100px，最终宽度是 500px，那么 Button 增加的宽度也应该占总增加宽度的一半，总增加宽度是 500-100=400，所以这个时候 Button 应该增加宽度 400*0.5=200，那么当前 Button 的宽度应该为初始宽度 + 增加宽度（100+200=300）。上述计算过程很简单，其实它就是整型估值器 IntEvaluator 的内部实现，所有我们不用自己写了，直接用吧。

## <a></a>写在后面的话

到此为止，本文的分析基本完成，有几点是我想再说一下的。

1.View 动画（渐变动画）的功能是有限的，大家可以尝试使用属性动画

2\. 为了在各种安卓版本上使用属性动画，你需要采用 nineoldandroids，它是 GitHub 开源项目，jar 包和源码都可以在网上下到，如果下不到 jar 包，我可以发给大家

3\. 再复杂的动画都是简单动画的合理组合，再加上本文介绍的方法，可以对任何属性作用动画效果，也就是说你几乎可以做出任何动画

4\. 属性动画中的插值器（Interpolator）和估值器（TypeEvaluator）很重要，它是实现非匀速动画的重要手段，你应该试着搞懂它，最好你还能够自定义它们

5\. 如果你能把我这个动画系列博文都看一遍并且理解它，我认为你对动画绝对算得上精通，而且我不认为有面试官能够在动画上问倒你