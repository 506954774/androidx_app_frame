package com.ilinklink.tg.entity;

import java.io.Serializable;
import java.util.List;

/**
 * TrainingData
 * Created By:Chuck
 * Des:
 * on 2020/9/29 17:13
 */
public class TrainingData implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * courseCover : string
     * courseId : string
     * courseName : string
     * courseNo : string
     * trainDegree : string
     * trainDuration : 0
     * trainGroupData : [{"armUse":0,"failData":[{"failImgEnd":"string","failImgStart":"string","failTimeEnd":"string","failTimeStart":"string"}],"groupId":"string","groupName":"string","leftArm":0,"rightArm":0,"trainDuration":0,"trainScore":0}]
     * trainPlace : string
     * trainScore : 0
     * trainScores : [0]
     * trainTarget : string
     */

    private String courseCover;
    private String courseId;
    private String courseName;
    private String courseNo;
    private String trainDegree;
    private int trainDuration;
    private String trainPlace;
    private int trainScore;
    private String trainTarget;
    private List<TrainGroupDataBean> trainGroupData;
    private List<Integer> trainScores;
    private long utime;

    public long getUtime() {
        return utime;
    }

    public void setUtime(long utime) {
        this.utime = utime;
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

    public String getTrainDegree() {
        return trainDegree;
    }

    public void setTrainDegree(String trainDegree) {
        this.trainDegree = trainDegree;
    }

    public int getTrainDuration() {
        return trainDuration;
    }

    public void setTrainDuration(int trainDuration) {
        this.trainDuration = trainDuration;
    }

    public String getTrainPlace() {
        return trainPlace;
    }

    public void setTrainPlace(String trainPlace) {
        this.trainPlace = trainPlace;
    }

    public int getTrainScore() {
        return trainScore;
    }

    public void setTrainScore(int trainScore) {
        this.trainScore = trainScore;
    }

    public String getTrainTarget() {
        return trainTarget;
    }

    public void setTrainTarget(String trainTarget) {
        this.trainTarget = trainTarget;
    }

    public List<TrainGroupDataBean> getTrainGroupData() {
        return trainGroupData;
    }

    public void setTrainGroupData(List<TrainGroupDataBean> trainGroupData) {
        this.trainGroupData = trainGroupData;
    }

    public List<Integer> getTrainScores() {
        return trainScores;
    }

    public void setTrainScores(List<Integer> trainScores) {
        this.trainScores = trainScores;
    }

    public static class TrainGroupDataBean implements Serializable {

        private static final long serialVersionUID = 1L;
        /**
         * armUse : 0
         * failData : [{"failImgEnd":"string","failImgStart":"string","failTimeEnd":"string","failTimeStart":"string"}]
         * groupId : string
         * groupName : string
         * leftArm : 0
         * rightArm : 0
         * trainDuration : 0
         * trainScore : 0
         */

        private int armUse;
        private String groupId;
        private String groupName;
        private int leftArm;
        private int rightArm;
        private int trainDuration;
        private int trainScore;
        private List<FailDataBean> failData;

        public int getArmUse() {
            return armUse;
        }

        public void setArmUse(int armUse) {
            this.armUse = armUse;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public int getLeftArm() {
            return leftArm;
        }

        public void setLeftArm(int leftArm) {
            this.leftArm = leftArm;
        }

        public int getRightArm() {
            return rightArm;
        }

        public void setRightArm(int rightArm) {
            this.rightArm = rightArm;
        }

        public int getTrainDuration() {
            return trainDuration;
        }

        public void setTrainDuration(int trainDuration) {
            this.trainDuration = trainDuration;
        }

        public int getTrainScore() {
            return trainScore;
        }

        public void setTrainScore(int trainScore) {
            this.trainScore = trainScore;
        }

        public List<FailDataBean> getFailData() {
            return failData;
        }

        public void setFailData(List<FailDataBean> failData) {
            this.failData = failData;
        }

        public static class FailDataBean implements Serializable {

            private static final long serialVersionUID = 1L;
            /**
             * failImgEnd : string
             * failImgStart : string
             * failTimeEnd : string
             * failTimeStart : string
             */

            private String failImgEnd;
            private String failImgStart;
            private String failTimeEnd;
            private String failTimeStart;

            public String getFailImgEnd() {
                return failImgEnd;
            }

            public void setFailImgEnd(String failImgEnd) {
                this.failImgEnd = failImgEnd;
            }

            public String getFailImgStart() {
                return failImgStart;
            }

            public void setFailImgStart(String failImgStart) {
                this.failImgStart = failImgStart;
            }

            public String getFailTimeEnd() {
                return failTimeEnd;
            }

            public void setFailTimeEnd(String failTimeEnd) {
                this.failTimeEnd = failTimeEnd;
            }

            public String getFailTimeStart() {
                return failTimeStart;
            }

            public void setFailTimeStart(String failTimeStart) {
                this.failTimeStart = failTimeStart;
            }
        }
    }
}
