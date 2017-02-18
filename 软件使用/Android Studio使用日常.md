# Android Studio使用日常 

[TOC]

# 1.常用快捷键

 - `Ctrl+Alt+F` 提取全局变量(快速将局部变量变为全局)

 - `alt+enter` 这个是`Android Studio`神快捷键。如果你还认为`Alt＋Enter`键是导入包，那就大错特错了。以后有事没事就按下吧。它会根据不同的情况给出操作建议，大大提高工作效率。

 - `Ctrl+Alt+V` 快速将返回的东西定义成对象(类似于`eclipse`的`ctrl+2`)

 - `alt+shift+r`  快速重命名

 - `Ctrl+Alt+Space`:智能提示代码

 - `alt + insert`  快速实现父类的构造方法

 - `Alt+Shift+P`:实现接口方法

 - `Ctrl+delete`(或`Ctrl+Backspace`) 快速删除当前单词

 - `Ctrl+Q`  快速返回刚才编辑的地方
 
 - `Shit+Alt+M` 提取方法

 - `Ctrl+Alt+V` 提取局部变量
# 2.Logcat使用

>打开 `LogCat`在搜索框右侧的`No Filters`中选择 `Edit Filter Configuration`选项,然后在`Name`中输入过滤器的名称，在`by Package Name`中输入你的应用的`Package Name`就可以了。然后在搜索框右侧的过滤器选项中选择你刚选择过滤器就可以了。

# 3.快速生成构造函数,Setter,Getter,ToString()方法

>在类中,有两种方式：

 - 方式一：Code-->Generate
 - 方式二：通过快捷键`Alt+Insert`

# 4. 关于VersionName和VersionCode

在 Android Studio 中，对于 VersionName 和 VersionCode 的声明转移到了 Module 的build.gradle文件中。请修改build.gradle 的以下部分

		defaultConfig {
	        minSdkVersion 16
	        targetSdkVersion 21
	        versionCode 2
	        versionName "2.0"
	    }