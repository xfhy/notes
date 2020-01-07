> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://zhuanlan.zhihu.com/p/79018661

AndroidKiller 使用与配置

1. 下载 AndroidKiller 工具

虽然 APK 属于压缩文件，但是 APK 包中的 AndroidManifest.xml 等文件无法通过直接解压的方式获取内容，需要通过 Apktool 工具进行反编译。在这里需要用到我们常用的反编译工具 AndroidKiller，可以到我分享的百度网盘链接 [http://pan.baidu.com/s/1dFzYtA5](https://link.zhihu.com/?target=http%3A//pan.baidu.com/s/1dFzYtA5) 密码：2gep 下载，下载解压后的目录结构如图所示。

![](https://pic4.zhimg.com/v2-41d8befbcd90bed44a0c33b76ab6182f_b.jpg)![](https://pic4.zhimg.com/v2-41d8befbcd90bed44a0c33b76ab6182f_r.jpg)

1. 配置 JDK 路径

（1） 解压后第一次打开 AndroidKiller 提示没有 JDK 环境，如图所示，不用管直接点击 “OK”。

![](https://pic2.zhimg.com/v2-01d7dccf43fa707d383b648aaf4d854d_b.jpg)![](https://pic2.zhimg.com/80/v2-01d7dccf43fa707d383b648aaf4d854d_hd.jpg)

（2） 打开 AndroidKiller 后，选中 “主页” 菜单栏下的 “配置” 选项，  
点击 “配置” 后弹出弹窗，点击 “Java” 图标配置 JDK 所在安装路径，如图所示。

![](https://pic4.zhimg.com/v2-9c460d2e4daae90622d38d320115f0ef_b.jpg)![](https://pic4.zhimg.com/v2-9c460d2e4daae90622d38d320115f0ef_r.jpg)

2. 解决 APP 反编译失败问题

（1） 由于 AndroidKiller 工具长年没有进行更新，导致很多 APP 无法反编译，需要手动更新 apktool 工具。打开 AndroidKiller 工具选中 “Android” 选项如图所示。

![](https://pic1.zhimg.com/v2-4167b4ea124bbea69922e6fa471d91f8_b.jpg)![](https://pic1.zhimg.com/v2-4167b4ea124bbea69922e6fa471d91f8_r.jpg)

(2). 下载新版 apktool 工具，点击添加按钮添加下载好的 apktool 工具，如图所示

![](https://pic1.zhimg.com/v2-60a033dede27300e5b1e10302975cdb8_b.jpg)![](https://pic1.zhimg.com/v2-60a033dede27300e5b1e10302975cdb8_r.jpg)

（3） 添加成功后，选择新添加的 apktool 工具，如图所示。

![](https://pic1.zhimg.com/v2-20873774fd4ba3c7305d9419e0c57ca8_b.jpg)![](https://pic1.zhimg.com/v2-20873774fd4ba3c7305d9419e0c57ca8_r.jpg)

(4). 解决 APP 回编译失败问题在回编译火柴人 APP 时报错了，如图所示。

![](https://pic3.zhimg.com/v2-8cee61a7731699d905c66276e545501e_b.jpg)![](https://pic3.zhimg.com/v2-8cee61a7731699d905c66276e545501e_r.jpg)

出现这个问题的原因是 AndroidKiller 很长是时间没有更新了。

解决这个问题只需要在 apktool 工具所在路径执行该命令：

java –jar apktool_2.3.4.jar empty-framework-dir

5. 更新 smali 插桩插件

打开 AndroidKiller 安装包所在位置，找到 cfgs 文件夹下的 injectcode 文件夹，将里面的插件删除。替换效果如下：

![](https://pic3.zhimg.com/v2-ef86ea232f4bdba48754c6bc127c39ce_b.jpg)![](https://pic3.zhimg.com/v2-ef86ea232f4bdba48754c6bc127c39ce_r.jpg)写下你的评论...