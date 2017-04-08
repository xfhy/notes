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

# 6. 