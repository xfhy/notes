
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.na517.project.library.BaseApplication;

/**
 * 描述： Toast工具类
 * 
 */
public final class ToastUtils {
    
    private static Handler handler = new Handler(Looper.getMainLooper());
    
    private static Toast mToast = null;
    
    private static Toast mCustomToast = null;
    
    private static Object synObj = new Object();
    
    /**
     * @description Toast消息提示，默认Toast.LENGTH_SHORT
     * @date 2015年8月17日
     * @param msg
     *            需要显示的消息
     */
    public static void showMessage(final String msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }
    
    /**
     * @description Toast消息提示，默认Toast.LENGTH_LONG
     * @date 2015年8月17日
     * @param msg
     *            需要显示的消息
     */
    public static void showMessageLong(final String msg) {
        showMessage(msg, Toast.LENGTH_LONG);
    }
    
    /**
     * @description Toast消息提示，默认Toast.LENGTH_SHORT
     * @date 2015年8月17日
     * @param msgResId
     *            消息id;
     */
    public static void showMessage(final int msgResId) {
        showMessage(msgResId, Toast.LENGTH_SHORT);
    }

    /**
     * @description Toast消息提示，默认Toast.LENGTH_SHORT
     * @date 2015年8月17日
     * @param msgResId
     *            消息id;
     */
    public static void showMessageLong(final int msgResId) {
        showMessage(msgResId, Toast.LENGTH_LONG);
    }

    public static void showMessage(final CharSequence msg, final int len) {
        if (msg == null || msg.equals("")) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) { // 加上同步是为了每个toast只要有机会显示出来
                    if (mToast != null) {
                        mToast.setText(msg);
                        mToast.setDuration(len);
                    }
                    else {
                        mToast = Toast.makeText(BaseApplication.getInstance().getApplicationContext(), msg, len);
                    }
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    mToast.show();
                }
            }
        });
    }

    public static void showMessage(final int msg, final int len) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    if (mToast != null) {
                        mToast.setText(msg);
                        mToast.setDuration(len);
                    }
                    else {
                        mToast = Toast.makeText(BaseApplication.getInstance().getApplicationContext(), msg, len);
                    }
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    mToast.show();
                }
            }
        });
    }
    
    /**
     * Toast发送消息
     */
    public static void showMessage(final View view, final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (mCustomToast == null) {
                                mCustomToast = new Toast(BaseApplication.getInstance());
                            }
                            mCustomToast.setDuration(len);
                            mCustomToast.setView(view);
                            mCustomToast.setGravity(Gravity.CENTER, 0, 0);
                            mCustomToast.show();
                        }
                    }
                });
            }
        }).start();
    }
}
