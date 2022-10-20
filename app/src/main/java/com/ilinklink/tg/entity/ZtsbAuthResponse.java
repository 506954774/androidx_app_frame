package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * BaiduPicResponse
 * Created By:Chuck
 * Des:
 * on 2020/11/2 17:11
 */
public class ZtsbAuthResponse implements Serializable {

    /**
     {
     "id": "025ab50f-cc4e-30a7-bf61-ed674fc7d69e",
     "name": "系统管理员",
     "gender": 1,
     "idNumber": "372330199402030102",
     "nation": "彝族",
     "rank": "正营级",
     "topDeptID": "017330b3-17e7-3c15-8968-32b485ad5463",
     "deptID": "017330b3-17e7-3c15-8968-32b485ad5463",
     "deptName": "七营",
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTQzMTEwMjksImlhdCI6MTY1MzEwMTQyOSwiaWQiOiIwMjVhYjUwZi1jYzRlLTMwYTctYmY2MS1lZDY3NGZjN2Q2OWUiLCJuYmYiOjE2NTMxMDE0MjksInVzZXJuYW1lIjoiYWRtaW4ifQ.xPJrrN36Z6RoOdwYICx_YNT-7O_ycVG_j63EIX37iss"
     }
     */

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private int gender;
    private String idNumber;
    private String nation;
    private String rank;
    private String topDeptID;
    private String deptID;
    private String deptName;
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getTopDeptID() {
        return topDeptID;
    }

    public void setTopDeptID(String topDeptID) {
        this.topDeptID = topDeptID;
    }

    public String getDeptID() {
        return deptID;
    }

    public void setDeptID(String deptID) {
        this.deptID = deptID;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toString() {
        return "ZtsbAuthResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", idNumber='" + idNumber + '\'' +
                ", nation='" + nation + '\'' +
                ", rank='" + rank + '\'' +
                ", topDeptID='" + topDeptID + '\'' +
                ", deptID='" + deptID + '\'' +
                ", deptName='" + deptName + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
