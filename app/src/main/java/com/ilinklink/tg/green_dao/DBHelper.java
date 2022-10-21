package com.ilinklink.tg.green_dao;

import android.content.Context;

import com.ilinklink.greendao.DaoSession;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.greendao.ExamInfoDao;
import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.ExamRecordDao;
import com.ilinklink.greendao.LoginModel;
import com.ilinklink.greendao.LoginModelDao;
import com.ilinklink.greendao.StudentExam;
import com.ilinklink.greendao.StudentExamDao;
import com.ilinklink.greendao.StudentExamRecord;
import com.ilinklink.greendao.StudentExamRecordDao;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.greendao.StudentInfoDao;
import com.ilinklink.greendao.TrainingModel;
import com.ilinklink.greendao.TrainingModelDao;
import com.ilinklink.greendao.WifiModel;
import com.ilinklink.greendao.WifiModelDao;
import com.ilinklink.tg.communal.AppLoader;
import com.ilinklink.tg.dto.QueryStudentExamRecordDto;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


/**
 * DBHelper
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/19  12:28
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class DBHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    private static DBHelper instance;
    private static Context appContext;
    private DaoSession mDaoSession;
    private LoginModelDao mLoginDao;//登录表
    private WifiModelDao mWifiModelDao;//wifi
    private TrainingModelDao mTrainingModelDao;//训练


    private StudentInfoDao mStudentInfoDao;
    private ExamInfoDao mExamInfoDao;
    private StudentExamDao mStudentExamDao;

    private ExamRecordDao mExamRecordDao;
    private StudentExamRecordDao mStudentExamRecordDao;


    private DBHelper() {
    }

    //单例模式，DBHelper只初始化一次
    public static DBHelper getInstance(Context context) {

        if (context == null) {
            context = AppLoader.getInstance();
        }
        if (instance == null) {
            instance = new DBHelper();
            if (appContext == null) {
                appContext = context.getApplicationContext();
            }
            instance.mDaoSession = AppLoader.getDaoSession(context);
            instance.mLoginDao = instance.mDaoSession.getLoginModelDao();
            instance.mWifiModelDao = instance.mDaoSession.getWifiModelDao();
            instance.mTrainingModelDao = instance.mDaoSession.getTrainingModelDao();

            instance.mStudentInfoDao = instance.mDaoSession.getStudentInfoDao();
            instance.mExamInfoDao = instance.mDaoSession.getExamInfoDao();
            instance.mStudentExamDao = instance.mDaoSession.getStudentExamDao();

            instance.mExamRecordDao = instance.mDaoSession.getExamRecordDao();
            instance.mStudentExamRecordDao = instance.mDaoSession.getStudentExamRecordDao();

        }
        return instance;
    }

    public void deleteAllLoginModel() {
        mLoginDao.deleteAll();
    }
    public void deleteAllWifiModel() {
        mWifiModelDao.deleteAll();
    }
    /**
     * @param :[bean]
     * @return type:long
     * @method name:saveLoginBean
     * @des:保存登录信息
     * @date 创建时间:2016/9/19
     * @author Chuck
     **/
    public long saveLoginBean(LoginModel bean) {
        mLoginDao.deleteAll();
        return mLoginDao.insertOrReplace(bean);
    }

    /**
     * @param :[]
     * @return type:com.ilinklink.greendao.LoginBean
     * @method name:getLoggedUser
     * @des:获取当前未登出的用户
     * @date 创建时间:2016/9/19
     * @author Chuck
     **/
    public LoginModel getLoggedUser() {

        QueryBuilder<LoginModel> mqBuilder = mLoginDao.queryBuilder();
        List<LoginModel> list = mqBuilder.where(LoginModelDao.Properties.Is_logged.eq(true))
                .orderDesc(LoginModelDao.Properties.Login_time)
                .limit(1)
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 保存wifi信息
     * @param bean
     * @return
     */
    public long saveWifiModel(WifiModel bean) {
        return mWifiModelDao.insertOrReplace(bean);
    }

    /**
     * 获取wifiModel
     * @return
     */
    public WifiModel getWifiModel(String wifiName) {

        QueryBuilder<WifiModel> mqBuilder = mWifiModelDao.queryBuilder();
        List<WifiModel> list = mqBuilder.where(WifiModelDao.Properties.WifiName.eq(wifiName))
                .limit(1)
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    /**
     * 获取wifiModel
     * @return
     */
    public void removeWifiModel(String wifiName) {

        WifiModel wifiModel = getWifiModel(wifiName);
        if(wifiModel != null){
            mWifiModelDao.delete(wifiModel);
        }

    }


    /**
     * 保存训练数据,场景:
     * 拿到相机回调,发送给算法服务器之前,插入,此时分数统一改为"-1"
     * @param bean
     * @return
     */
    public long saveTrainingModelBean(TrainingModel  bean) {
        return mTrainingModelDao.insertOrReplace(bean);
    }

    /**
     *
     * @param frameId
     * @return
     */
    public TrainingModel getTrainingModelById(String frameId ) {
        try {
            QueryBuilder<TrainingModel> mqBuilder = mTrainingModelDao.queryBuilder();
            List<TrainingModel> list = mqBuilder
                    .where(TrainingModelDao.Properties.FrameId.eq(frameId))
                    .orderDesc(TrainingModelDao.Properties.Time)
                    .list();
            return list.get(0);
        } catch (Exception e) {
            return  null;
        }
    }

    /**
     * 更新某一条训练数据,场景:
     * 1,算法服务器返回了分数,则把数据库里的分数改掉(插入分数不是为了训练中展示得分曲线,而是为了后续删除ok的byte数组)
     * 2,训练结束后,算法服务器已经返回了全部数据,把分数ok的帧删除(byte数组设置为空即可)
     * @param bean
     * @return
     */
    public void updateTrainingModelBean(TrainingModel  bean) {
         mTrainingModelDao.update(bean);
    }

    /**
     * 查出得分为-1的训练数据(即尚未计算出来的帧)
     * @param trainingId
     * @return
     */
    public List<TrainingModel> getUnGradedTrainingModelList(String trainingId ) {

        QueryBuilder<TrainingModel> mqBuilder = mTrainingModelDao.queryBuilder();
        List<TrainingModel> list = mqBuilder
                .where(TrainingModelDao.Properties.TrainingId.eq(trainingId))
                .where(TrainingModelDao.Properties.FrameMatchDegreet.eq(-1))
                .orderDesc(TrainingModelDao.Properties.Time)
                .list();
        return list;
    }

    /**
     * 查出大于或等于某个分数的训练数据
     * @param trainingId
     * @return
     */
    public List<TrainingModel> getGradedTrainingModelList(String trainingId,float score) {

        QueryBuilder<TrainingModel> mqBuilder = mTrainingModelDao.queryBuilder();
        List<TrainingModel> list = mqBuilder
                .where(TrainingModelDao.Properties.TrainingId.eq(trainingId))
                .where(TrainingModelDao.Properties.FrameMatchDegreet.ge(score))
                .orderDesc(TrainingModelDao.Properties.Time)
                .list();
        return list;
    }
    /**
     * 查出全部训练数据
     * @param trainingId
     * @return
     */
    public List<TrainingModel> getTrainingModelListAll(String trainingId) {

        QueryBuilder<TrainingModel> mqBuilder = mTrainingModelDao.queryBuilder();
        List<TrainingModel> list = mqBuilder
                .where(TrainingModelDao.Properties.TrainingId.eq(trainingId))
                .orderDesc(TrainingModelDao.Properties.Time)
                .list();
        return list;
    }
    /**
     * 删除全部训练数据
     * @param
     * @return
     */
    public void clearAllTrainingModel( ) {
        mTrainingModelDao.deleteAll();
    }


    /*****************************************************************************************************
     * 姿态识别
     *
     * ****************************************************************************************************
     *
     * **********/

    /**
     * 保存考生信息
     * @param bean
     * @return
     */
    public long saveStudentInfo(StudentInfo bean) {
        return mStudentInfoDao.insertOrReplace(bean);
    }

    /**
     * 根据学生的uuid查询最后那个学生
     * @return
     */
    public StudentInfo getStudentInfo(String uuid) {
        QueryBuilder<StudentInfo> mqBuilder = mStudentInfoDao.queryBuilder();
        List<StudentInfo> list = mqBuilder.where(StudentInfoDao.Properties.StudentUUID.eq(uuid))
                .orderDesc(StudentInfoDao.Properties.UpdateTime)
                .limit(1)
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    /**
     * @return
     */
    public StudentInfo getStudentInfoByRowId(Long id) {
        return mStudentInfoDao.load(id);
    }

    /**
     * @return
     */
    public void deleteStudentInfoByKey(Long id) {
        mStudentInfoDao.deleteByKey(id);
    }

    /**
     * 根据uuid删除学生信息
     * @return
     */
    public void removeStudentInfo(String uuid) {

        StudentInfo wifiModel = getStudentInfo(uuid);
        if(wifiModel != null){
            mStudentInfoDao.delete(wifiModel);
        }

    }

    /**
     *
     */
    public List<StudentInfo> getStudentInfoList(String uuid) {
        QueryBuilder<StudentInfo> mqBuilder = mStudentInfoDao.queryBuilder();
        List<StudentInfo> list = mqBuilder.where(StudentInfoDao.Properties.StudentUUID.eq(uuid))
                .orderDesc(StudentInfoDao.Properties.UpdateTime)
                .list();
        return list;
    }

    /**
     *
     */
    public List<StudentInfo> getAllStudents() {
       return mStudentInfoDao.loadAll();
    }


    /**

     * @param bean
     * @return
     */
    public long saveExamInfo(ExamInfo bean) {
        return mExamInfoDao.insertOrReplace(bean);
    }


    public ExamInfo getExamInfo(String uuid) {
        QueryBuilder<ExamInfo> mqBuilder = mExamInfoDao.queryBuilder();
        List<ExamInfo> list = mqBuilder.where(ExamInfoDao.Properties.ExamUUID.eq(uuid))
                .orderDesc(ExamInfoDao.Properties.Id)
                .limit(1)
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    public List<ExamInfo> getExamInfoList() {
        QueryBuilder<ExamInfo> mqBuilder = mExamInfoDao.queryBuilder();
        List<ExamInfo> list = mqBuilder.where(ExamInfoDao.Properties.ExamUUID.isNotNull())
                .orderAsc(ExamInfoDao.Properties.Id)
                .list();
        return list;
    }


    /**
      删除所有的考试信息
     * @author Chuck
     **/
    public long deleteAllExamInfos() {
        mExamInfoDao.deleteAll();
        mStudentExamDao.deleteAll();
        return 0;
    }


    /**

     * @param bean
     * @return
     */
    public long saveStudentExam(StudentExam bean) {
        return mStudentExamDao.insertOrReplace(bean);
    }


    /**
     * 根据考试id和学生id查询
     * @param examUUID
     * @param studentUUID
     * @return
     */
    public StudentExam getStudentExam(String examUUID,String studentUUID) {
        QueryBuilder<StudentExam> mqBuilder = mStudentExamDao.queryBuilder();
        List<StudentExam> list = mqBuilder
                .where(StudentExamDao.Properties.ExamUUID.eq(examUUID),StudentExamDao.Properties.StudentUUID.eq(studentUUID)  )
                .orderDesc(StudentExamDao.Properties.Id)
                .limit(1)
                .list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    /**
     *  插入一条考试数据
     * @param bean
     * @return
     */
    public long saveExamRecord(ExamRecord bean) {
        return mExamRecordDao.insertOrReplace(bean);
    }


    /**
     * 查询考试列表
     * @return
     */
    public List<ExamRecord> getExamRecordList() {
        QueryBuilder<ExamRecord> mqBuilder = mExamRecordDao.queryBuilder();
        List<ExamRecord> list = mqBuilder.where(ExamRecordDao.Properties.ExamUUID.isNotNull())
                .orderDesc(ExamRecordDao.Properties.Id)
                .list();
        return list;
    }

    /**
     * 查询某个考生针对某场考试的记录
     * @return
     */
    public List<StudentExamRecord> getStudentRecord(QueryStudentExamRecordDto dto) {
        QueryBuilder<StudentExamRecord> mqBuilder = mStudentExamRecordDao.queryBuilder();
        List<StudentExamRecord> list = mqBuilder
                .where(StudentExamRecordDao.Properties.ExamRecordId.eq(dto.getExamRecordId())
                ,StudentExamRecordDao.Properties.StudentUUID.eq(dto.getStudentUUID())
                )
                .orderDesc(StudentExamRecordDao.Properties.Id)
                .list();
        return list;
    }

    /**
     * 查询针对某场考试的记录
     * @return
     */
    public List<StudentExamRecord> getExamRecord(String examRecordId) {
        QueryBuilder<StudentExamRecord> mqBuilder = mStudentExamRecordDao.queryBuilder();
        List<StudentExamRecord> list = mqBuilder
                .where(StudentExamRecordDao.Properties.ExamRecordId.eq(examRecordId)
                )
                .orderDesc(StudentExamRecordDao.Properties.Id)
                .list();
        return list;
    }
}
