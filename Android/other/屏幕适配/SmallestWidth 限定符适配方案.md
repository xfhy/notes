> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5ba197e46fb9a05d0b142c62

原文地址: https://www.jianshu.com/p/2aded8bb6ede

以下是 **骚年你的屏幕适配方式该升级了!** 系列文章，欢迎转发以及分享:

*   [骚年你的屏幕适配方式该升级了!（一）- 今日头条适配方案](https://juejin.im/post/5b7a29736fb9a019d53e7ee2)
*   [骚年你的屏幕适配方式该升级了!（二）-smallestWidth 限定符适配方案](https://juejin.im/post/5ba197e46fb9a05d0b142c62)
*   [今日头条屏幕适配方案终极版正式发布!](https://juejin.im/post/5bce688e6fb9a05cf715d1c2)

扫描或点击以下二维码，加入技术交流 QQ 群 455850365

[![](https://user-gold-cdn.xitu.io/2018/10/30/166c39d6ff062b92?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)](https://link.juejin.im?target=https%3A%2F%2Fshang.qq.com%2Fwpa%2Fqunwpa%3Fidkey%3Dab3be932171c71fefc36a8a05f972d0ddfc80dd1e47e91db9eab9da65b4e86e7)

前言
==

**ok**，根据上一篇文章 [骚年你的屏幕适配方式该升级了!- 今日头条适配方案](https://link.juejin.im?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2F55e0fca23b4f) 的承诺，本文是这个系列的第二篇文章，这篇文章会详细讲解 **smallestWidth 限定符屏幕适配方案**

了解我的朋友一定知道，[MVPArms](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FJessYanCoding%2FMVPArms) 一直使用的是 **鸿神** 的 **AndroidAutoLayout** 屏幕适配方案，得益于 **AndroidAutoLayout** 的便捷，所以我对屏幕适配领域研究的不是很多，**AndroidAutoLayout** 停止维护后，我也一直在找寻着替代方案，直到 **今日头条屏幕适配方案** 刷屏，后来又无意间看到了 **smallestWidth 限定符屏幕适配方案**，这才慢慢的将研究方向转向了屏幕适配领域

最近一个月才开始慢慢恶补 **Android** 屏幕适配的相关知识，对这两个方案也进行了更深入的研究，可以说从一个小白慢慢成长而来，所以我明白小白的痛，因此在上一篇文章 [骚年你的屏幕适配方式该升级了!- 今日头条适配方案](https://link.juejin.im?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2F55e0fca23b4f) 中，把 **今日头条屏幕适配方案** 讲得非常的细，尽量把每一个知识点都描述清晰，深怕小白漏掉每一个细节，这篇文章我也会延续上一篇文章的优良传统，将 **smallestWidth 限定符屏幕适配方案** 的每一个知识点都描述清晰

顺便说一句，感谢大家对 [AndroidAutoSize](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FJessYanCoding%2FAndroidAutoSize%2Fblob%2Fmaster%2FREADME-zh.md) 的支持，我只是在上一篇文章中提了一嘴我刚发布的屏幕适配框架 [AndroidAutoSize](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FJessYanCoding%2FAndroidAutoSize%2Fblob%2Fmaster%2FREADME-zh.md)，还没给出详细的介绍和原理剖析 (原计划在本系列的第三篇文章中发布)，[AndroidAutoSize](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FJessYanCoding%2FAndroidAutoSize%2Fblob%2Fmaster%2FREADME-zh.md) 就被大家推上了 [Github Trending](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Ftrending%2Fjava%3Fsince%3Ddaily)，一个多星期就拿了 **2k+ stars**，随着关注度的增加，我在这段时间里也累坏了，**issues** 就没断过，不到半个月就提交了 **200** 多次 **commit**，但累并快乐着，在这里要再次感谢大家对 [AndroidAutoSize](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FJessYanCoding%2FAndroidAutoSize%2Fblob%2Fmaster%2FREADME-zh.md) 的认可

谈谈对百分比库的看法
==========

是这样的，在上篇文章中有一些兄弟提了一些观点，我很是认同，但是我站在执行者的角度来看待这个问题，也有一些不同的观点，以下是我在上篇文章中的回复

![](https://user-gold-cdn.xitu.io/2018/9/19/165efda831b048ed?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

**大家要注意了！这些观点其实针对的是所有以百分比缩放布局的库，而不只是今日头条屏幕适配方案，所以这些观点也同样适用于 smallestWidth 限定符屏幕适配方案，这点有很多人存在误解，所以一定要注意！**

![](https://user-gold-cdn.xitu.io/2018/9/19/165efda83345a4b5?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

上图的每一个方框都代表一种 **Android** 设备的屏幕，**Android** 的 **系统碎片化**、**机型以及屏幕尺寸碎片化**、**屏幕分辨率碎片化** 有多严重大家可以通过 **友盟指数** 了解一下，有些时候在某些事情的决断标准上，并不能按照事情的对错来决断，大多数情况还是要分析成本，收益等多种因素，通过利弊来决断，每个人的利弊标准又都不一样，所以每个人的观点也都会有差别，但也都应该得到尊重，所以我只是说说自己的观点，也不否认任何人的观点

方案是死的人是活的，在某些大屏手机或平板电脑上，您也可以采用其他适配方案和百分比库结合使用，比如针对某个屏幕区间的设备单独出一套设计图以显示比小屏幕手机更多更精细的内容，来达到与百分比库互补的效果，**没有一个方案可以说自己是完美的，但我们能清晰的认识到不同方案的优缺点，将它们的优点相结合，才能应付更复杂的开发需求，产出最好的产品**

**友情提示:** 下面要介绍的 **smallestWidth 限定符屏幕适配方案**，原理也同样是按照百分比缩放布局，理论上也会存在上面所说的 **大屏手机和小屏手机显示的内容相同** 的问题，选择与否请仔细斟酌

简介 smallestWidth 限定符适配方案
========================

这个方案的的使用方式和我们平时在布局中引用 **dimens** 无异，核心点在于生成 **dimens.xml** 文件，但是已经有大神帮我们做了这 [一步](https://link.juejin.im?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2F1302ad5a4b04)

```
├── src/main
│   ├── res
│   ├── ├──values
│   ├── ├──values-800x480
│   ├── ├──values-860x540
│   ├── ├──values-1024x600
│   ├── ├──values-1024x768
│   ├── ├──...
│   ├── ├──values-2560x1440
复制代码

```

如果有人还记得上面这种 **宽高限定符屏幕适配方案** 的话，就可以把 **smallestWidth 限定符屏幕适配方案** 当成这种方案的升级版，**smallestWidth 限定符屏幕适配方案** 只是把 **dimens.xml** 文件中的值从 **px** 换成了 **dp**，原理和使用方式都是没变的，这些在上面的文章中都有介绍，下面就直接开始剖析原理，**smallestWidth 限定符屏幕适配方案** 长这样👇

```
├── src/main
│   ├── res
│   ├── ├──values
│   ├── ├──values-sw320dp
│   ├── ├──values-sw360dp
│   ├── ├──values-sw400dp
│   ├── ├──values-sw411dp
│   ├── ├──values-sw480dp
│   ├── ├──...
│   ├── ├──values-sw600dp
│   ├── ├──values-sw640dp
复制代码

```

原理
==

其实 **smallestWidth 限定符屏幕适配方案** 的原理也很简单，开发者先在项目中根据主流屏幕的 **最小宽度 (smallestWidth)** 生成一系列 **values-sw<N>dp** 文件夹 (含有 **dimens.xml** 文件)，当把项目运行到设备上时，系统会根据当前设备屏幕的 **最小宽度 (smallestWidth)** 去匹配对应的 **values-sw<N>dp** 文件夹，而对应的 **values-sw<N>dp** 文件夹中的 **dimens.xml** 文字中的值，又是根据当前设备屏幕的 **最小宽度 (smallestWidth)** 而定制的，所以一定能适配当前设备

如果系统根据当前设备屏幕的 **最小宽度 (smallestWidth)** 没找到对应的 **values-sw<N>dp** 文件夹，则会去寻找与之 **最小宽度 (smallestWidth)** 相近的 **values-sw<N>dp** 文件夹，系统只会寻找小于或等于当前设备 **最小宽度 (smallestWidth)** 的 **values-sw<N>dp**，这就是优于 **宽高限定符屏幕适配方案** 的容错率，并且也可以少生成很多 **values-sw<N>dp** 文件夹，减轻 **App** 的体积

什么是 smallestWidth
-----------------

**smallestWidth** 翻译为中文的意思就是 **最小宽度**，那这个 **最小宽度** 是什么意思呢？

系统会根据当前设备屏幕的 **最小宽度** 来匹配 **values-sw<N>dp**，为什么不是根据 **宽度** 来匹配，而要加上 **最小** 这两个字呢？

这就要说到，移动设备都是允许屏幕可以旋转的，当屏幕旋转时，屏幕的高宽就会互换，加上 **最小** 这两个字，是因为这个方案是不区分屏幕方向的，它只会把屏幕的高度和宽度中值最小的一方认为是 **最小宽度**，这个 **最小宽度** 是根据屏幕来定的，是固定不变的，意思是不管您怎么旋转屏幕，只要这个屏幕的高度大于宽度，那系统就只会认定宽度的值为 **最小宽度**，反之如果屏幕的宽度大于高度，那系统就会认定屏幕的高度的值为 **最小宽度**

如果想让屏幕宽度随着屏幕的旋转而做出改变该怎么办呢？可以再根据 **values-w<N>dp** (去掉 **sw** 中的 **s**) 生成一套资源文件

如果想区分屏幕的方向来做适配该怎么办呢？那就只有再根据 **屏幕方向限定符** 生成一套资源文件咯，后缀加上 **-land** 或 **-port** 即可，像这样，**values-sw400dp-land (最小宽度 400 dp 横向)**，**values-sw400dp-port (最小宽度 400 dp 纵向)**

smallestWidth 的值是怎么算的
---------------------

要先算出当前设备的 **smallestWidth** 值我们才能知道当前设备该匹配哪个 **values-sw<N>dp** 文件夹

ok，还是按照上一篇文章的叙述方式，现在来举栗说明，帮助大家更好理解

我们假设设备的屏幕信息是 **1920 * 1080**、**480 dpi**

根据上面的规则我们要在屏幕的高度和宽度中选择值最小的一方作为最小宽度，**1080 < 1920**，明显 **1080 px** 就是我们要找的 **最小宽度** 的值，但 **最小宽度** 的单位是 **dp**，所以我们要把 **px** 转换为 **dp**

帮助大家再巩固下基础，下面的公式一定不能再忘了！

**px / density = dp**，**DPI / 160 = density**，所以最终的公式是 **px / (DPI / 160) = dp**

所以我们得到的 **最小宽度** 的值是 **360 dp (1080 / (480 / 160) = 360)**

现在我们已经算出了当前设备的最小宽度是 **360 dp**，我们晓得系统会根据这个 **最小宽度** 帮助我们匹配到 **values-sw360dp** 文件夹下的 **dimens.xml** 文件，如果项目中没有 **values-sw360dp** 这个文件夹，系统才会去匹配相近的 **values-sw<N>dp** 文件夹

**dimens.xml** 文件是整个方案的核心所在，所以接下来我们再来看看 **values-sw360dp** 文件夹中的这个 **dimens.xml** 是根据什么原理生成的

dimens.xml 生成原理
---------------

因为我们在项目布局中引用的 **dimens** 的实际值，来源于根据当前设备屏幕的 **最小宽度** 所匹配的 **values-sw<N>dp** 文件夹中的 **dimens.xml**，所以搞清楚 **dimens.xml** 的生成原理，有助于我们理解 **smallestWidth 限定符屏幕适配方案**

说到 **dimens.xml** 的生成，就要涉及到两个因数，第一个因素是 **最小宽度基准值**，第二个因素就是您的项目需要适配哪些 **最小宽度**，通俗理解就是需要生成多少个 **values-sw<N>dp** 文件夹

### 第一个因素

**最小宽度基准值** 是什么意思呢？简单理解就是您需要把设备的屏幕宽度分为多少份，假设我们现在把项目的 **最小宽度基准值** 定为 **360**，那这个方案就会理解为您想把所有设备的屏幕宽度都分为 **360** 份，方案会帮您在 **dimens.xml** 文件中生成 **1** 到 **360** 的 **dimens** 引用，比如 **values-sw360dp** 中的 **dimens.xml** 是长这样的

```
<?xml version="1.0" encoding="UTF-8"?>
<resources>
	<dimen >1dp</dimen>
	<dimen >2dp</dimen>
	<dimen >3dp</dimen>
	<dimen >4dp</dimen>
	<dimen >5dp</dimen>
	<dimen >6dp</dimen>
	<dimen >7dp</dimen>
	<dimen >8dp</dimen>
	<dimen >9dp</dimen>
	<dimen >10dp</dimen>
	...
	<dimen >356dp</dimen>
	<dimen >357dp</dimen>
	<dimen >358dp</dimen>
	<dimen >359dp</dimen>
	<dimen >360dp</dimen>
</resources>
复制代码

```

**values-sw360dp** 指的是当前设备屏幕的 **最小宽度** 为 **360dp** (该设备高度大于宽度，则最小宽度就是宽度，所以该设备宽度为 **360dp**)，把屏幕宽度分为 **360** 份，刚好每份等于 **1dp**，所以每个引用都递增 **1dp**，值最大的 **dimens** 引用 **dp_360** 值也是 **360dp**，刚好覆盖屏幕宽度

下面再来看看将 **最小宽度基准值** 定为 **360** 时，**values-sw400dp** 中的 **dimens.xml** 长什么样

```
<?xml version="1.0" encoding="UTF-8"?>
<resources>
	<dimen >1.1111dp</dimen>
	<dimen >2.2222dp</dimen>
	<dimen >3.3333dp</dimen>
	<dimen >4.4444dp</dimen>
	<dimen >5.5556dp</dimen>
	<dimen >6.6667dp</dimen>
	<dimen >7.7778dp</dimen>
	<dimen >8.8889dp</dimen>
	<dimen >10.0000dp</dimen>
	<dimen >11.1111dp</dimen>
	...
	<dimen >394.4444dp</dimen>
	<dimen >395.5556dp</dimen>
	<dimen >396.6667dp</dimen>
	<dimen >397.7778dp</dimen>
	<dimen >398.8889dp</dimen>
	<dimen >400.0000dp</dimen>
</resources>
复制代码

```

**values-sw400dp** 指的是当前设备屏幕的 **最小宽度** 为 **400dp** (该设备高度大于宽度，则最小宽度就是宽度，所以该设备宽度为 **400dp**)，把屏幕宽度同样分为 **360 份**，这时每份就等于 **1.1111dp** 了，每个引用都递增 **1.1111dp**，值最大的 **dimens** 引用 **dp_360** 同样刚好覆盖屏幕宽度，为 **400dp**

通过两个 **dimens.xml** 文件的比较，**dimens.xml** 的生成原理一目了然，方案会先确定 **最小宽度基准值**，然后将每个 **values-sw<N>dp** 中的 **dimens.xml** 文件都分配与 **最小宽度基准值** 相同的份数，再根据公式 **屏幕最小宽度 / 份数 (最小宽度基准值)** 求出每份占多少 **dp**，保证不管在哪个 **values-sw<N>dp** 中，**份数 (最小宽度基准值) * 每份占的 dp 值** 的结果都是刚好覆盖屏幕宽度，所以在 **份数** 不变的情况下，只需要根据屏幕的宽度在不同的设备上动态调整 **每份占的 dp 值**，就能完成适配

这样就能保证不管将项目运行到哪个设备上，只要当前设备能匹配到对应的 **values-sw<N>dp** 文件夹，那布局中的 **dimens** 引用就能根据当前屏幕的情况进行缩放，保证能完美适配，如果没有匹配到对应的 **values-sw<N>dp** 文件夹，也没关系，它会去寻找与之相近的 **values-sw<N>dp** 文件夹，虽然在这种情况下，布局中的 **dimens** 引用的值可能有些许误差，但是也能保证最大程度的完成适配

说到这里，那大家就应该就会明白我为什么会说 **smallestWidth 限定符屏幕适配方案** 的原理也同样是按百分比进行布局，如果在布局中，一个 **View** 的宽度引用 **dp_100**，那不管运行到哪个设备上，这个 **View** 的宽度都是当前设备屏幕总宽度的 **360 分之 100**，前提是项目提供有当前设备屏幕对应的 **values-sw<N>dp**，如果没有对应的 **values-sw<N>dp**，就会去寻找相近的 **values-sw<N>dp**，这时就会存在误差了，至于误差是大是小，这就要看您的第二个因数怎么分配了

其实 **smallestWidth 限定符屏幕适配方案** 的原理和 **今日头条屏幕适配方案** 挺像的，**今日头条屏幕适配方案** 是根据屏幕的宽度或高度动态调整每个设备的 **density** (每 **dp** 占当前设备屏幕多少像素)，而 **smallestWidth 限定符屏幕适配方案** 同样是根据屏幕的宽度动态调整每个设备 **每份占的 dp 值**

### 第二个因素

第二个因数是需要适配哪些 **最小宽度**？比如您想适配的 **最小宽度** 有 **320dp**、**360dp**、**400dp**、**411dp**、**480dp**，那方案就会为您的项目生成 **values-sw320dp**、**values-sw360dp**、**values-sw400dp**、**values-sw411dp**、**values-sw480dp** 这几个资源文件夹，像这样👇

```
├── src/main
│   ├── res
│   ├── ├──values
│   ├── ├──values-sw320dp
│   ├── ├──values-sw360dp
│   ├── ├──values-sw400dp
│   ├── ├──values-sw411dp
│   ├── ├──values-sw480dp
复制代码

```

方案会为您需要适配的 **最小宽度**，在项目中生成一系列对应的 **values-sw<N>dp**，在前面也说了，如果某个设备没有为它提供对应的 **values-sw<N>dp**，那它就会去寻找相近的 **values-sw<N>dp**，但如果这个相近的 **values-sw<N>dp** 与期望的 **values-sw<N>dp** 差距太大，那适配效果也就会大打折扣

那是不是 **values-sw<N>dp** 文件夹生成的越多，覆盖越多市面上的设备，就越好呢？

也不是，因为每个 **values-sw<N>dp** 文件夹其实都会占用一定的 **App** 体积，**values-sw<N>dp** 文件夹越多，**App** 的体积也就会越大

所以一定要合理分配 **values-sw<N>dp**，以越少的 **values-sw<N>dp** 文件夹，覆盖越多的机型

验证方案可行性
=======

原理讲完了，我们还是按照老规矩，来验证一下这个方案是否可行？

假设设计图总宽度为 **375 dp**，一个 **View** 在这个设计图上的尺寸是 **50dp * 50dp**，这个 **View** 的宽度占整个设计图宽度的 **13.3%** (**50 / 375 = 0.133**)

在使用 **smallestWidth 限定符屏幕适配方案** 时，需要提供 **最小宽度基准值** 和需要适配哪些 **最小宽度**，我们就把 **最小宽度基准值** 设置为 **375** (和 **设计图** 一致)，这时方案就会为我们需要适配的 **最小宽度** 生成对应的 **values-sw<N>dp** 文件夹，文件夹中的 **dimens.xml** 文件是由从 **1** 到 **375** 组成的 **dimens** 引用，把所有设备的屏幕宽度都分为 **375** 份，所以在布局文件中我们应该把这个 **View** 的高宽都引用 **dp_50**

下面就来验证下在使用 **smallestWidth 限定符屏幕适配方案** 的情况下，这个 **View** 与屏幕宽度的比例在分辨率不同的设备上是否还能保持和设计图中的比例一致

验证设备 1
------

**设备 1** 的屏幕总宽度为 **1080 px**，屏幕总高度为 **1920 px**，**DPI** 为 **480**

**设备 1** 的屏幕高度大于屏幕宽度，所以 **设备 1** 的 **最小宽度** 为屏幕宽度，再根据公式 **px / (DPI / 160) = dp**，求出 **设备 1** 的 **最小宽度** 的值为 **360 dp** (**1080 / (480 / 160) = 360**)

根据 **设备 1** 的 **最小宽度** 应该匹配的是 **values-sw360dp** 这个文件夹，假设 **values-sw360dp** 文件夹及里面的 **dimens.xml** 已经生成，且是按 **最小宽度基准值** 为 **375** 生成的，**360 / 375 = 0.96**，所以每份占的 **dp** 值为 **0.96**，**dimens.xml** 里面的内容是长下面这样的👇

```
<?xml version="1.0" encoding="UTF-8"?>
<resources>
	<dimen >0.96dp</dimen>
	<dimen >1.92dp</dimen>
	<dimen >2.88dp</dimen>
	<dimen >3.84dp</dimen>
	<dimen >4.8dp</dimen>
	...
	<dimen >48dp</dimen>
	...
	<dimen >356.16dp</dimen>
	<dimen >357.12dp</dimen>
	<dimen >358.08dp</dimen>
	<dimen >359.04dp</dimen>
	<dimen >360dp</dimen>
</resources>
复制代码

```

可以看到这个 **View** 在布局中引用的 **dp_50**，最终在 **values-sw360dp** 中定格在了 **48 dp**，所以这个 **View** 在 **设备 1** 上的高宽都为 **48 dp**，系统最后会将高宽都换算成 **px**，根据公式 **dp * (DPI / 160) = px**，所以这个 **View** 的高宽换算为 **px** 后等于 **144 px** (**48 * (480 / 160) = 144**)

**144 / 1080 = 0.133**，**View** 的实际宽度与 **屏幕总宽度** 的比例和 **View** 在设计图中的比例一致 (**50 / 375 = 0.133**)，所以完成了等比例缩放

某些设备的高宽是和 **设备 1** 相同的，但是 **DPI** 可能不同，而由于 **smallestWidth 限定符屏幕适配方案** 并没有像 **今日头条屏幕适配方案** 一样去自行修改 **density**，所以系统就会使用默认的公式 **DPI / 160** 求出 **density**，**density** 又会影响到 **dp** 和 **px** 的换算，因此 **DPI** 的变化，是有可能会影响到 **smallestWidth 限定符屏幕适配方案** 的

所以我们再来试试在这种特殊情况下 **smallestWidth 限定符屏幕适配方案** 是否也能完成适配

验证设备 2
------

**设备 2** 的屏幕总宽度为 **1080 px**，屏幕总高度为 **1920 px**，**DPI** 为 **420**

**设备 2** 的屏幕高度大于屏幕宽度，所以 **设备 2** 的 **最小宽度** 为屏幕宽度，再根据公式 **px / (DPI / 160) = dp**，求出 **设备 2** 的 **最小宽度** 的值为 **411.429 dp** (**1080 / (420 / 160) = 411.429**)

根据 **设备 2** 的 **最小宽度** 应该匹配的是 **values-sw411dp** 这个文件夹，假设 **values-sw411dp** 文件夹及里面的 **dimens.xml** 已经生成，且是按 **最小宽度基准值** 为 **375** 生成的，**411 / 375 = 1.096**，所以每份占的 **dp** 值为 **1.096**，**dimens.xml** 里面的内容是长下面这样的👇

```
<?xml version="1.0" encoding="UTF-8"?>
<resources>
	<dimen >1.096dp</dimen>
	<dimen >2.192dp</dimen>
	<dimen >3.288dp</dimen>
	<dimen >4.384dp</dimen>
	<dimen >5.48dp</dimen>
	...
	<dimen >54.8dp</dimen>
	...
	<dimen >406.616dp</dimen>
	<dimen >407.712dp</dimen>
	<dimen >408.808dp</dimen>
	<dimen >409.904dp</dimen>
	<dimen >411dp</dimen>
</resources>
复制代码

```

可以看到这个 **View** 在布局中引用的 **dp_50**，最终在 **values-sw411dp** 中定格在了 **54.8dp**，所以这个 **View** 在 **设备 2** 上的高宽都为 **54.8 dp**，系统最后会将高宽都换算成 **px**，根据公式 **dp * (DPI / 160) = px**，所以这个 **View** 的高宽换算为 **px** 后等于 **143.85 px** (**54.8 * (420 / 160) = 143.85**)

**143.85 / 1080 = 0.133**，**View** 的实际宽度与 **屏幕总宽度** 的比例和 **View** 在设计图中的比例一致 (**50 / 375 = 0.133**)，所以完成了等比例缩放

虽然 **View** 在 **设备 2** 上的高宽是 **143.85 px**，比 **设备 1** 的 **144 px** 少了 **0.15 px**，但是误差非常小，整体的比例并没有发生太大的变化，是完全可以接受的

这个误差是怎么引起的呢，因为 **设备 2** 的 **最小宽度** 的实际值是 **411.429 dp**，但是匹配的 **values-sw411dp** 舍去了小数点后面的位数 (**切记！系统会去寻找小于或等于 411.429 dp 的 values-sw<N>dp，所以 values-sw412dp 这个文件夹，设备 2 是匹配不了的**)，所以才存在了一定的误差，因此上面介绍的第二个因数是非常重要的，这直接决定误差是大还是小

可以看到即使在高宽一样但 **DPI** 不一样的设备上，**smallestWidth 限定符屏幕适配方案** 也能完成等比例适配，证明这个方案是可行的，如果大家还心存疑虑，也可以再试试其他分辨率的设备，其实到最后得出的比例都是在 **0.133** 左右，唯一的变数就是第二个因数，如果您生成的 **values-sw<N>dp** 与设备实际的 **最小宽度** 差别不大，那误差也就在能接受的范围内，如果差别很大，那就直接 **GG**

优点
==

1.  非常稳定，极低概率出现意外
    
2.  不会有任何性能的损耗
    
3.  适配范围可自由控制，不会影响其他三方库
    
4.  在插件的配合下，学习成本低
    

缺点
==

1.  在布局中引用 **dimens** 的方式，虽然学习成本低，但是在日常维护修改时较麻烦
    
2.  侵入性高，如果项目想切换为其他屏幕适配方案，因为每个 **Layout** 文件中都存在有大量 **dimens** 的引用，这时修改起来工作量非常巨大，切换成本非常高昂
    
3.  无法覆盖全部机型，想覆盖更多机型的做法就是生成更多的资源文件，但这样会增加 **App** 体积，在没有覆盖的机型上还会出现一定的误差，所以有时需要在适配效果和占用空间上做一些抉择
    
4.  如果想使用 **sp**，也需要生成一系列的 **dimens**，导致再次增加 **App** 的体积
    
5.  不能自动支持横竖屏切换时的适配，如上文所说，如果想自动支持横竖屏切换时的适配，需要使用 **values-w<N>dp** 或 **屏幕方向限定符** 再生成一套资源文件，这样又会再次增加 **App** 的体积
    
6.  不能以高度为基准进行适配，考虑到这个方案的名字本身就叫 **最小宽度限定符适配方案**，所以在使用这个方案之前就应该要知道这个方案只能以宽度为基准进行适配，为什么现在的屏幕适配方案只能以高度或宽度其中的一个作为基准进行适配，请看 [这里](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FJessYanCoding%2FAndroidAutoSize%2Fissues%2F8)
    

使用中的问题
======

这时有人就会问了，设计师给的设计图只标注了 **px**，使用这个方案时，那不是还要先将 **px** 换算成 **dp**？

其实也可以不用换算的，那这是什么骚操作呢？

很简单，你把设计图的 **px** 总宽度设置成 **最小宽度基准值** 就可以了，还是以前面验证可行性的例子

我们在前面验证可行性时把 **最小宽度基准值** 设置成了 **375**，为什么是 **375** 呢？因为设计图的总宽度为 **375 dp**，如果换算成 **px**，总宽度就是 **750 px**，我们这时把 **最小宽度基准值** 设置成 **750**，然后看看 **values-sw360dp** 中的 **dimens.xml** 长什么样👇

```
<?xml version="1.0" encoding="UTF-8"?>
<resources>
	<dimen >0.48dp</dimen>
	<dimen >0.96dp</dimen>
	<dimen >1.44dp</dimen>
	<dimen >1.92dp</dimen>
	<dimen >2.4dp</dimen>
	...
	<dimen >24dp</dimen>
	...
	<dimen >48dp</dimen>
	...
	<dimen >358.08dp</dimen>
	<dimen >358.56dp</dimen>
	<dimen >359.04dp</dimen>
	<dimen >359.52dp</dimen>
	<dimen >360dp</dimen>
</resources>
复制代码

```

**360 dp** 被分成了 **750** 份，相比之前的 **375** 份，现在 **每份占的 dp 值** 正好减少了一半，还记得在验证可行性的例子中那个 **View** 的尺寸是多少吗？**50dp * 50dp**，如果设计图只标注 **px**，那这个 **View** 在设计图上的的尺寸应该是 **100px * 100px**，那我们直接根据设计图上标注的 **px**，想都不用想直接在布局中引用 **px_100** 就可以了，因为在 **375** 份时的 **dp_50** 刚好等于 **750** 份时的 **px_100** (值都是 **48 dp**)，所以这时的适配效果和之前验证可行性时的适配效果没有任何区别

看懂了吗？直接将 **最小宽度基准值** 和布局中的引用都以 **px** 作为单位就可以直接填写设计图上标注的 **px**！

总结
==

关于文中所列出的优缺点，列出的缺点数量确实比列出的优点数量多，但 **缺点 3**，**缺点 4**，**缺点 5** 其实都可以归纳于 **占用 App 体积** 这一个缺点，因为他们都可以通过增加资源文件来解决问题，而 **缺点 6** 则是这个方案的特色，只能以宽度为基准进行适配，这个从这个方案的名字就能看出

请大家千万不要曲解文章的意思，不要只是单纯的对比优缺点的数量，缺点的数量大于优点的数量就一定是这个方案不行？没有一个方案是完美的，每个人的需求也都不一样，作为一篇科普类文章我只可能把这个方案描述得尽可能的全面

这个方案能给你带来什么，不能给你带来什么，我必须客观的描述清楚，这样才有助你做出决定，你应该注重的是在这些优缺点里什么是我能接受的，什么是我不能接受的，是否能为了某些优点做出某些妥协，而不只是单纯的去看数量，这样毫无意义，有些人就是觉得稳定性最重要，其他的都可以做出妥协，那其他缺点对于他来说都是无所谓的

好了，这个系列的第二篇文章讲完了，这篇文章也是按照上篇文章的优良传统，写的非常详细，哪怕是新手我相信也应该能看懂，为什么这么多人都不知道自己该选择什么样的方案，就是因为自己都没搞懂这些方案的原理，懂了原理过后才知道这些方案是否是自己想要的

接下来的第三篇文章会详细讲解两个方案的深入对比以及该如何选择，并剖析我根据 **今日头条屏幕适配方案** 优化的屏幕适配框架 [**AndroidAutoSize**](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FJessYanCoding%2FAndroidAutoSize) 的原理，敬请期待

如果大家想使用 **smallestWidth 限定符屏幕适配方案**，可以参考 [这篇文章](https://link.juejin.im?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2F1302ad5a4b04)，里面提供有自动生成资源文件的插件和 **Demo**，由于我并没有在项目中使用 **smallestWidth 限定符屏幕适配方案**，所以如果在文章中有遗漏的知识点请谅解以及补充，感谢！

公众号
---

扫码关注我的公众号 JessYan，一起学习进步，如果框架有更新，我也会在公众号上第一时间通知大家

![](https://user-gold-cdn.xitu.io/2019/2/25/1692228ad4606cd8?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

以下是 **骚年你的屏幕适配方式该升级了!** 系列文章，欢迎转发以及分享:

*   [骚年你的屏幕适配方式该升级了!（一）- 今日头条适配方案](https://juejin.im/post/5b7a29736fb9a019d53e7ee2)
*   [骚年你的屏幕适配方式该升级了!（二）-smallestWidth 限定符适配方案](https://juejin.im/post/5ba197e46fb9a05d0b142c62)
*   [今日头条屏幕适配方案终极版正式发布!](https://juejin.im/post/5bce688e6fb9a05cf715d1c2)

**Hello 我叫 JessYan，如果您喜欢我的文章，可以在以下平台关注我**

*   个人主页: [jessyan.me](https://link.juejin.im?target=http%3A%2F%2Fjessyan.me)
*   GitHub: [github.com/JessYanCodi…](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2FJessYanCoding)
*   掘金: [juejin.im/user/57a9db…](https://juejin.im/user/57a9dbd9165abd0061714613)
*   简书: [www.jianshu.com/u/1d0c0bc63…](https://link.juejin.im?target=http%3A%2F%2Fwww.jianshu.com%2Fu%2F1d0c0bc634db)
*   微博: [weibo.com/u/178626251…](https://link.juejin.im?target=http%3A%2F%2Fweibo.com%2Fu%2F1786262517)

-- The end