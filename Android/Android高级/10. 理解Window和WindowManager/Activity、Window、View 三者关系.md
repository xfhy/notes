> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://juejin.im/post/5cee14f6e51d45777540fd40?utm_source=gold_browser_extension

#### 目录介绍

*   01.Window，View，子 Window
*   02. 什么是 Activity
*   03. 什么是 Window
*   04. 什么是 DecorView
*   05. 什么是 View
*   06. 关系结构图
*   07.Window 创建过程
*   08. 创建机制分析
    *   8.1 Activity 实例的创建
    *   8.2 Activity 中 Window 的创建
    *   8.3 DecorView 的创建

### 给自己相个亲

*   个人相亲信息：[juejin.im/post/5cff2c…](https://juejin.im/post/5cff2c8b5188252354278c70)

### 弹窗系列博客

*   [01.Activity、Window、View 三者关系](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F01.Activity%25E3%2580%2581Window%25E3%2580%2581View%25E4%25B8%2589%25E8%2580%2585%25E5%2585%25B3%25E7%25B3%25BB.md)
    *   深入分析 Activity、Window、View 三者之间的关系
*   [02.Toast 源码深度分析](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F02.Toast%25E6%25BA%2590%25E7%25A0%2581%25E6%25B7%25B1%25E5%25BA%25A6%25E5%2588%2586%25E6%259E%2590.md)
    *   最简单的创建，简单改造避免重复创建，show() 方法源码分析，scheduleTimeoutLocked 吐司如何自动销毁的，TN 类中的消息机制是如何执行的，普通应用的 Toast 显示数量是有限制的，用代码解释为何 Activity 销毁后 Toast 仍会显示，Toast 偶尔报错 Unable to add window 是如何产生的，Toast 运行在子线程问题，Toast 如何添加系统窗口的权限等等
*   [03.DialogFragment 源码分析](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F03.DialogFragment%25E6%25BA%2590%25E7%25A0%2581%25E5%2588%2586%25E6%259E%2590.md)
    *   最简单的使用方法，onCreate(@Nullable Bundle savedInstanceState) 源码分析，重点分析弹窗展示和销毁源码，使用中 show() 方法遇到的 IllegalStateException 分析
*   [04.Dialog 源码分析](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F04.Dialog%25E6%25BA%2590%25E7%25A0%2581%25E5%2588%2586%25E6%259E%2590.md)
    *   AlertDialog 源码分析，通过 AlertDialog.Builder 对象设置属性，Dialog 生命周期，Dialog 中 show 方法展示弹窗分析，Dialog 的 dismiss 销毁弹窗，Dialog 弹窗问题分析等等
*   [05.PopupWindow 源码分析](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F05.PopupWindow%25E6%25BA%2590%25E7%25A0%2581%25E5%2588%2586%25E6%259E%2590.md)
    *   显示 PopupWindow，注意问题宽和高属性，showAsDropDown() 源码，dismiss() 源码分析，PopupWindow 和 Dialog 有什么区别？为何弹窗点击一下就 dismiss 呢？
*   [06.Snackbar 源码分析](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F06.Snackbar%25E6%25BA%2590%25E7%25A0%2581%25E5%2588%2586%25E6%259E%2590.md)
    *   最简单的创建，Snackbar 的 make 方法源码分析，Snackbar 的 show 显示与点击消失源码分析，显示和隐藏中动画源码分析，Snackbar 的设计思路，为什么 Snackbar 总是显示在最下面
*   [07. 弹窗常见问题](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F07.%25E5%25BC%25B9%25E7%25AA%2597%25E5%25B8%25B8%25E8%25A7%2581%25E9%2597%25AE%25E9%25A2%2598.md)
    *   DialogFragment 使用中 show() 方法遇到的 IllegalStateException, 什么常见产生的？Toast 偶尔报错 Unable to add window，Toast 运行在子线程导致崩溃如何解决？
*   [09.onAttachedToWindow 和 onDetachedFromWindow](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F09.onAttachedToWindow%25E5%2592%258ConDetachedFromWindow.md)
    *   onAttachedToWindow 的调用过程，onDetachedFromWindow 可以做什么？
*   [10.DecorView 介绍](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211%2FYCBlogs%2Fblob%2Fmaster%2Fandroid%2FWindow%2F10.DecorView%25E4%25BB%258B%25E7%25BB%258D.md)
    *   什么是 DecorView，DecorView 的创建，DecorView 的显示，深度解析

### 01.Window，View，子 Window

*   弹窗有哪些类型
    *   使用子窗口：在 Android 进程内，我们可以直接使用类型为子窗口类型的窗口。在 Android 代码中的直接应用是 PopupWindow 或者是 Dialog 。这当然可以，不过这种窗口依赖于它的宿主窗口，它可用的条件是你的宿主窗口可用
    *   采用 View 系统：使用 View 系统去模拟一个窗口行为，且能更加快速的实现动画效果，比如 SnackBar 就是采用这套方案
    *   使用系统窗口：比如吐司 Toast

### 02. 什么是 Activity

*   Activity 并不负责视图控制，它只是控制生命周期和处理事件。真正控制视图的是 Window。一个 Activity 包含了一个 Window，Window 才是真正代表一个窗口。
*   **Activity 就像一个控制器，统筹视图的添加与显示，以及通过其他回调方法，来与 Window、以及 View 进行交互。**

### 03. 什么是 Window

*   Window 是什么？
    *   表示一个窗口的概念，是所有 View 的直接管理者，任何视图都通过 Window 呈现 (点击事件由 Window->DecorView->View; Activity 的 setContentView 底层通过 Window 完成)
    *   Window 是一个抽象类，具体实现是 PhoneWindow。PhoneWindow 中有个内部类 DecorView，通过创建 DecorView 来加载 Activity 中设置的布局`R.layout.activity_main`。
    *   创建 Window 需要通过 WindowManager 创建，通过 WindowManager 将 DecorView 加载其中，并将 DecorView 交给 ViewRoot，进行视图绘制以及其他交互。
    *   WindowManager 是外界访问 Window 的入口
    *   Window 具体实现位于 WindowManagerService 中
    *   WindowManager 和 WindowManagerService 的交互是通过 IPC 完成
*   如何通过 WindowManager 添加 Window(代码实现)？
    *   如下所示
        
        ```
        //1. 控件 
        Button button = new Button(this); 
        button.setText("Window Button"); 
        //2. 布局参数 
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, 0, PixelFormat.TRANSPARENT); 
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED; 
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP; 
        layoutParams.x = 100; 
        layoutParams.y = 300; 
        // 必须要有type不然会异常: the specified window type 0 is not valid 
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR; 
        //3. 获取WindowManager并添加控件到Window中 
        WindowManager windowManager = getWindowManager(); 
        windowManager.addView(button, layoutParams);
        复制代码
        
        ```
        
*   WindowManager 的主要功能是什么？
    *   添加、更新、删除 View
        
        ```
        public interface ViewManager{ 
            public void addView(View view, ViewGroup.LayoutParams params); 
            //添加View 
            public void updateViewLayout(View view, ViewGroup.LayoutParams params); 
            //更新View 
            public void removeView(View view); 
            //删除View 
        }
        复制代码
        
        ```
        

### 04. 什么是 DecorView

*   DecorView 是 FrameLayout 的子类，它可以被认为是 Android 视图树的根节点视图。
    
    *   DecorView 作为顶级 View，一般情况下它内部包含一个竖直方向的 LinearLayout，**在这个 LinearLayout 里面有上下三个部分，上面是个 ViewStub，延迟加载的视图（应该是设置 ActionBar，根据 Theme 设置），中间的是标题栏 (根据 Theme 设置，有的布局没有)，下面的是内容栏。**
    *   具体情况和 Android 版本及主体有关，以其中一个布局为例，如下所示：
    
    ```
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:fitsSystemWindows="true"
        android:orientation="vertical">
        <!-- Popout bar for action modes -->
        <ViewStub
            android:id="@+id/action_mode_bar_stub"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inflatedId="@+id/action_mode_bar"
            android:layout="@layout/action_mode_bar"
            android:theme="?attr/actionBarTheme" />
    
        <FrameLayout
            style="?android:attr/windowTitleBackgroundStyle"
            android:layout_width="match_parent"
            android:layout_height="?android:attr/windowTitleSize">
    
            <TextView
                android:id="@android:id/title"
                style="?android:attr/windowTitleStyle"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@null"
                android:fadingEdge="horizontal"
                android:gravity="center_vertical" />
        </FrameLayout>
    
        <FrameLayout
            android:id="@android:id/content"
            android:layout_width="match_parent"
            android:layout_height="0dip"
            android:layout_weight="1"
            android:foreground="?android:attr/windowContentOverlay"
            android:foregroundGravity="fill_horizontal|top" />
    </LinearLayout>
    复制代码
    
    ```
    
*   在 Activity 中通过 setContentView 所设置的布局文件其实就是被加到内容栏之中的，成为其唯一子 View，就是上面的 id 为 content 的 FrameLayout 中，在代码中可以通过 content 来得到对应加载的布局。
    
    ```
    ViewGroup content = (ViewGroup)findViewById(android.R.id.content);
    ViewGroup rootView = (ViewGroup) content.getChildAt(0);
    复制代码
    
    ```
    

### 06. 关系结构图

*   Activity 与 PhoneWindow 与 DecorView 关系图
    *   ![](https://user-gold-cdn.xitu.io/2019/5/29/16b0201fe3dc60ec?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

### 07.Window 创建过程

*   App 点击桌面图片启动过程
    *   ![](https://user-gold-cdn.xitu.io/2019/5/29/16b0201fe3eabd7e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)
*   window 启动流程
    *   ![](https://user-gold-cdn.xitu.io/2019/5/29/16b0201fe3c92a7a?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)
*   Activity 与 PhoneWindow 与 DecorView 之间什么关系？
    *   一个 Activity 对应一个 Window 也就是 PhoneWindow，一个 PhoneWindow 持有一个 DecorView 的实例，DecorView 本身是一个 FrameLayout。

### 08. 创建机制分析

#### 8.1 Activity 实例的创建

*   ActivityThread 中执行 performLaunchActivity，从而生成了 Activity 的实例。源码如下所示，ActivityThread 类中源码
    
    ```
    private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
        ...
        Activity activity = null;
        try {
            java.lang.ClassLoader cl = r.packageInfo.getClassLoader();
            activity = mInstrumentation.newActivity(
                    cl, component.getClassName(), r.intent);
            ...
        } catch (Exception e) {
            ...
        }
    
        try {
            ...
            if (activity != null) {
                ...
                activity.attach(appContext, this, getInstrumentation(), r.token,
                        r.ident, app, r.intent, r.activityInfo, title, r.parent,
                        r.embeddedID, r.lastNonConfigurationInstances, config,
                        r.referrer, r.voiceInteractor);
                ...
            }
            ...
        } catch (SuperNotCalledException e) {
            throw e;
        } catch (Exception e) {
            ...
        }
    
        return activity;
    }
    复制代码
    
    ```
    

#### 8.2 Activity 中 Window 的创建

*   从上面的 performLaunchActivity 可以看出，在创建 Activity 实例的同时，会调用 Activity 的内部方法 attach
*   在 attach 该方法中完成 window 的初始化。源码如下所示，Activity 类中源码
    
    ```
    final void attach(Context context, ActivityThread aThread,
            Instrumentation instr, IBinder token, int ident,
            Application application, Intent intent, ActivityInfo info,
            CharSequence title, Activity parent, String id,
            NonConfigurationInstances lastNonConfigurationInstances,
            Configuration config, String referrer, IVoiceInteractor voiceInteractor,
            Window window, ActivityConfigCallback activityConfigCallback) {
    
        mWindow = new PhoneWindow(this, window, activityConfigCallback);
        mWindow.setWindowControllerCallback(this);
        mWindow.setCallback(this);
        mWindow.setOnWindowDismissedCallback(this);
        mWindow.getLayoutInflater().setPrivateFactory(this);
        if (info.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
            mWindow.setSoftInputMode(info.softInputMode);
        }
        if (info.uiOptions != 0) {
            mWindow.setUiOptions(info.uiOptions);
        }
    }
    复制代码
    
    ```
    

#### 8.3 DecorView 的创建

*   用户执行 Activity 的 setContentView 方法，内部是调用 PhoneWindow 的 setContentView 方法，在 PhoneWindow 中完成 DecorView 的创建。流程
    
    *   1.Activity 中的 setContentView
    *   2.PhoneWindow 中的 setContentView
    *   3.PhoneWindow 中的 installDecor
    
    ```
    public void setContentView(@LayoutRes int layoutResID) {
        getWindow().setContentView(layoutResID);
        initWindowDecorActionBar();
    }
    
    @Override
    public void setContentView(int layoutResID) {
        ...
        if (mContentParent == null) {
            installDecor();
        } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
            mContentParent.removeAllViews();
        }
        ...
    }
    
    private void installDecor() {
        if (mDecor == null) {
            mDecor = generateDecor();
            mDecor.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            mDecor.setIsRootNamespace(true);
            if (!mInvalidatePanelMenuPosted && mInvalidatePanelMenuFeatures != 0) {
                mDecor.postOnAnimation(mInvalidatePanelMenuRunnable);
            }
        }
        ...
    }
    复制代码
    
    ```
    

### 关于其他内容介绍

#### 01. 关于博客汇总链接

*   1. [技术博客汇总](https://link.juejin.im?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2F614cb839182c)
*   2. [开源项目汇总](https://link.juejin.im?target=https%3A%2F%2Fblog.csdn.net%2Fm0_37700275%2Farticle%2Fdetails%2F80863574)
*   3. [生活博客汇总](https://link.juejin.im?target=https%3A%2F%2Fblog.csdn.net%2Fm0_37700275%2Farticle%2Fdetails%2F79832978)
*   4. [喜马拉雅音频汇总](https://link.juejin.im?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2Ff665de16d1eb)
*   5. [其他汇总](https://link.juejin.im?target=https%3A%2F%2Fwww.jianshu.com%2Fp%2F53017c3fc75d)

#### 02. 关于我的博客

*   我的个人站点：www.yczbj.org， www.ycbjie.cn
*   github：[github.com/yangchong21…](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211)
*   知乎：[www.zhihu.com/people/yczb…](https://link.juejin.im?target=https%3A%2F%2Fwww.zhihu.com%2Fpeople%2Fyczbj%2Factivities)
*   简书：[www.jianshu.com/u/b7b2c6ed9…](https://link.juejin.im?target=http%3A%2F%2Fwww.jianshu.com%2Fu%2Fb7b2c6ed9284)
*   csdn：[my.csdn.net/m0_37700275](https://link.juejin.im?target=http%3A%2F%2Fmy.csdn.net%2Fm0_37700275)
*   喜马拉雅听书：[www.ximalaya.com/zhubo/71989…](https://link.juejin.im?target=http%3A%2F%2Fwww.ximalaya.com%2Fzhubo%2F71989305%2F)
*   开源中国：[my.oschina.net/zbj1618/blo…](https://link.juejin.im?target=https%3A%2F%2Fmy.oschina.net%2Fzbj1618%2Fblog)
*   泡在网上的日子：[www.jcodecraeer.com/member/cont…](https://link.juejin.im?target=http%3A%2F%2Fwww.jcodecraeer.com%2Fmember%2Fcontent_list.php%3Fchannelid%3D1)
*   邮箱：yangchong211@163.com
*   阿里云博客：[yq.aliyun.com/users/artic…](https://link.juejin.im?target=https%3A%2F%2Fyq.aliyun.com%2Fusers%2Farticle%3Fspm%3D5176.100-) 239.headeruserinfo.3.dT4bcV
*   segmentfault 头条：[segmentfault.com/u/xiangjian…](https://link.juejin.im?target=https%3A%2F%2Fsegmentfault.com%2Fu%2Fxiangjianyu%2Farticles)
*   掘金：[juejin.im/user/593943…](https://juejin.im/user/5939433efe88c2006afa0c6e)

### GitHub 链接：[github.com/yangchong21…](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fyangchong211)