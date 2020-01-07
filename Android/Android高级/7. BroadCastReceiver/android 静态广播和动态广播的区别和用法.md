> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/u011897782/article/details/81119231

一、什么是广播

BroadcastReceiver 是 android 系统的四大组件之一，本质上就是一个全局的监听器，用于监听系统全局的广播消息，可以方便的实现系统中不同组件之间的通信。

程序可以通过调用 context 的 sendBroadcast() 方法来启动指定的 BroadcastReceiver.

二、广播的生命周期

BroadcastReceiver 生命周期只有十秒左右，如果在 onReceive() 内做超过十秒的事情，就会报错。所以广播中不要执行耗时操作，可以考虑启动一个 Service 来完成操作。

三、注册 BroadcastReceiver

广播分为两种：静态注册和动态注册

1\. 静态注册

AndroidManifest.xml 文件中配置

特点：常驻形广播，程序推出后，广播依然存在。

示例：创建广播，新建一个类，继承自 BroadcastReceiver, 并重写 onReceive() 方法，在 manifest 文件中注册该广播，在发送广播。

![](https://img-blog.csdn.net/20180719172655939?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE4OTc3ODI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

![](https://img-blog.csdn.net/20180719172709261?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE4OTc3ODI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

2\. 动态注册

代码中动态指定广播地址并注册

特点：非常驻型，广播会跟随程序的生命周期的结束而结束。

示例：新建内部类，继承 BroadcastReceiver, 并重写 onReceive() 方法，在 onStart（）中注册广播，在 onStop() 中解除注册广播，在发送广播

![](https://img-blog.csdn.net/20180719172957268?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE4OTc3ODI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

![](https://img-blog.csdn.net/20180719173011628?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE4OTc3ODI=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)