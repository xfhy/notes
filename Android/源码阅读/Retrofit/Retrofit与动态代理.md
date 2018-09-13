
        
# 细细品读Retrofit的设计之美一




> [1.细细品读Retrofit的设计之美一](https://www.jianshu.com/p/2ff63eb587cf)<br>
[2. 细细品读Retrofit的设计之美二](https://www.jianshu.com/p/dab7f5720aa5)


### 引言

Retrofit是这两年比较流行的网络通讯库，之所以流行自然有它的优点，首先是大厂Square公司开源之作，俗话说大厂出品，必须精品。<br>
作为网络通讯框架，基本的组成部分就是三大块：1. 请求体部分。2. 响应体部分。3. 与UI层回调部分。

Retrofit的使用这里就不细说了，我们从构建Retrofit对象谈起。

```
private Retrofit buildRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(AppConst.BASE_URL)      // 设置基础请求地址
                .client(buildHttpClient())       // 构建自定义的httpClient 对象
                .addConverterFactory(FastJsonConverterFactory.create())  // 添加数据解析工厂
                .build();  // 构建
    }

```

### 首先：构建者Builder模式

整体看很明显有个构建者Builder模式，Builder模式在Android中是很常见的一种设计模式，它的优点很明显一般情况一个框架都是需要灵活自由的配置参数属性的，如果不用Builder模式，都改成setter、getter，那初始化一个Retrofit对象就显得复杂和臃肿了。而这里Builder模式加上链式调用方式，为Retrofit框架的参数配置增添了不少灵活和自由，而且代码可读性也增强了。<br>
其实Builder模式的套路很简单，下面来个简单的伪代码Builder模式：

```
// Builder模式的套路模板
public class Retrofit{
  final HttpUrl baseUrl;
  final List&lt;Converter.Factory&gt; converterFactories;
  ....//  省略一大坨代码
  
  Retrofit(HttpUrl baseUrl, List&lt;Converter.Factory&gt; converterFactories) {
      this.baseUrl = baseUrl;
      this.converterFactories = unmodifiableList(converterFactories); 
  }
  ....//  省略一大坨代码, 其实就是上面参数属性的一些获取方法
  public HttpUrl baseUrl() {
    return baseUrl;
  }

  public static final class Builder{
      private HttpUrl baseUrl;
      private final List&lt;Converter.Factory&gt; converterFactories = new ArrayList&lt;&gt;();

      public Builder baseUrl(HttpUrl baseUrl) {
        // ... 省去部分代码
        this.baseUrl = baseUrl;
        return this;
      }

      public Builder addConverterFactory(Converter.Factory factory) {
        converterFactories.add(checkNotNull(factory, "factory == null"));
        return this;
      }

      public Retrofit build() {
        // Make a defensive copy of the converters.
        List&lt;Converter.Factory&gt; converterFactories = new ArrayList&lt;&gt;(this.converterFactories);

        return new Retrofit(baseUrl, converterFactories);
      }
  }
}

```

以上就是Builder模式的套路模板，外部Retrofit的对象的构建最终是在build()方法new出来返回。<br>
Retrofit框架内部有好多地方都用到了Builder模式，也是为了方便自由配置参数的。<br>
Builder模式在Android开发中最常见的就是AlertDialog.Builder，可以自由的配置对话框的标题、内容、内容设置来源、确认取消等按钮事件等等。有兴趣的可以去了解下AlertDialog的源码，基本也是上面模板的套路。

### 代理模式

构建好Retrofit对象后，大家都知道这个框架网络请求的通讯接口api都是Interface接口中声明的，框架本身为了与网络请求业务做解耦用了动态代理的方式，为业务通讯接口生成代理对象，当代理对象调用业务接口方法api的时候，动态代理类就能监测到并回调，这时候就可以做网络框架该有的功能：解析通讯业务接口，生成网络请求体便于供应给底层OkHttp做具体的网络请求工作。其实就是框架本身没有办法直接使用业务接口，所以请了一个代理中介对象去间接的访问业务接口，来完成相关的业务功能。

```
public ApiWrapper() {
        // 构建生成retrofit对象
        Retrofit retrofit = buildRetrofit();
        // ApiService是网络通过create方法创建出的代理对象mApiService
        mApiService = retrofit.create(ApiService.class);
}

```

来看看create方法是如何创建出ApiService接口的对象的。

```
public &lt;T&gt; T create(final Class&lt;T&gt; service) {
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class&lt;?&gt;[] { service },
        new InvocationHandler() {

          @Override public Object invoke(Object proxy, Method method, Object[] args)
              throws Throwable {
           }
    }
}

```

通过Proxy.newProxyInstance方法动态创建了代理对象，也就是上文create方法返回的mApiService对象。是不是很懵逼，为什么Proxy.newProxyInstance方法就能创建出代理对象，而且又正好是ApiService.class这个接口对象呢，是怎么创建出来的？带着这些问题，我们来聊聊“代理模式”。

要搞清楚上面的这些问题，就得明白代理模式的套路，最重要就是区分清楚角色：<br>
**角色一：目标接口**<br>
**角色二：目标对象**<br>
**角色三：代理对象**

这里先举个简单的例子，有个这样的场景：

> 
Jerry是个程序员年纪大了眼看就要30岁了还是单身，家里人非常的着急，于是找到了隔壁老王（老王认识的妹纸比较多，为人热情）叫着帮忙介绍妹子给Jerry认识。


上面这个场景来区分下角色：<br>
角色一：目标接口，大龄单身汪找妹子（要干的事情）<br>
角色二：目标对象，单身程序员Jerry（需要找妹子的目标人物）<br>
角色三：代理对象，皮条客隔壁老王（代表Jerry去找妹子）

这里创建三个文件：目标接口 IFindGirl、目标类Jerry、代理类ProxyLaoWan

```
// IService.java
/**
 * 目标接口
 * Created by Administrator on 2017/9/17 0017.
 */
public interface IService {
    /**
     * 找妹子
     * @param name  名字
     * @param age   年龄
     */
    void findGirl(String name, int age);
}

// Jerry.java
/**
 * 目标对象：单身汪Jerry
 * Created by Administrator on 2017/9/17 0017.
 */
public class Jerry implements IService {
    private static final String TAG = "Jerry";

    @Override
    public void findGirl(String name, int age) {
        Log.e(TAG, name + " 说愿意做Jerry的女朋友");
    }
}

// ProxyLaoWan.java
/**
 * 代理对象：找对象的代理人老王
 * Created by Administrator on 2017/9/17 0017.
 */
public class ProxyLaoWan implements IService {

    private IService service;

    public ProxyLaoWan(IService service) {
        this.service = service;
    }

    @Override
    public void findGirl(String name, int age) {
        // 老王找到妹子后，再这告诉Jerry
        service.findGirl(name, age);
    }
}

```

使用的时候很简单：

```
// jerry
IService service = new Jerry();
// 创建代理人, 然后把Jerry委托给老王
ProxyLaoWan laoWan = new ProxyLaoWan(service);
// 老王帮Jerry去找妹子
laoWan.findGirl("Tom", 22);

```

这个例子中Jerry没有直接去找妹子“Tom”，而是通过了老王，这是一个典型的静态代理模式，Jerry把找妹子的事情委托代理给了老王，同样Jerry如果还有其它的事情，比如买最新的肾phone手机，可是国行的很贵，刚好老王要去香港，又委托老王买港版的iPhone X，于是就要IService目标接口中加入新的要干的事情buyPhone()，同样老王的类、Jerry类都需要实现相应的方法。如果Jerry不断的有新的事情要做，新的功能要扩展那需要修改的地方就比较多了不利于项目的扩展和团队开发。为此这样的需求就产生了动态代理模式。<br>
先来看看套路模板：

```
// 动态代理
    public void testDynamicProxy(){
        // 目标对象jerry
        final IService jerryService = new Jerry();
        // 代理对象老王
        IService proxyLaoWan = (IService) Proxy.newProxyInstance(
                jerryService.getClass().getClassLoader(),
                jerryService.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 调用目标接口方法的时候，就调用invoke方法
                        long currentTime = System.currentTimeMillis();
                        Object returnValue = method.invoke(jerryService, args);
                        long calledMethodTime = System.currentTimeMillis();
                        long invokeMethodTime = calledMethodTime - currentTime;
                        // 接口方法的执行时间，便于检测性能
                        Log.e("InvocationHandler", "方法执行性能时间：" + invokeMethodTime);
                        return returnValue;
                    }
                });

        // 老王帮忙找妹子，妹子叫Tom  22岁
        proxyLaoWan.findGirl("Tom", 22);

        // 老王帮Jerry买了价值8288元的iPhone X手机
        proxyLaoWan.buyPhone("iPhone X", 8288);
    }

```

Jerry如果还要委托老王给买手机，只要给目标接口加入buyPhone方法，然后Jerry实现这个方法，而代理者老王，不需要管都有什么具体的目标接口，通过Proxy.newProxyInstance创建的代理对象，就可以调用目标接口的方法。<br>
介绍下Proxy.newProxyInstance方法：

```
// Proxy.java
public static Object newProxyInstance(ClassLoader loader,
                                          Class&lt;?&gt;[] interfaces,
                                          InvocationHandler h);

```

第一个参数：目标对象的类加载器。因为这个代理对象是运行时才创建的，没有编译时候预先准备的字节码文件提供，所以需要一个类加载器来加载产生Proxy代理里的类类型，便于创建代理对象。<br>
第二个参数：interfaces是目标接口数组<br>
第三个参数：是代理对象当调用目标接口方法的时候，会先回调InvocationHandler接口的实现方法invoke。

到目前为止还是看不出来Proxy.newProxyInstance是怎么给我们创建代理对象的，下面分析下它的源码实现：

#### 动态代理的源码实现

```
class Proxy{
  private final static Class[] constructorParams = { InvocationHandler.class };

  public static Object newProxyInstance(ClassLoader loader,
                                          Class&lt;?&gt;[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        if (h == null) {  throw new NullPointerException(); }
        // 获取到Proxy类的 类类型Class
        Class&lt;?&gt; cl = getProxyClass0(loader, interfaces);
        // 通过Proxy类的类类型对象获取InvocationHandler作为参数的构造方法
        try {
            final Constructor&lt;?&gt; cons = cl.getConstructor(constructorParams);
            //  通过构造方法对象创建一个代理对象
            return newInstance(cons, h);
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString());
        }
    }

    private static Object newInstance(Constructor&lt;?&gt; cons, InvocationHandler h) {
        return cons.newInstance(new Object[] {h} );
    }
}

// Constructor.java
class Constructor{
  public T newInstance(Object... args) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (serializationClass == null) {
            // 看最终的实现是Native方法，使用底层NDK来实现创建的代理实例对象
            return newInstance0(args);
        } else {
            return (T) newInstanceFromSerialization(serializationCtor, serializationClass);
        }
    }
    
    // 底层NDK实现创建代理对象
    private static native Object newInstanceFromSerialization(Class&lt;?&gt; ctorClass, Class&lt;?&gt; allocClass)
        throws InstantiationException, IllegalArgumentException, InvocationTargetException;

    // 底层NDK实现创建代理对象
    private native T newInstance0(Object... args) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}

```

从上面代码中我们可以看出，最终的代理对象的创建是底层NDK来创建返回的，具体就不去看底层的实现了，大体了解到动态代理对象是通过这个构造方法来创建的。

```
protected Proxy(InvocationHandler h) {
  this.h = h;
}

```

经过上门对动态代理模式的一番学习和解释，现在回过头来看

```
mApiService = retrofit.create(ApiService.class);

public &lt;T&gt; T create(final Class&lt;T&gt; service) {
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class&lt;?&gt;[] { service },
        new InvocationHandler() {

          @Override public Object invoke(Object proxy, Method method, Object[] args)
              throws Throwable {
           }
    }
}

```

create方法创建返回的正是ApiService接口的代理对象，每当代理对象调用目标接口里的方法时，动态代理对象就会回调InvocationHandler接口的invoke实现方法。<br>
在Retrofit中，动态代理模式的角色划分：<br>
**角色一：**目标接口（委托方法），ApiService接口方法<br>
**角色二：**目标对象（委托方），ApiService.class<br>
**角色三：**代理对象，create创建的mApiService对象。

至此就把Retrofit中，动态代理业务的网络通讯接口讲清楚了，好处就是非入侵的方式，把网络通讯api调用代理出来，然后在调用回调的invoke方法里统一处理和准备网络框架需要构建的请求体，作为后续加入到请求队列任务池中进行具体的网络请求。动态代理模式也是AOP的一种实现方式，切片思想的一种。做过Java EE服务端开发的对于Spring的AOP应该深有体会，动态代理与Annotation的结合真是完美，这一点在Retrofit的各种请求方式、参数、url路径等等的注解就体现了。

文章有些长，这是第一篇，后面还会持续更新关于Retrofit的解读，不单单是让你懂的Retrofit的原理，还让你学会感受设计模式的美妙。

