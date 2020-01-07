
1. 加载圆角
```
Glide.with(context).load(imageUrl).apply(RequestOptions.bitmapTransform(new RoundedCorners(20))).into(icon);
```

2. 图片是gone的情况下监听不会被回调
3. 