# Kotlin和RecyclerView的一个demo

> Kotlin最近比较火,我简单学了一下,写了个小demo,RecyclerView的.

## 1.使用到的东西

- 语言:Kotlin [不会配置的点这里....滑稽](http://blog.csdn.net/xfhy_/article/details/76654797)
- RecyclerView
- OkHttp3
- RxJava
- RxAndroid
- Glide
- Gson

## 2.需要引入的库

	implementation 'com.android.support:recyclerview-v7:26.0.0'
    implementation 'com.squareup.okhttp3:okhttp:3.8.1'
    implementation 'io.reactivex.rxjava2:rxjava:2.1.2'
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
    implementation 'com.google.code.gson:gson:2.8.1'
    implementation 'com.github.bumptech.glide:glide:4.0.0'

## 3.首先来看一下RecyclerView子项的布局吧

**item_news.xml** 
就是一个ImageView和一个TextView

``` xml

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	              android:layout_width="match_parent"
	              android:layout_height="100dp"
	              android:background="#7dd9da"
	              android:gravity="center_vertical"
	              android:orientation="horizontal">
	
	    <ImageView
	        android:id="@+id/iv_news_des"
	        android:layout_width="80dp"
	        android:layout_height="80dp"
	        android:layout_marginStart="10dp"
	        android:scaleType="centerCrop"
	        android:src="@mipmap/ic_launcher"/>
	
	    <TextView
	        android:id="@+id/tv_news_title"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_marginStart="10dp"
	        android:ellipsize="end"
	        android:maxLines="1"
	        android:text="震惊...."
	        android:textColor="#AA000000"
	        android:textSize="20sp"
	        />
	
	</LinearLayout>

```

## 4.model,模型类

``` kotlin

	/**
	 * description：
	 * author feiyang
	 * create at 2017/8/3 17:37
	 */
	data class News(
	
	        @SerializedName("author_name")
	        var authorName: String?,
	        @SerializedName("category")
	        var category: String?,
	        @SerializedName("date")
	        var date: String?,
	        @SerializedName("thumbnail_pic_s")
	        var thumbnailPicS: String?,
	        @SerializedName("title")
	        var title: String?,
	        @SerializedName("uniquekey")
	        var uniquekey: String?,
	        @SerializedName("url")
	        var url: String?
	
	)

```

## 5.adapter(*重要)

``` kotlin

	/**
	 * description：RecyclerView的adapter
	 * author feiyang
	 * create at 2017/8/3 17:41
	 */
	class NewsAdapter : RecyclerView.Adapter
	<NewsAdapter.ViewHolder> {
	
	    private var context: Context? = null
	    private var newsList: ArrayList<News>? = null
	
	    //这是构造方法
	    constructor(context: Context, newsList: ArrayList<News>) {
	        this.context = context
	        this.newsList = newsList
	    }
	
	    class ViewHolder : RecyclerView.ViewHolder {
	
	        var ivDes: ImageView
	        var tvTitle: TextView
	
	        constructor(itemView: View) : super(itemView) {
	            ivDes = itemView.findViewById(R.id.iv_news_des)
	            tvTitle = itemView.findViewById(R.id.tv_news_title)
	        }
	    }
	
	    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
	        if (newsList?.size as Int > position) {
	            val news = newsList?.get(position)
	            //使用Glide加载图片
	            Glide.with(context).load(news?.thumbnailPicS).into(holder?.ivDes)
	            //设置标题
	            holder?.tvTitle?.text = news?.title
	        }
	    }
	
	    override fun getItemCount(): Int {
	        return newsList?.size as Int
	    }
	
	    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
	        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_news, parent, false)
	        return ViewHolder(view)
	    }
	
	    /**
	     * 添加数据
	     */
	    fun addData(dataList: ArrayList<News>): Unit {
	        //这里不用像java一样判断空了,这里肯定是非空的
	        if (dataList.size == 0) {
	            return
	        }
	        newsList?.addAll(dataList)
	        notifyDataSetChanged()
	    }
	
	    /**
	     * 更新数据
	     */
	    fun updateData(dataList: ArrayList<News>): Unit {
	        if (dataList.size==0) {
	            return
	        }
	        newsList?.clear()
	        newsList?.addAll(dataList)
	        notifyDataSetChanged()
	    }
	
	}

```
	
# 最后

核心代码就在上面了,如果想要完整代码的话,去[这里下载](http://download.csdn.net/detail/xfhy_/9920933)   不要积分....
