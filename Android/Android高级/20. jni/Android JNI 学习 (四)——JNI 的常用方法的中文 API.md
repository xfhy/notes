> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/67081d9b0a9c

22018.05.09 18:19:08 字数 12226 阅读 9799

本系列文章如下：

> *   [Android JNI(一)——NDK 与 JNI 基础](https://www.jianshu.com/p/87ce6f565d37)
> *   [Android JNI 学习 (二)——实战 JNI 之 “hello world”](https://www.jianshu.com/p/b4431ac22ec2)
> *   [Android JNI 学习 (三)——Java 与 Native 相互调用](https://www.jianshu.com/p/b71aeb4ed13d)
> *   [Android JNI 学习 (四)——JNI 的常用方法的中文 API](https://www.jianshu.com/p/67081d9b0a9c)
> *   [Android JNI 学习 (五)——Demo 演示](https://www.jianshu.com/p/0f34c097028a)

思维导图如下：

![](http://upload-images.jianshu.io/upload_images/5713484-489382d33286e74e.png)

image.png

本文主要是结合 [JNI 的常用接口文档](https://link.jianshu.com/?t=https%3A%2F%2Fdocs.oracle.com%2Fjavase%2F7%2Fdocs%2Ftechnotes%2Fguides%2Fjni%2Fspec%2Ffunctions.html%23wp9502)进行的翻译主要是帮助我们更好的理解 JNI 中常用的 API。具体如下：

一、Interface Function Table(接口函数表)
---------------------------------

每个函数都可以通过`JNIEnv`参数访问，JNIEnv 类型是指向一个存放所有 JNI 接口指针的指针，其定义如下：

```
typedef const struct JNINativeInterface *JNIEnv;


```

> 虚拟机初始化函数表，如下面代码所示，前三个条目是为了将来和 COM 兼容而保留的。另外，我们在函数表的开头附近保留了一些额外的 NULL 条目，例如，可以在 FindClass 之后添加未来与类相关的 JNI 操作，而不是在表的末尾。请注意，函数表可以在所有 JNI 接口指针之间共享。

首先我们来看下`JNINativeInterface`

```
const struct JNINativeInterface ... = {

    NULL,
    NULL,
    NULL,
    NULL,
    GetVersion,

    DefineClass,
    FindClass,

    FromReflectedMethod,
    FromReflectedField,
    ToReflectedMethod,

    GetSuperclass,
    IsAssignableFrom,

    ToReflectedField,

    Throw,
    ThrowNew,
    ExceptionOccurred,
    ExceptionDescribe,
    ExceptionClear,
    FatalError,

    PushLocalFrame,
    PopLocalFrame,

    NewGlobalRef,
    DeleteGlobalRef,
    DeleteLocalRef,
    IsSameObject,
    NewLocalRef,
    EnsureLocalCapacity,

    AllocObject,
    NewObject,
    NewObjectV,
    NewObjectA,

    GetObjectClass,
    IsInstanceOf,

    GetMethodID,

    CallObjectMethod,
    CallObjectMethodV,
    CallObjectMethodA,
    CallBooleanMethod,
    CallBooleanMethodV,
    CallBooleanMethodA,
    CallByteMethod,
    CallByteMethodV,
    CallByteMethodA,
    CallCharMethod,
    CallCharMethodV,
    CallCharMethodA,
    CallShortMethod,
    CallShortMethodV,
    CallShortMethodA,
    CallIntMethod,
    CallIntMethodV,
    CallIntMethodA,
    CallLongMethod,
    CallLongMethodV,
    CallLongMethodA,
    CallFloatMethod,
    CallFloatMethodV,
    CallFloatMethodA,
    CallDoubleMethod,
    CallDoubleMethodV,
    CallDoubleMethodA,
    CallVoidMethod,
    CallVoidMethodV,
    CallVoidMethodA,

    CallNonvirtualObjectMethod,
    CallNonvirtualObjectMethodV,
    CallNonvirtualObjectMethodA,
    CallNonvirtualBooleanMethod,
    CallNonvirtualBooleanMethodV,
    CallNonvirtualBooleanMethodA,
    CallNonvirtualByteMethod,
    CallNonvirtualByteMethodV,
    CallNonvirtualByteMethodA,
    CallNonvirtualCharMethod,
    CallNonvirtualCharMethodV,
    CallNonvirtualCharMethodA,
    CallNonvirtualShortMethod,
    CallNonvirtualShortMethodV,
    CallNonvirtualShortMethodA,
    CallNonvirtualIntMethod,
    CallNonvirtualIntMethodV,
    CallNonvirtualIntMethodA,
    CallNonvirtualLongMethod,
    CallNonvirtualLongMethodV,
    CallNonvirtualLongMethodA,
    CallNonvirtualFloatMethod,
    CallNonvirtualFloatMethodV,
    CallNonvirtualFloatMethodA,
    CallNonvirtualDoubleMethod,
    CallNonvirtualDoubleMethodV,
    CallNonvirtualDoubleMethodA,
    CallNonvirtualVoidMethod,
    CallNonvirtualVoidMethodV,
    CallNonvirtualVoidMethodA,

    GetFieldID,

    GetObjectField,
    GetBooleanField,
    GetByteField,
    GetCharField,
    GetShortField,
    GetIntField,
    GetLongField,
    GetFloatField,
    GetDoubleField,
    SetObjectField,
    SetBooleanField,
    SetByteField,
    SetCharField,
    SetShortField,
    SetIntField,
    SetLongField,
    SetFloatField,
    SetDoubleField,

    GetStaticMethodID,

    CallStaticObjectMethod,
    CallStaticObjectMethodV,
    CallStaticObjectMethodA,
    CallStaticBooleanMethod,
    CallStaticBooleanMethodV,
    CallStaticBooleanMethodA,
    CallStaticByteMethod,
    CallStaticByteMethodV,
    CallStaticByteMethodA,
    CallStaticCharMethod,
    CallStaticCharMethodV,
    CallStaticCharMethodA,
    CallStaticShortMethod,
    CallStaticShortMethodV,
    CallStaticShortMethodA,
    CallStaticIntMethod,
    CallStaticIntMethodV,
    CallStaticIntMethodA,
    CallStaticLongMethod,
    CallStaticLongMethodV,
    CallStaticLongMethodA,
    CallStaticFloatMethod,
    CallStaticFloatMethodV,
    CallStaticFloatMethodA,
    CallStaticDoubleMethod,
    CallStaticDoubleMethodV,
    CallStaticDoubleMethodA,
    CallStaticVoidMethod,
    CallStaticVoidMethodV,
    CallStaticVoidMethodA,

    GetStaticFieldID,

    GetStaticObjectField,
    GetStaticBooleanField,
    GetStaticByteField,
    GetStaticCharField,
    GetStaticShortField,
    GetStaticIntField,
    GetStaticLongField,
    GetStaticFloatField,
    GetStaticDoubleField,

    SetStaticObjectField,
    SetStaticBooleanField,
    SetStaticByteField,
    SetStaticCharField,
    SetStaticShortField,
    SetStaticIntField,
    SetStaticLongField,
    SetStaticFloatField,
    SetStaticDoubleField,

    NewString,

    GetStringLength,
    GetStringChars,
    ReleaseStringChars,

    NewStringUTF,
    GetStringUTFLength,
    GetStringUTFChars,
    ReleaseStringUTFChars,

    GetArrayLength,

    NewObjectArray,
    GetObjectArrayElement,
    SetObjectArrayElement,

    NewBooleanArray,
    NewByteArray,
    NewCharArray,
    NewShortArray,
    NewIntArray,
    NewLongArray,
    NewFloatArray,
    NewDoubleArray,

    GetBooleanArrayElements,
    GetByteArrayElements,
    GetCharArrayElements,
    GetShortArrayElements,
    GetIntArrayElements,
    GetLongArrayElements,
    GetFloatArrayElements,
    GetDoubleArrayElements,

    ReleaseBooleanArrayElements,
    ReleaseByteArrayElements,
    ReleaseCharArrayElements,
    ReleaseShortArrayElements,
    ReleaseIntArrayElements,
    ReleaseLongArrayElements,
    ReleaseFloatArrayElements,
    ReleaseDoubleArrayElements,

    GetBooleanArrayRegion,
    GetByteArrayRegion,
    GetCharArrayRegion,
    GetShortArrayRegion,
    GetIntArrayRegion,
    GetLongArrayRegion,
    GetFloatArrayRegion,
    GetDoubleArrayRegion,
    SetBooleanArrayRegion,
    SetByteArrayRegion,
    SetCharArrayRegion,
    SetShortArrayRegion,
    SetIntArrayRegion,
    SetLongArrayRegion,
    SetFloatArrayRegion,
    SetDoubleArrayRegion,

    RegisterNatives,
    UnregisterNatives,

    MonitorEnter,
    MonitorExit,

    GetJavaVM,

    GetStringRegion,
    GetStringUTFRegion,

    GetPrimitiveArrayCritical,
    ReleasePrimitiveArrayCritical,

    GetStringCritical,
    ReleaseStringCritical,

    NewWeakGlobalRef,
    DeleteWeakGlobalRef,

    ExceptionCheck,

    NewDirectByteBuffer,
    GetDirectBufferAddress,
    GetDirectBufferCapacity,

    GetObjectRefType
  };


```

下面我们就详细介绍下

二、获取 JNI 版本信息
-------------

在 JNIEnv 指针中，有个函数用于获取 JNI 的版本：

```
jint GetVersion(JNIEnv *env);


```

该方法主要返回本地 JNI 方法接口的版本信息。在不同的 JDK 环境下返回值是不同的，具体如下：

> *   在 JDK/JRE 1.1 中，返回`0x00010001`
> *   在 JDK/JRE 1.2 中，返回`0x00010002`
> *   在 JDK/JRE 1.3 中，返回`0x00010004`
> *   在 JDK/JRE 1.4 中，返回`0x00010006`

上面这些数字可不是我乱拍的，其实是早就被定义为一个宏了，如下：

```
#define JNI_VERSION_1_1 0x00010001
#define JNI_VERSION_1_2 0x00010002


#define JNI_EDETACHED    (-2)              
#define JNI_EVERSION     (-3)              

```

三、Java 类 操作
-----------

#### (一)、定义类 (加载类)

```
jclass DefineClass(JNIEnv *env,const char* name,jobject loader,const jbyte *buf, jsize bufLen)


```

这个函数，主要是从包含数据的 buffer 中加载类，该 buffer 包含类调用时未被虚拟机所引用的原始类数据。

入参解释：

> *   env：JNI 接口指针
> *   name：所定义的类名或者接口名，该字符串有 modefied UTF-8 编码
> *   loader：指派给定义的类加载器
> *   buf：包含. class 文件数据的 buffer
> *   bufLen：buffer 长度

返回：Java 类对象，当错误出现时返回 NULL

可能抛出的异常：

> *   如果没有指定这个 Java 类的，则会抛出`ClassFormatError`
> *   如果是一个类 / 接口是它自己的一个父类 / 父接口，则会抛出`ClassCircularityError`
> *   如果内存不足，则会抛出`OutOfMemoryError`
> *   如果想尝试在 Java 包中定义一个类，则会抛出`SecurityException`

#### (二)、查找类

```
jclass FindClass(JNIEnv *env,const char *name);


```

这里面有两种情况一个是 JDK release1.1，另外一种是 JDK release 1.2  
。从 JDK release 1.1，该函数加载一个本地定义类，它搜索 CLASSPATH 环境变量里的目录及 zip 文件查找特定名字的类。自从 Java 2 release 1.2，Java 安全模型允许非系统类加载跟调用本地方法。FindClass 定义与当前本地方法关联的类加载，也就是声明本地方法的类的类加载类。如果本地方法属于系统类，则不会涉及类加载器；否则，将调用适当的类加载来加载和链接指定的类。从 Java 2 SDK1.2 版本开始，通过调用接口调用 FindClass 时，没有当前的本机方法或关联的的类加载器。在这种情况下，在这种情况下，使用 ClassLoader.getSystemClassLoader 的结果。这是虚拟机为应用程序创建的类加载器，并且能够找到 java.class.path 属性列出的类。

入参解释：

> *   env：JNI 接口指针
> *   name：一个完全限定的类名，即包含 “包名”+“/”+ 类名。举个例子：如`java.lang.String`，该参数为`java/lang/String`；如果类名以`[`开头，将返回一个数组类。比如数组类的签名为`java.lang.Object[]`，该参数应该为 "[Ljava/lang/Object"

返回：  
返回对应完全限定类对象，当找不到类时，返回 NULL

可能抛出的异常：

> *   如果没有指定这个 Java 类的，则会抛出`ClassFormatError`
> *   如果是一个类 / 接口是它自己的一个父类 / 父接口，则会抛出`ClassCircularityError`
> *   如果没有找到该类 / 接口的定义，则抛出`NoClassDefFoundError`
> *   如果内存不足，则会抛出`OutOfMemoryError`

#### (三)、查找父类

```
jclass GetSuperclass(JNIEnv *env,jclass clazz);


```

如果 clazz 不是 Object 类，则此函数将返回表示该 clazz 的父类的 Class 对象，如果该类是 Object，或者 clazz 代表接口，则此函数返回 NULL。

入参解释：

> *   env：JNI 接口指针
> *   clazz：Java 的 Class 类

返回：  
如果 clazz 有父类则返回其父类，如果没有其父类则返回 NULL

#### (四)、安全转换

```
jboolean IsAssignableFrom(JNIEnv *env,jclass clazz1,jclass clazz2);


```

判断 clazz1 的对象是否可以安全地转化为 clazz2 的对象

入参解释：

> *   env：JNI 接口指针
> *   clazz1：Java 的 Class 类，即需要被转化的类
> *   clazz2：Java 的 Class 类，即需要转化为目标的类

返回：  
如果满足以下任一条件，则返回 JNI_TRUE：

*   如果 clazz1 和 clazz2 是同一个 Java 类。
*   如果 clazz1 是 clazz2 的子类
*   如果 clazz1 是 clazz2 接口的实现类

四、异常 操作
-------

#### (一)、抛出异常

```
jint Throw(JNIEnv *env,jthrowable obj);


```

传入一个 jthrowable 对象，并且在 JNI 并将其抛起

入参解释：

> *   env：JNI 接口指针
> *   jthrowable：一个 Java 的 java.lang.Throwable 对象

返回：  
成功返回 0，失败返回一个负数。

可能抛出的异常：  
抛出一个 java.lang.Throwable 对象

#### (二)、构造一个新的异常并抛出

```
jint ThrowNew(JNIEnv *env,jclass clazz,const char* message);


```

传入一个 message，并用其构造一个异常并且抛出。

入参解释：

> *   env：JNI 接口指针
> *   jthrowable：一个 Java 的 java.lang.Throwable 对象
> *   message：用于构造一个 java.lang.Throwable 对象的消息，该字符串用 modified UTF-8 编码

返回：  
如果成功返回 0，失败返回一个负数

可能抛出的异常：  
抛出一个新构造的 java.lang.Throwable 对象

#### (三)、检查是否发生异常，并抛出异常

```
jthrowable ExceptionOccurred(JNIEnv *env);


```

检测是否发生了异常，如果发生了，则返回该异常的引用 (再调用 ExceptionClear() 函数前，或者 Java 处理异常前)，如果没有发生异常，则返回 NULL。

入参解释：

> *   env：JNI 接口指针

返回：  
jthrowable 的异常引用或者 NULL

#### (四)、打印异常的堆栈信息

```
void ExceptionDescribe(JNIEnv *env)


```

打印这个异常的堆栈信息

入参解释：

> *   env：JNI 接口指针

#### (五)、清除异常的堆栈信息

```
void ExceptionClear(JNIEnv *env);


```

清除正在抛出的异常，如果当前没有异常被抛出，这个函数不起作用

入参解释：

> *   env：JNI 接口指针

#### (六)、致命异常

```
void FatalError(JNIEnv *env,const char* msg);


```

致命异常，用于输出一个异常信息，并终止当前 VM 实例，即退出程序。

入参解释：

> *   env：JNI 接口指针
> *   msg：异常的错误信息，该字符串用 modified UTF-8 编码

#### (七)、仅仅检查是否发生异常

```
jboolean ExceptionCheck(JNIEnv *env);


```

检查是否已经发生了异常，如果已经发生了异常，则返回 JNI_TRUE，否则返回 JNI_FALSE

入参解释：

> *   env：JNI 接口指针

返回：  
如果已经发生异常，返回 JNI_TRUE，如果没有发生异常则返回 JNI_FALSE

五、全局引用和局部引用
-----------

#### (一)、创建全局引用

```
jobject NewGlobalRef(JNIEnv *env,object obj);


```

给对象 obj 创建一个全局引用，obj 可以是全局或局部引用。全局引用必须通过 DeleteGlobalRef() 显示处理。

参数解释：

> *   env：JNI 接口指针
> *   obj：object 对象

返回：  
全局引用 jobject，如果内存溢出则返回 NULL

#### (二)、删除全局引用

```
void DeleteGlobalRef(JNIEnv *env,jobject globalRef);


```

删除全局引用

参数解释：

> *   env：JNI 接口指针
> *   globalRef：需要被删除的全局引用

#### (三)、删除局部引用

局部引用只在本地接口调用时的生命周期内有效，当本地方法返回时，它们会被自动释放。每个局部引用都会消耗一定的虚拟机资源，虽然局部引用可以被自动销毁，但是程序员也需要注意不要在本地方法中过度分配局部引用，过度分配局部引用会导致虚拟机在执行本地方法时内存溢出。

```
void DeleteLocalRef(JNIEnv *env, jobject localRef); 


```

通过 localRef 删除局部引用

参数解释

> *   env：JNI 接口指针
> *   localRef：需要被删除的局部引用

JDK/JRE 1.1 提供了上面的 DeleteLocalRef 函数，这样程序员就可以手动删除本地引用。  
从 JDK/JRE 1.2 开始，提供可一组生命周期管理的函数，他们是下面四个函数。

#### (四)、设定局部变量的容量

```
jint EnsureLocalCapacity(JNIEnv *env,jint capacity);


```

在当前线程中，通过传入一个容量 capacity，，限制局部引用创建的数量。成功则返回 0，否则返回一个负数，并抛出一个 OutOfMemoryError。VM 会自动确保至少可以创建 16 个局部引用。

参数解释

> *   env：JNI 接口指针
> *   capacity：容量

返回：  
成功返回 0，失败返回一个负数，并会抛出一个 OutOfMemoryError

为了向后兼容，如果虚拟机创建了超出容量的局部引用。VM 调用 FatalError，来保证不能创建更多的本地引用。(如果是 debug 模式，虚拟机回想用户发出 warning，并提示创建了更多的局部引用，在 JDK 中，程序员可以提供 - verbose：jni 命令行选项来打开这个消息)

#### (五)、在老的上创建一个新的帧

```
jint PushLocalFram(JNIEnv *env ,jint capacity);


```

在已经设置设置了局部变量容量的情况下， 重新创建一个局部变量容器。成功返回 0，失败返回一个负数并抛出一个 OutOfMemoryError 异常。

注意：当前的局部帧中，前面的局部帧创建的局部引用仍然是有效的

参数解释

> *   env：JNI 接口指针
> *   capacity：容量

#### (六)、释放一个局部引用

```
jobject PopLocalFrame(JNIEnv *env,jobject result)


```

弹出当前的局部引用帧，并且释放所有的局部引用。返回在之前局部引用帧与给定 result 对象对应的局部引用。如果不需要返回任何引用，则设置 result 为 NULL

参数解释

> *   env：JNI 接口指针
> *   result：需要释放的局部引用

#### (七)、创建一个局部引用

```
jobject NewLocalRef(JNIEnv *env,jobject ref);


```

创建一个引用自 ref 的局部引用。ref 可以是全局或者局部引用，如果 ref 为 NULL，则返回 NULL。

参数解释

> *   env：JNI 接口指针
> *   ref：可以试试局部引用也可以是全局引用。

#### (八)、弱全局引用

弱全局引用是一种特殊的全局引用，不像一般的全局引用，一个弱全局引用允许底层 Java 对象能够被垃圾回收。弱全局引用能够应用在任何全局或局部引用被使用的地方。当垃圾回收器运行的时候，如果对象只被弱引用所引用时，它将释放底层变量。一个弱阮菊引用指向一个被释放的对象相当于等于 NULL。编程人员可以通过使用 isSampleObject 对比弱引用和 NULL 来检测一个弱全局应用是否指向一个被释放的对象。弱全局引用在 JNI 中是 Java 弱引用的一个简化版本，在 Java 平台 API 中有有效。

当 Native 方法正在运行的时候，垃圾回收器可能正在工作，被弱引用所指向的对象可能在任何时候被释放。弱全局引用能够应用在任何全局引用所使用的地方，通常是不太适合那么做的，因为它们可能在不注意的时候编程 NULL。

当 IsSampleObject 能够识别一个弱全局引用是不是指向一个被释放的对象，但是这不妨碍这个对象在被检测之后马上被释放。这就说明了，程序员不能依赖这个方法来识别一个弱全局引用是否能够在后续的 JNI 函数调用中被使用。

如果想解决上述的问题，建议使用 JNI 函数 NewLocalRef 或者 NewGlobalRef 来用标准的全局也引用或者局部引用来指向相同的对象。如果这个独享已经被释放了这些函数会返回 NULL。否则会返回一个强引用 (这样就可以保证这个对象不会被释放)。当不需要访问这个对象时，新的引用必须显式被删除。

##### 1、创建全局弱引用

```
jweak NewWeakGlobalRef(JNIEnv *env,jobject obj);


```

创建一个新的弱全局引用。如果 obj 指向 NULL，则返回 NULL。如果 VM 内存溢出，将会抛出异常 OutOfMemoryError。

参数解释

> *   env：JNI 接口指针
> *   obj：引用对象

返回：  
全局弱引用

##### 2、删除全局弱引用

```
void DeleteWeakGlobalRef(JNIEnv *env,jweak obj);


```

VM 根据所给定的弱全局引用删除对应的资源。

参数解释

> *   env：JNI 接口指针
> *   obj：将删除的弱全局引用

六、对象操作
------

#### (一)、直接创建一个 Java 对象

```
jobject AllocObject(JNIEnv *env,jclass clazz);


```

不借助任何构造函数的情况下分配一个新的 Java 对象，返回对象的一个引用。

参数解释：

> *   env：JNI 接口指针
> *   clazz:：Java 类对象

返回：  
返回一个 Java 对象，如果该对象无法被创建，则返回 NULL。

异常：

*   如果该类是接口或者是抽象类，则抛出 InstantiationException
*   如果是内存溢出，则抛出 OutOfMemoryError

#### (二)、根据某个构造函数来创建 Java 对象

```
jobject NewObject(JNIEnv *env,jclass clazz,jmethodID methodID,...);
jobject NewObjectA(JNIEnv *env,jclass clazz,jmethodID methodID,const jvalue *args);
jobject NewObjectV(JNIEnv *env,jclass clazz,jmethodID methodID,va_list args);


```

构造一个新的 Java 对象，methodID 表明需要调用一个构造函数。这个 ID 必须通过调用 GetMethodID() 获得，GetMethodID() 为函数名，void(V) 为返回值。clazz 参数不能纸箱一个数组类

*   `NewObject`：需要把所有构造函数的入参，放在参数 methodID 之后。NewObject() 接受这些参数并将它们传递给需要被调用的 Java 的构造函数
*   `NewObjectA`：在 methodID 后面，放了一个类型为 jvalue 的参数数组——args，该数组存放着所有需要传递给构造函数的参数。NewObjectA() 接收到这个数组中的所有参数，并且按照顺序将它们传递给需要调用的 Java 方法。
*   `NewObjectV`：在 methodID 后面，放了一个类型为 va_list 的 args，参数存放着所有需要传递给构造函数的参数。NewObjectv() 接收到所有的参数，并且按照顺序将它们传递给需要调用的 Java 方法。

参数解释：

> *   env：JNI 接口指针
> *   clazz:：Java 类
> *   methodID：构造函数的方法 ID

附加参数：

*   NewObject 的附加参数：arguments 是构造函数的参数
*   NewObjectA 的附加参数：args 是构造函数的参数数组
*   NewObjectV 的附加参数：args 是构造函数的参数 list

返回：  
Java 对象，如果无法创建该对象，则返回 NULL

异常：  
如果传入的类是接口或者抽象类，则抛出 InstantiationException  
如果内存溢出，则抛出 OutOfMemoryError  
`所有的异常都是通过构造函数抛出`

#### (三)、获取某个对象的 “类”

```
jclass GetObjectClass(JNIEnv *env,object obj);


```

返回 obj 对应的类

参数解释

> *   env：JNI 接口指针
> *   obj：Java 对象，不能为 NULL

参数：  
env：JNI 接口指针  
obj：JAVA 对象，不能为 NULL

返回：  
返回一个 Java“类” 对象

#### (四)、获取某个对象的 “类型”

```
jobjectRefType GetObjectRefType(JNIEnv *env,jobject obj);


```

返回 obj 参数所以指向对象的类型，参数 obj 可以是局部变量，全局变量或者若全局引用。

参数解释

> *   env：JNI 接口指针
> *   obj：局部、全局或弱全局引用

返回：

*   JNIInvalidRefType=0：代表 obj 参数不是有效的引用类型
*   JNILocalRefType=1：代表 obj 参数是局部变量类型
*   JNIGlobalRefType=2：代表 obj 参数是全局变量类型
*   JNIWeakGlobalRefType=3：代表 obj 参数是弱全局有效引用

无效的引用就是没有引用的引用。也就是说，obj 的指针没有指向内存中创建函数时候的地址，或者已经从 JNI 函数中返回了。所以说 NULL 就是无效的引用。并且`GetObjectRefType(env,NULL)`将返回类型是`JNIInvalidRefType`。但是空引用返回的不是`JNIInvalidRefType`，而是它被创建时候的引用类型。

> PS: 不能在引用在删除的时候，调用该函数

#### (五)、判断某个对象是否是某个 “类” 的子类

```
jboolean IsInstanceOf(JNIEnv *env, jobject obj,jclass clazz); 


```

测试 obj 是否是 clazz 的一个实例

参数：

> *   env：JNI 接口指针
> *   obj：一个 Java 对象
> *   clazz：一个 Java 的类

返回：  
如果 obj 是 clazz 的实例，则返回 JNI_TRUE；否则则返回 JNI_FALSE；一个空对象可以是任何类的实例。

#### (六)、判断两个引用是否指向同一个引用

```
jboolean IsSampleObject(JNIEnv *env,jobject ref1,jobject ref2);


```

判断两个引用是否指向同一个对象

参数解释：

> *   env：JNI 接口指针
> *   ref1：Java 对象
> *   ref2：Java 对象

返回：  
如果同一个类对象，返回 JNI_TRUE；否则，返回 JNI_FALSE；

#### (七)、返回属性 id

```
jfieldID GetFieldID(JNIEnv *env,jclass clazz,const char *name,const char *sig);


```

获取某个类的非静态属性 id。通过方法`属性名`以及 · 属性的签名`(也就是属性的类型)，来确定对应的是哪个属性。通过检索这个属性ID，我们就可以调用Get <type>Field和Set <type>Field了，就是我们常用的`get`和`set` 方法

参数解释：

> *   env：JNI 接口指针
> *   clazz：一个 Java 类对象
> *   name：以 "0" 结尾的，而且字符类型是 "utf-8" 的属性名称
> *   sig：以 "0" 结尾的，而且字符类型是 "utf-8" 的属性签名

返回  
属性对应 ID，如果操作失败，则返回 NULL

异常：  
如果找不到指定的属性，则抛出 NoSuchFieldError  
如果类初始化失败，则抛出 ExceptionInitializerError  
如果内存不足了，则抛出 OutOfMemoryError

> PS：`GetFieldID()`可能会导致还未初始化的类开始初始化，同时在获取数组的长度不能使用`GetFieldID()`，而应该使用`GetArrayLength()`。

#### (八)、返回属性 id 系列

```
NativeType GetField(JNIEnv *env,jobject obj,jfieldID fielD);


```

返回某个类的非静态属性的值，这是一组函数的简称，具体如下：

```
jobject        GetObjectField(JNIEnv *env,jobject obj,jfieldID fielD)   
jboolean     GetBooleanField(JNIEnv *env,jobject obj,jfieldID fielD)
jbyte           GetByteField(JNIEnv *env,jobject obj,jfieldID fielD)
jchar           GetCharField(JNIEnv *env,jobject obj,jfieldID fielD)
jshort          GetShortField(JNIEnv *env,jobject obj,jfieldID fielD)
jint              GetIntField(JNIEnv *env,jobject obj,jfieldID fielD)
jlong           GetLongField(JNIEnv *env,jobject obj,jfieldID fielD)
jfloat           GetFloatField(JNIEnv *env,jobject obj,jfieldID fielD)
jdouble       GetDoubleField(JNIEnv *env,jobject obj,jfieldID fielD)


```

参数解释：  
env：JNI 接口指针  
obj：Java 对象，不能为空  
fieldID：有效的 fieldID

返回：  
对应属性的值

#### (九)、设置属性 id 系列

```
void Set<type>Field(JNIEnv *env,jobject obj,jfieldID fieldID,NativeType value)


```

设置某个类的的非静态属性的值。其中具体哪个属性通过`GetFieldID()`来确定哪个属性。这是一组函数的简称，具体如下：

```
void SetObjectField(jobject)

void SetBooleanField(jboolean)

void SetByteField(jbyte)

void SetCharField(jchar)

void SetShortField(jshort)

void SetIntField(jint)

void SetLongField(jlong)

void SetFloatField(jfloat)

void SetDoubleField(jdouble)


```

参数解释：

> *   env：JNI 接口指针
> *   obj：Java 对象，不能为空
> *   fieldID：有效的属性 ID
> *   value：属性的新值

#### (十)、获取某个类的某个方法 id

```
jmethodID GetMethodID(JNIEnv *env,jclass clazz,const char*name,const char* sig);


```

返回某个类或者接口的方法 ID，该方法可以是被定义在 clazz 的父类中，然后被 clazz 继承。我们是根据方法的名字以及签名来确定一个方法的。

> PS:`GetMethodID()`会造成还未初始化的类，进行初始化  
> 如果想获取构造函数的 ID, 请提供`init`作为方法名称，并将`void(V)`作为返回类型

参数解释：

> *   env：JNI 接口指针
> *   clazz：Java 类对象
> *   name：以 0 结尾的，并且是 "utf-8" 的字符串的方法名称
> *   sig：以 0 结尾的，并且是 "utf-8" 的字符串的方法签名

返回：  
返回一个方法 ID，没有找到指定的方法，则返回 NULL

异常：  
如果找不到指定的方法，则抛出 NoSuchMethodError  
如果累初始化失败，则抛出 ExceptionInInitializerError  
如果内存不够，则抛出 OutOfMemoryError

#### (十一)、调用 Java 实例的某个非静态方法 “系列”

```
NativeType Call<type>Method(JNIEnv *env,jobject obj,jmethodID methodID,...);
NativeType Call<type>MethodA(JNIEnv *env,jobjct obj,jmethodID methodID ,const jvalue *args);
NativeType  Call<type>MethodV(JNEnv *env,jobject obj,jmethodID methodID,va_list args); 


```

这一些列都是在`native`中调用 Java 对象的某个非静态方法，它们的不同点在于传参不同。是根据方法 ID 来指定对应的 Java 对象的某个方法。methodID 参数需要调用`GetMethodID()`获取。

> PS：当需要调用某个 "private" 函数或者构造函数时，这个 methodID 必须是 obj 类的方法，不能是它的父类的方法。

下面我们来看下他们的不同点

*   CallMethod：需要把方法的`入参`放在参数`methodID`后面。`CallMethod()`其实把这些参数传递给需要调用的 Java 方法。
*   CallMethodA：在`methodID`后面，有一个类型为`jvalue`的 args 数组，该数组存放所有需要传递给构造函数的参数。`CallMethodA()`收到这个数组中的参数，是按照顺序将他们传递给对应的 Java 方法
*   CallMethodV：在`methodID`后面，有一个类型 Wie`va_list`的参数 args，它存放着所有需要传递给构造函数的参数。CallMethodV() 接收所有的参数，并且按照顺序将它们传递给需要调用的 Java 方法。

```
Call<type>Method Routine Name            Native Type

CallVoidMethod() 
CallVoidMethodA()                        void
CallVoidMethodV()                       

CallObjectMethod()
CallObjectMethodA()                      jobject
CallObjectMethodV()

CallBooleanMethod()
CallBooleanMethodA()                     jboolean
CallBooleanMethodV()

CallByteMethod()
CallByteMethodA()                        jbyte
CallByteMethodV()

CallCharMethod()
CallCharMethodA()                        jchar
CallCharMethodV()

CallShortMethod() 
CallShortMethodA()                       jshort
CallShortMethodV()

CallIntMethod()
CallIntMethodA()                         jint
CallIntMethodV()

CallLongMethod()
CallLongMethodA()
CallLongMethodV()

CallFloatMethod()
CallFloatMethodA()                        jlong
CallFloatMethodV()

CallDoubleMethod()
CallDoubleMethodA()                      jfloat
CallDoubleMethodV()


```

参数解释：

> *   env：JNI 接口指针
> *   obj：对应的 Java 对象
> *   methodID：某个方法的方法 id

返回：  
返回调用 Java 方法对应的结果

异常：  
在 Java 方法执行过程中产生的异常。

#### (十二)、调用某个类的非抽象方法

调用父类中的实例方法，如下系列

```
CallNonvirtual<type>Method 
CallNonvirtual<type>MethodA 
CallNonvirtual<type>MethodV 


```

具体如下：

```
NativeType CallNonvirtual<Type>Method(JNIEnv *env,jobject obj,jclass clazz,jmethodID methodID,....);
NativeType CallNonvirtual<Type>MethodA(JNIEnv *env,jobject obj,jclass clazz,jmethodID methodID,const jvalue *args);
NativeType CallNonvirtual<type>MethodV(JNIEnv *env, jobject obj,
jclass clazz, jmethodID methodID, va_list args);


```

这一系列操作就是根据特定的类，和其方法 ID 来调用 Java 对象的实例的非静态方法，methodID 参数需要调用 GetMethodID() 获取。

`CallNonvirtual<Type>Method`和 Call<type>Method`是不同的，其中`CallNonvirtual<Type>Method`是基于"类"，而`和 Call<type>Method`是基于类的对象。所以说`CallNonvirtual<Type>Method` 的入参是 clazz，methodID 必须来源与 obi 的类，而不是它的父类

下面我们来看下他们的不同点

*   CallNonvirtual<type>Method ：需要把方法的`入参`放在参数`methodID`后面。`CallNonvirtual<type>Method()`其实把这些参数传递给需要调用的 Java 方法。
*   CallNonvirtual<type>Method：在`methodID`后面，有一个类型为`jvalue`的 args 数组，该数组存放所有需要传递给构造函数的参数。`CallNonvirtual<type>Method()`收到这个数组中的参数，是按照顺序将他们传递给对应的 Java 方法
*   CallNonvirtual<type>MethodV ：在`methodID`后面，有一个类型 Wie`va_list`的参数 args，它存放着所有需要传递给构造函数的参数。 CallNonvirtual<type>MethodV() 接收所有的参数，并且按照顺序将它们传递给需要调用的 Java 方法。

将上面这系列方法展开如下：

```
CallNonvirtual<type>Method Routine Name      Native Type
CallNonvirtualVoidMethod()
CallNonvirtualVoidMethodA()                  void
CallNonvirtualVoidMethodV()

CallNonvirtualObjectMethod()
CallNonvirtualObjectMethodA()                jobject
CallNonvirtualObjectMethodV()

CallNonvirtualBooleanMethod()
CallNonvirtualBooleanMethodA()               jboolean
CallNonvirtualBooleanMethodV()

CallNonvirtualByteMethod()
CallNonvirtualByteMethodA()                  jbyte
CallNonvirtualByteMethodV()

CallNonvirtualCharMethod()
CallNonvirtualCharMethodA()                  jchar
CallNonvirtualCharMethodV()

CallNonvirtualShortMethod()
CallNonvirtualShortMethodA()                 jshort
CallNonvirtualShortMethodV()

CallNonvirtualIntMethod()
CallNonvirtualIntMethodA()                   jint
CallNonvirtualIntMethodV()

CallNonvirtualLongMethod()
CallNonvirtualLongMethodA()                  jlong
CallNonvirtualLongMethodV()

CallNonvirtualFloatMethod()
CallNonvirtualFloatMethodA()                 jfloat
CallNonvirtualFloatMethodV()

CallNonvirtualDoubleMethod()
CallNonvirtualDoubleMethodA()                jdouble
CallNonvirtualDoubleMethodV()


```

参数解释：

> *   env：JNI 接口指针
> *   obj：Java 对象
> *   clazz：Java 类
> *   methodID：方法 ID

返回：  
调用 Java 方法的结果

抛出异常：  
在 Java 方法中执行过程可能产生的异常

#### (十三)、获取静态属性

```
jfieldID GetStaticFieldID(JNIEnv *env,jclass clazz,const char* name,const char *sig);


```

获取某个类的某个静态属性 ID，根据属性名以及标签来确定是哪个属性。`GetStaticField()`和`SetStaticField()`通过使用属性 ID 来对属性进行操作的。如果这个类还没有初始化，直接调用`GetStaticFieldID()`会引起这个类进行初始化。

参数解释：

> *   env：JNI 接口指针
> *   clazz：Java 类
> *   name：静态属性的属性名，是一个编码格式 "utf-8" 并且以 0 结尾的字符串。
> *   sig：属性的签名，是一个编码格式 "utf-8" 并且以 0 结尾的字符串。

返回：  
返回静态属性 ID，如果指定的静态属性无法找则返回 NULL

异常：  
如果指定的静态属性无法找到则抛出`NoSuchFieldError`  
如果类在初始化失败，则抛出`ExceptionInInitializerError`  
如果内存不够，则抛出`OutOfMemoryError`

#### (十四)、获取静态属性系列

```
NativeType GetStatic<type>Field(JNIEnv *env,jclass clazz,jfieldID fieldID);


```

这个系列返回一个对象的静态属性的值。可以通过`GetStaticFieldID()`来获取静态属性的的 ID，有了这个 ID，我们就可以获取这个对其进行操作了

下面表明了函数名和函数的返回值，所以只需要替换`GetStatic<type>Field`中的类替换为该字段的 Java 类型或者表中的实际静态字段存取器。并将`NativeType`替换为相应的本地类型

```
GetStatic<type>Field Routine Name      Native Type
GetStaticObjectField()                 jobject
GetStaticBooleanField()                jboolean
GetStaticByteField()                   jbyte
GetStaticCharField()                   jchar
GetStaticShortField()                  jshort
GetStaticIntField()                    jint
GetStaticLongField()                   jlong
GetStaticFloatField()                  jfloat
GetStaticDoubleField()                 jdouble


```

参数解释：

> *   env：JNI 接口指针
> *   clazz：Java 类
> *   field：静态属性 ID

返回：  
返回静态属性

#### (十五)、设置静态属性系列

```
void SetStatic<type>Field(JNIEnv *env,jclass clazz,jfieldID fieldID,NativeType value);


```

这个系列是设置类的静态属性的值。可以通过`GetStaticFieldID()`来获取静态属性的 ID。

下面详细介绍了函数名和其值，你可以通过`SetStatic<type>`并传入的 NativeType 来设置 Java 中的静态属性。

```
SetStatic<type>Field Routine Name         NativeType
SetStaticObjectField()                    jobject
SetStaticBooleanField()                   jboolean
SetStaticByteField()                      jbyte
SetStaticCharField()                      jchar
SetStaticShortField()                     jshort
SetStaticIntField()                       jint
SetStaticLongField()                      jlong
SetStaticFloatField()                     jfloat
SetStaticDoubleField()                    jdouble


```

参数解释：

> *   env：JNI 接口指针
> *   clazz：Java 类
> *   field：静态属性 ID
> *   value：设置的值

#### (十六)、获取静态函数 ID

```
jmethodID GetStaticMethodID(JNIEnv *env,jclass clazz,const char *name,const char sig);


```

返回类的静态方法 ID，通过它的方法名以及签名来确定哪个方法。如果这个类还没被初始化，调用`GetStaticMethodID()`将会导致这个类初始化。

参数解释：

> *   env：JNI 接口指针
> *   clazz：Java 类
> *   name：静态方法的方法名，以 "utf-8" 编码的，并且以 0 结尾的字符串
> *   sig：方法签名，以 "utf-8" 编码的，并且以 0 结尾的字符串

返回：  
返回方法 ID，如果操作失败，则返回 NULL

异常：  
如果没有找到对应的静态方法，则抛出`NoSuchMethodError`  
如果类初始化失败，则抛出`ExceptionInInitializerError`  
如果系统内存不足，则抛出`OutOfMemoryError`

#### (十七)、调用静态函数系列

```
NativeType CallStatic<type>Method(JNIEnv *env,jclass clazz,jmethodID methodID,...);
NativeType CallStatic<type>MethodA(JNIEnv *env,jclass clazz,jmethodID methodID,... jvalue *args);
NativeType CallStatic<type>MethodV(JNIEnv *env,jclass,jmethodID methodid, va_list args)


```

根据指定的方法 ID，就可以操作 Java 对象的静态方法了。可以通过`GetStaticMethodID()`来获得 methodID。方法的 ID 必须是 clazz 的，而不是其父类的方法 ID。

下面就是详细的方法了

```
CallStatic<type>Method Routine Name                Native Type
CallStaticVoidMethod()
CallStaticVoidMethodA()                            void
CallStaticVoidMethodV()

CallStaticObjectMethod()
CallStaticObjectMethodA()                          jobject
CallStaticObjectMethodV()

CallStaticBooleanMethod()
CallStaticBooleanMethodA()                         jboolean
CallStaticBooleanMethodV()

CallStaticByteMethod()
CallStaticByteMethodA()                            jbyte
CallStaticByteMethodV()

CallStaticCharMethod()
CallStaticCharMethodA()                            jchar
CallStaticCharMethodV()

CallStaticShortMethod()
CallStaticShortMethodA()                           jshort
CallStaticShortMethodV()

CallStaticIntMethod()
CallStaticIntMethodA()                             jint
CallStaticIntMethodV()

CallStaticLongMethod()
CallStaticLongMethodA()                            jlong
CallStaticLongMethodV()

CallStaticFloatMethod()
CallStaticFloatMethodA()                           jfloat
CallStaticFloatMethodV()

CallStaticDoubleMethod()
CallStaticDoubleMethodA()                          jdouble
CallStaticDoubleMethodV()


```

参数解释：

> *   env：JNI 接口指针
> *   clazz：Java 类
> *   methodID：静态方法 ID

返回：  
返回静态的 Java 方法的调用方法

异常：  
在 Java 方法中执行中抛出的异常

七、字符串操作
-------

#### （一)、创建一个字符串

```
jstring NewString(JNIEnv *env,const jchar *unicodeChars,jszie len);


```

参数解释：

> *   env：JNI 接口指针
> *   unicodeChars：指向 Unicode 字符串的指针
> *   len：unicode 字符串的长度

返回：  
返回一个 Java 字符串对象，如果该字符串无法被创建在，则返回 NULL

异常：  
如果内存不足，则抛出`OutOfMemoryError`

#### （二)、获取字符串的长度

```
jsize  GetStringLength(JNIEnv *env,jstring string);


```

返回 Java 字符串的长度 (unicode 字符的个数)

参数解释：

> *   env：JNI 接口指针
> *   string：Java 字符串对象

返回：  
返回 Java 字符串的长度

#### （三)、获取字符串的指针

```
const jchar* GetStringChar(JNIEnv *env,jstring string , jboolean *isCopy);


```

返回指向字符串的 UNICODE 字符数组的指针，该指针一直有效直到被`ReleaseStringchars()`函数调用。  
如果`isCopy`为非空，则在复制完成后将`isCopy`设为`JNI_TRUE`。如果没有复制，则设为`JNI_FALSE`。

参数解释：

> *   env：JNI 接口指针
> *   string：Java 字符串对象
> *   isCopy：指向布尔值的指针

返回：  
返回一个指向 unicode 字符串的指针，如果操作失败，则返回 NULL

#### （四)、释放字符串

```
void ReleaseStringChars(JNIEnv *env,jstring string,const jchar *chars);


```

通过 VM，native 代码不会再访问`chars`了。参数`chars`是一个指针。可以通过`GetStringChars()`函数获得。

参数解释：

> *   env：JNI 接口指针
> *   string：Java 字符串对象
> *   chars：指向 Unicode 字符串的指针

#### （五)、创建一个 UTF-8 的字符串

```
jstring NewStringUTF(JNIEnv *env,const char *bytes);


```

创建一个 UTF-8 的字符串。

参数解释：

> *   env：JNI 接口指针
> *   bytes：指向 UTF-8 字符串的指针

返回：  
Java 字符串对象，如果无法构造该字符串，则为 NULL。

异常：  
如果系统内存不足，则抛出`OutOfMemoryError`

#### （六)、获取一个 UTF-8 的字符串的长度

```
jsize GetStringUTFLength(JNIEnv *env,jstring string);


```

以字节为单位，返回字符串 UTF-8 的长度。

参数解释：

> *   env：JNI 接口指针
> *   String：Java 字符串对象

返回：  
字符串的 UTF-8 的长度

#### （七)、获取 StringUTFChars 的指针

```
const char *GetStringUFTChars(JNIEnv *env, jString string, jboolean *isCopy);


```

返回指向 UTF-8 字符数组的指针，除非该数组被`ReleaseStringUTFChars()`函数调用释放，否则一直有效。  
如果`isCopy`不是 NULL，`*isCopy`在赋值完成后即被设置为`JNI_TRUE`。如果未复制，则设置为`JNI_FALSE`。

参数解释：

> *   env：JNI 接口指针
> *   String：Java 字符串对象
> *   isCopy：指向布尔值的指针

返回：  
指向 UTF-8 的字符串指针，如果操作是啊白，则返回 NULL

#### （八)、释放 UTFChars

```
void ReleaseStringUTFChars(JNIEnv *env,jstring string,const char *urf)


```

通过虚拟机，native 代码不再访问了 utf 了。utf 是一个指针，可以调用`GetStringUTFChars()`获取。

参数解释：

> *   env：JNI 接口指针
> *   string：Java 字符串对象
> *   utf：指向 utf-8 字符串的指针

> 注意：在 JDK/JRE 1.1，程序员可以在用户提供的缓冲区获取基本类型数组元素，从 JDK/JRE1.2 开始，提供了额外方法，这些方法允许在用户提供的缓冲区获取 Unicode 字符 (UTF-16 编码) 或者是 UTF-8 的字符。这些方法如下：

#### （九)、1.2 新的字符串操作方法

##### 1 截取一个字符串

```
void GetStringRegion(JNIEnv *env,jstring str,jsize start,jsize len,jchar *buf)


```

在 str(Unicode 字符) 从 start 位置开始截取 len 长度放置在 buf 中。如果越界，则抛出 StringIndexOutOfBoundsException。

##### 2 截取一个字符串并将其转换为 UTF-8 格式

```
void GetStringUTFRegion(JNIEnv *env,jstring str,jsize start ,jsize len,char *buf);


```

将 str(Unicode 字符串) 从 start 位置开始截取 len 长度并且将其转换为 UTF-8 编码，然后将结果防止在 buf 中。

##### 3 截取一个字符串并将其转换为 UTF-8 格式

```
const jchar * GetStringCritical(JNIEnv *env,jstring string,jboolean *isCopy);
void ReleaseStringCritical(JNIEnv *env,jstring string,cost jchar * carray);


```

上面这两个函数有点类似于`GetStringChars()`和`ReleaseStringChars()`功能。如果可能的话虚拟机会返回一个指向字符串元素的指针；否则，则返回一个复制的副本。

> PS：`GetStringChars()`和`ReleaseStringChars()`这里两个函数有很大的限制。在使用这两个函数时，这两个函数中间的代码不能调用任何让线程阻塞或者等待 JVM 的其他线程的本地函数或者 JNI 函数。有了这些限制，JVM 就可以在本地方法持有一个从 GetStringCritical 得到的字符串的指指针时，禁止 GC。当 GC 被禁止时，任何线程如果出发 GC 的话，都会被阻塞。而`GetStringChars()`和`ReleaseStringChars()`这两个函数中间的任何本地代码都不可以执行会导致阻塞的调用或者为新对象在 JVM 中分配内存。否则，JVM 有可能死活，想象一下这样的场景：

*   1、只有当前线程触发的 GC 完成阻塞并释放 GC 时，由其他线程出发的 GC 才可能由阻塞中释放出来继续执行。
*   2、在这个过程中，当前线程会一直阻塞，因为任何阻塞性调用都需要获取一个正在被其他线程持有的锁，而其他线程正等待 GC。  
    `GetStringChars()`和`ReleaseStringChars()`的交替迭代调用是安全的，这种情况下，它们的使用必须有严格的顺序限制。而且，我们一定要记住检查是否因为内存溢出而导致它的返回值是 NULL。因为 JVM 在执行`GetStringChars()`这个函数时，仍有发生数据复制的可能性，尤其是当 JVM 在内存存储的数组不连续时，为了返回一个指向连续内存空间的指针，JVM 必须复制所有数据。  
    **总之，为了避免死锁，在 GetStringChars()`和`ReleaseStringChars()` 之间不要调用任何 JNI 函数。**

八、数组操作
------

#### (一)、获取数组的长度

```
jsize GetArrayLength(JNIEnv *env,jarray array)


```

返回数组的长度

参数解释：

> *   env：JNI 接口指针
> *   array：Java 数组

返回：  
数组的长度

#### (二)、创建对象数组

```
jobjectArray NewObjectArray(JNIEnv *env,jsize length,jclass elementClass, jobject initialElement);


```

创建一个新的对象数组，它的元素的类型是`elementClass`，并且所有元素的默认值是`initialElement`。

参数解释：

> *   env：JNI 接口指针
> *   length：数组大小
> *   elementClass：数组元素类
> *   initialElement：数组元素的初始值

返回：  
Java 数组对象，如果无法构造数组，则返回 NULL

异常：  
如果内存不足，则抛出`OutOfMemoryError`

#### (三)、获取数组元中的某个元素

```
jobject GetObjectArrayElement(JNIEnv *env,jobjectArray array,jsize index);


```

返回元素中某个位置的元素

参数解释：

> *   env：JNI 接口指针
> *   array：Java 数组
> *   index：数组下标

返回：  
Java 对象

异常：  
如果 index 下标不是一个有效的下标，则会抛出`ArrayIndexOutOfBoundsException`

#### (四)、设置数组中某个元素的值

```
void SetObjectArrayElement(JNIEnv *env,jobjectArray array,jsize index,jobject value);


```

设置下标为 index 元素的值。

参数解释：

> *   env：JNI 接口指针
> *   array：Java 数组
> *   index：数组下标
> *   value：数组元素的新值

异常：  
如果 index 不是有效下标，则会抛出`ArrayIndexOutOfBoundsException`  
如果 value 不是元素类的子类，则会抛出`ArrayStoreException`

#### (五)、创建基本类型数组系列

```
ArrayType New<PrimitiveType>Array(JNIEnv *env,jsize length);


```

用于构造基本类型数组对象的一系列操作。下面说明了特定基本类型数组的创建函数。可以把 New<PrimitiveType>Array 替换为某个实际的基本类型数组创建函数 ，然后将 ArrayType 替换为相应的数组类型

```
New<PrimitiveType>Array Routines           Array Type
NewBooleanArray()                          jbooleanArray
NewByteArray()                             jbyteArray
NewCharArray()                             jcharArray
NewShortArray()                            jshortArray
NewIntArray()                              jintArray
NewLongArray()                             jlongArray
NewFloatArray()                            jfloatArray
NewDoubleArray()                           jdoubleArray


```

参数解释：

> *   env：JNI 接口指针
> *   length：数组长度

返回：  
Java 数组，如果无法创建该数组，则返回 NULL。

#### (六)、获取基本类型数组的中数组指针系列

```
NativeType * Get<PrimitiveType>ArrayElements(JNIEnv *env,ArrayType array,jboolean * isCopy);


```

一组返回类型是基本类型的数组指针。在调用相应的`Release<PrimitiveType>ArrayElements()`函数前将一直有效。由于返回的数组可能是 Java 数组的副本，因此，对返回数组的变更没有在基本类型中反应出来。除非了调用

一组返回基本类型数组体的函数。结果在调用相应的 Release<PrimitiveType>ArrayElements() 函数前将一直有效。由于返回的数组可能是 Java 数组的副本，因此对返回数组的更改不必在基本类型数组中反映出来，直到调用 ``Release<PrimitiveType>ArrayElements()` 函数。  
如果 isCopy 不是 NULL，*isCopy 在复制完成后即被设为 JNI_TRUE。如果未复制，则设为 JNI_FALSE。

> 下面说明了特定的基本类型数组元素的具体函数：
> 
> *   将`Get<PrimitiveType>ArrayElements`替换为表中某个实际的基本 > 类型的函数
> *   将 ArrayType 替换为对应的数组类型
> *   将 NativeType 替换为本地变量

不管布尔数组在 Java 虚拟机总如何表示，`GetBooleanArrayElements()`将始终返回一个`jboolean`类型的指针，其中每一个字节代表一个元素 (开包表示)。内存中将确保所有其他类型的数组为连续的。

```
Get<PrimitiveType>ArrayElements Routines     Array Type         Native Type
GetBooleanArrayElements()                    jbooleanArray      jboolean
GetByteArrayElements()                       jbyteArray         jbyte
GetCharArrayElements()                       jcharArray         jchar
GetShortArrayElements()                      jshortArray        jshort
GetIntArrayElements()                        jintArray          jint
GetLongArrayElements()                       jlongArray         jlong
GetFloatArrayElements()                      jfloatArray        jfloat
GetDoubleArrayElements()                     jdoubleArray       jdouble


```

参数解释：

> *   env：JNI 接口指针
> *   array：Java 数组
> *   isCopy：指向布尔值的指针

返回：  
返回指向数组元素的指针，如果操作失败，则返回 NULL

#### (七)、释放基本类型的数组系列

```
void Release<PrimitiveType>ArrayElements(JNIEnv *env,ArrayType array,NativeType *elems,jint mode);


```

通知虚拟机 Native 不再访问数组的元素了。`elems`参数是使用相应的`Get <PrimitiveType> ArrayElements()`函数数组范返回的指针。如果有需要的话，该函数复制复制所有的 elems 上的变换到原始数组元素上去。mode 参数提供了数组 buffer 应该怎么被释放。如果`elems`不是被 array 的一个副本，mode 并没有什么影响。否则

果需要，该函数复制所有的在 elems 上的变换到原始的数组元素上去。  
mode 参数提供了数组 buffer 应该怎样被释放。如果 elems 不是 array 的一个副本，mode 并没有什么影响。

mode 的取值 有如下 3 种情况：

*   0：复制内容并释放 elems 缓冲区
*   JNI_COMMIT：复制内容但不释放 elems 缓冲区
*   JNI_ABORT：释放缓冲区而不复制可能的更改

大多数情况下，程序员将 “0” 作为参数传递，因为这样可以确保固定和复制数组的一致行为。其他选项可以让程序员更好的控制内存。

> 下面说明了特定的基本类型数组元素的具体函数：
> 
> *   将`Release <PrimitiveType> ArrayElements`替换下面中某个实际的基本 > 类型的函数
> *   将 ArrayType 替换为对应的基本数组类型
> *   将 NativeType 替换为本地变量

下面描述了基本类型数组释放的详情。 您应该进行以下替换：

```
Release<PrimitiveType>ArrayElements Routines     Array Type               Native Type
ReleaseBooleanArrayElements()                    jbooleanArray            jboolean
ReleaseByteArrayElements()                       jbyteArray               jbyte
ReleaseCharArrayElements()                       jcharArray               jchar
ReleaseShortArrayElements()                      jshortArray              jshort
ReleaseIntArrayElements()                        jintArray                jint
ReleaseLongArrayElements()                       jlongArray               jlong
ReleaseFloatArrayElements()                      jfloatArray              jfloat
ReleaseDoubleArrayElements()                     jdoubleArray             jdouble


```

参数解释：

> *   env：JNI 接口指针
> *   array：Java 数组
> *   elems：指向基本类型的数组的指针
> *   mode：释放模式

#### (八)、复制过去基本类型的数组系列

```
void Get<PrimitiveType> ArrayRegion(JNIEnv *env,ArrayType array,jsize start,jsize len,NativeType *buf);


```

复制基本类型的数组给 buff

> 下面说明了特定的基本类型数组元素的具体函数：
> 
> *   将`Get<PrimitiveType> ArrayRegion`替换下面中某个实际的基本 > 类型的函数
> *   将 ArrayType 替换为对应的基本数组类型
> *   将 NativeType 替换为本地变量

```
Get<PrimitiveType>ArrayRegion Routine           Array Type              Native Type
GetBooleanArrayRegion()                         jbooleanArray           jboolean
GetByteArrayRegion()                            jbyteArray              jbyte
GetCharArrayRegion()                            jcharArray              jchar
GetShortArrayRegion()                           jshortArray             jhort
GetIntArrayRegion()                             jintArray               jint
GetLongArrayRegion()                            jlongArray              jlong
GetFloatArrayRegion()                           jfloatArray             jloat
GetDoubleArrayRegion()                          jdoubleArray            jdouble


```

参数解释：

> *   env：JNI 接口指针
> *   array：Java 数组
> *   start：开始索引
> *   len：需要复制的长度
> *   buf：目标 buffer

异常：  
如果索引无效，则抛出`ArrayIndexOutOfBoundsException`

#### (九)、把基本类型数组的数组复制回来系列

```
void Set<PrimitiveType> ArrayRegion(JNIEnv *env,ArrayType array,jsize start,jsize len,const NativeType *buf);


```

主要是冲缓冲区复制基本类型的数组的函数

> 下面说明了特定的基本类型数组元素的具体函数：
> 
> *   将`Set<PrimitiveType>ArrayRegion`替换下面中某个实际的基本 > 类型的函数
> *   将 ArrayType 替换为对应的基本数组类型
> *   将 NativeType 替换为本地变量

```
Set<PrimitiveType>ArrayRegion Routine        Array Type            Native Type
SetBooleanArrayRegion()                      jbooleanArray         jboolean
SetByteArrayRegion()                         jbyteArray            jbyte
SetCharArrayRegion()                         jcharArray            jchar
SetShortArrayRegion()                        jshortArray           jshort
SetIntArrayRegion()                          jintArray             jint
SetLongArrayRegion()                         jlongArray            jlong
SetFloatArrayRegion()                        jfloatArray           jfloat 
SetDoubleArrayRegion()                       jdoubleArray          jdouble


```

参数解释：

> *   env：JNI 接口指针
> *   array：Java 数组
> *   start：开始索引
> *   len：需要复制的长度
> *   buf：源 buffer

异常：  
如果索引无效则会抛出 ArrayIndexOutOfBoundsException

#### (十)、补充

> 从 JDK/JER 1.1 开始提供`Get/Release<primitivetype>ArrayElements`函数获取指向原始数组元素的指针。如果 VM 支持锁定，则返回指向原始数组的指针，否则，复制。  
> 从 JDK/JRE 1.3 开始引入新的功能即便 VM 不支持锁定，本地代码也可以获取数组元素的直接指针，

```
void *GetPrimitiveArrayCritical(JNIEnv *env,jarray array,jboolean *isCopy);
void ReleasePrimitiveArrayCritical(JNIEnv *env,jarray array,void *carray,jint mode);


```

虽然这两个函数与上面的`Get/Release <primitivetype> ArrayElements`函数很像，但是在使用这个功能的时候，还是有很多的限制。

###### 在调用`GetPrimitiveArrayCritical`之后，调用`ReleasePrimitiveArrayCritical`之前，这个区域是不能调用其他 JNI 函数，而且也不能调用任何可能导致线程阻塞病等待另一个 Java 线程的系统调用。

比如，当前线程不能调用 read 函数来读取，正在被其他所写入的 stream。

九 系统级别的操作
---------

#### (一) 注册方法

```
jint RegisterNatives(JNIEnv *env,jclass clazz,const JNINativeMethod *methods,jint nMethod);


```

根据 clazz 参数注册本地方法，methods 参数制定 JNINativeMethod 结构数组，该数组包含本地方法的名字、签名及函数指针。其中名字及签名是指向编码为 “UTF-8” 的指针；nMethod 参数表明数组中本地方法的个数。

这里说下`JNINativeMethod`这个结构体

```
typedef struct { 
char *name; 
char *signature; 
void *fnPtr; 
} JNINativeMethod; 


```

参数解释：

> *   env：JNI 接口指针
> *   clazz：Java 类对象
> *   methods：类中的 native 方法
> *   nMethod：类中本地方法的个数

返回；  
成功返回 0，失败返回负数

异常：  
如果没有找到指定的方法或者方法不是本地方法，则抛出 NoSuchMethodError。

#### (二) 注销方法

```
jint UnregisterNatives(JNIEnv *env,jclass clazz);


```

注销本地方法。类回收之前还没有被函数注册的状态。该函数一般不能再 Native 代码中被调用，它为特定的程序提供了一种重加载重链接本地库的方法。

参数解释：

> *   JNI：接口指针
> *   clazz：Java 类对象

返回：  
注销成功返回 0，失败返回负数

#### (三) 监视操作

```
jint MonitorEnter(JNIEnv *env,jobject obj);


```

obj 引用的底层 Java 对象关联的监视器。obj 引用不能为空。每个 Java 对象都有一个相关的监视器。如果当前线程已经有关联到 obj 的监视器，它将添加监视器的计数器来表示这个线程进入监视器的次数。如果关联至 obj 的监视器不属于任何线程，那当前线程将变成该监视器的拥有者，并设置计数器为 1，如果其他计数器已经拥有了这个监视器，当前线程将进行等待直到监视器被释放，然后再获得监视器的拥有权。

通过`MonitorEnter JNI`函数调用的监视器不能用`monitorexit`Java 虚拟机指令或者同步方法退出。`MonitorEnter`JNI 函数调用和`monitorenter` Java 虚拟机指令可能用同样的对象竞争地进入监视器。

为了避免死锁，通过`MoniterEnter`JNI 函数调用进入的监视器必须用`MonitorExit`JNI 调用退出，除非`DetachCurrentThread`接口被隐式的调用来释放 JNI 监视器

参数解释：

> *   env：JNI 接口指针
> *   obj：普通的 Java 对象或类对象

返回：  
成功返回 0，失败返回负数

#### (四) 监视器退出

```
jint MonitorExit(JNIEnv *env,jobject obj);


```

当前线程拥有与该 obj 关联的监视器，线程减少计数器的值来指示线程进入监视器的次数。如果计数器的值变为 0，则线程释放该监视器。Native 代码不能直接调用`MonitorExit`来释放监视器。而是应该通过同步方法来使用 Java 虚拟机指令来释放监视器

参数解释：

> *   env：JNI 接口指针
> *   obj：普通的 Java 对象或类对象

返回：  
成功返回 0，失败返回负数

异常：  
如果当前线程不拥有该监视器，则应该抛出 IllegalMonitorStateException

十 NIO 操作
--------

NIO 相关操作允许 Native 代码直接访问`java.nio`的直接缓冲区。直接缓冲区的内容可能存在于普通的垃圾回收器以外的本地内存。有关直接缓冲区的信息，可以参考 NIO 和 java.nio.ByteBuffer 类的规范。

在 JDK/JRE 1.4 中引入了新的 JNI 函数，允许检查和操作做直接缓冲区

> *   NewDirectByteBuffer
> *   GetDirectBufferAddress
> *   GetDirectBufferCapacity

每个 Java 虚拟机的实现都必须支持这些功能，但并不是每个实现都需要支持对直接缓冲区的 JNI 访问。如果 JVM 不支持这种访问，那么`NewDirectByteBuffer`和`GetDirectBufferAddress`函数必须始终返回 NULL，并且`GetDirectBufferCapacity`函数必须始终返回 - 1。如果 JVM 确实支持这种访问，那么必须实现这三个函数才能返回合适的值。

#### (一) 返回 ByteBuffer

```
jobject NewDirectByteBuffer(JNIEnv *env,void *address,jlong capacity);


```

分配并返回一个直接的`java.nio.ByteBuffer`内存块从内存地址`address`开始的`capacity`个字节.

调用这个函数并返回字节缓冲区的对象的 Native 代码必须保证缓冲区指向一个可靠的可被读写的内存区域。进入非法的内存位置有可能会返回任意数值，DNA 不会有明显的印象，也有可能抛出异常。

参数解释：

> *   env：JNIEnv 接口指针
> *   address：内存区域的起始地址
> *   capacity：内存区域的大小

返回：  
返回一个新开辟的 java.nio.ByteBuffer 对象的本地引用。如果产生异常，则返回 NULL。如果 JVM 不支持 JNI 访问直接缓冲区，也会返回 NULL

异常：  
如果缓冲区分配失败，则返回 OutOfMemoryError

#### (二) 返回直接缓冲区中对象的初始地址

```
void* GetDirectBufferAddress(JNIEnv *env,jobject buf);


```

获取并返回 java.nio.Buffer 的内存初始地址

该函数允许 Native 代码通过直接缓冲区对象访问 Java 代码的同一内存区域

参数解释：

> *   env：JNIEnv 接口指针
> *   buf：java.nio.Buffer 对象

返回：  
返回内存区域的初始地址。如果内存区域未定义，返回 NULL，如果给定的对象不是 java.nio.buffer，则返回 NULL，如果虚拟机不支持 JNI 访问，则返回 NULL。

#### (三) 返回直接缓冲区中对象的内存容量

```
jlong GetDirectBufferCapacity(JNIEnv *env,jobject buf);


```

获取并返回 java.nio.Buffer 的内存容量。该容量是内存区域可容纳的元素的个数

参数：

> *   env：JNIEnv 接口指针
> *   buf：java.nio.Buffer 对象

返回：  
返回内存区域的容量。如果指定的对象不是`java.nio.buffer`，则返回 - 1，或者如果对象是未对齐的 view buffer 且处理器架构不支持对齐访问。如果虚拟机不支持 JNI 访问则返回 - 1。

十一、反射支持
-------

如果程序员知道`方法`和`属性`的名称和类型，则直接使用 JNI 调用 Java 方法或者访问 Java 字段。Java 核心反射 API 允许在运行时反射 Java 类。JNI 提供了 JNI 中使用的字段和方法 ID 与`Java Core Reflection API`中使用的字段和方法对象之间的一组转换函数。

#### (一)、转化获取方法 ID

```
jmethodID FromReflectedMethod(JNIEnv *env,jobject method);


```

将 java.lang.reflect.Method 或者 java.lang.reflect.Constructor 对象转换为方法 ID

参数解释：

> *   env：JNIEnv 接口指针
> *   method：java.lang.reflect.Method 或者 java.lang.reflect.Constructor 对象

返回：  
方法 ID

#### (二)、转化获取属性 ID

```
jfield FromReflectedField(JNIEnv *env,jobject field);


```

将 java.lang.reflect.Field 转化域 ID

参数解释：

> *   env：JNIEnv 接口指针
> *   field：java.lang.reflect.Field 对象

返回：  
域 ID

#### (三)、反转化并获取方法对象

```
jobject ToReflectedMethod(JNIEnv *env,jclass clazz,jmethodID methodID, jboolean isStatic);


```

将源自 cls 的方法 ID 转化为`java.lang.reflect.Method`或者`java.lang.reflect.Constructor`对象。如果方法 ID 指向一个静态属性，isStatic 必须设置为 JNI_TRUE，否则为 JNI_FALSE。

参数解释：

> *   env：JNIEnv 接口指针
> *   clazz：Java 类对象
> *   methodID：Java 类对应的方法 id
> *   isStatic：是否是静态方法

返回  
对应 Java 层 `java.lang.reflect.Method`或者`java.lang.reflect.Constructor`对象。如果失败，则返回 0

异常：  
如果内存不足，则抛出`OutOfMemoryError`。

#### (四)、反转化并获取属性对象

```
jobject ToReflectedField(JNIEnv *env,jclass cls,jfieldID field,jboolean isStatic)


```

将来源于 cls 的属性 ID 转化为`java.lang.reflect.Field`对象。如果属性 ID 指向一个静态属性，`isStatic`必须设置为`JNI_TRUE`，否则为`JNI_FALSE`。

参数解释：

> *   env：JNIEnv 接口指针
> *   cls：Java 类对象
> *   methodID：Java 对应的属性 ID
> *   isStatic：是否是静态属性

返回：  
成功返回`java.lang.reflect.Field`对象，失败返回 0

异常：  
如果内存不足，则抛出`OutOfMemoryError`

十二、获取虚拟机
--------

```
jint GetJavaVM(JNIEnv *env,JavaVM **vm);


```

返回当前线程对应的 java 虚拟机接口。返回的结果保存在 vm。

参数解释：

> *   env：JNI 接口指针
> *   vm：保存虚拟机指针

返回：  
成功返回 0，失败返回负数

上一篇文章 [Android JNI 学习 (三)——Java 与 Native 相互调用](https://www.jianshu.com/p/b71aeb4ed13d)  
下一篇文章 [Android JNI 学习 (五)——Demo 演示](https://www.jianshu.com/p/0f34c097028a)

[![](https://upload.jianshu.io/users/upload_avatars/5713484/0dbe60df-26f7-4174-a821-e1e7eea864c4.jpeg?imageMogr2/auto-orient/strip|imageView2/1/w/120/h/120/format/webp)](https://www.jianshu.com/u/8b9c629f69dd)

### 被以下专题收入，发现更多相似内容

### 推荐阅读[更多精彩内容](https://www.jianshu.com/)

*   本文已授权微信公众号：码个蛋 在微信公众号平台原创首发 前言 抽丝剥茧 RecyclerView 系列文章的目的在于帮...
    
    [![](https://upload-images.jianshu.io/upload_images/9271486-c6d7779c420d5c80.png?imageMogr2/auto-orient/strip|imageView2/1/w/300/h/240/format/webp)](https://www.jianshu.com/p/1ae2f2fcff2c)
*   转载请以链接形式标明出处：本文出自: 103style 的博客 本文操作以 Android Studio 3.4.2 ...
    
*   PlayerBase 框架 git 地址：https://github.com/jiajunhui/PlayerBas...
    
    [![](https://upload-images.jianshu.io/upload_images/2839783-1490e058adfdcba6.png?imageMogr2/auto-orient/strip|imageView2/1/w/300/h/240/format/webp)](https://www.jianshu.com/p/65fcb769f8da)
*   创建网络接口实例 retrofit 把网络请求地址分成两个部分，一个 baseurl，一个创建网络接口的地址。注意的是...
    
*   南尘在 2019 年 7 月毫无准备的情况下也参加了几家一线互联网公司的面试，包括阿里、头条、快手、趣头条、BIG...