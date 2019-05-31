> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/27181e2e32d2

## 背景

如果系统由于系统约束（而不是正常的应用程序行为）而破坏了 Activity，那么尽管实际 Activity 实例已经消失，但是系统还是会记住它已经存在，这样如果用户导航回到它，系统会创建一个新的实例的 Activity 使用一组保存的数据来描述 Activity 在被销毁时的状态。系统用于恢复以前状态的已保存数据称为 “实例状态”，是存储在 Bundle 对象中的键值对的集合。

## 解决

onSaveInstanceState() 和 onRestoreInstanceState() 就是这样的背景下大展身手了。

> **注意**
> 1、如果是用户自动按下返回键，或程序调用 finish() 退出程序，是不会触发 onSaveInstanceState() 和 onRestoreInstanceState() 的。
> 2、每次用户旋转屏幕时，您的 Activity 将被破坏并重新创建。当屏幕改变方向时，系统会破坏并重新创建前台 Activity，因为屏幕配置已更改，您的 Activity 可能需要加载替代资源（例如布局）。即会执行 onSaveInstanceState() 和 onRestoreInstanceState() 的。

## 介绍

默认情况下，系统使用 Bundle 实例状态来保存有关 View 中 Activity 布局每个对象的信息（例如输入到 EditText 对象中的文本值）。因此，如果您的 Activity 实例被销毁并重新创建，则布局状态会自动恢复到之前的状态。但是，您的 Activity 可能包含更多要恢复的状态信息，例如跟踪 Activity 中用户进度的成员变量。

为了让您为 Activity 添加额外的数据到已保存的实例状态，Activity 生命周期中还有一个额外的回调方法，这些回调方法在前面的课程中没有显示。该方法是 onSaveInstanceState()，系统在用户离开 Activity 时调用它。当系统调用此方法时，它将传递 Bundle 将在您的 Activity 意外销毁的事件中保存的对象，以便您可以向其中添加其他信息。然后，如果系统在被销毁之后必须重新创建 Activity 实例，它会将相同的 Bundle 对象传递给您的 Activity 的 onRestoreInstanceState() 方法以及您的 onCreate() 方法。

![](http://upload-images.jianshu.io/upload_images/2066935-5c781cf339feaf89.png) 这是一个简介图

> 如上图所示：
> 当系统开始停止您的 Activity 时，它会调用 onSaveInstanceState()（1），以便您可以指定要保存的其他状态数据，以防 Activity 必须重新创建实例。如果 Activity 被破坏并且必须重新创建相同的实例，则系统将（1）中定义的状态数据传递给 onCreate() 方法（2）和 onRestoreInstanceState() 方法（3）。

## 保存你的 Activity 状态

当您的 Activity 开始停止时，系统会调用，onSaveInstanceState() 以便您的 Activity 可以使用一组键值对来保存状态信息。此方法的默认实现保存有关 Activity 视图层次结构状态的信息，例如 EditText 小部件中的文本或 ListView 的滚动位置。

为了保存 Activity 的附加状态信息，您必须实现 onSaveInstanceState() 并向对象添加键值对 Bundle。例如：

<pre>static final String STATE_SCORE = "playerScore";
static final String STATE_LEVEL = "playerLevel";
...

@Override
public void onSaveInstanceState(Bundle savedInstanceState) {
    // 保存用户自定义的状态
    savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
    savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);

    // 调用父类交给系统处理，这样系统能保存视图层次结构状态
    super.onSaveInstanceState(savedInstanceState);
}

</pre>

## 恢复您的 Activity 状态

当您的 Activity 在之前被破坏后重新创建时，您可以从 Bundle 系统通过您的 Activity 中恢复您的保存状态。这两个方法 onCreate() 和 onRestoreInstanceState() 回调方法都会收到 Bundle 包含实例状态信息的相同方法。

因为 onCreate() 调用该方法是否系统正在创建一个新的 Activity 实例或重新创建一个以前的实例，所以您必须 Bundle 在尝试读取之前检查该状态是否为空。如果它为空，那么系统正在创建一个 Activity 的新实例，而不是恢复之前被销毁的实例。

例如，下面是如何恢复一些状态数据 onCreate()：

<pre>@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); // 记得总是调用父类

    // 检查是否正在重新创建一个以前销毁的实例
    if (savedInstanceState != null) {
        // 从已保存状态恢复成员的值
        mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
        mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
    } else {
        // 可能初始化一个新实例的默认值的成员
    }
    ...
}

</pre>

onCreate() 您可以选择执行 onRestoreInstanceState()，而不是在系统调用 onStart() 方法之后恢复状态。系统 onRestoreInstanceState() 只有在存在保存状态的情况下才会恢复，因此您不需要检查是否 Bundle 为空：

<pre>public void onRestoreInstanceState(Bundle savedInstanceState) {
    // 总是调用超类，以便它可以恢复视图层次超级
    super.onRestoreInstanceState(savedInstanceState);

    // 从已保存的实例中恢复状态成员
    mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
    mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
}

</pre>

毕。