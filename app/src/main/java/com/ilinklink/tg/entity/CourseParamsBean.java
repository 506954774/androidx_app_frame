package com.ilinklink.tg.entity;

import java.io.Serializable;

/**
 * FileName: CourseParamsBean
 * <p>
 * Author: WuJH
 * <p>
 * Date: 2020/9/28 15:42
 * <p>
 * Description:
 */
public class CourseParamsBean implements Serializable{

    /**
     courseCategory (integer): 课程品类 ,
     order (integer): 排序方式 0-默认 1-最近更新 2-最热门 ,
     pageIndex (integer): 页码 从1开始 ,
     pageSize (integer): 每页的数量 ,
     platform (integer): 平台 1-终端 2-手机App ,
     query (string): 查询关键字 ,
     trainDegree (integer): 训练难度 ,
     trainPlace (integer): 训练部位 ,
     trainTarget (integer): 训练目标
     */
    private int courseCategory;
    private int order;
    private int pageIndex;
    private int pageSize;
    private int platform;
    private String query;
    private int trainDegree;
    private int trainPlace;
    private int trainTarget;

    public int getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(int courseCategory) {
        this.courseCategory = courseCategory;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTrainDegree() {
        return trainDegree;
    }

    public void setTrainDegree(int trainDegree) {
        this.trainDegree = trainDegree;
    }

    public int getTrainPlace() {
        return trainPlace;
    }

    public void setTrainPlace(int trainPlace) {
        this.trainPlace = trainPlace;
    }

    public int getTrainTarget() {
        return trainTarget;
    }

    public void setTrainTarget(int trainTarget) {
        this.trainTarget = trainTarget;
    }
}
