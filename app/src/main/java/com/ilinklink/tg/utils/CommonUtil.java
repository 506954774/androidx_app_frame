package com.ilinklink.tg.utils;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * CommonUtil
 * Created By:WuJH
 * Des:
 * on 2018/12/13 17:32
 */
public class CommonUtil {

    public static String[] s1 = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
    public static String[] s2 = { "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千" };


    /**
     * 字符串MD5加密
     * @param string
     * @return
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        /*if (gps || network) {
            return true;
        }*/
        if (gps) {
            return true;
        }
        return false;
    }

    /**
     * 保留钱小数点后2位
     * @param money
     * @return
     */
    public static String moneyFilter(String money){

       if(money.contains(".")){
           String temp = money.substring(money.indexOf(".") + 1);
           if(temp.length() > 2){
               money = money.substring(0,money.indexOf(".") + 3);
           }else{
               if(temp.length() == 1){
                   money = money + "0";
               }
           }
       }
       return money;
    }

    /**
     * 保留小数点后1位
     * @param money
     * @return
     */
    public static String moneyFilterCopy(String money){

        if(money.contains(".")){
            String temp = money.substring(money.indexOf(".") + 1);
            if(temp.length() > 2){
                money = money.substring(0,money.indexOf(".") + 2);
            }else{
                if(temp.length() == 1){
                    money = money + "";
                }
            }
        }
        return money;
    }

    public static String no2Chinese(int no) {

        String string = no + "";
        String result = "";

        int n = string.length();
        for (int i = 0; i < n; i++) {

            int num = string.charAt(i) - '0';

            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                result += s1[num];
            }

        }
        return result;
    }

    /**
     * 跳转权限界面
     * @param activity
     */
    public static void jump2PermissionPager(Activity activity){

        PermissionPageUtil permissionPageUtil = new PermissionPageUtil(activity);
        permissionPageUtil.jumpPermissionPage();

    }
    /**
     * 跳转权限界面
     * @param activity
     */
    public static void jump2PermissionPager(Activity activity,int code){

        PermissionPageUtil permissionPageUtil = new PermissionPageUtil(activity);
        permissionPageUtil.jumpPermissionPage( code);

    }
}
