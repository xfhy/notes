![image](D4F1D9F1EC2E4E7E8F54F0ED49BBDBF6)

ContextWrapper里面的大部分方法被标记为hide

调用这些方法在Android P上会弹窗

```java
/**
* @hide
*/
@Override
public Handler getMainThreadHandler() {
    return mBase.getMainThreadHandler();
}
```