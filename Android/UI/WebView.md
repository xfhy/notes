# WebView

> 原文:http://blog.csdn.net/carson_ho/article/details/52693322     感谢作者分享

# 1. 简介

WebView是一个基于webkit引擎、展现web页面的控件。

	android的Webview在低版本和高版本采用了不同的webkit版本内核，4.4后直接使用了Chrome。

# 2. 作用

- 显示和渲染Web页面
- 直接使用html文件（网络上或本地assets中）作布局
- 可和JavaScript交互调用
	
	WebView控件功能强大，除了具有一般View的属性和设置外，还可以对url请求、页面加载、渲染、页面交互进行强大的处理。

# 3. 使用介绍

一般来说Webview可单独使用，可联合其子类一起使用，所以接下来，我会介绍：

- Webview自身的常见方法；
- Webview的最常用的子类 
（WebSettings类、WebViewClient类、WebChromeClient类)
- Android和Js的交互

## 3.1 Webview常用方法

### 3.1.1 WebView的状态

	//激活WebView为活跃状态，能正常执行网页的响应
	webView.onResume() ;
	
	//当页面被失去焦点被切换到后台不可见状态，需要执行onPause
	//通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。
	webView.onPause();
	
	//当应用程序(存在webview)被切换到后台时，这个方法不仅仅针对当前的webview而是全局的全应用程序的webview
	//它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
	webView.pauseTimers()
	//恢复pauseTimers状态
	webView.resumeTimers();
	
	//销毁Webview
	//在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview
	//但是注意：webview调用destory时,webview仍绑定在Activity上
	//这是由于自定义webview构建时传入了该Activity的context对象
	//因此需要先从父容器中移除webview,然后再销毁webview:
	rootLayout.removeView(webView); 
	webView.destroy();

### 3.1.2 关于前进 / 后退网页

	//是否可以后退
	Webview.canGoBack() 
	//后退网页
	Webview.goBack()
	
	//是否可以前进                     
	Webview.canGoForward()
	//前进网页
	Webview.goForward()
	
	//以当前的index为起始点前进或者后退到历史记录中指定的steps
	//如果steps为负数则为后退，正数则为前进
	Webview.goBackOrForward(intsteps) 

**常见用法：Back键控制网页后退**

- 问题：在不做任何处理前提下 ，浏览网页时点击系统的“Back”键,整个 Browser 会调用 finish()而结束自身
- 目标：点击返回后，是网页回退而不是推出浏览器
- 解决方案：在当前Activity中处理并消费掉该 Back 事件

		public boolean onKeyDown(int keyCode, KeyEvent event) {
		    if ((keyCode == KEYCODE_BACK) && mWebView.canGoBack()) { 
		        mWebView.goBack();
		        return true;
		    }
		    return super.onKeyDown(keyCode, event);
		}

### 3.1.3 清除缓存数据

	//清除网页访问留下的缓存
	//由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
	Webview.clearCache(true);
	
	//清除当前webview访问的历史记录
	//只会webview访问历史记录里的所有记录除了当前访问记录
	Webview.clearHistory();
	
	//这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
	Webview.clearFormData();

## 3.2 常用类

### 3.2.1 WebSettings类

- 作用：对WebView进行配置和管理
- 配置步骤 & 常见方法：

配置步骤1：添加访问网络权限（AndroidManifest.xml）

**这是前提！这是前提！这是前提！**

	<uses-permission android:name="android.permission.INTERNET"/>

配置步骤2：生成一个WebView组件（有两种方式）

	//方式1：直接在在Activity中生成
	WebView webView = new WebView(this)
	
	//方法2：在Activity的layout文件里添加webview控件：
	WebView webview = (WebView) findViewById(R.id.webView1);

配置步骤3：进行配置-利用WebSettings子类（常见方法）

	//声明WebSettings子类
	WebSettings webSettings = webView.getSettings();
	
	//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
	webSettings.setJavaScriptEnabled(true);  
	
	//支持插件
	webSettings.setPluginsEnabled(true); 
	
	//设置自适应屏幕，两者合用
	webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小 
	webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
	
	//缩放操作
	webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
	webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
	webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
	
	//其他细节操作
	webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存 
	webSettings.setAllowFileAccess(true); //设置可以访问文件 
	webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口 
	webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
	webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

**常见用法：设置WebView缓存**

- 当加载 html 页面时，WebView会在/data/data/包名目录下生成 database 与 cache 两个文件夹
- 请求的 URL记录保存在 WebViewCache.db，而 URL的内容是保存在 WebViewCache 文件夹下
- 是否启用缓存：

		 //优先使用缓存: 
	    WebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 
	        //缓存模式如下：
	        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
	        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
	        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
	        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
	
	    //不使用缓存: 
	    WebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

- 结合使用（离线加载）

		if (NetStatusUtil.isConnected(getApplicationContext())) {
		    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
		} else {
		    webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
		}
		
		webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
		webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
		webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能
		
		String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
		webSettings.setAppCachePath(cacheDirPath); //设置  Application Caches 缓存目录

注意： 每个 Application 只调用一次 WebSettings.setAppCachePath()，WebSettings.setAppCacheMaxSize()

### 3.2.2 WebViewClient类
- 作用：处理各种通知 & 请求事件
- 常见方法：

**常见方法1：shouldOverrideUrlLoading()**

- 作用：打开网页时不调用系统浏览器， 而是在本WebView中显示；在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。

		//步骤1. 定义Webview组件
		Webview webview = (WebView) findViewById(R.id.webView1);
		
		//步骤2. 选择加载方式
		  //方式1. 加载一个网页：
		  webView.loadUrl("http://www.google.com/");
		
		  //方式2：加载apk包中的html页面
		  webView.loadUrl("file:///android_asset/test.html");
		
		  //方式3：加载手机本地的html页面
		   webView.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");
		
		//步骤3. 复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
			
		    webView.setWebViewClient(new WebViewClient(){
					//This method was deprecated in API level 24.
					//Use shouldOverrideUrlLoading(WebView, WebResourceRequest) instead.
		      @Override
		      public boolean shouldOverrideUrlLoading(WebView view, String url) {
		          view.loadUrl(url);
		      return true;
		      }
		  });
**常见方法2：onPageStarted()**

- 作用：开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。

		webView.setWebViewClient(new WebViewClient(){
		      @Override
		      public void  onPageStarted(WebView view, String url, Bitmap favicon) {
		         //设定加载开始的操作
		      }
		  });

**常见方法3：onPageFinished()**

- 作用：在页面加载结束时调用。我们可以关闭loading 条，切换程序动作。

		 webView.setWebViewClient(new WebViewClient(){
		      @Override
		      public void onPageFinished(WebView view, String url) {
		         //设定加载结束的操作
		      }
		  });

**常见方法4：onLoadResource()**

- 作用：在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。

		 webView.setWebViewClient(new WebViewClient(){
		      @Override
		      public boolean onLoadResource(WebView view, String url) {
		         //设定加载资源的操作
		      }
		  });

常见方法5：onReceivedError（）

- 作用：加载页面的服务器出现错误时（如404）调用。 

> App里面使用webview控件的时候遇到了诸如404这类的错误的时候，若也显示浏览器里面的那种错误提示页面就显得很丑陋了，那么这个时候我们的app就需要加载一个本地的错误提示页面，即webview如何加载一个本地的页面

	//步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
	//步骤2：将该html文件放置到代码根目录的assets文件夹下
	
	//步骤3：复写WebViewClient的onRecievedError方法
	//该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
	    webView.setWebViewClient(new WebViewClient(){
	      @Override
	      public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
	switch(errorCode)
	                {
	                case HttpStatus.SC_NOT_FOUND:
	                    view.loadUrl("file:///android_assets/error_handle.html");
	                    break;
	                }
	            }
	        });

**常见方法6：onReceivedSslError()**

- 作用：处理https请求 

webView默认是不处理https请求的，页面显示空白，需要进行如下设置：

	webView.setWebViewClient(new WebViewClient() {    
        @Override    
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {    
            handler.proceed();    //表示等待证书响应
        // handler.cancel();      //表示挂起连接，为默认方式
        // handler.handleMessage(null);    //可做其他处理
        }    
    });    

### 3.2.3 WebChromeClient类

- 作用：辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。
- 常见使用：

**常见方法1： onProgressChanged（）**

- 作用：获得网页的加载进度并显示

		webview.setWebChromeClient(new WebChromeClient(){
	
	      @Override
	      public void onProgressChanged(WebView view, int newProgress) {
	          if (newProgress < 100) {
	              String progress = newProgress + "%";
	              progress.setText(progress);
	            } else {
	        }
	    });
**常见方法2： onReceivedTitle（）**

- 作用：获取Web页中的标题 

每个网页的页面都有一个标题，比如www.baidu.com这个页面的标题即“百度一下，你就知道”，那么如何知道当前webview正在加载的页面的title并进行设置呢？

	webview.setWebChromeClient(new WebChromeClient(){
	
	    @Override
	    public void onReceivedTitle(WebView view, String title) {
	       titleview.setText(title);
	    }

## 3.3 WebView与JS的交互

具体请看我写的文章 [Android WebView与JS的交互方式 最全面汇总](http://blog.csdn.net/carson_ho/article/details/64904691)

## 3.4 注意事项：如何避免WebView内存泄露？	

### 3.4.1 不在xml中定义 Webview ，而是在需要的时候在Activity中创建，并且Context使用 getApplicationgContext()

	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);

### 3.4.2 在 Activity 销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空。

	@Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

## 4.通过WebView获取网页标题

	//可以查看加载进度,以及js调用监控的客户端
    mWebView.setWebChromeClient(new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mTitle.setText(title);
        }
    });
