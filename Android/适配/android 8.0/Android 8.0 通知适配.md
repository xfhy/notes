# Android 8.0通知适配

> 从Android 8.0系统开始，Google引入了通知渠道这个概念。

什么是通知渠道呢？顾名思义，就是每条通知都要属于一个对应的渠道。每个App都可以自由地创建当前App拥有哪些通知渠道，但是这些通知渠道的控制权都是掌握在用户手上的。用户可以自由地选择这些通知渠道的重要程度，是否响铃、是否振动、或者是否要关闭这个渠道的通知。

拥有了这些控制权之后，用户就再也不用害怕那些垃圾推送消息的打扰了，因为用户可以自主地选择自己关心哪些通知、不关心哪些通知。举个具体的例子，我希望可以即时收到支付宝的收款信息，因为我不想错过任何一笔收益，但是我又不想收到支付宝给我推荐的周围美食，因为我没钱只吃得起公司食堂。这种情况，支付宝就可以创建两种通知渠道，一个收支，一个推荐，而我作为用户对推荐类的通知不感兴趣，那么我就可以直接将推荐通知渠道关闭，这样既不影响我关心的通知，又不会让那些我不关心的通知来打扰我了。

对于每个App来说，通知渠道的划分是非常需要仔细考究的，因为通知渠道一旦创建之后就不能再修改了，因此开发者需要仔细分析自己的App一共有哪些类型的通知，然后再去创建相应的通知渠道。这里我们来参考一下Twitter的通知渠道划分：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCKf2UolaRibX6w3Fm7iaQyLGkxUUPjNO6hibzqHHHEPvvNibKMwnMjSicV1g/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

可以看到，Twitter就是根据自己的通知类型，对通知渠道进行了非常详细的划分，这样用户的自主选择性就比较高了，也就大大降低了用户不堪其垃圾通知的骚扰而将App卸载的概率。

Google这次对于8.0系统通知渠道的推广态度还是比较强硬的。

首先，如果你升级了appcompat库，那么所有使用appcompat库来构建通知的地方全部都会进行废弃方法提示，如下所示：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCmWiaEfiasJ7FBLibibM4acYvia5oLicc5IKXO5apYHXCqGmRFscQR7ic383iaA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

**因此这里给大家的建议就是，一定要适配。**

## 创建通知渠道

**首先要确保的是当前手机的系统版本必须是Android 8.0系统或者更高，因为低版本的手机系统并没有通知渠道这个功能，不做系统版本检查的话会在低版本手机上造成崩溃。**

创建一个项目,确保targetSdkVersion已经指定到了26或者更高,接下来修改MainActivity中的代码，如下所示：
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
            channelId = "subscribe";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
    }
    
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
    
}
```
需要注意的是，创建一个通知渠道至少需要渠道ID、渠道名称以及重要等级这三个参数，其中渠道ID可以随便定义，只要保证全局唯一性就可以。渠道名称是给用户看的，需要能够表达清楚这个渠道的用途。重要等级的不同则会决定通知的不同行为，当然这里只是初始状态下的重要等级，用户可以随时手动更改某个渠道的重要等级，App是无法干预的。

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCwTwGHydl7t0l9ibqFVMLGqL7Tcb063dMxfWdZUhTjTonRf8ic2bLglUw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

刚才我们创建的两个通知渠道这里已经显示出来了。可以看到，由于这两个通知渠道的重要等级不同，通知的行为也是不同的，聊天消息可以发出提示音并在屏幕上弹出通知，而订阅消息只能发出提示音。

当然，用户还可以点击进去对该通知渠道进行任意的修改，比如降低聊天消息的重要等级，甚至是可以完全关闭该渠道的通知。

至于创建通知渠道的这部分代码，你可以写在MainActivity中，也可以写在Application中，实际上可以写在程序的任何位置，只需要保证在通知弹出之前调用就可以了。并且创建通知渠道的代码只在第一次执行的时候才会创建，以后每次执行创建代码系统会检测到该通知渠道已经存在了，因此不会重复创建，也并不会影响任何效率。

## 让通知显示出来

触发通知的代码和之前版本基本是没有任何区别的，只是在构建通知对象的时候，需要多传入一个通知渠道ID，表示这条通知是属于哪个渠道的。

那么下面我们就来让通知显示出来。

首先修改activity_main.xml中的代码，如下所示：
```xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="发送聊天消息"
        android:onClick="sendChatMsg"
        />
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="发送订阅消息"
        android:onClick="sendSubscribeMsg"
        />
</LinearLayout>
```
这里我们在布局文件中加入了两个按钮，很显然，一个是用于触发聊天消息渠道通知的，一个是用于触发订阅消息渠道通知的。

接下来修改MainActivity中的代码，如下所示：
```java
public class MainActivity extends AppCompatActivity {

    ...
    
    public void sendChatMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "chat")
                .setContentTitle("收到一条聊天消息")
                .setContentText("今天中午吃什么？")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }
    
    public void sendSubscribeMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "subscribe")
                .setContentTitle("收到一条订阅消息")
                .setContentText("地铁沿线30万商铺抢购中！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setAutoCancel(true)
                .build();
        manager.notify(2, notification);
    }
    
}
```

这里我们分别在sendChatMsg()和sendSubscribeMsg()方法中触发了两条通知，创建通知的代码就不再多做解释了，和传统创建通知的方法没什么两样，只是在NotificationCompat.Builder中需要多传入一个通知渠道ID，那么这里我们分别传入了chat和subscribe这两个刚刚创建的渠道ID。

现在重新运行一下代码，并点击发送聊天消息按钮，效果如下图所示：

![])(http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCEfOrIdFZcrjbcGaVHiapEj04fxAws9libvVOCmeSM04O0Zdao5I3ibzJw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

由于这是一条重要等级高的通知，因此会使用这种屏幕弹窗的方式来通知用户有消息到来。然后我们可以下拉展开通知栏，这里也能查看到通知的详细信息：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dC4LuWDJKSEkKicWdWT6RVKkzQkASJyZpH18Up1pJM4iaqiae4vSssxxDoQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

用户可以通过快速向左或者向右滑动来关闭这条通知。

接下来点击发送订阅消息按钮，你会发现现在屏幕上不会弹出一条通知提醒了，只会在状态栏上显示一个小小的通知图标：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCnFHn0okW6NA5JwjtggicEDuC3PZpfkL74FZkibibbbfNKzFo0clGIKibxQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

因为订阅消息通知的重要等级是默认级别，这就是默认级别通知的展示形式。当然我们还是可以下拉展开通知栏，查看通知的详细信息：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCRXrnNdne91ouQgAzgWYORZrRCqbmzGOw0Y7ebXhdrIicFvRgPSic9kSw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

不过上面演示的都是通知栏的传统功能，接下来我们看一看Android 8.0系统中通知栏特有的功能。

刚才提到了，快速向左或者向右滑动可以关闭一条通知，但如果你缓慢地向左或者向右滑动，就会看到这样两个按钮：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCSNwexa2d695WiceK4ETKjRK9Ho0qrWIzVvVCXrCjvlRsPP02icFd3ibrA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

其中，左边那个时钟图标的按钮可以让通知延迟显示。比方说这是一条比较重要的通知，但是我暂时没时间看，也不想让它一直显示在状态栏里打扰我，我就可以让它延迟一段后时间再显示，这样我就暂时能够先将精力放在专注的事情上，等过会有时间了这条通知会再次显示出来，我不会错过任何信息。如下所示：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCGpJibsNzXP92sLwIichoweE6sToCibPUHkhuK5nBgawXD0d5rVToxI16Q/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

而右边那个设置图标的按钮就可以用来对通知渠道进行屏蔽和配置了，用户对每一个App的每一个通知渠道都有绝对的控制权，可以根据自身的喜好来进行配置和修改。如下所示：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCrAJICtEqErwmRH6htC21canOQdLTtXCfHJWszia6vJrTX6BHlxXp6FA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

比如说我觉得订阅消息老是向我推荐广告，实在是太烦了，我就可以将订阅消息的通知渠道关闭掉。这样我以后就不会再收到这个通知渠道下的任何消息，而聊天消息却不会受到影响，这就是8.0系统通知渠道最大的特色。

另外，点击上图中的所有类别就可以进入到当前应用程序通知的完整设置界面。

## 管理通知渠道

在前面的内容中我们已经了解到，通知渠道一旦创建之后就不能再通过代码修改了。既然不能修改的话那还怎么管理呢？为此，Android赋予了开发者读取通知渠道配置的权限，如果我们的某个功能是必须按照指定要求来配置通知渠道才能使用的，那么就可以提示用户去手动更改通知渠道配置。

只讲概念总是不容易理解，我们还是通过具体的例子来学习一下。想一想我们开发的是一个类似于微信的App，聊天消息是至关重要的，如果用户不小心将聊天消息的通知渠道给关闭了，那岂不是所有重要的信息全部都丢了？为此我们一定要保证用户打开了聊天消息的通知渠道才行。

修改MainActivity中的代码，如下所示：
```java
public class MainActivity extends AppCompatActivity {

    ...
    
    public void sendChatMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel("chat");
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                startActivity(intent);
                Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
            }
        }
        Notification notification = new NotificationCompat.Builder(this, "chat")
                ...
                .build();
        manager.notify(1, notification);
    }
    
    ...
}
```
这里我们对sendChatMsg()方法进行了修改，通过getNotificationChannel()方法获取到了NotificationChannel对象，然后就可以读取该通知渠道下的所有配置了。这里我们判断如果通知渠道的importance等于IMPORTANCE_NONE，就说明用户将该渠道的通知给关闭了，这时会跳转到通知的设置界面提醒用户手动打开。

现在重新运行一下程序，效果如下图所示：
![](http://mmbiz.qpic.cn/mmbiz_gif/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCt2BsY5ylHP9SRpsUicWlmSPg5oXa0LQtqibICw4va46ictfRr078KIiaTg/640?wx_fmt=gif&tp=webp&wxfrom=5&wx_lazy=1)


可以看到，当我们将聊天消息的通知渠道关闭后，下次再次发送聊天消息将会直接跳转到通知设置界面，提醒用户手动将通知打开。

除了以上管理通知渠道的方式之外，Android 8.0还赋予了我们删除通知渠道的功能，只需使用如下代码即可删除：
```java
NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
manager.deleteNotificationChannel(channelId);
```

但是这个功能非常不建议大家使用。因为Google为了防止应用程序随意地创建垃圾通知渠道，会在通知设置界面显示所有被删除的通知渠道数量，如下图所示：
![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dC5adbZdwpMMW4k7uI1mupPNWYchq68lamLWsZGibrmMFXfWLh3eicdHNg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

这样是非常不美观的，所以对于开发者来说最好的做法就是仔细规划好通知渠道，而不要轻易地使用删除功能。

## 显示未读角标

前面我们提到过，苹果是从iOS 5开始才引入了通知栏功能，那么在iOS 5之前，iPhone都是怎么进行消息通知的呢？使用的就是未读角标功能，效果如下所示：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCqV1ib6lFkcZXjEMjB3SHFfHY5TgZC2JnhnWjfX4OJPV80E6AOsvMHAA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

实际上Android系统之前是从未提供过这种类似于iOS的角标功能的，但是由于很多国产手机厂商都喜欢跟风iOS，因此各种国产手机ROM都纷纷推出了自己的角标功能。

可是国产手机厂商虽然可以订制ROM，但是却没有制定API的能力，因此长期以来都没有一个标准的API来实现角标功能，很多都是要通过向系统发送广播来实现的，而各个手机厂商的广播标准又不一致，经常导致代码变得极其混杂。

值得高兴的是，从8.0系统开始，Google制定了Android系统上的角标规范，也提供了标准的API，长期让开发者头疼的这个问题现在终于可以得到解决了。

那么下面我们就来学习一下如何在Android系统上实现未读角标的效果。修改MainActivity中的代码，如下所示：
```java
public class MainActivity extends AppCompatActivity {

    ...
    
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
    
    public void sendSubscribeMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "subscribe")
                ...
                .setNumber(2)
                .build();
        manager.notify(2, notification);
    }
    
}
```

可以看到，这里我们主要修改了两个地方。第一是在创建通知渠道的时候，调用了NotificationChannel的setShowBadge(true)方法，表示允许这个渠道下的通知显示角标。第二是在创建通知的时候，调用了setNumber()方法，并传入未读消息的数量。

现在重新运行一下程序，并点击发送订阅消息按钮，然后在Launcher中找到NotificationTest这个应用程序，如下图所示：

![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCc69zMuHlrdyx9B4upfD1Y0ttLp2ufM8JB7fGGMGpeqYpBicegH4SdRA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

可以看到，在图标的右上角有个绿色的角标，说明我们编写的角标功能已经生效了。

需要注意的是，即使我们不调用setShowBadge(true)方法，Android系统默认也是会显示角标的，但是如果你想禁用角标功能，那么记得一定要调用setShowBadge(false)方法。

但是未读数量怎么没有显示出来呢？这个功能还需要我们对着图标进行长按才行，效果如下图所示：
![](http://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6bRXEBicyUhyyvjwLlOR4dCyCX1Um7OXKI1IXQn0jC1SnGx04keLcEmunIcxECEXzoYORBK29nibEw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

这样就能看到通知的未读数量是2了。

可能有些朋友习惯了iOS上的那种未读角标，觉得Android上这种还要长按的方式很麻烦。这个没有办法，因为这毕竟是Android原生系统，Google没有办法像国内手机厂商那样可以肆无忌惮地模仿iOS，要不然可能会吃官司的。但是我相信国内手机厂商肯定会将这部分功能进行定制，风格应该会类似于iOS。不过这都不重要，对于我们开发者来说，最好的福音就是有了统一的API标准，不管国内手机厂商以后怎么定制ROM，都会按照这个API的标准来定制，我们只需要使用这个API来进行编程就可以了。