package com.ilinklink.tg.communal;/**
 * Created by AA on 2016/7/7.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.google.common.base.Joiner;
import com.ilinklink.greendao.DaoMaster;
import com.ilinklink.greendao.DaoSession;
import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.LoginModel;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.entity.ExamInfoResponse;
import com.ilinklink.tg.entity.PersionImageRespons;
import com.ilinklink.tg.entity.SysConfig;
import com.ilinklink.tg.enums.ActivityLifecycleStatus;
import com.ilinklink.tg.green_dao.CustomDbUpdateHelper;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.utils.CollectionUtils;
import com.ilinklink.tg.utils.Constants;
import com.ilinklink.tg.utils.Json;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.PackageUtil;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.ilinklink.tg.utils.Tools;
import com.qdong.communal.library.module.network.LinkLinkApi;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.util.FileUtils;
import com.qdong.communal.library.util.JsonUtil;
import com.qdong.communal.library.util.OkHttpUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.ilinklink.app.fw.BuildConfig;
import com.tencent.smtt.sdk.QbSdk;

import org.json.JSONException;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * AppLoader
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/7  14:13
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class AppLoader extends Application{

    private static final String TAG = AppLoader.class.getSimpleName();

    private static AppLoader ourInstance;

    /**
     * activity生命周期监测
     **/
    private ActivityLifecycleCallbacksImpl mActivityLifecycleCallbacksImpl;

    /**
     * 手机状态栏的高度
     **/
    public static int STATUS_BAR_HEIGHT=70;//在LuanchActivity里赋值

    /**
     * 手机屏幕高度
     **/
    public static int SCREEN_HEIGHT;//在LuanchActivity里赋值

    /**
     * 手机屏幕宽度
     **/
    public static int SCREEN_WIDTH;//在LuanchActivity里赋值

    /**
     * 读取到host配置文件之后，初始化api实例
     **/
    private LinkLinkApi mApi;

    public static AppLoader getInstance() {
        return ourInstance;
    }

    /**
     * key是activity类名,value是他此时的状态
     **/
    private HashMap<String, ActivityLifecycleStatus> mActivityStack = new HashMap<String, ActivityLifecycleStatus>();

    //评论临时保存数据
    private static HashMap<String, String> mCommentMap = new HashMap<>();

    public static boolean[] isShowMainPop = {false, false, false, false};//记录首页弹窗是否打开过



   /* @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();

        ourInstance=this;
        mActivityLifecycleCallbacksImpl=new ActivityLifecycleCallbacksImpl();
        this.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacksImpl);

        *//***极光**//*
        JPushInterface.setDebugMode(com.qdong.ps.BuildConfig.LOG_SWITCH); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);


        //init demo helper
        DemoHelper.getInstance().init(this);
        //wechat
        initWx();
        //qq
        initQQ();


    }*/



    /***************************************************************************
     * QQ相关
     *
     */
    //private Tencent mTencent;

//    private void initQQ() {
//        mTencent = Tencent.createInstance(BuildConfig.QQ_APP_ID, this);
//    }

//    public Tencent getmTencent() {
//        return mTencent;
//    }

    /**
     * @param :[]
     * @return type:int
     * @method name:getScreenHeight
     * @des:获取屏幕高度
     * @date 创建时间:2016/7/12
     * @author Chuck
     **/
    public int getScreenHeight() {

        if (SCREEN_HEIGHT <= 0) {
            SCREEN_HEIGHT = SharedPreferencesUtil.getInstance(ourInstance).getInt(Constants.SCREEN_HEIGHT, 1280);
        }
        return SCREEN_HEIGHT;
    }

    public static void cleanIsShowMainPop() {
        AppLoader.isShowMainPop = new boolean[]{false, false, false, false};
    }

    /**
     * @param :[]
     * @return type:int
     * @method name:getScreenWidth
     * @des:获取屏幕宽度
     * @date 创建时间:2016/7/12
     * @author Chuck
     **/
    public int getScreenWidth() {

        if (SCREEN_WIDTH <= 0) {
            SCREEN_WIDTH = SharedPreferencesUtil.getInstance(ourInstance).getInt(Constants.SCREEN_WIDTH, 720);
        }
        return SCREEN_WIDTH;
    }

    /**
     * @param :[]
     * @return type:int
     * @method name:getStateBarHeight
     * @des:获取顶部状态栏的高度
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    public static int getStateBarHeight() {

        if (STATUS_BAR_HEIGHT == 0) {
            STATUS_BAR_HEIGHT = SharedPreferencesUtil.getInstance(ourInstance).getInt(SharedPreferencesUtil.STATEBARHEIGHT, 70);
        }
        return STATUS_BAR_HEIGHT;
        //return 0;
    }

    /**
     * @param :[]
     * @return type:boolean
     * @method name:isAppFront
     * @des:app此时是否有界面处在前台
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    public boolean isAppFront() {
        Set set = mActivityStack.entrySet();
        for (Iterator iter = set.iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            ActivityLifecycleStatus value = (ActivityLifecycleStatus) entry.getValue();
            if (value != null) {
                if (value == ActivityLifecycleStatus.RESUMED) {//有界面处于RESUMED
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param :[]
     * @return type:java.lang.String
     * @method name:getFrontActivityClassName
     * @des:获取最前台那个activity的类名(如果在前台)
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    public String getFrontActivityClassName() {

        Set set = mActivityStack.entrySet();
        for (Iterator iter = set.iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            ActivityLifecycleStatus value = (ActivityLifecycleStatus) entry.getValue();
            if (value != null) {
                if (value == ActivityLifecycleStatus.RESUMED) {//有界面处于Resumed
                    return (String) entry.getKey();
                }
            }
        }
        return null;
    }


    private class ActivityLifecycleCallbacksImpl implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LogUtil.e("AppLoader", "onActivityCreated:" + activity.getClass().getName());
            mActivityStack.put(activity.getClass().getName(), ActivityLifecycleStatus.CREATED);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            LogUtil.e("AppLoader", "onActivityStarted:" + activity.getClass().getName());
            mActivityStack.put(activity.getClass().getName(), ActivityLifecycleStatus.STARTED);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            LogUtil.e("AppLoader", "onActivityResumed:" + activity.getClass().getName());
            mActivityStack.put(activity.getClass().getName(), ActivityLifecycleStatus.RESUMED);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            LogUtil.e("AppLoader", "onActivityPaused:" + activity.getClass().getName());
            mActivityStack.put(activity.getClass().getName(), ActivityLifecycleStatus.PAUSED);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            LogUtil.e("AppLoader", "onActivityStopped:" + activity.getClass().getName());
            mActivityStack.put(activity.getClass().getName(), ActivityLifecycleStatus.STOPPED);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            LogUtil.e("AppLoader", "onActivityDestroyed:" + activity.getClass().getName());
            try {
                mActivityStack.remove(activity.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /***
     * GreenDao相关
     */
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    public static SQLiteDatabase db;
    public static final String DB_NAME = BuildConfig.DB_NAME;//gradle里面配置数据库名,编译时动态生成


    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            CustomDbUpdateHelper helper = new CustomDbUpdateHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }


    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }


    public static SQLiteDatabase getSQLDatebase(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            db = daoMaster.getDatabase();
        }
        return db;
    }


    /**
     * @param :[]
     * @return type:java.util.HashMap<java.lang.String,java.lang.String>
     * @method name:getAutoLoginParameterMap
     * @des:为自动登录提供登录参数
     * @date 创建时间:2016/8/22
     * @author Chuck
     **/
    public HashMap<String, String> getAutoLoginParameterMap() {

        HashMap<String, String> map = new HashMap<>();

      /*  map.put("account", "-1");
        map.put("password", "-1");*/

        LoginModel loginModel = DBHelper.getInstance(this).getLoggedUser();
        if (loginModel != null) {
            //BlowfishCrypter.setKey(SharedPreferencesUtil.getInstance(this).getString(Constants.CRYPT_KEY, ""));

            map.put("account", loginModel.getAccAcount() + "");
            map.put("password", loginModel.getPassword() + "");
            map.put("md5", loginModel.getMd5() + "");



           /* map.put("lgIp", NetworkUtil.getIpAddr(this));
            map.put("lgType", "1");
            map.put("lgAddress", SharedPreferencesUtil.getInstance(this).getString(Constants.LOCATION_ADDRESS,""));

            //信鸽推送的token
            map.put("tokenType", "1");
            map.put("tokenValue", ""+ SharedPreferencesUtil.getInstance(this).getXgToken());*/
        }

        //map.put("account","15262592514");
        //map.put("password","123456");

        return map;
    }


    /*****
     * 环信
     */
    // login user name
    public final String PREF_USERNAME = "username";

    /**
     * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
     */
    public static String currentUserNick = "";

   /* @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/


    /*******************************************************************************************************
     * Dex分包
     ******************************************************************************************************/
    /***分包的逻辑***/

    //public static final String KEY_DEX2_SHA1 = "dex2-SHA1-Digest";//不用这个,直接用版本名
    public static final String KEY_DEX2_SHA1 = BuildConfig.APPLICATION_ID + BuildConfig.VERSION_NAME;//不用这个,直接用版本名
    public static final String VALUE = "VALUE";//非空即可
    private static final long SHORT_WAINTTING_TIME = 10 * 1000;
    private static final long LONG_WAINTTING_TIME = 30 * 1000;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("loadDex", "=====================================App attachBaseContext ");

        if (!quickStart() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//>=5.0的系统默认对dex进行oat优化
            if (needWait(base)) {//dex还没有被执行dexopt
                waitForDexopt(base);
            }
            //MultiDex.install(this);
        } else {
            return;
        }

        return;
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:onCreate
     * @des:第一次执行完了attachBaseContext就会立马执行oncreate.当另外的进程system.exit后 回到这个进程时也会调用oncreate
     * @date 创建时间:2016/1/23
     * @author Chuck
     **/
    @Override
    public void onCreate() {

        Log.d("loadDex", "=====================================App onCreate ");

        try {

            if (quickStart()) {
                return;
            }

            Log.d("loadDex", "准备调用父类的onCreate,初始化各种SDK");
            super.onCreate();
            ourInstance = this;
            mActivityLifecycleCallbacksImpl = new ActivityLifecycleCallbacksImpl();
            this.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacksImpl);

            /***极光**/

            // 极光推送
            //JPushInterface.setDebugMode(com.qmzhhchsh.yp.BuildConfig.LOG_SWITCH);    // 设置开启日志,发布时请关闭日志
            //JPushInterface.init(this);

            DBHelper.getInstance(this);
            //init demo helper
            //DemoHelper.getInstance().init(this);
            //wechat
            //initWx();
            //qq
            //initQQ();
            //utilsCode
            initUtilsCode();

            //日志开关:
            com.qdong.communal.library.util.LogUtil.LOG = BuildConfig.LOG_SWITCH;


            /*****关闭友盟默认的统计设置************************************/


            /********
             *
             * 如果是发布正式版的,则强制把api的host改为正式版
             * ******/
            if (!BuildConfig.DEBUG) {
                com.qdong.communal.library.util.Constants.SERVER_URL = com.qdong.communal.library.util.Constants.SERVER_URL_PRODUCTION;

            }


            //针对动态改了域名的情况(这段代码,上线时要注释掉):
            String domain = SharedPreferencesUtil.getInstance(this).getDomain(com.qdong.communal.library.util.Constants.SERVER_URL);
            LogUtil.i(TAG, "DOMAIN:" + domain);
            if (!TextUtils.isEmpty(domain)) {
                com.qdong.communal.library.util.Constants.SERVER_URL = domain;
            }


            //腾讯X5 webView
            //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
            QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
                @Override
                public void onViewInitFinished(boolean arg0) {
                    // TODO Auto-generated method stub
                    //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                    Log.d("app", " onViewInitFinished is " + arg0);
                }

                @Override
                public void onCoreInitFinished() {
                    // TODO Auto-generated method stub
                }


            };
            //x5内核初始化接口
            QbSdk.initX5Environment(getApplicationContext(), cb);


            // 捕获未知异常
            CrashHandler.getInstance().init();

            //从sd卡读取配置文件
            //initConfig();

            //定时拉取考试数据
            syncExamData();

            //定时拉取学生数据
            syncStudentData();

        } catch (Exception e) {
            LogUtil.e("JPush", "准备调用父类的onCreate,初始化各种SDK");
        }


    }


    private void initUtilsCode() {
        Utils.init(this);
    }

    public boolean quickStart() {
        if (getCurProcessName(this).contains("mini")) {
            Log.d("loadDex", ":mini.loadingdex start!");
            return true;
        }
        return false;
    }

    //neead wait for dexopt ?
    private boolean needWait(Context context) {
        String flag = get2thDexSHA1();//这个flag就是一个常量字符串,但是key是版本名,所以高版本的还是要去loading dex的
        Log.d("loadDex", "dex2-sha1 " + flag);
        SharedPreferences sp = context.getSharedPreferences(
                PackageUtil.getPackageInfo(context).versionName, MODE_MULTI_PROCESS);
        String saveValue = sp.getString(KEY_DEX2_SHA1, "");

        Log.d("loadDex", "=================================saveValue:" + saveValue);

        if (TextUtils.isEmpty(flag) || TextUtils.isEmpty(saveValue)) {
            return true;
        }
        Log.d("loadDex", "=================================needWait=(!flag.equals(saveValue)):" + !flag.equals(saveValue));
        return !flag.equals(saveValue);//只要非空,必然是equals的
    }
    /* *//**
     * 这个方法可以去掉,只需要再弄一个当前版本的偏好key就行,因为你发版本,里面有几个dex是可以知道的,所以,可以通过版本名来作为key,就行
     * Get classes.dex file signature
     * @param context
     * @return
     *//*
    private String get2thDexSHA1(Context context) {
        ApplicationInfo ai = context.getApplicationInfo();
        String source = ai.sourceDir;
        try {
            JarFile jar = new JarFile(source);
            Manifest mf = jar.getManifest();
            Map<String, Attributes> map = mf.getEntries();
            Attributes a = map.get("classes2.dex");
            Attributes b = map.get("classes3.dex");
            if(a!=null){
                Log.e("loadDex","classes2.dex:"+a.getValue("SHA1-Digest"));//这里不能这么写,因为某些机型,他的key不是SHA1-Digest,比如htc的one x.key是"SHA1-256-Digest"
            }
            if(b!=null){
                Log.e("loadDex","classes3.dex:"+b.getValue("SHA1-Digest"));
            }

            return a.getValue("SHA1-Digest");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }*/

    /**
     * 这个方法可以去掉,只需要再弄一个当前版本的偏好key就行,因为你发版本,里面有几个dex是可以知道的,所以,可以通过版本名来作为key,就行
     * Get classes.dex file signature
     *
     * @return
     */
    private String get2thDexSHA1() {
        return VALUE;
    }

    // optDex finish
    public void installFinish(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                PackageUtil.getPackageInfo(context).versionName, MODE_MULTI_PROCESS);
        sp.edit().putString(KEY_DEX2_SHA1, get2thDexSHA1()).commit();
    }


    public static String getCurProcessName(Context context) {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    /**
     * @param :[base]
     * @return type:void
     * @method name:waitForDexopt
     * @des:如果dexopt没有执行 则跳转到另外一个进程的LoadResActivity去在子线程里执行dexopt.原来的进程会被挂起, 就不怕ANR了
     * @date 创建时间:2016/1/23
     * @author Chuck
     **/
    public void waitForDexopt(Context base) {


        Intent intent = new Intent();
        ComponentName componentName = new
                ComponentName(BuildConfig.APPLICATION_ID, LoadResActivity.class.getName());
        intent.setComponent(componentName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        base.startActivity(intent);

        long startWait = System.currentTimeMillis();
        long waitTime = SHORT_WAINTTING_TIME;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            waitTime = LONG_WAINTTING_TIME;//实测发现某些场景下有些2.3版本有可能10s都不能完成optdex
        }
        while (needWait(base)) {//此进程已经被挂起了,是不怕触发ANR的,故可以写不确定的循环,直到超时
            try {
                long nowWait = System.currentTimeMillis() - startWait;
                Log.d("loadDex", "wait ms :" + nowWait);
                if (nowWait >= waitTime) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onCreate();
                        }
                    });

                    return;
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public String getMac() {



        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    public static String getMac(Context context) {

        String mac = null;

        if (mac == null) {
            try {
                NetworkInterface networkInterface = NetworkInterface.getByName("wlan0");
                byte[] addrByte = networkInterface.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                for (byte b : addrByte) {
                    sb.append(String.format("%02X:", b));
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                mac = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                WifiManager wifiM = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                try {
                    WifiInfo wifiI = wifiM.getConnectionInfo();
                    mac = wifiI.getMacAddress();
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                    mac = "02:00:00:00:00:00";
                }
            }
        }
        return mac;
    }

    public static HashMap<String, String> getCommentMap() {
        return mCommentMap;
    }

    public static void addComment(String key, String value) {
        mCommentMap.put(key, value);
    }

    public static void cleanComment() {
        mCommentMap.clear();
        mCommentMap = new HashMap<>();
    }




    private Subscription mSubscriptionSyncData;
    private Subscription mSubscriptionSyncStudentData;

    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//2022-10-27 10:25:45


    /**
     * @method name:同步服务器数据，先调用接口，拉取考试信息，存入sqlite
     * @des:
     * @param :
     * @return type:
     * @date 创建时间:2022/10/26
     * @author Chuck
     **/

    protected void syncExamData() {
        String TAG="syncExamData";
    }


    /**
     * @method name:同步服务器数据，先调用接口，拉取学生照片数据。
     *              拉取学生照片后，对比本地数据，增量写入到数据库并调用特征值api
     * @des:
     * @param :
     * @return type:
     * @date 创建时间:2022/10/26
     * @author Chuck
     **/
    long timeBefore=System.currentTimeMillis();
    protected void syncStudentData() {
        String TAG="syncStudentData";
    }




}
