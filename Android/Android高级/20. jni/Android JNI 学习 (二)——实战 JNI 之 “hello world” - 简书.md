> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/b4431ac22ec2

本系列文章如下：

> *   [Android JNI(一)——NDK 与 JNI 基础](https://www.jianshu.com/p/87ce6f565d37)
> *   [Android JNI 学习 (二)——实战 JNI 之 “hello world”](https://www.jianshu.com/p/b4431ac22ec2)
> *   [Android JNI 学习 (三)——Java 与 Native 相互调用](https://www.jianshu.com/p/b71aeb4ed13d)
> *   [Android JNI 学习 (四)——JNI 的常用方法的中文 API](https://www.jianshu.com/p/67081d9b0a9c)
> *   [Android JNI 学习 (五)——Demo 演示](https://www.jianshu.com/p/0f34c097028a)

本地内容主要简介如下：

> *   1、环境展示
> *   2、传统方式的具体流程
> *   3、传统方式的相关问题
> *   4、传统方式的 so 文件
> *   5、通过 CMake 工具 demo 演示流程
> *   6、CMake 工具 demo 的背后原理
> *   7、CMake 的应用
> *   8、使用`experimental-plugin`插件编译

![](http://upload-images.jianshu.io/upload_images/5713484-19cf5fca659d5236.png)

本篇文章大纲. png

一、环境展示
------

操作系统为

![](http://upload-images.jianshu.io/upload_images/5713484-ca4370b8c95c5b37.png)

操作系统. png

Android 环境为：

![](http://upload-images.jianshu.io/upload_images/5713484-1cb7b9a6a6285166.png)

Android 环境. png

NDK 环境

![](http://upload-images.jianshu.io/upload_images/5713484-eb5ce7d721059e49.png)

NDK 环境. png

模拟器为

![](http://upload-images.jianshu.io/upload_images/5713484-013f9c417668e0f1.png)

模拟器. png

二、传统方式的具体流程
-----------

具体流程如下：

#### (一) 创建项目

首先在 Android Studio 创建一个 Android 项目，包名为`gebilaolitou.ndkdemo`

#### (二) 创建引用本地库的工具类

然后创建一个`class`为`NDKTools`

代码如下：

```
package gebilaolitou.ndkdemo;

public class NDKTools {

    public static native String getStringFromNDK();

}


```

#### (三) 修改相关 UI 显示

MainActivity 对应的 xml 中的`textview`添加 id  
如下：

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="gebilaolitou.ndkdemo.MainActivity">

    <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</android.support.constraint.ConstraintLayout>


```

然后修改 MainActivity，在里面调用`NDKTools`的`getStringFromNDK()`方法。

```
package gebilaolitou.ndkdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String text = NDKTools.getStringFromNDK();
        Log.i("gebilaolitou","text="+text);
        ((TextView)findViewById(R.id.tv)).setText(text);
    }
}


```

#### (四) 获取 classes 文件

在 Android Studio 中点击`Build`中的`Make Project`或者`Rebuild Project`进行编译来获取中间文件。如下图  

![](http://upload-images.jianshu.io/upload_images/5713484-3baae93d30240380.png)

编译. png

编译完成后，我们就可以获取 class 文件如下图

![](http://upload-images.jianshu.io/upload_images/5713484-a4be04e3c7cd4188.png)

classes 文件. png

#### (五) 进入相应目录

点击 Android Studio 下面的 Terminal，然后跳到`NDKDemo/app/build/intermediates/classes/debug`下 (其中 NDKDemo 为是项目的根目录)，在 Terminal 执行`pwd`确认目录。

#### (六) 获取. h 文件

在`NDKDemo/app/build/intermediates/classes/debug`下执行下面的命令`javah -jni gebilaolitou.ndkdemo.NDKTools`。如果没有问题，则会在`NDKDemo/app/build/intermediates/classes/debug`下面生成`gebilaolitou_ndkdemo_NDKTools.h`文件。如下图  

![](http://upload-images.jianshu.io/upload_images/5713484-ba1a08c3088b2f27.png)

头文件. png

其内容如下：

```
#include <jni.h>


#ifndef _Included_gebilaolitou_ndkdemo_NDKTools
#define _Included_gebilaolitou_ndkdemo_NDKTools
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_gebilaolitou_ndkdemo_NDKTools_getStringFromNDK
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif


```

如下图

![](http://upload-images.jianshu.io/upload_images/5713484-54de967e2daab8f8.png)

头文件内容. png

#### (七) 增加对应的. c 文件

在工程 main 目录下创建一个名字为 jni 目录，然后将刚才的. h 文件剪切过来。在 jni 目录下新建一个 c 文件。命名为`ndkdemotest.c`。此时项目目录如下：

![](http://upload-images.jianshu.io/upload_images/5713484-cfa43320759198ec.png)

jnipng

#### (八) 编写 ndkdemotest.c 文件

将 ndkdemotest.c 协商如下内容

```
#include "gebilaolitou_ndkdemo_NDKTools.h"

JNIEXPORT jstring JNICALL Java_gebilaolitou_ndkdemo_NDKTools_getStringFromNDK
  (JNIEnv *env, jobject obj){
     return (*env)->NewStringUTF(env,"Hellow World，这是隔壁老李头的NDK的第一行代码");
  }


```

![](http://upload-images.jianshu.io/upload_images/5713484-c48e6a8ec8de49f5.png)

ndkdemotest.png

内容不多，就是两部分，第一部分就是 添加`gebilaolitou_ndkdemo_NDKTools.h`头文件，然后就是具体实现`Java_gebilaolitou_ndkdemo_NDKTools_getStringFromNDK`函数

#### (九) 添加并编写 Android.mk 文件

同样在 jni 目录下，添加一个 Android.mk 文件，其目录结构如下：

![](http://upload-images.jianshu.io/upload_images/5713484-b95d764fa98c5585.png)

添加 Android.mk 文件. png

同样在 Android.mk 文件里面编写如下内容

```
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ndkdemotest-jni

LOCAL_SRC_FILES := ndkdemotest.c

include $(BUILD_SHARED_LIBRARY)


```

![](http://upload-images.jianshu.io/upload_images/5713484-6c0f5d8834122f06.png)

Android.mk 内容. png

关于 Android.mk 语言后面会单独写一篇文章进行讲解，这里重点说上面代码的内容

> *   `LOCAL_PATH := $(call my-dir)`：每个 Android.mk 文件必须以定义开始。它用于在开发 tree 中查找源文件。宏`my-dir`则由 Build System 提供。返回包含 Android.mk 目录路径。
> *   `include $(CLEAR_VARS)` ：`CLEAR_VARS`变量由 Build System 提供。并指向一个指定的 GNU Makefile，由它负责清理很多 LOCAL_xxx。例如 LOCAL_MODULE，LOCAL_SRC_FILES，LOCAL_STATIC_LIBRARIES 等等。但不是清理 LOCAL_PATH。这个清理是必须的，因为所有的编译控制文件由同一个 GNU Make 解析和执行，其变量是全局的。所以清理后才能便面相互影响。
> *   `LOCAL_MODULE := ndkdemotest-jni`：LOCAL_MODULE 模块必须定义，以表示 Android.mk 中的每一个模块。名字必须唯一且不包含空格。Build System 会自动添加适当的前缀和后缀。例如，demo，要生成动态库，则生成 libdemo.so。但请注意：如果模块名字被定义为 libabd，则生成 libabc.so。不再添加前缀。
> *   `LOCAL_SRC_FILES := ndkdemotest.c`：这行代码表示将要打包的 C/C++ 源码。不必列出头文件，build System 会自动帮我们找出依赖文件。缺省的 C++ 源码的扩展名为. cpp。
> *   `include $(BUILD_SHARED_LIBRARY)`：`BUILD_SHARED_LIBRARY`是 Build System 提供的一个变量，指向一个 GUN Makefile Script。它负责收集自从上次调用`include $(CLEAR_VARS)`后的所有 LOCAL_xxxxinx。并决定编译什么类型
>     *   `BUILD_STATIC_LIBRARY`：编译为静态库
>     *   `BUILD_SHARED_LIBRARY`：编译为动态库
>     *   `BUILD_EXECUTABLE`：编译为 Native C 可执行程序
>     *   `BUILD_PREBUILT`：该模块已经预先编译

PS: 这里不编写 Android.mk 会提示如下问题：

```
Error:Execution failed for task ':app:compileDebugNdk'.
> Error: Flag android.useDeprecatedNdk is no longer supported and will be removed in the next version of Android Studio.  Please switch to a supported build system.
  Consider using CMake or ndk-build integration. For more information, go to:
   https://d.android.com/r/studio-ui/add-native-code.html#ndkCompile
   To get started, you can use the sample ndk-build script the Android
   plugin generated for you at:
   /Users/gebilaolitou/AndroidStudioProjects/JNIDemo/app/build/intermediates/ndk/debug/Android.mk
  Alternatively, you can use the experimental plugin:
   https://developer.android.com/r/tools/experimental-plugin.html
  To continue using the deprecated NDK compile for another 60 days, set 
  android.deprecatedNdkCompileLease=1523001628930 in gradle.properties


```

全是英文，简单的翻译下如下：

> 错误：执行 app:compileDebugNdk 任务失败  
> 错误：不再支持 android.useDeprecatedNdk 标志，并且将会在未来的 Android Studio 版本中删除这个标志。请切换到 CMake 构建系统或者 ndk-build 中集成。更多的信息请参考`https://d.android.com/r/studio-ui/add-native-code.html#ndkCompile`。您可以使用 Android 的示例 ndk-build 脚本在以下位置生成的插件：  
> `/Users/gebilaolitou/AndroidStudioProjects/JNIDemo/app/build/intermediates/ndk/debug/Android.mk`。另外，你也可以使用实验性插件 [https://developer.android.com/r/tools/experimental-plugin.html](https://link.jianshu.com/?t=https%3A%2F%2Fdeveloper.android.com%2Fr%2Ftools%2Fexperimental-plugin.html)  
> 如果你还想继续再使用已经被弃用的 NDK 编译 60 天，你需要再 gradle.properties 中设置`android.deprecatedNdkCompileLease=1523001628930`

因为以上原因，我所以我们需要设置 Android.mk

#### (十) 修改相应的配置文件

**首先**检查`local.properties`文件中是否有 NDK 路径，如果有没有 NDK 路径，则添加 NDK 路径，比如我的如下：

```
ndk.dir=/Users/debilaolitouLibrary/Android/sdk/ndk-bundle
sdk.dir=/Users/debilaolitouLibrary/Library/Android/sdk


```

**其次**修改 app module 目录下的 build.gradle 中的内容，如下：

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 26
    defaultConfig {
        applicationId "gebilaolitou.ndkdemo"
        minSdkVersion 19
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"

        ndk{

            moduleName "ndkdemotest-jni"
            abiFilters "armeabi", "armeabi-v7a", "x86"

        }
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
        externalNativeBuild {
            ndkBuild {
                path 'src/main/jni/Android.mk'
            }
        }
        sourceSets.main {
            jni.srcDirs = []
            jniLibs.srcDirs = ['src/main/jniLibs']
        }
    }
}


```

这样就有了 so 文件 (此时还没生成 so 文件)

#### (十一) 修改引用类

**最后**在`NDKTools`类中添加静态初始化代码，如下：

```
public class NDKTools {

    static {
        System.loadLibrary("ndkdemotest-jni");
    }

    public static native String getStringFromNDK();
}


```

最后 run 一下即可，如下图

![](http://upload-images.jianshu.io/upload_images/5713484-3363390be8ba0136.png)

显示. png

三、传统方式的相关问题
-----------

有的同学在运行的时候，会报如下错误：

```
Error:Execution failed for task ':app:compileDebugNdk'.
> Error: Your project contains C++ files but it is not using a supported native build system.
  Consider using CMake or ndk-build integration. For more information, go to:
   https://d.android.com/r/studio-ui/add-native-code.html
  Alternatively, you can use the experimental plugin:
   https://developer.android.com/r/tools/experimental-plugin.html


```

首先把检查你项目中 gradle.properties 文件后面加上一句

```
Android.useDeprecatedNdk=true 


```

四、传统方式的 so 文件
-------------

> 大家可能会有疑问，那 so 去哪里了，我们平时使用第三方的 sdk 的 so 的时候，会要粘贴复制到项目里面，而我们上所述整个过程，并没有出现. so 这个文件，那么这个. so 去哪里了？

其实 Android Studio 自动帮我们把 so 放到 apk 里面，如果我们想找也能找到，如下图：

![](http://upload-images.jianshu.io/upload_images/5713484-9251bbead7afb964.png)

so 文件的位置. png

上面这套方式是传统的 Android Studio 的模式，那有没有更简单的方式，是有的，那下面我们就继续来看下

五、通过 CMake 工具 demo 演示流程
-----------------------

#### (一) 首先确保你本地有 CMake，我们来看下 SDK Tools

![](http://upload-images.jianshu.io/upload_images/5713484-6b65ac97fc4a60e5.png)

SDK Tools.png

上面看到第三个 `CMake` 我本地没有，所以我要进行安装

#### (二) 勾选`Include C++ Support`复选框。

在向导的 Configure your new project 部分，选中 Include C++ Support 复选框。  
如下图

![](http://upload-images.jianshu.io/upload_images/5713484-efe18fa21948545e.png)

勾选. png

> 这里有个坑，就是有好多同学说我没有这个`Include C++ Support`复选框，这是因为 Android Studio 设计的的 **"bug"**，你把这个对话框进行拉大，就出现了，因为一般的 Android 项目用不到，所以在设计这个的时候，如果不特意的拉大，就选择性的 "隐藏" 了, 太 JB 坑了。

然后一直下一步，直到`Customize C++ Support`部分

#### (三) `Customize C++ Support`的自定义项目

如下：

![](http://upload-images.jianshu.io/upload_images/5713484-91d8bbb86484cf1a.png)

模式. png

里面有个三个项目

> *   **C++ Standard**：即 C++ 标准，使用下拉列表选择你希望使用的 C++ 的标准，选择 Toolchain Default 会使用默认的`CMake`设置。
> *   **Exceptions Support**：如果你希望启用对 C++ 异常处理的支持，请选择此复选框。如果启动此复选框，Android Studio 会将`-fexceptions`标志添加到模块级`build.gradle`文件的`cppFlags`中，Gradle 会将其传递到 CMake。
> *   **Runtime Type Information Support**：如果开发者希望支持 RTTI，请选中此复选框。如果启用此复选框，Android Studio 会将`-frtti`标志添加到模块级`build.gradle`文件的 cppFlags 中，Gradle 会将其传递到 CMake。

最后点击 `Finish`。

#### (四) 检查 Android 目录

在 Android Studio 完成新项目的创建后，请从 IDE 左侧打开 Project 矿口并选择 Android 视图。如下图所示，Android Studio 将添加`cpp`和`External Build Files 组`：

![](http://upload-images.jianshu.io/upload_images/5713484-07edc704984694b3.png)

Android 模式. png

> 该图为开发者的原生源文件和外部构建脚本的 Android 视图组。

PS：(此视图无法反应磁盘上的实际文件层次结构，而是将相似文件分到一组中，简化项目导航)。如果为`Project`模式则如下：

![](http://upload-images.jianshu.io/upload_images/5713484-4f0fba5d6761ea45.png)

Project 模式. png

那我们简单介绍下这两个多出来的文件夹：

> *   在 **cpp** 文件夹中：可以找到属于项目的所有原生源文件等构建库。对于新项目，Android Studio 会创建一个示例 C++ 源文件 `native-lib.cpp`，并将其置于应用模块`src/main/cpp/`目录中。这个示例代码提供了一个简单的 C++ 函数`stringFromJNI()`，此函数可以返回字符串 **“Hello from C++”**
> *   在 **External Build Files** 文件夹中：可以找到 CMake 或 ndk-build 的构建脚本。与`build.gradle`文件指示 Gradle 构建应用一样，CMake 和 ndk-build 需要一个构建脚本来了解如何构原生库。对于新项目，Android Studio 会创建一个 CMake 构建脚本`CMakeLists.txt`，并将其置于模块根目录中。

#### (五) 直接运行项目

我们来直接 run 一下这个项目，看下结果

![](http://upload-images.jianshu.io/upload_images/5713484-b3658ead18a2f8b5.png)

结果 1.png

#### (六) 修改`native-lib.cpp`

这时候我们修改下`native-lib.cpp`，`native-lib.cpp`内容如下：  

![](http://upload-images.jianshu.io/upload_images/5713484-2ffdbfb91369d341.png)

native-lib.cpp 内容. png

再直接 run 一下项目，看下结果。如下：

![](http://upload-images.jianshu.io/upload_images/5713484-daec66a7b16c6be3.png)

结果 2.png

我们看到对应的文字已经修改了

六、CMake 工具 demo 的背后原理
---------------------

我们看打了，我们什么都没做，就自动实现了 C++ 的实现，它的背后原理是什么那？我们大家就思考一下？

#### (一)CMake 的入口

> 它既然可以跑起来，一定有一个入口，那这个入口在哪里那?

**~~~~~~~~~~~~~~~~~~~~~~~~~~ 分隔符~~~~~~~~~~~~~~~~~~~~**

先和大家说下我是怎么想象的，首先我们在点击 Android Studio 中的`run`按钮的时候，它是执行 Gradle 来进行打包的，所以说关于 CMake 的是怎么植入进去的，一定在项目的`build.gradle`，有相应的入口。

> 通过上面的思想，我们能举一反三得到什么？对的，就是类似于这种操作，一般都是在`build.gradle`里面实现的，因为在目前 Android Studio 就是通过 Gradle 是实现的

那我们就来看下它的`build.gradle`里面的代码，如下：

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 26
    defaultConfig {
        applicationId "gebilaolitou.cmakendkdemo"
        minSdkVersion 23
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags ""
            }
        }
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    externalNativeBuild {
        cmake {
            path "CMakeLists.txt"
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:26.1.0'
    implementation 'com.android.support.constraint:constraint-layout:1.0.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.1'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.1'
}


```

和我们平时搭建的项目差不多，就是多出来一块内容，**externalNativeBuild**。那这里我们重点说下 **externalNativeBuild**

#### (二) externalNativeBuild

我们在`build.gradle`里面看到，有两个地方用到了`externalNativeBuild`，一个是在`defaultConfig`里面，是一个是在`defaultConfig`外面。

> *   在`defaultConfig`外面的`externalNativeBuild`里面的`cmake`指明了`CMakeList.txt`的路径 (在本项目下，和是`build.gradle`在同一个目录里面)。
> *   在`defaultConfig`里面的`externalNativeBuild`里面的`cmake`主要填写的是`CMake`的命令参数。即由 arguments 中的参数最后转化成一个可执行的 CMake 的命令，可以在

defaultConfig 外面的 externalNativeBuild - cmake，指明了 CMakeList.txt 的路径；  
defaultConfig 里面的 externalNativeBuild - cmake，主要填写 CMake 的命令参数。即由 arguments 中的参数最后转化成一个可执行的 CMake 的命令，可以在 `app/externalNativeBuild/cmake/debug/{abi}/cmake_build_command.txt`中查到。如下  
路径位置如下图：  

![](http://upload-images.jianshu.io/upload_images/5713484-9c786f0b9f97731f.png)

路径. png

内容如下：

```
arguments : 
-H/Users/gebilaolitou/Desktop/codeLib/CMakeNDKDemo/app
-B/Users/gebilaolitou/Desktop/codeLib/CMakeNDKDemo/app/.externalNativeBuild/cmake/debug/x86
-DANDROID_ABI=x86
-DANDROID_PLATFORM=android-23
-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=/Users/gebilaolitou/Desktop/codeLib/CMakeNDKDemo/app/build/intermediates/cmake/debug/obj/x86
-DCMAKE_BUILD_TYPE=Debug
-DANDROID_NDK=/Users/gebilaolitou/Library/Android/sdk/ndk-bundle
-DCMAKE_CXX_FLAGS=
-DCMAKE_TOOLCHAIN_FILE=/Users/gebilaolitou/Library/Android/sdk/ndk-bundle/build/cmake/android.toolchain.cmake
-DCMAKE_MAKE_PROGRAM=/Users/gebilaolitou/Library/Android/sdk/cmake/3.6.4111459/bin/ninja
-GAndroid Gradle - Ninja
jvmArgs : 


```

更多的可以填写的命令参数和含义可以参见 [Android NDK-CMake 文档](https://link.jianshu.com/?t=https%3A%2F%2Fdeveloper.android.com%2Fndk%2Fguides%2Fcmake.html)

ok 上面既然提到了`CMakeLists.txt`，那我们就来看下`CMakeLists.txt`

#### (三) CMakeLists.txt

`CMakeLists.txt`这个文件主要定义了哪些文件需要编译，以及和其他库的关系等，那让我们来看下我们项目中的`CMakeLists.txt`的内容

```
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )


```

上面很多是注释，我们除去注释来个 "精简干练版" 的如下：

```
cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )


```

`CMakeLists.txt`我们看到这里主要是分为四个部分，下面我们就依次来看下

> *   cmake_minimum_required(VERSION 3.4.1)：指定 CMake 的最小版本
> *   add_library：创建一个静态或者动态库，并提供其关联的源文件路径，开发者可以定义多个库，CMake 会自动去构建它们。Gradle 可以自动将它们打包进 APK 中。
>     *   第一个参数——native-lib：是库的名称
>     *   第二个参数——SHARED：是库的类别，是`动态的`还是`静态的`
>     *   第三个参数——src/main/cpp/native-lib.cpp：是库的源文件的路径
> *   find_library：找到一个预编译的库，并作为一个变量保存起来。由于 CMake 在搜索库路径的时候会包含系统库，并且 CMake 会检查它自己之前编译的库的名字，所以开发者需要保证开发者自行添加的库的名字的独特性。
>     *   第一个参数——log-lib：设置路径变量的名称
>     *   第一个参数—— log：指定 NDK 库的名子，这样 CMake 就可以找到这个库
> *   target_link_libraries：指定 CMake 链接到目标库。开发者可以链接多个库，比如开发者可以在此定义库的构建脚本，并且预编译第三方库或者系统库。
>     *   第一个参数——native-lib：指定的目标库
>     *   第一个参数——${log-lib}：将目标库链接到 NDK 中的日志库，

这其实是一个最基础的`CMakeLists.txt` ，其实`CMakeLists.txt`里面可以非常强大，比如自定义命令、查找文件、头文件包含、设置变量等等。这里推荐 **CMake** 的[官网文档](https://link.jianshu.com/?t=https%3A%2F%2Fcmake.org%2Fdocumentation%2F)，不过是英文的，不好阅读，大家可以参考中文的 [CMake 手册](https://link.jianshu.com/?t=https%3A%2F%2Fwww.zybuluo.com%2Fkhan-lau%2Fnote%2F254724)

上面分析完毕`CMakeLists.txt`，我们就大致的知道了 CMake 整体的构建流程，那我们就来看下

#### (四) CMake 的运转流程

> *   1、Gradle 调用外部构建脚本`CMakeLists.txt`
> *   2、CMake 按照构建脚本的命令将 C++ 源文件 `native-lib.cpp` 编译到共享的对象库中，并命名为 `libnative-lib.so` ，Gradle 随后会将其打包到 APK 中
> *   3、运行时，应用的`MainActivity` 会使用`System.loadLibrary()`加载原生库。应用就是可以使用库的原生函数`stringFromJNI()`。

PS: 这里注意一点就是：`Instant Run` 与使用原生的项目不兼容

如果想看 Gradle 是否将原生库打包到 APK 中，可以使用`Analyze APK`来检测。

七、CMake 的应用
-----------

> 我们在做日常需求的时候，往往会遇到一个问题，即在已有的项目中，添加 C 库，这样就不能通过上面的**`创建`**流程，来使用 CMake。那怎么办？

其实没关系的，CMake 也提供这样的功能的，现在我们就回到上面的第一个 demo 中，删除和 NDK 的有关的所有代码，删除后其目录如下：

![](http://upload-images.jianshu.io/upload_images/5713484-0531d3a279fc1219.png)

新目录. png

#### (一) 创建源文件

即在`main`目录下新建一个目录，我们就叫`cpp`好了。然后在该目录下创建一个 C++ Source File(右键点击您刚刚创建的目录，然后选择 New> C/C++ Source File)。我们将其命名为 native-lib。

创建后，目录如下：

![](http://upload-images.jianshu.io/upload_images/5713484-5f344b9f5222fc42.png)

创建源文件. png

#### (二) 创建 CMake 构建脚本

> 因为目前这个项目没有 CMake 的构建脚本，所以咱们需要自行创建一个并包含适当的 CMake 命令。CMake 构建脚本是一个纯文本的文件，而且这个名字必须是是 **CMakeLists.txt**

要常创建一个可以用作 CMake 构建脚本的纯文本文件，请按以下步骤操作：

> *   1、从 Android Studio 左侧打开 Project 窗格并从下拉菜单中选择 Project 视图。
> *   2、右键点击 模块的根目录并选择 `New`——> `File`。  
>     `PS：这个位置不是不固定的，位置可以随意，但是配置构建脚本时，需要将这个位置写入构建脚本`
> *   3、输入`CMakeLists.txt`作为文件并点击`OK`

创建后，目录如下：

![](http://upload-images.jianshu.io/upload_images/5713484-5cc65e7fae6c82e2.png)

CMakeLists.txt.png

#### (三) 向 CMake 脚本文件写入数据

这块上面讲解了过了，就不详细说明了，内容如下：

```
cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library. 
             native-lib
             # Sets the library as a shared library.
             SHARED
             # Provides a relative path to your source file(s). 
             src/main/cpp/native-lib.cpp )

find_library( # Defines the name of the path variable that stores the
              # location of the NDK library.
              log-lib

              # Specifies the name of the NDK library that
              # CMake needs to locate.
              log )
              

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the log library to the target library.
                       ${log-lib} )


```

#### (四) 向 Gradle 关联到原生库

> 要将 Gradle 关联到原生库，需要提供一个指向 CMake 或 ndk-build 脚本文件的路径。在构建应用时，Gradle 会以依赖项的形式运行 CMake 或 ndk-build，并将共享的库打包到 APK 中。Gradle 还是用构建脚本来了解将那些文件添加到 Android 项目中。  
> `如果原生文件还没有构建脚本，需要创建CMake构建脚本`

关于 关联到原生库有两种方式，一种是通过 Android Studio，一种是手动，其实其背后的东西是一致的，我们就一一来说明

##### 1、通过 Android Studio 实现

> *   1、从 IDE 左侧打开`Project` 窗格 并选择 `Android` 视图
> *   2、右键点击想要关联到原生库的模块 (咱们这里是 **app** 模块)，并从菜单中选择 `Link C++ Project with Gradle`。如下图
> *   3、在下拉菜单中选择 **CMake**。使用 **Project Pat** 来为外部的 CMake 项目指定刚刚的 ``CMakeLists.txt` 脚本文件
> *   4、点击`OK`。

![](http://upload-images.jianshu.io/upload_images/5713484-987d3626d4b1b1d8.png)

Link C++ Project with Gradle.png

##### 2、手动实现

要手动配置 Gradle 以关联到原生库，需要将`externalNativeBuild{}` 块添加到模块级 `build.gradle` 文件中，并使用`cmake {}`对其进行配置

代码如下：

```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 26
    defaultConfig {
        applicationId "gebilaolitou.ndkdemo"
        minSdkVersion 19
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }

    }

    externalNativeBuild {
        cmake {
            path 'CMakeLists.txt'
        }
    }
}


```

#### (五) 编写 native-lib.cpp

这块很简单，内容如下：

```
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_gebilaolitou_ndkdemo_NDKTools_getStringFromNDK(
        JNIEnv *env, jobject ) {
    std::string hello = "(*^__^*) 嘻嘻……~Hello from C++ 隔壁老李头";
    return env->NewStringUTF(hello.c_str());
}


```

然后在`NDKTools.java`添加引用，如下：

```
package gebilaolitou.ndkdemo;


public class NDKTools {

    static {
        System.loadLibrary("native-lib");
    }

    public static native String getStringFromNDK();
}



```

然后直接运行，即可，结果如下：

![](http://upload-images.jianshu.io/upload_images/5713484-3ad725f5dcd60165.png)

结果 3.png

八、使用 experimental-plugin 插件简介
-----------------------------

我们在使用 NDK 开发有件比较麻烦的事情，就是编写 Android.mk 和 Application.mk，儿 Android Studio 的插件 gradle-experimental 就是用来解决这个问题的。所以使用 gradle-experimental 插件可以不用再编写. mk 文件情况下进行 NDK 开发。

gradle-experimental 是 Android Studio 的一个实验性的项目，是基于 gradle 的一个插件，主要用来自动化 NDK 的配置实现，无需自己编写 Android.mk 和 Android.mk，对于调试 NDK 项目也更加友好，不过现在已经 **不支持** ，详细请看 [Experimental Plugin User Guide](https://link.jianshu.com/?t=http%3A%2F%2Ftools.android.com%2Ftech-docs%2Fnew-build-system%2Fgradle-experimental)

> **Note to experimental Android plugin users:** The experimental plugin will no longer be supported after version 0.11.0 (released October 25, 2017). That's because the experimental plugin is designed around a [Software Component Model](https://link.jianshu.com/?t=https%3A%2F%2Fdocs.gradle.org%2Fcurrent%2Fuserguide%2Fsoftware_model.html) that Gradle announced they will no longer support ([read their blog post here](https://link.jianshu.com/?t=https%3A%2F%2Fblog.gradle.org%2Fstate-and-future-of-the-gradle-software-model)). Gradle has backported many features from the component model, which are now available with Android plugin 3.0.0, such as [variant-aware dependency resolution](https://link.jianshu.com/?t=https%3A%2F%2Fd.android.com%2Fstudio%2Fbuild%2Fgradle-plugin-3-0-0-migration.html%23variant_aware), and [api and implementation dependency configurations](https://link.jianshu.com/?t=https%3A%2F%2Fd.android.com%2Fstudio%2Fbuild%2Fgradle-plugin-3-0-0-migration.html%23new_configurations). Gradle is working on backporting built-in support for compiling C/C++ code, and the Android plugin will integrate that support when it becomes available. Until then, you can either keep using experimental plugin 0.11.0 with [Android Studio 3.0 or later](https://link.jianshu.com/?t=https%3A%2F%2Fdeveloper.android.com%2Fstudio%2Findex.html), or migrate to Android Studio's support for [using external native build tools](https://link.jianshu.com/?t=https%3A%2F%2Fd.android.com%2Fstudio%2Fprojects%2Fadd-native-code.html).

简单翻译下如下：

> 对使用`experimental`Android 插件的用户请注意：自 2017 年 10 月 25 日发布的 0.11.0 后，我们将不再支持`experimental`插件了。因为 Gradle 不再支持这个依靠软件组件模型设计`experimental`插件了 (通过他们的博客)。在 Gradle Android 插件的 3.0.0 版本，现在已经支持组建模型中的许多功能。例如`variant-aware dependency resolution`和`api and implementation dependency configurations`。Gradle 现在支持编译 C/C++ 代码的内置支持，并且 Android 插件再可用时集成该支持。在此之间，您可以继续使用 Android Studio3.0 或者更高版本的`experimental`插件，或者使用 Android Studio 支持的外部原生构建工具。

上一篇文章 [Android JNI(一)——NDK 与 JNI 基础](https://www.jianshu.com/p/87ce6f565d37)  
下一篇文章 [Android JNI 学习 (三)——Java 与 Native 相互调用](https://www.jianshu.com/p/b71aeb4ed13d)