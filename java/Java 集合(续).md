# Java 集合(续) #
<font size="4"><b>
1. TreeSet:元素唯一,并按自然顺序排序. 底层是二叉树实现的.可以让TreeSet根据你的要求排序的话,则需要![](http://i.imgur.com/xmUSSn1.png)<br/>
2. Comparator是一个接口,定义了两个方法：<br/>
compare()
根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数，通常使用-1, 0, +1
<font color="red"><br/>
equals()
!注意!：这是判断比较器本身是否与其他Comparator相等，不是判断TreeSet中的元素
</font>
<br/>
3. HashSet和TreeSet小结:<br/>![](http://i.imgur.com/0MlfTkc.png)<br/>
4. Map接口专门处理键值映射数据的存储，可以根据键实现对值的操作
最常用的实现类是HashMap<br/>
5. HashMap和Hashtable的比较:Hashtable和HashMap的异同<br/>
实现原理、功能相同，可以互用<br/>
主要区别<br/>
Hashtable继承Dictionary类，HashMap实现Map接口<br/>
Hashtable线程安全，HashMap线程非安全<br/>
Hashtable不允许null值，HashMap允许null值<br/>
6. 不涉及到多线程的开发过程中，最好使用ArrayList和HashMap
如果程序中用到多线程，酌情使用Vector和Hashtable<br/>
7. Set有一个forEach()方法,传入一个Consumer对象,对象里面有一个accept()方法,Set里面每个元素都会执行accept()方法.<br/>
8. 当TreeSet里面的一个元素的值改变(eg:对象的属性),并不会重新排序.排序只会在插入时排序.<br/>
9. 如果要对一堆数据进行排序,不应该用TreeSet,不仅仅因为它会去掉重复值.<br/>
对数组排序可以使用Arrays.sort() 内部实现是快速排序.对<br/>
对对象列表进行排序可以使用Collections.sort(),需要传入比较器(Comparator). 内部实现是合并排序.<br/>
10. 其实Java.util.Collections
里面提供了一个shuffle的接口，它可以很方便地将一个有序数组进行乱序处理。Collections.shuffle(list) 这里list可以是数组或者List集合<br/>
11. 集合遍历:遍历List, Set和Map集合<br/>
方法1：循环(仅适用于List)<br/>
方法2：增强型for循环(foreach循环，适用于所有类)<br/>
方法3：通过迭代器Iterator实现遍历<br/>
获取Iterator ：Collection 接口的iterate()方法<br/>
Iterator的方法<br/>
boolean hasNext(): 判断是否存在另一个可访问的元素 <br/>
Object next(): 返回要访问的下一个元素<br/>
方法4：(仅适用于JDK 1.8) 适用forEach()方法<br/>
12. 注意,Map的key是Set集合,所以遍历Map集合有点麻烦的,下面是用迭代器遍历.<br/>
-------------------------------------------------
		//第一种遍历  键值对遍历
		Map<String,Integer> map = new HashMap<>();
		map.put("1",1);
		map.put("2",2);
		map.put("3",3);
		map.put("4",4);
		Iterator<Map.Entry<String, Integer>> entries = map.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry<String, Integer> entry = entries.next();
			System.out.println("Key = "+entry.getKey()+",  value = "+entry.getValue());
		}

		//第二种    以key的方式遍历集合所有的Key值,是Set集合   
		Iterator<User> allUser = map.keySet().iterator();
		while(allUser.hasNext()){
			User user = allUser.next();   //获取到某个用户信息
		}
		
--------------------------------------------------------------

13. 在添加、删除和定位映射关系上，TreeMap类要比HashMap类的性能差一些，但是其中的映射关系具有一定的顺序。 
如果不需要一个有序的集合，则建议使用HashMap类；如果需要进行有序的遍历输出，则建议使用TreeMap类。  在这种情况下，可以先使用 HashMap。在需要排序时，利用现有的 HashMap，创建一个 TreeMap 类型的实例<br/>
14. Hash的查找是最快的.<br/>
</b></font>