package com.xfhy.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xfhy on 2017/2/18.
 * 流的一个工具类
 */
public class StreamUtils {

    /**
     * 将输入流转换成String并返回
     *
     * @param inputStream 需要转换成String的流
     * @return 返回转换成功的字符串, 如果转换失败则返回null
     */
    public static String streamToString(InputStream inputStream) {
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后记得关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }
}
