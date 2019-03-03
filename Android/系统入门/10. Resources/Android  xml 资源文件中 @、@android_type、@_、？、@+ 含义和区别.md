> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/mingli198611/article/details/7105850 <link rel="stylesheet" href="https://csdnimg.cn/release/phoenix/template/css/ck_htmledit_views-f57960eb32.css"> 

**Android  资源文件中 @、@android:type、@*、？、@+ 含义和区别**

**一.@代表引用资源**

**1\. 引用自定义资源。格式：@[package:]type/name**

android：text="@string/hello"

**2\. 引用系统资源。格式：@android:type/name**

    android:textColor="@android:color/opaque_red"

  注意：其实 **@android:type/name 是 @[package:]type/name 的一个子类**

**二.@* 代表引用系统的非 public 资源。格式：@*android:type/name**

系统资源定义分 public 和非 public。public 的声明在：

  <sdk_path>\platforms\android-8\data\res\values\public.xml

  **@*android:type/name：**可以调用系统定义的所有资源

**  @android:type/name：**只能够调用 publi 属性的资源。

  注意：没在 public.xml 中声明的资源是 google 不推荐使用的。

**三.？代表引用主题属性**

  另外一种资源值允许你引用当前主题中的属性的值。这个属性值只能在 style 资源和 XML 属性中使用；它允许你通过将它们改变为当前主题提供的标准变化来改变 UI 元素的外观，而不是提供具体的值。例如：

  android:textColor="?android:textDisabledColor" 

   注意，这和资源引用非常类似，除了我们使用一个 "?" 前缀代替了 "@"。当你使用这个标记时，你就提供了属性资源的名称，它将会在主题中被查找，所以你不需要显示声明这个类型 (如果声明，其形式就是? android:attr/android:textDisabledColor)。除了使用这个资源的标识符来查询主题中的值代替原始的资源，其命名语法和 "@" 形式一致：?[namespace:]type/name，这里类型可选。

 四**.@+ 代表在创建或引用资源 。格式：@+type/name**

    含义：”+” 表示在 R.java 中名为 type 的内部类中添加一条记录。如 "@+id/button" 的含义是在 R.java 文件中的 id 这个静态内部类添加一条常量名为 button。该常量就是该资源的标识符。如果标示符（包括系统资源）已经存在则表示引用该标示符。最常用的就是在定义资源 ID 中，例如：

 @+id / 资源 ID 名         新建一个资源 ID

 @id / 资源 ID 名          应用现有已定义的资源 ID，包括系统 ID

 @android:id / 资源 ID 名   引用系统 ID，其等效于 @id / 资源 ID 名

 android:id="@+id/selectdlg"

 android:id="@android:id/text1"

 android:id="@id/button3"