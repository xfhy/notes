> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://hencoder.com/ui-2-2/

这期是 HenCoder 布局部分的第二期：重写 onMeasure() 来全新定制自定义 View 的尺寸。

## 简介

这期虽然距离上期的时间比较久，但主要是我的个人原因，而不是因为这期的内容难。这期的内容还是比较简单的，主要是一些概念和原理上的东西，实操方面非常容易，所以和上期一样，主要把视频看看就差不多啦：

<embed quality="high" allowfullscreen="true" type="application/x-shockwave-flash" src="//static.hdslb.com/miniloader.swf" flashvars="aid=17689063&amp;page=1" pluginspage="//www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" id="fitvid273411">

> 在页面里看不到视频的，可以点击 [这里](https://www.bilibili.com/video/av17689063/) 去 B 站看；在海外看得卡的，可以点击 [这里](https://youtu.be/aOb4Hvqbeu4) 去 YouTube 看。

## 总结

和上期一样，这期同样是视频之后就直接是总结。

因为关键点全都在视频里讲清楚了，所以这里只总结一下视频中的关键点：

### 全新定制尺寸和修改尺寸的最重要区别

需要在计算的同时，保证计算结果满足父 View 给出的的尺寸限制

### 父 View 的尺寸限制

1.  由来：开发者的要求（布局文件中 `layout_` 打头的属性）经过父 View 处理计算后的更精确的要求；
2.  限制的分类：

    1.  `UNSPECIFIED`：不限制
    2.  `AT_MOST`：限制上限
    3.  `EXACTLY`：限制固定值

### 全新定义自定义 View 尺寸的方式

1.  重新 `onMeasure()`，并计算出 View 的尺寸；
2.  使用 `resolveSize()` 来让子 View 的计算结果符合父 View 的限制（当然，如果你想用自己的方式来满足父 View 的限制也行）。

## 练习项目

没有练习项目。

最近我的工作状态一直很不好，现在也还没有完全恢复，所以各位，这次就没有练习项目了。

## 下期预告

下期是布局部分的最后一期：重写 `onMeasure()` 和 `onLayout()` 来定制 `ViewGroup` 的内部布局。

## 觉得赞？

那就关注一下？↓↓↓

![](https://ws4.sinaimg.cn/large/006tNc79ly1fl6z2sve5kj30p00bx40b.jpg)

* * *