> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/d870f99b675c

Service 属于 android 四大组件之一，在很多地方经常被用到。开启 Service 有两种不同的方式：startService 和 bindService。不同的开启方式，Service 执行的生命周期方法也不同。
首先，先看一下 Service 都有哪些生命周期方法。
要想使用 Service 需要写一个自己的 MyService 类，并继承 Service。还要在清单文件中声明一下。

<pre><service android:/>

</pre>

<pre>public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("call", "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("call", "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("call", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("call", "onDestroy");
        super.onDestroy();
    }
}

</pre>

startService 开启服务和结束服务稍微简单一点。

<pre>//开启服务
Intent service = new Intent(this, MyService.class);
startService(service);

</pre>

<pre>//结束服务
stopService(service);

</pre>

开启服务时，调用一次 startService，生命周期执行的方法依次是：
onCreate() ==> onStartCommand();
调用多次 startService，onCreate 只有第一次会被执行，而 onStartCommand 会执行多次。
结束服务时，调用 stopService，生命周期执行 onDestroy 方法，并且多次调用 stopService 时，onDestroy 只有第一次会被执行。

bindService 开启服务就多了一些内容。

<pre>//开启服务
Intent service = new Intent(this, MyService.class);
MyConnection conn = new MyConnection();
//第一个参数：Intent意图
//第二个参数：是一个接口，通过这个接口接收服务开启或者停止的消息，并且这个参数不能为null
//第三个参数：开启服务时的操作，BIND_AUTO_CREATE代表自动创建service
bindService(service, conn, BIND_AUTO_CREATE);

</pre>

bindService 的方法参数需要一个 ServiceConnection 接口的实现类对象，我们自己写一个 MyConnection 类，并实现里面的方法。

<pre>private class MyConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //只有当我们自己写的MyService的onBind方法返回值不为null时，才会被调用
            Log.e("call","onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //这个方法只有出现异常时才会调用，服务器正常退出不会调用。
            Log.e("call","onServiceDisconnected");
        }
    }

</pre>

<pre>//结束服务
unbindService(conn);

</pre>

bingService 开启服务时，根据生命周期里 onBind 方法的返回值是否为空，有两种情况。
1、onBind 返回值是 null;
调用 bindService 开启服务，生命周期执行的方法依次是：
onCreate() ==> onBind();
调用多次 bindService，onCreate 和 onBind 也只在第一次会被执行。
调用 unbindService 结束服务，生命周期执行 onDestroy 方法，并且 unbindService 方法只能调用一次，多次调用应用会抛出异常。使用时也要注意调用 unbindService 一定要确保服务已经开启，否则应用会抛出异常。
2、onBind 返回值不为 null；
看一下 android 对于 onBind 方法的返回类型 IBinder 的介绍，字面上理解是 IBinder 是 android 提供的进程间和跨进程调用机制的接口。而且返回的对象不要直接实现这个接口，应该继承 Binder 这个类。
那么我们就在自己写的 MyService 里创建一个内部类 MyBinder，让他继承 Binder，并在 onBind 方法里返回 MyBinder 的对象。

<pre>@Override
public IBinder onBind(Intent intent) {
    Log.e("call", "onBind");
    MyBinder mbind = new MyBinder();
    Log.e("call", mbind.toString());
    return mbind;
}
private class MyBinder extends Binder{
    public void systemOut(){
        System.out.println("该方法在MyService的内部类MyBinder中");
    }
}

</pre>

这时候调用 bindService 开启服务，生命周期执行的方法依次是：
onCreate() ==> onBind() ==> onServiceConnected();
可以发现我们自己写的 MyConnection 类里的 onServiceConnected 方法被调用了。调用多次 bindService，onCreate 和 onBind 都只在第一次会被执行，onServiceConnected 会执行多次。
并且我们注意到 onServiceConnected 方法的第二个参数也是 IBinder 类型的，不难猜测 onBind() 方法返回的对象被传递到了这里。打印一下两个对象的地址可以证明猜测是正确的。
也就是说我们可以在 onServiceConnected 方法里拿到了 MyService 服务的内部类 MyBinder 的对象，通过这个内部类对象，只要强转一下，我们可以调用这个内部类的非私有成员对象和方法。
调用 unbindService 结束服务和上面相同，unbindService 只能调用一次，onDestroy 也只执行一次，多次调用会抛出异常。
接下来我们说一下 startService 和 bindService 开启服务时，他们与 activity 之间的关系。
1、startService 开启服务以后，与 activity 就没有关联，不受影响，独立运行。
2、bindService 开启服务以后，与 activity 存在关联，退出 activity 时必须调用 unbindService 方法，否则会报 ServiceConnection 泄漏的错误。

最后还有一点，同一个服务可以用两种方式一同开启，没有先后顺序的要求，MyService 的 onCreate 只会执行一次。
关闭服务需要 stopService 和 unbindService 都被调用，也没有先后顺序的影响，MyService 的 onDestroy 也只执行一次。但是如果只用一种方式关闭服务，不论是哪种关闭方式，onDestroy 都不会被执行，服务也不会被关闭。这一点需要注意。