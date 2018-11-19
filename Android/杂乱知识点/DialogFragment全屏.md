
## 方式1:

```kotlin
class HomeFullInviteDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_home_full_invite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        super.onActivityCreated(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(0x00000000))
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

}
```

## 方式2:
第一步在style中定义全屏Dialog样式
```
<style name="Dialog.FullScreen" parent="Theme.AppCompat.Dialog">
    <item name="android:windowNoTitle">true</item>
    <item name="android:windowBackground">@color/transparent</item>
    <item name="android:windowIsFloating">false</item>
</style>
```
第二步：设置样式，以DialogFragment为例，只需要在onCreate中setStyle(STYLE_NORMAL, R.style.Dialog_FullScreen)即可。（推荐使用DialogFragment，它复用了Fragment的声明周期，被杀死后，可以恢复重建）
```
public class FragmentFullScreen extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Dialog_FullScreen);
    }
}
```
如果是在Dialog中,设置如下代码即可。
```
public class FullScreenDialog extends Dialog {
    public FullScreenDialog(Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      }
}
```