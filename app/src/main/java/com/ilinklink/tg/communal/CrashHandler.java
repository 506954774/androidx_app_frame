package com.ilinklink.tg.communal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.ilinklink.tg.utils.Constants;
import com.ilinklink.tg.utils.LogUtil;
import com.qdong.communal.library.util.BitmapUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.spc.pose.demo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";


    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * CrashHandler实例
     */
    private static CrashHandler sInstance;

    /**
     * 用来存储设备信息和异常信息
     */
    private LinkedHashMap<String, String> infoMap = new LinkedHashMap<>();

    /**
     * 用于格式化日期,作为日志文件名的一部分
     */
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());// Locale.CHINA

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    private Context getApplicationContext() {
        return AppLoader.getInstance();
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (sInstance == null) {
            synchronized (CrashHandler.class) {
                if (sInstance == null) {
                    sInstance = new CrashHandler();
                }
            }
        }

        return sInstance;
    }

    /**
     * 初始化
     */
    public void init() {
        // 获取系统默认的UncaughtException处理器
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Log.e(TAG, "uncaughtException: "+ex.getMessage());


        ex.printStackTrace();

        boolean handled = handleException(ex);

        try {
            Thread.sleep(3000L);
            Log.e(TAG, "Thread.sleep(3000L);");

            //android.os.Process.killProcess(android.os.Process.myPid());

            System.exit(2);


        } catch (Exception e) {
            Log.e(TAG, "打断出现错误: ", e);
        }

        //mDefaultHandler.uncaughtException(thread, ex);

        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 自定义错误处理, 收集错误信息 发送错误报告等操作均在此完成.
     *
     * @return [true] 用户自己处理了该异常信息 [false]系统处理
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                LogUtil.i(TAG,"CRASHED");
                SharedPreferencesUtil.getInstance(getApplicationContext()).putString(Constants.INITENT_KEY_LAST_CRASH_TIME,""+System.currentTimeMillis());
                Looper.prepare();
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.unknown_exception), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        // 收集设备参数信息
        collectDeviceInfo();

        // 保存日志文件
        try {
            saveCrashInfo2File(ex);


            //关闭所有界面
            //AppLoader.getInstance().sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));
        } catch (IOException e) {
            e.printStackTrace();
        }



        //System.exit(1);

        return true;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo() {
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infoMap.put("VersionName", versionName);
                infoMap.put("VersionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "收集 程序包信息时 出错.", e);
        }

        String tempFieldResult;
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);

                Object obj = field.get(null);
                if (obj instanceof Object[]) {
                    tempFieldResult = Arrays.toString((String[]) obj);
                } else {
                    tempFieldResult = obj.toString();
                }

                infoMap.put(field.getName(), tempFieldResult);
            } catch (Exception e) {
                Log.e(TAG, "收集 程序崩溃信息时 出错.", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) throws IOException {
        String info = "";
        for (Map.Entry<String, String> entry : infoMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            info += (key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = "\n===========\n" + writer.toString();
        info += result;

        String time = formatter.format(new Date());
        String fileName = "crash (" + time + ").txt";

        String path = BitmapUtil.getLogSDcard() + File.separator + "CrashLog" + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(path + fileName);
        fos.write(info.getBytes());
        fos.close();
        writer.close();

        return fileName;
    }
}
