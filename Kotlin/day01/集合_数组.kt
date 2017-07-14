package day01

/**
 * Created by xfhy on 2017/7/13.
 */

fun main(args: Array<String>) {
    /*
        Array:Array<类型> 或 arrayOf(元素1,元素2,...元素n)
        大小固定,元素类型不可变
     */
    val cols = arrayOf("aaaa", "bbbb", "cccc", "dddd")
    /*for (col in cols) {
        println(col)
    }*/

    //创建一个有默认值的数组
    val col2s = Array(22, { "我是占位符" })
    /*for (col2 in col2s) {
        println(col2)
    }*/

    //创建1~100的数组   每个索引所在处的值是根据表达式的计算结果来的,
    // 比如这里的,i最终就是索引值+1,即1~100
    val oneToHandrail = Array(100, { i -> i + 1 })
    /*for (i in oneToHandrail) {
        println(i)
    }*/

    //获取数组中的元素
    val oneToTen = Array(100, { i -> i + 1 })
    /*println(oneToTen[5])      //取第6个元素
    println(oneToTen.first())   //第一个元素
    println(oneToTen.last())    //最后一个元素
    println(oneToTen.component1()) //取前5个元素
    println(oneToTen.component2())
    println(oneToTen.component3())
    println(oneToTen.component4())
    println(oneToTen.component5())*/

    //筛选过滤掉数组中重复的元素 .distinct()   获取.toSet()
    val noRepeat = arrayOf(1, 2, 3, 1)
    /*for (i in noRepeat.distinct()) {
        println(i)
    }
    for (i in noRepeat.toSet()) {    //转换成Set
        println(i)
    }*/

    //切割数组sliceArray()     区间是2~3,区间的表示方法2..3
    val sliceArray = oneToTen.sliceArray(2..3)
    /*for (i in sliceArray) {
        println(i)
    }*/

    //mutableList:MutableList类型<类型> 或mutableListOf(元素1,元素2,元素3)
    //大小可变,类型不可变
    val list = mutableListOf(1,2,3,4)
    println("-------------------------------------------------")
    list.add(1)
    list.add(1,8)
    list.indexOf(1)
    list.remove(2)   //移除2这个元素
    list.removeAt(0) //移除第1个元素
    for (i in list) {
        println(i)
    }


}