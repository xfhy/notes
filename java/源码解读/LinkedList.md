# LinkedList源码欣赏

## 一、概述
> LinkedList，相对于ArrayList，大家可能平时使用LinkedList要少一些，其实有时候使用LinkedList比ArrayList效率高很多，当然，这得视情况而定。

本文将带大家深入LinkedList源码，分析其背后的实现原理，以便以后在合适的情况下进行使用。

之前我所知道的LinkedList的知识：

- LinkedList底层是链表结构
- 插入和删除比较快（O(1)），查询则相对慢一些（O(n)）
- 因为是链表结构，所以分配的空间不要求是连续的

## 二、链表
> 因为LinkedList源码中很多地方是进行链表操作,所以先带大家复习一下链表的基础知识.以前用C语言实现的链表,大家可以去看一下,地址:https://github.com/xfhy/dataStructure

### 1. 单链表

![](http://olg7c0d2n.bkt.clouddn.com/18-5-8/63677937.jpg)

一个节点中包含数据和下一个节点的指针(注意,是下一个节点的指针,而不是下一个节点数据的指针),尾节点没有下一个节点,所以指向null.访问某个节点只能从头节点开始查找,然后依次往后遍历.

### 2. 单向循环链表

![](http://olg7c0d2n.bkt.clouddn.com/18-5-8/47063362.jpg)

单向循环链表比单链表多了一个尾节点的指针指向的是头结点.

### 3. 双向链表

![](http://olg7c0d2n.bkt.clouddn.com/18-5-8/73677769.jpg)

双向链表的每个节点包含以下数据:上一个节点的指针,自己的数据,下一个节点的指针.尾节点没有下一个节点,所以指向null.这样的结构,比如我拿到链表中间的一个节点,即可以往前遍历,也可以往后遍历.

### 4. 双向循环链表

![](http://olg7c0d2n.bkt.clouddn.com/18-5-8/5983848.jpg)

双向循环链表的尾节点的下一个节点是头结点,头节点的上一个节点是尾节点.

## 三、LinkedList的继承关系

![](http://olg7c0d2n.bkt.clouddn.com/18-5-8/60559373.jpg)

源码中的定义:
```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
```
- AbstractSequentialList这个类提供了List的一个骨架实现接口，以尽量减少实现此接口所需的工作量由“顺序访问”数据存储（如链接列表）支持。对于随机访问数据（如数组），应使用AbstractList优先于此类。

- 实现了List接口,意味着LinkedList元素是有序的,可以重复的,可以有null元素的集合.

- Deque是Queue的子接口,Queue是一种队列形式,而Deque是双向队列,它支持从两个端点方向检索和插入元素.

- 实现了Cloneable接口,标识着可以它可以被复制.注意,ArrayList里面的clone()复制其实是浅复制(不知道此概念的赶快去查资料,这知识点非常重要).

- 实现了Serializable 标识着集合可被序列化。

## 四、看LinkedList源码前的准备

### 1. 节点定义

```java
private static class Node<E> {
    E item;  //该节点的数据
    Node<E> next; //指向下一个节点的指针
    Node<E> prev; //指向上一个节点的指针

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

Node是LinkedList的静态内部类.

为什么是静态内部类?我觉得可能原因如下:普通内部类会有外部类的强引用,而静态内部类就没有.有外部类的强引用的话,很容易造成内存泄漏,写成静态内部类可以避免这种情况的发生.

### 2. 成员变量

看构造方法之前先看看几个属性:

```java
//链表长度
transient int size = 0;
/**
* 头结点
*/
transient Node<E> first;

/**
* 尾节点
*/
transient Node<E> last;
```

这里为什么要存在一个成员变量尾节点?我感觉是为了方便,比如查找相应索引处元素+插入元素到最后.查找相应索引处元素时,先判断索引是在前半段还是在后半段,如果是在后半段,那么直接从尾节点出发,从后往前进行查找,这样速度更快.在插入元素到最后时,可以直接通过尾节点方便的进行插入.

### 3. 构造方法
下面是构造方法源码:

```java
/**
* 构造一个空列表
*/
public LinkedList() {
}

/**
* 构造列表通过指定的集合
*/
public LinkedList(Collection<? extends E> c) {
    this();
    addAll(c);
}
```

两个构造方法都比较简单,就是构造一个列表,其中的addAll()方法待会儿放到后面分析.

**思考:为什么LinkedList没有提供public LinkedList(int initialCapacity)这种构建指定大小列表的构造方式?**

因为ArrayList有这种构造方法`public ArrayList(int initialCapacity)`,ArrayList提供这种构造方法的好处在于在知道需要多大的空间的情况下,可以按需构造列表,无需浪费多余的空间和不必要的生成新数组的操作.而LinkedList可以很轻松动态的增加元素(O(1)),所以没必要一开始就构造一个有很多元素的列表,到时需要的时候再按需加上去就行了.

## 五、添加元素

### 1. add(E e)

方法作用:将e添加到链表末尾,返回是否添加成功

```java
/**
* 添加指定元素到链表尾部
*/
public boolean add(E e) {
    linkLast(e);
    return true;
}
/**
* Links e as last element.将e添加到尾部
*/
void linkLast(E e) {
    //1. 暂记尾节点
    final Node<E> l = last;
    //2. 构建节点 前一个节点是之前的尾节点
    final Node<E> newNode = new Node<>(l, e, null);
    //3. 新建的节点是尾节点了
    last = newNode;
    //4. 判断之前链表是否为空  
    //为空则将新节点赋给头结点(相当于空链表插入第一个元素,头结点等于尾节点)
    //非空则将之前的尾节点指向新节点
    if (l == null)
        first = newNode;
    else
        l.next = newNode;
    //5. 链表长度增加
    size++;
    modCount++;
}
```

大体思路:

1. 构建一个新的节点
2. 将该新节点作为新的尾节点.如果是空链表插入第一个元素,那么头结点=尾节点=新节点;如果不是,那么将之前的尾节点指向新节点.
3. 增加链表长度

**小细节**
`boolean add(E e)`添加成功返回true,添加失败返回false.我们在代码中没有看到有返回false的情况啊,直接在代码中写了个返回true,什么判断条件都没有,啊??

![](http://olg7c0d2n.bkt.clouddn.com/18-4-20/58741120.jpg)

仔细想想,分配内存空间不是必须是连续的,所以只要是还能给它分配空间,就不会添加失败.当空间不够分配时(内存溢出),会抛出OutOfMemory.


### 2. addLast(E e)

方法作用:添加元素到末尾.  内部实现和`add(E e)`一样.

```java
public void addLast(E e) {
    linkLast(e);
}
```

### 3. addFirst(E e) 

方法作用:添加元素到链表头部

```java
public void addFirst(E e) {
    linkFirst(e);
}
/**
* 添加元素到链表头部
*/
private void linkFirst(E e) {
    //1. 记录头结点
    final Node<E> f = first;
    //2. 创建新节点  next指针指向之前的头结点
    final Node<E> newNode = new Node<>(null, e, f);
    //3. 新建的节点就是头节点了
    first = newNode;
    //4. 判断之前链表是否为空  
    //为空则将新节点赋给尾节点(相当于空链表插入第一个元素,头结点等于尾节点)
    //非空则将之前的头结点的prev指针指向新节点
    if (f == null)
        last = newNode;
    else
        f.prev = newNode;
    //5. 链表长度增加
    size++;
    modCount++;
}
```

大体思路:

1. 构建一个新的节点
2. 将该新节点作为新的头节点.如果是空链表插入第一个元素,那么头结点=尾节点=新节点;如果不是,那么将之前的头节点的prev指针指向新节点.
3. 增加链表长度

### 4. push(E e)

方法作用:添加元素到链表头部   这里的意思比拟压栈.和pop(出栈:移除链表第一个元素)相反.

内部实现是和`addFirst()`一样的.

```java
public void push(E e) {
    addFirst(e);
}
```

### 5. offer(),offerFirst(E e),offerLast(E e)

方法作用:添加元素到链表头部.  内部实现其实就是`add(e)`

```java
public boolean offer(E e) {
    return add(e);
}
public boolean offerFirst(E e) {
    addFirst(e);
    return true;
}

/**
* 添加元素到末尾
*/
public boolean offerLast(E e) {
    addLast(e);
    return true;
}
```

### 6. add(int index, E element)

方法作用:添加元素到指定位置,可能会抛出`IndexOutOfBoundsException`

```java
//添加元素到指定位置
public void add(int index, E element) {
    //1. 越界检查
    checkPositionIndex(index);

    //2. 判断一下index大小
    //如果是和list大小一样,那么就插入到最后
    //否则插入到index处
    if (index == size)
        linkLast(element);
    else
        linkBefore(element, node(index));
}

//检查是否越界
private void checkPositionIndex(int index) {
    if (!isPositionIndex(index))
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}

/**
* Returns the (non-null) Node at the specified element index.
返回指定元素索引处的（非空）节点。
*/
Node<E> node(int index) {
    // assert isElementIndex(index);

    /**
    * 这里的思想非常巧妙,如果index在链表的前半部分,那么从first开始往后查找
    否则,从last往前面查找
    */
    //1. 如果index<size/2 ,即index在链表的前半部分
    if (index < (size >> 1)) {
        //2. 记录下第一个节点
        Node<E> x = first;
        //3. 循环从第一个节点开始往后查,直到到达index处,返回index处的元素
        for (int i = 0; i < index; i++)
            x = x.next;
        return x;
    } else {
        //index在链表的后半部分
        //4. 记录下最后一个节点
        Node<E> x = last;
        //5. 循环从最后一个节点开始往前查,直到到达index处,返回index处的元素
        for (int i = size - 1; i > index; i--)
            x = x.prev;
        return x;
    }
}
/**
* Links e as last element.
将e链接到list最后一个元素
*/
void linkLast(E e) {
    //1. 记录最后一个元素l
    final Node<E> l = last;
    //2. 构建一个新节点,数据为e,前一个是l,后一个是null
    final Node<E> newNode = new Node<>(l, e, null);
    //3. 现在新节点是最后一个元素了,所以需要记录下来
    last = newNode;
    //4. 如果之前list为空,那么first=last=newNode,只有一个元素
    if (l == null)
        first = newNode;
    else
        //5. 非空的话,那么将之前的最后一个指向新的节点
        l.next = newNode;
    //6. 链表长度+1
    size++;
    modCount++;
}

/**
* Inserts element e before non-null Node succ.
在非null节点succ之前插入元素e。
*/
void linkBefore(E e, Node<E> succ) {
    // assert succ != null;
    //1. 记录succ的前一个节点
    final Node<E> pred = succ.prev;
    //2. 构建一个新节点,数据是e,前一个节点是pred,下一个节点是succ
    final Node<E> newNode = new Node<>(pred, e, succ);
    //3. 将新节点作为succ的前一个节点
    succ.prev = newNode;
    //4. 判断pred是否为空
    //如果为空,那么说明succ是之前的头节点,现在新节点在succ的前面,所以新节点是头节点
    if (pred == null)
        first = newNode;
    else
        //5. succ的前一个节点不是空的话,那么直接将succ的前一个节点指向新节点就可以了
        pred.next = newNode;
    //6. 链表长度+1
    size++;
    modCount++;
}
```

大体思路:

1. 首先判断一下插入的位置是在链表的最后还是在链表中间.
2. 如果是插入到链表末尾,那么将之前的尾节点指向新节点
3. 如果是插入到链表中间
    1. 需要先找到链表中index索引处的节点.
    2. 将新节点赋值为index处节点的前一个节点
    3. 将index处节点的前一个节点的next指针赋值为新节点

> 哇,这里描述起来有点困难,,,,不知道我描述清楚没有.如果没看懂我的描述,看一下代码+再结合代码注释+画一下草图应该更清晰一些.

### 6. addAll(int index, Collection<? extends E> c)

方法作用:将指定集合的所有元素插入到index位置

```java
//将指定集合的所有元素插入到末尾位置
public boolean addAll(Collection<? extends E> c) {
    return addAll(size, c);
}

//将指定集合的所有元素插入到index位置
public boolean addAll(int index, Collection<? extends E> c) {
    //1. 入参合法性检查
    checkPositionIndex(index);

    //2. 将集合转成数组
    Object[] a = c.toArray();
    //3. 记录需要插入的集合元素个数
    int numNew = a.length;
    //4. 如果个数为0,那么插入失败,不继续执行了
    if (numNew == 0)
        return false;

    //5. 判断一下index与size是否相等
    //相等则插入到链表末尾
    //不相等则插入到链表中间  index处   
    Node<E> pred, succ;   
    if (index == size) {
        succ = null;
        pred = last;
    } else {
        //找到index索引处节点  这样就可以方便的拿到该节点的前后节点信息
        succ = node(index);
        //记录index索引处节点前一个节点
        pred = succ.prev;
    }

    //6. 循环将集合中所有元素连接到pred后面
    for (Object o : a) {
        @SuppressWarnings("unchecked") E e = (E) o;
        Node<E> newNode = new Node<>(pred, e, null);
        //如果前一个是空,那么将新节点作为头结点
        if (pred == null)
            first = newNode;
        else
            //指向新节点
            pred.next = newNode;
        pred = newNode;
    }

    //7. 判断succ是否为空
    //为空的话,那么集合的最后一个元素就是尾节点
    //非空的话,那么将succ连接到集合的最后一个元素后面
    if (succ == null) {
        last = pred;
    } else {
        pred.next = succ;
        succ.prev = pred;
    }

    //8. 链表长度+numNew
    size += numNew;
    modCount++;
    return true;
}
```

大体思路:

1. 将需要添加的集合转成数组a
2. 判断需要插入的位置index是否等于链表长度size,如果相等则插入到链表最后;如果不相等,则插入到链表中间,还需要找到index处节点succ,方便拿到该节点的前后节点信息.
3. 记录index索引处节点的前一个节点pred,循环将集合中所有元素连接到pred的后面
4. 将集合最后一个元素的next指针指向succ,将succ的prev指针指向集合的最后一个元素

## 六、删除元素

### 1. remove(),removeFirst()

方法作用: 移除链表第一个元素

```java
/**
* 移除链表第一个节点
*/
public E remove() {
    return removeFirst();
}

/**
* 移除链表第一个节点
*/
public E removeFirst() {
    final Node<E> f = first;
    //注意:如果之前是空链表,移除是要报错的哟
    if (f == null)
        throw new NoSuchElementException();
    return unlinkFirst(f);
}

/**
* Unlinks non-null first node f.
* 将第一个节点删掉
*/
private E unlinkFirst(Node<E> f) {
    // assert f == first && f != null;
    //1. 记录第一个节点的数据值
    final E element = f.item;
    //2. 记录下一个节点
    final Node<E> next = f.next;
    //3. 将第一个节点置空  帮助GC回收
    f.item = null;
    f.next = null; // help GC
    //4. 记录头节点
    first = next;
    //5. 如果下一个节点为空,那么链表无节点了    如果不为空,将头节点的prev指针置为空
    if (next == null)
        last = null;
    else
        next.prev = null;
    //6. 链表长度-1
    size--;
    modCount++;
    //7. 返回删除的节点的数据值
    return element;
}
```

大体思路:其实就是将第一个节点移除并置空,然后将第二个节点作为头节点.思路还是非常清晰的,主要是对细节的处理.

### 2. remove(int index)

方法作用:移除指定位置元素

```java
//移除指定位置元素
public E remove(int index) {
    //检查入参是否合法
    checkElementIndex(index);
    //node(index)找到index处的节点  
    return unlink(node(index));
}

//移除节点x
E unlink(Node<E> x) {
    // assert x != null;
    //1. 记录该节点数据值,前一个节点prev,后一个节点next
    final E element = x.item;
    final Node<E> next = x.next;
    final Node<E> prev = x.prev;

    //2. 判断前一个节点是否为空
    if (prev == null) {
        //为空的话,那么说明之前x节点是头节点  这时x的下一个节点成为头节点
        first = next;
    } else {
        //非空的话,将前一个节点的next指针指向x的下一个节点
        prev.next = next;
        //x的prev置为null
        x.prev = null;
    }

    //3. 判断x后一个节点是否为空
    if (next == null) {
        //为空的话,那么说明之前x节点是尾节点,这时x的前一个节点成为尾节点
        last = prev;
    } else {
        //为空的话,将x的下一个节点的prev指针指向prev(x的前一个节点)
        next.prev = prev;
        //x的next指针置空
        x.next = null;
    }

    //4. x节点数据值置空
    x.item = null;
    //5. 链表长度-1
    size--;
    modCount++;
    //6. 将x节点的数据值返回
    return element;
}
```

大体思路:
1. 首先找到index索引处的节点(这样就可以方便的获取该节点的前后节点),记为x
2. 记录x的前(prev)后(next)节点
3. 将x的前一个节点prev节点的next指针指向next,将x节点的后一个节点的prev指针指向prev节点.
4. 将x节点置空,链表长度-1

### 3. remove(Object o)

方法作用:从此链表中删除第一次出现的指定元素o

```java
public boolean remove(Object o) {
    //1. 判断o是否为空
    if (o == null) {
        //为null  循环,找第一个数据值为null的节点
        for (Node<E> x = first; x != null; x = x.next) {
            if (x.item == null) {
                //删除该节点
                unlink(x);
                return true;
            }
        }
    } else {
        //非空  循环,找第一个与o的数据值相等的节点
        for (Node<E> x = first; x != null; x = x.next) {
            if (o.equals(x.item)) {
                //删除该节点
                unlink(x);
                return true;
            }
        }
    }
    return false;
}
```

大体思路:

### 4. removeFirstOccurrence(Object o)

方法作用:

```java
```

大体思路:

### 5. removeLast()

方法作用:

```java
```

大体思路:

### 6. removeLastOccurrence(Object o)


方法作用:

```java
```

大体思路:

## 七、修改元素
## 八、查询元素
## 九、LinkedList的继承关系
## 十、LinkedList的继承关系
