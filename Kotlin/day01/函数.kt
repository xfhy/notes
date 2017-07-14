package day01

/**
 * Created by xfhy on 2017/7/14.
 */


/**
 * 加法函数
 * @param x 加数1
 * @param y 加数2   默认值是0
 */
fun add(x: Int, y: Int = 0): Int {
    return x + y
}

/**
 * 实现求和   将任意多个整数传入
 */
fun sum(vararg numbers: Int): Int {
    //可变参数vararg
    var total = 0

    for (i in numbers) {
        total += i
    }

    return total
}

fun main(args: Array<String>) {

    val result = add(1, 2)
    val result2 = add(1)  //默认值可以不写

    //可以更人性化的把参数的名称写出来,但是在调用java方法的时候不行
    val result3 = add(x = 1, y = 4)
    println(result)
    println(result2)
    println(result3)

    val a = intArrayOf(2,3,4,5,6)

    println(sum(1, 2, 3, 4, 5))
    println(sum(1))
    println(sum(*a))   //可变参数那里是多个Int,可以传入一个Int数组

}