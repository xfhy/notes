> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://blog.csdn.net/u010410408/article/details/51695307 版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/u010410408/article/details/51695307 [![](https://img-blog.csdnimg.cn/20190428183830702.jpg)](https://h5.youzan.com/v2/goods/3f0g1ehtlriv3)

# <a></a>Window

## <a></a>基础

        其子类为 PhoneWindow。

## <a></a>构造方法

        在 Activity#attch() 中，会执行 new PhoneWindow(this)，因此 Window 中的 mContext 成员变量为它所关联的 Activity。当然，在 Activity 中可以通过 mWindow 指向一个 PhoneWindow 实例。

## <a></a>setWindowManager()

        在 Activity#attach() 中调用，该方法会对 mWindowManager 变量进行赋值，其为一个 WindowManagerImpl 实例。

## <a></a>setCallback()

        在 Activity#attach() 中调用，为 mCallback 赋值，其值与 mContext 一样，都是指向同一个 Activity 实例。

# <a></a>PhoneWindow

## <a></a>setContentView

调用 Activity#setContentView() 最终会调用到 PhoneWindow#setContentView，先罗列里面会用到的方法。如下：

<pre>    //可以看出它mDecor为DecorView对象
    protected DecorView generateDecor() {
        return new DecorView(getContext(), -1);
    }
    //findViewById()如下：
    public View findViewById(@IdRes int id) {
        return getDecorView().findViewById(id);
    }
	//获取theme中关于window属性的设置
    public final TypedArray getWindowStyle() {
        synchronized (this) {
            if (mWindowStyle == null) {
                mWindowStyle = mContext.obtainStyledAttributes(
                        com.android.internal.R.styleable.Window);
            }
            return mWindowStyle;
        }
    }
    public final TypedArray obtainStyledAttributes(@StyleableRes int[] attrs) {
        return getTheme().obtainStyledAttributes(attrs);
    }
	//是否有指定的feature。
    public boolean hasFeature(int feature) {
        return (getFeatures() & (1 << feature)) != 0;
    }
	//设置feature。上面的getFeatures()只是返回mFeatures
    public boolean requestFeature(int featureId) {
        final int flag = 1<<featureId;
        mFeatures |= flag;
        mLocalFeatures |= mContainer != null ? (flag&~mContainer.mFeatures) : flag;
        return (mFeatures&flag) != 0;
    }
    //可以看出它mDecor为DecorView对象
    protected DecorView generateDecor() {
        return new DecorView(getContext(), -1);
    }

    //findViewById()如下：
    public View findViewById(@IdRes int id) {
        return getDecorView().findViewById(id);
    }
	//获取theme中关于window属性的设置
    public final TypedArray getWindowStyle() {
        synchronized (this) {
            if (mWindowStyle == null) {
                mWindowStyle = mContext.obtainStyledAttributes(
                        com.android.internal.R.styleable.Window);
            }
            return mWindowStyle;
        }
    }
    public final TypedArray obtainStyledAttributes(@StyleableRes int[] attrs) {
        return getTheme().obtainStyledAttributes(attrs);
    }
	//是否有指定的feature。
    public boolean hasFeature(int feature) {
        return (getFeatures() & (1 << feature)) != 0;
    }
	//设置feature。上面的getFeatures()只是返回mFeatures
    public boolean requestFeature(int featureId) {
        final int flag = 1<<featureId;
        mFeatures |= flag;
        mLocalFeatures |= mContainer != null ? (flag&~mContainer.mFeatures) : flag;
        return (mFeatures&flag) != 0;
    }
</pre>

setContentView() 本身代码如下：

<pre>    @Override
    public void setContentView(int layoutResID) {
        if (mContentParent == null) {
            installDecor();
        } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            mContentParent.removeAllViews();
        }
        if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            final Scene newScene = Scene.getSceneForLayout(mContentParent, layoutResID,
                    getContext());
            transitionTo(newScene);
        } else {
            mLayoutInflater.inflate(layoutResID, mContentParent);
        }
        final Callback cb = getCallback();
        if (cb != null && !isDestroyed()) {
            cb.onContentChanged();
        }
    }
    @Override
    public void setContentView(int layoutResID) {
        if (mContentParent == null) {
            installDecor();
        } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            mContentParent.removeAllViews();
        }

        if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            final Scene newScene = Scene.getSceneForLayout(mContentParent, layoutResID,
                    getContext());
            transitionTo(newScene);
        } else {
            mLayoutInflater.inflate(layoutResID, mContentParent);
        }
        final Callback cb = getCallback();
        if (cb != null && !isDestroyed()) {
            cb.onContentChanged();
        }
    }
</pre>

  首先看 installDecor 的作用：创建一个 DecorView 对象，并赋值给 mDecor。同时为 mContentParent 进行赋值。
        其次，如果没设置 FEATURE_CONTENT_TRANSITIONS，那就移除所有的原来组件，并且调用 mLayoutInflater.inflate(layoutResID, mContentParent); 将新布局添加到 mContentParent 中。
如果有 FEATURE_CONTENT_TRANSITIONS，会通过 transitionTo 进行界面切换，切换过程会有一个淡入淡出的效果。

        最后会调用 onContentChanged() 回调。

        整体逻辑就这么多，关键是要看 mContentParent 指的是什么——因为它是我们写的布局的父类。

## <a></a>generateLayout(DecorView)

        因为在 installDecor() 中会调用 generateLayout(mDecor) 生成 mContentParent, 所以先看 generateLayout(mDecor) 方法。
        其方法看着比较多，但实质就是根据该 activity 的 theme 找到对应的布局，inflate 该布局并返回 id 为 android:id/content 的 ViewGroup。

<pre>     protected ViewGroup generateLayout(DecorView decor) {
        TypedArray a = getWindowStyle();// Apply data from current theme.
        //一个输出语句，省略
		//获取theme中的windowIsFloating属性
        mIsFloating = a.getBoolean(R.styleable.Window_windowIsFloating, false);
        int flagsToUpdate = (FLAG_LAYOUT_IN_SCREEN|FLAG_LAYOUT_INSET_DECOR)
                & (~getForcedWindowFlags());
        if (mIsFloating) {
            setLayout(WRAP_CONTENT, WRAP_CONTENT);
            setFlags(0, flagsToUpdate);
        } else {
            setFlags(FLAG_LAYOUT_IN_SCREEN|FLAG_LAYOUT_INSET_DECOR, flagsToUpdate);
        }
        //根据指定的属性调用requestFeatures()设置一些feature和flags，具体代码略
        if (a.getBoolean(R.styleable.Window_windowNoTitle, false)) {
            requestFeature(FEATURE_NO_TITLE);//无标题feature
        } else if (a.getBoolean(R.styleable.Window_windowActionBar, false)) {
            requestFeature(FEATURE_ACTION_BAR);// Don't allow an action bar if there is no title.
        }
	//略了一部分代码
        if (a.getBoolean(R.styleable.Window_windowSwipeToDismiss, false)) {
            requestFeature(FEATURE_SWIPE_TO_DISMISS);//侧滑返回。但很可惜api21以后才能用，而且还有bug
        }
       //略了一部分setFlags与requestFeature代码，它们都是根据a中设置的一些属性值
        int layoutResource;
        int features = getLocalFeatures();
        //根据features值获取layoutResourece的值，它代表一个布局的id。代码略
        mDecor.startChanging();
	//添加布局，并将布局添加到decor上。
        View in = mLayoutInflater.inflate(layoutResource, null);
        decor.addView(in, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mContentRoot = (ViewGroup) in;
	//获取添加自己View的viewgroup，layoutResource会有好几个，但都会含义一个id为@android:id/content的ViewGroup，例如可以见下面的screen_swipe_dismiss.xml
        ViewGroup contentParent = (ViewGroup)findViewById(ID_ANDROID_CONTENT);
        //一些关于features进行的设置
        mDecor.finishChanging();
        return contentParent;
    }
     protected ViewGroup generateLayout(DecorView decor) {
        TypedArray a = getWindowStyle();// Apply data from current theme.
        //一个输出语句，省略
		//获取theme中的windowIsFloating属性
        mIsFloating = a.getBoolean(R.styleable.Window_windowIsFloating, false);
        int flagsToUpdate = (FLAG_LAYOUT_IN_SCREEN|FLAG_LAYOUT_INSET_DECOR)
                & (~getForcedWindowFlags());
        if (mIsFloating) {
            setLayout(WRAP_CONTENT, WRAP_CONTENT);
            setFlags(0, flagsToUpdate);
        } else {
            setFlags(FLAG_LAYOUT_IN_SCREEN|FLAG_LAYOUT_INSET_DECOR, flagsToUpdate);
        }

        //根据指定的属性调用requestFeatures()设置一些feature和flags，具体代码略
        if (a.getBoolean(R.styleable.Window_windowNoTitle, false)) {
            requestFeature(FEATURE_NO_TITLE);//无标题feature
        } else if (a.getBoolean(R.styleable.Window_windowActionBar, false)) {
            requestFeature(FEATURE_ACTION_BAR);// Don't allow an action bar if there is no title.
        }
	//略了一部分代码
        if (a.getBoolean(R.styleable.Window_windowSwipeToDismiss, false)) {
            requestFeature(FEATURE_SWIPE_TO_DISMISS);//侧滑返回。但很可惜api21以后才能用，而且还有bug
        }
       //略了一部分setFlags与requestFeature代码，它们都是根据a中设置的一些属性值
        int layoutResource;
        int features = getLocalFeatures();
        //根据features值获取layoutResourece的值，它代表一个布局的id。代码略
        mDecor.startChanging();
	//添加布局，并将布局添加到decor上。
        View in = mLayoutInflater.inflate(layoutResource, null);
        decor.addView(in, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mContentRoot = (ViewGroup) in;
	//获取添加自己View的viewgroup，layoutResource会有好几个，但都会含义一个id为@android:id/content的ViewGroup，例如可以见下面的screen_swipe_dismiss.xml
        ViewGroup contentParent = (ViewGroup)findViewById(ID_ANDROID_CONTENT);
        //一些关于features进行的设置
        mDecor.finishChanging();

        return contentParent;
    }
</pre>

        具体的解释上注释中。其中 ID_ANDROID_CONTRENT 定义如下：

<pre>     /**
     * The ID that the main layout in the XML layout file should have.
     */
    public static final int ID_ANDROID_CONTENT = com.android.internal.R.id.content;
     /**
     * The ID that the main layout in the XML layout file should have.
     */
    public static final int ID_ANDROID_CONTENT = com.android.internal.R.id.content;
</pre>

        从这个注释中我们还可以看出 contentParent 的作用。

## <a></a>installDecor()

再分析下 installDecor() 代码：

<pre>    private void installDecor() {
        if (mDecor == null) {
			//生成mDecor，并进行一系列的设置
            mDecor = generateDecor();
            mDecor.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);//子View不获取焦点时，它才获取焦点
            mDecor.setIsRootNamespace(true);
            if (!mInvalidatePanelMenuPosted && mInvalidatePanelMenuFeatures != 0) {
                mDecor.postOnAnimation(mInvalidatePanelMenuRunnable);
            }
        }
        if (mContentParent == null) {
            mContentParent = generateLayout(mDecor);
            // Set up decor part of UI to ignore fitsSystemWindows if appropriate.
            mDecor.makeOptionalFitsSystemWindows();
            //mDecor是一个ViewGroup，这里省略的就是对其中的某些子View进行初始化设置的代码
            if (mDecor.getBackground() == null && mBackgroundFallbackResource != 0) {
                mDecor.setBackgroundFallback(mBackgroundFallbackResource);
            }
            //一些关于TransitionManager的初始化工作
        }
    }
    private void installDecor() {
        if (mDecor == null) {
			//生成mDecor，并进行一系列的设置
            mDecor = generateDecor();
            mDecor.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);//子View不获取焦点时，它才获取焦点
            mDecor.setIsRootNamespace(true);
            if (!mInvalidatePanelMenuPosted && mInvalidatePanelMenuFeatures != 0) {
                mDecor.postOnAnimation(mInvalidatePanelMenuRunnable);
            }
        }
        if (mContentParent == null) {
            mContentParent = generateLayout(mDecor);
            // Set up decor part of UI to ignore fitsSystemWindows if appropriate.
            mDecor.makeOptionalFitsSystemWindows();
            //mDecor是一个ViewGroup，这里省略的就是对其中的某些子View进行初始化设置的代码

            if (mDecor.getBackground() == null && mBackgroundFallbackResource != 0) {
                mDecor.setBackgroundFallback(mBackgroundFallbackResource);
            }
            //一些关于TransitionManager的初始化工作
        }
    }
</pre>

  主要过程分为两部分：生成 mDecor。

        根据 mDecor, 通过 generateLayout(mDecor) 生成 mContentParent。然后对 mContentParent 进行一系列设置。

## <a></a>总结

整个 setContentView() 可以分为三步

        1）初始化 DecorView，然后根据设置的 features 不同为 decorView 加载不同的布局。如果已经初始化过了，则此步省略。

         2）decorView 中 find 一个 id 为 android:id/content 的 mContentParent——这即是自己布局的父容器。如果第一步不执行，则此步了不执行。

        3）通过 LayoutInflater.inflate 将自己的布局添加到 mContentParent 中。

由此也可以看出 setContentView() 的主要作用：

        1）为当前 Window 对象中的 mDecor，mContentParent 赋值，参考 [DecorView](http://blog.csdn.net/u010410408/article/details/51219244) 部分。

        2）根据 xml 中使用的 View 类，生成 View 树——这是通过 LayoutInflater 完成的。并将该 View 树添加到 mDecor 中 (具体来说应该是 mDecor 的中的 mContentParent)。这样，当 window 显示时，其关联的 View 树就可以显示出来。

## <a></a>文件

        一个 decorview 加载的布局：

<pre>	<com.android.internal.widget.SwipeDismissLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@android:id/content"
    android:fitsSystemWindows="true"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    />
	<com.android.internal.widget.SwipeDismissLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@android:id/content"
    android:fitsSystemWindows="true"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    />
</pre>

        从中可以看出，它的确有一个 id 为 android:id/content 的 ViewGroup。