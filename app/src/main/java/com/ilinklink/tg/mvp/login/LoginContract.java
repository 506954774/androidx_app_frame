package com.ilinklink.tg.mvp.login;

import com.ilinklink.tg.mvp.BaseView;


/**
 * LoginContract
 * 用接口定义出P和V各自要实现的方法
 * 本项目,登录分3种,账户登录,微信登录,微博登录
 * Created By:Chuck
 * Des:
 * on 2018/12/7 14:08
 */
public interface LoginContract {


    /**
     * LoginPresenter
     * 账户登录
     * 责任人:  Chuck
     * 修改人： Chuck
     * 创建/修改时间: 2018/12/7  14:12
     * Copyright : 全民智慧城市  版权所有
     **/
    public interface LoginPresenter {
        void login(String account, String pwd);//登录
        void pollingTask(String uuid);//扫码轮询
        void checkUserInfo();
    }


    /**
     * LoginView
     * 登录界面需要实现的接口,注意,这个要继承BaseView
     * 责任人:  Chuck
     * 修改人： Chuck
     * 创建/修改时间: 2018/12/7  14:14
     * Copyright : 2014-2017 深圳令令科技有限公司-版权所有
     **/
    public interface LoginView extends BaseView {

        void loginSuccess();//登录成功,本地登录
        void loginFailed(String msg);
        void jumpNextActivity(int type);//1,主界面,2健康信息资料
    }


}
