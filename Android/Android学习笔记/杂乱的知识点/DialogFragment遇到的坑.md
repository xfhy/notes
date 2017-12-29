# DialogFragment

> 平时使用对话框时,如果需要输入值,或者是其他什么很多的操作,官方建议操作DialogFragment.

平时在使用时,其实是好好的,今天我突发奇想,快速得点击按钮让DialogFragment快速的显示隐藏,于是,,,,程序就崩溃了....no zuo no die,,哈哈,崩溃这么大的bug,必须修复.

报错信息如下:

```java
java.lang.IllegalStateException: Fragment already added: FilterDialogFragment{2ba55429 #1 }
	at android.support.v4.app.FragmentManagerImpl.addFragment(FragmentManager.java:1892)
	at android.support.v4.app.BackStackRecord.executeOps(BackStackRecord.java:760)
	at android.support.v4.app.FragmentManagerImpl.executeOps(FragmentManager.java:2590)
	at android.support.v4.app.FragmentManagerImpl.executeOpsTogether(FragmentManager.java:2377)
	at android.support.v4.app.FragmentManagerImpl.removeRedundantOperationsAndExecute(FragmentManager.java:2332)
	at android.support.v4.app.FragmentManagerImpl.execPendingActions(FragmentManager.java:2239)
	at android.support.v4.app.FragmentManagerImpl$1.run(FragmentManager.java:700)
	at android.os.Handler.handleCallback(Handler.java:739)
	at android.os.Handler.dispatchMessage(Handler.java:95)
	at android.os.Looper.loop(Looper.java:135)
	at android.app.ActivityThread.main(ActivityThread.java:5418)
	at java.lang.reflect.Method.invoke(Native Method)
	at java.lang.reflect.Method.invoke(Method.java:372)
	at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1037)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:832)
```

可以看到是重复添加了,解决办法:判断是否已经添加了DialogFragment.
```java
if (mFilterDialogFragment == null) {
    mFilterDialogFragment = FilterDialogFragment.newInstance(mHKeywordsRes);
} else if(!mFilterDialogFragment.isAdded()){
    mFilterDialogFragment.show(getSupportFragmentManager(), "");
}
```

