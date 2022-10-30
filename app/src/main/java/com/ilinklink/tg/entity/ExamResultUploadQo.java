package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * ExamResultUploadQo
 * Created By:Chuck
 * Des:
 * on 2022/10/30 17:24
 */
public class ExamResultUploadQo implements Serializable {


    private String edExamTime;
    private int edExamineNum;
    private String edUploadDevice;
    private int ehpId;
    private int suId;

    public String getEdExamTime() {
        return edExamTime;
    }

    public void setEdExamTime(String edExamTime) {
        this.edExamTime = edExamTime;
    }

    public int getEdExamineNum() {
        return edExamineNum;
    }

    public void setEdExamineNum(int edExamineNum) {
        this.edExamineNum = edExamineNum;
    }

    public String getEdUploadDevice() {
        return edUploadDevice;
    }

    public void setEdUploadDevice(String edUploadDevice) {
        this.edUploadDevice = edUploadDevice;
    }

    public int getEhpId() {
        return ehpId;
    }

    public void setEhpId(int ehpId) {
        this.ehpId = ehpId;
    }

    public int getSuId() {
        return suId;
    }

    public void setSuId(int suId) {
        this.suId = suId;
    }

    @Override
    public String toString() {
        return "ExamResultUploadQo{" +
                "edExamTime='" + edExamTime + '\'' +
                ", edExamineNum=" + edExamineNum +
                ", edUploadDevice='" + edUploadDevice + '\'' +
                ", ehpId=" + ehpId +
                ", suId=" + suId +
                '}';
    }
}
