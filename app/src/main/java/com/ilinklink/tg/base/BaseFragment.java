package com.ilinklink.tg.base;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.dialog.DialogConstom;

import com.ilinklink.tg.interfaces.CallBack;
import com.ilinklink.tg.interfaces.OnSoftKeyBoardVisibleListener;
import com.ilinklink.tg.interfaces.PermissionCallBack;
import com.ilinklink.tg.interfaces.PermissionCallBack2;
import com.ilinklink.tg.mvp.login.LoginActivity;

import com.ilinklink.tg.utils.BhUtils;
import com.ilinklink.tg.utils.CommonUtil;
import com.ilinklink.tg.utils.LogUtil;

import com.ilinklink.tg.utils.ToastHelper;
import com.ilinklink.tg.utils.Tools;
import com.qdong.communal.library.module.network.LinkLinkApi;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.module.network.RxHelper;
import com.qdong.communal.library.widget.custommask.CustomMaskLayerView;
import com.ilinklink.app.fw.R;
import com.tbruyelle.rxpermissions2.Permission;


import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * BaseFragment
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/8  9:44
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {
    protected static String TAG="";

    /**接口api**/
    protected LinkLinkApi mApi;
    protected T mViewBind;
    protected CustomMaskLayerView mLoadingView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       /* if (mViewBind == null) {
            mViewBind = DataBindingUtil.inflate(inflater, getLayoutId(), container, true);
        } else {
            // 不为null时，需要把自身从父布局中移除，因为ViewPager会再次添加
            ViewParent parent = mViewBind.getRoot().getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView( mViewBind.getRoot());
            }
        }*/
        mApi= RetrofitAPIManager.provideClientApi(getActivity());
        mViewBind = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return mViewBind.getRoot();
    }

    public abstract int getLayoutId();



    @Override
    public void onResume() {
        super.onResume();
       /* try {
            if(getActivity() instanceof MainActivity){
                MainActivity activity= (MainActivity) getActivity();
            }
            //MobclickAgent.onPageStart(getClass().getName()); //统计页面，页面名可自定义


        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            //MobclickAgent.onPageEnd(getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected ArrayList<Subscription> mSubscriptions=new ArrayList<>();

    public LinkLinkApi getmApi() {
        return mApi;
    }

    public ArrayList<Subscription> getmSubscriptions() {
        return mSubscriptions;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            for(Subscription s:mSubscriptions){
                LogUtil.e("Subscription",s);
                RxHelper.getInstance(AppLoader.getInstance()).unsubscribe(s);
            }

            if (receiver != null) {
                getActivity().unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * **********************************************************************************************************
     * **********************************************************************************************************
     * **********************************************************************************************************
     * 软键盘监听相关
     **/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TAG=this.getClass().getSimpleName();

        //软键盘监听设置
        addOnSoftKeyBoardVisibleListener(getActivity(), getOnSoftKeyBoardVisibleListener());

        mLoadingView=((BaseActivity)getActivity()).getmLoadingView();

        try {
            //检查激活状态
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected boolean showActiveDialog(){
        return true;
    }

    private boolean sLastVisiable;//标示此时键盘是否已经弹出  true为已经弹出

    /**
     * @param :[activity, listener]
     * @return type:void
     *
     * @method name:addOnSoftKeyBoardVisibleListener
     * @des:添加软键盘是否弹出的监听器
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    private void addOnSoftKeyBoardVisibleListener(Activity activity, final OnSoftKeyBoardVisibleListener listener) {

        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHight = rect.bottom - rect.top;
                int hight = decorView.getHeight();
                boolean visible = (double) displayHight / hight < 0.8;

                if (visible != sLastVisiable && listener != null) {
                    listener.onSoftKeyBoardVisible(visible);
                }
                sLastVisiable = visible;
            }
        });
    }




    /**
     * @param :[]
     * @return type:com.qdong.onemile.interfaces.OnSoftKeyBoardVisibleListener
     *
     * @method name:getOnSoftKeyBoardVisibleListener
     * @des:子类可以重写这个来获取软键盘的弹出,收起事件
     * @date 创建时间:2016/7/11
     * @author Chuck
     **/
    protected OnSoftKeyBoardVisibleListener getOnSoftKeyBoardVisibleListener() {
        return null;
    }


    /**
     * @method name:showSoftInput
     * @des:手动弹出软键盘
     * @param :[editText]
     * @return type:void
     * @date 创建时间:2016/9/3
     * @author Chuck
     **/
    protected void showSoftInput(final EditText editText) {

        try {
            if(editText!=null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isSoftInputShowwing()){
                            showOrHideInput(editText);
                        }
                    }
                },500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param :[]
     * @return type:boolean
     *
     * @method name:isSoftInputShowwing
     * @des:软键盘此时是否弹出了 true为已弹出
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    protected boolean isSoftInputShowwing() {
        return sLastVisiable;
    }

    /**
     * @param :[show, view]
     * @return type:void
     *
     * @method name:showOrHideInput
     * @des:展示或者隐藏软件盘 切换 如果此时软键盘弹了就隐藏,如果此时隐藏了就弹出
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    protected void showOrHideInput(EditText view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @method name:executeTaskAutoRetry

     * @param :[observable, observer]
     * @return type:void
     * @date 创建时间:2017/5/13
     * @author Chuck
     **/
    protected void executeTaskAutoRetry(final Observable<LinkLinkNetInfo> observable, final Observer<LinkLinkNetInfo> observer){

        Observer<LinkLinkNetInfo> ob=new AbstractObserver () {


            @Override
            public void onCompleted() {
                observer.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG,"父类处理异常:"+e.getMessage());

                if(RxHelper.THROWABLE_MESSAGE_LOGIN_FAILED.equals(e.getMessage())){//自动登陆失败,跳登录界面
                    //getActivity().sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else {
                    observer.onError(e);
                }
            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(isNeedLogin(linkLinkNetInfo.getErrorCode())){
                    //getActivity().sendBroadcast(new Intent(Constants.ACTION_FINISH_ALL));//发出广播,finish掉所有界面

                    //startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                else if(RxHelper.ERROR_SQUEEZE_OUT.equals(linkLinkNetInfo.getErrorCode())){//被挤下线
                    mLoadingView.dismiss();
                    Tools.quitApp(getActivity());
                }
                else{
                    observer.onNext(linkLinkNetInfo);
                }
            }
        };

        mSubscriptions.add(RxHelper.getInstance(getActivity()).executeTaskAutoRetry(observable,mApi,ob, AppLoader.getInstance().getAutoLoginParameterMap()));
    }

    /*****************************************************************************
    *****************************************************************************
     * 支付相关
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //处理支付结果
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(PAY_RESULT);
        getActivity().registerReceiver(receiver, filter);
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

    /**
     * @method name:checkLocationPermission
     * @des:定位动态授权,拒绝后,会弹框引导用户前往设置界面
     * @param :[callBack :回调, requestCodeGoToSetting:跳转到设置界面的code,子类在onActivityResult里可再作后续处理]
     * @return type:void
     * @date 创建时间:2019/4/25
     * @author Chuck
     **/
    protected void checkLocationPermission(PermissionCallBack2 callBack, int requestCodeGoToSetting) {

        // BUGFIX: 2019/12/20  BY:Chuck http://10.32.156.110:8080/browse/YPANDROID2-1466
        if (!BhUtils.isLocationEnabled(getActivity())) {

            //这里需要延迟个0.3秒显示无权限的对话框，不然截图拿到是滑动到一半的过程
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    DialogConstom mDialog = new DialogConstom(getActivity(), false, true, getString(R.string.location_permission_dialog_title), getString(R.string.location_permission_dialog_content), 14, getString(R.string.location_permission_dialog_confirm_btn), getString(R.string.location_permission_dialog_cancel_btn), new CallBack() {
                        @Override
                        public void callBack(Object object) {
                            if (object != null && object instanceof Boolean) {
                                boolean commit = (boolean) object;
                                if (commit) {
                                    //进入设置去开启服务
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent,requestCodeGoToSetting);

                                } else {
                                   //点击了暂不,则什么也不做
                                }
                            }
                        }
                    });
                    mDialog.show();
                }
            }, 300);

            return;
        } 


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
    private void showSettingDialog(PermissionCallBack2 callBack, int code) {

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
}
