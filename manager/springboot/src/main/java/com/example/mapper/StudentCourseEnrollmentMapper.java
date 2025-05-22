// com/example/mapper/StudentCourseEnrollmentMapper.java
package com.example.mapper;

import com.example.entity.StudentCourseEnrollment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface StudentCourseEnrollmentMapper {
    int insert(StudentCourseEnrollment enrollment);
    int deleteById(Integer id);
    // 通常基于 studentId 和 courseId 来删除或更新状态
    int deleteByStudentAndCourse(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);
    int updateStatus(StudentCourseEnrollment enrollment); // 例如只更新状态
    StudentCourseEnrollment selectByStudentAndCourse(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);
    List<StudentCourseEnrollment> selectByStudentId(Integer studentId);
    List<StudentCourseEnrollment> selectByCourseId(Integer courseId);
    List<StudentCourseEnrollment> selectAll(StudentCourseEnrollment enrollment); // 可带条件查询
}