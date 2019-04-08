> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/509c78602ed2

1、Java 读写文件的 IO 流分两大类，字节流和字符流，基类分别是字符：Reader 和 Writer；字节：InputStream 和 OutPutStream，如下图：

![](https://upload-images.jianshu.io/upload_images/3403199-e7d6ffadb302200e.png)

2、 BufferedInputStream 是带缓冲区的输入流，默认缓冲区大小是 8M，能够减少访问磁盘的次数，提高文件读取性能；BufferedOutputStream 是带缓冲区的输出流，能够提高文件的写入效率。BufferedInputStream 与 BufferedOutputStream 分别是 FilterInputStream 类和 FilterOutputStream 类的子类，实现了装饰设计模式。

3、流的程序一般分以下四步：
（1）创建文件对象

```
File file = new File("xxx.txt");

```

（2）用流装载文件，如果用字符流的话，则是：

```
FileReader fileReader = new FileReader(file);

```

（3）如果用缓冲区，则用缓冲区装载流，用缓冲区是为了提高读写性能

```
BufferedReader bufferedReader = new BufferedReader(fileReader);

```

（4）开始读写操作

```
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
           stringBuffer.append(str);
        } 

```

如果遇到字节流要转换成字符流，则在缓冲区前加一步

```
 InputStreamReader inputStreamReader = new InputStreamReader(inputStream)

```

或者需要编码转换的，则在缓冲区前加一步

```
InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

```

4、字节流文件操作（读写）的代码 Demo 如下：

```
    public static void readFileByByte(String filePath) {

        File file = new File(filePath);

        InputStream inputStream = null; 
        OutputStream outputStream = null;  

        try {

            inputStream = new FileInputStream(file); 
            outputStream = new FileOutputStream("d:/work/readFileByByte.txt");

            int temp;
            while ((temp = inputStream.read()) != -1) {
                outputStream.write(temp);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (inputStream != null && outputStream != null) {
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
        }
    }

```

5、字符流文件操作（读写）的代码 Demo 如下：

```
    public static void readFileByCharacter(String filePath) {

        File file = new File(filePath);

        FileReader reader = null;
        FileWriter writer = null;
        try {
            reader = new FileReader(file);
            writer = new FileWriter("d:/work/readFileByCharacter.txt");

            int temp;
            while ((temp = reader.read()) != -1) {
                writer.write((char)temp);
            }
        } catch (IOException e) {
            e.getStackTrace();
        } finally {
            if (reader != null && writer != null) {
                try {
                    reader.close();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

```

6、还有一种方法，按行（读写）。Demo 如下

```
    public static void readFileByLine(String filePath) {

        File file = new File(filePath);

        BufferedReader bufReader = null;
        BufferedWriter bufWriter = null;
        try {

            bufReader = new BufferedReader(new FileReader(file));
            bufWriter = new BufferedWriter(new FileWriter("d:/work/readFileByLine.txt"));

            String temp = null;
            while ((temp = bufReader.readLine()) != null) {
                bufWriter.write(temp+"\n");
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (bufReader != null && bufWriter != null) {
                try {
                    bufReader.close();
                    bufWriter.close();
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
        }
    }

```

7、需要将字节流要转换成字符流时，Demo 如下：

```
    private static String getOuterIp() throws IOException {  
        InputStream inputStream = null;  
        BufferedReader bufferedReader = null;
        try {  
            URL url = new URL("http://1212.ip138.com/ic.asp");  
            URLConnection urlconnnection = url.openConnection();  
            inputStream = urlconnnection.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GB2312");  //字节流转字符流，并且设置编码格式
            bufferedReader = new BufferedReader(inputStreamReader);  
            StringBuffer webContent = new StringBuffer();  
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                webContent.append(str);  
            }  
            int ipStart = webContent.indexOf("[") + 1;  
            int ipEnd = webContent.indexOf("]");  
            return webContent.substring(ipStart, ipEnd);  
        } finally {  
            if (inputStream != null && bufferedReader != null) {  
                inputStream.close();  
                bufferedReader.close();
            }  
        }  
    }  

```