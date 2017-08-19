# Simple XML解析XML

# 源数据如下:

	<rss version="2.0">
		<channel>
		
			<item>
				<newsid>321993</newsid>
				<title><![CDATA[德国骨科之力：和泉纱雾手办开订，买手办送角色CV写真]]></title>
				<v>000</v>
				<url><![CDATA[/html/it/321993.htm]]></url>
				<postdate>2017-8-19 12:40:45</postdate>
				<image>http://img.ithome.com/newsuploadfiles/thumbnail/2017/8/321993.jpg</image>
				<description><![CDATA[近日，A-1改编的四月番《情色漫画老师》女主角和泉纱雾最新手办开订，其中，豪华版还将附赠和泉纱雾声优藤田茜的写真集，以及原作者伏见司签名的明信片和电]]></description>
				<hitcount>805</hitcount>
				<commentcount>28</commentcount>
				<forbidcomment>false</forbidcomment>
				<cid>32</cid>
			</item>
			<item>
				<newsid>321992</newsid>
				<title><![CDATA[三星“弃疗”：Note 8旗舰机现身官网，外观一览无余]]></title>
				<v>000</v>
				<url><![CDATA[/html/android/321992.htm]]></url>
				<postdate>2017-8-19 12:38:42</postdate>
				<image>http://img.ithome.com/newsuploadfiles/thumbnail/2017/8/321992.jpg</image>
				<description><![CDATA[从外观到配置以及价格，三星即将在8月23日正式发布的下半年旗舰Note 8基本上已经没有秘密可言，但意外的是三星在发布会之前自己也做了一把爆料者]]></description>
				<hitcount>3820</hitcount>
				<commentcount>84</commentcount>
				<forbidcomment>false</forbidcomment>
				<cid>74</cid>
			</item>
			<item>
				<newsid>321991</newsid>
				<title><![CDATA[高通高层：一定能赢下和苹果的专利诉讼]]></title>
				<v>000</v>
				<url><![CDATA[/html/it/321991.htm]]></url>
				<postdate>2017-8-19 12:35:02</postdate>
				<image>http://img.ithome.com/newsuploadfiles/thumbnail/2017/8/321991.jpg</image>
				<description><![CDATA[芯片巨头高通和苹果的专利侵权诉讼已经持续了好几个月，目前看起来也远未到结束的时候。不过，近日该公司执行副总裁兼总法律顾问Donald J. Rosenberg强调，高通一定会赢]]></description>
				<hitcount>402</hitcount>
				<commentcount>20</commentcount>
				<forbidcomment>false</forbidcomment>
				<cid>150</cid>
			</item>
			<item>
				<newsid>321990</newsid>
				<title><![CDATA[vivo X20通过3C认证：支持18W快充]]></title>
				<v>000</v>
				<url><![CDATA[/html/android/321990.htm]]></url>
				<postdate>2017-8-19 12:13:23</postdate>
				<image>http://img.ithome.com/newsuploadfiles/thumbnail/2017/8/321990.jpg</image>
				<description><![CDATA[vivo X9s、X9s Plus之后，vivo新旗舰已经在路上了。近日，两款型号为X20和X20A的vivo新机已通过3C认证，从相关信息来看，这两款机型支持最大18W快充]]></description>
				<hitcount>2814</hitcount>
				<commentcount>50</commentcount>
				<forbidcomment>false</forbidcomment>
				<cid>74</cid>
			</item>
			<item>
				<newsid>321973</newsid>
				<title><![CDATA[量变的8代Core：更多Intel Coffee Lake处理器规格曝光]]></title>
				<v>000</v>
				<url><![CDATA[/html/digi/321973.htm]]></url>
				<postdate>2017-8-19 10:49:35</postdate>
				<image>http://img.ithome.com/newsuploadfiles/thumbnail/2017/8/321973.jpg</image>
				<description><![CDATA[在昨天我们报道了Coffee Lake的性能提升幅度PPT，但由于照片太模糊，部分参数无法看清，本着对读者负责任的态度，干脆只给出型号和单/多线程性能提升的资料]]></description>
				<hitcount>6030</hitcount>
				<commentcount>125</commentcount>
				<forbidcomment>false</forbidcomment>
				<cid>100</cid>
			</item>
		
		</channel>
	</rss>

# 开始解析

可以看到源数据分为3层,我分别以ScienceRSS,ScienceChannel,ScienceNews来名称

- 第一层是一个对象
- 第二层是一个List
- 第三层是一个对象

## 第一层

	@Root(name = "rss", strict = false)
	public class ScienceRSS {
	//里面有一个version,如果不加strict = false就会报错
	
	    @Element(name = "channel")
	    public ScienceChannel scienceChannel;
	}
## 第二层

	@Root(name = "channel")  //根元素
	public class ScienceChannel {
	
	    @ElementList(inline = true, required = false)  //里面是数组
	    public List<ScienceNews> mScienceNewsList;
	
	    public List<ScienceNews> getmScienceNewsList() {
	        return mScienceNewsList;
	    }
	
	    public void setmScienceNewsList(List<ScienceNews> mScienceNewsList) {
	        this.mScienceNewsList = mScienceNewsList;
	    }
	}
## 第三层

	@Root(name = "item", strict = false)
	public class ScienceNews {
	
	    /**
	     * 新闻id
	     */
	    @Element(name = "newsid")
	    public String newsId;
	    /**
	     * 新闻标题
	     */
	    @Element(name = "title")
	    public String title;
	    /**
	     * 新闻的url(不完整)
	     */
	    @Element(name = "url")
	    public String url;
	    /**
	     * 更新时间
	     */
	    @Element(name = "postdate")
	    public String postdate;
	    /**
	     * 图片地址
	     */
	    @Element(name = "image")
	    public String image;
	    /**
	     * 描述
	     */
	    @Element(name = "description")
	    public String description;
	    /**
	     * 点击数量
	     */
	    @Element(name = "hitcount")
	    public String hitCount;
	    /**
	     * 评论数量
	     */
	    @Element(name = "commentcount")
	    public String commentCount;
	    /**
	     * 禁止评论?   false  true
	     */
	    @Element(name = "forbidcomment")
	    public boolean forbidComment;
	    @Element(name = "cid")
	    public String cid;
	}

# 正式开始用Simple XML进行解析

	Persister persister = new Persister();
	ScienceRSS scienceRSS = persister.read(ScienceRSS.class, result);
    ScienceChannel scienceChannel = scienceRSS.scienceChannel;
    return scienceChannel.getmScienceNewsList();

怎么样,是不是非常简单啊.不过现在用XML是真的少,一般都用JSON.了解一下还是好的.