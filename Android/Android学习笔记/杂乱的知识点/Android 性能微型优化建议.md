# Android 性能微型优化建议

官方原文：https://developer.android.com/training/articles/perf-tips.html#PackageInner

> 本文档主要涉及可以在整合时提高整体应用程序性能的微型优化，但这些更改不太可能会产生显着的性能影响。选择正确的算法和数据结构应始终是你的首要任务，但不属于本文档的范围。你应该使用本文档中的提示作为通用编码实践，你可以将其纳入你的习惯以实现一般代码效率。

编写高效代码有两个基本规则：

- 不要做你不需要做的工作。
- 如果可以避免，请不要分配内存。

当微软优化Android应用程序时，你遇到的最棘手的问题之一就是你的应用程序肯定会在多种类型的硬件上运行。在不同处理器上运行的不同版本的VM以不同的速度运行。甚至一般情况下，你可以简单地说“设备X是比设备Y更快/更慢的因素F”，并将结果从一个设备扩展到其他设备。具体来说，仿真器上的测量对于任何设备的性能都不甚了解。具有和不具有JIT（JIT是”Just In Time Compiler”的缩写，就是”即时编译技术”，与Dalvik虚拟机相关。JIT是在2.2版本提出的，目的是为了提高Android的运行速度，一直存活到4.4版本，因为在4.4之后的ROM中，就不存在Dalvik虚拟机了。）的设备之间也存在巨大差异：具有JIT的设备的最佳代码并不总是没有设备的最佳代码。

为了确保你的应用在各种设备上的表现良好，请确保你的代码在所有级别都有效，并大力优化你的性能。

## 1. 避免创建不必要的对象

创建一个对象是需要付出代价的。用于临时对象的具有每线程分配池的代代垃圾收集器可以使分配更便宜，但分配内存总是比不分配内存更昂贵。

当您在应用程序中分配更多对象时，您将强制定期进行垃圾回收，从而在用户体验中创建一些“打嗝”。 Android 2.3中引入的并发垃圾回收器有助于实现，但必须避免不必要的工作。

因此，您应避免创建不需要的对象实例。一些可以帮助你理解的事例：

- 如果你有一个方法返回一个字符串，并且你知道它的结果总是被附加到一个StringBuffer，改变你的方法和实现，使函数直接追加，而不是创建一个短命的临时对象。
- 从一组输入数据中提取字符串时，尝试返回原始数据的子字符串，而不是创建副本。您将创建一个新的String对象，但它将与数据共享char []。 （如果您仅使用原始输入的一小部分，那么您的权衡将会在内存中保持一切，如果您使用这种方式）。

一个更激进的想法是将多维数组分解成并行的单维数组：

- 一个int数组比一个Integer对象的数组要好得多，但这也概括为两个并行的int数组也比一个（int，int）对象的数组更有效率。原始类型的任何组合也是如此。
- 如果您需要实现一个存储（Foo，Bar）对象的元组的容器，请尝试记住两个并行的Foo []和Bar []数组通常比单个数组的自定义（Foo，Bar）对象要好得多。 （当然，这是例外，当您为其他代码设计一个API时，在这种情况下，为了实现良好的API设计，通常情况下要做到一个小小的妥协，最好是在你的自己的内部代码，你应该尝试尽可能高效。）

一般来说，如果可以，避免创建短期临时对象。创建的对象越少意味着垃圾收集越少，直接影响用户体验。

## 2. Prefer Static Over Virtual

如果您不需要访问对象的字段，请使您的方法成为静态方式。 调用速度将快15％-20％。 这也是很好的做法，因为你可以从方法签名中得知调用该方法不能改变对象的状态。

## 3. 对于常量请使用：static final

考虑以下声明在一个类的顶部：

``` java
static int intVal = 42;
static String strVal =“Hello，world！”;
```
编译器生成一个类初始化方法，称为`<clinit>`，它是在类首次使用时执行的。该方法将值42存储到intVal中，并从strfile的类文件字符串常量表中提取引用。当这些值稍后引用时，它们将通过字段查找进行访问。

我们可以通过“final”关键字改善这种方式：
``` java
static final int intVal = 42;
static final String strVal =“Hello，world！”;
```
该类不再需要一个`<clinit>`方法，因为这些常量进入dex文件中的静态字段初始化器。引用intVal的代码将直接使用整数值42，并且对strVal的访问将使用相对便宜的“字符串常量”指令而不是字段查找。

**注意：此优化仅适用于原始类型和字符串常量，而不适用于任意引用类型。不过，尽可能地声明常量`static final`是个好习惯。**

## 4.使用增强型循环语法

增强的for循环（有时也称为“for-each”循环）可用于实现Iterable接口和数组的集合。 使用集合，分配一个迭代器来对hasNext（）和next（）进行接口调用。 使用ArrayList，手写的计数循环速度大约比for-each快3倍（有或没有JIT），但对于其他集合，增强型循环语法将完全等同于显式迭代器使用。

有几种方法来迭代数组：

``` java
static class Foo {
    int mSplat;
}

Foo[] mArray = ...

public void zero() {
    int sum = 0;
    for (int i = 0; i < mArray.length; ++i) {
        sum += mArray[i].mSplat;
    }
}

public void one() {
    int sum = 0;
    Foo[] localArray = mArray;
    int len = localArray.length;

    for (int i = 0; i < len; ++i) {
        sum += localArray[i].mSplat;
    }
}

public void two() {
    int sum = 0;
    for (Foo a : mArray) {
        sum += a.mSplat;
    }
}

```

- zero（）是最慢的，因为JIT不能优化通过循环的每次迭代获得数组长度一次的成本。

- one（）更快。 它将所有内容都拉到局部变量中，避免查找。 只有阵列长度提供了性能优势。

- 对于没有JIT的设备，two（）是最快的，并且对于具有JIT的设备与one（）不可区分。 它使用Java编程语言`1.5`版中引入的增强型for循环语法。

**所以，您应该默认使用增强型for循环，但考虑一个手写的计数循环，用于性能关键的ArrayList迭代(因为ArrayList的手写的计数循环比for-each快)。**

## 5. Consider Package Instead of Private Access with Private Inner Classes

考虑以下类定义：
``` java
public class Foo {
    private class Inner {
        void stuff() {
            Foo.this.doStuff(Foo.this.mValue);
        }
    }

    private int mValue;

    public void run() {
        Inner in = new Inner();
        mValue = 27;
        in.stuff();
    }

    private void doStuff(int value) {
        System.out.println("Value is " + value);
    }
}
```
这里重要的是我们定义一个私有内部类（Foo $ Inner），它直接访问外部类中的私有方法和私有实例字段。 这是合法的，代码按预期打印“Value is 27”。

问题是VM考虑从Foo $ Inner直接访问Foo的私有成员是非法的，因为Foo和Foo $ Inner是不同的类，尽管Java语言允许内部类访问外部类的私有成员。 为了弥合差距，编译器生成一些合成方法：

``` java
/*package*/ static int Foo.access$100(Foo foo) {
    return foo.mValue;
}
/*package*/ static void Foo.access$200(Foo foo, int value) {
    foo.doStuff(value);
}
```

内部类代码只要需要访问mValue字段或调用外部类中的doStuff（）方法，就会调用这些静态方法。 这意味着上面的代码真的归结为通过访问器方法访问成员字段的情况。 早些时候我们讨论了访问者比直接访问访问速度慢，所以这是某种语言习语的一个例子，导致“看不见”的性能命中。

如果您在性能热点中使用这样的代码，则可以通过声明内部类访问的字段和方法来获取包访问权限而不是私有访问来避免开销。 不幸的是，这意味着可以在同一个包中的其他类直接访问这些字段，因此您不应该在公共API中使用。

## 6.避免使用float

作为经验法则，在Android设备上，浮点大约比整数慢2倍。

在速度方面，float和double在现代化的硬件上没有什么区别。 在空间上，double是两倍。 与台式机一样，假设空间不是问题，您应该更喜欢double。

此外，即使是整数，一些处理器也有硬件倍增，但缺少硬件分割。 在这种情况下，在软件中执行整数除法和模数运算，如果您正在设计哈希表或进行大量数学运算，则需要考虑一下。

## 7. Know and Use the Libraries  多看看官方文档

除了所有通常的理由，多看看Libraries，请记住，系统可以自由地用手工编码汇编程序替换对Libraries方法的调用，这可能比JIT可以生成的最佳代码更好 等效的Java。 这里的典型例子是String.indexOf（）和相关的API，Dalvik用内联的内在函数代替。 类似地，System.arraycopy（）方法比使用JIT的Nexus One上的手编循环快约9倍。

**很多时候Android提供的方法比Java的方法的效率更高在Android设备上。**

## 7. Use Native Methods Carefully  小心

使用Android NDK开发本地代码的应用程序不一定比使用Java语言编程更有效率。首先，有一个与Java-native 过渡相关联的成本，并且JIT不能在这些边界之间进行优化。如果您正在分配本机资源（本地堆，文件描述符或其他内容），那么安排及时收集这些资源可能会更加困难。您还需要编译您希望运行的每个架构的代码（而不是依赖它具有JIT）。您甚至可能需要为您认为相同架构编译多个版本：为G1中的ARM处理器编译的本机代码无法充分利用Nexus One中的ARM，并为Nexus One中的ARM编译代码不会在G1上运行ARM。

当您拥有要移植到Android的现有Native code 库时，Native code 非常有用，而不是“加速”使用Java语言编写的Android应用程序部分。

如果您确实需要使用本机代码，您应该阅读我们的[JNI技巧](https://developer.android.google.cn/guide/practices/jni.html)。

## 8. Performance Myths

在没有JIT的设备上，通过具有确切类型而不是接口的变量来调用方法的效率是稍微高一些的。 （所以，例如，调用HashMap map 上的方法比Map map代价更小，即使在这两种情况下，map都是HashMap。）情况并不是这么慢， 实际差距更像是慢6％。 此外，JIT使两者有效地无法区分。

在没有JIT的设备上，缓存字段访问比重复访问该字段快20％。 使用JIT，现场访问费用与本地访问相同，所以这不是一个值得的优化，除非您觉得它使您的代码更容易阅读。 （这也是final，static和static final字段也是如此。）

## 9. Always Measure

在开始优化之前，请确保您遇到需要解决的问题。 确保您可以准确衡量您现有的表现，否则您将无法衡量您尝试的替代品的好处。

您可能还会发现Traceview对于分析有用，但重要的是要意识到它目前禁用JIT，这可能会导致它错误地给JIT可能获胜的代码。 在进行Traceview数据建议的更改后，特别重要的是确保生成的代码在没有Traceview的情况下运行得更快。

有关更多帮助分析和调试应用程序，请参阅以下文档：

- [使用Traceview和dmtracedump进行分析](https://developer.android.google.cn/tools/debugging/debugging-tracing.html)
- [使用Systrace分析UI性能](https://developer.android.google.cn/tools/debugging/systrace.html)
