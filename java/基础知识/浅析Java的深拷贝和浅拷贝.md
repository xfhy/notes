### 浅析Java的深拷贝和浅拷贝

首先来看看浅拷贝和深拷贝的定义：

浅拷贝：使用一个已知实例对新创建实例的成员变量逐个赋值，这个方式被称为浅拷贝。

深拷贝：当一个类的拷贝构造方法，不仅要复制对象的所有非引用成员变量值，还要为引用类型的成员变量创建新的实例，并且初始化为形式参数实例值。这个方式称为深拷贝

#### 1. 浅拷贝

写一个普通类Person,让其实现Cloneable接口.

> 补充:Cloneable是一个空的接口,里面没有定义方法.只是一个标识作用.一个类要实现clone()方法,需要实现Cloneable接口,然后外部调用clone()方法时才不会抛异常.  因为Object的clone()方法判断了是否实现了Cloneable接口.
> 
>    ```
>   protected Object clone() throws CloneNotSupportedException {
>             if (!(this instanceof Cloneable)) {
>                 throw new CloneNotSupportedException("Class " + getClass().getName() +
>                                                      " doesn't implement Cloneable");
>             }
>             //这是native方法
>             return internalClone();
>        }
>    ```

```
static class Person implements Cloneable {
    public String a = "a";
    public String b = "b";
    public int c = 1;
    public Student mStudent = new Student();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class Student implements Cloneable{
    }
```

我们来看一下以下代码:
```
Person person = new Person();

Person person1 = person;
System.out.println("person : "+person);  //A$Person@4517d9a3
System.out.println("person1 : "+person1);//A$Person@4517d9a3
```
输出是
```
A$Person@4517d9a3
A$Person@4517d9a3
```
地址是相同的,这个是肯定的,只是复制了引用,地址肯定相同.

```java
Person person = new Person();

try {
    Person clone = (Person) person.clone();
    System.out.println(person == clone);   //false
    System.out.println(person);            //A$Person@4517d9a3
    System.out.println(clone);             //A$Person@372f7a8d   看到没,clone出来的地址不一样
    System.out.println(person.mStudent);   //A$Student@2f92e0f4
    System.out.println(clone.mStudent);    //A$Student@2f92e0f4   内部对象,clone之后其实是一样的
} catch (CloneNotSupportedException e) {
    e.printStackTrace();
}
```

其实clone()方法克隆出来的对象,地址是不一样的. 但是里面的属性如果是对象的话,那么不会去深度复制.

由上可知，从Object中继承过来的clone默认实现的是浅拷贝。


#### 2. 深拷贝

> 深拷贝会拷贝所有的属性,不是简单的拷贝对象属性的引用地址,还会拷贝分配的内存.但是深拷贝相比与浅拷贝速度要慢一些,而且花销较大.

现在我们将上面的Person简单修改一下clone()方法
```
@Override
protected Object clone() throws CloneNotSupportedException {
    Person person = (Person) super.clone();
    person.mStudent = (Student) person.mStudent.clone();
    return person;
}
```

再来执行一下如下代码:
```
try {
    Person clone = (Person) person.clone();
    System.out.println(person == clone);   //false
    System.out.println(person);            //A$Person@4517d9a3
    System.out.println(clone);             //A$Person@372f7a8d   看到没,clone出来的地址不一样
    System.out.println(person.mStudent);   //A$Student@2f92e0f4
    System.out.println(clone.mStudent);    //A$Student@28a418fc   现在不一样了
} catch (CloneNotSupportedException e) {
    e.printStackTrace();
}
```

我们在Person的clone()中不仅复制了自己,还复制了Student.达到了深拷贝.在下面的测试代码中,我们发现,结果其实是符合预期的,属性student的地址是不一样的,说明实现了拷贝.


#### 3. 总结

**慎用 Object 的 clone 方法来拷贝对象。** 

对象的 clone 方法默认是浅拷贝，若想实现深拷贝需要重写 clone 方法实现属性对象的拷贝。