package com.xfhy.mobilesafe.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xfhy.mobilesafe.global.MyApplication;

/**
 * Created by xfhy on 2017/4/26.
 * 网络工具类
 */

public class HttpUtils {

    /**
     * 网络连接是否正常
     *
     * @return
     */
    public static boolean isNetworkConnected() {
        Context context = MyApplication.getContext();
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

}
