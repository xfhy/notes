## 记录场景

Android 8.0 有一项复杂功能；系统不允许后台应用创建后台服务。 因此，Android 8.0 引入了一种全新的方法，即 Context.startForegroundService()，以在前台启动新服务。 
在系统创建服务后，应用有5秒的时间来调用该服务的 startForeground() 方法以显示新服务的用户可见通知。如果应用在此时间限制内未调用 startForeground()，则系统将停止服务并声明此应用为 ANR。

但是目前在调用：context.startForegroundService(intent)时报如下ANR，startForegroundService()文档说明在service启动后要调用startForeground()。

```
android.app.RemoteServiceException: Context.startForegroundService() did not then call Service.startForeground()
```

## 解决方案

使用startForegroundService启动服务后，在service的onCreate方法中调用startForeground()。

注意:在api 26 中要使用Notification.Builder(Context, String) ，并且要指明 NotificationChannel 的Id. 如果不加则会提示：Developer warning for package XXX，Failed to post notification on channel “null”.

## 最终Service代码

```kotlin
class InitService : IntentService("InitService") {

    @TargetApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        //通知渠道
        val channel = NotificationChannel("start", "start",
                NotificationManager.IMPORTANCE_HIGH)

        //创建渠道
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = Notification.Builder(this, "start").build()
        startForeground(1, notification)
    }

    override fun onHandleIntent(intent: Intent?) {
        .....
    }

    companion object {
        @JvmStatic
        fun startActionInit(context: Context) {
            val intent = Intent(context, InitService::class.java)
            //8.0以上区别对待 开启后台Service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}

```
