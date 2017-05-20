# PopupWindow弹出窗体

## 1.案例1 在view的右方显示窗体

	 /**
     * 显示弹出窗口
     * @param view 需要在哪里弹出
     */
    private void showPopupWindow(View view) {
        //将弹出窗口需要展示的布局加载进来
        View popupView = View.inflate(mContext,R.layout.popupwindow_layout,null);

        //1, 创建popupWindow  因为上面的View是LinearLayout,所以这里用的规则也是LinearLayout的
        //参数:view,宽度,高度,是否能获取焦点
        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,true);
        //2, 设置popupWindow的背景颜色  如果不设置,那么返回按钮将无法返回    这里设置成透明的
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        //3, 将popupWindow显示到view的左下角
        //参数:view,往x方向偏移量,往y方向的偏移量
        //这里最终是放在view的正右方
        popupWindow.showAsDropDown(view,50,-view.getHeight()-20);
    }
