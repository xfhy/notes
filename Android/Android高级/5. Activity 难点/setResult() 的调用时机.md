> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.cnblogs.com/shaweng/p/3875825.html

　　今天遇到这样一个问题，我在 Activity-**A** 中用 startActivityForResult() 方法启动了 Activity**-B**，并且在 **B** 中通过 setResult**()** 方法给 **A** 返回值，由于某些原因不能在 setResult() 之后立刻调用 finish() 函数，只能通过用户按 Back 键自己退出到 **A**。按理说从 **B** 退出回到 Aactivity-**A** 过程中，**A** 中的 onActivityResult**() **应该被调用， 可是通过 log 发现，整个操作过程中 onActivityResult() 始终没有被调用。 前后研究了半天才发现 是 setResult() 的调用时机不对造成的，因为在我是在 **B** 的 onStop() 函数中调用 setResult() 函数的，这个时候的 seResult 是没有任何意义的，因为已经错过了 **A** onActivityResult() 的调用时机。

　　因为在 **B **退回** A** 过程中，执行过程是

　　B---onPause
　　A---onActivityResult
　　A---onRestart
　　A---onStart
　　A---onResume
　　B---onStop
　　B---onDestroy

　　从上面过程可以看出，首先是 **B** 处于 Pause 状态，然后等待 **A** 执行onRestart——> onStart ——〉onResume，然后才是 B 的 onSstop——>onSdestroy，而 **A** 的 onActivityResult() 需要在 **B** 的 onPause 之后，**A** 的 onRestart 之前这中间调用，所以 **B** 中的 setResult() 函数应该放在 **B** 的 onPause 之前调用。

另外我试验了一下，如果把 setResult() 放在 **B** 的 onPause() 里面调用，结果仍然是无效的。

那么 setResult() 应该在什么时候调用呢？从源码可以看出，Activity 返回 result 是在被 finish 的时候，也就是说调用 setResult() 方法必须在 finish() 之前。所以在 onPause、onStop、onDestroy 方法中调用 setResult() 也有可能不会返回成功，因为这些方法调用不一定是在 finish 之前的，当然在 onCreate() 就调用 setResult 肯定是在 finish 之前的，但是又不满足业务需要。

实际使用场景有两个：

（1）按 BACK 键从一个 Activity 退出来的，一按 BACK，android 就会自动调用 Activity 的 finish() 方法，

       方法：重写 onBackPressed() 方法，捕获 BACK 事件，捕获到之后先 setResult。代码：

[?](#)

| 1234567 | `@Override``public` `void` `onBackPressed()``{``Log.i(TAG,` `"onBackPressed"``);``setResult(Const.LIVE_OK);``super``.onBackPressed();``}` |

（2）按**点击事件**中显式的调用 finish()

[?](#)

| 12 | `setResult(RESULT_OK);``finish();` |

执行过程为：

　　B---onBackPressed
　　B---finish
　　B---onPause
　　A---onActivityResult
　　A---onRestart
　　A---onStart
　　A---onResume
　　B---onStop
　　B---onDestroy