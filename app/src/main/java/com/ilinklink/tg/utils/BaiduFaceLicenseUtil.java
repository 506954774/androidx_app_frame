package com.ilinklink.tg.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BaiduFaceLicenseUtil {

    /**
     * 百度人脸license缓存,key:mac地址, value:百度的在线激活的key
     */
    private static HashMap<String,String> baiduLicenseKeyMaps=new HashMap<>();
    static {
        //chuck的华为pad
        baiduLicenseKeyMaps.put("22:B3:B1:EF:46:EE","KQXQ-58GQ-MXJC-RXMH");
        //学文的华为pad
        baiduLicenseKeyMaps.put("E2:5D:96:5B:42:7A","2XVH-6FJB-2FVD-28EE");
        //学文的华为pad
        baiduLicenseKeyMaps.put("E2:58:D2:0A:BD:40","2XVH-6FJB-2FVD-28EE");
        //品超测试的华为pad
        baiduLicenseKeyMaps.put("52:E9:30:25:4E:9E","DCRC-XAKK-KEUZ-4DCW");
    }


    /**
     * 根据mac地址查询百度的人脸license 序列号
     * @param context
     * @param mac
     * @return
     */
    public static String getBaiduFaceLicenseKey(Context context,String mac) {
        if(!TextUtils.isEmpty(com.qdong.communal.library.util.Constants.BAIDU_FACE_KEY)){
            return com.qdong.communal.library.util.Constants.BAIDU_FACE_KEY;
        }
        return baiduLicenseKeyMaps.get(mac);
    }



}