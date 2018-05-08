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

方法作用:

```java
```

大体思路:

### 6. addAll(Collection<? extends E> c)

方法作用:

```java
```

大体思路:

### 7. addAll(int index, Collection<? extends E> c)

方法作用:

```java
```

大体思路:


```
add()
push()
```

## 六、LinkedList的继承关系
## 七、LinkedList的继承关系
## 八、LinkedList的继承关系
## 九、LinkedList的继承关系
## 十、LinkedList的继承关系
