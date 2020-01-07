> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/wvTNOJ-mlTPDTzBRKLR8ZA

自从 Android Studio 升级到 2.3 版本以后，使用 CMake 进行编译就方便多了，不需要再写 Android.mk 了，也不需要用 javah 来生成头文件了，直接写好 native 方法，快捷方式就可以生成对应的 C++ 方法，只要专注写好 C++ 代码，CMake 就可以指定的 CPU 架构生成对应的 SO 库。

JNI 和 NDK 的区别
-------------

NDK 开发难免会搞不清 JNI 和 NDK 的区别。

JNI 全称是 Java Native Interface，即 Java 本地接口。它是用来使得 Java 语言和 C/C++ 语言相互调用的。它本身和 Android 并无关系，只是在 Android 开发中会用到，在其他地方也会用到的。

而 NDK 的全称是 Native Development Kit，和 SDK 的全称是 Software Development Kit 一样，都是开发工具包。NDK 是 Android 开发的工具包，主要用作 C/C++ 开发，提供了相关的动态库。

在 Android 上进行 NDK 开发还是得先学会 JNI 相关技能，先可以从 Java 层到 C/C++ 层的相互调用，然后再学习 NDK 开发的那些技巧。

简单实例
----

在 AS 新建工程时若选择了 Include C++ Support，就会自带配置好的 C++ 开发环境。

在声明 native 方法时还是用 Java 来写比较好，比 Kotlin 的 external 关键字要友好多了，可以直接快捷键生成对用的 C++ 方法。

声明 native 方法如下：

```
1public static native int plus(int a, int b);


```

快捷键便会生成对应的 C++ 方法

```
1extern "C"
2JNIEXPORT jint JNICALL
3Java_com_glumes_myapplication_NativeClass_plus(JNIEnv *env, jobject instance, jint a, jint b) {
4    jint sum = a + b;
5    return sum;
6}


```

这是一个简单的计算 a+b 的 native 方法，但却包含了许多基本内容，在 C++ 层接收来自 Java 层的参数，并转换成 C++ 层的数据类型，计算之后再返回成 Java 层的数据类型。

在 Java 层中只有两个参数，而在 C++ 代码就有四个参数了，至少都会包含前面两个参数，下面讲解这些参数意义。

其中：

*   `env`变量是 JNIEnv 类型的对象，该对象是一个 Java 虚拟机所运行的环境，通过它可以访问到 Java 虚拟机内部的各种对象。
    

##### JNIEnv 类型对象参数 env

`JNIEnv*` 是定义任意 native 函数的第一个参数，它是一个指针，通过它可以访问虚拟机内部的各种数据结构，同时它还指向 JVM 函数表的指针，函数表中的每一个入口指向一个 JNI 函数，每个函数用于访问 JVM 中特定的数据结构。

结构如下图所示：

![](https://mmbiz.qpic.cn/mmbiz_png/e1icyHPvia5MaIwiaEZiaycf6VlyUlAGicoEMnITv6LH4elIqCAJWVJreAGEDia79bTb8BXWTl8yibR7JhCFib5pdSP7gg/640?wx_fmt=png)

可以看到这里面涉及了三类指针，JNIEnv * 本身就是指针，而它指向的也是指针，在 JVM 函数表里面的每一项又都是指针。

##### jobject 参数

jobject 是 native 函数里的第二个参数类型，但却不是一定的。

如果该 native 方法是一个静态 static 方法，那么第二个参数就是 jobject 类型，指的是调用该函数的对象；

如果是一个实例方法，那么第二个参数就是 jclass 类型，指的是调用该函数的类。

##### 基本数据类型转换

在 Java 中传递的参数类型是 int，而在 JNI 中就成了 jint，这就涉及到 Java 到 JNI 的数据类型转换。

如下表所示：

| Java 类型 | Native 类型 | 符号属性 | 字长 |
| --- | --- | --- | --- |
| boolean | jboolean | 无符号 | 8 位 |
| byte | jbyte | 无符号 | 8 位 |
| char | jchar | 无符号 | 16 位 |
| short | jshort | 有符号 | 16 位 |
| int | jnit | 有符号 | 32 位 |
| long | jlong | 有符号 | 64 位 |
| float | jfloat | 有符号 | 32 位 |
| double | jdouble | 有符号 | 64 位 |

我们传递的基本数据类型在 JNI 中都有相对的数据类型。

##### 引用数据类型转换

除了基本数据类型之外，引用数据类型也有着一一对应。

| Java 引用类型 | Native 类型 | Java 引用类型 | Native 类型 |
| --- | --- | --- | --- |
| All objects | jobject | char[] | jcharArray |
| java.lang.Class | jclass | short[] | jshortArray |
| java.lang.String | jstring | int[] | jintArray |
| Object[] | jobjectArray | long[] | jlongArray |
| boolean[] | jbooleanArray | float[] | jfloatArray |
| byte[] | jbyteArray | double[] | jdoubleArray |
| java.lang.Throwable | jthrowable |   
 |   
 |

可以看到，除了 Java 中基本数据类型的数组、Class、String 和 Throwable 外，其余所有 Java 对象的数据类型在 JNI 中都用 jobject 表示。

明白了参数类型之后，接下来就是按照正常写代码一样，完成函数的返回值了。

String 字符串操作
------------

对于基本数据类型的操作，比如 boolean、int、float 等都大同小异，无非是在原来的数据类型前面加了一个 `j`来表示 JNI 数据类型。

而对于 String 类型，必须要使用合适的 JNI 函数来将 jstring 转变成 C/C++ 字符串。

对于下面的 Native 方法，传入一个字符串，并要求返回一个字符串。

```
1    public static native String getNativeString(String str);


```

生成的对应的 C++ 代码如下：

```
 1extern "C"
 2JNIEXPORT jstring JNICALL
 3Java_com_glumes_cppso_SampleNativeMethod_getNativeString(JNIEnv *env, jclass type, jstring str_) {
 4
 5    // 生成 jstring 类型的字符串
 6    jstring returnValue = env->NewStringUTF("hello native string");
 7    // 将 jstring 类型的字符串转换为 C 风格的字符串，会额外申请内存
 8    const char *str = env->GetStringUTFChars(str_, 0);
 9    // 释放掉申请的 C 风格字符串的内存
10    env->ReleaseStringUTFChars(str_, str);
11    // 返回 jstring 类型字符串
12    return returnValue;
13}


```

Java 层的字符串到了 JNI 就成了 jstring 类型的，但 jstring 指向的是 JVM 内部的一个字符串，它不是 C 风格的字符串 `char*`，所以不能像使用 C 风格字符串一样来使用 jstring 。

JNI 支持将 jstring 转换成 UTF 编码和 Unicode 编码两种。因为 Java 默认使用 Unicode 编码，而 C/C++ 默认使用 UTF 编码。

*   GetStringUTFChars(jstring string, jboolean* isCopy)
    

将 jstring 转换成 UTF 编码的字符串

*   GetStringChars(jstring string, jboolean* isCopy)
    

将 jstring 转换成 Unicode 编码的字符串，由于 Native 层是 C/C++ 编码，默认使用 UTF 格式，所以 GetStringChars 并不常用。

其中，jstring 类型参数就是我们需要转换的字符串，而 isCopy 参数的值为  `JNI_TRUE` 或者 `JNI_FALSE` ，代表是否返回 JVM 源字符串的一份拷贝。如果为`JNI_TRUE` 则返回拷贝，并且要为产生的字符串拷贝分配内存空间；如果为`JNI_FALSE` 就直接返回了 JVM 源字符串的指针，意味着可以通过指针修改源字符串的内容，但这就违反了 Java 中字符串不能修改的规定，在实际开发中，直接填 NULL 就好了。

当调用完 GetStringUTFChars 方法时别忘了做完全检查。因为 JVM 需要为产生的新字符串分配内存空间，如果分配失败就会返回 NULL，并且会抛出 OutOfMemoryError 异常，所以要对 GetStringUTFChars 结果进行判断。

当使用完 UTF 编码的字符串时，还不能忘了释放所申请的内存空间。调用 ReleaseStringUTFChars 方法进行释放。

完整地转换字符串的代码如下：

```
 1    // 申请分配内存空间，jstring 转换为 C 风格字符串
 2    const char *utfStr = env->GetStringUTFChars(str_,NULL);
 3    // 做检查判断
 4    if (utfStr == NULL){
 5        return NULL;
 6    }
 7    // 实际操作
 8    printf("%s",utfStr);
 9    // 操作结束后，释放内存
10    env->ReleaseStringUTFChars(str_,utfStr);


```

除了将 jstring 转换为 C 风格字符串，JNI 还提供了将 C 风格字符串转换为 jstring 类型。

通过 NewStringUTF 函数可以将 UTF 编码的 C 风格字符串转换为 jstring 类型，通过 NewString 函数可以将 Unicode 编码的 C 风格字符串转换为 jstring 类型。这个 jstring 类型会自动转换成 Java 支持的 Unicode 编码格式。

除了 jstring 和 C 风格字符串的相互转换之外，JNI 还提供了其他的函数。

#### 获得源字符串的指针

在某些情况下，我们只需要获得 Java 字符串的直接指针，而不需要把它转换成 C 风格的字符串。

比如，一个字符串内容很大，有 1 M 多，而我们只是需要读取字符串内容，这种情况下再把它转换为 C 风格字符串，不仅多此一举（通过直接字符串指针也可以读取内容），而且还需要为 C 风格字符串分配内存。

为此，JNI 提供了 GetStringCritical 和 ReleaseStringCritical 函数来返回字符串的直接指针，这样只需要分配一个指针的内存空间就好了。

```
1    const jchar *c_str = NULL;
2    c_str = env->GetStringCritical(str_, NULL);
3
4    if (c_str == NULL) {
5        // error handle
6    }
7    env->ReleaseStringCritical(str_, c_str);


```

和 GetStringUTFChars 一样，在使用完之后，还需要将分配的指针内存空间给释放掉。

注意它的返回值指针类型是 `const jchar *`，而 GetStringUTFChars 函数的返回值就是 `const char*`，这就说明 GetStringUTFChars 返回的是 C 风格字符串的指针，而 GetStringCritical 返回的是源 Java 字符串的直接指针。

另外，GetStringCritical 还有额外的限制。

在 GetStringCritical 和 ReleaseStringCritical 两个函数之间的 Native 代码不能调用任何会让线程阻塞或者等待 JVM 中其他线程的 Native 函数或 JNI 函数。

因为通过 GetStringCritical 得到的是一个指向 JVM 内部字符串的直接指针，获取这个直接指针后会导致暂停 GC 线程，当 GC 线程被暂停后，如果其他线程触发 GC 继续运行的话，都会导致阻塞调用者。所以，GetStringCritical 和 ReleaseStringCritical 这对函数中间的任何本地代码都不可以执行导致阻塞的调用或为新对象在 JVM 中分配内存，否则，JVM 有可能死锁。

另外还是需要检查是否因为内存溢出而导致返回值为 NULL，因为 JVM 在执行 GetStringCritical 函数时，仍有发生数据复制的可能性，尤其是当 JVM 内部存储的数组不连续时，为了返回一个指向连续内存空间的指针，JVM 必须复制所有数据。

#### 获得字符串的长度：

由于 UTF-8 编码的字符串以 `\0` 结尾，而 Unicode 字符串不是，所以对于两种编码获得字符串长度的函数也是不同的。

*   GetStringLength
    

获得 Unicode 编码的字符串的长度。

*   GetStringUTFLength
    

获得 UTF-8 编码的字符串的长度，或者使用 C 语言的 strlen 函数。

这里的字符串指的是 Java 层的字符串，传入的参数都是 jsting 类型，而 Java 层默认是 Unicode 编码，所以大多使用 GetStringLength 方法。

#### 获得指定范围的字符串内容

JNI 提供了函数来获得字符串指定范围的内容，这里的字符串指的是 Java 层的字符串。函数会把源字符串复制到一个预先分配的缓冲区内。

*   GetStringRegion
    

获得 Unicode 编码的字符串指定内容。

*   GetStringUTFRegion
    

获得 UTF-8 编码的字符串指定内容。

```
1    jchar outbuf[128],inbuf[128];
2    int len = env->GetStringLength(str_);
3    env->GetStringRegion(str_,0,len,outbuf);
4    LOGD("%s",outbuf);


```

String 字符串函数操作总结
----------------

#### 关于字符串的函数汇总

| JNI 函数 | 描述 |
| --- | --- |
| GetStringChars / ReleaseStringChars | 获得或释放一个指向 Unicode 编码的字符串的指针（指 C/C++ 字符串） |
| GetStringUTFChars / ReleaseStringUTFChars | 获得或释放一个指向 UTF-8 编码的字符串的指针（指 C/C++ 字符串） |
| GetStringLength | 返回 Unicode 编码的字符串的长度 |
| getStringUTFLength | 返回 UTF-8 编码的字符串的长度 |
| NewString | 将 Unicode 编码的 C/C++ 字符串转换为 Java 字符串 |
| NewStringUTF | 将 UTF-8 编码的 C/C++ 字符串转换为 Java 字符串 |
| GetStringCritical / ReleaseStringCritical | 获得或释放一个指向字符串内容的指针 (指 Java 字符串) |
| GetStringRegion | 获取或者设置 Unicode 编码的字符串的指定范围的内容 |
| GetStringUTFRegion | 获取或者设置 UTF-8 编码的字符串的指定范围的内容 |

#### 选择合适的 JNI 函数

![](https://mmbiz.qpic.cn/mmbiz_png/e1icyHPvia5MaIwiaEZiaycf6VlyUlAGicoEMAgnn0wsew49Zwb6Pibw6VQW58r8wrFGGoxnEMKlHcsJJc1EySd3wjaQ/640?wx_fmt=png)

对于 JNI String 操作，要选择合适的函数，上表可以作为参考。

具体详情代码可以参考我的 Github 地址：  
https://github.com/glumes/AndroidDevWithCpp

参考
--

1.  《The Java Native Interface》
    

欢迎关注微信公众号：【纸上浅谈】，获得最新文章推送~~

![](https://mmbiz.qpic.cn/mmbiz_gif/e1icyHPvia5MYiaGGA60BvD5E58fwkebspU9dXK2nHm9KxzCj4lcHZe5U8RojDquCbbPXJjgNkSPdmL4q8gL9mPFw/640?wx_fmt=gif)