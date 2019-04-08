> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/mynameishuangshuai/article/details/51491074 版权声明：本文为博主原创文章，未经博主允许不得转载；来自 http://blog.csdn.net/mynameishuangshuai https://blog.csdn.net/mynameishuangshuai/article/details/51491074

       最近有几位朋友给我留言，让我谈一下对 Activity 启动模式的理解。我觉得对某个知识点的理解必须要动手操作才能印象深刻，所以今天写一篇博文，结合案例理解 Activity 启动模式。由于之前看过 “区长” 的一篇博文（文章结尾处有链接）深受启发，因此本文是在那篇文章的基础上更加全面的讲解。
       众所周知当我们多次启动同一个 Activity 时，系统会创建多个实例，并把它们按照先进后出的原则一一放入任务栈中，当我们按 back 键时，就会有一个 activity 从任务栈顶移除，重复下去，直到任务栈为空，系统就会回收这个任务栈。但是这样以来，系统多次启动同一个 Activity 时就会重复创建多个实例，这种做法显然不合理，为了能够优化这个问题，Android 提供四种启动模式来修改系统这一默认行为。
       进入正题，Activity 的四种启动模式如下：
       **standard、singleTop、singleTask、singleInstance**
       接下来，我们一边讲理论一边结合案例来全面学习这四种启动模式。
       为了打印方便，定义一个基础 Activity，在其 onCreate 方法和 onNewIntent 方法中打印出当前 Activity 的日志信息，主要包括所属的 task，当前类的 hashcode，以及 taskAffinity 的值。之后我们进行测试的 Activity 都直接继承该 Activity

```
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by huangshuai on 2016/5/23.
 * Email：huangshuai@wooyun.org
 * 方便打印的基础Activity
 */
public class BaseActivity extends AppCompatActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        Log.i("WooYun", "*****onCreate()方法******");
        Log.i("WooYun", "onCreate：" + getClass().getSimpleName() + " TaskId: " + getTaskId() + " hasCode:" + this.hashCode());
        dumpTaskAffinity();
    }

@Override
protected void onNewIntent(Intent intent) {
super.onNewIntent(intent);
        Log.i("WooYun", "*****onNewIntent()方法*****");
        Log.i("WooYun", "onNewIntent：" + getClass().getSimpleName() + " TaskId: " + getTaskId() + " hasCode:" + this.hashCode());
        dumpTaskAffinity();
    }

protected void dumpTaskAffinity(){
try {
            ActivityInfo info = this.getPackageManager()
                    .getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            Log.i("WooYun", "taskAffinity:"+info.taskAffinity);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

## <a></a>standard - 默认模式

       这个模式是默认的启动模式，即标准模式，在不指定启动模式的前提下，系统默认使用该模式启动 Activity，每次启动一个 Activity 都会重写创建一个新的实例，不管这个实例存不存在，这种模式下，谁启动了该模式的 Activity，该 Activity 就属于启动它的 Activity 的任务栈中。这个 Activity 它的 onCreate()，onStart()，onResume() 方法都会被调用。
**配置形式：**

```
<activity android: > 
```

**使用案例：**
       对于 standard 模式，android:launchMode 可以不进行声明，因为默认就是 standard。
       StandardActivity 的代码如下，入口 Activity 中有一个按钮进入该 Activity，这个 Activity 中又有一个按钮启动 StandardActivity。

```
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by huangshuai on 2016/5/23.
 * Email：huangshuai@wooyun.org
 * Standard模式
*/
public class ActivityStandard extends BaseActivity  {
private Buttonjump;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);

jump= (Button) findViewById(R.id.btn_standard);
jump.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(ActivityStandard.this, ActivityStandard.class);
                startActivity(intent);
            }
        });
    }
}
```

       我们首先进入 StandardActivity，进入后再点击进入 Standard 的按钮，再按四次返回键不断返回。

![](https://img-blog.csdn.net/20160524154843649)

输出的日志如下：

![](https://img-blog.csdn.net/20160524160959018)

       可以看到日志输出了四次 StandardActivity 的和一次 MainActivity 的，从 MainActivity 进入 StandardActivity 一次，后来我们又按了三次按钮，总共四次 StandardActivity 的日志，并且所属的任务栈的 id 都是 2087，这也验证了**谁启动了该模式的 Activity，该 Activity 就属于启动它的 Activity 的任务栈中**这句话，因为启动 StandardActivity 的是 MainActivity，而 MainActivity 的 taskId 是 2087，因此启动的 StandardActivity 也应该属于 id 为 2087 的这个 task，后续的 3 个 StandardActivity 是被 StandardActivity 这个对象启动的，因此也应该还是 2087，所以 taskId 都是 2087。并且每一个 Activity 的 hashcode 都是不一样的，说明他们是不同的实例，即 “每次启动一个 Activity 都会重写创建一个新的实例”

## <a></a>singleTop - 栈顶复用模式

       这个模式下，如果新的 activity 已经位于栈顶，那么这个 Activity 不会被重写创建，同时它的 onNewIntent 方法会被调用，通过此方法的参数我们可以去除当前请求的信息。如果栈顶不存在该 Activity 的实例，则情况与 standard 模式相同。需要注意的是这个 Activity 它的 onCreate()，onStart() 方法不会被调用，因为它并没有发生改变。
**配置形式：**

```
<activity android:>
```

**使用案例：**
ActivitySingleTop.java

```
/**
 * Created by huangshuai on 2016/5/23.
 * Email：huangshuai@wooyun.org
 * SingleTop模式
*/
public class ActivitySingleTop extends BaseActivity {
private Button jump,jump2;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletop);

jump = (Button) findViewById(R.id.btn_singletop);
jump2 = (Button) findViewById(R.id.btn_other);
jump.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(ActivitySingleTop.this, ActivitySingleTop.class);
                startActivity(intent);
            }
        });
jump2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(ActivitySingleTop.this, OtherTopActivity.class);
                startActivity(intent);
            }
        });
    }
```

OtherTopActivity.java

```
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by huangshuai on 2016/5/23.
 * Email：huangshuai@wooyun.org
 */
public class OtherTopActivity extends BaseActivity {
private Button jump;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

jump= (Button) findViewById(R.id.btn_other);
jump.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(OtherTopActivity.this, ActivitySingleTop.class);
                startActivity(intent);
            }
        });
    }
}
```

操作和 standard 模式类似，直接贴输出日志

![](https://img-blog.csdn.net/20160524161028907)

       我们看到，除了第一次进入 SingleTopActivity 这个 Activity 时，输出的是 onCreate 方法中的日志，后续的都是调用了 onNewIntent 方法，并没有调用 onCreate 方法，并且四个日志的 hashcode 都是一样的，说明栈中只有一个实例。这是因为第一次进入的时候，栈中没有该实例，则创建，后续的三次发现栈顶有这个实例，则直接复用，并且调用 onNewIntent 方法。那么假设栈中有该实例，但是该实例不在栈顶情况又如何呢？
       我们先从 MainActivity 中进入到 SingleTopActivity，然后再跳转到 OtherActivity 中，再从 OtherActivity 中跳回 SingleTopActivity，再从 SingleTopActivity 跳到 SingleTopActivity 中，看看整个过程的日志。

![](https://img-blog.csdn.net/20160524155204119)

![](https://img-blog.csdn.net/20160524155215667)

![](https://img-blog.csdn.net/20160524161126394)

       我们看到从 MainActivity 进入到 SingleTopActivity 时，新建了一个 SingleTopActivity 对象，并且 task id 与 MainActivity 是一样的，然后从 SingleTopActivity 跳到 OtherActivity 时，新建了一个 OtherActivity，此时 task 中存在三个 Activity，从栈底到栈顶依次是 MainActivity，SingleTopActivity，OtherActivity，此时如果再跳到 SingleTopActivity，即使栈中已经有 SingleTopActivity 实例了，但是依然会创建一个新的 SingleTopActivity 实例，这一点从上面的日志的 hashCode 可以看出，此时栈顶是 SingleTopActivity，如果再跳到 SingleTopActivity，就会复用栈顶的 SingleTopActivity，即会调用 SingleTopActivity 的 onNewIntent 方法。这就是上述日志的全过程。
**对以上内容进行总结**
       standard 启动模式是默认的启动模式，每次启动一个 Activity 都会新建一个实例不管栈中是否已有该 Activity 的实例。
**singleTop 模式分 3 种情况**

1.  当前栈中已有该 Activity 的实例并且该实例位于栈顶时，不会新建实例，而是复用栈顶的实例，并且会将 Intent 对象传入，回调 onNewIntent 方法
2.  当前栈中已有该 Activity 的实例但是该实例不在栈顶时，其行为和 standard 启动模式一样，依然会创建一个新的实例
3.  当前栈中不存在该 Activity 的实例时，其行为同 standard 启动模式

       standard 和 singleTop 启动模式都是在原任务栈中新建 Activity 实例，不会启动新的 Task，即使你指定了 taskAffinity 属性。
那么什么是 taskAffinity 属性呢，可以简单的理解为任务相关性。

*   这个参数标识了一个 Activity 所需任务栈的名字，默认情况下，所有 Activity 所需的任务栈的名字为应用的包名
*   我们可以单独指定每一个 Activity 的 taskAffinity 属性覆盖默认值
*   一个任务的 affinity 决定于这个任务的根 activity（root activity）的 taskAffinity
*   在概念上，具有相同的 affinity 的 activity（即设置了相同 taskAffinity 属性的 activity）属于同一个任务
*   为一个 activity 的 taskAffinity 设置一个空字符串，表明这个 activity 不属于任何 task

       很重要的一点 taskAffinity 属性不对 standard 和 singleTop 模式有任何影响，即时你指定了该属性为其他不同的值，这两种启动模式下不会创建新的 task（如果不指定即默认值，即包名）
指定方式如下：

```
<activity android:/>
```

```
<activity android:/>
```

## <a></a>singleTask - 栈内复用模式

       这个模式十分复杂，有各式各样的组合。在这个模式下，如果栈中存在这个 Activity 的实例就会复用这个 Activity，不管它是否位于栈顶，复用时，会将它上面的 Activity 全部出栈，并且会回调该实例的 onNewIntent 方法。其实这个过程还存在一个任务栈的匹配，因为这个模式启动时，会在自己需要的任务栈中寻找实例，这个任务栈就是通过 taskAffinity 属性指定。如果这个任务栈不存在，则会创建这个任务栈。
**配置形式：**

```
<activity android: >
```

**使用案例：**
ActivitySingleTask.java

```
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by huangshuai on 2016/5/23.
 * Email：huangshuai@wooyun.org
 * SingleTask模式
*/
public class ActivitySingleTask extends BaseActivity {
private Button jump,jump2;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

jump = (Button) findViewById(R.id.btn_task);
jump2 = (Button) findViewById(R.id.btn_other);
jump.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(ActivitySingleTask.this, ActivitySingleTask.class);
startActivity(intent);
            }
        });
jump2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(ActivitySingleTask.this, OtherTaskActivity.class);
startActivity(intent);
            }
        });
    }
}
```

OtherTaskActivity.java

```
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by huangshuai on 2016/5/23.
 * Email：huangshuai@wooyun.org
 */
public class OtherTaskActivity extends BaseActivity {
private Button jump;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_task);

jump= (Button) findViewById(R.id.btn_other);
jump.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
                Intent intent = new Intent(OtherTaskActivity.this, ActivitySingleTask.class);
                startActivity(intent);
            }
        });
    }
}
```

       现在我们先不指定任何 taskAffinity 属性，对它做类似 singleTop 的操作，即从入口 MainActivity 进入 SingleTaskActivity，然后跳到 OtherActivity，再跳回到 SingleTaskActivity。看看整个过程的日志。

![](https://img-blog.csdn.net/20160524155738918)

![](https://img-blog.csdn.net/20160524155749637)

![](https://img-blog.csdn.net/20160524155757465)

![](https://img-blog.csdn.net/20160524161742872)

       当我们从 MainActiviyty 进入到 SingleTaskActivity，再进入到 OtherActivity 后，此时栈中有 3 个 Activity 实例，并且 SingleTaskActivity 不在栈顶，而在 OtherActivity 跳到 SingleTaskActivity 时，并没有创建一个新的 SingleTaskActivity，而是复用了该实例，并且回调了 onNewIntent 方法。并且原来的 OtherActivity 出栈了，具体见下面的信息，使用命令 **adb shell dumpsys activity activities** 可进行查看

![](https://img-blog.csdn.net/20160524155846441)

       可以看到当前栈中只有两个 Activity，即原来栈中位于 SingleTaskActivity 之上的 Activity 都出栈了。
       我们看到使用 singleTask 启动模式启动一个 Activity，它还是在原来的 task 中启动。其实是这样的，我们并没有指定 taskAffinity 属性，这说明和默认值一样，也就是包名，当 MainActivity 启动时创建的 Task 的名字就是包名，因为 MainActivity 也没有指定 taskAffinity，而当我们启动 SingleTaskActivity ，首先会寻找需要的任务栈是否存在，也就是 taskAffinity 指定的值，这里就是包名，发现存在，就不再创建新的 task，而是直接使用。当该 task 中存在该 Activity 实例时就会复用该实例，这就是栈内复用模式。
       这时候，如果我们指定 SingleTaskActivity 的 taskAffinity 值。

```
<activity android:/>
```

还是之前的操作。但是日志就会变得不一样。

![](https://img-blog.csdn.net/20160524161157832)

       我们看到 SingleTaskActivity 所属的任务栈的 TaskId 发生了变换，也就是说开启了一个新的 Task，并且之后的 OtherActivity 也运行在了该 Task 上
打印出信息也证明了存在两个不同的 Task

![](https://img-blog.csdn.net/20160524155946943)

如果我们指定 MainActivity 的 taskAffinity 属性和 SingleTaskActivity 一样，又会出现什么情况呢。

![](https://img-blog.csdn.net/20160524161147142)

没错，就是和他们什么都不指定是一样的。
这时候，就有了下面的结论
singleTask 启动模式启动 Activity 时，首先会根据 taskAffinity 去寻找当前是否存在一个对应名字的任务栈

*   如果不存在，则会创建一个新的 Task，并创建新的 Activity 实例入栈到新创建的 Task 中去
*   如果存在，则得到该任务栈，查找该任务栈中是否存在该 Activity 实例
                  如果存在实例，则将它上面的 Activity 实例都出栈，然后回调启动的 Activity 实例的 onNewIntent 方法
                  如果不存在该实例，则新建 Activity，并入栈
    此外，我们可以将两个不同 App 中的 Activity 设置为相同的 taskAffinity，这样虽然在不同的应用中，但是 Activity 会被分配到同一个 Task 中去。
    我们再创建另外一个应用，指定它的 taskAffinity 和之前的一样，都是 com.xingyu.demo.singletask

```
<activity android:/>
```

然后启动一个应用，让他跳转到该 Activity 后，再按 home 键后台，启动另一个应用再进入该 Activity，看日志

![](https://img-blog.csdn.net/20160524161225892)

       我们看到，指定了相同的 taskAffinity 的 SingleTaskActivity 和 OtherActivity 被启动到了同一个 task 中，taskId 都为 2169。

## <a></a>singleInstance - 全局唯一模式

       该模式具备 singleTask 模式的所有特性外，与它的区别就是，这种模式下的 Activity 会单独占用一个 Task 栈，具有全局唯一性，即整个系统中就这么一个实例，由于栈内复用的特性，后续的请求均不会创建新的 Activity 实例，除非这个特殊的任务栈被销毁了。以 singleInstance 模式启动的 Activity 在整个系统中是单例的，如果在启动这样的 Activiyt 时，已经存在了一个实例，那么会把它所在的任务调度到前台，重用这个实例。
**配置形式：**

```
<activity android: >
```

**使用案例：**
增加一个 Activity 如下：
ActivitySingleInstance.java

```
import android.os.Bundle;

/**
 * Created by huangshuai on 2016/5/24.
 * Email：huangshuai@wooyun.org
 * SingleInstance模式
 */
public class ActivitySingleInstance extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleinstance);
    }
}
配置属性如下：
<activity
    android:
    android:launchMode="singleInstance">

    <intent-filter>
        <action android: />
        <category android: />
    </intent-filter>
</activity>
```

使用下面的方式分别在两个应用中启动它

```
Intent intent = new Intent();
intent.setAction("com.castiel.demo.singleinstance");
startActivity(intent);
```

做的操作和上一次是一样的，查看日志

![](https://img-blog.csdn.net/20160524160301264)

       我们看到，第一个应用启动 SingleInstanceActivity 时，由于系统中不存在该实例，所以新建了一个 Task，按 home 键后，使用另一个 App 进入该 Activity，由于系统中已经存在了一个实例，不会再创建新的 Task，直接复用该实例，并且回调 onNewIntent 方法。可以从他们的 hashcode 中可以看出这是同一个实例。因此我们可以理解为：SingleInstance 模式启动的 Activity 在系统中具有全局唯一性。

参考链接：[http://blog.csdn.net/sbsujjbcy/article/details/49360615](http://blog.csdn.net/sbsujjbcy/article/details/49360615)

> **安卓开发高级技术交流 QQ 群：108721298 欢迎入群**
> 
> **微信公众号：mobilesafehome**
> 
> （本公众号支持投票）
> 
> ![](https://img-blog.csdn.net/20160509111910010)

<link href="https://csdnimg.cn/release/phoenix/mdeditor/markdown_views-a47e74522c.css" rel="stylesheet">