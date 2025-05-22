package com.example.entity;

import java.io.Serializable;

public class Student extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    // id, username, password, name, avatar, role 已从 Account 继承

    private Integer collegeId;
    private Integer specialityId;
    private Integer classId;
    private Integer score; // 学分

    // 非数据库字段，用于连接查询显示
    private String collegeName;
    private String specialityName;
    private String className;


    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public Integer getSpecialityId() {
        return specialityId;
    }

    public void setSpecialityId(Integer specialityId) {
        this.specialityId = specialityId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getSpecialityName() {
        return specialityName;
    }

    public void setSpecialityName(String specialityName) {
        this.specialityName = specialityName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}