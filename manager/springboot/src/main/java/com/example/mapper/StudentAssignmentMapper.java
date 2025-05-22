// com/example/mapper/StudentAssignmentMapper.java
package com.example.mapper;

import com.example.entity.StudentAssignment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface StudentAssignmentMapper {
    int insert(StudentAssignment studentAssignment);
    int updateById(StudentAssignment studentAssignment); // For grading
    StudentAssignment selectById(Integer id);
    StudentAssignment selectByAssignmentAndStudent(@Param("assignmentId") Integer assignmentId, @Param("studentId") Integer studentId);
    List<StudentAssignment> selectByAssignmentId(@Param("assignmentId") Integer assignmentId); // Get all submissions for an assignment
    List<StudentAssignment> selectByStudentId(@Param("studentId") Integer studentId); // Get all assignments submitted by a student
    List<StudentAssignment> selectAll(StudentAssignment studentAssignment); // For filtering/admin view
    int deleteById(Integer id); // if needed
    int deleteByAssignmentId(@Param("assignmentId") Integer assignmentId);
}