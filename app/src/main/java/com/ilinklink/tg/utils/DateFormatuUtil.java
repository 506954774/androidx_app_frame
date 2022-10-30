package com.ilinklink.tg.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * FileName: DateFormatuUtil
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/9/29 18:30
 * <p>
 * Description:
 */
public class DateFormatuUtil {

    /**
     * 根据生日获取年龄
     *
     * @return 年龄
     */
    public static int getAge(String birthday) {
        if (TextUtils.isEmpty(birthday))
            return 0;
        int y = stringChangeInt(getDate("yyyy", Long.parseLong(birthday)));
        if (y <= 0)
            return 0;
        int age = stringChangeInt(getDate("yyyy")) - y;
        return age <= 0 ? 0 : age;
    }

    /**
     * 取得日期：年
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取指定格式的当前系统时间字符串
     *
     * @param fmt
     * @return
     */
    public static String getDate(String fmt) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(System.currentTimeMillis());
    }

    /**
     * 获取指定格式的当前系统时间字符串
     *
     * @param fmt
     * @return
     */
    public static String getDate(String fmt, long time) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(time);
    }

    /**
     * 获取指定格式的当前系统时间字符串
     *
     * @param fmt
     * @return
     */
    public static String formatDate(String fmt) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(System.currentTimeMillis());
    }

    /**
     * 从字符串转为int类型
     *
     * @param str
     * @return
     */
    public static int stringChangeInt(String str) {
        return Integer.parseInt(str);
    }

    /**
     * 秒转换为时间
     *
     * @param time 时间秒值
     * @return 转换好的字符串
     */
    public static String secsToTime(long time) {

        int day = (int) (time / (24 * 3600));
        int hour = (int) (time / 3600);
        int minute = (int) (time / 60);

        if (day > 0) {
            hour = hour - (day * 24);
            return day + "天" + hour + "小时" + minute + "分钟";
        } else if (hour > 0) {
            minute = minute - (hour * 60);
            return hour + "小时" + minute + "分钟";
        } else if (minute > 0) {
            return minute + "分钟";
        } else {
            return time + "秒";
        }


    }

    /**
     * 时间拼接
     *
     * @param num 数值
     * @return 格式化好的时间字符串
     */
    public static String formaTime(int num) {
        /** Bugfix-修改格式化方式-LHD-20160427-LHD-START */
        DecimalFormat format = new DecimalFormat("00");
        return format.format(num);
        /** Bugfix-修改格式化方式-LHD-20160427-LHD-END */
    }


    // 1971-01-01 08:00:00的时间戳
    private static final long TIME = 31536_000_000L;
    public static Integer getAge(Long birth) {
        if (birth == null) {
            return null;
        }

        Long now = System.currentTimeMillis();
        if (birth > now) {
            return 0;
        }

        if (birth > 0) {
            Double age = Math.ceil((now - birth) / TIME);
            return age.intValue();
        }
        return null;
    }

    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//2022-10-27 10:25:45

    public static Integer getAgeByBirthday(String birth) {
        Long time= null;
        try {
            time = simpleDateFormat.parse(birth).getTime();
        } catch (ParseException e) {
            return 20;
        }
        return getAge(time);
    }

}
