1. String拼接`"[$tag] $message"`  使用`$`符号.复杂一点就用`{}`括起来
2. 强转用`as`
3. 是否是 用`is`
4. 所有的类都继承自Any,默认都是final,需要继承的话需要在父类加上open 或者abstract
5. kotlin一切都是对象   不像java,java有几本数据类型.当然，像integer，float或者boolean等类型仍然存在，但是它们全部都会作为对象
存在的。
6. 位运算:`and` 和  `or`
7. 转换基本数据类型(其实也是对象)
```kotlin
val i:Int=7
val d: Double = i.toDouble()
```
8. 访问String中的某个位置的字符
`val c = str[2]`
9. 尽可能地使用 val  。除了个别情况（特别是在Android
中，有很多类我们是不会去直接调用构造函数的），大多数时候是可以的。
10. 属性自带setter,getter,也可以自定义

field是预留字段,可以用来在属性访问器内访问属性自身的值
```kotlin
class Person {
    var name: String = ""
        get() = field.toUpperCase()
        set(value) {
            field = "Name:$value"
        }

}
```

11. 扩展函数并不是真正地修改了原来的类，它是以静态导入的方式来实现的。**扩展函数可以被声明在任何文件中，因此有个通用的实践是把一系列有关的函数放在一个新建的文件里**。

```kotlin
//扩展函数
    fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        //扩展函数表现得就像是属于这个类的一样，而且我们可
//        以使用 this  关键字和调用所有public方法。
        Toast.makeText(this, message, duration).show()
    }
```

12. Anko其实扩展了系统的一些函数,比如toast等

13. Anko提供了非常简单的DSL来处理异步任务，它满足大部分的需求。它提供了一个
基本的 async  函数用于在其它线程执行代码，也可以选择通过调用 uiThread  的
方式回到主线程。在子线程中执行请求如下这么简单：

14. 数据类是一种非常强大的类，它可以让你避免创建Java中的用于保存状态但又操作
非常简单的POJO的模版代码。它们通常只提供了用于访问它们属性的简单的getter
和setter。定义一个新的数据类非常简单：

```kotlin
data class Forecast(val date: Date, val temperature: Float, val
details: String)
```

15. 映射对象到变量中
映射对象的每一个属性到一个变量中，这个过程就是我们知道的多声明。这就是为
什么会有 componentX  函数被自动创建。
```kotlin
val f1 = Forecast(Date(), 27.5f, "Shiny day")
val (date, temperature, details) = f1
```

16. 伴随对象`Companion objects`
可以拿来声明一些公用的方法,或者属性(需要用的时候才被初始化),和Java的静态方法有点儿像,只是有点像.

17. with函数
![](http://olg7c0d2n.bkt.clouddn.com/17-12-22/45059035.jpg)

18. while条件中不允许包含赋值语句,例如java中这么写的
```java
int dataSize;
while ((dataSize = input.read()) != -1) {
}
```
在kotlin中这么写是报错的

`assignments are not expressions,and only expressions are allowed in this connect`
kotlin不支持在条件里面包含赋值语句，你可以使用do...while()或者是用apply, also这种，
while (input.read().apply{ d = this } != -1)

19. 伴生对象
请注意，即使伴生对象的成员看起来像其他语言的静态成员，在运行时他们仍然是真实对象的实例成员

当然，在 JVM 平台，如果使用  @JvmStatic  注解，你可以将伴生对象的成员生成为真正的静态方法和字段
```kotlin
class MyClass {
    companion object Factory {
        fun create(): MyClass = MyClass()

        fun test() {
            println("test")
        }
    }

}
object MyClass2 {
    @JvmStatic
    fun printXX(xx: String) {
        println(xx)
    }
}

fun main(args: Array<String>) {
    val create = MyClass.Factory.create()
    MyClass.test()
    //可以省略伴生对象的名称
    val myClass = MyClass.create()

    MyClass2.printXX("哈哈")
}
```

20. 编译期常量
已知值的属性可以使用  const  修饰符标记为 编译期常量。 这些属性需要满足以下要求：
- 位于顶层或者是  object  的一个成员
- 用  String  或原生类型 值初始化
- 没有自定义 getter
```kotlin
const val SUBSYSTEM_DEPRECATED: String = "This subsystem is deprecated"
@Deprecated(SUBSYSTEM_DEPRECATED) fun foo() { …… }
```

21. Kotlin 的接口与 Java 8 类似，既包含抽象方法的声明，也包含实现。与抽象类不同的是，接
口无法保存状态。它可以有属性但必须声明为抽象或提供访问器实现

22. 可见性修饰符
- 如果你不指定任何可见性修饰符，默认为  public  ，这意味着你的声明将随处可见；
- 如果你声明为  private  ，它只会在声明它的文件内可见；
- 如果你声明为  internal  ，它会在相同模块内随处可见；
- protected  不适用于顶层声明。

23. 扩展函数(还有扩展属性)    比如:
```Kotlin
fun Any?.toString(): String {
    if (this == null) return "This Any is null"
// 空检测之后，“this”会自动转换为非空类型，所以下面的 toString()
// 解析为 Any 类的成员函数
    return toString()
}
```

24. 数据类
`data class User(val name:String,val age:Int)`
如果生成的类需要含有一个无参的构造函数，则所有的属性必须指定默认值
`data class User(val name:String="",val age:Int=0)` 现在就可以使用`val user = User()`了

25. 泛型函数
```kotlin
fun <T> singletonList(item: T): List<T> {
    val list = ArrayList<T>()
    return list
}

//冒号之后指定的类型是上界：只有  Comparable<T>  的子类型可以替代  T
fun <T : Comparable<T>> sort(list: List<T>) {}
```

26. 嵌套类和内部类
```kotlin
class Outer {
    private val bar: Int = 1

    //这是嵌套类 不能访问外面这个类的信息
    class Nested {
        fun foo() = 2
        fun test() {
        }
    }
}

class Outer2 {
    private val bar: Int = 1

    //类可以标记为  inner  以便能够访问外部类的成员。内部类会带有一个对外部类的对象的引 用
    inner class Inner {
        fun foo() = bar
    }
}
```

27. **对象表达式(匿名内部类)**  这个在安卓中使用比较多
如果对象是函数式 Java 接口（即具有单个抽象方法的 Java 接口）的实例， 你可以使用带接口类型前缀的lambda表达式创建它：
`val listener = ActionListener { println("clicked") }`

如果是多个抽象方法,也可以这样写
```kotlin
window.addMouseListener(object: MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
    }
    override fun mouseEntered(e: MouseEvent) {
    }
})
```

28. 任何时候，如果我们只需要“一个对象而已”，并不需要特殊超类型，那么我们可以简单地写
```kotlin
fun  foo() {
    val adHoc = object {
        var x: Int = 1
        var y: Int = 1
    }
    print(adHoc.x + adHoc.y)
}
```

29. **对象声明(单例模式)**  这称为对象声明。并且它总是在  object  关键字后跟一个名称。 就像变量声明一样，对象声
明不是一个表达式，不能用在赋值语句的右边。
```kotlin
object DataProviderManager {
    fun getData(): Int {
        return 1
    }
}

fun main(args: Array<String>) {
    //要引用该对象，我们直接使用其名称即可：
    val data = DataProviderManager.getData()
    println(data) //==1
}
```

30. **对象表达式和对象声明之间的语义差异**
- 对象表达式(匿名内部类)是在使用他们的地方立即执行（及初始化）的；
- 对象声明(单例模式)是在第一次被访问到时延迟初始化的；
- 伴生对象(有点像静态成员)的初始化是在相应的类被加载（解析）时，与 Java 静态初始化器的语义相匹配。

## 31. **委托**

标准委托:

- 延迟属性 也就是我们通常说的懒汉,在定义的时候不进行初始化，把初始化的工作延迟到第一次调用的时候。kotlin中实现延迟属性很简单，来看一下。
```kotlin
val str: String by lazy {
        println("Just run when first being used")
        "value"
}
```

- 可观察属性:
```kotlin
var age: Int by Delegates.observable(0) {
    /*
    * Delegates.observable() 接受两个参数：初始值和修改时处理程序（handler）。
    * 每当我们给属性赋值时会调用该处理程序（在赋值后执行）。
    * 它有三个参数：被赋值的属性、旧值和新值。在上面的例子中，
    * 我们对lazyTest.age赋值，set变化触发了观察者，执行了println()代码段。
    * */
    property, oldValue, newValue ->
    println("被赋值的属性property=$property  旧值=$oldValue   新值=$newValue")
}

//只有在新值大于旧值的时候   才会赋值
var gender: Int by Delegates.vetoable(0) { property, oldValue, newValue ->
    (oldValue < newValue)
}
```

32. 直观上的相等
你可以停止使用equals()方法来判断相等,因为== 这个操作符将会检测结构相等性。
```kotlin
val john1 = Person("John")
val john2 = Person("John")

john1 == john2    // true  (structural equality 结构(即John)相等)
john1 === john2   // false (referential equality 引用相等)
```

33. 默认参数
不需要定义几个相似参数的方法
```kotlin
fun build(title: String, width: Int = 800, height: Int = 600) {
    Frame(title, width, height)
}
```

34. when表达式
switch语句被替换成更加易读和灵活的when表达式。
```kotlin
when (x) {
    1 -> print("x is 1")
    2 -> print("x is 2")
    3, 4 -> print("x is 3 or 4")
    in 5..10 -> print("x is 5, 6, 7, 8, 9, or 10")
    else -> print("x is out of range")
}
```
既可以作为一个表达式或者一个语句,也可以有参数或者没有参数
```kotlin
val res: Boolean = when {
    obj == null -> false
    obj is String -> true
    else -> throw IllegalStateException()
}
```

35. 范围(Ranges)
方便可读性
```kotlin
for (i in 1..100) { ... } 
for (i in 0 until 100) { ... }
for (i in 2..10 step 2) { ... } 
for (i in 10 downTo 1) { ... } 
if (x in 1..10) { ... }
```

36. 扩展方法/功能
是否还记得第一次使用Java中List的排序么?你找不到一个sort方法进行排序从而你不得不咨询你的老师或者查找google来获取到Collections.sort()这个方法.后来当你使用一个String的时候可能你会写一个帮助类来帮助你达到想要的目的,因为你不知道有StringUtils.capitalize()
如果只有一种方法可以向旧类添加新功能,这样你的IDE将会帮助你在代码中找到相应的功能,这一点在Kotlin上面很容易实现.
```kotlin
fun String.format(): String {
    return this.replace(' ', '_')
}
val formatted = str.format()
```
标准库扩展了Java的原始类型的功能，这正是String特别需要的：
```kotlin
str.removeSuffix(".txt")  删除后缀
str.capitalize()  首字母大写
str.substringAfterLast("/")  删除/之前的字母,包括/也删除
str.replaceAfter(":", "classified") 
```

37. 空值安全
安全调用可以链接在一起，以避免我们有时用其他语言编写的嵌套的if-not-null检查，如果我们想要一个非null之外的默认值，我们可以使用elvis操作符
`val name = ship?.captain?.name ?: "unknown" `

如果这样并不适用于你，而且你需要一个NPE，你将不得不明确地要求它
```kotlin
val x = b?.length ?: throw NullPointerException()  // 与下面一样
val x = b!!.length                                 // 与上面一样
```