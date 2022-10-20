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

package com.ilinklink.tg.mvp.exam.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ilinklink.tg.base.BaseMvpActivity;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
import com.ilinklink.tg.utils.ExamSettingUtils;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.qdong.communal.library.util.ToastUtil;
import com.spc.pose.demo.R;
import com.spc.pose.demo.databinding.ActivityExamSettingBinding;

import java.util.ArrayList;

public  class ExamSettingActivity   extends BaseMvpActivity<ActivityExamSettingBinding> implements View.OnClickListener {


    public static final String INTENT_EXAM_SUBJECT_NAME="INTENT_EXAM_SUBJECT_NAME";
    public static final String INTENT_EXAM_THRESHOLD="INTENT_EXAM_THRESHOLD";

    private static final int FACE_RECOGNIZE = 985;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setIsTitleBar(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_setting);
        mViewBind.setClick(this);

        //获得焦点
        mViewBind.etThreshold.setFocusable(true);
        //获得焦点
        mViewBind.etThreshold.setFocusableInTouchMode(true);

        InputMethodManager inputManager = (InputMethodManager)mViewBind.etThreshold.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mViewBind.etThreshold, 0);


        initSetting();
    }

    private void initSetting(){
        String examSubjectName=getIntent().getStringExtra(INTENT_EXAM_SUBJECT_NAME);
        mViewBind.tvSettingTitle.setText(StringUtils.null2Length0(examSubjectName) +"参数设置");


        String examSetting = ExamSettingUtils.getExamSetting(this, examSubjectName);
        if(!TextUtils.isEmpty(examSetting)){
           mViewBind.etThreshold.setText(examSetting);
        }


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.iv_back:

                if(TextUtils.isEmpty(mViewBind.etThreshold.getText().toString())){
                    ToastUtil.showCustomMessageShort(this,mViewBind.etThreshold.getHint().toString());
                    return;
                }


                Intent data = new Intent();
                data.putExtra(INTENT_EXAM_THRESHOLD,mViewBind.etThreshold.getText().toString());
                setResult(RESULT_OK,data);
                finish();
                break;
            case R.id.iv_setting:
                ToastUtils.showShort("该功能尚未开启");

                break;
            case R.id.tv_go_2_select_subject:
                startActivity(new Intent( this, SelectSubjectActivity.class));
                break;
                //前往人脸识别时,把数据清空
            case R.id.tv_stu_face:
                go2FaceRecognize();
                break;




        }
    }

    private void go2FaceRecognize(){
        //人脸识别界面
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SdCardLogUtil.logInSdCard("ExamActivity","onActivityResult");


        if(FACE_RECOGNIZE==requestCode){
            if(RESULT_OK==resultCode){

            }
        }
    }

    //禁用返回键
    @Override
    public void onBackPressed() {
          onClick(mViewBind.ivBack);
    }


}
