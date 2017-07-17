package day02

/**
 * Created by xfhy on 2017/7/15.
 *
 * 设定类本身及其属性,方法,构造器
 * 以及接口和对象的对外访问权限,即"可见性"
 *
 * private    私有:仅当前类可见,最小的可见性
 * protected   保护:仅子类可见
 * internal     内部:当前模块可见
 * public       公开:默认,对外完全可见
 *
 */

open class People{
    private var a = 0
    protected var b = "b"
    internal var c = 1
    var d = 2

    override fun toString(): String {
        return "People(a=$a, b='$b', c=$c, d=$d)"
    }

}
class Student:People{
    constructor() : super()

    fun test(): Unit {
//        a = 1    父类的私有属性不能被访问
        b = "aaa"
        c = 2
        d = 3

        println("b=$b,c=$c,d=$d")
    }



}

fun main(args: Array<String>) {

    val people = People()
    people.c = 2
    people.d = 3

    val student = Student()
    student.test()

}