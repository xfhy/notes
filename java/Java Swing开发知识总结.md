# Java Swing开发知识总结
<font size="5">
1. JFrame<br/>
----------------------------------------------------------------------------
- 设置默认窗口左上角的小图标:<br/>

		Image icon = Toolkit.getDefaultToolkit().getImage("image/login/默认小图标.png");   
		mainFrame.setIconImage(icon);   //设置窗口左上角的小图标
- 设置窗体大小不可改变:mainFrame.setResizable(false);<br/>
- 设置JFrame居屏幕中央:mainFrame.setLocationRelativeTo(null); <br/>
- 设置JFrame退出:mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);<br/>
- 设置JFrame可见:mainFrame.setVisible(true); <br/>
- 设置JFrame背景图片

		public class JFrameBackground {  
  
		 private JFrame frame = new JFrame("背景图片测试");  
		  
		 private JPanel imagePanel;  
		  
		 private ImageIcon background;  
		  
		 public static void main(String[] args) {  
		  new JFrameBackground();  
		 }  
		  
		 public JFrameBackground() {  
		  background = new ImageIcon("003.jpg");// 背景图片  
		  JLabel label = new JLabel(background);// 把背景图片显示在一个标签里面  
		  // 把标签的大小位置设置为图片刚好填充整个面板  
		  label.setBounds(0, 0, background.getIconWidth(),  
		    background.getIconHeight());  
		  // 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明  
		  imagePanel = (JPanel) frame.getContentPane();  
		  imagePanel.setOpaque(false);  
		  // 内容窗格默认的布局管理器为BorderLayout  
		  imagePanel.setLayout(new FlowLayout());  
		  imagePanel.add(new JButton("测试按钮"));  
		  
		  frame.getLayeredPane().setLayout(null);  
		  // 把背景图片添加到分层窗格的最底层作为背景  
		  frame.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));  
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		  frame.setSize(background.getIconWidth(), background.getIconHeight());  
		  frame.setResizable(false);  
		  frame.setVisible(true);  
		 }  
		}  
- 监听JFrame窗口大小改变的方法:

		frame.addComponmentListener(new ComponentAdapter(){
		@Override public void componentResized(ComponentEvent e){
		    // write you code here
		}});

----------------------------------------------------------------------------

2. GridBagLayout(最难布局),需要配合GridBagConstraints使用:<br/>
----------------------------------------------------------------------------
- gridx和gridy： 设置Component在网格中的横向和纵向位置。<br/>
- gridwidth和gridheight：设置Component能在横向和纵向横跨多少个网格。<br/>
- fill：控制Component如何填充网格的区域：
NONE、HORIZONTAL 、VERTICAL 、BOTH 。<br/>
- ipadx和ipady：设置Component的内部填充大小，即在Component的最小大小上还需要加多少<br/>
- insets：外部填充大小，类似于Border<br/>
- weightx和weighty：设置在横向和纵向的占用比重。<br/>
当窗口大小改变的时候，可以使用这两个属性来控制Component随着窗口变化时，Component大小的变化比率。<br/>
两个属性的默认值为0，取值范围[0.0, 1.0]。<br/>
窗口大小变化时，比较同一行或者同一列中不同Component所对应的值的比值。<br/>

----------------------------------------------------------------------------

3. JButton<br/>
---------------------------------------------------------------------------
- 去掉外面那层样式,现在这个按钮就像文本一样:registeredAccountBtn.setContentAreaFilled(false);<br/>

---------------------------------------------------------------------------

4. JPasswordField<br/>
---------------------------------------------------------------------------
- 设置明文显示文字:passwordTextField.setEchoChar('\0');  <br/>
- 
---------------------------------------------------------------------------

5. JTextField<br/>
---------------------------------------------------------------------------
- 当需要限制用户输入时,需要重写PlainDocument,还可以限制用户输入的长度.调用时,userNameTextField.setDocument(new MyRegExp(NAMEREGEX,20));  这样调用即可.

		public class MyRegExp extends PlainDocument{
		
			/**
			 * 
			 */
			private static final long serialVersionUID = 2851695051373575598L;
			private Pattern pattern;
		    private Matcher m;
		    private int maxLength;
		    public MyRegExp(String pat,int maxLength)
		    {
		        super();
		        this.pattern=Pattern.compile(pat);
		        this.maxLength = maxLength;
		    }
		    
		    /**
		     * 向文档中插入某些内容。插入内容会导致在实际发生改变时存储写锁定，接着会向线程上抓取该写入锁定的观察者发出通知。
		     */
		    @Override
		    public void insertString(int offset, String str, AttributeSet attr)
		            throws BadLocationException {   
		        if (str == null){
		            return;
		        }
		        String tmp=getText(0, offset).concat(str);  //concat:将指定字符串连接到此字符串的结尾
		        m=pattern.matcher(tmp);
		        
		        //如果符合,才进行插入   getLength():以前的长度    str:现在需要插入的字符串
		        if(m.matches() && (getLength()+str.length()) <= maxLength)
		            super.insertString(offset, str, attr);
		    }
		}

---------------------------------------------------------------------------

6. JTextArea:
--------------------------------------------------------------------------

- 设置边框:diaryTextArea.setBorder(BorderFactory.createLineBorder(Color.gray,2));  //设置文本域边框
--------------------------------------------------------------------------
</font>
