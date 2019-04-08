> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5b30e39bf265da599423510a#heading-10

> 本文由[`玉刚说写作平台`](https://link.juejin.im?target=http%3A%2F%2Frenyugang.io%2Fpost%2F75)提供写作赞助
> 
> 原作者：`Boy·哈利波特`
> 
> 版权声明：本文版权归微信公众号 `玉刚说` 所有，未经许可，不得以任何形式转载

## 一、前言

* * *

#### 1.1、Android Architecture Components 介绍

Android Architecture Components 是谷歌在 Google I/O 2017 发布一套帮助开发者解决 Android 架构设计的方案。里面包含了两大块内容：

*   生命周期相关的 Lifecycle-aware Components
*   数据库解决方案 Room

#### 1.2、组件功能

官方给予 Google 组件的功能：A collection of libraries that help you design robust, testable, and maintainable apps. Start with classes for managing your UI component lifecycle and handling data persistence。

使用 Google 提供的处理数据持久化和管理组件生命周期的类，有助于应用开发者们构建更加鲁棒性，可测的，稳定可靠的应用。

提供主要的组件有：

*   Lifecycle：管理组件生命周期
*   Room: 持久化数据结构

#### 1.3、主要架构

![](https://user-gold-cdn.xitu.io/2018/6/25/16436f9149cde438?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 1.4、使用组件

在项目根目录 build.gradle 文件添加仓库依赖：

```
allprojects {
    repositories {
        jcenter()
        google()
    }
}
复制代码
```

如果遇到如下因 gradle 版本导致的编译失败问题：

```
Error:(6, 1) A problem occurred evaluating root project 'TestArc'.>
Could not find method google() for arguments [] on repository container；
复制代码
```

可修改为：

```
maven {
   url 'https://maven.google.com'
}
复制代码
```

然后在主 module 的 build.gradle 文件添加需要依赖的组件：

```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    compile 'com.android.support:appcompat-v7:26.+'

    def lifecycle_version = "1.1.1"

    // ViewModel and LiveData
    compile "android.arch.lifecycle:extensions:$lifecycle_version"
    // alternatively - just ViewModel
    compile "android.arch.lifecycle:viewmodel:$lifecycle_version" // use -ktx for Kotlin
    // alternatively - just LiveData
    compile "android.arch.lifecycle:livedata:$lifecycle_version"
    // alternatively - Lifecycles only (no ViewModel or LiveData).
    // Support library depends on this lightweight import
    compile "android.arch.lifecycle:runtime:$lifecycle_version"

    annotationProcessor "android.arch.lifecycle:compiler:$lifecycle_version"
    // alternately - if using Java8, use the following instead of compiler
    compile "android.arch.lifecycle:common-java8:$lifecycle_version"

    // optional - ReactiveStreams support for LiveData
    compile "android.arch.lifecycle:reactivestreams:$lifecycle_version"

    // optional - Test helpers for LiveData
    // compile "android.arch.core:core-testing:$lifecycle_version"
    compile 'com.squareup.retrofit2:retrofit:2.1.0'

    compile 'com.squareup.retrofit2:converter-gson:2.1.0'
    compile 'com.facebook.stetho:stetho:1.3.1'

    //  room
    compile 'android.arch.persistence.room:runtime:1.1.0'
    annotationProcessor 'android.arch.persistence.room:compiler:1.1.0'
    compile "android.arch.persistence.room:rxjava2:1.1.0"
}
复制代码
```

## 二、Lifecycle 管理生命周期

* * *

#### 2.1、Lifecycle 介绍

Lifecycle 组件指的是 android.arch.lifecycle 包下提供的各种类与接口，可以让开发者构建能感知其他组件（主要指 Activity 、Fragment）生命周期（lifecycle-aware）的类。

#### 2.2、常规 MVP Presenter 使用

比如我们需要监听某个 Activity 生命周期的变化，在生命周期改变的时候打印日志，一般做法构造回调的方式，先定义基础 BaseActivityPresenter 接口：

```
public interface BaseActivityPresenter extends BasePresenter{

    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

}
复制代码
```

在实现类中增加自定义操作（打印日志）：

```
public class ActivityPresenter implements BaseActivityPresenter {

    private static String TAG = ActivityPresenter.class.getSimpleName();

    @Override
    public void onCreate() {
        LogUtil.i(TAG, "onCreate()");
    }

    @Override
    public void onStart() {
        LogUtil.i(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        LogUtil.i(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        LogUtil.i(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        LogUtil.i(TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy()");
    }

}
复制代码
```

然后在需要监听的 Activity 中依次回调方法：

```
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBasePresenter = new ActivityPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBasePresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBasePresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBasePresenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBasePresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBasePresenter.onDestroy();
    }
复制代码
```

在 Activity 的 onCreate() 方法中创建 BasePresenter，监听 Activity 的生命周期方法。

#### 2.3、使用 Lifecycle

上述写可以实现基础的功能，但是不够灵活，假如除了 ActivityPresenter 类，还有别的类要监听 Activity 生命周期变化，那也需要添加许多生命周期的回调方法，比较繁琐。那我们是否可以当 Activity 生命周期发生变化的时候主动通知需求方呢？答案就是使用 Lifecycle 提供的 LifecycleObserver：

```
public class ActivityLifeObserver implements BaseActivityPresenter,
 LifecycleObserver {

    private String TAG = ActivityLifeObserver.class.getSimpleName();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @Override
    public void onCreate() {
        LogUtil.i(TAG, "onCreate()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    @Override
    public void onStart() {
        LogUtil.i(TAG, "onStart()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume() {
        LogUtil.i(TAG, "onResume()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Override
    public void onPause() {
        LogUtil.i(TAG, "onPause()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    @Override
    public void onStop() {
        LogUtil.i(TAG, "onStop()");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy()");
    }

}
复制代码
```

让我们的业务类实现 ActivityLifeObserver 接口，同时在每一个方法实现上增加 @OnLifecycleEvent(Lifecycle.Event.XXXX）注解，OnLifecycleEvent 对应了 Activity 的生命周期方法。被监听的 Actiivty 实现 LifecycleOwner 接口，然后在需要监听的 Activity 中注册：

```
public class DetailActivity extends AppCompatActivity implements LifecycleOwner{

    private static String TAG = DetailActivity.class.getSimpleName();
    private LifecycleRegistry mLifecycleRegistry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mLifecycleRegistry = new LifecycleRegistry(this);
        // 注册需要监听的 Observer 
        mLifecycleRegistry.addObserver(new ActivityLifeObserver());
        mLifecycleRegistry.addObserver(new LocationLifeObserver());
    }

    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
复制代码
```

运行如下：

```
com.troy.androidrc I/ActivityLifeObserver: onCreate()
com.troy.androidrc I/ActivityLifeObserver: onStart()
com.troy.androidrc I/ActivityLifeObserver: onResume()
com.troy.androidrc I/ActivityLifeObserver: onPause()
com.troy.androidrc I/ActivityLifeObserver: onStop()
com.troy.androidrc I/ActivityLifeObserver: onDestroy()
复制代码
```

其中 Lifecycle 使用两个主要的枚举类来表示其所关联组件的生命周期：

*   Event 事件 从组件或者 Lifecycle 类分发出来的生命周期，它们和 Activity／Fragment 生命周期的事件一一对应。(ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY)；
*   State 状态 当前组件的生命周期状态 (INITIALIZED, DESTROYED, CREATED, STARTED, RESUMED)。

![](https://user-gold-cdn.xitu.io/2018/6/25/16436f9149f7df30?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

LifecycleRegistry 类用于注册和反注册需要观察当前组件生命周期的 Observer，用法如下：

```
//  初始化
mLifecycleRegistry = new LifecycleRegistry(this);
mActivityLifeObserver = new ActivityLifeObserver();
//  注册观察者
mLifecycleRegistry.addObserver(mActivityLifeObserver);
mLifecycleRegistry.addObserver(new LocationLifeObserver());
//  移除观察者
mLifecycleRegistry.removeObserver(mActivityLifeObserver);
复制代码
```

## 三、LiveData && ViewModel

* * *

#### 3.1、LiveData && ViewModel 介绍

LiveData 是一种持有可被观察数据的类（an observable data holder class）。和其他可被观察的类不同的是，LiveData 是有生命周期感知能力的（lifecycle-aware,），这意味着它可以在 activities, fragments, 或者 services 生命周期是活跃状态时更新这些组件。

ViewModel 与 LiveData 之间的关系图如下：

![](https://user-gold-cdn.xitu.io/2018/6/25/16436f9149e62735?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 3.2、LiveData && ViewModel 使用

在 Activity 页面有一 TextView，需要展示用户 User 的信息，User 类定义：

```
public class User {

    public String userId;

    public String name;

    public String phone;

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ",  + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

复制代码
```

常规的做法：

```
// 获取 User 的数据后
mTvUser.setText(user.toString());
复制代码
```

这样做的一个问题，如果获取或者修改 User 的来源不止一处，那么需要在多个地方更新 TextView，并且如果在多处 UI 用到了 User，那么也需要在多处更新。

使用 LiveData 与 ViewModel 的组合，将 LiveData 持有 User 实体，作为一个被观察者，当 User 改变时，所有使用 User 的地方自动 change。构建一个 UserViewModel 如下：

```
public class UserViewModel extends ViewModel
 implements BaseViewModel<User> {

    private String TAG = UserViewModel.class.getSimpleName();

    private MutableLiveData<User> liveUser;

    public MutableLiveData<User> getData(){
        if(liveUser == null){
            liveUser = new MutableLiveData<User>();
        }

        liveUser.setValue(loadData());
        return this.liveUser;
    }

    public void changeData(){
        if(liveUser != null){
            liveUser.setValue(loadData());
        }
    }

    @Override
    public User loadData() {
        User user = new User();
        user.userId = RandomUtil.getRandomNumber();
        user.name = RandomUtil.getChineseName();
        user.phone = RandomUtil.getRandomPhone();
        LogUtil.i(TAG, "loadData(): " + user.toString());
        return user;
    }

    @Override
    public void clearData() {

    }
}
复制代码
```

自定义的 UserViewModel 继承系统的 ViewModel，将 User 封装成 MutableLiveData： `if(liveUser == null){ liveUser = new MutableLiveData<User>(); }`

在使用 User 的地方增加观察：

```
//  view model.observe
mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
mUserViewModel.getData().observe(this, new Observer<User>() {
   @Override
   public void onChanged(@Nullable User user) {
       if(user != null){
           mTvUser.setText(user.toString());
       }
   }
});
复制代码
```

数据源发送改变的时候：

```
//	改变 User 内容
mButtonUser.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         if(mUserViewModel != null && mUserViewModel.getData() != null){
             mUserViewModel.changeData();
         }
     }
});

//	setValue
public void changeData(){
   if(liveUser != null){
        liveUser.setValue(loadData());
    }
}
复制代码
```

这样使用到 User 的地方，UI 会自动更新，日志如下：

```
com.troy.androidrc I/DetailActivity:
 User{userId='9372622', name='邓楠', phone='15607043749'}

com.troy.androidrc I/DetailActivity:
 User{userId='6099877', name='文瑾慧', phone='13005794027'}

复制代码
```

## 四、Room

* * *

#### 4.1、Room 介绍

Room 持久层库提供了一个方便我们访问 SQLite 数据库的抽象层 (an abstraction layer)，帮助我们更好的在 APP 上创建我们的数据缓存，能够让 APP 即使在没有网络的情况也能正常使用。

Room 的架构如下：

![](https://user-gold-cdn.xitu.io/2018/6/25/16436f9149dc94b3?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 4.2、Room 使用与主要注解

创建包含订单表的数据库如下步骤：

1、创建 Order.java:

```
@Entity(tableName = "orders")
public class Order {

    @PrimaryKey
    @ColumnInfo(name = "order_id")
    public long orderId;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "owner_name")
    public String ownerName;

    @ColumnInfo(name = "owner_phone")
    public String ownerPhone;

    //  指示 Room 需要忽略的字段或方法
    @Ignore
    public String ignoreText;

    @Embedded
    public OwnerAddress ownerAddress;
}
复制代码
```

2、创建 OrderDao：

```
@Dao
public interface OrderDao {

    @Query("SELECT * FROM orders")
    List<Order> loadAllOrders();

    @Insert
    void insertAll(Order... orders);

    @Query("SELECT * FROM orders WHERE order_id IN (:orderIds)")
    List<Order> queryOrderById(long[] orderIds);

    @Delete
    void deleteOrder(Order... orders);

    @Update
    void updateOrder(Order... orders);
}
复制代码
```

3、创建数据库

```
@Database(entities = {Order.class, AddressInfo.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase{

   public abstract OrderDao getOrderDao();

}

// 实现类
public static void buildDb(){
DB_INSTANCE = Room.
    databaseBuilder(TroyApplication.getInstance(), AppDatabase.class, "troy_db")   //  指定数据库名称
    .addCallback(new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);  //  数据库创建回调；
            LogUtil.i(TAG, "onCreate");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);   //  数据库使用回调；
            LogUtil.i(TAG, "onOpen");
        }
    })
    .allowMainThreadQueries()   // 数据库操作可运行在主线程
    .build();

}
复制代码
```

使用到的主要注解：

*   @Entity(tableName = "orders") // 定义表名；
*   @PrimaryKey // 定义主键；
*   @ColumnInfo(name = "order_id") // 定义数据表中的字段名；
*   @Ignore // 指示 Room 需要忽略的字段或方法；
*   @Embedded // 指定嵌入实体
*   @Query("SELECT * FROM orders") // 定义查询数据接口；
*   @Insert // 定义增加数据接口；
*   @Delete // 定义删除数据接口；
*   @Update // 定义更新数据接口；
*   @Database // 定义数据库信息，表信息，数据库版本

#### 3.3、增删改查实现

增：

```
//  1、插入接口声明
@Insert
void insertAll(Order... orders);

//  2、插入接口实现
@Override
  public void insertAll(Order... orders) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfOrder.insert(orders);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
}

//  3、插入接口调用
AppDatabase db = DbManager.getDbInstance();
OrderDao orderDao = db.getOrderDao();
Order order = Order.createNewOrder();
orderDao.insertAll(order);
复制代码
```

删：

```
//	1、删除接口声明
@Delete
void deleteOrder(Order... orders);

//	2、删除接口实现
@Override
  public void deleteOrder(Order... orders) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfOrder.handleMultiple(orders);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
}

//	3、删除接口调用
AppDatabase db = DbManager.getDbInstance();
OrderDao orderDao = db.getOrderDao();
orderDao.deleteOrder(orderList.get(orderList.size() - 1));
复制代码
```

改：

```
//	1、修改接口声明
 @Update
 void updateOrder(Order... orders);

//	2、修改接口实现
@Override
  public void updateOrder(Order... orders) {
    __db.beginTransaction();
    try {
      __updateAdapterOfOrder.handleMultiple(orders);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
}

//	3、修改接口调用
AppDatabase db = DbManager.getDbInstance();
OrderDao orderDao = db.getOrderDao();
Order order = orderList.get(orderList.size() - 1);
order.ownerName = "update - " + RandomUtil.getChineseName();
orderDao.updateOrder(order);
复制代码
```

查：

```
//	1、查询接口声明
@Query("SELECT * FROM orders")
List<Order> loadAllOrders();

//	2、查询接口实现
@Override
  public List<Order> loadAllOrders() {
    final String _sql = "SELECT * FROM orders";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfOrderId = _cursor.getColumnIndexOrThrow("order_id");
      final int _cursorIndexOfAddress = _cursor.getColumnIndexOrThrow("address");
      final int _cursorIndexOfOwnerName = _cursor.getColumnIndexOrThrow("owner_name");
      final int _cursorIndexOfOwnerPhone = _cursor.getColumnIndexOrThrow("owner_phone");
      final int _cursorIndexOfStreet = _cursor.getColumnIndexOrThrow("street");
      final int _cursorIndexOfState = _cursor.getColumnIndexOrThrow("state");
      final int _cursorIndexOfCity = _cursor.getColumnIndexOrThrow("city");
      final int _cursorIndexOfPostCode = _cursor.getColumnIndexOrThrow("post_code");
      final List<Order> _result = new ArrayList<Order>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Order _item;
        final Order.OwnerAddress _tmpOwnerAddress;
        if (! (_cursor.isNull(_cursorIndexOfStreet) && _cursor.isNull(_cursorIndexOfState) && _cursor.isNull(_cursorIndexOfCity) && _cursor.isNull(_cursorIndexOfPostCode))) {
          _tmpOwnerAddress = new Order.OwnerAddress();
          _tmpOwnerAddress.street = _cursor.getString(_cursorIndexOfStreet);
          _tmpOwnerAddress.state = _cursor.getString(_cursorIndexOfState);
          _tmpOwnerAddress.city = _cursor.getString(_cursorIndexOfCity);
          _tmpOwnerAddress.postCode = _cursor.getInt(_cursorIndexOfPostCode);
        }  else  {
          _tmpOwnerAddress = null;
        }
        _item = new Order();
        _item.orderId = _cursor.getLong(_cursorIndexOfOrderId);
        _item.address = _cursor.getString(_cursorIndexOfAddress);
        _item.ownerName = _cursor.getString(_cursorIndexOfOwnerName);
        _item.ownerPhone = _cursor.getString(_cursorIndexOfOwnerPhone);
        _item.ownerAddress = _tmpOwnerAddress;
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

//	3、查询接口调用
AppDatabase db = DbManager.getDbInstance();
OrderDao orderDao = db.getOrderDao();
return orderDao.loadAllOrders();
复制代码
```

#### 3.4、内嵌实体

如果实体 Order 内部包含地址信息，地址信息分别包含 城市，邮政等信息，可以这样写，使用 @Embedded 注解：

```
static class OwnerAddress {

        public String street;
        public String state;
        public String city;

        @ColumnInfo(name = "post_code")
        public int postCode;
}

@Embedded
public OwnerAddress ownerAddress;
复制代码
```

#### 3.5、配合 LiveData

数据查询可以返回 LiveData 数据：

```
 @Query("SELECT * FROM orders")
 LiveData<List<Order>> loadAllOrderData();
复制代码
```

#### 3.6、配合 RxJava

通过 query 查询返回的实体，可以封装成 对应 RxJava 的操作符封装对象，例如 Flowable，Maybe 等：

```
//	接口声明
@Query("SELECT * from orders where order_id = :id LIMIT 1")
Flowable<Order> queryOrderByIdV2(long id);

@Query("SELECT * from orders where order_id = :id LIMIT 1")
Maybe<Order> queryOrderByIdV3(long id);

// 接口调用
private Maybe<Order> queryOrderV3(){
      AppDatabase db = DbManager.getDbInstance();
      OrderDao orderDao = db.getOrderDao();
      return orderDao.queryOrderByIdV3(10001);
}

Maybe<Order> orderMaybe = queryOrderV3();
        orderMaybe.subscribeOn(Schedulers.io())
             .observeOn(Schedulers.newThread())
             .subscribe(new Consumer<Order>() {
                 @Override
                 public void accept(@NonNull Order order) throws Exception {

                 }
});
复制代码
```

以上代码 Demo 实现：

![](https://user-gold-cdn.xitu.io/2018/6/25/16436f9149f3e996?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

## 五、总结

学会使用 Android Architecture Components 提供的组件简化我们的开发，能够使我们开发的应用模块更解耦更稳定，视图与数据持久层分离，以及更好的扩展性与灵活性，

参考致谢：

1.  [Architechture](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Ftopic%2Flibraries%2Farchitecture%2F)
2.  [Lifecycle package class](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Freference%2Fandroid%2Farch%2Flifecycle%2Fpackage-summary)
3.  [Save data in a local database using Room](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.google.cn%2Ftraining%2Fdata-storage%2Froom%2F)
4.  [Android Room with a View](https://link.juejin.im?target=https%3A%2F%2Fcodelabs.developers.google.com%2Fcodelabs%2Fandroid-room-with-a-view%2F%232)
5.  [Google Samples](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fgooglesamples%2Fandroid-architecture-components)

![](https://user-gold-cdn.xitu.io/2018/6/25/16436fca472067b9?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)关注我，接收更多一手技术干货