> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/singwhatiwanna/article/details/76084580 [](http://creativecommons.org/licenses/by-sa/4.0/)版权声明：本文为博主原创文章，遵循 [CC 4.0 BY-SA](http://creativecommons.org/licenses/by-sa/4.0/) 版权协议，转载请附上原文出处链接和本声明。 本文链接：[https://blog.csdn.net/singwhatiwanna/article/details/76084580](https://blog.csdn.net/singwhatiwanna/article/details/76084580)

前言
--

Android 方向的第一期文章，会专注于 Gradle 系列，名字叫做『 Gradle 从入门到实战』，计划有如下几个课程：

*   Groovy 基础
*   全面理解 Gradle
*   如何创建 Gradle 插件
*   分析 Android 的 build tools 插件
*   实战，从 0 到 1 完成一款 Gradle 插件

本篇文章讲解 Groovy 基础。为什么是 Groovy 基础呢，因为玩转 Gradle 并不需要学习 Groovy 的全部细节。Groovy 是一门 jvm 语言，功能比较强大，细节也很多，全部学习的话比较耗时，对我们来说收益较小。

为什么是 Gradle？
------------

Gradle 是目前 Android 主流的构建工具，不管你是通过命令行还是通过 AndroidStudio 来 build，最终都是通过 Gradle 来实现的。所以学习 Gradle 非常重要。

目前国内对 Android 领域的探索已经越来越深，不少技术领域如插件化、热修复、构建系统等都对 Gradle 有迫切的需求，不懂 Gradle 将无法完成上述事情。所以 Gradle 必须要学习。

如何学习 Gradle？
------------

大部分人对 Gradle 表示一脸懵逼，每当遇到一个问题的时候都需要从网上去查，这是一个误区。

Gradle 不单单是一个配置脚本，它的背后是几门语言，如果硬让我说，我认为是三门语言。

*   Groovy Language
*   Gradle DSL
*   Android DSL

DSL 的全称是 Domain Specific Language，即领域特定语言，或者直接翻译成 “特定领域的语言”，算了，再直接点，其实就是这个语言不通用，只能用于特定的某个领域，俗称 “小语言”。因此 DSL 也是语言。

在你不懂这三门语言的情况下，你很难达到精通 Gradle 的程度。这个时候从网上搜索，或者自己记忆的一些配置，其实对你来说是很大的负担。但是把它们当做语言来学习，则不需要记忆这些配置，因为语言都是有文档的，我们只需要学语法然后查文档即可，没错，这就是学习方法，这就是正道。

你需要做什么呢？跟着我学习就行啦！下面步入正题，让我们来开始学习 Groovy 的基本语法。

Groovy 和 Java 的关系
-----------------

Groovy 是一门 jvm 语言，它最终是要编译成 class 文件然后在 jvm 上执行，所以 Java 语言的特性 Groovy 都支持，我们完全可以混写 Java 和 Groovy。

既然如此，那 Groovy 的优势是什么呢？简单来说，Groovy 提供了更加灵活简单的语法，大量的语法糖以及闭包特性可以让你用更少的代码来实现和 Java 同样的功能。比如解析 xml 文件，Groovy 就非常方便，只需要几行代码就能搞定，而如果用 Java 则需要几十行代码。

Groovy 的变量和方法声明
---------------

在 Groovy 中，通过 def 关键字来声明变量和方法，比如：

```
def a = 1;
def b = "hello world";
def int c = 1;
def hello() {
   println ("hello world");
   return 1;
}
def a = 1;
def b = "hello world";
def int c = 1;
 
def hello() {
   println ("hello world");
   return 1;
}

```

在 Groovy 中，很多东西都是可以省略的，比如

*   语句后面的分号是可以省略的
*   变量的类型和方法的返回值也是可以省略的
*   方法调用时，括号也是可以省略的
*   甚至语句中的 return 都是可以省略的

所以上面的代码也可以写成如下形式：

```
def a = 1
def b = "hello world"
def int c = 1
def hello() {
   println "hello world" // 方法调用省略括号
   1;                    // 方法返回值省略return
}
def hello(String msg) {
   println (msg)
}
// 方法省略参数类型
int hello(msg) {
   println (msg)
   return 1
}
// 方法省略参数类型
int hello(msg) {
   println msg
   return 1 // 这个return不能省略
   println "done"
}
def a = 1
def b = "hello world"
def int c = 1
 
def hello() {
   println "hello world" // 方法调用省略括号
   1;                    // 方法返回值省略return
}
 
def hello(String msg) {
   println (msg)
}
 
// 方法省略参数类型
int hello(msg) {
   println (msg)
   return 1
}
 
// 方法省略参数类型
int hello(msg) {
   println msg
   return 1 // 这个return不能省略
   println "done"
}

```

**总结**

*   在 Groovy 中，类型是弱化的，所有的类型都可以动态推断，但是 Groovy 仍然是强类型的语言，类型不匹配仍然会报错；
*   在 Groovy 中很多东西都可以省略，所以寻找一种自己喜欢的写法；
*   Groovy 中的注释和 Java 中相同。

Groovy 的数据类型
------------

在 Groovy 中，数据类型有：

*   Java 中的基本数据类型
*   Java 中的对象
*   Closure（闭包）
*   加强的 List、Map 等集合类型
*   加强的 File、Stream 等 IO 类型

类型可以显示声明，也可以用 def 来声明，用 def 声明的类型 Groovy 将会进行类型推断。

基本数据类型和对象这里不再多说，和 Java 中的一致，只不过在 Gradle 中，对象默认的修饰符为 public。下面主要说下 String、闭包、集合和 IO 等。

**1. String**

String 的特色在于字符串的拼接，比如

```
def a = 1
def b = "hello"
def c = "a=${a}, b=${b}"
println c
outputs:
a=1, b=hello
def a = 1
def b = "hello"
def c = "a=${a}, b=${b}"
println c
 
outputs:
a=1, b=hello

```

**2. 闭包**

Groovy 中有一种特殊的类型，叫做 Closure，翻译过来就是闭包，这是一种类似于 C 语言中函数指针的东西。闭包用起来非常方便，在 Groovy 中，闭包作为一种特殊的数据类型而存在，闭包可以作为方法的参数和返回值，也可以作为一个变量而存在。

如何声明闭包？

```
{ parameters ->
   code
}
{ parameters ->
   code
}

```

闭包可以有返回值和参数，当然也可以没有。下面是几个具体的例子：

```
def closure = { int a, String b ->
   println "a=${a}, b=${b}, I am a closure!"
}
// 这里省略了闭包的参数类型
def test = { a, b ->
   println "a=${a}, b=${b}, I am a closure!"
}
def ryg = { a, b ->
   a + b
}
closure(100, "renyugang")
test.call(100, 200)
def c = ryg(100,200)
println c
def closure = { int a, String b ->
   println "a=${a}, b=${b}, I am a closure!"
}
 
// 这里省略了闭包的参数类型
def test = { a, b ->
   println "a=${a}, b=${b}, I am a closure!"
}
 
def ryg = { a, b ->
   a + b
}
 
closure(100, "renyugang")
test.call(100, 200)
def c = ryg(100,200)
println c

```

闭包可以当做函数一样使用，在上面的例子中，将会得到如下输出：

```
a=100, b=renyugang, I am a closure!
a=100, b=200, I am a closure!
300
a=100, b=renyugang, I am a closure!
a=100, b=200, I am a closure!
300

```

另外，如果闭包不指定参数，那么它会有一个隐含的参数 it

```
// 这里省略了闭包的参数类型
def test = {
   println "find ${it}, I am a closure!"
}
test(100)
outputs:
find 100, I am a closure! 
// 这里省略了闭包的参数类型
def test = {
   println "find ${it}, I am a closure!"
}
test(100)
 
outputs:
find 100, I am a closure! 

```

闭包的一个难题是如何确定闭包的参数，尤其当我们调用 Groovy 的 API 时，这个时候没有其他办法，只有查询 Groovy 的文档：

[http://www.groovy-lang.org/api.html](http://link.zhihu.com/?target=http%3A//www.groovy-lang.org/api.html)

[http://docs.groovy-lang.org/latest/html/groovy-jdk/index-all.html](http://link.zhihu.com/?target=http%3A//docs.groovy-lang.org/latest/html/groovy-jdk/index-all.html)

下面会结合具体的例子来说明如何查文档。

**3. List 和 Map**

Groovy 加强了 Java 中的集合类，比如 List、Map、Set 等。

List 的使用如下：

```
def emptyList = []
def test = [100, "hello", true]
test[1] = "world"
println test[0]
println test[1]
test << 200
println test.size
outputs:
100
world
4
def emptyList = []
 
def test = [100, "hello", true]
test[1] = "world"
println test[0]
println test[1]
test << 200
println test.size
 
outputs:
100
world
4

```

List 还有一种看起来很奇怪的操作符 <<，其实这并没有什么大不了，左移位表示向 List 中添加新元素的意思，这一点从文档当也能查到。

![](https://img-blog.csdn.net/20170726140909724)

其实 Map 也有左移操作，这如果不查文档，将会非常费解。

Map 的使用如下：

```
def emptyMap = [:]
def test = ["id":1, "name":"renyugang", "isMale":true]
test["id"] = 2
test.id = 900
println test.id
println test.isMale
outputs:
900
true
def emptyMap = [:]
def test = ["id":1, "name":"renyugang", "isMale":true]
test["id"] = 2
test.id = 900
println test.id
println test.isMale
 
outputs:
900
true

```

可以看到，通过 Groovy 来操作 List 和 Map 显然比 Java 简单的多。

这里借助 Map 再讲述下如何确定闭包的参数。比如我们想遍历一个 Map，我们想采用 Groovy 的方式，通过查看文档，发现它有如下两个方法，看起来和遍历有关：

![](https://img-blog.csdn.net/20170726141108426)

可以发现，这两个 each 方法的参数都是一个闭包，那么我们如何知道闭包的参数呢？当然不能靠猜，还是要查文档。

![](https://img-blog.csdn.net/20170726141135044)

通过文档可以发现，这个闭包的参数还是不确定的，如果我们传递的闭包是一个参数，那么它就把 entry 作为参数；如果我们传递的闭包是 2 个参数，那么它就把 key 和 value 作为参数。

按照这种提示，我们来尝试遍历下：

```
def emptyMap = [:]
def test = ["id":1, "name":"renyugang", "isMale":true]
test.each { key, value ->
   println "two parameters, find [${key} : ${value}]"
}
test.each {
   println "one parameters, find [${it.key} : ${it.value}]"
}
outputs:
two parameters, find [id : 1]
two parameters, find [name : renyugang]
two parameters, find [isMale : true]
one parameters, find [id : 1]
one parameters, find [name : renyugang]
one parameters, find [isMale : true]
def emptyMap = [:]
def test = ["id":1, "name":"renyugang", "isMale":true]
 
test.each { key, value ->
   println "two parameters, find [${key} : ${value}]"
}
 
test.each {
   println "one parameters, find [${it.key} : ${it.value}]"
}
 
outputs:
two parameters, find [id : 1]
two parameters, find [name : renyugang]
two parameters, find [isMale : true]
 
one parameters, find [id : 1]
one parameters, find [name : renyugang]
one parameters, find [isMale : true]

```

另外一个 eachWithIndex 方法教给大家练习，自己查文档，然后尝试用这个方法去遍历。

试想一下，如果你不知道查文档，你又怎么知道 each 方法如何使用呢？光靠从网上搜，API 文档中那么多接口，搜的过来吗？记得住吗？

**4. 加强的 IO**

在 Groovy 中，文件访问要比 Java 简单的多，不管是普通文件还是 xml 文件。怎么使用呢？还是来查文档。

![](https://img-blog.csdn.net/20170726141155161)

根据 File 的 eachLine 方法，我们可以写出如下遍历代码，可以看到，eachLine 方法也是支持 1 个或 2 个参数的，这两个参数分别是什么意思，就需要我们学会读文档了，一味地从网上搜例子，多累啊，而且很难彻底掌握：

```
def file = new File("a.txt")
println "read file using two parameters"
file.eachLine { line, lineNo ->
   println "${lineNo} ${line}"
}
println "read file using one parameters"
file.eachLine { line ->
   println "${line}"
}
outputs:
read file using two parameters
1 欢迎
2 关注
3 玉刚说
read file using one parameters
欢迎
关注
玉刚说
def file = new File("a.txt")
println "read file using two parameters"
file.eachLine { line, lineNo ->
   println "${lineNo} ${line}"
}
 
println "read file using one parameters"
file.eachLine { line ->
   println "${line}"
}
 
outputs:
read file using two parameters
1 欢迎
2 关注
3 玉刚说
 
read file using one parameters
欢迎
关注
玉刚说

```

除了 eachLine，File 还提供了很多 Java 所没有的方法，大家需要浏览下大概有哪些方法，然后需要用的时候再去查就行了，这就是学习 Groovy 的正道。

下面我们再来看看访问 xml 文件，也是比 Java 中简单多了。  
Groovy 访问 xml 有两个类：XmlParser 和 XmlSlurper，二者几乎一样，在性能上有细微的差别，如果大家感兴趣可以从文档上去了解细节，不过这对于本文不重要。

在下面的链接中找到 XmlParser 的 API 文档，参照例子即可编程，

[http://docs.groovy-lang.org/docs/latest/html/api/](http://link.zhihu.com/?target=http%3A//docs.groovy-lang.org/docs/latest/html/api/)。

假设我们有一个 xml，attrs.xml，如下所示：

```
<resources>
<declare-styleable >
   <attr >#98ff02</attr>
   <attr >100</attr>
   <attr >renyugang</attr>
</declare-styleable>
</resources>
<resources>
<declare-styleable >
 
   <attr >#98ff02</attr>
   <attr >100</attr>
   <attr >renyugang</attr>
</declare-styleable>
 
</resources>

```

那么如何遍历它呢？

```
def xml = new XmlParser().parse(new File("attrs.xml"))
// 访问declare-styleable节点的name属性
println xml['declare-styleable'].@name[0]
// 访问declare-styleable的第三个子节点的内容
println xml['declare-styleable'].attr[2].text()
outputs：
CircleView
renyugang
def xml = new XmlParser().parse(new File("attrs.xml"))
// 访问declare-styleable节点的name属性
println xml['declare-styleable'].@name[0]
 
// 访问declare-styleable的第三个子节点的内容
println xml['declare-styleable'].attr[2].text()
 
 
outputs：
CircleView
renyugang

```

更多的细节都可以从我发的那个链接中查到，大家有需要查文档即可。

Groovy 的其他特性

除了本文中已经分析的特性外，Groovy 还有其他特性。

*   **Class 是一等公民**

在 Groovy 中，所有的 Class 类型，都可以省略. class，比如：

```
func(File.class)
func(File)
def func(Class clazz) {
}
func(File.class)
func(File)
 
def func(Class clazz) {
}

```

*   **Getter 和 Setter**

在 Groovy 中，Getter/Setter 和属性是默认关联的，比如：

```
class Book {
   private String name
   String getName() { return name }
   void setName(String name) { this.name = name }
}
class Book {
   String name
}
class Book {
   private String name
   String getName() { return name }
   void setName(String name) { this.name = name }
}
 
class Book {
   String name
}

```

上述两个类完全一致，只有有属性就有 Getter/Setter；同理，只要有 Getter/Setter，那么它就有隐含属性。

*   **with 操作符**

在 Groovy 中，当对同一个对象进行操作时，可以使用 with，比如：

```
Book bk = new Book()
bk.id = 1
bk.name = "android art"
bk.press = "china press"
可以简写为：
Book bk = new Book() 
bk.with {
   id = 1
   name = "android art"
   press = "china press"
}
Book bk = new Book()
bk.id = 1
bk.name = "android art"
bk.press = "china press"
 
可以简写为：
Book bk = new Book() 
bk.with {
   id = 1
   name = "android art"
   press = "china press"
}

```

*   **判断是否为真**

在 Groovy 中，判断是否为真可以更简洁：

```
if (name != null && name.length > 0) {}
可以替换为：
if (name) {}
if (name != null && name.length > 0) {}
 
可以替换为：
if (name) {}

```

*   **简洁的三元表达式**

在 Groovy 中，三元表达式可以更加简洁，比如：

```
def result = name != null ? name : "Unknown"
// 省略了name
def result = name ?: "Unknown"
def result = name != null ? name : "Unknown"
 
// 省略了name
def result = name ?: "Unknown"

```

*   **简洁的非空判断**

在 Groovy 中，非空判断可以用? 表达式，比如：

```
if (order != null) {
   if (order.getCustomer() != null) {
       if (order.getCustomer().getAddress() != null) {
       System.out.println(order.getCustomer().getAddress());
       }
   }
}
可以简写为：
println order?.customer?.address
if (order != null) {
   if (order.getCustomer() != null) {
       if (order.getCustomer().getAddress() != null) {
       System.out.println(order.getCustomer().getAddress());
       }
   }
}
 
可以简写为：
println order?.customer?.address

```

*   **使用断言**

在 Groovy 中，可以使用 assert 来设置断言，当断言的条件为 false 时，程序将会抛出异常：

```
def check(String name) {
   // name non-null and non-empty according to Gro    ovy Truth
   assert name
   // safe navigation + Groovy Truth to check
   assert name?.size() > 3
}
def check(String name) {
   // name non-null and non-empty according to Gro    ovy Truth
   assert name
   // safe navigation + Groovy Truth to check
   assert name?.size() > 3
}

```

*   **switch 方法**

在 Groovy 中，switch 方法变得更加灵活，可以同时支持更多的参数类型：

```
def x = 1.23
def result = ""
switch (x) {
   case "foo": result = "found foo"
   // lets fall through
   case "bar": result += "bar"
   case [4, 5, 6, 'inList']: result = "list"
   break
   case 12..30: result = "range"
   break
   case Integer: result = "integer"
   break
   case Number: result = "number"
   break
   case { it > 3 }: result = "number > 3"
   break
   default: result = "default"
}
assert result == "number"
def x = 1.23
def result = ""
switch (x) {
   case "foo": result = "found foo"
   // lets fall through
   case "bar": result += "bar"
   case [4, 5, 6, 'inList']: result = "list"
   break
   case 12..30: result = "range"
   break
   case Integer: result = "integer"
   break
   case Number: result = "number"
   break
   case { it > 3 }: result = "number > 3"
   break
   default: result = "default"
}
assert result == "number"

```

*   **== 和 equals**

在 Groovy 中，== 相当于 Java 的 equals，，如果需要比较两个对象是否是同一个，需要使用. is()。

```
Object a = new Object()
Object b = a.clone()
assert a == b
assert !a.is(b)
Object a = new Object()
Object b = a.clone()
 
assert a == b
assert !a.is(b)

```

本小节参考了如下文章，十分感谢原作者的付出：

1. [http://www.jianshu.com/p/ba55dc163dfd](http://link.zhihu.com/?target=http%3A//www.jianshu.com/p/ba55dc163dfd)

编译、运行 Groovy
------------

可以安装 Groovy sdk 来编译和运行。但是我并不想搞那么麻烦，毕竟我们的最终目的只是学习 Gradle。

推荐大家通过这种方式来编译和运行 Groovy。

在当面目录下创建 build.gradle 文件，在里面创建一个 task，然后在 task 中编写 Groovy 代码即可，如下所示：

```
task(yugangshuo).doLast {
   println "start execute yuangshuo"
   haveFun()
}
def haveFun() {
   println "have fun!"
   System.out.println("have fun!");
   1
   def file1 = new File("a.txt")
   def file2 = new File("a.txt")
   assert file1 == file2
   assert !file1.is(file2)
}
class Book {
   private String name
   String getName() { return name }
   void setName(String name) { this.name = name }
}
task(yugangshuo).doLast {
   println "start execute yuangshuo"
   haveFun()
}
 
def haveFun() {
   println "have fun!"
   System.out.println("have fun!");
   1
   def file1 = new File("a.txt")
   def file2 = new File("a.txt")
   assert file1 == file2
   assert !file1.is(file2)
}
 
class Book {
   private String name
   String getName() { return name }
   void setName(String name) { this.name = name }
}

```

只需要在 haveFun 方法中编写 Groovy 代码即可，如下命令即可运行：

```
gradle yugangshuo


```

本公众号聚焦于『Android 开发前沿、AI 技术等、职业发展、生活感悟、妹子图』，欢迎大家关注玉刚说：

![](https://img-blog.csdn.net/20170726142854164)