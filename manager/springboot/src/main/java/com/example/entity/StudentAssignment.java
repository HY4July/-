// com/example/entity/StudentAssignment.java
package com.example.entity;

import java.io.Serializable;
// import java.util.Date;
import java.sql.Timestamp;

public class StudentAssignment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer assignmentId; // 对应 AssignmentInfo 的 ID
    private Integer studentId;
    private String submissionContent;
    private String submissionFilePath;
    private String submissionTime; // "yyyy-MM-dd HH:mm:ss"
    private String status; // e.g., PENDING_REVIEW, REVIEWED, LATE
    private String score;
    private String teacherComment;
    private String gradedTime; // "yyyy-MM-dd HH:mm:ss"
    private String gradedFilePath;

    // 非数据库字段
    private String studentName;
    private String assignmentTitle; // 作业标题，方便列表展示
    private String courseName; // 作业所属课程名称

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer assignmentId) { this.assignmentId = assignmentId; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getSubmissionContent() { return submissionContent; }
    public void setSubmissionContent(String submissionContent) { this.submissionContent = submissionContent; }
    public String getSubmissionFilePath() { return submissionFilePath; }
    public void setSubmissionFilePath(String submissionFilePath) { this.submissionFilePath = submissionFilePath; }
    public String getSubmissionTime() { return submissionTime; }
    public void setSubmissionTime(String submissionTime) { this.submissionTime = submissionTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }
    public String getTeacherComment() { return teacherComment; }
    public void setTeacherComment(String teacherComment) { this.teacherComment = teacherComment; }
    public String getGradedTime() { return gradedTime; }
    public void setGradedTime(String gradedTime) { this.gradedTime = gradedTime; }
    public String getGradedFilePath() { return gradedFilePath; }
    public void setGradedFilePath(String gradedFilePath) { this.gradedFilePath = gradedFilePath; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getAssignmentTitle() { return assignmentTitle; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
}