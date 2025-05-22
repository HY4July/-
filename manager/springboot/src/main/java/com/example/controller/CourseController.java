// com/example/controller/CourseController.java
package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Course;
import com.example.service.CourseService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    private boolean isAdmin() {
        Account currentUser = TokenUtils.getCurrentUser();
        return RoleEnum.ADMIN.name().equals(currentUser.getRole());
    }

    @PostMapping("/add")
    public Result add(@RequestBody Course course) {
        if (!isAdmin()) return Result.error(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        courseService.add(course);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        if (!isAdmin()) return Result.error(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        courseService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result updateById(@RequestBody Course course) {
        if (!isAdmin()) return Result.error(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        courseService.updateById(course);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Course course = courseService.selectById(id);
        return Result.success(course);
    }

    @GetMapping("/selectAll")
    public Result selectAll(Course course) {
        List<Course> list = courseService.selectAll(course);
        return Result.success(list);
    }

    @GetMapping("/selectPage")
    public Result selectPage(Course course,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Course> page = courseService.selectPage(course, pageNum, pageSize);
        return Result.success(page);
    }

    // 学生选课接口
    @PostMapping("/enroll/{courseId}")
    public Result studentEnrollCourse(@PathVariable Integer courseId) {
        courseService.studentEnrollCourse(courseId);
        return Result.success("选课成功");
    }

    // 学生退课接口
    @PostMapping("/drop/{courseId}")
    public Result studentDropCourse(@PathVariable Integer courseId) {
        courseService.studentDropCourse(courseId);
        return Result.success("退课成功");
    }

    // 查询学生已选课程
    @GetMapping("/student/myCourses")
    public Result getStudentCourses(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        PageInfo<Course> page = courseService.selectStudentCourses(currentUser.getId(), pageNum, pageSize);
        return Result.success(page);
    }

    // 学生查询可选课程
    @GetMapping("/student/available")
    public Result getAvailableCoursesForStudent(@RequestParam(required = false) String name,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        PageInfo<Course> page = courseService.selectAvailableCoursesForStudent(currentUser.getId(), name, pageNum, pageSize);
        return Result.success(page);
    }
}