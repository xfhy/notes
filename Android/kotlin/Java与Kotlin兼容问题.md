
### 1. Java调用Kotlin文件方法或者属性

BasicDataType.kt最终会编译成静态的属性或者方法

```
# BasicDataType.kt
val str = "1"
val age = 23

# Test.java
//需要使用类名+kt的方法,访问 有点像静态的
System.out.println(BasicDataTypeKt.getAge());
```

### 2. object

```
object ObjectTest {
    //这里可以加入一个@JvmStatic注解,然后在Java中就可以直接类名.这个方法名 进行调用
    fun sayMessage(msg: String) {
        println(msg)
    }
}

java中调用
ObjectTest.INSTANCE.sayMessage("");

```

### 3. class

```
Test::class.java
```

### 4. val不能为null

当java返回的数据赋值给kotlin变量时,不确定是否为null,这时使用可空类型