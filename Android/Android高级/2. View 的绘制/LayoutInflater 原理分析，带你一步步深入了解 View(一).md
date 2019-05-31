> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/guolin_blog/article/details/12921889 版权声明：本文出自郭霖的博客，转载必须注明出处。 https://blog.csdn.net/sinyu890807/article/details/12921889 <link rel="stylesheet" href="https://csdnimg.cn/release/phoenix/template/css/ck_htmledit_views-f57960eb32.css"> <link rel="stylesheet" href="https://csdnimg.cn/release/phoenix/template/css/ck_htmledit_views-f57960eb32.css">

转载请注明出处：[http://blog.csdn.net/guolin_blog/article/details/12921889](http://blog.csdn.net/guolin_blog/article/details/12921889)

有段时间没写博客了，感觉都有些生疏了呢。最近繁忙的工作终于告一段落，又有时间写文章了，接下来还会继续坚持每一周篇的节奏。

有不少朋友跟我反应，都希望我可以写一篇关于 View 的文章，讲一讲 View 的工作原理以及自定义 View 的方法。没错，承诺过的文章我是一定要兑现的，而且在 View 这个话题上我还准备多写几篇，尽量能将这个知识点讲得透彻一些。那么今天就从 LayoutInflater 开始讲起吧。

相信接触 Android 久一点的朋友对于 LayoutInflater 一定不会陌生，都会知道它主要是用于加载布局的。而刚接触 Android 的朋友可能对 LayoutInflater 不怎么熟悉，因为加载布局的任务通常都是在 Activity 中调用 setContentView() 方法来完成的。其实 setContentView() 方法的内部也是使用 LayoutInflater 来加载布局的，只不过这部分源码是 internal 的，不太容易查看到。那么今天我们就来把 LayoutInflater 的工作流程仔细地剖析一遍，也许还能解决掉某些困扰你心头多年的疑惑。

先来看一下 LayoutInflater 的基本用法吧，它的用法非常简单，首先需要获取到 LayoutInflater 的实例，有两种方法可以获取到，第一种写法如下：

```
LayoutInflater layoutInflater = LayoutInflater.from(context);
```

当然，还有另外一种写法也可以完成同样的效果：

```
LayoutInflater layoutInflater = (LayoutInflater) context		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
```

其实第一种就是第二种的简单写法，只是 Android 给我们做了一下封装而已。得到了 LayoutInflater 的实例之后就可以调用它的 inflate() 方法来加载布局了，如下所示：

```
layoutInflater.inflate(resourceId, root);
```

inflate() 方法一般接收两个参数，第一个参数就是要加载的布局 id，第二个参数是指给该布局的外部再嵌套一层父布局，如果不需要就直接传 null。这样就成功成功创建了一个布局的实例，之后再将它添加到指定的位置就可以显示出来了。

下面我们就通过一个非常简单的小例子，来更加直观地看一下 LayoutInflater 的用法。比如说当前有一个项目，其中 MainActivity 对应的布局文件叫做 activity_main.xml，代码如下所示：

```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"    android:id="@+id/main_layout"    android:layout_width="match_parent"    android:layout_height="match_parent" > </LinearLayout>
```

这个布局文件的内容非常简单，只有一个空的 LinearLayout，里面什么控件都没有，因此界面上应该不会显示任何东西。

那么接下来我们再定义一个布局文件，给它取名为 button_layout.xml，代码如下所示：

```
<Button xmlns:android="http://schemas.android.com/apk/res/android"    android:layout_width="wrap_content"    android:layout_height="wrap_content"    android:text="Button" > </Button>
```

这个布局文件也非常简单，只有一个 Button 按钮而已。现在我们要想办法，如何通过 LayoutInflater 来将 button_layout 这个布局添加到主布局文件的 LinearLayout 中。根据刚刚介绍的用法，修改 MainActivity 中的代码，如下所示：

```
public class MainActivity extends Activity { 	private LinearLayout mainLayout; 	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		setContentView(R.layout.activity_main);		mainLayout = (LinearLayout) findViewById(R.id.main_layout);		LayoutInflater layoutInflater = LayoutInflater.from(this);		View buttonLayout = layoutInflater.inflate(R.layout.button_layout, null);		mainLayout.addView(buttonLayout);	} }
```

可以看到，这里先是获取到了 LayoutInflater 的实例，然后调用它的 inflate() 方法来加载 button_layout 这个布局，最后调用 LinearLayout 的 addView() 方法将它添加到 LinearLayout 中。

现在可以运行一下程序，结果如下图所示：

![](https://img-blog.csdn.net/20131217222059562?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ3VvbGluX2Jsb2c=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

Button 在界面上显示出来了！说明我们确实是借助 LayoutInflater 成功将 button_layout 这个布局添加到 LinearLayout 中了。LayoutInflater 技术广泛应用于需要动态添加 View 的时候，比如在 ScrollView 和 ListView 中，经常都可以看到 LayoutInflater 的身影。

当然，仅仅只是介绍了如何使用 LayoutInflater 显然是远远无法满足大家的求知欲的，知其然也要知其所以然，接下来我们就从源码的角度上看一看 LayoutInflater 到底是如何工作的。

不管你是使用的哪个 inflate() 方法的重载，最终都会辗转调用到 LayoutInflater 的如下代码中：

```
public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {    synchronized (mConstructorArgs) {        final AttributeSet attrs = Xml.asAttributeSet(parser);        mConstructorArgs[0] = mContext;        View result = root;        try {            int type;            while ((type = parser.next()) != XmlPullParser.START_TAG &&                    type != XmlPullParser.END_DOCUMENT) {            }            if (type != XmlPullParser.START_TAG) {                throw new InflateException(parser.getPositionDescription()                        + ": No start tag found!");            }            final String name = parser.getName();            if (TAG_MERGE.equals(name)) {                if (root == null || !attachToRoot) {                    throw new InflateException("merge can be used only with a valid "                            + "ViewGroup root and attachToRoot=true");                }                rInflate(parser, root, attrs);            } else {                View temp = createViewFromTag(name, attrs);                ViewGroup.LayoutParams params = null;                if (root != null) {                    params = root.generateLayoutParams(attrs);                    if (!attachToRoot) {                        temp.setLayoutParams(params);                    }                }                rInflate(parser, temp, attrs);                if (root != null && attachToRoot) {                    root.addView(temp, params);                }                if (root == null || !attachToRoot) {                    result = temp;                }            }        } catch (XmlPullParserException e) {            InflateException ex = new InflateException(e.getMessage());            ex.initCause(e);            throw ex;        } catch (IOException e) {            InflateException ex = new InflateException(                    parser.getPositionDescription()                    + ": " + e.getMessage());            ex.initCause(e);            throw ex;        }        return result;    }}
```

从这里我们就可以清楚地看出，LayoutInflater 其实就是使用 Android 提供的 pull 解析方式来解析布局文件的。不熟悉 pull 解析方式的朋友可以网上搜一下，教程很多，我就不细讲了，这里我们注意看下第 23 行，调用了 createViewFromTag() 这个方法，并把节点名和参数传了进去。看到这个方法名，我们就应该能猜到，它是用于根据节点名来创建 View 对象的。确实如此，在 createViewFromTag() 方法的内部又会去调用 createView() 方法，然后使用反射的方式创建出 View 的实例并返回。

当然，这里只是创建出了一个根布局的实例而已，接下来会在第 31 行调用 rInflate() 方法来循环遍历这个根布局下的子元素，代码如下所示：

```
private void rInflate(XmlPullParser parser, View parent, final AttributeSet attrs)        throws XmlPullParserException, IOException {    final int depth = parser.getDepth();    int type;    while (((type = parser.next()) != XmlPullParser.END_TAG ||            parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {        if (type != XmlPullParser.START_TAG) {            continue;        }        final String name = parser.getName();        if (TAG_REQUEST_FOCUS.equals(name)) {            parseRequestFocus(parser, parent);        } else if (TAG_INCLUDE.equals(name)) {            if (parser.getDepth() == 0) {                throw new InflateException("<include /> cannot be the root element");            }            parseInclude(parser, parent, attrs);        } else if (TAG_MERGE.equals(name)) {            throw new InflateException("<merge /> must be the root element");        } else {            final View view = createViewFromTag(name, attrs);            final ViewGroup viewGroup = (ViewGroup) parent;            final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);            rInflate(parser, view, attrs);            viewGroup.addView(view, params);        }    }    parent.onFinishInflate();}
```

可以看到，在第 21 行同样是 createViewFromTag() 方法来创建 View 的实例，然后还会在第 24 行递归调用 rInflate() 方法来查找这个 View 下的子元素，每次递归完成后则将这个 View 添加到父布局当中。

这样的话，把整个布局文件都解析完成后就形成了一个完整的 DOM 结构，最终会把最顶层的根布局返回，至此 inflate() 过程全部结束。

比较细心的朋友也许会注意到，inflate() 方法还有个接收三个参数的方法重载，结构如下：

```
inflate(int resource, ViewGroup root, boolean attachToRoot)
```

那么这第三个参数 attachToRoot 又是什么意思呢？其实如果你仔细去阅读上面的源码应该可以自己分析出答案，这里我先将结论说一下吧，感兴趣的朋友可以再阅读一下源码，校验我的结论是否正确。

1\. 如果 root 为 null，attachToRoot 将失去作用，设置任何值都没有意义。

2\. 如果 root 不为 null，attachToRoot 设为 true，则会给加载的布局文件的指定一个父布局，即 root。

3\. 如果 root 不为 null，attachToRoot 设为 false，则会将布局文件最外层的所有 layout 属性进行设置，当该 view 被添加到父 view 当中时，这些 layout 属性会自动生效。

4\. 在不设置 attachToRoot 参数的情况下，如果 root 不为 null，attachToRoot 参数默认为 true。

好了，现在对 LayoutInflater 的工作原理和流程也搞清楚了，你该满足了吧。额。。。。还嫌这个例子中的按钮看起来有点小，想要调大一些？那简单的呀，修改 button_layout.xml 中的代码，如下所示：

```
<Button xmlns:android="http://schemas.android.com/apk/res/android"    android:layout_width="300dp"    android:layout_height="80dp"    android:text="Button" > </Button>
```

这里我们将按钮的宽度改成 300dp，高度改成 80dp，这样够大了吧？现在重新运行一下程序来观察效果。咦？怎么按钮还是原来的大小，没有任何变化！是不是按钮仍然不够大，再改大一点呢？还是没有用！

其实这里不管你将 Button 的 layout_width 和 layout_height 的值修改成多少，都不会有任何效果的，因为这两个值现在已经完全失去了作用。平时我们经常使用 layout_width 和 layout_height 来设置 View 的大小，并且一直都能正常工作，就好像这两个属性确实是用于设置 View 的大小的。而实际上则不然，它们其实是用于设置 View 在布局中的大小的，也就是说，首先 View 必须存在于一个布局中，之后如果将 layout_width 设置成 match_parent 表示让 View 的宽度填充满布局，如果设置成 wrap_content 表示让 View 的宽度刚好可以包含其内容，如果设置成具体的数值则 View 的宽度会变成相应的数值。这也是为什么这两个属性叫作 layout_width 和 layout_height，而不是 width 和 height。

再来看一下我们的 button_layout.xml 吧，很明显 Button 这个控件目前不存在于任何布局当中，所以 layout_width 和 layout_height 这两个属性理所当然没有任何作用。那么怎样修改才能让按钮的大小改变呢？解决方法其实有很多种，最简单的方式就是在 Button 的外面再嵌套一层布局，如下所示：

```
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"    android:layout_width="match_parent"    android:layout_height="match_parent" >     <Button        android:layout_width="300dp"        android:layout_height="80dp"        android:text="Button" >    </Button> </RelativeLayout>
```

可以看到，这里我们又加入了一个 RelativeLayout，此时的 Button 存在与 RelativeLayout 之中，layout_width 和 layout_height 属性也就有作用了。当然，处于最外层的 RelativeLayout，它的 layout_width 和 layout_height 则会失去作用。现在重新运行一下程序，结果如下图所示：

![](https://img-blog.csdn.net/20131218220447843?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ3VvbGluX2Jsb2c=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

OK！按钮的终于可以变大了，这下总算是满足大家的要求了吧。

看到这里，也许有些朋友心中会有一个巨大的疑惑。不对呀！平时在 Activity 中指定布局文件的时候，最外层的那个布局是可以指定大小的呀，layout_width 和 layout_height 都是有作用的。确实，这主要是因为，在 setContentView() 方法中，Android 会自动在布局文件的最外层再嵌套一个 FrameLayout，所以 layout_width 和 layout_height 属性才会有效果。那么我们来证实一下吧，修改 MainActivity 中的代码，如下所示：

```
public class MainActivity extends Activity { 	private LinearLayout mainLayout; 	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		setContentView(R.layout.activity_main);		mainLayout = (LinearLayout) findViewById(R.id.main_layout);		ViewParent viewParent = mainLayout.getParent();		Log.d("TAG", "the parent of mainLayout is " + viewParent);	} }
```

可以看到，这里通过 findViewById() 方法，拿到了 activity_main 布局中最外层的 LinearLayout 对象，然后调用它的 getParent() 方法获取它的父布局，再通过 Log 打印出来。现在重新运行一下程序，结果如下图所示：

 ![](https://img-blog.csdn.net/20131218222529609?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ3VvbGluX2Jsb2c=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

非常正确！LinearLayout 的父布局确实是一个 FrameLayout，而这个 FrameLayout 就是由系统自动帮我们添加上的。

说到这里，虽然 setContentView() 方法大家都会用，但实际上 Android 界面显示的原理要比我们所看到的东西复杂得多。任何一个 Activity 中显示的界面其实主要都由两部分组成，标题栏和内容布局。标题栏就是在很多界面顶部显示的那部分内容，比如刚刚我们的那个例子当中就有标题栏，可以在代码中控制让它是否显示。而内容布局就是一个 FrameLayout，这个布局的 id 叫作 content，我们调用 setContentView() 方法时所传入的布局其实就是放到这个 FrameLayout 中的，这也是为什么这个方法名叫作 setContentView()，而不是叫 setView()。

最后再附上一张 Activity 窗口的组成图吧，以便于大家更加直观地理解：

![](https://img-blog.csdn.net/20131218231254906?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ3VvbGluX2Jsb2c=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

好了，今天就讲到这里了，支持的、吐槽的、有疑问的、以及打酱油的路过朋友尽管留言吧 ^v^ 感兴趣的朋友可以继续阅读 [Android 视图绘制流程完全解析，带你一步步深入了解 View(二)](http://blog.csdn.net/guolin_blog/article/details/16330267) 。

> 关注我的技术公众号，每天都有优质技术文章推送。关注我的娱乐公众号，工作、学习累了的时候放松一下自己。
> 
> 微信扫一扫下方二维码即可关注：
> 
> ![](https://img-blog.csdn.net/20160507110203928)         ![](https://img-blog.csdn.net/20161011100137978)