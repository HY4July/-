// com/example/entity/AssignmentInfo.java
package com.example.entity;

import java.io.Serializable;
// import java.util.Date; // 或 String
import java.sql.Timestamp;


public class AssignmentInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private String content;
    private Integer courseId;
    private Integer teacherId;
    private String publishTime; // "yyyy-MM-dd HH:mm:ss"
    private String dueTime;     // "yyyy-MM-dd HH:mm:ss"
    private String attachmentPath;
    private String studentSubmissionStatus;
    private String studentScore;
    private Integer studentSubmissionId; // 学生提交记录的ID，方便学生跳转到自己的提交详情

    // 非数据库字段
    private String courseName;
    private String teacherName;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
    public String getPublishTime() { return publishTime; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }
    public String getDueTime() { return dueTime; }
    public void setDueTime(String dueTime) { this.dueTime = dueTime; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getStudentSubmissionStatus() { return studentSubmissionStatus; }
    public void setStudentSubmissionStatus(String studentSubmissionStatus) { this.studentSubmissionStatus = studentSubmissionStatus; }
    public String getStudentScore() { return studentScore; }
    public void setStudentScore(String studentScore) { this.studentScore = studentScore; }
    public Integer getStudentSubmissionId() { return studentSubmissionId; }
    public void setStudentSubmissionId(Integer studentSubmissionId) { this.studentSubmissionId = studentSubmissionId; }
}