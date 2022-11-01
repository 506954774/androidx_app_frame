package com.ilinklink.tg.entity;

import java.io.Serializable;
import java.util.List;

/**
 * ExamInfoResponse
 * Created By:Chuck
 * Des:
 * on 2022/10/29 11:16
 */

public class ExamInfoResponse implements Serializable {


    private int exId;
    private String exTime;
    private String exName;
    private int exStatus;
    private List<SubjectsDTO> subjects;
    private List<PersonsDTO> persons;

    public int getExId() {
        return exId;
    }

    public void setExId(int exId) {
        this.exId = exId;
    }

    public String getExTime() {
        return exTime;
    }

    public void setExTime(String exTime) {
        this.exTime = exTime;
    }

    public String getExName() {
        return exName;
    }

    public void setExName(String exName) {
        this.exName = exName;
    }

    public int getExStatus() {
        return exStatus;
    }

    public void setExStatus(int exStatus) {
        this.exStatus = exStatus;
    }

    public List<SubjectsDTO> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectsDTO> subjects) {
        this.subjects = subjects;
    }

    public List<PersonsDTO> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonsDTO> persons) {
        this.persons = persons;
    }

    public static class SubjectsDTO implements Serializable{
        private int suId;
        private String suName;
        private String suDescribe;
        private int ehsDifficulty;
        private int ehsAssessmentAmount;
        private int ehsQualifiedNum;

        public int getSuId() {
            return suId;
        }

        public void setSuId(int suId) {
            this.suId = suId;
        }

        public String getSuName() {
            return suName;
        }

        public void setSuName(String suName) {
            this.suName = suName;
        }

        public String getSuDescribe() {
            return suDescribe;
        }

        public void setSuDescribe(String suDescribe) {
            this.suDescribe = suDescribe;
        }

        public int getEhsDifficulty() {
            return ehsDifficulty;
        }

        public void setEhsDifficulty(int ehsDifficulty) {
            this.ehsDifficulty = ehsDifficulty;
        }

        public int getEhsAssessmentAmount() {
            return ehsAssessmentAmount;
        }

        public void setEhsAssessmentAmount(int ehsAssessmentAmount) {
            this.ehsAssessmentAmount = ehsAssessmentAmount;
        }

        public int getEhsQualifiedNum() {
            return ehsQualifiedNum;
        }

        public void setEhsQualifiedNum(int ehsQualifiedNum) {
            this.ehsQualifiedNum = ehsQualifiedNum;
        }
    }

    public static class PersonsDTO implements Serializable{
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
    }

    @Override
    public String toString() {
        return "ExamInfoResponse{" +
                "exId=" + exId +
                ", exTime='" + exTime + '\'' +
                ", exName='" + exName + '\'' +
                ", exStatus=" + exStatus +
                ", subjects=" + subjects +
                ", persons=" + persons +
                '}';
    }
}
