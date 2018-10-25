>  原文地址 https://blog.csdn.net/qq_19431333/article/details/52862348 

Snackbar 是 Android 支持库中用于显示简单消息并且提供和用户的一个简单操作的一种弹出式提醒。当使用 Snackbar 时，提示会出现在消息最底部，通常含有一段信息和一个可点击的按钮。下图是 Gmail 中删除一封邮件时弹出的 Snackbar：


在上图中，最下方的黑色区域，包含左边文字和右边” 撤销” 字样的就是 Snackbar。Snackbar 在显示一段时间后就会自动消失。同样作为消息提示，Snackbar 相比于 Toast 而言，增加了一个用户操作，并且在同时弹出多个消息时，Snackbar 会停止前一个，直接显示后一个，也就是说同一时刻只会有一个 Snackbar 在显示；而 Toast 则不然，如果不做特殊处理，那么同时可以有多个 Toast 出现；Snackbar 相比于 Dialog，操作更少，因为只有一个用户操作的接口，而 Dialog 最多可以设置三个，另外 Snackbar 的出现并不影响用户的继续操作，而 Dialog 则必须需要用户做出响应，所以相比 Dialog，Snackbar 更轻量。
经过上面的比较，可以看出 Snackbar 可以用于显示用户信息并且该信息不需要用户立即做出反馈的时候。

# <a></a>一、如何使用 Snackbar？

Snackbar 没有公有的构造方法，但是提供了静态方法 make 方法：

```
static Snackbar make(View view, CharSequence text, int duration)

static Snackbar make(View view, int resId, int duration)
```

其中 view 参数是用于查找合适父布局的一个起点，下面分析源码的时候会解释到。如果父布局是一个 CoordinatorLayout，那么 Snackbar 还会有别的一些特性：可以滑动消除；并且如果有 FloatingActionButton 时，会将 FloatingActionButton 上移，而不会挡住 Snackbar 的显示。

## <a></a>1.1、父布局不是 CoordinatorLayout

在创建了一个 Snackbar 对象后，可以调用一些 set** 方法进行设置，其中 setAction() 方法用于设置右侧的文字显示以及点击事件，setCallback() 方法用于设置一个状态回调，在 Snackbar 显示和消失的时候会触发方法。下面是一段创建 Snackbar 的代码：

```
 Snackbar.make(view, "已删除一个会话", Snackbar.LENGTH_SHORT)
                .setAction("撤销", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(Main2Activity.this, "撤销了删除", Toast.LENGTH_SHORT).show();

                    }
                }).show();
```

以上代码在一个按钮的点击事件中创建一个 Snackbar 并显示，内容模仿上面的 Gmail 例子，并且给 “撤销” 一个点击事件，只是简单的显示一个 Toast。Activity 的根布局是一个 RelativeLayout，并且下部有一个 FloatingActionButton，在 Snackbar 出现后，可以看到 Snackbar 遮挡了 FlaotingActionButton 的一部分，具体效果如下：
![](https://img-blog.csdn.net/20161019191608111)

## <a></a>1.2、父布局是 CoordinatorLayout

在父布局不是 CoordinatorLayout 的情况下，如果有 FloaingActionButton，那么弹出的 Snackbar 会遮挡 FloatingActionButton，为了解决这个问题，可以将父布局改成 CoordinatorLayout，并且这会带来一个新特性，就是 Snackbar 可以通过右滑消失。代码一样，只是布局不同。直接看效果图：
![](https://img-blog.csdn.net/20161019191636971)
可以看到当 Snackbar 出现时，FloatingActionButton 会上移并且支持右滑消失。

## <a></a>1.3、Snackbar 消失的几种方式

Snackbar 显示只有一种方式，那就是调用 show() 方法，但是消失有几种方式：时间到了自动消失、点击了右侧按钮消失、新的 Snackbar 出现导致旧的 Snackbar 消失、滑动消失或者通过调用 dismiss() 消失。这些方式分别对应于 Snackbar.Callback 中的几个常量值。
- DISMISS_EVENT_ACTION：点击了右侧按钮导致消失
- DISMISS_EVENT_CONSECUTIVE：新的 Snackbar 出现导致旧的消失
- DISMISS_EVENT_MANUAL：调用了 dismiss 方法导致消失
- DISMISS_EVENT_SWIPE：滑动导致消失
- DISMISS_EVENT_TIMEOUT：设置的显示时间到了导致消失
Callback 有两个方法：

```
void    onDismissed(Snackbar snackbar, int event)

void    onShown(Snackbar snackbar)

```

其中 onShown 在 Snackbar 可见时调用，onDismissed 在 Snackbar 准备消失时调用。一般我们可以在 onDismissed 方法中正在处理我们所需要的操作，比如删除一封邮件，那么如果是点击了 “撤销” 按钮，那就应该不再删除邮件直接消失就可以了，但是对于其他的几种情况，就需要真正地删除邮件了（发送数据到后台等等…）。下面是模拟这样一段过程：

```
   Snackbar.make(view, "已删除一个会话", Snackbar.LENGTH_SHORT).setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {

                switch (event) {

                    case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                    case Snackbar.Callback.DISMISS_EVENT_MANUAL:
                    case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                        //TODO 网络操作
                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        Toast.makeText(MainActivity.this, "撤销了删除操作", Toast.LENGTH_SHORT).show();
                        break;

                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
                Log.i(TAG, "onShown");
            }
        }).show();
```

上述代码在 onDismissed 中根据消失类型进行不同的处理。效果如下：
![](https://img-blog.csdn.net/20161019191716850)

# <a></a>二、Snackbar 源码分析

## <a></a>2.1、Snackbar 的创建分析

从前面的段落知道，创建 Snackbar 需要使用静态的 make 方法，并且其中的 view 参数是一个查找父布局的起点。下面是 make 方法的实现：

```
public static Snackbar make(@NonNull View view, @NonNull CharSequence text,
            @Duration int duration) {
        Snackbar snackbar = new Snackbar(findSuitableParent(view));
        snackbar.setText(text);
        snackbar.setDuration(duration);
        return snackbar;
    }
```

其中 findSuitableParent() 方法为以 view 为起点寻找合适的父布局，下面是 findSuitableParent 方法的实现：

```
private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }
```

可以看到如果 view 是 CoordinatorLayout，那么就直接作为父布局了；如果是 FrameLayout，并且如果是 android.R.id.content，也就是查找到了 DecorView，即最顶部，那么就只用这个 view；如果不是的话，先保存下来；接下来就是获取 view 的父布局，然后循环再次判断。这样导致的结果最终会有两个选择，要么是 CoordinatorLayout，要么就是 FrameLayout，并且是最顶层的那个布局。具体情况是这样的：
- 如果从 View 往上搜寻，如果有 CoordinatorLayout，那么就使用该 CoordinatorLayout
- 如果从 View 往上搜寻，没有 CoordinatorLayout，那么就使用 android.R.id.content 的 FrameLayout
接下来再看 Snackbar 的构造方法：

```
 private Snackbar(ViewGroup parent) {
        mTargetParent = parent;
        mContext = parent.getContext();

        ThemeUtils.checkAppCompatTheme(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = (SnackbarLayout) inflater.inflate(
                R.layout.design_layout_snackbar, mTargetParent, false);

        mAccessibilityManager = (AccessibilityManager)
                mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }
```

其中 SnackbarLayout 就是 Snackbar 的样式，SnackbarLayout 继承自 LinearLayout 并且有一个 TextView 和一个 Button，其中 TextView 就是左边用于显示文字，Button 就是右边用于设置点击事件的。SnackbarLayout 的部分代码如下：

```
public static class SnackbarLayout extends LinearLayout {
        private TextView mMessageView;
        private Button mActionView;

       ...

        public SnackbarLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
            mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1);
            mMaxInlineActionWidth = a.getDimensionPixelSize(
                    R.styleable.SnackbarLayout_maxActionInlineWidth, -1);
            if (a.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, a.getDimensionPixelSize(
                        R.styleable.SnackbarLayout_elevation, 0));
            }
            a.recycle();

            setClickable(true);

            // Now inflate our content. We need to do this manually rather than using an <include>
            // in the layout since older versions of the Android do not inflate includes with
            // the correct Context.
            LayoutInflater.from(context).inflate(R.layout.design_layout_snackbar_include, this);

            ViewCompat.setAccessibilityLiveRegion(this,
                    ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
            ViewCompat.setImportantForAccessibility(this,
                    ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

            // Make sure that we fit system windows and have a listener to apply any insets
            ViewCompat.setFitsSystemWindows(this, true);
            ViewCompat.setOnApplyWindowInsetsListener(this,
                    new android.support.v4.view.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    // Copy over the bottom inset as padding so that we're displayed above the
                    // navigation bar
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(),
                            v.getPaddingRight(), insets.getSystemWindowInsetBottom());
                    return insets;
                }
            });
        }

        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
            mMessageView = (TextView) findViewById(R.id.snackbar_text);
            mActionView = (Button) findViewById(R.id.snackbar_action);
        }

        TextView getMessageView() {
            return mMessageView;
        }

        Button getActionView() {
            return mActionView;
        }

        ...
}
```

至此，Snackbar 被创建了。

## <a></a>2.2、对 Snackbar 进行设置

Snackbar 有一些 setXX 方法，比如 setAction、setActionTextColor 等方法，这里我们主要介绍 setAction 和 setActionTextColor 方法的实现，其余的类似。从 2.1 的分析我们知道，Snackbar 其实就是一个包含了 TextView 和 Button 的 LinearLayout。明白了这一点之后，就好理解 setXX 方法了，首先看 setAction() 方法的实现：

```
 /**
     * Set the action to be displayed in this {@link Snackbar}.
     *
     * @param text     Text to display
     * @param listener callback to be invoked when the action is clicked
     */
    @NonNull
    public Snackbar setAction(CharSequence text, final View.OnClickListener listener) {
        final TextView tv = mView.getActionView();

        if (TextUtils.isEmpty(text) || listener == null) {
            tv.setVisibility(View.GONE);
            tv.setOnClickListener(null);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view);
                    // Now dismiss the Snackbar
                    dispatchDismiss(Callback.DISMISS_EVENT_ACTION);
                }
            });
        }
        return this;
    }
```

首先调用 mView.getActionView() 方法，返回的 tv 其实就是右边的 Button，然后判断文本和监听器，设置可见性、文本、监听器。在前面的例子中，我们知道一旦点击了按钮，Snackbar 就会消失，处理消失的逻辑在 dispatchDismiss() 方法中，下面是 dispatchDismiss() 方法的实现：

```
void dispatchDismiss(@Callback.DismissEvent int event) {
        SnackbarManager.getInstance().dismiss(mManagerCallback, event);
    }
```

可以看到，会获取一个 SnackbarManager 对象的实例，然后调用 dismiss 方法，具体的消失稍后再讲。
下面看 setActionTextColor 方法，该方法用于设置按钮文本颜色，方法如下：

```
/**
     * Sets the text color of the action specified in
     * {@link #setAction(CharSequence, View.OnClickListener)}.
     */
    @NonNull
    public Snackbar setActionTextColor(@ColorInt int color) {
        final TextView tv = mView.getActionView();
        tv.setTextColor(color);
        return this;
    }
```

首先是获取到 Button 实例，然后调用 setTextColor 方法，其余 setXX 之类的设置样式方法类似

## <a></a>2.3、Snackbar 的显示与消失

如果需要让 Snackbar 显示，那么需要调用 show 方法，下面是 show 方法的实现：

```
/**
     * Show the {@link Snackbar}.
     */
    public void show() {
        SnackbarManager.getInstance().show(mDuration, mManagerCallback);
    }
```

首先获取一个 SnackbarManager 对象，然后调用它的 show 方法，show 方法如下：

```
 public void show(int duration, Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                // Means that the callback is already in the queue. We'll just update the duration
                mCurrentSnackbar.duration = duration;

                // If this is the Snackbar currently being shown, call re-schedule it's
                // timeout
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
                scheduleTimeoutLocked(mCurrentSnackbar);
                return;
            } else if (isNextSnackbarLocked(callback)) {
                // We'll just update the duration
                mNextSnackbar.duration = duration;
            } else {
                // Else, we need to create a new record and queue it
                mNextSnackbar = new SnackbarRecord(duration, callback);
            }

            if (mCurrentSnackbar != null && cancelSnackbarLocked(mCurrentSnackbar,
                    Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE)) {
                // If we currently have a Snackbar, try and cancel it and wait in line
                return;
            } else {
                // Clear out the current snackbar
                mCurrentSnackbar = null;
                // Otherwise, just show it now
                showNextSnackbarLocked();
            }
        }
    }
```

上面的代码比较复杂，下面根据具体情况来分析，首先看其中的参数 Callback。
其中 mManagerCallback 是 SnackbarManager 的 Callback，每一个 Snackbar 都会有一个这样的对象，定义如下：

```
final SnackbarManager.Callback mManagerCallback = new SnackbarManager.Callback() {
        @Override
        public void show() {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, Snackbar.this));
        }

        @Override
        public void dismiss(int event) {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0, Snackbar.this));
        }
    };
```

在 show 和 dismiss 方法中就是通过 Handler 发送了一个消息，sHandler 的定义如下：

```
static final Handler sHandler;
    static final int MSG_SHOW = 0;
    static final int MSG_DISMISS = 1;

    static {
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_SHOW:
                        ((Snackbar) message.obj).showView();
                        return true;
                    case MSG_DISMISS:
                        ((Snackbar) message.obj).hideView(message.arg1);
                        return true;
                }
                return false;
            }
        });
    }
```

可以看到 sHandler 是一个静态的并且在 Snackbar 被加载进类加载器的时候就会创建，handlerMessage 方法就是调用 Snackbar 的 showView() 显示和 hideView() 消失。showView 和 hideView 方法后面再看。
下面针对 show 方法进行分析：
1\. 如果当前没有 Snackbar 显示，这时显示一个 Snackbar 并调用了 show 方法，那么最终会进入到 SnackbarManager 的 show 方法中，由于是第一个 Snackbar，那么 mCurrentSnackbar、mNextSnackbar 均为 null，则首先执行这一行代码，

```
mNextSnackbar = new SnackbarRecord(duration, callback);
```

接下来，由于 mCurrentShackbar 为 null，则会执行 else 的代码：

```
 // Clear out the current snackbar
                mCurrentSnackbar = null;
                // Otherwise, just show it now
                showNextSnackbarLocked();
```

由于执行 mNextSnackbar，自然要将 mCurrentSnackbar 置为 null，然后调用 showNextSnackbarLocked() 方法，下面是该方法的实现：

```
private void showNextSnackbarLocked() {
        if (mNextSnackbar != null) {
            mCurrentSnackbar = mNextSnackbar;
            mNextSnackbar = null;

            final Callback callback = mCurrentSnackbar.callback.get();
            if (callback != null) {
                callback.show();
            } else {
                // The callback doesn't exist any more, clear out the Snackbar
                mCurrentSnackbar = null;
            }
        }
    }
```

首先将 mCurrntSnackbar 设为 mNextSnackbar，然后获取 Callback，调用 Callback 的 show 方法，从前面的分析知道 show 方法中向 Snackbar 的 Handler 发送一个消息，最后调用 Snackbar 的 showView() 方法显示 Snackbar。
2\. 如果当前已经有一个 Snackbar 显示了，又再调用了该对象的 show 方法，但是只是设置了不同时间，那么就会执行下段代码：

```
if (isCurrentSnackbarLocked(callback)) {
                // Means that the callback is already in the queue. We'll just update the duration
                mCurrentSnackbar.duration = duration;

                // If this is the Snackbar currently being shown, call re-schedule it's
                // timeout
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
                scheduleTimeoutLocked(mCurrentSnackbar);
                return;
            }
```

重置 mCurrentSnackbar 的时间，然后移除 mCureentSnackbar 发出的消息和回调，mCurrentSnackbar 会发出什么消息呢？mCurrentSnackbar 会在 Snackbar 的时间到了后发送一个超时的消息给 Handler，下面是 handler 的实现：

```
mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_TIMEOUT:
                        handleTimeout((SnackbarRecord) message.obj);
                        return true;
                }
                return false;
            }
        });
```

Handler 的处理又是调用 handleTimeout 方法，handleTimeout 方法的实现如下：

```
 void handleTimeout(SnackbarRecord record) {
        synchronized (mLock) {
            if (mCurrentSnackbar == record || mNextSnackbar == record) {
                cancelSnackbarLocked(record, Snackbar.Callback.DISMISS_EVENT_TIMEOUT);
            }
        }
    }
```

从上面可以知道会调用 cancelSnackbarLocked 方法，实现如下：

```
private boolean cancelSnackbarLocked(SnackbarRecord record, int event) {
        final Callback callback = record.callback.get();
        if (callback != null) {
            // Make sure we remove any timeouts for the SnackbarRecord
            mHandler.removeCallbacksAndMessages(record);
            callback.dismiss(event);
            return true;
        }
        return false;
    }
```

从上面可以看出，首先移除 SnackbarRecord 发出的所有消息，然后调用 Callback 的 dismiss 方法，从上面我们知道最终是向 Snackbar 的 sHandler 发送了一条消息，最终是调用 Snackbar 的 hideView 消失。
show 方法中重置了时间以及删除了 Handler 中的消息后就是调用了 scheduleTimeoutLocked 方法

```
 private void scheduleTimeoutLocked(SnackbarRecord r) {
        if (r.duration == Snackbar.LENGTH_INDEFINITE) {
            // If we're set to indefinite, we don't want to set a timeout
            return;
        }

        int durationMs = LONG_DURATION_MS;
        if (r.duration > 0) {
            durationMs = r.duration;
        } else if (r.duration == Snackbar.LENGTH_SHORT) {
            durationMs = SHORT_DURATION_MS;
        }
        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r), durationMs);
    }
```

从上面可以看出，如果显示的时间是一个不定的，那么就不管；然后设置时间，最后调用 sendMessageDelayed，由于该 Snackbar 目前正在显示，所以就会在 durationMs 后发送 MSG_TIMEOUT 的消息，从上面的分析知道，SnackbarManager 的 Handler 在收到 MSG_TIMEOUT 后最终会将消息发送给 Snackbar 的 sHandler，最后调用 hideView 方法。
3\. 如果当前已有一个 Snackbar 正在显示，又创建了一个新的 Snackbar 并调用 show 方法，那么 SnackbarManager 的 show 方法会执行

```
else if (isNextSnackbarLocked(callback)) {
                // We'll just update the duration
                mNextSnackbar.duration = duration;
            } else {
                // Else, we need to create a new record and queue it
                mNextSnackbar = new SnackbarRecord(duration, callback);
            }
```

首先进入 isNextSnackbarLocked 方法，就是判断该 callback 是否是 mNextSnackbar 的，按照我们这个情况不是的，那么就会 else 语句创建 mNextSnackbar。接下来执行下段代码：

```
 if (mCurrentSnackbar != null && cancelSnackbarLocked(mCurrentSnackbar,
                    Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE)) {
                // If we currently have a Snackbar, try and cancel it and wait in line
                return;
            } else {
                // Clear out the current snackbar
                mCurrentSnackbar = null;
                // Otherwise, just show it now
                showNextSnackbarLocked();
            }
```

这时，mCurrentSnackbar 不为 null，然后调用 cancelSnackbarLocked 方法，cancelSnackbarLocked 方法在前面已经提到就是在 Handler 中移除 mCurrentSnackbar 发出的消息，然后调用 Callback 的 dismiss 方法，最终是调用 Snackbar 的 hideView 方法，并且注意到传入的参数为 DISMISS_EVENT_CONSECUTIVE，该参数代表新的 Snackbar 出现导致旧的消失。在这里我们只看到了旧的消失，而没有看到新的显示，答案在 Snackbar 的 hideView 中，下面是 hideView 的实现：

```
final void hideView(@Callback.DismissEvent final int event) {
        if (shouldAnimate() && mView.getVisibility() == View.VISIBLE) {
            animateViewOut(event);
        } else {
            // If anims are disabled or the view isn't visible, just call back now
            onViewHidden(event);
        }
    }
```

首先判断是调用 animateViewOut 还是 onViewHidden 方法，下面是 animateViewOut 方法的实现：

```
private void animateViewOut(final int event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(mView)
                    .translationY(mView.getHeight())
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(View view) {
                            mView.animateChildrenOut(0, ANIMATION_FADE_DURATION);
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            onViewHidden(event);
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(mView.getContext(),
                    R.anim.design_snackbar_out);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(ANIMATION_DURATION);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    onViewHidden(event);
                }

                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mView.startAnimation(anim);
        }
    }
```

可以看到在动画结束的最后都调用了 onViewHidden 方法，所以最终都是要调用 onViewHidden 方法的。animateViewOut 提供动画效果，onViewHidden 提供具体的业务处理，下面是 onViewHidden 方法

```
void onViewHidden(int event) {
        // First tell the SnackbarManager that it has been dismissed
        SnackbarManager.getInstance().onDismissed(mManagerCallback);
        // Now call the dismiss listener (if available)
        if (mCallback != null) {
            mCallback.onDismissed(this, event);
        }
        if (Build.VERSION.SDK_INT < 11) {
            // We need to hide the Snackbar on pre-v11 since it uses an old style Animation.
            // ViewGroup has special handling in removeView() when getAnimation() != null in
            // that it waits. This then means that the calculated insets are wrong and the
            // any dodging views do not return. We workaround it by setting the view to gone while
            // ViewGroup actually gets around to removing it.
            mView.setVisibility(View.GONE);
        }
        // Lastly, hide and remove the view from the parent (if attached)
        final ViewParent parent = mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mView);
        }
    }
```

从代码中可以看出，首先调用 SnackbarManager 的 onDismissed 方法，然后判断 Snackbar.Callback 是不是 null，调用 Snackbar.Callback 的 onDismissed 方法，就是我们上面介绍的处理 Snackbar 消失的方法。最后就是将 Snackbar 的 mView 移除。下面看 SnackbarManager 的 onDismissed 方法：

```
/**
     * Should be called when a Snackbar is no longer displayed. This is after any exit
     * animation has finished.
     */
    public void onDismissed(Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                // If the callback is from a Snackbar currently show, remove it and show a new one
                mCurrentSnackbar = null;
                if (mNextSnackbar != null) {
                    showNextSnackbarLocked();
                }
            }
        }
    }
```

从上面的方法可以看到，将 mCurrentSnackbar 置为 null，然后因为 mNextSnackbar 不为 null，所以调用 showNextSnackbarLocked 方法，从上面的介绍知道 showNextSnackbarLocked 就是将其置为 mCurrentSnackbar 然后最后调用了 Snackbar 的 showView 方法显示。
下面我们看一下 Snackbar 的 showView 方法是如何实现的:

```
 final void showView() {
        if (mView.getParent() == null) {
            final ViewGroup.LayoutParams lp = mView.getLayoutParams();

            if (lp instanceof CoordinatorLayout.LayoutParams) {
                // If our LayoutParams are from a CoordinatorLayout, we'll setup our Behavior
                final CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) lp;

                final Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
                behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
                    @Override
                    public void onDismiss(View view) {
                        view.setVisibility(View.GONE);
                        dispatchDismiss(Callback.DISMISS_EVENT_SWIPE);
                    }

                    @Override
                    public void onDragStateChanged(int state) {
                        switch (state) {
                            case SwipeDismissBehavior.STATE_DRAGGING:
                            case SwipeDismissBehavior.STATE_SETTLING:
                                // If the view is being dragged or settling, cancel the timeout
                                SnackbarManager.getInstance().cancelTimeout(mManagerCallback);
                                break;
                            case SwipeDismissBehavior.STATE_IDLE:
                                // If the view has been released and is idle, restore the timeout
                                SnackbarManager.getInstance().restoreTimeout(mManagerCallback);
                                break;
                        }
                    }
                });
                clp.setBehavior(behavior);
                // Also set the inset edge so that views can dodge the snackbar correctly
                clp.insetEdge = Gravity.BOTTOM;
            }

            mTargetParent.addView(mView);
        }

        mView.setOnAttachStateChangeListener(new SnackbarLayout.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {}

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (isShownOrQueued()) {
                    // If we haven't already been dismissed then this event is coming from a
                    // non-user initiated action. Hence we need to make sure that we callback
                    // and keep our state up to date. We need to post the call since removeView()
                    // will call through to onDetachedFromWindow and thus overflow.
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onViewHidden(Callback.DISMISS_EVENT_MANUAL);
                        }
                    });
                }
            }
        });

        if (ViewCompat.isLaidOut(mView)) {
            if (shouldAnimate()) {
                // If animations are enabled, animate it in
                animateViewIn();
            } else {
                // Else if anims are disabled just call back now
                onViewShown();
            }
        } else {
            // Otherwise, add one of our layout change listeners and show it in when laid out
            mView.setOnLayoutChangeListener(new SnackbarLayout.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom) {
                    mView.setOnLayoutChangeListener(null);

                    if (shouldAnimate()) {
                        // If animations are enabled, animate it in
                        animateViewIn();
                    } else {
                        // Else if anims are disabled just call back now
                        onViewShown();
                    }
                }
            });
        }
    }
```

前面的先不看，后面的和 hideView 类似，animateViewIn 负责动画，但是最终会调用 onViewShown，所以直接看 onViewShown 方法，

```
void onViewShown() {
        SnackbarManager.getInstance().onShown(mManagerCallback);
        if (mCallback != null) {
            mCallback.onShown(this);
        }
    }
```

可以看到会调用 SnackbarManager 的 onShown 方法，然后如果 Snackbar.Callback 不为 null，就调用其 onShown 回调。下面是 SnackbarManager 的 onShown 方法：

```
/**
     * Should be called when a Snackbar is being shown. This is after any entrance animation has
     * finished.
     */
    public void onShown(Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                scheduleTimeoutLocked(mCurrentSnackbar);
            }
        }
    }
```

可以看到最终调用了 scheduleTimeoutLocked 方法，从上面的分析知道 scheduleTimeoutLocked 方法就是在设定的时间到达后发送一条 MSG_TIMEOUT 消息给 SnackbarManager 的 Handler，最后又是回到了 Snackbar 的 hideView 方法。
4\. 显式调用 dismiss 方法，Snackbar 的 dismiss 方法如下：

```
/**
     * Dismiss the {@link Snackbar}.
     */
    public void dismiss() {
        dispatchDismiss(Callback.DISMISS_EVENT_MANUAL);
    }
```

前面介绍过 dispatchDismiss 方法，最终是调用 SnackbarManager 的 dismiss 方法，如下：

```
public void dismiss(Callback callback, int event) {
        synchronized (mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                cancelSnackbarLocked(mCurrentSnackbar, event);
            } else if (isNextSnackbarLocked(callback)) {
                cancelSnackbarLocked(mNextSnackbar, event);
            }
        }
    }
```

从上面的代码可以看出，就是调用 cancelSnackbarLocked 方法，而 cancelSnackbarLocked 方法如下：

```
private boolean cancelSnackbarLocked(SnackbarRecord record, int event) {
        final Callback callback = record.callback.get();
        if (callback != null) {
            // Make sure we remove any timeouts for the SnackbarRecord
            mHandler.removeCallbacksAndMessages(record);
            callback.dismiss(event);
            return true;
        }
        return false;
    }
```

可以看到该方法首先移除 Handler 中的消息，然后调用 dismiss 方法，最终还是回到 Snackbar 的 hideView 方法。

## 2.4、总结

上面设计到两个类，Snackbar 和 SnackbarManager，SnackbarManager 内部有两个 SnackbarRecord，一个 mCurrentSnackbar，一个 mNextSnackbar，SnackbarManager 通过这两个对象实现 Snackbar 的顺序显示，如果在一个 Snackbar 显示之前有 Snackbar 正在显示，那么使用 mNextSnackbar 保存第二个 Snackbar，然后让第一个 Snackbar 消失，然后消失之后再调用 SnackbarManager 显示下一个 Snackbar，如此循环，实现了 Snackbar 的顺序显示。
Snackbar 负责显示和消失，具体来说其实就是添加和移除 View 的过程。
Snackbar 和 SnackbarManager 的设计很巧妙，利用一个 SnackbarRecord 对象保存 Snackbar 的显示时间以及 SnackbarManager.Callback 对象，前面说到每一个 Snackbar 都有一个叫做 mManagerCallback 的 SnackbarManager.Callback 对象，下面看一下 SnackRecord 类的定义：

```
private static class SnackbarRecord {
        final WeakReference<Callback> callback;
        int duration;

        SnackbarRecord(int duration, Callback callback) {
            this.callback = new WeakReference<>(callback);
            this.duration = duration;
        }

        boolean isSnackbar(Callback callback) {
            return callback != null && this.callback.get() == callback;
        }
    }
```

Snackbar 向 SnackbarManager 发送消息主要是调用 SnackbarManager.getInstace() 返回一个单例对象；而 SnackManager 向 Snackbar 发送消息就是通过 show 方法传入的 Callback 对象。
SnackbarManager 中的 Handler 只处理一个 MSG_TIMEOUT 事件，最后是调用 Snackbar 的 hideView 消失的；Snackbar 的 sHandler 处理两个消息，showView 和 hideView，而消息的发送者是 mManagerCallback，控制者是 SnackbarManager。
