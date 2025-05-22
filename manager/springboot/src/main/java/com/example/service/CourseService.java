// com/example/service/CourseService.java
package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.Course;
import com.example.entity.StudentCourseEnrollment; // 新增
import com.example.exception.CustomException;
import com.example.mapper.CourseMapper;
import com.example.mapper.StudentCourseEnrollmentMapper; // 新增
import com.example.mapper.TeacherMapper; // 用于获取教师姓名
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CourseService {

    @Resource
    private CourseMapper courseMapper;
    @Resource
    private TeacherMapper teacherMapper; // 用于填充teacherName
    @Resource
    private StudentCourseEnrollmentMapper studentCourseEnrollmentMapper; // 用于选课

    public void add(Course course) {
        // 可选：添加一些业务校验，比如课程名是否重复等
        courseMapper.insert(course);
    }

    public void deleteById(Integer id) {
        // 可选：删除课程前，可能需要处理已选课的学生等逻辑
        courseMapper.deleteById(id);
    }

    public void updateById(Course course) {
        courseMapper.updateById(course);
    }

    public Course selectById(Integer id) {
        Course course = courseMapper.selectById(id);
        // if (course != null && course.getTeacherId() != null) {
        //     Account teacher = teacherMapper.selectById(course.getTeacherId());
        //     if (teacher != null) {
        //         course.setTeacherName(teacher.getName());
        //     }
        // } // 这部分逻辑已移到 Mapper.xml 中通过 JOIN 实现
        return course;
    }

    public List<Course> selectAll(Course course) {
        List<Course> courses = courseMapper.selectAll(course);
        // for (Course c : courses) {
        //     if (c.getTeacherId() != null) {
        //         Account teacher = teacherMapper.selectById(c.getTeacherId());
        //         if (teacher != null) {
        //             c.setTeacherName(teacher.getName());
        //         }
        //     }
        // } // 这部分逻辑已移到 Mapper.xml 中通过 JOIN 实现
        return courses;
    }

    public PageInfo<Course> selectPage(Course course, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> list = courseMapper.selectAll(course);
        // for (Course c : list) { // Mapper 中已处理 teacherName
        //     if (c.getTeacherId() != null) {
        //         Account teacher = teacherMapper.selectById(c.getTeacherId());
        //         if (teacher != null) {
        //             c.setTeacherName(teacher.getName());
        //         }
        //     }
        // }
        return PageInfo.of(list);
    }

    @Transactional
    public void studentEnrollCourse(Integer courseId) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (ObjectUtil.isNull(currentUser.getId()) || !"STUDENT".equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR.code, "学生用户才能选课");
        }
        Integer studentId = currentUser.getId();

        Course course = courseMapper.selectById(courseId);
        if (ObjectUtil.isNull(course)) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "课程不存在");
        }
        // 检查是否已选
        StudentCourseEnrollment existingEnrollment = studentCourseEnrollmentMapper.selectByStudentAndCourse(studentId, courseId);
        if (ObjectUtil.isNotNull(existingEnrollment) && "ENROLLED".equals(existingEnrollment.getStatus())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "您已选修该课程");
        }
        // 检查人数限制
        List<StudentCourseEnrollment> enrollments = studentCourseEnrollmentMapper.selectByCourseId(courseId);
        long currentEnrollCount = enrollments.stream().filter(e -> "ENROLLED".equals(e.getStatus())).count();
        if (course.getNum() != null && currentEnrollCount >= course.getNum()) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "课程人数已满");
        }

        StudentCourseEnrollment enrollment = new StudentCourseEnrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setStatus("ENROLLED");
        enrollment.setEnrollmentDate(DateUtil.now()); // Hutool 工具类获取当前时间字符串
        studentCourseEnrollmentMapper.insert(enrollment);
    }

    @Transactional
    public void studentDropCourse(Integer courseId) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (ObjectUtil.isNull(currentUser.getId()) || !"STUDENT".equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR.code, "学生用户才能退课");
        }
        Integer studentId = currentUser.getId();

        StudentCourseEnrollment existingEnrollment = studentCourseEnrollmentMapper.selectByStudentAndCourse(studentId, courseId);
        if (ObjectUtil.isNull(existingEnrollment) || !"ENROLLED".equals(existingEnrollment.getStatus())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "您未选修该课程或状态异常");
        }
        // 实际项目中可能是逻辑删除或更新状态为DROPPED
        studentCourseEnrollmentMapper.deleteByStudentAndCourse(studentId, courseId);
    }

    public PageInfo<Course> selectStudentCourses(Integer studentId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> courses = courseMapper.selectStudentEnrolledCourses(studentId); // 需要在CourseMapper.xml中定义此查询
        return PageInfo.of(courses);
    }
    public PageInfo<Course> selectAvailableCoursesForStudent(Integer studentId, String courseName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 这个查询会复杂一些，需要排除该学生已经选修的课程
        List<Course> courses = courseMapper.selectAvailableCoursesForStudent(studentId, courseName); // 需要在CourseMapper.xml中定义此查询
        return PageInfo.of(courses);
    }
}