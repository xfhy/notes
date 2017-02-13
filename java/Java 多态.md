# Java 多态 #
<font size="5">
1. Java中所有方法都是virtual方法(虚方法)<br/>
2. 随机数生成器Romdom,需要配合种子使用.其实这是伪随机(固定的序列,同一种子生成的随机数相同).nextInt(10)表示去的[0,10)之间的数.<br/>
3. <font size="5" color="red"><b>
instanceof(重点,重点,重点)&nbsp;&nbsp;&nbsp;使用方法:对象 instanceof 类或接口 ,作用:判断是否为类或接口的对象. Java中强制类型转换,如果不是基本类型则必须加instanceof判断</b>
</font><br/>
4. 多态:①继承②方法重名(父与子)<br/>
5. Object:Java中所有类都直接或间接的继承自Object类.Object中有一些重要的方法,需要经常用到.如下:

----------
- +final getClass():Class&nbsp;&nbsp;&nbsp;获取当前对象所属的类信息

- +toString():String&nbsp;&nbsp;&nbsp;返回当前对象本身的有关信息

- equals(Object obj):boolean&nbsp;&nbsp;&nbsp;比较两个对象是否是相等

- =clone():clone&nbsp;&nbsp;&nbsp;生成当前对象的一个拷贝

- +hashCode():int&nbsp;&nbsp;&nbsp;返回该对象的哈希代码值

- =finalize():void&nbsp;&nbsp;&nbsp;销毁对象时被调用的方法

----------
6.为了调试方便,自定义类属性多的话,则尽量重写toString()方法<br/>
7. 如果子类中不重写Object类中的equals()方法,则equals()方法与 "==" 一样<br/>
8. System.gc():建议虚拟机进行垃圾回收.<br/>
9. 不建议在finalize()中写释放资源的代码,如果要释放资源,可以自己写一个方法,专门用于释放资源.<br/>
10. 自定义类要实现克隆,clone(),则需实现Cloneable接口,自定义一个方法,public修饰,名叫clone,返回克隆好的对象,Object中的clone()方法是非常快的.<br/>
11. 深拷贝:对自定义类中的内部类中写一个clone()方法(内部类里面也写一个克隆方法),当克隆该自定义类时,需要把内部类也克隆一次.不然就只会克隆该自定义类而自定义类里面的内部类不会被克隆.<br/>
12. 抽象类有利于代码复用,接口易于代码维护<br/>
</font>