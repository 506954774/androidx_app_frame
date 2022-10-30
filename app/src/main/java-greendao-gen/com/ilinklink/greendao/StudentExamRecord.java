package com.ilinklink.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table STUDENT_EXAM_RECORD.
 */
public class StudentExamRecord {

    /**
     * 系统生成的id
     */
    private Long id;
    /**
     * 学生考试记录
     */
    private String studentExamRecordId;
    /**
     * 关联的考试记录id
     */
    private String examRecordId;
    /**
     * 学生id
     */
    private String studentUUID;
    /**
     * 学生名称
     */
    private String studentName;
    /**
     * 考试时间
     */
    private String examTime;
    /**
     * 得分，json串，每个科目的个数  {@link com.ilinklink.tg.entity.SubjectExamResult}
     */
    private String subResultJson;
    /**
     * 描述 实际存考试的科目id信息
     */
    private String desc;
    /**
     * 预留字段 实际值：学生的编号
     */
    private String reservedColumn;
    /**
     * 预留字段2 实际值：此数据是否已经上传到服务器
     */
    private String reservedColumn2;

    public StudentExamRecord() {
    }

    public StudentExamRecord(Long id) {
        this.id = id;
    }

    public StudentExamRecord(Long id, String studentExamRecordId, String examRecordId, String studentUUID, String studentName, String examTime, String subResultJson, String desc, String reservedColumn, String reservedColumn2) {
        this.id = id;
        this.studentExamRecordId = studentExamRecordId;
        this.examRecordId = examRecordId;
        this.studentUUID = studentUUID;
        this.studentName = studentName;
        this.examTime = examTime;
        this.subResultJson = subResultJson;
        this.desc = desc;
        this.reservedColumn = reservedColumn;
        this.reservedColumn2 = reservedColumn2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentExamRecordId() {
        return studentExamRecordId;
    }

    public void setStudentExamRecordId(String studentExamRecordId) {
        this.studentExamRecordId = studentExamRecordId;
    }

    public String getExamRecordId() {
        return examRecordId;
    }

    public void setExamRecordId(String examRecordId) {
        this.examRecordId = examRecordId;
    }

    public String getStudentUUID() {
        return studentUUID;
    }

    public void setStudentUUID(String studentUUID) {
        this.studentUUID = studentUUID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getSubResultJson() {
        return subResultJson;
    }

    public void setSubResultJson(String subResultJson) {
        this.subResultJson = subResultJson;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getReservedColumn() {
        return reservedColumn;
    }

    public void setReservedColumn(String reservedColumn) {
        this.reservedColumn = reservedColumn;
    }

    public String getReservedColumn2() {
        return reservedColumn2;
    }

    public void setReservedColumn2(String reservedColumn2) {
        this.reservedColumn2 = reservedColumn2;
    }

    @Override
    public String toString() {
        return "StudentExamRecord{" +
                "id=" + id +
                ", studentExamRecordId='" + studentExamRecordId + '\'' +
                ", examRecordId='" + examRecordId + '\'' +
                ", studentUUID='" + studentUUID + '\'' +
                ", studentName='" + studentName + '\'' +
                ", examTime='" + examTime + '\'' +
                ", subResultJson='" + subResultJson + '\'' +
                ", desc='" + desc + '\'' +
                ", reservedColumn='" + reservedColumn + '\'' +
                ", reservedColumn2='" + reservedColumn2 + '\'' +
                '}';
    }
}
