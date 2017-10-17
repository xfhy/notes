# ImageView

## 1.src属性和background属性的区别：

在API文档中我们发现ImageView有两个可以设置图片的属性，分别是：src和background
常识：
① background通常指的都是背景,而src指的是内容!!
② 当使用src填入图片时,是按照图片大小直接填充,并不会进行拉伸
而使用background填入图片,则是会根据ImageView给定的宽度来进行拉伸

## 2.adjustViewBounds设置缩放是否保存原图长宽比

> ImageView为我们提供了adjustViewBounds属性，用于设置缩放时是否保持原图长宽比！ 单独设置不起作用，需要配合maxWidth和maxHeight属性一起使用！而后面这两个属性 也是需要adjustViewBounds为true才会生效的~

- android:maxHeight:设置ImageView的最大高度
- android:maxWidth:设置ImageView的最大宽度

示例:

	<!-- 限制了最大宽度与高度,并且设置了调整边界来保持所显示图像的长宽比-->
    <ImageView
        android:id="@+id/imageView2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="5px"
        android:adjustViewBounds="true"
        android:maxHeight="200px"
        android:maxWidth="200px"
        android:src="@mipmap/meinv" />

## 3.scaleType设置缩放类型

- fitXY:对图像的横向与纵向进行独立缩放,使得该图片完全适应ImageView,但是图片的横纵比可能会发生改变
- fitStart:保持纵横比缩放图片,知道较长的边与Image的编程相等,缩放完成后将图片放在ImageView的左上角
- fitCenter:同上,缩放后放于中间;
- fitEnd:同上,缩放后放于右下角;
- center:保持原图的大小，显示在ImageView的中心。当原图的size大于ImageView的size，超过部分裁剪处理。
- centerCrop:保持横纵比缩放图片,知道完全覆盖ImageView,可能会出现图片的显示不完全
- centerInside:保持横纵比缩放图片,直到ImageView能够完全地显示图片
- matrix:默认值，不改变原图的大小，从ImageView的左上角开始绘制原图， 原图超过ImageView的部分作裁剪处理

## 4.灰色背景

	//设置正在播放的视频图片为灰色
    ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(0);

    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
    //想要恢复颜色的话,设置为null
    holder.videoImg.setColorFilter(filter);
