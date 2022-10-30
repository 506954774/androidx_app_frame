package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * ExamHasStu
 * Created By:Chuck
 * Des:
 * on 2022/10/30 18:21
 */
public class ExamHasStu implements Serializable {


    private int peId;
    private int ehpId;
    private String peName;
    private String peNo;

    public int getPeId() {
        return peId;
    }

    public void setPeId(int peId) {
        this.peId = peId;
    }

    public int getEhpId() {
        return ehpId;
    }

    public void setEhpId(int ehpId) {
        this.ehpId = ehpId;
    }

    public String getPeName() {
        return peName;
    }

    public void setPeName(String peName) {
        this.peName = peName;
    }

    public String getPeNo() {
        return peNo;
    }

    public void setPeNo(String peNo) {
        this.peNo = peNo;
    }

    @Override
    public String toString() {
        return "ExamHasStu{" +
                "peId=" + peId +
                ", ehpId=" + ehpId +
                ", peName='" + peName + '\'' +
                ", peNo='" + peNo + '\'' +
                '}';
    }
}
