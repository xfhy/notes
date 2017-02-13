# Java JDBC #
<font size="5"><b>
1. 为什么需要JDBC:JDBC是Java数据库连接技术(Java Database Connection)的简称，提供连接各种常用数据库的能力
<br/>
2. JDBC API:与数据库建立连接、执行SQL 语句、处理结果<br/>
-----------------------------------------------------------
- DriverManager ：依据数据库的不同，管理JDBC驱动<br/>
- DataSource：推荐在容器中替代DriverManager，可以实现连接池化<br/>
- Connection ：负责连接数据库并担任传送数据的任务  <br/>
- Statement ：由 Connection 产生、负责执行SQL语句<br/>
- ResultSet：负责保存Statement执行后所产生的查询结果<br/>

-----------------------------------------------------------
3. JDBC工作模板:<br/>
-----------------------------------------------------------
	try {
			Class.forName(JDBC驱动类);   //1.加载JDBC驱动
		} 
		........
		try{
			//2.与数据库建立连接
			Connection con = DriverManager.getConnection(url,数据库用户名,密码);
			Statement stmt = con.createStatement();
			//3.发送sql语句,并得到返回结果
			ResultSet rs = stmt.executeQuery("select a,b,c from table1");
			
			//4.处理返回结果
			while(rs.next()){
				int x = rs.getInt("a");
				String s = rs.getString("b");
				float f = rs.getFloat("c");
			}
			
			rs.close();   //   5.释放资源    记得判断非空
			stmt.close();
			con.close();
		}

-----------------------------------------------------------

4. Apache和PhpMyAdmin,Apache是运行Php页面的容器（服务器）,PhpMyAdmin是使用Php技术制作的网页版本的MySql管理器。由于MySql与Apache都可以无安装运行，因此有人制作了绿色版的MySql便携版本。最著名的套装称为XAMPP.
5. 更改MySql的密码,MySql中有一个mysql数据库用于存储用户名和密码等MySql配置信息。
其中表user表示所有可以操作MySql的用户表。
通过修改user表中的记录可以修改用户名和密码。
MySql默认管理员用户为root，密码通过md5加密存储。
update user set password=PASSWORD(“root”) where user=“root”;
也可以通过PhpMyAdmin修改,修改MySql密码后，PhpMyAdmin也无法正常连接MySql。
需要修改文件config.inc.php中$cfg[‘Servers’][$i][‘password’] = ‘root’;字段.<br/>
6. JDBC驱动模板:

		Connection conn = null;
		try {
			//这个类（一般在jar包中），必须在程序搜索路径中
			Class.forName(" com.mysql.jdbc.Driver ");  //加载驱动
			… …
			conn = DriverManager.getConnection(   //建立连接
			" jdbc:mysql://localhost:3306/mysql", “root", “root");
			 … …
		} catch (ClassNotFoundException e) {   //必须进行相关异常处理
			……
		} catch (SQLException e) {
			 … …
		} finally {
			try {
				conn.close();   //关闭连接
			} catch (SQLException e) {
				 … …
			}
		}
<br/>
7. 常见驱动类：<br/>
Oracle：oracle.jdbc.driver.OracleDriver<br/>
MS SqlServer：com.microsoft.jdbc.sqlserver.SQLServerDriver<br/>
MySql：com.mysql.jdbc.Driver<br/>
8. 建立和关闭连接:

		Connection conn = null;
		try {
			String url = “连接字符串”;  //每一种数据库的连接字符串标准有所不同
			conn = DriverManager.getConnection(url);
		} catch(SQLException e) {		//必须捕获异常
			//处理异常
		} finally {  //关闭数据库连接的过程一定在finally模块中完成
			if (conn != null) {
				try {
				conn.close();
				} catch (SQLException e) {
					//处理异常
				}
			}
		}
警告:数据库连接是紧缺资源，使用完成之后一定要确保合法关闭。
<br/>
9. 常见连接字符串:
Oracle
jdbc:oracle:thin:@localhost:1521:pets
MS Sql Server
jdbc:sqlserver://localhost:1433; DatabaseName=pets
MySql
jdbc:mysql://localhost:3306/pets
总结：JDBC连接字符串四要素：
协议（在jdbc:之后）
地址(url)
端口
数据库
<br/>
10. <font color="red">警告+经验:
为了避免程序和数据库连接中出现乱码，必须：
创建数据库和表时，编码一定选择utf8_bin
eclipse中程序编码一定为utf-8
eclipse控制台必须为utf-8
MySql连接字符串后加?useUnicode=true&characterEncoding=utf-8</font>
<br/>
11. 使用java操作数据库,更新、添加、删除操作步骤完全相同，只是sql语句不同.一般插入的时候使用execute()方法，更新或者删除的时候使用executeUpdate()方法
<br/>
12. PreparedStatement比Statement安全<br/>
</b></font>