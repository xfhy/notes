> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/f7d4819b7b24

在 java 中，线程间的通信可以使用 wait、notify、notifyAll 来进行控制。从名字就可以看出来这 3 个方法都是跟多线程相关的，但是可能让你感到吃惊的是：**这 3 个方法并不是 Thread 类或者是 Runnable 接口的方法，而是 Object 类的 3 个本地方法。**

其实要理解这一点也并不难，调用一个 Object 的 wait 与 notify/notifyAll 的时候，必须保证调用代码对该 Object 是同步的，也就是说必须在作用等同于 synchronized(obj){......} 的内部才能够去调用 obj 的 wait 与 notify/notifyAll 三个方法，否则就会报错：

```
  java.lang.IllegalMonitorStateException:current thread not owner

```

也就是说，**在调用这 3 个方法的时候，当前线程必须获得这个对象的锁**，那么这 3 个方法就是和对象锁相关的，所以是属于 Object 的方法而不是 Thread，因为不是每个对象都是 Thread。所以我们在理解 wait、notify、notifyAll 之前，先要了解以下对象锁。

多个线程都持有同一个对象的时候，如果都要进入 synchronized(obj){......} 的内部，就必须拿到这个对象的锁，synchronized 的机制保证了同一时间最多只能有 1 个线程拿到了对象的锁，如下图：

![](https://upload-images.jianshu.io/upload_images/151858-c1d566181a8034cc.png)

下面我们来看一下这 3 个方法的作用：
wait：线程自动释放其占有的对象锁，并等待 notify
notify：唤醒一个正在 wait 当前对象锁的线程，并让它拿到对象锁
notifyAll：唤醒所有正在 wait 前对象锁的线程

notify 和 notifyAll 的最主要的区别是：notify 只是唤醒一个正在 wait 当前对象锁的线程，而 notifyAll 唤醒所有。值得注意的是：notify 是本地方法，具体唤醒哪一个线程由虚拟机控制；notifyAll 后并不是所有的线程都能马上往下执行，它们只是跳出了 wait 状态，接下来它们还会是竞争对象锁。

下面通过一个常用生产者、消费者的例子来说明。
消息实体类：

```
package com.podongfeng;

/**
 * Title: Message.class<br>
 * Description: 消息实体<br>
 * Create DateTime: 2016年04月17日 下午1:27 <br>
 *
 * @author podongfeng
 */
public class Message {
}

```

生产者：

```
package com.podongfeng;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: Producer.class<br>
 * Description: 消息生产者<br>
 * Create DateTime: 2016年04月17日 下午1:28 <br>
 *
 * @author podongfeng
 */
public class Producer extends Thread {

    List<Message> msgList = new ArrayList<>();

    @Override public void run() {
        try {
            while (true) {
                Thread.sleep(3000);
                Message msg = new Message();
                synchronized(msgList) {
                    msgList.add(msg);
                    msgList.notify(); //这里只能是notify而不能是notifyAll，否则remove(0)会报java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message waitMsg() {
        synchronized(msgList) {
            if(msgList.size() == 0) {
                try {
                    msgList.wait();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return msgList.remove(0);
        }
    }
}

```

消费者：

```
package com.podongfeng;

/**
 * Title: Consumer.class<br>
 * Description: 消息消费者<br>
 * Create DateTime: 2016年04月17日 下午1:28 <br>
 *
 * @author podongfeng
 */
public class Consumer extends Thread {

    private Producer producer;

    public Consumer(String name, Producer producer) {
        super(name);
        this.producer = producer;
    }

    @Override public void run() {
        while (true) {
            Message msg = producer.waitMsg();
            System.out.println("Consumer " + getName() + " get a msg");
        }
    }

    public static void main(String[] args) {
        Producer p = new Producer();
        p.start();
        new Consumer("Consumer1", p).start();
        new Consumer("Consumer2", p).start();
        new Consumer("Consumer3", p).start();
    }
}

```

消费者线程调用 waitMsg 去获取一个消息实体，如果 msgList 为空，则线程进入 wait 状态；生产这线程每隔 3 秒钟生产出体格 msg 实体并放入 msgList 列表，完成后，调用 notify 唤醒一个消费者线程去消费。

最后再次提醒注意：
**wait、notify、notifyAll 并不是 Thread 类或者是 Runnable 接口的方法，而是 Object 类的 3 个本地方法。**
**在调用这 3 个方法的时候，当前线程必须获得这个对象的锁**