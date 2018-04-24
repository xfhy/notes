# ArrayList源码分析

# ArrayList源码概述

## 一、ArrayList的基本特点

1. 快速随机访问
2. 允许存放多个null元素
3. 底层是Object数组
4. 增加元素个数可能很慢(可能需要扩容),删除元素可能很慢(可能需要移动很多元素),改对应索引元素比较快


## 二、ArrayList的继承关系

![](http://olg7c0d2n.bkt.clouddn.com/18-4-24/67873829.jpg)

来看下源码中的定义

```java
public class ArrayList<E> extends AbstractList<E> 
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable
```

- 可以看到继承了AbstractList,此类提供 List 接口的骨干实现，以最大限度地减少实现"随机访问"数据存储（如数组）支持的该接口所需的工作.对于连续的访问数据（如链表），应优先使用 AbstractSequentialList，而不是此类.

- 实现了List接口,意味着ArrayList元素是有序的,可以重复的,可以有null元素的集合.

- 实现了RandomAccess接口标识着其支持随机快速访问,实际上,我们查看RandomAccess源码可以看到,其实里面什么都没有定义.因为ArrayList底层是数组,那么随机快速访问是理所当然的,访问速度O(1).

- 实现了Cloneable接口,标识着可以它可以被复制.注意,ArrayList里面的clone()复制其实是浅复制(不知道此概念的赶快去查资料,这知识点非常重要).

- 实现了Serializable 标识着集合可被序列化。

## 三、ArrayList 的构造方法

在说构造方法之前我们要先看下与构造参数有关的几个全局变量：

```java
/**
 * ArrayList 默认的数组容量
 */
 private static final int DEFAULT_CAPACITY = 10;

/**
 * 用于空实例的共享空数组实例
 */
 private static final Object[] EMPTY_ELEMENTDATA = {};

/**
 * 另一个共享空数组实例，用的不多,用于区别上面的EMPTY_ELEMENTDATA
 */
 private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

/**
 * ArrayList底层的容器  
 */
transient Object[] elementData; // non-private to simplify nested class access

//当前存放了多少个元素   并非数组大小
private int size;
```

注意到,底层容器数组的前面有一个transient关键字,啥意思??

查阅[资料](https://blog.csdn.net/zero__007/article/details/52166306)后,大概知道:transient标识之后是不被序列化的

但是ArrayList实际容器就是这个数组为什么标记为不序列化??那岂不是反序列化时会丢失原来的数据?

![](https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3388479372,1706122097&fm=27&gp=0.jpg)

其实是ArrayList在序列化的时候会调用writeObject()，直接将size和element写入ObjectOutputStream；反序列化时调用readObject()，从ObjectInputStream获取size和element，再恢复到elementData。

原因在于elementData是一个缓存数组，它通常会预留一些容量，等容量不足时再扩充容量，那么有些空间可能就没有实际存储元素，采用上诉的方式来实现序列化时，就可以保证只序列化实际存储的那些元素，而不是整个数组，从而节省空间和时间。

### 无参构造方法

```java
/**
 * 构造一个初始容量为10的空列表。
 */
public ArrayList() {
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}
```

命名里面讲elementData指向了一个空数组，为什么注释却说初始容量为10。这里先卖个关子，稍后分析。

### 指定初始容量的构造方法

```java
public ArrayList(int initialCapacity) {
        //容量>0 -> 构建数组
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
          //容量==0  指向空数组
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
          //容量<0  报错呗
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        }
    }
```

如果我们预先知道一个集合元素的容纳的个数的时候推荐使用这个构造方法，避免使用ArrayList默认的扩容机制而带来额外的开销.

### 使用另一个集合 Collection 的构造方法

```java
/**
 * 构造一个包含指定集合元素的列表，元素的顺序由集合的迭代器返回。
 */
 public ArrayList(Collection<? extends E> c) {
    elementData = c.toArray();
    if ((size = elementData.length) != 0) {
        // c.toArray 可能(错误地)不返回 Object[]类型的数组 参见 jdk 的 bug 列表(6260652)
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, size, Object[].class);
    } else {
        // 如果集合大小为空将赋值为 EMPTY_ELEMENTDATA    空数组
        this.elementData = EMPTY_ELEMENTDATA;
    }
}
```

## 四.增加元素+扩容机制

### 1. 添加单个元素

```java
/**
* 添加指定元素到末尾
*/
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}

private void ensureCapacityInternal(int minCapacity) {
    //如果是以ArrayList()构造方法初始化,那么数组指向的是DEFAULTCAPACITY_EMPTY_ELEMENTDATA.第一次add()元素会进入if内部,
    //且minCapacity为1,那么最后minCapacity肯定是10,所以ArrayList()构造方法上面有那句很奇怪的注释.
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }

    ensureExplicitCapacity(minCapacity);
}

private void ensureExplicitCapacity(int minCapacity) {
    //列表结构被修改的次数,用于保证线程安全,如果在迭代的时候该值意外被修改,那么会报ConcurrentModificationException错
    modCount++;

    // 溢出?
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}

//扩容
private void grow(int minCapacity) {
    // overflow-conscious code
    //1. 记录之前的数组长度
    int oldCapacity = elementData.length;
    //2. 新数组的大小=老数组大小+老数组大小的一半
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    //3. 判断上面的扩容之后的大小newCapacity是否够装minCapacity个元素
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;

    //4.判断新数组容量是否大于最大值
    //如果新数组容量比最大值(Integer.MAX_VALUE - 8)还大,那么交给hugeCapacity()去处理,该抛异常则抛异常
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    //5. 复制数组,注意,这里是浅复制
    elementData = Arrays.copyOf(elementData, newCapacity);
}

//巨大容量,,,666,这个名字取得好
private static int hugeCapacity(int minCapacity) {
    //溢出啦,扔出一个小错误
    if (minCapacity < 0) // overflow
        throw new OutOfMemoryError();
    return (minCapacity > MAX_ARRAY_SIZE) ?
        Integer.MAX_VALUE :
        MAX_ARRAY_SIZE;
}

```
大体思路:

1. 首先判断如果新添加一个元素是否会导致数组溢出
    
    判断是否溢出:如果原数组是空的,那么第一次添加元素时会给数组一个默认大小10.接着是判断是否溢出,如果溢出则去扩容,扩容规则: **新数组大小是原来数组大小的1.5倍**,最后通过Arrays.copyOf()去浅复制.

2. 添加元素到末尾

### 2. 添加元素到指定位置

```java
/**
* 添加元素在index处,对应索引处元素(如果有)和后面的元素往后移一位,腾出坑
*/
public void add(int index, E element) {
    //1. 入参合法性检查
    rangeCheckForAdd(index);

    //2. 是否需要扩容
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    //3. 将elementData从index开始的size - index个元素复制到elementData的`index + 1`处
    //相当于index处以及后面的往后移动了一位
    System.arraycopy(elementData, index, elementData, index + 1,
                        size - index);
    //4. 将元素放到index处   填坑
    elementData[index] = element;
    //5. 记录当前真实数据个数
    size++;
}

//index不合法时,抛IndexOutOfBoundsException
private void rangeCheckForAdd(int index) {
    if (index > size || index < 0)
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}
```

大体思路:这里理解了上面的扩容之后,这里是比较简单的.其实就是在数组的某一个位置插入元素,那么我们将该索引处往后移动一位,腾出一个坑,最后将该元素放到此索引处(填坑)就行啦.


