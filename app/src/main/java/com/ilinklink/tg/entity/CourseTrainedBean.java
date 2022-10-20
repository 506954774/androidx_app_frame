package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * Created by John on 2017/4/7.
 */

public class CourseTrainedBean implements Serializable  {
    private static final long serialVersionUID = 1L;


    /**
     * collectCount : 0
     * completeCount : 0
     * completeTime : 2020-09-21T01:43:16.838Z
     * courseCover : string
     * courseId : string
     * courseName : string
     * courseNo : string
     * trainDuration : 0
     * trainId : string
     * trainScore : 0
     */

    private int collectCount;
    private int completeCount;
    private String completeTime;
    private String courseCover;
    private String courseId;
    private String courseName;
    private String courseNo;
    private int trainDuration;
    private String trainId;
    private int trainScore;

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getCourseCover() {
        return courseCover;
    }

    public void setCourseCover(String courseCover) {
        this.courseCover = courseCover;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(String courseNo) {
        this.courseNo = courseNo;
    }

    public int getTrainDuration() {
        return trainDuration;
    }

    public void setTrainDuration(int trainDuration) {
        this.trainDuration = trainDuration;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public int getTrainScore() {
        return trainScore;
    }

    public void setTrainScore(int trainScore) {
        this.trainScore = trainScore;
    }
}
