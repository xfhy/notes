

![](http://olg7c0d2n.bkt.clouddn.com/18-10-8/61389259.jpg)

tinker将old.apk和new.apk做了diff，拿到patch.dex，然后将patch.dex与本机中apk的classes.dex做了合并，生成新的classes.dex，运行时通过反射将合并后的dex文件放置在加载的dexElements数组的前面。

运行时替代的原理，其实和Qzone的方案差不多，都是去反射修改dexElements。

两者的差异是：Qzone是直接将patch.dex插到数组的前面；而tinker是将patch.dex与app中的classes.dex合并后的全量dex插在数组的前面。

tinker这么做的目的还是因为Qzone方案中提到的CLASS_ISPREVERIFIED的解决方案存在问题；而tinker相当于换个思路解决了该问题。
