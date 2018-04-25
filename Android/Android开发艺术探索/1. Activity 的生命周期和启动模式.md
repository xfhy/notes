# Activity的生命周期和启动模式

## 1.1 Activity生命周期

### 1.1.1 典型情况下的生命周期

（1）onCreate：表示 Activity 正在被创建，这是生命周期的第一个方法。在这个方法中，
可以做一些初始化工作，比如调用setContentView去加载界面布局资源、初始化Activity
所需数据等。

（2）onRestart：表示 Activity 正在重新启动。一般情况下，当当前 Activity 从不可见重
新变为可见状态时，onRestart 就会被调用。这种情形一般是用户行为所导致的，比如用户
按 Home 键切换到桌面或者用户打开了一个新的 Activity，这时当前的 Activity 就会暂停，
也就是 onPause 和 onStop 被执行了，接着用户又回到了这个 Activity，就会出现这种情况。

（3）onStart：表示 Activity 正在被启动，即将开始，这时 Activity 已经可见了，但是还
没有出现在前台，还无法和用户交互。这个时候其实可以理解为 Activity 已经显示出来了，
但是我们还看不到。

（4）onResume：表示 Activity 已经可见了，并且出现在前台并开始活动。要注意这个
和onStart的对比，onStart和onResume都表示Activity已经可见，但是onStart的时候Activity
还在后台，onResume 的时候 Activity 才显示到前台。

（5）onPause：表示 Activity 正在停止，正常情况下，紧接着 onStop 就会被调用。在特
殊情况下，如果这个时候快速地再回到当前 Activity，那么 onResume 会被调用。笔者的理
解是，这种情况属于极端情况，用户操作很难重现这一场景。此时可以做一些存储数据、
停止动画等工作，但是注意不能太耗时，因为这会影响到新 Activity 的显示，onPause 必须
先执行完，新 Activity 的 onResume 才会执行。

（6）onStop：表示 Activity 即将停止，可以做一些稍微重量级的回收工作，同样不能太
耗时。

（7）onDestroy：表示 Activity 即将被销毁，这是 Activity 生命周期中的最后一个回调，
在这里，我们可以做一些回收工作和最终的资源释放。

![](https://upload-images.jianshu.io/upload_images/626583-7d0978fec26e655c.JPG?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

**问题 1：onStart 和 onResume、onPause 和 onStop 从描述上来看差不多，对我们来说有
什么实质的不同呢？**

onStart()和onStop()是从Activity是否可见这个角度来回调的,而onResume()onPause()是从是否位于前台这个角度来回调的.

**问题2: 第一个Activity启动第二个Activity,第一个界面的onPause()会先调用,再调用第二个界面的onCreate(),onStart(),onResume()**

### 1.1.2 异常情况下的生命周期分析

1. Activity在异常情况下终止的，系统会调用onSaveInstanceState来保存当前Activity的状态，在onStop之前，和onPause没有既定的时序关系；当这个Activity重新创建后，系统会调用onRestoreInstanceState方法并把销毁前保存的信息传回， onRestoreInstanceState的调用时机在onStart之后。

2. 当Activity异常情况下的情况下需要重新创建时，系统会默认我们保存当前Activity的视图结果，并在Activity重启后为我们恢复这些数据，比如文本框用户输入的数据，ListView的滚动位置等，
具体可以查看相对应View的源码，查看onSaveInstanceState和onRestoreInstanceState方法。

3. 关于保存和恢复View的层次结构，系统的工作流程：首先Activity会调用onSaveInstanceState去保存数据，然后Activity会委托Window去保存数据，接着Window会委托它上面的顶级容器去保存数据，顶层容器一般是DecorView（ViewGroup)；最后顶层容器再去一一通知它的子元素保存数据，这样整个数据的保存过程就完成了。

4. 如果资源内存不足优先级低的Activity会被杀死，优先级从高到低：

5. 前台Activity-正在和用户交互优先级最高；

6. 如果我们不想配置发生改变就Activity重新创建，可以使用Activity的configChanges属性；常用的有locale、orientation和keyboardHidden这三个选项；指定了configChanges后，Activity发生对应改变后，不会重启Activity，只会调用onConfigurationChanged方法。

## 1.2 Activity的启动模式

### 1.2.1 Activity的LaunchMode

1. 任务栈是一个“后进先出”的栈结构，每次finish()处于前台的Activity就会出栈，直到栈为空为止，当栈中无任何Activity的时候，系统就会回收这个任务栈。

2. 四种启动模式

3. **standard**：标准模式，系统默认模式，每次启动都会创建一个新的实例；在这种模式下，谁启动了这个Activity，这个Activity就在启动它的那个Activity所在的栈中。当我们使用ApplicationContext去启动standard模式的Activity就会报错，因为standard模式的Activity会默认进入启动它的Activity所属的任务栈中，而非Activity类型的Context并没有任务栈。解决的办法是为这个待启动的Activity指定FLAG_ACTIVITY_NEW_TASK标记位，这样启动的时候就会为它创建一个新的任务栈。

4. **singleTop**: 栈顶复用模式。这种模式下，如果新的Activity已经位于栈顶，那么此Activity不会创建，同时他的onNewIntent方法会被回调，onCreate、onStart不会被调用。如果新的Activity的实例存在但不是位于栈顶，那么新的Activty依然会重新创建。

5. **singleTask**: 栈内复用模式。这是一种单实例模式，这种模式下，Activity在一个栈中存在，那么多次启动该Activity都不会重新创建实例，和sinleTop一样，系统会回调其onNewIntent。如果启动的Activity没有所需要的任务栈，就会先创建任务栈再创建Activity。singleTask默认具有clearTop的效果，具有该模式的Activity会让其之上的Activity全部出栈。

6. **singleInstance**: 单实例模式。这是一种加强的singleTask模式，除了具备singleTask的特性之外，具有该模式的Activity只能单独位于一个任务栈中；比如Activity A是singleInstance模式的，当A启动后，系统会为它创建一个新的任务栈，后续的启动均不会创建新的Activity，除非这个任务栈被系统销毁了。

7. 参数TaskAffinity用于指定Activity栈，TaskAffinity属性经常和singleTask启动模式或allowTaskReparenting属性配对使用，其他情况没有意义。当应用A启动了应用B的某个Activity后，如果这个Activity的allowTaskReparenting属性为true的话，那么当应用B被启动后，此Activity会直接从应用A的任务栈转移到应用B的任务栈中。

8. Activity的启动模式可以通过AndroidMenifest为其指定启动模式
```xml
<activity
      android:name=".MainActivity"
      android:launchMode="singleTask" />
```
还可以通过在Intent中设置标记位来为Activity指定启动模式
```java
    Intent intent = new Intent(this,MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
```
两种方法都可以为Activity指定启动模式，但还是有区别。第二种方式的优先级高于第一种，如果都设置了只有第二种会生效。第一种方式无法直接为Activity设置FLAG_ACTIVITY_CLEAR_TOP标记，而第二种方式无法为Activity指定singleInstance模式。

9. 通过adb shell dumpsys activity命令可以详细的了解当前任务栈情况。

### 1.2.2 Activity的Flags

- **FLAG_ACTIVITY_NEW_TASK**
为Activity指定“singleTask”启动模式，其效果和xml中指定该启动模式相同。

- **FLAG_ACTIVITY_SINGLE_TOP**
为Activity指定“singleTop”启动模式，其效果和xml中指定该启动模式相同。
- **FLAG_ACTIVITY_CLEAR_TOP**
具有此标记位的Activity，当他启动时
，在同一个任务栈中所有位于它上面的Activity都要出栈。这个标记位一般会和singleTask启动模式一起出现，在这种情况下。singleTask启动模式默认具有此标记为的效果。

- **FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS**
具有这个标记为的Activity不会出现在历史Activity的列表中，当某些情况不希望用户通过历史列表回到我们Activity的时候这边标记比较有用。等同于在XML中指定Activity的属性android:excludeFromRecents="true"。

## 1.3  IntentFilter的匹配规则

1. action的匹配规则
一个过滤规则中可以有多个action,Intent中的action能够和过滤规则中的任何一个action相同即可匹配成功。如果Intent没有指定action，那么匹配失败。action区分大小写。

2. category的匹配规则
Intent中可以没有category;如果有，不管有几个，每个都要能和过滤规则中的任何一个category相同；如果没有，依然可以匹配成功，原因是因为如果没有指定category，在调用startActivity或startActivityForResult时系统会默认加上“android.intent.category.DEFAULT”这个category。同时为了我们的Activity能够支持隐式调用，就必须要在intent-filter中指定“android.intent.category.DEFAULT”这个category。

3. data的匹配规则
data的匹配规则和action类似，如果过滤规则定义了data，那么Intent必须定义可匹配的data。
先介绍一下data的结构（有些复杂）
```xml
<data android:scheme="string"
           android:host="string"
           android:port="string"
           android:path="string"
           android:pathPattern="string"
           android:pathPrefix="string"
           android:mimeType="string"
```
data由两部分组成，mimeType和URI。mimeType是指媒体类型，比如image/jpeg、audio/mpeg4-generic和video/等，可以表示图片、文本、视频等不同的媒体格式，而URI中包含的数据就比较多了，下面是URI的结构*：

```
<scheme>://<host>:<port>/[<path>|<pathPrefix>|<pathPattern>]
//实际例子
content://com.example.project:200/folder/subfolder/etc
http://www.baidu.com:80/search/info
```

Scheme：URI的模式，比如http、file、content等，如果URI中没有指定scheme，那么整个URI的其他参数无效，也意味着URI是无效的。

Host：URI的主机名，比如www.baidu.com，如果host未指定，那么整个URI中的其他参数无效，也以为着URI无效。

Port：URI中的端口号，比如80，仅当URI中指定了scheme和host参数的时候port参数才是有意义的。

Path、pathPattern和pathPrefix：这三个参数都是表示路径信息；其中path表示完整的路径信息；pathPattern也表示完整路径信息，但是它里面可以包含通配符“”，“”表示0个或多个任意字符；pathPrefix表示路径的前缀信息。

### data的匹配规则

1. 要求Intent中必须含有data数据，并且data数据能够完全匹配过滤规则中的某一个data；这里的完全匹配是指过滤规则中出现的data部分也出现在了Intent中的data中。

2. 如果没有指定URI，是有默认值的，URI的默认值为content和file。也就是说，虽然没有指定URI，但Intent中的URI部分的schema必须为content或者file才能匹配。

3. 如果要为Intent指定完整的data，必须调用setDataAndType方法，不能先调用setData再调用setType，因为这两个方法都会清除对方的值。

### Tips

1. 当我们隐式启动一个Activity的时候，可以做一下判断，看是否能匹配到我们的隐式Intent，如果不做判断没找到对应的Activity系统就会抛出android.content.ActivityNotFoundException异常。
第一种：采用PackageManager的resolveActivity方法或者Intent的resolveActivity方法，如果找不到匹配的Activity就会返回null，我们通过判断返回值就可以规避上述错误了。

2. 在intent-filter中声明了<category android:name="android.intent.category.DEFAULT"/>这个category的Activity，才可以接收隐式意图。

3. 有一类action和category的共同作用是标明这是一个入口Activity，并且会出现在系统的应用列表中，少一个都没有任何意义，也不会出现在系统的应用列表中。

```java
<action android:name="android.intent.action.MAIN" />
<category android:name="android.intent.category.LAUNCHER" />
```