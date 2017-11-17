# ViewPager中的Fragment生命周期

## 今天踩了个巨坑....

当我在使用ViewPager+fragment时,我在一个ViewPager中放置了4个fragment,这时fragment的生命周期是很无语的....

比如我从第一个fragment切换到第二个fragment(就是ViewPager滑动了一下),然后第一个fragment的生命周期居然不回调onPause(),也不回调onStop();

这是我最后才发现的一个坑,最开始我一直以为是我代码的问题....

放在这里,引以为戒,希望以后不要再碰这个坑....

## 离开fragment需要做一下操作

当我需要离开第一个fragment时,我需要停止轮播图的切换,这时,我需要判断当前这个fragment是否对用户可见.
显然这时不能用onPause()去实现.

## 解决方案

- 方案1：设置Viewpager的缓存机制，不缓存除当前页以外的页面数据，所见即所得，离开即销毁；
此方案对需求改动较大，且较影响用户体验;

- 方案2：重载Fragment.onHiddenChanged(boolean hidden)方法，其参数hidden代表当前fragment显隐状态改变时，是否为隐藏状态，可通过check此参数作处理；
此方案局限在于本方法的系统调用时间发生在显隐状态改变时，但第一次显示时此方法并不调用；

- 方案3：重载Fragment.setUserVisibleHint(boolean isVisibleToUser)方法，其参数isVisibleToUser顾名思义最接近我们的需求，代表页面是否“真正”对使用者显示；
此方案局限在于此方法的第一次系统调用甚至早于Fragment的onCreate方法，故其第一次调用时isVisibleToUser值总为false，影响我们对生命周期顺序的判定；
```java
Fragment1 - isVisibleToUser - false (多余)
Fragment1 - isVisibleToUser - true
Fragment1 - isVisibleToUser - false
Fragment2 - isVisibleToUser - false (多余)
Fragment2 - isVisibleToUser - true
Fragment2 - isVisibleToUser - false
```

## 实际采用的解决方案

根据对产品需求的理解和用户体验的统一，选择在方案3基础上加以改进；
setUserVisibleHint()方法本身很接近我们的需求，它的局限点我采取了一个侵入式的解决方式：
```java
protected boolean isCreated = false;  
  
@Override  
public void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  
  
    // ...  
    isCreated = true;  
}  
  
/** 
 * 此方法目前仅适用于标示ViewPager中的Fragment是否真实可见 
 * For 友盟统计的页面线性不交叉统计需求 
 */  
@Override  
public void setUserVisibleHint(boolean isVisibleToUser) {  
    super.setUserVisibleHint(isVisibleToUser);  
  
    if (!isCreated) {  
        return;  
    }  
  
    if (isVisibleToUser) {  
        umengPageStart();  
    }else {  
        umengPageEnd();  
    }  
  
}  
```
对onCreate方法结束的一个标记即可解决问题；
切记：此标记的改变请勿放在Fragment的onActivtyCreate方法中，此方法调用滞后于setUserVisibleHint的判断