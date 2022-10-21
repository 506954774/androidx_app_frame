package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * Created by John on 2017/4/7.
 */

public class SubjectExamResult implements Serializable  {


    /**
     * 俯卧撑分数
     */
    private String pushUpsScore;

    /**
     * 仰卧起坐分数
     */
    private String sithUpsScore;

    /**
     * 引体向上分数
     */
    private String pullUpsScore;


    /**
     * 双杠臂屈伸分数
     */
    private String parallelBarsScore;


    public String getPushUpsScore() {
        return pushUpsScore;
    }

    public void setPushUpsScore(String pushUpsScore) {
        this.pushUpsScore = pushUpsScore;
    }

    public String getSithUpsScore() {
        return sithUpsScore;
    }

    public void setSithUpsScore(String sithUpsScore) {
        this.sithUpsScore = sithUpsScore;
    }

    public String getPullUpsScore() {
        return pullUpsScore;
    }

    public void setPullUpsScore(String pullUpsScore) {
        this.pullUpsScore = pullUpsScore;
    }

    public String getParallelBarsScore() {
        return parallelBarsScore;
    }

    public void setParallelBarsScore(String parallelBarsScore) {
        this.parallelBarsScore = parallelBarsScore;
    }

    @Override
    public String toString() {
        return "SubjectExamResult{" +
                "pushUpsScore='" + pushUpsScore + '\'' +
                ", sithUpsScore='" + sithUpsScore + '\'' +
                ", pullUpsScore='" + pullUpsScore + '\'' +
                ", parallelBarsScore='" + parallelBarsScore + '\'' +
                '}';
    }
}
