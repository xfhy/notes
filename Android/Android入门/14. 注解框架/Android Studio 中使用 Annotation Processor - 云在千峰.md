> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 http://blog.chengyunfeng.com/?p=1021

# [在 Android Studio 中使用 Annotation Processor](http://blog.chengyunfeng.com/?p=1021 "在 Android Studio 中使用 Annotation Processor")

作者: rain 分类: [移动](http://blog.chengyunfeng.com/?cat=5) 发布时间: 2016-09-30 00:25 Java <textarea wrap="soft" class="crayon-plain print-no" data-settings="dblclick" readonly="" style="tab-size: 4; font-size: 12px !important; line-height: 15px !important; z-index: 0; opacity: 0; overflow: hidden;">package com.example.autoparcel; import java.lang.annotation.ElementType; import java.lang.annotation.Retention; import java.lang.annotation.RetentionPolicy; import java.lang.annotation.Target; @Target(ElementType.TYPE) // 代表在类级别上才能使用该注解 @Retention(RetentionPolicy.SOURCE) // 代表该注解只存在源代码中，编译后的字节码中不存在 public @interface AutoParcel {}</textarea>

| 123456789 | package com.example.autoparcel;import java.lang.annotation.ElementType;import java.lang.annotation.Retention;import java.lang.annotation.RetentionPolicy;import java.lang.annotation.Target;@Target(ElementType.TYPE) // 代表在类级别上才能使用该注解@Retention(RetentionPolicy.SOURCE) // 代表该注解只存在源代码中，编译后的字节码中不存在public @interface AutoParcel {}  |

由于 AutoParcel 只在源代码中存在，编译后没有在字节码中，所以最最终的运行时是没有影响的。

由于这个 library 库需要在 Android 项目中引用，所以需要修改其 gradle 文件制定编译的 Java 语言版本（library/build.gradle ）：

Java <textarea wrap="soft" class="crayon-plain print-no" data-settings="dblclick" readonly="" style="tab-size: 4; font-size: 12px !important; line-height: 15px !important; z-index: 0; opacity: 0; overflow: hidden;">apply plugin: 'java' // This module will be used in Android projects, need to be // compatible with Java 1.7 sourceCompatibility = JavaVersion.VERSION_1_7 targetCompatibility = JavaVersion.VERSION_1_7 dependencies { ... }</textarea>

| 1234567891011 | apply plugin: 'java' // This module will be used in Android projects, need to be// compatible with Java 1.7sourceCompatibility = JavaVersion.VERSION_1_7targetCompatibility = JavaVersion.VERSION_1_7 dependencies {    ...}  |

## 注解处理器

注解处理器的功能就是用来读取代码中的注解然后来生成相关的代码。

创建一个 Java module 名字为 “compiler”。 该模块在编译的时候，来获取哪些类使用了 AutoParcel 注解，然后把继承这些类实现 Parcelable 的代码。该模块并不在 Android 项目中引用，只存在于编译的时候。所以这个模块的 Java 版本号可以随意指定（Java 8 、9）。

创建一个 AutoParcelProcessor 类来处理注解：

![](http://pic.goodev.org/wp-files/2016/09/1-ae1bIzz7l_PknqupuQU11g.png)

Java <textarea wrap="soft" class="crayon-plain print-no" data-settings="dblclick" readonly="" style="tab-size: 4; font-size: 12px !important; line-height: 15px !important; z-index: 0; opacity: 0; overflow: hidden;">package com.example.autoparcel.codegen; @SupportedAnnotationTypes("com.example.autoparcel.AutoParcel") public final class AutoParcelProcessor extends AbstractProcessor { @Override public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment env) { ... } }</textarea>

| 1234567891011 | package com.example.autoparcel.codegen;@SupportedAnnotationTypes("com.example.autoparcel.AutoParcel")public final class AutoParcelProcessor extends AbstractProcessor {  @Override  public boolean process(           Set<? extends TypeElement> annotations,            RoundEnvironment env) {    ...  }}  |

对于该类有几点要求：
1\. 需要继承至 AbstractProcessor
2\. 需要使用类的全称（包含包名）来指定其支持的注解类型（com.example.autoparcel.AutoParcel）
3\. 实现 process() 函数，在该函数中来处理所支持的注解类型并生成需要的代码。

下面只是介绍了实现 process() 函数的关键部分，完整代码参考最后的项目。

如果没有其他处理器需要继续处理该注解，则 process() 返回 true。针对我们这个情况，只有 AutoParcelProcessor 需要处理 AutoParcel 注解，所以该函数返回 true。

Java <textarea wrap="soft" class="crayon-plain print-no" data-settings="dblclick" readonly="" style="tab-size: 4; font-size: 12px !important; line-height: 15px !important; z-index: 1; opacity: 1; overflow: hidden; height: 668px;">package com.example.autoparcel.codegen; ... @Override public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment env) { Collection<? extends Element> annotatedElements = env.getElementsAnnotatedWith(AutoParcel.class); List<TypeElement> types = new ImmutableList.Builder<TypeElement>() .addAll(ElementFilter.typesIn(annotatedElements)) .build(); for (TypeElement type : types) { processType(type); } // 返回 true ，其他处理器不关心 AutoParcel 注解 return true; } private void processType(TypeElement type) { String className = generatedSubclassName(type); String source = generateClass(type, className); writeSourceFile(className, source, type); } private void writeSourceFile( String className, String text, TypeElement originatingType) { try { JavaFileObject sourceFile = processingEnv.getFiler(). createSourceFile(className, originatingType); Writer writer = sourceFile.openWriter(); try { writer.write(text); } finally { writer.close(); } } catch (IOException e) {// silent} } ...</textarea>

| 1234567891011121314151617181920212223242526272829303132333435363738394041424344 | package com.example.autoparcel.codegen;...@Overridepublic boolean process(      Set<? extends TypeElement> annotations,       RoundEnvironment env) {     Collection<? extends Element> annotatedElements =            env.getElementsAnnotatedWith(AutoParcel.class);    List<TypeElement> types =           new ImmutableList.Builder<TypeElement>()            .addAll(ElementFilter.typesIn(annotatedElements))            .build();     for (TypeElement type : types) {        processType(type);    }     // 返回 true ，其他处理器不关心 AutoParcel  注解    return true;}private void processType(TypeElement type) {    String className = generatedSubclassName(type);    String source = generateClass(type, className);    writeSourceFile(className, source, type);}private void writeSourceFile(        String className,         String text,         TypeElement originatingType) {    try {        JavaFileObject sourceFile =            processingEnv.getFiler().                createSourceFile(className, originatingType);        Writer writer = sourceFile.openWriter();        try {            writer.write(text);        } finally {            writer.close();        }    } catch (IOException e) {// silent}}...  |

注解处理器类编写完后，还需要创建一个 java META_INF 文件来告诉系统具有注解处理功能。Java 代码在编译的时候，系统编译器会查找所有的 META_INF 中的注册的注解处理器来处理注解。

在 Android studio 的 compiler 项目中创建如下目录：

> compiler/src/main/resources/META_INF/services

在 services 目录下面创建一个名字为 “javax.annotation.processing.Processor” 的文本文件：

![](http://pic.goodev.org/wp-files/2016/09/1-HSlUpd6fEFGaTkNuYfaaEg.png)

该文件中每行一个注解处理器的全名：

Java <textarea wrap="soft" class="crayon-plain print-no" data-settings="dblclick" readonly="" style="tab-size: 4; font-size: 12px !important; line-height: 15px !important; z-index: 0; opacity: 0; overflow: hidden;">com.example.autoparcel.codegen.AutoParcelProcessor</textarea>

| 12 | com.example.autoparcel.codegen.AutoParcelProcessor  |

这样，注解处理器就创建好了。

## 在 Android Studio 中使用

在 Android Studio 跟目录的 settings.gradle 中添加前面创建的两个模块：

Java <textarea wrap="soft" class="crayon-plain print-no" data-settings="dblclick" readonly="" style="tab-size: 4; font-size: 12px !important; line-height: 15px !important; z-index: 0; opacity: 0; overflow: hidden;">include ':app', ':compiler', ':library'</textarea>

| 12 | include ':app', ':compiler', ':library'  |

在 app/build.gradle 中添加前面创建的两个模块为依赖项：

Java <textarea wrap="soft" class="crayon-plain print-no" data-settings="dblclick" readonly="" style="tab-size: 4; font-size: 12px !important; line-height: 15px !important; z-index: 0; opacity: 0; overflow: hidden;">... dependencies { ... provided project(':library') apt project(':compiler') ... }</textarea>

| 12345678 | ...dependencies {    ...    provided project(':library')    apt project(':compiler')    ...}  |

注意上面 library 项目使用的是 provided 依赖，这是由于 provided 中的代码只在编译的时候存在，并不会打包到最终的应用中去，所以可以使用 provided。 二 compiler 项目为注解编译器，通过使用 [android-apt](https://bitbucket.org/hvisser/android-apt) 插件来指定 apt 选项。

> apt 是 Annotation Processing Tool 的缩写。

现在就可以在项目中使用 AutoParcel 注解了：

Java <textarea wrap="soft" class="crayon-plain print-no" data-settings="dblclick" readonly="" style="tab-size: 4; font-size: 12px !important; line-height: 15px !important; z-index: 0; opacity: 0; overflow: hidden;">@AutoParcel public class Foo{ ... }</textarea>

| 12345 | @AutoParcelpublic class Foo{...}  |

本文示例来源于真实的项目 [auto-parcel](https://github.com/aitorvs/auto-parcel).
使用 Auto-Parcel 就再也不需要手工编写 Parcelable 相关的代码啦。解放双手从今天开始！

本文出自 云在千峰，转载时请注明出处及相应链接。

本文永久链接: http://blog.chengyunfeng.com/?p=1021

_0_[android](http://blog.chengyunfeng.com/?tag=android), [annotation](http://blog.chengyunfeng.com/?tag=annotation) <script type="text/javascript">/*640*60 创建于 2017/6/9*/ var cpro_id = "u3001411";</script>