package com.ilinklink.tg.mvp;

import android.app.Activity;
import android.content.Intent;

import com.ilinklink.tg.base.AbstractObserver;
import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.interfaces.GetVerifyCodeCallback;
import com.ilinklink.tg.mvp.login.LoginActivity;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.Tools;
import com.qdong.communal.library.module.network.LinkLinkApi;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RxHelper;
import com.spc.pose.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * BaseModel
 * mvp 过于繁琐,直接把M放进P里(P继承M,以获得网络请求的相关功能),加快效率.后续如果不想使用retroFit,则修改此基类
 * Created By:Chuck
 * Des:
 * on 2018/12/7 11:48
 */
public class BaseModel {

    //存储链
    protected ArrayList<Subscription> mSubscriptions=new ArrayList<>();


    /**接口api**/
    protected LinkLinkApi mApi;

    //上下文
    protected Activity mActivity;



    /**
     * @method name:executeTaskAutoRetry
     * 发起网络请求,这个方法会自动处理登录token过期的异常:
     * 如果token过期了,会自动使用缓存的用户名密码登录一次,然后自动重试1次
     * @param :[observable, observer]
     * @return type:void
     * @date 创建时间:2017/5/13
     * @author Chuck
     **/
    public void executeTaskAutoRetry(final Observable<LinkLinkNetInfo> observable, final Observer<LinkLinkNetInfo> observer){


        Observer<LinkLinkNetInfo> ob=new AbstractObserver() {


            @Override
            public void onCompleted() {
                observer.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i("BaseModel","父类处理异常:"+e.getMessage());

                if(RxHelper.THROWABLE_MESSAGE_AUTO_LOGIN_FAILED.equals(e.getMessage())){//自动登陆失败,跳登录界面
                    if(mActivity!=null){
                        //mActivity.sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    }
                }
                else {
                    observer.onError(e);
                }
            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(isNeedLogin(linkLinkNetInfo.getErrorCode())){
                    if(mActivity!=null){
                        //mActivity.sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    }
                }
                else if(RxHelper.ERROR_SQUEEZE_OUT.equals(linkLinkNetInfo.getErrorCode())){//被挤下线
                    Tools.quitApp(mActivity);
                }
                else{
                    observer.onNext(linkLinkNetInfo);
                }
            }
        };

        if(mActivity!=null){
            mSubscriptions.add(RxHelper.getInstance(mActivity).executeTaskAutoRetry(observable,mApi,ob, AppLoader.getInstance().getAutoLoginParameterMap()));
        }

    }

    /**
     * @method name:executeGetVerifyCodeTaskAndAutoRetry
     * 获取验证码的任务
     * 发起网络请求,这个方法会自动处理登录token过期的异常:
     * 如果token过期了,会自动使用缓存的用户名密码登录一次,然后自动重试1次
     * @param :[observable, observer]
     * @return type:void
     * @date 创建时间:2017/5/13
     * @author Chuck
     **/
    public void executeGetVerifyCodeTaskAndAutoRetry(String mobilePhone, final GetVerifyCodeCallback<LinkLinkNetInfo> getVerifyCodeCallback){


        Observer<LinkLinkNetInfo> ob=new AbstractObserver() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i("BaseModel","父类处理异常:"+e.getMessage());

                if(RxHelper.THROWABLE_MESSAGE_AUTO_LOGIN_FAILED.equals(e.getMessage())){//自动登陆失败,跳登录界面
                    if(mActivity!=null){
                        //mActivity.sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    }
                }
                else {
                    getVerifyCodeCallback.getVerifyCodeFail(AppLoader.getInstance().getString(R.string.get_code_error));
                }
            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(isNeedLogin(linkLinkNetInfo.getErrorCode())){
                    if(mActivity!=null){
                        //mActivity.sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    }
                }
                else if(RxHelper.ERROR_SQUEEZE_OUT.equals(linkLinkNetInfo.getErrorCode())){//被挤下线
                    Tools.quitApp(mActivity);
                }
                else{
                    getVerifyCodeCallback.getVerifyCodeSuccess(linkLinkNetInfo);
                }
            }
        };

        Map<String,Object> params = new HashMap<>();
        params.put("mobilePhone",mobilePhone);
        Observable observable = mApi.sendSmsCode(params);

        if(mActivity!=null){
            mSubscriptions.add(RxHelper.getInstance(mActivity).executeTaskAutoRetry(observable,mApi,ob, AppLoader.getInstance().getAutoLoginParameterMap()));
        }

    }

}
