
2种序列化方式

### 1. Serializable

- Serializable是一个接口
- serialVersionUID,在序列化和反序列化时,必须一致,不然会导致crash,序列化失败.
- 可以重写系统默认的序列化和反序列化过程,重写writeObject()和readObject()方法


### 2. Parcelable

实现方式

```java
public class User implements Parcelable {

    public int userId;
    public String userName;
    public String userSecondName;
    public boolean isMale;

    protected User(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
        userSecondName = in.readString();
        isMale = in.readByte() != 0;
    }

    //反序列化  交给我
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    //序列化 交给我
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeString(userSecondName);
        dest.writeByte((byte) (isMale ? 1 : 0));
    }
}
```

- Parcel内部包装了可序列化的数据,可在Binder中自由传输
- Intent,Bundle,Bitmap等都实现了Parcelable,是可以直接序列化的.List和Map其实也可以序列化,前提是他们的每个元素都是可序列化的. View是没有实现Parcelable的.

### 3. 如何进行对象序列化

```java
ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("cache.txt"));
objectOutputStream.writeObject(user);
objectOutputStream.close();

ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("cache.txt"));
User newUser = (User) objectInputStream.readObject();
System.out.println(newUser.userName);
```


### 什么时候用哪个

- Serializable是Java的,Parcelable是Android上的,Parcelable效率更高
- 将Parcelable对象序列化到存储设备中或者将对象序列化后通过网络传输 可以是可以,但是过程稍显复杂,这2种情况建议使用Serializable