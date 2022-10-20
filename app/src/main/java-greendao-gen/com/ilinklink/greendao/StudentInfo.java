package com.ilinklink.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table STUDENT_INFO.
 */
public class StudentInfo {

    private Long id;
    private String studentUUID;
    private String name;
    private String imageUrl;
    private String desc;
    private Long updateTime;
    private Long imageDownloadTime;

    public StudentInfo() {
    }

    public StudentInfo(Long id) {
        this.id = id;
    }

    public StudentInfo(Long id, String studentUUID, String name, String imageUrl, String desc, Long updateTime, Long imageDownloadTime) {
        this.id = id;
        this.studentUUID = studentUUID;
        this.name = name;
        this.imageUrl = imageUrl;
        this.desc = desc;
        this.updateTime = updateTime;
        this.imageDownloadTime = imageDownloadTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentUUID() {
        return studentUUID;
    }

    public void setStudentUUID(String studentUUID) {
        this.studentUUID = studentUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getImageDownloadTime() {
        return imageDownloadTime;
    }

    public void setImageDownloadTime(Long imageDownloadTime) {
        this.imageDownloadTime = imageDownloadTime;
    }

}
