> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/cfa46bea6d7b

##### 简介

ContentProvider 是 Android 中提供的专门用于不同应用进行数据共享的方式，它是一种进程间的通信，底层是用 Binder 实现的。ContentProvider 是系统为我们封装的，使得我们无须关心底层细节即可轻松实现 IPC。
　　系统给我们提供了许多 ContentProvider，比如通信录，日程表信息等，要跨进程访问这些信息，只需要通过 ContentResolver 的 query、update、insert 和 delete 方法即可。自定义一个 ContentProvider 只需继承 ContentProvider 类并实现六个抽象方法即可：onCreate、query、update、insert、delete 和 getType。onCreate 代表 ContentProvider 的创建，一般来说我们需要做一些初始化工作；getType 用来返回一个 Uri 请求所对应的 MIME 类型（媒体类型），比如图片、视频等，如果我们应用不关注这个选项，可以直接在这个方法中返回 null 或者 “_/_”。根据 Binder 的工作原理，我们知道这六个方法均运行在 ContentProvider 的进程中，**除了 onCreate 由系统回调并运行在主线程中，其他五个方法均由外界回调并运行在 Binder 线程池中。**
　　ContentProvider 对底层的数据存储方式没有任何要求，既可以使用 SQLite 数据库，也可以使用普通文件，甚至可以采用内存中的一个对象来进行数据存储。

##### 使用

<pre><provider
       android:
       android:authorities="com.cwx.test.xxxProvider"
       android:permission="com.cwx.provider"
       android:process=":provider"
>
</provider>

</pre>

android:authorities 是 ContentProvider 的唯一标识，通过这个属性外部应用就可以访问我们的 ContentProvider，建议命名时加上包名前缀。android:process 声明 ContentProvider 运行于独立的进程。android:permission 声明外界需要访问该 ContentProvider 时需要声明该权限。如果声明 android:readPermission 和 android:writePermission 属性，则外部应用也必须声明相应的权限才可以进行读 / 写操作，否则会异常终止。
　　java 代码中访问 ContentProvider 的关键代码如下：

<pre>Uri uri = Uri.parse("content://com.cwx.test.xxxProvider");
getContentResolver().query(uri,null,null,null,null);

</pre>

##### 具体细节

ContentProvider 通过 Uri 来区分外界要访问的数据集合。我们可以用 UriMatcher 的 addURI 方法将 Uri 和 Uri_Code 关联起来。当外界访问我们的 ContentProvider 时，我们可以根据携带过来的 Uri 来得到 Uri_Code, 并根据 Uri_Code 匹配外界要访问的表，然后进行相应的操作。
　　具体例子如下：

<pre>public static final String AUTHORITY = "com.cwx.test.provider";
public static final Uri TEST1_PROVIDER = Uri.parse("content://"+AUTHORITY +"/test1");
public static final Uri TEST2_PROVIDER = Uri.parse("content://"+AUTHORITY +"/test2");
public static final int TEST1_URI_CODE = 0；
public static final int  TEST2_URI_CODE =1;
private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.No_MATCH);
static{
   sUriMatcher.addURI(AUTHORITY,"test1",TEST1_URI_CODE);
   sUriMatcher.addURI(AUTHORITY,"test2",TEST2_URI_CODE);
}

</pre>

通过以上代码设置，外界就可以通过 uri 访问到 ContentProvider 具体对应的表了。

<pre>string name = "";
switch(sUriMatcher.match(uri)){
    case TEST1_URI_CODE:
          name = "test1_name";//test1_name为contentProvider维护的一个数据库表
          break;
    case TEST2_URI_CODE:
          name = "test2_name";
          break;
}

</pre>

##### 注意点

*   当通过增删改方法导致 ContentProvider 数据发生变化时需要通过 ContentResolver 的 notifyChange 方法来通知外界数据发生改变。
*   可以调用 ContentResolver 的 registerContentObserver 方法来注册观察者，通过 unregisterContentObserver 方法来反注册观察者。
*   query、update、insert、delete 存在多线程并发访问，需要做好线程同步。