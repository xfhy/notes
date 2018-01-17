
### IBaseView
所有view的父接口
```kotlin
interface IBaseView {

    fun showLoading()

    fun dismissLoading()

    fun showErrorMessage(message: String)

}
```

### IPresenter

> presenter父接口 

- in 使得一个类型参数逆变，逆变类型参数只能用作输入，可以作为入参的类型但是无法作为返回值的类型：

```kotlin
interface IPresenter<in V: IBaseView> {

    fun attachView(mRootView: V)

    fun detachView()

}
```

### BasePresenter

- 将view的绑定到BasePresenter中
- 在 mvp 的项目中处理内存泄露,将需要被 CompositeDisposable 管理的 observer 加入到管理集合中.当detach的时候及时取消所有正在执行的订阅.

```kotlin
//open表示可继承该类
open class BasePresenter<T : IBaseView> : IPresenter<T> {

    //绑定的view
    var mRootView: T? = null
        private set

    private var compositeDisposable = CompositeDisposable()


    override fun attachView(mRootView: T) {
        this.mRootView = mRootView
    }

    override fun detachView() {
        mRootView = null

         //保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }

    }

    private val isViewAttached: Boolean
        get() = mRootView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")


}
```

### BaseFragment

- 当fragment的view加载完毕时记录状态
- 加载数据之后也记录 已加载 的状态
- 当fragment对用户可见时去尝试加载数据

```kotlin
 abstract class BaseFragment: Fragment() {

    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false
    /**
     * 多种状态的 View 的切换
     */
    protected var mLayoutStatusView: MultipleStatusView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(getLayoutId(),null)
    }



    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewPrepare = true
        initView()
        lazyLoadDataIfPrepared()
        //多种状态切换的view 重试点击事件
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        lazyLoad()
    }


    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId():Int

    /**
     * 初始化 ViewI
     */
    abstract fun initView()

    /**
     * 懒加载
     */
    abstract fun lazyLoad()

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.getRefWatcher(activity)?.watch(activity)
    }
}
```

###  契约类
```kotlin
interface HomeContract {
    interface View : IBaseView {
        /**
         * 设置第一次请求的数据
         */
        fun setHomeData(homeBean: HomeBean)
    }
    interface Presenter : IPresenter<View> {
        /**
         * 获取首页精选数据
         */
        fun requestHomeData(num: Int)
    }
}
```

### 实现presenter

- 通过model获取数据
- 通过view进行显示数据

```kotlin
class HomePresenter : BasePresenter<HomeContract.View>(), HomeContract.Presenter{
    //懒加载model 需要用到时才加载
    private val homeModel: HomeModel by lazy {
        HomeModel()
    }
    ...
}
```

### 实现model

- 通过Retrofit+RxJava2获取网络数据

```kotlin
class HomeModel {

    /**
     * 获取首页 Banner 数据
     */
    fun requestHomeData(num: Int): Observable<HomeBean> {
        return RetrofitManager.service.getFirstHomeData(num)
                .compose(SchedulerUtils.ioToMain())
    }
}
```