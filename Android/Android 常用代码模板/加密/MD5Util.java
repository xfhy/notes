package com.xfhy.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xfhy on 2017/4/17.
 * 进行字符串的加密  算法是MD5
 */

public class MD5Util {

    private static final String Confused = "@#^&.'ld'as";  //加盐   混淆

    /**
     * 将字符串加密   算法是MD5
     * 已加盐  混淆处理
     *
     * @param str 需要加密的字符串
     * @return 返回MD5算法加密之后的32位字符
     */
    public static String encoder(String str) {

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bs = digest.digest(str.getBytes());
            //System.out.println(bs.length);   //这里的长度是16位
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bs) {
                int i = b & 0xff;   //将字节码转换成整数
                String hexString = Integer.toHexString(i);  //将整数转换成16进制数
                if (hexString.length() == 1) {   //如果是1位  则强行弄成2位
                    hexString = "0" + hexString;
                }
                stringBuffer.append(hexString);   //拼接字符串
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";

    }

}
