package com.ilinklink.tg.moudle.exam;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.StudentExamRecord;
import com.ilinklink.tg.entity.ExamInfoResponse;
import com.ilinklink.tg.entity.ExamResultUploadQo;
import com.ilinklink.tg.entity.SubjectExamResult;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.exam.BasePoseActivity2;
import com.ilinklink.tg.utils.CollectionUtils;
import com.ilinklink.tg.utils.Json;
import com.ilinklink.tg.utils.LogUtil;
import com.qdong.communal.library.module.network.LinkLinkApi;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.ilinklink.tg.mvp.exam.BasePoseActivity2.POSE_DETECTION_1;

/**
 * ExamResultUploadHanlde
 * Created By:Chuck
 * Des:
 * on 2022/10/30 17:21
 */
public class ExamResultUploadHanlde {
    private static final ExamResultUploadHanlde ourInstance = new ExamResultUploadHanlde();

    public static ExamResultUploadHanlde getInstance() {
        return ourInstance;
    }

    private ExamResultUploadHanlde() {
    }

    /**
     * 针对某次考试，上传考试成绩
     * @param act
     * @param callback
     */
    public void uploadExamResult(Activity act,String examRecordId,ResultCallback callback){

        String TAG="uploadExamResult";
        LogUtil.i(TAG,"examRecordId:"+examRecordId );

        List<StudentExamRecord> examInfoList = DBHelper.getInstance(act).getExamRecordNotUploaded(examRecordId);
        if(CollectionUtils.isNullOrEmpty(examInfoList)){
            LogUtil.i(TAG,"数据库里面的此次考试成绩已经全部上传完毕" );
            if(callback!=null){
                callback.onResult(false,"此次考试成绩之前已上传");
            }
            return;
        }

        //key:学生id  value：ehpId
        HashMap<String , String> personMap=new HashMap();
        ExamRecord examRecordById = DBHelper.getInstance(act).getExamRecordById(examInfoList.get(0).getExamRecordId());
        if(examRecordById!=null){
            //实际存储此次考试的考生关联，格式：  "1-2,2-3"  ehpId-peId，ehpId-peId
            String reservedColumn2 = examRecordById.getReservedColumn2();
            //reservedColumn2="1-13";
            LogUtil.i(TAG,"数据库里面的考生和ehpId关联：" +reservedColumn2);
            if(!TextUtils.isEmpty(reservedColumn2)){
                String[] split = reservedColumn2.split(",");
                if(split!=null&&split.length>0){
                    for (String data:split){
                        if(!TextUtils.isEmpty(data)&&data.contains("-")){
                            String[] s = data.split("-");
                            personMap.put(s[1],s[0]);
                        }
                    }
                }
            }
        }
        LogUtil.i(TAG,"考生map："+personMap );

        ArrayList<ExamResultUploadQo> qos=new ArrayList<ExamResultUploadQo>();
        ArrayList<String> stuExamRecordIds=new ArrayList<String>();
        for (StudentExamRecord record:examInfoList){
            String subResultJson = record.getSubResultJson();
            if(TextUtils.isEmpty(subResultJson)){
                continue;
            }
            else {
                SubjectExamResult score=Json.fromJson(subResultJson,SubjectExamResult.class);

                stuExamRecordIds.add(record.getStudentExamRecordId());
                String desc = record.getDesc();
                if(desc!=null){
                    List<ExamInfoResponse.SubjectsDTO> subs= Json.jsonToArrayList(desc, ExamInfoResponse.SubjectsDTO.class);
                    if(!CollectionUtils.isNullOrEmpty(subs)){
                        for (ExamInfoResponse.SubjectsDTO subject:subs){

                            String ehpId = personMap.get(record.getStudentUUID());
                            if(!TextUtils.isEmpty(ehpId)){
                                ExamResultUploadQo qo=new ExamResultUploadQo();
                                qo.setEhpId(Integer.parseInt(ehpId));
                                qo.setEdExamTime(record.getExamTime());
                                qo.setEdUploadDevice("001");
                                qo.setSuId(subject.getSuId());

                                try {
                                    //   分数格式：     String score=count+","+pass;
                                    if (subject.getSuName().equals(BasePoseActivity2.POSE_DETECTION_1)) {

                                       // Log.i(TAG,"onExamFinished,===============设置规则,仰卧起坐" );
                                        qo.setEdExamineNum(Integer.parseInt(score.getSithUpsScore().split(",")[0]));

                                    } else if (subject.getSuName().equals(BasePoseActivity2.POSE_DETECTION_2)) {
                                       // Log.i(TAG,"onExamFinished,===============设置规则,俯卧撑" );

                                        qo.setEdExamineNum(Integer.parseInt(score.getPushUpsScore().split(",")[0]));

                                    } else if (subject.getSuName().equals(BasePoseActivity2.POSE_DETECTION_3)) {
                                       // Log.i(TAG,"onExamFinished,===============设置规则,单杠引体向上" );

                                        qo.setEdExamineNum(Integer.parseInt(score.getPullUpsScore().split(",")[0]));


                                    } else if (subject.getSuName().equals(BasePoseActivity2.POSE_DETECTION_4)) {
                                       // Log.i(TAG,"onExamFinished,===============设置规则,双杠臂屈伸" );

                                        qo.setEdExamineNum(Integer.parseInt(score.getParallelBarsScore().split(",")[0]));

                                    }
                                } catch (Exception e) {
                                    LogUtil.i(TAG,"解析分数出错：score:"+score );
                                }

                                qos.add(qo);

                            }
                        }
                    }
                }
            }

        }

        LinkLinkApi mApi = RetrofitAPIManager.provideClientApi(act);

        if(CollectionUtils.isNullOrEmpty(qos)){
            LogUtil.i(TAG,"构造qo，结果为空:"+qos );
            if(callback!=null){
                callback.onResult(false,"本地数据异常，上传失败");
            }
            return;
        }

        String jsonArray=Json.toJson(qos);

        LogUtil.i(TAG,"准备调用分数上传接口:"+jsonArray );

        //ExamResultUploadQo[] objects = (ExamResultUploadQo[]) qos.toArray();
        Map<String, Object> qoMap=new HashMap<>();
        qoMap.put("params",qos);
        mApi.uploadExamResult2(qoMap)
        //mApi.uploadExamResult(jsonArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LinkLinkNetInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG,"onCompleted,=============== " );

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG,"onError,=============== " +e.getMessage());

                        if(callback!=null){
                          callback.onResult(false,e.getMessage());
                      }
                    }

                    @Override
                    public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                        Log.i(TAG,"onNext,=============== " +linkLinkNetInfo);

                        if(callback!=null){
                            if(linkLinkNetInfo.isSuccess()){

                                //遍历数据，把数据给改为已上传到服务器
                                for (StudentExamRecord record:examInfoList){
                                    boolean changed = DBHelper.getInstance(act).changeExamRecordUploadedStatus(record);
                                    Log.i(TAG,"修改本地考试记录的上传标识,===============changed: " +changed);
                                }

                                callback.onResult(true,null);
                            }
                            else {
                                callback.onResult(false,linkLinkNetInfo.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * callback
     */
    public interface ResultCallback{
        void onResult(boolean sussesed,String msg);
    }
}
