package com.ilinklink.tg.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.dialog.DialogConstom;
import com.ilinklink.tg.interfaces.CallBack;
import com.ilinklink.tg.interfaces.PermissionCallBack;
import com.ilinklink.tg.interfaces.PermissionCallBack2;
import com.ilinklink.tg.mvp.login.LoginActivity;
import com.ilinklink.tg.utils.CommonUtil;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.Tools;
import com.qdong.communal.library.module.BaseRefreshableListFragment.BaseRefreshableListFragment2;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.LoadMoreView2;
import com.qdong.communal.library.module.BaseRefreshableListFragment.adapter.SimpleLoadMoreView2;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RxHelper;
import com.qdong.communal.library.util.Constants;
import com.ilinklink.app.fw.R;
import com.tbruyelle.rxpermissions2.Permission;

import java.util.HashMap;

import androidx.annotation.Nullable;
import rx.Observable;
import rx.Observer;

/**
 *CustomBaseRefreshableFragment
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/8/22  16:03
 * Copyright : 全民智慧城市 版权所有
 **/
public abstract class CustomBaseRefreshableFragment2<T> extends BaseRefreshableListFragment2<T> {

    protected boolean USE_FAKE_DATA =false;//假数据开关

    /**
     * @method name:onAuthFailed
     * @des:重试登录,依然失败,走此回调
     * @param :[]
     * @return type:void
     * @date 创建时间:2019/1/23
     * @author Chuck
     **/
    @Override
    protected void onAuthFailed(){
      //跳到登录界面:
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    /**
     * @param :[]
     * @return type:java.util.HashMap<java.lang.String,java.lang.String>
     *
     * @method name:getAutoLoginParameterMap
     * @des:统一提供自动登录时需要的参数map
     * @date 创建时间:2016/8/22
     * @author Chuck
     **/
    @Override
    public HashMap<String, String> getAutoLoginParameterMap() {
        return AppLoader.getInstance().getAutoLoginParameterMap();
    }

    /****************************************************
     * 统一ip和端口,如果不重写,默认使用Constans里的常量
     *
     * @return
     */
    @Override
    public String getBaseUrl() {
        return Constants.SERVER_URL;
        //return "http://101.201.82.183:8080/";
        //return "http://192.168.1.230:10005/";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            //检查激活状态

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected boolean showActiveDialog(){
        return true;
    }

    @Override
    public Activity getContext(){
        return  getActivity();
    }

    protected void fackData(){

    }

    /**
     * @method name:executeTaskAutoRetry
     * @param :[observable, observer]
     * @return type:void
     * @date 创建时间:2017/5/13
     * @author Chuck
     **/
    public void executeTaskAutoRetry(final Observable<LinkLinkNetInfo> observable, final Observer<LinkLinkNetInfo> observer){

        Observer<LinkLinkNetInfo> ob=new AbstractObserver () {


            @Override
            public void onCompleted() {
                observer.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG,"父类处理异常:"+e.getMessage());

                if(RxHelper.THROWABLE_MESSAGE_AUTO_LOGIN_FAILED.equals(e.getMessage())){//自动登陆失败,跳登录界面
                    //getActivity().sendBroadcast(new Intent(com.qmzhhchsh.yp.utils.Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else {
                    observer.onError(e);
                }
            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(isNeedLogin(linkLinkNetInfo.getErrorCode())){
                    //getActivity().sendBroadcast(new Intent(com.qmzhhchsh.yp.utils.Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面

                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else if(RxHelper.ERROR_SQUEEZE_OUT.equals(linkLinkNetInfo.getErrorCode())){//被挤下线
                    loadingView.dismiss();
                    Tools.quitApp(getActivity());
                }
                else{
                    observer.onNext(linkLinkNetInfo);
                }
            }
        };

        mSubscriptions.add(RxHelper.getInstance(getActivity()).executeTaskAutoRetry(observable,mApi,ob, AppLoader.getInstance().getAutoLoginParameterMap()));
    }


    //无加载更多的业务,则无需重写 by:Chuck
    @Override
    protected LoadMoreView2 getRecyclerViewLoadMoreView() {
        return new SimpleLoadMoreView2();
        //return null;
    }


    public void onResume() {
        super.onResume();
        try {



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @method name:checkLocationPermission
     * @des:定位动态授权,拒绝后,会弹框引导用户前往设置界面
     * @param :[callBack :回调, requestCodeGoToSetting:跳转到设置界面的code,子类在onActivityResult里可再作后续处理]
     * @return type:void
     * @date 创建时间:2019/4/25
     * @author Chuck
     **/
    protected void checkLocationPermission(PermissionCallBack2 callBack, int requestCodeGoToSetting) {

        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ;

        if(isPermissionOK){//低版本设备直接回调
            if(callBack!=null){
                callBack.permissionGranted(null);
            }
            return;
        }

        PermissionCallBack callBack1 = new PermissionCallBack() {
            @Override
            public void permissionGranted(Permission permission) {
                if(callBack!=null){
                    callBack.permissionGranted(permission);
                }
            }

            @Override
            public void permissionShouldShowRequest(Permission permission) {
                showSettingDialog(callBack,requestCodeGoToSetting);
            }

            @Override
            public void permissionNeverAgain(Permission permission) {
                showSettingDialog(callBack,requestCodeGoToSetting);
            }

            @Override
            public void complete() {

            }
        };

/*
        PermissionsUtil.requestPermission(getActivity(),callBack1, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);*/
    }

    /**
     * @method name:showSettingDialog
     * @des:弹框跳转到设置界面
     * @param :[]
     * @return type:void
     * @date 创建时间:2019/4/25
     * @author Chuck
     **/
    private void showSettingDialog(PermissionCallBack2 callBack,int code) {

        CallBack callBack1 = new CallBack() {
            @Override
            public void callBack(Object object) {
                if (object !=null && object instanceof Boolean){
                    boolean commit= (boolean) object;
                    if(commit){//true:确认  false :取消
                        //ToastHelper.showCustomMessage("delete");
                        CommonUtil.jump2PermissionPager(getActivity(),code);
                    }else{
                        if(callBack!=null){//拒绝了
                            callBack.permissionRefused();
                        }
                    }
                }
            }
        };

        DialogConstom dialog = new DialogConstom(
                getActivity(),
                false,
                true,
                this.getString(R.string.dialog_location_hint),
                this.getString(R.string.location_srtting_hint),
                //DensityUtil.dip2px(activity,9),
                -1,
                this.getString(R.string.go_to_setting),
                this.getString(R.string.location_cancel),
                callBack1);


        dialog.show();
    }

    public void setRefreshEnable(boolean enable) {
        if (mPtrFrame != null) {
            mPtrFrame.setEnabled(enable);
        }
    }
}
