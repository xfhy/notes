> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/mq2553299/article/details/80788692 版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/mq2553299/article/details/80788692

> 本文已授权 微信公众号 **玉刚说** （[@任玉刚](https://blog.csdn.net/singwhatiwanna/)）独家发布。

> **争取打造 Android Jetpack 讲解的最好的博客系列**：
> 
> *   [Android 官方架构组件 Lifecycle：生命周期组件详解 & 原理分析](https://www.jianshu.com/p/b1208012b268)
> *   [Android 官方架构组件 ViewModel: 从前世今生到追本溯源](https://www.jianshu.com/p/59adff59ed29)
> *   [Android 官方架构组件 LiveData: 观察者模式领域二三事](https://www.jianshu.com/p/550a8bd71214)
> *   [Android 官方架构组件 Paging：分页库的设计美学](https://www.jianshu.com/p/10bf4bf59122)
> *   [Android 官方架构组件 Navigation：大巧不工的 Fragment 管理框架](https://www.jianshu.com/p/ad040aab0e66)

> **Android Jetpack 实战篇**：
> 
> *   [开源项目：MVVM+Jetpack 实现的 Github 客户端](https://github.com/qingmei2/MVVM-Rhine)
> *   [总结：使用 MVVM 尝试开发 Github 客户端及对编程的一些思考](https://www.jianshu.com/p/b03710f19123)

## <a></a><a id="_13" target="_blank"></a>前言

我是一个崇尚 **开源** 的 Android 开发者，在过去的一段时间里，我研究了 Github 上的一些优秀的开源库，这些库源码中那些 **天马行空** 的 **设计** 和 **思想** 令我沉醉其中。

在我职业生涯的 **伊始**，我没有接触过 **技术大牛**, 但是 **阅读源码** 可以让我零距离碰撞 **全球行业** 内 **最顶尖工程师们** 的思想，我渐渐爱上了 **源码阅读**。

在感叹这些 **棒极了** 的设计方式时，我也尝试去 **模仿** 他们的代码风格。后来朋友问我代码中为什么有这么多 **设计模式** 时，我才发现，**单例** ，**代理** ，**工厂** ，**装饰**，**Builder** ，甚至更多，当初这些书上怎么也捋不清的设计模式，现在的我正在潜移默化使用它们，这不是 **夸张**，我在写这些代码时，它们似乎就应该这么用。

今年年初，我尝试开源了一个 **灵活可高度定制** 的 Android 图片选择框架 **[RxImagePicker](https://github.com/qingmei2/RxImagePicker)** 。这个库获得了部分认可，当然意见和建议也接踵而来，我很快认识到了自己目前能力的不足—— 通过 **组合** 的方式 **将多个优秀的库封装在一起** ，并不是就意味着真正拥有了 **组织架构** 的能力，而自己对于架构的掌握能力，目前还有很多不足之处。

![](https://upload-images.jianshu.io/upload_images/7293029-852c37a98a8cb3df.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我意识到自己的不足，于是我积极寻找 **更多优秀的架构**，试图通过 **源码** 学习更多 API 之外的一些东西：**编程思想** 和 **架构设计** 。

很快，我找到了一个很优秀的库，**Paging**—— 它同样做到了 **业务层与 UI 层之间** 的隔离，并且，它的设计更为 **优秀**。

## <a></a><a id="Paging_Libray_29" target="_blank"></a>Paging Libray

在不久前的 Google 2018 I/O 大会上，Google 正式推出了 **AndroidJetpack** ——这是一套组件、工具和指导，可以帮助开发者构建出色的 Android 应用，**AndroidJetpack** 隆重推出了一个新的分页组件：**Paging Library**。

我尝试研究了 **Paging Library**，并分享给大家，本文的目标是阐述：

*   1\. 了解并如何使用 **Paging**
*   2\. 知道 **Paging** 中每个类的 **职责**，并了解掌握其 **原理**
*   3\. 站在设计者的角度，彻底搞懂 **Paging** 的 **设计思想**

> 本文不是 **Paging API** 使用代码的展示，但通过本文 **彻底搞懂** 它的原理之后，API 的使用也只是 **顺手拈来**。

## <a></a><a id="_41" target="_blank"></a>它是什么，怎么用？

一句话概述： **Paging** 可以使开发者更轻松在 **RecyclerView** 中 **分页加载数据**。

### <a></a><a id="1_45" target="_blank"></a>1\. 官方文档

**[官方文档](https://developer.android.com/topic/libraries/architecture/paging/)** 永远是最接近 **正确** 和 **核心理念** 的参考资料 —— 在不久之后，本文可能会因为框架本身 API 的迭代更新而 **毫无意义**，但官方文档不会，即使在最恶劣的风暴中，它依然是最可靠的 **指明灯**：

[https://developer.android.com/topic/libraries/architecture/paging/](https://developer.android.com/topic/libraries/architecture/paging/)

其次，一个好的 Demo 能够起到重要的启发作用， 这里我推荐这个 Sample:

项目地址：[https://github.com/googlesamples/android-sunflower](https://github.com/googlesamples/android-sunflower)

> 因为刚刚发布的原因，目前 Paging 的中文教程 **比较匮乏**，许多资料的查阅可能需要开发者 **自备梯子**。

### <a></a><a id="2_57" target="_blank"></a>2\. 分页效果

在使用之前，我们需要搞明白的是，目前 Android 设备中比较主流的两种 **分页模式**，用我的语言概述，大概是：

*   传统的 **上拉加载更多** 分页效果
*   **无限滚动** 分页效果

从语义上来讲，我的描述有点不太直观，不了解的读者估计会很迷糊。

![](https://upload-images.jianshu.io/upload_images/7293029-a1dd4218649bf317.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

举个例子，传统的 **上拉加载更多** 分页效果，应该类似 **淘宝 APP** 这种，滑到底部，再上拉显示 footer，才会加载数据：

![](https://upload-images.jianshu.io/upload_images/7293029-7c956d14871186bf.gif?imageMogr2/auto-orient/strip)

而**无限滚动** 分页效果，应该像是 **京东 APP** 这样，如果我们慢慢滑动，当滑动了一定量的数据（这个阈值一般是数据总数的某个百分比）时，会自动请求加载下一页的数据，如果我们继续滑动，到达一定量的数据时，它会继续加载下一页数据，直到加载完所有数据——在用户看来，就好像是一次就加载出所有商品一样：

![](https://upload-images.jianshu.io/upload_images/7293029-aced17013c5a64f7.gif?imageMogr2/auto-orient/strip)

很明显，**无限滚动** 分页效果带来的用户体验更好，不仅是京东，包括 **知乎** 等其它 APP，所采用的分页加载方式都是 **无限滚动** 的模式，而 **Paging** 也正是以**无限滚动** 的分页模式而设计的库。

### <a></a><a id="3Sample_77" target="_blank"></a>3.Sample 展示

我写了一个 Paging 的 sample，它最终的效果是这样：

![](https://upload-images.jianshu.io/upload_images/7293029-7632d616fcd6ce0a.gif?imageMogr2/auto-orient/strip)

项目结构图如下，这可以帮你尽快了解 sample 的结构：

![](https://upload-images.jianshu.io/upload_images/7293029-14afe026a9b0dacb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

> 我把这个 sample 的源码托管在了我的 github 上，你可以通过 [点我查看源码](https://github.com/qingmei2/SamplePaging) 。

### <a></a><a id="4Paging_89" target="_blank"></a>4\. 使用 Paging

现在你已经对 Paging 的功能有了一定的了解，我们可以开始尝试使用它了。

**请注意，本小节旨在简单阐述 Paging 入门使用，读者不应该困惑于 Kotlin 语法或者 Room 库的使用——你只要能看懂基本流程就好了。**

> 因此，我 **更建议** 读者 [点击进入 github](https://github.com/qingmei2/SamplePaging)，并将 Sample 代码拉下来阅读，**仅仅是阅读**—— 相比 Kotlin 语法和 Room 的 API 使用，**理解代码的流程** 更为重要。

#### <a id="_Modulebuildgradle_97" target="_blank"></a>① 在 Module 下的 build.gradle 中添加以下依赖：

```
    def room_version = "1.1.0"
    def paging_version = "1.0.0"
    def lifecycle_version = "1.1.1"

    //Paging的依赖
    implementation "android.arch.paging:runtime:$paging_version"
    //Paging对RxJava2的原生支持
    implementation "android.arch.paging:rxjava2:1.0.0-rc1"

    //我在项目中使用了Room，这是Room的相关依赖
    implementation "android.arch.persistence.room:runtime:$room_version"
    kapt "android.arch.persistence.room:compiler:$room_version"
    implementation "android.arch.persistence.room:rxjava2:$room_version"

    implementation "android.arch.lifecycle:extensions:$lifecycle_version"

```

#### <a id="__116" target="_blank"></a>② 创建数据源

我们要展示在 list 中的数据，主要以 **网络请求** 和 **本地持久化存储** 的方式获取，本文为了保证简单，数据源通过 **Room 数据库中** 获得。

创建 Student 实体类：

```
@Entity
data class Student(@PrimaryKey(autoGenerate = true) val id: Int,
                   val name: String)

```

创建 Dao:

```
@Dao
interface StudentDao {

    @Query("SELECT * FROM Student ORDER BY name COLLATE NOCASE ASC")
    fun getAllStudent(): DataSource.Factory<Int, Student>
}

```

创建数据库：

```
@Database(entities = arrayOf(Student::class), version = 1)
abstract class StudentDb : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {

        private var instance: StudentDb? = null

        @Synchronized
        fun get(context: Context): StudentDb {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext,
                        StudentDb::class.java, "StudentDatabase")
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                ioThread {
                                    get(context).studentDao().insert(
                                            CHEESE_DATA.map { Student(id = 0, name = it) })
                                }
                            }
                        }).build()
            }
            return instance!!
        }
    }
}
private val CHEESE_DATA = arrayListOf(
        "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
        "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
        "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese",
        "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell",
        "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String", "Aromes au Gene de Marc",
        "Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
        "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon",
        "Barry's Bay Cheddar", "Basing", "Basket Cheese", "Bath Cheese", "Bavarian Bergkase",
        "Baylough", "Beaufort", "Beauvoorde", "Beenleigh Blue", "Beer Cheese", "Bel Paese",
        "Bergader", "Bergere Bleue", "Berkswell", "Beyaz Peynir", "Bierkase", "Bishop Kennedy",
        "Blarney", "Bleu d'Auvergne", "Bleu de Gex", "Bleu de Laqueuille",
        "Bleu de Septmoncel", "Bleu Des Causses", "Blue", "Blue Castello", "Blue Rathgore",
        "Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini", "Bocconcini (Australian)"
)

```

### <a></a><a id="_AdapterViewHolder_186" target="_blank"></a>③ 创建 Adapter 和 ViewHolder

这一步就很简单了，就像往常一样，我们创建一个 item 的 layout 布局文件 (已省略，就是一个 TextView 用于显示 Student 的 name)，同时创建对应的 ViewHolder:

```
class StudentViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.student_item, parent, false)) {

    private val nameView = itemView.findViewById<TextView>(R.id.name)
    var student: Student? = null

    fun bindTo(student: Student?) {
        this.student = student
        nameView.text = student?.name
    }
}

```

我们的 Adapter 需要继承 PagedListAdapter 类：

```
class StudentAdapter : PagedListAdapter<Student, StudentViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder =
            StudentViewHolder(parent)

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Student>() {
            override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
                    oldItem == newItem
        }
    }
}

```

### <a></a><a id="__228" target="_blank"></a>④ 展示在界面上

我们创建一个 ViewModel, 它用于承载 **与 UI 无关** 业务代码：

```
class MainViewModel(app: Application) : AndroidViewModel(app) {

    val dao = StudentDb.get(app).studentDao()

    val allStudents = LivePagedListBuilder(dao.getAllStudent(), PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)                         //配置分页加载的数量
            .setEnablePlaceholders(ENABLE_PLACEHOLDERS)     //配置是否启动PlaceHolders
            .setInitialLoadSizeHint(PAGE_SIZE)              //初始化加载的数量
            .build()).build()

    companion object {
        private const val PAGE_SIZE = 15

        private const val ENABLE_PLACEHOLDERS = false
    }
}

```

最终，在 Activity 中，每当观察到数据源中 **数据的变化**，我们就把最新的数据交给 Adapter 去 **展示**：

```
class MainActivity : AppCompatActivity() {

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(application) as T
        }).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = StudentAdapter()
        recyclerView.adapter = adapter
        // 将数据的变化反映到UI上
        viewModel.allStudents.observe(this, Observer { adapter.submitList(it) })
    }
}

```

> 到这里，**Paging** 最基本的使用就已经讲解完毕了。您可以通过运行预览和示例 **基本一致** 的效果，如果有疑问，可以[点我查看源码](https://github.com/qingmei2/SamplePaging) 。

## <a></a><a id="_276" target="_blank"></a>从入门到放弃？

阅读到这里，我相信不少朋友会有这样一个想法—— **这个库看起来感觉好麻烦，我为什么要用它呢？**

![](https://upload-images.jianshu.io/upload_images/7293029-2b1864ee1583eddf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我曾经写过一篇标题很浮夸的博客：**[0 行 Java 代码实现 RecyclerView](https://www.jianshu.com/p/c69b0e4e18f1)**—— 文中我提出了一种使用 **DataBinding** 不需要哪怕一行 Java 代码就能实现列表 / 多类型列表的方式，但是最后我也提到了，这只是一种思路，这种简单的方式背后，可能会隐藏着 **严重耦合** 的情况—— **“一行代码实现 XXX”** 的库屡见不鲜，它们看上去很 **简单** ，但是真正做到 **灵活，松耦合** 的库寥寥无几，我认为这种方式是有缺陷的。

因此，简单并不意味着设计思想的优秀，**“看起来很麻烦”** 也不能成为否认 **Paging** 的理由，本文不会去阐述 **Paging** 在实际项目中应该怎么用，且不说代码正确性与否，这种做法本身就会固定一个人的思维。但如果你理解了 **Paging 本身原理** 的话，相信**掌握其用法** 也就不在话下了。

## <a></a><a id="Paging_287" target="_blank"></a>Paging 原理详解

先上一张图

![](http://upload-images.jianshu.io/upload_images/7293029-27facf0a399c66b8.gif?imageMogr2/auto-orient/strip)

这是官方提供的非常棒的原理示意图，简单概括一下：

*   DataSource： **数据源**，数据的改变会驱动列表的更新，因此，数据源是很重要的
*   PageList： **核心类**，它从数据源取出数据，同时，它负责控制 **第一次默认加载多少数据，之后每一次加载多少数据，如何加载**等等，并将数据的变更反映到 UI 上。
*   PagedListAdapter: **适配器**，RecyclerView 的适配器，通过分析数据是否发生了改变，负责处理 UI 展示的逻辑（增加 / 删除 / 替换等）。

### <a></a><a id="1_299" target="_blank"></a>1\. 创建数据源

我们思考一个问题，将数据作为列表展示在界面上，我们首先需要什么。

**数据源**，是的，在 Paging 中，它被抽象为 **DataSource** , 其获取需要依靠 **DataSource** 的内部工厂类 **DataSource.Factory** ，通过 create() 方法就可以获得 **DataSource** 的实例：

```
public abstract static class Factory<Key, Value> {
     public abstract DataSource<Key, Value> create();
}

```

数据源一般有两种选择，**远程服务器请求** 或者 **读取本地持久化数据**——这些并不重要，本文我们以 Room 数据库为例:

```
@Dao
interface StudentDao {

    @Query("SELECT * FROM Student ORDER BY name COLLATE NOCASE ASC")
    fun getAllStudent(): DataSource.Factory<Int, Student>
}

```

**Paging** 可以获得 **Room** 的原生支持，因此作为示例非常合适，当然我们更多获取 **数据源** 是通过 **API 网络请求**，其实现方式可以参考[官方 Sample](https://github.com/qingmei2/android-architecture-components/tree/master/PagingWithNetworkSample), 本文不赘述。

现在我们创建好了 StudentDao, 接下来就是展示 UI 了，在那之前，我们需要配置好 PageList。

### <a></a><a id="2PageList_326" target="_blank"></a>2\. 配置 PageList

上文我说到了 PageList 的作用：

*   1\. 从数据源取出数据
*   2\. 负责控制 **第一次默认加载多少数据，之后每一次加载多少数据，如何加载** 等等
*   3\. 将数据的变更反映到 UI 上。

我们仔细想想，这是有必要配置的，因此我们需要初始化 PageList:

```
 val allStudents = LivePagedListBuilder(dao.getAllStudent(), PagedList.Config.Builder()
            .setPageSize(15)                         //配置分页加载的数量
            .setEnablePlaceholders(false)     //配置是否启动PlaceHolders
            .setInitialLoadSizeHint(30)              //初始化加载的数量
            .build()).build()

```

我们按照上面分的三个职责来讲：

*   **从数据源取出数据**

很显然，这对应的是 dao.getAllStudent() ，通过数据库取得最新数据，如果是网络请求，也应该对应 API 的请求方法，返回值应该是 DataSource.Factory 类型。

*   **进行相关配置**

PageList 提供了 **PagedList.Config** 类供我们进行实例化配置，其提供了 4 个可选配置：

```
 public static final class Builder {
            //  省略其他Builder内部方法 
            private int mPageSize = -1;    //每次加载多少数据
            private int mPrefetchDistance = -1;   //距底部还有几条数据时，加载下一页数据
            private int mInitialLoadSizeHint = -1; //第一次加载多少数据
            private boolean mEnablePlaceholders = true; //是否启用占位符，若为true，则视为固定数量的item
}

```

*   **将变更反映到 UI 上**
    这个指的是 **LivePagedListBuilder**，而不是 **PagedList.Config.Builder**，它可以设置 **获取数据源的线程** 和 **边界 Callback**, 但是一般来讲可以不用配置，大家了解一下即可。

经过以上操作，我们的 PageList 设置好了，接下来就可以配置 UI 相关了。

### <a></a><a id="3Adapter_368" target="_blank"></a>3\. 配置 Adapter

就像我们平时配置 **RecyclerView** 差不多，我们配置了 ViewHolder 和 RecyclerView.Adapter，略微不同的是，我们需要继承 PagedListAdapter：

```
class StudentAdapter : PagedListAdapter<Student, StudentViewHolder>(diffCallback) {
    //省略 onBindViewHolder() && onCreateViewHolder()  
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Student>() {
            override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
                    oldItem == newItem
        }
    }
}

```

当然我们还需要传一个 **DifffUtil.ItemCallback** 的实例，这里需要对数据源返回的数据进行了比较处理, 它的意义是——我需要知道怎么样的比较，才意味着数据源的变化，并根据变化再进行的 UI 刷新操作。

ViewHolder 的代码正常实现即可，不再赘述。

### <a></a><a id="4UI_390" target="_blank"></a>4\. 监听数据源的变更，并响应在 UI 上

这个就很简单了，我们在 Activity 中声明即可：

```
val adapter = StudentAdapter()
recyclerView.adapter = adapter

viewModel.allStudents.observe(this, Observer { adapter.submitList(it) })

```

这样，每当数据源发生改变，Adapter 就会自动将 **新的数据** 动态反映在 UI 上。

## <a></a><a id="_404" target="_blank"></a>分页库的设计美学

现在，我简单了解了它的原理，但是还不是很够—— 正如我前言所说的，从别人的 **代码设计和思想** 中 **取长补短，化为己用** ，这才是我的目的。

让我们回到最初的问题：

> 看起来很麻烦，那么我为什么用这个库?

我会有这种想法，我为什么不能把所有功能都封装到一个 RecyclerView 的 Adapter 里面呢，它包含 **下拉刷新**，**上拉加载分页** 等等功能。

原因很简单，因为这样做会将 **业务层代码** 和 **UI 层** 混在一起造 **耦合** ，最直接就导致了 **难以通过代码进行单元测试**。

**UI 层** 和 **业务层** 代码的隔离是优秀的设计，这样更便于 **测试** ，我们可以从 Google 官方文档的目录结构中看到这一点：

![](https://upload-images.jianshu.io/upload_images/7293029-a8aa7ac73a2e4356.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

接下来，我会尝试站在设计者的角度，尝试去理解 **Paging** 如此设计的原因。

### <a></a><a id="1PagedListAdapterRecyclerView_423" target="_blank"></a>1.PagedListAdapter：基于 RecyclerView 的封装

将分页数据作为 List 展示在界面上，RecyclerView 是首选，那么实现一个对应的 **PagedListAdapter** 当然是不错的选择。

Google 对 **PagedListAdapter** 的职责定义的很简单，仅仅是一个被代理的对象而已，所有相关的数据处理逻辑都委托给了 **AsyncPagedListDiffer**：

```
public abstract class PagedListAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected PagedListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        mDiffer = new AsyncPagedListDiffer<>(this, diffCallback);
        mDiffer.mListener = mListener;
    }

    public void submitList(PagedList<T> pagedList) {
        mDiffer.submitList(pagedList);
    }

    protected T getItem(int position) {
        return mDiffer.getItem(position);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getItemCount();
    }

   public PagedList<T> getCurrentList() {
        return mDiffer.getCurrentList();
    }
}

```

当数据源发生改变时，实际上会通知 **AsyncPagedListDiffer** 的 **submitList()** 方法通知其内部保存的 **PagedList** 更新并反映在 UI 上：

```
//实际上内部存储了要展示在UI上的数据源PagedList<T>
public class AsyncPagedListDiffer<T> {
    //省略大量代码
    private PagedList<T> mPagedList;
    private PagedList<T> mSnapshot;
}

```

篇幅所限，我们不讨论数据是如何展示的（答案很简单，就是通过 RecyclerView.Adapter 的 notifyItemChange() 相关方法），我们有一个问题更需要去关注：

> Paging **未滑到底部便开始加载数据的逻辑** 在哪里？

如果你认真思考，你应该能想到正确的答案，在 **getItem()** 方法中执行。

```
public T getItem(int index) {
        //省略部分代码
        mPagedList.loadAround(index);  //如果需要，请求更多数据
        return mPagedList.get(index);  //返回Item数据
}

```

每当 RecyclerView 要展示一个新的 Item 时，理所应当的会通过 **getItem()** 方法获取相应的数据，既然如此，为何不在返回最新数据之前，判断当前的数据源是否需要 **加载下一页数据** 呢？

### <a></a><a id="2PagedList__482" target="_blank"></a>2\. 抽象类 PagedList: 设计模式的组合美学

我们来看抽象类 PagedList.loadAround(index) 方法：

```
    public void loadAround(int index) {
        mLastLoad = index + getPositionOffset();
        loadAroundInternal(index);

        mLowestIndexAccessed = Math.min(mLowestIndexAccessed, index);
        mHighestIndexAccessed = Math.max(mHighestIndexAccessed, index);
        tryDispatchBoundaryCallbacks(true);
    }
    //这个方法是抽象的
    abstract void loadAroundInternal(int index);

```

需要再次重复的是，即使是 PagedList, 也有很多种不同的 **数据分页策略**:

![](https://upload-images.jianshu.io/upload_images/7293029-d4f9de0a11253aaa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这些不同的 **PagedList** 在处理分页逻辑上，可能有不同的逻辑，那么，作为设计者，应该做到的是，**把异同的逻辑抽象出来交给子类实现（即 loadAroundInternal 方法），而把公共的处理逻辑暴漏出来**，并向上转型交给 Adapter（实际上是 **AsyncPagedListDiffer**）去执行分页加载的 API，也就是 loadAround 方法。

好处显而易见，对于 Adapter 来说，它只需要知道，在我需要请求分页数据时，调用 PagedList 的 loadAround 方法即可，至于 **是 PagedList 的哪个子类，内部执行什么样的分页逻辑，Adapter 并不关心**。

这些 PagedList 的不同策略的逻辑，是在 PagedList.create() 方法中进行的处理：

```
    private static <K, T> PagedList<T> create(@NonNull DataSource<K, T> dataSource,
            @NonNull Executor notifyExecutor,
            @NonNull Executor fetchExecutor,
            @Nullable BoundaryCallback<T> boundaryCallback,
            @NonNull Config config,
            @Nullable K key) {
        if (dataSource.isContiguous() || !config.enablePlaceholders) {
            //省略其他代码
            //返回ContiguousPagedList
            return new ContiguousPagedList<>(contigDataSource,    
                    notifyExecutor,
                    fetchExecutor,
                    boundaryCallback,
                    config,
                    key,
                    lastLoad);
        } else {
            //返回TiledPagedList
            return new TiledPagedList<>((PositionalDataSource<T>) dataSource,
                    notifyExecutor,
                    fetchExecutor,
                    boundaryCallback,
                    config,
                    (key != null) ? (Integer) key : 0);
        }
    }

```

**PagedList** 是一个抽象类，实际上它的作用是 **通过 Builder 实例化 PagedList 真正的对象**：

![](https://upload-images.jianshu.io/upload_images/7293029-fd3d43a78392fe85.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

通过 Builder.build() 调用 create() 方法，决定实例化哪个 PagedList 的子类：

```
        public PagedList<Value> build() {
            return PagedList.create(
                    mDataSource,
                    mNotifyExecutor,
                    mFetchExecutor,
                    mBoundaryCallback,
                    mConfig,
                    mInitialKey);
        }

```

Builder 模式是非常耳熟能详的设计模式，它的好处是作为 API 的门面，便于开发者更简单上手并进行对应的配置。

不同的 PagedList 对应不同的 DataSource，比如：

```
class ContiguousPagedList<K, V> extends PagedList<V> implements PagedStorage.Callback {

    ContiguousPagedList(
            //请注意这行，ContiguousPagedList内部需要ContiguousDataSource
            @NonNull ContiguousDataSource<K, V> dataSource,
            @NonNull Executor mainThreadExecutor,
            @NonNull Executor backgroundThreadExecutor,
            @Nullable BoundaryCallback<V> boundaryCallback,
            @NonNull Config config,
            final @Nullable K key,
            int lastLoad) {
            //.....
    }

abstract class ContiguousDataSource<Key, Value> extends DataSource<Key, Value> {
      //......
}

class TiledPagedList<T> extends PagedList<T> implements PagedStorage.Callback {

    TiledPagedList(
            //请注意这行，TiledPagedList内部需要PositionalDataSource
            @NonNull PositionalDataSource<T> dataSource,
            @NonNull Executor mainThreadExecutor,
            @NonNull Executor backgroundThreadExecutor,
            @Nullable BoundaryCallback<T> boundaryCallback,
            @NonNull Config config,
            int position) {
           //......
    }
}

public abstract class PositionalDataSource<T> extends DataSource<Integer, T> {
    //......
}

```

回到 create() 方法中，我们看到 dataSource 此时也仅仅是接口类型的声明：

```
private static <K, T> PagedList<T> create(
            //其实这时候dataSource只是作为DataSource类型的声明
            @NonNull DataSource<K, T> dataSource,
            @NonNull Executor notifyExecutor,
            @NonNull Executor fetchExecutor,
            @Nullable BoundaryCallback<T> boundaryCallback,
            @NonNull Config config,
            @Nullable K key) {
}

```

实际上，create 方法的作用是，通过将不同的 DataSource, 作为依赖实例化对应的 PagedList，除此之外，还有对 DataSource 的对应处理，或者 Wrapper（再次包装，详情请参考源码的 create 方法，篇幅所限本文不再叙述）。

这个过程中，通过 Builder，将 **多种数据源（DataSource）**，**多种分页策略（PagedList）** 互相进行组合，并 **向上转型** 交给 **适配器（Adapter）** ，然后 Adapter 将对应的功能 **委托** 给了 **代理类的 AsyncPagedListDiffer** 处理——这之间通过数种设计模式的组合，最终展现给开发者的是一个 **简单且清晰** 的 API 接口去调用，其设计的精妙程度，远非笔者这种普通的开发者所能企及。

### <a></a><a id="3_617" target="_blank"></a>3\. 更多

实际上，笔者上文所叙述的内容只是 **Paging** 的冰山一角，其源码中，还有很多很值得学习的优秀思想，本文无法一一列举，比如 **线程的切换**（加载分页数据应该在 io 线程，而反映在界面上时则应该在 ui 线程），再比如库 **对多种响应式数据类型的支持（LiveData，RxJava）**, 这些实用的功能实现，都通过 **Paging** 优秀的设计，将其复杂的实现封装了起来，而将简单的 API 暴露给开发者调用，有兴趣的朋友可以去研究一下。

## <a></a><a id="_621" target="_blank"></a>小结 && 吐槽

本文的发布也算是 **历经坎坷**—— 初稿完成时间为 6 月初，但拖了一个月才发出来，原因就是本文写好之后，本来准备第二天发表，结果第二天早上一起来，发现 **这篇花费了 3 天晚上总结的文章，竟然没有能成功保存！**

我自认为我内心还算比较强大，但还是差点崩溃，写过博客的同学都知道，这样一篇 6k + 的博客，所需要耗费很多心血才能完成。

我重新整理了思路写了一遍——收获是有的，再一次翻阅源码的同时，我又加深了对 Paging 设计思想的理解，这个过程很痛苦，但是也值得。

当然，即使如此，笔者水平有限，也难免文中内容有理解错误之处，也希望能有朋友不吝赐教，共同讨论一起进步。

**--------------------------------------------------- 广告分割线 -------------------------------------------------------**

## <a></a><a id="_632" target="_blank"></a>关于我

Hello，我是[却把清梅嗅](https://github.com/qingmei2)，如果您觉得文章对您有价值，欢迎 ❤️，也欢迎关注我的[博客](https://www.jianshu.com/u/df76f81fe3ff)或者 [Github](https://github.com/qingmei2)。

如果您觉得文章还差了那么点东西，也请通过**关注**督促我写出更好的文章——万一哪天我进步了呢？

*   [我的 Android 学习体系](https://github.com/qingmei2/android-programming-profile)
*   [关于文章纠错](https://github.com/qingmei2/Programming-life/blob/master/error_collection.md)
*   [关于知识付费](https://github.com/qingmei2/Programming-life/blob/master/appreciation.md)

<link href="https://csdnimg.cn/release/phoenix/mdeditor/markdown_views-7b4cdcb592.css" rel="stylesheet">