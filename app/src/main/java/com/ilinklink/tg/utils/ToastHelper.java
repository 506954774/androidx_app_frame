package com.ilinklink.tg.utils;

import android.content.res.Resources;
import android.view.Gravity;
import android.widget.Toast;

import com.ilinklink.tg.communal.AppLoader;
import com.qdong.communal.library.util.ToastUtil;

/**
 * ToastHelper
 * Created By:Chuck
 * Des:
 * on 2018/9/12 17:45
 */
public class ToastHelper {

    private static final boolean USE_SYSTEM_TOAST = true;//使用系统的toast
    /**
     * 如果是MIUI系统,调用普通的Toast
     */
    public static boolean isMiui = RomCheckUtil.isMiui();

    /**
     * 显示自定义的Toast消息
     * @param textResId  消息id
     */
    public static void showCustomMessage( int textResId){
        try {

            if(USE_SYSTEM_TOAST){
                Toast.makeText(AppLoader.getInstance(),AppLoader.getInstance().getString(textResId),Toast.LENGTH_SHORT).show();
                return;
            }

            if(isMiui){
                Toast.makeText(AppLoader.getInstance(),AppLoader.getInstance().getString(textResId),Toast.LENGTH_SHORT).show();
            }else{
                ToastUtil.showCustomMessage(AppLoader.getInstance(),textResId);
            }

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void showCustomMessage(String message){
        try {

            if(USE_SYSTEM_TOAST){
                Toast.makeText(AppLoader.getInstance(),message,Toast.LENGTH_SHORT).show();
                return;
            }


            if(isMiui){
                Toast.makeText(AppLoader.getInstance(),message,Toast.LENGTH_SHORT).show();
            }else{
                ToastUtil.showCustomMessage(AppLoader.getInstance(),message);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void showCustomMessage(Object message){
        try {

            if(USE_SYSTEM_TOAST){
                Toast.makeText(AppLoader.getInstance(),message.toString(),Toast.LENGTH_SHORT).show();
                return;
            }

            if(isMiui){
                Toast.makeText(AppLoader.getInstance(),message.toString(),Toast.LENGTH_SHORT).show();
            }else{
                ToastUtil.showCustomMessage(AppLoader.getInstance(),message);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void showCustomMessageAtCenter(String message){
        try {

            Toast toast = Toast.makeText(AppLoader.getInstance(),message,Toast.LENGTH_SHORT);
            if(USE_SYSTEM_TOAST){
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                return;
            }

            if(isMiui){
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else{
                ToastUtil.showCustomMessage(AppLoader.getInstance(),message);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
