# Android View的点击事件无效 解决办法

> 背景:有时候一个控件,即设置了点击事件监听器setOnClickListener(this);,又设置了setOnTouchListener(this); 这个时候点击事件就会无效.

解决方案:当即需要监听点击事件 又需要监听触摸事件   onTouch()必须返回false,否则点击事件无效

	public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   //按下
                break;
            case MotionEvent.ACTION_MOVE:   //移动
                break;
            case MotionEvent.ACTION_UP:     //抬起
                break;
            default:
                break;
        }

        //当即需要监听点击事件 又需要监听触摸事件   这里必须返回false,否则点击事件无效
        return false;
    }

	public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_drag:   //处理iv_drag的点击事件
                ToastUtil.show("你点我了");
                break;
        }
    }
