> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA

![](https://mmbiz.qpic.cn/mmbiz_gif/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGKxX2oJgeqbjpxh7f4WlhS3ms9ic3icv8Iibg30Bj1tgpgG2zSuaKxExmQ/640?wx_fmt=gif)

每天叫醒你的不是闹钟，而是姿势

在 Android 开发中，由于 Android 碎片化严重，屏幕分辨率千奇百怪，而想要在各种分辨率的设备上显示基本一致的效果，适配成本越来越高。虽然 Android 官方提供了 dp 单位来适配，但其在各种奇怪分辨率下表现却不尽如人意，因此下面探索一种简单且低侵入的适配方式。

![](https://mmbiz.qpic.cn/mmbiz_png/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGY863ov6JALom5d4IcDNfhaQ82wu7zWbicNEGMZEeQQzbtZO6s3uFcdA/640?wx_fmt=png)

**传统 dp 适配方式的缺点**

android 中的 dp 在渲染前会将 dp 转为 px，计算公式：  

*   px = density * dp;
    
*   density = dpi / 160;
    
*   px = dp * (dpi / 160);
    

而 dpi 是根据屏幕真实的分辨率和尺寸来计算的，每个设备都可能不一样的。

**屏幕尺寸、分辨率、像素密度三者关系**

通常情况下，一部手机的分辨率是宽 x 高，屏幕大小是以寸为单位，那么三者的关系是：

![](https://mmbiz.qpic.cn/mmbiz_png/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYG51prmqwNCLAVALyK5Rhv4uSbrU5FQKQL6bZI3iaibTJaz3NMpEQ8zWAA/640?wx_fmt=png)

举个例子：屏幕分辨率为：1920*1080，屏幕尺寸为 5 吋的话，那么 dpi 为 440。  

![](https://mmbiz.qpic.cn/mmbiz_png/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYG0aE9VoUJylxjwZHClXzKeeiadnQyvpLwsyZfES4axmPkmrwZ1jtyibKA/640?wx_fmt=png)

**这样会存在什么问题呢？**

假设我们 UI 设计图是按屏幕宽度为 360dp 来设计的，那么在上述设备上，屏幕宽度其实为 1080/(440/160)=392.7dp，也就是屏幕是比设计图要宽的。这种情况下， 即使使用 dp 也是无法在不同设备上显示为同样效果的。 同时还存在部分设备屏幕宽度不足 360dp，这时就会导致按 360dp 宽度来开发实际显示不全的情况。

  
而且上述屏幕尺寸、分辨率和像素密度的关系，很多设备并没有按此规则来实现， 因此 dpi 的值非常乱，没有规律可循，从而导致使用 dp 适配效果差强人意。

![](https://mmbiz.qpic.cn/mmbiz_png/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGY863ov6JALom5d4IcDNfhaQ82wu7zWbicNEGMZEeQQzbtZO6s3uFcdA/640?wx_fmt=png)

**探索新的适配方式**

**梳理需求**

首先来梳理下我们的需求，一般我们设计图都是以固定的尺寸来设计的。比如以分辨率 1920px * 1080px 来设计，以 density 为 3 来标注，也就是屏幕其实是 640dp * 360dp。如果我们想在所有设备上显示完全一致，其实是不现实的，因为屏幕高宽比不是固定的，16:9、4:3 甚至其他宽高比层出不穷，宽高比不同，显示完全一致就不可能了。但是通常下，我们只需要以宽或高一个维度去适配，比如我们 Feed 是上下滑动的，只需要保证在所有设备中宽的维度上显示一致即可，再比如一个不支持上下滑动的页面，那么需要保证在高这个维度上都显示一致，尤其不能存在某些设备上显示不全的情况。同时考虑到现在基本都是以 dp 为单位去做的适配，如果新的方案不支持 dp，那么迁移成本也非常高。

因此，总结下大致需求如下：

1.  支持以宽或者高一个维度去适配，保持该维度上和设计图一致；
    
2.  支持 dp 和 sp 单位，控制迁移成本到最小。
    

**找兼容突破口**

从 dp 和 px 的转换公式 ：px = dp * density 

可以看出，如果设计图宽为 360dp，想要保证在所有设备计算得出的 px 值都正好是屏幕宽度的话，我们只能修改 density 的值。

通过阅读源码，我们可以得知，density 是 DisplayMetrics 中的成员变量，而 DisplayMetrics 实例通过 Resources#getDisplayMetrics 可以获得，而 Resouces 通过 Activity 或者 Application 的 Context 获得。

先来熟悉下 DisplayMetrics 中和适配相关的几个变量：

*   DisplayMetrics#density 就是上述的 density
    
*   DisplayMetrics#densityDpi 就是上述的 dpi
    
*   DisplayMetrics#scaledDensity 字体的缩放因子，正常情况下和 density 相等，但是调节系统字体大小后会改变这个值
    

**那么是不是所有的 dp 和 px 的转换都是通过 DisplayMetrics 中相关的值来计算的呢？**

首先来看看布局文件中 dp 的转换，最终都是调用 TypedValue#applyDimension(int unit, float value, DisplayMetrics metrics) 来进行转换:

![](https://mmbiz.qpic.cn/mmbiz_jpg/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGMuDxhfdfuUNTrcK53gBdh0pntjAsTcqMb5o0RJG06dy8oK6eWZX9MA/640?wx_fmt=jpeg)

这里用到的 DisplayMetrics 正是从 Resources 中获得的。

再看看图片的 decode，BitmapFactory#decodeResourceStream 方法:

![](https://mmbiz.qpic.cn/mmbiz_jpg/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGnnPNN2o1ic2l2n44tkwicGP44gxOoG4NTne94bzBicu4h6T4cPnxvrxjw/640?wx_fmt=jpeg)

可见也是通过 DisplayMetrics 中的值来计算的。

当然还有些其他 dp 转换的场景，基本都是通过 DisplayMetrics 来计算的，这里不再详述。因此，想要满足上述需求，我们只需要修改 DisplayMetrics 中和 dp 转换相关的变量即可。

**最终方案**

下面假设设计图宽度是 360dp，以宽维度来适配。

那么适配后的 density = 设备真实宽 (单位 px) / 360，接下来只需要把我们计算好的 density 在系统中修改下即可，代码实现如下：

![](https://mmbiz.qpic.cn/mmbiz_jpg/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGOqZUB55MX5uoJ57bRICLjBV3GmlJocWGpQFzEiaAYfANvVNbxO4B1gQ/640?wx_fmt=jpeg)

同时在 Activity#onCreate 方法中调用下。代码比较简单，也没有涉及到系统非公开 api 的调用，因此理论上不会影响 app 稳定性。

于是修改后上线灰度测试了一版，稳定性符合预期，没有收到由此带来的 crash，但是收到了很多字体过小的反馈：

![](https://mmbiz.qpic.cn/mmbiz_png/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGWAarQNCEVn9ZczAJqfXRp2CqNI50kGueXGJTQPhU7U3ibI7cQ1Oc6EQ/640?wx_fmt=png)

原因是在上面的适配中，我们忽略了 DisplayMetrics#scaledDensity 的特殊性，将 DisplayMetrics#scaledDensity 和 DisplayMetrics#density 设置为同样的值，从而某些用户在系统中修改了字体大小失效了，但是我们还不能直接用原始的 scaledDensity，直接用的话可能导致某些文字超过显示区域，因此我们可以通过计算之前 scaledDensity 和 density 的比获得现在的 scaledDensity，方式如下：

![](https://mmbiz.qpic.cn/mmbiz_jpg/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGUle8F5PrpBI2LZ3icLOq2hQHl0ujhsMyjta7mcZ8Iqbs0BECMN64kVw/640?wx_fmt=jpeg)

但是测试后发现另外一个问题，就是如果在系统设置中切换字体，再返回应用，字体并没有变化。于是还得监听下字体切换，调用 Application#registerComponentCallbacks 注册下 onConfigurationChanged 监听即可。

因此最终方案如下：

![](https://mmbiz.qpic.cn/mmbiz_jpg/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGhxiapFRVjOtiaWzcERXwjaRDJgyyoIibSq2AJrby8q2aExttHeZfk0VZQ/640?wx_fmt=jpeg)

当然以上代码只是以设计图宽 360dp 去适配的，如果要以高维度适配，可以再扩展下代码即可。

![](https://mmbiz.qpic.cn/mmbiz_png/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGY863ov6JALom5d4IcDNfhaQ82wu7zWbicNEGMZEeQQzbtZO6s3uFcdA/640?wx_fmt=png)

**Showcase**

适配前后和设计图对比：

![](https://mmbiz.qpic.cn/mmbiz_png/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGRV6WsheI6R1esPW4nFXS6YtlLCNmiaUlreYVaYApFHSCD8dOddyss0A/640?wx_fmt=png)

适配后各机型的显示效果：

![](https://mmbiz.qpic.cn/mmbiz_jpg/5EcwYhllQOgM19n6iawpWQRCfcibxicoBYGYGKG0w6qrU95oiayI8CHUugNjNMapksj1LkAo2vmj6RhicgQffQgt2zQ/640?wx_fmt=jpeg)

**参考**

https://developer.android.com/guide/practices/screens_support.html