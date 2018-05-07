
> 有时候APP不抛出错误，但是会抛出ANR。

一般我们的APP出现ANR后会将ANR信息保存到`/data/anr/traces.txt`里面。

在系统里面配置环境变量：adb，然后打开命令行，输入

```
方式1：
cd data/   进入data目录
cd anr/    进入anr目录
ls 可以看到之前产生的traces.txt文件
cat traces.txt  查看文件内容，当然也可以push到电脑上慢慢看


方式2：
adb pull /data/anr .  直接将anr下面的全部文件push到电脑上，如果是windows的话在C:\Users\用户名\anr下面
```