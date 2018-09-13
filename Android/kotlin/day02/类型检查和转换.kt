package day02

/**
 * Created by xfhy on 2017/7/16.
 */

fun main(args: Array<String>) {

    val a = 3
    val b = 5

    val c = if (a > b) "大于" else a - b

    /*-----类型的判断-----*/
    if (c is String) {
        println("c是字符串,长度是${c.length}")
    } else {
        println("c是数字,值是$c")
    }

    //Kotlin的编译器,大多数时候会智能转换
    if (c is Int) println(c.inc())

    //手动转换:    强制转换:as    安全转换:as?

    //强制转换
    val d = c as Int
    println("d是强制转换过来的,值是$d")

    //安全转换   如果转换失败,则值是null
    val r = "dada"
    val e = r as? Int
    println("e的值是$e")



}
