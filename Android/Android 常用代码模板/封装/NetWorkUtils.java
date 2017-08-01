
public final class NetWorkUtils {
    
    private static LoadingDialog mDialog;
    
    /**
     * @param context
     * @param url
     *            请求URL
     * @param action
     *            请求方法
     * @param requestModel
     *            请求参数
     * @param callback
     *            回调
     */
    public static synchronized void start(final Context context,
                                          final String url,
                                          final String action,
                                          final Serializable requestModel,
                                          final ResponseCallback callback) {
        start(context, url, action, requestModel, false, callback, "", "");
    }
    
    /**
     * @param context
     * @param url
     *            请求URL
     * @param action
     *            请求方法
     * @param requestModel
     *            请求参数
     * @param isShowDialog
     *            是否显示对话框，true显示，false不显示
     * @param callback
     *            回调
     */
    public static synchronized void start(final Context context,
                                          final String url,
                                          final String action,
                                          final Serializable requestModel,
                                          final boolean isShowDialog,
                                          final ResponseCallback callback) {
        start(context, url, action, requestModel, isShowDialog, "", callback, "", "");
    }
    
    /**
     * @param context
     * @param url
     * @param action
     * @param requestModel
     * @param isShowDialog
     * @param callback
     * @param pid
     *            网络请求签名验证pid
     * @param secret
     *            网络请求签名验证secret
     */
    public static synchronized void start(final Context context,
                                          final String url,
                                          final String action,
                                          final Serializable requestModel,
                                          final boolean isShowDialog,
                                          final ResponseCallback callback,
                                          final String pid,
                                          final String secret) {
        start(context, url, action, requestModel, isShowDialog, "", callback, pid, secret);
    }
    
    /**
     * @param context
     * @param url
     *            请求URL
     * @param action
     *            请求方法
     * @param requestModel
     *            请求参数
     * @param isShowDialog
     *            是否显示对话框，true显示，false不显示 对话框内容
     * @param callback
     *            回调
     * @param pid
     *            网络请求签名验证pid，默认已有，如需要单独设置，请传值
     * @param secret
     *            网络请求签名验证secret
     */
    public static synchronized void start(final Context context,
                                          final String url,
                                          final String action,
                                          final Serializable requestModel,
                                          final boolean isShowDialog,
                                          final String dialogHint,
                                          final ResponseCallback callback,
                                          final String pid,
                                          final String secret) {
        ResponseCallback responseCallback = new ResponseCallback() {
            
            @Override
            public void onLoading() {
                callback.onLoading();
            }
            
            @Override
            public void onSuccess(String result) {
                BaseResultModel baseResult;
                try {
                    LogUtils.v("NetworkUtils", String.format("返回值：（请求URL: %s请求Action: %s==%s", url, action, result));
                    baseResult = JSON.parseObject(result, BaseResultModel.class);
                    // 成功
                    if (baseResult.success || baseResult.code == 0) {
                        callback.onSuccess(baseResult.data);
                    }
                    else {
                        // 登录失效，发送广播重新跳转到登录界面
                        if (baseResult.code == 7) {
                            Intent intent = new Intent();
                            intent.setAction("com.diaoxian");
                            intent.putExtra("diaoXian", "10010001010");
                            context.sendBroadcast(intent);
                        }
                        callback.onError(new Error(baseResult.message, baseResult.code, baseResult.data));
                    }
                }
                catch (Exception e) {
                    callback.onError(new Error("数据解析异常", 10000, ""));
                    e.printStackTrace();
                }
                
            }
            
            @Override
            public void onError(Error error) {
                callback.onError(error);
            }
        };
        
        String params = RequestParamsUtils.createRequestParams(requestModel, action, context, pid, secret);
        LogUtils.i("NetworkUtils", String.format("请求值：（请求URL: %s请求Action: %s==%s", url, action, params));
        NetworkRequest.start(context, url, params, isShowDialog, dialogHint, responseCallback);
    }

    
    /**
     * @param context
     * @param url
     * @param param
     * @param isShowDialog
     * @param callback
     */
    public static synchronized void startForCashier(final Context context,
                                                    final String url,
                                                    final String param,
                                                    final boolean isShowDialog,
                                                    final ResponseCallback callback) {
        startForCashier(context, url, param, isShowDialog, "", callback);
    }
    
    /**
     * @param context
     * @param url
     *            请求URL
     * @param param
     *            请求参数
     * @param isShowDialog
     *            是否显示对话框，true显示，false不显示
     * @param dialogHint
     *            对话框内容
     * @param callback
     *            回调
     */
    public static synchronized void startForCashier(final Context context,
                                                    final String url,
                                                    final String param,
                                                    final boolean isShowDialog,
                                                    final String dialogHint,
                                                    final ResponseCallback callback) {
        ResponseCallback responseCallback = new ResponseCallback() {
            
            @Override
            public void onLoading() {
                callback.onLoading();
            }
            
            @Override
            public void onSuccess(String result) {
                BaseCashierResponse baseResult = null;
                try {
                    byte[] by = Base64Utils.decode(result);
                    String str = new String(by, "utf-8");
                    baseResult = JSON.parseObject(str, BaseCashierResponse.class);
                }
                catch (UnsupportedEncodingException exception) {
                    exception.printStackTrace();
                }
                if (baseResult == null) {
                    callback.onError(new Error(""));
                    return;
                }
                // 成功
                if (baseResult.r.equals("1")) {
                    callback.onSuccess(baseResult.d);
                }
                // 失败
                else {
                    Error e = new Error(baseResult.err_msg);
                    // 返回错误码信息
                    callback.onError(e);
                }
            }
            
            @Override
            public void onError(Error error) {
                callback.onError(error);
            }
        };
        NetworkRequest.start(context, url, param, isShowDialog, dialogHint, responseCallback);
    }
    
    /**
     * 根据tag取消网络请求
     * 
     * @param object
     */
    public static void cancelRequestByTag(Object object) {
        OkHttpUtils.getInstance().cancelTag(object);
    }
    
    /**
     * 关闭对话框
     */
    public static void dismissLoadingDialog(Context context) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
    
    public static void showLoadingDialog(Context context) {
        showLoadingDialog(context, "");
    }

    /**
     * fix backpress as well as call callbacklisten
     */
    public static void showLoadingDialogWithCallBack(Context context,boolean canCancle,DialogInterface.OnCancelListener cancelListener) {
        showLoadingDialog(context, canCancle,cancelListener);
    }
    /**
     * 是否显示dialog
     * 
     * @return
     */
    public static boolean isShowLoadingDialog() {
        if (mDialog == null) {
            return false;
        }
        return mDialog.isShowing();
    }
    
    /**
     * 唤起正在加载的dialog
     *
     * @param message
     *            context
     * @param context
     *            需要显示的消息
     */
    public static void showLoadingDialog(Context context, String message) {
        mDialog = new LoadingDialog(context, message);
        mDialog.show();
    }

    /**
     * 唤起正在加载的dialog
     */
    public static void showLoadingDialog(Context context,boolean canCancle,DialogInterface.OnCancelListener cancelListener) {
        mDialog = new LoadingDialog(context,canCancle, cancelListener);
        mDialog.show();
    }
}
