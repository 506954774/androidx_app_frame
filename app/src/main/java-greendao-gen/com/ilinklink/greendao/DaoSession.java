package com.ilinklink.greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.ilinklink.greendao.LoginModel;
import com.ilinklink.greendao.WifiModel;
import com.ilinklink.greendao.TrainingModel;
import com.ilinklink.greendao.StudentInfo;
import com.ilinklink.greendao.ExamInfo;
import com.ilinklink.greendao.StudentExam;
import com.ilinklink.greendao.ExamRecord;
import com.ilinklink.greendao.StudentExamRecord;

import com.ilinklink.greendao.LoginModelDao;
import com.ilinklink.greendao.WifiModelDao;
import com.ilinklink.greendao.TrainingModelDao;
import com.ilinklink.greendao.StudentInfoDao;
import com.ilinklink.greendao.ExamInfoDao;
import com.ilinklink.greendao.StudentExamDao;
import com.ilinklink.greendao.ExamRecordDao;
import com.ilinklink.greendao.StudentExamRecordDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig loginModelDaoConfig;
    private final DaoConfig wifiModelDaoConfig;
    private final DaoConfig trainingModelDaoConfig;
    private final DaoConfig studentInfoDaoConfig;
    private final DaoConfig examInfoDaoConfig;
    private final DaoConfig studentExamDaoConfig;
    private final DaoConfig examRecordDaoConfig;
    private final DaoConfig studentExamRecordDaoConfig;

    private final LoginModelDao loginModelDao;
    private final WifiModelDao wifiModelDao;
    private final TrainingModelDao trainingModelDao;
    private final StudentInfoDao studentInfoDao;
    private final ExamInfoDao examInfoDao;
    private final StudentExamDao studentExamDao;
    private final ExamRecordDao examRecordDao;
    private final StudentExamRecordDao studentExamRecordDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        loginModelDaoConfig = daoConfigMap.get(LoginModelDao.class).clone();
        loginModelDaoConfig.initIdentityScope(type);

        wifiModelDaoConfig = daoConfigMap.get(WifiModelDao.class).clone();
        wifiModelDaoConfig.initIdentityScope(type);

        trainingModelDaoConfig = daoConfigMap.get(TrainingModelDao.class).clone();
        trainingModelDaoConfig.initIdentityScope(type);

        studentInfoDaoConfig = daoConfigMap.get(StudentInfoDao.class).clone();
        studentInfoDaoConfig.initIdentityScope(type);

        examInfoDaoConfig = daoConfigMap.get(ExamInfoDao.class).clone();
        examInfoDaoConfig.initIdentityScope(type);

        studentExamDaoConfig = daoConfigMap.get(StudentExamDao.class).clone();
        studentExamDaoConfig.initIdentityScope(type);

        examRecordDaoConfig = daoConfigMap.get(ExamRecordDao.class).clone();
        examRecordDaoConfig.initIdentityScope(type);

        studentExamRecordDaoConfig = daoConfigMap.get(StudentExamRecordDao.class).clone();
        studentExamRecordDaoConfig.initIdentityScope(type);

        loginModelDao = new LoginModelDao(loginModelDaoConfig, this);
        wifiModelDao = new WifiModelDao(wifiModelDaoConfig, this);
        trainingModelDao = new TrainingModelDao(trainingModelDaoConfig, this);
        studentInfoDao = new StudentInfoDao(studentInfoDaoConfig, this);
        examInfoDao = new ExamInfoDao(examInfoDaoConfig, this);
        studentExamDao = new StudentExamDao(studentExamDaoConfig, this);
        examRecordDao = new ExamRecordDao(examRecordDaoConfig, this);
        studentExamRecordDao = new StudentExamRecordDao(studentExamRecordDaoConfig, this);

        registerDao(LoginModel.class, loginModelDao);
        registerDao(WifiModel.class, wifiModelDao);
        registerDao(TrainingModel.class, trainingModelDao);
        registerDao(StudentInfo.class, studentInfoDao);
        registerDao(ExamInfo.class, examInfoDao);
        registerDao(StudentExam.class, studentExamDao);
        registerDao(ExamRecord.class, examRecordDao);
        registerDao(StudentExamRecord.class, studentExamRecordDao);
    }
    
    public void clear() {
        loginModelDaoConfig.getIdentityScope().clear();
        wifiModelDaoConfig.getIdentityScope().clear();
        trainingModelDaoConfig.getIdentityScope().clear();
        studentInfoDaoConfig.getIdentityScope().clear();
        examInfoDaoConfig.getIdentityScope().clear();
        studentExamDaoConfig.getIdentityScope().clear();
        examRecordDaoConfig.getIdentityScope().clear();
        studentExamRecordDaoConfig.getIdentityScope().clear();
    }

    public LoginModelDao getLoginModelDao() {
        return loginModelDao;
    }

    public WifiModelDao getWifiModelDao() {
        return wifiModelDao;
    }

    public TrainingModelDao getTrainingModelDao() {
        return trainingModelDao;
    }

    public StudentInfoDao getStudentInfoDao() {
        return studentInfoDao;
    }

    public ExamInfoDao getExamInfoDao() {
        return examInfoDao;
    }

    public StudentExamDao getStudentExamDao() {
        return studentExamDao;
    }

    public ExamRecordDao getExamRecordDao() {
        return examRecordDao;
    }

    public StudentExamRecordDao getStudentExamRecordDao() {
        return studentExamRecordDao;
    }

}
