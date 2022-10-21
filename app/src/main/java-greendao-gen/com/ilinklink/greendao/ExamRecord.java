package com.ilinklink.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table EXAM_RECORD.
 */
public class ExamRecord {

    /**
     * 系统生成的id
     */
    private Long id;
    /**
     * 记录id
     */
    private String examRecordId;
    /**
     * 考试UUID
     */
    private String examUUID;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String desc;
    /**
     * 预留字段
     */
    private String reservedColumn;
    /**
     * 预留字段2
     */
    private String reservedColumn2;
    /**
     * 考试时间  yyyy-MM-dd HH:mm:ss
     */
    private String examTime;

    public ExamRecord() {
    }

    public ExamRecord(Long id) {
        this.id = id;
    }

    public ExamRecord(Long id, String examRecordId, String examUUID, String name, String desc, String reservedColumn, String reservedColumn2, String examTime) {
        this.id = id;
        this.examRecordId = examRecordId;
        this.examUUID = examUUID;
        this.name = name;
        this.desc = desc;
        this.reservedColumn = reservedColumn;
        this.reservedColumn2 = reservedColumn2;
        this.examTime = examTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExamRecordId() {
        return examRecordId;
    }

    public void setExamRecordId(String examRecordId) {
        this.examRecordId = examRecordId;
    }

    public String getExamUUID() {
        return examUUID;
    }

    public void setExamUUID(String examUUID) {
        this.examUUID = examUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    @Override
    public String toString() {
        return "ExamRecord{" +
                "id=" + id +
                ", examRecordId='" + examRecordId + '\'' +
                ", examUUID='" + examUUID + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", reservedColumn='" + reservedColumn + '\'' +
                ", reservedColumn2='" + reservedColumn2 + '\'' +
                ", examTime='" + examTime + '\'' +
                '}';
    }
}
