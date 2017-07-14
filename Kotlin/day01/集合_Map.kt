package day01

/**
 * Created by xfhy on 2017/7/14.
 * Map 无序可重复
 *
 */

fun main(args: Array<String>) {
    //不可变
    val mapTest1 = mapOf(Pair(1, "qq"), Pair(2, "ww"), Pair(3, "ee"))

    val first = mapTest1.get(1)   //获取key为1的value

    //获取key为2的value,并且带default值
    println(mapTest1.getOrDefault(2, "key为2的value不存在"))
    println(mapTest1.getOrDefault(100, "key为100的value不存在"))

    //获取所有的key
    for (key in mapTest1.keys) {
        println(key)
    }

    //获取所有的value
    for (value in mapTest1.values) {
        println(value)
    }


    //将Map转换成可以动态添加的mutableMap
    val mutableMap = mapTest1.toMutableMap()
    mutableMap.put(4, "rr")

    //添加或更新mutableMap
    mutableMap[2] = "22222"   //有key则更新
    mutableMap[5] = "55555"   //无key则添加

    //移除元素
    mutableMap.remove(2)

    //遍历mutableMap
    for (mutableEntry in mutableMap) {
        println("${mutableEntry.key}---${mutableEntry.value}")
    }

}

