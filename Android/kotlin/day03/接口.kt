package day03

/**
 * Created by xfhy on 2017/7/16.
 *
 * 接口 interface
 *
 */

interface HttpListener {
    //有get属性,没有set
    val isHaHa: Boolean
        get() = true

    fun onFinish(): Unit
    fun onError(): Unit

    //接口中的方法可以有默认实现,通常指该方法是固定不变的
    fun test(){

    }
}

class HttpConnect : HttpListener {

    override fun onFinish() {
    }

    override fun onError() {
    }
}

fun main(args: Array<String>) {


}
