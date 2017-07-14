package day01

/**
 * Created by xfhy on 2017/7/13.
 * 代表变量没有值的情况
 * val sex: Boolean?,无值则是null
 */
fun main(args: Array<String>) {
    val addr: String? = "四川师范大学东校区"
    val sex: Boolean?

    println("您的地址是${addr}")

    sex = false
    if (sex){
        println("您是男生")
    }
}