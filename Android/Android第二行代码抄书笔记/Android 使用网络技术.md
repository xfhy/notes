# Android 使用网络技术

[TOC]

# 1. 使用WebView

> 有时候我们会遇到特殊需求,比如说要求在应用程序中展示一些网页.明确指出不允许打开系统浏览器.

	WebView webView = (WebView) findViewById(R.id.web_view);
	//设置支持JavaScript脚本
    webView.getSettings().setJavaScriptEnabled(true);
	//设置当需要从一个网页跳转到另一个网页时,任然在该WebView中显示
    webView.setWebViewClient(new WebViewClient());
	//展示相应网页的内容
    webView.loadUrl("http://www.baidu.com");

# 2. 使用Http协议访问网络

> 在Android 6.0系统中,HttpClient的功能被完全移除了,标志这此功能被正式弃用.官方建议使用HttpURLConnection.

## 2.1 使用HttpURLConnection

> 通常情况下,我们都应该将这些通用的网络操作提取到一个公共的类里,并提供一个静态方法,当想要发起网络请求的时候,只需要简单地调用一下即可.

**准备工作**

子线程不能通过return返回数据,所以需要利用java的回调机制.将响应数据返回给调用方.先新建一个HttpCallbackListener

	public interface HttpCallbackListener{
		//调用方在这里根据返回的内容执行具体的内容
		void onFinish(String response);
		//在这哭对异常情况进行处理
		void onError(Exception e);
	}

**GET方式**

请求服务器返回数据

	public class HttpUtil {
	
		/**
	     * 发送网络请求
	     * 这个需要放在子线程中操作
	     * 子线程不能更新UI
	     */
		public static String sendHttpRequest(final String address,final HttpCallbackListener listener){
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					Httpconnection connection = null;
					try {
						//1. 创建一个URL对象
						URL url = new URL("https://www.baidu.com");
						//2. 打开连接  获得Httpconnection
						connection = (Httpconnection) url.openConnection();
						//3. 设置HTTP请求的方式
						connection.setRequestMethod("GET");
						//4. 设置一些参数   连接超时时间    读取超时时间
						connection.setConnectTimeout(8000);
						connection.setReadTimeout(8000);
						//5. 在获取url请求的数据前需要判断响应码，200 ：成功,206:访问部分数据成功
						// 300：跳转或重定向  400：错误 500：服务器异常
						int responseCode = connection.getResponseCode();
						if (responseCode == 200) {
							//6. 获取服务器返回的输入流
							InputStream inputStream = connection.getInputStream();
							//7. 将输入量解析出来
							String result = StreamUtils.streamToString(inputStream);

							if(listener != null){
								//回调onFinish()方法
								listener.onFinish(result);
							}
						}
					} catch (Exception e) {
						if(listener != null){
							//回调onError()方法
							listener.onError(e);
						}
						e.printStackTrace();
					} finally {
						//9. 最后记得断开连接
						if (connection != null){
							connection.disconnect();//关闭http连接
						}
					}
				}
			}).start();
	                
		}
		
	}

这其中StreamUtils代码如下:

	public class StreamUtils {
    
	    //将流转换为String
	    public static String streamToString(InputStream inputStream) {
	        StringBuilder sb = new StringBuilder();
	        String line = "";
	        BufferedReader bufferedReader = null;
	        try {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
	            while( (line=bufferedReader.readLine()) != null ){
	                sb.append(line);
	            }
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if(bufferedReader != null){
	                try {
	                    bufferedReader.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	
	        return sb.toString();
	    }
	}

**POST方式**

提交数据给服务器,把HTTP请求的方式改成POST,并在获取输入流之前把要提交的数据写出即可.注意每条
数据都要以键值对的形式存在,数据与数据之间用`&`符号隔开.

		//3. 设置HTTP请求的方式
        urlConnection.setRequestMethod("POST");

		connection.setRequestMethod("POST");
		DataOutPutStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes("username=admin&passwod=123456");

## 2.2 使用OkHttp

> 开源库,网络通信.有许多出色的网络通信库都可以替代原生的HttpURLConnection,而其中OkHttp无疑是
做得最出色的一个.

> 现在已经成了广大Android开发者首选的网络通信库.

> OkHttp的项目主页地址是`https://github.com/square/okhttp`

**使用方法**

1. 编辑`app/build.gradle`,在`dependencies`中添加如下内容:

	compile 'com.squareup.okhttp3:okhttp:3.6.0'

2. 发送GET请求

		//1. 首先创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //2. 创建Request对象
        Request request = new Request.Builder()
                .url("http://www.baidu.com")   //设置目标地址URL
                .build();
        //3.获取服务器返回的数据    就是这个Response对象
        Response response = client.newCall(request).execute();
        //4. 将数据弄出来
        String responseData = response.body().string();
        //5 . 通过Handler更新UI
        Message msg = Message.obtain();
        msg.obj = responseData;
        handler.sendMessage(msg);

在开发中一般是像下面这样用的

	public class HttpUtil {
	
		/**
	     * 发送网络请求  用OkHttp    GET方式
		 * sendRequestWithOkHttp()方法中有一个okhttp3.Callback参数,这个是OkHttp库中自带的一个回调接口
		 * OkHttp在enqueue()方法中已经帮我们开好子线程了,然后在子线程中去执行HTTP请求,并将最终的请求结果回调到
		 * okhttp3.Callback当中.
	     */
	    public static void sendRequestWithOkHttp(String address,okhttp3.Callback callback){
	        
			//1. 首先创建OkHttpClient对象
			OkHttpClient client = new OkHttpClient();
			//2. 创建Request对象
			Request request = new Request.Builder()
					.url(address)   //设置目标地址URL
					.build();
			//3.获取服务器返回的数据    
			client.newCall(request).enqueue(callback);
		}
		
	}

当然这样写到HttpUtil类里面的话,调用方需要这样写:

	HttpUtil.sendOkHttpRequest("http://www.baidu.com",new okhttp3.Callback(){
		@Override
		public void onResponse(Call call,Response response) throws IOException {
			//得到服务器返回的具体内容
			String responseData = response.body().string();
		}
		
		@Override
		public void onFailure(Call call, IOException e) {
			//在这里对异常情况进行处理
		}
		
	});

3. 发送POST请求

我们需要先构造出一个RequestBody对象来存放待提交的参数

	RequestBody requestBody = new FormBody.Builder()
		.add("username","admin")
		.add("password","123")
		.build();
然后在Request.Builder中调用post()方法,并将RequestBody对象传入

	Request request = new Request.Builder()
		.url("http://www.baidu.com")
		.post(requestBody)
		.build();

# 3. 解析xml格式

**Pull解析方式**

	private void parseXMLWithPull(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    // 开始解析某个结点
                    case XmlPullParser.START_TAG: {
                        if ("id".equals(nodeName)) {
                            id = xmlPullParser.nextText();
                        } else if ("name".equals(nodeName)) {
                            name = xmlPullParser.nextText();
                        } else if ("version".equals(nodeName)) {
                            version = xmlPullParser.nextText();
                        }
                        break;
                    }
                    // 完成解析某个结点
                    case XmlPullParser.END_TAG: {
                        if ("app".equals(nodeName)) {
                            Log.d("MainActivity", "id is " + id);
                            Log.d("MainActivity", "name is " + name);
                            Log.d("MainActivity", "version is " + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

**SAX解析方式**

	public class ContentHandler extends DefaultHandler {

	    private String nodeName;
	
	    private StringBuilder id;
	
	    private StringBuilder name;
	
	    private StringBuilder version;
	
	    @Override
	    public void startDocument() throws SAXException {
	        id = new StringBuilder();
	        name = new StringBuilder();
	        version = new StringBuilder();
	    }
	
	    @Override
	    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	        // 记录当前结点名
	        nodeName = localName;
	    }
	
	    @Override
	    public void characters(char[] ch, int start, int length) throws SAXException {
	        // 根据当前的结点名判断将内容添加到哪一个StringBuilder对象中
	        if ("id".equals(nodeName)) {
	            id.append(ch, start, length);
	        } else if ("name".equals(nodeName)) {
	            name.append(ch, start, length);
	        } else if ("version".equals(nodeName)) {
	            version.append(ch, start, length);
	        }
	    }
	
	    @Override
	    public void endElement(String uri, String localName, String qName) throws SAXException {
	        if ("app".equals(localName)) {
	            Log.d("ContentHandler", "id is " + id.toString().trim());
	            Log.d("ContentHandler", "name is " + name.toString().trim());
	            Log.d("ContentHandler", "version is " + version.toString().trim());
	            // 最后要将StringBuilder清空掉
	            id.setLength(0);
	            name.setLength(0);
	            version.setLength(0);
	        }
	    }
	
	    @Override
	    public void endDocument() throws SAXException {
	        super.endDocument();
	    }
	
	}

然后

	private void parseXMLWithSAX(String xmlData) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ContentHandler handler = new ContentHandler();
            // 将ContentHandler的实例设置到XMLReader中
            xmlReader.setContentHandler(handler);
            // 开始执行解析
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


# 4. 解析JSON格式

> 比如XML,JSON的主要优势在于它的体积小,在网络上传输的时候可以更省流量.但缺点在于,它的语义性较差,看起来不如xml直观.

> 解析JSON数据有很多方法.可以使用官方提供的JSONObject,也可以使用谷歌的开源库GSON.另外,一些第三方的开源库如Jackson,FastJSON等也非常不错.

## 4.1 使用JSONObject

	private void parseJSONWithJSONObject(String jsonData){
        /**
         * 客户端数据为
         * [{"id":"5","version":"5.5","name":"Clash of Clans"},
         {"id":"6","version":"7.0","name":"Boom Beach"},
         {"id":"7","version":"3.5","name":"Clash Royale"}
         ]
         */
        try {
            //1. 创建JSONArray   里面是数组
            JSONArray jsonArray = new JSONArray(jsonData);
            //2. 解析数组里面的每一个数据
            for (int i=0; i<jsonArray.length(); i++){
                //3. 一个数组元素就是一个JSONObject
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //4. 根据key获取对应的值
                String id = jsonObject.getString("id");
                String version = jsonObject.getString("version");
                String name = jsonObject.getString("name");

                Log.i(TAG, "parseJSONWithJSONObject: id"+id);
                Log.i(TAG, "parseJSONWithJSONObject: version"+version);
                Log.i(TAG, "parseJSONWithJSONObject: name"+name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

## 4.2 使用GSON

> 神奇之处在于,它可以将一段JSON格式的字符串自动映射成一个对象,从而不需要我们再手动去编写代码进行解析了.

**准备工作**:

编辑app/build.gradle文件,在dependencies闭包中添加如下内容

`compile 'com.google.code.gson:gson:2.7'`


**示例**

服务端的json数据为

		[{"id":"5","version":"5.5","name":"Clash of Clans"},
         {"id":"6","version":"7.0","name":"Boom Beach"},
         {"id":"7","version":"3.5","name":"Clash Royale"}
         ]

这里首先需要创建一个app实体类

	public class App {

	    private String id;
	    private String name;
	    private String version;
	
	    public String getId() {
	        return id;
	    }
	
	    public void setId(String id) {
	        this.id = id;
	    }
	
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	        this.name = name;
	    }
	
	    public String getVersion() {
	        return version;
	    }
	
	    public void setVersion(String version) {
	        this.version = version;
	    }
	}

然后这样写:

	 private void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        List<App> appList = gson.fromJson(jsonData,new TypeToken<List<App>>(){}.getType());
        for (App app : appList) {
            Log.i(TAG, "parseJSONWithGSON: "+app.getId());
            Log.i(TAG, "parseJSONWithGSON: "+app.getName());
            Log.i(TAG, "parseJSONWithGSON: "+app.getVersion());
        }
    }

如果是单独的一个JApp对象,不是一个数组,那么这样写:

	App app = json.fromJson(jsonData, App.class);

**有时候由于JSON中的一些字段可能不太适合直接作为Java字段来命名,因此这里使用了@SerializedName注解的方式来让JSON字段和java字段之间建立映射关系**

类似于下面这样

	json结构如下:
	  "basic" :{
	      "city":"苏州",
	      "id":"CN101190401",
	      "update":{
	          "loc":"2016-08-08 21:58"
	      }
	  }

---
	public class Basic {

	    @SerializedName("city")
	    public String cityName;
	
	    @SerializedName("id")
	    public int weatherId;
	
	    public Update update;
	
	    public class Update {
	        @SerializedName("loc")
	        public String updateTime;
	    }
	
	}


# 5. 