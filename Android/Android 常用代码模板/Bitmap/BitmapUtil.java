package com.xfhy.bitmap.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;

/**
 * Created by feiyang on 2018/6/26 11:38
 * Description :
 */
public class BitmapUtil {

    /**
     * 从资源加载Bitmap并根据宽高压缩后返回
     *
     * @param resource  Resources
     * @param resId     资源id
     * @param reqWidth  需要的宽
     * @param reqHeight 需要的高
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources resource, int resId, int reqWidth,
                                                         int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //将inJustDecodeBounds设置为true  仅仅是为了测量  这样消耗小
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resource, resId, options);

        //计算适合的inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //重新设置inJustDecodeBounds为false
        options.inJustDecodeBounds = false;
        //decode资源 生成Bitmap
        return BitmapFactory.decodeResource(resource, resId, options);
    }

    /**
     * 计算合适的inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //计算图片的原本高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        //原图宽高比req的大 那么需要缩小
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            //缩小  inSampleSize越大,到时候压缩也就越小,结果压缩为1/inSampleSize
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
