> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/xiao__gui/article/details/8188833 <link rel="stylesheet" href="https://csdnimg.cn/release/phoenix/template/css/ck_htmledit_views-f1a9c33fcd.css">

在 Java 中，synchronized 关键字是用来控制线程同步的，就是在多线程的环境下，控制 synchronized 代码段不被多个线程同时执行。synchronized 既可以加在一段代码上，也可以加在方法上。

关键是，不要认为给方法或者代码段加上 synchronized 就万事大吉，看下面一段代码：

```
class Sync { 	public synchronized void test() {		System.out.println("test开始..");		try {			Thread.sleep(1000);		} catch (InterruptedException e) {			e.printStackTrace();		}		System.out.println("test结束..");	}} class MyThread extends Thread { 	public void run() {		Sync sync = new Sync();		sync.test();	}} public class Main { 	public static void main(String[] args) {		for (int i = 0; i < 3; i++) {			Thread thread = new MyThread();			thread.start();		}	}}
```

运行结果：
_test 开始..
test 开始..
test 开始..
test 结束..
test 结束..
test 结束.._

可以看出来，上面的程序起了三个线程，同时运行 Sync 类中的 test() 方法，虽然 test() 方法加上了 synchronized，但是还是同时运行起来，貌似 synchronized 没起作用。 

将 test() 方法上的 synchronized 去掉，在方法内部加上 synchronized(this)：

```
public void test() {	synchronized(this){		System.out.println("test开始..");		try {			Thread.sleep(1000);		} catch (InterruptedException e) {			e.printStackTrace();		}		System.out.println("test结束..");	}}
```

运行结果：
_test 开始..
test 开始..
test 开始..
test 结束..
test 结束..
test 结束.._

一切还是这么平静，没有看到 synchronized 起到作用。 

实际上，synchronized(this) 以及非 static 的 synchronized 方法（至于 static synchronized 方法请往下看），只能防止多个线程同时执行同一个对象的同步代码段。

回到本文的题目上：synchronized 锁住的是代码还是对象。答案是：synchronized 锁住的是括号里的对象，而不是代码。对于非 static 的 synchronized 方法，锁的就是对象本身也就是 this。

当 synchronized 锁住一个对象后，别的线程如果也想拿到这个对象的锁，就必须等待这个线程执行完成释放锁，才能再次给对象加锁，这样才达到线程同步的目的。即使两个不同的代码段，都要锁同一个对象，那么这两个代码段也不能在多线程环境下同时运行。

所以我们在用 synchronized 关键字的时候，能缩小代码段的范围就尽量缩小，能在代码段上加同步就不要再整个方法上加同步。这叫减小锁的粒度，使代码更大程度的并发。原因是基于以上的思想，锁的代码段太长了，别的线程是不是要等很久，等的花儿都谢了。当然这段是题外话，与本文核心思想并无太大关联。

再看上面的代码，每个线程中都 new 了一个 Sync 类的对象，也就是产生了三个 Sync 对象，由于不是同一个对象，所以可以多线程同时运行 synchronized 方法或代码段。

为了验证上述的观点，修改一下代码，让三个线程使用同一个 Sync 的对象。

```
class MyThread extends Thread { 	private Sync sync; 	public MyThread(Sync sync) {		this.sync = sync;	} 	public void run() {		sync.test();	}} public class Main { 	public static void main(String[] args) {		Sync sync = new Sync();		for (int i = 0; i < 3; i++) {			Thread thread = new MyThread(sync);			thread.start();		}	}}
```

运行结果：
_test 开始..
test 结束..
test 开始..
test 结束..
test 开始..
test 结束.._

可以看到，此时的 synchronized 就起了作用。 

那么，如果真的想锁住这段代码，要怎么做？也就是，如果还是最开始的那段代码，每个线程 new 一个 Sync 对象，怎么才能让 test 方法不会被多线程执行。 

解决也很简单，只要锁住同一个对象不就行了。例如，synchronized 后的括号中锁同一个固定对象，这样就行了。这样是没问题，但是，比较多的做法是让 synchronized 锁这个类对应的 Class 对象。

```
class Sync { 	public void test() {		synchronized (Sync.class) {			System.out.println("test开始..");			try {				Thread.sleep(1000);			} catch (InterruptedException e) {				e.printStackTrace();			}			System.out.println("test结束..");		}	}} class MyThread extends Thread { 	public void run() {		Sync sync = new Sync();		sync.test();	}} public class Main { 	public static void main(String[] args) {		for (int i = 0; i < 3; i++) {			Thread thread = new MyThread();			thread.start();		}	}}
```

运行结果：
_test 开始..
test 结束..
test 开始..
test 结束..
test 开始..
test 结束.._

上面代码用 synchronized(Sync.class) 实现了全局锁的效果。

最后说说 static synchronized 方法，static 方法可以直接类名加方法名调用，方法中无法使用 this，所以它锁的不是 this，而是类的 Class 对象，所以，static synchronized 方法也相当于全局锁，相当于锁住了代码段。

作者：叉叉哥   转载请注明出处：[http://blog.csdn.net/xiao__gui/article/details/8188833](http://blog.csdn.net/xiao__gui/article/details/8188833)