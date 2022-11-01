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

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.ilinklink.greendao.StudentExam;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.moudle.exam.ExamResultUploadHanlde;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.mvp.exam.setting.ExamSettingActivity;
import com.ilinklink.tg.mvp.facerecognize.FaceRecognizeResult;
import com.ilinklink.tg.mvp.selectsubject.SelectSubjectActivity;
import com.ilinklink.tg.utils.ExamSettingUtils;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.qdong.communal.library.util.ToastUtil;
import com.spc.pose.demo.R;

import java.util.ArrayList;

import megvii.testfacepass.MainActivity;

public  class ExamActivity2 extends BasePoseActivity2{


    private static final int FACE_RECOGNIZE = 985;

    //给父类存起来,父类destory时遍历释放资源
    @Override
    public ArrayList<BasePresenter> initPresenters() {
        ArrayList<BasePresenter> list = new ArrayList<>();
        mPresenter = new ExamPresenterImpl(this);
        list.add((BasePresenter) mPresenter);
        return list;
    }

    @Override
    public void uploadExamResultOnCompleted(boolean successed, String message) {
        mLoadingView.dismiss();



        SdCardLogUtil.logInSdCard("ExamActivity","ExamActivity,uploadExamResultOnCompleted:successed:"+successed+",message:"+message);

        if(successed){
            ToastUtil.showCustomMessageShort(this,getString(R.string.exam_result_succed));


            initExamView();


        }
        else {
            ToastUtil.showCustomMessageShort(this,getString(R.string.exam_result_failed)+message);
        }
    }

    @Override
    protected void startExam(){

        //如果没有时间限制,则不开启倒计时


             super.startExam();


            //mViewBind.tvExamStatus.setText(getString(R.string.exam_status_examming));


            mIsExamming=true;

            countDown();




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

                //上传考试结果的回调，将在主线程执行
                ExamResultUploadHanlde.ResultCallback callback=new ExamResultUploadHanlde.ResultCallback() {
                    /**
                     * 结果回调
                     * @param sussesed true成功  false失败
                     * @param msg 失败原因
                     */
                    @Override
                    public void onResult(boolean sussesed, String msg) {
                        Log.i("uploadExamResult","界面收到回调,===============sussesed: " +sussesed+",msg:"+msg);

                    }
                };
                //第一个参数上下文，第二个参数examRecordId，第三个参数回调接口
                ExamResultUploadHanlde.getInstance().uploadExamResult(this,mStudentExamRecord.getExamRecordId(),callback);
                break;
            case R.id.tv_go_2_select_subject:
                startActivity(new Intent( this, SelectSubjectActivity.class));
                finish();
                break;
                //前往人脸识别时,把数据清空
            case R.id.tv_stu_face:
                go2FaceRecognize();
                break;
            case R.id.tv_start_exam:
                startExam();
                break;
            case R.id.tv_commit_exam_result:
                ToastUtils.showShort("该功能尚未开启");
                break;
            case R.id.tv_redo_exam:
                startExam();
                break;
            case R.id.tv_stop_exam:
                stopCount();
                initView();
                break;
            case R.id.tv_exam_compelated:
                finish();
                break;


        }
    }

    private void go2FaceRecognize(){

        //人脸识别界面
        startActivity(new Intent( this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SdCardLogUtil.logInSdCard("ExamActivity","onActivityResult");


        if(FACE_RECOGNIZE==requestCode){
            if(RESULT_OK==resultCode){

                //sd卡里存的是本地数据库考生表的id.因为百度计算特征值时,照片命名的长度有限制
                String userId = FaceRecognizeResult.getInstance().getStudentId();

                if(!TextUtils.isEmpty(userId)){
                    StudentInfo studentInfo = DBHelper.getInstance(this).getStudentInfoByRowId(Long.parseLong(userId));

                    //根据考试uuid和考生的uuid查询 studentExam,提交成绩时需要这个对象
                    StudentExam studentExam = DBHelper.getInstance(this).getStudentExam(mExam.getExamUUID(), studentInfo.getStudentUUID());

                    if(studentExam==null){
                        SdCardLogUtil.logInSdCard("ExamActivity","DBHelper.getInstance(this).getStudentExam(mExam.getExamUUID(), studentInfo.getStudentUUID()) ==null,根据考试uuid和考生的uuid查询 studentExam,结果为空 ");

                        ToastUtil.showCustomMessage(this,getString(R.string.exam_student_not_in_this_plan));
                    }
                    else {

                        onStudentSelected(studentExam);
                    }

                }

            }
        }


        LogUtil.i("BasePoseActivity", "onActivityResult,requestCode:" + requestCode);
        LogUtil.i("BasePoseActivity", "onActivityResult,resultCode:" + resultCode);


        if(requestCode==GO_TO_SETTING){
            if(resultCode==RESULT_OK){
                String stringExtra = data.getStringExtra(ExamSettingActivity.INTENT_EXAM_THRESHOLD);

                LogUtil.i("BasePoseActivity", "onActivityResult,stringExtra:" + stringExtra);


                if(!TextUtils.isEmpty(stringExtra)){
                    ToastUtil.showCustomMessageShort(this,stringExtra);
                    ExamSettingUtils.setExamSetting(this, selectedModel,stringExtra);
                    mThreshold=Float.parseFloat(stringExtra);

                    LogUtil.i("BasePoseActivity", "onActivityResult,mThreshold:" + mThreshold);

                }


            }
        }
    }
}
