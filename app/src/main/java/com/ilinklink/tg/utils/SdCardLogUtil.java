package com.ilinklink.tg.utils;

import android.util.Log;

import com.qdong.communal.library.util.FileUtils;


/**
 * LogUtil
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/7  15:28
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class SdCardLogUtil {

    private  static  final boolean LOG= true;//

    public static void i(String tag,Object object){
        if(LOG){
            Log.i(tag,object==null?"null":object.toString());
            logInSdCard(tag,object==null?"null":"INFO:"+object.toString());
        }
    }
    public static void e(String tag,Object object){
        if(LOG){
            Log.e(tag,object==null?"null":object.toString());
            logInSdCard(tag,object==null?"null":"ERROR:"+object.toString());
        }
    }

    public static void d(String tag,Object object){
        if(LOG){
            Log.d(tag,object==null?"null":object.toString());
            logInSdCard(tag,object==null?"null":"DEBUG:"+object.toString());

        }
    }
    public static void w(String tag,Object object){
        if(LOG){
            Log.w(tag,object==null?"null":object.toString());
            logInSdCard(tag,object==null?"null":"WARNING:"+object.toString());

        }
    }

    /**
     * 往sd卡写日志
     * @param tag
     * @param message
     */
    public static void logInSdCard(String tag,String message){
        new Thread(){
            @Override
            public void run() {
                try {
                    FileUtils.outputLog(message);
                } catch (Exception e) {
                    Log.e(tag,e.getMessage());
                }
            }
        }.start();
    }
}
