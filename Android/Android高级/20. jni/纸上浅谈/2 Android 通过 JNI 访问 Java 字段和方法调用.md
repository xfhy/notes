> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/bwTXABjcrIpOLi0c8OeeJQ

在前面的两篇文章中，介绍了 Android 通过 JNI 进行基础类型、字符串和数组的相关操作，并描述了 Java 和 Native 在类型和签名之间的转换关系。

有了之前那些基础，就可以实现 Java 和 Native 的相互调用了，在 Native 中去访问 Java 类的字段并调用相应的方法。

访问字段
----

Native 方法访问 Java 的字段有两种形式，分别是访问类的实例字段和访问类的静态字段。

不管哪种操作，首先要定义一个具体的 Java 类型，其中，有实例的字段类型和方法，也有静态的字段类型和方法。

```
 1public class Animal {
 2    protected String name;
 3    public static int num = 0;
 4    public Animal(String name) {
 5        this.name = name;
 6    }
 7    public String getName() {
 8        return this.name;
 9    }
10    public int getNum() {
11        return num;
12    }
13}


```

### 访问类的实例字段

访问 Java 类的字段，大致步骤如下：

1.  获取 Java 对象的类
    
2.  获取对应字段的 id
    
3.  获取具体的字段值
    

以访问以上 Animal 类的 name 字段，并将其修改为例：

```
1private native void accessInstanceFiled(Animal animal);


```

对应的 C++ 代码如下：

```
 1extern "C"
 2JNIEXPORT void JNICALL
 3Java_com_glumes_cppso_jnioperations_FieldAndMethodOps_accessInstanceFiled(JNIEnv *env,jobject instance, jobject animal) {
 4    jfieldID fid; // 想要获取的字段 id
 5    jstring jstr; // 字段对应的具体的值
 6    const char *str; // 将 Java 的字符串转换为 Native 的字符串
 7    jclass cls = env->GetObjectClass(animal); // 获取 Java 对象的类
 8    fid = env->GetFieldID(cls, "name", "Ljava/lang/String;"); // 获取对应字段的 id
 9    if (fid == NULL) { // 如果字段为 NULL ，直接退出，查找失败
10        return;
11    }
12    jstr = (jstring) env->GetObjectField(animal, fid); // 获取字段对应的值
13    str = env->GetStringUTFChars(jstr, NULL);
14    if (str == NULL) {
15        return;
16    }
17    LOGD("name is %s", str);
18    env->ReleaseStringUTFChars(jstr, str);
19    jstr = env->NewStringUTF("replaced name");
20    if (jstr == NULL) {
21        return;
22    }
23    env->SetObjectField(animal, fid, jstr); // 修改字段对应的值 
24}


```

在上面的代码中，首先通过 `GetObjectClass` 函数获取对应的 Java 类，其参数就是要获得的对象类型 jobject ，然后得到的结果就是一个 jclass 类型的值，代表 Java 的 Class 类型。

其次是通过 `GetFieldID` 方法获得 Java 类型对应的字段 id 。其中，第一个参数就是之前获得的 Java 类型，第二个参数就是在 Java 中字段的具体名字，第三个参数就是字段对应的具体类型，这个类型的签名描述要转换成 Native 的表示形式，也就是之前提到的 Java 和 Native 的签名转换。

得到了 Java 类型和字段的 id 后，就可以通过 `GetObjectField` 方法来获取具体的值，它的两个参数分别是之前获得的 Java 类型和字段 id 。

`GetObjectField` 方法有很多形态，对于字段值是引用类型的，统一是 `GetObjectField`，然后得到的结果转型为想要的类型。对于基础类型，则有则对应的方法，比如 `GetBooleanField`、`GetIntField`、`GetDoubleField` 等等。

得到了字段的值之后，就可以进行想要的操作了。

最后，还可以通过 `SetObjectField` 方法来修改字段对应的值。它的前两个参数也是对应的 Java 类型和字段 id，最后的参数则是具体的值，此方法也是针对于字段类型是引用类型，而对于基础类型，也有着对应的方法，比如 `SetBooleanField`、`SetCharField`、`SetDoubleField`。

### 访问类的静态字段

访问类的静态字段，大致步骤和类的实例字段类似：

```
1private native void accessStaticField(Animal animal);


```

对应的 C++ 代码如下：

```
 1extern "C"
 2JNIEXPORT void JNICALL
 3Java_com_glumes_cppso_jnioperations_FieldAndMethodOps_accessStaticField(JNIEnv *env, jobject instance,jobject animal) {
 4    jfieldID fid;
 5    jint num;
 6    jclass cls = env->GetObjectClass(animal);
 7    fid = env->GetStaticFieldID(cls, "num", "I");
 8    if (fid == NULL) {
 9        return;
10    }
11    num = env->GetStaticIntField(cls, fid);
12    LOGD("get static field num is %d", num);
13    env->SetStaticIntField(cls, fid, ++num);
14}


```

类的静态和实例字段的访问最大不同就在于，JNI 调用对应的方法不同。对于类的静态字段，JNI 的方法多了 `Static` 的标志来表明这个对应于类的静态字段访问。

方法调用
----

JNI 调用 Java 方法和 JNI 访问 Java 字段的步骤也大致相同，

1.  获取 Java 对象的类
    
2.  获取对应方法的 id
    
3.  调用具体的方法
    

以调用类的实例方法和静态方法为例：

### 调用类的实例方法

JNI 调用 Java 类的实例方法

```
1    private native void callInstanceMethod(Animal animal);


```

对应 C++ 代码如下：

```
 1// Native 访问 Java 的类实例方法
 2extern "C"
 3JNIEXPORT void JNICALL
 4Java_com_glumes_cppso_jnioperations_FieldAndMethodOps_callInstanceMethod(JNIEnv *env, jobject instance,jobject animal) {
 5    jclass cls = env->GetObjectClass(animal); // 获得具体的类
 6    jmethodID mid = env->GetMethodID(cls, "callInstanceMethod", "(I)V"); // 获得具体的方法 id
 7    if (mid == NULL) {
 8        return;
 9    }
10    env->CallVoidMethod(animal, mid, 2); // 调用方法
11}


```

与访问字段不同的是，`GetFieldID` 方法换成了 `GetMethodID` 方法，另外由 `CallVoidMethod` 函数来调用具体的方法，前面两个参数是获得的类和方法 id，最后的参数是具体调用方法的参数。

`GetMethodID` 方法的第一个参数就是具体的 Java 类型，第二个参数是该 Java 类的对应实例方法的名称，第三个参数就是该方法对应的返回类型和参数签名转换成 Native 对应的描述。

对于不需要返回值的函数，调用 `CallVoidMethod` 即可，对于返回值为引用类型的，调用 `CallObjectMethod` 方法，对于返回基础类型的方法，则有各自对应的方法调用，比如：`CallBooleanMethod`、`CallShortMethod`、`CallDoubleMethod` 等等。

### 调用类的静态方法

对于调用类的静态方法和调用类的实例方法类似：

```
1    private native void callStaticMethod(Animal animal);


```

对应 C++ 代码如下：

```
 1// Native 访问 Java 的静态方法
 2extern "C"
 3JNIEXPORT void JNICALL
 4Java_com_glumes_cppso_jnioperations_FieldAndMethodOps_callStaticMethod(JNIEnv *env,jobject instance, jobject animal) {
 5    jclass cls = env->GetObjectClass(animal);
 6    jmethodID argsmid = env->GetStaticMethodID(cls, "callStaticMethod",
 7                                               "(Ljava/lang/String;)Ljava/lang/String;");
 8    if (argsmid == NULL) {
 9        return;
10    }
11    jstring jstr = env->NewStringUTF("jstring");
12    env->CallStaticObjectMethod(cls, argsmid, jstr);


```

调用类的静态方法 callStaticMethod，该方法需要传递一个 String 字符串参数，同时返回一个字符串参数。

具体的调用过程和调用类的实例方法类似，差别也只是在于调用方法名多加了一个 Static 的标识。

小结
--

可以看到，从 JNI 中访问 Java 的字段和访问，两者的步骤都是大致相似的，只是调用的 JNI 方法有所区别。

具体示例代码可参考我的 Github 项目，欢迎 Star。

https://github.com/glumes/AndroidDevWithCpp

相关文章：

[Android JNI 基础知识](http://mp.weixin.qq.com/s?__biz=MzA4MjU1MDk3Ng==&mid=2451526384&idx=1&sn=47bc245e77f0c9a60c2db413522ab6ef&chksm=886ffb5fbf1872492d0f1fa47524871d9832fc79229590ca6b103a2b30d170b15cced35deb26&scene=21#wechat_redirect)  

[Android JNI 数组操作](http://mp.weixin.qq.com/s?__biz=MzA4MjU1MDk3Ng==&mid=2451526387&idx=1&sn=4d22820359c813bcea2ce3e7d7d23290&chksm=886ffb5cbf18724a3bea1e3a7921717c02e0462b9b20498cc63ede46853920ce7f697bfe1760&scene=21#wechat_redirect)  

欢迎关注微信公众号：【纸上浅谈】，获得最新文章推送~~

![](https://mmbiz.qpic.cn/mmbiz_gif/e1icyHPvia5MYiaGGA60BvD5E58fwkebspU9dXK2nHm9KxzCj4lcHZe5U8RojDquCbbPXJjgNkSPdmL4q8gL9mPFw/640?wx_fmt=gif)