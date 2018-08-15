
        
# HashMap源码解析（JDK8）

## 1 概述

本文将从几个常用方法下手，来阅读`HashMap`的源码。<br>
按照从构造方法->常用API（增、删、改、查）的顺序来阅读源码，并会讲解阅读方法中涉及的一些变量的意义。了解`HashMap`的特点、适用场景。

**如果本文中有不正确的结论、说法，请大家提出和我讨论，共同进步，谢谢。**

## 2 概要

概括的说，`HashMap` 是一个**关联数组、哈希表**，它是**线程不安全**的，允许**key为null**,**value为null**。遍历时**无序**。<br>
其底层数据结构是**数组**称之为**哈希桶**，每个**桶里面放的是链表**，链表中的**每个节点**，就是哈希表中的**每个元素**。<br>
在JDK8中，当链表长度达到8，会转化成红黑树，以提升它的查询、插入效率，它实现了`Map<K,V>, Cloneable, Serializable`接口。

因其底层哈希桶的数据结构是数组，所以也会涉及到**扩容**的问题。

当`HashMap`的容量达到`threshold`域值时，就会触发扩容。扩容前后，哈希桶的**长度一定会是2的次方**。<br>
这样在根据key的hash值寻找对应的哈希桶时，可以**用位运算替代取余操作**，**更加高效**。

而key的hash值，并不仅仅只是key对象的`hashCode()`方法的返回值，还会经过**扰动函数**的扰动，以使hash值更加均衡。<br>
因为`hashCode()`是`int`类型，取值范围是40多亿，只要哈希函数映射的比较均匀松散，碰撞几率是很小的。<br>
但就算原本的`hashCode()`取得很好，每个key的`hashCode()`不同，但是由于`HashMap`的哈希桶的长度远比hash取值范围小，默认是16，所以当对hash值以桶的长度取余，以找到存放该key的桶的下标时，由于取余是通过与操作完成的，会忽略hash值的高位。因此只有`hashCode()`的低位参加运算，发生不同的hash值，但是得到的index相同的情况的几率会大大增加，这种情况称之为**hash碰撞。** 即，碰撞率会增大。

**扰动函数**就是为了解决hash碰撞的。它会综合hash值高位和低位的特征，并存放在低位，因此在与运算时，相当于高低位一起参与了运算，以减少hash碰撞的概率。（在JDK8之前，扰动函数会扰动四次，JDK8简化了这个操作）

扩容操作时，会new一个新的`Node`数组作为哈希桶，然后将原哈希表中的所有数据(`Node`节点)移动到新的哈希桶中，相当于对原哈希表中所有的数据重新做了一个put操作。所以性能消耗很大，**可想而知，在哈希表的容量越大时，性能消耗越明显。**

扩容时，如果发生过哈希碰撞，节点数小于8个。则要根据链表上每个节点的哈希值，依次放入新哈希桶对应下标位置。<br>
因为扩容是容量翻倍，所以原链表上的每个节点，现在可能存放在原来的下标，即low位， 或者扩容后的下标，即high位。 high位=  low位+原哈希桶容量<br>
如果追加节点后，链表数量》=8，则转化为红黑树

由迭代器的实现可以看出，遍历HashMap时，顺序是按照哈希桶从低到高，链表从前往后，依次遍历的。属于**无序**集合。

整个HashMap示意图：图片来源于网络，侵删：

## 

![](http://upload-images.jianshu.io/upload_images/1696338-1529a68fefebc912)


`HashMap`的源码中，充斥个各种位运算代替常规运算的地方，以提升效率：

<li>与运算替代模运算。用 `hash &amp; (table.length-1)` 替代 `hash % (table.length)`
</li>
- 用`if ((e.hash &amp; oldCap) == 0)`判断扩容后，节点e处于低区还是高区。

## 3 链表节点Node

在开始之前，我们先看一下挂载在哈希表上的元素，链表的结构：

```java
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;//哈希值
        final K key;//key
        V value;//value
        Node<K,V> next;//链表后置节点

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        //每一个节点的hash值，是将key的hashCode 和 value的hashCode 亦或得到的。
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
        //设置新的value 同时返回旧value
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
        
        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &amp;&amp;
                    Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }
    

```

<strong>由此可知，这是一个单链表~。<br>
每一个节点的hash值，是将key的hashCode 和 value的hashCode 亦或得到的。</strong>

## 4 构造函数

```java
    //最大容量 2的30次方
    static final int MAXIMUM_CAPACITY = 1 << 30;
    //默认的加载因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    //哈希桶，存放链表。 长度是2的N次方，或者初始化时为0.
    transient Node<K,V>[] table;
        
    //加载因子，用于计算哈希表元素数量的阈值。  threshold = 哈希桶.length * loadFactor;
    final float loadFactor;
    //哈希表内元素数量的阈值，当哈希表内元素数量超过阈值时，会发生扩容resize()。
    int threshold;

    public HashMap() {
        //默认构造函数，赋值加载因子为默认的0.75f
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
    public HashMap(int initialCapacity) {
        //指定初始化容量的构造函数
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    //同时指定初始化容量 以及 加载因子， 用的很少，一般不会修改loadFactor
    public HashMap(int initialCapacity, float loadFactor) {
        //边界处理
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        //初始容量最大不能超过2的30次方
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        //显然加载因子不能为负数
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        //设置阈值为  》=初始化容量的 2的n次方的值
        this.threshold = tableSizeFor(initialCapacity);
    }
    //新建一个哈希表，同时将另一个map m 里的所有元素加入表中
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
    

```

```java
    //根据期望容量cap，返回2的n次方形式的 哈希桶的实际容量 length。 返回值一般会>=cap 
    static final int tableSizeFor(int cap) {
    //经过下面的 或 和位移 运算， n最终各位都是1。
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        //判断n是否越界，返回 2的n次方作为 table（哈希桶）的阈值
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

```

```java
    //将另一个Map的所有元素加入表中，参数evict初始化时为false，其他情况为true
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        //拿到m的元素数量
        int s = m.size();
        //如果数量大于0
        if (s > 0) {
            //如果当前表是空的
            if (table == null) { // pre-size
                //根据m的元素数量和当前表的加载因子，计算出阈值
                float ft = ((float)s / loadFactor) + 1.0F;
                //修正阈值的边界 不能超过MAXIMUM_CAPACITY
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                         (int)ft : MAXIMUM_CAPACITY);
                //如果新的阈值大于当前阈值
                if (t > threshold)
                    //返回一个 》=新的阈值的 满足2的n次方的阈值
                    threshold = tableSizeFor(t);
            }
            //如果当前元素表不是空的，但是 m的元素数量大于阈值，说明一定要扩容。
            else if (s > threshold)
                resize();
            //遍历 m 依次将元素加入当前表中。
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

```

先看一下扩容函数： 这是一个重点！重点！重点！<br>
<strong>初始化或加倍哈希桶大小。如果是当前哈希桶是null,分配符合当前阈值的初始容量目标。<br>
否则，因为我们扩容成以前的两倍。<br>
在扩容时，要注意区分以前在哈希桶相同index的节点，现在是在以前的index里，还是index+oldlength 里</strong>

```java
final Node<K,V>[] resize() {
        //oldTab 为当前表的哈希桶
        Node<K,V>[] oldTab = table;
        //当前哈希桶的容量 length
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        //当前的阈值
        int oldThr = threshold;
        //初始化新的容量和阈值为0
        int newCap, newThr = 0;
        //如果当前容量大于0
        if (oldCap > 0) {
            //如果当前容量已经到达上限
            if (oldCap >= MAXIMUM_CAPACITY) {
                //则设置阈值是2的31次方-1
                threshold = Integer.MAX_VALUE;
                //同时返回当前的哈希桶，不再扩容
                return oldTab;
            }//否则新的容量为旧的容量的两倍。 
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &amp;&amp;
                     oldCap >= DEFAULT_INITIAL_CAPACITY)//如果旧的容量大于等于默认初始容量16
                //那么新的阈值也等于旧的阈值的两倍
                newThr = oldThr << 1; // double threshold
        }//如果当前表是空的，但是有阈值。代表是初始化时指定了容量、阈值的情况
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;//那么新表的容量就等于旧的阈值
        else {}//如果当前表是空的，而且也没有阈值。代表是初始化时没有任何容量/阈值参数的情况               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;//此时新表的容量为默认的容量 16
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);//新的阈值为默认容量16 * 默认加载因子0.75f = 12
        }
        if (newThr == 0) {//如果新的阈值是0，对应的是  当前表是空的，但是有阈值的情况
            float ft = (float)newCap * loadFactor;//根据新表容量 和 加载因子 求出新的阈值
            //进行越界修复
            newThr = (newCap < MAXIMUM_CAPACITY &amp;&amp; ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        //更新阈值 
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        //根据新的容量 构建新的哈希桶
            Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        //更新哈希桶引用
        table = newTab;
        //如果以前的哈希桶中有元素
        //下面开始将当前哈希桶中的所有节点转移到新的哈希桶中
        if (oldTab != null) {
            //遍历老的哈希桶
            for (int j = 0; j < oldCap; ++j) {
                //取出当前的节点 e
                Node<K,V> e;
                //如果当前桶中有元素,则将链表赋值给e
                if ((e = oldTab[j]) != null) {
                    //将原哈希桶置空以便GC
                    oldTab[j] = null;
                    //如果当前链表中就一个元素，（没有发生哈希碰撞）
                    if (e.next == null)
                        //直接将这个元素放置在新的哈希桶里。
                        //注意这里取下标 是用 哈希值 与 桶的长度-1 。 由于桶的长度是2的n次方，这么做其实是等于 一个模运算。但是效率更高
                        newTab[e.hash &amp; (newCap - 1)] = e;
                        //如果发生过哈希碰撞 ,而且是节点数超过8个，转化成了红黑树（暂且不谈 避免过于复杂， 后续专门研究一下红黑树）
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    //如果发生过哈希碰撞，节点数小于8个。则要根据链表上每个节点的哈希值，依次放入新哈希桶对应下标位置。
                    else { // preserve order
                        //因为扩容是容量翻倍，所以原链表上的每个节点，现在可能存放在原来的下标，即low位， 或者扩容后的下标，即high位。 high位=  low位+原哈希桶容量
                        //低位链表的头结点、尾节点
                        Node<K,V> loHead = null, loTail = null;
                        //高位链表的头节点、尾节点
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;//临时节点 存放e的下一个节点
                        do {
                            next = e.next;
                            //这里又是一个利用位运算 代替常规运算的高效点： 利用哈希值 与 旧的容量，可以得到哈希值去模后，是大于等于oldCap还是小于oldCap，等于0代表小于oldCap，应该存放在低位，否则存放在高位
                            if ((e.hash &amp; oldCap) == 0) {
                                //给头尾节点指针赋值
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }//高位也是相同的逻辑
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }//循环直到链表结束
                        } while ((e = next) != null);
                        //将低位链表存放在原index处，
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        //将高位链表存放在新index处
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

```

再看一下 往哈希表里插入一个节点的`putVal`函数,如果参数`onlyIfAbsent`是true，那么不会覆盖相同key的值value。如果`evict`是false。那么表示是在初始化时调用的

```java
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        //tab存放 当前的哈希桶， p用作临时链表节点  
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        //如果当前哈希表是空的，代表是初始化
        if ((tab = table) == null || (n = tab.length) == 0)
            //那么直接去扩容哈希表，并且将扩容后的哈希桶长度赋值给n
            n = (tab = resize()).length;
        //如果当前index的节点是空的，表示没有发生哈希碰撞。 直接构建一个新节点Node，挂载在index处即可。
        //这里再啰嗦一下，index 是利用 哈希值 &amp; 哈希桶的长度-1，替代模运算
        if ((p = tab[i = (n - 1) &amp; hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {//否则 发生了哈希冲突。
            //e
            Node<K,V> e; K k;
            //如果哈希值相等，key也相等，则是覆盖value操作
            if (p.hash == hash &amp;&amp;
                ((k = p.key) == key || (key != null &amp;&amp; key.equals(k))))
                e = p;//将当前节点引用赋值给e
            else if (p instanceof TreeNode)//红黑树暂且不谈
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {//不是覆盖操作，则插入一个普通链表节点
                //遍历链表
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {//遍历到尾部，追加新节点到尾部
                        p.next = newNode(hash, key, value, null);
                        //如果追加节点后，链表数量》=8，则转化为红黑树
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    //如果找到了要覆盖的节点
                    if (e.hash == hash &amp;&amp;
                        ((k = e.key) == key || (key != null &amp;&amp; key.equals(k))))
                        break;
                    p = e;
                }
            }
            //如果e不是null，说明有需要覆盖的节点，
            if (e != null) { // existing mapping for key
                //则覆盖节点值，并返回原oldValue
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                //这是一个空实现的函数，用作LinkedHashMap重写使用。
                afterNodeAccess(e);
                return oldValue;
            }
        }
        //如果执行到了这里，说明插入了一个新的节点，所以会修改modCount，以及返回null。
        
        //修改modCount
        ++modCount;
        //更新size，并判断是否需要扩容。
        if (++size > threshold)
            resize();
        //这是一个空实现的函数，用作LinkedHashMap重写使用。
        afterNodeInsertion(evict);
        return null;
    }

```

`newNode`如下：构建一个链表节点

```java
    // Create a regular (non-tree) node
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<>(hash, key, value, next);
    }

```

```java
    // Callbacks to allow LinkedHashMap post-actions
    void afterNodeAccess(Node<K,V> p) { }
    void afterNodeInsertion(boolean evict) { }

```

小结：

- 运算尽量都用**位运算**代替**，更高效**。
<li>对于**扩容**导致需要新建数组存放更多元素时，除了要将老数组中的元素迁移过来，也记得将**老数组中的引用置null**，以便**GC**
</li>
<li>取下标 是用 **哈希值 与运算 （桶的长度-1）**  `i = (n - 1) &amp; hash`。 由于桶的长度是2的n次方，这么做其实是等于 一个**模运算**。但是**效率更高**
</li>
- 扩容时，如果发生过哈希碰撞，节点数小于8个。则要根据链表上每个节点的哈希值，依次放入新哈希桶对应下标位置。
- 因为扩容是容量翻倍，所以原链表上的每个节点，现在可能存放在原来的下标，即low位， 或者扩容后的下标，即high位。 high位=  low位+原哈希桶容量
- 利用**哈希值 与运算 旧的容量** ，`if ((e.hash &amp; oldCap) == 0)`,可以得到哈希值去模后，是大于等于oldCap还是小于oldCap，等于0代表小于oldCap，**应该存放在低位，否则存放在高位**。这里又是一个利用位运算 代替常规运算的高效点
- 如果追加节点后，链表数量》=8，则转化为红黑树
- 插入节点操作时，有一些空实现的函数，用作LinkedHashMap重写使用。

## 5 增、改

### 1往表中插入或覆盖一个key-value

```java
    public V put(K key, V value) {
        //先根据key，取得hash值。 再调用上一节的方法插入节点
        return putVal(hash(key), key, value, false, true);
    }

```

这个根据key取hash值的函数也要关注一下，它称之为“扰动函数”，关于这个函数的用处 开头已经总结过了：

```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

```

而key的hash值，并不仅仅只是key对象的`hashCode()`方法的返回值，还会经过**扰动函数**的扰动，以使hash值更加均衡。<br>
因为`hashCode()`是`int`类型，取值范围是40多亿，只要哈希函数映射的比较均匀松散，碰撞几率是很小的。<br>
但就算原本的`hashCode()`取得很好，每个key的`hashCode()`不同，但是由于`HashMap`的哈希桶的长度远比hash取值范围小，默认是16，所以当对hash值以桶的长度取余，以找到存放该key的桶的下标时，由于取余是通过与操作完成的，会忽略hash值的高位。因此只有`hashCode()`的低位参加运算，发生不同的hash值，但是得到的index相同的情况的几率会大大增加，这种情况称之为**hash碰撞。** 即，碰撞率会增大。

**扰动函数**就是为了解决hash碰撞的。它会综合hash值高位和低位的特征，并存放在低位，因此在与运算时，相当于高低位一起参与了运算，以减少hash碰撞的概率。（在JDK8之前，扰动函数会扰动四次，JDK8简化了这个操作）

### 2往表中批量增加数据

```java
    public void putAll(Map<? extends K, ? extends V> m) {
        //这个函数上一节也已经分析过。//将另一个Map的所有元素加入表中，参数evict初始化时为false，其他情况为true
        putMapEntries(m, true);
    }

```

### 3 只会往表中插入 key-value, 若key对应的value之前存在，不会覆盖。（jdk8增加的方法）

```java
    @Override
    public V putIfAbsent(K key, V value) {
        return putVal(hash(key), key, value, true, true);
    }

```

## 6 删

### 以key为条件删除

如果key对应的value存在，则删除这个键值对。 并返回value。如果不存在 返回null。

```java
    public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
    }

```

//从哈希表中删除某个节点， 如果参数`matchValue`是true，则必须key 、value都相等才删除。<br>
//如果`movable`参数是false，在删除节点时，不移动其他节点

```java
    final Node<K,V> removeNode(int hash, Object key, Object value,
                               boolean matchValue, boolean movable) {
        // p 是待删除节点的前置节点
        Node<K,V>[] tab; Node<K,V> p; int n, index;
        //如果哈希表不为空，则根据hash值算出的index下 有节点的话。
        if ((tab = table) != null &amp;&amp; (n = tab.length) > 0 &amp;&amp;
            (p = tab[index = (n - 1) &amp; hash]) != null) {
            //node是待删除节点
            Node<K,V> node = null, e; K k; V v;
            //如果链表头的就是需要删除的节点
            if (p.hash == hash &amp;&amp;
                ((k = p.key) == key || (key != null &amp;&amp; key.equals(k))))
                node = p;//将待删除节点引用赋给node
            else if ((e = p.next) != null) {//否则循环遍历 找到待删除节点，赋值给node
                if (p instanceof TreeNode)
                    node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
                else {
                    do {
                        if (e.hash == hash &amp;&amp;
                            ((k = e.key) == key ||
                             (key != null &amp;&amp; key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            //如果有待删除节点node，  且 matchValue为false，或者值也相等
            if (node != null &amp;&amp; (!matchValue || (v = node.value) == value ||
                                 (value != null &amp;&amp; value.equals(v)))) {
                if (node instanceof TreeNode)
                    ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
                else if (node == p)//如果node ==  p，说明是链表头是待删除节点
                    tab[index] = node.next;
                else//否则待删除节点在表中间
                    p.next = node.next;
                ++modCount;//修改modCount
                --size;//修改size
                afterNodeRemoval(node);//LinkedHashMap回调函数
                return node;
            }
        }
        return null;
    }

```

```java
    void afterNodeRemoval(Node<K,V> p) { }

```

### 以key value 为条件删除

```java
    @Override
    public boolean remove(Object key, Object value) {
        //这里传入了value 同时matchValue为true
        return removeNode(hash(key), key, value, true, true) != null;
    }

```

## 7 查

### 以key为条件，找到返回value。没找到返回null

```java
    public V get(Object key) {
        Node<K,V> e;
        //传入扰动后的哈希值 和 key 找到目标节点Node
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

```

```java
    //传入扰动后的哈希值 和 key 找到目标节点Node
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        //查找过程和删除基本差不多， 找到返回节点，否则返回null
        if ((tab = table) != null &amp;&amp; (n = tab.length) > 0 &amp;&amp;
            (first = tab[(n - 1) &amp; hash]) != null) {
            if (first.hash == hash &amp;&amp; // always check first node
                ((k = first.key) == key || (key != null &amp;&amp; key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &amp;&amp;
                        ((k = e.key) == key || (key != null &amp;&amp; key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

```

### 判断是否包含该key

```java
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

```

### 判断是否包含value

```java
    public boolean containsValue(Object value) {
        Node<K,V>[] tab; V v;
        //遍历哈希桶上的每一个链表
        if ((tab = table) != null &amp;&amp; size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    //如果找到value一致的返回true
                    if ((v = e.value) == value ||
                        (value != null &amp;&amp; value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }

```

### java8新增，带默认值的get方法

以key为条件，找到了返回value。否则返回defaultValue

```java
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? defaultValue : e.value;
    }


```

### 遍历

```java
    //缓存 entrySet
    transient Set<Map.Entry<K,V>> entrySet;
     */
    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
    }

```

```java
    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        //一般我们用到EntrySet，都是为了获取iterator
        public final Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        //最终还是调用getNode方法
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            Object key = e.getKey();
            Node<K,V> candidate = getNode(hash(key), key);
            return candidate != null &amp;&amp; candidate.equals(e);
        }
        //最终还是调用removeNode方法
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                Object key = e.getKey();
                Object value = e.getValue();
                return removeNode(hash(key), key, value, true, true) != null;
            }
            return false;
        }
        //。。。
    }

```

//EntryIterator的实现：

```java
    final class EntryIterator extends HashIterator
        implements Iterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextNode(); }
    }

```

```java
    abstract class HashIterator {
        Node<K,V> next;        // next entry to return
        Node<K,V> current;     // current entry
        int expectedModCount;  // for fast-fail
        int index;             // current slot

        HashIterator() {
            //因为hashmap也是线程不安全的，所以要保存modCount。用于fail-fast策略
            expectedModCount = modCount;
            Node<K,V>[] t = table;
            current = next = null;
            index = 0;
            //next 初始时，指向 哈希桶上第一个不为null的链表头
            if (t != null &amp;&amp; size > 0) { // advance to first entry
                do {} while (index < t.length &amp;&amp; (next = t[index++]) == null);
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        //由这个方法可以看出，遍历HashMap时，顺序是按照哈希桶从低到高，链表从前往后，依次遍历的。属于无序集合。
        final Node<K,V> nextNode() {
            Node<K,V>[] t;
            Node<K,V> e = next;
            //fail-fast策略
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (e == null)
                throw new NoSuchElementException();
            //依次取链表下一个节点，
            if ((next = (current = e).next) == null &amp;&amp; (t = table) != null) {
                //如果当前链表节点遍历完了，则取哈希桶下一个不为null的链表头
                do {} while (index < t.length &amp;&amp; (next = t[index++]) == null);
            }
            return e;
        }

        public final void remove() {
            Node<K,V> p = current;
            if (p == null)
                throw new IllegalStateException();
            ////fail-fast策略
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            current = null;
            K key = p.key;
            //最终还是利用removeNode 删除节点
            removeNode(hash(key), key, null, false, false);
            expectedModCount = modCount;
        }
    }

```

## 8 总结

HashMap特点和精髓可以参看本文第二章【概要】 和第四章的【小结】部分。

后续会另开新篇聊一聊红黑树。

20170920 add,从网上转了一张图，据说来自美团，侵删：

## 9 与`HashTable`的区别

- 与之相比`HashTable`是线程安全的，且不允许key、value是null。
<li>
`HashTable`默认容量是11。</li>
<li>
`HashTable`是直接使用key的hashCode(`key.hashCode()`)作为hash值，不像`HashMap`内部使用`static final int hash(Object key)`扰动函数对key的hashCode进行扰动后作为hash值。</li>
<li>
`HashTable`取哈希桶下标是直接用模运算%.（因为其默认容量也不是2的n次方。所以也无法用位运算替代模运算）</li>
<li>扩容时，新容量是原来的2倍+1。`int newCapacity = (oldCapacity << 1) + 1;`
</li>
<li>
`Hashtable`是`Dictionary`的子类同时也实现了`Map`接口，`HashMap`是`Map`接口的一个实现类；</li>

