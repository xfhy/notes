# 自定义View

## 1. 执行顺序

![](http://olg7c0d2n.bkt.clouddn.com/17-5-31/47456363.jpg)

先是调用Activity的 onCreate()方法 ,执行完onCreate()方法之后,
再是View自己的 onMeasure()->onLayout()->onDraw() ;

## 2. dispatchDraw()

绘制VIew本身的内容，通过调用View.onDraw(canvas)函数实现,绘制自己的孩子通过dispatchDraw（canvas）实现

画完背景后，draw过程会调用onDraw(Canvas canvas)方法，然后就是dispatchDraw(Canvas canvas)方法,
dispatchDraw()主要是分发给子组件进行绘制，我们通常定制组件的时候重写的是onDraw()方法。值得注意的是ViewGroup容器组件的绘制
，当它没有背景时直接调用的是dispatchDraw ()方法,而绕过了draw()方法，当它有背景的时候就调用draw()方法，而draw()方法里包含了
dispatchDraw()方法的调用。因此要在ViewGroup上绘制东西的时候往往重写的是
dispatchDraw()方法而不是onDraw()方法，或者自定制一个Drawable，重写它的draw(Canvas c)和getIntrinsicWidth(),getIntrinsicHeight()方法，然后设为背景


## 3. 自定义属性

1. 在`res/value/`下新建`attrs.xml`声明节点declare-styleable

		<?xml version="1.0" encoding="utf-8"?>
		<resources>
		    
		    <declare-styleable name="ToggleView">
		        <!--fornat属性用来指定属性的类型
			      dimension表示大小
			      reference引用属性
			      color是颜色
			      -->
			      <attr name="switch_background" format="reference" />
			      <attr name="slide_button" format="reference" />
			      <attr name="switch_state" format="boolean" />
		    </declare-styleable>
		    
		</resources>

2. R会自动创建变量

		attr 3个变量
		styleable 一个int数组, 3个变量(保存位置)

3. 在用到自定义控件的xml配置声明的属性/ 注意添加命名空间
	
	    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
		    xmlns:tools="http://schemas.android.com/tools"
			xmlns:attr="http://schemas.android.com/apk/res-auto"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent"
		    tools:context="com.xfhy.toggleview.MainActivity$PlaceholderFragment" >
		
		    <com.xfhy.toggleview.view.ToggleView
		        android:id="@+id/tv_switch"
		        android:layout_width="wrap_content"
		        android:layout_height="wrap_content"
		        attr:switch_background="@drawable/switch_background"
		        attr:slide_button="@drawable/slide_button"
		        attr:switch_state="false"
		        android:layout_centerInParent="true" />
		
		</RelativeLayout>
4. 然后在自定义控件的构造函数中获取并使用

		TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.ViewPagerIndicator);
        mTabVisibleCount = attributes.getInt(
                R.styleable.ViewPagerIndicator_visible_tab_count,
                COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        // 用完必须释放
        attributes.recycle();

## 4.自定义控件占用某子控件的监听器

假如自定义一个ViewPagerIndicator,在控件内部需要用到ViewPager的监听器,则需要给外部暴露一个接口,因为用户也可能想要ViewPager的监听器来做另外的事情.

![](http://olg7c0d2n.bkt.clouddn.com/17-6-6/78542851.jpg)

## 5.设置字体大小

记得在前面加一个单位:`TypedValue.COMPLEX_UNIT_SP`
textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

## 6.getDimension() 

获取的是px,系统会自动给我们转成px.
