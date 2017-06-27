# TextInputLayout 

## 1. 友好的输入框

友好提示

	if (TextUtils.isEmpty(userName)) {
            mUserInputLayout.setErrorEnabled(true);
            mUserInputLayout.setError("请输入用户名");
            return ;
        } else {
            mUserInputLayout.setError("");
            mUserInputLayout.setErrorEnabled(false);
        }
        
## 2. demo

	<?xml version="1.0" encoding="utf-8"?>
	<android.support.design.widget.CoordinatorLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    tools:context="com.xfhy.design.SecondActivity">
	
	    <android.support.design.widget.TextInputLayout
	        android:id="@+id/design_input_username"
	        android:layout_width="match_parent"
	        android:layout_height="100dp">
	
	        <EditText
	            android:id="@+id/et_input_username"
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content"
	            android:hint="用户名"
	            />
	
	    </android.support.design.widget.TextInputLayout>
	
	    <android.support.design.widget.TextInputLayout
	        android:id="@+id/design_input_pass"
	        android:layout_width="match_parent"
	        android:layout_height="100dp"
	        android:layout_marginTop="100dp">
	
	        <EditText
	            android:id="@+id/et_input_pass"
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content"
	            android:hint="密码"
	            />
	
	    </android.support.design.widget.TextInputLayout>
	
	    <Button
	        android:id="@+id/btn_second_login"
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:layout_marginTop="200dp"
	        android:text="登录"/>
	
	</android.support.design.widget.CoordinatorLayout>

