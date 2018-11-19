> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://mp.weixin.qq.com/s/V0cL9bSTM65HTIJ4CU4Cow

<section class="" style="font-size: 16px;color: rgb(84, 84, 84);margin-left: 6px;margin-right: 6px;line-height: 1.6;letter-spacing: 1px;word-break: break-all;font-family: &quot;Helvetica Neue&quot;, PingFangSC-Regular, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;">

> 本文由**玉刚说写作平台**提供写作赞助
> 原作者：**水晶虾饺**

这是一个系列，通过 8 篇文章帮助大家建立起 Flutter 的知识体系，建议大家好好阅读并收藏起来，如果能随手转发，那就更好了。本篇文章我们先介绍 Flutter 里一些常用的 UI 控件，然后借助官网提供的两个 demo 把所学的控件知识实际使用起来。

# 基本控件

## Widget

在 Flutter 里，UI 控件就是所谓的 Widget。通过组合不同的 Widget，来实现我们用户交互界面。

Widget 分为两种，一种是无状态的，叫 StatelessWidget，它只能用来展示信息，不能有动作（用户交互）；另一种是有状态的，叫 StatefulWidget，这种 Widget 可以通过改变状态使得 UI 发生变化，它可以包含用户交互。

StatelessWidget 的使用非常简单，我们只需要继承 StatelessWidget，然后实现 build 方法就可以了：

```
class FooWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    // ...  }}
```

关于 build 方法的实现，在后面我们学习具体的控件时读者就会了解的，这里暂时忽略掉。

StatefulWidget 用起来麻烦一些，他还需要一个 State：

```
class BarWidget extends StatefulWidget {  @override  State createState() {    return _BarWidgetState();  }}class _BarWidgetState extends State<BarWidget> {  @override  Widget build(BuildContext context) {    // ...  }}
```

这里看起来可能有些绕，BarWidget 依赖了 _BarWidgetState，而 _BarWidgetState 又继承了 State<BarWidget>。如果读者不太理解，其实也没有什么关系，这只是一个样板代码，照着写就行了。

从 BarWidget 的实现来看，好像跟前面使用 StatelessWidget 没有什么区别，都是在 build 方法里面返回一个 Widget，只是 stateful widget 把这个方法挪到了 State 里面。实际上，两者的区别非常大。stateless widget 整个生命周期里都不会改变，所以 build 方法只会执行一次。而 stateful widget 只要状态改变，就会调用 build 方法重新创建 UI。

为了触发 UI 的重建，我们可以调用 setState 方法。下面的代码读者留意一下即可，在后面我们学习了相关的控件后再回过头来看。

```
class BarWidget extends StatefulWidget {  @override  State createState() {    return _BarWidgetState();  }}class _BarWidgetState extends State<BarWidget> {  var i = 0;  @override  Widget build(BuildContext context) {    return Row(      children: <Widget>[        Text('i = $i'),        RaisedButton(          onPressed: () {            setState(() {              ++i;            });          },          child: Text('click'),        )      ],    );  }}
```

下面我们开始学习一些具体的控件。

## 文本

为了展示文本，我们使用 Text：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Text("Put your text here");  }}
```

这就是最简单的文本了，它使用的是默认的样式。很多情况下，我们都需要对文本的样式进行修改，这个时候，可以使用 TextStyle：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Text(      "Put your text here",      style: TextStyle(        color: Colors.blue,        fontSize: 16.0,        fontWeight: FontWeight.bold      ),    );  }}
```

## 图片

使用 Image，可以让我们向用户展示一张图片。图片的来源可以是网络、文件、资源和内存，它们对应的构造函数分别是：

```
Image.asset(name);Image.file(file);Image.memory(bytes);Image.network(src);
```

比方说，为了展示一张来自网络的图片，我们可以这样：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Image.network(      "http://www.example.com/xxx.png",      width: 200.0,      height: 150.0,    );  }}
```

## 按钮

Flutter 提供了两个基本的按钮控件：FlatButton 和 RaisedButton，它们的使用方法是类似的：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    var flatBtn = FlatButton(      onPressed: () => print('FlatButton pressed'),      child: Text('BUTTON'),    );    var raisedButton = RaisedButton(      onPressed: () => print('RaisedButton pressed'),      child: Text('BUTTON'),    );    return raisedButton;  }}
```

通过设置 onPressed 回调，我们可以在按钮被点击的时候得到回调。child 参数用于设置按钮的内容。虽然我们给 child 传递的是 Text，但这不是必需的，它可以接受任意的 Widget，比方说，Image。

注意，由于我们只是在按钮点击的时候打印一个字符串，这里使用 StatelessWidget 是没有问题的。但如果有其他 UI 动作（比如弹出一个 dialog，则必须使用 StatefulWidget）。

它们的区别只是样式不同而已的：

FlatButton：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDNMIcbNB5ibicibCwJiasUKREEsvWjZUkiadQbr5v9xOH8XpibrjV27iaulOyQ/640?wx_fmt=png)flat-button

RaiseButton：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDbvzvrNqOx30lUEc4xUdI15hibqANYMd4ZwrZ7DLKFvENqicEeMzIQZ0A/640?wx_fmt=png)raised-button

## 文本输入框

Flutter 的文本输入框叫 TextField。为了获取用户输入的文本，我们需要给他设置一个 controller。通过这个 controller，就可以拿到文本框里的内容：

```
class MessageForm extends StatefulWidget {  @override  State createState() {    return _MessageFormState();  }}class _MessageFormState extends State<MessageForm> {  var editController = TextEditingController();  @override  Widget build(BuildContext context) {    // Row、Expand 都是用于布局的控件，这里可以先忽略它们    return Row(      children: <Widget>[        Expanded(          child: TextField(            controller: editController,          ),        ),        RaisedButton(          child: Text("click"),          onPressed: () => print('text inputted: ${editController.text}'),        )      ],    );  }  @override  void dispose() {    super.dispose();    // 手动调用 controller 的 dispose 方法以释放资源    editController.dispose();  }}
```

## 显示弹框

在前面的 TextField 例子中，我们只是把用户的输入通过 print 打印出来，这未免也太无趣了。在这一小节，我们要把它显示在 dialog 里。为了弹出一个 dialog，我们需要调用 showDialog 方法并传递一个 builder：

```
class _MessageFormState extends State<MessageForm> {  var editController = TextEditingController();  @override  Widget build(BuildContext context) {    return Row(      children: <Widget>[        Expanded(          child: TextField(            controller: editController,          ),        ),        RaisedButton(          child: Text("click"),          onPressed: () {            showDialog(                // 第一个 context 是参数名，第二个 context 是 State 的成员变量                context: context,                builder: (_) {                  return AlertDialog(                    // dialog 的内容                    content: Text(editController.text),                    // actions 设置 dialog 的按钮                    actions: <Widget>[                      FlatButton(                        child: Text('OK'),                        // 用户点击按钮后，关闭弹框                        onPressed: () => Navigator.pop(context),                      )                    ],                  );                }            );          }        )      ],    );  }  @override  void dispose() {    super.dispose();    editController.dispose();  }}
```

## 最简单的布局——Container、Padding 和 Center：

我们经常说，Flutter 里面所有的东西都是 Widget，所以，布局也是 Widget。

控件 Container 可以让我们设置一个控件的尺寸、背景、margin 等：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Container(      child: Text('text'),      padding: EdgeInsets.all(8.0),      margin: EdgeInsets.all(4.0),      width: 80.0,      decoration: BoxDecoration(        // 背景色        color: Colors.grey,        // 圆角        borderRadius: BorderRadius.circular(5.0),      ),    );  }}
```

如果我们只需要 padding，可以使用控件 Padding：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Padding(      padding: EdgeInsets.all(8.0),      child: Text('text'),    );  }}
```

Center 就跟它的名字一样，把一个控件放在中间：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Container(      padding: EdgeInsets.all(8.0),      margin: EdgeInsets.all(4.0),      width: 200.0,      height: 200.0,      decoration: BoxDecoration(        // 背景色        color: Colors.grey,        // 圆角        borderRadius: BorderRadius.circular(5.0),      ),      // 把文本放在 Container 的中间      child: Center(        child: Text('text'),      ),    );  }}
```

## 水平、竖直布局和 Expand

我们经常说，Flutter 里面所有的东西都是 Widget，所以，布局也是 Widget。水平布局我们可以使用 Row，竖直布局使用 Column。

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Row(      // 只有一个子元素的 widget，一般使用 child 参数来设置；Row 可以包含多个子控件，      // 对应的则是 children。      children: <Widget>[        Text('text1'),        Text('text2'),        Text('text3'),        Text('text4'),      ],    );  }}
```

Column 的使用是一样的：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Column(      children: <Widget>[        Text('text1'),        Text('text2'),        Text('text3'),        Text('text4'),      ],    );  }}
```

关于 Expand 控件，我们来看看 TextField 的那个例子：

```
class MessageForm extends StatefulWidget {  @override  State createState() {    return _MessageFormState();  }}class _MessageFormState extends State<MessageForm> {  var editController = TextEditingController();  @override  Widget build(BuildContext context) {    return Row(      children: <Widget>[        // 占满一行里除 RaisedButton 外的所有空间        Expanded(          child: TextField(            controller: editController,          ),        ),        RaisedButton(          child: Text("click"),          onPressed: () => print('text inputted: ${editController.text}'),        )      ],    );  }  @override  void dispose() {    super.dispose();    editController.dispose();  }}
```

这里通过使用 Expand，TextField 才能够占满一行里除按钮外的所有空间。此外，当一行 / 列里有多个 Expand 时，我们还可以通过设置它的 flex 参数，在多个 Expand 之间按比例划分可用空间。

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Row(      children: <Widget>[        Expanded(          // 占一行的 2/3          flex: 2,          child: RaisedButton(child: Text('btn1'),),        ),        Expanded(          // 占一行的 1/3          flex: 1,          child: RaisedButton(child: Text('btn2'),),        ),      ],    );  }}
```

## Stack 布局

有些时候，我们可能会希望一个控件叠在另一个控件的上面。于是，Stack 应运而生：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Stack(      children: <Widget>[        Text('foobar'),        Text('barfoo'),      ],    );  }}
```

默认情况下，子控件都按 Stack 的左上角对齐，于是，上面的两个文本完全一上一下堆叠在一起。我们还可以通过设置 alignment 参数来改变这个对齐的位置：

```
class TestWidget extends StatelessWidget {  @override  Widget build(BuildContext context) {    return Stack(      // Aligment 的取值范围为 [-1, 1]，Stack 中心为 (0, 0)，      // 这里设置为 (-0.5, -0.5) 后，可以让文本对齐到 Container 的 1/4 处      alignment: const Alignment(-0.5, -0.5),      children: <Widget>[        Container(          width: 200.0,          height: 200.0,          color: Colors.blue,        ),        Text('foobar'),      ],    );  }}
```

效果如下：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDmJMLhQqbWuv8ElDPLarX1f3H9wksJ8icN1U9icCCcd8TN9aMibJc1xvvg/640?wx_fmt=png)screenshot-stack

通过组合 Row/Column 和 Stack，已经能够完成绝大部分的布局了，所以 Flutter 里没有相对布局之类的东西。更多的 Flutter 控件，读者可以参考 https://flutter.io/widgets/。

# 示例一

在这一节里，我们综合前面所学的知识，来实现下面这个界面。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDBEayssSZHziazAE8uTAeUPYs5P5L17oMZFhgx3DJ8uviciau721FJC5Yw/640?wx_fmt=png)lakes-diagram

## 展示图片

1.  把图片 lake 放到项目根目录的 images 文件夹下（如果没有，你需要自己创建一个）

2.  修改 pubspec.yaml，找到下面这个地方，然后把图片加进来

    ```
    flutter:  # The following line ensures that the Material Icons font is  # included with your application, so that you can use the icons in  # the material Icons class.  uses-material-design: true  # To add assets to your application, add an assets section, like this:  # assets:  #  - images/a_dot_burr.jpeg  #  - images/a_dot_ham.jpeg
    ```

    修改后如下：

    ```
    flutter:  # The following line ensures that the Material Icons font is  # included with your application, so that you can use the icons in  # the material Icons class.  uses-material-design: true  # To add assets to your application, add an assets section, like this:  assets:    - images/lake.jpg
    ```

3.  现在，我们可以把这张图片展示出来了：

    ```
    void main() {  runApp(MyApp());}class MyApp extends StatelessWidget {  @override  Widget build(BuildContext context) {    return MaterialApp(      title: 'Flutter UI basic 1',      home: Scaffold(        appBar: AppBar(          title: Text('Top Lakes'),        ),        body: Image.asset(          'images/lake.jpg',          width: 600.0,          height: 240.0,          // cover 类似于 Android 开发中的 centerCrop，其他一些类型，读者可以查看          // https://docs.flutter.io/flutter/painting/BoxFit-class.html          fit: BoxFit.cover,        )      ),    );  }}
    ```

如果读者是初学 Flutter，**强烈建议**在遇到不熟悉的 API 时翻一翻文档，并在文档中找到 demo 所使用的 API。我们的例子不可能覆盖所有的 API，通过这种方式熟悉文档后，读者就可以根据文档实现出自己想要的效果。不妨就从 Image 开始吧，在 https://docs.flutter.io/flutter/widgets/Image/Image.asset.html 找出上面我们使用的 Image.asset 构造函数的几个参数的含义，还有 BoxFit 的其他几个枚举值。

## 布局

在这一小节，我们来实现图片下方的标题区域。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDmXpGXqk3zlInSWpBo41ibtu2KuetMibRq1yZSoUA0GbMDtKoyg0HAExA/640?wx_fmt=png)

我们直接来看代码：

```
class _TitleSection extends StatelessWidget {  final String title;  final String subtitle;  final int starCount;  _TitleSection(this.title, this.subtitle, this.starCount);  @override  Widget build(BuildContext context) {    // 为了给 title section 加上 padding，这里我们给内容套一个 Container    return Container(      // 设置上下左右的 padding 都是 32。类似的还有 EdgeInsets.only/symmetric 等      padding: EdgeInsets.all(32.0),      child: Row(        children: <Widget>[          // 这里为了让标题占满屏幕宽度的剩余空间，用 Expanded 把标题包了起来          Expanded(            // 再次提醒读者，Expanded 只能包含一个子元素，使用的参数名是 child。接下来，            // 为了在竖直方向放两个标题，加入一个 Column。            child: Column(              // Column 是竖直方向的，cross 为交叉的意思，也就是说，这里设置的是水平方向              // 的对齐。在水平方向，我们让文本对齐到 start（读者可以修改为 end 看看效果）              crossAxisAlignment: CrossAxisAlignment.start,              children: <Widget>[                // 聪明的你，这个时候肯定知道为什么突然加入一个 Container 了。                // 跟前面一样，只是为了设置一个 padding                Container(                  padding: const EdgeInsets.only(bottom: 8.0),                  child: Text(                    title,                    style: TextStyle(fontWeight: FontWeight.bold),                  ),                ),                Text(                  subtitle,                  style: TextStyle(color: Colors.grey[500]),                )              ],            ),          ),          // 这里是 Row 的第二个子元素，下面这两个就没用太多值得说的东西了。          Icon(            Icons.star,            color: Colors.red[500],          ),          Text(starCount.toString())        ],      ),    );  }}
```

## 对齐

接下来我们要做的这一部分在布局上所用到的知识，基本知识在上一小节我们都已经学习了。这里唯一的区别在于，三个按钮是水平分布的。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDJnyT14gq96uayMaaBt3JsibjVaoX48wf3hG1Gok6lEhiaqBHvfqoUrLQ/640?wx_fmt=png)

实现如下：

```
Widget _buildButtonColumn(BuildContext context, IconData icon, String label) {  final color = Theme.of(context).primaryColor;  return Column(    // main axis 跟我们前面提到的 cross axis 相对应，对 Column 来说，指的就是竖直方向。    // 在放置完子控件后，屏幕上可能还会有一些剩余的空间（free space），min 表示尽量少占用    // free space；类似于 Android 的 wrap_content。    // 对应的，还有 MainAxisSize.max    mainAxisSize: MainAxisSize.min,    // 沿着 main axis 居中放置    mainAxisAlignment: MainAxisAlignment.center,    children: <Widget>[      Icon(icon, color: color),      Container(        margin: const EdgeInsets.only(top: 8.0),        child: Text(          label,          style: TextStyle(            fontSize: 12.0,            fontWeight: FontWeight.w400,            color: color,          ),        ),      )    ],  );}class MyApp extends StatelessWidget {  @override  Widget build(BuildContext context) {    //...    Widget buttonSection = Container(      child: Row(        // 沿水平方向平均放置        mainAxisAlignment: MainAxisAlignment.spaceEvenly,        children: [          _buildButtonColumn(context, Icons.call, 'CALL'),          _buildButtonColumn(context, Icons.near_me, 'ROUTE'),          _buildButtonColumn(context, Icons.share, 'SHARE'),        ],      ),    );  //...}
```

关于 cross/main axis，看看下面这两个图就很清楚了：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDNcIduxgPrQAibe09yYPZXv1fQZSbH3p66qDthcFKYpicRbYZ1NWxAJXQ/640?wx_fmt=png)
![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDQiaxh6UGibUt4ibTtCcQEa1mTpfXaBvM9v1O7WPCPAE2ttA1M4eZOibexA/640?wx_fmt=png)

MainAxisAlignment 的更多的信息，可以查看 https://docs.flutter.io/flutter/rendering/MainAxisAlignment-class.html。

## 全部放到一起

```
class MyApp extends StatelessWidget {  @override  Widget build(BuildContext context) {    final titleSection = _TitleSection(        'Oeschinen Lake Campground', 'Kandersteg, Switzerland', 41);    final buttonSection = ...;    final textSection = Container(        padding: const EdgeInsets.all(32.0),        child: Text(          '''Lake Oeschinen lies at the foot of the Blüemlisalp in the Bernese Alps. Situated 1,578 meters above sea level, it is one of the larger Alpine Lakes. A gondola ride from Kandersteg, followed by a half-hour walk through pastures and pine forest, leads you to the lake, which warms to 20 degrees Celsius in the summer. Activities enjoyed here include rowing, and riding the summer toboggan run.          ''',          softWrap: true,        ),    );    return MaterialApp(      title: 'Flutter UI basic 1',      home: Scaffold(          appBar: AppBar(            title: Text('Top Lakes'),          ),          // 由于我们的内容可能会超出屏幕的长度，这里把内容都放到 ListView 里。          // 除了这种用法，ListView 也可以像我们在 Android 原生开发中使用 ListView 那样，          // 根据数据动态生成一个个 item。这个我们在下一节再来学习          body: ListView(            children: <Widget>[              Image.asset(                'images/lake.jpg',                width: 600.0,                height: 240.0,                // cover 类似于 Android 开发中的 centerCrop，其他一些类型，读者可以查看                // https://docs.flutter.io/flutter/painting/BoxFit-class.html                fit: BoxFit.cover,              ),              titleSection,              buttonSection,              textSection            ],          ),      )    );  }}}
```

现在，如果没有出错的话，运行后应该就可以看到下面这个页面。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDF6YibeWIUrYia5ZibXeIR4Mg56YaGSDgCicNfiaLuZQSbC4g2U5IDkQH22A/640?wx_fmt=png)

如果你遇到了麻烦，可以在这里找到所有的源码：

```
git clone https://github.com/Jekton/flutter_demo.gitcd flutter_demogit checkout ui-basic1
```

更多的布局知识，读者还可以参考 https://flutter.io/tutorials/layout/。

# 示例二

在这一小节我们来实现一个 list view。

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDLMAuaUn2FP9jSb8TqRJqfOC1gpDkIRTZA4qHEQfdcJqG5bTleQhIicw/640?wx_fmt=png)

这里我们采用的还是官网提供的例子，但是换一种方式来实现，让它跟我们平时使用 Java 时更像一些。

首先给数据建模：

```
enum BuildingType { theater, restaurant }class Building {  final BuildingType type;  final String title;  final String address;  Building(this.type, this.title, this.address);}
```

然后实现每个 item 的 UI：

```
class ItemView extends StatelessWidget {  final int position;  final Building building;  ItemView(this.position, this.building);  @override  Widget build(BuildContext context) {    final icon = Icon(        building.type == BuildingType.restaurant            ? Icons.restaurant            : Icons.theaters,        color: Colors.blue[500]);    final widget = Row(      children: <Widget>[        Container(          margin: EdgeInsets.all(16.0),          child: icon,        ),        Expanded(          child: Column(            crossAxisAlignment: CrossAxisAlignment.start,            children: <Widget>[              Text(                building.title,                style: TextStyle(                  fontSize: 20.0,                  fontWeight: FontWeight.w500,                )              ),              Text(building.address)            ],          ),        )      ],    );    return widget;  }}
```

接着是 ListView。由于渲染机制不同，这里没必要弄个 adapter 来管理 widget：

```
class BuildingListView extends StatelessWidget {  final List<Building> buildings;  BuildingListView(this.buildings);  @override  Widget build(BuildContext context) {    // ListView.builder 可以按需生成子控件    return ListView.builder(      itemCount: buildings.length,      itemBuilder: (context, index) {        return new ItemView(index, buildings[index]);      }    );  }}
```

现在，我们来给 item 加上点击事件。

```
// 定义一个回调接口typedef OnItemClickListener = void Function(int position);class ItemView extends StatelessWidget {  final int position;  final Building building;  final OnItemClickListener listener;  // 这里的 listener 会从 ListView 那边传过来  ItemView(this.position, this.building, this.listener);  @override  Widget build(BuildContext context) {    final widget = ...;    // 一般来说，为了监听手势事件，我们使用 GestureDetector。但这里为了在点击的时候有个    // 水波纹效果，使用的是 InkWell。    return InkWell(      onTap: () => listener(position),      child: widget    );  }}class BuildingListView extends StatelessWidget {  final List<Building> buildings;  final OnItemClickListener listener;  // 这是对外接口。外部通过构造函数传入数据和 listener  BuildingListView(this.buildings, this.listener);  @override  Widget build(BuildContext context) {    return ListView.builder(      itemCount: buildings.length,      itemBuilder: (context, index) {        return new ItemView(index, buildings[index], listener);      }    );  }}
```

最后加上一些脚手架代码，我们的列表就能够跑起来了：

```
void main() {  runApp(MyApp());}class MyApp extends StatelessWidget {  @override  Widget build(BuildContext context) {    final buildings = [      Building(BuildingType.theater, 'CineArts at the Empire', '85 W Portal Ave'),      Building(BuildingType.theater, 'The Castro Theater', '429 Castro St'),      Building(BuildingType.theater, 'Alamo Drafthouse Cinema', '2550 Mission St'),      Building(BuildingType.theater, 'Roxie Theater', '3117 16th St'),      Building(BuildingType.theater, 'United Artists Stonestown Twin', '501 Buckingham Way'),      Building(BuildingType.theater, 'AMC Metreon 16', '135 4th St #3000'),      Building(BuildingType.restaurant, 'K\'s Kitchen', '1923 Ocean Ave'),      Building(BuildingType.restaurant, 'Chaiya Thai Restaurant', '72 Claremont Blvd'),      Building(BuildingType.restaurant, 'La Ciccia', '291 30th St'),      // double 一下      Building(BuildingType.theater, 'CineArts at the Empire', '85 W Portal Ave'),      Building(BuildingType.theater, 'The Castro Theater', '429 Castro St'),      Building(BuildingType.theater, 'Alamo Drafthouse Cinema', '2550 Mission St'),      Building(BuildingType.theater, 'Roxie Theater', '3117 16th St'),      Building(BuildingType.theater, 'United Artists Stonestown Twin', '501 Buckingham Way'),      Building(BuildingType.theater, 'AMC Metreon 16', '135 4th St #3000'),      Building(BuildingType.restaurant, 'K\'s Kitchen', '1923 Ocean Ave'),      Building(BuildingType.restaurant, 'Chaiya Thai Restaurant', '72 Claremont Blvd'),      Building(BuildingType.restaurant, 'La Ciccia', '291 30th St'),    ];    return MaterialApp(      title: 'ListView demo',      home: Scaffold(        appBar: AppBar(          title: Text('Buildings'),        ),        body: BuildingListView(buildings, (index) => debugPrint('item $index clicked'))      ),    );  }}
```

这个时候你应该可以看到像这样的界面了：

![](https://mmbiz.qpic.cn/mmbiz_png/zKFJDM5V3WwUngGKiauicIP9OKgDoZpwjDGPEeHVVkYINC8XVEwACjPtMshHr7lKjBSAvEVm85O5Oaav51OASQAw/640?wx_fmt=png)

如果你遇到了什么麻烦，可以查看 tag ui-basic2 的代码：

```
git clone https://github.com/Jekton/flutter_demo.gitcd flutter_demogit checkout ui-basic2
```

**推荐阅读**
[Flutter 学习指南：熟悉 Dart 语言](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649493277&idx=1&sn=83cae5e71af5a1ba486add4824e51e91&chksm=8eec84e2b99b0df4fc5f9f56719fc5edcdaeac2851b7147ebbe331f21f9f7d13f32ab104d2f7&scene=21#wechat_redirect) [Flutter 学习指南：编写第一个应用](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649493273&idx=1&sn=7e543c597adfab4bdd79a0699239db64&chksm=8eec84e6b99b0df0020242b0ed085b4b38fcbae8a31710d387cd3545ddb22139ed1d32eb545c&scene=21#wechat_redirect) [Flutter 学习指南：开发环境搭建](http://mp.weixin.qq.com/s?__biz=MzIwMTAzMTMxMg==&mid=2649492521&idx=1&sn=723658efc8221f15f6a6a93e1a3f08c8&chksm=8eec87d6b99b0ec0fef71ee2a29f03c9997124fd91c031d350500df973e648954435e823b04f&scene=21#wechat_redirect)

编程 · 思维 · 职场
关注后回复 [礼包] 领取 50 元编程礼包

![](https://mmbiz.qpic.cn/mmbiz_jpg/zKFJDM5V3WzzNpnqOGq3mMO64mFVSicAIkzUSiam08j6DetjnjeujRjEAZRe7PqmPGqow3GWxSk4gas6r7BA4k6A/640?wx_fmt=jpeg)

</section>