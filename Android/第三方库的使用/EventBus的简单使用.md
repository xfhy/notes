# EventBus的简单使用

> 由于项目中在用EventBus,感觉还挺方便的,赶快搞一波.之前听说RxJava和RxBus代替EventBus,所以一直没去学EventBus的用法.其实简单上手还是非常容易的.

- 概念：EventBus是一个Android端优化的publish/subscribe消息总线，简化了应用程序内各组件间、组件与后台线程间的通信。比如请求网络，等网络返回时通过Handler或Broadcast通知UI，两个Fragment之间需要通过Listener通信，这些需求都可以通过EventBus实现。
- EventBus比较适合仅仅当做组件间的通讯工具使用，主要用来传递消息。
- 使用EventBus可以避免搞出一大推的interface

## 1. 定义 events:
```java
public class LoginSeccessEvent {
    public String msg;
    public String username;
    public String password;
}
```
## 2. 注册并取消注册您的用户。 
例如在Android上，活动和片段通常应该根据其生命周期进行注册：
我在MainActivity中调用如下方法进行注册和取消注册,然后在onEventMainThread()中就会收到event(前提是有其他地方调用了EventBus.getDefault().post(loginSeccessEvent);发布事件),这个event可以携带一些信息,这样就非常方便.Activity不用setResult()和onActivityResult(),fragment和fragment传递数据也非常方便,避免写很多接口.
```java
 @Override
 public void onStart() {
     super.onStart();
     EventBus.getDefault().register(this);
 }

 @Override
 public void onStop() {
     super.onStop();
     EventBus.getDefault().unregister(this);
 }

准备订阅者：声明和注释你的订阅方法，可选地指定一个线程模式：
@Subscribe
public void onEventMainThread(LoginSeccessEvent loginSeccessEvent) {
    String msg = loginSeccessEvent.getMsg();
    String name = loginSeccessEvent.getUsername();
    String password = loginSeccessEvent.getPassword();
    mReceiver.setText(msg);
    Snackbar.make(mReceiver, name + password, Snackbar.LENGTH_LONG).show();
}

```

## 3. 发布 events:
我在LoginActivity(需要回传event的界面)把event发布出去,然后在MainActivity就会接收到
```java
LoginSeccessEvent loginSeccessEvent = new LoginSeccessEvent();
loginSeccessEvent.setMsg("登录成功");
loginSeccessEvent.setUsername(mEmailView.getText().toString());
loginSeccessEvent.setPassword(mPasswordView.getText().toString());
EventBus.getDefault().post(loginSeccessEvent);
```