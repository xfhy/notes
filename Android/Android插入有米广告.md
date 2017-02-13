# Android插入有米广告 #
<font size="5"><b>
PS:个人开发商也可以通过广告来赚取资金,是不是挺爽的呢.本文是叙述的Android平台,有米广告.首先需要去有米广告官网申请一个开发者账号,然后申请应用的发布 ID 和密钥,比较简单,这里不再阐述.本文主要是记录一下在Android应用中插入无积分广告条调用.因为之前因为一直看不懂官方的api,后来搞了好久才成功,所以在这里记录一下,希望同样看不懂的朋友可以通过这里学点东西.<br/>
-------------------------------------------------------------------
一. 首先我们需要配置有米 Android SDK 通用基本配置
1. 导入 SDK : 将 sdk 解压后的 libs 目录下的 YoumiSdk_*.jar 文件导入到工程指定的 libs 目录.将 sdk 解压后的 assets 目录下的 dex.jar （文件名不能变动）文件导入到工程的 assets 目录下<br/>
2. 权限配置: 请将下面权限配置代码复制到 AndroidManifest.xml 文件中：<br/>

	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

	<!-- 如果使用积分墙广告,还需要配置下面权限 -->
	<uses-permission android:name="android.permission.GET_TASKS" />
	<uses-permission
            android:name="android.permission.PACKAGE_USAGE_STATS"
            tools:ignore="ProtectedPermissions" />

	<!-- 以下为可选权限 -->
	<uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
<br/>
3. 初始化应用信息:请务必在应用第一个 Activity（启动的第一个类）的 onCreate 中调用以下代码<br/>

	import net.youmi.android.AdManager;
	...
	AdManager.getInstance(Context context).init(String appId, String appSecret, boolean isTestModel, boolean isEnableYoumiLog);
	参数说明：
	appId 和 appSecret 分别为应用的发布 ID 和密钥，由有米后台自动生成，通过在有米后台 > 应用详细信息 可以获得。
	isTestModel : 是否开启测试模式，true 为是，false 为否。（上传有米审核及发布到市场版本，请设置为 false）
	isEnableYoumiLog: 是否开启有米的Log输出，默认为开启状态
	上传到有米主站进行审核时，务必开启有米的Log，这样才能保证通过审核
	开发者发布apk到各大市场的时候，强烈建议关闭有米的Log
--------------------------------------------------------------------
二.无积分广告调用（重要）
1. 复制以下代码到要展示广告的 Activity 的 layout 文件中，并且放在合适的位置：<br/>
----------------------------------------------
	<LinearLayout
    android:id="@+id/ll_banner"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:gravity="center_horizontal">
	</LinearLayout>
<br/>
2. 将控件加入布局,在展示广告的 Activity 类中，添加如下代码：
--------------------------------------------------------
	// 获取广告条
	View bannerView = BannerManager.getInstance(Context context)
    .getBannerView(new net.youmi.android.normal.banner.BannerViewListener(){

                          /**
                           * 请求广告成功
                           */
                          @Override
                          public void onRequestSuccess() {

                          }

                          /**
                           * 切换广告条
                           */
                          @Override
                          public void onSwitchBanner() {

                          }

                          /**
                           * 请求广告失败
                           */
                          @Override
                          public void onRequestFailed() {

                          }
                      });

	// 获取要嵌入广告条的布局
	LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.ll_banner);

	// 将广告条加入到布局中
	bannerLayout.addView(bannerView);
<br/>
3. BannerViewListener 定义如下,注意,注意,这里的BannerViewListener是有米已经定义好了的接口,而不是自己去定义.：
---------------------------------------
	 public interface BannerViewListener {

    /**
     * 请求广告成功
     */
    void onRequestSuccess();

    /**
     * 切换广告条
     */
    void onSwitchBanner();

    /**
     * 请求广告失败
     */
    void onRequestFailed();
	}
<br/>
<br/>
三.申请接入正式广告<br/>
你以为上面的完成了就可以了么?哈哈,还早着呢,还需要到官网个人中心那里去申请添加应用,然后将自己的应用上传到这里,然后需要有米审核通过,你才可以得到有米的正式广告显示在你的应用中,不然,只是显示一个测试广告在你的应用中.
----------------------------------------------------------------
本文参考链接如下:
[https://www.youmi.net/sdk/android/16/doc/644/2/cn/%E6%9C%89%E7%B1%B3AndroidSDK%E9%80%9A%E7%94%A8%E5%9F%BA%E6%9C%AC%E9%85%8D%E7%BD%AE%E6%96%87%E6%A1%A3.html](https://www.youmi.net/sdk/android/16/doc/644/2/cn/%E6%9C%89%E7%B1%B3AndroidSDK%E9%80%9A%E7%94%A8%E5%9F%BA%E6%9C%AC%E9%85%8D%E7%BD%AE%E6%96%87%E6%A1%A3.html "有米 Android SDK 通用基本配置开发者文档¶")<br/>
[https://www.youmi.net/sdk/android/16/doc/630/4/cn/%E6%9C%89%E7%B1%B3AndroidSDK%E6%97%A0%E7%A7%AF%E5%88%86%E5%B9%BF%E5%91%8A%E5%BC%80%E5%8F%91%E8%80%85%E6%96%87%E6%A1%A3.html](https://www.youmi.net/sdk/android/16/doc/630/4/cn/%E6%9C%89%E7%B1%B3AndroidSDK%E6%97%A0%E7%A7%AF%E5%88%86%E5%B9%BF%E5%91%8A%E5%BC%80%E5%8F%91%E8%80%85%E6%96%87%E6%A1%A3.html "有米 Android 无积分开发者文档")
</b></font>
