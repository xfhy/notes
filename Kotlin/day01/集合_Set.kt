package day01

/**
 * Created by xfhy on 2017/7/13.
 *
 * 集合类型 Set:无序不重复
 * 主要方法;交差并补
 */

fun main(args: Array<String>) {

    val setTest1 = setOf(1,2,3)
    val setTest2 = setOf(2,3,4)
    val setTest3 = setOf(3,4,5)

    if (setTest1.isEmpty()) {
        println("setTest1是空的")
    }
    //println(setTest1.count())   //个数
    //println(setTest1.contains(1)) //包含

    //数组转换成集合
    val toTypedArray = setTest1.toTypedArray()

    //集合的交集
    val intersect = setTest1.intersect(setTest2).intersect(setTest3)
    /*println("交集---------------")
    for (i in intersect) {
        println(i)
    }*/

    //集合的差集
    val subtract = setTest1.subtract(setTest2)
    /*println("差集---------------")
    for (i in subtract) {
        println(i)
    }*/

    //集合的并集
    val union = setTest1.union(setTest2)
    /*println("并集---------------")
    for (i in union) {
        println(i)
    }*/

    val minus = setTest1.minus(setTest2)
    /*println("补集---------------")
    for (i in minus) {
        println(i)
    }*/

    val toMutableSet = setTest1.toMutableSet()

    val mutableSet = mutableSetOf(1,2,3,4,5,6)
    mutableSet.add(2222)

}