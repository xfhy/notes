# Kotlin与Java互操作

## 已映射类型

> Kotlin 特殊处理一部分 Java 类型。这样的类型不是“按原样”从 Java 加载，而是 映射 到相应的 Kotlin 类型。 映射只发生在编译期间，运行时表示保持不变。 Java 的原生类型映射到相应的 Kotlin 类型（请记住平台类型）：

![](http://olg7c0d2n.bkt.clouddn.com/18-1-24/57383025.jpg)

一些非原生的内置类型也会作映射：
![](http://olg7c0d2n.bkt.clouddn.com/18-1-24/89378811.jpg)

Java 的装箱原始类型映射到可空的 Kotlin 类型：
![](http://olg7c0d2n.bkt.clouddn.com/18-1-24/98671289.jpg)
