# 自定义TextView 可展开收缩

> 其实就是设置它的行数

	public class ExpandableTextView extends AppCompatTextView {

	    /**
	     * 收缩时至多显示多少行
	     */
	    private final int MAX = 2;
	
	    /**
	     * 如果完全伸展需要多少行
	     */
	    private int lines;
	
	    /**
	     * 当前这个TextView
	     */
	    private ExpandableTextView mTextView;
	    /**
	     * 标记当前TextView的展开/收缩状态
	     */
	    private boolean expandableStatus = false;
	
	    public ExpandableTextView(Context context) {
	        super(context);
	        init();
	    }
	
	    public ExpandableTextView(Context context, @Nullable AttributeSet attrs) {
	        super(context, attrs);
	        init();
	    }
	
	    public ExpandableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
	        init();
	    }
	
	    /**
	     * 初始化
	     */
	    private void init() {
	        mTextView = this;
	        // ViewTreeObserver View观察者，在View即将绘制但还未绘制的时候执行的，在onDraw之前
	        final ViewTreeObserver mViewTreeObserver = this.getViewTreeObserver();
	
	        mViewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
	
	            @Override
	            public boolean onPreDraw() {
	                // 避免重复监听
	                mTextView.getViewTreeObserver().removeOnPreDrawListener(this);
	
	                //获取TextView的行数
	                lines = getLineCount();
	
	                return true;
	            }
	        });
	
	        //设置TextView的至多这么高   行数
	        setMaxLines(MAX);
	    }
	
	    /**
	     * 设置是否展开或者收缩，
	     *
	     * @param isExpand true，展开；  false，不展开
	     */
	    public void setExpandable(boolean isExpand) {
	        if (isExpand) {
	            lines = getMaxLines();
	            setMaxLines(lines + 1);
	        } else {
	            //设置当前应该显示的行数   既收缩起来
	            setMaxLines(MAX);
	        }
	        //当前的收缩状态
	        expandableStatus = isExpand;
	    }
	
	    /**
	     * 获取当前的TextView的扩展状态
	     *
	     * @return
	     */
	    public boolean getExpandableStatus() {
	        return expandableStatus;
	    }
	}
