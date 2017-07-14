# Handler 总结

## Handler

- 发送消息，它能把消息发送给Looper管理的MessageQueue。

- 处理消息，并负责处理Looper分给它的消息。

- Handler的构造方法，会首先得到当前线程中保存的Looper实例，进而与Looper实例中的MessageQueue想关联。　

- Handler的sendMessage方法，会给msg的target赋值为handler自身，然后加入MessageQueue中。　　

## Looper

- 每个线程只有一个Looper，它负责管理对应的MessageQueue，会不断地从MessageQueue取出消息，并将消息分给对应的Hanlder处理。 　

- 主线程中，系统已经初始化了一个Looper对象，因此可以直接创建Handler即可，就可以通过Handler来发送消息、处理消息。 程序自己启动的子线程，程序必须自己创建一个Looper对象，并启动它，调用Looper.prepare()方法。

- prapare()方法：保证每个线程最多只有一个Looper对象。 　

- looper()方法：启动Looper，使用一个死循环不断取出MessageQueue中的消息，并将取出的消息分给对应的Handler进行处理。 　

## MessageQueue

- 由Looper负责管理，它采用先进先出的方式来管理Message。　

## Message

- Handler接收和处理的消息对象。

