> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/qq_27570955/article/details/55046934 版权声明：本文为博主原创文章，未经博主允许不得转载。如有问题，请与我联系 (QQ：3290985311) 朱小姐。 https://blog.csdn.net/qq_27570955/article/details/55046934

 * 本篇文章已授权微信公众号 guolin_blog （郭霖）独家发布 

**1、public void onConfigurationChanged(Configuration newConfig) 方法介绍**

newConfig：新的设备配置信息

当系统的配置信息发生改变时，系统会调用此方法。注意，只有在配置文件 AndroidManifest 中处理了 configChanges 属性对应的设备配置，该方法才会被调用。如果发生设备配置与在配置文件中设置的不一致，则 Activity 会被销毁并使用新的配置重建。

例如：

当屏幕方向发生改变时，Activity 会被销毁重建，如果在 AndroidManifest 文件中处理屏幕方向配置信息如下

![](https://img-blog.csdn.net/20170214161345816?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjc1NzA5NTU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

则 Activity 不会被销毁重建，而是调用 onConfigurationChanged 方法。

如果 configChanges 只设置了 orientation，则当其他设备配置信息改变时，Activity 依然会销毁重建，且不会调用 onConfigurationChanged。

例如，在上面的配置的情况下，如果语言改变了，Acitivyt 就会销毁重建，且不会调用 onConfigurationChanged 方法。

**2、configChanges 设置取值**

![](https://img-blog.csdn.net/20170213121232491?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjc1NzA5NTU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

**注意：横竖屏切换的属性是 orientation。如果 targetSdkVersion 的值大于等于 13，则如下配置才会回调 onConfigurationChanged 方法**

<pre> android:configChanges="orientation|screenSize"
</pre>

如果 targetSdkVersion 的值小于 13，则只要配置

<pre>  android:configChanges="orientation"
</pre>

就可以了。

网上有很多文章写说横竖屏切换时 onConfigurationChanged 方法没有调用，使用如下的配置

<pre>android:configChanges="orientation|keyboard|keyboardHidden"
</pre>

但是！！！其实查官方文档，只要配置 android:configChanges="orientation|screenSize" 就可以了。

**扩展：**

当用户接入一个外设键盘时，默认软键盘会自动隐藏，系统自动使用外设键盘。这个过程 Activity 的销毁和隐藏执行了两次。并且 onConfigurationChanged() 周期不会调用。

但是在配置文件中设置 android:configChanges="keyboardHidden|keyboard"。当接入外设键盘或者拔出外设键盘时，调用的周期是先调用 onConfigurationChanged() 周期后销毁重建。

在这里有一个疑点，为什么有两次的销毁重建？

其中一次的销毁重建可以肯定是因为外设键盘的插入和拔出。当设置 android:configChanges="keyboardHidden|keyboard" 之后，就不会销毁重建，而是调用 onConfigurationChanged() 方法。

但是还有一次销毁重建一直存在。

经过测试，当接入外设键盘时，除了键盘类型的改变，触摸屏也发生了变化。因为使用外设键盘，触摸屏不能使用了。（如果是接入触摸板，不知道会不会有这个问题？欢迎大家提供意见)。这里，我接入的是键盘，所以触摸屏不能使用了。

**总结：**

如果是键盘类型发生了改变，则 configChanges 属性配置如下 Activity 才不会销毁重建，且回调 onConfigurationChanged 方法

![](https://img-blog.csdn.net/20170214164630166?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjc1NzA5NTU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

**note: 这里的外置物理键盘可以是游戏手柄、扫描枪、键盘等等。**

**总结：**

设备配置的更改会导致 Acitivity 销毁重建，而设置 android:configChanges 则避免 Activity 销毁重建，系统会回调 onConfigurationChanged 方法。

官方文档：

https://developer.android.com/guide/topics/manifest/activity-element.html