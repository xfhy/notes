![](https://mmbiz.qpic.cn/mmbiz_png/v1LbPPWiaSt6VSnfwpCc1egmy69vHN2BV9nNIkvN2nUGupAfSfTBriazydoGcibAspHXhAVq77rfia22OpvnhibpVqw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

![image](7B2DF148DA844C798DA56CC20B121255)



在上面的过程中，你可以看到，为什么用 @Bind 、 @OnClick 等注解标注的属性、方法必须是 public 或 protected？

因为ButterKnife 是通过 被代理类引用.this.editText 来注入View的。为什么要这样呢？

答案就是：性能 。如果你把 View 和方法设置成 private，那么框架必须通过反射来注入。

想深入到源码细节了解 ButterKnife 更多？

how-butterknife-actually-works

https://medium.com/@lgvalle/how-butterknife-actually-works-85be0afbc5ab

ButterKnife源码分析

https://www.jianshu.com/p/1c449c1b0fa2

拆 JakeWharton 系列之 ButterKnife

https://juejin.im/post/58f388d1da2f60005d369a09

![image](25C87C5C292E4AFEA76EC268E9CB7B94)



