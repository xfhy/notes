
#### Bundle

四大组件中的三大组件(Activity,Service,Receiver)都是支持在Intent中传递Bundle数据的.Bundle实现了Parcelable接口,所以可以很方便得在进程间传输.

#### 文件

Linux上的并发读写没有做限制,所以如果并发的读写的话,会出问题. 在非并发的条件下,文件是一种很不错的方式. 首先进程A将对象序列化到文件中,然后进程B再反序列化该文件得到相同内容的对象.

文件共享方式适合在对数据要求不高的进程间进行通信,并且要妥善处理并发读/写问题.

SharedPreferences也是操作文件,但是它的读/写有一定的缓存策略,即在内存中会有一份SharedPreferences文件的缓存,因此在多进程模式下,系统对它的读写变得不可靠. 当面对高并发的读/写访问时,SharedPreferences有很大几率会丢失数据,因此,不建议进程间通信使用SharedPreferences.

#### Messenger

Messenger是一种轻量级的IPC方法,它底层其实是使用的AIDL.

下面是服务端的典型代码:
```java
public class MessengerService extends Service {

    private static final String TAG = "MessengerService";
    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.w(TAG, "receive msg from client: " + msg.getData().getString("msg"));
                default:
                    super.handleMessage(msg);
            }
        }
    }
}

```

客户端发消息给服务端:

```java
public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";
    private Messenger mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //这个IBinder对象是Service传回来的,
            mService = new Messenger(service);

            //消息组装
            Message message = Message.obtain(null, 1);
            Bundle bundle = new Bundle();
            bundle.putString("msg", "hello this is client");
            message.setData(bundle);

            //发送消息给服务端
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        //绑定服务  Service在另一个进程
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
```

加入服务端可以回传数据的代码:
```java
private static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.w(TAG, "receive msg from client: " + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message message = Message.obtain(null, 1);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply","恩,你的消息我收到了");
                    message.setData(bundle);

                    //发送
                    try {
                        client.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    }
```

加入客户端接收服务端的消息:
```java
private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ....
            //需要把这个传过去  把这个进程的Messenger传入另一个进程,它拿到后可以放消息在这里面
            message.replyTo = mGetReplyMessenger;
            .......
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.w(TAG, "handleMessage: 收到服务端消息:" + msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
```

- Messenger是以串行的方式处理客户端发来的消息.如果大量的消息同时发生到服务端,服务端仍然只能一个个处理.

![image](F522797AE88241688CA77CFB98AFA8D6)


#### AIDL

先介绍使用AIDL来进行进程间通信的流程,分为服务端和客户端两个方面.

1. 服务端

服务端首先需要创建一个Service用来监听客户端的连接请求,然后创建一个AIDL文件,将暴露给客户端的接口在这个AIDL文件中声明,最后在Service中实现这个AIDL接口.

2. 客户端

客户端首先需要绑定服务端的Service,绑定成功后,将服务端返回的Binder对象转成AIDL接口所属的类型,接着就可以调用AIDL中的方法了.