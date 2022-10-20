package com.ilinklink.tg.utils;

import android.os.Environment;

import static com.qdong.communal.library.util.Constants.getQDongDir;

/**
 * FileName: Constants
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/8/10 18:42
 * <p>
 * Description:
 */
public class Constants {

    public static int DEFAULT_THRESHOLD_WIDTH = 4000;
    public static int DEFAULT_THRESHOLD_HEIGHT = 4000;

    public static String LOGIN_ERROR_CODE = "000003";

    public static  float HEIGHT=4.0F;//本来是4.0. 但是很多机型屏幕高度不够
    public static final float WIDTH=3F;
    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DEFAULT_CACHE_DIR = SDCARD_DIR + "/PLDroidPlayer";

    /** 获取sd卡tigan/saves目录 */
    public static final String QD_PERSONAL_DIR = getQDongDir() + "saves";

    public static final String SCREEN_HEIGHT = "SCREEN_HEIGHT";//屏幕高度
    public static final String SCREEN_WIDTH = "SCREEN_WIDTH";//屏幕宽度

    public static final String ACCOUNT = "ACCOUNT";//用户账号

    public static final String INITENT_KEY_LAST_CRASH_TIME = "INITENT_KEY_LAST_CRASH_TIME";//有些手机,崩溃后会直接再进一次上个界面.但是这样是不合理的(有些业务,界面会出问题),所以,处理方案:崩溃时记录一个时间,每个activity在onCreate时,判断是否刚刚崩溃过,如果是,则finish调,用户必须重新走一遍Launch界面
    /*******************************************************
     * 广播action
     */
    public static final String ACTION_FINISH_ALL = "ACTION_FINISH_ALL";//title
    public static final String ACTION_ALARM = "ACTION_ALARM";//title
    public static final String ACTION_NOTIFYCATION_CLICKED = "ACTION_NOTIFYCATION_CLICKED";//广播点击

    /********************************************************
     *  wifi状态
     */
    public static final String WIFI_STATE_CONNECT = "已连接";
    public static final String WIFI_STATE_ON_CONNECTING = "正在连接";
    public static final String WIFI_STATE_UNCONNECT = "未连接";
    public static final String WIFI_STATE_SAVED = "已保存";
}
