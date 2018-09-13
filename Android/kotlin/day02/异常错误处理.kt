package day02

/**
 * Created by xfhy on 2017/7/16.
 */

fun main(args: Array<String>) {

    /*----直接显示错误------*/

    try {
        "ttt0".toInt()
    } catch(e: Exception) {
        println(e)
    }

    /*--忽略错误----*/

    //将后面的值给a
    val a: Int? = try {
        "qqq3".toInt()
    } catch (e: Exception) {
        null
    }

    println(a)

}