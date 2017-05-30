# Android 启动模式

总结:

![](http://olg7c0d2n.bkt.clouddn.com/17-5-23/24020345-file_1495533485379_82de.png)

# 1. 定义启动模式

## 1.1 使用清单文件

在清单文件中声明 Activity 时，您可以使用 <activity> 元素的 launchMode 属性指定 Activity 应该如何与任务关联。

launchMode 属性指定有关应如何将 Activity 启动到任务中的指令。您可以分配给 launchMode 属性的启动模式共有四种：

**"standard"（默认模式）**

默认。系统在启动 Activity 的任务中创建 Activity 的新实例并向其传送 Intent。Activity 可以多次实例化，而每个实例均可属于不同的任务，并且一个任务可以拥有多个实例。

**"singleTop"**

如果当前任务的顶部已存在 Activity 的一个实例，则系统会通过调用该实例的 onNewIntent() 方法向其传送 Intent，而不是创建 Activity 的新实例。Activity 可以多次实例化，而每个实例均可属于不同的任务，并且一个任务可以拥有多个实例（但前提是位于返回栈顶部的 Activity 并不是 Activity 的现有实例）。
例如，假设任务的返回栈包含根 Activity A 以及 Activity B、C 和位于顶部的 D（堆栈是 A-B-C-D；D 位于顶部）。收到针对 D 类 Activity 的 Intent。如果 D 具有默认的 "standard" 启动模式，则会启动该类的新实例，且堆栈会变成 A-B-C-D-D。但是，如果 D 的启动模式是 "singleTop"，则 D 的现有实例会通过 onNewIntent() 接收 Intent，因为它位于堆栈的顶部；而堆栈仍为 A-B-C-D。但是，如果收到针对 B 类 Activity 的 Intent，则会向堆栈添加 B 的新实例，即便其启动模式为 "singleTop" 也是如此。

注：为某个 Activity 创建新实例时，用户可以按“返回”按钮返回到前一个 Activity。 但是，当 Activity 的现有实例处理新 Intent 时，则在新 Intent 到达 onNewIntent() 之前，用户无法按“返回”按钮返回到 Activity 的状态。

**"singleTask"**

系统创建新任务并实例化位于新任务底部的 Activity。但是，如果该 Activity 的一个实例已存在于一个单独的任务中，则系统会通过调用现有实例的 onNewIntent() 方法向其传送 Intent，而不是创建新实例。一次只能存在 Activity 的一个实例。
注：尽管 Activity 在新任务中启动，但是用户按“返回”按钮仍会返回到前一个 Activity。

这个启动模式是专门针对于启动其他应用的activity，只有启动其他activity的时候才会新建一个新的任务栈。系统会创建一个新的任务，并将启动的Activity放入这个新任务的栈底位置。但是，如果现有任务当中已经存在一个该Activity的实例了，那么系统就不会再创建一次它的实例，而是会直接调用它的onNewIntent()方法

**"singleInstance"**

与 "singleTask" 相同，只是系统不会将任何其他 Activity 启动到包含实例的任务中。该 Activity 始终是其任务唯一仅有的成员；由此 Activity 启动的任何 Activity 均在单独的任务中打开。
我们再来看另一示例，Android 浏览器应用声明网络浏览器 Activity 应始终在其自己的任务中打开（通过在 <activity> 元素中指定 singleTask 启动模式）。这意味着，如果您的应用发出打开 Android 浏览器的 Intent，则其 Activity 与您的应用位于不同的任务中。相反，系统会为浏览器启动新任务，或者如果浏览器已有任务正在后台运行，则会将该任务上移一层以处理新 Intent。

## 1.2 

启动 Activity 时，您可以通过在传递给 startActivity() 的 Intent 中加入相应的标志，修改 Activity 与其任务的默认关联方式。可用于修改默认行为的标志包括：

FLAG_ACTIVITY_NEW_TASK
在新任务中启动 Activity。如果已为正在启动的 Activity 运行任务，则该任务会转到前台并恢复其最后状态，同时 Activity 会在 onNewIntent() 中收到新 Intent。
正如前文所述，这会产生与 "singleTask"launchMode 值相同的行为。

FLAG_ACTIVITY_SINGLE_TOP
如果正在启动的 Activity 是当前 Activity（位于返回栈的顶部），则 现有实例会接收对 onNewIntent() 的调用，而不是创建 Activity 的新实例。
正如前文所述，这会产生与 "singleTop"launchMode 值相同的行为。

FLAG_ACTIVITY_CLEAR_TOP
如果正在启动的 Activity 已在当前任务中运行，则会销毁当前任务顶部的所有 Activity，并通过 onNewIntent() 将此 Intent 传递给 Activity 已恢复的实例（现在位于顶部），而不是启动该 Activity 的新实例。
产生这种行为的 launchMode 属性没有值。

FLAG_ACTIVITY_CLEAR_TOP 通常与 FLAG_ACTIVITY_NEW_TASK 结合使用。一起使用时，通过这些标志，可以找到其他任务中的现有 Activity，并将其放入可从中响应 Intent 的位置。

注：如果指定 Activity 的启动模式为 "standard"，则该 Activity 也会从堆栈中移除，并在其位置启动一个新实例，以便处理传入的 Intent。 这是因为当启动模式为 "standard" 时，将始终为新 Intent 创建新实例。
