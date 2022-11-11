package com.ilinklink.tg.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ilinklink.tg.utils.Json;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.ToastHelper;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.ilinklink.app.fw.R;


import java.util.Map;

import androidx.databinding.ViewDataBinding;
import rx.Observer;

/**
 * BasePayAct
 * 支付,父类
 * Created By:Chuck
 * Des:
 * on 2018/9/25 18:05
 * * Copyright : 全民智慧城市 版权所有
 */
public  class BasePayAct<T extends ViewDataBinding> extends BaseActivity<T> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //处理支付结果
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(PAY_RESULT);
        registerReceiver(receiver, filter);
    }

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {

                default:
                    break;
            }
        }
    };


    protected Observer<LinkLinkNetInfo> mAliPayPrepay = new Observer<LinkLinkNetInfo>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            LogUtil.i(TAG,"E:"+e.getMessage());

            ToastHelper.showCustomMessage(getString(R.string.pay_failed));
        }

        @Override
        public void onNext(final LinkLinkNetInfo LinkLinkNetInfo) {
            new Thread() {

                @Override
                public void run() {
                    try {

                        LogUtil.i(TAG,"LinkLinkNetInfo:"+LinkLinkNetInfo);

                          /*  //签名,从服务器获取
                            String result = new PayTask(this).pay(response.getKey());

                            //调用支付宝sdk的api
                            AliPayUtil.aliSerResult(this, handler, result);*/



                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updatePayState(-1);
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }.start();

        }
    };

    protected Observer<LinkLinkNetInfo> mWechatPrepay = new Observer<LinkLinkNetInfo>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            LogUtil.i(TAG,"E:"+e.getMessage());

            ToastHelper.showCustomMessage(getString(R.string.pay_failed));

        }

        @Override
        public void onNext(LinkLinkNetInfo LinkLinkNetInfo) {
            try {


            } catch (Exception e) {
                LogUtil.e(TAG,"异常:"+e.getMessage());
                updatePayState(-1);
                e.printStackTrace();
            }
        }
    };


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            updatePayState(msg.what);
        }
    };


    /**
     * 更新支付状态
     *
     * @param state
     */
    public void updatePayState(int state) {
        switch (state) {
            case -1: // 支付失败
            case -3:
            case -4:
                ToastHelper.showCustomMessage(getString(R.string.pay_failed));
                payOnFailed();
                break;
            case -2://支付取消
                ToastHelper.showCustomMessage(getString(R.string.pay_canceled));
                payOnCancle();
                break;
            case 0: // 支付成功
                ToastHelper.showCustomMessage( getString(R.string.pay_success));
                payOnSucess();
                break;
            case 1://账单确认
                ToastHelper.showCustomMessage( getString(R.string.pay_info_confirm));
                break;
            case 2://微信版本过低
                ToastHelper.showCustomMessage( getString(R.string.pay_wechat_client_version_too_low));
                break;
        }


    }




    //广播接受结果,微信,支付宝
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i("BasePay","onReceive,action:"+intent.getAction());
            if (intent.getAction().equals(PAY_RESULT)) {
                updatePayState(intent.getIntExtra(PAY_ERROR_CODE, -1));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    public static final int PAY_WAY_WECHAT=0;
    public static final int PAY_WAY_ALI=1;
    private static final String PAY_RESULT = "result";
    private static final String PAY_ERROR_CODE = "errCode";

    private MyReceiver receiver;


    //子类可重写以实现自己的业务,父类已经toast过结果了
    protected void payOnSucess(){

    }
    protected void payOnFailed(){

    }
    protected void payOnCancle(){

    }


}
