> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/Vt__FGCcWftanYW5ndyXmA

<section class="" data-source="bj.96weixin.com">

<section>

<section class="">

<section class="" data-source="bj.96weixin.com" style="white-space: normal;">

<section style="margin: 5px auto 1.3em;">

<section class="" style="padding: 8px 10px;color: rgb(255, 255, 255);box-sizing: border-box;display: inline-block;vertical-align: middle;background-color: rgb(89, 195, 249);">

本文作者

</section>

</section>

</section>

</section>

</section>

</section>

作者：**唯鹿**

链接：

https://blog.csdn.net/qq_17766199/article/details/83147483

本文由作者授权发布。

RecyclerView 的强大无人不知，它封装了 ViewHolder，便于我们回收复用；配合 LayoutManager、ItemDecoration、ItemAnimator 便于你制定各种列表效果。当然可能还有一些 “遗珠” 你不太了解，今天就说说它们。

<section style="margin-top: 10px;margin-bottom: 10px;white-space: normal;text-align: center;">

<section class="" style="padding-top: 4px;padding-right: 10px;padding-bottom: 4px;border-top: 2px solid rgb(89, 195, 249);border-bottom: 2px solid rgb(89, 195, 249);display: inline-block;border-right-color: rgb(89, 195, 249);border-left-color: rgb(89, 195, 249);">

<section style="margin-top: -8px;display: inline-block;float: left;width: 60px;background-color: rgb(254, 254, 254);">

<section style="display: table;width: 60px;">

<section style="display: table-cell;line-height: 1em;">_1_</section>

</section>

</section>

SortedList</section>

</section>

顾名思义就是排序列表，它适用于列表有序且不重复的场景。并且 SortedList 会帮助你比较数据的差异，定向刷新数据。而不是简单粗暴的 notifyDataSetChanged()。

我想到了一个场景，在选择城市页面，我们都需要根据拼音首字母来排序。我们来使用 SortedList 来实现一下。

City 对象：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class City {    private int id;    private String cityName;    private String firstLetter;    public City(int id, String cityName, String firstLetter) {        this.id = id;        this.cityName = cityName;        this.firstLetter = firstLetter;    }}
```

</section>

创建 SortedListAdapterCallback 的实现类 SortedListCallback，SortedListCallback 定义了如何排序和如何判断重复项。

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class SortedListCallback extends SortedListAdapterCallback<City> {    public SortedListCallback(RecyclerView.Adapter adapter) {        super(adapter);    }    /**     * 排序条件     */    @Override    public int compare(City o1, City o2) {        return o1.getFirstLetter().compareTo(o2.getFirstLetter());    }    /**     * 用来判断两个对象是否是相同的Item。     */    @Override    public boolean areItemsTheSame(City item1, City item2) {        return item1.getId() == item2.getId();    }    /**     * 用来判断两个对象是否是内容的Item。     */    @Override    public boolean areContentsTheSame(City oldItem, City newItem) {        if (oldItem.getId() != newItem.getId()) {            return false;        }        return oldItem.getCityName().equals(newItem.getCityName());    }}
```

</section>

Adapter 部分

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class SortedAdapter extends RecyclerView.Adapter<SortedAdapter.ViewHolder> {    // 数据源使用SortedList    private SortedList<City> mSortedList;    private LayoutInflater mInflater;    public SortedAdapter(Context mContext) {        mInflater = LayoutInflater.from(mContext);    }    public void setSortedList(SortedList<City> mSortedList) {        this.mSortedList = mSortedList;    }    /**     * 批量更新操作，例如：     * <pre>     *     mSortedList.beginBatchedUpdates();     *     try {     *         mSortedList.add(item1)     *         mSortedList.add(item2)     *         mSortedList.remove(item3)     *         ...     *     } finally {     *         mSortedList.endBatchedUpdates();     *     }     * </pre>    * */    public void setData(List<City> mData){        mSortedList.beginBatchedUpdates();        mSortedList.addAll(mData);        mSortedList.endBatchedUpdates();    }    public void removeData(int index){        mSortedList.removeItemAt(index);    }    public void clear(){        mSortedList.clear();    }    @Override    @NonNull    public SortedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {        return new ViewHolder(mInflater.inflate(R.layout.item_test, parent, false));    }    @Override    public void onBindViewHolder(@NonNull SortedAdapter.ViewHolder holder, final int position) {       ...    }    @Override    public int getItemCount() {        return mSortedList.size();    }    ...}
```

</section>

使用部分：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class SortedListActivity extends AppCompatActivity {    private SortedAdapter mSortedAdapter;    private int count = 10;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_sorted_list);        RecyclerView mRecyclerView = findViewById(R.id.rv);        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));        mSortedAdapter = new SortedAdapter(this);        // SortedList初始化        SortedListCallback mSortedListCallback = new SortedListCallback(mSortedAdapter);        SortedList mSortedList = new SortedList<>(City.class, mSortedListCallback);        mSortedAdapter.setSortedList(mSortedList);        mRecyclerView.setAdapter(mSortedAdapter);        updateData();    }    private void addData() {        mSortedAdapter.setData(new City(count, "城市 " + count, "c"));        count ++;    }    private List<City> mList = new ArrayList();    private void updateData() {        mList.clear();        mList.add(new City(0, "北京", "b"));        mList.add(new City(1, "上海", "s"));        mList.add(new City(2, "广州", "g"));        mList.add(new City(3, "深圳", "s"));        mList.add(new City(4, "杭州", "h"));        mList.add(new City(5, "西安", "x"));        mList.add(new City(6, "成都", "c"));        mList.add(new City(7, "武汉", "w"));        mList.add(new City(8, "南京", "n"));        mList.add(new City(9, "重庆", "c"));        mSortedAdapter.setData(mList);    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        getMenuInflater().inflate(R.menu.menu, menu);        return true;    }    private Random mRandom = new Random();    @Override    public boolean onOptionsItemSelected(MenuItem item) {        int i = item.getItemId();        if (i == R.id.menu_add) {            addData();        } else if (i == R.id.menu_update) {            // 修改，自动去重            updateData();        } else if (i == R.id.menu_delete) {            // 随意删除一个            if (mSortedAdapter.getItemCount() > 0){                mSortedAdapter.removeData(mRandom.nextInt(mSortedAdapter.getItemCount()));            }        }else if (i == R.id.menu_clear){            mSortedAdapter.clear();        }        return true;    }}
```

</section>

使用起来还是很简单的，来看一下效果图：

![](https://mmbiz.qpic.cn/mmbiz_gif/MOu2ZNAwZwNmJ2CRvrQ0KKia13pzW44UWEsyLS4botZIgjMe6r3UFZ1tNL3bfPI3pgDic2oEuGsSsfGjGBKjBcQA/640?wx_fmt=gif)

可以看到，我每次添加一条 c 字母的数据，它会自动帮我排序好，同时刷新列表。修改数据时，自动去重。比起暴力刷新，优雅多了。

<section style="margin-top: 10px;margin-bottom: 10px;white-space: normal;text-align: center;">

<section class="" style="padding-top: 4px;padding-right: 10px;padding-bottom: 4px;border-top: 2px solid rgb(89, 195, 249);border-bottom: 2px solid rgb(89, 195, 249);display: inline-block;border-right-color: rgb(89, 195, 249);border-left-color: rgb(89, 195, 249);">

<section style="margin-top: -8px;display: inline-block;float: left;width: 60px;background-color: rgb(254, 254, 254);">

<section style="display: table;width: 60px;">

<section style="display: table-cell;line-height: 1em;">_2_</section>

</section>

</section>

AsyncListUtil</section>

</section>

AsyncListUtil 在 support-v7:23 就存在了。它是异步加载数据的工具，它一般用于加载数据库数据，我们无需在 UI 线程上查询游标，同时它可以保持 UI 和缓存同步，并且始终只在内存中保留有限数量的数据。使用它可以获得更好的用户体验。

注意，这个类使用单个线程来加载数据，因此它适合从磁盘、数据库加载数据，不适用于从网络加载数据。

用法如下，首先实现 AsyncListUtil：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class MyAsyncListUtil extends AsyncListUtil<TestBean> {    /**     * 一次加载数据的个数，分页数量     */    private static final int TILE_SIZE = 20;    public MyAsyncListUtil(RecyclerView mRecyclerView) {        super(TestBean.class, TILE_SIZE, new MyDataCallback(), new MyViewCallback(mRecyclerView));        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {            @Override            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {                super.onScrolled(recyclerView, dx, dy);                // 更新当前可见范围数据                onRangeChanged();            }        });    }    /**     * 获取数据回调     */    public static class MyDataCallback extends DataCallback<TestBean>{        /**         * 总数据个数         */        @Override        public int refreshData() {            return 200;        }        /**         * 填充数据（后台线程），一般为读取数据库数据         */        @Override        public void fillData(@NonNull TestBean[] data, int startPosition, int itemCount) {            for (int i = 0; i < itemCount; i++) {                TestBean item = data[i];                if (item == null) {                    item = new TestBean(startPosition, "Item：" + (startPosition + i));                    data[i] = item;                }            }            try {                // 模拟加载数据中                Thread.sleep(500);            } catch (InterruptedException e) {                e.printStackTrace();            }        }    }    /**     * 用于获取可见项范围和更新通知的回调     */    public static class MyViewCallback extends ViewCallback {        private RecyclerView mRecyclerView;        public MyViewCallback(RecyclerView mRecyclerView) {            this.mRecyclerView = mRecyclerView;        }        /**         * 展示数据的范围         */        @Override        public void getItemRangeInto(@NonNull int[] outRange) {            RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();            LinearLayoutManager mgr = (LinearLayoutManager) manager;            outRange[0] = mgr.findFirstVisibleItemPosition();            outRange[1] = mgr.findLastVisibleItemPosition();        }        /**         * 刷新数据         */        @Override        public void onDataRefresh() {            mRecyclerView.getAdapter().notifyDataSetChanged();        }        /**         * Item更新         */        @Override        public void onItemLoaded(int position) {            mRecyclerView.getAdapter().notifyItemChanged(position);        }    }}
```

</section>

Adapter：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class AsyncListUtilAdapter extends RecyclerView.Adapter<AsyncListUtilAdapter.ViewHolder> {    private MyAsyncListUtil mMyAsyncListUtil;    public AsyncListUtilAdapter(Context mContext, MyAsyncListUtil mMyAsyncListUtil) {        this.mMyAsyncListUtil = mMyAsyncListUtil;    }    @Override    public int getItemCount() {        return mMyAsyncListUtil.getItemCount();    }    @Override    public void onBindViewHolder(@NonNull AsyncListUtilAdapter.ViewHolder holder, final int position) {        TestBean bean = mMyAsyncListUtil.getItem(position);        // 有可能获取为空，这是可以显示加载中，等待同步数据。        if (bean == null){            holder.mTvName.setText("加载中...");        }else {            holder.mTvName.setText(bean.getName());        }    }    ......}
```

</section>

注释还是很清楚的，直接上效果图：

![](https://mmbiz.qpic.cn/mmbiz_gif/MOu2ZNAwZwNmJ2CRvrQ0KKia13pzW44UWj6H2dn7ZrbTLI5d9qSDbUfqTyX4ajOShWNMm0xGT8lCP5cBl66utFw/640?wx_fmt=gif)

<section style="margin-top: 10px;margin-bottom: 10px;white-space: normal;text-align: center;">

<section class="" style="padding-top: 4px;padding-right: 10px;padding-bottom: 4px;border-top: 2px solid rgb(89, 195, 249);border-bottom: 2px solid rgb(89, 195, 249);display: inline-block;border-right-color: rgb(89, 195, 249);border-left-color: rgb(89, 195, 249);">

<section style="margin-top: -8px;display: inline-block;float: left;width: 60px;background-color: rgb(254, 254, 254);">

<section style="display: table;width: 60px;">

<section style="display: table-cell;line-height: 1em;">_3_</section>

</section>

</section>

AsyncListDiffer</section>

</section>

虽然 SortedList、AsyncListUtil 很方便了，但是大多数的列表都无需我们排序和加载本地数据，大多是获取网络数据展示。这个时候就可以使用 DiffUtil 了。DiffUtil 是 support-v7:24.2.0 中的新工具类，它用来比较新旧两个数据集，寻找最小变化量，定向刷新列表。关于 DiffUtil 的介绍很早之前在张旭童的【Android】RecyclerView 的好伴侣：详解 DiffUtil 博客中就有详细介绍，我这里就不赘述了。

_https://blog.csdn.net/zxt0601/article/details/52562770_

不过 DiffUtil 的问题在于计算数据差异 DiffUtil.calculateDiff(mDiffCallback) 时是一个耗时操作，需要我们放到子线程去处理，最后在主线程刷新。为了方便这一操作，在 support-v7:27.1.0 又新增了一个 DiffUtil 的封装类，那就是 AsyncListDiffer。

首先先上效果图，一个简单的列表展示，同时增、删、改操作。

![](https://mmbiz.qpic.cn/mmbiz_gif/MOu2ZNAwZwNmJ2CRvrQ0KKia13pzW44UWPFF1JPYBFpc4xvLIpAGwmRqgrshfG8sEW9SmByV8ic58AwnFJXCZ2qQ/640?wx_fmt=gif)

我用 AsyncListDiffer 来实现这一效果。首先实现 DiffUtil.ItemCallback，类似 SortedList，制定规则，如何区分数据。这里和 DiffUtil 用法几乎一样，只是它是实现 DiffUtil.Callback。

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class MyDiffUtilItemCallback extends DiffUtil.ItemCallback<TestBean> {     /**     * 是否是同一个对象     */      @Override    public boolean areItemsTheSame(@NonNull TestBean oldItem, @NonNull TestBean newItem) {        return oldItem.getId() == newItem.getId();    }     /**     * 是否是相同内容     */     @Override    public boolean areContentsTheSame(@NonNull TestBean oldItem, @NonNull TestBean newItem) {        return oldItem.getName().equals(newItem.getName());    }    /**     * areItemsTheSame()返回true而areContentsTheSame()返回false时调用,也就是说两个对象代表的数据是一条，但是内容更新了。此方法为定向刷新使用，可选。     */    @Nullable    @Override    public Object getChangePayload(@NonNull TestBean oldItem, @NonNull TestBean newItem) {        Bundle payload = new Bundle();        if (!oldItem.getName().equals(newItem.getName())) {            payload.putString("KEY_NAME", newItem.getName());        }        if (payload.size() == 0){            //如果没有变化 就传空            return null;        }        return payload;    }}
```

</section>

Adapter 部分有两种实现方法，一种是实现 RecyclerView.Adapter，

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class AsyncListDifferAdapter extends RecyclerView.Adapter<AsyncListDifferAdapter.ViewHolder> {    private LayoutInflater mInflater;    // 数据的操作由AsyncListDiffer实现    private AsyncListDiffer<TestBean> mDiffer;    public AsyncListDifferAdapter(Context mContext) {        // 初始化AsyncListDiffe        mDiffer = new AsyncListDiffer<>(this, new MyDiffUtilItemCallback());        mInflater = LayoutInflater.from(mContext);    }    public void setData(TestBean mData){        List<TestBean> mList = new ArrayList<>();        mList.addAll(mDiffer.getCurrentList());        mList.add(mData);        mDiffer.submitList(mList);    }    public void setData(List<TestBean> mData){        // 由于DiffUtil是对比新旧数据，所以需要创建新的集合来存放新数据。        // 实际情况下，每次都是重新获取的新数据，所以无需这步。        List<TestBean> mList = new ArrayList<>();        mList.addAll(mData);        mDiffer.submitList(mList);    }    public void removeData(int index){        List<TestBean> mList = new ArrayList<>();        mList.addAll(mDiffer.getCurrentList());        mList.remove(index);        mDiffer.submitList(mList);    }    public void clear(){        mDiffer.submitList(null);    }    @Override    @NonNull    public AsyncListDifferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {        return new ViewHolder(mInflater.inflate(R.layout.item_test, parent, false));    }    @Override    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {        if (payloads.isEmpty()) {            onBindViewHolder(holder, position);        } else {            Bundle bundle = (Bundle) payloads.get(0);            holder.mTvName.setText(bundle.getString("KEY_NAME"));        }    }    @Override    public void onBindViewHolder(@NonNull AsyncListDifferAdapter.ViewHolder holder, final int position) {        TestBean bean = mDiffer.getCurrentList().get(position);        holder.mTvName.setText(bean.getName());    }    @Override    public int getItemCount() {        return mDiffer.getCurrentList().size();    }    static class ViewHolder extends RecyclerView.ViewHolder {       ......    }}
```

</section>

另一种 Adapter 写法可以实现 ListAdapter，它的内部帮我们实现了 getItemCount()、getItem() 和 AsyncListDiffer 的初始化。

源码如下，很简单：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public abstract class ListAdapter<T, VH extends ViewHolder> extends Adapter<VH> {    private final AsyncListDiffer<T> mHelper;    protected ListAdapter(@NonNull ItemCallback<T> diffCallback) {        this.mHelper = new AsyncListDiffer(new AdapterListUpdateCallback(this), (new Builder(diffCallback)).build());    }    protected ListAdapter(@NonNull AsyncDifferConfig<T> config) {        this.mHelper = new AsyncListDiffer(new AdapterListUpdateCallback(this), config);    }    public void submitList(@Nullable List<T> list) {        this.mHelper.submitList(list);    }    protected T getItem(int position) {        return this.mHelper.getCurrentList().get(position);    }    public int getItemCount() {        return this.mHelper.getCurrentList().size();    }}
```

</section>

不过有个缺点，没有提供直接获取当前集合的 getCurrentList() 方法。所以需要自己维护一个集合。希望以后可以添加上吧。所以现阶段我还是不推荐这种写法。。。不过我们可以去做这个封装。

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class MyListAdapter extends ListAdapter<TestBean, MyListAdapter.ViewHolder> {    private LayoutInflater mInflater;    // 自己维护的集合    private List<TestBean> mData = new ArrayList<>();    public MyListAdapter(Context mContext) {        super(new MyDiffUtilItemCallback());        mInflater = LayoutInflater.from(mContext);    }    public void setData(TestBean testBean){        mData.add(testBean);        List<TestBean> mList = new ArrayList<>();        mList.addAll(mData);        // 提交新的数据集        submitList(mList);    }    public void setData(List<TestBean> list){        mData.clear();        mData.addAll(list);        List<TestBean> mList = new ArrayList<>();        mList.addAll(mData);        submitList(mList);    }    public void removeData(int index){        mData.remove(index);        List<TestBean> mList = new ArrayList<>();        mList.addAll(mData);        submitList(mList);    }    public void clear(){        mData.clear();        submitList(null);    }    @Override    @NonNull    public MyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {        return new ViewHolder(mInflater.inflate(R.layout.item_test, parent, false));    }    @Override    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {        if (payloads.isEmpty()) {            onBindViewHolder(holder, position);        } else {            Bundle bundle = (Bundle) payloads.get(0);            holder.mTvName.setText(bundle.getString("KEY_NAME"));        }    }    @Override    public void onBindViewHolder(@NonNull MyListAdapter.ViewHolder holder, final int position) {        TestBean bean = getItem(position);        holder.mTvName.setText(bean.getName());    }    static class ViewHolder extends RecyclerView.ViewHolder {        ......    }}
```

</section>

最后就是 Activity 了：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public class AsyncListDifferActivity extends AppCompatActivity {    private AsyncListDifferAdapter mAsyncListDifferAdapter;    private int count = 10;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_sorted_list);        RecyclerView mRecyclerView = findViewById(R.id.rv);        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));        mAsyncListDifferAdapter = new AsyncListDifferAdapter(this);        mRecyclerView.setAdapter(mAsyncListDifferAdapter);        initData();    }    private void addData() {        mAsyncListDifferAdapter.setData(new TestBean(count, "Item " + count));        count ++;    }    private List<TestBean> mList = new ArrayList();    private void initData() {        mList.clear();        for (int i = 0; i < 10; i++){            mList.add(new TestBean(i, "Item " + i));        }        mAsyncListDifferAdapter.setData(mList);    }    private void updateData() {        mList.clear();        for (int i = 9; i >= 0; i--){            mList.add(new TestBean(i, "Item " + i));        }        mAsyncListDifferAdapter.setData(mList);    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        getMenuInflater().inflate(R.menu.menu, menu);        return true;    }    private Random mRandom = new Random();    @Override    public boolean onOptionsItemSelected(MenuItem item) {        int i = item.getItemId();        if (i == R.id.menu_add) {            addData();        } else if (i == R.id.menu_update) {            updateData();        } else if (i == R.id.menu_delete) {            if (mAsyncListDifferAdapter.getItemCount() > 0){                mAsyncListDifferAdapter.removeData(mRandom.nextInt(mAsyncListDifferAdapter.getItemCount()));            }        }else if (i == R.id.menu_clear){            mAsyncListDifferAdapter.clear();        }        return true;    }}
```

</section>

我们简单的看一下 AsyncListDiffer 的 submitList 源码：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
public void submitList(@Nullable final List<T> newList) {        final int runGeneration = ++this.mMaxScheduledGeneration;        if (newList != this.mList) {            if (newList == null) {                // 新数据为null时清空列表                int countRemoved = this.mList.size();                this.mList = null;                this.mReadOnlyList = Collections.emptyList();                this.mUpdateCallback.onRemoved(0, countRemoved);            } else if (this.mList == null) {                // 旧数据为null时添加数据                this.mList = newList;                this.mReadOnlyList = Collections.unmodifiableList(newList);                this.mUpdateCallback.onInserted(0, newList.size());            } else {                final List<T> oldList = this.mList;                // 计算数据差异放在子线程                this.mConfig.getBackgroundThreadExecutor().execute(new Runnable() {                    public void run() {                        final DiffResult result = DiffUtil.calculateDiff(new Callback() {                           ...                        });                        // 主线程刷新列表                        AsyncListDiffer.this.mMainThreadExecutor.execute(new Runnable() {                            public void run() {                                if (AsyncListDiffer.this.mMaxScheduledGeneration == runGeneration) {                                    AsyncListDiffer.this.latchList(newList, result);                                }                            }                        });                    }                });            }        }    }void latchList(@NonNull List<T> newList, @NonNull DiffResult diffResult) {     this.mList = newList;     this.mReadOnlyList = Collections.unmodifiableList(newList);     // 熟悉的dispatchUpdatesTo方法     diffResult.dispatchUpdatesTo(this.mUpdateCallback);}
```

</section>

AsyncListDiffer 就是在这里帮我们做了线程的处理。方便我们正确规范的使用。

<section style="margin-top: 10px;margin-bottom: 10px;white-space: normal;text-align: center;">

<section class="" style="padding-top: 4px;padding-right: 10px;padding-bottom: 4px;border-top: 2px solid rgb(89, 195, 249);border-bottom: 2px solid rgb(89, 195, 249);display: inline-block;border-right-color: rgb(89, 195, 249);border-left-color: rgb(89, 195, 249);">

<section style="margin-top: -8px;display: inline-block;float: left;width: 60px;background-color: rgb(254, 254, 254);">

<section style="display: table;width: 60px;">

<section style="display: table-cell;line-height: 1em;">_4_</section>

</section>

</section>

SnapHelper</section>

</section>

SnapHelper 是 support-v7:24.2.0 新增的，用于控制 RecyclerView 滑动停止后 Item 的对齐方式。默认提供了两种对齐方式 PagerSnapHelper 与 LinearSnapHelper。PagerSnapHelper 和 ViewPage 效果一样，一次滑动一页。

LinearSnapHelper 这是 Item 居中对齐。使用方式非常简单：

<section class="" style="font-size: 16px;color: rgb(62, 62, 62);line-height: 1.6;letter-spacing: 0px;">

```
PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper(); mPagerSnapHelper.attachToRecyclerView(mRecyclerView);
```

</section>

效果如下：

![](https://mmbiz.qpic.cn/mmbiz_gif/MOu2ZNAwZwNmJ2CRvrQ0KKia13pzW44UWcsYxnCuZaia23WzBIso8upYh5BycAgzw2NnsKiavxPGKIoYn4Kl5jictA/640?wx_fmt=gif)

当然我们可以自定义 SnapHelper，来实现我们想要的对齐方式，下面我们来实现一下左对齐。

> 注：长度超过了微信限制，自定义 SnapHelper 的代码可以通过原博客查看。

大家可以下载代码去体验。本篇所有代码已上传至 Github。希望点赞支持！！

_https://github.com/simplezhli/RecyclerViewExtensionsDemo_

**参考**

*   【Android】RecyclerView 的好伴侣：详解 DiffUtil

    _https://blog.csdn.net/zxt0601/article/details/52562770_

*   让你明明白白的使用 RecyclerView——SnapHelper 详解

    _https://www.jianshu.com/p/e54db232df62_

推荐阅读：

[Android 高斯模糊你所不知道的坑](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650826760&idx=1&sn=f7e9616bd41595bd83f2b00aec8d6aa4&chksm=80b7bc96b7c035805d1010c61928662a808fedef40e2ce4ff05ab98385908b166aa8e62d549d&scene=21#wechat_redirect)

[安卓 6.0 到 9.0 适配](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650826756&idx=1&sn=dd888498081cb61e9bdb7bc6ebcf3a55&chksm=80b7bc9ab7c0358c1e85150120ecdb3c3164670c8b27cecb0c16621667e229c929f65a6bbae6&scene=21#wechat_redirect)

[一些提高 Android 开发效率的经验和技巧](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650826723&idx=1&sn=e9a1a75b3c64d82d7b09f078421ec70f&chksm=80b7b37db7c03a6bc6c53eb3c7a9485b353265261a639365c7d695e4cff88804163e96276adf&scene=21#wechat_redirect)

![](https://mmbiz.qpic.cn/mmbiz_jpg/MOu2ZNAwZwP4yDt9RiaN89t9lxTz0vZWZy9sYR54YefTFFBPmPLwnAN9PNicI0rZznIYt4r2Q40DbAAiatTS1MlVw/640?wx_fmt=jpeg)

**扫一扫** 关注我的公众号

如果你想要跟大家分享你的文章，欢迎投稿~

┏(＾0＾)┛明天见！