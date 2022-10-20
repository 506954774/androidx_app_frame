package com.ilinklink.tg.utils;


import java.text.DecimalFormat;
import java.util.Calendar;


/**
 * 数据解析相关

 */
public class DataDecodeUtil {

    /**
     * 16进制累加和校验
     *
     * @param data 除去校验位的数据
     * @param sign 校验位的数据
     * @return
     */
    public static boolean isEffective(String data, String sign) {
        if (StringUtil.isEmpty(data) || StringUtil.isEmpty(sign))
            return false;

        boolean result=makeChecksum(data).toUpperCase().equals(sign);

        if(!result){
            LogUtil.e("DeviceBleManager","==========================校验失败!");
        }


        return result;
    }

    /**
     * 生成16进制累加和校验码
     *
     * @param data 除去校验位的数据
     * @return
     */
    public static String makeChecksum(String data) {
        if (StringUtil.isEmpty(data))
            return "";
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        //用256求余最大是255，即16进制的FF
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        //如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2)
            hex = "0" + hex;
        return hex;
    }

    /**
     * 获取内容数据
     *
     * @param readBody 读取到的内容
     * @return 需要的内容数据
     */
    public static String getBody(String readBody) {
        String body = readBody.substring(6, readBody.length() - 2);
        LogUtil.e("getBody", body);
        return body;
    }


    /**
     * 获取内容数据
     *
     * @param readBody 读取到的内容
     * @return 需要的内容数据
     */
    public static String getBody(String readBody,int paddingByteQuantity) {
        String body = readBody.substring(6, readBody.length() - (2+paddingByteQuantity*2));//把头, check和补的paddingByteQuantity个字节的"00"都去掉
        LogUtil.e("getBody", body);
        return body;
    }






    /**
     * 经纬度单独解析方式
     */
    public static double latLng(String body) {
        StringBuilder sb = new StringBuilder();
        int a = Integer.valueOf(body.substring(2, 4), 16);
        if (body.substring(0, 2).equals("FF"))  // 负数
            a = a - 256;
        sb.append(a).append(".");
        DecimalFormat format = new DecimalFormat("00");
        for (int i = 2; i < 5; i++) {
            String str = body.substring(i * 2, (i + 1) * 2);
            int b = Integer.valueOf(str, 16);
            sb.append(format.format(b));
        }
        String d = sb.toString();
        return Double.valueOf(d);
    }


    /**
     * 解析电池序列号
     *
     * @param body 需要解析的车体字符串
     * @return 根据规则解析好的对象
     */
    public static String decodeBatterySerialNumber(String body) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < body.length() / 2; i++) {
            int a = Integer.valueOf(body.substring(i * 2, (i + 1) * 2), 16);
            if (a == 0)
                break;
            char b = Character.valueOf((char) a);
            sb.append(b);
        }
        return sb.toString();
    }

    /**
     * APP时间转码
     *
     * @param time 设定需要转码时间
     * @return 转换为16进制的时间串
     */
    public static String encodeAppTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int year = Integer.valueOf(String.valueOf(c.get(Calendar.YEAR)).substring(2));
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        StringBuilder sb = new StringBuilder("AA070A");
        sb.append(integerToHexString(year)).append(integerToHexString(month));
        sb.append(integerToHexString(day)).append(integerToHexString(hour));
        sb.append(integerToHexString(minute)).append(integerToHexString(second));
        String str = sb.toString().toUpperCase();
        sb.append(makeChecksum(str)); // 添加校验码
        return sb.toString().toUpperCase();
    }

    /**
     * 测试方法APP时间转码0xffffff
     * 为了测试,获取新的激活时间
     */
    public static String encodeAppTimeTest() {


        StringBuilder sb = new StringBuilder("AA070A");

        sb.append(integerToHexString(255)).append(integerToHexString(255));
        sb.append(integerToHexString(255)).append(integerToHexString(255));
        sb.append(integerToHexString(255)).append(integerToHexString(255));

        String str = sb.toString().toUpperCase();
        sb.append(makeChecksum(str)); // 添加校验码

        String result=sb.toString().toUpperCase();

        LogUtil.e("encodeAppTimeTest","result"+result);

        return result;
    }

    /**
     * 把12位数字转换成6位BCD码
     */
    public static String stringToHexString(String str, int len){
        String result="";
        try {
            if(len<12){
                for (int i = 0; i<12-len; i++) {//
                    str+="0";
                }
            }

            for(int i=0;i<12;i+=2){
                int a=Integer.parseInt(str.substring(i,i+2),10);
                String hex=integerToHexString(a);
                result+=hex;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return result;
        }
    }




    /**
     * Integer转换为String，并自动添加O
     * 10进制转16进制
     *
     * @param num 数据
     * @return 转换成功数据
     */
    public static String integerToHexString(int num) {
        return num < 16 ? "0" + Integer.toHexString(num) : Integer.toHexString(num);
    }

    // 十六进制转二进制
    public static String HToB(String a) {
        String b = Integer.toBinaryString(Integer.valueOf(toD(a, 16)));
        return b;
    }


    // 二进制转十六进制
    public static String DToH(String a) {
        // 将二进制转为十进制再从十进制转为十六进制
        String b = Integer.toHexString(Integer.valueOf(toD(a, 2)));
        return b;
    }

    // 任意进制数转为十进制数
    public static String toD(String a, int b) {
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r = (int) (r + formatting(a.substring(i, i + 1))
                    * Math.pow(b, a.length() - i - 1));
        }
        return String.valueOf(r);
    }

    // 将十六进制中的字母转为对应的数字
    public static int formatting(String a) {
        int i = 0;
        for (int u = 0; u < 10; u++) {
            if (a.equals(String.valueOf(u))) {
                i = u;
            }
        }
        if (a.equals("A")) {
            i = 10;
        }
        if (a.equals("B")) {
            i = 11;
        }
        if (a.equals("C")) {
            i = 12;
        }
        if (a.equals("D")) {
            i = 13;
        }
        if (a.equals("E")) {
            i = 14;
        }
        if (a.equals("F")) {
            i = 15;
        }
        return i;
    }

    /**
     * APP时间转码
     *
     * @param time 设定需要转码时间
     * @return 转换为16进制的时间串
     */
    public static String setTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int year = Integer.valueOf(String.valueOf(c.get(Calendar.YEAR)).substring(2));
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        StringBuilder sb = new StringBuilder("AA040A");
        sb.append(integerToHexString(year)).append(integerToHexString(month));
        sb.append(integerToHexString(day)).append(integerToHexString(hour));
        sb.append(integerToHexString(minute)).append(integerToHexString(second));
        String str = sb.toString().toUpperCase();
        sb.append(makeChecksum(str)); // 添加校验码
        return sb.toString().toUpperCase();
    }


    

    
}
