> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://wx.zsxq.com/dweb/#

Android 高级学习路线【绝密，泄露必究】 欢迎来到炼铁狱，预祝打怪升级愉快。 

引言 本学习路线的内容都是面试高频，难度大，但是学会以后终身受用。这里的高级采用的是 BAT 标准，即严格意义的高级工程师，在小公司都可以当个 leader 了。 

本学习路线共有 22 关，如下图所示，高级路线更偏重对原理和知识完备度的把握，除此之外，还涉及到一部分架构、jni 以及 Gradle 等知识。 
学习这部分知识不要心急，慢慢来。 

第一关：Binder、AIDL、多进程（建议学习时间：2 周） 
知识点：Binder 原理、AIDL 的使用、多进程的定义和特性 学习资料： ① Android 开发艺术探索第 2 章【推荐理由】地球人都知道学 Android 要看艺术探索。 ② [Android Bander 设计与实现 - 设计篇 - universus 的专栏 - CSDN 博客](https://blog.csdn.net/universus/article/details/6211589 "Android Bander设计与实现 - 设计篇 - universus的专栏 - CSDN博客") 【推荐理由】Binder 底层史上最牛逼分析，没有之一。 ③ 艺术探索第 2 章的例子，请一定手动运行一遍并仔细理解，地址：[GitHub - singwhatiwanna/android-art-res: the sourc...](https://github.com/singwhatiwanna/android-art-res "GitHub - singwhatiwanna/android-art-res: the sourc...")。 第二关：View 的绘制（建议学习时间：3 天） 知识点：View 的 measure、layout 和 draw，View 的工作原理 学习资料： ① Android 开发艺术探索第 4 章【推荐理由】地球人都知道学 Android 要看艺术探索。 ② [图解 View 测量、布局及绘制原理 - 简书](https://www.jianshu.com/p/3d2c49315d68 "图解View测量、布局及绘制原理 - 简书") 【推荐理由】配有流程图，比艺术探索好理解一些。 ③ [android ListView 工作原理 - Android - 掘金](https://juejin.im/entry/5819968bda2f60005dda6a2d "android ListView 工作原理 - Android - 掘金")【推荐理由】帮大家了解 ListView 的工作过程，很有价值。 第三关：事件分发（建议学习时间：1 周） 知识点：事件分发原理和规则 学习资料： ① Android 开发艺术探索第 3 章【推荐理由】地球人都知道学 Android 要看艺术探索。 ② [Android 事件分发机制，大表哥带你慢慢深入 - 简书](https://www.jianshu.com/p/fc0590afb1bf "Android事件分发机制，大表哥带你慢慢深入 - 简书") 【推荐理由】通过实际的例子来讲事件分发，好理解。 ③ [Android ViewGroup 事件分发机制 - Hongyang - CSDN 博客](https://blog.csdn.net/lmj623565791/article/details/39102591 "Android ViewGroup事件分发机制 - Hongyang - CSDN博客") 【推荐理由】张鸿洋写的。 第四关：消息队列（建议学习时间：1 天） 要求：Handler、Looper、Thread 三者之间的关系；得知道子线程创建 Handler 为什么会报错，如何才能不报错 学习资料： ① Android 开发艺术探索第 10 章【推荐理由】这块内容不难，艺术探索就够了。 ② [源码角度讲解子线程创建 Handler 报错的原因 - 曹银飞的专栏 - CSDN 博客](https://blog.csdn.net/dfskhgalshgkajghljgh/article/details/52601802 "源码角度讲解子线程创建Handler报错的原因 - 曹银飞的专栏 - CSDN博客")【推荐理由】一个实际的例子帮助大家更好地理解。 


第五关：Activity 难点（建议学习时间：1 天） ① setResult 和 finish 的顺序关系 ② onSaveInstanceState() 和 onRestoreInstanceState() ③ onNewIntent() 和 onConfigurationChanged() 学习资料： ① [setResult() 的调用时机 - 沙翁 - 博客园](https://www.cnblogs.com/shaweng/p/3875825.html "setResult()的调用时机 - 沙翁 - 博客园")【推荐理由】清晰易懂，直接了当。 ② [onSaveInstanceState() 和 onRestoreInstanceState() 使用详解...](https://www.jianshu.com/p/27181e2e32d2 "onSaveInstanceState()和onRestoreInstanceState()使用详解...")【推荐理由】简单好懂。 ③ [关于 onConfigurationChanged 方法及常见问题解决 - 朱小姐。的博客 - CSDN...](https://blog.csdn.net/qq_27570955/article/details/55046934 "关于onConfigurationChanged方法及常见问题解决 - 朱小姐。的博客 - CSDN...")【推荐理由】简单好懂，文章在郭霖公号投稿了。 ④ 艺术探索第 1 章【推荐理由】地球人都知道学 Android 要看艺术探索。 

第六关：Service 难点（建议学习时间：2 天） 
- ① 先 start 再 bind，如何停止一个 Service 
- ② Service onStartCommand 的返回值
- ③ bindService 后，ServiceConnection 里面的回调方法运行在哪个线程？它们的调用时机分别是什么？ 
- ④ Service 的 onCreate 运行在哪个线程？ 
- 学习资料： 
- ① [Android 中 startService 和 bindService 的区别 - 简书](https://www.jianshu.com/p/d870f99b675c "Android中startService和bindService的区别 - 简书")【推荐理由】详细全面地回答了上面的问题。 
- ② [Service: onStartCommand 诡异的返回值 - CodingMan - CSDN 博...](https://blog.csdn.net/veryitman/article/details/7600008 "Service: onStartCommand 诡异的返回值 - CodingMan - CSDN博...")【推荐理由】通过实例来演示 onStartCommand 那诡异的返回值。 
- ③ Service 的 onCreate、onStartCommand、onDestory 等全部生命周期方法都运行在 UI 线程，ServiceConnection 里面的回调方法也是运行在 UI 线程，大家一定要记住。【推荐理由】任玉刚说的，你们自己可以打 log 验证一下 

第七关：ContentProvider 难点 (建议学习时间：3 天) 
- ① ContentProvider 的生命周期 
- ② ContentProvider 的 onCreate 和 CRUD 运行在哪个线程？它们是线程安全的吗？ 
- ③ ContentProvider 的内部存储只能是 sqlite 吗？ 
- 学习资料： 
- ① 艺术探索第 9 章中 ContentProvider 的启动、艺术探索第二章中 ContentProvider 的介绍【推荐理由】详细了解下，艺术探索的内容无需解释
- ② [android ContentProvider onCreate() 在 Application......](https://www.jianshu.com/p/0f1e36507b9d "android ContentProvider onCreate()在 Application......")【推荐理由】此文明确说明了 ContentProvider 的 onCreate 早于 Application 的 onCreate 而执行。 
- ③ [ContentProvider 总结 - 简书](https://www.jianshu.com/p/cfa46bea6d7b "ContentProvider总结 - 简书")【推荐理由】此文明确说明了 ContentProvider 的 onCreate 和 CRUD 所在的线程 注意：ContentProvider 的底层是 Binder，当跨进程访问 ContentProvider 的时候，CRUD 运行在 Binder 线程池中，不是线程安全的，而如果在同一个进程访问 ContentProvider，根据 Binder 的原理，同进程的 Binder 调用就是直接的对象调用，这个时候 CRUD 运行在调用者的线程中。另外，ContentProvider 的内部存储不一定是 sqlite，它可以是任意数据。 

第八关：AsyncTask 原理 (建议学习时间：3 天) 要求：知道 AsyncTask 的工作原理，知道其串行和并行随版本的变迁
- ① [Android 源码分析—带你认识不一样的 AsyncTask - 任玉刚 - CSDN 博客](https://blog.csdn.net/singwhatiwanna/article/details/17596225 "Android源码分析—带你认识不一样的AsyncTask - 任玉刚 - CSDN博客") 【推荐理由】只看这一篇文章就够了
- ② [https://android.googlesource.com/platform/frameworks/base/ /android-8.1.0_r46/core/java/android/os/AsyncTask.java](https://android.googlesource.com/platform/frameworks/base/+/android-8.1.0_r46/core/java/android/os/AsyncTask.java "https://android.googlesource.com/platform/frameworks/base/ /android-8.1.0_r46/core/java/android/os/AsyncTask.java") 【推荐理由】阅读 AsyncTask 8.1 版本的源码，看看是否有更新 

第九关：RemoteViews(建议学习时间：7 天) 要求：熟悉 RemoteViews 并了解其原理 
- ① Android 开发艺术探索 第 5 章【推荐理由】艺术探索是高级工程师进阶必备 
- ② [关于 RemoteViews 跨进程资源访问的勘误 - 掘金](https://juejin.im/post/5c3b588be51d4551de1da844 "关于 RemoteViews 跨进程资源访问的勘误 - 掘金") 【推荐理由】进一步理解 RemoteViews 的实现，通过它可以实现资源的跨进程访问，艺术探索中的担心是多余的 

第十关：Window 和 ViewRootImpl(建议学习时间：14 天) 要求：熟悉 Window、WMS 和 ViewRootImpl 的原理 
- ① Android 开发艺术探索 第 8 章【推荐理由】艺术探索是高级工程师进阶必备
- ② Android 进阶解密 第 8 章 【推荐理由】进阶必备
- ③ [Android 窗口机制（四）ViewRootImpl 与 View 和 WindowManager - 简书](https://www.jianshu.com/p/9da7bfe18374 "Android窗口机制（四）ViewRootImpl与View和WindowManager - 简书")【推荐理由】另一个优秀作者对 Window 的描述 ④ [Android 中 MotionEvent 的来源和 ViewRootImpl - 任玉刚 - CSDN 博客](https://blog.csdn.net/singwhatiwanna/article/details/50775201 "Android中MotionEvent的来源和ViewRootImpl - 任玉刚 - CSDN博客") 【推荐理由】另一个角度理解下输入事件和 ViewRootImpl 的关联 
- https://juejin.im/post/5cee14f6e51d45777540fd40?utm_source=gold_browser_extension

第十一关：刁钻问题汇总 (建议学习时间：一周) 
① 子线程访问 UI 却不报错的原因：[Android 中子线程真的不能更新 UI 吗？ - yinhuanxu - CSDN 博客](https://blog.csdn.net/xyh269/article/details/52728861 "Android中子线程真的不能更新UI吗？ - yinhuanxu - CSDN博客") 
② 主线程的消息循环是一个死循环，为何不会卡死：[Android 中为什么主线程不会因为 Looper.loop() 里的死循环卡死？ - 知乎](https://www.zhihu.com/question/34652589 "Android中为什么主线程不会因为Looper.loop()里的死循环卡死？ - 知乎") ③ Binder、IBinder、IInterface 的关系：[把玩 Android 多进程. pdf_免费高速下载 | 百度网盘 - 分享无限制](https://pan.baidu.com/s/1VImj3EXesFXAqT3pskcSig "把玩Android多进程.pdf_免费高速下载|百度网盘-分享无限制") 注意：主线程的消息循环背后，一切皆是消息，消息机制和 Binder 是 Android 系统的两大核心机制，屏幕触摸消息、键盘消息、四大组件的启动等均是由消息驱动。 

第十二关：Retrofit 原理分析 (建议学习时间：14 天) 要求：熟悉 Retrofit/OKHttp 的工作原理 
- ① [OKHttp 源码解析 - 简书](https://www.jianshu.com/p/27c1554b7fee "OKHttp源码解析 - 简书")【推荐理由】okhttp 源码分析
- ② [Retrofit 原理解析最简洁的思路 - 知乎](https://zhuanlan.zhihu.com/p/35121326 "Retrofit原理解析最简洁的思路 - 知乎") 【推荐理由】retrofit 原理分析
- ③ [Retrofit 是如何工作的？ - 简书](https://www.jianshu.com/p/cb3a7413b448 "Retrofit是如何工作的？ - 简书")【推荐理由】另一个 retrofit 原理分析
- ④ 自行阅读 okhttp/retrofit 的源码，并写出一篇原理分析的文章 【推荐理由】源码一定要亲自读一读，并沉淀为自己的知识 
- ⑤ 网易云课堂 上面有okhttp源码解析

第十三关：RxJava 原理分析 (建议学习时间：14 天) 
- ① [友好 RxJava2.x 源码解析（一）基本订阅流程 - 掘金](https://juejin.im/post/5a209c876fb9a0452577e830 "友好 RxJava2.x 源码解析（一）基本订阅流程 - 掘金")【推荐理由】基本订阅流程，已在玉刚说投稿 
- ② [友好 RxJava2.x 源码解析（二）线程切换 - 掘金](https://juejin.im/post/5a248206f265da432153ddbc "友好 RxJava2.x 源码解析（二）线程切换 - 掘金") 【推荐理由】线程切换，已在玉刚说投稿 
- ③ [友好 RxJava2.x 源码解析（三）zip 源码分析 - 掘金](https://juejin.im/post/5ac16a2d6fb9a028b617a82a "友好 RxJava2.x 源码解析（三）zip 源码分析 - 掘金")【推荐理由】zip，已在玉刚说投稿 
- https://juejin.im/post/5b1fbd796fb9a01e8c5fd847  详解 RxJava 的消息订阅和线程切换原理
- ④ 自行阅读 RxJava 源码，并写出一篇原理分析的文章 【推荐理由】源码一定要亲自读一读，并沉淀为自己的知识

第十四关：Glide 原理分析 (建议学习时间：14 天) 
- ① [Android 图片加载框架最全解析（二），从源码的角度理解 Glide 的执行流程 - 郭霖的专栏 - ...](https://blog.csdn.net/guolin_blog/article/details/53939176 "Android图片加载框架最全解析（二），从源码的角度理解Glide的执行流程 - 郭霖的专栏 - ...")【推荐理由】glide 工作原理，文章很长，郭霖出品 
- ② 自行阅读 glide 4 源码，并写出一篇原理分析的文章 【推荐理由】源码一定要亲自读一读，并沉淀为自己的知识 

第十五关：Groovy (建议学习时间：3 天) 要求：熟悉 groovy 的常见语法 
- ① [Gradle 从入门到实战 - Groovy 基础 - 任玉刚 - CSDN 博客](https://blog.csdn.net/singwhatiwanna/article/details/76084580 "Gradle从入门到实战 - Groovy基础 - 任玉刚 - CSDN博客")【推荐理由】groovy 语法基础，任玉刚出品 
- ② [The Apache Groovy programming language - Documenta...](http://www.groovy-lang.org/documentation.html "The Apache Groovy programming language - Documenta...") 【推荐理由】官方文档，可当做字典来查阅 

第十六关：Gradle 插件基础 (建议学习时间：7 天) 要求：熟悉 gradle 语法，可以书写简单的 gradle 插件 
- ① [全面理解 Gradle - 执行时序 - 任玉刚 - CSDN 博客](https://blog.csdn.net/singwhatiwanna/article/details/78797506 "全面理解Gradle - 执行时序 - 任玉刚 - CSDN博客")【推荐理由】gradle 执行时序，任玉刚出品 
- ② [全面理解 Gradle - 定义 Task - 任玉刚 - CSDN 博客](https://blog.csdn.net/singwhatiwanna/article/details/78898113 "全面理解Gradle - 定义Task - 任玉刚 - CSDN博客")【推荐理由】task 定义，任玉刚出品 
- ③ [一篇文章带你了解 Gradle 插件的所有创建方式](https://mp.weixin.qq.com/s/KCpl0CNgwMv0CgvbadNK6A "一篇文章带你了解Gradle插件的所有创建方式")【推荐理由】gradle 插件的三种创建方式，已在玉刚说投稿 
- ④ [写给 Android 开发者的 Gradle 系列（三）撰写 plugin - 掘金](https://juejin.im/post/5b02113a5188254289190671 "写给 Android 开发者的 Gradle 系列（三）撰写 plugin - 掘金") 【推荐理由】一个简单的小例子，让大家理解 gradle 插件的价值 

第十七关：设计模式 (建议学习时间：30-60 天) 要求：熟悉 6 大基本原则、23 种设计模式，并能在实际中灵活使用 
- ① 《大话设计模式》【推荐理由】强烈建议买一本设计模式的书，好好看看，这事急不得 
- ② [23 种设计模式全解析 - 龙鱼鹿 - CSDN 博客](https://blog.csdn.net/longyulu/article/details/9159589 "23种设计模式全解析 - 龙鱼鹿 - CSDN博客") 【推荐理由】这是一篇文章，涵盖了全部设计模式，我收藏了好几年了，拿出来给大家看，但是只看这篇文章是远远不够的 
- ③ [https://t.zsxq.com/QzZZZNj](https://t.zsxq.com/QzZZZNj "https://t.zsxq.com/QzZZZNj")【推荐理由】学习设计模式的精神，任玉刚出品 
- ④ [如何通俗理解设计模式及其思想? - 掘金](https://juejin.im/post/5b3cddb6f265da0f8145c049 "如何通俗理解设计模式及其思想? - 掘金") 【推荐理由】学习设计模式的精神，却把青梅嗅出品 

第十八关：MVC、MVP、MVVM (建议学习时间：14 天) 要求：熟悉它们并会灵活使用 
- ① [MVC、MVP、MVVM，我到底该怎么选？ - 掘金](https://juejin.im/post/5b3a3a44f265da630e27a7e6 "MVC、MVP、MVVM，我到底该怎么选？ - 掘金")【推荐理由】3M，理论结合小例子，好理解，玉刚说写作平台文章 
- ② [一个小例子彻底搞懂 MVP - 掘金](https://juejin.im/post/5b33ad92f265da59584da067 "一个小例子彻底搞懂 MVP - 掘金")【推荐理由】讲解 MVP，理论结合小例子，好理解，玉刚说写作平台文章 
- ③ [我对移动端架构的思考 - 掘金](https://juejin.im/post/5b44d50de51d451925627900 "我对移动端架构的思考 - 掘金")【推荐理由】3M，Mr.S 的作品，玉刚说写作平台文章 

第十九关：组件化 (建议学习时间：7 天) 
- ① [Android 组件化最佳实践 - 掘金](https://juejin.im/post/5b5f17976fb9a04fa775658d "Android 组件化最佳实践 - 掘金")【推荐理由】一篇长文搞定，包括所有内容，分析 + 实例 
- [从智行 Android 项目看组件化架构实践](https://mp.weixin.qq.com/s?__biz=MjM5MDI3MjA5MQ==&mid=2697268363&idx=1&sn=3db2dce36a912936961c671dd1f71c78&scene=21#wechat_redirect)
- 组件化 网易云课堂 xfhy微信号  Android高级开发工程师直播课2

插件化

第二十关：jni 和 ndk 基础 (建议学习时间：30-60 天) 要求：熟悉 jni 和 ndk 语法，能进行简单的开发 
- ① 《Android 开发艺术探索》第 14 章【推荐理由】这是最最基本的 jni 和 ndk 入门 
- ② [JNI/NDK 开发指南 - 技术改变生活（为理想而奋斗，为目标而努力！） - xyang0917 -...](https://blog.csdn.net/xyang81/column/info/blogjnindk "JNI/NDK开发指南 - 技术改变生活（为理想而奋斗，为目标而努力！） - xyang0917 -...")【推荐理由】找了半天，找到一个还凑合的教程，真是资源匮乏呀 
- ③ [https://developer.android.com/ndk/guides/](https://developer.android.com/ndk/guides/ "https://developer.android.com/ndk/guides/")【推荐理由】官方的 ndk 入门指南，讲了很多配置选项，推荐看看 
- ④ [Android JNI 编程实践 - 简书](https://www.jianshu.com/p/9b83cc5a5ba8 "Android JNI 编程实践 - 简书")【推荐理由】讲解了如何注册 jin 函数表，也需要大家看一下 
- ⑤ 书籍《Android C++ 高级编程 使用 NDK》 【推荐理由】如果要系统学习 ndk，还是推荐看看书 第 21-22 关：点击 [https://t.zsxq.com/2rB2nAQ](https://t.zsxq.com/2rB2nAQ "https://t.zsxq.com/2rB2nAQ") 继续


