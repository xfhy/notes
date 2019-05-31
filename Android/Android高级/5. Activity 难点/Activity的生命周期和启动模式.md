
## 1. Activity生命周期

1. onStart和onStop是从Activity是否可见的角度来回调的,而onResume和onPause是从Activity是否位于前台这个角度来回调的
2. 旧Activity先pause,新Activity再启动.源码中就是这么写的.不能在pause中执行重量级的操作,否则会影响下一个Activity的启动,应该尽快让新的Activity显示到前台.
3. 资源相关的系统配置发送改变导致Activity被杀死并重新创建,这时会回调Activity的onSaveInstanceState和onRestoreInstanceState方法.

![image](AC220E5EB7D14FF2A6AB7B78986D9056)

4. 每个View都有onSaveInstanceState和onRestoreInstanceState这两个方法,系统已经帮我们实现好了,可以恢复一些数据.
5. 系统只会在Activity即将被销毁并且有机会重新显示的情况下才会去调用它onSaveInstanceState方法
6. 当在清单文件中写了android:configChanges="orientation|screenSize",那么在屏幕旋转时Activity不会重新启动,而是会回调onConfigurationChanged方法.

## 2. Activity启动模式

### 2.1 standard

标准模式,每次启动一个Activity都会重新创建一个新的实例,不管这个实例是否已经存在.谁启动了这个Activity,那么这个Activity就运行在启动它的那个Activity所在栈中.(API 28上用ApplicationContext去启动standard模式的Activity居然不会报错..但是在之前的老版本中会报错,因为ApplicationContext没有所谓的任务栈,需要添加一个`FLAG_ACTIVITY_NEW_TASK`标记位)

### 2.2 singleTop

栈顶复用模式,在这种模式下,如果新Activity已经位于任务栈的栈顶,那么此Activity不会被重新创建,同时它的onNewIntent方法会被回调,通过此方法的参数我们可以取出当前请求的信息.

### 2.3 singleTask

栈内复用模式.只要Activity在一个栈中存在,那么再次启动该Activity就不会重新创建实例,但是会调用onNewIntent方法. 启动一个singleTask的Activity时,首先寻找系统中是否存在该Activity所需的任务栈,如果没有则先创建任务栈再将实例创建并放入其中.如果存在,则不用创建新的任务栈,直接复用之前的Activity,并且出栈该Activity之上的所有Activity

### 2.4 singleInstance

单实例模式,这是一种加强的singleTask模式,它除了具有singleTask模式的所有特性外,还加强了一点,那就是具有此种模式的Activity只能单独地位于一个任务栈中.换句话说，比如Activity A是singleInstance模式，当A启动后，系统会
为它创建一个新的任务栈，然后A独自在这个新的任务栈中，由于栈内复用的特性，后续的请求均不会创建新的Activity，除非这个独特的任务栈被系统销毁了。

### 2.5 TaskAffinity

标识Activity所需的任务栈的名字.这个参数标识了一个Activity所需要的任务栈的名字，默认情况下，所有Activity所需的任务栈的名字为应用的包名。

启动一个Application的时候，系统会为它默认创建一个对应的Task，用来放置根Activity。默认启动Activity会放在同一个Task中，新启动的Activity会被压入启动它的那个Activity的栈中，并且显示它。当用户按下回退键时，这个Activity就会被弹出栈，按下Home键回到桌面，再启动另一个应用，这时候之前那个Task就被移到后台，成为后台任务栈，而刚启动的那个Task就被调到前台，成为前台任务栈，Android系统显示的就是前台任务栈中的Top实例Activity。

### 2.6 指定启动模式

#### 2.6.1 通过AndroidMenifest

```xml
<activity
    android:name="com.ryg.chapter_1.SecondActivity"
    android:configChanges="screenLayout"
    android:launchMode="singleTask"
    android:label="@string/app_name" />
```

#### 2.6.2 通过Intent设置标志位

```java
Intent intent = new Intent();
intent.setClass(MainActivity.this,SecondActivity.class);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
startActivity(intent);
```

## 3. IntentFilter的匹配规则

### 3.1 action的匹配规则

action的匹配要求Intent中的action存在且必须和过滤规则中的其中一个action相同,区分大小写

### 3.2 category的匹配规则

系统调用startActivity时已经添加了`android.intent.category.DEFAULT`这个默认的category了.如果Intent中添加有category,那么所有的category都必须和过滤规则中的其中一个category相同.

### 3.3 data的匹配规则

data由两部分组成，mimeType和URI。mimeType指媒体类型，比如image/jpeg、
audio/mpeg4-generic和video/*等，可以表示图片、文本、视频等不同的媒体格式

```
<data android:scheme="string"
    android:host="string"
    android:port="string"
    android:path="string"
    android:pathPattern="string"
    android:pathPrefix="string"
    android:mimeType="string" />
```

- 如果要为Intent指定完整的data，必须要调用setDataAndType方法，不能先调用setData再调用setType，因为这两个方法彼此会清除对方的值.
- 隐式调用之前先用 Intent的resolveActivity方法 判断一下,返回值是否为空,如果为空则表示找不到匹配的.不判断的话,找不到匹配的就会报错.