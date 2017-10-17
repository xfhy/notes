# Linux 程序设计入门 #
<font size="5"><b>
1. 简单的GCC语法：

- gcc –c test.c，表示只编译test.c文件，成功时输出目标文件test.o<br/>
- gcc –o test test.o，将test.o连接成可执行的二进制文件test<br/>
- gcc –o test test.c，将test.c编译并连接成可执行的二进制文件test<br/>
- -o选项表示我们要求输出的可执行文件名。 <br/>
- -c选项表示我们只要求编译器输出目标代码，而不必要输出可执行文件。 <br/>
- -g选项表示我们要求编译器在编译的时候提供我们以后对程序进行调试的信息。<br/>
- $@--目标文件，$^--所有的依赖文件，$<--第一个依赖文件.详细请查看别个大神的博客:[http://blog.csdn.net/kesaihao862/article/details/7332528](http://blog.csdn.net/kesaihao862/article/details/7332528 "引用:makefile 中 $@ $^ %< 使用")<br/>

----------

2. makefile:在makefile中写入如下语句:
<pre><code>
main:
	gcc -o hello hello.c
clean:
	rm -f hello hello.o
</code></pre>
<br/>
在上面的代码块中,第一行的那个是默认的,比如你执行命令时输入make,则会默认执行main下面的语句.
在定义好依赖关系后，后续的那一行定义了如何生成目标文件的操作系统命令，一定要以一个Tab键作为开头。当我们输入命令 make main时,会执行编译并链接hello.c文件.当输入make clean时,会执行命令把hello 和 hello.c文件删除.是不是特别方便.<br/>
3. Linux压缩命令:<br/>
gzip -cr test > 1.zip 将test文件夹压缩到1.zip中<br/>
gunzip -r 1.zip > 3.txt 将1.zip解压到3.txt中<br/>
tar -czvf my.tar.gz test 将test文件夹压缩到my.tar.gz中<br/>
tar –zxvf my.tar.gz 将my.tar.gz解压<br/>

-c ：建立一个压缩文件的参数指令(create 的意思)<br/>
-z ：是否同时具有 gzip 的属性？亦即是否需要用 gzip 压缩？<br/>
-v ：压缩的过程中显示文件！这个常用，但不建议用在背景执行过程！<br/>
-f ：强制转换<br/>
4. 下面再插入一段makefile,继续分析
<pre><code>
	# 变量的声明(有点像C语言里面的宏定义)
objects = main.o print.o

	# helloworld:main.o print.o 
helloworld:$(objects)
	# helloworld就是我们要生成的目标
	# main.o print.o是生成此目标的先决条件
	gcc -o helloworld $(objects)
	# shell命令,最前面的一定是tab键
$(objects) : print.h # 都依赖print.h

main.o:main.c print.h
	gcc -c main.c
	
print.o:print.c print.h
	gcc -c print.c

clean:
	rm helloworld $(objects)
</code></pre>
---------------------------------------------------------------------
上面其实是3个文件,如下
<pre><code>
1. print.h
　　　　　　#include<stdio.h>
　　　　　　void printhello();

2. print.c
　　　　　　#include"print.h"
　　　　　　void printhello(){
　　　　　　　　printf("Hello, world\n");
　　　　　　}

3. main.c
　　　　　　#include "print.h"
　　　　　　int main(void){
　　　　　　　　printhello();
　　　　　　　　return 0;
　　　　　　}
</code></pre>
-----------------------------------------------------------------
解释一下,首先objects就像C语言里面的宏定义,在下面的任何地方都代表main.o print.o;
在helloworld那个语句之前就会执行main.o和print.o,进行编译.
<br/>
</b></font>