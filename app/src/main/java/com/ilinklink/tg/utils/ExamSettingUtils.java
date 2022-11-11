package com.ilinklink.tg.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.qdong.communal.library.util.SharedPreferencesUtil;

import java.util.ArrayList;

/**
 * ActionRulesUtils
 * Created By:Chuck
 * Des:
 * on 2022/6/22 10:03
 */
public  class ExamSettingUtils {



    public static String getExamSetting(Activity activity,String key){
        return SharedPreferencesUtil.getInstance(activity).getString(key,"");
    }


    public static void setExamSetting(Activity activity,String key,String value){
        SharedPreferencesUtil.getInstance(activity).putString(key,value);
    }

}
