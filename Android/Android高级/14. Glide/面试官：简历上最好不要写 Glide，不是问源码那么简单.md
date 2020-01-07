> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5dbeda27e51d452a161e00c8?utm_source=gold_browser_extension#heading-7

这次来面试的是一个有着 5 年工作经验的小伙，截取了一段对话如下：

> 面试官：我看你写到 Glide，为什么用 Glide，而不选择其它图片加载框架？  
> 小伙：Glide 使用简单，链式调用，很方便，一直用这个。  
> 面试官：有看过它的源码吗？跟其它图片框架相比有哪些优势？  
> 小伙：没有，只是在项目中使用而已~  
> 面试官：假如现在不让你用开源库，需要你自己写一个图片加载框架，你会考虑哪些方面的问题，说说大概的思路。  
> 小伙：额~，压缩吧。  
> 面试官：还有吗？  
> 小伙：额~，这个没写过。

说到图片加载框架，大家最熟悉的莫过于 Glide 了，但我却不推荐简历上写熟悉 Glide，除非你熟读它的源码，或者参与 Glide 的开发和维护。

在一般面试中，遇到图片加载问题的频率一般不会太低，只是问法会有一些差异，例如：

*   简历上写 Glide，那么会问一下 Glide 的设计，以及跟其它同类框架的对比 ；
*   假如让你写一个图片加载框架，说说思路；
*   给一个图片加载的场景，比如网络加载一张或多张大图，你会怎么做；

带着问题进入正文~

一、谈谈 Glide
----------

### 1.1 Glide 使用有多简单？

Glide 由于其口碑好，很多开发者直接在项目中使用，使用方法相当简单

[github.com/bumptech/gl…](https://github.com/bumptech/glide)

1、添加依赖：

```
implementation 'com.github.bumptech.glide:glide:4.10.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.10.0'
复制代码

```

2、添加网络权限

```
<uses-permission android: />
复制代码

```

3、一句代码加载图片到 ImageView

```
Glide.with(this).load(imgUrl).into(mIv1);
复制代码

```

进阶一点的用法，参数设置

```
RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.mipmap.ic_launcher)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
    		.override(200, 100);
    
Glide.with(this)
            .load(imgUrl)
            .apply(options)
            .into(mIv2);
复制代码

```

使用 Glide 加载图片如此简单，这让很多开发者省下自己处理图片的时间，图片加载工作全部交给 Glide 来就完事，同时，很容易就把图片处理的相关知识点忘掉。

### 1.2 为什么用 Glide？

从前段时间面试的情况，我发现了这个现象：简历上写熟悉 Glide 的，基本都是熟悉使用方法，很多 3 年 - 6 年工作经验，除了说 Glide 使用方便，不清楚 Glide 跟其他图片框架如 **Fresco** 的对比有哪些优缺点。

首先，当下流行的图片加载框架有那么几个，可以拿 Glide 跟 [Fresco](https://github.com/facebook/fresco) 对比，例如这些：

**Glide：**

*   多种图片格式的缓存，适用于更多的内容表现形式（如 Gif、WebP、缩略图、Video）
*   生命周期集成（根据 Activity 或者 Fragment 的生命周期管理图片加载请求）
*   高效处理 Bitmap（bitmap 的复用和主动回收，减少系统回收压力）
*   高效的缓存策略，灵活（Picasso 只会缓存原始尺寸的图片，Glide 缓存的是多种规格），加载速度快且内存开销小（默认 Bitmap 格式的不同，使得内存开销是 Picasso 的一半）

**Fresco：**

*   最大的优势在于 5.0 以下 (最低 2.3) 的 bitmap 加载。在 5.0 以下系统，Fresco 将图片放到一个特别的内存区域(Ashmem 区)
*   大大减少 OOM（在更底层的 Native 层对 OOM 进行处理，图片将不再占用 App 的内存）
*   适用于需要高性能加载大量图片的场景

对于一般 App 来说，Glide 完全够用，而对于图片需求比较大的 App，为了防止加载大量图片导致 OOM，Fresco 会更合适一些。并不是说用 Glide 会导致 OOM，Glide 默认用的内存缓存是 LruCache，内存不会一直往上涨。

二、假如让你自己写个图片加载框架，你会考虑哪些问题？
--------------------------

首先，梳理一下必要的图片加载框架的需求：

*   异步加载：线程池
*   切换线程：Handler，没有争议吧
*   缓存：LruCache、DiskLruCache
*   防止 OOM：软引用、LruCache、图片压缩、Bitmap 像素存储位置
*   内存泄露：注意 ImageView 的正确引用，生命周期管理
*   列表滑动加载的问题：加载错乱、队满任务过多问题

当然，还有一些不是必要的需求，例如加载动画等。

### 2.1 异步加载：

线程池，多少个？

缓存一般有三级，内存缓存、硬盘、网络。

由于网络会阻塞，所以读内存和硬盘可以放在一个线程池，网络需要另外一个线程池，网络也可以采用 Okhttp 内置的线程池。

读硬盘和读网络需要放在不同的线程池中处理，所以用两个线程池比较合适。

Glide 必然也需要多个线程池，看下源码是不是这样

```
public final class GlideBuilder {
  ...
  private GlideExecutor sourceExecutor; //加载源文件的线程池，包括网络加载
  private GlideExecutor diskCacheExecutor; //加载硬盘缓存的线程池
  ...
  private GlideExecutor animationExecutor; //动画线程池
复制代码

```

Glide 使用了三个线程池，不考虑动画的话就是两个。

### 2.2 切换线程：

图片异步加载成功，需要在主线程去更新 ImageView，

**无论是 RxJava、EventBus，还是 Glide，只要是想从子线程切换到 Android 主线程，都离不开 Handler。**

看下 Glide 相关源码：

```
    class EngineJob<R> implements DecodeJob.Callback<R>,Poolable {
	  private static final EngineResourceFactory DEFAULT_FACTORY = new EngineResourceFactory();
	  //创建Handler
	  private static final Handler MAIN_THREAD_HANDLER =
	      new Handler(Looper.getMainLooper(), new MainThreadCallback());

复制代码

```

> 问 RxJava 是完全用 Java 语言写的，那怎么实现从子线程切换到 Android 主线程的？ 依然有很多 3-6 年的开发答不上来这个很基础的问题，而且只要是这个问题回答不出来的，接下来有关于原理的问题，基本都答不上来。
> 
> 有不少工作了很多年的 Android 开发不知道**鸿洋、郭霖、玉刚说**，不知道掘金是个啥玩意，内心估计会想是不是还有叫掘银掘铁的（我不知道有没有）。
> 
> 我想表达的是，干这一行，真的是需要有对技术的热情，不断学习，**不怕别人比你优秀，就怕比你优秀的人比你还努力，而你却不知道**。

### 2.3 缓存

我们常说的图片三级缓存：内存缓存、硬盘缓存、网络。

#### 2.3.1 内存缓存

一般都是用`LruCache`

Glide 默认内存缓存用的也是 LruCache，只不过并没有用 Android SDK 中的 LruCache，不过内部同样是基于 LinkHashMap，所以原理是一样的。

```
// -> GlideBuilder#build
if (memoryCache == null) {
  memoryCache = new LruResourceCache(memorySizeCalculator.getMemoryCacheSize());
}
复制代码

```

既然说到 LruCache ，必须要了解一下 LruCache 的特点和源码：

**为什么用 LruCache？**

LruCache 采用**最近最少使用算法**，设定一个缓存大小，当缓存达到这个大小之后，会将最老的数据移除，避免图片占用内存过大导致 OOM。

##### LruCache 源码分析

```
    public class LruCache<K, V> {
	// 数据最终存在 LinkedHashMap 中
    private final LinkedHashMap<K, V> map;
	...
	public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
		// 创建一个LinkedHashMap，accessOrder 传true
        this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
    }
    ...
复制代码

```

LruCache 构造方法里创建一个 **LinkedHashMap**，accessOrder 参数传 true，表示按照访问顺序排序，数据存储基于 LinkedHashMap。

先看看 LinkedHashMap 的原理吧

LinkedHashMap 继承 HashMap，在 HashMap 的基础上进行扩展，put 方法并没有重写，说明 **LinkedHashMap 遵循 HashMap 的数组加链表的结构**，

![](https://user-gold-cdn.xitu.io/2019/11/3/16e3183d92d1997d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

LinkedHashMap 重写了 **createEntry** 方法。

看下 HashMap 的 createEntry 方法

```
void createEntry(int hash, K key, V value, int bucketIndex) {
    HashMapEntry<K,V> e = table[bucketIndex];
    table[bucketIndex] = new HashMapEntry<>(hash, key, value, e);
    size++;
}
复制代码

```

**HashMap 的数组里面放的是`HashMapEntry` 对象**

看下 LinkedHashMap 的 createEntry 方法

```
void createEntry(int hash, K key, V value, int bucketIndex) {
    HashMapEntry<K,V> old = table[bucketIndex];
    LinkedHashMapEntry<K,V> e = new LinkedHashMapEntry<>(hash, key, value, old);
    table[bucketIndex] = e; //数组的添加
    e.addBefore(header);  //处理链表
    size++;
}
复制代码

```

**LinkedHashMap 的数组里面放的是`LinkedHashMapEntry`对象**

**LinkedHashMapEntry**

```
private static class LinkedHashMapEntry<K,V> extends HashMapEntry<K,V> {
    // These fields comprise the doubly linked list used for iteration.
    LinkedHashMapEntry<K,V> before, after; //双向链表

	private void remove() {
        before.after = after;
        after.before = before;
    }

	private void addBefore(LinkedHashMapEntry<K,V> existingEntry) {
        after  = existingEntry;
        before = existingEntry.before;
        before.after = this;
        after.before = this;
    }
复制代码

```

**LinkedHashMapEntry 继承 HashMapEntry，添加 before 和 after 变量，所以是一个双向链表结构，还添加了`addBefore`和`remove` 方法，用于新增和删除链表节点。**

**LinkedHashMapEntry#addBefore**  
将一个数据添加到 Header 的前面

```
private void addBefore(LinkedHashMapEntry<K,V> existingEntry) {
        after  = existingEntry;
        before = existingEntry.before;
        before.after = this;
        after.before = this;
}
复制代码

```

existingEntry 传的都是链表头 header，将一个节点添加到 header 节点前面，只需要移动链表指针即可，添加新数据都是放在链表头 header 的 before 位置，**链表头节点 header 的 before 是最新访问的数据，header 的 after 则是最旧的数据。**

再看下 **LinkedHashMapEntry#remove**

```
private void remove() {
        before.after = after;
        after.before = before;
    }
复制代码

```

链表节点的移除比较简单，改变指针指向即可。

再看下 **LinkHashMap 的 put 方法**

```
public final V put(K key, V value) {
    
    V previous;
    synchronized (this) {
        putCount++;
        //size增加
        size += safeSizeOf(key, value);
        // 1、linkHashMap的put方法
        previous = map.put(key, value);
        if (previous != null) {
            //如果有旧的值，会覆盖，所以大小要减掉
            size -= safeSizeOf(key, previous);
        }
    }


    trimToSize(maxSize);
    return previous;
}
复制代码

```

LinkedHashMap 结构可以用这种图表示

![](https://user-gold-cdn.xitu.io/2019/11/3/16e3183d9e907230?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

LinkHashMap 的 put 方法和 get 方法最后会调用`trimToSize`方法，**LruCache 重写`trimToSize`方法，判断内存如果超过一定大小，则移除最老的数据**

**LruCache#trimToSize，移除最老的数据**

```
public void trimToSize(int maxSize) {
    while (true) {
        K key;
        V value;
        synchronized (this) {
            
            //大小没有超出，不处理
            if (size <= maxSize) {
                break;
            }

            //超出大小，移除最老的数据
            Map.Entry<K, V> toEvict = map.eldest();
            if (toEvict == null) {
                break;
            }

            key = toEvict.getKey();
            value = toEvict.getValue();
            map.remove(key);
            //这个大小的计算，safeSizeOf 默认返回1；
            size -= safeSizeOf(key, value);
            evictionCount++;
        }

        entryRemoved(true, key, value, null);
    }
}
复制代码

```

对 LinkHashMap 还不是很理解的话可以参考：  
[图解 LinkedHashMap 原理](https://www.jianshu.com/p/8f4f58b4b8ab)

LruCache 小结：

*   **LinkHashMap 继承 HashMap，在 HashMap 的基础上，新增了双向链表结构，每次访问数据的时候，会更新被访问的数据的链表指针，具体就是先在链表中删除该节点，然后添加到链表头 header 之前，这样就保证了链表头 header 节点之前的数据都是最近访问的（从链表中删除并不是真的删除数据，只是移动链表指针，数据本身在 map 中的位置是不变的）。**
*   **LruCache 内部用 LinkHashMap 存取数据，在双向链表保证数据新旧顺序的前提下，设置一个最大内存，往里面 put 数据的时候，当数据达到最大内存的时候，将最老的数据移除掉，保证内存不超过设定的最大值。**

#### 2.3.2 磁盘缓存 DiskLruCache

依赖：

> implementation 'com.jakewharton:disklrucache:2.0.2'

DiskLruCache 跟 LruCache 实现思路是差不多的，一样是设置一个总大小，每次往硬盘写文件，总大小超过阈值，就会将旧的文件删除。简单看下 remove 操作：

```
	// DiskLruCache 内部也是用LinkedHashMap
	private final LinkedHashMap<String, Entry> lruEntries =
      	new LinkedHashMap<String, Entry>(0, 0.75f, true);
	...

    public synchronized boolean remove(String key) throws IOException {
	    checkNotClosed();
	    validateKey(key);
	    Entry entry = lruEntries.get(key);
	    if (entry == null || entry.currentEditor != null) {
	      return false;
	    }
	
            //一个key可能对应多个value，hash冲突的情况
	    for (int i = 0; i < valueCount; i++) {
	      File file = entry.getCleanFile(i);
            //通过 file.delete() 删除缓存文件，删除失败则抛异常
	      if (file.exists() && !file.delete()) {
	        throw new IOException("failed to delete " + file);
	      }
	      size -= entry.lengths[i];
	      entry.lengths[i] = 0;
	    }
	    ...
	    return true;
  }
复制代码

```

可以看到 DiskLruCache 同样是利用 LinkHashMap 的特点，只不过数组里面存的 Entry 有点变化，Editor 用于操作文件。

```
private final class Entry {
    private final String key;

    private final long[] lengths;

    private boolean readable;

    private Editor currentEditor;

    private long sequenceNumber;
	...
}
复制代码

```

### 2.4 防止 OOM

加载图片非常重要的一点是需要防止 OOM，上面的 LruCache 缓存大小设置，可以有效防止 OOM，但是当图片需求比较大，可能需要设置一个比较大的缓存，这样的话发生 OOM 的概率就提高了，那应该探索其它防止 OOM 的方法。

##### 方法 1：软引用

回顾一下 Java 的四大引用：

*   强引用： 普通变量都属于强引用，比如 `private Context context;`
*   软应用： SoftReference，在发生 OOM 之前，垃圾回收器会回收 SoftReference 引用的对象。
*   弱引用： WeakReference，发生 GC 的时候，垃圾回收器会回收 WeakReference 中的对象。
*   虚引用： 随时会被回收，没有使用场景。

怎么理解强引用：

> 强引用对象的回收时机依赖垃圾回收算法，我们常说的可达性分析算法，当 Activity 销毁的时候，Activity 会跟 GCRoot 断开，至于 GCRoot 是谁？这里可以大胆猜想，Activity 对象的创建是在 ActivityThread 中，ActivityThread 要回调 Activity 的各个生命周期，肯定是持有 Activity 引用的，那么这个 GCRoot 可以认为就是 ActivityThread，当 Activity 执行 onDestroy 的时候，ActivityThread 就会断开跟这个 Activity 的联系，Activity 到 GCRoot 不可达，所以会被垃圾回收器标记为可回收对象。

软引用的设计就是应用于会发生 OOM 的场景，大内存对象如 Bitmap，可以通过 SoftReference 修饰，防止大对象造成 OOM，看下这段代码

```
    private static LruCache<String, SoftReference<Bitmap>> mLruCache = new LruCache<String, SoftReference<Bitmap>>(10 * 1024){
        @Override
        protected int sizeOf(String key, SoftReference<Bitmap> value) {
            //默认返回1，这里应该返回Bitmap占用的内存大小，单位：K

            //Bitmap被回收了，大小是0
            if (value.get() == null){
                return 0;
            }
            return value.get().getByteCount() /1024;
        }
    };

复制代码

```

LruCache 里存的是软引用对象，那么当内存不足的时候，Bitmap 会被回收，也就是说通过 SoftReference 修饰的 Bitmap 就不会导致 OOM。

当然，这段代码存在一些问题，Bitmap 被回收的时候，LruCache 剩余的大小应该重新计算，可以写个方法，当 Bitmap 取出来是空的时候，LruCache 清理一下，重新计算剩余内存；

还有另一个问题，就是内存不足时软引用中的 Bitmap 被回收的时候，这个 LruCache 就形同虚设，相当于内存缓存失效了，必然出现效率问题。

##### 方法 2：onLowMemory

**当内存不足的时候，Activity、Fragment 会调用`onLowMemory`方法，可以在这个方法里去清除缓存，Glide 使用的就是这一种方式来防止 OOM。**

```
//Glide
public void onLowMemory() {
    clearMemory();
}

public void clearMemory() {
    // Engine asserts this anyway when removing resources, fail faster and consistently
    Util.assertMainThread();
    // memory cache needs to be cleared before bitmap pool to clear re-pooled Bitmaps too. See #687.
    memoryCache.clearMemory();
    bitmapPool.clearMemory();
    arrayPool.clearMemory();
  }
复制代码

```

##### 方法 3：从 Bitmap 像素存储位置考虑

我们知道，系统为每个进程，也就是每个虚拟机分配的内存是有限的，早期的 16M、32M，现在 100+M，  
虚拟机的内存划分主要有 5 部分：

*   虚拟机栈
*   本地方法栈
*   程序计数器
*   方法区
*   堆

而对象的分配一般都是在堆中，堆是 JVM 中最大的一块内存，OOM 一般都是发生在堆中。

Bitmap 之所以占内存大不是因为对象本身大，而是因为 Bitmap 的像素数据， **Bitmap 的像素数据大小 = 宽 * 高 * 1 像素占用的内存。**

1 像素占用的内存是多少？不同格式的 Bitmap 对应的像素占用内存是不同的，具体是多少呢？  
在 Fresco 中看到如下定义代码

```
  /**
   * Bytes per pixel definitions
   */
  public static final int ALPHA_8_BYTES_PER_PIXEL = 1;
  public static final int ARGB_4444_BYTES_PER_PIXEL = 2;
  public static final int ARGB_8888_BYTES_PER_PIXEL = 4;
  public static final int RGB_565_BYTES_PER_PIXEL = 2;
  public static final int RGBA_F16_BYTES_PER_PIXEL = 8;
复制代码

```

如果 Bitmap 使用 `RGB_565` 格式，则 1 像素占用 2 byte，`ARGB_8888` 格式则占 4 byte。  
**在选择图片加载框架的时候，可以将内存占用这一方面考虑进去，更少的内存占用意味着发生 OOM 的概率越低。** Glide 内存开销是 Picasso 的一半，就是因为默认 Bitmap 格式不同。

至于宽高，是指 Bitmap 的宽高，怎么计算的呢？看`BitmapFactory.Options` 的 outWidth

```
/**
     * The resulting width of the bitmap. If {@link #inJustDecodeBounds} is
     * set to false, this will be width of the output bitmap after any
     * scaling is applied. If true, it will be the width of the input image
     * without any accounting for scaling.
     *
     * <p>outWidth will be set to -1 if there is an error trying to decode.</p>
     */
    public int outWidth;
复制代码

```

看注释的意思，如果 `BitmapFactory.Options` 中指定 `inJustDecodeBounds` 为 true，则为原图宽高，如果是 false，则是缩放后的宽高。**所以我们一般可以通过压缩来减小 Bitmap 像素占用内存**。

扯远了，上面分析了 Bitmap 像素数据大小的计算，只是说明 Bitmap 像素数据为什么那么大。**那是否可以让像素数据不放在 java 堆中，而是放在 native 堆中呢**？据说 Android 3.0 到 8.0 之间 Bitmap 像素数据存在 Java 堆，而 8.0 之后像素数据存到 native 堆中，是不是真的？看下源码就知道了~

###### 8.0 Bitmap

java 层创建 Bitmap 方法

```
    public static Bitmap createBitmap(@Nullable DisplayMetrics display, int width, int height,
            @NonNull Config config, boolean hasAlpha, @NonNull ColorSpace colorSpace) {
        ...
        Bitmap bm;
        ...
        if (config != Config.ARGB_8888 || colorSpace == ColorSpace.get(ColorSpace.Named.SRGB)) {
            //最终都是通过native方法创建
            bm = nativeCreate(null, 0, width, width, height, config.nativeInt, true, null, null);
        } else {
            bm = nativeCreate(null, 0, width, width, height, config.nativeInt, true,
                    d50.getTransform(), parameters);
        }

        ...
        return bm;
    }

复制代码

```

Bitmap 的创建是通过 native 方法 `nativeCreate`

对应源码 [8.0.0_r4/xref/frameworks/base/core/jni/android/graphics/Bitmap.cpp](https://www.androidos.net.cn/android/8.0.0_r4/xref/frameworks/base/core/jni/android/graphics/Bitmap.cpp)

```
//Bitmap.cpp
static const JNINativeMethod gBitmapMethods[] = {
    {   "nativeCreate",             "([IIIIIIZ[FLandroid/graphics/ColorSpace$Rgb$TransferParameters;)Landroid/graphics/Bitmap;",
        (void*)Bitmap_creator },
...
复制代码

```

JNI 动态注册，nativeCreate 方法 对应 `Bitmap_creator`；

```
//Bitmap.cpp
static jobject Bitmap_creator(JNIEnv* env, jobject, jintArray jColors,
                              jint offset, jint stride, jint width, jint height,
                              jint configHandle, jboolean isMutable,
                              jfloatArray xyzD50, jobject transferParameters) {
    ...
    //1. 申请堆内存，创建native层Bitmap
    sk_sp<Bitmap> nativeBitmap = Bitmap::allocateHeapBitmap(&bitmap, NULL);
    if (!nativeBitmap) {
        return NULL;
    }

    ...
    //2.创建java层Bitmap
    return createBitmap(env, nativeBitmap.release(), getPremulBitmapCreateFlags(isMutable));
}
复制代码

```

主要两个步骤：

1.  申请内存，创建 native 层 Bitmap，看下`allocateHeapBitmap`方法  
    [8.0.0_r4/xref/frameworks/base/libs/hwui/hwui/Bitmap.cpp](https://www.androidos.net.cn/android/8.0.0_r4/xref/frameworks/base/libs/hwui/hwui/Bitmap.cpp)

```
//
static sk_sp<Bitmap> allocateHeapBitmap(size_t size, const SkImageInfo& info, size_t rowBytes,
        SkColorTable* ctable) {
    // calloc 是c++ 的申请内存函数
    void* addr = calloc(size, 1);
    if (!addr) {
        return nullptr;
    }
    return sk_sp<Bitmap>(new Bitmap(addr, size, info, rowBytes, ctable));
}
复制代码

```

可以看到通过 c++ 的 `calloc` 函数申请了一块内存空间，然后创建 native 层 Bitmap 对象，把内存地址传过去，也就是 native 层的 Bitmap 数据（像素数据）是存在 native 堆中。

2.  创建 java 层 Bitmap

```
//Bitmap.cpp
jobject createBitmap(JNIEnv* env, Bitmap* bitmap,
        int bitmapCreateFlags, jbyteArray ninePatchChunk, jobject ninePatchInsets,
        int density) {
    ...
    BitmapWrapper* bitmapWrapper = new BitmapWrapper(bitmap);
     //通过JNI回调Java层，调用java层的Bitmap构造方法
    jobject obj = env->NewObject(gBitmap_class, gBitmap_constructorMethodID,
            reinterpret_cast<jlong>(bitmapWrapper), bitmap->width(), bitmap->height(), density,
            isMutable, isPremultiplied, ninePatchChunk, ninePatchInsets);

   ...
    return obj;
}

复制代码

```

env->NewObject，通过 JNI 创建 Java 层 Bitmap 对象，`gBitmap_class，gBitmap_constructorMethodID`这些变量是什么意思，看下面这个方法，对应 java 层的 Bitmap 的类名和构造方法。

```
//Bitmap.cpp
int register_android_graphics_Bitmap(JNIEnv* env)
{
    gBitmap_class = MakeGlobalRefOrDie(env, FindClassOrDie(env, "android/graphics/Bitmap"));
    gBitmap_nativePtr = GetFieldIDOrDie(env, gBitmap_class, "mNativePtr", "J");
    gBitmap_constructorMethodID = GetMethodIDOrDie(env, gBitmap_class, "<init>", "(JIIIZZ[BLandroid/graphics/NinePatch$InsetStruct;)V");
    gBitmap_reinitMethodID = GetMethodIDOrDie(env, gBitmap_class, "reinit", "(IIZ)V");
    gBitmap_getAllocationByteCountMethodID = GetMethodIDOrDie(env, gBitmap_class, "getAllocationByteCount", "()I");
    return android::RegisterMethodsOrDie(env, "android/graphics/Bitmap", gBitmapMethods,
                                         NELEM(gBitmapMethods));
}
复制代码

```

8.0 的 Bitmap 创建就两个点：

1.  创建 native 层 Bitmap，在 native 堆申请内存。
2.  通过 JNI 创建 java 层 Bitmap 对象，这个对象在 java 堆中分配内存。

像素数据是存在 native 层 Bitmap，也就是证明 8.0 的 Bitmap 像素数据存在 native 堆中。

###### 7.0 Bitmap

直接看 native 层的方法，

[/7.0.0_r31/xref/frameworks/base/core/jni/android/graphics/Bitmap.cpp](https://www.androidos.net.cn/android/7.0.0_r31/xref/frameworks/base/core/jni/android/graphics/Bitmap.cpp)

```
//JNI动态注册
static const JNINativeMethod gBitmapMethods[] = {
    {   "nativeCreate",             "([IIIIIIZ)Landroid/graphics/Bitmap;",
        (void*)Bitmap_creator },
...

static jobject Bitmap_creator(JNIEnv* env, jobject, jintArray jColors,
                              jint offset, jint stride, jint width, jint height,
                              jint configHandle, jboolean isMutable) {
    ... 
    //1.通过这个方法来创建native层Bitmap
    Bitmap* nativeBitmap = GraphicsJNI::allocateJavaPixelRef(env, &bitmap, NULL);
    ...

    return GraphicsJNI::createBitmap(env, nativeBitmap,
            getPremulBitmapCreateFlags(isMutable));
}

复制代码

```

native 层 Bitmap 创建是通过`GraphicsJNI::allocateJavaPixelRef`，看看里面是怎么分配的， GraphicsJNI 的实现类是 [Graphics.cpp](https://www.androidos.net.cn/android/7.0.0_r31/xref/frameworks/base/core/jni/android/graphics/Graphics.cpp)

```
android::Bitmap* GraphicsJNI::allocateJavaPixelRef(JNIEnv* env, SkBitmap* bitmap,
                                             SkColorTable* ctable) {
    const SkImageInfo& info = bitmap->info();
    
    size_t size;
    //计算需要的空间大小
    if (!computeAllocationSize(*bitmap, &size)) {
        return NULL;
    }

    // we must respect the rowBytes value already set on the bitmap instead of
    // attempting to compute our own.
    const size_t rowBytes = bitmap->rowBytes();
    // 1. 创建一个数组，通过JNI在java层创建的
    jbyteArray arrayObj = (jbyteArray) env->CallObjectMethod(gVMRuntime,
                                                             gVMRuntime_newNonMovableArray,
                                                             gByte_class, size);
    ...
    // 2. 获取创建的数组的地址
    jbyte* addr = (jbyte*) env->CallLongMethod(gVMRuntime, gVMRuntime_addressOf, arrayObj);
    ...
    //3. 创建Bitmap，传这个地址
    android::Bitmap* wrapper = new android::Bitmap(env, arrayObj, (void*) addr,
            info, rowBytes, ctable);
    wrapper->getSkBitmap(bitmap);
    // since we're already allocated, we lockPixels right away
    // HeapAllocator behaves this way too
    bitmap->lockPixels();

    return wrapper;
}
复制代码

```

可以看到，7.0 像素内存的分配是这样的：

1.  通过 JNI 调用 java 层创建一个数组
2.  然后创建 native 层 Bitmap，把数组的地址传进去。

由此说明，7.0 的 Bitmap 像素数据是放在 java 堆的。

当然，3.0 以下 Bitmap 像素内存据说也是放在 native 堆的，但是需要手动释放 native 层的 Bitmap，也就是需要手动调用 recycle 方法，native 层内存才会被回收。这个大家可以自己去看源码验证。

###### native 层 Bitmap 回收问题

Java 层的 Bitmap 对象由垃圾回收器自动回收，而 native 层 Bitmap 印象中我们是不需要手动回收的，源码中如何处理的呢？

记得有个面试题是这样的：

> 说说 final、finally、finalize 的关系

三者除了长得像，其实没有半毛钱关系，final、finally 大家都用的比较多，而 `finalize` 用的少，或者没用过，`finalize` 是 Object 类的一个方法，注释是这样的：

```
/**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the {@code finalize} method to dispose of
     * system resources or to perform other cleanup.
     * <p>
     ...**/
  protected void finalize() throws Throwable { }
复制代码

```

意思是说，垃圾回收器确认这个对象没有其它地方引用到它的时候，会调用这个对象的`finalize`方法，子类可以重写这个方法，做一些释放资源的操作。

**在 6.0 以前，Bitmap 就是通过这个 finalize 方法来释放 native 层对象的。** [6.0 Bitmap.java](https://www.androidos.net.cn/android/6.0.1_r16/xref/frameworks/base/graphics/java/android/graphics/Bitmap.java)

```
Bitmap(long nativeBitmap, byte[] buffer, int width, int height, int density,
            boolean isMutable, boolean requestPremultiplied,
            byte[] ninePatchChunk, NinePatch.InsetStruct ninePatchInsets) {
        ...
        mNativePtr = nativeBitmap;
        //1.创建 BitmapFinalizer
        mFinalizer = new BitmapFinalizer(nativeBitmap);
        int nativeAllocationByteCount = (buffer == null ? getByteCount() : 0);
        mFinalizer.setNativeAllocationByteCount(nativeAllocationByteCount);
}

 private static class BitmapFinalizer {
        private long mNativeBitmap;

        // Native memory allocated for the duration of the Bitmap,
        // if pixel data allocated into native memory, instead of java byte[]
        private int mNativeAllocationByteCount;

        BitmapFinalizer(long nativeBitmap) {
            mNativeBitmap = nativeBitmap;
        }

        public void setNativeAllocationByteCount(int nativeByteCount) {
            if (mNativeAllocationByteCount != 0) {
                VMRuntime.getRuntime().registerNativeFree(mNativeAllocationByteCount);
            }
            mNativeAllocationByteCount = nativeByteCount;
            if (mNativeAllocationByteCount != 0) {
                VMRuntime.getRuntime().registerNativeAllocation(mNativeAllocationByteCount);
            }
        }

        @Override
        public void finalize() {
            try {
                super.finalize();
            } catch (Throwable t) {
                // Ignore
            } finally {
                //2.就是这里了，
                setNativeAllocationByteCount(0);
                nativeDestructor(mNativeBitmap);
                mNativeBitmap = 0;
            }
        }
    }

复制代码

```

在 Bitmap 构造方法创建了一个 `BitmapFinalizer`类，重写 finalize 方法，在 java 层 Bitmap 被回收的时候，BitmapFinalizer 对象也会被回收，finalize 方法肯定会被调用，在里面释放 native 层 Bitmap 对象。

6.0 之后做了一些变化，BitmapFinalizer 没有了，被 [NativeAllocationRegistry](https://www.androidos.net.cn/android/8.0.0_r4/xref/libcore/luni/src/main/java/libcore/util/NativeAllocationRegistry.java) 取代。

例如 8.0 Bitmap 构造方法

```
    Bitmap(long nativeBitmap, int width, int height, int density,
            boolean isMutable, boolean requestPremultiplied,
            byte[] ninePatchChunk, NinePatch.InsetStruct ninePatchInsets) {
       
        ...
        mNativePtr = nativeBitmap;
        long nativeSize = NATIVE_ALLOCATION_SIZE + getAllocationByteCount();
        //  创建NativeAllocationRegistry这个类，调用registerNativeAllocation 方法
        NativeAllocationRegistry registry = new NativeAllocationRegistry(
            Bitmap.class.getClassLoader(), nativeGetNativeFinalizer(), nativeSize);
        registry.registerNativeAllocation(this, nativeBitmap);
    }
复制代码

```

NativeAllocationRegistry 就不分析了， **不管是 BitmapFinalizer 还是 NativeAllocationRegistry，目的都是在 java 层 Bitmap 被回收的时候，将 native 层 Bitmap 对象也回收掉。** 一般情况下我们无需手动调用 recycle 方法，由 GC 去盘它即可。

上面分析了 Bitmap 像素存储位置，我们知道，Android 8.0 之后 Bitmap 像素内存放在 native 堆，Bitmap 导致 OOM 的问题基本不会在 8.0 以上设备出现了（没有内存泄漏的情况下），那 8.0 以下设备怎么办？赶紧升级或换手机吧~

![](https://user-gold-cdn.xitu.io/2019/11/3/16e3183d9ec9fa30?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

我们换手机当然没问题，但是并不是所有人都能跟上 Android 系统更新的步伐，所以，问题还是要解决~

Fresco 之所以能跟 Glide 正面交锋，必然有其独特之处，文中开头列出 Fresco 的优点是：“在 5.0 以下 (最低 2.3) 系统，Fresco 将图片放到一个特别的内存区域(Ashmem 区)” 这个 Ashmem 区是一块匿名共享内存，Fresco 将 Bitmap 像素放到共享内存去了，共享内存是属于 native 堆内存。

Fresco 关键源码在 `PlatformDecoderFactory` 这个类

```
public class PlatformDecoderFactory {

  /**
   * Provide the implementation of the PlatformDecoder for the current platform using the provided
   * PoolFactory
   *
   * @param poolFactory The PoolFactory
   * @return The PlatformDecoder implementation
   */
  public static PlatformDecoder buildPlatformDecoder(
      PoolFactory poolFactory, boolean gingerbreadDecoderEnabled) {
    //8.0 以上用 OreoDecoder 这个解码器
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      int maxNumThreads = poolFactory.getFlexByteArrayPoolMaxNumThreads();
      return new OreoDecoder(
          poolFactory.getBitmapPool(), maxNumThreads, new Pools.SynchronizedPool<>(maxNumThreads));
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      //大于5.0小于8.0用 ArtDecoder 解码器
      int maxNumThreads = poolFactory.getFlexByteArrayPoolMaxNumThreads();
      return new ArtDecoder(
          poolFactory.getBitmapPool(), maxNumThreads, new Pools.SynchronizedPool<>(maxNumThreads));
    } else {
      if (gingerbreadDecoderEnabled && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        //小于4.4 用 GingerbreadPurgeableDecoder 解码器
        return new GingerbreadPurgeableDecoder();
      } else {
        //这个就是4.4到5.0 用的解码器了
        return new KitKatPurgeableDecoder(poolFactory.getFlexByteArrayPool());
      }
    }
  }
}

复制代码

```

8.0 先不看了，看一下 4.4 以下是怎么得到 Bitmap 的，看下`GingerbreadPurgeableDecoder`这个类有个获取 Bitmap 的方法

```
//GingerbreadPurgeableDecoder
private Bitmap decodeFileDescriptorAsPurgeable(
      CloseableReference<PooledByteBuffer> bytesRef,
      int inputLength,
      byte[] suffix,
      BitmapFactory.Options options) {
    //  MemoryFile ：匿名共享内存
    MemoryFile memoryFile = null;
    try {
      //将图片数据拷贝到匿名共享内存
      memoryFile = copyToMemoryFile(bytesRef, inputLength, suffix);
      FileDescriptor fd = getMemoryFileDescriptor(memoryFile);
      if (mWebpBitmapFactory != null) {
        // 创建Bitmap，Fresco自己写了一套创建Bitmap方法
        Bitmap bitmap = mWebpBitmapFactory.decodeFileDescriptor(fd, null, options);
        return Preconditions.checkNotNull(bitmap, "BitmapFactory returned null");
      } else {
        throw new IllegalStateException("WebpBitmapFactory is null");
      }
    } 
  }

复制代码

```

捋一捋，4.4 以下，Fresco 使用匿名共享内存来保存 Bitmap 数据，首先将图片数据拷贝到匿名共享内存中，然后使用 Fresco 自己写的加载 Bitmap 的方法。

Fresco 对不同 Android 版本使用不同的方式去加载 Bitmap，至于 4.4-5.0，5.0-8.0，8.0 以上，对应另外三个解码器，大家可以从`PlatformDecoderFactory` 这个类入手，自己去分析，思考为什么不同平台要分这么多个解码器，8.0 以下都用匿名共享内存不好吗？期待你在评论区跟大家分享~

### 2.5 ImageView 内存泄露

> 曾经在 Vivo 驻场开发，带有头像功能的页面被测出内存泄漏，原因是 SDK 中有个加载网络头像的方法，持有 ImageView 引用导致的。

当然，修改也比较简单粗暴，**将 ImageView 用 WeakReference 修饰**就完事了。

事实上，这种方式虽然解决了内存泄露问题，但是并不完美，例如在界面退出的时候，我们除了希望 ImageView 被回收，同时希望加载图片的任务可以取消，队未执行的任务可以移除。

Glide 的做法是监听生命周期回调，看 `RequestManager` 这个类

```
public void onDestroy() {
    targetTracker.onDestroy();
    for (Target<?> target : targetTracker.getAll()) {
      //清理任务
      clear(target);
    }
    targetTracker.clear();
    requestTracker.clearRequests();
    lifecycle.removeListener(this);
    lifecycle.removeListener(connectivityMonitor);
    mainHandler.removeCallbacks(addSelfToLifecycle);
    glide.unregisterRequestManager(this);
  }
复制代码

```

在 Activity/fragment 销毁的时候，取消图片加载任务，细节大家可以自己去看源码。

### 2.6 列表加载问题

#### 图片错乱

由于 RecyclerView 或者 LIstView 的复用机制，网络加载图片开始的时候 ImageView 是第一个 item 的，加载成功之后 ImageView 由于复用可能跑到第 10 个 item 去了，在第 10 个 item 显示第一个 item 的图片肯定是错的。

常规的做法是给 ImageView 设置 tag，tag 一般是图片地址，更新 ImageView 之前判断 tag 是否跟 url 一致。

当然，可以在 item 从列表消失的时候，取消对应的图片加载任务。要考虑放在图片加载框架做还是放在 UI 做比较合适。

#### 线程池任务过多

列表滑动，会有很多图片请求，如果是第一次进入，没有缓存，那么队列会有很多任务在等待。所以在请求网络图片之前，需要判断队列中是否已经存在该任务，存在则不加到队列去。

总结
--

本文通过 Glide 开题，分析一个图片加载框架必要的需求，以及各个需求涉及到哪些技术和原理。

*   异步加载：最少两个线程池
*   切换到主线程：Handler
*   缓存：LruCache、DiskLruCache，涉及到 LinkHashMap 原理
*   防止 OOM：软引用、LruCache、图片压缩没展开讲、Bitmap 像素存储位置源码分析、Fresco 部分源码分析
*   内存泄露：注意 ImageView 的正确引用，生命周期管理
*   列表滑动加载的问题：加载错乱用 tag、队满任务存在则不添加

文中也遗留一些问题，例如：  
Fresco 为什么要在不同 Android 版本上使用不同解码器去获取 Bitmap，8.0 以下都用匿名共享内存不可以吗？期待你主动学习并且在评论区跟大家分享~

就这样，欢迎评论区留言~

相关参考文章：  
[图解 LinkedHashMap 原理](https://www.jianshu.com/p/8f4f58b4b8ab)  
[谈谈 fresco 的 bitmap 内存分配](https://blog.csdn.net/chiefhsing/article/details/53899242)

我在掘金发布的其它文章：

[总结 UI 原理和高级的 UI 优化方式](https://juejin.im/post/5dac6aa2518825630e5d17da)  
[面试官：说说多线程并发问题](https://juejin.im/post/5d7da37d6fb9a06b0202f156)  
[面试官又来了：你的 app 卡顿过吗？](https://juejin.im/post/5d837cd1e51d4561cb5ddf66)  
[面试官：今日头条启动很快，你觉得可能是做了哪些优化？](https://juejin.im/post/5d95f4a4f265da5b8f10714b)