package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * PersionImageRespons
 * Created By:Chuck
 * Des:
 * on 2022/10/29 16:00
 */
public class PersionImageRespons implements Serializable {


    private int peId;
    private String orgId;
    private String peName;
    private int peSex;
    private String peBirthday;
    private String peNo;
    private String pePhotoUrl;
    private String pePhotoSuffix;
    private String peCreator;
    private String peCtime;
    private String peUtime;

    public int getPeId() {
        return peId;
    }

    public void setPeId(int peId) {
        this.peId = peId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getPeName() {
        return peName;
    }

    public void setPeName(String peName) {
        this.peName = peName;
    }

    public int getPeSex() {
        return peSex;
    }

    public void setPeSex(int peSex) {
        this.peSex = peSex;
    }

    public String getPeBirthday() {
        return peBirthday;
    }

    public void setPeBirthday(String peBirthday) {
        this.peBirthday = peBirthday;
    }

    public String getPeNo() {
        return peNo;
    }

    public void setPeNo(String peNo) {
        this.peNo = peNo;
    }

    public String getPePhotoUrl() {
        return pePhotoUrl;
    }

    public void setPePhotoUrl(String pePhotoUrl) {
        this.pePhotoUrl = pePhotoUrl;
    }

    public String getPePhotoSuffix() {
        return pePhotoSuffix;
    }

    public void setPePhotoSuffix(String pePhotoSuffix) {
        this.pePhotoSuffix = pePhotoSuffix;
    }

    public String getPeCreator() {
        return peCreator;
    }

    public void setPeCreator(String peCreator) {
        this.peCreator = peCreator;
    }

    public String getPeCtime() {
        return peCtime;
    }

    public void setPeCtime(String peCtime) {
        this.peCtime = peCtime;
    }

    public String getPeUtime() {
        return peUtime;
    }

    public void setPeUtime(String peUtime) {
        this.peUtime = peUtime;
    }

    @Override
    public String toString() {
        return "PersionImageRespons{" +
                "peId=" + peId +
                ", orgId='" + orgId + '\'' +
                ", peName='" + peName + '\'' +
                ", peSex=" + peSex +
                ", peBirthday='" + peBirthday + '\'' +
                ", peNo='" + peNo + '\'' +
                ", pePhotoUrl='" + pePhotoUrl + '\'' +
                ", pePhotoSuffix='" + pePhotoSuffix + '\'' +
                ", peCreator='" + peCreator + '\'' +
                ", peCtime='" + peCtime + '\'' +
                ", peUtime='" + peUtime + '\'' +
                '}';
    }
}
