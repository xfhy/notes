
## 1.首先需要一个登陆APP需要封装的数据model
> 假设model名称是AccountInfo
在AccountInfo里面封装用户名,密码,登陆凭证类型,登陆ip等一些用户相关的信息

## 2.用户输入完账号密码
这时需要将上面的model全部填写完毕,注意:这里的password是需要加密的,比如我们可以使用MD5进行加密,然后加密之后的密码再赋值到accountInfo中.服务器那边存放的账户信息中密码是直接存储的MD5加密之后的,下次进行密码比较时直接用MD5加密之后的进行比较.

ND5加密需要写一个工具类,这个不难.之前我写了一个MD5加密,[地址](https://github.com/xfhy/notes/blob/1d033be78f1594aa1d7bba75fd1d0696d4f4961a/Android/Android%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/%E6%9D%82%E4%B9%B1%E7%9A%84%E7%9F%A5%E8%AF%86%E7%82%B9/%E5%8A%A0%E5%AF%86.md)

## 3.使用post请求将账号密码发往服务器进行验证
其实下面2种方式是差不多的,只不过是请求和返回的bean对象不同而已(一个是包含了密码,一个是包含了验证码等其他信息).验证成功则登录成功,验证失败则登录失败.

### 3.1 普通登录
### 3.2 验证码登录

## 4.登录成功
4.1将账户信息model(假设为AccountInfo,我觉得该model应该定义在基础library中.将登录返回参数数据model中的sessionID也一并存于账户信息model中,并且将sessionID同时也保存到SharedPreferences中)存于Application中,AccountInfo是Application的成员变量.同时序列化该对象到本地文件中.

登录成功后保存服务器返回的sessionId到SharedPreferences中,并新建一个AccountInfo对象,将用户信息全部放进去.

```java
/**
* 将账户信息放入本地文件，做缓存来用
*/
public synchronized AccountInfo getAccountInfoFromFile() {
    String path = this.getFilesDir().getPath() + "/";
    AccountInfo accountInfo = (AccountInfo) FileUtil.getFile(path + LOCAL_ACCOUNTINFO_NAME);
         /**
        * 如果文件中不存在序列化的AccountInfo，重新实例化一个。 但一般登录成功后，文件将保存AccountInfo
        */
    return accountInfo != null ? accountInfo : new AccountInfo();
}

//这里的方法写的不怎么好,IO流关闭操作应该放入finally中
public static Object getFile(String fileName) {
    try {
        File file = new File(fileName);
        if (file.exists() && !file.isDirectory()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fileInputStream);
            Object obj = ois.readObject();
            ois.close();
            fileInputStream.close();
            return obj;
        } else {
            return null;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    } catch (OutOfMemoryError e) {
        e.printStackTrace();
        return null;
    }
}
```

4.2并同时将账户信息保存到本地文件中,用ObjectOutputStream写到文件中.

4.3同时将账户信息转换成json然后保存到SP文件中.

## 5.在其他组件中使用账户信息
5.1可以通过前面已经保存到SP文件中的账户信息来获取当前登录的账户信息.在AccountInfoTemp中声明一个静态方法,就可以在组件中任何地方访问账户信息了.

5.2或者可以在组件中自定义一个与该组件业务相关的model(假设叫AccountInfoTemp),然后在进入组件时,需要传入AccountInfoTemp对象,该对象中已经赋值(比如账户名称,账号那些已经是赋值好了的).在AccountInfoTemp中声明一个静态方法,就可以在组件中任何地方访问账户信息了.

## 6.下一次进入时实现自动登录
首先判断本地的账户信息(序列化到文件中了的)中是否sessionID和账户都非空,只有在非空的时候才证明之前是登录成功了的.

如果非空,那么去请求网络判断是否能登录成功,登录成功则跳转到主界面.

如果没有网络,显示一个没有网络的对话框,然后进入主页呗(因为上一次是登录成功了的).

## 7.实现"被挤下线"功能

所谓的被挤下线功能，即一个账号在A客户端保持登陆状态，然后又在B客户端进行了登陆操作，那么A客户端就会被挤下线。

> 服务端需要返回Token,每次在app登录时为app分配一个新的token,如果在某次请求中app传递token不是最新的,则视为需要重新登录，在token失效的情况下,返回约定好的code 

App如何知道该账户已经在其他设备上登陆了呢？有三种实现方式 
1. api请求中后台返回特定的code。缺点是需要下次请求才知道被踢下线(我们目前就是使用这种方式)
2. 使用推送。后台可以推送给APP，从而使APP得知已在其他地方登陆，可以及时响应。
3. 使用第三方的监听器。比如集成了环信，环信自身有提供连接状态的接听,通过监听环信的用户状态,从而达到监听app自身用户系统的效果

目前APP是将sessionId封装到了网络请求model中的mToken中,传给服务端,服务器端发现app传递token不是最新的,则视为需要重新登录，在token失效的情况下,返回约定好的code.