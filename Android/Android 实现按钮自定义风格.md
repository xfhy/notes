# Android 实现按钮自定义风格 #
<font size="4"><b>
1. 在drawable里面新建一个selector的xml文件,然后在xml文件里面写代码,然后在布局时将按钮的背景设置成该xml文件.<br/>

	 <shape>     
	    <!-- 实心 -->     
	    <solid android:color="#ff9d77"/>   
  
	    <!-- 渐变 -->     
	    <gradient     
	        android:startColor="#ff8c00"     
	        android:endColor="#FFFFFF"     
	        android:angle="270" />    
 
	    <!-- 描边 -->     
	    <stroke     
	        android:width="2dp"     
	        android:color="#dcdcdc" />     

	    <!-- 圆角 -->     
	    <corners     
	        android:radius="2dp" />     

	    <padding     
	        android:left="10dp"     
	        android:top="10dp"     
	        android:right="10dp"     
	        android:bottom="10dp" />     
	 </shape>  

</b></font>