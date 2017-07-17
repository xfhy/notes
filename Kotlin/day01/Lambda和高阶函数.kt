package day01

/**
 * Created by xfhy on 2017/7/14.
 *
 * 高阶函数:参数或返回值的类型是函数型'
 * 函数型:(参数)->返回值
 * lambda:一种无名函数的简写,{ (参数) -> 函数执行语句 }
 * 其他语言称之为闭包,既有能力访问其自身范围外的变量
 *
 */

fun main(args: Array<String>) {
    /*
        高阶函数:描述任务的结果,而不是使用循环详细推算
        map:常用于对集合类型的元素类型整体转变
        其lambda中参数的约定名称为it
     */
    val a = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    //将数组a中的每个元素转换成字符串类型的,然后放到b数组中
    //map():将原来的元素根据给定的规则转换成相应的值(我理解的)
    val b = a.map { "我是${it}" }
    for (i in b) {
        println(i)
    }

    //求a中能整除2的数   filter():返回仅包含与给定谓词匹配的元素的列表。 筛选
    val cs = a.filter { it % 2 == 0 }
    for (c in cs) {
        println(c)
    }

    //求a数组中的元素的和
    var sum = 0
    a.map { sum += it }
    println(sum)


}