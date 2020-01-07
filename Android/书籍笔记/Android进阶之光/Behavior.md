

Behavior TextView根据Button的位置进行自己的位置更新
```kotlin
class EasyBehavior : CoordinatorLayout.Behavior<TextView> {//这里的泛型是child的类型，也就是观察者View

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    //确定你是否依赖于这个View
    override fun layoutDependsOn(parent: CoordinatorLayout?, child: TextView?, dependency: View?): Boolean {
        //告知监听的dependency是Button
        return dependency is Button
    }

    //当依赖的View发生变化时，主要用于跟随其做运动，如果只是简单的动画，就可以在这里实现了
    @SuppressLint("SetTextI18n")
    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: TextView?, dependency: View?): Boolean {
        //这里实现自己的运动的方法，
        //child代表的是当前使用这个Behavior的View啦
        //dependency就是我们所依赖的对象
        dependency?.let {
            child?.x = dependency.x - 200f
            child?.y = dependency.y - 400f
        }

        child?.text = "${dependency?.x},${dependency?.y}"
        return true
    }

    //开始嵌套滚动
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: TextView, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        //这里返回true，才会接受到后续滑动事件。
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    //嵌套滚动的过程中
    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: TextView, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        //进行滑动事件处理
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }

    //快速的滑动中
    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: TextView, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
    }

    //即将开始嵌套滚动，每次滑动前，Child 先询问 Parent 是否需要滑动，即dispatchNestedPreScroll()，
    // 这就回调到 Parent 的onNestedPreScroll()，Parent 可以在这个回调中“劫持”掉 Child 的滑动，也就是先于 Child 滑动。
    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: TextView, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    //即将开始快速划动，这里可以做一些对动画的缓冲处理，也就是我们如何去应对用户快速的操作
    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: TextView, target: View, velocityX: Float, velocityY: Float): Boolean {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }

}
```