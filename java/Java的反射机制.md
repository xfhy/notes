# Java的反射机制

## 1. 简单介绍

- 在Java语言中，动态获取类的信息以及动态调用对象的方法的功能被称为Java的反射（Reflection）机制。
- Reflection是Java不同于C++、C#等静态语言，而被视为准动态语言的一个关键性质。
- 这个机制允许程序在运行时透过Reflection APIs取得任何一个已知名称的class的内部信息：
 - 其modifiers（诸如public、static等）
 - superclass（例如Object）
 - 实现了的 interfaces （例如Serializable）
 - 其fields和methods的所有信息
 - 并可于运行时改变fields内容或调用methods。
- Java程序可以加载一个运行时才得知名称的class，获悉其完整构造（但不包括methods定义），并生成其对象实体、或对其fields设值、或唤起其methods。
- 这种“看透”class的能力（the ability of the program to examine itself）被称为introspection（内省、内观、反省）。Reflection和introspection是常被并提的两个术语。

## 2. Java反射机制的功能

- 在运行时判断任意一个对象所属的类。
- 在运行时构造任意一个类的对象。
- 在运行时判断任意一个类所具有的成员变量和方法。
- 在运行时调用任意一个对象的方法。

## 3. Reflection相关类

在JDK中，主要由以下类来实现Java反射机制，这些类（除了第一个）都位于java.lang.reflect包中
- Class类：代表一个类，位于java.lang包下。
- Field类：代表类的成员变量（成员变量也称为类的属性）。
- Method类：代表类的方法。
- Constructor类：代表类的构造方法。
- Array类：提供了动态创建数组，以及访问数组的元素的静态方法。

## 4. Class对象

- Class类是整个Java反射机制的基础。
- Java中，无论生成某个类的多少个对象，这些对象都被同一个类描述，
描述它们的类都对应于同一个Class对象
  - 这个Class对象是由JVM生成的
  - 通过这个Class对象能够获悉整个类的结构
  - 通过这个Class对象还可以操作每一个对象
  - Class对象不能通过new的方式创建。
- 如何获取Class对象？
	1. 使用Class类的静态方法
		`Class clazz = Class.forName("java.lang.String");`
	2. 使用类的.class语法
		`Class clazz = String.class;`
	3. 使用对象的getClass()方法
		`String str;Class clazz = str.getClass();`

## 5. Constructor类

Constructor类用来描述类中所定义的构造方法。
获取类的所有构造方法
`Constructor constructors[] =
	Class.forName(“java.lang.String”).getConstructors();
`

获取类中某个具体的构造方法
`Constructor constructor =
	Class.forName(“java.lang.String”).getConstructor(String.class);
`

## 6. 构造对象

正常情况

	String str1 = new String();
	String str2 = new String(“hello”);


通过反射调用默认构造函数：

	String str1 = (String)Class.forName(“java.lang.String”).newInstance();


通过反射调用带参数构造函数：

	Constructor constructor = String.class.getConstructor(String.class)
	String str2 = (String)constructor.newInstance(“hello”); 

## 7. Field类

- Field类用来表示类中的属性(字段)。
- 获取所有的字段
	`Field[] fields = User.class.getFields();`

- 获取某个特定名字的public字段
`Field field = Class.getField(String name);`

- 获取某个特定名字的非公有字段
`Field field = Class.getDeclaredField(String fieldName);`

通过类的getXXXField()方法获取的是类的方法，不是对象的方法。

## 8. Method类

Method用来表示类中的方法。
通过Class对象的如下方法得到Method对象：
按名称得到某个特定的public方法(包括从父类或接口继承的方法)
`Method getMethod(String name, Class<?>... parameterTypes) `

得到public方法(包括从父类或接口继承的方法)
`Method[] getMethods()  `

得到方法(不包括继承的方法)
`Method[] getDeclaredMethods()
Method getDeclaredMethod(String name, Class<?>... parameterTypes)`


- 调用Method

invoke(Object obj,Object …obj)方法用来调用Method所表示的方法。
其中，第一个参数表示此方法作用于哪一个对象。
如果调用的是个静态方法，那么invoke()方法中第一个参数用null表示。
后面的参数表示该方法真实的参数


## 9. Java反射的应用

`ReflectTest.java`文件

	public class ReflectTest {

		public static void main(String[] args) throws Exception {
	
			/*-------1, 通过构造方法构造一个String对象------*/
			// 获得String的new String(StringBuffer s) 的这个构造方法
			Constructor<String> constructor = String.class.getConstructor(StringBuffer.class);
			// 根据上面得到的Constructor构造出String对象
			String str1 = constructor.newInstance(new StringBuffer("abc"));
			System.out.println(str1);
	
			/*---------2, 通过构造方法构造一个String对象------------*/
			String str2 = String.class.newInstance();
	
			/*---------3, 通过Field获取对象的属性------------*/
			Point point = new Point(3, 5);
			// 通过字节码获取Point类的y属性,注意,不是Point对应的对象的y属性
			// 获取共有的属性
			Field fieldY = point.getClass().getField("y");
			System.out.println(fieldY.get(point));
			// 获取私有的属性
			Field fieldX = point.getClass().getDeclaredField("x");
			// 设置可以获得 暴力反射 设置完了这个之后才可以得到类的私有属性的值
			fieldX.setAccessible(true);
			System.out.println(fieldX.get(point));
	
			/*-------4,小案例:通过反射将任意一个对象中的所有String类型的成员变量所对应的字符串内容中的b改成a----------*/
			User user = new User();
			Field[] fields = user.getClass().getDeclaredFields(); // 获取字节码中所有的成员变量
			for (Field field : fields) { // 遍历
				field.setAccessible(true); // 设置可以获得私有属性
	
				if (field.getType() == String.class) { // 判断该属性的类型是否是String
														// 字节码就String,不需要用equals
					String oldValue = (String) field.get(user); // 获取属性里面的值
					String newValue = oldValue.replace('b', 'a'); // 把字符串里面所有的b替换成a
					field.set(user, newValue); // 把改过的值放回属性中
				}
	
			}
	
			System.out.println(user);
	
			/*-----------5,通过反射调用对象里面的public方法---------*/
			String str4 = "abc";
			// 获取这个String类里面的方法charAt,并且形参参数是int的那个
			Method methodCharAt = String.class.getMethod("charAt", int.class);
			// 通过一个实体类来调用此方法,并传入参数1 invoke调用方法
			System.out.println(methodCharAt.invoke(str4, 1));
	
			/*-------------6,通过反射调用对象里面的private方法----------------*/
			User user2 = new User();
			Method methodGetX = User.class.getDeclaredMethod("getX", null);
			methodGetX.setAccessible(true);
			System.out.println(methodGetX.invoke(user2, null));
	
			/*-------------6,通过反射调用类里面的static方法----------------*/
			Method methodGetScore = User.class.getMethod("getScore", int.class);
			System.out.println(methodGetScore.invoke(null, 6));
	
			/*-----------7, 对接收数组参数的成员方法进行反射------------*/
			String startingClassName = args[0];   //这里需要传入下面的Traversal的完整类名
			//加载那个类的字节码,获取里面的main方法  并且main方法的参数是String[]
			Method methodMain = Class.forName(startingClassName).getMethod("main", String[].class);
			//1. 执行main方法   编译器为了兼容1.4版本的,传入数组它会默认会把它拆分为单个,这里传入Object[]会被拆分,这样
			//String[]会被作为参数
			methodMain.invoke(null, new Object[] { new String[] { "dada", "qqqq", "wwww" } });
			//2. 执行main方法   或者直接将String[]数组转成Object,这样传入时就不会被拆分啦
			methodMain.invoke(null, (Object)new String[] { "dada", "qqqq", "wwww" });
		}
	
	}
	
	class Traversal {
		public static void main(String[] args) {
			for (int i = 0; i < args.length; i++) {
				System.out.println(args[i]);
			}
		}
	}

`User.java`文件

	public class User {

		private int x = 2;
		private String name = "wobcao";
		private String rule = "hebh";
		public String age = "1b3";
	
		private int getX() {
			return x;
		}
	
		@Override
		public String toString() {
			return "User [x=" + x + ", name=" + name + ", rule=" + rule + ", age=" + age + "]";
		}
	
		public static int getScore(int number) {
			return number * 2;
		}
	
	}

`Point.java`文件

	public class Point {

		/**
		 * 私有属性x
		 */
		private int x;
		/**
		 * 共有属性y
		 */
		public int y;
	
		/**
		 * 构造方法
		 * @param x
		 * @param y
		 */
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	
	}

