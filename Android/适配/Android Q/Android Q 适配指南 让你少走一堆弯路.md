

# 导读

**文中链接请自行科学上网**

**Android Q Beta 1** 刚出，讲道理国内是不到下半年不用理睬 **Q** 的，但是上月末的一封华为要求适配 Q 的邮件要求我们在 5 月底之前完成相关适配，不然应用会被下架。

一开始还心生奇怪，为什么这次华为的邮件来的那么早以及严格。当我仔细阅读了官方文档之后发现 **Q** 的更新特别多，且不适配**应用可能无法正常运行** (不管 targetSDK 是否为 Q)。

国内相关的文章还比较少，本文将总结归纳 AndroidQ 官方文档并将自己所踩过的坑记录下来，以便大家少走弯路。

本文将从三个角度介绍 **Android Q** 的部分适配问题，也是大家开发适配过程中**大概率**会遇到的问题：

*   Q 行为变更：所有应用 `（不管targetSdk是多少，对所有跑在Q设备上的应用均有影响）`
*   Q 行为变更：以 Android Q 为目标平台的应用`（targetSDK == Q 才有影响）`
*   项目升级遇到的问题

至于 **Q 的新功能及 SDK**，我粗略扫了一眼，项目中并没有涉及，故暂不介绍，只放出链接 [AndroidQ 新 API 及功能](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Fpreview%2Ffeatures)。

# Q 行为变更：所有应用

*   ## 用户隐私权限变更

    > AndroidQ 引入了大量更改和限制以增强对用户隐私的保护。

    官方文档将这一部分内容独立于 **Q 行为变更：所有应用**来介绍，是因为这一部分内容**庞大且重要** ，个人认为 **Q** 的最大更新就是**用户隐私权限变更**。具体变更的权限如下：

    | 权限 | 受影响应用 | 如何启用 (影响范围) |
    | --- | --- | --- |
    | 存储权限 | 访问和共享外部存储设备中的文件的应用 | adb shell sm set-isolated-storage on(下文详述) |
    | 定位权限 | 在后台时请求访问用户位置信息的应用 | 这种权限策略在 Android Q 上始终处于启用状态 |
    | 从后台启动 Activity | 不需要用户互动就启动 Activity 的应用 | 关闭允许系统执行后台活动开发者选项即可启用限制 |
    | 设备标识符 (deviceId) | 访问设备序列号或 IMEI 的应用 | 在搭载 Android Q 的设备上安装应用 |
    | 无线扫描权限 | 使用 WLAN API 和 Bluetooth API 的应用 | 以 Android Q 为目标平台 |

    因为`从后台启动Activity权限`和`无线扫描权限`两种权限的变更影响较少。本文不作详述，如有涉及请查阅[官方文档](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Fpreview%2Fprivacy%2Fbackground-activity-starts)。

    <pre>  从后台启动Activity权限变更仅针对与用户毫无交互就启动一个Activity的情况，(比如微信登陆授权)
    复制代码
    </pre>

    以下会着重介绍`存储权限`,`定位权限`和`设备标识符`三种权限的变更与适配

    *   ### 存储权限

        > Android Q 在外部存储设备中为每个应用提供了一个 “隔离存储沙盒”（例如 /sdcard）。任何其他应用都无法直接访问您应用的沙盒文件。由于文件是您应用的私有文件，因此您不再需要任何权限即可在外部存储设备中访问和保存自己的文件。此变更可让您更轻松地保证用户文件的隐私性，并有助于减少应用所需的权限数量。

        **沙盒**，简单而言就是应用专属文件夹，并且访问这个文件夹无需权限。谷歌官方**推荐**应用在沙盒内存储文件的地址为`Context.getExternalFilesDir()`下的文件夹。比如要存储一张图片, 则应放在`Context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)`中。

        以下将按访问的目标文件的地址介绍如何适配。

        *   访问自己文件：Q 中用更精细的媒体特定权限**替换并取消**了 `READ_EXTERNAL_STORAGE` 和 `WRITE_EXTERNAL_STORAGE`权限，**并且无需特定权限**，应用即可访问**自己沙盒中**的文件。

        *   访问系统媒体文件：Q 中引入了一个新定义**媒体文件的共享集合**，如果要访问沙盒外的媒体共享文件，比如**照片，音乐，视频等**，需要申请新的**媒体权限**:`READ_MEDIA_IMAGES`,`READ_MEDIA_VIDEO`,`READ_MEDIA_AUDIO`, 申请方法同原来的存储权限。

        *   访问系统下载文件：对于**系统下载文件夹**的访问，暂时没做限制，但是，要访问其中其他应用的文件，必须允许用户使用系统的文件选择器应用来选择文件。

        *   访问其他应用沙盒文件：如果你的应用需要使用**其他应用在沙盒内创建的文件**，请点击[使用其他应用的文件](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Fpreview%2Fprivacy%2Fscoped-storage%23work-with-other-apps-files), 本文不做介绍。

        所以请判断当应用运行在 **Q** 平台上时，**取消**对`READ_EXTERNAL_STORAGE` 和 `WRITE_EXTERNAL_STORAGE`两个权限的申请。并**替换**为新的媒体特定权限。

        ### 关于存储权限的 (如何启用) 影响范围

        *   #### 模拟器

            在 **Android Q Beat1** 中，谷歌**暂未开放**存储权限的改动。我们需要使用 adb 命令

            <pre>adb shell sm set-isolated-storage on
            复制代码
            </pre>

            来开启模拟器对于**存储权限**的变更来进行适配。

        *   #### 真机

            当满足以下**每个条件**时，将开启**兼容模式**，即不开启 **Q** 设备中的存储权限改动：

            <pre>  应用targetSDK<=P。
              应用安装在从 Android P 升级到 Android Q 的设备上。
            复制代码
            </pre>

            **但是当应用重新安装 (更新) 时，不会重新开启兼容模式，存储权限改动将生效。**

        所以按官方文档所说，**无论 targetSDK 是否为 Q，必须对应用进行存储权限改动的适配。**

        在我的测试中，当`targetSDK<=P`, 在 Q Beat1 版上申请两个旧权限时会自动改成申请三个新权限，不会影响应用正常使用，但当`targetSDK==Q`时，申请旧权限将失败并影响应用正常使用。

    *   ### 定位权限

        > 为了让用户更好地控制应用对位置信息的访问权限，Android Q 引入了新的位置权限 ACCESS_BACKGROUND_LOCATION。与现有的 ACCESS_FINE_LOCATION 和 ACCESS_COARSE_LOCATION 权限不同，新权限仅会影响应用在后台运行时对位置信息的访问权。除非应用的某个 Activity 可见或应用正在运行前台服务，否则应用将被视为在后台运行。

        与 iOS 系统一样，**Q** 中也加入了后台位置权限`ACCESS_BACKGROUND_LOCATION`，如果应用需要在后台时也获得用户位置 (比如滴滴)，就需要动态申请`ACCESS_BACKGROUND_LOCATION`权限。当然如果不需要的话，**应用就无需任何改动**，且谷歌会按照应用的 targetSDK 作出不同处理：

        *   targetSDK <= P 应用如果请求了`ACCESS_FINE_LOCATION` 或 `ACCESS_COARSE_LOCATION`权限，**Q** 设备会自动帮你申请`ACCESS_BACKGROUND_LOCATION`权限。

        ### 设备唯一标识符

        > 从 Android Q 开始，应用必须具有 READ_PRIVILEGED_PHONE_STATE 签名权限才能访问设备的不可重置标识符（包含 IMEI 和序列号）。许多用例不需要不可重置的设备标识符。如果您的应用没有该权限，但您仍尝试查询标识符的相关信息。会返回空值或报错。

        设备唯一标识符需要**特别注意**，原来的`READ_PHONE_STATE`权限已经**不能获得 IMEI 和序列号**，如果想在 **Q** 设备上通过

        <pre>((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()
        复制代码
        </pre>

        获得设备 ID，会返回空值`(targetSDK<=P)`或者报错`(targetSDK==Q)`。且官方所说的`READ_PRIVILEGED_PHONE_STATE`权限只提供给系统 app，所以这个方法算是废了。

        谷歌官方给予了[设备唯一 ID 最佳做法](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Ftraining%2Farticles%2Fuser-data-ids)，但是此方法给出的 ID 可变，可以按照具体需求具体解决。

        本文给出一个**不变**和**基本不重复**的 UUID 方法。

        <pre>public static String getUUID() {

        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = android.os.Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
            //使用硬件信息拼凑出来的15位号码
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
        复制代码
        </pre>

        虽然由于唯一标识符权限的更改会导致`android.os.Build.getSerial()`返回 unknown, 但是由于`m_szDevIDShort`是由硬件信息拼出来的，所以仍然保证了 UUID 的**唯一性**和**持久性**。

        经测试上述方法完全相同的手机有可能重复，网上还有其他方案比如 androidID, 但是 androidID 可能由于机型原因返回 null，所以个人任务两种方法半斤八两。设备 ID 的获取一个版本比一个版本艰难，如果有好的方法欢迎指出。

*   ## minSDK 警告

    > 在 Android Q 中，当用户首次运行以 Android 6.0（API 级别 23）以下的版本为目标平台的任何应用时，Android 平台会向用户发出警告。如果此应用要求用户授予权限，则系统会先向用户提供调整应用权限的机会，然后才会允许此应用首次运行。

    谷歌要求运行在 **Q** 设备上的应用`targetSDK>=23`, 不然会向用户发出警告。

# Q 行为变更：以 Android Q 为目标平台的应用

## 非 SDK 接口限制

**非 SDK 接口**限制在 Android P 中就已提出，但是在 **Q** 中，**被限制的接口的分类有较大变化**。

*   ### 非 SDK 接口介绍

    > 为了确保应用稳定性和兼容性，Android 平台开始限制您的应用可在 Android 9（API 级别 28）中使用哪些非 SDK 接口。Android Q 包含更新后的受限非 SDK 接口列表（基于与 Android 开发者之间的协作以及最新的内部测试）。

    **非 SDK 接口限制**就是某些`SDK`中的私用方法，如`private`方法，你通过 **Java 反射等方法**获取并调用了。那么这些调用将在`target>=P`或`target>=Q`的设备上被限制使用，当你使用了这些方法后，会报错:

    | 获取方法 | 报错信息 |
    | --- | --- |
    | Dalvik instruction referencing a field | NoSuchFieldError thrown |
    | Dalvik instruction referencing a method | NoSuchMethodError thrown |
    | Reflection via Class.getDeclaredField() or Class.getField() | NoSuchFieldException thrown |
    | Reflection via Class.getDeclaredMethod(), Class.getMethod() | NoSuchMethodException thrown |
    | Reflection via Class.getDeclaredFields(), Class.getFields() | Non-SDK members not in results |
    | Reflection via Class.getDeclaredMethods(), Class.getMethods() | Non-SDK members not in results |
    | JNI via env->GetFieldID() | NULL returned, NoSuchFieldError thrown |
    | JNI via env->GetMethodID() | NULL returned, NoSuchMethodError thrown |

*   ### 非 SDK 接口查找

    > 如果您不确定自己的应用是否使用了非 SDK 接口，则可以测试该应用进行确认

    当你调用了非 SDK 接口时，会有类似`Accessing hidden XXX`的日志:

    <pre>Accessing hidden field Landroid/os/Message;->flags:I (light greylist, JNI)
    复制代码
    </pre>

    但是一个大项目到底哪里使用了这些方法，靠`review`代码和看日志肯定是不现实的，谷歌官方也提供了官方检查器`veridex`用来检测一个 apk 中哪里使用了非 SDK 接口。[veridex 下载](https://link.juejin.im?target=https%3A%2F%2Fandroid.googlesource.com%2Fplatform%2Fprebuilts%2Fruntime%2F%2B%2Fmaster%2Fappcompat)。

    其中有`windows`,`linux`和`mac`版本，对应下载即可。下载解压后命令行`cd`到`veridex`目录下使用`./appcompat.sh --dex-file=Q.apk`即可自动扫描。`Q.apk`为包的绝对路径，如果包与`veridex`在相同目录下直接输入包文件名即可。

    扫描结果分为两部分，一部分为被调用的**非 SDK 接口**的位置，另一部分为**非 SDK 接口**数量统计，例如：

    ![](https://user-gold-cdn.xitu.io/2019/4/10/16a0571cf239c4be?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)
    *   greylist: 灰名单，即当前版本仍能使用的非 SDK 接口，但在下一版本中可能变成**被限制的非 SDK 接口**
    *   blacklist：黑名单，使用了就会报错。也是我们项目中必须解决的非 SDK 接口
    *   greylist-max-o： 在`targetSDK<=O`中能使用，但是在`targetSDK>=P`中被限制的非 SDK 接口
    *   greylist-max-p： 在`targetSDK<=P`中能使用，但是在`targetSDK>=Q`中被限制的非 SDK 接口

    所以从适配 **Q** 的角度出发，除了 greylist 我们可以暂时不解决以外，其余三种类型的**非 SDK 接口**需要我们进行适配。

*   ### 非 SDK 接口适配

    > 如果您的应用依赖于非 SDK 接口，则应该开始计划迁移到 SDK 替代方案。如果您无法为应用中的某项功能找到使用非 SDK 接口的替代方案，则应该请求新的公共 API。

    官方要求`targetSDK>=P`的应用不使用这些方法，并寻找其他的公共 API 去替代这些**非 SDK 接口**，如果找不到，则可以向谷歌申请，请求一个[新的公共 API](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Fdistribute%2Fbest-practices%2Fdevelop%2Frestrictions-non-sdk-interfaces%23feature-request)(一般不需要)。

    就我个人扫描并定位的结果来看，项目中使用非 SDK 接口大概率有以下两种情况：

    *   在自定义 View 的过程中为了方便，使用反射修改某个参数。
    *   三方 SDK 中使用了非 SDK 接口 (这种情况比较多)。

    第一种是好解决的，毕竟是我们自己写的代码。

    第二种就头疼了，只能更新到最新的三方 SDK 版本，或者提工单、换库 (也是整个适配过程中工作量最庞大的部分)。

# 项目升级遇到的问题

*   ### 模拟器 X86，项目中 SO 库为 v7

    *   找到 so 库源代码，编译成 x86
    *   如果 so 库只是某个功能点使用，对 APP 整体没大影响，就可以屏蔽特定 so 库功能或略过测试
    *   如果 so 库是项目核心库必须加载，也可使用腾讯云测，上面有谷歌亲儿子 Q 版本。腾讯云测有 adb 远程连接调试功能 (我没成功过)。adb 连不上也没关系，直接安装就行，云测上也可以直接看日志。
    *   至于 inter 的 houdini 我尝试研究过，理论上能安装在 x86 模拟器上让它编译 v7 的 so 库，但是由于关于 houdini 的介绍比较少也比较旧，建议大家时间不充裕的话就别研究了。
*   ### Requires development platform Q but this is a release platform.

    由于目前 Q 是 preview 版，所以 targetSDK==Q 的应用只能在 Q 设备上跑。

*   ### INSTALL_FAILED_INVALID_APK: Failed to extract native libraries, res=-2

    这个错误是由于打包压缩 so 库时造成的，具体原因可见 [issuetracker.google.com/issues/3704…](https://link.juejin.im?target=https%3A%2F%2Fissuetracker.google.com%2Fissues%2F37045367)

    <pre>在AndroidManifest.xml的application节点下加入android:extractNativeLibs="true"
    复制代码
    </pre>

    可能有人加了上面代码还是不行，在 app/build.gradle 中的 defaultConfig 节点下加入

    <pre>packagingOptions{ doNotStrip "/armeabi/.so" doNotStrip "/armeabi-v7a/.so" doNotStrip "/x86/.so" }
    复制代码
    </pre>

*   ### Didn't find class “org.apache.http.client.methods.HttpPost"

    <pre>在AndroidManifest.xml的application节点下加入
    <uses-library android:/>
    复制代码
    </pre>

*   ### 如果你的项目没有适配过 android O 或 P，那么你需要注意：

    *   android O 的读取已安装应用权限（对应用内自动更新有影响）
    *   android P 的默认禁止访问 http 的 API

    这两个版本的适配问题本文就不做详述，网上有很多详细的介绍。

# 总结

*   适配还是不能拉下，如果你一下子从 6.0 升级到 Q，你真的会哭的。
*   平时也多注意三方库的更新，因为安卓版本的更新势必导致了需要更新三方库。
*   官方文档的永远是最准确的。

# 参考文献

### [官方文档](https://link.juejin.im?target=https%3A%2F%2Fdeveloper.android.com%2Fpreview)

### [非 SDK 接口](https://juejin.im/post/5afe50eef265da0b70262463)