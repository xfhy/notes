# 使用LitePal操作数据库

> 开源库 LitePal.LitePal是一款开源的Android数据库框架,它采用了对象关系映射(ORM)的模式,并将我们平时开发最常用到的一些数据库功能进行了封装,使得不用编写一行SQL语句就可以完成各种建表和增删改查的操作.详细使用文档,[地址](https://github.com/LitePalFramework/LitePal).

> 现在大多数的开源项目都会将版本提交到`jcenter`上,我们只需要在app/build.gradle文件中声明该开源库的引用就可以了

**使用方法**

###  配置工作

1. 在`dependencies`闭包中添加如下内容 ` compile 'org.litepal.android:core:1.4.1' `

2. 接下来需要配置litepal.xml文件.右击app/src/main目录->new->Directory,创建一个assets目录,然后在assets目录下再新建一个litepal.xml文件,编辑如下:

		<?xml version="1.0" encoding="utf-8" ?>
		<litepal>
		
		    <!--数据库名-->
		    <dbname value="BookStore"></dbname>
		
		    <!--数据库版本号-->
		    <version value="2"></version>
		
		    <!--所有的映射模型-->
		    <list>
		        <!--使用mapping标签来声明我们要配置的映射模型类-->
		        <mapping class="com.xfhy.litepaltest.bean.Book"></mapping>
		        <mapping class="com.xfhy.litepaltest.bean.Category"></mapping>
		    </list>
		</litepal>

3. 最后还需要配置一下`LitePalApplication`,修改AndroidManifest.xml中的代码,在`application`标签下插入` android:name="org.litepal.LitePalApplication" `

### 使用

**创建和升级数据库**

1. 新建一个bean对象,必须继承自`DataSupport`,写好getter和setter方法.
2. 在litepal.xml中的`list`标签里面写上
`<mapping class="com.xfhy.litepaltest.bean.Book"></mapping>`
`<mapping>`用来声明我们配置的映射模型表

3. 调用`LitePal.getDatabase();`即可创建数据库.该方法返回值 : A writable SQLiteDatabase instance.调用完,即可创建表.

4. 更新数据库:比如新建一个bean对象Category(也需要将<`mapping class="com.xfhy.litepaltest.bean.Category"></mapping>`添加到litepal.xml中),或者增加Book这个bean对象的属性等.
直接改好之后,将litepal.xml中的`version`值加1,然后调用`LitePal.getDatabase();`即可更新数据库,category表将会创建,book新增加的属性也会加到book表中成为1列.以前的数据也不会丢失.

**使用ListPal添加数据**
> 封装数据到bean对象,然后调用bean对象的save()方法即可添加该bean对象的数据到数据库

	private void addData() {
	        //新建一个实体类,放好数据,调用save()即可插入数据库
	        Book book = new Book();
	        book.setName("语文书");
	        book.setAuthor("晓峰哈虐");
	        book.setPages(454);
	        book.setPrice(45);
	        book.setPress("三和");
	
	        //这里会重复保存,如果多点击一次Add按钮
	        boolean result = book.save();   //保存到数据库  成功则返回true
	        if(result){
	            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
	        } else {
	            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
	        }
	    }

**使用LitePal更新数据**
> 将需要更新为xx的数据封装到bean对象中,然后调用updateAll(),将where部分填好.

	 	Book book = new Book();
        book.setAuthor("你猜");   //更新为xx
        book.setPages(222);

        //这里和SqLiteDatabase的update()方法的where参数部分有点类似,但更简洁
        book.updateAll("name= ? and author = ?","数学书","晓峰哈虐");

更新所有字段为默认值:book.updateAll();

**使用LitePal删除数据**

	//删除数据   不传参则删除全部
    //参数:表名.class   where占位符   占位符的值
    DataSupport.deleteAll(Book.class,"price < ?","20");

**使用LitePal查询数据**

查询全部:

	List<Book> books = DataSupport.findAll(Book.class);
    for (Book book : books) {
        Log.i(TAG, "name: "+book.getName());
        Log.i(TAG, "author: "+book.getAuthor());
        Log.i(TAG, "press: "+book.getPress());
        Log.i(TAG, "id: "+book.getId());
        Log.i(TAG, "pages: "+book.getPages());
        Log.i(TAG, "price: "+book.getPrice());
    }

	//查询第一条数据
    Book firstBook = DataSupport.findFirst(Book.class);
    //查询book表的最后一条数据
    Book lastBook = DataSupport.findLast(Book.class);

    //查询book表的 指定的几列数据
    DataSupport.select("name","author").find(Book.class);

    //查询book表的   指定的约束条件  数据
    DataSupport.where("pages > ?","400").find(Book.class);

    //查询book表的  指定结果的排序方式
    DataSupport.order("price desc").find(Book.class);  //desc是降序     asc或者不写是升序

    //查询book表的  指定查询结果数量
    DataSupport.limit(3).find(Book.class);

    //指定查询结果的偏移量
    DataSupport.offset(3).find(Book.class);
    DataSupport.limit(3).offset(3).find(Book.class);  //查询第4,5,6条数据

    //组合查询
    DataSupport.select("name","author","pages")
            .where("pages > ?","400")
            .order("pages")
            .limit(10)
            .offset(10)
            .find(Book.class);

    //用原生SQL语句进行查询
    DataSupport.findBySQL("select * from Book where pages > ? and price < ?","400","20"); 