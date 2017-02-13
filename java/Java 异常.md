# Java 异常 #
<font size="5"><b>
1. try..catch...finally finally无论如何都会被执行到,除非catch块中有System.exit(),但是这条语句是不允许写的.<br/>
2. 如果catch块中有return,catch捕获后,先执行块中其他代码,再执行finally中的代码,最后执行catch块中的return语句<br/>
3. 如何知道调用的函数可能会抛出哪些异常,在函数定义后边写throws XXException.<br/>
4. 自定义异常,可以自己写一个类继承自Exception,并重写构造方法,传入异常信息,还可以在抛异常出传入出错的那个对象,方便跟踪错误.像下面这样,我抛出异常时,就附带了异常信息,而且还附带了出了问题的Person类,这样方便调试,能很快定位哪里出错.
    
    class MyException extends Exception{
		public MyException(String message,Person person){
			
		}
	}
<br/>
5. Java中常见的异常<br/>
----------------------------------------------

- Exception 异常层次结构的父类
- ArithmeticException 算术错误情形，如以零作除数
- ArrayIndexOutOfBoundsException 数组下标越界
- java.lang.NullPointerException 尝试访问 null 对象成员
- ClassNotFoundException 不能加载所需的类
- IllegalArgumentException 方法接收到非法参数
- ClassCastException 对象强制类型转换出错
- NumberFormatException 数字格式转换异常，如把"abc"转换成数字


----------------------------------------------
6. Checked和Unchecked异常
------------------------------------------------------
- Java当中Checked异常是必须catch并处理的，而unchecked异常不强制要求程序员处理
- 在处理checked异常的catch语句中，不要什么都不做
- 不要直接catch Exception，这样会处理所有的异常包括checked和unchecked
<br/>
------------------------------------------------------
</b></font>
