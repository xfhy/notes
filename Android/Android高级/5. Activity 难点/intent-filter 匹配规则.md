> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/7ebc63399968 ![](http://upload-images.jianshu.io/upload_images/1986815-5199574dae6a1f56.jpg)

> 我们知道 Activity 的启动方式分为隐式和显式两种，至于两者的区别想必大家都已经很清楚了，显式需要明确的指定被启动对象的组件信息，比如包名和类名等，而隐式调用就不需要明确指定组件的信息。隐式调用就需要 Intent 能够匹配目标组件的 IntentFilter 中所设置的过滤信息，如果匹配不成功就不能启动目标 Activity。

IntentFiler 中过滤的信息包含 action，category，data，如下边示例所示，本文也将围绕这个示例进行讲解。

<pre><activity android:>
    <intent-filter>
        <action android:/>
        <action android:/>

        <category android:/>
        <category android:/>
        <category android:/>

        <data android:mimeType="text/plain"/>
    </intent-filter>
</activity>

</pre>

总的原则是：

1.  匹配过滤列表时需要同时匹配过滤列表中的 action，category，data；
2.  一个过滤列表中可以有多个 action，category，data 并各自构成不同类别，一个 Intent 必须同时匹配 action 类别，category 类别和 data 类别才算完全匹配；
3.  一个 Activity 中可以有多组 intent-filter，一个 Intent 只要匹配任何一组 intent-filter 就算匹配成功；

下边将分别介绍 3 种属性的匹配规则

##### 1.action 的匹配规则

我们知道 action 是一个字符串，系统默认帮我们定义了一些 action，同时我们也可以自定义。字符串中的字幕是严格区分大小写的，这个在匹配的时候是要注意的。关于 action 的匹配很简单，我们可以直接总结归纳一下：

> **Intent 中必须存在 action，这一点和 category 不同**；
> 
> **action 的字符串严格区分大小写，intent 中的 action 必须和过滤规则中的 action 完全一致才能匹配成功**；

> **匹配规则中可以同时有多个 action，但是 Intent 中的 action 只需与其中之一相同即可匹配成功**；

那么按照以上总结的规则，为了匹配本文开头的示例，那么 Intent 的 action
可以为 “com.leo.a” 或者是“com.leo.a”。

##### 2.category 的匹配规则

和 action 一样，category 也是一个字符串，系统同样默认定义了一些，我们一样可以自定义。其实 category 的匹配规则和 action 有很强的可比性，所以我们可以完全类比 action 来进行总结

> **匹配规则中必须添加 “android.intent.category.DEFAULT” 这个过滤条件；**

> **Intent 中可以不设置 category，这个时候你在使用 startActivity 或者 startActivityForResult 的时候，
> 其实系统自动会为你添加 1 中的那个默认 category；**

> **Intent 中可以同时设置多个 category，一旦设置多个 catrgory，
> 那么每个 category 都必须能够和过滤条件中的某个 category 匹配成功.**

我们发现 3 中的规则和 action 的匹配规则有所不同，action 有多个的时候，只要其中之一能够匹配成功即可，但是 category 必须是每一个都需要匹配成功，这个对比起来还是挺好记住的吧。那么按照这个规则的话，为了匹配示例中的过滤条件，我们可以这样设置 category：intent.addCategory("com.leo.c"); 或者是 intent.addCategory("com.leo.d"); 在或者是不设置 category

##### 3.data 的匹配规则

在介绍 data 的匹配之前，我觉得还是有必要再温故一下 data 的结构吧

<pre><data android:scheme="string"
      android:host="string"
      android:port="80"
      android:path="/string"
      android:pathPattern="string"
      android:pathPrefix="/string"
      android:mimeType="text/plain"/>

</pre>

总的来说 data 包含两部分：mimeType 和 URI。

1.  mimeType 表示 image/ipeg,video/* 等媒体类型
2.  URI 信息量相对大一点，其结构一般为：
    `<scheme>://<host>:<port>/[<path>|<pathPrefix|<pathPattern>>]`

下边讲分别来介绍下各个节点数据的含义

1.  scheme：整个 URI 的模式，如常见的 http，file 等，注意如果 URI 中没有指定的 scheme，那么整个 uri 无效
2.  host：URI 的域名，比如我们常见的 [www.mi.com,www.baidu.com](https://link.jianshu.com?t=http://www.mi.com,www.baidu.com)，与 scheme 一样，一旦没有 host 那么整个 URI 也毫无意义；
3.  port：端口号，比如 80，很容易理解，只有在 URI 中指定了 scheme 和 host 之后端口号才是有意义的；
4.  path，pathPattern，pathPrefix 包含路径信息，path 表示完整的路径，pathPattern 在此基础上可以包含通配符，pathPrefix 表示路径的前缀信息；

ok，data 的格式介绍完毕。

其实 data 的匹配规则和 action 也有点类似：

> **Intent 中必须有 data 数据**；

> **Intent 中的 data 必须和过滤规则中的某一个 data 完全匹配**；

> **过滤规则中可以有多个 data 存在，但是 Intent 中的 data 只需匹配其中的任意一个 data 即可**；

> **过滤规则中可以没有指定 URI，但是系统会赋予其默认值：content 和 file，这一点在 Intent 中需要注意**；

> **为 Intent 设定 data 的时候必须要调用 setDataAndType（）方法，而不能先 setData 再 setType，因为这两个方法是互斥的，都会清除对方的值，这个有兴趣可以参见源码**；

> **在匹配规则中，data 的 scheme，host，port，path 等属性可以写在同一个 </> 中，也可以分开单独写，其功效是一样的**；

好了，说到这里，action，category 和 data 的匹配规则基本都说完了，我们现在可以完全匹配一下文章开头的过滤条件示例了：

<pre> Intent intent = new Intent("com.leo.a");
 intent.addCategory("com.leo.c");
 intent.setDataAndType(Uri.parse("file://abc"), "text/plain");
 startActivity(intent);

</pre>

##### 4\. 匹配失败

前边我们说到 data 的 URI 默认是 content 和 file，所以如果你把 intent.setDataAndType(Uri.parse("file://abc"), "text/plain"); 中的 URI "file://abc" 改为 "[http://abc](https://link.jianshu.com?t=http://abc)" 的话，就会出现以下异常

![](http://upload-images.jianshu.io/upload_images/1986815-f9fd968f17545304.png)

其实包括 action，category 和 data 在内所有的匹配失败都会报这个异常，提示也很清晰明了。

##### 5\. 杜绝异常的发生

为了避免发生以上异常，我们可以使用 PackageManager 或者 Intent 的 resolveActivity（）方法，，如果 intent 和过滤规则匹配失败，那么将返回 null，我们也就不会再继续调用 startActivity 方法啦，从而去修改 intent 再次进行匹配直到成功匹配到预期 intent。

##### 6\. 其他

前文中也提到了在 action 和 category 中有一些是系统自带的，其中有些比较重要，比如

<pre><action android:/>

<category android:/>

</pre>

这两者一般成双成对出现，用来表明这是一个程序的入口，并且会出现在系统的应用列表中。之所以成双成对的出现是说，二者缺一不可，少了彼此都没有实际意义。

另外本文讲到的所有 intent-filter 匹配规则同样适用于 Service 和 BroadcastReceiver，但是在 Android5.0 以后需要显式调用来启动 Service，否则会报异常：java.lang.IllegalArgumentException: Service Intent must be explicit。

![](http://upload-images.jianshu.io/upload_images/1986815-ef05b12cabf8aa41.jpg) 扫码快速关注