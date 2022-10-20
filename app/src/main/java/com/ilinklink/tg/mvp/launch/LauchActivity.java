package com.ilinklink.tg.mvp.launch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.entity.SysConfig;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.initfacefeatrue.InitFaceFeatrueAct2;
import com.ilinklink.tg.mvp.initfacefeatrue.InitFaceFeatrueAct3;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
import com.ilinklink.tg.utils.Constants;
import com.ilinklink.tg.utils.NetUtil;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.ilinklink.tg.utils.Tools;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.util.DensityUtil;
import com.qdong.communal.library.util.FileUtils;
import com.qdong.communal.library.util.JsonUtil;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityLaunchBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * LauchActivity
 *
 * 逻辑:
 * 获取磁盘权限,摄像头权限  > 登录 (缓存token) > 检验百度人脸的license > 进入主页 InitActivity.class
 *
 * 闪屏界面
 * Created By:Chuck
 * Des:
 * on 2018/12/6 15:43
 */
public class LauchActivity extends BaseMvpActivity<ActivityLaunchBinding> implements View.OnClickListener {

    private static final int PERMISSION_REQUESTS = 1;
    private static final int ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION = 110;



    //给父类存起来,父类destory时遍历释放资源
    @Override
    public ArrayList<BasePresenter> initPresenters() {
        ArrayList<BasePresenter> list = new ArrayList<>();
        return list;
    }


    /**
     * @param :[]
     * @return type:boolean
     * @method name:isRelativeStatusBar
     * @des:设置状态栏类型,返回true,则沉浸,false则线性追加
     * @date 创建时间:2018/12/4
     * @author Chuck
     **/
    @Override
    protected boolean isRelativeStatusBar() {
        return true;

    }

    @Override
    protected int getStatusBarColor() {
        return Color.parseColor("#00000000");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setIsTitleBar(false);

        super.onCreate(savedInstanceState);

        //设置布局,里面有埋点按钮,详细看布局文件
        setContentView(R.layout.activity_launch);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                jump();
            } else {
                Intent intent = new Intent( Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION );
                intent.setData( Uri.parse( "package:" + this.getPackageName() ) );
                startActivityForResult( intent,ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION );
            }
        }
        else {
            jump();
        }

    }




    private void jump(){
        initView();

        //获取权限
        if (allPermissionsGranted()) {
            cameraPermissionGranted();
        } else {
            getRuntimePermissions();
        }
    }





    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        Log.i(TAG, "allNeededPermissions: " + allNeededPermissions);

        if (!allNeededPermissions.isEmpty()) {
            String[] strings = allNeededPermissions.toArray(new String[0]);
            Log.i(TAG, "allNeededPermissions.toArray(new String[0]): " + Arrays.toString(strings));
            ActivityCompat.requestPermissions(
                    this, strings, PERMISSION_REQUESTS);
        }
    }

    private String[] getRequiredPermissions() {
        try {
           /* PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            LogUtil.i("getRequiredPermissions", Arrays.toString(ps));
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }*/

            /***
             *
             *
             [android.permission.INTERNET,
             android.permission.WRITE_EXTERNAL_STORAGE,
             android.permission.READ_EXTERNAL_STORAGE,
             android.permission.CAMERA,
             android.permission.MANAGE_EXTERNAL_STORAGE,
             android.permission.ACCESS_NETWORK_STATE
             ]
             */
            String[] result=null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
               /* result= new String[]{
                        "android.permission.INTERNET",
                        "android.permission.CAMERA",
                        "android.permission.ACCESS_NETWORK_STATE",
                        "android.permission.MANAGE_EXTERNAL_STORAGE",

                };*/


                result= new String[]{
                        "android.permission.INTERNET",
                        "android.permission.CAMERA",
                        "android.permission.ACCESS_NETWORK_STATE",
                        "android.permission.WRITE_EXTERNAL_STORAGE",
                        "android.permission.READ_EXTERNAL_STORAGE",
                  //      "android.permission.MANAGE_EXTERNAL_STORAGE",

                };

                LogUtil.i("getRequiredPermissions", Arrays.toString(result));


            } else {
                result= new String[]{
                        "android.permission.INTERNET",
                        "android.permission.CAMERA",
                        "android.permission.ACCESS_NETWORK_STATE",
                        "android.permission.WRITE_EXTERNAL_STORAGE",
                        "android.permission.READ_EXTERNAL_STORAGE",
                };

                LogUtil.i("getRequiredPermissions", Arrays.toString(result));

            }

            return result;

        } catch (Exception e) {
            return new String[0];
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            cameraPermissionGranted();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void cameraPermissionGranted(){

       // initConfig();

       // initData();

       // login();

        //startActivity(new Intent(this,SelectSubjectActivity.class));
        //startActivity(new Intent(this, InitFaceFeatrueAct2.class));
        startActivity(new Intent(this, InitFaceFeatrueAct3.class));
        finish();

    }


    private void initConfig(){
        try {
            //SysConfig
            //String json = Tools.readLocalJson(this, getQDongDir() + "cofig.json");

            //   String sdCardRoot=Environment.getExternalStorageDirectory().getAbsolutePath();
            //
            //            String  jsonPath=Environment.getExternalStorageDirectory() + "/"  + "cofig.json";
//Environment.getExternalStorageDirectory()

            String  jsonPath=null;

            File sdRootFile = FileUtils.getSDRootFile();
            File file = null;
            if (sdRootFile != null && sdRootFile.exists()) {

                LogUtil.i(TAG,"jsonPath,===============getExternalStorageDirectory.getAbsolutePath,sdCardRoot:"+sdRootFile.getAbsolutePath());

                SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============getExternalStorageDirectory.getAbsolutePath,sdCardRoot:"+sdRootFile.getAbsolutePath());


                file = new File(sdRootFile, "config.json");
                if (file.exists()) {
                    SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============config.json文件存在,路径:"+file.getAbsolutePath() );

                    jsonPath=file.getAbsolutePath();
                }
                else {
                    SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============config.json文件不存在" );
                }
            }

            File jsonFile=new File(jsonPath);

            LogUtil.i(TAG,"jsonPath,===============jsonPath:"+jsonPath);
            LogUtil.i(TAG,"jsonPath,===============jsonFile.exsit:"+jsonFile.exists());

            SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============jsonPath:"+jsonPath);
            SdCardLogUtil.logInSdCard(TAG,"jsonPath,===============jsonFile.exsit:"+jsonFile.exists());


            //读取配置文件
            String json = Tools.readLocalJson(this, jsonPath);
            if(!TextUtils.isEmpty(json)){
                SysConfig config= JsonUtil.fromJson(json,SysConfig.class);

                SdCardLogUtil.logInSdCard("initConfig","===============加载配置文件,config:"+config);

                LogUtil.i(TAG,"===============加载配置文件,config:"+config);


                if(config!=null){
                    com.qdong.communal.library.util.Constants.DEBUG_HOST=config.getHost();
                    com.qdong.communal.library.util.Constants.PRODUCTION_HOST=config.getHost();
                    com.qdong.communal.library.util.Constants.FILE_URL=config.getHost();
                    com.qdong.communal.library.util.Constants.BAIDU_FACE_KEY=config.getBaiduFaceKey();

                    //重新初始化api
                    mApi= RetrofitAPIManager.provideClientApi(this);
                }
            }
            else {

                SdCardLogUtil.logInSdCard("initConfig","============没有发现配置文件");

                LogUtil.i(TAG,"============没有发现配置文件");
            }
        } catch (Exception e) {

            SdCardLogUtil.logInSdCard("initConfig","===============加载配置文件,失败:"+e.getMessage());

            LogUtil.i(TAG,"===============加载配置文件,失败:"+e.getMessage());
        }
    }


    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void initData() {
        //ToastUtil.showCustomMessageShort("mac地址:"+NetUtil.getNewMac());
        SdCardLogUtil.logInSdCard("mac地址","mac地址:"+NetUtil.getNewMac());

    }



    //重置界面
    private void initView() {
        mViewBind.setClick(this);

    }




    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.custom_tv_login://账户登录


                break;


        }
    }






    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        LogUtil.i(TAG,"Permission,===============onActivityResult" );

        if(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION==requestCode){
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                LogUtil.i(TAG,"Permission,===============Environment.isExternalStorageManager()" );

                jump();
            } else {
                Intent intent = new Intent( Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION );
                intent.setData( Uri.parse( "package:" + this.getPackageName() ) );
                startActivityForResult( intent,ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION );
            }
        }
    }



    private void jump2UserHealthyInfoActivity(){
        Intent intent = null;//new Intent(this, HealthyInfoActivity.class);
        //intent.putExtras(mBundle);
        startActivity(intent);
        finish();
    }

    private void jump2MainActivity() {

        Intent intent = null;//new Intent(this, MainActivity.class);
        //intent.putExtras(mBundle);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mLoginPresenter.pollingTask(uuID);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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

}
