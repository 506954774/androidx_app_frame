/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：gumengfu
 * 创 建 日 期：2014-7-21 下午3:57:08
 * 版 本 号：
 * 修 改 人：
 * 描 述：
 * <p>
 *
 * </p>
 ***********************************************************/
package com.qdong.communal.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;


/**
 * <p>
 * 	快速打印Toast工具类
 *
 *
 *
 * </p>
 * @author gumengfu
 * @date 2014-7-21
 * @version
 * @since
 */
public class ToastUtil {



	/**
	 * 显示自定义的Toast消息
	 * @param context
	 * @param textResId  消息id
	 */
	public static void showCustomMessage(Context context, int textResId){
		try {
			String msg=context.getResources().getString(textResId);
			Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
	}


	public static void showCustomMessage(Context context, String message){

		try {
			Toast.makeText(context,message,Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static void showCustomMessageShort(Context context, String message){

		try {
			Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void showCustomMessage(Context context, Object message){
		try {
			Toast.makeText(context,message==null?"null":message.toString(),Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}





}
