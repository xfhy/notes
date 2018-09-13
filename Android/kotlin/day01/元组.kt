package day01

/**
 * Created by xfhy on 2017/7/13.
 * 元组(Tuple),给多个变量同时赋值,分二元(Pair)和三元(Triple)
 */
fun main(args: Array<String>) {
    //同时定义多个量
    val (day, method, course) = Triple(3, "学会", "XX")

    val (cost, unit) = Pair(0, "元")

    println("${day}天${method}${course},${cost}${unit}")

    val point = Pair(1, 2)

    println("点(${point.first},${point.second})")
}