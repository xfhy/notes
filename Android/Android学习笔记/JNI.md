# JNI

> what 什么是JNI
> 
* JNI java native interface native本地  java本地接口
* 通过JNI可以实现java和本地代码之间相互调用
* jni可以看做是翻译 实际上就是一套协议

> why 为什么要用JNI
> 
* Java 一处编译到处运行 
	* ①java运行在虚拟机上 JNI可以扩展java虚拟机的能力 让java代码可以调用驱动
	* ②java是解释型语言 运行效率相对较低 C/C++的效率要高很多 通过jni把耗时操作方法C/C++可以提高java运行效率
	* ③ java代码编译成的.class 文件安全性较差, 可以通过jni 把重要的业务逻辑放到c/c++去实现,c/c++反编译比较困难 安全性较高
* C历史悠久 1972年C 通过JNI可以调用优秀的C开源类库 

> 怎么用JNI
> 
* java
* c/c++ 能看懂 会调用
* JNI开发流程 NDK native develop kit  

# 警告!!!

写完之后一定要记得加载动态链接库

	static {
		System.loadLibrary("hello");
	}

# 1.交叉编译

- 在一个平台上去编译另一个平台上可以执行的本地代码
- cpu平台 arm x86 mips
- 操作系统平台  Windows Linux max os
- 原理  模拟不同平台的特性去编译代码

# 2. jni开发工具

- ndk native develop kit 
- ndk 目录
   * docs 帮助文档
   * platforms  好多平台版本文件夹 选择时选择项目支持的最小版本号对应的文件夹
	* 每一个版本号的文件夹中放了 不同cpu架构的资源文件
	* include文件夹 jni开发中常用的 .h头文件
	* lib 文件夹 google打包好的 提供给开发者使用的 .so文件
	* samples google官方提供的样例工程 可以参考进行开发
	* android-ndk-r9d\build\tools linux系统下的批处理文件 在交叉编译时会自动调用
	* ndk-build 交叉编译的命令
* cdt eclipse的插件 高亮C代码 C的代码提示

# 3. jni helloworld

- jni开发的步骤
- 
   1.写Java代码  声明本地方法,用到native关键字,本地方法不用去实现

   2.项目根目录下创建jni文件夹

   3.在jni文件夹下创建.c文件

      * 本地函数命名规则:Java_包名_类名_本地方法名
      * JNIENV* env JNIEnv 是JNINativeInterface这个结构体的一级指针
      * JniNativeInterface这个结构体定义了大量的函数指针
      * env 就是结构体JniNativeInterface这个结构体的二级指针
      * `(*env)->`调用结构体中的函数指针
      * 第二个参数jobject 调用本地函数的java对象就是这个jobject
      
		jstring Java_com_xfhy_jnihelloworld_MainActivity_helloFromC(JNIEnv* env,jobject thiz){
		char* str = "hello from c!";
		//到jni.h中找到如下方法   jstring     (*NewStringUTF)(JNIEnv*, const char*);
		return (*env)->NewStringUTF(env,str);
	}

函数中必须有2个形参:`JNIEnv* env,jobject thiz`;如果java中的native函数有形参的话,则需要把这些形参加在`JNIEnv* env,jobject thiz`这个2个形参之后.

   4.导入<jni.h>

   5.创建Android.mk makefile 告诉编译器.c的源文件在什么地方,要生成的编译对象的名字是什么
 	
			LOCAL_PATH := $(call my-dir)
		
		    include $(CLEAR_VARS)
		
		    LOCAL_MODULE    := hello   #指定了生成的动态链接库的名字
		    LOCAL_SRC_FILES := hello.c #指定了C的源文件叫什么名字
		
		    include $(BUILD_SHARED_LIBRARY)	

   6.打开项目工程目录,打开命令行,输入`ndk-build`即可编译.调用ndk-build编译c代码生成动态链接库.so文件, 编译完成后文件的位置 lib->armeabi->.so;

   7.在java代码中加载动态链接库 System.loadlibrary("动态链接库的名字"); Android.mkLOCAL_MODULE所指定的名字

# 4. jni开发中的常见错误
* java.lang.UnsatisfiedLinkError: Native method not found: 本地方法没有找到
	* 本地函数名写错
	* 忘记加载.so文件 没有调用System.loadlibrary 
* findLibrary returned null
	* `System.loadLibrary("libhello");` 加载动态链接库时 动态链接库名字写错
	* 平台类型错误 把只支持arm平台的.so文件部署到了 x86cpu的设备上 

			在jni目录下创建 Application.mk 在里面指定 
			APP_ABI := armeabi x86
			APP_PLATFORM := android-14

* javah 命令:生成java代码中本地方法名对应的C语言的函数名

		使用方法:javah com.xfhy.jnihelloworld.MainActivity

	* jdk 1.7 项目 src目录下运行javah
	* jdk 1.6 项目 bin目录下 classes文件夹
	* javah native方法声明的java类的全类名 

# 5.jni简便开发流程
* ① 写java代码 native 声明本地方法
* ② 添加本地支持 右键单击项目->andorid tools->add native surport
	* 如果发现 finish不能点击需要给工作空间配置ndk目录的位置
	* window->preferences->左侧选择android->ndk 把ndk解压的目录指定进来
* ③ 如果写的是.c的文件 先修改一下生成的.cpp文件的扩展名 不要忘了 相应修改Android.mk文件中LOCAL_SRC_FILES的值
* ④ javah生成头文件 在生成的头文件中拷贝c的函数名到.c的文件
* ⑤ 解决CDT插件报错的问题
* 右键单击项目选择 properties 选测 c/c++ general->paths and symbols->include选项卡下->点击add..->file system 选择ndk目录下 platforms文件夹 对应平台下(项目支持的最小版本)
 usr 目录下 arch-arm -> include  确定后 会解决代码提示和报错的问题
* ⑥编写C函数 如果需要单独编译一下c代码就在c/c++视图中找到小锤子,点一下小锤子就编译了
*  如果想直接运行到模拟器上 就不用锤子了,直接右键run as,然后就自动编译了
* ⑦ java代码中不要忘了 System.loadlibrary(); 

		static {
			System.loadLibrary("hello");   //加载动态链接库
		}

# 6. Java向C传递一些基本的类型,处理

**将一个jstring转换成一个c语言的char* 类型工具方法**

	char* _JString2CStr(JNIEnv* env, jstring jstr) {
		 char* rtn = NULL;
		 jclass clsstring = (*env)->FindClass(env, "java/lang/String");
		 jstring strencode = (*env)->NewStringUTF(env,"GB2312");
		 jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B");
		 jbyteArray barr = (jbyteArray)(*env)->CallObjectMethod(env, jstr, mid, strencode); // String .getByte("GB2312");
		 jsize alen = (*env)->GetArrayLength(env, barr);
		 jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
		 if(alen > 0) {
			rtn = (char*)malloc(alen+1); //"\0"
			memcpy(rtn, ba, alen);
			rtn[alen]=0;
		 }
		 (*env)->ReleaseByteArrayElements(env, barr, ba,0);
		 return rtn;
	}


**int类型**

	JNIEXPORT jint JNICALL Java_com_xfhy_javapassdata_JNI_add
	  (JNIEnv * env, jobject clazz, jint x, jint y){
		return x+y;   //直接返回x+y
	}

**String类型**

	JNIEXPORT jstring JNICALL Java_com_xfhy_javapassdata_JNI_sayHelloInC
	  (JNIEnv *env, jobject clazz, jstring str){
	
		//将jstring转换成char* 类型
		char* cstr = _JString2CStr(env,str);
	    //调用C语言的strlen测量cstr字符串的长度
		int length = strlen(cstr);
		int i=0;
		for(i=0; i<length; i++){
			*(cstr+i) += 1;   //将字符串+1
		}
		return (*env)->NewStringUTF(env,cstr);    //将char* 类型转换成String类型返回
	
	}

**int[]类型**

	JNIEXPORT jintArray JNICALL Java_com_xfhy_javapassdata_JNI_arrElementsIncrease
	  (JNIEnv *env, jobject clazz, jintArray jArray) {
		//jsize       (*GetArrayLength)(JNIEnv*, jarray);    返回数组长度
		int length = (*env)->GetArrayLength(env,jArray);
		//jint*       (*GetIntArrayElements)(JNIEnv*, jintArray, jboolean*);   最后一个参数表示是否拷贝,可以不用传值
		//返回int* 返回该数组的首地址    这样就可以直接通过该指针直接操作该数组了
		int* cArray = (*env)->GetIntArrayElements(env,jArray,NULL);
		int i;
		for(i=0; i<length; i++) {
			*(cArray+i) += 10;
		}
		return jArray;   //直接将原数组返回(这时已经是修改过了的)
	}

# 7.C代码中向logcat输出内容

1.Android.mk文件增加以下内容

	LOCAL_LDLIBS += -llog

2.C代码中增加以下内容

	#include <android/log.h>
	#define LOG_TAG "xfhy"
	#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
	#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

* define C的宏定义 起别名  #define LOG_TAG "xfhy" 给"xfhy"起别名LOG_TAG 
* #define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__),ANDROID_LOG_DEBUG表示优先级     debug      ANDROID_LOG_INFO表示info 这些在log.h中可以看到
* 给 __android_log_print函数起别名  写死了前两个参数 第一个参数 优先级 第二个参数TAG
* __VA_ARGS__:是可变参数的固定写法
* LOGI(...)在调用的时候 用法跟printf()一样

# 8. C代码回调java方法

首先需要了解:

**Java反射**

	public class Demo {
	
		public static void main(String[] args) {
			
			//1.获取字节码对象
			Class util = Utils.class;
			try {
				//2.获取Method对象    方法名,参数类型
				Method method = util.getMethod("test", String.class);
				//3.通过字节码对象创建一个Obejct 
				Object obj = util.newInstance();
				//4.通过对象调用方法     对带有指定参数的指定对象调用由此 Method 对象表示的底层方法
				method.invoke(obj, "hello");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public class Utils {
		
			public void test(String string) {
				System.out.println(string);
			}
			
		}

	}

**C代码回调java方法**

* ① 找到字节码对象 
	*  //jclass      (*FindClass)(JNIEnv*, const char*); 
	*  //第二个参数 要回调的java方法所在的类的路径 "com/itheima/callbackjava/JNI"
* ② 通过字节码对象找到方法对象
	* //jmethodID   (*GetMethodID)(JNIEnv*, jclass, const char*, const char*);
	* 第二个参数 字节码对象 第三个参数 要反射调用的java方法名 第四个参数 要反射调用的java方法签名
	* javap -s 要获取方法签名的类的全类名 项目/bin/classes 运行javap 
* ③ 通过字节码创建 java对象(可选) 如果本地方法和要回调的java方法在同一个类里,可以直接用 jni传过来的java对象 调用创建的Method
	* jobject obj =(*env)->AllocObject(env,claz);
	* 当回调的方法跟本地方法不在一个类里 需要通过刚创建的字节码对象手动创建一个java对象
	* 再通过这个对象来回调java的方法
	* 需要注意的是 如果创建的是一个activity对象 回调的方法还包含上下文 这个方法行不通!!!回报空指针异常 
* ④ 反射调用java方法
	* //void        (*CallVoidMethod)(JNIEnv*, jobject, jmethodID, ...);
	* 第二个参数 调用java方法的对象 第三个参数 要调用的jmethodID对象 可选的参数 调用方法时接收的参数 

			/*
			 * Class:     com_xfhy_callbackjava_JNI     java里的类的完整路径
			 * Method:    callbackvoidmethod     java里面的方法名
			 * Signature: ()V   方法签名
			 */
			JNIEXPORT void JNICALL Java_com_xfhy_callbackjava_JNI_callbackvoidmethod
			  (JNIEnv *env, jobject clazz) {
			
				//1.找到字节码对象
				//jclass      (*FindClass)(JNIEnv*, const char*);
				//参数: env,需要反射的对象的全路径
				jclass claz = (*env)->FindClass(env,"com/xfhy/callbackjava/JNI");
			
				//2.通过字节码对象找到方法对象
				//jmethodID   (*GetMethodID)(JNIEnv*, jclass, const char*, const char*);
				//第二个参数 字节码对象 第三个参数 要反射调用的java方法名 第四个参数 要反射调用的java方法签名
				//javap -s 要获取方法签名的类的全类名 项目/bin/classes 运行javap
				jmethodID methodID = (*env)->GetMethodID(env,claz,"helloFromJava","()V");
			
				//3.通过字节码创建java对象(可选),如果本地方法和要调用的java方法在同一个类里,可以直接用jni传过来的java对象,调用创建的Method
				//jobject obj =(*env)->AllocObject(env,claz);
					//* 当回调的方法跟本地方法不在一个类里 需要通过刚创建的字节码对象手动创建一个java对象
					//* 再通过这个对象来回调java的方法
					//* 需要注意的是, 如果创建的是一个activity对象, 回调的方法还包含上下文 ,这个方法行不通!!!回报空指针异常
			
				//4.反射调用java方法
				//void        (*CallVoidMethod)(JNIEnv*, jobject, jmethodID, ...);
				//第二个参数 调用java方法的对象 第三个参数 要调用的jmethodID对象 可选的参数 调用方法时接收的参数
				(*env)->CallVoidMethod(env,clazz,methodID);
			}

# 9.c++ 开发JNI

**C的预处理命令**

* #开头的就是c/c++的预处理命令
* 在编译之前 先会走预编译阶段 预编译阶段的作用就是 把 include进来的头文件 copy到源文件中
* define这些宏定义 用真实的值替换一下
* `#if #else #endif` 该删除的删除掉

**c++开发jni代码时**

* env不再是结构体Jninativeinterface的二级指针
* _JNIEnv JNIEnv  _JNIEnv 是C++的结构体 C++结构体跟C区别 C++的结构体可以定义函数
* env 是JNIEnv的一级指针 也就是结构体_JNIEnv的一级指针 env-> 来调用 结构体里的函数
* _JNIEnv的函数 实际上调用的就是结构体JNINativeInterface的同名函数指针
* 在调用时第一个参数 env已经传进去了

* **C++的函数要先声明再使用** 可以把javah生成的头文件include进来作为函数的声明
* include的方法 `<> "" ""`
* 如果用"" 来导入头文件 系统会先到 源代码所在的文件夹去找头文件 如果找不到再到系统指定的incude文件夹下找
* //用<> 直接到系统指定的include目录下去找