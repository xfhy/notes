package com.xfhy.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.xfhy.mobilesafe.global.MyApplication;

/**
 * Created by xfhy on 2017/4/14.
 * 用来操作SharedPreferences的一个工具类
 */

public class SpUtil {

    /**
     * SharedPreferences名称
     */
    private static final String CONFIG_SP = "config";

    private static SharedPreferences mSharedPreferences = null;


    /**
     * 写入boolean变量至SharedPreferences中
     *
     * @param key   存储节点名称
     * @param value 存储节点的值 boolean
     */
    public static void putBoolean(String key, boolean value) {
        if (mSharedPreferences == null) {   //为null的时候才创建
            //获取SharedPreferences
            mSharedPreferences = MyApplication.getContext().getSharedPreferences(CONFIG_SP,
                    Context.MODE_PRIVATE);
        }
        //提交数据    如果您不关心返回值，并且您从应用程序的主进程中使用它，请考虑使用apply（）。
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * 读取boolean标示从SharedPreferences中
     *
     * @param key      存储节点名称
     * @param delValue 没有此节点默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static boolean getBoolan(String key, boolean delValue) {
        if (mSharedPreferences == null) {  //有可能未初始化
            mSharedPreferences = MyApplication.getContext().getSharedPreferences(CONFIG_SP, Context
                    .MODE_PRIVATE);
        }
        return mSharedPreferences.getBoolean(key, delValue);
    }

    /**
     * 写入String变量至SharedPreferences中
     *
     * @param key   存储节点名称
     * @param value 存储节点的值 boolean
     */
    public static void putString(String key, String value) {
        if (mSharedPreferences == null) {   //为null的时候才创建
            //获取SharedPreferences
            mSharedPreferences = MyApplication.getContext().getSharedPreferences(CONFIG_SP,
                    Context.MODE_PRIVATE);
        }
        //提交数据    如果您不关心返回值，并且您从应用程序的主进程中使用它，请考虑使用apply（）。
        mSharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * 读取String标示从SharedPreferences中
     *
     * @param key      存储节点名称
     * @param delValue 没有此节点默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static String getString(String key, String delValue) {
        if (mSharedPreferences == null) {  //有可能未初始化
            mSharedPreferences = MyApplication.getContext().getSharedPreferences(CONFIG_SP, Context
                    .MODE_PRIVATE);
        }
        return mSharedPreferences.getString(key, delValue);
    }

    /**
     * 移除SP中对应节点的项
     *
     * @param key 需要移除的节点的key
     */
    public static void remove(String key) {
        if (mSharedPreferences == null) {  //有可能未初始化
            mSharedPreferences = MyApplication.getContext().getSharedPreferences(CONFIG_SP, Context
                    .MODE_PRIVATE);
        }
        mSharedPreferences.edit().remove(key).apply();
    }

    /**
     * 写入Int变量至SharedPreferences中
     *
     * @param key   存储节点名称
     * @param value 存储节点的值
     */
    public static void putInt(String key, int value) {
        if (mSharedPreferences == null) {   //为null的时候才创建
            //获取SharedPreferences
            mSharedPreferences = MyApplication.getContext().getSharedPreferences(CONFIG_SP,
                    Context.MODE_PRIVATE);
        }
        //提交数据    如果您不关心返回值，并且您从应用程序的主进程中使用它，请考虑使用apply（）。
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * 读取String标示从SharedPreferences中
     *
     * @param key      存储节点名称
     * @param delValue 没有此节点默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static int getInt(String key, int delValue) {
        if (mSharedPreferences == null) {  //有可能未初始化
            mSharedPreferences = MyApplication.getContext().getSharedPreferences(CONFIG_SP, Context
                    .MODE_PRIVATE);
        }
        return mSharedPreferences.getInt(key, delValue);
    }
}
