package com.ilinklink.tg.mvp;

import android.app.Activity;

/**
 * BaseView
 * mvp v的基础接口
 * Created By:Chuck
 * Des:
 * on 2018/12/7 11:33
 */
public interface BaseView {


    Activity getActivityContext();//当前上下文

    void showLoading(String msg);//显示loading

    void dismissLoading();//消除loading

    void showToast(String msg);//吐司提示

}
