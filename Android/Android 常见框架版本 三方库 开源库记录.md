
- [Glide](https://github.com/bumptech/glide) 
```
repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation 'com.github.bumptech.glide:glide:4.9.0'
  annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
}
```
- [fastjson](https://github.com/alibaba/fastjson) `compile 'com.alibaba:fastjson:1.1.71.android'`
- [RxJava](https://github.com/ReactiveX/RxJava) 
- [RxAndroid](https://github.com/ReactiveX/RxAndroid) 

```
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
// Because RxAndroid releases are few and far between, it is recommended you also
// explicitly depend on RxJava's latest version for bug fixes and new features.
// (see https://github.com/ReactiveX/RxJava/releases for latest 2.x.x version)
implementation 'io.reactivex.rxjava2:rxjava:2.x.x'
```

- [Gson](https://github.com/google/gson) ` implementation 'com.google.code.gson:gson:2.8.5'`
- [AndroidX](https://developer.android.google.cn/jetpack/androidx/migrate?hl=en)
```
//ConstraintLayout
com.android.support.constraint:constraint-layout:1.1.3
androidx.constraintlayout:constraintlayout:1.1.2

//RecyclerView
com.android.support:recyclerview-v7	
androidx.recyclerview:recyclerview:1.0.0

//AppCompat
com.android.support:appcompat-v7	
androidx.appcompat:appcompat:1.0.2

//MaterialDesign
com.android.support:design	
com.google.android.material:material:1.0.0

//CardView
com.android.support:cardview-v7	
androidx.cardview:cardview:1.0.0

```


```gradle
api "androidx.appcompat:appcompat:1.1.0"
//design
api "com.google.android.material:material:1.0.0"
api "androidx.constraintlayout:constraintlayout:1.1.3"

//anko
api "org.jetbrains.anko:anko:0.10.5"
//gson
api "com.google.code.gson:gson:2.8.5"

//OkHttp Retrofit
api "com.squareup.okhttp3:okhttp:3.11.0"
api "com.squareup.okhttp3:logging-interceptor:3.11.0"
api "com.squareup.retrofit2:retrofit:2.4.0"
api "com.squareup.retrofit2:converter-gson:2.4.0"
api "com.squareup.retrofit2:adapter-rxjava2:2.4.0"

//RxLifecycle
//api "com.trello.rxlifecycle2:rxlifecycle-kotlin:2.2.2"
//api "com.trello.rxlifecycle2:rxlifecycle-components:2.2.2"

//log
api "com.orhanobut:logger:2.2.0"

//权限
api "com.tbruyelle.rxpermissions2:rxpermissions:0.9.5@aar"

api "androidx.cardview:cardview:1.0.0"
api "androidx.recyclerview:recyclerview:1.1.0"

//多dex配置
api "androidx.multidex:multidex:2.0.1"

//RxKotlin
api "io.reactivex.rxjava2:rxkotlin:2.2.0"
api "io.reactivex.rxjava2:rxandroid:2.1.1"

//ARouter  路由
api "com.alibaba:arouter-api:1.3.1"

implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:kotlin_version"

//安卓官方 Room
api "androidx.room:room-runtime:2.2.3"
// use kapt for Kotlin
kapt "androidx.room:room-compiler:2.2.3"
// optional - RxJava support for Room
api "androidx.room:room-rxjava2:2.2.3"
// optional - Guava support for Room, including Optional and ListenableFuture
api "androidx.room:room-guava:2.2.3"

//屏幕适配 https://github.com/JessYanCoding/AndroidAutoSize
api "me.jessyan:autosize:1.1.2"

//-------------------------其他常用------------------------------
//matisse 知乎图片选择
//api 'com.zhihu.android:matisse:0.5.0-beta3'

//CircleImageView  圆形图片
//api 'de.hdodenhof:circleimageview:2.2.0'

//动画
//api "com.airbnb.android:lottie:2.8.0"

//GSYVideoPlayer  视频播放器
//api 'com.shuyu:GSYVideoPlayer:2.1.1'

//Bugly 热修复+更新+异常上报  推荐
//api 'com.tencent.bugly:crashreport_upgrade:1.3.5'
```