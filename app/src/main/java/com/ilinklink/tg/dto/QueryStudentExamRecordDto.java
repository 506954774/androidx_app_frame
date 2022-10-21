package com.ilinklink.tg.dto;

import java.io.Serializable;
import java.util.List;

/**
 * CourseDatails
 * Created By:Chuck
 * Des:
 * on 2020/9/19 11:19
 */
public class QueryStudentExamRecordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String examRecordId;
    private String studentUUID;

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

    @Override
    public String toString() {
        return "QueryStudentExamRecordDto{" +
                "examRecordId='" + examRecordId + '\'' +
                ", studentUUID='" + studentUUID + '\'' +
                '}';
    }
}
