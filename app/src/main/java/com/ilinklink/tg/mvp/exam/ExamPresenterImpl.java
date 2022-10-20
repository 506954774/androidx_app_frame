package com.ilinklink.tg.mvp.exam;

import android.text.TextUtils;

import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.greendao.StudentExam;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.entity.ZtsbExamListData;
import com.ilinklink.tg.green_dao.DBHelper;
import com.ilinklink.tg.mvp.BasePresenter;
import com.ilinklink.tg.utils.CollectionUtils;
import com.ilinklink.tg.utils.Json;
import com.ilinklink.tg.utils.LogUtil;
import com.ilinklink.tg.utils.SdCardLogUtil;
import com.qdong.communal.library.module.network.LinkLinkNetInfo;
import com.qdong.communal.library.util.FileUtils;
import com.qdong.communal.library.util.OkHttpUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.qdong.communal.library.util.Constants.FILE_URL;

/**

 * Created By:Chuck
 * Des:
 * on 2018/12/7 14:16
 */
public class ExamPresenterImpl extends BasePresenter<ExamContract.ExamView> implements ExamContract.ExamPresenter{

    public ExamPresenterImpl(ExamContract.ExamView mView) {
        super(mView);
    }
    private Subscription subscription = null;


    @Override
    public void onDestory() {
        super.onDestory();
        LogUtil.i(TAG,"onDestory()");
    }

    @Override
    public void getExamInfoAndSave() {

        //链式调用,调用接口 > SQLite操作 >返回结果给界面

        LogUtil.i(TAG,"getExamInfoAndSave()");

        mSubscriptions.add(
                //调用接口
                mApi.getExamInfo()
                        .subscribeOn(Schedulers.io())
                        //写入SQLite数据库
                        .map(new Func1<LinkLinkNetInfo, LinkLinkNetInfo>() {
                            @Override
                            public LinkLinkNetInfo call(LinkLinkNetInfo linkLinkNetInfo) {

                                LinkLinkNetInfo result=new LinkLinkNetInfo();
                                result.setCode(0);

                                ZtsbExamListData ztsbExamListData = Json.fromJson(linkLinkNetInfo.getData(), ZtsbExamListData.class);

                                LogUtil.i(TAG,"ztsbExamListData==null?:"+ztsbExamListData==null);


                                if(ztsbExamListData==null|| CollectionUtils.isNullOrEmpty(ztsbExamListData.getContentExamEmps())){
                                    result.setCode(1);
                                    result.setMessage("暂无考试信息");

                                    return result;
                                }


                                //插入数据库
                                try {
                                    //先删掉,再插入
                                    DBHelper.getInstance(mView.getActivityContext()).deleteAllExamInfos();

                                    for (ZtsbExamListData.ContentExamEmpsDTO exam:ztsbExamListData.getContentExamEmps()){

                                        ExamInfo examEntity=new ExamInfo();
                                        examEntity.setExamUUID(exam.getContentId());
                                        examEntity.setName(exam.getContentName());
                                        examEntity.setLimitTime(exam.getLimit_time());

                                        DBHelper.getInstance(mView.getActivityContext()).saveExamInfo(examEntity);

                                        //立马查出刚刚那条数据
                                        ExamInfo examInfo = DBHelper.getInstance(mView.getActivityContext()).getExamInfo(exam.getContentId());
                                        if(examInfo!=null){

                                            SdCardLogUtil.logInSdCard(TAG,"考试信息入库,================插入一条考试记录成功,examInfo.getExamUUID():"+examInfo.getExamUUID());
                                            LogUtil.e(TAG,"考试信息入库,================插入一条考试记录成功,examInfo.getExamUUID():"+examInfo.getExamUUID());

                                            //插入此次考试对应的考生集合,1对多

                                            List<ZtsbExamListData.ContentExamEmpsDTO.ExamEmpsDTO> examEmps = exam.getExamEmps();
                                            if(!CollectionUtils.isNullOrEmpty(examEmps)){
                                                for(ZtsbExamListData.ContentExamEmpsDTO.ExamEmpsDTO examStudent: examEmps){
                                                    StudentExam stuExam=new StudentExam();
                                                    stuExam.setExamUUID(exam.getContentId());
                                                    stuExam.setResultUUID(examStudent.getExamResultId());
                                                    stuExam.setStudentUUID(examStudent.getEmpID());
                                                    DBHelper.getInstance(mView.getActivityContext()).saveStudentExam(stuExam);
                                                }

                                                SdCardLogUtil.logInSdCard(TAG,"考试信息入库,================插入此条考试对应的考生集合成功,数量:"+examEmps.size());

                                                LogUtil.e(TAG,"考试信息入库,================插入此条考试对应的考生集合成功,数量:"+examEmps.size());
                                            }

                                        }

                                    }


                                } catch (Exception e) {

                                    SdCardLogUtil.logInSdCard(TAG,"考试信息入库,================插入数据库,异常:"+e.getMessage());
                                    LogUtil.e(TAG,"================插入数据库,异常:"+e.getMessage());
                                    result.setCode(1);
                                    result.setMessage("插入数据库,异常:"+e.getMessage());
                                }

                                return result;
                            }
                        })

                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<LinkLinkNetInfo>() {
                            @Override
                            public void onCompleted() {
                                LogUtil.e(TAG,"================getExamInfoAndSave,onCompleted()");
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.e(TAG,"================getExamInfoAndSave,onError");
                            }

                            @Override
                            public void onNext(LinkLinkNetInfo result) {
                                LogUtil.i(TAG,"================getExamInfoAndSave,Observer,onNext,线程id:{0}",Thread.currentThread().getId());

                                LogUtil.i(TAG,"================getExamInfoAndSave,Observer,onNext,result:{0}",result);

                                mView.getExamInfoAndSaveOnCompleted(result.getCode()==0,result.getMessage());

                            }
                        })
        );

    }

    @Override
    public void compareFaceFeatrue() {

        //链式调用,调用接口 > SQLite操作 >返回结果给界面

        LogUtil.i(TAG,"compareFaceFeatrue()");

        mSubscriptions.add(
                //调用接口
                mApi.getExamInfo()
                        .subscribeOn(Schedulers.io())
                        //对比数据库
                        .map(new Func1<LinkLinkNetInfo, LinkLinkNetInfo>() {
                            @Override
                            public LinkLinkNetInfo call(LinkLinkNetInfo linkLinkNetInfo) {

                                LinkLinkNetInfo result=new LinkLinkNetInfo();
                                result.setCode(0);

                                ZtsbExamListData ztsbExamListData = Json.fromJson(linkLinkNetInfo.getData(), ZtsbExamListData.class);

                                LogUtil.i(TAG,"ztsbExamListData==null?:"+ztsbExamListData==null);


                                if(ztsbExamListData==null|| CollectionUtils.isNullOrEmpty(ztsbExamListData.getEmps())){
                                    result.setCode(1);
                                    result.setMessage("暂时没有考生");
                                }

                                try {


                                    //查询数据库里面的全部数据
                                    List<StudentInfo> allStudents = DBHelper.getInstance(mView.getActivityContext()).getAllStudents();

                                    if(CollectionUtils.isNullOrEmpty(allStudents)){
                                        result.setCode(0);
                                        result.setDesc(ztsbExamListData.getEmps().size());
                                    }
                                    else {

                                        int needUpdateCount=0;

                                        for (ZtsbExamListData.EmpsDTO stu:ztsbExamListData.getEmps()){
                                            StudentInfo studentInfo = DBHelper.getInstance(mView.getActivityContext()).getStudentInfo(stu.getEmpID());
                                            //如果sqlite里面没有这条数据,则加1
                                            if(studentInfo==null){
                                                needUpdateCount++;
                                                continue;
                                            }
                                            else {
                                                Long updateTime=stu.getUpdatedTime().getTime();
                                                //接口返回数据的更新日期比数据库里面的大,则表示需要更新
                                                if(updateTime.compareTo(studentInfo.getUpdateTime())>0){
                                                    needUpdateCount++;
                                                    continue;
                                                }
                                                //更新时间一致,但是sd卡里面的文件丢失了,也要重新来写入
                                                else {
                                                    boolean studentImageFileExsit = isStudentImageFileExsit(studentInfo.getId());
                                                    if(!studentImageFileExsit){
                                                        needUpdateCount++;
                                                        continue;
                                                    }
                                                }
                                            }

                                        }


                                        result.setDesc(needUpdateCount);

                                    }

                                } catch (Exception e) {

                                    SdCardLogUtil.logInSdCard(TAG,"compareFaceFeatrue,================对比考生照片,异常:"+e.getMessage());
                                    LogUtil.e(TAG,"compareFaceFeatrue,================对比考生照片,异常:"+e.getMessage());
                                    result.setCode(1);
                                    result.setMessage("对比考生照片,异常:"+e.getMessage());
                                }

                                return result;
                            }
                        })

                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<LinkLinkNetInfo>() {
                            @Override
                            public void onCompleted() {
                                LogUtil.e(TAG,"================compareFaceFeatrue,onCompleted()");
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.e(TAG,"================compareFaceFeatrue,onError");
                            }

                            @Override
                            public void onNext(LinkLinkNetInfo result) {
                                LogUtil.i(TAG,"================compareFaceFeatrue,Observer,onNext,线程id:{0}",Thread.currentThread().getId());

                                LogUtil.i(TAG,"================compareFaceFeatrue,Observer,onNext,result:{0}",result);

                                SdCardLogUtil.logInSdCard(TAG,"compareFaceFeatrue,================此次需要更新的照片数量:"+String.valueOf(result.getDesc()));


                                if(result.getCode()==0){
                                    mView.compareFaceFeatrueOnCompleted(true,"此次需要更新照片的数量:"+result.getDesc().toString());
                                }
                                else {
                                    mView.compareFaceFeatrueOnCompleted(false,result.getMessage());
                                }


                            }
                        })
        );
    }

    @Override
    public void downLoadFaceImages() {

        //链式调用,调用接口 > SQLite操作 >返回结果给界面

        LogUtil.i(TAG,"downLoadFaceImages()");

        mSubscriptions.add(
                //调用接口
                mApi.getExamInfo()
                        .subscribeOn(Schedulers.io())
                        //对比数据库
                        .map(new Func1<LinkLinkNetInfo, LinkLinkNetInfo>() {
                            @Override
                            public LinkLinkNetInfo call(LinkLinkNetInfo linkLinkNetInfo) {

                                LinkLinkNetInfo result=new LinkLinkNetInfo();
                                //此次新下载的图片数量
                                result.setDesc("0");
                                //错误码
                                result.setCode(0);

                                ZtsbExamListData ztsbExamListData = Json.fromJson(linkLinkNetInfo.getData(), ZtsbExamListData.class);

                                LogUtil.i(TAG,"ztsbExamListData==null?:"+ztsbExamListData==null);


                                if(ztsbExamListData==null|| CollectionUtils.isNullOrEmpty(ztsbExamListData.getEmps())){
                                    result.setCode(1);
                                    result.setMessage("暂时没有考生");
                                }

                                try {


                                    int downloadSuccedCount=0;

                                    for (ZtsbExamListData.EmpsDTO newStu:ztsbExamListData.getEmps()){

                                        //过滤异常数据
                                        if(TextUtils.isEmpty(newStu.getEmpID())){
                                            continue;
                                        }
                                        //过滤异常数据
                                        if(TextUtils.isEmpty(newStu.getEmpUrl())){
                                            continue;
                                        }

                                        StudentInfo oldStu = DBHelper.getInstance(mView.getActivityContext()).getStudentInfo(newStu.getEmpID());
                                        //如果sqlite里面没有这条数据,则加1
                                        if(oldStu==null){
                                            LogUtil.e(TAG,"downLoadFaceImages,================sqlite里面没有这条数据,准备插入/更新");
                                            boolean downloadSucced = insertAndDownloadImage(newStu);
                                            if(downloadSucced){
                                                downloadSuccedCount++;
                                            }

                                        }
                                        else {
                                            Long updateTime=newStu.getUpdatedTime().getTime();
                                            //接口返回数据的更新日期比数据库里面的大,则表示需要更新
                                            if(updateTime.compareTo(oldStu.getUpdateTime())>0){
                                                LogUtil.e(TAG,"downLoadFaceImages,================接口返回数据的更新日期比数据库里面的大,则表示需要更新");
                                                boolean downloadSucced = insertAndDownloadImage(newStu);
                                                if(downloadSucced){
                                                    downloadSuccedCount++;
                                                }
                                            }
                                            //更新时间一致,但是sd卡里面的文件丢失了,也要重新来写入
                                            else {
                                                boolean studentImageFileExsit = isStudentImageFileExsit(oldStu.getId());

                                                if(newStu.getEmpUrl()!=null&&!newStu.getEmpUrl().equals(oldStu.getImageUrl())){
                                                    LogUtil.e(TAG,"downLoadFaceImages,================url不同,需要更新");
                                                    boolean downloadSucced = insertAndDownloadImage(newStu);
                                                    if(downloadSucced){
                                                        downloadSuccedCount++;
                                                    }
                                                }
                                                else if(!studentImageFileExsit){
                                                    LogUtil.e(TAG,"downLoadFaceImages,================更新时间一致,但是sd卡里面的文件丢失了,需要更新");
                                                    boolean downloadSucced = insertAndDownloadImage(newStu);
                                                    if(downloadSucced){
                                                        downloadSuccedCount++;
                                                    }
                                                }
                                            }
                                        }

                                    }


                                    result.setDesc(downloadSuccedCount);


                                } catch (Exception e) {

                                    SdCardLogUtil.logInSdCard(TAG,"downLoadFaceImages,================downLoadFaceImages,异常:"+e.getMessage());
                                    LogUtil.e(TAG,"downLoadFaceImages,================downLoadFaceImages,异常:"+e.getMessage());
                                    result.setCode(1);
                                    result.setMessage("downLoadFaceImages,异常:"+e.getMessage());
                                }

                                return result;
                            }
                        })

                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<LinkLinkNetInfo>() {
                            @Override
                            public void onCompleted() {
                                LogUtil.e(TAG,"================downLoadFaceImages,onCompleted()");
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.e(TAG,"================downLoadFaceImages,onError");
                            }

                            @Override
                            public void onNext(LinkLinkNetInfo result) {
                                LogUtil.i(TAG,"================downLoadFaceImages,Observer,onNext,线程id:{0}",Thread.currentThread().getId());

                                LogUtil.i(TAG,"================downLoadFaceImages,Observer,onNext,result:{0}",result);


                                if(result.getCode()==0){
                                    SdCardLogUtil.logInSdCard(TAG,"此次下载完成的照片数量:"+result.getDesc().toString());
                                    LogUtil.e(TAG,"此次下载完成的照片数量:"+result.getDesc().toString());


                                    mView.downLoadFaceFeatrueOnCompleted(true,"此次新增的照片数量:"+result.getDesc().toString(),Integer.parseInt(result.getDesc().toString()));
                                }
                                else {
                                    SdCardLogUtil.logInSdCard(TAG,"下载图片失败:"+result.getMessage());
                                    LogUtil.e(TAG,"下载图片失败:"+result.getMessage());


                                    mView.downLoadFaceFeatrueOnCompleted(false,result.getMessage(),Integer.parseInt(result.getDesc().toString()));
                                }


                            }
                        })
        );
    }

    @Override
    public void uploadExamResult(HashMap<String,Object> maps) {
        executeTaskAutoRetry(mApi.commitExamResult(maps), new Observer<LinkLinkNetInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(LinkLinkNetInfo linkLinkNetInfo) {
                if(linkLinkNetInfo.getCode()==0){
                    mView.uploadExamResultOnCompleted(true,null);
                }
                else {
                    mView.uploadExamResultOnCompleted(false,linkLinkNetInfo.getMessage());
                }
            }
        });
    }

    @Override
    public boolean isStudentImageFileExsit(Long stuId) {

        // 1、获取导入目录 /sdcard/Face-Import
        File batchImportDir = FileUtils.getBatchImportDirectory();

        if(!batchImportDir.exists()){
            return false;
        }

        // 考生的照片的绝对路径 /sdcard/Face-Import/123.jpg
        String path=batchImportDir.getAbsolutePath()+"/"+stuId+".jpg";
        File imageFile=new File(path);
        return imageFile.exists();
    }

    @Override
    public boolean insertAndDownloadImage(ZtsbExamListData.EmpsDTO stu) throws Exception {

        SdCardLogUtil.logInSdCard(TAG,"insertAndDownloadImage,================准备插入图片数据:"+stu);
        LogUtil.e(TAG,"insertAndDownloadImage,================准备插入图片数据:"+stu);

        // 1、获取导入目录 /sdcard/Face-Import
        File batchImportDir = FileUtils.getBatchImportDirectory();
        String imagePath="";

        //先删sqlite数据
        //再删sd图片 ,考生的照片的绝对路径 /sdcard/Face-Import/123.jpg
        //再插入一条sqlite
        //再下载一张图片

        StudentInfo studentInfo = DBHelper.getInstance(mView.getActivityContext()).getStudentInfo(stu.getEmpID());

        if(studentInfo!=null){
            SdCardLogUtil.logInSdCard(TAG,"insertAndDownloadImage,================studentInfo!=null");
            LogUtil.e(TAG,"insertAndDownloadImage,================studentInfo!=null");

            //deleteStudentInfoByKey
            DBHelper.getInstance(mView.getActivityContext()).deleteStudentInfoByKey(studentInfo.getId());

            if(!batchImportDir.exists()){
                batchImportDir.mkdir();
            }

            // 考生的照片的绝对路径 /sdcard/Face-Import/123.jpg
            imagePath=batchImportDir.getAbsolutePath()+"/"+studentInfo.getId()+".jpg";
            File imageFile=new File(imagePath);

            SdCardLogUtil.logInSdCard(TAG,"insertAndDownloadImage,================考生的照片的绝对路径imageFile:"+imageFile);
            LogUtil.e(TAG,"insertAndDownloadImage,================考生的照片的绝对路径imageFile:"+imageFile);


            if(imageFile.exists()){
                SdCardLogUtil.logInSdCard(TAG,"insertAndDownloadImage,================imageFile.exists()=true");
                LogUtil.e(TAG,"insertAndDownloadImage,================imageFile.exists()=true,先删掉旧的图片");
                imageFile.delete();
            }
            else {
                SdCardLogUtil.logInSdCard(TAG,"insertAndDownloadImage,================imageFile.exists()=false");
                LogUtil.e(TAG,"insertAndDownloadImage,================imageFile.exists()=false");
            }

        }


        StudentInfo newStudent=new StudentInfo();
        newStudent.setStudentUUID(stu.getEmpID());
        newStudent.setName(stu.getEmpName());
        newStudent.setImageUrl(stu.getEmpUrl());
        newStudent.setUpdateTime(stu.getUpdatedTime().getTime());
        newStudent.setImageDownloadTime(0L);

        DBHelper.getInstance(mView.getActivityContext()).saveStudentInfo(newStudent);

        //每次给个新的id 考生的照片的绝对路径 /sdcard/Face-Import/123.jpg
        imagePath=batchImportDir.getAbsolutePath()+"/"+newStudent.getId()+".jpg";
        File imageFile=new File(imagePath);



        SdCardLogUtil.logInSdCard(TAG,"insertAndDownloadImage,================saveStudentInfo,插入一条到sqlite成功,uuid:"+stu.getEmpID());
        LogUtil.e(TAG,"insertAndDownloadImage,================saveStudentInfo,插入一条到sqlite成功,uuid:"+stu.getEmpID());


        newStudent = DBHelper.getInstance(mView.getActivityContext()).getStudentInfo(stu.getEmpID());

        LogUtil.e(TAG,"insertAndDownloadImage,================saveStudentInfo,插入一条到sqlite成功,id:"+newStudent.getId());

        //下载图片
        String url = spliceImageUrl(newStudent);

        LogUtil.e(TAG,"insertAndDownloadImage,================图片完整路径,url:"+url);

        String path = OkHttpUtil.downloadFile(url, imagePath);


        if(!TextUtils.isEmpty(path)){
            newStudent.setImageDownloadTime(System.currentTimeMillis());

            //更新数据
            DBHelper.getInstance(mView.getActivityContext()).saveStudentInfo(newStudent);

            return true;
        }
        else {
            LogUtil.e(TAG,"insertAndDownloadImage,================下载图片失败 url:"+url);

            return false;
        }


    }


    @Override
    public String spliceImageUrl(StudentInfo studentInfo) {
        if(studentInfo==null||TextUtils.isEmpty(studentInfo.getImageUrl())){
            return null;
        }
        //return "http://117.50.177.58:9000/ucloud/537900991d4d4765ad668f0bd3c05980.jpg";
        return FILE_URL+studentInfo.getImageUrl();
    }
}
