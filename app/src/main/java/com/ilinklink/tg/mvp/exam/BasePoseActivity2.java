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

package com.ilinklink.tg.mvp.exam;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
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
import com.google.mlkit.vision.pose.Pose;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.greendao.StudentExam;
import com.ilinklink.greendao.StudentExamRecord;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.dto.QueryStudentExamRecordDto;
import com.ilinklink.tg.entity.SubjectExamResult;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.exam.setting.ExamSettingActivity;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
import com.ilinklink.tg.utils.ActionRulesUtils;
import com.ilinklink.tg.utils.CollectionUtils;
import com.ilinklink.tg.utils.ExamSettingUtils;
import com.ilinklink.tg.utils.Json;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.qdong.communal.library.module.network.RxHelper;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityFuwochengBinding;
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


public  class BasePoseActivity2 extends BaseMvpActivity<ActivityFuwochengBinding> implements View.OnClickListener  ,OnRequestPermissionsResultCallback, OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, ExamContract
        .ExamView {

    public static final String EXAM_DATA = "EXAM_DATA";


    public static final String EXAM_UUID = "EXAM_UUID";
    public static final String EXAM_NAME = "EXAM_NAME";

    private static final String POSE_DETECTION_1 = "仰卧起坐计数";
    private static final String POSE_DETECTION_2 = "俯卧撑";
    private static final String POSE_DETECTION_3 = "单杠引体向上";
    private static final String POSE_DETECTION_4 = "双杠臂屈伸";
    private static final String TAG = "BasePoseActivity2";
    private static final int PERMISSION_REQUESTS = 1;
    protected static final int GO_TO_SETTING = 110;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    protected String selectedModel = POSE_DETECTION_2;

    protected ExamContract.ExamPresenter mPresenter;

    protected String mExamUUID;

    //动作计数的回调
    protected ActionCountCallBackBean mActionCountCallBackBean;

    protected ExamInfo mExam;

    //当前正在考试
    protected boolean mIsExamming;

    //当前考试的学生
    protected StudentExam mStudentExam;

    //当前正在倒计时
    protected boolean mIsCountDown;

    //动作难度阈值
    protected float mThreshold=15f;
    private String mExamName;

    //上个界面传来的json串数据
    protected String mDataJson;
    //将要缓存的数据
    protected StudentExamRecord mStudentExamRecord;


    // 设置考试科目
    private void resetSubject() {

        mExamName = getIntent().getStringExtra(EXAM_NAME);
        if ("单杠引体向上".equals(mExamName)) {
            selectedModel = POSE_DETECTION_3;
        } else if ("双杠臂屈伸".equals(mExamName)) {
            selectedModel = POSE_DETECTION_4;
        } else if ("俯卧撑".equals(mExamName)) {
            selectedModel = POSE_DETECTION_2;
        }
        else if ("仰卧起坐".equals(mExamName)) {
            selectedModel = POSE_DETECTION_1;
        } else {
            selectedModel = POSE_DETECTION_2;
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setIsTitleBar(false);
        super.onCreate(savedInstanceState);
        resetSubject();
        setContentView(R.layout.activity_fuwocheng);
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
        options.add(selectedModel);
        //options.add(POSE_DETECTION_2);
        //options.add(POSE_DETECTION_3);
        //options.add(POSE_DETECTION_1);

        //options.add(POSE_DETECTION_4);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
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
        //countDown();
        mViewBind.tvSubjectName.setText(selectedModel);


       // startExam();
    }

    protected void startExam() {

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


        ArrayList<ActionRules> actionRules = new ArrayList<>();

        int actionType = PoseConfig.Builder.POSE_ACTION_SIT_UP;
        if (selectedModel.equals(POSE_DETECTION_1)) {

            SdCardLogUtil.logInSdCard(TAG,"actionType,===============设置规则,仰卧起坐" );

            // 仰卧起坐
            actionType = PoseConfig.Builder.POSE_ACTION_SIT_UP;
            actionRules.addAll(ActionRulesUtils.crateSitUpActionRules());
        } else if (selectedModel.equals(POSE_DETECTION_2)) {
            SdCardLogUtil.logInSdCard(TAG,"actionType,===============设置规则,俯卧撑" );

            // 俯卧撑
            actionType = PoseConfig.Builder.POSE_ACTION_PUSH_UP;
            actionRules.addAll(ActionRulesUtils.cratePushUpActionRules());

        } else if (selectedModel.equals(POSE_DETECTION_3)) {
            SdCardLogUtil.logInSdCard(TAG,"actionType,===============设置规则,单杠引体向上" );

            //单杠
            actionType = PoseConfig.Builder.POSE_ACTION_SINGLEBAR_PULLUP;
            actionRules.addAll(ActionRulesUtils.crateSiglebarPullupActionRules());

        } else if (selectedModel.equals(POSE_DETECTION_4)) {
            SdCardLogUtil.logInSdCard(TAG,"actionType,===============设置规则,双杠臂屈伸" );

            //双杠
            actionType = PoseConfig.Builder.POSE_ACTION_PARALLEL_PULLUP;
            actionRules.addAll(ActionRulesUtils.crateParallelbarPullupActionRules());

        }


        SdCardLogUtil.logInSdCard(TAG,"actionType,===============规则:"+actionRules.toString() );



        PoseConfig.Builder builder = new PoseConfig.Builder();
        builder.setModel(PoseConfig.Builder.POSE_DETECTOR_PERFORMANCE_MODE_STANDARD) // 高精度模型慢
                // 要识别的动作
                .setActionType(actionType)
                .setFirstEnterThreshold(mThreshold)
                .setSecondEnterThreshold(mThreshold)
                // 设置 识别的回调
                .setPoseCallback(mPoseCallBack)
                .setActionCountCallBack(mActionCountCallBack)
        .setRules(actionRules)
        ;

        PoseDetectorProcessor modelConfig = PoseAgent.getProcessor(this, builder.builder());
        cameraSource.setMachineLearningFrameProcessor(modelConfig);

    }


    PoseCallBack mPoseCallBack = new PoseCallBack() {
        @Override
        public void onPoseSuccess(Pose pose, List<String> poseClassification) {

            SdCardLogUtil.logInSdCard("PoseCallBack", selectedModel + ",PoseCallBack.onPoseSuccess(),List<String> poseClassification:" + poseClassification);

            LogUtil.i("onPoseSuccess", "poseClassification:" + poseClassification);


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


    ActionCountCallBack mActionCountCallBack = new ActionCountCallBack() {
        @Override
        public void onPoseSuccess(ActionCountCallBackBean actionCountCallBackBean) {
            SdCardLogUtil.logInSdCard("ActionCountCallBack", selectedModel + ",=================计数回调:当前开始状态是否为考试中?"+mIsExamming+",ActionCountCallBack,actionCountCallBackBean.getGooCount():" + actionCountCallBackBean.getGooCount() + ",actionCountCallBackBean.getAllCount():" + actionCountCallBackBean.getAllCount() + ",actionCountCallBackBean.getErrorDesc():" + actionCountCallBackBean.getErrorDesc());

            LogUtil.i("onPoseSuccess", selectedModel + ",=================计数回调:当前开始状态是否为考试中?"+mIsExamming+",ActionCountCallBack,actionCountCallBackBean.getGooCount():" + actionCountCallBackBean.getGooCount() + ",actionCountCallBackBean.getAllCount():" + actionCountCallBackBean.getAllCount() + ",actionCountCallBackBean.getErrorDesc():" + actionCountCallBackBean.getErrorDesc());


            if (actionCountCallBackBean != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mIsExamming) {

                            mActionCountCallBackBean = actionCountCallBackBean;

                            mViewBind.tvExamSucessedCount.setText(String.valueOf(actionCountCallBackBean.getGooCount()));
                            mViewBind.tvCountTotal.setText(String.valueOf(actionCountCallBackBean.getAllCount()));
                            mViewBind.tvExamFailedCount.setText(String.valueOf(actionCountCallBackBean.getAllCount()-actionCountCallBackBean.getGooCount()));

                            String error = "";
                            List<String> errorDesc = actionCountCallBackBean.getErrorDesc();
                            if (!CollectionUtils.isNullOrEmpty(errorDesc)) {
                                //error=errorDesc.get(errorDesc.size()-1);
                                error = errorDesc.toString();
                            }
                            mViewBind.tvExamFailedDesc.setText(error);
                        }


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

        LogUtil.i("BasePoseActivity", "onResume,mThreshold:" + mThreshold);

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


        String examSetting = ExamSettingUtils.getExamSetting(this, selectedModel);

        if(!TextUtils.isEmpty(examSetting)){
            mThreshold=Float.parseFloat(examSetting);
        }

        LogUtil.i("BasePoseActivity", "mThreshold:" + mThreshold);



        mDataJson=getIntent().getStringExtra(EXAM_DATA);
        if(mDataJson!=null){
            mStudentExamRecord= Json.fromJson(mDataJson,StudentExamRecord.class);
        }

    }

    //重置界面
    private void initView() {
        mViewBind.setClick(this);

        if(mStudentExamRecord!=null){
            mViewBind.tvStudentName.setText(mStudentExamRecord.getStudentName());
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                setResult(RESULT_OK);
                break;
            case R.id.iv_setting:
                //ToastUtils.showShort("该功能尚未开启");
                intent = new Intent( this, ExamSettingActivity.class);
                intent.putExtra(ExamSettingActivity.INTENT_EXAM_SUBJECT_NAME,selectedModel);
                startActivityForResult(intent,GO_TO_SETTING);
                break;
            case R.id.tv_go_2_select_subject:
                startActivity(new Intent(this, SelectSubjectActivity.class));
                break;
        }
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/


    private Subscription mSubscriptionCount;

    //考试倒计时,60秒
    protected long mTimer = 120;


    /**
     * @param :[]
     * @return type:void
     * @method name:countDown
     * @date 创建时间:2016/9/3
     * @author Chuck
     **/
    protected void countDown() {

        Action1<? super Long> observer = new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                try {

                    if (aLong >= 0 && aLong <= mTimer) {
                        mIsCountDown=true;
                        mViewBind.tvTime.setText(String.valueOf(mTimer - aLong) + "秒");
                        mViewBind.progressbarHint.setProgress((int) (100 * aLong / mTimer));
                    } else {

                        mIsExamming=false;
                        mIsCountDown=false;
                        mViewBind.tvTime.setText(String.valueOf(0) + "秒");
                        mViewBind.progressbarHint.setProgress(100);

                        /**取消此任务**/
                        RxHelper.getInstance(BasePoseActivity2.this).unsubscribe(mSubscriptionCount);

                        onExamFinished();
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


    private void onExamFinished(){
        Log.i(TAG,"onExamFinished,===============" );


        QueryStudentExamRecordDto dto=new QueryStudentExamRecordDto();
        dto.setExamRecordId(mStudentExamRecord.getExamRecordId());
        dto.setStudentUUID(mStudentExamRecord.getStudentUUID());
        List<StudentExamRecord> studentRecord = DBHelper.getInstance(this).getStudentRecord(dto);

        if(!CollectionUtils.isNullOrEmpty(studentRecord)){
            mStudentExamRecord = studentRecord.get(studentRecord.size()-1);
        }


        SubjectExamResult result= Json.fromJson(mStudentExamRecord.getSubResultJson(),SubjectExamResult.class);
        if(result==null){
            result=new SubjectExamResult();
        }

        Integer count=Integer.parseInt(mViewBind.tvExamSucessedCount.getText().toString());
        int pass=0;
        if(count!=null){
            if(count.compareTo(new Integer(10))>0){
                pass=1;
            }
        }
        else {
            count=0;
        }

        String score=count+","+pass;

        if (selectedModel.equals(POSE_DETECTION_1)) {

            Log.i(TAG,"onExamFinished,===============设置规则,仰卧起坐" );

            result.setSithUpsScore(score);

        } else if (selectedModel.equals(POSE_DETECTION_2)) {
            Log.i(TAG,"onExamFinished,===============设置规则,俯卧撑" );

            result.setPushUpsScore(score);

        } else if (selectedModel.equals(POSE_DETECTION_3)) {
            Log.i(TAG,"onExamFinished,===============设置规则,单杠引体向上" );

            result.setPullUpsScore(score);


        } else if (selectedModel.equals(POSE_DETECTION_4)) {
            Log.i(TAG,"onExamFinished,===============设置规则,双杠臂屈伸" );

            result.setParallelBarsScore(score);

        }

        mStudentExamRecord.setSubResultJson(Json.toJson(result));

        Log.i(TAG,"onExamFinished,===============准备存入此次考试成绩："+ mStudentExamRecord);

        DBHelper.getInstance(this).saveStudentExamRecord(mStudentExamRecord);

    }

    @Override
    public void getExamInfoAndSaveOnCompleted(boolean successed, String message) {

    }

    @Override
    public void compareFaceFeatrueOnCompleted(boolean isNeedUpdate, String message) {

    }

    @Override
    public void downLoadFaceFeatrueOnCompleted(boolean successed, String message, int count) {

    }

    @Override
    public void uploadExamResultOnCompleted(boolean successed, String message) {

    }

    @Override
    public void initExamView() {

        mViewBind.tvExamStatus.setText(getString(R.string.exam_status_no_student));

        mStudentExam = null;

        mIsExamming = false;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mViewBind.tvExamSucessedCount.setText("0");
                mViewBind.tvCountTotal.setText("0");
                mViewBind.tvTime.setText(getString(R.string.exam_time_no_limit));

                String error = "";
                mViewBind.tvExamFailedDesc.setText(error);
            }
        });
    }


    public void onStudentSelected(StudentExam studentExam) {
        mActionCountCallBackBean=null;
        mStudentExam=studentExam;
        StudentInfo studentInfo = DBHelper.getInstance(this).getStudentInfo(studentExam.getStudentUUID());
        mViewBind.tvStudentName.setText(studentInfo.getName());
        mViewBind.tvExamStatus.setText(getString(R.string.exam_status_ready));
    }
}