# Java Swing基础 #
<font size="5"><b>
1. Swing的几乎所有组件都是根据MVC模式设计的
而JList才是使用MVC模式的经典例子。  ListModel,ListCellRenderer(ListUI),JList<br/>
2. JComboxBox是下拉列表框。
和JList类似，JComboBox使用ComboBoxModel.<br/>
3. ListCellRenderer:<br/>
---------------------------------------------------------
- ListCellRenderer用于将ListModel中的每一个数据项，渲染称为某种显示格式。<br/>
- JList和JComboBox都可以使用ListCellRenderer来显示每一个列表项目。<br/>
- 默认的ListCellRenderer只是将ListModel中的数据显示成为一个简单的字符串。<br/>
- 如果需要显示格式更为复杂的列表项目，则可以自己继承并重写ListCellRenderer。<br/>

---------------------------------------------------------

4. JTextField和JPasswordField:输入的框,可以使用以下方法来控制特殊字符：setEchoChar(*);
<br/>
5. JFormattedTextField:<br/>
---------------------------------------------------------
- JFormattedTextField可以验证用户的输入是否合法。<br/>
- 主要构造函数：<br/>
	JFormattedTextField(Format)<br/>
	JFormattedTextField(Formatter)<br/>
- Format:<br/>
	DateFormat<br/>
	MessageFormat<br/>
	NumberFormat <br/>
- 当JFormattedTextField失去焦点的时候，Format将查看用户的输入，如果可以通过Format将用户的输入转换为特定的类型，则用户输入是合法的。<br/>
- 可以使用isEditValid()方法来获取用户刚刚输入是否合法。<br/>

---------------------------------------------------------
</b></font>