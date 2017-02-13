# Java String学习笔记 #
<font size="5">
1. String => equalsIgnoreCase() 忽略大小写进行比较(offline)<br/>
2. 对象的方法<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- inplace   改变对象的属性<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- offline 未改变<br/>
3. String => trim() :去掉空格<br/>
4. String str = "java"; String池, intern(): 返回池中的有的字符串<br/>
5. StringBuffer对字符串频繁修改时,可以大大提高程序执行效率<br/>
6. System.out.println(xx);  当遇到未知的对象时,会自动调用xx.toString()方法.所以自己写的类,如果属性多的话,尽量要写toString()方法,方便调试<br/>
7. 2.	StringBuilder：线程非安全的;StringBuffer：线程安全的;当我们在字符串缓冲去被多个线程使用是，JVM不能保证StringBuilder的操作是安全的，虽然他的速度最快，但是可以保证StringBuffer是可以正确操作的。当然大多数情况下就是我们是在单线程下进行的操作，所以大多数情况下是建议用StringBuilder而不用StringBuffer的，就是速度的原因
7. 枚举 enum Non{FAIL,E,P;} <br/>
</font>