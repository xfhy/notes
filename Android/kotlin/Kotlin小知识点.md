
- val和final不一样,val可以通过重写getter方法动态地获取不同的值
- 如果是写工具方法或者常量  直接写到top-level里面,顶层函数,不属于任何类

### 1. 函数默认参数

```
fun test(name:String="dasdas"){
    
}
```

### 2. 扩展函数

静态地给一个类 添加 扩展成员方法+扩展成员变量

```
fun File.readText(charset: Charset = Charsets.UTF_8): String = readBytes().toString(charset)
```

SDK里面的扩展,

### 3. 默认类是不能继承

需要添加一个open关键字

### 4. Lambda 闭包

所有的lambda都是,Function接口

```
//可以省略很多东西
val thread = Thread{}
thread.start()


//闭包声明 和使用
val echo = { name: String ->
    println(name)
}

echo("李四")
echo.invoke("张三")
```

### 5. 高阶函数

函数的参数是函数

```kotlin
//block是一个函数  参数为空,返回值是Unit
//声明高阶函数时,加上inline关键字(只会用于修饰高阶函数),减少生成匿名内部类.编译以后是拆解成了语句的调用,减少了临时匿名内部类的生成
inline fun onlyIf(isDebug: Boolean, block: () -> Unit) {
    //如果isDebug 则调用block函数
    if (isDebug) block()
}

//函数在最后一个位置,所以{}可以放到最外层
onlyIf(true) {
    //这里面的东西就是block函数
    println("嘿嘿")
}
```

### 6. 构造函数

默认每个类都是添加了public final的,默认是不能被继承的.需要继承的话,需要添加open.

```
//在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
class CustomView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defaultStyle: Int = 0) :
    View(context, attr, defaultStyle) {
}
```

### 7. 伴生对象 Kotlin特有的单例

推荐的单例写法:
```
//单例
//1. 私有化构造方法 2. get方法返回,
class Single private constructor() {
    companion object {
        fun get(): Single {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = Single()
    }
}
```

```
class StringUtils {
    //伴生对象声明
    companion object {
        fun isEmpty(str: String): Boolean {
            //比较字符串内容 是==
            return "" == str
        }
    }
}

fun main() {
    //就像java中调用一个对象的static方法一样
    println(StringUtils.isEmpty("ss"))
}
```

### 8. 静态方法

```
@JvmStatic
fun test(){}

或者是object 或者是伴生对象
```

### 9. 数据类

```
data class User()
```

### 10. 结构

```
val map = mapOf<String, String>("key" to "value1", "key1" to "value2")
    for ((k, v) in map) {
        println("key = $k value=$v")
    }
```

### 11. kotlin的循环

```
//输出0到9
for (i in 0..9) {
    println(i)
}

//输出1到9
for (i in 1 until 10) {
    println(i)
}

//输出10到1
for (i in 10 downTo 1) {
    println(i)
}

//步进2
for (i in 1..10 step 2) {
}

//从0到4 输出
repeat(5) {
    println(it)
}
```

### 12. 作用域函数

kotlin内置,对数据进行操作转换等

```
data class User(var name: String)

fun main(args: Array<String>) {
    val user = User("张三")

    //let和run都会返回闭包的执行结果,区别在于let有闭包参数,而run没有
    val letResult = user.let { "let 输出点东西 ${it.name}" }
    println(letResult)
    val runResult = user.run { "run 输出点东西 ${this.name}" }
    println(runResult)
    /*
    *   let 输出点东西 张三
        run 输出点东西 张三
    * */

    //also和apply都不返回闭包的执行结果,返回的是当前执行的对象,这里是返回的user
    //also有闭包参数,而apply没有闭包参数
    user.also { it.name }.apply { }.also { }.also { }

    //takeIf的闭包返回一个判断结果,为false时,takeIf会返回空
    //takeUnless与takeIf刚好相反,闭包的判断结果,为true时函数会返回空
    user.takeIf { it.name.isNotEmpty() }?.also { println("姓名为${it.name}") } ?: println("姓名为空")
    user.takeUnless { it.name.isNotEmpty() }?.also { println("姓名为空") } ?: println("姓名为${user.name}")

    //重复执行当前闭包
    repeat(5) {
        println(user.name)
    }

    //with比较特殊,不是以扩展方法的形式存在的,而是一个顶级函数
    //eg:可以在这里传入一个view,然后赋值一些初始值什么的
    with(user) {
        this.name = "李四"
    }

}
```

### 13. kotlin 比较对象

```
== //默认就是调用equals方法
=== 两个对象是否一致
```

### 14. val是不可变的变量

重写getter方法,达到一种近似修改其值的手段

```
class Person(var birthYear:Int) {
    val age: Int 
        get() {
            return Calendar.getInstance().get(Calendar.YEAR) - brithYear
        }
}
```

**编译时常量**
object或者伴生对象,或者是写到顶级模块 才能使用const
```
const val a = 0
```

### 15. kotlin内联

```
fun main() {
    test1 {
        println("hello")

        //如果不是内联,则只能中断当前这个lambda,不能中断外部函数
        //return@test1

        //如果当前lambda是内联的,那么可以中断外部函数
        //比如这里的话,如果是inline 下面的println("后面的东西")就不会被执行
        return
    }

    println("后面的东西")

}

//l参数是一个lambda
inline fun test1(l: () -> Unit) {
    l.invoke()
    return
}
```

### 16. kotlin真泛型

Android中实现MVP

```
class View<T>(val clazz: Class<T>) {
    val presenter by lazy {
        clazz.newInstance()
    }

    companion object {
        //伴生对象会在构造函数加载之前创建
        //构造函数执行之前就会执行下面的重载函数(重载运算符 invoke)   真泛型
        //
        inline operator fun <reified T> invoke() = View(T::class.java)
    }
}

class Presenter {
    override fun toString(): String {
        return "presenter"
    }
}

fun main() {
    val b = View<Presenter>().presenter
    val a = View.Companion.invoke<Presenter>().presenter

    println(a)
    println(b)
}
```
