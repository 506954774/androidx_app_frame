package com.ilinklink.tg.base;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.dialog.DialogConstom;
import com.ilinklink.tg.interfaces.CallBack;
import com.ilinklink.tg.interfaces.PermissionCallBack;
import com.ilinklink.tg.mvp.login.LoginActivity;
import com.ilinklink.tg.mvp.login.LoginContract;
import com.ilinklink.tg.mvp.login.LoginPresenterImpl;
import com.ilinklink.tg.utils.CommonUtil;
import com.ilinklink.tg.utils.Constants;
import com.qdong.communal.library.util.DensityUtil;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.NetworkUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.ilinklink.app.fw.R;
import com.tbruyelle.rxpermissions2.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

import static com.ilinklink.tg.utils.PermissionPageUtil.REQUEST_PERMISSION_CODE;

/**
 * LaunchActivity
 * 启动界面
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/7  15:38
 * Copyright : 全民智慧城市 版权所有
 **/

public class LaunchActivity extends BaseActivity implements LoginContract.LoginView{

    String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private com.qdong.communal.library.util.PermissionsUtil mPermissionsUtil;
    public static int TO_GRANT_AUTHORIZATION=1024;//申请权限
    private Bundle mBundle;
    private Map<Integer,DialogConstom> dialogConstomMap = new HashMap<>();
    private PermissionCallBack callBack;
    private LoginContract.LoginPresenter mLoginPresenter;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //状态栏色值,不重写的话,默认是白色
    @Override
    protected int getStatusBarColor() {
        return Color.parseColor("#FFFFFF");
    }

    @Override
    protected boolean showActiveDialog() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TAG="LaunchActivity";

        super.onCreate(savedInstanceState);
        setIsTitleBar(false);
        setContentView(R.layout.base_launch_activity);
        mPermissionsUtil = new com.qdong.communal.library.util.PermissionsUtil(this);
        mBundle = getIntent().getExtras();
        mLoginPresenter = new LoginPresenterImpl(this);
        //获取一次clientKey
        //ClientKeyProvider.getInstance().getClientKeyFromServer(this);
        mSubscriptions.add(Observable
                .timer(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())//延时300毫秒跳转,这个操作符产生一个Observer<Long>
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        /**跳转***/

                        jump();

                        return null;
                    }
                })
                .subscribe());

        overridePendingTransition(R.anim.base_display, R.anim.base_fade);//透明度渐增

    }

    private void goSetWifiActivity() {
      /*  Intent intent = new Intent(LaunchActivity.this, SetWifiActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_right_out);
        finish();*/
    }

    private void checkPermission() {

        if(callBack == null){
            //callBack = generatePermissionCallBack();
            callBack = new PermissionCallBack() {
                @Override
                public void permissionGranted(Permission permission) {
                    jump2Next();
                }

                @Override
                public void permissionShouldShowRequest(Permission permission) {

                    if(mPermissionsUtil.hasPermissions(LaunchActivity.this,PERMISSIONS_STORAGE)){

                        jump2Next();
                    }else{
                        String message = getString(R.string.permission_tips1);

                        showMessageOKCancel(0,message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkPermission();
                            }
                        });
                    }

                }

                @Override
                public void permissionNeverAgain(Permission permission) {

                    if(mPermissionsUtil.hasPermissions(LaunchActivity.this,PERMISSIONS_STORAGE)){

                        jump2Next();
                    }else{

                        String message = getString(R.string.permission_tips2);

                        showMessageOKCancel(1,message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CommonUtil.jump2PermissionPager(LaunchActivity.this);
                            }
                        });
                    }


                }

                @Override
                public void complete() {

                }
            };
        }


      /*  PermissionsUtil.requestPermission(this,callBack,Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);*/


    }

    private void jump2Next() {
        /*if(!SharedPreferencesUtil.getInstance(LaunchActivity.this).isGuideActvityShowed()){
            // TODO: 2016/9/25 show guide activity
            startActivity(new Intent(LaunchActivity.this,GuideActivity.class));
            finish();
            //go2LoginActivity();
        }
        else {
            go2MainActivity();
        }*/

        //直接进入首页
        go2MainActivity();
    }

    @NonNull
    private PermissionCallBack generatePermissionCallBack() {
        return new PermissionCallBack() {
            @Override
            public void permissionGranted(Permission permission) {
                if(!SharedPreferencesUtil.getInstance(LaunchActivity.this).isGuideActvityShowed()){
                    // TODO: 2016/9/25 show guide activity
                    //startActivity(new Intent(LaunchActivity.this,GuideActivity.class));
                    finish();
                    //go2LoginActivity();
                }
                else {
                    go2MainActivity();
                }
            }

            @Override
            public void permissionShouldShowRequest(Permission permission) {
                String message = getString(R.string.permission_tips1);

                showMessageOKCancel(0,message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkPermission();
                    }
                });
            }

            @Override
            public void permissionNeverAgain(Permission permission) {
                String message = getString(R.string.permission_tips2);

                showMessageOKCancel(1,message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CommonUtil.jump2PermissionPager(LaunchActivity.this);
                    }
                });

            }

            @Override
            public void complete() {

            }
        };
    }


    /**
     * 显示弹框
     * @param message
     *
     */
    private void showMessageOKCancel(int type,String message,DialogInterface.OnClickListener onClickListener) {

        DialogConstom mDialog = dialogConstomMap.get(type);

        if(mDialog == null){
            CallBack callBack = new CallBack() {
                @Override
                public void callBack(Object object) {
                    if (object !=null && object instanceof Boolean){
                        boolean commit= (boolean) object;
                        if(commit){//true:确认  false :取消
                            //ToastHelper.showCustomMessage("delete");
                            if(onClickListener!=null){
                                onClickListener.onClick(null,0);
                            }
                        }else{
                            finish();
                        }
                    }
                }
            };
            mDialog = new DialogConstom(
                    this,
                    false,
                    true,
                    this.getString(R.string.camera_permission_tips),
                    message,
                    //DensityUtil.dip2px(activity,9),
                    -1,
                    this.getString(R.string.camera_permission_setting),
                    this.getString(R.string.camera_permission_sd_cancel),
                    callBack);

            dialogConstomMap.put(type,mDialog);
        }
        if(!mDialog.isShowing()){
            mDialog.show();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


    }

    /**
     * @method name:jump
     * @des:跳转
     * @param :[]
     * @return type:void
     * @date 创建时间:2016/10/11
     * @author Chuck
     **/
    private void jump() {

        mSubscriptions.add(Observable
                .timer(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())//延时200毫秒跳转,这个操作符产生一个Observer<Long>
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        if(NetworkUtil.hasNetWork(getApplicationContext())){//有网
                            mLoginPresenter.checkUserInfo();
                        }else{
                            goSetWifiActivity();
                        }
                        return null;
                    }
                })
                .subscribe());

    }

    private void go2LoginActivity() {
        Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
        if(mBundle == null){
            mBundle = new Bundle();
        }
        intent.putExtras(mBundle);
        startActivity(intent);
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_right_out);
        finish();
    }
    private void go2MainActivity() {
        //checkCameraPermissions();
       /* Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_right_out);
        finish();*/
    }

    /**
     * @method
     * @des:跳转至拍照界面,参数自己填.子类重写这个,以实现拍照授权后的回调.重写之前,如果界面有额外的
     * 授权,重写onRequestPermissionsResult时,要把super给做了.
     * @date 创建时间:2018/12/21
     * @author Chuck
     **/
    @Override
    protected void onCameraGranted(){
       /* Intent intent = new Intent(LaunchActivity.this, TiganAct.class);
        if(mBundle != null){
            intent.putExtras(mBundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_right_out);
        finish();*/
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        LogUtil.i(TAG,"onWindowFocusChanged(boolean hasFocus):"+hasFocus);

        //activity获取焦点时调用
        if(hasFocus){
            try {
                //初始化应用状态栏的高度
                Rect outRect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
                if(outRect.top != 0) {
                    AppLoader.STATUS_BAR_HEIGHT = outRect.top;
                    SharedPreferencesUtil.getInstance(this).putInt(SharedPreferencesUtil.STATEBARHEIGHT, outRect.top);
                }

                int  screenHeight= DensityUtil.getDisplayHeight(this);
                int  screenWidth= DensityUtil.getDisplayWidth(this);
                if(screenHeight>0){
                    SharedPreferencesUtil.getInstance(this).putInt(Constants.SCREEN_HEIGHT,screenHeight);
                }
                if(screenWidth>0){
                    SharedPreferencesUtil.getInstance(this).putInt(Constants.SCREEN_WIDTH,screenWidth);
                }
            } catch (Exception e) {
                LogUtil.e(TAG,"onWindowFocusChanged(boolean hasFocus) exception:"+e.getMessage());

                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            checkPermission();
        }
    }

    @Override
    public void loginSuccess() {
        go2MainActivity();
    }

    @Override
    public void loginFailed(String msg) {
        go2LoginActivity();
    }

    @Override
    public void jumpNextActivity(int type) {
        go2MainActivity();
    }

    @Override
    public Activity getActivityContext() {
        return this;
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showToast(String msg) {

    }
}
