# 使用GSON解析json数据

- 将json数据转换成java bean

		Gson gson = new Gson();
	    MovieListBean movieListBean = gson.fromJson(result, MovieListBean.class);

# 1.常见错误

**使用GSON一直报错**

**com.google.gson.JsonSyntaxException: Java.lang.IllegalStateException: closed**

 
解决:OkHttp请求,response.body().string()只能调用一次

# 2.序列化时排除字段的几种方式

- 排除transient字段 给字段加上transient修饰符就可以了
- 使用@Expose注解
