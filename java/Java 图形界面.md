# Java 图形界面 #
<font size="5"><b>
1. ScrollPane本身默认使用了布局管理器是BorderLayout<br/>
2. 2个按钮被绑定到同一个监听器中,则可以通过监听器里面的方法的参数e.getSource()来获取事件源,然后根据这个事件源即可区分这2个按钮.<br/>
3. dispose() :关闭窗口<br/>
4. 菜单项后面有...则一般是要弹出对话框,平时写的时候注意一下.<br/>
5. Class.getResource方式加载图片打包(jar)后才可以用,如果是直接new Icon("ok.png");是不行的.<br/>
6. 小心工具栏上的按钮抢夺文本框的焦点, eg:用户选了一段文本,点复制选项,然后焦点就不在文本框中了,而跑到菜单项上了,这样的用户体验是极度不好的.需要设置一下焦点,菜单项的焦点为false<br/>
7. 文件选择器实现文件过滤需实现FileFilter.<br/>
8. 打开了模式对话框,则下面的代码不会执行,除非关闭了对话框,会被阻塞在那里.<br/>
9. 一些组件<br/>
---------------------------------------------------------------------------
Icon  图标<br/>
JButton 按钮<br/>
JRadioButton 单选按钮<br/>
ButtonGroup 单选按钮组,将单选按钮组合在一起的<br/>
JCheckBox  复选框<br/>
JComboBox 下拉选择框<br/>
JList 列表选择框<br/>
JTextArea 文本区域<br/>
JTextField 文本框   <br/>
JMenuBar 菜单栏<br/>
JMenu 菜单<br/>
JMenuItem 菜单项<br/>
JCheckBoxMenuItem 可勾选或者不勾选的菜单项<br/>
JPopupMenu 右键菜单<br/>

---------------------------------------------------------------------------
</b></font>