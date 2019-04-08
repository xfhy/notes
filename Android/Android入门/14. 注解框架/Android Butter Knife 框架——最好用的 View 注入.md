> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/9ad21e548b69

最近在看 GitHub 上的一些代码时，发现很多工程都用到了 Butter Knife 这个框架，能节省很多代码量。像`findViewById`这种代码就不用再出现了，而且这个框架也提供了很多其他有用的注解。
抱着学习的心态看了官网上的文档，挺简单，也很实用，决定以后就用这个库了。
下面是我翻译的官方文档，诸位看官轻喷。官方文档也挺简单，英语好的不好的，都建议去看看原文。

另外注意，这个库的版本更新挺快的，我第一次用到的时候是 7.1.0，而现在的最新版本已经是 8.5.1 了，也就是说大家可能需要去 [ButterKnife 的 Github](https://link.jianshu.com?t=https://github.com/JakeWharton/butterknife) 查看最近的版本。

![](https://upload-images.jianshu.io/upload_images/677256-8e156a827fa7e53e.png)

# Butter Knife

本文章翻译自：[http://jakewharton.github.io/butterknife/](https://link.jianshu.com?t=http://jakewharton.github.io/butterknife/)

Butter Knife，专门为 Android View 设计的绑定注解，专业解决各种`findViewById`。

## 简介

对一个成员变量使用`@BindView`注解，并传入一个 View ID， ButterKnife 就能够帮你找到对应的 View，并自动的进行转换（将 View 转换为特定的子类）：

```
class ExampleActivity extends Activity {
    @BindView(R.id.title)  TextView title;
    @BindView(R.id.subtitle) TextView subtitle;
    @BindView(R.id.footer) TextView footer;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);
        ButterKnife.bind(this);
        // TODO Use fields...
    }
}

```

与缓慢的反射相比，Butter Knife 使用再编译时生成的代码来执行 View 的查找，因此不必担心注解的性能问题。调用`bind`来生成这些代码，你可以查看或调试这些代码。

例如上面的例子，生成的代码大致如下所示：

```
public void bind(ExampleActivity activity) {
    activity.subtitle = (android.widget.TextView) activity.findViewById(2130968578);
    activity.footer = (android.widget.TextView) activity.findViewById(2130968579);
    activity.title = (android.widget.TextView) activity.findViewById(2130968577);
}

```

### 资源绑定

绑定资源到类成员上可以使用`@BindBool`、`@BindColor`、`@BindDimen`、`@BindDrawable`、`@BindInt`、`@BindString`。使用时对应的注解需要传入对应的 id 资源，例如`@BindString`你需要传入`R.string.id_string`的字符串的资源 id。

```
class ExampleActivity extends Activity {
  @BindString(R.string.title) String title;
  @BindDrawable(R.drawable.graphic) Drawable graphic;
  @BindColor(R.color.red) int red; // int or ColorStateList field
  @BindDimen(R.dimen.spacer) Float spacer; // int (for pixel size) or float (for exact value) field
  // ...
}

```

### 在非 Activity 中使用绑定

Butter Knife 提供了 bind 的几个重载，只要传入跟布局，便可以在任何对象中使用注解绑定。

例如在 Fragment 中：

```
public class FancyFragment extends Fragment {
    @BindView(R.id.button1) Button button1;
    @BindView(R.id.button2) Button button2;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fancy_fragment, container, false);
        ButterKnife.bind(this, view);
        // TODO Use fields...
        return view;
    }
}

```

还有一种比较常见的场景，就是在 ListView 的 Adapter 中，我们常常会使用 ViewHolder：

```
public class MyAdapter extends BaseAdapter {
    @Override public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.whatever, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.name.setText("John Doe");
        // etc...

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.title)
        TextView name;
        @BindView(R.id.job_title) TextView jobTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

```

你能在提供给的例子中找到上述实现。

`ButterKnife.bind`的调用可以被放在任何你想调用`findViewById`的地方。

提供的其他绑定 API:

*   使用 Activity 作为跟布局在任意对象中进行绑定。如果你使用了类似 MVC 的编程模式，你可以对 controller 使用它的 Activity 用`ButterKnife.bind(this, activity)`进行绑定。

*   使用`ButterKnife.bind(this)`绑定一个布局的子布局。如果你在布局中使用了`<merge>`标签并且在自定义的控件构造时 inflate 这个布局，你可以在 inflate 之后立即调用它。或者，你可以在`onFinishInflate()`回调中使用它。

### View 列表

你可以一次性将多个 views 绑定到一个`List`或数组中：

```
@BindViews({ R.id.first_name, R.id.middle_name, R.id.last_name })
List<EditText> nameViews;

```

`apply`函数，该函数一次性在列表中的所有 View 上执行一个动作：

```
ButterKnife.apply(nameViews, DISABLE);
ButterKnife.apply(nameViews, ENABLED, false);

```

`Action`和`Setter`接口能够让你指定一些简单的动作：

```
static final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
    @Override public void apply(View view, int index) {
        view.setEnabled(false);
    }
};
static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
    @Override public void set(View view, Boolean value, int index) {
        view.setEnabled(value);
    }
};

```

Android 中的`Property`属性也可以使用`apply`方法进行设置：

```
ButterKnife.apply(nameViews, View.ALPHA, 0.0f);

```

### 监听器绑定

使用本框架，监听器能够自动的绑定到特定的执行方法上：

```
@OnClick(R.id.submit)
public void submit(View view) {
  // TODO submit data to server...
}

```

而监听器方法的参数都时可选的：

```
@OnClick(R.id.submit)
public void submit() {
    // TODO submit data to server...
}

```

指定一个特定的类型，Butter Knife 也能识别：

```
@OnClick(R.id.submit)
public void sayHi(Button button) {
    button.setText("Hello!");
}

```

可以指定多个 View ID 到一个方法上，这样，这个方法就成为了这些 View 的共同事件处理。

```
@OnClick({ R.id.door1, R.id.door2, R.id.door3 })
public void pickDoor(DoorView door) {
    if (door.hasPrizeBehind()) {
        Toast.makeText(this, "You win!", LENGTH_SHORT).show();
    } else {
        Toast.makeText(this, "Try again", LENGTH_SHORT).show();
    }
}

```

自定义 View 时，绑定事件监听不需要指定 ID

```
public class FancyButton extends Button {
    @OnClick
    public void onClick() {
        // TODO do something!
    }
}

```

### 重置绑定:

Fragment 的生命周期与 Activity 不同。在 Fragment 中，如果你在`onCreateView`中使用绑定，那么你需要在`onDestroyView`中设置所有 view 为`null`。为此，ButterKnife 返回一个`Unbinder`实例以便于你进行这项处理。在合适的生命周期回调中调用`unbind`函数就可完成重置。

```
public class FancyFragment extends Fragment {
    @BindView(R.id.button1) Button button1;
    @BindView(R.id.button2) Button button2;
    private Unbinder unbinder;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fancy_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        // TODO Use fields...
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

```

### 可选绑定：

在默认情况下， `@bind`和监听器的绑定都是必须的，如果目标 view 没有找到的话，Butter Knife 将会抛出个异常。

如果你并不想使用这样的默认行为而是想创建一个可选的绑定，那么你只需要在变量上使用`@Nullable`注解或在函数上使用`@Option`注解。

注意：任何名为`@Nullable`的注解都可以使用在变量上。但还时强烈建议使用 Android 注解库中的`@Nullable`。使用这个库对你的代码有很多好处，关于该库的详情，可以点击此处：[Android Tools Project](https://link.jianshu.com?t=http://tools.android.com/tech-docs/support-annotations)

```
@Nullable @BindView(R.id.might_not_be_there) TextView mightNotBeThere;

@Optional @OnClick(R.id.maybe_missing) void onMaybeMissingClicked() {
    // TODO ...
}

```

### 对于包含多个方法的监听

当一个监听器包含多个回调函数时，使用函数的注解能够对其中任何一个函数进行绑定。每一个注解都会绑定到一个默认的回调。你也可以使用`callback`参数来指定一个其他函数作为回调。

```
@OnItemSelected(R.id.list_view)
void onItemSelected(int position) {
    // TODO ...
}

@OnItemSelected(value = R.id.maybe_missing, callback = NOTHING_SELECTED)
void onNothingSelected() {
    // TODO ...
}

```

### 福利

Butter Knife 提供了一个`findViewById`的简化代码：`findById`，用这个方法可以在`View`、`Activity`和`Dialog`中找到想要 View，而且，该方法使用的泛型来对返回值进行转换，也就是说，你可以省去`findViewById`前面的强制转换了。

```
View view = LayoutInflater.from(context).inflate(R.layout.thing, null);
TextView firstName = ButterKnife.findById(view, R.id.first_name);
TextView lastName = ButterKnife.findById(view, R.id.last_name);
ImageView photo = ButterKnife.findById(view, R.id.photo);

```

如果你只是使用这个方法，可以使用静态引入`ButterKnife.findById`方法。

## 下载

```
dependencies {
    compile 'com.jakewharton:butterknife:8.5.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.5.1'
}

```

## License

```
Copyright 2013 Jake Wharton

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```