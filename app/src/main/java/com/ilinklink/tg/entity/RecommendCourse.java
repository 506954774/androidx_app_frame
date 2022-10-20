package com.ilinklink.tg.entity;

import java.io.Serializable;
import java.util.List;

/**
 * RecommendCourse
 * Created By:Chuck
 * Des:
 * on 2020/9/19 16:53
 */
public class RecommendCourse implements Serializable {

    private static final long serialVersionUID = -5204676011139121597L;



    /**
     * count : 7
     * courseList : [{"courseId":"9a5aa01eb7c64ff3b84043a1671477d5","courseNo":"200822103948","courseName":"零基础健塑形-1","courseCover":"http://www.ilinklink.com/mage1/M00/00/15/rBJz-V9Z5zeAQF6rABU_IW4c6H0721.jpg","collectCount":1,"completeCount":0},{"courseId":"e645ec90794540f7bc0802b6377bde16","courseNo":"200903112708","courseName":"零基础健塑形-2","courseCover":"http://www.ilinklink.com/mage1/M00/00/15/rBJz-V9Z5weASK9_ABPF7mgJSk4876.jpg","collectCount":0,"completeCount":0},{"courseId":"e645ec90794540f7bc0802b6377bde16","courseNo":"200903112708","courseName":"零基础健塑形-2","courseCover":"http://www.ilinklink.com/mage1/M00/00/15/rBJz-V9Z5viAd7D0ABD2Tn6LBTc098.png","collectCount":0,"completeCount":0},{"courseId":"e645ec90794540f7bc0802b6377bde16","courseNo":"200903112708","courseName":"零基础健塑形-2","courseCover":"http://www.ilinklink.com/mage1/M00/00/15/rBJz-V9Z50KAYoBKAANbJjJSKzw369.jpg","collectCount":0,"completeCount":0},{"courseId":"9a5aa01eb7c64ff3b84043a1671477d5","courseNo":"200822103948","courseName":"零基础健塑形-1","courseCover":"http://www.ilinklink.com/mage1/M00/00/15/rBJz-V9Z51CALBwgAANydbSVNmc852.jpg","collectCount":1,"completeCount":0},{"courseId":"e645ec90794540f7bc0802b6377bde16","courseNo":"200903112708","courseName":"零基础健塑形-2","courseCover":"http://www.ilinklink.com/mage1/M00/00/15/rBJz-V9Z51yAHc40AASPmTYHE7A758.png","collectCount":0,"completeCount":0},{"courseId":"9a5aa01eb7c64ff3b84043a1671477d5","courseNo":"200822103948","courseName":"零基础健塑形-1","courseCover":"http://www.ilinklink.com/mage1/M00/00/15/rBJz-V9Z53-AUAHgAAAsbTEtVyw990.jpg","collectCount":1,"completeCount":0}]
     */

    private int count;
    private List<CourseListBean> courseList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CourseListBean> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseListBean> courseList) {
        this.courseList = courseList;
    }

    public static class CourseListBean implements Serializable {

        private static final long serialVersionUID = -5204676011139121597L;

        /**
         * courseId : 9a5aa01eb7c64ff3b84043a1671477d5
         * courseNo : 200822103948
         * courseName : 零基础健塑形-1
         * courseCover : http://www.ilinklink.com/mage1/M00/00/15/rBJz-V9Z5zeAQF6rABU_IW4c6H0721.jpg
         * collectCount : 1
         * completeCount : 0
         */

        private String courseId;
        private String courseNo;
        private String courseName;
        private String courseCover;
        private int collectCount;
        private String completeTime;
        private int completeCount;
        private String trainDegree;
        private String trainPlace;
        private String trainTarget;
        private String trainDuration;
        private String trainId;
        private String trainScore;
        private int courseRemove;//删除标志 0-未删除 1-已删除
        private int courseOnSale;//课程上架状态 0-未上架 1-已上架

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCourseNo() {
            return courseNo;
        }

        public void setCourseNo(String courseNo) {
            this.courseNo = courseNo;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCourseCover() {
            return courseCover;
        }

        public void setCourseCover(String courseCover) {
            this.courseCover = courseCover;
        }

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

        public String getTrainDegree() {
            return trainDegree;
        }

        public void setTrainDegree(String trainDegree) {
            this.trainDegree = trainDegree;
        }

        public String getTrainPlace() {
            return trainPlace;
        }

        public void setTrainPlace(String trainPlace) {
            this.trainPlace = trainPlace;
        }

        public String getTrainTarget() {
            return trainTarget;
        }

        public void setTrainTarget(String trainTarget) {
            this.trainTarget = trainTarget;
        }

        public String getTrainDuration() {
            return trainDuration;
        }

        public void setTrainDuration(String trainDuration) {
            this.trainDuration = trainDuration;
        }

        public String getTrainId() {
            return trainId;
        }

        public void setTrainId(String trainId) {
            this.trainId = trainId;
        }

        public String getTrainScore() {
            return trainScore;
        }

        public void setTrainScore(String trainScore) {
            this.trainScore = trainScore;
        }

        public String getCompleteTime() {
            return completeTime;
        }

        public void setCompleteTime(String completeTime) {
            this.completeTime = completeTime;
        }

        public int getCourseRemove() {
            return courseRemove;
        }

        public void setCourseRemove(int courseRemove) {
            this.courseRemove = courseRemove;
        }

        public int getCourseOnSale() {
            return courseOnSale;
        }

        public void setCourseOnSale(int courseOnSale) {
            this.courseOnSale = courseOnSale;
        }
    }
}
