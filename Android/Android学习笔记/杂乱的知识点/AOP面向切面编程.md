# AOP 面向切面编程

> 感谢http://blog.csdn.net/z240336124/article/details/77750874  算是勉强了解了AOP

## 1.使用场景还原

当我们打开京东 app 进入首页，如果当前是没有网络的状态，里面的按钮点击是没有反应的。只有当我们打开网络的情况下，点击按钮才能跳转页面.按照我们一般人写代码的逻辑应该是这个样子：

``` java

	 /**
     * 跳转到待收货页面
     */
    public void jumpWaitReceiving() {
        // 判断当前有没有网络
        if(CheckNetUtil.isNetworkAvailable(this)) {
            // 当前有网络我才跳转，进入待收货页面
            Intent intent = new Intent(this, WaitReceivingActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 跳转到我的钱包页面
     */
    public void jumpMineWallet() {
        if(CheckNetUtil.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, MineWalletActivity.class);
            startActivity(intent);
        }
    }

```
分析:当然,上面只有2个按钮,也就只检测了2次,写了2次网络监测,但是如果项目中的其他很多地方(几十处几百处)需要用到网络监测的话,就非常麻烦了,需要重复写很多次上面的if条件.而且这仅仅是网络监测,万一要需要在这里加入网络埋点、友盟统计、日志打印、日志上传、登录判断等等。那就更加麻烦了，需要重复写的地方更多。

## 2.在Android Studio中引入AspectJ

在项目的根目录的build.gradle文件中添加依赖，修改后文件如下

	repositories {
	    jcenter()
	}
	dependencies {
	    classpath 'com.android.tools.build:gradle:2.3.0'
	    classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:1.0.8'
	
	    // NOTE: Do not place your application dependencies here; they belong
	    // in the individual module build.gradle files
	}

然后在项目或者库的build.gradle文件中添加AspectJ的依赖

	compile 'org.aspectj:aspectjrt:1.8.9'

同时在该文件中加入AspectJX模块

	apply plugin: 'android-aspectjx'


## 3.AOP 偷懒式网络访问

``` java

	/**
    * 跳转到待收货页面
    */
    @CheckNet
    public void jumpWaitReceiving() {
        Intent intent = new Intent(this, WaitReceivingActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到我的钱包页面
     */
    @CheckNet
    public void jumpMineWallet() {
        Intent intent = new Intent(this, MineWalletActivity.class);
        startActivity(intent);
    }

```

上面我只是加了1个注解，就实现了执行方法前的网络监测（其实还需要写下面的SectionNetAspect）。
这个注解是我自己写的，如下：

	@Retention(RetentionPolicy.RUNTIME)
	public @interface CheckNet {
	}

这里，解释一下，上面的Retention，首先要明确生命周期长度 SOURCE < CLASS < RUNTIME ，所以前者能作用的地方后者一定也能作用。一般如果需要在运行时去动态获取注解信息，那只能用 RUNTIME 注解；如果要在编译时进行一些预处理操作，比如生成一些辅助代码（如 ButterKnife），就用 CLASS注解；如果只是做一些检查性的操作，比如 @Override 和 @SuppressWarnings，则可选用 SOURCE 注解。

有了这个注解之后，就可以写到需要监测网络的地方，然后再写**处理网络监测切面**。其实就是在工程里面建一个类，随便放在哪里都行，随便什么名字都可以。

``` java

	
/**
 * author feiyang
 * create at 2017/9/4 16:23
 * description：处理网络监测切面
 */
@Aspect
public class SectionNetAspect {

    private static final String TAG = "SectionNetAspect";

    /**
     * 找到处理的切点
     * * *(..)  可以处理所有的方法
     */
    @Pointcut("execution(@com.na517.aspectjdemo.CheckNet * *(..))")
    public void checkNetBehavior() {

    }

    /**
     * 处理切面
     */
    @Around("checkNetBehavior()")
    public Object checkNet(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.e(TAG, "checkNet");
        //做埋点  日志上传  权限监测

        //网络监测
        //1. 获取CheckNet注解  NDK 图片压缩 C++调用Java方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CheckNet checkNet = signature.getMethod().getAnnotation(CheckNet.class);
        if (checkNet != null) {
            //2. 判断有没有网络
            Log.e(TAG, "判断有没有网络");
            Object object = joinPoint.getThis();
            Context context = getContext(object);
            if (!isNetworkAvailable(context)) {
                // 3.没有网络不要往下执行
                Toast.makeText(context, "请检查您的网络", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        //继续下一个注解那里去执行
        return joinPoint.proceed();
    }

    private Context getContext(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof View) {
            return ((View) object).getContext();
        }
        return null;
    }

    /**
     * 检查当前网络是否可用
     *
     * @return
     */
    private static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}


```
现在好了，把上面这个类放到项目中，即可在需要的地方加入 @CheckNet，即可监测是否有网络。简单吧，very nice。