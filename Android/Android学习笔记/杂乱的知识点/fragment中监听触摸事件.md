# fragment中监听触摸事件

1,首先需要定义一个接口,用来"观察"触摸事件

	public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }

2,再定义一个List集合存储这些监听器,因为可能有多个fragment需要监听触摸事件

	/**
     * fragment的触摸事件监听器
     */
    private List<MyOnTouchListener> onTouchListeners = new ArrayList<>();

3,再到Activity中写入如下代码,我觉得其实就是将传入Activity的事件悄悄地传递给fragment,让fragment知道此次的触摸事件是什么类型(ACTION_DOWM,ACTION_MOVE,ACTION_UP)的

	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            if (listener != null) {
                listener.onTouch(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }

4,最后,再到fragment中注册触摸事件即可

	onTouchListener = new MainActivity.MyOnTouchListener() {
            @Override
            public boolean onTouch(MotionEvent ev) {

                Log.e(TAG, "onTouch: "+ev.getAction());
				
                return false;
            }
        };
        ((MainActivity) getActivity()).registerMyOnTouchListener(onTouchListener);
