
## 自定义通知

```kotlin
val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

val intent = Intent(this,MainActivity::class.java)
val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
val remoteViews = RemoteViews(packageName,R.layout.layout_notification)
remoteViews.setTextViewText(R.id.mTextTv,"xfhy")
remoteViews.setImageViewResource(R.id.mRmIv,R.drawable.ic_android)

//可以设置某个控件的点击事件
//            remoteViews.setOnClickPendingIntent(R.id.mOpenDemoTv,pendingIntent)

val notification = NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_android)
		.setPriority(NotificationManager.IMPORTANCE_HIGH)
		.setAutoCancel(true)
		.setContent(remoteViews)
		.setContentIntent(pendingIntent)
		.build()
manager.notify(2, notification)
```