# 让你的kotlin代码远离!!

[原文](https://zhuanlan.zhihu.com/p/27285806)

> 简评：优雅的运用 Kotlin 的 null safety 特性，而不要简单的直接用 !!。

对于 Null 的检查是 Kotlin 的特点之一。强制你在编码过程中考虑变量是否可为 null，因此可以避免很多在 Java 中隐藏的 NullPointerException。

但是，当你用插件直接将 Java 代码转换为 Kotlin 时，你会发现有很多 !! 在里面。但其实 !! 意味着「有一个潜在未处理的 KotlinNullPointerException 在这里」。

这里就介绍 6 个避免 !! 的方法：

## 1. 用 val 而不是 var

在 Kotlin 中 val 代表只读，var 代表可变。建议尽可能多的使用 val。val 是线程安全的，并且不需要担心 null 的问题。只需要注意 val 在某些情况下也是可变的就行了。

可以看看这里：[Mutable vals in Kotlin](http://link.zhihu.com/?target=http%3A//blog.danlew.net/2017/05/30/mutable-vals-in-kotlin/)
## 2. 使用 lateinit

有些情况我们不能使用 val，比如，在 Android 中某些属性需要在 onCreate() 方法中初始化。对于这种情况，Kotlin 提供了 lateinit 关键字。
```kotlin
private lateinit var mAdapter: RecyclerAdapter<Transaction>

override fun onCreate(savedInstanceState: Bundle?) {
   super.onCreate(savedInstanceState)
   mAdapter = RecyclerAdapter(R.layout.item_transaction)
}

fun updateTransactions() {
   mAdapter.notifyDataSetChanged()
}
```
要注意，访问未初始化的 lateinit 属性会导致 UninitializedPropertyAccessException。

并且 lateinit 不支持基础数据类型，比如 Int。对于基础数据类型，我们可以这样：

private var mNumber: Int by Delegates.notNull<Int>()

## 3. 使用 let 函数

下面是 Kotlin 代码常见的编译错误：


许多开发者都会选择 quick-fix：
```Kotlin
private var mPhotoUrl: String? = null

fun uploadClicked() {
    if (mPhotoUrl != null) {
        uploadPhoto(mPhotoUrl!!)
    }
}
```
但这里选择 let 函数是一个更优雅的解决方法：
```kotlin
private var mPhotoUrl: String? = null

fun uploadClicked() {
    mPhotoUrl?.let { uploadPhoto(it) }
}
```
## 4. 创建全局函数来处理更复杂的情况

let 是一个对于 null 检查很好的替代品，但有时我们会遇到更复杂的情况。比如：
```kotlin
if (mUserName != null && mPhotoUrl != null) {
   uploadPhoto(mUserName!!, mPhotoUrl!!)
}
```
你可以选择嵌套两个 let，但这样可读性并不好。这时你可以构建一个全局函数：
```kotlin
fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
   if (value1 != null && value2 != null) {
       bothNotNull(value1, value2)
   }
}
```

简单调用`ifNotNull("", "", { a, b -> print("$a $b") })`

## 5. 使用 Elvis 运算符

Elvis 运算符在 Groovy 和 PHP 等语言中都存在。对于当值可能为 null 的情况特别方便：
```kotlin
fun getUserName(): String {
   if (mUserName != null) {
       return mUserName!!
   } else {
       return "Anonymous"
   }
}
```
上面的代码就可以简化为：
```kotlin
fun getUserName(): String {
   return mUserName ?: "Anonymous"
}
```
为什么叫 Elvis 呢？因为 ?: 很像猫王的发型：

## 6. 自定义崩溃信息

如果我们使用 !!，那么当这个变量为 null 时，只会简单的抛出一个 KotlinNullPointerException。这时我们可以用 requireNotNull 或 checkNotNull 来附带异常信息，方便我们调试。
```kotlin
uploadPhoto(requireNotNull(intent.getStringExtra("PHOTO_URL"), { "Activity parameter 'PHOTO_URL' is missing" }))
```
总而言之，绝大多数情况下你都不需要 !!，可以用上面提到的 6 个技巧来消除 !!。这样能让代码更安全、更容易 debug 并且更干净。
