## 8.0系统的通知栏适配

> 从Android 8.0系统开始，Google引入了通知渠道这个概念。

**我需要适配吗?**

如果你将项目中的targetSdkVersion指定到了26或者更高，那么Android系统就会认为你的App已经做好了8.0系统的适配工作，当然包括了通知栏的适配。这个时候如果还不使用通知渠道的话，那么你的App的通知将完全无法弹出。因此这里给大家的建议就是，**一定要适配。**

注意以下几点:
1. 渠道只需要创建一次,可以是MainActivity或者Application或者其他地方,在显示之前创建渠道
2. 系统检测到已经创建渠道时不会重复创建,不会影响任何效率
3. 在Android 8.0以上不创建通知渠道就无法显示通知
4. 从8.0系统开始，Google制定了Android系统上的角标规范，也提供了标准的API
5. 不建议使用删除通知渠道API   ,删除了在设置里面也能看见.

下面是创建,管理通知渠道的代码
```kotlin
class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANNELID_CHAT = "chat"
        const val CHANNELID_SUBSCRIBE = "subscribe"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //大于8.0 才需要创建通知渠道  必须做判断,不做判断低版本会崩溃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建2个通知渠道
            //渠道ID
            var channelId = "chat"
            //渠道名称
            var channelName = "聊天消息"
            //重要等级
            var importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)

            channelId = "subscribe"
            channelName = "订阅消息"
            importance = NotificationManager.IMPORTANCE_DEFAULT
            createNotificationChannel(channelId, channelName, importance)
        }

        mSendMsgBtn.setOnClickListener {
            sendChatMsg()
        }
        mLikeMsgBtn.setOnClickListener {
            sendSubscribeMsg()
        }
        mCreateSubscriptBtn.setOnClickListener {
            createSubscript()
        }

    }

    /**
     * 创建渠道
     */
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        //如果需要展示桌面
        channel.setShowBadge(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * 发送聊天消息
     */
    private fun sendChatMsg() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //判断用户是否已经将该通知渠道关闭  如果关闭则需要跳转到该通知渠道设置界面,并提示用户打开该通知渠道
            val channel = manager.getNotificationChannel(CHANNELID_CHAT)
            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.id)
                startActivity(intent)
                Toast.makeText(this, "请收到将通知打开", Toast.LENGTH_SHORT).show()
            }
        }

        //只是在NotificationCompat.Builder中需要多传入一个通知渠道ID
        val builder = NotificationCompat.Builder(this, CHANNELID_CHAT)
        val notification = builder.setContentTitle("收到一条聊天消息")
                .setContentText("今天晚上吃什么?")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_android)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_android))
                .setAutoCancel(true)
                .build()
        manager.notify(1, notification)
    }

    /**
     * 发送订阅消息
     */
    private fun sendSubscribeMsg() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNELID_SUBSCRIBE)
                .setContentTitle("收到一条订阅消息")
                .setContentText("地跌沿线30万商铺抢购中!")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_android)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_android))
                .setAutoCancel(true)
                .setNumber(2)  //显示未读消息角标
                .build()
        manager.notify(2, notification)
    }

    /**
     * 创建未读消息角标
     */
    private fun createSubscript() {

    }

    //除了以上管理通知渠道的方式之外，Android 8.0还赋予了我们删除通知渠道的功能，只需使用如下代码即可删除
    //但是这个功能非常不建议大家使用。因为Google为了防止应用程序随意地创建垃圾通知渠道，会在通知设置界面显示所有被删除的通知渠道数量
    /*private fun deleteNotifi(){
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.deleteNotificationChannel(channelId)
    }*/

}
```
