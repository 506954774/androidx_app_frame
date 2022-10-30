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
import android.view.View;

import com.blankj.utilcode.util.Utils;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.ilinklink.greendao.DaoMaster;
import com.ilinklink.greendao.DaoSession;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.LoginModel;

import com.ilinklink.greendao.StudentExam;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.entity.ExamInfoResponse;
import com.ilinklink.tg.entity.PersionImageRespons;
import com.ilinklink.tg.entity.SysConfig;
import com.ilinklink.tg.entity.ZtsbExamListData;
import com.ilinklink.tg.enums.ActivityLifecycleStatus;
import com.ilinklink.tg.green_dao.CustomDbUpdateHelper;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.exam.BasePoseActivity2;
import com.ilinklink.tg.mvp.initfacefeatrue.InitFaceFeatrueAct3;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
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
import com.qdong.communal.library.module.network.RxHelper;
import com.qdong.communal.library.util.FileUtils;
import com.qdong.communal.library.util.JsonUtil;
import com.qdong.communal.library.util.OkHttpUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;

import com.spc.pose.demo.BuildConfig;
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

import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassConfig;
import mcv.facepass.types.FacePassModel;
import mcv.facepass.types.FacePassPose;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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


            // 初始化人脸sdk
            initFacePassSDK();
            initFaceHandler();

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


        Action1<? super Long> observer = new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                try {

                    if(mApi==null){
                        mApi = RetrofitAPIManager.provideClientApi(getApplicationContext());
                    }

                    //调用接口
                    mApi.getExamInfoList()
                            .subscribeOn(Schedulers.io())
                            //写入SQLite数据库
                            .map(new Func1<LinkLinkNetInfo, LinkLinkNetInfo>() {
                                @Override
                                public LinkLinkNetInfo call(LinkLinkNetInfo linkLinkNetInfo) {

                                    LogUtil.i(TAG,"================getExamInfo,call,线程id:{0}",Thread.currentThread().getId());

                                    LinkLinkNetInfo result=new LinkLinkNetInfo();
                                    result.setCode(LinkLinkNetInfo.SUCESS_CODE);

                                    ArrayList<ExamInfoResponse> examInfoResponses =null;

                                    try {
                                        examInfoResponses = Json.toList(linkLinkNetInfo.getData(), ExamInfoResponse.class);
                                        LogUtil.i(TAG,"================examInfoResponses:{0}",examInfoResponses);

                                    } catch (JSONException e) {

                                    }

                                    LogUtil.i(TAG,"examInfoResponses==null?:"+examInfoResponses==null);


                                    if(  CollectionUtils.isNullOrEmpty(examInfoResponses )){


                                        result.setCode(LinkLinkNetInfo.FAIL_CODE);
                                        result.setMessage("暂无考试信息");
                                        LogUtil.i(TAG,"暂无考试信息" );

                                        return result;
                                    }
                                    else {

                                        //考试信息入库
                                        ExamInfoResponse examInfoResponse = examInfoResponses.get(0);

                                        ExamRecord examRecord=new ExamRecord();
                                        examRecord.setExamRecordId(String.valueOf(examInfoResponse.getExId()));
                                        examRecord.setExamUUID(String.valueOf(examInfoResponse.getExId()));
                                        examRecord.setName(examInfoResponse.getExName());
                                        examRecord.setExamTime(examInfoResponse.getExTime());

                                        String json= Json.toJson(examInfoResponse.getSubjects());
                                        //使用预留字段，存储此次考试的科目集合json串
                                        examRecord.setReservedColumn(json);

                                        if(!CollectionUtils.isNullOrEmpty(examInfoResponse.getPersons())){
                                           ArrayList<String> stuIds=new ArrayList<>();
                                           for (ExamInfoResponse.PersonsDTO dto:examInfoResponse.getPersons()){
                                                stuIds.add(dto.getEhpId()+"-"+dto.getPeId());
                                            }
                                            //使用预留字段2来存储此次参加考试的学生
                                            examRecord.setReservedColumn2(Joiner.on(",").join(stuIds));

                                        }

                                        /**
                                         * 此次考试的数据
                                         */
                                        List<ExamRecord> oldExamRecordList = DBHelper.getInstance(getApplicationContext()).getExamRecordList();
                                        Log.i(TAG,"oldExamRecordList:"+oldExamRecordList);

                                        if(CollectionUtils.isNullOrEmpty(oldExamRecordList)){
                                            DBHelper.getInstance(getApplicationContext()).saveExamRecord(examRecord);
                                            LogUtil.i(TAG,"插入一条考试数据：" +examRecord);
                                        }
                                        else {

                                            ExamRecord oldExamInfo = oldExamRecordList.get(0);

                                           //对比id，不一致则删除
                                           if(!String.valueOf(examInfoResponse.getExId()).equals(oldExamInfo.getExamUUID())){
                                               LogUtil.i(TAG,"对比id，不一致");
                                               DBHelper.getInstance(getApplicationContext()).saveExamRecord(examRecord);
                                           }
                                           //id一致则更新
                                           else {
                                               //此id是数据库框架自动生成的id
                                               examRecord.setId(oldExamInfo.getId());
                                               DBHelper.getInstance(getApplicationContext()).saveExamRecord(examRecord);

                                               LogUtil.i(TAG,"对比id，id一致则更新：" +examRecord);
                                           }
                                        }



                                    }

                                    return result;
                                }
                            })

                            .observeOn(Schedulers.io())
                            .subscribe(new Observer<LinkLinkNetInfo>() {
                                @Override
                                public void onCompleted() {
                                    LogUtil.e(TAG,"================getExamInfoAndSave,onCompleted()");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    LogUtil.e(TAG,"================getExamInfoAndSave,onError:"+ e.getMessage());
                                }

                                @Override
                                public void onNext(LinkLinkNetInfo result) {
                                    LogUtil.i(TAG,"================getExamInfoAndSave,Observer,onNext,线程id:{0}",Thread.currentThread().getId());

                                    LogUtil.i(TAG,"================getExamInfoAndSave,Observer,onNext,result:{0}",result);



                                }
                            });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mSubscriptionSyncData = Observable.interval(1, 60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())//指定观察者的执行线程,最终来到主线程执行
                .subscribe(observer);//订阅

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


        Action1<? super Long> observer = new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                try {

                    if(mApi==null){
                        mApi = RetrofitAPIManager.provideClientApi(getApplicationContext());
                    }

                    //调用接口
                    mApi.getStuInfoList()
                            .subscribeOn(Schedulers.io())

                            .map(new Func1<LinkLinkNetInfo, LinkLinkNetInfo>() {
                                @Override
                                public LinkLinkNetInfo call(LinkLinkNetInfo linkLinkNetInfo) {

                                    while (FacePassHandler.isAvailable()==false||mFacePassHandler==null){
                                        LogUtil.i(TAG,"FacePassHandler.isAvailable()==false，while循环将线程阻塞" );
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if(!FacePassHandler.isAvailable()){
                                        LogUtil.i(TAG,"FacePassHandler.isAvailable() ==false " );
                                        return null;
                                    }

                                    if(mFacePassHandler==null){
                                        LogUtil.i(TAG,"mFacePassHandler==null" );
                                        return null;
                                    }

                                    timeBefore=System.currentTimeMillis();

                                    LogUtil.i(TAG,"================getStuInfoList,call,线程id:{0}",Thread.currentThread().getId());

                                    LinkLinkNetInfo result=new LinkLinkNetInfo();
                                    result.setCode(LinkLinkNetInfo.SUCESS_CODE);

                                    ArrayList<PersionImageRespons> persionImageRespons =null;

                                    try {
                                         persionImageRespons = Json.toList(linkLinkNetInfo.getData(), PersionImageRespons.class);
                                         //LogUtil.i(TAG,"================getStuInfoList:{0}",persionImageRespons);

                                    } catch (JSONException e) {

                                    }

                                    LogUtil.i(TAG,"persionImageRespons==null?:"+persionImageRespons==null);



                                    if(  CollectionUtils.isNullOrEmpty(persionImageRespons )){
                                        result.setCode(LinkLinkNetInfo.FAIL_CODE);
                                        result.setMessage("暂无考生信息");

                                        return result;
                                    }
                                    else {

                                        //对比数据数据，
                                        for( PersionImageRespons person :persionImageRespons){

                                            if(person.getPeId()<13){
                                                //continue;
                                            }

                                            StudentInfo old = DBHelper.getInstance(getApplicationContext()).getStudentInfo(person.getPeId()+"");
                                            if(old==null){
                                                LogUtil.i(TAG,"sqlite中没有此id的考生，PeId:"+person.getPeId());
                                                indertOrUpdateStudentInfo(person);
                                            }
                                            else {
                                                boolean versionTheSame=false;
                                                boolean sdCardImageExsit=false;
                                                boolean hasFaceToken=!TextUtils.isEmpty(old.getFaceToken());

                                                if(person.getPeUtime()==null){
                                                    versionTheSame=true;
                                                }
                                                else {
                                                    try {
                                                        LogUtil.i(TAG,"person.getPeUtime():"+person.getPeUtime());
                                                        versionTheSame=old.getUpdateTime().equals(simpleDateFormat.parse(person.getPeUtime()).getTime());
                                                    } catch (ParseException e) {
                                                    }
                                                }

                                                String oldImagePath= com.qdong.communal.library.util.Constants.FACE_IMAGES_PATH+ File.separator+person.getPeId()+".jpg";
                                                File oldFile=new File( oldImagePath);
                                                sdCardImageExsit = oldFile.exists();

                                                LogUtil.i(TAG,"================versionTheSame:{0},sdCardImageExsit:{1},hasFaceToken:{2}",versionTheSame,sdCardImageExsit,hasFaceToken);

                                                //如果版本一致，且文件存在，token也存在，则跳出循环
                                                if(versionTheSame&&sdCardImageExsit&&hasFaceToken){
                                                    continue;
                                                }
                                                else {
                                                    //先remove文件
                                                    if(sdCardImageExsit){
                                                        oldFile.deleteOnExit();
                                                    }

                                                    if(!TextUtils.isEmpty(old.getFaceToken())){
                                                        try {
                                                            mFacePassHandler.unBindGroup(group_name,old.getFaceToken().getBytes());
                                                        } catch (FacePassException e) {
                                                            LogUtil.i(TAG,"解绑人脸token失败" );
                                                        }

                                                    }


                                                    indertOrUpdateStudentInfo(person);

                                                }

                                            }

                                        }
                                    }

                                    return result;
                                }
                            })



                            .observeOn(Schedulers.io())
                            .subscribe(new Observer<LinkLinkNetInfo>() {
                                @Override
                                public void onCompleted() {
                                    LogUtil.e(TAG,"================getStuInfoList,onCompleted()");

                                    LogUtil.i(TAG,"================getStuInfoList,总耗时:{0}",(System.currentTimeMillis()-timeBefore)+"毫秒");

                                }

                                @Override
                                public void onError(Throwable e) {
                                    LogUtil.e(TAG,"================getStuInfoList,onError:"+ e.getMessage());
                                }

                                @Override
                                public void onNext(LinkLinkNetInfo result) {
                                    LogUtil.i(TAG,"================getStuInfoList,Observer,onNext,线程id:{0}",Thread.currentThread().getId());
                                    LogUtil.i(TAG,"================getStuInfoList,Observer,onNext,result:{0}",result);

                                }
                            });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mSubscriptionSyncStudentData = Observable.interval(1, 300, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())//指定观察者的执行线程,最终来到主线程执行
                .subscribe(observer);//订阅

    }


    /**
     * 插入或者更新数据
     * @param person
     */
    private void indertOrUpdateStudentInfo(PersionImageRespons person ){
        String TAG="syncStudentData";


        //下载图片，构造考生数据，入库
        StudentInfo studentInfo=new StudentInfo();
        studentInfo.setStudentUUID(person.getPeId()+"");
        studentInfo.setName(person.getPeName());
        studentInfo.setGender(person.getPeSex()==1?"男":"女");
        studentInfo.setImageUrl(person.getPePhotoUrl());
        studentInfo.setBirthday(person.getPeBirthday());
        studentInfo.setDeptId("");
        studentInfo.setDeptName("");
        studentInfo.setImageSdCardPath("");
        studentInfo.setFaceToken("");
        studentInfo.setDesc(person.getPeNo());
        String ut=person.getPeCtime();
        if(!TextUtils.isEmpty(person.getPeUtime())){
            ut=person.getPeUtime();
        }
        person.setPeUtime(ut);
        try {
            studentInfo.setUpdateTime(simpleDateFormat.parse(person.getPeUtime()).getTime());
            LogUtil.i(TAG,"================更新时间:{0},{1}",studentInfo.getStudentUUID(),person.getPeUtime());
        } catch (Exception e) {

        }

        //下载图片，刷新人脸特征值
        insertAndDownloadImage(studentInfo);
    }

    /**
     * 先下载图片，然后添加到人脸特征值数据
     * @param stu
     */
    private void insertAndDownloadImage(StudentInfo stu){
        String TAG="syncStudentData";

        if(mFacePassHandler==null){
            LogUtil.e(TAG,"insertAndDownloadImage,人脸sdk没有初始化，mFacePassHandler==null");
            return;
        }
        if(TextUtils.isEmpty(stu.getImageUrl())){
            LogUtil.e(TAG,"insertAndDownloadImage,考生图片为空==null");
            return;
        }

        //删sd图片 ,考生的照片的绝对路径 /sdcard/Face-Import/123.jpg
        //删旧的学生信息（如果存在）
        //再插入一条sqlite
        //下载一张图片
        //加载特征值

        //如果旧的存在，先删除
        String oldImagePath= com.qdong.communal.library.util.Constants.FACE_IMAGES_PATH+ File.separator+stu.getStudentUUID()+".jpg";
        File oldFile=new File( oldImagePath);
        LogUtil.e(TAG,"insertAndDownloadImage,文件是否存在？"+oldFile.exists());
        if(oldFile.exists()){
            oldFile.delete();
        }

        // 1、获取导入目录 /sdcard/Face-Import
        File batchImportDir = FileUtils.getBatchImportDirectory();

        StudentInfo old = DBHelper.getInstance(getApplicationContext()).getStudentInfo(stu.getStudentUUID());

        if(old!=null){
            SdCardLogUtil.logInSdCard(TAG,"insertAndDownloadImage,================studentInfo!=null");

            stu.setId(old.getId());

            if(!batchImportDir.exists()){
                batchImportDir.mkdir();
            }
        }

        //考生图片地址
        String url = com.qdong.communal.library.util.Constants.FILE_URL + File.separator+stu.getImageUrl();

        LogUtil.e(TAG,"insertAndDownloadImage,================图片完整路径,url:"+url);

        String path = null;
        try {
            path = OkHttpUtil.downloadFile(url, oldImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(path)){

            LogUtil.e(TAG,"insertAndDownloadImage,================下载成功:"+path);

            boolean isSuccess = false;
            try {
                isSuccess = mFacePassHandler.createLocalGroup(group_name);
            } catch (FacePassException e) {
                e.printStackTrace();
            }


            stu.setImageDownloadTime(System.currentTimeMillis());


            String imagePath= oldImagePath;

            Log.i(TAG,"imagePath:"+imagePath );

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            try {
                FacePassAddFaceResult result = mFacePassHandler.addFace(bitmap);

                if (result != null) {
                    if (result.result == 0) {
                        //toast("add face successfully！");
                        Log.i(TAG,"add face successfully！,faceToken："+new String(result.faceToken));

                        byte[] faceToken =result.faceToken;
                        if (faceToken == null || faceToken.length == 0 || TextUtils.isEmpty(group_name)) {
                            Log.i(TAG,"bindGroup,params error！" );

                        }
                        try {
                            boolean b = mFacePassHandler.bindGroup(group_name, faceToken);
                            String bindGroupResult = b ? "success " : "failed";
                            Log.i(TAG,"bindGroupResult:  " + bindGroupResult);

                            if(b){
                                stu.setImageSdCardPath(path);
                                stu.setFaceToken(new String(faceToken));

                                Log.i(TAG,"===========================================绑定faceToken成功:  " + bindGroupResult);

                                //更新数据
                                DBHelper.getInstance(getApplicationContext()).saveStudentInfo(stu);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,"bindGroupResult,Exception:  " + e.getMessage());
                            SdCardLogUtil.logInSdCard(TAG,"bindGroupResult,===============Exception :"+e.getMessage());
                        }


                    } else if (result.result == 1) {
                        // toast("no face ！");
                        Log.i(TAG,"add face failed,no face ！" );

                    } else {
                        Log.i(TAG,"add face failed,quality problem！" );

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG,"bindGroupResult,FacePassException:"+e.getMessage() );
                SdCardLogUtil.logInSdCard(TAG,"bindGroupResult,===============Exception :"+e.getMessage());

            }

        }
        else {
            LogUtil.e(TAG,"insertAndDownloadImage,================下载图片失败 url:"+url);
        }
    }

    /* SDK 实例对象 */
    FacePassHandler mFacePassHandler;
    private static final String DEBUG_TAG = "FacePassDemo";
    // 需要客户根据自己需求配置
    private static final String authIP = "https://api-cn.faceplusplus.com";
    //private static final String apiKey = "fHrrO2VYQ8WQTujUuqsGixdBasnetD8J";
    private static final String apiKey = "7B18IrqUfpAdvmeP7MyhNOTEx4c8u0QT";
    //private static final String apiSecret = "9rwNIpyUclXsYRn3WRrzhKDVIl8MEb9O";
    private static final String apiSecret = "dO6TUdfpeRWnywYrrI6wcmaPDbdris9F";
    /* 根据需求配置单目 / 双目场景，默认单目 */
    private static FacePassCameraType CamType =  FacePassCameraType.FACEPASS_SINGLECAM;

    /* 人脸识别Group */
    private static final String group_name = "facepass";

    private boolean isLocalGroupExist = false;


    private  enum FacePassSDKMode {
        MODE_ONLINE,
        MODE_OFFLINE
    }

    private  enum FacePassCameraType{
        FACEPASS_SINGLECAM,
        FACEPASS_DUALCAM
    };


    private void initFacePassSDK() {
        Context mContext = getApplicationContext();
        FacePassHandler.initSDK(mContext);
        FacePassHandler.authPrepare(mContext);
        FacePassHandler.getAuth(authIP, apiKey, apiSecret, true);
        Log.d("FacePassDemo", FacePassHandler.getVersion());

    }

    private void initFaceHandler() {
        String TAG="initFaceHandler";

        new Thread() {
            @Override
            public void run() {
                while (true ) {
                    while (FacePassHandler.isAvailable()) {
                        Log.d(DEBUG_TAG, "start to build FacePassHandler");
                        FacePassConfig config;
                        try {
                            /* 填入所需要的模型配置 */
                            config = new FacePassConfig();
                            config.poseBlurModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.pose_blur.arm.190630.bin");

                            config.livenessModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.CPU.rgb.G.bin");
                            if (CamType ==  FacePassCameraType.FACEPASS_DUALCAM) {
                                config.rgbIrLivenessModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.CPU.rgbir.G.bin");
                            }

                            config.searchModel = FacePassModel.initModel(getApplicationContext().getAssets(), "feat2.arm.K.v1.0_1core.bin");

                            config.detectModel = FacePassModel.initModel(getApplicationContext().getAssets(), "detector.arm.G.bin");
                            config.detectRectModel = FacePassModel.initModel(getApplicationContext().getAssets(), "detector_rect.arm.G.bin");
                            config.landmarkModel = FacePassModel.initModel(getApplicationContext().getAssets(), "pf.lmk.arm.E.bin");

                            config.rcAttributeModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.RC.arm.G.bin");
                            config.occlusionFilterModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.occlusion.arm.20201209.bin");
                            //config.smileModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.RC.arm.200815.bin");
                            //config.ageGenderModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.age_gender.arm.190630.bin");

                            /* 送识别阈值参数 */
                            config.rcAttributeAndOcclusionMode = 1;
                            config.searchThreshold = 65f;
                            config.livenessThreshold = 55f;
                            if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
                                config.livenessEnabled = false;
                                config.rgbIrLivenessEnabled = true;
                            } else {
                                config.livenessEnabled = true;
                                config.rgbIrLivenessEnabled = false;
                            }


                            config.poseThreshold = new FacePassPose(35f, 35f, 35f);
                            config.blurThreshold = 0.8f;
                            config.lowBrightnessThreshold = 30f;
                            config.highBrightnessThreshold = 210f;
                            config.brightnessSTDThreshold = 80f;
                            config.faceMinThreshold = 100;
                            config.retryCount = 10;
                            config.smileEnabled = false;
                            config.maxFaceEnabled = true;

                            config.fileRootPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

                            /* 创建SDK实例 */
                            mFacePassHandler = new FacePassHandler(config);

                            /* 入库阈值参数 */
                            FacePassConfig addFaceConfig = mFacePassHandler.getAddFaceConfig();
                            addFaceConfig.poseThreshold.pitch = 35f;
                            addFaceConfig.poseThreshold.roll = 35f;
                            addFaceConfig.poseThreshold.yaw = 35f;
                            addFaceConfig.blurThreshold = 0.7f;
                            addFaceConfig.lowBrightnessThreshold = 70f;
                            addFaceConfig.highBrightnessThreshold = 220f;
                            addFaceConfig.brightnessSTDThreshold = 60f;
                            addFaceConfig.faceMinThreshold = 100;
                            addFaceConfig.rcAttributeAndOcclusionMode = 2;
                            mFacePassHandler.setAddFaceConfig(addFaceConfig);

                            checkGroup();

                            //initFaceFeatrue();

                        } catch (FacePassException e) {
                            e.printStackTrace();
                            Log.d(DEBUG_TAG, "FacePassHandler is null");
                            return;
                        }
                        return;
                    }
                    try {
                        /* 如果SDK初始化未完成则需等待 */
                        sleep(500);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    private void checkGroup() {
        if (mFacePassHandler == null) {
            return;
        }
        try {
            String[] localGroups = mFacePassHandler.getLocalGroups();
            isLocalGroupExist = false;
            if (localGroups == null || localGroups.length == 0) {

                return;
            }
            for (String group : localGroups) {
                if (group_name.equals(group)) {
                    isLocalGroupExist = true;
                }
            }
            if (!isLocalGroupExist) {


            }
        } catch (FacePassException e) {
            e.printStackTrace();
        }
    }

    private void initFaceFeatrue(){
        String TAG="initFaceFeatrue";

        new Thread(){
            @Override
            public void run() {
                //创建group


                boolean isSuccess = false;
                String groupName="facepass";
                try {
                    isSuccess = mFacePassHandler.createLocalGroup(groupName);
                } catch (FacePassException e) {
                    e.printStackTrace();
                }

                if(isLocalGroupExist){
                    isSuccess=true;
                }

                Log.i(TAG,"createLocalGroup,isSuccess："+isSuccess);
                Log.i(TAG,"createLocalGroup, groupName："+groupName);

                //选取照片

                if(isSuccess){

                    //Constants.FACE_IMAGES_PATH
                    String [] imagesPaths={"40.jpg","41.jpg","42.jpg","45.jpg"};
                    String [] names={"李景亮","赵峰","杨晓云","刘东华"};
                    String [] sns={"HAM2205011","HAM2205025","HAM2205038","HAM2205058"};
                    String [] heads={
                            "https://www.baidu.com/link?url=Kg8cLPDuX5DN7pTQZPMan0AMVsgD_Mh8OHxFTXI2xLXcYM9Dx2gHymK-q4r9yfnh3fPtoC9wgU5aPB2ae6BBivSnr80h5KsOhbiAiq4qPv_z5C2nqjoYXIGQOvIFxMk4EyKTxcoGFCa31bLZe9ZX9e3f7QH24zOfNY5FFJ-RHgSQ3TfgUqgs8ZuB_DBbJ7KPILfN9OXHZmsVsKzQ9riGeSvphgYZ8pdu31zPT0bOaYDIvosnIk2052jqV4qLPpNhVwtOk3sjBPsJ1xLUa1kwzwITITkZXsGux9KnIHMqyrBa_GxRQFEJhtjRaaY7D2HkQfKm8TQ5-j3MyMimfQTPvu75AqOv3tiLGx0n5lUI9remmf0ZumcgndGn3PLVWLc8WI3BIWMSkhVe7V38U1AyZl_tJ_r3s_gCjRzRv7tFPqSp2ezI_cuxS_Eq_LBGBnvnPltivuJRmnwXF0zcmYoLif_ei8GsDX0jTmJvHmjt6Z0DI3M-HqiVCaTnFBmucbqgA9F7AJRq0lqPVHCi1OLVsso6ivA2rau2fhl1DT7jox1-pq_UITPPTfbI0KYMQkvsN-Wwc7f7o5YDcXHQRVn_O79C20hYI5_lS1ft59ckoB5K_GfEilCBSQipG9ecTTVnHDBBC_srGR-l2i0tAgxtBSLHP2enZ79tO8H1uhx6ibDLPeYq8yYCAU1Nk7zlNZEd6JZo5eZyDD2f98cJ9aHPhkDvoOuZjUcjZZQPR___nDWBM0bcS3rZtqqkPGtIa_Ccsj0VwIdPNwVzCKZXxMBFia&wd=&eqid=b273940500037240000000056352a9b2",

                            "https://www.baidu.com/link?url=BI9aolXHH3wwtJOP-5xxxOsIFo08U8_7bAjD4X3AW--IrMu3WSRBICWVlctRDbK7KnvfxU8zMSC9UKPIcXyW_v9UXDgERQKX3p23Hyswt9cADFyMoUDFLiP2frTJkV9BAVElLwUk5Miv-IklkQv8zNr3oiwQdtNgZ_63QdYY4Y7fQZ9VB-eNJbrBM4M_8W4mdpXxZiWuBUnk8J90tuG0r3GQhAx9yubSYp24XeAIgKv_M_ygerEh-hMF-wkypuufPaHL8-_PGYquRJrvHgkKnDOCM4b4OC-wfApps7pOuaWPvqf9HEr3RoYR0RHxNe-bOv6R5Eq2hRxfj1nBKEBsQq_QT2cqftK5fSM-w-ZVGh64IHiT8zpCg8u183VQOnxSpvDwA-kAqcj3s-4tZBCCf9QtCQXek3yX7D0HVU_hydgOCWq39Cm2XXHutz8N8ePGOSfTdWCgG_HQ7cK9wK2X2W_4xM3gY0pPDi2W1fLieFxCJXzJ2_NANdxiVqto1CO-hRqApjz5T5D6OsAobE40Fv8KwUz-cKQYXvUYkH6u_MWo5yYDxnLdfx7Ypo0cyhpf4qNMjs6xsNPhRU2FgO9E94aneoUJLuIYqzUdYyEKauQKfy0gKo5HVoW9k308S5E0vwuxJFW88sMbV6up78CtsuqduFowzS7ZxQuSRkYONElbomsXOUs1QyAbPUDfDX3VM_Roi-SYEgySOFKuovJsk6miAhr6hjuSjrELvxY3q0bC0nIkiSm1INLN-F8cMvDIsPhuLlWlQYshlCTn7jvdWq&wd=&eqid=b273940500037240000000056352a9b2",

                            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fq_70%2Cc_zoom%2Cw_640%2Fimages%2F20170929%2F09ed3516c62b4790a6ab30055cb1f570.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1668953810&t=d4f9d35fd50913abc36d6ab913ffbac6",

                            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20200417%2Fe9e2f2504e46461cbf8352810bef64ef.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1668953810&t=a3f0fa5bb0e7662a98854b952509892b"};

                    List<String> faceTokenList = new ArrayList<>();

                    try {
                        byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
                        if (faceTokens != null && faceTokens.length > 0) {
                            for (int j = 0; j < faceTokens.length; j++) {
                                if (faceTokens[j].length > 0) {
                                    faceTokenList.add(new String(faceTokens[j]));
                                }
                            }
                        }

                        Log.i(TAG,"此groupName既有的token列表，faceTokenList:"+faceTokenList );

                    } catch (FacePassException e) {
                        e.printStackTrace();
                    }

                    int successCount=0;
                    int failureCount=0;
                    // TODO: 2022/10/16 此处应该做判断，本地磁盘的图片文件要有token关联。 根据文件绝对路径拿到token，然后判断此token是否已经被绑定过
                    for (int i = 0; i < imagesPaths.length; i++) {

                        boolean bindBefore=false;
                        StudentInfo old = DBHelper.getInstance(getApplicationContext()).getStudentInfo(imagesPaths[i]);
                        if(old!=null){
                            if(old.getDesc()!=null){

                                if(!CollectionUtils.isNullOrEmpty(faceTokenList)){
                                    if(faceTokenList.contains(old.getFaceToken())){
                                        bindBefore=true;
                                        successCount++;
                                        Log.i(TAG,"此照片已经被绑定过,path："+imagesPaths[i]);
                                    }
                                }

                              /*  String[] split = old.getDesc().split(",");
                                //需要判断，因为下载完成之后，是没有token的。
                                if(split.length==2){
                                    String token=old.getDesc().split(",")[1];
                                    if(!CollectionUtils.isNullOrEmpty(faceTokenList)){
                                        if(faceTokenList.contains(token)){
                                            bindBefore=true;
                                            successCount++;
                                            Log.i(TAG,"此照片已经被绑定过,path："+imagesPaths[i]);
                                        }
                                    }
                                }*/
                            }
                        }

                        if(bindBefore){
                            Log.i(TAG,"此照片已经被绑定过,循环将 continue" );

                            //界面绘制进度



                            continue;
                        }

                        String imagePath= com.qdong.communal.library.util.Constants.FACE_IMAGES_PATH+ File.separator+imagesPaths[i];

                        Log.i(TAG,"imagePath:"+imagePath );

                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                        try {
                            FacePassAddFaceResult result = mFacePassHandler.addFace(bitmap);
                            Log.d("qujiaqi", "result:" + result
                                    + ",bl:" + result.blur
                                    + ",pp:" + result.pose.pitch
                                    + ",pr:" + result.pose.roll
                                    + ",py" + result.pose.yaw);
                            if (result != null) {
                                if (result.result == 0) {
                                    //toast("add face successfully！");
                                    Log.i(TAG,"add face successfully！,faceToken："+new String(result.faceToken));


                                    byte[] faceToken =result.faceToken;
                                    if (faceToken == null || faceToken.length == 0 || TextUtils.isEmpty(groupName)) {
                                        Log.i(TAG,"bindGroup,params error！" );
                                        failureCount++;

                                    }
                                    try {
                                        boolean b = mFacePassHandler.bindGroup(groupName, faceToken);
                                        String bindGroupResult = b ? "success " : "failed";
                                        Log.i(TAG,"bindGroupResult:  " + bindGroupResult);

                                        if(b){

                                            StudentInfo student=new StudentInfo();
                                            student.setStudentUUID(imagesPaths[i]);
                                            student.setImageSdCardPath(imagePath);
                                            student.setName(names[i]);
                                            student.setFaceToken(new String(faceToken));
                                            student.setImageUrl(heads[i]);
                                            student.setDesc(sns[i]);
                                            student.setUpdateTime(System.currentTimeMillis());
                                            student.setImageDownloadTime(System.currentTimeMillis());

                                            DBHelper.getInstance(getApplicationContext()).saveStudentInfo(student);

                                            successCount++;
                                            //界面绘制进度

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e(TAG,"bindGroupResult,Exception:  " + e.getMessage());
                                        failureCount++;
                                    }


                                } else if (result.result == 1) {
                                    // toast("no face ！");
                                    Log.i(TAG,"add face failed,no face ！" );
                                    failureCount++;


                                } else {
                                    Log.i(TAG,"add face failed,quality problem！" );
                                    failureCount++;

                                    //toast("quality problem！");

                                }
                            }
                        } catch (FacePassException e) {
                            e.printStackTrace();
                            Log.i(TAG,"bindGroupResult,FacePassException:"+e.getMessage() );

                        }
                    }


                }

            }
        }.start();
    }

    boolean looper=true;
    private void initConfig(){

        String TAG="initConfig";
        new Thread() {
            @Override
            public void run() {
                while (looper ) {
                    while (FacePassHandler.isAvailable()&&looper) {

                        try {
                            //SysConfig
                            //String json = Tools.readLocalJson(this, getQDongDir() + "cofig.json");

                            //   String sdCardRoot=Environment.getExternalStorageDirectory().getAbsolutePath();
                            //
                            //            String  jsonPath=Environment.getExternalStorageDirectory() + "/"  + "cofig.json";
//Environment.getExternalStorageDirectory()

                            String  jsonPath=null;

                            File sdRootFile = FileUtils.getSDRootFile();
                            File file = null;
                            if (sdRootFile != null && sdRootFile.exists()) {

                                com.qdong.communal.library.util.LogUtil.i(TAG,"jsonPath,===============getExternalStorageDirectory.getAbsolutePath,sdCardRoot:"+sdRootFile.getAbsolutePath());

                                SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============getExternalStorageDirectory.getAbsolutePath,sdCardRoot:"+sdRootFile.getAbsolutePath());


                                file = new File(sdRootFile, "config.json");
                                String configPath=Environment
                                        .getExternalStorageDirectory().getPath()
                                         + File.separator
                                        + "config.json";
                                com.qdong.communal.library.util.LogUtil.i(TAG,"jsonPath,===============configPath:"+configPath);

                                file = new File( configPath);
                                if (file.exists()) {
                                    SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============config.json文件存在,路径:"+file.getAbsolutePath() );

                                    jsonPath=file.getAbsolutePath();
                                }
                                else {
                                    SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============config.json文件不存在" );
                                    mApi = RetrofitAPIManager.provideClientApi(getApplicationContext());
                                    looper=false;

                                }
                            }

                            File jsonFile=new File(jsonPath);

                            com.qdong.communal.library.util.LogUtil.i(TAG,"jsonPath,===============jsonPath:"+jsonPath);
                            com.qdong.communal.library.util.LogUtil.i(TAG,"jsonPath,===============jsonFile.exsit:"+jsonFile.exists());

                            SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============jsonPath:"+jsonPath);
                            SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============jsonFile.exsit:"+jsonFile.exists());


                            //读取配置文件
                            String json = Tools.readLocalJson(getApplicationContext(), jsonPath);
                            if(!TextUtils.isEmpty(json)){
                                SysConfig config= JsonUtil.fromJson(json,SysConfig.class);

                                SdCardLogUtil.logInSdCard("initConfig","===============加载配置文件,config:"+config);

                                com.qdong.communal.library.util.LogUtil.i(TAG,"===============加载配置文件,config:"+config);


                                if(config!=null){
                                    com.qdong.communal.library.util.Constants.DEBUG_HOST=config.getHost();
                                    com.qdong.communal.library.util.Constants.PRODUCTION_HOST=config.getHost();
                                    com.qdong.communal.library.util.Constants.FILE_URL=config.getFileHost();

                                    com.qdong.communal.library.util.Constants.SERVER_URL = com.qdong.communal.library.util.Constants.DEBUG_HOST+com.qdong.communal.library.util.Constants.DEBUG_PORT;

                                    mApi = RetrofitAPIManager.provideClientApi(getApplicationContext());

                                    looper=false;

                                }
                            }
                            else {

                                SdCardLogUtil.logInSdCard("initConfig","============没有发现配置文件");

                                com.qdong.communal.library.util.LogUtil.i(TAG,"============没有发现配置文件");
                                looper=false;

                            }
                        } catch (Exception e) {

                            SdCardLogUtil.logInSdCard("initConfig","===============加载配置文件,失败:"+e.getMessage());

                            com.qdong.communal.library.util.LogUtil.i(TAG,"===============加载配置文件,失败:"+e.getMessage());
                        }

                    }
                }
            }
        }.start();



    }


    public FacePassHandler getmFacePassHandler() {
        return mFacePassHandler;
    }
}
