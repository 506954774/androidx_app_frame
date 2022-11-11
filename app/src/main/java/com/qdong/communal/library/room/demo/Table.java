package com.qdong.communal.library.room.demo;

/**
 * Table
 * Created By:Chuck
 * Des:
 * on 2022/9/18 18:32
 */

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 想要创建一个表
 * 主键唯一 自动增长（auto）
 *
 */
@Entity
public class Table {
    //设置主键，自动增长
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String course_name;
    private String teacher_name;
    private String course_adress;
    //构造方法
    public Table(String course_name, String teacher_name, String course_adress) {
        this.course_name = course_name;
        this.teacher_name = teacher_name;
        this.course_adress = course_adress;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getCourse_adress() {
        return course_adress;
    }

    public void setCourse_adress(String course_adress) {
        this.course_adress = course_adress;
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", course_name='" + course_name + '\'' +
                ", teacher_name='" + teacher_name + '\'' +
                ", course_adress='" + course_adress + '\'' +
                '}';
    }
}


