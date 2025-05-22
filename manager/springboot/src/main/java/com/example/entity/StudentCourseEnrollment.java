// com/example/entity/StudentCourseEnrollment.java
package com.example.entity;

import java.io.Serializable;
import java.util.Date; // 使用 java.util.Date 或 String 类型，根据数据库 DATETIME 对应

public class StudentCourseEnrollment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer studentId;
    private Integer courseId;
    private String enrollmentDate; // 对应 DATETIME，用 String 格式化 "yyyy-MM-dd HH:mm:ss"
    private String status;

    // 非数据库字段，用于列表显示
    private String studentName;
    private String courseName;

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

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}