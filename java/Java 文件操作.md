# Java 文件操作 #
<font size="5"><b>
1. File类的常用方法<br/>
![](http://i.imgur.com/lzffDDv.png)<br/>
2. java流的分类<br/>
 ![](http://i.imgur.com/XAtvjla.png)<br/>
3. 文件的读写:<br/>
文本文件的读写:
  用FileInputStream和FileOutputStream读写文本文件<br/>
  用BufferReader 和BufferWriter读写文本文件<br/>
二进制的读写:<br/>
 使用DataInputStream和DataOutputStream读写二进制文件
4. 使用字节流读文本文件:<br/>
------------------------------------------------------
- 引入相关的类

		import java.io.IOException;
		import java.io.FileInputStream;
- 构造文件输入流FileInputStream 对象

		FileInputStream fis= new FileInputStream(“c:\\test.txt");

- 读取文本文件的数据

		fis.available();  //返回的实际可读字节数，也就是总大小
		fis.read();     
- 关闭文件流对象
	
		fis.close();
<font size="4" color="red">
警告:关闭文件操作一定要在finally中，否则很可能造成文件损坏，或者访问冲突。
</font>
------------------------------------------------------
5. 使用字节流写文本文件:<br/>
------------------------------------------------------
- 引入相关的类

		import java.io.IOException;
		import java.io.FileOutputStream;
- 构造文件输出流FileOutputStream 对象

		FileOutputStream fos = new FileOutputStream("c:\\test.txt");

- 把数据写入文本文件

		String str ="好好学习Java";
		byte[] words  = str.getBytes();
		fos.write(words, 0, words.length); 
 
- 关闭文件流对象
		fis.close();

------------------------------------------------------
6. BufferedReader类:提高字符流读取文本文件的效率<br/>
使用FileReader类与BufferedReader类<br/>
BufferedReader类是Reader类的子类<br/>
BufferedReader类带有缓冲区<br/>
按行读取内容的readLine()方法(BufferedReader类特有的方法)<br/>
7. 使用 BufferedReader 读文本文件:<br/>
------------------------------------------------------
- 引入相关的类

		import java.io.FileReader;
		import java.io.BufferedReader;
		import java.io.IOException
- 构造BufferedReader 对象和FileReader 对象

		Reader fr=new 
               FileReader("C:\\myTest.txt "); 
		BufferedReader br=new
               BufferedReader(fr); 


- 调用readLine ()方法读取数据

		br.readLine();
 
- 关闭文件流对象

		br.close();
		fr.close();

------------------------------------------------------
8. Properties文件,键值对:用户的配置,用户密码保存等.eg:default_name=root<br/>
读取时一定要trim(),必须判断是否有2个值,判断是否加了中文空格等.<br/>
9. 关闭文件流时:1.放在finally中 2.判断是否为null 3.单独try...catch 4.后打开的先关闭<br/>
10. 当文件需要配置编码时,需要BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file) , "UTF-8"));固定一下编码<br/>
</b><font>