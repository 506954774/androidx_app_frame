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

import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.vision.pose.Pose;
import com.ilinklink.tg.utils.LogUtil;
import com.spc.pose.demo.R;
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
import com.spc.posesdk.java.posedetector.PoseDetectorProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

@KeepName
public final class YintixiangshangActivity2 extends AppCompatActivity implements OnRequestPermissionsResultCallback, OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision_live_preview);
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
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

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

        PoseConfig.Builder builder = new PoseConfig.Builder();
        builder.setModel(PoseConfig.Builder.POSE_DETECTOR_PERFORMANCE_MODE_STANDARD) // 高精度模型慢
                .setActionType(actionType) // 要识别的动作
                .setActionCountCallBack(new ActionCountCallBack() {
                    @Override
                    public void onPoseSuccess(ActionCountCallBackBean actionCountCallBackBean) {

                        LogUtil.i("onPoseSuccess","actionCountCallBackBean:"+actionCountCallBackBean);


                        if(actionCountCallBackBean!=null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //mViewBind.tvExamSucessedCount.setText(String.valueOf(actionCountCallBackBean.));
                                    //mViewBind.tvCountTotal.setText(String.valueOf(examSucessedCount));
                                }
                            });
                        }

                    }
                });

               /* .setCallback(new PoseCallBack() {
                    @Override
                    public void onPoseSuccess(Pose pose, List<String> poseClassification) {

                        LogUtil.i("onPoseSuccess","poseClassification:"+poseClassification);

                        LogUtil.i("onPoseSuccess","parsePoseCount:"+ Tools.parsePoseCount(poseClassification));

                        int examSucessedCount=Tools.parsePoseCount(poseClassification);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mViewBind.tvExamSucessedCount.setText(String.valueOf(examSucessedCount));
                                mViewBind.tvCountTotal.setText(String.valueOf(examSucessedCount));
                            }
                        });

                        graphicOverlay.add(
                                new PoseGraphic(
                                        graphicOverlay,
                                        pose,
                                        true,
                                        true,
                                        true,
                                        poseClassification));
                    }
                }); // 设置 识别的回调*/

        PoseDetectorProcessor modelConfig = PoseAgent.getProcessor(this, builder.builder());

        cameraSource.setMachineLearningFrameProcessor(modelConfig);
    }


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
}
