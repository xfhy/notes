# ImageView

- ImageView的android:scaleType="fitXY"可以适配XY轴,全屏就可以这样设置

# 1.灰色背景

	//设置正在播放的视频图片为灰色
    ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(0);

    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
    //想要恢复颜色的话,设置为null
    holder.videoImg.setColorFilter(filter);
