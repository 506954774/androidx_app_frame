package com.ilinklink.tg.utils;

import android.text.TextUtils;

import java.util.UUID;

/**
 * ObjectUtil
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2020/8/14  15:35
 * Copyright : 2014-2018 深圳令令科技有限公司-版权所有
 **/
public class ObjectUtil {
    public static Object getObjectWithDefault(Object s1,Object defaultObject){
        return s1==null?defaultObject:s1;//空则按返回默认，否则返回s1
    }

    public static String getStringWithDefault(String s1,String defaultString){
        return TextUtils.isEmpty(s1)?defaultString:s1;//空则按返回默认，否则返回s1
    }

    public static String genUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
