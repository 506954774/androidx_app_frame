package com.ilinklink.tg.mvp.exam;

import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.tg.entity.ZtsbExamListData;
import com.ilinklink.tg.mvp.BaseView;

import java.util.HashMap;


/**
 *
 * Created By:Chuck
 * Des:
 * on 2018/12/7 14:08
 */
public interface ExamContract {



    public interface ExamPresenter {
        //从服务器获取考试信息并存入sqlite
        void getExamInfoAndSave();
        //从后台获取人脸照片,并与sqlite里的数据做对比
        void compareFaceFeatrue();
        //更新数据,下载到sd卡指定路径
        void downLoadFaceImages();
        //上传考试结果
        void uploadExamResult(HashMap<String,Object> maps);
        //考生的图片是否已经下载了.参数是sqlite的考生表的数据id
        boolean isStudentImageFileExsit(Long stuId);
        //插入数据到SQLite,并下载图片到sd卡
        boolean insertAndDownloadImage(ZtsbExamListData.EmpsDTO dto) throws Exception;
        //拼接完整的图片路径
        String spliceImageUrl(StudentInfo studentInfo);

    }



    public interface ExamView extends BaseView {

        //从服务器获取考试信息并存入sqlite
        void getExamInfoAndSaveOnCompleted(boolean successed,String message);
        //从后台获取人脸照片,并与sqlite里的数据做对比
        void compareFaceFeatrueOnCompleted(boolean isNeedUpdate,String message);
        //更新数据,下载到sd卡指定路径
        void downLoadFaceFeatrueOnCompleted(boolean successed,String message,int count);
        //上传考试结果
        void uploadExamResultOnCompleted(boolean successed,String message);

        //考试之前,控件清空
        void initExamView();

    }


}
