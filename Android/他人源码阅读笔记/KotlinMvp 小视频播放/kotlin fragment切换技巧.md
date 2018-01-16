
- kotlin中when非常强大,完全可以代替if..else if,switch.when还可以用来实现判断类似java中的instanceof的作用,kotlin中是用is关键字.
- 以下方式用闭包完美的简单的实现了判空操作,且展示了非空的fragment.如果fragment为空,则利用`?:`符号执行之后的初始化操作,简直完美

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