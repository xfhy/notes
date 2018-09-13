package day02

/**
 * Created by xfhy on 2017/7/15.
 */

class News{
    //默认地区
    private var lang = "cn"

    //新闻分类   嵌套类    和主类关系不是太大
    //实例化时不需要实例化主类即可实例化嵌套类
    class Category {
        var list = arrayOf("推荐","科技","美女")
        //将上面的数组转为String
        val desc = list.joinToString()
//        lang = "11"   嵌套类不能访问主类的属性
    }

    //语种   内部类    可以直接访问主类的变量
    //实例化时需要先实例化主类,再实例化内部类
    inner class Lang{
        fun changeRegion(newRegion: String){
            lang = newRegion
            println("现在的语种是$lang")
        }
    }

}

fun main(args: Array<String>) {
    println(News.Category().desc)

    println(News().Lang().changeRegion("呵呵"))
}