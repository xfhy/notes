package day02

/**
 * Created by xfhy on 2017/7/15.
 */

//枚举
enum class Pocker {
    A, B, C, D
}

//带构造方法的枚举
enum class Size(var height: Int) {
    S(150), M(160), L(170), XL(180), XXL(190),
}

fun main(args: Array<String>) {
    println(Pocker.values().joinToString())

    //名称
    println(Size.valueOf("XL").name)
    //序号
    println(Size.valueOf("XL").ordinal)

    println(Size.valueOf("XL"))
    println(Size.values().joinToString { it.name + " : " + it.height })
}
