> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/B05nQmQ2AYyZAn_ospAlEQ

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6FSn51QbdP1ic92cjsQM7LkE1CHaFMjMuwuvk5aHV43DjI5QRia5rU0DDUrXldEI1t6d6VjDg0mThw/640?wx_fmt=png)

<section class="" data-id="t2" style="line-height: 27.2px;white-space: normal;max-width: 100%;letter-spacing: 0.544px;box-sizing: border-box !important;overflow-wrap: break-word !important;background-color: rgb(255, 255, 255);">

<section class="" style="margin-top: 2em;margin-right: auto;margin-left: auto;padding-top: 0.5em;padding-bottom: 0.5em;max-width: 100%;box-sizing: border-box;width: 536px;border-top-width: 1px;border-top-style: solid;border-top-color: rgb(204, 204, 204);font-size: 1em;overflow-wrap: break-word !important;">

今日科技快讯

</section>

</section>

近日虎嗅网发文称：“据虎嗅得到的独家消息，成都成华区监察委已立案调查‘东方广益 6 亿元入股锤子科技（北京）股份有限公司’事宜”，并称认为该项目有国有资产流失嫌疑。对此，当晚 23 时，成都东方广益投资有限公司在官网发布声明：2019 年 4 月 27 日，成都东方广益投资有限公司关注到网上有网民发布关于成都成华区监察委已立案调查 “东方广益 6 亿元入股锤子科技（北京）股份有限公司” 事宜的消息。该消息严重不实，我公司保留依法追究发帖者法律责任的权利。特此声明。

<section class="" data-id="t2" style="line-height: 27.2px;white-space: normal;max-width: 100%;letter-spacing: 0.544px;box-sizing: border-box !important;overflow-wrap: break-word !important;background-color: rgb(255, 255, 255);">

<section class="" style="margin-top: 2em;margin-right: auto;margin-left: auto;padding-top: 0.5em;padding-bottom: 0.5em;max-width: 100%;box-sizing: border-box;width: 536px;border-top-width: 1px;border-top-style: solid;border-top-color: rgb(204, 204, 204);font-size: 1em;overflow-wrap: break-word !important;">

作者简介

</section>

</section>

大家早上好，本篇文章来自 **Joseph_L** 的投稿，和大家分享了 View.inflate 方法的相关内容，相信会对大家有所帮助！同时也感谢作者贡献的精彩文章。

****Joseph_L**** 的博客地址：

> <section class="">
> 
> https://www.jianshu.com/u/6b51f840977d
> 
> </section>

<section class="" data-id="t2" style="line-height: 27.2px;white-space: normal;max-width: 100%;letter-spacing: 0.544px;box-sizing: border-box !important;overflow-wrap: break-word !important;background-color: rgb(255, 255, 255);">

<section class="" style="margin-top: 2em;margin-right: auto;margin-left: auto;padding-top: 0.5em;padding-bottom: 0.5em;max-width: 100%;box-sizing: border-box;width: 536px;border-top-width: 1px;border-top-style: solid;border-top-color: rgb(204, 204, 204);font-size: 1em;overflow-wrap: break-word !important;">

开始

</section>

</section>

误用 LayoutInflater 的 inflate() 方法已经不是什么稀罕事儿了……

做 Android 开发做久了，一定会或多或少地对布局的渲染有一些懵逼：

1.  View.inflate() 和 LayoutInflator.from().inflate() 有啥区别？

2.  调用 inflate() 方法的时候有时候传 null，有时候传 parent 是为啥？

3.  用 LayoutInflater 有时候还可能传个 attachToRoot ，这又是个啥？

接下来我们就从源码的角度来寻找一下这几个问题的答案，后面再用几个示例来验证我们的猜想。

话不多说，Let's go !

<section class="" data-id="t2" style="line-height: 27.2px;white-space: normal;max-width: 100%;letter-spacing: 0.544px;box-sizing: border-box !important;overflow-wrap: break-word !important;background-color: rgb(255, 255, 255);">

<section class="" style="margin-top: 2em;margin-right: auto;margin-left: auto;padding-top: 0.5em;padding-bottom: 0.5em;max-width: 100%;box-sizing: border-box;width: 536px;border-top-width: 1px;border-top-style: solid;border-top-color: rgb(204, 204, 204);font-size: 1em;overflow-wrap: break-word !important;">

基本介绍

</section>

</section>

先来看一下这个方法具体做了什么：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
 /** * Inflate a view from an XML resource.  This convenience method wraps the {@link * LayoutInflater} class, which provides a full range of options for view inflation. */public static View inflate(Context context, int resource, ViewGroup root) {    LayoutInflater factory = LayoutInflater.from(context);    return factory.inflate(resource, root);}
```

</section>

当我们查看源码，就会发现，这个方法的内部实际上就是调用了 LayoutInflater 的 inflate 方法。正如此方法的注释所言，这是一个方便开发者调用的 LayoutInflater 的包装方法，而 LayoutInflater 本身则为 View 的渲染提供了更多的选择。

那么我们现在的问题就变成了， LayoutInflater 又做了什么？

继续追踪代码，我们会发现， LayoutInflator.from().inflate()  是这个样子的：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
// LayoutInflator#inflate(int, ViewGroup)public View inflate(@LayoutRes int resource, @Nullable ViewGroup root) {        return inflate(resource, root, root != null);}
```

</section>

啥？重载？

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
// LayoutInflator#inflate(int, ViewGroup, boolean)public View inflate(int resource, ViewGroup root, boolean attachToRoot) {    final Resources res = getContext().getResources();    final XmlResourceParser parser = res.getLayout(resource);    try {        return inflate(parser, root, attachToRoot);    } finally {        parser.close();    }}
```

</section>

这里我们看到，通过层层调用，最终会调用到 LayoutInflator#inflate(int, ViewGroup, boolean) 方法，很明显，这个方法会将我们传入的布局 id 转换为 XmlResourceParser，然后进行另一次，也是最后一次重载。

这个方法就厉害了，这里基本上包括了我们所有问题的答案，我们继续往下看。

<section class="" data-id="t2" style="line-height: 27.2px;white-space: normal;max-width: 100%;letter-spacing: 0.544px;box-sizing: border-box !important;overflow-wrap: break-word !important;background-color: rgb(255, 255, 255);">

<section class="" style="margin-top: 2em;margin-right: auto;margin-left: auto;padding-top: 0.5em;padding-bottom: 0.5em;max-width: 100%;box-sizing: border-box;width: 536px;border-top-width: 1px;border-top-style: solid;border-top-color: rgb(204, 204, 204);font-size: 1em;overflow-wrap: break-word !important;">

源码分析

</section>

</section>

话不多说，上代码。接下来我们来逐段分析下这个 inflate 方法：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {    final Context inflaterContext = mContext;    final AttributeSet attrs = Xml.asAttributeSet(parser);    // 默认返回结果为传入的根布局    View result = root;    // 通过 createViewFromTag() 方法找到传入的 layoutId 的根布局，并赋值给 temp    final View temp = createViewFromTag(root, name, inflaterContext, attrs);    ViewGroup.LayoutParams params = null;    // 如果传入的父布局不为空    if (root != null) {        // 为这个 root 生成一套合适的 LayoutParams        params = root.generateLayoutParams(attrs);        if (!attachToRoot) {            // 如果没有 attachToRoot，那为根布局设置 layoutparams            temp.setLayoutParams(params);        }    }    // 如果传入的父布局不为空，且想要 attachToRoot    if (root != null && attachToRoot) {        // 那就将传入的布局以及 layoutparams 通过 addView 方法添加到父布局中         root.addView(temp, params);    }    // 如果传入的根布局为空，或者不想 attachToRoot，则返回要加载的 layoutId    if (root == null || !attachToRoot) {        result = temp;    }    return result;}
```

</section>

代码也分析完了，我再来总结一下：

*   View#inflate 只是个简易的包装方法，实际上还是调用的 LayoutInflater#inflate ;

*   LayoutInflater#inflate 由于可以自己选择 root 和 attachToRoot 的搭配（后面有解释），使用起来更加灵活；

*   实际上的区别只是在于 root 是否传空，以及 attachToRoot 真假与否；

*   当  root 传空时，会直接返回要加载的 layoutId，返回的 View 没有父布局且没有 LayoutParams；

*   当  root 不传空时，又分为 attachToRoot 为真或者为假。

**attachToRoot = true**

会为传入的 `layoutId` 直接设置参数，并将其添加到 `root` 中，然后将传入的 `root` 返回；

**attachToRoot = false**

会为传入的 `layoutId` 设置参数，但是不会添加到 `root` ，然后返回 `layoutId` 对应的 View；

> 这里需要注意的是，虽然不马上将 View 添加到 parent 中，但是这里最好也传上 parent，而不是粗暴的传入 null；因为子 View 的 LayoutParams 需要由 parent 来确定；否则会在手动 addView 时调用 `generateDefaultLayoutParams()` 为子 View 生成一个宽高都为包裹内容的 LayoutParams，而这并不一定是我们想要的。

<section class="" data-id="t2" style="line-height: 27.2px;white-space: normal;max-width: 100%;letter-spacing: 0.544px;box-sizing: border-box !important;overflow-wrap: break-word !important;background-color: rgb(255, 255, 255);">

<section class="" style="margin-top: 2em;margin-right: auto;margin-left: auto;padding-top: 0.5em;padding-bottom: 0.5em;max-width: 100%;box-sizing: border-box;width: 536px;border-top-width: 1px;border-top-style: solid;border-top-color: rgb(204, 204, 204);font-size: 1em;overflow-wrap: break-word !important;">

测试 & 检验

</section>

</section>

单说起来可能有些抽象，下面使用代码来进行具体的测试与检验。

#### **View.inflate(context, layoutId, null)**

如之前所说，这实际上调用的是 getLayoutInflater().inflate(layoutId, null) ，结合之前的源码来看：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {    View result = root;    final View temp = createViewFromTag(root, name, inflaterContext, attrs);    if (root == null || !attachToRoot) {        result = temp;    }    return result;}
```

</section>

很明显，传入的 root 为空，则会直接将加载好的 xml 布局返回，而这种情况下返回的这个 View 没有参数，也没有父布局。

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
protected void onCreate(@Nullable Bundle savedInstanceState) {    super.onCreate(savedInstanceState);    setContentView(R.layout.layout_test);    View inflateView = View.inflate(this, R.layout.layout_basic_use_item, null);    Log.e("Test", "LayoutParams -> " + inflateView.getLayoutParams());    Log.e("Test", "Parent -> " + inflateView.getParent());}
```

</section>

![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt5mw4Ho1e8hQxbCibes2tMZcjGShqbyCh9ucKyAH1RKbJYON925EaaqshB6yfbzojLEq3T3S3BiaTeA/640?wx_fmt=png)

如图所示，正如我们想的，root 传 null 时，参数以及父布局返回结果均为 null。

#### **View.inflate(context, layoutId, mParent)**

按之前分析过的，此方法实际调用的是 getLayoutInflater().inflate(layoutId, root, true) ，再来看源码：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {    final Context inflaterContext = mContext;    final AttributeSet attrs = Xml.asAttributeSet(parser);    View result = root;     final View temp = createViewFromTag(root, name, inflaterContext, attrs);    ViewGroup.LayoutParams params = null;    if (root != null) {        params = root.generateLayoutParams(attrs);    }    if (root != null && attachToRoot) {        root.addView(temp, params);    }    return result;}
```

</section>

如源码所示，返回的 result 会在最开始就被赋值为入参的 root，root 不为空，同时 attachToRoot 为 true，就会将加载好的布局直接通过 addView 方法添加到 root 布局中，然后将 root 返回。

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
protected void onCreate(@Nullable Bundle savedInstanceState) {    super.onCreate(savedInstanceState);    setContentView(R.layout.layout_test);    LinearLayout mParent = findViewById(R.id.ll_root);    View inflateView = View.inflate(this, R.layout.layout_basic_use_item, mParent);    Log.e("Test", "LayoutParams -> " + inflateView.getLayoutParams());    Log.e("Test", "Parent -> " + inflateView.getParent());    Log.e("Test", "inflateView -> " + inflateView);}
```

</section>

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5mw4Ho1e8hQxbCibes2tMZcTdaGtxNj7hDvbCSDS3PBL6SdB0Ntnm7BkN8BOxn7XhNdvKFtt1pbdg/640?wx_fmt=jpeg)

如图示，返回的 View 正是我们传入的 mParent，对应的 id 是 ll_root，参数也不再为空。

#### **getLayoutInflater().inflate(layoutId, root, false)**

也许会有人问了，现在要么是 root 传空，返回 layoutId 对应的布局；要么是 root 不传空，返回传入的 root 布局。那我要是想 root 不传空，但是还是返回 layoutId 对应的布局呢？

这就是 View#inflate 的局限了，由于它是包装方法，因此 attachToRoot 并不能因需定制。这时候我们完全可以自己调用 getLayoutInflater().inflate(layoutId, root, false) 方法，手动的将第三个参数传为 false，同时为这个方法传入目标根布局。这样，我们就可以得到一个有 LayoutParams，但是没有 parentView 的 layoutId 布局了。

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
protected void onCreate(@Nullable Bundle savedInstanceState) {    super.onCreate(savedInstanceState);    setContentView(R.layout.layout_test);    LinearLayout mParent = findViewById(R.id.ll_root);    View inflateView = getLayoutInflater().inflate(R.layout.main, mParent, false);    Log.e("Test", "LayoutParams -> " + inflateView.getLayoutParams());    Log.e("Test", "Parent -> " + inflateView.getParent());}
```

</section>

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt5mw4Ho1e8hQxbCibes2tMZcSM2ZvoW5TiaGyvsZPw0H6jsYmvnb0xT1gYnneGRyInmhn6ZaoCtzuicw/640?wx_fmt=jpeg)

与我们分析的一致，有参数，但是没有父布局，且返回的就是我们加载的布局 id。我们在之后可以通过 addView 方法手动将这个布局加入父布局中。

这里还有个要注意的点，那就是 params = root.generateLayoutParams(attrs); 这句代码，我们会发现，为 layoutId 设置的 params 参数，实际上是通过 root 来生成的。这也就告诉我们，虽然不马上添加到 parent 中，但是这里最好也传上 parent，而不是粗暴的传入 null，因为子 View 的 LayoutParams 需要由 parent 来确定；当然，传入 null 也不会有问题，因为在执行 addView() 方法的时候，如果当前 childView 没有参数，会调用 generateDefaultLayoutParams() 生成一个宽高都包裹的 LayoutParams 赋值给 childView，而这并不一定是我们想要的。

#### attachToRoot 必须为 false！

代码写多了，大家有时候会发现这个 attachToRoot 也不是想怎样就怎样的，有时候它还就必须是 false，不能为 true。下面我们就来看看这些情况。

*   **RecylerView#onCreateViewHolder()**

在为 RecyclerView 创建 ViewHolder 时，由于 View 复用的问题，是 RecyclerView 来决定什么时候展示它的子 View，这个完全不由我们决定，这种情况下，attachToRoot 必须为 false：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: &quot;Helvetica Neue&quot;, Helvetica, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;">

```
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {        LayoutInflater inflater = LayoutInflater.from(getActivity());        View view = inflater.inflate(R.layout.item, parent, false);        return new ViewHolder(view);  }
```

</section>

*   **Fragment#onCreateView()**

由于 Fragment 需要依赖于 Activity 展示，一般在 Activity 中也会有容器布局来盛放 Fragment：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
Fragment fragment = new Fragment();getSupportFragmentManager()        .beginTransaction()        .add(R.id.root_container, fragment)        .commit(); 
```

</section>

上述代码中的 R.id.root_container 便为容器，这个 View 会作为参数传递给 Fragment#onCreateView() :

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;font-family: 'Helvetica Neue', Helvetica, 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;">

```
public View onCreateView(LayoutInflater inflater, ViewGroup container,                          Bundle savedInstanceState) {    return inflater.inflate(R.layout.fragment_layout, parentViewGroup, false); }
```

</section>

它也是你在 inflate() 方法中传入的 ViewGroup，FragmentManager 会将 Fragment 的 View 添加到 ViewGroup 中，言外之意就是，Fragment 对应的布局展示或者说添加进 ViewGroup 时也不是我们来控制的，而是 FragmentManager 来控制的。

<section class="" data-id="t2" style="line-height: 27.2px;white-space: normal;max-width: 100%;letter-spacing: 0.544px;box-sizing: border-box !important;overflow-wrap: break-word !important;background-color: rgb(255, 255, 255);">

<section class="" style="margin-top: 2em;margin-right: auto;margin-left: auto;padding-top: 0.5em;padding-bottom: 0.5em;max-width: 100%;box-sizing: border-box;width: 536px;border-top-width: 1px;border-top-style: solid;border-top-color: rgb(204, 204, 204);font-size: 1em;overflow-wrap: break-word !important;">

总结

</section>

</section>

总结一下就是，当我们不为子 View 的展示负责时，attachToRoot 必须为 false；否则就会出现对应的负责人，比如上面说的 Rv 或者 FragmentManager，已经把布局 id 添加到 ViewGroup 了，我们还继续设置 attachToRoot 为 true，想要手动 addView，那必然会发生 child already has parent 的错误。

推荐阅读：

[没想到 Flutter 也能实现如此丝般顺滑的上拉抽屉动画效果](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650245555&idx=1&sn=d155aa1b139a33316a3d37808e2b9d48&chksm=886378dcbf14f1ca905e49e6afcac2a6e15e802ced2d34063165fa97723a9a102277a0941ab5&scene=21#wechat_redirect)

[像小红书一样的图片裁剪控件联动效果](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650245492&idx=1&sn=e55a2d8758ecc8a4938b75aa085f7a67&chksm=8863781bbf14f10d8a8fdce17cb3bac713280d68428385be210c52b08e59abde68037c0672c9&scene=21#wechat_redirect)

[分享一个我开发的 MVVM 架构的开源小项目](http://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650245265&idx=1&sn=11083bc452fe295e996f7a94d25ff5f0&chksm=886379febf14f0e8b84329cd5b6d96d2abe300b54153bbec03b7ac8c9cbe3200881d407e7e1b&scene=21#wechat_redirect)

欢迎关注我的公众号，学习技术或投稿

![](http://mmbiz.qpic.cn/mmbiz/wyice8kFQhf4Mm0CFWFnXy6KtFpy8UlvN0DOM3fqc64fjEj9tw23yYSqujQjSQoU1rC0vicL9Mf0X6EMR4gFluJw/640.png?)

![](https://mmbiz.qpic.cn/mmbiz_jpg/v1LbPPWiaSt6FSn51QbdP1ic92cjsQM7LkBCfnaJMtcibMw9vYtdQ6QQM3CcFFbGqMoNucFlBRJw9E6VQWYk30ficw/640?wx_fmt=jpeg)

长按上图，识别图中二维码即可关注