# fragment保存临时数据与恢复

# 单个Fragment遭遇突发情况

- 点击back键
- 点击锁屏键
- 点击home键
- 其他APP进入前台
- 启动了另一个Activity
- 屏幕方向旋转
- APP被Kill

# 结论

1.无论任务栈中fragment数量为多少，onSaveInstanceState方法都没有调用
2.当fragment任务栈中有多个fragment时，进入下一个fragment时，并不会销毁
fragment实例，而是仅仅销毁视图，最终调用的方法为onDestoryView。
所以此时我们要去保存临时数据，并不能仅保存在onSaveInstanceState中（因为它
可能不会调用），还应该在onDestoryView方法中进行保存临时数据的操作，源码如下：

# 代码

## 在fragment中

	@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mPresenter.restoreStateFromArguments(getArguments())) {
            //第一次进入做一些初始化操作
            //mPresenter.requestItNewestFromNet();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //可能在此保存临时数据
        mPresenter.onSaveInstanceState(getArguments());
        LogUtil.e("保存临时数据" + getArguments());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //可能在此保存临时数据
        mPresenter.onDestroyView(getArguments());
    }

## 在presenter中

	@Override
    public void onDestroyView(Bundle arguments) {
        saveStateToArguments(arguments);
    }

    @Override
    public void onSaveInstanceState(Bundle arguments) {
        LogUtil.e("保存数据");
        saveStateToArguments(arguments);
    }

    @Override
    public void saveStateToArguments(Bundle arguments) {
        savedState = saveState();
        if (savedState != null) {
            arguments.putBundle(SAVED_VIEW_STATE, savedState);
        }
    }

    @Override
    public boolean restoreStateFromArguments(Bundle bundle) {
        if (bundle == null) { //第一次进来,这个肯定为空
            return false;
        }

        savedState = bundle.getBundle(SAVED_VIEW_STATE);
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    @Override
    public void restoreState() {
        if (savedState != null) {
            mScienceNewsList = (List<ScienceNews>) savedState.get(SAVED_VIEW_STATE);
            mView.onSuccess(mScienceNewsList);
        }
    }

    @Override
    public Bundle saveState() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(SAVED_VIEW_STATE, (Serializable) mScienceNewsList);
        return bundle;
    }


因为没有了系统提供的bundle参数,我们选择把数据保存在Arguments中，代码就不带
着大家一步一步的看了，因为逻辑并不复杂，挺好理解的。通过这种方式，我们就挺容
易的将临时数据和fragment的一些状态保存进bundle中并在需要时恢复了。

# 总结

Fragment对临时数据的保存，仅仅依靠onSaveInstanceState方法是不行的，还需要在
onDestoryView中进行相应操作，具体参考上面的代码。
Fragment中对于一些持久性的数据，仍应在onPause中保存。但是要注意，onPause方法中
不能进行大量操作.