
# LinkedHashMap源码解析（JDK8）

## 1 概述

在[上文](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/77413921)中，我们已经聊过了`HashMap`,本篇是基于[上文](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/77413921)的基础之上。所以如果没看过[上文](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/77413921)，请先阅读[面试必备：HashMap源码解析（JDK8）](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/77413921)<br>
本文将从几个常用方法下手，来阅读`LinkedHashMap`的源码。<br>
按照从构造方法-&gt;常用API（增、删、改、查）的顺序来阅读源码，并会讲解阅读方法中涉及的一些变量的意义。了解`LinkedHashMap`的特点、适用场景。

**如果本文中有不正确的结论、说法，请大家提出和我讨论，共同进步，谢谢。**

## 2 概要

概括的说，`LinkedHashMap` 是一个**关联数组、哈希表**，它是**线程不安全**的，允许**key为null**,**value为null**。<br>
它继承自`HashMap`，实现了`Map&lt;K,V&gt;`接口。其内部还维护了一个**双向链表**，在每次**插入数据，或者访问、修改数据**时，**会增加节点、或调整链表的节点顺序**。以决定迭代时输出的顺序。

默认情况，遍历时的顺序是**按照插入节点的顺序**。这也是其与`HashMap`最大的区别。<br>
也可以在构造时传入`accessOrder`参数，使得其遍历顺序**按照访问的顺序**输出。

因继承自`HashMap`,所以`HashMap`[上文](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/77413921)分析的特点，除了输出无序，其他`LinkedHashMap`都有，比如扩容的策略，哈希桶长度一定是2的N次方等等。<br>
`LinkedHashMap`在实现时，就是重写override了几个方法。以满足其输出序列有序的需求。

### 示例代码:

根据这段实例代码，先从现象看一下`LinkedHashMap`的特征：<br>
在每次**插入数据，或者访问、修改数据**时，**会增加节点、或调整链表的节点顺序**。以决定迭代时输出的顺序。

```
        Map&lt;String, String&gt; map = new LinkedHashMap&lt;&gt;();
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        map.put("4", "d");

        Iterator&lt;Map.Entry&lt;String, String&gt;&gt; iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println("以下是accessOrder=true的情况:");

        map = new LinkedHashMap&lt;String, String&gt;(10, 0.75f, true);
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        map.put("4", "d");
        map.get("2");//2移动到了内部的链表末尾
        map.get("4");//4调整至末尾
        map.put("3", "e");//3调整至末尾
        map.put(null, null);//插入两个新的节点 null
        map.put("5", null);//5
        iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

```

输出：

```
1=a
2=b
3=c
4=d
以下是accessOrder=true的情况:
1=a
2=b
4=d
3=e
null=null
5=null

```

## 3 节点

`LinkedHashMap`的节点`Entry&lt;K,V&gt;`继承自`HashMap.Node&lt;K,V&gt;`，在其基础上扩展了一下。改成了一个**双向链表**。

```
    static class Entry&lt;K,V&gt; extends HashMap.Node&lt;K,V&gt; {
        Entry&lt;K,V&gt; before, after;
        Entry(int hash, K key, V value, Node&lt;K,V&gt; next) {
            super(hash, key, value, next);
        }
    }

```

同时类里有两个成员变量`head tail`,分别指向内部双向链表的表头、表尾。

```
    //双向链表的头结点
    transient LinkedHashMap.Entry&lt;K,V&gt; head;

    //双向链表的尾节点
    transient LinkedHashMap.Entry&lt;K,V&gt; tail;

```

## 4 构造函数

```
    //默认是false，则迭代时输出的顺序是插入节点的顺序。若为true，则输出的顺序是按照访问节点的顺序。
    //为true时，可以在这基础之上构建一个LruCach
    final boolean accessOrder;
    
    public LinkedHashMap() {
        super();
        accessOrder = false;
    }
    //指定初始化时的容量，
    public LinkedHashMap(int initialCapacity) {
        super(initialCapacity);
        accessOrder = false;
    }
    //指定初始化时的容量，和扩容的加载因子
    public LinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }
    //指定初始化时的容量，和扩容的加载因子，以及迭代输出节点的顺序
    public LinkedHashMap(int initialCapacity,
                         float loadFactor,
                         boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }
    //利用另一个Map 来构建，
    public LinkedHashMap(Map&lt;? extends K, ? extends V&gt; m) {
        super();
        accessOrder = false;
        //该方法上文分析过，批量插入一个map中的所有数据到 本集合中。
        putMapEntries(m, false);
    }
    

```

小结：<br>
构造函数和`HashMap`相比，就是增加了一个`accessOrder`参数。用于控制迭代时的节点顺序。

## 5 增

`LinkedHashMap`并没有重写任何put方法。但是其重写了构建新节点的`newNode()`方法.<br>
`newNode()`会在`HashMap`的`putVal()`方法里被调用，`putVal()`方法会在批量插入数据`putMapEntries(Map&lt;? extends K, ? extends V&gt; m, boolean evict)`或者插入单个数据`public V put(K key, V value)`时被调用。

`LinkedHashMap`重写了`newNode()`,在每次**构建新节点**时，通过`linkNodeLast(p);`将**新节点链接在内部双向链表的尾部**。

```
    //在构建新节点时，构建的是`LinkedHashMap.Entry` 不再是`Node`.
    Node&lt;K,V&gt; newNode(int hash, K key, V value, Node&lt;K,V&gt; e) {
        LinkedHashMap.Entry&lt;K,V&gt; p =
            new LinkedHashMap.Entry&lt;K,V&gt;(hash, key, value, e);
        linkNodeLast(p);
        return p;
    }
    //将新增的节点，连接在链表的尾部
    private void linkNodeLast(LinkedHashMap.Entry&lt;K,V&gt; p) {
        LinkedHashMap.Entry&lt;K,V&gt; last = tail;
        tail = p;
        //集合之前是空的
        if (last == null)
            head = p;
        else {//将新节点连接在链表的尾部
            p.before = last;
            last.after = p;
        }
    }

```

以及`HashMap`专门预留给`LinkedHashMap`的`afterNodeAccess() afterNodeInsertion() afterNodeRemoval()` 方法。

```
    // Callbacks to allow LinkedHashMap post-actions
    void afterNodeAccess(Node&lt;K,V&gt; p) { }
    void afterNodeInsertion(boolean evict) { }
    void afterNodeRemoval(Node&lt;K,V&gt; p) { }

```

```
    //回调函数，新节点插入之后回调 ， 根据evict 和   判断是否需要删除最老插入的节点。如果实现LruCache会用到这个方法。
    void afterNodeInsertion(boolean evict) { // possibly remove eldest
        LinkedHashMap.Entry&lt;K,V&gt; first;
        //LinkedHashMap 默认返回false 则不删除节点
        if (evict &amp;&amp; (first = head) != null &amp;&amp; removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }
    }
    //LinkedHashMap 默认返回false 则不删除节点。 返回true 代表要删除最早的节点。通常构建一个LruCache会在达到Cache的上限是返回true
    protected boolean removeEldestEntry(Map.Entry&lt;K,V&gt; eldest) {
        return false;
    }

```

`void afterNodeInsertion(boolean evict)`以及`boolean removeEldestEntry(Map.Entry&lt;K,V&gt; eldest)`是构建LruCache需要的回调，在`LinkedHashMap`里可以忽略它们。

## 6 删

`LinkedHashMap`也没有重写`remove()`方法，因为它的删除逻辑和`HashMap`并无区别。<br>
但它重写了`afterNodeRemoval()`这个回调方法。该方法会在`Node&lt;K,V&gt; removeNode(int hash, Object key, Object value, boolean matchValue, boolean movable)`方法中回调，`removeNode()`会在所有涉及到删除节点的方法中被调用，[上文](https://link.jianshu.com?t=http://blog.csdn.net/zxt0601/article/details/77413921)分析过，是删除节点操作的真正执行者。

```
    //在删除节点e时，同步将e从双向链表上删除
    void afterNodeRemoval(Node&lt;K,V&gt; e) { // unlink
        LinkedHashMap.Entry&lt;K,V&gt; p =
            (LinkedHashMap.Entry&lt;K,V&gt;)e, b = p.before, a = p.after;
        //待删除节点 p 的前置后置节点都置空
        p.before = p.after = null;
        //如果前置节点是null，则现在的头结点应该是后置节点a
        if (b == null)
            head = a;
        else//否则将前置节点b的后置节点指向a
            b.after = a;
        //同理如果后置节点时null ，则尾节点应是b
        if (a == null)
            tail = b;
        else//否则更新后置节点a的前置节点为b
            a.before = b;
    }

```

## 7 查

`LinkedHashMap`重写了`get()和getOrDefault()`方法：

```
    public V get(Object key) {
        Node&lt;K,V&gt; e;
        if ((e = getNode(hash(key), key)) == null)
            return null;
        if (accessOrder)
            afterNodeAccess(e);
        return e.value;
    }
    public V getOrDefault(Object key, V defaultValue) {
       Node&lt;K,V&gt; e;
       if ((e = getNode(hash(key), key)) == null)
           return defaultValue;
       if (accessOrder)
           afterNodeAccess(e);
       return e.value;
   }

```

对比`HashMap`中的实现,`LinkedHashMap`只是增加了在成员变量(构造函数时赋值)`accessOrder`为true的情况下，要去回调`void afterNodeAccess(Node&lt;K,V&gt; e)`函数。

```
    public V get(Object key) {
        Node&lt;K,V&gt; e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

```

在`afterNodeAccess()`函数中，**会将当前被访问到的节点e，移动至内部的双向链表的尾部。**

```
    void afterNodeAccess(Node&lt;K,V&gt; e) { // move node to last
        LinkedHashMap.Entry&lt;K,V&gt; last;//原尾节点
        //如果accessOrder 是true ，且原尾节点不等于e
        if (accessOrder &amp;&amp; (last = tail) != e) {
            //节点e强转成双向链表节点p
            LinkedHashMap.Entry&lt;K,V&gt; p =
                (LinkedHashMap.Entry&lt;K,V&gt;)e, b = p.before, a = p.after;
            //p现在是尾节点， 后置节点一定是null
            p.after = null;
            //如果p的前置节点是null，则p以前是头结点，所以更新现在的头结点是p的后置节点a
            if (b == null)
                head = a;
            else//否则更新p的前直接点b的后置节点为 a
                b.after = a;
            //如果p的后置节点不是null，则更新后置节点a的前置节点为b
            if (a != null)
                a.before = b;
            else//如果原本p的后置节点是null，则p就是尾节点。 此时 更新last的引用为 p的前置节点b
                last = b;
            if (last == null) //原本尾节点是null  则，链表中就一个节点
                head = p;
            else {//否则 更新 当前节点p的前置节点为 原尾节点last， last的后置节点是p
                p.before = last;
                last.after = p;
            }
            //尾节点的引用赋值成p
            tail = p;
            //修改modCount。
            ++modCount;
        }
    }

```

值得注意的是，`afterNodeAccess()`函数中，会修改`modCount`,因此当你正在`accessOrder=true`的模式下,迭代`LinkedHashMap`时，如果同时查询访问数据，也会导致`fail-fast`，因为迭代的顺序已经改变。

### 7.2 containsValue

它重写了该方法，相比`HashMap`的实现，**更为高效**。

```
    public boolean containsValue(Object value) {
        //遍历一遍链表，去比较有没有value相等的节点，并返回
        for (LinkedHashMap.Entry&lt;K,V&gt; e = head; e != null; e = e.after) {
            V v = e.value;
            if (v == value || (value != null &amp;&amp; value.equals(v)))
                return true;
        }
        return false;
    }

```

对比`HashMap`，是用两个for循环遍历，相对低效。

```
    public boolean containsValue(Object value) {
        Node&lt;K,V&gt;[] tab; V v;
        if ((tab = table) != null &amp;&amp; size &gt; 0) {
            for (int i = 0; i &lt; tab.length; ++i) {
                for (Node&lt;K,V&gt; e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                        (value != null &amp;&amp; value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }

```

## 8 遍历

重写了`entrySet()`如下：

```
    public Set&lt;Map.Entry&lt;K,V&gt;&gt; entrySet() {
        Set&lt;Map.Entry&lt;K,V&gt;&gt; es;
        //返回LinkedEntrySet
        return (es = entrySet) == null ? (entrySet = new LinkedEntrySet()) : es;
    }
    final class LinkedEntrySet extends AbstractSet&lt;Map.Entry&lt;K,V&gt;&gt; {
        public final Iterator&lt;Map.Entry&lt;K,V&gt;&gt; iterator() {
            return new LinkedEntryIterator();
        }
    }

```

最终的EntryIterator:

```
    final class LinkedEntryIterator extends LinkedHashIterator
        implements Iterator&lt;Map.Entry&lt;K,V&gt;&gt; {
        public final Map.Entry&lt;K,V&gt; next() { return nextNode(); }
    }
    
    abstract class LinkedHashIterator {
        //下一个节点
        LinkedHashMap.Entry&lt;K,V&gt; next;
        //当前节点
        LinkedHashMap.Entry&lt;K,V&gt; current;
        int expectedModCount;

        LinkedHashIterator() {
            //初始化时，next 为 LinkedHashMap内部维护的双向链表的扁头
            next = head;
            //记录当前modCount，以满足fail-fast
            expectedModCount = modCount;
            //当前节点为null
            current = null;
        }
        //判断是否还有next
        public final boolean hasNext() {
            //就是判断next是否为null，默认next是head  表头
            return next != null;
        }
        //nextNode() 就是迭代器里的next()方法 。
        //该方法的实现可以看出，迭代LinkedHashMap，就是从内部维护的双链表的表头开始循环输出。
        final LinkedHashMap.Entry&lt;K,V&gt; nextNode() {
            //记录要返回的e。
            LinkedHashMap.Entry&lt;K,V&gt; e = next;
            //判断fail-fast
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            //如果要返回的节点是null，异常
            if (e == null)
                throw new NoSuchElementException();
            //更新当前节点为e
            current = e;
            //更新下一个节点是e的后置节点
            next = e.after;
            //返回e
            return e;
        }
        //删除方法 最终还是调用了HashMap的removeNode方法
        public final void remove() {
            Node&lt;K,V&gt; p = current;
            if (p == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            current = null;
            K key = p.key;
            removeNode(hash(key), key, null, false, false);
            expectedModCount = modCount;
        }
    }
    

```

值得注意的就是：`nextNode()` 就是迭代器里的`next()`方法 。<br>
该方法的实现可以看出，迭代`LinkedHashMap`，就是从**内部维护的双链表的表头开始循环输出**。<br>
而双链表节点的顺序在`LinkedHashMap`的**增、删、改、查时都会更新。以满足按照插入顺序输出，还是访问顺序输出。**

## 总结

`LinkedHashMap`相对于`HashMap`的源码比，是很简单的。因为大树底下好乘凉。它继承了`HashMap`，仅重写了几个方法，以**改变它迭代遍历时的顺序**。这也是其与`HashMap`相比最大的不同。<br>
在每次**插入数据，或者访问、修改数据**时，**会增加节点、或调整链表的节点顺序**。以决定迭代时输出的顺序。

<li>
`accessOrder` ,默认是false，则迭代时输出的顺序是**插入节点的顺序**。若为true，则输出的顺序是按照访问节点的顺序。为true时，可以在这基础之上构建一个`LruCache`.</li>
<li>
`LinkedHashMap`并没有重写任何put方法。但是其重写了构建新节点的`newNode()`方法.在每次构建新节点时，将**新节点链接在内部双向链表的尾部**
</li>
<li>
`accessOrder=true`的模式下,在`afterNodeAccess()`函数中，会将当前**被访问**到的节点e，**移动**至内部的双向链表**的尾部**。值得注意的是，`afterNodeAccess()`函数中，会修改`modCount`,因此当你正在`accessOrder=true`的模式下,迭代`LinkedHashMap`时，如果同时查询访问数据，也会导致`fail-fast`，因为迭代的顺序已经改变。</li>
<li>
`nextNode()` 就是迭代器里的`next()`方法 。<br>
该方法的实现可以看出，迭代`LinkedHashMap`，就是从**内部维护的双链表的表头开始循环输出**。<br>
而双链表节点的顺序在`LinkedHashMap`的**增、删、改、查时都会更新。以满足按照插入顺序输出，还是访问顺序输出。**
</li>
- 它与`HashMap`比，还有一个小小的优化，重写了`containsValue()`方法，直接遍历内部链表去比对value值是否相等。
<li>
</li>

那么，还有最后一个小问题？为什么它不重写`containsKey()`方法，也去循环比对内部链表的key是否相等呢？