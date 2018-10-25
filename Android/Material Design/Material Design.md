脑图展示

![](https://mmbiz.qpic.cn/mmbiz_png/jE32KtUXy6FZfDV7CO21g6UcwKhzCLfBVRB6viaB6nLTPYiaSnoianicYUtUR98iadyA6TMycWOamxaksiabjVE3B4Rg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

> AndroidX了解一下:简单地说就是新的库可以在不同的Android版本上使用。比如之前我们如果使用support为27.1.1的相关依赖库时。可能需要所有相关的support 库都为27.1.1。如果其中有bug的话，可能需要所有的都去升级，存在一个绑定关系，而且正式版的发布周期也很长。
通过AndroidX，我们可以看到实时实现的特性和bug修复。升级个别依赖，不需要对使用的所有其他库进行更新。这就和我们使用Github上的开源库一样的，出了问题，我们可以提出bug和意见。作者修复后，发布新版本，我们就可以直接替换使用了。更加的透明便捷。**官方博客中有说道，为了给开发者一定迁移的时间，所以28.0.0的稳定版本还是采用android.support。但是所有后续的功能版本都将采用androidx。**

#### 一、初始化配置

Step 1： 打开工程目录下的build.gradle文件，并添加maven引用
```
allprojects {
    repositories {
        google()
        jcenter()
        // 1.添加Google Maven地址
        maven {
            url "https://maven.google.com"
        }
    }
}
```
Step 2： 修改编译版本
```
// 2.修改编译版本为 android - P
compileSdkVersion 'android-P'
```

Step 3： 移除项目工程中依赖的v7包以及添加material依赖
```
dependencies {
    // 3.移除项目工程中依赖的v7包
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    implementation 'com.android.support.constraint:constraint-layout:1.1.0'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    // 4.添加material依赖
    implementation 'com.google.android.material:material:1.0.0'
}
```
当然，你可以使用com.android.support:design:28.0.0-alpha1，但是主要注意的是design包和material二者只能选一。

Step 4： 使用：`androidx.appcompat.app.AppCompatActivity`

注意：使用的是androidx。
```
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
```
同步一下，运行一波，虽然毛也没。。。好歹不报错~


