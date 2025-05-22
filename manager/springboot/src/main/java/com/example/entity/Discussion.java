package com.example.entity;

import java.io.Serializable;
// 如果决定使用 java.util.Date 或 java.time.LocalDateTime，请取消注释相应的导入
// import java.util.Date;
// import java.time.LocalDateTime;

public class Discussion implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer studentId;
    private String content;
    private String createTime; // 对应 DATETIME，建议使用 String 格式 "yyyy-MM-dd HH:mm:ss" 或 LocalDateTime
    private String reply;
    private Integer courseId;

    // 非数据库字段，用于前端展示关联信息
    private String studentName;
    private String courseName;
    private String studentAvatar; // 新增：提问学生的头像，便于前端显示

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentAvatar() {
        return studentAvatar;
    }

    public void setStudentAvatar(String studentAvatar) {
        this.studentAvatar = studentAvatar;
    }
}