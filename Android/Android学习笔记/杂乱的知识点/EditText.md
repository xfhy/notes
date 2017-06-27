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

## 4. 光标在最前面

属性加上`android:gravity="top"`

## 5.EditText外面有一层包围圈

类似于下面这样
![](http://olg7c0d2n.bkt.clouddn.com/17-6-27/97306196.jpg)

自定义一个drawable,自己画一个background,是layer-list

	<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">

    <!--搜索界面的输入文字框背景-->

	    <item>
	
	        <!--正方形-->
	        <shape android:shape="rectangle">
	
	            <!--颜色-->
	            <solid android:color="@color/color_alpha_0"/>
	            <!--圆角-->
	            <corners android:radius="3dp"/>
	            <!--描边-->
	            <stroke
	                android:width="0.5px"
	                android:color="@color/color_alpha_6"/>
	
	        </shape>
	
	    </item>
	
	</layer-list>

## 6.设置EditText光标Cursor颜色及粗细

在android的输入框里，如果要修改光标的颜色及粗细步骤如下两步即可搞定：

1.在资源文件drawable下新建一个光标控制color_cursor.xml

    <?xml version="1.0" encoding="utf-8"?>
    <shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
        <size android:width="1dp" />
        <solid android:color="#008000"  />
    </shape>

2.设置EditText：`android:textCursorDrawable="@drawable/color_cursor"`


## 7.让EditText默认把软件盘的回车改为搜索

	android:imeOptions="actionSearch"
    android:inputType="text"
## 8.EditText监听回车事件

	//设置当enter按下时的监听事件
     mEdit.setOnEditorActionListener(this);

## 9.让EditText  动态显示和隐藏软键盘

	public static void hideSoftKeyboard(EditText editText, Context context) {  
                     if (editText != null && context != null) {  
                         InputMethodManager imm = (InputMethodManager) context  
                                 .getSystemService(Context.INPUT_METHOD_SERVICE);  
                         imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);  
                     }  
                 }  
                 public static void showSoftKeyboard(EditText editText, Context context) {  
                     if (editText != null && context != null) {  
                         InputMethodManager imm = (InputMethodManager) context  
                                 .getSystemService(Activity.INPUT_METHOD_SERVICE);  
                         imm.showSoftInput(editText, 0);  
                     }  
                 }  

## 10.监听EditText文本输入的变化

	 mEdit.addTextChangedListener(this);

## 11.让EditText不能自动获取焦点

解决之道：在EditText的父级控件中找一个，设置成

      android:focusable="true"  
       android:focusableInTouchMode="true"

## 12.EditText 下划线颜色更改

styles里apptheme（app主题）
    加 <item name="colorAccent">@color/primary_blue（其他颜色也行）</item>
## 13.EditText默认时不弹出软键盘的方法

在 父 Activity 中 onCreate 中加上
    
	getWindow().setSoftInputMode(   WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
