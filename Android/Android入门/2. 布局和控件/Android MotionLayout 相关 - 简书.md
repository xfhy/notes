> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/54a6e2568cdd

I / O '18 提到了 MotionLayout，当时还没有正式发布前段时间，在今年的 6 月 26 日正式发布了 ConstraintLayout 的 2.0alpha 版，也算正式推出了 MotionLayout。 MotionLayout 是 ConstraintLayout 的子类，它具有 ConstraintLayout 的所有属性。MotionLayout 用来处理两个 ConstraintSet 之间的切换，并在根据两个 ConstraintSet 的 CustomAttribute 参数来自动生成切换动画，关于 ConstraintSet 下面会讨论。同时 MotionLayout 所增加的是可以直接通过触摸屏幕来控制动画的运行进度。也就是说 MotionLayout 会管理你的触摸事件通过跟踪手指的速度，并将其与系统中的视图速度相匹配。从而可以自然地在两者之间通过触摸滑动平稳过渡。并且在动画里面加入了关键帧的概念，使得其自动生成动画在运行时某一阶段会运行到关键帧的状态。同时 MotionLayout 支持在 XML 中完全描述一个复杂的动画，而不需要通过 Java 代码来实现。

![](http://upload-images.jianshu.io/upload_images/8398510-798827b3b4d16922)

ConstraintSet 之间切换

ConstraintLayout 中的动画要借助于 ConstraintSet。ConstraintSet 是一个轻量级对象，表示 ConstraintLayout 中所有子元素的 constraints，margins 和 padding 。当将 ConstraintSet 应用于显示 ConstraintLayout 时，布局会使用 ConstraintSet 中的约束来更新对应 ConstraintLayout 中的。ConstraintSet 仅为视图的大小和位置设置动画，不会为其他属性设置动画（例如颜色）。

下面的代码示例显示了如何动画将单个按钮移动到屏幕底部：

```
public class MainActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyframe_one);
        constraintLayout = findViewById(R.id.constraint_layout);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateToKeyframeTwo();
            }
        });
    }

    void animateToKeyframeTwo() {

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.load(this, R.layout.keyframe_two); 
        TransitionManager.beginDelayedTransition(constraintLayout); 
        constraintSet.applyTo(constraintLayout);
    }
}




```

![](http://upload-images.jianshu.io/upload_images/8398510-fe1d2b785723957a)

ConstraintSet 动画

这边要注意一下，只会运行 R.layout.keyframe_two 中与 R.layout.keyframe_one 中 id 对应的动画。不会出现 R.layout.keyframe_two 中有而 R.layout.keyframe_one 中没有的视图，也不会对 R.layout.keyframe_one 有但 R.layout.keyframe_two 中没有的视图有任何效果。

前面已经演示了怎么对 ConstraintLayout 布局设置动画，现在来讨论下 MotionLayout 布局下的动画。

3.1 MotionScene
---------------

与通常的布局不同，MotionLayout 所做的约束保存在一个单独的 XML 文件 MotionScene 中，该文件存储在您的 res/xml 目录中。

![](http://upload-images.jianshu.io/upload_images/8398510-06c569da951edf6f)

MotionScene 包含内容

MotionScene 文件可以包含指定动画所需的全部内容，例如前面提到的 ConstraintSets、ConstraintSets 直接的过渡、关键帧、触摸处理等等。

3.2 创建动画简单流程
------------

### 3.2.1 先决条件

① Android Studio 3.2.0 或更高版本

② 运行 Android API 等级 21 或更高版本的设备或模拟器

③ 添加依赖：

```
implementation 'com.android.support:appcompat-v7:27.0.2'

implementation 'com.android.support.constraint:constraint-layout:2.0.0-alpha1'



```

或者

```
implementation 'androidx.appcompat:appcompat:1.0.0-beta1'

implementation 'androidx.constraintlayout:constraintlayout:2.0.0-alpha1'



```

这边要说明一下 androidx 也是今年 IO 刚刚推出的一个依赖，用来替代之前的 com.android.support 依赖。添加了它就不用添加一大堆 v4、v7、v13 等依赖了。

### 3.2.2 定义布局

前面提到过 MotionLayout 是 ConstraintLayout 的子类，所以 MotionLayout 可以直接替换 ConstraintLayout。因为 ConstraintLayout 有的功能 MotionLayout 都有。

### 3.2.3 创建 MotionScene

这一步是 MotionLayout 的关键, 在 res 下的 xml 文件夹中创建 MotionScene。其实在 MotionLayout 中可以不用添加想进行动画的视图的约束，而将约束放在 ConstraintSet 中，在将 ConstraintSet 放在 MotionScene 中。

```
<MotionScene

    xmlns:android="http://schemas.android.com/apk/res/android"

    xmlns:app="http://schemas.android.com/apk/res-auto">

    <ConstraintSet android:id="@+id/starting_set">

        <Constraint android:id="@+id/actor"

            app:layout_constraintBottom_toBottomOf="parent"

            app:layout_constraintRight_toRightOf="parent"

            android:layout_width="60dp"

            android:layout_height="60dp"/>

    </ConstraintSet >

    <ConstraintSet  android:id="@+id/ending_set" >

        <Constraint android:id="@+id/actor"

            app:layout_constraintTop_toTopOf="parent"

            app:layout_constraintLeft_toLeftOf="parent"

            android:layout_width="60dp"

            android:layout_height="60dp"/>

    </ConstraintSet >

</MotionScene>



```

这边需要注意的是每个 ConstraintSet 里面的元素必须始终指定所需的位置和所需的大小。它会覆盖任何以前设置的布局信息。并且里面的 id 和 MotionLayout 中的视图的 id 要对应才会有反应。

为了帮助 MotionLayout 的视图理解必须约束集的顺序，需要创建一个 Transition 元素。通过使用其直观命名 constraintSetStart 和 constraintSetEnd 属性，可以指定首先应用哪个集合以及最后应用哪个集合。该 Transition 元素还允许指定动画的持续时间。

```
// 放在上面的<MotionScene> 和</MotionScene>之中和ConstraintSet 标签平级。

<Transition

    android:id="@+id/my_transition"

    app:constraintSetStart="@+id/starting_set"

    app:constraintSetEnd="@+id/ending_set"

    app:duration="2000"/>



```

此时，一个简单的 MotionScene 完成。但是此时任然没有和 MotionLayout 进行绑定。需要给 MotionLayout 添加 app:layoutDescription 属性来将上面的 MotionScene 绑定：

```
app:layoutDescription="@xml/my_scene"



```

### 3.2.4 启动动画

运行应用程序时，MotionLayout 视图将自动将 constraintSetStart 属性中指定的约束集设置到自己身上。因此，要启动动画，需要做的就是调用 transitionToEnd() 方法从而实现 ConstraintSet 之间的转换：

```
motion_container.transitionToEnd();



```

![](http://upload-images.jianshu.io/upload_images/8398510-7946141931b2f6b0)

动画效果

### 3.2.5 动画执行进度监听

可以通过给 MotionLayout 设置监听器来监听动画进度, 和动画完成时的回调：

```
motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {

            @Override

            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

                seekBar.setProgress((int)(v*100));

            }

            @Override

            public void onTransitionCompleted(MotionLayout motionLayout, int i)             {

            }

});



```

上面对进度的监听通过 seekbar 表示出来

![](http://upload-images.jianshu.io/upload_images/8398510-e6e6449be27b4e34)

动画进度监听

3.3 关键帧（Key Frames）
-------------------

在上面的动画中，Button 小部件看起来像在直线的路径中移动。这是因为 MotionLayout 的视图此时其实只有两个关键帧：起始帧 Button 位于屏幕的右下角，终点帧 Button 位于屏幕的左上角。如果要改变路径的形状，则必须提供一些介于起点和终点之间关键。

在开始创建关键帧之前，必须将 KeyFrameSet 标签添加到 MotionScene 之中。可以自由创建任意数量的关键帧。

```
<KeyFrameSet >

            ...

</KeyFrameSet >



```

MotionLayout 视图支持许多不同类型的关键帧。这里使用其中两种类型：KeyPosition 和 KeyCycle 。

### 3.3.1 KeyPosition

KeyPosition 可以帮助视图改变运动路径的形状。创建它们时，请确保提供目标视图的 ID，沿时间轴的位置，可以是 0 到 100 之间的任意数字，以及指定 X 或 Y 坐标已经运行到的百分比。可以设置 type 参数指出坐标是相对于实际的 X 或 Y 轴，还是相对于路径本身。

```
<KeyFrameSet >

    <KeyPosition

    app:target="@+id/button"

    app:framePosition="30"

    app:type="deltaRelative"

    app:percentX="0.85"/>

    <KeyPosition

    app:target="@+id/button"

    app:framePosition="60"

    app:type="deltaRelative"

    app:percentX="1"/>

</KeyFrameSet>



```

上面第一个 KeyPosition 代表 button 按钮在运行道 30% 的时候，相对于运行轨迹 x 已经运行了 85% 了。第二个 KeyPosition 代表 button 按钮在运行道 60% 的时候，相对于运行轨迹 x 已经运行了 100% 了. 效果如下，这样就可以避开和 seekbar 的冲突了：

![](http://upload-images.jianshu.io/upload_images/8398510-928e784e5d79ba19)

KeyPosition 关键帧

### 3.3.2 KeyCycle

KeyCycle 用来给动画添加振动。可以通过提供诸如要使用的波形和波形周期等详细信息来配置 KeyCycle。下面是 KeyCycle 支持的各种振动波形：

![](http://upload-images.jianshu.io/upload_images/8398510-017296183e59b11c)

KeyCycle 波形

在上述动画中加入如下 KeyCycle

```
<KeyCycle

    app:target="@+id/button"

    app:framePosition="30"

    android:rotation="50"

    app:waveShape="sin"

    app:wavePeriod="1"/>



```

![](http://upload-images.jianshu.io/upload_images/8398510-a1740308bd83f0f4)

KeyCycle 关键帧

3.4 交互式动画
---------

上面的动画运行我都是通过对 Button 按钮设置点击监听事件，然后调用 motion_container.transitionToEnd(); 方法来使他运行的。其实完全不必这么麻烦，因为 MotionLayout 的视图允许开发者将触摸事件直接附加到视图中。截止到现在，它支持点击和滑动事件。要实现上面实现的点击事件可以在 MotionScene 中增加代码如下：

```
<OnClick

    app:target="@+id/button"

    app:mode="transitionToEnd"/>



```

而可以通过给 MotionScene 增加 OnSwipe 标签来使视图通过在屏幕滑动而大运行。在创建该标签时，必须确保提供正确的拖动方向以及应作为拖动控制柄的视图的边。可以这么理解，相对于初始位置，如果想往上滑起到增加动画进度就设置为 dragUp，想往下滑起到增加动画进度就设置为 dragDown，左右同样道理。至于 touchAnchorSide 这个参数的本意应该设置拉目标视图的边，但我发现就算不设置 touchAnchorSide 这个参数或者设置成任意值 top bottom 或者 left right，对动画都没有影响。这可能是 MotionLayout 的一个 bug 毕竟现在还只是 alpha 版。

```
<OnSwipe

    app:touchAnchorId="@+id/actor"

    app:dragDirection="dragUp"/>



```

![](http://upload-images.jianshu.io/upload_images/8398510-7ff9fe93244929c9)

OnSwipe 动画

之前一篇讨论 ConstraintLayout 的文章，基本上都是在布局编辑器中进行操作。这也是 ConstraintLayout 的一大优点，MotionLayout 作为其子类，官方也为它专门提供了强大的可视化编辑器。不过可惜的是，到目前为止还不能使用，下面是 MotionEditor 的官方预告片的一个节选：

![](http://upload-images.jianshu.io/upload_images/8398510-b64079d433db18d7)

MotionEditor 编辑动画