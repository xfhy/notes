# 简单使用阿里云api

# 使用 AppCode 调用（简单身份认证）

> 您可以通过 APPCODE 的方式，实现到被调用接口的身份认证，获取访问相关 API 的调用权限。

使用方法

请求Header中添加的Authorization字段；
配置Authorization字段的值为“APPCODE ＋ 半角空格 ＋APPCODE值”。

格式：

Authorization:APPCODE AppCode值

[查看我的 AppCode值](https://market.console.aliyun.com/imageconsole/index.htm?spm=5176.7744816.2.1.mOl1En#/apiService/list)

使用时:

	/**
     * appcode
     */
    public static final String APP_CODE = "xxxxxxxxxxxxxxx";
	String host = "http://toutiao-ali.juheapi.com";
	String path = "/toutiao/index";

	private void getDataFromServer() {
        RequestParams params = new RequestParams(host+path);
        params.setHeader("Authorization", "APPCODE " + APP_CODE);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.d(TAG, "onSuccess: "+result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }
