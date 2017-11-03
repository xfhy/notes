# Android免root查看数据库内容

> 用Android Studio 的一个小工具

## 1.首先需要Android Studio 3.0

还没有升级AS 3.0的小伙伴赶快升级啦.
AS升级方式:菜单栏Help->Check for Updates

![](http://olg7c0d2n.bkt.clouddn.com/17-11-3/77167082.jpg)

## 2.电脑插入手机

将手机打开开发者模式,打开USB调试.这里不同手机打开方式可能不同,具体的请自行百度.装上手机USB驱动,这里可以使用豌豆荚或者应用宝都行.

## 3.在AS右下角找到Device File Explorer

点击打开后,可以看到自己手机的文件信息,如下所示:
![](http://olg7c0d2n.bkt.clouddn.com/17-11-3/85444486.jpg)

如果没有显示文件信息,可能是手机驱动没装好,或者是USB调试未打开.

打开目录data/data/自己应用的包名/databases

在这个目录下存放着自己应用的数据库文件(xx.db),在需要查看的数据库文件上右键保存,保存到电脑硬盘上.
![](http://olg7c0d2n.bkt.clouddn.com/17-11-3/52990305.jpg)

## 4.下载Sqlite可视化工具

这里我使用的是SQLite Expert Personal 4,下载地址我就不贴了,百度有很多.

用可视化工具查看SqLite文件比较方便,使用方式我也不再赘述了,相信大家早就会了.

## 总结

我的测试机是vivo的,哇...根本root不了啊,然后不root查看数据库真的麻烦啊.之前用了一个三方控件,可以在浏览器上查看数据库数据,但是比较麻烦.今天终于找到简单方法了,而且非常方便.Android Studio 果然强大,点赞.
