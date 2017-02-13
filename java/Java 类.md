# Java 类 #
<font size="5">
1. 类中的属性,eg:①private int a = 2; 构造方法中②a=4;   则①比②先执行,最后a==4;<br/>
2. this();调用本类中构造方法,必须作为第一条语句出现<br/>
3. 类图,画法: +:public , -:private, +print():void => 方法, +name:String => 属性<br/>
4. 调用父类的属性或方法: eg: super.health; super.print();<br/>
5. 使用final修饰引用型变量,变量的值是固定不变的,而变量所指向的对象的属性值是可变的<br/>
6. 如果一个类继承于一个抽象类，则子类必须实现父类的抽象方法。如果子类没有实现父类的抽象方法，则必须将子类也定义为为abstract类。<br/>
7. 抽象类不能用来创建对象；<br/>
<font/>