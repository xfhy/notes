# Android 使用UncaughtExceptionHandler定制自己的错误日志系统

 前言:  
    一个合理的app应该有自己的bug日志系统,可以是拿给第三方进行记录,还是本地记录成文件等都行.反正只要是开发者自己能查到错误日志就行.很多时候,我们已经处理了很多可能要发生的异常情况,但是天天恢恢疏而不(yao)漏,有时候可能会发生一些很意外的bug或者异常直接导致app崩溃等严重问题.而且该导致app崩溃的bug不易复现,这就非常尴尬.所以,我们需要记录这些日志供开发人员进行研究,然后改写代码.还好我们有UncaughtExceptionHandler.Java为我们提供了一个机制，用来捕获并处理在一个线程对象中抛出的未检测异常，以避免程序终止。我们可以通过UncaughtExceptionHandler来实现这种机制。

## 1.UncaughtExceptionHandler用处

当线程被uncaught exception事件终止之后，UncaughtExceptionHandler接口用于处理后续事件。

## 2.什么是uncaught exception
uncaught exception即没有捕获的异常  很多异常是没有去捕获的,比如NullPointerException,这种错误一旦出现则app必崩溃,一般可能是自己的代码写得有点问题.

## 3.写一个类实现UncaughtExceptionHandler

```java
/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告. 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static CrashHandler mInstance;

    private Map<String, String> mErrorMap = new HashMap<>();

    private DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (mInstance == null){
            mInstance = new CrashHandler();
        }
        return mInstance;
    }

    /**
     * 初始化
     */
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            handleException(ex);
            Thread.sleep(1000);
            //退出所有Activity
            AppManager.getAppManager().finishAllActivity();
            //友盟,用来提交错误日志
            MobclickAgent.reportError(BaseApplication.getInstance(), "崩溃：" + ex.getCause());
            android.os.Process.killProcess(android.os.Process.myPid()); // 获取PID
            System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出

        } catch (InterruptedException e) {
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                //子线程  自己管理Looper
                Looper.prepare();
                ToastUtils.showMessage(BaseApplication.getInstance(), "很抱歉，程序出现异常，即将退出");
                ex.printStackTrace();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo();
        saveCatchInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo() {
        mErrorMap.put("versionName", PackageUtils.getVersionName(BaseApplication.getInstance()));
        mErrorMap.put("versionCode", PackageUtils.getVersionCode(BaseApplication.getInstance()) + "");
        //通过反射获取所有的字段信息  比如时间,设备系统版本,设备型号等等
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mErrorMap.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
    }

    private String getFilePath() {
        String file_dir;
        boolean isSDCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        boolean isRootDirExist = Environment.getExternalStorageDirectory().exists();
        if (isSDCardExist && isRootDirExist) {
            file_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crashlog/";
        } else {
            file_dir = MyApplication.getInstance().getFilesDir().getAbsolutePath() + "/crashlog/";
        }
        return file_dir;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCatchInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : mErrorMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            String time = mFormatter.format(new Date());
            String fileName = "crash-" + time + ".log";
            String file_dir = getFilePath();

            File dir = new File(file_dir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(file_dir + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
        }
        return null;
    }
}
```
有了上面的"防范",所有的错误都会被捕获到,并且开发者可以查看日志修改bug.

## 4.使用

在Application中初始化
```java
CrashHandler.getInstance().init();
```