##WAMP初识,JAVA JDBC连接WAMP的MYSQL数据库##


<font size=4>**介绍:**</font>
Windows下的Apache+Mysql/MariaDB+Perl/PHP/Python，一组常用来搭建动态网站或者服务器的开源软件，本身都是各自独立的程序，但是因为常被放在一起使用，拥有了越来越高的兼容度，共同组成了一个强大的Web应用程序平台。
</br>
</br>
首先本博客是我自己想用java连接数据库进行操作,然后我又不想用SQL(太大了,10个G啊..),所以想用一下wamp,里面有集成的一个小型数据库.是mysql的.

<font size=4>**使用步骤:**</font>
1. <font size=4>百度下载一个wamp,安装到Windows上,全部点击下一步,即可安装完成.</font>
2. <font size=4>然后,打开wamp软件,打开浏览器输入localhost,如果出现下图所示</font> ![](http://i.imgur.com/tEM6nyO.png),则表示成功安装.
3. 点击左下角的phpmyadmin,将服务器排序规则设置为utf8_bin.现在可以建立数据库了,![](http://i.imgur.com/8QaQOOh.png),点击如图所示的New,新建一个数据库.
4. 建立好数据库后.用java连接数据库,具体的连接值是   jdbc.drivers=com.mysql.jdbc.Driver
;   jdbc.url=jdbc:mysql://localhost:3306/DormMan?characterEncoding=utf-8
;   jdbc.username=root
;   jdbc.password=
JAVA的代码如下

    Class.forName(drivers);   //加载驱动		
    //试图建立到给定数据库 URL 的连接
    conn = DriverManager.getConnection(url, username, password);