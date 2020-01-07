> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5c0bd6b05188257c3045dc50

> 平时使用 ConstraintLayout, 断断续续的, 基本都是在自己的小 demo 里面使用. 公司的项目暂时还没有使用. 这次公司项目需要大改, 我决定用上这个 nice 的布局. 减少嵌套 (之前的老代码, 实在是嵌套得太深了.... 无力吐槽).

首先, ConstraintLayout 是一个新的布局, 它是直接继承自 ViewGroup 的, 所以在兼容性方面是非常好的. 官方称可以兼容到 API 9\. 可以放心食用.

#### 一、Relative positioning

先来看看下面一段简单示例:

<pre><android.support.constraint.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <Button
        android:id="@+id/btn1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="按钮1"/>

    <Button
        android:id="@+id/btn2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintLeft_toRightOf="@+id/btn1"
        android:text="按钮2"/>

</android.support.constraint.ConstraintLayout>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8bdf1324b?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

上面有一个简单的属性:`layout_constraintLeft_toRightOf`, 表示将按钮 2 放到按钮 1 的左边. 如果没有这一句属性, 那么两个按钮会重叠在一起, 就像 FrameLayout.

像这样的属性还有很多:

<pre>layout_constraintLeft_toLeftOf  我的左侧与你的左侧对齐
layout_constraintLeft_toRightOf  我的左侧与你的右侧对齐
layout_constraintRight_toLeftOf 我的右侧与你的左侧对齐
layout_constraintRight_toRightOf 我的右侧与你的右侧对齐
layout_constraintTop_toTopOf 我的顶部与你的顶部对齐
layout_constraintTop_toBottomOf 我的顶部与你的底部对齐 (相当于我在你下面)
layout_constraintBottom_toTopOf 
layout_constraintBottom_toBottomOf
layout_constraintBaseline_toBaselineOf 基线对齐
layout_constraintStart_toEndOf 我的左侧与你的右侧对齐
layout_constraintStart_toStartOf
layout_constraintEnd_toStartOf
layout_constraintEnd_toEndOf
复制代码
</pre>

上面的属性都非常好理解, 除了一个相对陌生的`layout_constraintBaseline_toBaselineOf`基线对齐. 咱们上代码:

<pre> <TextView
        android:id="@+id/btn1"
        android:text="按钮1"
        android:textSize="26sp"/>

    <TextView
        android:id="@+id/btn2"
        android:text="按钮2"
        app:layout_constraintBaseline_toBaselineOf="@+id/btn1"
        app:layout_constraintLeft_toRightOf="@+id/btn1"/>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8c0e37bab?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

一目了然, 相当于文字的基线是对齐了的. 如果没有加`layout_constraintBaseline_toBaselineOf`属性, 那么是下面这样的:

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8bd318c01?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 二、与父亲边缘对齐

当需要子 view 放在父 view 的底部或者最右侧时. 我们使用:

<pre><android.support.constraint.ConstraintLayout
        app:layout_constraintEnd_toEndOf="parent">

        <TextView
            android:text="按钮2"
            app:layout_constraintBottom_toBottomOf="parent"/>

    </android.support.constraint.ConstraintLayout>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8c0ed7483?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

<pre>app:layout_constraintBottom_toBottomOf="parent"  我的底部与父亲底部对齐
app:layout_constraintTop_toTopOf="parent"   我的顶部与父亲的顶部对齐
app:layout_constraintLeft_toLeftOf="parent"  我的左侧与父亲的左侧对齐
app:layout_constraintRight_toRightOf="parent"  我的右侧与父亲的右侧对齐
复制代码
</pre>

#### 三、居中对齐

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8bdbdd5f3?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

下面的 TextView, 与父亲左侧对齐, 与父亲右侧对齐, 所以, 最右, 它水平居中对齐.

<pre><TextView
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"/>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8bd756bad?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

可能你也想到了, 居中对齐其实就是 2 个对齐方式相结合. 最后产生的效果. 比如:

<pre>这是垂直居中
app:layout_constraintTop_toTopOf="parent"
app:layout_constraintBottom_toBottomOf="parent"
复制代码
</pre>

<pre>位于父亲的正中央
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintLeft_toLeftOf="parent"
app:layout_constraintRight_toRightOf="parent"
app:layout_constraintTop_toTopOf="parent"
复制代码
</pre>

#### 四、边距

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8f02ef48c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

边距和原来是一样的.

<pre>android:layout_marginStart
android:layout_marginEnd
android:layout_marginLeft
android:layout_marginTop
android:layout_marginRight
android:layout_marginBottom
复制代码
</pre>

举个例子:

<pre><TextView
    android:id="@+id/btn1"
    android:text="按钮1"
    android:textSize="26sp"/>
<TextView
    android:id="@+id/btn2"
    android:text="按钮2"
    android:layout_marginStart="40dp"
    app:layout_constraintLeft_toRightOf="@+id/btn1"/>
复制代码
</pre>

效果如下:

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8f034c0a3?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### Bias(偏向某一边)

上面的水平居中, 是使用的与父亲左侧对齐 + 与父亲右侧对齐. 可以理解为左右的有一种约束力, 默认情况下, 左右的力度是一样大的, 那么 view 就居中了.

当左侧的力度大一些时, view 就会偏向左侧. 就像下面这样.

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8f1ab0610?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

当我们需要改变这种约束力的时候, 需要用到如下属性:

<pre>layout_constraintHorizontal_bias  水平约束力
layout_constraintVertical_bias  垂直约束力
复制代码
</pre>

来举个例子:

<pre><android.support.constraint.ConstraintLayout
    <Button
        android:text="按钮1"
        app:layout_constraintHorizontal_bias="0.3"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"/>
</android.support.constraint.ConstraintLayout>        
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e8f60700ae?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

可以看到, 左右有 2 根约束线. 左侧短一些. 那么就偏向于左侧

#### 五、Circular positioning (Added in 1.1)

> 翻译为: 圆形的定位 ?

这个就比较牛逼了, 可以以角度和距离约束某个 view 中心相对于另一个 view 的中心,

可能比较抽象, 来看看谷歌画的图:

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e90d61b977?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

他的属性有:

<pre>layout_constraintCircle ：引用另一个小部件ID
layout_constraintCircleRadius ：到其他小部件中心的距离
layout_constraintCircleAngle ：小部件应该处于哪个角度（以度为单位，从0到360）
复制代码
</pre>

举个例子:

<pre><Button
    android:id="@+id/btn1"
    android:text="按钮1"/>
<Button
    android:text="按钮2"
    app:layout_constraintCircle="@+id/btn1"
    app:layout_constraintCircleRadius="100dp"
    app:layout_constraintCircleAngle="145"/>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e90f38d12e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 六、Visibility behavior 可见性行为

当一个 View 在 ConstraintLayout 中被设置为 gone, 那么你可以把它当做一个点 (这个 view 所有的 margin 都将失效). 这个点是假设是实际存在的.

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e91ce5ac6d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

举个例子:

<pre><Button
    android:id="@+id/btn1"
    android:text="按钮1"
    android:textSize="26sp"/>

<Button
    android:id="@+id/btn2"
    android:layout_marginStart="20dp"
    android:text="按钮2"
    android:visibility="gone"
    app:layout_constraintLeft_toRightOf="@+id/btn1"/>

<Button
    android:id="@+id/btn3"
    android:layout_marginStart="20dp"
    android:text="按钮3"
    app:layout_constraintLeft_toRightOf="@+id/btn2"/>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e9200c7d66?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

可以看到, 按钮 3 和按钮 1 中间的 margin 只有 20.

再举个例子:

<pre> <Button
    android:id="@+id/btn2"
    android:layout_marginStart="20dp"
    android:text="按钮2"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent"/>

<Button
    android:id="@+id/btn3"
    android:text="按钮3"
    app:layout_constraintLeft_toRightOf="@+id/btn2"
    app:layout_constraintTop_toTopOf="@+id/btn2"
    app:layout_constraintBottom_toBottomOf="@+id/btn2"/>
复制代码
</pre>

我将按钮 3 放到按钮 2 的右侧, 这时是没有给按钮 2 加`android:visibility="gone"`的.

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e92fd74fb7?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

现在我们来给按钮 2 加上`android:visibility="gone"`

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e9341c9007?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

这时, 按钮 2 相当于缩小成一个点, 那么按钮 3 还是在他的右侧不离不弃.

#### 七、Dimensions constraints 尺寸限制

在 ConstraintLayout 中, 可以给一个 view 设置最小和最大尺寸.

属性如下 (这些属性只有在给出的宽度或高度为 wrap_content 时才会生效):

<pre>android:minWidth 设置布局的最小宽度
android:minHeight 设置布局的最小高度
android:maxWidth 设置布局的最大宽度
android:maxHeight 设置布局的最大高度
复制代码
</pre>

#### 八、Widgets dimension constraints 宽高约束

平时我们使用`android:layout_width和 android:layout_height`来指定 view 的宽和高.

在 ConstraintLayout 中也是一样, 只不过多了一个 0dp.

*   使用长度, 例如
*   使用 wrap_content，view 计算自己的大小
*   使用 0dp，相当于 “MATCH_CONSTRAINT”

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e93f53da87?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

下面是例子

<pre><Button
    android:id="@+id/btn1"
    android:layout_width="100dp"
    android:layout_height="wrap_content"
    android:text="按钮1"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"/>

<Button
    android:id="@+id/btn2"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:text="按钮2"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toBottomOf="@+id/btn1"/>

<Button
    android:id="@+id/btn3"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_marginStart="60dp"
    android:text="按钮3"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toBottomOf="@+id/btn2"/>
复制代码
</pre>

展示出来的是:

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e960e1fb2b?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 九、WRAP_CONTENT：强制约束（在 1.1 中添加）

当一个 view 的宽或高, 设置成 wrap_content 时, 如果里面的内容实在特别宽的时候, 他的约束会出现问题. 我们来看一个小栗子:

<pre><Button
    android:id="@+id/btn1"
    android:layout_width="100dp"
    android:layout_height="wrap_content"
    android:text="Q"/>

<Button
    android:id="@+id/btn2"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV"
    app:layout_constraintLeft_toRightOf="@id/btn1"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toBottomOf="@id/btn1"/>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e96b98b2bb?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

从右侧的图片可以看出, 按钮 2 里面的内容确实是在按钮 1 的内容的右侧. 但是按钮 2 整个来说, 却是没有整个的在按钮 1 的右侧.

这时需要用到下面 2 个属性

<pre>app:layout_constrainedWidth=”true|false”
app:layout_constrainedHeight=”true|false”
复制代码
</pre>

给按钮 2 加一个`app:layout_constrainedWidth="true"`, 来看效果:

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e96d359cd0?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

哈哈, 又看到了我们想要的效果. 爽歪歪.

#### 十、MATCH_CONSTRAINT 尺寸（在 1.1 中添加）

当一个 view 的长宽设置为 MATCH_CONSTRAINT(即 0dp) 时, 默认是使该 view 占用所有的可用的空间. 这里有几个额外的属性

<pre>layout_constraintWidth_min和layout_constraintHeight_min：将设置此维度的最小大小
layout_constraintWidth_max和layout_constraintHeight_max：将设置此维度的最大大小
layout_constraintWidth_percent和layout_constraintHeight_percent：将此维度的大小设置为父级的百分比
复制代码
</pre>

这里简单举个百分比的例子: 居中并且 view 的宽是父亲的一半

<pre> <Button
    android:id="@+id/btn1"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:text="Q"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintWidth_percent="0.5"/>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e973ddd41f?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

It's so easy! 这极大的减少了我们的工作量.

**注意**

*   百分比布局是必须和 MATCH_CONSTRAINT(0dp) 一起使用
*   `layout_constraintWidth_percent 或layout_constraintHeight_percent`属性设置为 0 到 1 之间的值

#### 十一、按比例设置宽高 (Ratio)

可以设置 View 的宽高比例, 需要将至少一个约束维度设置为 0dp（即`MATCH_CONSTRAINT`）, 再设置`layout_constraintDimensionRatio`.

举例子:

<pre><Button
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:text="按钮"
    app:layout_constraintDimensionRatio="16:9"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent"/>
复制代码
</pre>

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e983f9c8c2?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

该比率可表示为：

*   浮点值，表示宽度和高度之间的比率
*   “宽度：高度” 形式的比率

如果两个尺寸都设置为 MATCH_CONSTRAINT（0dp），也可以使用比率。在这种情况下，系统设置满足所有约束的最大尺寸并保持指定的纵横比。要根据另一个特定边的尺寸限制一个特定边，可以预先附加 W,“或” H, 分别约束宽度或高度。例如，如果一个尺寸受两个目标约束（例如，宽度为 0dp 且以父节点为中心），则可以指示应该约束哪一边，通过 在比率前添加字母 W（用于约束宽度）或 H（用于约束高度），用逗号分隔：

<pre><Button android:layout_width="0dp"
   android:layout_height="0dp"
   app:layout_constraintDimensionRatio="H,16:9"
   app:layout_constraintBottom_toBottomOf="parent"
   app:layout_constraintTop_toTopOf="parent"/>
复制代码
</pre>

上面的代码将按照 16：9 的比例设置按钮的高度，而按钮的宽度将匹配父项的约束。

#### 十二、Chains（链）

设置属性 layout_constraintHorizontal_chainStyle 或 layout_constraintVertical_chainStyle 链的第一个元素时，链的行为将根据指定的样式（默认值 CHAIN_SPREAD）更改。

*   CHAIN_SPREAD - 元素将展开（默认样式）
*   加权链接 CHAIN_SPREAD 模式，如果设置了一些小部件 MATCH_CONSTRAINT，它们将分割可用空间
*   CHAIN_SPREAD_INSIDE - 类似，但链的端点不会分散
*   CHAIN_PACKED - 链条的元素将被包装在一起。然后，子项的水平或垂直偏差属性将影响打包元素的定位

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e989117f96?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

下面是一个类似 LinearLayout 的 weight 的效果, 需要用到`layout_constraintHorizontal_weight`属性:

<pre><Button
    android:id="@+id/btn1"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:text="A"
    app:layout_constraintEnd_toStartOf="@id/btn2"
    app:layout_constraintHorizontal_chainStyle="spread"
    app:layout_constraintHorizontal_weight="1"
    app:layout_constraintStart_toStartOf="parent"/>

<Button
    android:id="@+id/btn2"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:text="按钮2"
    app:layout_constraintEnd_toStartOf="@id/btn3"
    app:layout_constraintHorizontal_weight="2"
    app:layout_constraintStart_toEndOf="@id/btn1"/>

<Button
    android:id="@+id/btn3"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:text="问问"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintHorizontal_weight="3"
    app:layout_constraintStart_toEndOf="@id/btn2"/>
复制代码
</pre>

例子的效果图如下:

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e993557792?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 十三、Guideline

> 这是一个虚拟视图

Guideline 可以创建相对于 ConstraintLayout 的水平或者垂直准线. 这根辅助线, 有时候可以帮助我们定位.

<pre>layout_constraintGuide_begin   距离父亲起始位置的距离（左侧或顶部）
layout_constraintGuide_end    距离父亲结束位置的距离（右侧或底部）
layout_constraintGuide_percent    距离父亲宽度或高度的百分比(取值范围0-1)
复制代码
</pre>

我们拿辅助线干嘛??? 比如有时候, 可能会有这样的需求, 有两个按钮, 在屏幕中央一左一右. 如果是以前的话, 我会搞一个 LinearLayout,. 然后将 LinearLayout 居中, 然后按钮一左一右.

效果图如下:

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e99a602498?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

现在我们使用 Guideline 的话, 就超级方便了, 看代码:

<pre><!--水平居中-->
<android.support.constraint.Guideline
    android:id="@+id/gl_center"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    app:layout_constraintGuide_percent="0.5"/>

<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="按钮1"
    app:layout_constraintEnd_toStartOf="@id/gl_center"/>

<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="按钮2"
    app:layout_constraintLeft_toRightOf="@id/gl_center"/>
复制代码
</pre>

#### 十四、Barrier

> 虚拟视图

Barrier 是一个类似于屏障的东西. 它和 Guideline 比起来更加灵活. 它可以用来约束多个 view.

比如下面的姓名和联系方式, 右侧的 EditText 是肯定需要左侧对齐的, 左侧的 2 个 TextView 可以看成一个整体, Barrier 会在最宽的那个 TextView 的右边, 然后右侧的 EditText 在 Barrier 的右侧.

![](https://user-gold-cdn.xitu.io/2018/12/8/1678e3e99cc8a851?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

Barrier 有 2 个属性

*   **barrierDirection**，取值有 top、bottom、left、right、start、end，用于控制 Barrier 相对于给定的 View 的位置。比如在上面的栗子中，Barrier 应该在 姓名 TextView 的右侧，因此这里取值 right（也可 end，可随意使用. 这个 right 和 end 的问题, 其实在 RelativeLayout 中就有体现, 在 RelativeLayout 中写 left 或者 right 时会给你一个警告, 让你换成 start 和 end）。
*   **constraint_referenced_ids**，取值是要依赖的控件的 id（不需要 @+id/）。Barrier 将会使用 ids 中最大的一个的宽（高）作为自己的位置。

**ps**: 这个东西有一个小坑, 如果你写完代码, 发现没什么问题, 但是预览出来的效果却不是你想要的. 这时, 运行一下程序即可. 然后预览就正常了, 在手机上展示的也是正常的.

例子的代码如下 (如果预览不正确, 那么一定要运行一下, 不要怀疑是自己代码写错了):

<pre><TextView
    android:id="@+id/tv_name"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="姓名:"
    app:layout_constraintBottom_toBottomOf="@id/tvTitleText"/>

<TextView
    android:id="@+id/tv_phone"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="联系方式:"
    app:layout_constraintBottom_toBottomOf="@id/tvContentText"
    app:layout_constraintTop_toBottomOf="@+id/tv_name"/>

<EditText
    android:id="@+id/tvTitleText"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="null"
    android:text="张三"
    android:textSize="14sp"
    app:layout_constraintStart_toEndOf="@+id/barrier2"/>

<EditText
    android:id="@+id/tvContentText"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="null"
    android:text="xxxxxxxxxxxxxxx"
    android:textSize="14sp"
    app:layout_constraintStart_toEndOf="@+id/barrier2"
    app:layout_constraintTop_toBottomOf="@+id/tvTitleText"/>

<android.support.constraint.Barrier
    android:id="@+id/barrier2"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:barrierDirection="right"
    app:constraint_referenced_ids="tv_name,tv_phone"/>
复制代码
</pre>

#### 十五、Group

> 固定思议, 这是一个组. 这也是一个虚拟视图.

可以把 View 放到里面, 然后 Group 可以同时控制这些 view 的隐藏.

<pre><android.support.constraint.Group
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:visibility="gone"
    app:constraint_referenced_ids="btn1,btn2"/>

<Button
    android:id="@+id/btn1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="按钮1"/>

<Button
    android:id="@+id/btn2"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="按钮2"
    app:layout_constraintTop_toBottomOf="@id/btn1"/>
复制代码
</pre>

*   Group 有一个属性`constraint_referenced_ids`, 可以将那些需要同时隐藏的 view 丢进去.
*   别将 view 放 Group 包起来. 这样会报错, 因为 Group 只是一个不执行 onDraw() 的 View.
*   使用多个 Group 时，尽量不要将某个 View 重复的放在 多个 Group 中，实测可能会导致隐藏失效.

#### 十六、何为虚拟视图

上面我们列举的虚拟视图一共有:

*   Guideline
*   Barrier
*   Group

> 来我们看看源码

<pre>//Guideline

public class Guideline extends View {
public Guideline(Context context) {
    super(context);
    //这个8是什么呢?  
    //public static final int GONE = 0x00000008;
    //其实是View.GONE的值
    super.setVisibility(8);
}

public Guideline(Context context, AttributeSet attrs) {
    super(context, attrs);
    super.setVisibility(8);
}

public Guideline(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    super.setVisibility(8);
}

public Guideline(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr);
    super.setVisibility(8);
}

//可见性永远为GONE
public void setVisibility(int visibility) {
}

//没有绘画
public void draw(Canvas canvas) {
}

//大小永远为0
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    this.setMeasuredDimension(0, 0);
}
复制代码
</pre>

我们看到 Guideline 其实是一个普通的 View, 然后在构造函数里将自己设置为 GONE

*   并且 setVisibility() 为空方法, 该 View 就永远为 GONE 了.
*   draw() 方法为空, 意思是不用去绘画.
*   onMeasure() 中将自己长宽设置成 0.

综上所述, 我觉得这个 Guideline 就是一个不可见的且不用测量, 不用绘制, 那么我们就可以忽略其绘制消耗.

然后 Barrier 和 Group 都是继承自 ConstraintHelper 的, ConstraintHelper 是一个 View.ConstraintHelper 的 onDraw() 和 onMeasure() 如下:

<pre>public void onDraw(Canvas canvas) {
}

protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //mUseViewMeasure一直是false,在Group中用到了,但是还是将它置为false了.
    if (this.mUseViewMeasure) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    } else {
        this.setMeasuredDimension(0, 0);
    }

}
复制代码
</pre>

哈哈, 其实是和 Guideline 一样的嘛, 还是可以忽略其带来的性能消耗嘛. 上面的 mUseViewMeasure 一直是 false, 所以长宽一直为 0.

所以我们可以将 Guideline,Barrier,Group 视为虚拟试图, 因为它们几乎不会带来多的绘制性能损耗. 我是这样理解的.

#### 十七、Optimizer 优化 (add in 1.1)

可以通过将标签 app：layout_optimizationLevel 元素添加到 ConstraintLayout 来决定应用哪些优化。这个我感觉还处于实验性的阶段, 暂时先别用.. 哈哈

使用方式如下:

<pre><android.support.constraint.ConstraintLayout 
    app:layout_optimizationLevel="standard|dimensions|chains"
复制代码
</pre>

*   none：不优化
*   standard：默认, 仅优化直接和障碍约束
*   direct：优化直接约束
*   barrier：优化障碍约束
*   chain：优化链条约束
*   dimensions: 优化维度测量，减少匹配约束元素的度量数量

#### 总结

我把一些常用的属性和怎么用都列举出来, 方便大家查阅. 如有不对的地方, 欢迎指正.