> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/guolin_blog/article/details/50727753 版权声明：本文出自郭霖的博客，转载必须注明出处。 https://blog.csdn.net/sinyu890807/article/details/50727753

转载请注明出处：[http://blog.csdn.net/guolin_blog/article/details/50727753](http://blog.csdn.net/guolin_blog/article/details/50727753)
好像有挺久时间没更新博客了，最近我为了准备下一个系列的博客，也是花了很长的时间研读源码。很遗憾的是，下一个系列的博客我可能还要再过一段时间才能写出来，那么为了不至于让大家等太久，今天就给大家更新一篇单篇的文章，讲一讲 Android drawable 方面的微技巧。

* * *

话说微技巧这个词也是我自己发明的，因为 drawable 这个东西相信大家天天都在使用，每个人都再熟悉不过了，之所以叫微技巧就是对于这个我们再熟悉不过的技术，可能还有一些你所不知道的细节，那今天我们就来一起探究一下这些微小的细节吧。

大家都知道，在 Android 项目当中，drawable 文件夹都是用来放置图片资源的，不管是 jpg、png、还是 9.png，都可以放在这里。除此之外，还有像 selector 这样的 xml 文件也是可以放在 drawable 文件夹下面的。

但是如果你现在使用 Android Studio 来新建一个项目，你会发现有如下的目录结构：

<center>![](https://img-blog.csdn.net/20160422203208418)</center>

嗯？怎么会有这么多 mipmap 开头的文件夹，而且它们的命名规则和 drawable 文件夹很相似，也是 hdpi、mdpi、xhdpi 等等，并且里面还真是放的图片，难道 Android 项目中放置图片的位置已经改了？

对于刚刚从 Eclipse 转向 Android Studio 的开发者们可能会对 mipmap 文件夹感到陌生，其实不用担心，我们平时的编程习惯并不需要发生任何改变，因为 mipmap 文件夹只是用来放置应用程序的 icon 的，仅此而已。那么在此之前，我们都是把应用程序的 icon 图标和普通的图片资源一起放到 drawable 文件夹下的，这样看上去就会比较杂乱，有的时候想从一堆的图片资源里面找 icon 半天也找不到，而文件一多也就容易出现漏放的情况，但恰恰 Android 是极度建议我们在每一种分辨率的文件夹下面都放一个相应尺寸的 icon 的，因此将它们独立出来专门放到 mimap 文件夹当中就很好地解决了这个问题。

另外，将 icon 放置在 mipmap 文件夹还可以让我们程序的 launcher 图标自动拥有跨设备密度展示的能力，比如说一台屏幕密度是 xxhdpi 的设备可以自动加载 mipmap-xxxhdpi 下的 icon 来作为应用程序的 launcher 图标，这样图标看上去就会更加细腻。

关于建议使用 mipmap 的原文可以参阅这篇文章：[Getting Your Apps Ready for Nexus 6 and Nexus 9](http://android-developers.blogspot.de/2014/10/getting-your-apps-ready-for-nexus-6-and.html)， 当然你还是要科学上网的。

除此之外，对于每种密度下的 icon 应该设计成什么尺寸其实 Android 也是给出了最佳建议，icon 的尺寸最好不要随意设计，因为过低的分辨率会造成图标模糊，而过高的分辨率只会徒增 APK 大小。建议尺寸如下表所示：

| 密度 | 建议尺寸 |
| --- | --- |
| mipmap-mdpi | 48 * 48 |
| mipmap-hdpi | 72 * 72 |
| mipmap-xhdpi | 96 * 96 |
| mipmap-xxhdpi | 144 * 144 |
| mipmap-xxxhdpi | 192 * 192 |

然后我们引用 mipmap 的方式和之前引用 drawable 的方式是完全一致的，在资源中就使用 @mipmap/res_id，在代码就使用 R.mipmap.res_id。比如 AndroidManifest.xml 中就是这样引用 ic_launcher 图标的：

```
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:supportsRtl="true"
    android:theme="@style/AppTheme">
    <activity android:>
        <intent-filter>
            <action android:/>
            <category android:/>
        </intent-filter>
    </activity>
</application>
```

好的，关于 mimap 的内容就讲这么多，它并不是本篇文章的重点，接下来我们来真真正正看一些 drawable 的微技巧。

* * *

首先我准备了一张 270*480 像素的图片：

<center>![](https://img-blog.csdn.net/20160424154510643)</center>

将图片命名为 android_logo.png，然后把它放在 drawable-xxhdpi 文件夹下面。为什么要放在这个文件夹下呢？是因为我的手机屏幕的密度就是 xxhdpi 的。那么怎么才能知道自己手机屏幕的密度呢？你可以使用如下方法先获取到屏幕的 dpi 值：

```
float xdpi = getResources().getDisplayMetrics().xdpi;
float ydpi = getResources().getDisplayMetrics().ydpi;
```

其中 xdpi 代表屏幕宽度的 dpi 值，ydpi 代表屏幕高度的 dpi 值，通常这两个值都是近乎相等或者极其接近的，在我的手机上这两个值都约等于 403。那么 403 又代表着什么意思呢？我们直接参考下面这个表格就知道了：

| dpi 范围 | 密度 |
| --- | --- |
| 0dpi ~ 120dpi | ldpi |
| 120dpi ~ 160dpi | mdpi |
| 160dpi ~ 240dpi | hdpi |
| 240dpi ~ 320dpi | xhdpi |
| 320dpi ~ 480dpi | xxhdpi |
| 480dpi ~ 640dpi | xxxhdpi |

从表中可以看出，403dpi 是处于 320dpi 到 480dpi 之间的，因此属于 xxhdpi 的范围。

图片放好了之后，下面我在布局文件中引用这张图片，如下所示：

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
   >

    <ImageView
        android:id="@+id/image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/android_logo"
        />

</LinearLayout>
```

在 ImageView 控件中指定加载 android_logo 这张图，并把 ImageView 控件的宽高都设置成 wrap_content，这样图片有多大，我们的控件就会有多大。

现在运行一下程序，效果如下所示：

<center>![](https://img-blog.csdn.net/20160423155934999)</center>

由于我的手机分辨率是 1080*1920 像素的，而这张图片的分辨率是 270*480 像素的，刚好是手机分辨率的四分之一，因此从上图中也可以看出，android_logo 图片的宽和高大概都占据了屏幕宽高的四分之一左右，大小基本是比较精准的。

到目前为止一切都挺顺利的，不是吗？下面我们尝试做点改变，将 android_logo.png 这张图移动到 drawable-xhdpi 文件夹下，注意不是复制一份到 drawable-xhdpi 文件夹下，而是将图片移动到 drawable-xhdpi 文件夹下，然后重新运行一下程序，效果如下图所示：

<center>![](https://img-blog.csdn.net/20160423160735251)</center>

嗯？怎么感觉图片好像变大了一点，是错觉吗？

那么我们再将这张图移动到 drawable-mdpi 文件夹下试试，重新运行程序，效果如下图所示：

<center>![](https://img-blog.csdn.net/20160423161009361)</center>

这次肯定不是错觉了，这实在是太明显了，图片被放大了！

那么为什么好端端的一张图片会被自动放大呢？而且这放大的比例是不是有点太过份了。其实不然，Android 所做的这些缩放操作都是有它严格的规定和算法的。可能有不少做了很多年 Android 的朋友都没去留意过这些缩放的规则，因为这些细节太微小了，那么本篇的微技巧探索里面，我们就来把这些细节理理清楚。

首先解释一下图片为什么会被放大，当我们使用资源 id 来去引用一张图片时，Android 会使用一些规则来去帮我们匹配最适合的图片。什么叫最适合的图片？比如我的手机屏幕密度是 xxhdpi，那么 drawable-xxhdpi 文件夹下的图片就是最适合的图片。因此，当我引用 android_logo 这张图时，如果 drawable-xxhdpi 文件夹下有这张图就会优先被使用，在这种情况下，图片是不会被缩放的。但是，如果 drawable-xxhdpi 文件夹下没有这张图时， 系统就会自动去其它文件夹下找这张图了，优先会去更高密度的文件夹下找这张图片，我们当前的场景就是 drawable-xxxhdpi 文件夹，然后发现这里也没有 android_logo 这张图，接下来会尝试再找更高密度的文件夹，发现没有更高密度的了，这个时候会去 drawable-nodpi 文件夹找这张图，发现也没有，那么就会去更低密度的文件夹下面找，依次是 drawable-xhdpi -> drawable-hdpi -> drawable-mdpi -> drawable-ldpi。
总体匹配规则就是这样，那么比如说现在终于在 drawable-mdpi 文件夹下面找到 android_logo 这张图了，但是系统会认为你这张图是专门为低密度的设备所设计的，如果直接将这张图在当前的高密度设备上使用就有可能会出现像素过低的情况，于是系统自动帮我们做了这样一个放大操作。

那么同样的道理，如果系统是在 drawable-xxxhdpi 文件夹下面找到这张图的话，它会认为这张图是为更高密度的设备所设计的，如果直接将这张图在当前设备上使用就有可能会出现像素过高的情况，于是会自动帮我们做一个缩小的操作。所以，我们可以尝试将 android_logo 这张图移动到 drawable-xxxhdpi 文件夹下面将会得到这样的结果：

<center>![](https://img-blog.csdn.net/20160423172553614)</center>

可以看到，现在图片的宽和高都达到不手机屏幕的四分之一，说明图片确实是被缩小了。

另外，刚才在介绍规则的时候提到了一个 drawable-nodpi 文件夹，这个文件夹是一个密度无关的文件夹，放在这里的图片系统就不会对它进行自动缩放，原图片是多大就会实际展示多大。但是要注意一个加载的顺序，drawable-nodpi 文件夹是在匹配密度文件夹和更高密度文件夹都找不到的情况下才会去这里查找图片的，因此放在 drawable-nodpi 文件夹里的图片通常情况下不建议再放到别的文件夹里面。

图片被放大的原因现在我们已经搞清楚了，那么接下来还有一个问题，就是放大的倍数是怎么确定的呢？很遗憾，我没有找到相关的文档记载，但是我自己总结出了一个规律，这里跟大家分享一下。

还是看一下刚才的 dpi 范围 - 密度 表格：

| dpi 范围 | 密度 |
| --- | --- |
| 0dpi ~ 120dpi | ldpi |
| 120dpi ~ 160dpi | mdpi |
| 160dpi ~ 240dpi | hdpi |
| 240dpi ~ 320dpi | xhdpi |
| 320dpi ~ 480dpi | xxhdpi |
| 480dpi ~ 640dpi | xxxhdpi |

可以看到，每一种密度的 dpi 范围都有一个最大值，这个最大值之间的比例就是图片会被系统自动放大的比例。
口说无凭，下面我们来通过实例验证一下，修改布局文件中的代码，如下所示：

```
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
   >

    <ImageView
        android:id="@+id/image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/android_logo"
        />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="获取图片宽高"
        android:onClick="buttonClick"
        />

</LinearLayout>
```

可以看到，我们添加了一个按钮，并给按钮注册了一个点击事件。然后在 MainActivity 中处理这个点击事件：

```
public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image);
    }

    public void buttonClick(View view) {
        Toast.makeText(this, "图片宽度：" + imageView.getWidth(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "图片高度：" + imageView.getHeight(), Toast.LENGTH_SHORT).show();
    }
}
```

这里在点击事件中分别获取图片的宽和高并使用 Toast 提示出来。代码修改这么多就可以了，然后将图片移动到 drawable-mdpi 文件夹下。

下面我们来开始分析，mdpi 密度的最高 dpi 值是 160，而 xxhdpi 密度的最高 dpi 值是 480，因此是一个 3 倍的关系，那么我们就可以猜测，放到 drawable-mdpi 文件夹下的图片在 xxhdpi 密度的设备上显示会被放大 3 倍。对应到 android_logo 这张图，原始像素是 270*480，放大 3 倍之后就应该是 810*1440 像素。下面运行程序，效果如下图所示：

<center>![](https://img-blog.csdn.net/20160423175803621)</center>

验证通过。我们再来试验一次，将图片移动到 drawable-xxxhdpi 目录下。xxxhdpi 密度的最高 dpi 值是 640，480 是它的 0.75 倍，那么我们就可以猜测，放到 drawable-xxxdpi 文件夹下的图片在 xxhdpi 密度的设备上显示会被缩小至 0.75 倍。270*480 的 0.75 倍应该是 202.5*360，由于像素不支持小数点，那么四舍五入就应该是 203*360 像素。重新运行程序，效果如下图所示：

<center>![](https://img-blog.csdn.net/20160423180631696)</center>

再次验证通过。如果你有兴趣的话可以使用其它几种 dpi 的 drawable 文件夹来试一试，应该都是适配这套缩放规则的。这样我们就把图片为什么会被缩放，以及具体的缩放倍数都搞明白了，drawable 相关的细节你已经探究的非常细微了。

不过本篇文章到这里还没结束，下面我准备讲一讲我们在实际开发当中会遇到的场景。根据 Android 的开发建议，我们在准备图片资源时尽量应该给每种密度的设备都准备一套，这样程序的适配性就可以达到最好。但实际情况是，公司的 UI 们通常就只会给一套图片资源，想让他们针对每种密度的设备都设计一套图片资源，并且还是按照我们上面讲的缩放比例规则来设计，就有点想得太开心了。没错，这个就是现实情况，那么在这种情况下，我们应该将仅有的这一套图片资源放在哪个密度的文件夹下呢？

可以这样来分析，根据我们刚才所学的内容，如果将一张图片放在低密度文件夹下，那么在高密度设备上显示图片时就会被自动放大，而如果将一张图片放在高密度文件夹下，那么在低密度设备上显示图片时就会被自动缩小。那我们可以通过成本的方式来评估一下，一张原图片被缩小了之后显示其实并没有什么副作用，但是一张原图片被放大了之后显示就意味着要占用更多的内存了。因为图片被放大了，像素点也就变多了，而每个像素点都是要占用内存的。

我们仍然可以通过例子来直观地体会一下，首先将 android_logo.png 图片移动到 drawable-xxhdpi 目录下，运行程序后我们通过 Android Monitor 来观察程序内存使用情况：

<center>![](https://img-blog.csdn.net/20160423221840743)</center>

可以看到，程序所占用的内存大概稳定在 19.45M 左右。然后将 android_logo.png 图片移动到 drawable-mdpi 目录下，重新运行程序，结果如下图所示：

<center>![](https://img-blog.csdn.net/20160423222108212)</center>

现在涨到 23.40M 了，占用内存明显增加了。如果你将图片移动到 drawable-ldpi 目录下，你会发现占用内存会更高。

通过这个例子同时也验证了一个问题，我相信有不少比较有经验的 Android 程序员可能都遇到过这个情况，就是当你的项目变得越来越大，有的时候加载一张 drawable-hdpi 下的图片，程序就直接 OOM 崩掉了，但如果将这张图放到 drawable-xhdpi 或 drawable-xxhdpi 下就不会崩掉，其实就是这个道理。

那么经过上面一系列的分析，答案自然也就出来了，图片资源应该尽量放在高密度文件夹下，这样可以节省图片的内存开支，而 UI 在设计图片的时候也应该尽量面向高密度屏幕的设备来进行设计。就目前来讲，最佳放置图片资源的文件夹就是 drawable-xxhdpi。那么有的朋友可能会问了，不是还有更高密度的 drawable-xxxhdpi 吗？干吗不放在这里？这是因为，市面上 480dpi 到 640dpi 的设备实在是太少了，如果针对这种级别的屏幕密度来设计图片，图片在不缩放的情况下本身就已经很大了，基本也起不到节省内存开支的作用了。

* * *

好的，关于 drawable 微技巧方面的探索我们就讲到这里，本篇文章中也是集合了不少我平时的工作经验总结，以及通过做试验所得出的一些结论，相信还是可以给大家带来不少帮助的。后面我会抓紧时间继续准备新系列的内容，敬请期待。

> 关注我的技术公众号，每天都有优质技术文章推送。关注我的娱乐公众号，工作、学习累了的时候放松一下自己。
> 
> 微信扫一扫下方二维码即可关注：
> 
> ![](https://img-blog.csdn.net/20160507110203928)         ![](https://img-blog.csdn.net/20161011100137978)

<link href="https://csdnimg.cn/release/phoenix/mdeditor/markdown_views-a47e74522c.css" rel="stylesheet">