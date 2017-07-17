package day03

/**
 * Created by xfhy on 2017/7/17.
 *
 * 扩展: 对既有的类增加新功能而无需继承该类,即使无法获取其源代码
 * 主要作用是"立即"为相关类整体上添加"工具类(Utils)"方法或属性,高效优雅
 *
 * Kotlin支持: 扩展函数,扩展属性
 *
 * 与接受者类中的参数,名称都一样的扩展是无效的
 * 尽量避免与已有的名字重名,如果一定要重名,参数名和类型也要不一样
 *
 */


//扩展函数 : fun 需要扩展的类型.新扩展函数名(参数列表){//函数体}
//1, 普通函数扩展  整数的平方
fun Int.square(): Int {
    return this * this
}

//2,泛型函数扩展: 取数字型数组中最大的元素
fun <T> Array<T>.biggest(): T?
        where T : Number, T : Comparable<T> {
    return this.max()
}

//泛型属性扩展
val Int.next: Int
    get() = this + 1

//泛型属性扩展   数字是半径,然后计算面积
val <T> T.area: Double where T : Number
    get() = 3.14 * this.toDouble() * this.toDouble()

fun main(args: Array<String>) {

    println(3.square())
    println(arrayOf(1, 2, 3, 343423432, 43, 1241).biggest())

    println(4.next)
    println(3.14.area)

}