# GreenDao踩坑记

## 前言

以前都是自己写SQLite的dao,然后自己写sql语句,自己管理.最近发现一款开源库比较火,已经被开发者广泛使用.入门简单,不用再写sql语句,增删改查都只需一句话即可搞定.

当然流行也是有原因的,在第三方主流库中它的操作(插入,更新,读取)是最快的.

![](http://greenrobot.org/wordpress/wp-content/uploads/greenDAO-vs-OrmLite-vs-ActiveAndroid.png)

优点主要是下面几点:

- 存取速度快
- 支持数据库加密
- 轻量级
- 激活实体
- 支持缓存
- 代码自动生成

## 1. 引入GreenDao

首先在应用程序最外层的build.gradle中加入如下代码:

``` gradle
    // In your root build.gradle file:
    buildscript {
        repositories {
            jcenter()
            mavenCentral() // add repository
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:2.3.3'
            classpath 'org.greenrobot:greendao-gradle-plugin:3.2.2' // add plugin
        }
    }
```

其次是在app主工程的build.gradle里面加入如下代码:

    // In your app projects build.gradle file:
    apply plugin: 'com.android.application'
    apply plugin: 'org.greenrobot.greendao' // apply plugin
    
    dependencies {
        compile 'org.greenrobot:greendao:3.2.2' // add library
    }

## 2.创建Bean对象

``` java
@Entity
public class CacheBean {
    /**
     * 对象的Id，必须使用Long类型作为EntityId，否则会报错。(autoincrement = true)表示主键会自增，如果false就会使用旧值
     */
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String key;
    private String json;
}
```

其中注解的意思:
- @Entity：告诉GreenDao该对象为实体，只有被@Entity注释的Bean类才能被dao类操作
- @Id：对象的Id，使用Long类型作为EntityId，否则会报错。(autoincrement = true)表示主键会自增，如果false就会使用旧值
- @Property：可以自定义字段名，注意外键不能使用该属性
- @NotNull：属性不能为空
- @Transient：使用该注释的属性不会被存入数据库的字段中
- @Unique：该属性值必须在数据库中是唯一值
- @Generated：编译后自动生成的构造函数、方法等的注释，提示构造函数、方法等不能被修改


**注意:写好了bean对象之后必须进行如下操作:Android Studio菜单栏->Build->make project,然后greenDao会自动生成代码**

自动生成的代码如下:
![](http://olg7c0d2n.bkt.clouddn.com/17-10-10/35015343.jpg)

其中这些类对应的意思:
- DevOpenHelper：创建SQLite数据库的SQLiteOpenHelper的具体实现
- DaoMaster：GreenDao的顶级对象，作为数据库对象、用于创建表和删除表
- DaoSession：管理所有的Dao对象，Dao对象中存在着增删改查等API

## 3.初始化(创建)数据库

直接在应用程序的Application中进行初始化GreenDao,获取可以操作数据库的dao

``` java
public class NewsApplication extends Application {

    /**
     * 获取Dao对象管理者  Dao对象中存在着增删改查等API
     */
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        initDatabase();
    }

    private void initDatabase() {
        //创建数据库news.db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "news.db", null);
        //获取可写数据库
        Database db = helper.getWritableDb();
        //获取Dao对象管理者
        daoSession = new DaoMaster(db).newSession();
    }

    /**
     * 获取Dao对象管理者
     * @return DaoSession
     */
    public static DaoSession getDaoSession() {
        return daoSession;
    }
}

```

## 4.数据库的增删改查,实现自己的业务逻辑

``` java

public class CacheDao {

    /**
     * 插入cache  如果已经存在则替换
     *
     * @param cacheBean CacheBean数据
     */
    public static void insertCache(CacheBean cacheBean) {
        NewsApplication.getDaoSession().insertOrReplace(cacheBean);
    }

    /**
     * 删除缓存
     *
     * @param cacheBean CacheBean
     */
    public static void deleteCache(CacheBean cacheBean) {
        NewsApplication.getDaoSession().delete(cacheBean);
    }

    /**
     * 更新缓存
     *
     * @param cacheBean CacheBean
     */
    public static void updateCache(CacheBean cacheBean) {
        NewsApplication.getDaoSession().update(cacheBean);
    }

    /**
     * 查询指定key的缓存
     *
     * @param key 缓存的key  唯一标识
     * @return 返回查询到的key的缓存集合  一般来说,这里是返回1个,当然,可能数据库里面没有该key对应的缓存
     */
    public static List<CacheBean> queryCacheByKey(String key) {
        //CacheEntityDao是自动生成的里面是一些数据库操作
        //然后这里的Properties.Key也是自动生成的,意思是表里面的一个字段
        return NewsApplication.getDaoSession().queryBuilder(CacheBean.class).where(CacheEntityDao
                .Properties.Key.eq(key)).list();
    }

    /**
     * 查询全部缓存数据
     *
     * @return 缓存集合
     */
    public static List<CacheBean> queryAllCache() {
        return NewsApplication.getDaoSession().loadAll(CacheBean.class);
    }

}


```

## 结语

关于GreenDao的基本操作就到这里,它的高级操作请到[官网](http://greenrobot.org/greendao/features/)学习.
