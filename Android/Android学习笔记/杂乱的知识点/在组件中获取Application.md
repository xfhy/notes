首先项目是组件化了的,其次有一个library组件,用做基础组件.
然后在基础组件里面新建一个BaseApplication作为Application基类.然后在主工程里面新建一个XXApplication继承BaseApplication(记得在清单文件中注册),就可以啦.

我们在BaseApplication中的onCreate()中获取到了实例,那么即可在该library中获取Application了.当然,也可以在其他引入了基础组件的组件里面获取Application

```java
public class BaseApplication extends Application{
    
    private static BaseApplication mApplication;
    
    public static BaseApplication getInstance() {
        return mApplication;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}
```