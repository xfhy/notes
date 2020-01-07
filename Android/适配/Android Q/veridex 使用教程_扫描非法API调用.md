
## 1. 限制非SDK接口背景

从Android P开始,谷歌就开始限制开发者,禁止反射调用系统的一些方法,特别是谷歌给出的黑名单里面的方法.当然,这是为了提升开发者体验和增强APP稳定,更是为了安卓的生态发展.

那些官方限制的都是一些非SDK接口,就是一些private的或者是hide的方法或字段,这些因为是没有公开的,可能会在某个版本就消失了.所以不应该访问这些SDK中未列出的方法或字段.


## 2. 下载扫描工具

好了,废话不多说.最近公司为了适配Android Q,其实都还好,还是比较好适配的.什么存储权限,定位,唯一码.不清楚的看[这里](https://juejin.im/post/5cad5b7ce51d456e5a0728b0).

本文主要是教大家如何使用veridex.这个工具可以帮我们查找出一个APK中有哪些地方使用了非SDK接口.主要是项目如果特别大的话,查找哪里调用了非SDK接口非常麻烦,,而且最麻烦的是第三方SDK中的一些骚操作,你不知道它干了啥,也不能改代码,只能通过工具来扫描.

- [官方下载地址](https://android.googlesource.com/platform/prebuilts/runtime/+/master/appcompat)
- 百度网盘下载链接,更方便地下载(失效了,记得在下方评论区留言)：https://pan.baidu.com/s/1MnpHXoSSdfNYwgBTDRrmTg 
提取码：ynyy 

## 3. 开始查找非SDK接口调用

在官方的工具中,分别提供了linux,mac,windows的工具.veridex-linux是给linux用的,veridex-mac是给mac用的.

![image](A4CEF0F384204875950B3AB1BA5DD1C6)

如果你是linux或者macOS还好,直接用就行(下面会介绍使用方式).如果是windows,则必须是Windows 10,而且必须打开WSL,具体教程看[这里](https://docs.microsoft.com/en-us/windows/wsl/install-win10);

好了,现在准备工作OK了,不管你是什么系统,要使用工具,首先需要进入veridex目录,使用下面的命令行方式.

```
./appcompat.sh --dex-file=test.apk
```

test.apk是你的apk名称,尽量把apk放到veridex工具的同一目录下.执行上面的命令后,会得到一些输出,但是这些输出是在命令行上的,一晃而过,不好观察.我们使用下面的命令行将其扫描内容输出到文件中

```
./appcompat.sh --dex-file=test.apk >> happy.txt
```

当然也可以加一个`--imprecise`参数,加这个参数是为了让输出内容更加详细,但是也可能存在误报.我在使用过程中,发现很多误报,明明没什么问题的,结果报出来了.

```
./appcompat.sh --dex-file=test.apk --imprecise >> happy2.txt
```

扫描出来的内容大概如下所示:

```
......

#62: Linking blacklist Ljunit/textui/TestRunner;->fPrinter:Ljunit/textui/ResultPrinter; use(s):
       Ljunit/textui/TestRunner;-><init>(Ljunit/textui/ResultPrinter;)V
       Ljunit/textui/TestRunner;->doRun(Ljunit/framework/Test;Z)Ljunit/framework/TestResult;
       Ljunit/textui/TestRunner;->doRun(Ljunit/framework/Test;Z)Ljunit/framework/TestResult;
       Ljunit/textui/TestRunner;->pause(Z)V
       //表示junit.textui.TestRunner的setPrinter方法里面在调用非SDK接口
       Ljunit/textui/TestRunner;->setPrinter(Ljunit/textui/ResultPrinter;)V
.......

//这种是灰名单,不用管
#71: Reflection greylist Lsun/misc/Unsafe;->theUnsafe use(s):
       Lcom/bonree/gson/internal/UnsafeAllocator;->create()Lcom/bonree/gson/internal/UnsafeAllocator;

71 hidden API(s) used: 4 linked against, 67 through reflection
       57 in greylist
       28 in blacklist
       0 in greylist-max-o
       14 in greylist-max-p
To run an analysis that can give more reflection accesses, 
but could include false positives, pass the --imprecise flag. 
```

上面是一个简单示例,可以看到我这里扫描的时候有28处黑名单(公司项目中不会有这么多的,放心,不然得累死).

从上面的文件中我们读取到如下信息:

- 调用某个API是否非法
- 该方法限制级别是什么
- 调用该方法的位置是哪里

**在Android Q版本,为了更精准的控制与兼容,对非SDK接口分类进行修改,修改后如下所示:**

* whitelist 白名单 可随意调用
* greylist 灰名单  警告
* greylist-max-o  targetSDK>=O时不允许调用,targetSDK<O时警告
* greylist-max-p targetSDK>=P时不允许调用,targetSDK<P时警告
* blacklist 黑名单 不允许调用

由于我上面是测试,我将Junit的引入方法改为api,平时应该这样写`testImplementation 'junit:junit:4.12'`.这样在打包的时候就会将Junit也打进去,扫描的时候也会把Junit里面的东西扫出来,就会出现很多黑名单里面的API.

这个时候我们需要根据这个扫描出来的文件去项目中,一个一个地找黑名单调用处.如果是自己的代码,则自己修改,使用SDK公开的方法,别反射.如果是第三方SDK中的代码,则给该SDK提工单.因为三方SDK一般公司用的时候都是给了钱的,提工单的时候回复还是比较快的,解决也很快.

因为我在公司不是用的macOS,也是我装了个虚拟机Ubuntu,把上面的东西跑出来,然后一个一个地将项目中的黑名单移除.还好工程量不大,哈哈,开心.
