package com.ilinklink.tg.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.ilinklink.greendao.LoginModel;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.login.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * BhUtils
 * 获取用户基本信息
 * 责任人:  jibinghao
 * 修改人： jibinghao
 * 创建/修改时间时间: 2019/1/17 3:31 PM
 * Copyright : 全民智慧城市  版权所有
 **/

public class BhUtils {

    public static int getUid(Context context) {

        LoginModel loginModel = DBHelper.getInstance(context).getLoggedUser();
        int uid = -1;
        if (loginModel != null && !TextUtils.isEmpty(loginModel.getUid())) {
            try {
                return Integer.parseInt(loginModel.getUid());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return uid;
    }


    public static String formatPhoneNumber(String phoneNumber) {
        String msg = "";
        if (TextUtils.isEmpty(phoneNumber)) {

        } else {
            if (phoneNumber.length() == 11) {
                msg = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
            } else {
                msg = phoneNumber;
            }
        }
        return msg;
    }



    public static boolean isLogin(Activity activity) {
        if (DBHelper.getInstance(activity).getLoggedUser() == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isLogin(Activity activity, int requestCode) {
        if (DBHelper.getInstance(activity).getLoggedUser() == null) {
            //replaceFragment(createFragment(mFts[mSelectedIndex]));//还原原来的碎片
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivityForResult(intent, requestCode);
            return false;
        } else {
            return true;
        }
    }


    private static final int MIN_DELAY_TIME = 500;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime;

    public static boolean isFastClick() {


        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
            lastClickTime = currentClickTime;
        }
        return flag;
    }

    public static boolean isFastClickThreeSecond() {


        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= 3000) {
            flag = false;
            lastClickTime = currentClickTime;
        }
        return flag;
    }

    public static String getFormatTime(long second) {
        second /= 1000;
        long hour, minute;
        hour = second / 3600;
        minute = (second - hour * 3600) / 60;
        second = second - hour * 3600 - minute * 60;


        String timeFormat = formatZero(hour) + ":" + formatZero(minute) + ":" + formatZero(second);
        return timeFormat;
    }

    public static String formatZero(long msg) {
        //测试说这里小于10要加个0
        String strMsg = "";
        if (msg <= 0) {
            strMsg = "00";
        } else if (msg < 10) {
            strMsg = "0" + msg;
        } else {
            strMsg = String.valueOf(msg);
        }
        return strMsg;
    }

    /**
     * @param :[]
     * @return type:boolean
     * @method name:hasRecordPermission
     * @des:
     * @date 创建时间:2019/4/16
     * @author jibinghao
     **/
    public static boolean hasRecordPermission() {
        return true;
        //专门給锤子系统判断这个录音权限
//        if (!RomUtil.isSmartisan()) {
//            return true;
//        }
//        int minBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
//        int bufferSizeInBytes = 640;
//        byte[] audioData = new byte[bufferSizeInBytes];
//        int readSize = 0;
//        AudioRecord audioRecord = null;
//        try {
//            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 8000,
//                    AudioFormat.CHANNEL_IN_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
//            // 开始录音
//            audioRecord.startRecording();
//        } catch (Exception e) {
//            //可能情况一
//            if (audioRecord != null) {
//                audioRecord.release();
//                audioRecord = null;
//            }
//            return false;
//        }
//        // 检测是否在录音中,6.0以下会返回此状态
//        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
//            //可能情况二
//            if (audioRecord != null) {
//                audioRecord.stop();
//                audioRecord.release();
//                audioRecord = null;
//            }
//            return false;
//        } else {// 正在录音
//            readSize = audioRecord.read(audioData, 0, bufferSizeInBytes);
//            // 检测是否可以获取录音结果
//            if (readSize <= 0) {
//                //可能情况三
//                if (audioRecord != null) {
//                    audioRecord.stop();
//                    audioRecord.release();
//                    audioRecord = null;
//                }
//                return false;
//            } else {
//                //有权限，正常启动录音并有数据
//                if (audioRecord != null) {
//                    audioRecord.stop();
//                    audioRecord.release();
//                    audioRecord = null;
//                }
//                return true;
//            }
//        }
    }

    public static boolean isSend = false;


    /**
     * @param :[mEventID, createById, childId]
     * @return type:java.lang.String
     * @method name:searchKey
     * @des:
     * @date 创建时间:2019/4/16
     * @author jibinghao
     **/
    public static String searchKey(String type, String mEventID, String createById, String childId) {
        return type + "-" + mEventID + "-" + childId + "-" + createById;
    }


    /**
     * @param :[mEventID, createById, childId]
     * @return type:java.lang.String
     * @method name:searchKey
     * @des:
     * @date 创建时间:2019/4/16
     * @author jibinghao
     **/
    public static String searchKey(String mEventID, String createById, String childId) {
        return searchKey("dongbiji", mEventID, createById, childId);
    }

    public static int getTextViewLine(TextView tv, String content, int width) {
        /**
         * @method name:isExceedMaxLine
         * @des: 获取textview的行数
         * @param :[tv, content, width]
         * @return type:int
         * @date 创建时间:2019/4/20
         * @author jibinghao
         **/
        if (TextUtils.isEmpty(content)) {
            return 0;
        }
        StaticLayout staticLayout = new StaticLayout(content, tv.getPaint(), width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        return staticLayout.getLineCount();
    }


    public static final long PHOTO_MAX_SIZE = 500 * 1024;//500KB


    public static String getImgH5(String imgPath) {
        StringBuilder content = new StringBuilder();


//        content.append("<img src=\"").append(imgPath).append("\"/>");
        content.append("<img class=\"wscnph\" style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"").append(imgPath).append("\"/>");

        return content.toString();
    }

    public static String getTextH5(String content) {
//        String msg = "";
//        msg = "<p>" + content + "</p>";
        return content;
    }


    /**
     * 获取img标签中的src值
     *
     * @param content
     * @return
     */
    public static String getImgSrc(String content) {
        String str_src = null;
        //目前img标签标示有3种表达式
        //<img alt="" src="1.jpg"/>   <img alt="" src="1.jpg"></img>     <img alt="" src="1.jpg">
        //开始匹配content中的<img />标签
        Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher m_img = p_img.matcher(content);
        boolean result_img = m_img.find();
        if (result_img) {
            while (result_img) {
                //获取到匹配的<img />标签中的内容
                String str_img = m_img.group(2);

                //开始匹配<img />标签中的src
                Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher m_src = p_src.matcher(str_img);
                if (m_src.find()) {
                    str_src = m_src.group(3);
                }
                //结束匹配<img />标签中的src

                //匹配content中是否存在下一个<img />标签，有则继续以上步骤匹配<img />标签中的src
                result_img = m_img.find();
            }
        }
        return str_src;
    }

    /**
     * 关键字高亮显示
     *
     * @param target 需要高亮的关键字
     * @param text   需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     * SpannableStringBuilder textString = TextUtilTools.highlight(item.getItemName(), KnowledgeActivity.searchKey);
     * vHolder.tv_itemName_search.setText(textString);
     */
    public static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(Color.parseColor("#EE5C42"));// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 从html文本中提取图片地址，或者文本内容
     *
     * @param html       传入html文本
     * @param isGetImage true获取图片，false获取文本
     * @return
     */
    public static ArrayList<String> getTextFromHtml(String html, boolean isGetImage) {
        ArrayList<String> imageList = new ArrayList<>();
        ArrayList<String> textList = new ArrayList<>();
        //根据img标签分割出图片和字符串
        List<String> list = cutStringByImgTag(html);
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i);
            if (text.contains("<img") && text.contains("src=")) {
                //从img标签中获取图片地址
                String imagePath = getImgSrc(text);
                imageList.add(imagePath);
            } else {
                textList.add(text);
            }
        }
        //判断是获取图片还是文本
        if (isGetImage) {
            return imageList;
        } else {
            return textList;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeFormat(String msg) {
        try {
            if (TextUtils.isEmpty(msg)) {
                return "";
            }
            String content = TimeUtils.date2String(TimeUtils.string2Date(msg), new SimpleDateFormat("yyyy-MM-dd HH:mm"));
            return content;
        } catch (Exception e) {
            return msg;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeFormatYMD(String msg) {
        try {
            if (TextUtils.isEmpty(msg)) {
                return "";
            }
            String content = TimeUtils.date2String(TimeUtils.string2Date(msg), new SimpleDateFormat("yyyy-MM-dd"));
            return content;
        } catch (Exception e) {
            return msg;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getExperienceTimeFormat(String msg) {
        try {
            if (TextUtils.isEmpty(msg)) {
                return "";
            }
            String content = TimeUtils.date2String(TimeUtils.string2Date(msg), new SimpleDateFormat("yyyy.MM.dd HH:mm"));
            return content;
        } catch (Exception e) {
            return msg;
        }
    }

    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }


    public static String getLastDay(int year, int month) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        return sdf.format(cal.getTime());
    }

    /**
     * @param targetStr 要处理的字符串
     * @description 切割字符串，将文本和img标签碎片化，如"ab<img>cd"转换为"ab"、"<img>"、"cd"
     */
    public static List<String> cutStringByImgTag(String targetStr) {
        List<String> splitTextList = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<img.*?src=\\\"(.*?)\\\".*?>");
        Matcher matcher = pattern.matcher(targetStr);
        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                splitTextList.add(targetStr.substring(lastIndex, matcher.start()));
            }
            splitTextList.add(targetStr.substring(matcher.start(), matcher.end()));
            lastIndex = matcher.end();
        }
        if (lastIndex != targetStr.length()) {
            splitTextList.add(targetStr.substring(lastIndex, targetStr.length()));
        }
        return splitTextList;
    }


    static double proportion2 = 16.0f / 9.0f;
    static double proportion3 = 3.0f / 4.0f;
    static int screenWidth = ScreenUtils.getScreenWidth();
    static int screenHeight = ScreenUtils.getScreenHeight();

    public static int getBannerFinalHeight(int originWidth, int originHeight) {
        if (originHeight == 0 || originWidth == 0) {
            return screenWidth;
        }

        double proportion = 1.0f;
        proportion = (originWidth * 1.0f) / (originHeight * 1.0f);
        if (proportion > proportion2) {
            proportion = proportion2;
        } else if (originHeight > screenHeight) {
            proportion = proportion3;
        }
        int finalHeight = (int) (screenWidth / proportion);
        LogUtils.e("getBannerFinalHeight", finalHeight);
        return finalHeight;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
