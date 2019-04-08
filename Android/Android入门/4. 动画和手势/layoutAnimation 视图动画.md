> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/5d811aaf4541

这是我这个系列的目录，有兴趣的可以看下： [android 动画系列 - 目录](https://www.jianshu.com/p/41049edc7bfc)

我的一贯原则，先上图看了再说：

![](https://upload-images.jianshu.io/upload_images/1785445-b48b31096ab80f27.gif)

android 视图动画这块其实 挺有意思的，应用的也是挺多的，效果就是在一个页面启动后，页面中的元素挨个执行入场动画，看着挺唬人的。简单来说就是可以在 ViewGroup 的添加一个入场动画，这个 ViewGroup 的所有子元素都会按照你设计的顺序执行一遍动画，至于子元素的范围目测是直接子元素才行，关于这块的内容其实挺老的，android 在很早就已经支持了，但是这块知识点算是冷门，详细学习过的人应该不多。

视图动画分 3 种：

*   layoutAnimation
*   gridLayoutAnimation
*   animateLayoutChanges
*   LayoutTransition

哈哈，说起 layoutAnimation 学习过的人应该有不少，但是说起后几个想必学习过的人就不多了，当然了从后几种使用的机会不是很多。

#### 区别

*   layoutAnimation：
    layoutAnimation 是在 ViewGroup 创建之后，显示时作用的，作用时间是：ViewGroup 的首次创建显示，之后再有改变就不行了。
    动画只能使用 tween 动画

*   gridLayoutAnimation：
    是给网格布局使用的，现在 gridLayoutAnimation 也不用了，这个大家就不用看了，想详细看也没事，看最下面的参考资料
    动画只能使用 tween 动画

*   animateLayoutChanges：
    这是在 ViewGroup 创建显示之后，内容改变时对于新的内容做动画，和 layoutAnimation 是配合使用的。

*   LayoutTransition：
    这个作用范围 = layoutAnimation + animateLayoutChanges。这个大家看源码应该可以看到，一般我是没看见有人用这个。这个这里我就不说了，同样的，详细在下面的参考资料里
    动画只能使用属性动画

* * *

#### layoutAnimation

layoutAnimation 是我们使用最多的了，他可以赋予页面一种页面展开的交互效果，当然他也是有限制的，只能给子 view 提供统一的动画效果，而不能给不同的 view 赋予不同的动画效果，这种需求就只能自己手动挨个 view 写了，实际还是使用 layoutAnimation 的，除非产品脑洞大开... 哈哈哈~~

layoutAnimation 最大的优点就是使用简单，google 提供了 2 种方式，xml 和代码，先来说 xml 的方式

** xml 实现 layoutAnimation**

步奏有 3：

*   先写一个 tween 动画 xml
*   在写一个专门的 layoutAnimation xml 文件
*   最后给根节点的 viewGroup 设置上这个你定义的 layoutAnimation

** layoutAnimation xml 定义：**

```
<?xml version="1.0" encoding="utf-8"?>
<layoutAnimation xmlns:android="http://schemas.android.com/apk/res/android"
                 android:animation="@anim/tran_left"
                 android:animationOrder="normal"
                 android:delay="0.3"
/>

```

属性参数很简单，没几个

*   animation 是你指定的动画
*   animationOrder 是子元素动画的顺序，有三种选项：nomal 丶 reverse 丶和 random，其中 normal 表示顺序显示，reverse 表示逆向显示，random 则表示随机播放入场动画。
*   delay 是子元素开始动画的时间延迟，比如子元素入场动画的时间周期为 300ms，那么 0.5 就代表每个子元素都需要延迟 150ms 才能播放入场动画。总体来说，第一个子元素延迟 150ms 开始播放入场动画，第二个子元素延迟 300ms 开始播放入场动画，依次类推。

这里使用的动画 xml 如下：

```
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
           android:duration="500"
           android:fromYDelta="100%p"
           android:toYDelta="0"
/>

```

好了到最后了，给根视图节点设置上就行了：

![](https://upload-images.jianshu.io/upload_images/1785445-956b4dc664e7588b.png)

效果图：

![](https://upload-images.jianshu.io/upload_images/1785445-3c61f12899f9a563.gif)

注意一点这是在启动页面上添加的效果，要是我们不再启动页上添加而放在另一个页面呢，速度上会不会有不同呢

非启动页效果图：

![](https://upload-images.jianshu.io/upload_images/1785445-cec6a7d470f3153e.gif)

实测有时候会在卡第一个和第二个 textview，之后的没事，有时候不会卡，我的机器配置还算不错，也没开别的软件，具体还是听见下面的 代码生成 layoutAnimation 的方式

** 代码实现 layoutAnimation**

我觉得使用代码生成 layoutAnimation 比在 xml 里设置要好些，用代码的方式没有再遇到卡的问题了，我们在 view 渲染完后再给他设置

```
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = (ViewGroup) findViewById(R.id.activity_main);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                // 添加布局动画
                addLayoutAnimation(rootView);
            }
        });

    }

    private void addLayoutAnimation(ViewGroup view) {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.tran_left);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setDelay(0.3f);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        view.setLayoutAnimation(layoutAnimationController);
        view.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Toast.makeText(MainActivity.this, "end!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

```

还可以设置监听动画哦，这是代码方式的效果图：

![](https://upload-images.jianshu.io/upload_images/1785445-0c88e2240483114d.gif)

大家注意哦，用代码的方式就不能在 xml 里设置了哦！

* * *

#### animateLayoutChanges

animateLayoutChanges 这个是在 ViewGroup 内容改变的时候对于改变的部分做动画，使用起来很简单，在你需要显示动画的 view xml 节点上设置 android:animateLayoutChanges="true" 就可以了，和 layoutAnimation 在使用上不冲突，不管是 xml 还是代码的方式都不冲突。但是这个 animateLayoutChanges 你不能指定动画，使用使用系统默认提供的淡入淡出的动画效果，先来看下效果图：

![](https://upload-images.jianshu.io/upload_images/1785445-be08216f34cff25c.gif)

页面的中间有一个线性布局，往里添加一些 view 进去，代码很简单我就不贴了，大伙看看图感受一下就 ok 了。有简单就有复杂，你要是想指定 viewGroup 内容改变时的动画可以去看 LayoutTransition 这块，提哦那个样这部分代码在下面的参考资料里，我就不说 LayoutTransition 了，要是说起来东西比较多，估计我也说不大好，还是看看比较好的资料把

* * *

#### 参考资料

*   [layoutAnimation 与 gridLayoutAnimation](https://link.jianshu.com?t=http://wiki.jikexueyuan.com/project/android-animation/11.html)
*   [animateLayoutChanges 与 LayoutTransition](https://link.jianshu.com?t=http://wiki.jikexueyuan.com/project/android-animation/12.html)