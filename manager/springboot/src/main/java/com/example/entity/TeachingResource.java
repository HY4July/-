// com/example/entity/TeachingResource.java
package com.example.entity;

import java.io.Serializable;
// import java.util.Date; // 如果 upload_time 用 Date 类型
import java.sql.Timestamp; // 或者用 Timestamp，或者用 String

public class TeachingResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String type; // 文件后缀或自定义类型
    private String path; // 文件URL
    private Integer teacherId;
    private Integer courseId;
    private String uploadTime; // 格式化后的时间字符串，例如 "yyyy-MM-dd HH:mm:ss"
    private String description;
    private Long size; // 文件大小，字节
    private Integer downloads; // 下载次数

    // 非数据库字段，用于前端展示
    private String teacherName;
    private String courseName;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}