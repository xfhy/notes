# xutils3基本使用

## 1.引入依赖

`使用compile 'org.xutils:xutils:3.4.0',这个版本要旧一点,但是比3.5.0更加兼容更多的机型`

## 2.使用xUtils3加载图片

	//简单加载,用户体验不好
        //x.image().bind(mImage,image_url);

        //建造者模式  设置ImageOptions
        ImageOptions.Builder builder = new ImageOptions.Builder();
        //设置加载中的图片
        builder.setLoadingDrawableId(R.drawable.loading);
        //设置加载失败显示的图片
        builder.setFailureDrawableId(R.drawable.error);
        //设置为渐变进入
        builder.setFadeIn(true);
        //裁剪图片为圆形
        builder.setCircular(true);
        ImageOptions build = builder.build();

        //加载图片
        x.image().bind(mImage,image_url,build);
        
        上面的加载图片默认有缓存..下一次即使没网络依然可以加载成功
        
## 3.xutils3用get方式请求网络数据

	//构建请求参数信息
                RequestParams requestParams = new RequestParams(json_url);
                //发送请求,在回调中处理结果
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "onSuccess: result--" + result);

                        //将json数据转换成java bean
                        Gson gson = new Gson();
                        MovieListBean movieListBean = gson.fromJson(result, MovieListBean.class);
                        Log.d(TAG, "onSuccess: "+movieListBean.toString());
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d(TAG, "onError: " + ex.getMessage() + ex.getCause() + ex
                                .getStackTrace() + ex.getLocalizedMessage());
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.d(TAG, "onCancelled: ");
                    }

                    @Override
                    public void onFinished() {
                        Log.d(TAG, "onFinished: ");
                    }
                });
                

**上面的onSuccess()是在主线程中的..**