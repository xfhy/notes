package day02

/**
 * Created by xfhy on 2017/7/14.
 *
 * Kotlin默认是不可继承的,如果需要继承,需要在定义类时在前面加上Chinese
 *
 *  属性覆盖:子类覆盖父类的属性
 *
 */

/**
 * 定义一个类
 */
open class Chinese constructor(var sex: Boolean, var region: String) {

    //普通属性
    open var skin = "yellow"

    //组合属性 ,由其他属性计算而来的(get)
    val avgLife: Double
        get() {
            when (this.region) {
                "sh" -> {
                    return 82.4
                }
                "sc" -> {
                    return 78.4
                }
                else -> {
                    return 0.0
                }
            }
        }

    //组合属性可以反过来影响其他属性(set,可选)
    var avgSalary: Int
        get() {
            when (this.region) {
                "sh" -> {
                    return 4900
                }
                "cd" -> {
                    return 9000
                }
                else -> {
                    return 3600
                }
            }
        }
        set(value) {
            when (value) {
                in 4000..5000 -> {
                    this.region = "sh"
                }
                in 5000..6000 -> {
                    this.region = "bj"
                }
                else -> {
                    this.region = "other"
                }
            }
        }

    fun print(): Unit {
        println(this.toString())
    }

    /**
     * 如果子类需要覆写父类的方法,则需要在父类的方法前面加上open
     */
    open fun cook() {
        val menu = arrayOf("青椒肉丝", "鱼香肉丝", "鱼香茄子")
        //浓缩数组
        val desc = menu.reduce { s1, s2 -> s1 + "," + s2 }
        println("我会做$desc")
    }

    //覆写方法
    override fun toString(): String {
        return "Chinese(sex=$sex, region='$region')"
    }
}

//继承
class Shanghai(sex: Boolean, region: String = "上海") : Chinese(sex, region) {

    override var skin = "ShangHaiYellow"

    //覆写父类的方法
    override fun cook() {
        super.cook()  //可以调用父类的方法

        val menu = arrayOf("麻婆豆腐")
        val desc = menu.reduce { s1, s2 -> s1 + "," + s2 }
        println("我还会$desc")
    }
}

class Sichuan(sex: Boolean, region: String) : Chinese(sex, region)

fun main(args: Array<String>) {

    //实例化类
    val c = Chinese(true, "北京")
    c.print()
    println(c.avgLife)
    c.avgSalary = 500
    c.print()
    c.cook()

    val shanghai = Shanghai(false,"上海")
    //多态
    println("----------------------------")
    shanghai.cook()

    val sichuan = Sichuan(false, "四川")
    sichuan.print()

}