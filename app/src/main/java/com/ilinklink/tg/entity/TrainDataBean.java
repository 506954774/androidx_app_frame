package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * FileName: TrainDataBean
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/9/29 17:46
 * <p>
 * Description:
 */
public class TrainDataBean implements Serializable{

    /**
     * trainDuration : 0
     * trainCourse : 0
     * trainComplete : 0
     * trainDays : 0
     */

    private long trainDuration;
    private long trainCourse;
    private long trainComplete;
    private long trainDays;

    public long getTrainDuration() {
        return trainDuration;
    }

    public void setTrainDuration(long trainDuration) {
        this.trainDuration = trainDuration;
    }

    public long getTrainCourse() {
        return trainCourse;
    }

    public void setTrainCourse(long trainCourse) {
        this.trainCourse = trainCourse;
    }

    public long getTrainComplete() {
        return trainComplete;
    }

    public void setTrainComplete(long trainComplete) {
        this.trainComplete = trainComplete;
    }

    public long getTrainDays() {
        return trainDays;
    }

    public void setTrainDays(long trainDays) {
        this.trainDays = trainDays;
    }
}
