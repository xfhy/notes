package com.xfhy.vmovie.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xfhy.vmovie.listener.HttpCallbackListener;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xfhy on 2017年6月15日11:07:50
 * 网络工具类
 */

public class HttpUtils {

    /**
     * 获取Okhttp客户端
     * 用于管理所有的请求，内部支持并发，所以我们不必每次请求都创建一个 OkHttpClient
     * 对象，这是非常耗费资源的
     */
    public static OkHttpClient okHttpClient = null;

    /**
     * 初始化OkHttpClient
     */
    public static void initOkHttp() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
    }

    /**
     * 网络连接是否正常
     *
     * @return true:有网络    false:无网络
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * get方式访问网络
     *
     * @param url      要访问的url
     * @param from     由谁发起的调用,用于区别调用者
     * @param listener 访问网络的接口回调
     */
    public static void requestGet(final String url, final int from, final HttpCallbackListener
            listener) {
        //1, 开一个子线程请求网络数据
        new Thread(new Runnable() {
            @Override
            public void run() {

                //2, 创建请求
                Request request = new Request.Builder().url(url).build();
                try {
                    //3, 发送请求
                    Response response = HttpUtils.okHttpClient.newCall(request).execute();

                    //4, 请求成功
                    if (response.isSuccessful()) {
                        if (listener != null) {
                            //回调成功的接口
                            listener.onFinish(from, response.body().string());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        //回调失败的接口
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }

}
