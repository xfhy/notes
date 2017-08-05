# Kotlin配合Gson进行json数据解析

> 昨天写的小demo里使用到了这个

# 1,首先引入第3方库

	implementation 'com.squareup.okhttp3:okhttp:3.8.1'
    implementation 'io.reactivex.rxjava2:rxjava:2.1.2'
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
    implementation 'com.google.code.gson:gson:2.8.1'

# 2,正式开始解析

``` java

	//使用Gson解析json数据
    val gson = Gson()
    //NewsResponse::class.java 表示Java的class
	//这里的NewsResponse是model对象(数据模型)
    val newsResponse = gson.fromJson(response, NewsResponse::class.java)

```

