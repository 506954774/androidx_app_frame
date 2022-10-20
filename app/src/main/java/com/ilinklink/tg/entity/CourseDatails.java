package com.ilinklink.tg.entity;

import java.io.Serializable;
import java.util.List;

/**
 * CourseDatails
 * Created By:Chuck
 * Des:
 * on 2020/9/19 11:19
 */
public class CourseDatails implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * collect : 0
     * completeCount : 0
     * courseCover : string
     * courseDesc : string
     * courseGroupList : [{"actionCount":0,"armUse":0,"armValue":0,"endTime":0,"groupDesc":"string","groupId":"string","groupName":"string","groupOrder":0,"startTime":0,"videoDuration":0,"videoId":"string"}]
     * courseName : string
     * courseNo : string
     * courseOnSale : 0
     * courseRemove : 0
     * trainDegree : string
     * trainPlace : string
     * trainTarget : string
     * updateTime : 2020-09-19T01:58:04.175Z
     * videoDuration : 0
     * videoId : string
     */

    private String mCourseId;
    private int collect;
    private int completeCount;
    private String courseCover;
    private String courseDesc;
    private String courseName;
    private String courseNo;
    private int courseOnSale;
    private int courseRemove;
    private String trainDegree;
    private String trainPlace;
    private String trainTarget;
    private String updateTime;
    private int videoDuration;
    private int videoSize;
    private String videoId;
    private String videoFileUrl ;
    private List<CourseGroupListBean> courseGroupList;

    public String getmCourseId() {
        return mCourseId;
    }

    public void setmCourseId(String mCourseId) {
        this.mCourseId = mCourseId;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoFileUrl() {
        return videoFileUrl;
    }

    public void setVideoFileUrl(String videoFileUrl) {
        this.videoFileUrl = videoFileUrl;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public String getCourseCover() {
        return courseCover;
    }

    public void setCourseCover(String courseCover) {
        this.courseCover = courseCover;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
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

    public int getCourseOnSale() {
        return courseOnSale;
    }

    public void setCourseOnSale(int courseOnSale) {
        this.courseOnSale = courseOnSale;
    }

    public int getCourseRemove() {
        return courseRemove;
    }

    public void setCourseRemove(int courseRemove) {
        this.courseRemove = courseRemove;
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public List<CourseGroupListBean> getCourseGroupList() {
        return courseGroupList;
    }

    public void setCourseGroupList(List<CourseGroupListBean> courseGroupList) {
        this.courseGroupList = courseGroupList;
    }

    public static class CourseGroupListBean implements Serializable {

        private static final long serialVersionUID = 1L;
        /**
         * actionCount : 0
         * armUse : 0
         * armValue : 0
         * endTime : 0
         * groupDesc : string
         * groupId : string
         * groupName : string
         * groupOrder : 0
         * startTime : 0
         * videoDuration : 0
         * videoId : string
         */

        private int actionCount;
        private int armUse;
        private int armValue;
        private int endTime;
        private String groupDesc;
        private String groupId;
        private String groupName;
        private int groupOrder;
        private int startTime;
        private int videoDuration;
        private String videoId;
        private String videoCover ;
        private String videoFileUrl ;
        private boolean isExpanded;

        public String getVideoCover() {
            return videoCover;
        }

        public void setVideoCover(String videoCover) {
            this.videoCover = videoCover;
        }

        public String getVideoFileUrl() {
            return videoFileUrl;
        }

        public void setVideoFileUrl(String videoFileUrl) {
            this.videoFileUrl = videoFileUrl;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }



        public int getActionCount() {
            return actionCount;
        }

        public void setActionCount(int actionCount) {
            this.actionCount = actionCount;
        }

        public int getArmUse() {
            return armUse;
        }

        public void setArmUse(int armUse) {
            this.armUse = armUse;
        }

        public int getArmValue() {
            return armValue;
        }

        public void setArmValue(int armValue) {
            this.armValue = armValue;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public String getGroupDesc() {
            return groupDesc;
        }

        public void setGroupDesc(String groupDesc) {
            this.groupDesc = groupDesc;
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

        public int getGroupOrder() {
            return groupOrder;
        }

        public void setGroupOrder(int groupOrder) {
            this.groupOrder = groupOrder;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getVideoDuration() {
            return videoDuration;
        }

        public void setVideoDuration(int videoDuration) {
            this.videoDuration = videoDuration;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }
    }

    @Override
    public String toString() {
        return "CourseDatails{" +
                "collect=" + collect +
                ", completeCount=" + completeCount +
                ", courseCover='" + courseCover + '\'' +
                ", courseDesc='" + courseDesc + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseNo='" + courseNo + '\'' +
                ", courseOnSale=" + courseOnSale +
                ", courseRemove=" + courseRemove +
                ", trainDegree='" + trainDegree + '\'' +
                ", trainPlace='" + trainPlace + '\'' +
                ", trainTarget='" + trainTarget + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", videoDuration=" + videoDuration +
                ", videoSize=" + videoSize +
                ", videoId='" + videoId + '\'' +
                ", videoFileUrl='" + videoFileUrl + '\'' +
                ", courseGroupList=" + courseGroupList +
                '}';
    }
}
