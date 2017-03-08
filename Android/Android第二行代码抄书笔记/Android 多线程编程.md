# Android 多线程编程

> 主线程不能够做耗时的操作，网络请求就是耗时的操作需要放到子线程做。子线程不能更新控件的内容(更新Ui)。所以产生了矛盾，解决办法就是使用Handler.

[TOC]

# 1. 消息机制的写法 Handler 

**使用Handler的步骤**：

1. 主线程中创建一个Handler

		private Handler handler = new Handler(){
				public void handleMessage(android.os.Message msg) {
		
				};
		};

2. 重写handler的handlermessage方法

3. 子线程中创建一个Message对象，将获取的数据绑定给msg

		Message msg = new Message();
		//另一种方式：Message msg = Messge.obtain;
		msg.what = 1;  //这个在主线程中用于判断是谁发送的Message对象
		msg.obj = result;

4. 主线程中的handler对象在子线程中将message发送给主线程

				handler.sendMessage(msg);
		
5. 主线程中handlermessage方法接受子线程发来的数据，就可以做更新UI的操作。

		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case UPDATE_TEXT:
					//在这里可以执行UI操作
					break;
			}
		};
		
# 2. 消息机制原理

> Android中的异步消息处理主要由4个部分组成:Message,Handler,MessageQueue和Looper.

1. Message:用来携带子线程中的数据。
2. MessageQueue:用来存放所有子线程发来的Message.
3. Handler:用来在子线程中发送Message，在主线程中接受Message，处理结果
4. Looper:是一个消息循环器，一直循环遍历MessageQueue，从MessageQueue中取一个Message，派发给Handler处理。

# 3.使用AsyncTask

> 借助AsyncTask,可以十分简单的从子线程切换到主线程.当然,它的背后实现原理也是基于异步消息处理机制的.只是Android帮我们做了封装.

 基本用法

AsyncTask 是一个抽象类，所以如果我们想使用它，就必须要创建一个子类去继承它。在继承时我们可以为AsyncTask类指定三个泛型参数，这三个参数的用途如下。

1.  Params

初始化参数类型

2.  Progress

进度参数类型

3.  Result

返回值参数类型。

因此，一个最简单的自定义 AsyncTask 就可以写成如下方式：

	class DownloadTask extends AsyncTask<Void, Integer, Boolean> {
	    ……
	}

经常需要重写的方法有以下4个:

- onPreExecute()  主线程

这个方法会在后台任务开始执行之前调用,用于进行一些界面上的初始化操作,比如显示一个进度条对话框等.

- onProgressUpdate()  主线程

当在后台任务中调用了publishProgress(Progress...)方法后，这个方法就很快会被调用，方法中携带的参数就是在后台任务中传递过来的。在这个方法中可以对UI进行操作，利用参数中的数值就可以对界面元素进行相应的更新。

- doInBackground()  子线程

这个方法中的所有代码都会在子线程中运行，我们应该在这里去处理所有的耗时任务。任务一旦完成就可以通过return语句来将任务的执行结果进行返回，如果AsyncTask的第三个泛型参数指定的是Void，就可以不返回任务执行结果。注意，在这个方法中是不可以进行UI操作的，如果需要更新UI元素，比如说反馈当前任务的执行进度，可以调用publishProgress(Progress...)方法来完成。

- onPostExecute()  主线程

当后台任务执行完毕并通过return语句进行返回时，这个方法就很快会被调用。返回的数据会作为参数传递到此方法中，可以利用返回的数据来进行一些UI操作，比如说提醒任务执行的结果，以及关闭掉进度条对话框等。

**启动任务**:只需要编写如下代码`new DownloadTask().execute()`,这里DownloadTask是继承自AsyncTask的.

**总结**

简单来说,使用AsyncTask的诀窍就是,在doInBackground()方法中执行具体的耗时任务,在onProgressUpdate()方法中进行UI操作,在onPostExecute()方法中执行一些具体的任务的收尾工作.
