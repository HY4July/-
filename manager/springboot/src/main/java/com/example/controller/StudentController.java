package com.example.controller;

import com.example.common.Result;
import com.example.entity.Student; // 确保 Student 实体类已包含新字段
import com.example.service.StudentService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    /**
     * 新增
     * 前端发送的 JSON 对象如果包含 collegeId, specialityId, classId, score 等字段，
     * Spring 会自动将它们绑定到这里的 student 对象的相应属性上。
     */
    @PostMapping("/add")
    public Result add(@RequestBody Student student) {
        // StudentService 的 add 方法会处理这些字段的持久化
        studentService.add(student);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        studentService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        studentService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     * 前端发送的 JSON 对象如果包含 collegeId, specialityId, classId, score 等字段，
     * Spring 会自动将它们绑定到这里的 student 对象的相应属性上。
     * StudentService 的 updateById 方法会处理这些字段的更新。
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Student student) {
        studentService.updateById(student);
        return Result.success();
    }

    /**
     * 根据ID查询
     * StudentService 和 StudentMapper 应确保能返回包含所有新字段（及关联名称如className）的 Student 对象。
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Student student = studentService.selectById(id);
        return Result.success(student);
    }

    /**
     * 查询所有
     * StudentService 和 StudentMapper 应确保能返回包含所有新字段（及关联名称如className）的 Student 对象列表。
     * 注意：这里的参数 student 是通过 URL query params (GET请求的查询参数) 绑定的，不是 @RequestBody。
     * 如果需要按 collegeId, classId 等进行筛选，确保 StudentService 和 StudentMapper 支持这些查询条件。
     */
    @GetMapping("/selectAll")
    public Result selectAll(Student student ) {
        List<Student> list = studentService.selectAll(student);
        return Result.success(list);
    }

    /**
     * 分页查询
     * StudentService 和 StudentMapper 应确保能返回包含所有新字段（及关联名称如className）的 Student 对象列表。
     * 注意：这里的参数 student 是通过 URL query params (GET请求的查询参数) 绑定的。
     * 如果需要按 collegeId, classId 等进行筛选，确保 StudentService 和 StudentMapper 支持这些查询条件。
     */
    @GetMapping("/selectPage")
    public Result selectPage(Student student,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Student> page = studentService.selectPage(student, pageNum, pageSize);
        return Result.success(page);
    }

}