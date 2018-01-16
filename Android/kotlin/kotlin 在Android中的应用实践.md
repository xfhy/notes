# kotlin在Android中的应用实践

1. kotlin中使用var修饰的属性不允许不初始化，但是有的时候需要在使用的时候进行初始化,针对该情况，可以使用Delegates.notNull()代理，运行时对属性进行判断，若未初始化，则抛出java.lang.IllegalStateException异常：
```kotlin
var context: Context by Delegates.notNull()
            private set
```
2. 获取java的class
开启新的activity
```kotlin
val intent = Intent(this, MainActivity::class.java)
startActivity(intent)
```
3. 一个lambda 表达式只有一个参数是很常见的。 如果Kotlin 可以自己计算出签名，它允许我们不声明唯一的参数，并且将隐含地为我们声明其名称为 it 
```kotlin
/**
* 切换Fragment
* @param position 下标
*/
private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (position) {
                0 // 首页
                -> mHomeFragment?.let {
                //it即为当前对象   这里用闭包简化了判空的过程
                transaction.show(it)
                } ?: HomeFragment.getInstance(mTitles[position]).let {  //当非空时需要添加进来,进行初始化
                mHomeFragment = it
                transaction.add(R.id.fl_container, it, "home")
                }
                1  //发现
                -> mDiscoveryFragment?.let {
                transaction.show(it)
                } ?: DiscoveryFragment.getInstance(mTitles[position]).let {
                mDiscoveryFragment = it
                transaction.add(R.id.fl_container, it, "discovery") }
                2  //热门
                -> mHotFragment?.let {
                transaction.show(it)
                } ?: HotFragment.getInstance(mTitles[position]).let {
                mHotFragment = it
                transaction.add(R.id.fl_container, it, "hot") }

                else -> {

                }
        }
}
```

4. `?:`表示当是空的时候执行后面的语句
5. presenter懒加载(其他懒加载也是这样用的,哈哈)
private val mPresenter by lazy { HomePresenter() }   