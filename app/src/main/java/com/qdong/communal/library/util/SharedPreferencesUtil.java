package com.qdong.communal.library.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ilinklink.app.fw.BuildConfig;


public class SharedPreferencesUtil {
    public static final String IMEI = "IMEI_";
    public static final String SOUND = "SOUND";
    public static final String ALARM_SENSIVITY_ = "ALARM_SENSIVITY_";
    public static SharedPreferencesUtil INSTANCE;
    private static SharedPreferences mPrefer;	//SharedPreferences对象
    private static final String APP_NAME= BuildConfig.FILE_ROOT_NAME;	//保存数据的文件名
    public static final String FIRST_USE="first_use";	//第一次使用APP
    public static final String STATEBARHEIGHT="stateBarHeight";	//通知栏的高度
    public static final String CLIENT_KEY="CLIENT_KEY";
    public static final String ACCESS_TOKEN="access_Token";
    public static final String REFRESH_TOKEN="refresh_Token";
    public static final String DOMAIN="DOMAIN";
    public static final String IMAGE_SIZE_UPLOAD_LIMIT_M="IMAGE_SIZE_UPLOAD_LIMIT_M";
    public static final String BIG_IMAGE_SIZE_UPLOAD_LIMIT_M="BIG_IMAGE_SIZE_UPLOAD_LIMIT_M";



    public static SharedPreferencesUtil getInstance(Context context){
        if(INSTANCE==null){
            return new SharedPreferencesUtil(context);
        }
        return INSTANCE;
    }

    private SharedPreferencesUtil(Context context) {
        init(context);
    }

    /**初始化SharedPreferences*/
    public void init(Context context){
        //实例化SharedPreferences
        mPrefer = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 返回的是true，表示传入的字符串是否包含知道的char值序列，
     * @param key
     * @return
     */
    public boolean contains(String key){
        return mPrefer.contains(key);
    }


    public boolean isDeviceAlarm(String imei){
        return getBoolean(IMEI +imei,true);
    }
    public void setDeviceAlarm(String imei,boolean status){
       putBoolean(IMEI+imei,status);
    }

    public boolean isAlarmSound(String userId){
        return getBoolean(SOUND +userId,true);
    }
    public void setAlarmSound(String userId,boolean status){
       putBoolean(SOUND+userId,status);
    }

    public String getAlarmSensivity(String userId){
        return getString(ALARM_SENSIVITY_ +userId,"高");
    }
    public void setAlarmSensivity(String userId,String status){
       putString(ALARM_SENSIVITY_+userId,status);
    }


    /**
     * 保存sessionId
     * @param value
     */
    public void putSessionId(String value){
        SharedPreferences.Editor edit = mPrefer.edit();	//实例化SharedPreferences.Editor对象
        edit.putString(Constants.SESSION_ID, value==null?"":value);	//保存键值
        edit.commit();	//提交数据
    }

    /**
     * 取得sessionId
     * @param value
     * @return
     */
    public String getSessionId( String value){
        return mPrefer.getString(Constants.SESSION_ID, value);
    }


    /**
     * @method name:putDomain
     * @des:
     * @param :[value]
     * @return type:void
     * @date 创建时间:2019/3/26
     * @author Chuck
     **/
    public void putDomain(String value){
        SharedPreferences.Editor edit = mPrefer.edit();
        edit.putString(DOMAIN, value==null?"":value);
        edit.commit();
    }

    /**
     * @method name:getDomain
     * @des:
     * @param :[value]
     * @return type:java.lang.String
     * @date 创建时间:2019/3/26
     * @author Chuck
     **/
    public String getDomain( String value){
        return mPrefer.getString(DOMAIN, value);
    }

    /**
     * @method name:setGuideActivityShowed
     * @des:设置已经展示过了引导页面
     * @param :[]
     * @return type:void
     * @date 创建时间:2016/8/27
     * @author Chuck
     **/
    public void setGuideActivityShowed(){
        SharedPreferences.Editor edit = mPrefer.edit();	//实例化SharedPreferences.Editor对象
        edit.putBoolean(Constants.HAS_SHOWED_GUID_ACTIVITY,true);	//保存键值
        edit.commit();	//提交数据
    }


    /**
     * @method name:isGuideActvityShowed
     * @des:获取是否已经展示过引导界面
     * @param :[]
     * @return type:boolean
     * @date 创建时间:2016/8/27
     * @author Chuck
     **/
    public boolean isGuideActvityShowed(){
        return mPrefer.getBoolean(Constants.HAS_SHOWED_GUID_ACTIVITY, false);
    }

    /**
     * 保存字符串类型的数据
     * @param key
     * @param value
     */
    public void putString(String key, String value){
        SharedPreferences.Editor edit = mPrefer.edit();	//实例化SharedPreferences.Editor对象
        edit.putString(key, value==null?"":value);	//保存键值
        edit.commit();	//提交数据
    }

    /**
     * 取得字符类型的数据
     * @param key
     * @param value
     * @return
     */
    public String getString(String key, String value){
        return mPrefer.getString(key, value);
    }




    /**
     * 保存整型类型的数据
     * @param key
     * @param value
     */
    public void putInt(String key, int value){
        SharedPreferences.Editor edit = mPrefer.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    /**
     * 获取整型类型的数据
     * @param key
     * @param value
     * @return
     */
    public int getInt(String key, int value){
        return mPrefer.getInt(key, value);
    }

    /**
     * 保存布尔类型的数据
     * @param key
     * @param value
     */
    public void putBoolean(String key, boolean value) {
        mPrefer.edit().putBoolean(key, value).commit();
    }

    /**
     * @param key
     * @param /value
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {
        return mPrefer.getBoolean(key, defValue);
    }

    /**
     * 保存long型的数据
     * @param key
     * @param value
     */
    public void putLong(String key, long value){
        mPrefer.edit().putLong(key, value).commit();
    }

    /**
     * 取得long型的数据
     * @param key
     * @param value
     * @return
     */
    public long getLong(String key, long value){
        return mPrefer.getLong(key, value);
    }

    public void removeKey(String key) {
        mPrefer.edit().remove(key).commit();
    }


    public static final String XG_TOKEN="XG_TOKEN";
    public void saveXgToken(String token){
        SharedPreferences.Editor edit = mPrefer.edit();	//实例化SharedPreferences.Editor对象
        edit.putString(XG_TOKEN, token==null?"":token);	//保存键值
        edit.commit();	//提交数据
    }
    public String getXgToken(){
        return mPrefer.getString(XG_TOKEN, "");
    }
}
