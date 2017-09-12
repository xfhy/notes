package com.feiyang.complexlistlayout.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

/**
 * description：设备信息(比如屏幕高度,宽度那些)
 * author feiyang
 * create at 2017/8/7 19:06
 */
public class DeviceInforUtils {

    /**
     * 获取屏幕信息 宽度,高度
     * @param context
     * @return
     */
    public static DisplayMetrics getScreenInfor(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return displayMetrics;
    }

}
