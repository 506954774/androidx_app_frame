package com.ilinklink.tg.base;

import android.text.TextUtils;

import com.qdong.communal.library.module.network.LinkLinkNetInfo;

import rx.Observer;

/**
 * AbstractObserver
 * Created By:Chuck
 * Des:
 * on 2018/9/28 17:45
 * * Copyright : 全民智慧城市 版权所有
 */
public abstract class AbstractObserver implements Observer<LinkLinkNetInfo> {


    /**
     * @method name:isNeedLogin
     * @des:是否需要重新登录
     * @param :[errorCode]
     * @return type:boolean
     * @date 创建时间:2018/12/5
     * @author Chuck
     **/
    protected boolean isNeedLogin(String errorCode){

        if(TextUtils.isEmpty(errorCode)){
            return false;
        }
        switch (errorCode){
            case "000001":
                return true;
            case "000004":
                return true;
            case "-000004":
                return true;
            case "-000001":
                return true;
        }
        return false;
    }

}
