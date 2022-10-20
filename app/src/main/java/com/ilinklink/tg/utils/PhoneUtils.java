package com.ilinklink.tg.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ZhangXiWei on 2016/11/9.
 * 判断手机号格式的工具类
 */

public class PhoneUtils {
    public static boolean isMobile(String mobiles) {
        Pattern p = Pattern.compile("^(13[0-9]|14[579]|15[0-35-9]|17[6-8]|18[0-9]|19[0-9])[0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return m.matches();
        }
    }
}
