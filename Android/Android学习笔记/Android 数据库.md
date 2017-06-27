# Android 数据库

[TOC]

# 1 Android下数据库创建 
	
> 什么情况下我们才用数据库做数据存储？ 大量数据结构相同的数据需要存储时。
 
> mysql sqlserver2000  sqlite 嵌入式 轻量级

SqliteOpenHelper

创建数据库步骤：

1. 创建一个类集成SqliteOpenHelper，需要添加一个构造方法，实现两个方法oncreate ,onupgrade

		构造方法中的参数介绍：
		//context :上下文   ， name：数据库文件的名称    factory：用来创建cursor对象，默认为null 
		//version:数据库的版本号，从1开始，如果发生改变，onUpgrade方法将会调用,android 4.0之后只能升不能降
		super(context, "info.db", null,1);
		

2. 创建这个帮助类的一个对象，调用getReadableDatabase()方法，会帮助我们创建打开一个数据库

3. 复写oncreate和onupgrdate方法：
		oncreate方法是数据库第一次创建的时候会被调用;  特别适合做表结构的初始化,需要执行sql语句；SQLiteDatabase db可以用来执行sql语句
		
		//onUpgrade数据库版本号发生改变时才会执行； 特别适合做表结构的修改



	帮助类对象中的getWritableDatabase 和 getReadableDatabase都可以帮助我们获取一个数据库操作对象SqliteDatabase.

###区别：

getReadableDatabase:
	先尝试以读写方式打开数据库，如果磁盘空间满了，他会重新尝试以只读方式打开数据库。
	
getWritableDatabase:
		直接以读写方式打开数据库，如果磁盘空间满了，就直接报错。

## SQLite数据库

- integer 整型
- real 浮点型
- text文本类型
- blob 二进制类型


- primary key 主键
- autoincrement 自增长

# 2 Android下数据库第一种方式增删改查

1. 创建一个帮助类的对象，调用getReadableDatabase方法，返回一个SqliteDatebase对象

2. 使用SqliteDatebase对象调用execSql()做增删改,调用rawQuery方法做查询。

**特点:增删改没有返回值，不能判断sql语句是否执行成功。sql语句手动写，容易写错**


	private MySqliteOpenHelper mySqliteOpenHelper;
	public InfoDao(Context context){
		//创建一个帮助类对象
		mySqliteOpenHelper = new MySqliteOpenHelper(context);

		
	}

	public void add(InfoBean bean){

		//执行sql语句需要sqliteDatabase对象
		//调用getReadableDatabase方法,来初始化数据库的创建
		SQLiteDatabase 	db = mySqliteOpenHelper.getReadableDatabase();
		//sql:sql语句，  bindArgs：sql语句中占位符的值
		db.execSQL("insert into info(name,phone) values(?,?);", new Object[]{bean.name,bean.phone});
		//关闭数据库对象
		db.close();
	}

	public void del(String name){


		//执行sql语句需要sqliteDatabase对象
		//调用getReadableDatabase方法,来初始化数据库的创建
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		//sql:sql语句，  bindArgs：sql语句中占位符的值
		db.execSQL("delete from info where name=?;", new Object[]{name});
		//关闭数据库对象
		db.close();

	}
	public void update(InfoBean bean){

		//执行sql语句需要sqliteDatabase对象
		//调用getReadableDatabase方法,来初始化数据库的创建
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		//sql:sql语句，  bindArgs：sql语句中占位符的值
		db.execSQL("update info set phone=? where name=?;", new Object[]{bean.phone,bean.name});
		//关闭数据库对象
		db.close();

	}
	public void query(String name){
		
		//执行sql语句需要sqliteDatabase对象
		//调用getReadableDatabase方法,来初始化数据库的创建
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		//sql:sql语句，  selectionArgs:查询条件占位符的值,返回一个cursor对象
		Cursor cursor = db.rawQuery("select _id, name,phone from info where name = ?", new String []{name});
		//解析Cursor中的数据    这里即使表里没数据.cursor也不为null
		if(cursor.moveToFirst()){//判断cursor中是否存在数据
			
			//循环遍历结果集，获取每一行的内容
			while(cursor.moveToNext()){//条件，游标能否定位到下一行
				//获取数据
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String name_str = cursor.getString(cursor.getColumnIndex("name"));
				String phone = cursor.getString(cursor.getColumnIndex("phone"));
				System.out.println("_id:"+id+";name:"+name_str+";phone:"+phone);
			}
			cursor.close();//关闭结果集
			
		}
		//关闭数据库对象
		db.close();

	}
	
# 3 Android下另外一种增删改查方式 
	
1. 创建一个帮助类的对象，调用getReadableDatabase方法，返回一个SqliteDatebase对象

2. 使用SqliteDatebase对象调用insert,update,delete ,query方法做增删改查。

**特点:增删改有了返回值，可以判断sql语句是否执行成功，但是查询不够灵活，不能做多表查询。所以在公司一般人增删改喜欢用第二种方式，查询用第一种方式。**

			private MySqliteOpenHelper mySqliteOpenHelper;
	public InfoDao(Context context){
		//创建一个帮助类对象
		mySqliteOpenHelper = new MySqliteOpenHelper(context);
	}

	public boolean add(InfoBean bean){

		//执行sql语句需要sqliteDatabase对象
		//调用getReadableDatabase方法,来初始化数据库的创建
		SQLiteDatabase 	db = mySqliteOpenHelper.getReadableDatabase();
		
		
		ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
		values.put("name", bean.name);
		values.put("phone", bean.phone);
		
		//table: 表名 , nullColumnHack：可以为空，标示添加一个空行, values:数据一行的值 , 返回值：代表添加这个新行的Id ，-1代表添加失败
		long result = db.insert("info", null, values);//底层是在拼装sql语句
	
		//关闭数据库对象
		db.close();
		
		if(result != -1){//-1代表添加失败
			return true;
		}else{
			return false;
		}
	}

	public int del(String name){

		//执行sql语句需要sqliteDatabase对象
		//调用getReadableDatabase方法,来初始化数据库的创建
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		
		//table ：表名, whereClause: 删除条件, whereArgs：条件的占位符的参数 ; 返回值：成功删除多少行
		int result = db.delete("info", "name = ?", new String[]{name});
		//关闭数据库对象
		db.close();
		
		return result;

	}
	public int update(InfoBean bean){

		//执行sql语句需要sqliteDatabase对象
		//调用getReadableDatabase方法,来初始化数据库的创建
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		ContentValues values = new ContentValues();//是用map封装的对象，用来存放值
		values.put("phone", bean.phone);
		//table:表名, values：更新的值, whereClause:更新的条件, whereArgs：更新条件的占位符的值,返回值：成功修改多少行
		int result = db.update("info", values, "name = ?", new String[]{bean.name});
		//关闭数据库对象
		db.close();
		return result;

	}
	public void query(String name){
	
		//执行sql语句需要sqliteDatabase对象
		//调用getReadableDatabase方法,来初始化数据库的创建
		SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
		
		//table:表名, columns：查询的列名,如果null代表查询所有列； selection:查询条件, selectionArgs：条件占位符的参数值,
		//groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
		Cursor cursor = db.query("info", new String[]{"_id","name","phone"}, "name = ?", new String[]{name}, null, null, "_id desc");
		//解析Cursor中的数据
		if(cursor.moveToFirst()){//判断cursor中是否存在数据
			
			//循环遍历结果集，获取每一行的内容
			while(cursor.moveToNext()){//条件，游标能否定位到下一行
				//获取数据
				String id = cursor.getString(cursor.getColumnIndex("_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
				System.out.println("_id:"+id+";name:"+name_str+";phone:"+phone);
				
				
			}
			cursor.close();//关闭结果集
			
		}
		//关闭数据库对象
		db.close();

	}

		

# 4. 数据库的事务 
> 事务： 执行多条sql语句，要么同时执行成功，要么同时执行失败，不能有的成功，有的失败

银行转账


	//点击按钮执行该方法
	public void transtation(View v){
		//1.创建一个帮助类的对象
		BankOpenHelper bankOpenHelper = new BankOpenHelper(this);
		//2.调用数据库帮助类对象的getReadableDatabase创建数据库，初始化表数据，获取一个SqliteDatabase对象去做转账（sql语句）
		SQLiteDatabase db = bankOpenHelper.getReadableDatabase();
		//3.转账,将李四的钱减200，张三加200
		db.beginTransaction();//开启一个数据库事务
		try {
			db.execSQL("update account set money= money-200 where name=?",new String[]{"李四"});
			int i = 100/0;//模拟一个异常
			db.execSQL("update account set money= money+200 where name=?",new String[]{"张三"});

			db.setTransactionSuccessful();//标记事务中的sql语句全部成功执行
		} finally {
			db.endTransaction();//判断事务的标记是否成功，如果不成功，回滚错误之前执行的sql语句 
		}
	}

# 5. 使用LitePal操作数据库

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

# 常见错误

bug:数据库语句报错

 	db.execSQL("create table " + CACHE_TABLE_NAME + "(" +
                    CACHE_TABLE_ID + " integer primary key autoincrement," +
                    CACHE_TABLE_TYPE + " integer not null," +
                    CACHE_TABLE_RESPONSE + " text," +
                    CACHE_TABLE_DATE + " text" +
                    ");");

解决:autoincrement关键字必须写在primary key后面才行