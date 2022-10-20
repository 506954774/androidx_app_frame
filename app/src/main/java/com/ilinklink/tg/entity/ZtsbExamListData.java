package com.ilinklink.tg.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * BaiduPicResponse
 * Created By:Chuck
 * Des:
 * on 2020/11/2 17:11
 */
public class ZtsbExamListData   implements Serializable {
    private List<EmpsDTO> emps;
    private List<ContentExamEmpsDTO> contentExamEmps;
    private double face_detect_value;

    public List<EmpsDTO> getEmps() {
        return emps;
    }

    public void setEmps(List<EmpsDTO> emps) {
        this.emps = emps;
    }

    public List<ContentExamEmpsDTO> getContentExamEmps() {
        return contentExamEmps;
    }

    public void setContentExamEmps(List<ContentExamEmpsDTO> contentExamEmps) {
        this.contentExamEmps = contentExamEmps;
    }

    public double getFace_detect_value() {
        return face_detect_value;
    }

    public void setFace_detect_value(double face_detect_value) {
        this.face_detect_value = face_detect_value;
    }

    public static class EmpsDTO implements Serializable{
        private String empID;
        private String empName;
        private String empUrl;
        private Date updatedTime;

        public String getEmpID() {
            return empID;
        }

        public void setEmpID(String empID) {
            this.empID = empID;
        }

        public String getEmpName() {
            return empName;
        }

        public void setEmpName(String empName) {
            this.empName = empName;
        }

        public String getEmpUrl() {
            return empUrl;
        }

        public void setEmpUrl(String empUrl) {
            this.empUrl = empUrl;
        }

        public Date getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(Date updatedTime) {
            this.updatedTime = updatedTime;
        }


        @Override
        public String toString() {
            return "EmpsDTO{" +
                    "empID='" + empID + '\'' +
                    ", empName='" + empName + '\'' +
                    ", empUrl='" + empUrl + '\'' +
                    ", updatedTime=" + updatedTime +
                    '}';
        }
    }

    public static class ContentExamEmpsDTO implements Serializable{
        private String contentId;
        private String contentName;
        private Long limit_time;
        private List<ExamEmpsDTO> examEmps;

        public String getContentId() {
            return contentId;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

        public String getContentName() {
            return contentName;
        }

        public void setContentName(String contentName) {
            this.contentName = contentName;
        }

        public Long getLimit_time() {
            return limit_time;
        }

        public void setLimit_time(Long limit_time) {
            this.limit_time = limit_time;
        }

        public List<ExamEmpsDTO> getExamEmps() {
            return examEmps;
        }

        public void setExamEmps(List<ExamEmpsDTO> examEmps) {
            this.examEmps = examEmps;
        }

        public static class ExamEmpsDTO implements Serializable{
            private String empID;
            private String empName;
            private String empUrl;
            private String examEmpId;
            private String examResultId;

            public String getEmpID() {
                return empID;
            }

            public void setEmpID(String empID) {
                this.empID = empID;
            }

            public String getEmpName() {
                return empName;
            }

            public void setEmpName(String empName) {
                this.empName = empName;
            }

            public String getEmpUrl() {
                return empUrl;
            }

            public void setEmpUrl(String empUrl) {
                this.empUrl = empUrl;
            }

            public String getExamEmpId() {
                return examEmpId;
            }

            public void setExamEmpId(String examEmpId) {
                this.examEmpId = examEmpId;
            }

            public String getExamResultId() {
                return examResultId;
            }

            public void setExamResultId(String examResultId) {
                this.examResultId = examResultId;
            }
        }
    }
}