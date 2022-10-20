package com.ilinklink.tg.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * BaiduPicResponse
 * Created By:Chuck
 * Des:
 * on 2020/11/2 17:11
 */
public class BaiduPicResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * task_id : 1313844053
     * course_id : c1
     * group_id : 2
     * frame : 61
     * time_in_cam : 2.033333333333333
     * time_in_video : 1.4000
     * best_pose_id : null
     * best_pose_name : 无
     * min_distance : 1
     * score : 10
     * sim_matrix : {"1":0.4954894502629649,"2":0.18795637702748055,"3":0.38464849342363916}
     * box : [262,106,376,466]
     * points : {"nose":[317,136],"left_eye":[324,129],"right_eye":[313,129],"left_ear":[334,140],"right_ear":[303,140],"left_shoulder":[348,182],"right_shoulder":[289,182],"left_elbow":[380,220],"right_elbow":[260,224],"left_wrist":[412,259],"right_wrist":[222,259],"left_hip":[338,280],"right_hip":[299,280],"left_knee":[341,354],"right_knee":[292,354],"left_ankle":[341,428],"right_ankle":[296,424]}
     */

    private String task_id;
    private String course_id;
    private int group_id;
    private String frame;
    private double time_in_cam;
    private String time_in_video;
    private Object best_pose_id;
    private String best_pose_name;
    private int min_distance;
    private int score;
    private SimMatrixBean sim_matrix;
    private PointsBean points;
    private List<Integer> box;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public double getTime_in_cam() {
        return time_in_cam;
    }

    public void setTime_in_cam(double time_in_cam) {
        this.time_in_cam = time_in_cam;
    }

    public String getTime_in_video() {
        return time_in_video;
    }

    public void setTime_in_video(String time_in_video) {
        this.time_in_video = time_in_video;
    }

    public Object getBest_pose_id() {
        return best_pose_id;
    }

    public void setBest_pose_id(Object best_pose_id) {
        this.best_pose_id = best_pose_id;
    }

    public String getBest_pose_name() {
        return best_pose_name;
    }

    public void setBest_pose_name(String best_pose_name) {
        this.best_pose_name = best_pose_name;
    }

    public int getMin_distance() {
        return min_distance;
    }

    public void setMin_distance(int min_distance) {
        this.min_distance = min_distance;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public SimMatrixBean getSim_matrix() {
        return sim_matrix;
    }

    public void setSim_matrix(SimMatrixBean sim_matrix) {
        this.sim_matrix = sim_matrix;
    }

    public PointsBean getPoints() {
        return points;
    }

    public void setPoints(PointsBean points) {
        this.points = points;
    }

    public List<Integer> getBox() {
        return box;
    }

    public void setBox(List<Integer> box) {
        this.box = box;
    }

    public static class SimMatrixBean implements Serializable {

        private static final long serialVersionUID = 1L;
        /**
         * 1 : 0.4954894502629649
         * 2 : 0.18795637702748055
         * 3 : 0.38464849342363916
         */

        @SerializedName("1")
        private double _$1;
        @SerializedName("2")
        private double _$2;
        @SerializedName("3")
        private double _$3;

        public double get_$1() {
            return _$1;
        }

        public void set_$1(double _$1) {
            this._$1 = _$1;
        }

        public double get_$2() {
            return _$2;
        }

        public void set_$2(double _$2) {
            this._$2 = _$2;
        }

        public double get_$3() {
            return _$3;
        }

        public void set_$3(double _$3) {
            this._$3 = _$3;
        }
    }

    public static class PointsBean implements Serializable {

        private static final long serialVersionUID = 1L;
        private List<Integer> nose;
        private List<Integer> left_eye;
        private List<Integer> right_eye;
        private List<Integer> left_ear;
        private List<Integer> right_ear;
        private List<Integer> left_shoulder;
        private List<Integer> right_shoulder;
        private List<Integer> left_elbow;
        private List<Integer> right_elbow;
        private List<Integer> left_wrist;
        private List<Integer> right_wrist;
        private List<Integer> left_hip;
        private List<Integer> right_hip;
        private List<Integer> left_knee;
        private List<Integer> right_knee;
        private List<Integer> left_ankle;
        private List<Integer> right_ankle;

        public List<Integer> getNose() {
            return nose;
        }

        public void setNose(List<Integer> nose) {
            this.nose = nose;
        }

        public List<Integer> getLeft_eye() {
            return left_eye;
        }

        public void setLeft_eye(List<Integer> left_eye) {
            this.left_eye = left_eye;
        }

        public List<Integer> getRight_eye() {
            return right_eye;
        }

        public void setRight_eye(List<Integer> right_eye) {
            this.right_eye = right_eye;
        }

        public List<Integer> getLeft_ear() {
            return left_ear;
        }

        public void setLeft_ear(List<Integer> left_ear) {
            this.left_ear = left_ear;
        }

        public List<Integer> getRight_ear() {
            return right_ear;
        }

        public void setRight_ear(List<Integer> right_ear) {
            this.right_ear = right_ear;
        }

        public List<Integer> getLeft_shoulder() {
            return left_shoulder;
        }

        public void setLeft_shoulder(List<Integer> left_shoulder) {
            this.left_shoulder = left_shoulder;
        }

        public List<Integer> getRight_shoulder() {
            return right_shoulder;
        }

        public void setRight_shoulder(List<Integer> right_shoulder) {
            this.right_shoulder = right_shoulder;
        }

        public List<Integer> getLeft_elbow() {
            return left_elbow;
        }

        public void setLeft_elbow(List<Integer> left_elbow) {
            this.left_elbow = left_elbow;
        }

        public List<Integer> getRight_elbow() {
            return right_elbow;
        }

        public void setRight_elbow(List<Integer> right_elbow) {
            this.right_elbow = right_elbow;
        }

        public List<Integer> getLeft_wrist() {
            return left_wrist;
        }

        public void setLeft_wrist(List<Integer> left_wrist) {
            this.left_wrist = left_wrist;
        }

        public List<Integer> getRight_wrist() {
            return right_wrist;
        }

        public void setRight_wrist(List<Integer> right_wrist) {
            this.right_wrist = right_wrist;
        }

        public List<Integer> getLeft_hip() {
            return left_hip;
        }

        public void setLeft_hip(List<Integer> left_hip) {
            this.left_hip = left_hip;
        }

        public List<Integer> getRight_hip() {
            return right_hip;
        }

        public void setRight_hip(List<Integer> right_hip) {
            this.right_hip = right_hip;
        }

        public List<Integer> getLeft_knee() {
            return left_knee;
        }

        public void setLeft_knee(List<Integer> left_knee) {
            this.left_knee = left_knee;
        }

        public List<Integer> getRight_knee() {
            return right_knee;
        }

        public void setRight_knee(List<Integer> right_knee) {
            this.right_knee = right_knee;
        }

        public List<Integer> getLeft_ankle() {
            return left_ankle;
        }

        public void setLeft_ankle(List<Integer> left_ankle) {
            this.left_ankle = left_ankle;
        }

        public List<Integer> getRight_ankle() {
            return right_ankle;
        }

        public void setRight_ankle(List<Integer> right_ankle) {
            this.right_ankle = right_ankle;
        }
    }
}
