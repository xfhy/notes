# Android 体系与系统架构


## 1. Android系统架构

- Linux

Android最底层最核心的部分,Linux层包含了Android系统的核心服务,包括硬件驱动,进程管理,安全系统,等等

- ART/Dalvik

Dalvik包含了一整套的Android运行环境虚拟机,每个App都会分配Dalvik虚拟机来保证互相之间不受干扰,并保持独立,它的特点是在运行时编译; 而在Android 5.X版本开始,ART模式已经取代了Dalvik,ART采用的是安装时就进行编译,以后运行时就不用编译了.

- framework

	提供应用程序开发的各种API进行快速开发，以及隐藏在每个应用后面的是一系列的服务和系统，大部分使用Java编写，所谓官方源码很多也就是看这里，其中包括：
	
	- 丰富而又可扩展的视图（Views），可以用来构建应用程序， 它包括列表（lists），网格（grids），文本框（text boxes），按钮（buttons）， 甚至可嵌入的web浏览器。
	- 内容提供器（Content Providers）使得应用程序可以访问另一个应用程序的数据（如联系人数据库）， 或者共享它们自己的数据
	- 资源管理器（Resource Manager）提供 非代码资源的访问，如本地字符串，图形，和布局文件（ layout files ）。
	- 通知管理器 （Notification Manager） 使得应用程序可以在状态栏中显示自定义的提示信息。
	- 活动管理器（ Activity Manager） 用来管理应用程序生命周期并提供常用的导航回退功能。

- Standard libraries

这里包含的是Android中的一些标准库,所谓标准,就是开发者在开源环境中可以使用的开发库.

- application

Android会同一系列核心应用程序包一起发布，该应用程序包包括email客户端，SMS短消息程序，日历，地图，浏览器，联系人管理程序等。所有的应用程序都是使用JAVA语言编写的。通常开发人员就处在这一层。

## 2. 
