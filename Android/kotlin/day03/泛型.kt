package day03

/**
 * Created by xfhy on 2017/7/17.
 *
 * 泛型 Generics
 *
 * 一般用于函数的参数类型定义,让函数更通用
 *
 */


fun main(args: Array<String>) {

    //最常用的例子,print和println()函数,几乎可以让任何类型的参数
    println(2)
    println("hehe")
    println(arrayOf("Android", "IOS"))

    //自定义一个泛型函数
    fun <T> showText(para: T) {
        println(para)
    }

    showText("aaaaaa")
    showText(11111)

    //泛型约束   <泛型占位符 : 类型>
    fun <T : Number> sum(vararg numbers: T): Double {
        return numbers.sumByDouble { it.toDouble() }
    }

    var result = sum(1, 2, 3, 3.2)
    println(result)

    //多重约束  where ,各个约束用逗号分隔,写在函数体之前
    //例子:把数组中大于某个元素的部分值取出并升序排列
    fun <T> bigger(list: Array<T>, thre: T): List<T>
                where T: Number,T: Comparable<T>
    {
        return list.filter { it>thre }.sorted()
    }

}

