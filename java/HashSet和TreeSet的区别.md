# HashSet和TreeSet的区别 #
<font size="4"><b>

这个不能发表成博客,这是https://zhidao.baidu.com/question/85559012.html别人写的.

1. HashSet是通过HashMap实现的,TreeSet是通过TreeMap实现的,只不过Set用的只是Map的key<br/>
2. Map的key和Set都有一个共同的特性就是集合的唯一性.TreeMap更是多了一个排序的功能.<br/>
3. hashCode和equal()是HashMap用的, 因为无需排序所以只需要关注定位和唯一性即可.<br/>
   a. hashCode是用来计算hash值的,hash值是用来确定hash表索引的.<br/>
   b. hash表中的一个索引处存放的是一张链表, 所以还要通过equal方法循环比较链上的每一个对象
       才可以真正定位到键值对应的Entry.<br/>
   c. put时,如果hash表中没定位到,就在链表前加一个Entry,如果定位到了,则更换Entry中的value,并返回旧value<br/>
4. 由于TreeMap需要排序,所以需要一个Comparator为键值进行大小比较.当然也是用Comparator定位的.<br/>
   a. Comparator可以在创建TreeMap时指定<br/>
   b. 如果创建时没有确定,那么就会使用key.compareTo()方法,这就要求key必须实现Comparable接口.<br/>
   c. TreeMap是使用Tree数据结构实现的,所以使用compare接口就可以完成定位了.<br/>
</b></font>