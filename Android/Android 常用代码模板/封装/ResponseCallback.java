

/**
 * 网络请求回调接口
 * 

 */
public interface ResponseCallback {
    /**
     * 加载进度视图 可选
     *
     */
    void onLoading();

    /**
     * 成功才调用该方法 返回数据处理
     *
     * @param result
     *            返回值
     */
    void onSuccess(String result);

    /**
     * 客户端请求失败
     *
     * @param error
     *            错误信息
     */
    void onError(Error error);
}
