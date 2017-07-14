package day01

/**
 * Created by xfhy on 2017/7/14.
 */

fun main(args: Array<String>) {
    for (i in 1..10) {
        println(i)
    }

    //可以直接把简单的if.else语句里面的内容赋值给前面的量
    val result = if (3 < 4) "小于" else 0
    println(result)


    val a = 10

    //when: 可对某个变量的大小/范围/值表达式/类型等进行判断
    //当符合某个条件时,去大括号里面执行,其他的分支就不会被执行
    //一般条件是互斥的
    when (a) {
        in 1..10 -> {  //当a的值是1~10之间的数时
            println("a的值是1~10之间的数")
        }
    /*!in 10..20 -> {
        println("a的值不是10~20之间的数")
    }*/
    /*(9 - 3) -> {
        println("当a的值是6时")
    }*/
        is Int -> {
            println("a是整型")
        }
        else -> {
            println("未知的情况")
        }
    }

}