package day01

/**
 * Created by xfhy on 2017/7/13.
 */
fun main(args: Array<String>) {
    val name = "我是xfhy"

    //方法
    println(name.isEmpty())
    println(name.count())

    //Char方法
    val a = '我'
    println(a.isDigit())   //是数字
    println(a.isLetter())  //是字母

    //String转Char
    val hehe = "qwert家具啊哈"
    var chars = hehe.toCharArray()
    //循环输出数组
    for (word in chars) {
        println(word)
    }

    //字符串模板:可以把各种变量组合成一个动态的字符串
    val eventTime = Triple(6, 1, 3)
    val company1 = "顺丰"
    val company2 = "菜鸟"
    val admin = "国家邮政局"
    val newTitle = "${eventTime.first}月${eventTime.second}日,${company1}" +
            "大战${company2}正酣,${eventTime.third}日星夜,${admin}紧急叫停"
    println(newTitle)

}