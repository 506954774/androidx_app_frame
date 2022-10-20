package com.ilinklink.tg.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * PackageUtil
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/1/23  10:07
 * Copyright : 2014-2015 深圳掌通宝科技有限公司-版权所有
 **/
public class PackageUtil {
    public static PackageInfo getPackageInfo(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("loadDex",e.getLocalizedMessage());
        }
        return  new PackageInfo();
    }
}
