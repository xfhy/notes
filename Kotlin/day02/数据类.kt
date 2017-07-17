package day02

/**
 * Created by xfhy on 2017/7/15.
 * 数据类:专用于只保存数据的类,比如用户的聊天记录,自动登录信息等
 * 已经实现了toString,hashCode,equals()方法
 */

data class Article(var id: Int, var title: String)

fun main(args: Array<String>) {

    var article = Article(1000,"震惊....")

    println(article.toString())

    //克隆  并修改里面的属性然后给article1
    val article1 = article.copy(title = "哈哈....")
    println(article1.toString())

    println(article.equals(article1))
    println(article.hashCode())

    //数据类对象的解构
    val (id,title) = article
    println("id=$id,title=$title")

    //component1 可以直接这样访问第一个元素
    println(article.component1())
    println(article.component2())
}
