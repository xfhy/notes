# 自定义View  让文字居中画出来

在onDraw()方法中调用下面的方法

	/**
     * 画文字  居中
     *
     * @param canvas
     */
    private void canvasSche(Canvas canvas) {
        String text = String.valueOf(schedule);
        //拿到字符串的宽度
        mTextWidth = mTextPaint.measureText(text);
        //文字的x轴坐标
        mTextX = (getWidth() - mTextWidth) / 2;
        //文字的y轴坐标
        //描述给定文本大小的字体的各种指标的类。
        // 记住，Y值增加下降，所以这些值将是正的，测量距离上升的值将为负。 这个类由getFontMetrics（）返回。
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextY = getHeight() / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        //画字 居中显示
        canvas.drawText(text, mTextX, mTextY, mTextPaint);
    }
