# Android:动画

## 1.帧动画
## 2.补间动画:平移,缩放,旋转,渐变

- 补间动画不会改变控件的位置,即使设置了setFillAfter(true),但是控件其实还是在原来的位置.障眼法.
- 官方更推荐使用xml来写补间动画

### 2.1 平移

	<?xml version="1.0" encoding="utf-8"?>
	   <translate xmlns:android="http://schemas.android.com/apk/res/android"
	              android:fromXDelta="0%"
	              android:toXDelta="50%p">
	   
	       <!--
	           1. 绝对值  相对于View的坐标系
	           2. 百分比值,相对于自己的坐标系
	           3. 百分比加p,相对于父控件的坐标系
	           -->
	   
	   </translate>
	   
	   //平移动画
	   Animation animation = AnimationUtils.loadAnimation(this, R.anim.image_translate);
	   animation.setDuration(2000);
	   mImage.startAnimation(animation);

### 2.2 旋转

	<?xml version="1.0" encoding="utf-8"?>
	<rotate xmlns:android="http://schemas.android.com/apk/res/android"
	        android:duration="2000"
	        android:fromDegrees="0"
	        android:pivotX="50%"
	        android:pivotY="50%"
	        android:toDegrees="360"
	    >
	
	    <!--旋转-->
	
	    <!--
	        android:duration="2000"    时长
	        android:fromDegrees="0"   从哪个角度开始旋转
	        android:pivotX="50%"   旋转时x中心点
	        android:pivotY="50%"
	        android:toDegrees="360"
	        -->
	
	</rotate>

### 2.3 缩放

	<?xml version="1.0" encoding="utf-8"?>
	<scale xmlns:android="http://schemas.android.com/apk/res/android"
	       android:fromXScale="0"
	       android:fromYScale="0"
	       android:pivotX="50%"
	       android:pivotY="50%"
	       android:toXScale="1"
	       android:toYScale="0.5"
	       android:duration="2000"
	    >
	
	    <!--
	        android:fromXScale="0"   x缩放起始点
	       android:fromYScale="0"    y缩放起始点
	       android:pivotX="50%"      中心
	       android:pivotY="50%"
	       android:toXScale="1"      x缩放结束点
	       android:toYScale="0.5"
	       android:duration="2000"
	
	    -->
	
	</scale>

### 2.4 渐变

	<?xml version="1.0" encoding="utf-8"?>
	<alpha xmlns:android="http://schemas.android.com/apk/res/android"
	       android:duration="2000"
	       android:fromAlpha="1"
	       android:toAlpha="0"
	
	    >
	
	</alpha>

## 3.属性动画(API11):根绝属性然后改变

> 修改固有属性,实现动画效果 真实地属性改变

//属性动画    参数:需要动画的控件,需要改变的属性,改属性值从哪里变到哪里

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mImage, "translationX",0, 100,
                        50, 300, 10);
                objectAnimator.setDuration(4000);
                objectAnimator.start();  //开始执行属性动画
                
- 可以中途停止,可控性更高
- 能用补间动画实现的,尽量使用补间动画(补间动画没有那么多消耗)
- 可以修改的属性,都可以做成动画

## 4.转场动画(API 21)

- 出现的并不多(毕竟国内..)
- 整体转场
- 共享元素
- 水波纹揭露
