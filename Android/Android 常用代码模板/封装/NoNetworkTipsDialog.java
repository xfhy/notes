
import android.content.Context;
import android.content.Intent;

import com.na517.project.library.util.StringUtils;

/**
 */
public class NoNetworkTipsDialog extends ConfirmDialog {

    public NoNetworkTipsDialog(Context context,String showContent) {
        super(context, "提示", showContent, "取消", "去设置");
        setOnConfirmDialogListener(new OnConfirmDialogListener() {
            @Override
            public void onLeftClick() {
                dismiss();
            }

            @Override
            public void onRightClick() {
                // 跳转到系统的网络设置界面
                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                getContext().startActivity(intent);
            }
        });
    }


}
