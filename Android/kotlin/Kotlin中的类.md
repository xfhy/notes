# 类

## 定义一个类

如果你想定义一个类，你只需要使用 class  关键字。

	class MainActivity{
	}

它有一个默认唯一的构造器。我们会在以后的课程中学习在特殊的情况下创建其它
额外的构造器，但是请记住大部分情况下你只需要这个默认的构造器。你只需要在
类名后面写上它的参数。如果这个类没有任何内容可以省略大括号：
class Person(name: String, surname: String)
那么构造函数的函数体在哪呢？你可以写在 init  块中：

	class Person(name: String, surname: String) {
		init{
		...
		}
	}

## 类继承

默认任何类都是基础继承自 Any  （与java中的 Object  类似），但是我们可以继
承其它类。所有的类默认都是不可继承的（final），所以我们只能继承那些明确声
明 open  或者 abstract  的类：

	open class Animal(name: String)
	class Person(name: String, surname: String) : Animal(name)

## 函数

函数（我们Java中的方法）可以使用 fun  关键字就可以定义:

	fun onCreate(savedInstanceState: Bundle?) {
	}

如果你没有指定它的返回值，它就会返回 Unit  ，与Java中的 void  类似，但
是 Unit  是一个真正的对象。你当然也可以指定任何其它的返回类型：

	fun add(x: Int, y: Int) : Int {
		return x + y
	}

小提示：**分号不是必须的**
就想你在上面的例子中看到的那样，我在每句的最后没有使用分号。当然
你也可以使用分号，分号不是必须的，而且不使用分号是一个不错的实
践。当你这么做了，你会发现这节约了你很多时间。

然而如果返回的结果可以使用一个表达式计算出来，你可以不使用括号而是使用等
号：`fun add(x: Int,y: Int) : Int = x + y`

## 构造方法和函数参数
Kotlin中的参数与Java中有些不同。如你所见，我们先写参数的名字再写它的类
型：

	fun add(x: Int, y: Int) : Int {
		return x + y
	}

我们可以给参数指定一个默认值使得它们变得可选，这是非常有帮助的。这里有一
个例子，在Activity中创建了一个函数用来toast一段信息：

	fun toast(message: String, length: Int = Toast.LENGTH_SHORT) {
		Toast.makeText(this, message, length).show()
	}

如你所见，第二个参数（length）指定了一个默认值。这意味着你调用的时候可以
传入第二个值或者不传，这样可以避免你需要的重载函数：

	toast("Hello")
	toast("Hello", Toast.LENGTH_LONG)

这个与下面的Java代码是一样的：

	void toast(String message){
	}
	void toast(String message, int length){
		Toast.makeText(this, message, length).show();
	}

这跟你想象的一样复杂。再看看这个例子：

toast(message = "Hello", length = Toast.LENGTH_SHORT)