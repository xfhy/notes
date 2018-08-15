
# SparseArray源码解析

## 1 概述

在前文中，我们已经聊过了`HashMap`和`LinkedHashMap` `ArrayMap`.所以如果没看过，可以先阅读<br>
[面试必备：HashMap源码解析（JDK8）](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/77413921) ,<br>
[面试必备：LinkedHashMap源码解析（JDK8](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/77429150) ，<br>
[面试必备：ArrayMap源码解析](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/78333328)<br>
今天依旧是看看android sdk的源码。

本文将从几个常用方法下手，来阅读`SparseArray`的源码。<br>
按照从构造方法-&gt;常用API（增、删、改、查）的顺序来阅读源码，并会讲解阅读方法中涉及的一些变量的意义。了解`SparseArray`的特点、适用场景。

**如果本文中有不正确的结论、说法，请大家提出和我讨论，共同进步，谢谢。**

## 2 概要

概括的说，`SparseArray&lt;E&gt;`是用于在Android平台上替代`HashMap`的数据结构,更具体的说，<br>
是用于替代`key`为`int`类型，`value`为`Object`类型的`HashMap`。<br>
和`ArrayMap`类似，它的实现相比于`HashMap`更加**节省空间**，而且由于key指定为`int`类型，也可以节省`int`-`Integer`的**装箱拆箱**操作带来的**性能消耗**。

它仅仅实现了`implements Cloneable`接口，所以使用时不能用`Map`作为声明类型来使用。

它也是**线程不安全**的，允许value为null。

从原理上说，<br>
它的内部实现也是**基于两个数组**。<br>
一个`int[]`数组`mKeys`，用于保存每个item的`key`，`key`本身就是`int`类型，所以可以理解`hashCode`值就是`key`的值.<br>
一个`Object[]`数组`mValues`，保存`value`。**容量**和`key`数组的**一样**。

类似`ArrayMap`,<br>
它扩容的更合适，**扩容时只需要数组拷贝工作，不需要重建哈希表**。

同样它**不适合大容量**的数据存储。存储大量数据时，它的性能将退化至少50%。

比传统的`HashMap`**时间效率低**。<br>
因为其会对key从小到大排序，使用**二分法**查询key对应在数组中的下标。<br>
在添加、删除、查找数据的时候都是**先使用二分查找法得到相应的index**，然后通过index来进行添加、查找、删除等操作。

所以其是按照`key`的大小排序存储的。

另外，`SparseArray`**为了提升性能**，在**删除操作时**做了一些**优化**：<br>
当删除一个元素时，并不是立即从`value`数组中删除它，并压缩数组，<br>
而是将其在`value`数组中**标记为已删除**。这样当存储相同的`key`的`value`时，可以**重用**这个空间。<br>
如果该空间没有被重用，随后将在合适的时机里执行gc（垃圾收集）操作，将数组压缩，以免浪费空间。

### 适用场景：

<li>
**数据量不大**（千以内）</li>
<li>
**空间**比时间**重要**
</li>
- 需要使用`Map`，且`key`为`int`类型。

示例代码：

```
        SparseArray&lt;String&gt; stringSparseArray = new SparseArray&lt;&gt;();
        stringSparseArray.put(1,"a");
        stringSparseArray.put(5,"e");
        stringSparseArray.put(4,"d");
        stringSparseArray.put(10,"h");
        stringSparseArray.put(2,null);

        Log.d(TAG, "onCreate() called with: stringSparseArray = [" + stringSparseArray + "]");

```

输出：

```
//可以看出是按照key排序的
onCreate() called with: stringSparseArray = [{1=a, 2=null, 4=d, 5=e, 10=h}]

```

## 3 构造函数

```
    //用于标记value数组，作为已经删除的标记
    private static final Object DELETED = new Object();
    //是否需要GC 
    private boolean mGarbage = false;
    
    //存储key 的数组
    private int[] mKeys;
    //存储value 的数组
    private Object[] mValues;
    //集合大小
    private int mSize;
    
    //默认构造函数，初始化容量为10
    public SparseArray() {
        this(10);
    }
    //指定初始容量
    public SparseArray(int initialCapacity) {
        //初始容量为0的话，就赋值两个轻量级的引用
        if (initialCapacity == 0) {
            mKeys = EmptyArray.INT;
            mValues = EmptyArray.OBJECT;
        } else {
        //初始化对应长度的数组
            mValues = ArrayUtils.newUnpaddedObjectArray(initialCapacity);
            mKeys = new int[mValues.length];
        }
        //集合大小为0
        mSize = 0;
    }

```

构造函数 无亮点，路过。<br>
关注一下几个变量：

- 底层数据结构为`int[]`和`Object[]`类型数组。
<li>
`mGarbage`: 是否需要GC</li>
<li>
`DELETED`: 用于标记value数组，作为已经删除的标记</li>

## 4 增 、改

### 4.1 单个增、改：

```
    public void put(int key, E value) {
        //利用二分查找，找到 待插入key 的 下标index
        int i = ContainerHelpers.binarySearch(mKeys, mSize, key);
        //如果返回的index是正数，说明之前这个key存在，直接覆盖value即可
        if (i &gt;= 0) {
            mValues[i] = value;
        } else {
            //若返回的index是负数，说明 key不存在.
            
            //先对返回的i取反，得到应该插入的位置i
            i = ~i;
            //如果i没有越界，且对应位置是已删除的标记，则复用这个空间
            if (i &lt; mSize &amp;&amp; mValues[i] == DELETED) {
            //赋值后，返回
                mKeys[i] = key;
                mValues[i] = value;
                return;
            }
            
            //如果需要GC，且需要扩容
            if (mGarbage &amp;&amp; mSize &gt;= mKeys.length) {
                //先触发GC
                gc();
                //gc后，下标i可能发生变化，所以再次用二分查找找到应该插入的位置i
                // Search again because indices may have changed.
                i = ~ContainerHelpers.binarySearch(mKeys, mSize, key);
            }
            //插入key（可能需要扩容）
            mKeys = GrowingArrayUtils.insert(mKeys, mSize, i, key);
            //插入value（可能需要扩容）
            mValues = GrowingArrayUtils.insert(mValues, mSize, i, value);
            //集合大小递增
            mSize++;
        }
    }
    //二分查找 基础知识不再详解
    static int binarySearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;

        while (lo &lt;= hi) {
            //关注一下高效位运算
            final int mid = (lo + hi) &gt;&gt;&gt; 1;
            final int midVal = array[mid];

            if (midVal &lt; value) {
                lo = mid + 1;
            } else if (midVal &gt; value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        //若没找到，则lo是value应该插入的位置，是一个正数。对这个正数去反，返回负数回去
        return ~lo;  // value not present
    }
    
    //垃圾回收函数,压缩数组
    private void gc() {
        //保存GC前的集合大小
        int n = mSize;
        //既是下标index，又是GC后的集合大小
        int o = 0;
        int[] keys = mKeys;
        Object[] values = mValues;
        //遍历values集合，以下算法 意义为 从values数组中，删除所有值为DELETED的元素
        for (int i = 0; i &lt; n; i++) {
            Object val = values[i];
            //如果当前value 没有被标记为已删除
            if (val != DELETED) {
                //压缩keys、values数组
                if (i != o) {
                    keys[o] = keys[i];
                    values[o] = val;
                    //并将当前元素置空，防止内存泄漏
                    values[i] = null;
                }
                //递增o
                o++;
            }
        }
        //修改 标识，不需要GC
        mGarbage = false;
        //更新集合大小
        mSize = o;
    }

```

GrowingArrayUtils.insert:

```
    //
    public static int[] insert(int[] array, int currentSize, int index, int element) {
        //断言 确认 当前集合长度 小于等于 array数组长度
        assert currentSize &lt;= array.length;
        //如果不需要扩容
        if (currentSize + 1 &lt;= array.length) {
            //将array数组内元素，从index开始 后移一位
            System.arraycopy(array, index, array, index + 1, currentSize - index);
            //在index处赋值
            array[index] = element;
            //返回
            return array;
        }
        //需要扩容
        //构建新的数组
        int[] newArray = new int[growSize(currentSize)];
        //将原数组中index之前的数据复制到新数组中
        System.arraycopy(array, 0, newArray, 0, index);
        //在index处赋值
        newArray[index] = element;
        //将原数组中index及其之后的数据赋值到新数组中
        System.arraycopy(array, index, newArray, index + 1, array.length - index);
        //返回
        return newArray;
    }
    //根据现在的size 返回合适的扩容后的容量
    public static int growSize(int currentSize) {
        //如果当前size 小于等于4，则返回8， 否则返回当前size的两倍
        return currentSize &lt;= 4 ? 8 : currentSize * 2;
    }

```

<li><p>**二分查找**，若未找到返回下标时，**与JDK里的实现不同**，JDK是返回`return -(low + 1); // key not found.`,而这里是对 **低位去反** 返回。<br>
这样在函数调用处，根据返回值的正负，可以判断是否找到index。**对负index取反，即可得到应该插入的位置**。</p></li>
- **扩容时**，当前容量小于等于4，则扩容后容量为8.否则为**当前容量的两倍**。和`ArrayList,ArrayMap`不同(扩容一半)，和`Vector`相同(扩容一倍)。
- 扩容操作依然是用数组的复制、覆盖完成。类似`ArrayList`.

## 5 删

### 5.1 按照key删除

```
    //按照key删除
    public void remove(int key) {
        delete(key);
    }
    
    public void delete(int key) {
        //二分查找得到要删除的key所在index
        int i = ContainerHelpers.binarySearch(mKeys, mSize, key);
        //如果&gt;=0,表示存在
        if (i &gt;= 0) {
            //修改values数组对应位置为已删除的标志DELETED
            if (mValues[i] != DELETED) {
                mValues[i] = DELETED; 
                //并修改 mGarbage ,表示稍后需要GC
                mGarbage = true;
            }
        }
    }

```

### 5.2 按照index删除

```
    public void removeAt(int index) {
        //根据index直接索引到对应位置 执行删除操作
        if (mValues[index] != DELETED) {
            mValues[index] = DELETED;
            mGarbage = true;
        }
    }

```

### 5.3 批量删除

```
    public void removeAtRange(int index, int size) {
    //越界修正
        final int end = Math.min(mSize, index + size);
        //for循环 执行单个删除操作
        for (int i = index; i &lt; end; i++) {
            removeAt(i);
        }
    }

```

## 6 查

### 6.1 按照key查询

```
    //按照key查询，如果key不存在，返回null
    public E get(int key) {
        return get(key, null);
    }

    //按照key查询，如果key不存在，返回valueIfKeyNotFound
    public E get(int key, E valueIfKeyNotFound) {
        //二分查找到 key 所在的index
        int i = ContainerHelpers.binarySearch(mKeys, mSize, key);
        //不存在
        if (i &lt; 0 || mValues[i] == DELETED) {
            return valueIfKeyNotFound;
        } else {//存在
            return (E) mValues[i];
        }
    }

```

### 6.2 按照下标查询

```
    public int keyAt(int index) {
    //按照下标查询时，需要考虑是否先GC
        if (mGarbage) {
            gc();
        }

        return mKeys[index];
    }
    
    public E valueAt(int index) {
     //按照下标查询时，需要考虑是否先GC
        if (mGarbage) {
            gc();
        }

        return (E) mValues[index];
    }

```

### 6.3查询下标：

```
    public int indexOfKey(int key) {
     //查询下标时，也需要考虑是否先GC
        if (mGarbage) {
            gc();
        }
        //二分查找返回 对应的下标 ,可能是负数
        return ContainerHelpers.binarySearch(mKeys, mSize, key);
    }
    public int indexOfValue(E value) {
     //查询下标时，也需要考虑是否先GC
        if (mGarbage) {
            gc();
        }
        //不像key一样使用的二分查找。是直接线性遍历去比较，而且不像其他集合类使用equals比较，这里直接使用的 ==
        //如果有多个key 对应同一个value，则这里只会返回一个更靠前的index
        for (int i = 0; i &lt; mSize; i++)
            if (mValues[i] == value)
                return i;

        return -1;
    }

```

<li>按照value查询下标时，不像key一样使用的二分查找。是直接**线性遍历**去比较，而且不像其他集合类使用`equals`比较，这里直接使用的 **==**
</li>
<li>如果有多个key 对应同一个value，则这里只会返回一个**更靠前的index**
</li>

## 总结

`SparseArray`的源码相对来说比较简单，经过之前几个集合的源码洗礼，很轻松就可以掌握大体流程和关键思想：**时间换空间**。

Android sdk中，还提供了三个类似思想的集合：

<li>
`SparseBooleanArray`,`value`为`boolean`
</li>
<li>
`SparseIntArray`,`value`为`int`
</li>
<li>
`SparseLongArray`,`value`为`long`
</li>

他们和`SparseArray`**唯一的区别在于`value`的类型**，`SparseArray`的`value`可以是任意类型。而它们是三个常使用的拆箱后的基本类型。
