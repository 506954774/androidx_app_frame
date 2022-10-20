package com.ilinklink.tg.mvp.login;

import android.util.Log;

import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.entity.UserInfoBean;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.utils.Constants;
import com.ilinklink.tg.utils.Json;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.Tools;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.spc.pose.demo.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * LoginPresenter
 * 登录,业务调度者,泛型是登录界面接口,
 * Created By:Chuck
 * Des:
 * on 2018/12/7 14:16
 */
public class LoginPresenterImpl extends BasePresenter<LoginContract.LoginView> implements LoginContract.LoginPresenter{

    public LoginPresenterImpl(LoginContract.LoginView mView) {
        super(mView);
    }
    private Subscription subscription = null;
    @Override
    public void login(final String account,final String pwd) {


        Map<String, Object> map=new HashMap<>();
        map.put("account",account);
        map.put("password",pwd);

        executeTaskAutoRetry(mApi.userLogin(map)
                , new Observer<LinkLinkNetInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                         if(mView!=null){
                             mView.loginFailed(mView.getActivityContext().getString(R.string.login_failed));
                         }
                    }

                    @Override
                    public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                         if(linkLinkNetInfo.isSuccess()){

                             SharedPreferencesUtil.getInstance(mActivity).putString(SharedPreferencesUtil.ACCESS_TOKEN,linkLinkNetInfo.getData().getAsString());
                             SharedPreferencesUtil.getInstance(AppLoader.getInstance()).putString(Constants.ACCOUNT,account);
                             RetrofitAPIManager.ACCESSTOKEN=linkLinkNetInfo.getData().getAsString();//token
                             /*LoginModel login=new LoginModel();
                             login.setAccAcount(account);
                             login.setPassword(pwd);
                             DBHelper.getInstance(AppLoader.getInstance()).saveLoginBean(login);*/

                             mView.loginSuccess();
                         }
                         else{
                             mView.loginFailed(linkLinkNetInfo.getMessage());
                         }
                    }
        });

    }

    @Override
    public void pollingTask(String uuid) {
        subscription = Observable.interval(1,1, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<LinkLinkNetInfo>>() {
                    @Override
                    public Observable<LinkLinkNetInfo> call(Long aLong) {
                        return mApi.queryToken(uuid);
                    }
                })
                .map(new Func1<LinkLinkNetInfo, Boolean>() {

                    @Override
                    public Boolean call(LinkLinkNetInfo linkLinkNetInfo) {

                        Log.i("TAG","开始轮询");

                        if(linkLinkNetInfo.isSuccess()){
                            if(!Tools.checkJsonNull(linkLinkNetInfo.getData())){
                                Log.i("TAG",linkLinkNetInfo.getData().getAsString());
                                SharedPreferencesUtil.getInstance(mActivity).putString(SharedPreferencesUtil.ACCESS_TOKEN,linkLinkNetInfo.getData().getAsString());
                                //SharedPreferencesUtil.getInstance(AppLoader.getInstance()).putString(Constants.ACCOUNT,account);
                                RetrofitAPIManager.ACCESSTOKEN=linkLinkNetInfo.getData().getAsString();//token
                                return true;
                            }
                        }

                        return false;
                    }
                })
                .takeUntil(new Func1<Boolean, Boolean>() {

                    @Override
                    public Boolean call(Boolean b) {
                        return b;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(aBoolean){
                            mView.loginSuccess();
                        }

                    }
                });
    }

    /**
     * 获取用户健康信息
     */
    @Override
    public void checkUserInfo() {

        executeTaskAutoRetry(mApi.queryUserInfo(), new Observer<LinkLinkNetInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.loginFailed("");
            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(linkLinkNetInfo.isSuccess()){
                    UserInfoBean userInfoBean = Json.fromJson(linkLinkNetInfo.getData(),UserInfoBean.class);
                    if(userInfoBean.getGender() == 0 || userInfoBean.getHeight() == 0 || userInfoBean.getWeight() == 0
                            || userInfoBean.getBust() == 0 || userInfoBean.getWaist() == 0 || userInfoBean.getHipline() == 0
                            || Tools.checkNull(userInfoBean.getBirth())){
                        //跳转录入健康资料界面
                        mView.jumpNextActivity(2);
                    }else{
                        mView.jumpNextActivity(1);
                    }
                }else{

                    if(linkLinkNetInfo.getErrorCode().equals(Constants.LOGIN_ERROR_CODE)){
                        mView.loginFailed(linkLinkNetInfo.getMessage());
                    }else{
                        mView.showToast(linkLinkNetInfo.getMessage());
                    }
                    //
                }
            }
        });
    }

    @Override
    public void onDestory() {
        super.onDestory();
        LogUtil.i(TAG,"onDestory()");
    }

    public void endPolling(){
        if(subscription != null){
            subscription.unsubscribe();
        }
    }
}
