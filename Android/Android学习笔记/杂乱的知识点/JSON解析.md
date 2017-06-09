# JSON解析

> 比起XML,JSON的主要优势在于它的体积小,在网络上传输的时候可以更省流量.但缺点在于,它的语义性较差,看起来不如xml直观.

> 解析JSON数据有很多方法.可以使用官方提供的JSONObject,也可以使用谷歌的开源库GSON.另外,一些第三方的开源库如Jackson,FastJSON等也非常不错.

## 1. 使用JSONObject

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

## 2. 使用GSON

> 神奇之处在于,它可以将一段JSON格式的字符串自动映射成一个对象,从而不需要我们再手动去编写代码进行解析了.

**对象书写技巧**:

1.逢{}创建对象,逢[]创建集合(一般是ArrayList)

2.所有字段名称要和json返回字段高度一致,如果不太适合直接作为Java字段来命名,可以使用@SerializedName注解的方式来让JSON字段和java字段之间建立映射关系

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
