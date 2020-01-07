> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/87ce6f565d37

232018.05.09 17:15:33 字数 5481 阅读 62244

本系列文章如下：

> *   [Android JNI(一)——NDK 与 JNI 基础](https://www.jianshu.com/p/87ce6f565d37)
> *   [Android JNI 学习 (二)——实战 JNI 之 “hello world”](https://www.jianshu.com/p/b4431ac22ec2)
> *   [Android JNI 学习 (三)——Java 与 Native 相互调用](https://www.jianshu.com/p/b71aeb4ed13d)
> *   [Android JNI 学习 (四)——JNI 的常用方法的中文 API](https://www.jianshu.com/p/67081d9b0a9c)
> *   [Android JNI 学习 (五)——Demo 演示](https://www.jianshu.com/p/0f34c097028a)

本片文章大纲如下：

> *   1、导读
> *   2、什么是 NDK
> *   3、为什么使用 NDK
> *   4、NDK 到 SO
> *   5、JNI

![](http://upload-images.jianshu.io/upload_images/5713484-638d00dd948b1770.png)

大纲. png

一、导读
----

> 在 Android OS 上开发应用程序，Google 提供了两种开发包：SDK 和 NDK。你可以从 Google 官方查阅到有许多关于 SDK 的优秀书籍、文章作为参考，但是 Google 提供的 NDK 资源，相对于 SDK 还是比较少的。本系列文章主要是用于，自己记录自学 NDK 的经验，并且希望能够帮助到哪些想学习 NDK 的朋友。

Android 平台从一开就已经支持了 C/C++ 了。我们知道 Android 的 SDK 主要是基于 Java 的，所以导致了在用 Android SDK 进行开发的工程师们都必须使用 Java 语言。不过，Google 从一开始就说明 Android 也支持 JNI 编程方式，也就是第三方应用完成可以通过 JNI 调用自己的 C 动态度。于是 NDK 就应运而生了。

二、什么是 NDK
---------

##### NDK 其中 NDK 的全拼是：Native Develop Kit。

那我们先来看下 [Android NDK 官网](https://link.jianshu.com/?t=https%3A%2F%2Fdeveloper.android.google.cn%2Fndk%2Findex.html)是对 NDK 怎么解释的  

![](http://upload-images.jianshu.io/upload_images/5713484-874ed012a3e19062.png)

NDK 官网. png

关键文字如下：

> Android NDK 是一套允许您使用原生代码语言 (例如 C 和 C++) 实现部分应用的工具集。在开发某些类型应用时，这有助于您重复使用以这些语言编写的代码库。

简单的来说:

> Android NDK 就是一套工具集合，允许你使用 C/C++ 语言来实现应用程序的部分功能。

NDK 是 Native Develop Kit 的含义，从含义很容易理解，本地开发。大家都知道，Android 开发语言是 Java，不过我们也知道，Android 是基于 Linux 的，其核心库很多都是 C/C++ 的，比如 Webkit 等。那么 NDK 的作用，就是 Google 为了提供给开发者一个在 Java 中调用 C/C++ 代码的一个工作。NDK 本身其实就是一个交叉工作链，包含了 Android 上的一些库文件，然后，NDK 为了方便使用，提供了一些脚本，使得更容易的编译 C/C++ 代码。总之，在 Android 的 SDK 之外，有一个工具就是 NDK，用于进行 C/C++ 的开发。一般情况，是用 NDK 工具把 C/C++ 编译为. co 文件，然后在 Java 中调用。

NDK 不适用于大多数初学的 Android 工程师，对于许多类型的 Android 应用没有什么价值。因为它不可避免地会增加开发过程的复杂性，所以一般很少使用。那为什么 Google 还提供 NDK，我们就一起研究下

三、为什么使用 NDK
-----------

上面提及了 NDK 不适合大多数初级 Android 工程师，由于它增加了开发的复杂度，所以对许多类型的 Android 其实也没有大的作用。不过在下面的需求下，NDK 还是有很大的价值的：

> *   1、在平台之间移植其应用
> *   2、重复使用现在库，或者提供其自己的库重复使用
> *   3、在某些情况下提性能，特别是像游戏这种计算密集型应用
> *   4、使用第三方库，现在许多第三方库都是由 C/C++ 库编写的，比如 Ffmpeg 这样库。
> *   5、不依赖于 Dalvik Java 虚拟机的设计
> *   6、代码的保护。由于 APK 的 Java 层代码很容易被反编译，而 C/C++ 库反编译难度大。

四、NDK 到 so
----------

![](http://upload-images.jianshu.io/upload_images/5713484-36140dc3f1a04ca2.png)

ndk 到 so.png

从上图这个 Android 系统框架来看，我们上层通过 JNI 来调用 NDK 层的，使用这个工具可以很方便的编写和调试 JNI 的代码。因为 C 语言的不跨平台，在 Mac 系统的下使用 NDK 编译在 Linux 下能执行的函数库——so 文件。其本质就是一堆 C、C++ 的头文件和实现文件打包成一个库。目前 Android 系统支持以下七种不用的 CPU 架构，每一种对应着各自的应用程序二进制接口 ABI：(Application Binary Interface)定义了二进制文件 (尤其是. so 文件) 如何运行在相应的系统平台上，从使用的指令集，内存对齐到可用的系统函数库。对应关系如下：

> *   ARMv5——armeabi
> *   ARMv7 ——armeabi-v7a
> *   ARMv8——arm64- v8a
> *   x86——x86
> *   MIPS ——mips
> *   MIPS64——mips64
> *   x86_64——x86_64

五、JNI
-----

#### (一) 什么是 JNI?

[oracle 中关于 JNI 的指导](https://link.jianshu.com/?t=https%3A%2F%2Fdocs.oracle.com%2Fjavase%2F6%2Fdocs%2Ftechnotes%2Fguides%2Fjni%2F)

> Java 调用 C/C++ 在 Java 语言里面本来就有的，并非 Android 自创的，即 JNI。JNI 就是 Java 调用 C++ 的规范。当然，一般的 Java 程序使用的 JNI 标准可能和 android 不一样，Android 的 JNI 更简单。

JNI，全称为 Java Native Interface，即 Java 本地接口，JNI 是 Java 调用 Native 语言的一种特性。通过 JNI 可以使得 Java 与 C/C++ 机型交互。即可以在 Java 代码中调用 C/C++ 等语言的代码或者在 C/C++ 代码中调用 Java 代码。由于 JNI 是 JVM 规范的一部分，因此可以将我们写的 JNI 的程序在任何实现了 JNI 规范的 Java 虚拟机中运行。同时，这个特性使我们可以复用以前用 C/C++ 写的大量代码 JNI 是一种在 Java 虚拟机机制下的执行代码的标准机制。代码被编写成汇编程序或者 C/C++ 程序，并组装为动态库。也就允许非静态绑定用法。这提供了一个在 Java 平台上调用 C/C++ 的一种途径，反之亦然。

PS：  
开发 JNI 程序会受到系统环境限制，因为用 C/C++ 语言写出来的代码或模块，编译过程当中要依赖当前操作系统环境所提供的一些库函数，并和本地库链接在一起。而且编译后生成的二进制代码只能在本地操作系统环境下运行，因为不同的操作系统环境，有自己的本地库和 CPU 指令集，而且各个平台对标准 C/C++ 的规范和标准库函数实现方式也有所区别。这就造成了各个平台使用 JNI 接口的 Java 程序，不再像以前那样自由的跨平台。如果要实现跨平台， 就必须将本地代码在不同的操作系统平台下编译出相应的动态库。

#### (二) 为什么需要 JNI

因为在实际需求中，需要 Java 代码与 C/C++ 代码进行交互，通过 JNI 可以实现 Java 代码与 C/C++ 代码的交互

#### (三) JNI 的优势

与其它类似接口 Microsoft 的原始本地接口等相比，JNI 的主要竞争优势在于：它在设计之初就确保了二进制的兼容性，JNI 编写的应用程序兼容性以及其再某些具体平台上的 Java 虚拟机兼容性 (当谈及 JNI 时，这里并不特比针对 Davik 虚拟机，JNI 适用于所有 JVM 虚拟机)。这就是为什么 C/C++ 编译后的代码无论在任何平台上都能执行。不过，一些早期版本并不支持二进制兼容。二进制兼容性是一种程序兼容性类型，允许一个程序在不改变其可执行文件的条件下在不同的编译环境中工作。

#### (四) JNI 的三个角色

![](http://upload-images.jianshu.io/upload_images/5713484-62edbafafd3f3b2d.png)

JNI 的三个角色. png

JNI 下一共涉及到三个角色：C/C++ 代码、本地方法接口类、Java 层中具体业务类。

JNI 简要流程

![](http://upload-images.jianshu.io/upload_images/5713484-87fe79f0796a77a3.png)

简要流程. png

#### (五) JNI 的命名规则

随便举例如下：

```
JNIExport jstring JNICALL Java_com_example_hellojni_MainActivity_stringFromJNI( JNIEnv* env,jobject thiz ) 


```

**`jstring`** 是**返回值类型**  
**`Java_com_example_hellojni`** 是**包名**  
**`MainActivity`** 是**类名**  
**`stringFromJNI`** 是**方法名**

其中**`JNIExport`**和**`JNICALL`**是不固定保留的关键字不要修改

#### (六) 如何实现 JNI

JNI 开发流程的步骤：

> *   第 1 步：在 Java 中先声明一个 native 方法
> *   第 2 步：编译 Java 源文件 javac 得到. class 文件
> *   第 3 步：通过 javah -jni 命令导出 JNI 的. h 头文件
> *   第 4 步：使用 Java 需要交互的本地代码，实现在 Java 中声明的 Native 方法（如果 Java 需要与 C++ 交互，那么就用 C++ 实现 Java 的 Native 方法。）
> *   第 5 步：将本地代码编译成动态库 (Windows 系统下是. dll 文件，如果是 Linux 系统下是. so 文件，如果是 Mac 系统下是. jnilib)
> *   第 6 步：通过 Java 命令执行 Java 程序，最终实现 Java 调用本地代码。

PS：javah 是 JDK 自带的一个命令，-jni 参数表示将 class 中用到 native 声明的函数生成 JNI 规则的函数

如下图：

![](http://upload-images.jianshu.io/upload_images/5713484-160c7f1255923d43.png)

JNI 开发流程. png

#### (七) JNI 结构

![](http://upload-images.jianshu.io/upload_images/5713484-cbb0d96443494e7b.png)

JNI 结构. png

这张 JNI 函数表的组成就像 C++ 的虚函数表。虚拟机可以运行多张函数表，举例来说，一张调试函数表，另一张是调用函数表。JNI 接口指针仅在当前线程中起作用。这意味着指针不能从一个线程进入另一个线程。然而，可以在不同的咸亨中调用本地方法。

![](http://upload-images.jianshu.io/upload_images/5713484-8b84c6a37e1c8967.png)

JNI 接口. png

示例代码

```
jdouble Java_pkg_Cls_f__ILjava_lang_String_2 (JNIEnv *env, jobject obj, jint i, jstring s)
{
     const char *str = (*env)->GetStringUTFChars(env, s, 0); 
     (*env)->ReleaseStringUTFChars(env, s, str); 
     return 10;
}


```

里面的方法有三个入参，我们就依次来看下：

> *   *env：一个接口指针
> *   obj：在本地方法中声明的对象引用
> *   i 和 s：用于传递的参数

关于 obj、i 和 s 的类型大家可以参考下面的 JNI 数据类型，JNI 有自己的原始数据类型和数据引用类型如下：

![](http://upload-images.jianshu.io/upload_images/5713484-d1db8ac59efc0ce7.png)

JNI 的原始数据类型. png

关于 _env，会在下面_ _JNI 原理_ * 中讲解。

#### (八) JNI 原理

> 在计算机系统中，每一种编程语言都有一个执行环境 (Runtime)，执行环境用来解释执行语言中的语句，不同的编程语言的执行环境就好比神话世界中的 "阴阳两界" 一样，一般人不能同时生存在阴阳两界中，只有一些特殊的仙人——"黑白无常" 才能自由穿梭在 "阴阳两界"，而 "黑白无常" 往返于阴阳两界时手持生日薄，"黑白无常" 按生死薄上记录的任命来 "索魂"。

Java 语言的执行环境是 Java 虚拟机 (JVM)，JVM 其实是主机环境中的一个进程，每个 JVM 虚拟机都在本地环境中有一个 JavaVM 结构体，该结构体在创建 Java 虚拟机时被返回，在 JNI 环境中创建 JVM 的函数为 **JNI_CreateJavaVM。**

```
JNI_CreateJavaVM(JavaVM **pvm, void **penv, void*args);


```

##### 1、JavaVM

![](http://upload-images.jianshu.io/upload_images/5713484-652ef89a9509f85a.png)

JVM 与 JavaVM.png

其中 JavaVM 是 Java 虚拟机在 JNI 层的代表，JNI 全局仅仅有一个 JavaVM 结构中封装了一些函数指针（或叫函数表结构），JavaVM 中封装的这些函数指针主要是对 JVM 操作接口。另外，在 C 和 C++ 中的 JavaVM 的定义有所不同，在 C 中 JavaVM 是 JNIInvokeInterface_类型指针，而在 C++ 中有对 JNIInvokeInterface_进行了一次封装，比 C 中少了一个参数，这也是为什么 JNI 代码更推荐使用 C++ 来编写的原因。

下面我们来重点说一下 JNIEnv

##### 2、JNIEnv

> JNIEnv 是当前 Java 线程的执行环境，一个 JVM 对应一个 JavaVM 结构，而一个 JVM 中可能创建多个 Java 线程，每个线程对应一个 JNIEnv 结构，它们保存在线程本地存储 TLS 中。因此，不同的线程的 JNIEnv 是不同，也不能相互共享使用。JNIEnv 结构也是一个函数表，在本地代码中通过 JNIEnv 的函数表来操作 Java 数据或者调用 Java 方法。也就是说，只要在本地代码中拿到了 JNIEnv 结构，就可以在本地代码中调用 Java 代码。

![](http://upload-images.jianshu.io/upload_images/5713484-4383ea9f680216b9.png)

JVM 与 JNIEnv.png

##### 2.1JNIEnv 是什么？

> JNIEnv 是一个线程相关的结构体，该结构体代表了 Java 在本线程的执行环境

##### 2.2、JNIEnv 和 JavaVM 的区别：

> *   JavaVM：JavaVM 是 Java 虚拟机在 JNI 层的代表，JNI 全局仅仅有一个
> *   JNIEnv：JavaVM 在线程中的代码，每个线程都有一个，JNI 可能有非常多个 JNIEnv；

##### 2.3、JNIEnv 的作用：

> *   调用 Java 函数：JNIEnv 代表了 Java 执行环境，能够使用 JNIEnv 调用 Java 中的代码
> *   操作 Java 代码：Java 对象传入 JNI 层就是 jobject 对象，需要使用 JNIEnv 来操作这个 Java 对象

##### 2.4、JNIEnv 的创建与释放

##### 2.4.1、JNIEnv 的创建

JNIEnv 创建与释放：从 JavaVM 获得，这里面又分为 C 与 C++，我们就依次来看下：

> *   C 中——**JNIInvokeInterface**：JNIInvokeInterface 是 C 语言环境中的 JavaVM 结构体，调用 (_AttachCurrentThread)(JavaVM_, JNIEnv*_, void_) 方法，能够获得 JNIEnv 结构体
> *   C++ 中 ——**_JavaVM**：_JavaVM 是 C++ 中 JavaVM 结构体，调用 jint AttachCurrentThread(JNIEnv** p_env, void* thr_args) 方法，能够获取 JNIEnv 结构体；

##### 2.4.2、JNIEnv 的释放

> *   C 中释放：调用 JavaVM 结构体 JNIInvokeInterface 中的 (_DetachCurrentThread)(JavaVM_) 方法，能够释放本线程的 JNIEnv
> *   C++ 中释放：调用 JavaVM 结构体_JavaVM 中的 jint DetachCurrentThread(){ return functions->DetachCurrentThread(this); } 方法，就可以释放 本线程的 JNIEnv

##### 2.5、JNIEnv 与线程

JNIEnv 是线程相关的，即在每一个线程中都有一个 JNIEnv 指针，每个 JNIEnv 都是线程专有的，其他线程不能使用本线程中的 JNIEnv，即线程 A 不能调用线程 B 的 JNIEnv。所以 JNIEnv 不能跨线程。

> *   JNIEnv 只在当前线程有效：JNIEnv 仅仅在当前线程有效，JNIEnv 不能在线程之间进行传递，在同一个线程中，多次调用 JNI 层方便，传入的 JNIEnv 是同样的
> *   本地方法匹配多个 JNIEnv：在 Java 层定义的本地方法，能够在不同的线程调用，因此能够接受不同的 JNIEnv

##### 2.6、JNIEnv 结构

JNIEnv 是一个指针，指向一个线程相关的结构，线程相关结构，线程相关结构指向 JNI 函数指针数组，这个数组中存放了大量的 JNI 函数指针，这些指针指向了详细的 JNI 函数。

![](http://upload-images.jianshu.io/upload_images/5713484-48acac24bc7f78a1.png)

JNIEnv 结构. png

##### 2.7、与 JNIEnv 相关的常用函数

##### 2.7.1 创建 Java 中的对象

> *   jobject NewObject(JNIEnv *env, jclass clazz,jmethodID methodID, ...)：
> *   jobject NewObjectA(JNIEnv *env, jclass clazz,jmethodID methodID, const jvalue *args)：
> *   jobject NewObjectV(JNIEnv *env, jclass clazz,jmethodID methodID, va_list args)：

第一个参数 jclass class 代表的你要创建哪个类的对象，第二个参数, jmethodID methodID 代表你要使用那个构造方法 ID 来创建这个对象。只要有 jclass 和 jmethodID，我们就可以在本地方法创建这个 Java 类的对象。

##### 2.7.2 创建 Java 类中的 String 对象

> *   jstring NewString(JNIEnv *env, const jchar *unicodeChars,jsize len)：

通过 Unicode 字符的数组来创建一个新的 String 对象。  
env 是 JNI 接口指针；unicodeChars 是指向 Unicode 字符串的指针；len 是 Unicode 字符串的长度。返回值是 Java 字符串对象，如果无法构造该字符串，则为 null。

> 那有没有一个直接直接 new 一个 utf-8 的字符串的方法呢？答案是有的，就是`jstring NewStringUTF(JNIEnv *env, const char *bytes)`这个方法就是直接 new 一个编码为 utf-8 的字符串。

##### 2.7.3 创建类型为基本类型 PrimitiveType 的数组

> *   ArrayType New<PrimitiveType>Array(JNIEnv *env, jsize length);  
>     指定一个长度然后返回相应的 Java 基本类型的数组

| 方法 | 返回值 |
| --- | --- |
| New<PrimitiveType>Array Routines | Array Type |
| NewBooleanArray() | jbooleanArray |
| NewByteArray() | jbyteArray |
| NewCharArray() | jcharArray |
| NewShortArray() | jshortArray |
| NewIntArray() | jintArray |
| NewLongArray() | jlongArray |
| NewFloatArray() | jfloatArray |
| NewDoubleArray() | jdoubleArray |

用于构造一个新的数组对象，类型是原始类型。基本的原始类型如下：

| 方法 | 返回值 |
| --- | --- |
| New<PrimitiveType>Array Routines | Array Type |
| NewBooleanArray() | jbooleanArray |
| NewByteArray() | jbyteArray |
| NewCharArray() | jcharArray |
| NewShortArray() | jshortArray |
| NewIntArray() | jintArray |
| NewLongArray() | jlongArray |
| NewFloatArray() | jfloatArray |
| NewDoubleArray() | jdoubleArray |

##### 2.7.4 创建类型为 elementClass 的数组

> *   jobjectArray NewObjectArray(JNIEnv *env, jsize length,  
>     jclass elementClass, jobject initialElement);

造一个新的数据组，类型是 elementClass，所有类型都被初始化为 initialElement。

##### 2.7.5 获取数组中某个位置的元素

> jobject GetObjectArrayElement(JNIEnv *env,  
> jobjectArray array, jsize index);

返回 Object 数组的一个元素

##### 2.7.6 获取数组的长度

> jsize GetArrayLength(JNIEnv *env, jarray array);

获取 array 数组的长度.

关于 JNI 的常用方法，我们会在后面一期详细介绍。文档可以参考 [https://docs.oracle.com](https://link.jianshu.com/?t=https%3A%2F%2Fdocs.oracle.com%2Fjavase%2F1.5.0%2Fdocs%2Fguide%2Fjni%2Fspec%2FjniTOC.html)

#### (九) JNI 的引用

> Java 内存管理这块是完全透明的，new 一个实例时，只知道创建这个类的实例后，会返回这个实例的一个引用，然后拿着这个引用去访问它的成员 (属性、方法)，完全不用管 JVM 内部是怎么实现的，如何为新建的对象申请内存，使用完之后如何释放内存，只需要知道有个垃圾回收器在处理这些事情就行了，然而，从 Java 虚拟机创建的对象传到 C/C++ 代码就会产生引用，根据 Java 的垃圾回收机制，只要有引用存在就不会触发该该引用所指向 Java 对象的垃圾回收。

在 JNI 规范中定义了三种引用：局部引用（Local Reference）、全局引用（Global Reference）、弱全局引用（Weak Global Reference）。区别如下：

在 JNI 中也同样定义了类似与 Java 的应用类型，在 JNI 中，定义了三种引用类型：

> *   局部引用 (Local Reference)
> *   全局引用 (Global Reference)
> *   弱全局引用 (Weak Global Reference)

下面我们就依次来看下：

##### 1、局部引用 (Local Reference)

> 局部引用，也成本地引用，通常是在函数中创建并使用。会阻止 GC 回收所有引用对象。

最常见的引用类型，基本上通过 JNI 返回来的引用都是局部引用，例如使用 NewObject，就会返回创建出来的实例的局部引用，局部引用值在该 native 函数有效，所有在该函数中产生的局部引用，都会在函数返回的时候自动释放 (freed)，也可以使用 DeleteLocalRef 函数手动释放该应用。之所以使用 DeleteLocalRef 函数：实际上局部引用存在，就会防止其指向对象被垃圾回收期回收，尤其是当一个局部变量引用指向一个很庞大的对象，或是在一个循环中生成一个局部引用，最好的做法就是在使用完该对象后，或在该循环尾部把这个引用是释放掉，以确保在垃圾回收器被触发的时候被回收。在局部引用的有效期中，可以传递到别的本地函数中，要强调的是它的有效期仍然只是在第一次的 Java 本地函数调用中，所以千万不能用 C++ 全部变量保存它或是把它定义为 C++ 静态局部变量。

##### 2、全局引用 (Global Reference)

> 全局引用可以跨方法、跨线程使用，直到被开发者显式释放。类似局部引用，一个全局引用在被释放前保证引用对象不被 GC 回收。和局部应用不同的是，没有俺么多函数能够创建全局引用。能创建全部引用的函数只有 NewGlobalRef，而释放它需要使用 ReleaseGlobalRef 函数

##### 3、弱全局引用 (Weak Global Reference)

> 是 JDK 1.2 新增加的功能，与全局引用类似，创建跟删除都需要由编程人员来进行，这种引用与全局引用一样可以在多个本地带阿妈有效，不一样的是，弱引用将不会阻止垃圾回收期回收这个引用所指向的对象，所以在使用时需要多加小心，它所引用的对象可能是不存在的或者已经被回收。

通过使用 NewWeakGlobalRef、ReleaseWeakGlobalRef 来产生和解除引用。

##### 4、引用比较

在给定两个引用，不管是什么引用，我们只需要调用 IsSameObject 函数来判断他们是否是指向相同的对象。代码如下：

```
(*env)->IsSameObject(env, obj1, obj2)


```

如果 obj1 和 obj2 指向相同的对象，则返回 **JNI_TRUE(或者 1)**，否则返回 **JNI_FALSE(或者 0)**,

> PS：有一个特殊的引用需要注意：NULL，JNI 中的 NULL 引用指向 JVM 中的 null 对象，如果 obj 是一个全局或者局部引用，使用`(*env)->IsSameObject(env, obj, NULL)`或者`obj == NULL`用来判断 obj 是否指向一个 null 对象即可。但是需要注意的是，`IsSameObject`用于弱全局引用与 NULL 比较时，返回值的意义是不同于局部引用和全局引用的。代码如下：

```
jobject local_obj_ref = (*env)->NewObject(env, xxx_cls,xxx_mid);
jobject g_obj_ref = (*env)->NewWeakGlobalRef(env, local_ref);

jboolean isEqual = (*env)->IsSameObject(env, g_obj_ref, NULL);


```

自此，关于 NDK 与 JNI 基础已经讲解完毕，下一篇文章，让我们来了解一下 [Android JNI 学习 (二)——实战 JNI 之 “hello world”](https://www.jianshu.com/p/b4431ac22ec2)

[![](https://upload.jianshu.io/users/upload_avatars/5713484/0dbe60df-26f7-4174-a821-e1e7eea864c4.jpeg?imageMogr2/auto-orient/strip|imageView2/1/w/120/h/120/format/webp)](https://www.jianshu.com/u/8b9c629f69dd)

### 被以下专题收入，发现更多相似内容

### 推荐阅读[更多精彩内容](https://www.jianshu.com/)

*   根据官方应用了解 Android 9.0 的版本变更内容如下图： 以 API 级别 28+ 为目标的应用需要注意的行为...
    
    [![](https://upload-images.jianshu.io/upload_images/11094855-ba540f46f9d2cefc.png?imageMogr2/auto-orient/strip|imageView2/1/w/300/h/240/format/webp)](https://www.jianshu.com/p/d6261c2824ee)
*   本文已授权微信公众号：码个蛋 在微信公众号平台原创首发 前言 抽丝剥茧 RecyclerView 系列文章的目的在于帮...
    
    [![](https://upload-images.jianshu.io/upload_images/9271486-c6d7779c420d5c80.png?imageMogr2/auto-orient/strip|imageView2/1/w/300/h/240/format/webp)](https://www.jianshu.com/p/1ae2f2fcff2c)
*   转载请以链接形式标明出处：本文出自: 103style 的博客 本文操作以 Android Studio 3.4.2 ...
    
*   PlayerBase 框架 git 地址：https://github.com/jiajunhui/PlayerBas...
    
    [![](https://upload-images.jianshu.io/upload_images/2839783-1490e058adfdcba6.png?imageMogr2/auto-orient/strip|imageView2/1/w/300/h/240/format/webp)](https://www.jianshu.com/p/65fcb769f8da)
*   IPC（进程间通信）机制不是 Android 系统所独有的，其他系统也有相应的进程间通信机制。Android 系统架构中...
    
    [![](https://upload-images.jianshu.io/upload_images/2819106-a94732cfc58f24c1.png?imageMogr2/auto-orient/strip|imageView2/1/w/300/h/240/format/webp)](https://www.jianshu.com/p/b35e0716bce1)