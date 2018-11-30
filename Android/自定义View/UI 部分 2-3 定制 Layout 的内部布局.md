> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://hencoder.com/ui-2-3/

这期是 HenCoder 布局部分的最后一期：重写 `onMeasure()` 和 `onLayout()` 来定制 `Layout` 的内部布局。

## 简介

这期虽然距离上期的时间比较久，但主要是我的个人原因，而不是因为这期的内容难。这期的内容还是比较简单的，主要是一些概念和原理上的东西，实操方面非常容易，所以和上期一样，主要把视频看看就差不多啦：

<embed quality="high" allowfullscreen="true" type="application/x-shockwave-flash" src="//static.hdslb.com/miniloader.swf" flashvars="aid=18330166&amp;page=1" pluginspage="//www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" id="fitvid972016">

> 如果看不到视频，可以点 [这里](https://www.bilibili.com/video/av18330166/) 直接去 B 站看；如果你在海外，可以点 [这里](http://www.youtube.com/timedtext_video?v=fiSEB4w1lok&ref=share) 去 YouTube 上看。

## 总结

这期的文章依然是只有总结。但这次主要是因为我最近实在太多事情了……

### 定制 Layout 内部布局的方式

1.  重写 `onMeasure()` 来计算内部布局
2.  重写 `onLayout()` 来摆放子 View

### 重写 onMeasure() 的三个步骤：

1.  调用每个子 View 的 `measure()` 来计算子 View 的尺寸
2.  计算子 View 的位置并保存子 View 的位置和尺寸
3.  计算自己的尺寸并用 `setMeasuredDimension()` 保存

### 计算子 View 尺寸的关键

计算子 View 的尺寸，关键在于 `measure()` 方法的两个参数——也就是子 View 的两个 `MeasureSpec` 的计算。

#### 子 View 的 MeasureSpec 的计算方式：

*   结合开发者的要求（xml 中 `layout_` 打头的属性）和自己的可用空间（自己的尺寸上限 - 已用尺寸）
*   尺寸上限根据自己的 `MeasureSpec` 中的 mode 而定
    *   EXACTLY / AT_MOST：尺寸上限为 `MeasureSpec` 中的 `size`
    *   UNSPECIFIED：尺寸无上限

### 重写 onLayout() 的方式

在 `onLayout()` 里调用每个子 View 的 `layout()` ，让它们保存自己的位置和尺寸。

## 练习项目

这期还是没有练习项目。

## 降速生产声明

最近把 HenCoder 做得越来越溜的同时，各种工作上的事情和一些个人私事也忽然蜂拥而至。由于个人能力有限，接下来 HenCoder 将会被迫进一步降低产出速度。

呼…… 在未来的某个时间，我们下期再见啦！

## 觉得赞？

那就关注一下？↓↓↓

![](https://ws4.sinaimg.cn/large/006tNc79ly1fl6z2sve5kj30p00bx40b.jpg)

* * *