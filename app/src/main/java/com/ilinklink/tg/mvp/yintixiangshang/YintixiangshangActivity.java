/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ilinklink.tg.mvp.yintixiangshang;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.blankj.utilcode.util.ToastUtils;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.vision.pose.Pose;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.facerecognize.FaceRecognizeResult;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.qdong.communal.library.module.network.RxHelper;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityYintixiangshangBinding;
import com.spc.pose.demo.preference.activity.SettingsActivity;
import com.spc.pose.demo.preference.graphic.PoseGraphic;
import com.spc.posesdk.ActionCountCallBack;
import com.spc.posesdk.CameraSource;
import com.spc.posesdk.CameraSourcePreview;
import com.spc.posesdk.GraphicOverlay;
import com.spc.posesdk.PoseAgent;
import com.spc.posesdk.PoseCallBack;
import com.spc.posesdk.PoseConfig;
import com.spc.posesdk.bean.ActionCountCallBackBean;
import com.spc.posesdk.bean.ActionRules;
import com.spc.posesdk.java.posedetector.PoseDetectorProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@KeepName
public final class YintixiangshangActivity extends BaseMvpActivity<ActivityYintixiangshangBinding> implements View.OnClickListener  ,OnRequestPermissionsResultCallback, OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private static final String POSE_DETECTION_1 = "仰卧起坐计数";
    private static final String POSE_DETECTION_2 = "俯卧撑计数";
    private static final String POSE_DETECTION_3 = "单杠引体向上";
    private static final String POSE_DETECTION_4 = "双杠臂屈伸";
    private static final String TAG = "LivePreviewActivity";
    private static final int PERMISSION_REQUESTS = 1;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private String selectedModel = POSE_DETECTION_3;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setIsTitleBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yintixiangshang);
        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        Spinner spinner = findViewById(R.id.spinner);
        List<String> options = new ArrayList<>();
        options.add(POSE_DETECTION_3);
        options.add(POSE_DETECTION_1);
        options.add(POSE_DETECTION_2);

        options.add(POSE_DETECTION_4);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        //spinner.setAdapter(dataAdapter);
        //spinner.setOnItemSelectedListener(this);

        ToggleButton facingSwitch = findViewById(R.id.facing_switch);
        facingSwitch.setOnCheckedChangeListener(this);

        ImageView settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent.putExtra(
                            SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
                    startActivity(intent);
                });

        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
        } else {
            getRuntimePermissions();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }

        initView();
        initData();
        countDown();
        mViewBind.tvSubjectName.setText(selectedModel);
    }

    @Override
    public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedModel = parent.getItemAtPosition(pos).toString();
        Log.d(TAG, "Selected model: " + selectedModel);
        preview.stop();
        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
            startCameraSource();
        } else {
            getRuntimePermissions();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * 切换前后摄像头
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
        if (cameraSource != null) {
            if (isChecked) {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
            } else {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
            }
        }
        preview.stop();
        startCameraSource();
    }


    /**
     * 调用SDK，初始化模型
     *
     * @param selectedModel 动作类别
     */
    private void createCameraSource(String selectedModel) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        int actionType = PoseConfig.Builder.POSE_ACTION_SIT_UP;
        if (selectedModel.equals(POSE_DETECTION_1)) {
            // 仰卧起坐
            actionType = PoseConfig.Builder.POSE_ACTION_SIT_UP;
        } else if (selectedModel.equals(POSE_DETECTION_2)) {
            // 俯卧撑
            actionType = PoseConfig.Builder.POSE_ACTION_PUSH_UP;
        } else if (selectedModel.equals(POSE_DETECTION_3)) {
            actionType = PoseConfig.Builder.POSE_ACTION_SINGLEBAR_PULLUP;
        } else if (selectedModel.equals(POSE_DETECTION_4)) {
            actionType = PoseConfig.Builder.POSE_ACTION_PARALLEL_PULLUP;
        }

        ArrayList<ActionRules> actionRules = new ArrayList<>();

        ActionRules rules = new ActionRules();
        rules.setClassName("up");
        rules.setP1Name("LEFT_ELBOW");
        rules.setP2Name("LEFT_WRIST");
        rules.setP3Name("LEFT_PINKY");
        rules.setCompareBy(">=");
        rules.setDegree(30);
        rules.setDescription("身体前倾不够");
        actionRules.add(rules);

        PoseConfig.Builder builder = new PoseConfig.Builder();
        builder.setModel(PoseConfig.Builder.POSE_DETECTOR_PERFORMANCE_MODE_STANDARD) // 高精度模型慢
                // 要识别的动作
                .setActionType(actionType)
                .setFirstEnterThreshold(10f)
                .setSecondEnterThreshold(10f)
                // 设置 识别的回调
                .setPoseCallback(mPoseCallBack)
                .setActionCountCallBack(mActionCountCallBack)
        //.setRules(actionRules)
        ;

        PoseDetectorProcessor modelConfig = PoseAgent.getProcessor(this, builder.builder());
        cameraSource.setMachineLearningFrameProcessor(modelConfig);

    }


    PoseCallBack mPoseCallBack=new PoseCallBack() {
        @Override
        public void onPoseSuccess(Pose pose, List<String> poseClassification) {

            SdCardLogUtil.logInSdCard("PoseCallBack",selectedModel+",PoseCallBack.onPoseSuccess(),List<String> poseClassification:"+poseClassification);

            LogUtil.i("onPoseSuccess","poseClassification:"+poseClassification);


            graphicOverlay.add(
                    new PoseGraphic(
                            graphicOverlay,
                            pose,
                            true,
                            true,
                            true,
                            poseClassification));
        }
    };


    ActionCountCallBack mActionCountCallBack=new ActionCountCallBack() {
        @Override
        public void onPoseSuccess(ActionCountCallBackBean actionCountCallBackBean) {
            SdCardLogUtil.logInSdCard("ActionCountCallBack",selectedModel+",onPoseSuccess,actionCountCallBackBean.getGooCount():"+actionCountCallBackBean.getGooCount()+",actionCountCallBackBean.getAllCount():"+actionCountCallBackBean.getAllCount()+",actionCountCallBackBean.getErrorDesc():"+actionCountCallBackBean.getErrorDesc());

            LogUtil.i("onPoseSuccess","actionCountCallBackBean.getGooCount():"+actionCountCallBackBean.getGooCount()+",actionCountCallBackBean.getAllCount():"+actionCountCallBackBean.getAllCount()+",actionCountCallBackBean.getErrorDesc():"+actionCountCallBackBean.getErrorDesc());


            if(actionCountCallBackBean!=null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewBind.tvExamSucessedCount.setText(String.valueOf(actionCountCallBackBean.getGooCount()));
                        mViewBind.tvCountTotal.setText(String.valueOf(actionCountCallBackBean.getAllCount()));
                    }
                });
            }
        }
    };

    /**
     * 开始预览
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        createCameraSource(selectedModel);
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
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

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

///////////////////////////////////////////////////////////////////
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



    private void initData() {

    }

    //重置界面
    private void initView() {
        mViewBind.setClick(this);

        mViewBind.tvStudentName.setText(FaceRecognizeResult.getInstance().getStudentId());
    }




    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_setting:
                ToastUtils.showShort("该功能尚未开启");

                break;
            case R.id.tv_go_2_select_subject:
                startActivity(new Intent( this, SelectSubjectActivity.class));
                break;


        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    private Subscription mSubscriptionCount;

    //考试倒计时,60秒
    private long mTimer=60;


    /**
     * @method name:countDown

     * @param :[]
     * @return type:void
     * @date 创建时间:2016/9/3
     * @author Chuck
     **/
    private void countDown() {

        Action1<? super Long> observer = new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                try {

                    if(aLong >= 0 && aLong <=mTimer){

                        mViewBind.tvTime.setText(String.valueOf(60-aLong)+"秒");
                        mViewBind.progressbarHint.setProgress((int) (100*aLong/mTimer));
                    }
                    else {

                        mViewBind.tvTime.setText(String.valueOf(0)+"秒");
                        mViewBind.progressbarHint.setProgress(100);

                        /**取消此任务**/
                        RxHelper.getInstance(YintixiangshangActivity.this).unsubscribe(mSubscriptionCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mSubscriptionCount = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程,最终来到主线程执行
                .subscribe(observer);//订阅

        mSubscriptions.add(mSubscriptionCount);

    }
}
