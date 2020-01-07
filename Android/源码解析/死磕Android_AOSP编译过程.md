

一直有个小心愿，想要了解以及调试Android源码。为了实现这个目标，我经历了种种坎坷，花了一个多周才搞定了。最近家里的电脑一直没怎么关机，一直在给我跑AOSP。公司最近在天天加班，所以一天只有一次试错的机会，出错了只能晚上回来继续解决问题。源码实在是太大了，我这个Android P的代码刚下载下来的时候差不多有60G左右吧。

## 1. 准备工作

1. 一块大一点儿的硬盘,至少得有200G剩余空间. 
2. CPU尽量好点的,这样编译快.
3. 网速越快越好,不然你想想60G啊，要下载到什么时候...
4. 系统最好是Ubuntu或者mac OS,官方是这样推荐的. 

为此我还专门买了一块2T的硬盘,学习得舍得花钱.我的那个CPU不是很好,编译特别特别慢,每次编译都是一个整整一个晚上.网速有多快搞多快.

## 2. 系统安装

> 如果你当前使用的系统已经是mac OS或者是Ubuntu,那么可以跳过这一节.

系统安装之前需要准备Ubuntu系统镜像  我是下载的mint.因为个人原因,之前用过mint一段时间,所以这次使用的是mint,其实和Ubuntu一样的.官方原话: Linux Mint 是一款基于 Ubuntu 与 Debian 开发的 Linux 操作系统发行,被很多爱好者誉为“最好的桌面应用系统”,就像是 Linux 世界中的 macOS,非常适合个人日常办公或开发电脑使用.

[mint下载地址](https://www.linuxmint.com/download.php)

我之前是用的Windows 10,于是我下载了mint之后开始用VirtualBox安装该Linux系统..这个没啥好说的,一路next,然后内存最好是4G,硬盘空间给它200G. 如果需要看教程可以参考[这里](https://jingyan.baidu.com/article/7f766daff541cd4101e1d0cd.html).如果你对VMware比较喜欢,也可以用VMware安装虚拟机,安装过程是差不多的.

我是安装的真机....没错是那种真实的系统,我的Windows 10是在原来的那块SSD上,新的Linux系统是安装在新的2T机械硬盘上,我以为安装之后开机时引导会有问题,但是还好,居然没出问题,哈哈哈哈哈.

安装Linux的时候,需要先下载ultraiso软件,制作U盘启动盘.制作过程参考[这里](https://jingyan.baidu.com/article/f3ad7d0f013f6f09c3345bf7.html).写入方式记得选"RAW".然后重启电脑,U盘设置为第一启动项.之后的安装过程就是一路next,记得选择和Windows并存.最好是别切换语言,就用English,这样的条件下系统默认创建的文件夹是英文的,用着舒服.详细安装过程看[这里](https://jingyan.baidu.com/article/f3ad7d0f013f6f09c3345bf7.html)

安装完系统之后,进入系统设置,可以看到底部有一个软件源,进入之后将源换成中国的.

![image.png](WEBRESOURCE607d901bfc0ef345e403f8fce41cc0ad)

![image.png](WEBRESOURCEaf503fb1a380dba87c0ef8001e36482b)


## 3. 开始下载AOSP

首先需要安装Git,因为源码是用Git管理的.

```
sudo apt-get install git
```

接下来创建一个bin文件夹,并加入到PATH中,有点像Windows的环境变量.
```
mkdir ~/bin
PATH=~/bin:$PATH
```

安装curl下载的库：

```
sudo apt-get install curl
```

下载repo并设置权限：
```
curl https://mirrors.tuna.tsinghua.edu.cn/git/git-repo > ~/bin/repo
chmod a+x ~/bin/repo
```

然后找个空旷的地方,创建一个AOSP文件夹,待会儿需要把源码下载到这里:

然后运行下面这句话,添加源.这里使用的是清华的源.
```
export REPO_URL='https://mirrors.tuna.tsinghua.edu.cn/git/git-repo/'
```

然后初始化Git,邮箱和姓名：
```
git config --global user.email "xxx@gmail.com"
git config --global user.name "xxx"
```

初始化仓库：
```
repo init -u https://aosp.tuna.tsinghua.edu.cn/platform/manifest
```

初始化并指定版本：
```
repo init -u https://aosp.tuna.tsinghua.edu.cn/platform/manifest -b android-9.0.0_r8
```

开始同步源码:
```
repo sync
```

等待源码下载完成后，注意,这个过程千万不能断网,也不能关机,也不能让电脑睡眠啥的.下载源码很费时间,,,,

## 4. 开始编译AOSP

首先需要安装jdk
```
sudo apt-get update
sudo apt-get install openjdk-8-jdk
```

然后进入AOSP文件夹,

```
source build/envsetup.sh
// 编译前删除build文件夹
make clobber
```

这里我选择编译开发工程师的版本,可以方便debug

```
lunch aosp_x86-eng
```

然后开始编译

```
make -j6
```

直到看到下面的log

![image.png](WEBRESOURCE90047184db5e7a897d7937d94955e495)

我这里是下载了10个多小时.终于下载好了......对于电脑来说,又是一个不眠之夜

运行模拟器
在编译完成之后,就可以通过以下命令运行Android虚拟机了，命令如下:
```
source build/envsetup.sh
lunch 5
emulator
```
如果是在编译完后运行虚拟机，由于之前已经执行过source和lunch命令了，可以直接运行：
```
emulator
```

最后的最后,,我看到了胜利的曙光

![image.png](WEBRESOURCEd85ff21bc19a93f6df2d643828144be5)


## 感谢

- [刘皇叔的总结](http://liuwangshu.cn/tags/AOSP%E5%9F%BA%E7%A1%80/)