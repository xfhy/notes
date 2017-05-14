# AIDL简单使用

## 1. 在Android Studio下的简单配置

1. 需要在src/main下新建一个aidl文件夹
2. 然后把aidl文件放在这下面即可(如果有包名,则还需要在里面新建package).

## 2. AIDL用来做什么

AIDL是Android中IPC（Inter-Process Communication）方式中的一种，AIDL是Android Interface definition language的缩写，对于小白来说，AIDL的作用是让你可以在自己的APP里绑定一个其他APP的service，这样你的APP可以和其他APP交互。

## 3. 最后

为什么APP间的进程交互这么麻烦，是因为它们属于不同的进程，之间的交互涉及到进程间的通讯。
而AIDL只是Android中众多进程间通讯方式中的一种方式