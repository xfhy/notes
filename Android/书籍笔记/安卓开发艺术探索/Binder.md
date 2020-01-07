
#### Binder是什么

- Binder是一个类,实现了IBinder接口
- Binder是Android中的一种跨进程通信方式
- Binder是ServiceManager连接各种Manager(ActivityManager,WindowManager等等)和相应ManagerService的桥梁
- Binder是客户端和服务端进行通信的媒介

- 客户端请求时是会挂起客户端的

![image](F93652B5D72E475A96A69DB4F5A0C14F)