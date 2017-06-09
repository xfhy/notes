
public class AToolActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);

        initPermission();  //统一申请权限

        initClickEvent();  //初始化点击事件

    }


    /**
     * 统一申请权限
     */
    private void initPermission() {
        //申请权限
        //检查用户是否已经给我们授权了权限,相等则已经授权,不等则没授权
        boolean haveWriteStorage = ContextCompat.checkSelfPermission(mContext, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean haveReadSms = ContextCompat.checkSelfPermission(mContext, Manifest.permission
                .READ_SMS) == PackageManager.PERMISSION_GRANTED;
        if (!haveWriteStorage || !haveReadSms) {
            ActivityCompat.requestPermissions(AToolActivity.this, new String[]{Manifest
                            .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS},
                    MY_PERMISSION_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_STATE:
                //数组中有值    数组第一个就是上面所申请的权限的第一个的是否成功的值   如果为0(PERMISSION_GRANTED)则成功
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    //申请权限成功
                    ToastUtil.showSuccess("申请权限成功");
                } else {
                    //申请权限失败
                    ToastUtil.showError("亲,未授权的话无法正常使用功能哦~");
                }
                break;
        }
    }
}
