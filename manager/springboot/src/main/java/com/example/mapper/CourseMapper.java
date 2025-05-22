// com/example/mapper/CourseMapper.java
package com.example.mapper;

import com.example.entity.Course;
import org.apache.ibatis.annotations.Param; // 新增导入
import java.util.List;

public interface CourseMapper {
    int insert(Course course);
    int deleteById(Integer id);
    int updateById(Course course);
    Course selectById(Integer id);
    List<Course> selectAll(Course course); // 可带条件查询，例如按课程名、教师ID、状态等
    List<Course> selectByTeacherId(Integer teacherId);

    // ===== 新增的方法声明开始 =====
    /**
     * 查询指定学生已选修的课程列表
     * @param studentId 学生ID
     * @return 该学生已选修的课程列表
     */
    List<Course> selectStudentEnrolledCourses(@Param("studentId") Integer studentId);

    /**
     * 查询学生可选的课程列表（排除已选课程）
     * @param studentId 学生ID
     * @param courseName 课程名称（用于模糊查询，可为null）
     * @return 学生可选的课程列表
     */
    List<Course> selectAvailableCoursesForStudent(@Param("studentId") Integer studentId, @Param("courseName") String courseName);
    // ===== 新增的方法声明结束 =====
}