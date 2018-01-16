
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