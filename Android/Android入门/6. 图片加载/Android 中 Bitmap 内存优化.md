> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/3f6f6e4f1c88

Android 开发中，Bitmap 是经常会遇到的对象，特别是在列表图片展示、大图显示等界面。而 Bitmap 实实在在是内存使用的 “大客户”。如何更好的使用 Bitmap，减少其对 App 内存的使用，是 Android 优化方面不可回避的问题。因此，本文从常规的 Bitmap 使用，到 Bitmap 内存计算进行了介绍，最后分析了 Bitmap 的源码和其内存模型在不同版本上的变化。

## Bitmap 的使用

一般来说，一个对象的使用，我们会尝试利用其构造函数去生成这个对象。在 Bitmap 中，其构造函数：

```
// called from JNI
    Bitmap(long nativeBitmap, byte[] buffer, int width, int height, int density,
            boolean isMutable, boolean requestPremultiplied,
            byte[] ninePatchChunk, NinePatch.InsetStruct ninePatchInsets) 

```

通过构造函数的注释，得知这是一个给 native 层调用的方法，因此可以知道 Bitmap 的创建将会涉及到底层库的支持。为了方便从不同来源来创建 Bitmap，Android 中提供了 BitmapFactory 工具类。BitmapFactory 类中有一系列的 decodeXXX 方法，用于解析资源文件、本地文件、流等方式，基本流程都很类似，读取目标文件，转换成输入流，调用 native 方法解析流，虽然 Java 层代码没有体现，但是我们可以猜想到，最后 native 方法解析完成后，必然会通过 JNI 调用 Bitmap 的构造函数，完成 Java 层的 Bitmap 对象创建。

```
// BitmapFactory部分代码：
public static Bitmap decodeResource(Resources res, int id)
public static Bitmap decodeStream(InputStream is)
private static native Bitmap nativeDecodeStream

```

native 层的代码稍后我们在看，先从 Java 层来看看常规的使用。典型的一个例子是，当我们需要从本地 Resource 中加载一个图片，并展示出来，我们可以通过 BitmapFacotry 来完成：

```
Bitmap bitmapDecode = BitmapFactory.decodeResource(getResources(), resId);
imageView.setImageBitmap(bitmapDecode);

```

当然，这里简单的使用`imageView.setImageResource(int resId)`也能实现一样的效果，实际上 setImageResource 方法只是封装了 bitmap 的读入、解析的过程，并且这个过程是在 UI 线程完成的，对于性能是有所影响的。另外，也对接下来讨论的内容，Bitmap 占用的内存有影响。

## Bitmap 到底占用多大的内存

Bitmap 作为位图，需要读入一张图片每一个像素点的数据，其主要占用内存的地方也正是这些像素数据。对于像素数据总大小，我们可以猜想为：像素总数量 × 每个像素的字节大小，而像素总数量在矩形屏幕表现下，应该是：横向像素数量 × 纵向像素数量，结合得到：

> Bitmap 内存占用 ≈ 像素数据总大小 = 横向像素数量 × 纵向像素数量 × 每个像素的字节大小

### 单个像素的字节大小

单个像素的字节大小由 Bitmap 的一个可配置的参数 Config 来决定。
Bitmap 中，存在一个枚举类 Config，定义了 Android 中支持的 Bitmap 配置：

| Config | 占用字节大小（byte） | 说明 |
| --- | --- | --- |
| ALPHA_8 (1) | 1 | 单透明通道 |
| RGB_565 (3) | 2 | 简易 RGB 色调 |
| ARGB_4444 (4) | 4 | 已废弃 |
| ARGB_8888 (5) | 4 | 24 位真彩色 |
| RGBA_F16 (6) | 8 | Android 8.0 新增（更丰富的色彩表现 HDR） |
| HARDWARE (7) | Special | Android 8.0 新增 （Bitmap 直接存储在 graphic memory）**注 1** |

> **注 1：**关于 Android 8.0 中新增的这个配置，[stackoverflow](https://link.jianshu.com?t=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F45511017%2Fbitmap-config-hardware-vs-bitmap-config-rgb-565) 已经有相关问题，可以关注下。

之前我们分析到，Bitmap 的 decode 实际上是在 native 层完成的，因此在 native 层也存在对应的 Config 枚举类。
一般使用时，我们并未关注这个配置，在 BitmapFactory 中，有：

```
  * Image are loaded with the {@link Bitmap.Config#ARGB_8888} config by default.
  */
  public Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;

```

因此，Android 系统中，默认 Bitmap 加载图片，使用 24 位真彩色模式。

### Bitmap 占用内存大小实例

首先准备了一张 800×600 分辨率的 jpg 图片，大小约 135k, 放置于 res/drawable 文件夹下：

![](https://upload-images.jianshu.io/upload_images/1982126-da26664954d7e8a3.jpg)

并将其加载到一个 200dp×300dp 大小的 ImageView 中，使用 BitmapFactory。

```
Bitmap bitmapDecode = BitmapFactory.decodeResource(getResources(), resId);
imageView.setImageBitmap(bitmapDecode);

```

打印出相关信息：

![](https://upload-images.jianshu.io/upload_images/1982126-2ef0db27b9d455cb.png)

图中显示了从资源文件中 decode 得到的 bitmap 的长、宽和占用内存大小（byte）等信息。
首先，从数据上可以验证：

> 17280000 = 2400 * 1800 * 4

这意味着，为了将单张 800 * 600 的图片加载到内存当中，付出了近 17.28M 的代价，即使现在手机运存普遍上涨，这样的开销也是无法接受的，因此，对于 Bitmap 的使用，是需要非常小心的。好在，目前主流的图像加载库（Glide、Fresco 等）基本上都不在需要开发者去关心 Bitmap 内存占用问题。
先暂时回到 Bitmap 占用内存的计算上来，对比之前定义的公式和源图片的尺寸数据，我们会发现，这张 800 * 600 大小的图片，decode 到内存中的 Bitmap 的横纵像素数量实际是：2400 * 1800，相当于缩放了 3 倍大小。为了探究这缩放来自何处，我们开始跟踪源码：之前提到过，Bitmap 的 decode 过程实际上是在 native 层完成的，为此，需要从 [BitmapFactory.cpp](https://link.jianshu.com?t=https%3A%2F%2Fandroid.googlesource.com%2Fplatform%2Fframeworks%2Fbase%2F%2B%2Fandroid-8.0.0_r34%2Fcore%2Fjni%2Fandroid%2Fgraphics%2FBitmapFactory.cpp)#nativeDecodeXXX 方法开始跟踪，这里省略其他 decode 代码，直接贴出和缩放相关的代码如下：

```
if (env->GetBooleanField(options, gOptions_scaledFieldID)) {
    const int density = env->GetIntField(options, gOptions_densityFieldID);
    const int targetDensity = env->GetIntField(options, gOptions_targetDensityFieldID);
    const int screenDensity = env->GetIntField(options, gOptions_screenDensityFieldID);
    if (density != 0 && targetDensity != 0 && density != screenDensity) {
        scale = (float) targetDensity / density;
    }
}
...
int scaledWidth = decoded->width();
int scaledHeight = decoded->height();

if (willScale && mode != SkImageDecoder::kDecodeBounds_Mode) {
    scaledWidth = int(scaledWidth * scale + 0.5f);
    scaledHeight = int(scaledHeight * scale + 0.5f);
}
...
if (willScale) {
    const float sx = scaledWidth / float(decoded->width());
    const float sy = scaledHeight / float(decoded->height());
    bitmap->setConfig(decoded->getConfig(), scaledWidth, scaledHeight);
    bitmap->allocPixels(&javaAllocator, NULL);
    bitmap->eraseColor(0);
    SkPaint paint;
    paint.setFilterBitmap(true);
    SkCanvas canvas(*bitmap);
    canvas.scale(sx, sy);
    canvas.drawBitmap(*decoded, 0.0f, 0.0f, &paint);
}

```

从上述代码中，我们看到 bitmap 最终通过 canvas 绘制出来，而 canvas 在绘制之前，有一个 scale 的操作，scale 的值由

> `scale = (float) targetDensity / density;`

这一行代码决定，即缩放的倍率和 targetDensity 和 density 相关，而这两个参数都是从传入的 options 中获取到的。这时候，需要回到 Java 层，看看 options 这个对象的定义和赋值。

### BitmapFactory#Options

Options 是 BitmapFactory 中的一个静态内部类，用于配置 Bitmap 在 decode 时的一些参数。

```
// native层doDecode方法，传入了Options参数
static jobject doDecode(JNIEnv* env, SkStreamRewindable* stream, jobject padding, jobject options)

```

其内部有很多可配置的参数，下面的类图，列举出了部分常用的参数。

![](https://upload-images.jianshu.io/upload_images/1982126-ad1f448f84f8225e.png)

我们先关注之前提到的几个密度相关的参数，通过阅读源码的注释，大概可以知道这三个密度参数代表的涵义：

*   inDensity：Bitmap 位图自身的密度、分辨率
*   inTargetDensity: Bitmap 最终绘制的目标位置的分辨率
*   inScreenDensity: 设备屏幕分辨率

其中 inDensity 和图片存放的资源文件的目录有关，同一张图片放置在不同目录下会有不同的值：

| density | 0.75 | 1 | 1.5 | 2 | 3 | 3.5 | 4 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| densityDpi | 120 | 160 | 240 | 320 | 480 | 560 | 640 |
| DpiFolder | ldpi | mdpi | hdpi | xhdpi | xxhdpi | xxxhdpi | xxxxhdpi |

inTargetDensity 和 inScreenDensity 一般来说，很少手动去赋值，默认情况下，是和设备分辨率保持一致。为此，我在手机（红米 4，Android 6.0 系统，设备 dpi 480）上测试加载不同资源文件下的 bitmap 的参数，结果见下图：

![](https://upload-images.jianshu.io/upload_images/1982126-f043ccfa23de30bf.png)

以上可以验证几个结论：

*   同一张图片，放在不同资源目录下，其分辨率会有变化，
*   bitmap 分辨率越高，其解析后的宽高越小，甚至会小于图片原有的尺寸（即缩放），从而内存占用也相应减少
*   图片不特别放置任何资源目录时，其默认使用 mdpi 分辨率：160
*   资源目录分辨率和设备分辨率一致时，图片尺寸不会缩放

因此，关于 Bitmap 占用内存大小的公式，从之前：

> Bitmap 内存占用 ≈ 像素数据总大小 = 横向像素数量 × 纵向像素数量 × 每个像素的字节大小

可以更细化为：

> Bitmap 内存占用 ≈ 像素数据总大小 = 图片宽 × 图片高 × (设备分辨率 / 资源目录分辨率)^2 × 每个像素的字节大小

对于本节中最开始的例子，如下：

> 17,280,000 = 800 * 600 * (480 / 160)^2 * 4

## Bitmap 内存优化

图片占用的内存一般会分为运行时占用的运存和存储时本地开销（反映在包大小上），这里我们只关注运行时占用内存的优化。
在上一节中，我们看到对于一张 800 * 600 大小的图片，不加任何处理直接解析到内存中，将近占用了 17.28M 的内存大小。想象一下这样的开销发生在一个图片列表中，内存占用将达到非常夸张的地步。从之前 Bitmap 占用内存的计算公式来看，减少内存主要可以通过以下几种方式：

1.  使用低色彩的解析模式，如 RGB565，减少单个像素的字节大小
2.  资源文件合理放置，高分辨率图片可以放到高分辨率目录下
3.  图片缩小，减少尺寸

**第一种方式**，大约能减少一半的内存开销。Android 默认是使用 ARGB8888 配置来处理色彩，占用 4 字节，改用 RGB565，将只占用 2 字节，代价是显示的色彩将相对少，适用于对色彩丰富程度要求不高的场景。
**第二种方式**，和图片的具体分辨率有关，建议开发中，高分辨率的图像应该放置到合理的资源目录下，注意到 Android 默认放置的资源目录是对应于 160dpi，目前手机屏幕分辨率越来越高，此处能节省下来的开销也是很可观的。理论上，图片放置的资源目录分辨率越高，其占用内存会越小，但是低分辨率图片会因此被拉伸，显示上出现失真。另一方面，高分辨率图片也意味着其占用的本地储存也变大。
**第三种方式**，理论上根据适用的环境，是可以减少十几倍的内存使用的，它基于这样一个事实：源图片尺寸一般都大于目标需要显示的尺寸，因此可以通过缩放的方式，来减少显示时的图片宽高，从而大大减少占用的内存。

前两种方式，相对比较简单。第三种方式会涉及到一些编码，目前也有很多典型的使用方式，如下：

```
BitmapFactory.Options options = new BitmapFactory.Options();
options.inPreferredConfig = Bitmap.Config.RGB_565;
options.inJustDecodeBounds = true;
BitmapFactory.decodeResource(getResources(), resId,options);
options.inJustDecodeBounds = false;
options.inSampleSize = BitmapUtil.computeSampleSize(options, -1, imageView.getWidth() * imageView.getHeight());
Bitmap newBitmap = BitmapFactory.decodeResource(getResources(), resId, options);

```

原理很简单，充分利用了 Options 类里的参数设置，也可以从 native 底层源码上看到对应的逻辑。第一次解析 bitmap 只获取尺寸信息，不生成像素数据，继而比较 bitmap 尺寸和目标尺寸得到缩放倍数，第二次根据缩放倍数去解析我们实际需要的尺寸大小。

```
// Apply a fine scaling step if necessary.
    if (needsFineScale(codec->getInfo().dimensions(), size, sampleSize)) {
        willScale = true;
        scaledWidth = codec->getInfo().width() / sampleSize;
        scaledHeight = codec->getInfo().height() / sampleSize;
    }

```

![](https://upload-images.jianshu.io/upload_images/1982126-41ea3bf10c9ee049.png)

上图是使用上述手段优化后的结果，可以看到现在占用的内存大小大约为 960KB，从优化后的宽高来看，第三种方式并没有效果。应为目标 ImageView 尺寸也不小，而 inSampleSize 的值必须是 2 的整数幂，因此计算得到的值还是 1。

PS: Bitmap 内存占用的优化还有一个方式是复用和缓存

## 不同 Android 版本时的 Bitmap 内存模型

我们知道 Android 系统中，一个进程的内存可以简单分为 Java 内存和 native 内存两部分，而 Bitmap 对象占用的内存，有 Bitmap 对象内存和像素数据内存两部分，在不同的 Android 系统版本中，其所存放的位置也有变化。[Android Developers](https://link.jianshu.com?t=https%3A%2F%2Fdeveloper.android.com%2Ftopic%2Fperformance%2Fgraphics%2Fmanage-memory.html) 上列举了从 API 8 到 API 26 之间的分配方式：

| API 级别 | API 10 - | API 11 ~ API 25 | API 26 + |
| --- | --- | --- | --- |
| Bitmap 对象存放 | Java heap | Java heap | Java heap |
| 像素 (pixel data) 数据存放 | native heap | Java heap | native heap |

可以看到，最新的 Android O 之后，谷歌又把像素存放的位置，从 java 堆改回到了 native 堆。API 11 的那次改动，是源于 native 的内存释放不及时，会导致 OOM，因此才将像素数据保存到 Java 堆，从而保证 Bitmap 对象释放时，能够同时把像素数据内存也释放掉。

![](https://upload-images.jianshu.io/upload_images/1982126-d42a1d72f2485a38.png) ![](https://upload-images.jianshu.io/upload_images/1982126-37de701c32d50601.png)

上面两幅图展示了不同系统，加载图片后，内存的变化，8.0 的截图比较模糊。途中浅蓝色对应的是 Java heap 使用，深蓝色对应的是 native heap 的使用。
跟踪一下 8.0 的 native 源码来看看具体的变化：

```
// BitmapFactory.cpp
    if (!decodingBitmap.setInfo(bitmapInfo) ||
            !decodingBitmap.tryAllocPixels(decodeAllocator, colorTable.get())) {
        // SkAndroidCodec should recommend a valid SkImageInfo, so setInfo()
        // should only only fail if the calculated value for rowBytes is too
        // large.
        // tryAllocPixels() can fail due to OOM on the Java heap, OOM on the
        // native heap, or the recycled javaBitmap being too small to reuse.
        return nullptr;
    }

// Graphics.cpp
bool HeapAllocator::allocPixelRef(SkBitmap* bitmap, SkColorTable* ctable) {
    mStorage = android::Bitmap::allocateHeapBitmap(bitmap, sk_ref_sp(ctable));
    return !!mStorage;
}

// https://android.googlesource.com/platform/frameworks/base/+/master/libs/hwui/hwui/Bitmap.cpp
static sk_sp<Bitmap> allocateHeapBitmap(size_t size, const SkImageInfo& info, size_t rowBytes) {
    void* addr = calloc(size, 1);
    if (!addr) {
        return nullptr;
    }
    return sk_sp<Bitmap>(new Bitmap(addr, size, info, rowBytes));
}

```

还是通过 BitmapFactory.cpp#doDecode 方法来跟踪，发现其中 tryAllocPixels 方法，应该是尝试去进行内存分配，其中 decodeAllocator 会被赋值为 HeapAllocator，通过一系列的调用，最终通过 calloc 方法，在 native 分配内存。
至于为什么 Google 在 8.0 上改变了 Bitmap 像素数据的存放方式，我猜想和 8.0 中的 GC 算法调整有关系。GC 算法的优化，使得 Bitmap 占用的大内存区域，在 GC 后也能够比较快速的回收、压缩，重新使用。

| （native 存放） | 退出 Activity | 退出 App |
| --- | --- | --- |
| onStop 中主动调用 gc() 和 recycler() | 内存不释放 | 内存释放 |
| 无调用 | 内存不释放 | 内存不释放 |

| （gpu 存放） | 退出 Activity | 退出 App |
| --- | --- | --- |
| onStop 中主动调用 gc() 和 recycler() | 内存释放 | 内存释放 |
| 无调用 | 内存释放 | 内存释放 |

# 总结

```
// 8.0源码
    Bitmap(long nativeBitmap, int width, int height, int density,
            boolean isMutable, boolean requestPremultiplied,
            byte[] ninePatchChunk, NinePatch.InsetStruct ninePatchInsets)
// 7.0源码
Bitmap(long nativeBitmap, byte[] buffer, int width, int height, int density,
            boolean isMutable, boolean requestPremultiplied,
            byte[] ninePatchChunk, NinePatch.InsetStruct ninePatchInsets)

```

一开始看两者 java 代码不同，少了存放像素的 buffer 字段，查阅相关资料到 native 源码对比，最终总结了下 Bitmap 内存相关的知识。另外，在 Android 8.0 中，关于 Bitmap 的改动有两方面还需深入探究的：1、Config 配置为 Hardware 时的优劣。Hardware 配置实际上没有改变像素的位储存大小（还是默认的 ARGB8888），但是改变了 bitmap 像素的存储位置（存放到 GPU 内存中），对实际应用的影响会如何？；2、Bitmap 在 8.0 后又回归到 native 存放 bitmap 像素数据，而这部分数据的回收时机和触发方式又是如何？一般测试下，可以通过 native 分配 Bitmap 超过 1G 的内存数据而不发生崩溃。