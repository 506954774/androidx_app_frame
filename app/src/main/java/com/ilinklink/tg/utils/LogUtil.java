package com.ilinklink.tg.utils;

import android.util.Log;


import com.ilinklink.app.fw.BuildConfig;

import java.text.MessageFormat;


/**
 * LogUtil
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/7  15:28
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class LogUtil {

    private  static  final boolean LOG= BuildConfig.DEBUG;//gradle里配置

    public static void i(String tag,Object object){
        if(LOG){
            Log.i(tag,object==null?"null":object.toString());
        }
    }
    public static void e(String tag,Object object){
        if(LOG){
            Log.e(tag,object==null?"null":object.toString());
        }
    }

    public static void d(String tag,Object object){
        if(LOG){
            Log.d(tag,object==null?"null":object.toString());
        }
    }
    public static void w(String tag,Object object){
        if(LOG){
            Log.w(tag,object==null?"null":object.toString());
        }
    }
    public static void i(String tag,String messageFormat,Object ... object){
        if(LOG){
            Log.i(tag,object==null?"null":MessageFormat.format(messageFormat,object));
        }
    }
    public static void e(String tag,String messageFormat,Object ... object){
        if(LOG){
            Log.e(tag,object==null?"null":MessageFormat.format(messageFormat,object));
        }
    }

    public static void d(String tag,String messageFormat,Object ... object){
        if(LOG){
            Log.d(tag,object==null?"null":MessageFormat.format(messageFormat,object));
        }
    }
    public static void w(String tag,String messageFormat,Object ... object){
        if(LOG){
            Log.w(tag,object==null?"null":MessageFormat.format(messageFormat,object));
        }
    }

}
