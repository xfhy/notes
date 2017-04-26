# EditText

## 1.监听器

addTextChangedListener  :文本变化观察者
setOnEditorActionListener: 键盘回车事件

## 2.常用属性

- android:password="true"  这条可以让EditText显示的内容自动为星号，输入时内容会在1秒内变成*字样。
- android:numeric="true" 这条可以让输入法自动变为数字输入键盘，同时仅允许0-9的数字输入
- android:capitalize="abcde" 这样仅允许接受输入abcde，一般用于密码验证
- android:hint="密码"  设置显示的提示信息
- android:maxLine="2"  设置最多多少行
- android:inputType="number"  输入类型
- android:imeOptions="actionDone"  键盘回车的类型

			下面列出比较经常用到的几个属性以及替换的文本外观：
		　　actionUnspecified        未指定         EditorInfo.IME_ACTION_UNSPECIFIED.  
		　　actionNone                 动作            EditorInfo.IME_ACTION_NONE 
		　　actionGo                    去往            EditorInfo.IME_ACTION_GO
		　　actionSearch               搜索            EditorInfo.IME_ACTION_SEARCH    
		　　actionSend                 发送            EditorInfo.IME_ACTION_SEND   
		　　actionNext                下一项           EditorInfo.IME_ACTION_NEXT   
		　　actionDone               完成              EditorInfo.IME_ACTION_DONE 

## 3.输入完成,关闭输入法

> 有时候输入完成,需要关闭输入法,不然用户体验效果不好

		etQueryPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //关闭输入法
                InputMethodManager inputMethodManager = (InputMethodManager)
                        QueryAddressActivity.this.getSystemService
                                (Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(etQueryPhone.getWindowToken(), 0);
                return true;
            }
        });

## 4.抖动EditText

1.在res/anim下创建shake.xml文件

	<?xml version="1.0" encoding="utf-8"?>
	<translate xmlns:android="http://schemas.android.com/apk/res/android"
	           android:duration="1000"
	           android:fromXDelta="0"
	           android:interpolator="@anim/cycle_7"
	           android:toXDelta="10">
	
	    <!--duration为抖动时间，fromXDelta，toXDelta抖动幅度,interpolator是插补器-->
	
	</translate>
2.在res/anim下创建cycle_7.xml

	<?xml version="1.0" encoding="utf-8"?>
	<cycleInterpolator xmlns:android="http://schemas.android.com/apk/res/android"
	                   android:cycles="6">
	
	    <!--设置次数-->
	
	</cycleInterpolator>
3.使用时

	Animation shake = AnimationUtils.loadAnimation(QueryAddressActivity.this, R.anim.shake);
    etQueryPhone.startAnimation(shake);
