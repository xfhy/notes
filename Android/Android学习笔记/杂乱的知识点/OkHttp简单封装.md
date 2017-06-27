# OkHttp简单封装

> 由于OkHttp访问网络需要在子线程中进行，所以每次都单独开一个子线程，非常麻烦，而且会导致代码非常臃肿非常混乱。所以这里做了一个简单的封装，使用OkHttp访问网络非常简单了。只需要一句话就行。记得在Application中初始化OkHttpClient哦，

## 封装的代码

	public class HttpUtils {

	    /**
	     * 获取Okhttp客户端
	     * 用于管理所有的请求，内部支持并发，所以我们不必每次请求都创建一个 OkHttpClient
	     * 对象，这是非常耗费资源的
	     */
	    public static OkHttpClient okHttpClient = null;
	
	    /**
	     * 初始化OkHttpClient
	     */
	    public static void initOkHttp() {
	        if (okHttpClient == null) {
	            okHttpClient = new OkHttpClient();
	        }
	    }
	
	    /**
	     * 网络连接是否正常
	     *
	     * @return true:有网络    false:无网络
	     */
	    public static boolean isNetworkConnected(Context context) {
	        if (context != null) {
	            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
	                    .getSystemService(Context.CONNECTIVITY_SERVICE);
	            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
	            if (mNetworkInfo != null) {
	                return mNetworkInfo.isAvailable();
	            }
	        }
	        return false;
	    }
	
	    /**
	     * get方式访问网络
	     *
	     * @param url      要访问的url
	     * @param from     由谁发起的调用,用于区别调用者
	     * @param listener 访问网络的接口回调
	     */
	    public static void requestGet(final String url, final int from, final HttpCallbackListener
	            listener) {
	        //1, 开一个子线程请求网络数据
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	
	                //2, 创建请求
	                Request request = new Request.Builder().url(url).build();
	                try {
	                    //3, 发送请求
	                    Response response = HttpUtils.okHttpClient.newCall(request).execute();
	
	                    //4, 请求成功
	                    if (response.isSuccessful()) {
	                        if (listener != null) {
	                            //回调成功的接口
	                            listener.onFinish(from, response.body().string());
	                        }
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    if (listener != null) {
	                        //回调失败的接口
	                        listener.onError(e);
	                    }
	                }
	            }
	        }).start();
	    }
	
	}

## 其中用到的接口

	/**
	 * Created by xfhy on 2017/6/18.
	 * 网络调用回调接口
	 */
	
	public interface HttpCallbackListener {
	
	    /**
	     * 网络数据访问成功回调
	     * @param from  由谁发起的调用,用于区别调用者
	     * @param response 访问成功返回的数据
	     */
	    void onFinish(int from, String response);
	
	    /**
	     * 在这里对异常情况进行处理
	     * @param e
	     */
	    void onError(Exception e);
	
	}

