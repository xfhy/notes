# Java 集合 #
<font size="5"><b>
1. ![](http://i.imgur.com/pnE1P25.png)<br/>
2. Collection 接口存储一组不唯一，无序的对象;<br/>
3. List 接口存储一组不唯一，<font color="red">有序</font>（插入顺序）的对象<br/>
4. Set 接口存储一组<font color="red">唯一</font>，无序的对象 <br/>
5. 所有放到List中的都是Object,取出时必须强制类型转换(强转时记得判断instanceof).<br/>
6. List中有许多方法是unchecked异常,可能抛出,使用时一定要记得提前判断传入参数,尽量try...catch,捕获到异常时输出异常信息.<br/>
7. List中的add()方法返回是boolean,在内存不够用时会返回失败false.<br/>
8. List避免重复,插入之前判断contains().<br/>
9. Set无法随机访问,必须使用迭代器遍历.<br/>
10. Set使用对象equals()方法判断两个对象是不是同一个对象;<font color="red">如果你重写了equals()方法，那么一定要记得重写hashCode()方法!
</font>两对象的hashcode相等不一定两个对象equals;但是如果两个对象equals，那么两个对象的hashcode一定相等；<br/>
11. equals()方法注意事项<br/>
----------------------------------------------------------------------
<font size="4">
a. 自反性：对于任何值x， x.equals(x) 都应返回 true。 <br/>
b. 对称性：对于任何非空引用值 x 和 y，当且仅当 y.equals(x) 返回 true 时，x.equals(y) 才应返回 true。 <br/>
c. 传递性：对于任何非空引用值 x、y 和 z，如果 x.equals(y) 返回 true，并且 y.equals(z) 返回 true，那么 x.equals(z) 应返回 true。  <br/>
d. 一致性：对于任何非空引用值 x 和 y，多次调用 x.equals(y) 始终返回 true 或始终返回 false，前提是对象上 equals 比较中所用的信息没有被修改。  <br/>
e. 对于任何值x ，x.equals(null) 都应返回 false。 <br/>
</font>
----------------------------------------------------------------------
12. 另外一个常用的列表类是Vector类，它也实现了List接口，可以实现ArrayList的所有操作<br/>
----------------------------------------------------------------------
<font size="4">
Vector和ArrayList的异同:<br/>
1.实现原理、功能相同，可以互用<br/>
2.主要区别:<br/>
Vector线程安全操作相对较慢<br/>
ArrayList重速度轻安全，线程非安全<br/>
长度需增长时，Vector默认增长一倍，ArrayList增长50%<br/>
Vector可以使用capacity()方法获取实际的空间<br/>
</font>
----------------------------------------------------------------------
<br/>
13. 当我们在使用形如HashMap、HashSet、HashTable等以Hash开头的集合类时，hashCode()会被隐式调用以来创建哈希映射关系。<br/>
14. 所有用于判断相等的字段在hashCode中都要使用<br/>
15. hashCode方法的模板：<br/>
<pre><code>
public int hashCode() {
	final int PRIME1 = 13; //这里最好是定义成类的属性,最好是素数
	final int PRIME2 = 17;
	…
	int result= 1;
	result = PRIME1 * property1;
	result += PRIMER2 * property2;
	return result;
}
</code></pre>

16. equals重写的模板:(这里引用:[http://blog.csdn.net/min123456520/article/details/5194030](http://blog.csdn.net/min123456520/article/details/5194030 "java中重写equals方法"))<br/>

		/*
 		 * 重写equals必须注意：
		  *   1 自反性：对于任意的引用值x，x.equals(x)一定为true
		  *   2  对称性：对于任意的引用值x 和 y，当x.equals(y)返回true，y.equals(x)也一定返回true
		  *   3 传递性：对于任意的引用值x、y和ｚ，如果x.equals(y)返回true，并且y.equals(z)也返回true，那么x.equals(z)也一定返   回 true
		   *  4 一致性：对于任意的引用值x 和 y，如果用于equals比较的对象信息没有被修改，
		   *           多次调用x.equals(y)要么一致地返回true，要么一致地返回false
		   *  5 非空性：对于任意的非空引用值x，x.equals(null)一定返回false
		   *　
		   * 请注意：
		   * 重写equals方法后最好重写hashCode方法，否则两个等价对象可能得到不同的hashCode,这在集合框架中使用可能产生严重后果
		  */
		 
		 
		 /*
		  *  1.重写equals方法修饰符必须是public,因为是重写的Object的方法.
		     *  2.参数类型必须是Object.
		  */ 
		 public boolean equals(Object other){   //重写equals方法，后面最好重写hashCode方法
		  
		  if(this == other)    //先检查是否其自反性，后比较other是否为空。这样效率高
		   return true;
		  if(other == null)         
		   return false;
		  if( !(other instanceof Cat))
		   return false;
		  
		  final Cat cat = (Cat)other;
		  
		  if( !getName().equals(cat.getName()))
		   return false;
		  if( !getBirthday().equals(cat.getBirthday()))
		   return false;
		  return true;
		 }
17. hashCode()方法重写的模板:(这里引用:[http://blog.csdn.net/min123456520/article/details/5194030](http://blog.csdn.net/min123456520/article/details/5194030 "java中重写equals方法"))<br/>

		 public int hashCode(){                 //hashCode主要是用来提高hash系统的
		//查询效率。当hashCode中不进行任何操作时，可以直接让其返回 一常数，或者不进行重写。
		  int result = getName().hashCode();
		  result = 29 * result +getBirthday().hashCode();
		  return result;
		  //return 0;
		 }
</b></font>