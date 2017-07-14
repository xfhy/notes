package day01

/**
 * Created by xfhy on 2017/7/13.
 * 值的类型
 * Int
 * 形式:
 * 由于Kotlin的类型推断,类型可以不写
 */

var run: Int = 5

fun main(args: Array<String>) {
    day01.run += 6
    println("每天跑${day01.run}公里")
}

