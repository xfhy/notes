

/**

 */
public class LoadingDialog extends Dialog {
    
    private String mHint;
    
    private Context mContext;
    
    public LoadingDialog(Context context, String hint) {
        super(context, R.style.CommanDialogStyle);
        mHint = hint;
        mContext = context;
        initView();
    }

    public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
        initView();
    }
    
    private void initView() {
        setCanceledOnTouchOutside(false);
        
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null);
        setContentView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_loading);
        TextView textView = (TextView) view.findViewById(R.id.tv_hint);
        
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
        
        if (!StringUtils.isNullOrEmpty(mHint)) {
            textView.setText(mHint);
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NetworkRequest.cancelRequestByTag(mContext);
    }
}
