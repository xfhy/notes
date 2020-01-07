> 本文由 [简悦 SimpRead](http://ksria.com/simpread/) 转码， 原文地址 https://www.jianshu.com/p/0f1e36507b9d

分析源码 5.1.1 r1
源码地址 [http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/app/ActivityThread.java#ActivityThread.installProvider%28android.content.Context%2Candroid.app.IActivityManager.ContentProviderHolder%2Candroid.content.pm.ProviderInfo%2Cboolean%2Cboolean%2Cboolean%29](https://link.jianshu.com?t=http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/app/ActivityThread.java#ActivityThread.installProvider%28android.content.Context%2Candroid.app.IActivityManager.ContentProviderHolder%2Candroid.content.pm.ProviderInfo%2Cboolean%2Cboolean%2Cboolean%29)

## 主要看 ActivityThread 类

### bind Application 的方法

handleBindApplication(AppBindData data)

### 内部代码块

<pre>try {
4524            // If the app is being launched for full backup or restore, bring it up in
4525            // a restricted environment with the base application class.
4526            Application app = data.info.makeApplication(data.restrictedBackupMode, null);
4527            mInitialApplication = app;
4528
4529            // don't bring up providers in restricted mode; they may depend on the
4530            // app's custom Application class
4531            if (!data.restrictedBackupMode) {
4532                List<ProviderInfo> providers = data.providers;
4533                if (providers != null) {
4534                    installContentProviders(app, providers);   //继续跟踪这个做了哪些事情
4535                    // For process that contains content providers, we want to
4536                    // ensure that the JIT is enabled "at some point".
4537                    mH.sendEmptyMessageDelayed(H.ENABLE_JIT, 10*1000);
4538                }
4539            }
4540
4541            // Do this after providers, since instrumentation tests generally start their
4542            // test thread at this point, and we don't want that racing.
4543            try {
4544                mInstrumentation.onCreate(data.instrumentationArgs);
4545            }
4546            catch (Exception e) {
4547                throw new RuntimeException(
4548                    "Exception thrown in onCreate() of "
4549                    + data.instrumentationName + ": " + e.toString(), e);
4550            }
4551
4552            try {
4553                mInstrumentation.callApplicationOnCreate(app);   // 执行Application.onCreate()
4554            } catch (Exception e) {
4555                if (!mInstrumentation.onException(app, e)) {
4556                    throw new RuntimeException(
4557                        "Unable to create application " + app.getClass().getName()
4558                        + ": " + e.toString(), e);
4559                }
4560            }

</pre>

### 跟踪 installContentProviders(app, providers)

<pre>private void More ...installContentProviders(
4581            Context context, List<ProviderInfo> providers) {
4582        final ArrayList<IActivityManager.ContentProviderHolder> results =
4583            new ArrayList<IActivityManager.ContentProviderHolder>();
4584
4585        for (ProviderInfo cpi : providers) {
4586            if (DEBUG_PROVIDER) {
4587                StringBuilder buf = new StringBuilder(128);
4588                buf.append("Pub ");
4589                buf.append(cpi.authority);
4590                buf.append(": ");
4591                buf.append(cpi.name);
4592                Log.i(TAG, buf.toString());
4593            }
4594            IActivityManager.ContentProviderHolder cph = installProvider(context, null, cpi,  //继续跟踪这里
4595                    false /*noisy*/, true /*noReleaseNeeded*/, true /*stable*/);
4596            if (cph != null) {
4597                cph.noReleaseNeeded = true;
4598                results.add(cph);
4599            }
4600        }
4601
4602        try {
4603            ActivityManagerNative.getDefault().publishContentProviders(
4604                getApplicationThread(), results);
4605        } catch (RemoteException ex) {
4606        }
4607    }

</pre>

### 跟踪 installProvider(context, null, cpi,...

<pre>try {
4986                final java.lang.ClassLoader cl = c.getClassLoader();
4987                localProvider = (ContentProvider)cl.
4988                    loadClass(info.name).newInstance();
4989                provider = localProvider.getIContentProvider();
4990                if (provider == null) {
4991                    Slog.e(TAG, "Failed to instantiate class " +
4992                          info.name + " from sourceDir " +
4993                          info.applicationInfo.sourceDir);
4994                    return null;
4995                }
4996                if (DEBUG_PROVIDER) Slog.v(
4997                    TAG, "Instantiating local provider " + info.name);
4998                // XXX Need to create the correct context for this provider.
4999                localProvider.attachInfo(c, info); //继续跟踪这里
5000            } catch (java.lang.Exception e) {
5001                if (!mInstrumentation.onException(null, e)) {
5002                    throw new RuntimeException(
5003                            "Unable to get provider " + info.name
5004                            + ": " + e.toString(), e);
5005                }
5006                return null;
5007            }

</pre>

### 跟踪 localProvider.attachInfo(c, info)

<pre>1674    private void More ...attachInfo(Context context, ProviderInfo info, boolean testing) {
1675        mNoPerms = testing;
1676
1677        /*
1678         * Only allow it to be set once, so after the content service gives
1679         * this to us clients can't change it.
1680         */
1681        if (mContext == null) {
1682            mContext = context;
1683            if (context != null) {
1684                mTransport.mAppOpsManager = (AppOpsManager) context.getSystemService(
1685                        Context.APP_OPS_SERVICE);
1686            }
1687            mMyUid = Process.myUid();
1688            if (info != null) {
1689                setReadPermission(info.readPermission);
1690                setWritePermission(info.writePermission);
1691                setPathPermissions(info.pathPermissions);
1692                mExported = info.exported;
1693                mSingleUser = (info.flags & ProviderInfo.FLAG_SINGLE_USER) != 0;
1694                setAuthorities(info.authority);
1695            }
1696            ContentProvider.this.onCreate();  // 调用ContentProvider onCreate()
1697        }
1698    }

</pre>

通过查看源码的方式验证 ContentProvider onCreate() 优先执行与 Application onCreate()