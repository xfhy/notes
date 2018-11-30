> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://hencoder.com/ui-3-1/

休息了几个月，HenCoder 又回来了。

这期的内容是之前说过的，自定义 View 的最后一部分：触摸反馈。触摸反馈的概念简单，但是内部逻辑比较复杂，往往把开发者难倒、让人总也学不会的也是因为逻辑太多绕不过来，所以我这次又做了一个长长的视频来讲解原理，把最本质的东西拆解开来讲，希望能让你比较舒服地吸收。视频的制作花了 5 天时间，一共 12 分钟多，全部是讲的触摸反馈的一些最核心的逻辑和原理。

细节上反而没有讲太多，因为讲这方面细节的文章，网上已经一大堆了，而且不少都写得很好。

闲话说完，放视频：

<embed src="https://imgcache.qq.com/tencentvideo_v1/playerv3/TPout.swf?max_age=86400&amp;v=20161117&amp;vid=a0684ijwxzr&amp;auto=0" allowfullscreen="true" quality="high" align="middle" allowscriptaccess="always" type="application/x-shockwave-flash" id="fitvid150097">

如果在页面中看不到视频，可以点 [这里](https://v.qq.com/x/page/a0684ijwxzr.html) 去看原视频。

### 总结：

自定义触摸反馈的关键：

1.  重写 `onTouchEvent()`，在里面写上你的触摸反馈算法，并返回 `true`（关键是 `ACTION_DOWN` 事件时返回 `true`）。
2.  如果是会发生触摸冲突的 `ViewGroup`，还需要重写 `onInterceptTouchEvent()`，在事件流开始时返回 `false`，并在确认接管事件流时返回一次 `true`，以实现对事件的拦截。
3.  当子 View 临时需要组织父 View 拦截事件流时，可以调用父 View 的 `requestDisallowInterceptTouchEvent()` ，通知父 View 在当前事件流中不再尝试通过 `onInterceptTouchEvent()` 来拦截。

### HenCoder Plus

另外，今天还要公布我的一个新项目：HenCoder Plus。

和 HenCoder 定位不同，HenCoder Plus 并不是一个精华技术分享，而是一个系统化的教学项目。人的技术往往是不均衡的，有些方面已经很强了，但有些方面却还比较弱，这些弱项经常会在一定高度之后限制技术人的发展。HenCoder Plus 的目的就是，针对一些最为普遍和关键性的技术短板，在短时间内进行集中教学，帮助需要的人得到快速的提升，让自己的「技术木桶」更加均衡。

为了保证事情稳步进行不出差错，HenCoder Plus 事先进行了几天时间的低调宣传，现在已经有接近 30 人参与。如果你感兴趣，可以扫下面的二维码，或者直接访问 [http://plus.hencoder.com](http://plus.hencoder.com) 来了解详情。

![](https://s1.ax1x.com/2018/06/11/Cqhq0g.png)

![](https://s1.ax1x.com/2018/06/11/Cq5C8I.png)

### 说两点

1.  最好有一定开发经验再来报名，建议是至少一年以上。因为 HenCoder Plus 是一个针对有经验的人的收费教学，我不想浪费你的钱。
2.  HenCoder 还会用我习惯的方式，低频率、高质量地继续更新。

* * *