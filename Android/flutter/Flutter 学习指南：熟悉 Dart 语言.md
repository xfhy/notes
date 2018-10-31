> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/mtm9PKv2G_hSgbvXTbQtTA

<section class="" style="font-size: 16px;color: rgb(84, 84, 84);margin-left: 6px;margin-right: 6px;line-height: 1.6;letter-spacing: 1px;word-break: break-all;font-family: &quot;Helvetica Neue&quot;, PingFangSC-Regular, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;">

> 本文由**玉刚说写作平台**提供写作赞助
> 作者：**水晶虾饺**

本文是 Flutter 学习指南的第 4 篇，假定读者有一定的编程经验。通过快速浏览 Dart 的一些基础特性，让读者具备使用它进行开发的基本能力。

# 变量

## 基本类型

```
bool done = true;int num = 2;double x = 3.14;final bool visible = false;final int amount = 100;final double y = 2.7;const bool debug = true;const int sum = 42;const double z = 1.2;
```

跟常用的其他语言不同，Dart 没有 byte、char 和 float，int、double 都是 64 位。final 跟 Java 里的 final 一样，表示一个运行时常量（在程序运行的时候赋值，赋值后值不再改变）。const 表示一个编译时常量，在程序编译的时候它的值就确定了。

如果你觉得每次写变量类型太麻烦，你应该会喜欢 Dart 的类型推断功能：

```
var done = true;var num = 2;var x = 3.14;final visible = false;final amount = 100;final y = 2.7;const debug = true;const sum = 42;const z = 1.2;
```

Dart 里所有的东西都是对象，包括 int、函数。

## String

```
var str = ' foo';var str2 = str.toUpperCase();var str3 = str.trim();assert(str == str2);assert(!identical(str, str2));
```

Dart 里的 String 跟 Java 中的一样，是不可变对象；不同的是，检测两个 String 的内容是否一样事，我们使用 == 进行比较；如果要测试两个对象是否是同一个对象（indentity test），使用 identical 函数。

## List、Map 和 Set

### List

```
// 使用构造函数创建对象// 跟 var list = new List<int>(); 一样var list = List<int>();list.add(1);list.add(2);// 通过字面量创建对象，list 的泛型参数可以从变量定义推断出来。// 推荐使用字面量方式创建对象var list2 = [1, 2];// 没有元素，显式指定泛型参数为 intvar list3 = <int>[];list3.add(1);list3.add(2);var list4 = const[1, 2];// list4 指向的是一个常量，我们不能给它添加元素（不能修改它）list4.add(3);       // error// list4 本身不是一个常量，所以它可以指向另一个对象list4 = [4, 5];     // it's fineconst list5 = [1, 2];// 相当于 const list5 = const[1, 2];list5.add(3);       // error// Dart 同样提供了 for-in 循环。// 因为语音设计时就考虑到了这个需求，in 在 Dart 里是一个关键字var list6 = [1, 3, 5, 7];for (var e in list6) {  print(e);}
```

在 Dart 2 里，创建对象时可以省略 new 关键字，也推荐省略 new。

### Set

```
var set = Set<String>();set.add('foo');set.add('bar');assert(set.contains('foo'));
```

我们只能通过 Set 的构造函数创建实例。

### Map

```
var map = Map<String, int>();// 添加map['foo'] = 1;map['bar'] = 3;// 修改map['foo'] = 4;// 对应的 key 不存在时，返回 nullif (map['foobar'] == null) {  print('map does not contain foobar');}var map2 = const {  'foo': 2,  'bar': 4,};var map3 = <String, String>{};
```

## dynamic 和 Object

前面我们说过，Dart 里所有东西都是对象。所有这些对象的父类就是 Object。

```
Object o = 'string';o = 42;o.toString();   // 我们只能调用 Object 支持的方法dynamic obj = 'string';obj['foo'] = 4;  // 可以编译通过，但在运行时会抛出 NoSuchMethodError
```

Object 和 dynamic 都使得我们可以接收任意类型的参数，但两者的区别非常的大。

使用 Object 时，我们只是在说接受任意类型，我们需要的是一个 Object。类型系统会保证其类型安全。

使用 dynamic 则是告诉编译器，我们知道自己在做什么，**不用做类型检测**。当我们调用一个不存在的方法时，会执行 noSuchMethod() 方法，默认情况下（在 Object 里实现）它会抛出 NoSuchMethodError。

为了在运行时检测进行类型检测，Dart 提供了一个关键字 is：

```
dynamic obj = <String, int>{};if (obj is Map<String, int>) {  // 进过类型判断后，Dart 知道 obj 是一个 Map<String, int>，  // 所以这里不用强制转换 obj 的类型，即使我们声明 obj 为 Object。  obj['foo'] = 42;}// 虽然 Dart 也提供了 as 让我们进行类型的强制转换，但为了进来更安全// 的转换，更推荐使用 isvar map = obj as Map<String, int>;
```

# 语句

```
var success = true;if (success) {  print('done');} else {  print('fail');}for (var i = 0; i < 5; ++i) {  print(i);}var sum = 0;var j = 1;do {  sum += j;  ++j;} while (j < 5);while (sum-- > 0) {  print(sum);}var type = 1;switch (type) {  case 0:    // ...    break;  case 1:    // ..    break;  case 2:    // ...    break;  default:    // ...    break;}
```

常见的 if/else，do while，while 和 switch 在 Dart 里面都支持。switch 也支持 String 和 enum。

# 函数

最普通的函数看起来跟 Java 里的一样：

```
int foo(int x) {  return 0;}
```

Dart 也支持可选参数：

```
void main() {  print(foo(2));  print(foo(1, 2));}int foo(int x, [int y]) {  // 是的，int 也可以是 null  if (y != null) {    return x + y;  }  return x;}// 结果：// 2// 3
```

默认参数也是支持的：

```
int foo(int x, [int y = 0]) {  return x + y;}
```

还能用具名参数（named parameters）：

```
void main() {  print(foo(x: 1, y: 2));  // 具名参数的顺序可以是任意的  print(foo(y: 3, x: 4));  // 所有的具名参数都是可选的，这个调用是合法的，但它会导致 foo() 在运行时抛异常  print(foo());}int foo({int x, int y}) {  return x + y;}
```

具名参数也可以有默认参数：

```
void main() {  print(foo(x: 1, y: 2));  print(foo());}int foo({int x = 0, int y = 0}) {  return x + y;}
```

如果想告诉用户某个具名参数是必须的，可以使用注解 @required：

```
int foo({@required int x, @required int y}) {  return x + y;}
```

@required 是 meta 包里提供的 API，更多的信息读者可以查看 https://pub.dartlang.org/packages/meta。

函数还可以在函数的内部定义：

```
// typedef 在 Dart 里面用于定义函数类型的别名typedef Adder = int Function(int, int);Adder makeAdder(int extra) {  int adder(int x, int y) {    return x + y + extra;  }  return adder;}void main() {  var adder = makeAdder(2);  print(adder(1, 2));}// 结果：// 5
```

像上面这样简单的函数，我们还可以使用 lambda：

```
typedef Adder = int Function(int, int);Adder makeAdder(int extra) {  return (int x, int y) {    return x + y + extra;  };  // 如果只有一个语句，我们可以使用下面这种更为简洁的形式  // return (int x, int y) => x + y + extra;}void main() {  var adder = makeAdder(2);  print(adder(1, 2));}
```

Dart 里面不仅变量支持类型推断，lambda 的参数也支持自动推断。上面的代码还可以进一步简化为：

```
typedef Adder = int Function(int, int);Adder makeAdder(int extra) {  // 我们要返回的类型是 Adder，所以 Dart 知道 x, y 都是 int  return (x, y) => x + y + extra;}void main() {  var adder = makeAdder(2);  print(adder(1, 2));}
```

美中不足的是，Dart 不支持函数的重载。

# 异常

抛出异常：

```
throw Exception('put your error message here');
```

捕获异常：

```
try {  // ...// 捕获特定类型的异常} on FormatException catch (e) {  // ...// 捕获特定类型的异常，但不需要这个对象} on Exception {  // ..// 捕获所有异常} catch (e) {  // ...} finally {  // ...}
```

跟 Java 不同的是，Dart 可以抛出任意类型的对象：

```
throw 42;
```

# 类

定义一个类：

```
class Point2D {  static const someConst = 2;  int x;  // 成员变量也可以是 final 的  final int y;  Point2D(int x, int y) {    this.x = x;    this.y = y;  }}
```

由于这种初始化方式很常见，Dart 提供了更简洁的方式：

```
class point2d {  int x;  int y;  point2d(this.x, this.y);}
```

此外，还可以使用初始化列表（initializer list）对对象进行初始化：

```
class Point2D {  int x;  int y;  // 由于是在 initializer list 中，Dart 知道第一个 x 是 this.x，  // 第二个 x 是构造函数的参数  Point2D(int x, int y) : x = x, y = y {    // ...  }}
```

initializer list 会在构造函数的函数体运行前执行。

Dart 具有垃圾收集功能，对象的使用跟 Java 里几乎是一样的：

```
main() {  var point = Point2D(1, 2);  point.x = 4;  print(point);}class Point2D {  int x;  int y;  Point2D(this.x, this.y);  // 所有的类都继承自 Object，toString() 是 Object 中的方法  @override  String toString() {    // 在字符串的内部可以通过 ${expression} 的方式插入值，如果    // expression 是一个变量，可以省略花括号    return "Point2D{x=$x, y=$y}";  }}// 结果：// Point2D{x=4, y=2}
```

Dart 使用 package 的概念来管理源码和可见性。它没有 public、private 之类的访问权限控制符，默认情况下，所有的符号都是公开的。如果我们不想某个变量对包的外部可见，可以使用下划线开头来给变量命名。

```
class _Foo {  // ...}class Bar {  int _x;}
```

下面我们使用 Dart 的访问控制，实现一个带偏移量的 Point：

```
class OffsetPoint {  int _x;  int _y;  int offset;  OffsetPoint(int x, int y, int offset)      : _x = x, _y = y, offset = offset {}  // 定义一个 getter  int get x => _x + offset;  // getter 不能有参数，连括号都省掉了  int get y {    return _y + offset;  }  // 定义 setter  void set x (int x) => _x = x;  void set y (int y) => _y = y;  @override  String toString() {    return "OffsetPoint{x=$x, y=$y}";  }}main() {  var point = OffsetPoint(1, 2, 10);  // 使用 getter/setter 时，就像它是一个普通的成员变量  print(point.x)  print(point);  point.x = 4;  print(point);}// 结果：// 11// OffsetPoint{x=11, y=12}// OffsetPoint{x=14, y=12}
```

在 Dart 里继承对象也很简单：

```
class Point2D {  int x;  int y;  Point2D(this.x, this.y);}class Point3D extends Point2D {  int z;  // 父类的构造函数只能在 initializer list 里调用  Point3D(int x, int y, int z): z = z, super(x, y) {  }}
```

但是对象构造时它跟 Java、C++ 都不太一样：

1.  先执行子类 initializer list，但只初始化自己的成员变量

2.  初始化父类的成员变量

3.  执行父类构造函数的函数体

4.  执行之类构造函数的函数体

基于这个初始化顺序，推荐是把 super() 放在 initializer list 的最后。此外，在 initializer list 里不能访问 this（也就是说，只能调用静态方法）。

虽然 Dart 是单继承的，但它也提供了一定程度的多重继承支持：

```
abstract class Bark {  void bark() {    print('woof');  }}class Point3D extends Point2D with Bark {  int z;  // 父类的构造函数只能在 initializer list 里调用  Point3D(int x, int y, int z): z = z, super(x, y) {  }}// 没有其他类需要继承，所以直接 extends Bark 就可以了class Foo extends Bark {}void main() {  var p = Point3D(1, 2, 3);  p.bark();}
```

Dart 把支持多重继承的类叫做 mixin。更详细的介绍，读者可以参考 https://www.dartlang.org/articles/language/mixins。

# 泛型

```
class Pair<S, T> {  S first;  T second;  Pair(this.first, this.second);}void main() {  var p = Pair('hello', 2);  print(p is Pair<String, int>);  // is! 也是 Dart 的运算符，下面的语句跟 !(p is Pair<int, int>) 是一样的，  // 但 is! 读起来跟像英语  print(p is! Pair<int, int>);  print(p is Pair);}// 结果：// true// true// true
```

跟 Java 不同，Dart 的泛型参数类型在运行时是保留的。

# Future

Dart 是单线程的，主线程由一个事件循环来执行（类似 Android 的主线程）。对于异步代码，我们通过 Future 来获取结果：

```
import 'dart:io';void foo() {  var file = File('path-to-your-file');  file.exists()      .then((exists) => print('file ${exists ? 'exists' : 'not exists'}'))      .catchError((e) => print(e));}
```

Dart 2 提供了 async 函数，用来简化这种编程范式。下面这段代码的效果跟上面是一样的：

```
void foo() async {  var file = File('path-to-your-file');  try {    var exists = await file.exists();    print('file ${exists ? 'exists' : 'not exists'}');  } catch (e) {    print(e);  }}
```

但是要注意，上面两段代码并不是完全一样的：

```
// import 语句用于导入一个包import 'dart:io';void main() {  foo();  bar();}void bar() {  var file = File('path-to-your-file');  file.exists()      .then((exists) => print('bar: file ${exists ? 'exists' : 'not exists'}'))      .catchError((e) => print(e));  print('bar: after file.exists() returned');}void foo() async {  var file = File('path-to-your-file');  try {    var exists = await file.exists();    print('bar: file ${exists ? 'exists' : 'not exists'}');    print('bar: after file.exists() returned');  } catch (e) {    print(e);  }}// 一种可能的结果：// bar: after file.exists() returned// foo: file not exists// foo: after file.exists() returned// bar: file not exists
```

这里的关键在于，bar 函数里面，file.exists() 执行完后，会马上执行下面的语句；而 foo 则会等待结果，然后才继续执行。关于 Future 的更多的细节，强烈建议读者阅读 https://webdev.dartlang.org/articles/performance/event-loop。

最后需要说明的是，Dart 的生成器、Stream 在这里我们并没有介绍，读者可以参考 https://www.dartlang.org/guides/language/language-tour。此外，Dart 官网还有许多资源等待读者去发掘。

**推荐阅读**
[Flutter 学习指南：Flutter 是什么？](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492485&idx=1&sn=a6b96ad736c862b6efbed31192370b68&chksm=8eec87fab99b0eec8f81ee0f9d6b03403b530ff7ff4b5c2d07e1e9fc6762081b2f0b0af89a9c&scene=21#wechat_redirect) [Flutter 学习指南：开发环境搭建](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492521&idx=1&sn=723658efc8221f15f6a6a93e1a3f08c8&chksm=8eec87d6b99b0ec0fef71ee2a29f03c9997124fd91c031d350500df973e648954435e823b04f&scene=21#wechat_redirect) [Flutter 学习指南：编写第一个应用](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649493273&idx=1&sn=7e543c597adfab4bdd79a0699239db64&chksm=8eec84e6b99b0df0020242b0ed085b4b38fcbae8a31710d387cd3545ddb22139ed1d32eb545c&scene=21#wechat_redirect)

编程 · 思维 · 职场
欢迎大家扫码关注

![](https://mmbiz.qpic.cn/mmbiz_jpg/zKFJDM5V3WzzNpnqOGq3mMO64mFVSicAIkzUSiam08j6DetjnjeujRjEAZRe7PqmPGqow3GWxSk4gas6r7BA4k6A/640?wx_fmt=jpeg)

</section>