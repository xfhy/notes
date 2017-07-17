package day02

/**
 * Created by xfhy on 2017/7/15.
 *
 * 有时候只需要对某个类进行轻微改造,避免继承,供临时使用,避免继承
 * 对象声明和表达式就很有用
 */

open class Chinese2(var name: String) {
    open var skin = "yellow"
    open var height = 1
}

fun main(args: Array<String>) {

    //对象表达式  :  val 对象名 = object : 类,接口(属性或方法的override定义)
    val baak = object : Chinese2("baak") {
        override var skin = "black"   //将skin改为black,只修改一部分
    }

    //纯对象表达式   临时使用,无需继承任何类
    val point = object {
        var x = 100
        var y = 200
    }

    //工具类
    println(HttpUtils.isNetwork())

    //伴生对象   可以产生唯一性
    var create = Card.create()

}

/**
 * 对象声明,不能用在函数中
 * 一般用于对其他类的一种使用上的封装
 *
 * 就像是Java中的工具类
 */
object HttpUtils {

    fun isNetwork(): Boolean {
        return false
    }
}


/**
 * 伴生对象:一般用于创建一个类的实例的"工厂"方法
 * Java中的 静态成员
 */
class Card{
    companion object {
        fun create(): Card {
            return Card()
        }
    }
}

