# xUtils3使用

[TOC]

> 简单记录下,一些较常用的Android开源框架

# 1. xUtils3

 > xUtils是基于Afinal开发的目前功能比较完善的一个Android开源框架，最近又发布了xUtil3.0，在增加新功能的同时又提高了框架的性能，下面来看看官方（https://github.com/wyouflf/xUtils3）对xUtils3的介绍：

 - xUtils包含了很多实用的android工具； 
 - xUtils支持超大文件(超过2G)上传，更全面的http请求协议支持(11种谓词)，拥有更加灵活的ORM，更多的事件注解支持且不受混淆影响； 
 - xUtils 最低兼容Android 4.0 (api level 14)； 
 - xUtils3变化较多所以建立了新的项目不在旧版(github.com/wyouflf/xUtils)上继续维护, 相对于旧版本：
 - HTTP实现替换HttpClient为UrlConnection, 自动解析回调泛型, 更安全的断点续传策略； 
 - 支持标准的Cookie策略, 区分domain, path； 
 - 事件注解去除不常用的功能, 提高性能； 
 - 数据库api简化提高性能, 达到和greenDao一致的性能； 
 - 图片绑定支持gif(受系统兼容性影响, 部分gif文件只能静态显示), webp; 支持圆角, 圆形, 方形等裁剪, 支持自动旋转。

 xUtils3使用方法:
#### 使用前配置
##### 需要的权限
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
##### 初始化
		// 在application的onCreate中初始化
		@Override
		public void onCreate() {
		    super.onCreate();
		    x.Ext.init(this);
		    x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
		    ...
		}

### 下载文件

			RequestParams params = new RequestParams(mDownloadUrl);
            LogUtil.i(tag,mDownloadUrl);
            //3. 设置文件保存路径
            params.setSaveFilePath(Environment.getExternalStorageDirectory().
                    getAbsolutePath() + File.separator + "update.apk");
            //4. 自动为文件命名
            params.setAutoRename(true);
            x.http().get(params,new Callback.ProgressCallback<File>(){
                @Override
                public void onWaiting() {
                }

                @Override
                public void onStarted() {
                    //下载刚刚开始
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    //下载中.......
                }

                @Override
                public void onSuccess(File result) {
                    //下载成功
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    //下载出错
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                    //下载完成
                }
            });