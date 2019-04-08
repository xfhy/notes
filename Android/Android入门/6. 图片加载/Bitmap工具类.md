
封装一些常用功能

```java
class BitmapUtil {

    public static Bitmap getBitmapById(Context context, int resId) {
        if (context == null || resId <= 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        //搞成这个,变为2字节  比ARGB_8888节约一半的消耗
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        //我只是简单测量一下  解析bitmap只获取尺寸信息，不生成像素数据
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        //现在可不是简单的测量了
        options.inJustDecodeBounds = false;
        //返回一个缩小的图像,以节约内存.  比如inSampleSize是4,那么宽高返回的是1/4,像素点是原来的1/16  必须是2的冥
        options.inSampleSize = BitmapUtil.computeSampleSize(options,
                PixelUtil.dp2px(context, 200), PixelUtil.dp2px(context, 150));
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    /**
     * @param options      bitmap配置信息
     * @param desireWidth  目标宽度
     * @param desireHeight 目标高度
     * @return 合适的inSimpleSize
     */
    public static int computeSampleSize(BitmapFactory.Options options, int desireWidth, int desireHeight) {
        if (options == null || desireWidth == 0 || desireHeight == 0) {
            return 1;
        }
        double radioW = options.outWidth * 1.0 / desireWidth;
        double radioH = options.outHeight * 1.0 / desireHeight;
        double minRadio = Math.min(radioW, radioH);
        double n = 1.0;
        //API中对于inSimpleSize的注释：最终的inSimpleSize应该为2的倍数，我们应该向上取与压缩比最接近的2的倍数。
        while (n <= minRadio) {
            n *= 2;
        }
        return (int) n;
    }
}

```