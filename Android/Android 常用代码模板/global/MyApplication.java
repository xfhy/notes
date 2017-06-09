package com.xfhy.mobilesafe.global;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Printer;

import com.xfhy.mobilesafe.activity.BaseActivity;

import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by xfhy on 2017/4/13.
 * Android提供了一个Application类,每当应用程序启动的时候,系统就会自动将这个类进入初始化.
 * 而我们可以定制一个自己的Application类,
 * 以便于管理应用程序内一些全局的状态信息,比如说全局Context;
 */

public class MyApplication extends Application {

    /**
     * 一个全局的Context
     */
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //初始化xUtils3
        x.Ext.init(this);
        x.Ext.setDebug(false); //输出debug日志，开启会影响性能

        //设置一个全局的没有捕获到的异常的处理方式
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //当遇到未捕获到的异常时,会调用此方法
                e.printStackTrace();
                String errorLogPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File
                                .separator + "error.log";
                File file = new File(errorLogPath);
                PrintWriter printWriter = null;
                try {
                    //1, 将错误日志记录到文件中
                    printWriter = new PrintWriter(file);
                    e.printStackTrace(printWriter);

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } finally {
                    if (printWriter != null) {
                        printWriter.close();
                    }
                }

                //2, 应该将错误日志上传到公司的服务器
                //3, 退出应用
                System.exit(0);

            }
        });

    }

    /**
     * 获取一个全局的Context
     *
     * @return
     */
    public static Context getContext() {
        return context;
    }

}
