# ProgressBar

# 1.更改ProgressBar的颜色

1. 在res/drawable下创建my_progress_style.xml文件

	<?xml version="1.0" encoding="utf-8"?>
	<rotate xmlns:android="http://schemas.android.com/apk/res/android"
	        android:fromDegrees="0"
	        android:pivotX="50%"
	        android:pivotY="50%"
	        android:toDegrees="360">
	
	    <!--
	        这是进度条的颜色设置
	
	        rotate旋转动画,pivotX是X中心点
	    -->
	
	
	    <!--
	        android:innerRadiusRatio="3"  环的内半径表示为环的宽度的比率
	        android:thicknessRatio="8"  环的厚度表示为环的宽度的比率
	        android:useLevel="false" 是否使用可绘制的级别值（请参阅{@link android.graphics.drawable.Drawable＃getLevel（）}）来缩放形状。 缩放行为取决于形状类型。 对于“环”，角度从0到360。对于所有其他类型，没有影响。 默认值为true。
	    -->
	    <shape
	        android:innerRadiusRatio="3"
	        android:shape="ring"
	        android:thicknessRatio="8"
	        android:useLevel="false"
	        >
	
	        <!--
	        android:type="sweep" 梯度类型 默认类型是线性的。
	        -->
	        <gradient
	            android:centerY="0.50"
	            android:startColor="#00ffffff"
	            android:endColor="@color/blueStatus"
	            android:type="sweep"
	            android:useLevel="false"/>
	
	    </shape>
	
	</rotate>
2. 然后在使用ProgressBar的时候

		<ProgressBar
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:indeterminateDrawable="@drawable/my_progress_style"
	        />

