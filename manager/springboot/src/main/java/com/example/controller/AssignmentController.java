// com/example/controller/AssignmentController.java
package com.example.controller;

import com.example.common.Result;
import com.example.entity.AssignmentInfo;
import com.example.entity.StudentAssignment;
import com.example.service.AssignmentService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 如果需要在这里处理文件

import javax.annotation.Resource;

@RestController
@RequestMapping("/assignment")
public class AssignmentController {

    @Resource
    private AssignmentService assignmentService;

    // === 教师操作：作业信息 (AssignmentInfo) ===
    @PostMapping("/info/publish")
    public Result publishAssignmentInfo(@RequestBody AssignmentInfo assignmentInfo) {
        // 权限检查在 Service 层进行
        assignmentService.publishAssignment(assignmentInfo);
        return Result.success("作业发布成功");
    }

    @PutMapping("/info/update")
    public Result updateAssignmentInfo(@RequestBody AssignmentInfo assignmentInfo) {
        assignmentService.updateAssignmentInfo(assignmentInfo);
        return Result.success("作业更新成功");
    }

    @DeleteMapping("/info/delete/{id}")
    public Result deleteAssignmentInfo(@PathVariable Integer id) {
        assignmentService.deleteAssignmentInfo(id);
        return Result.success("作业删除成功");
    }

    // 教师查看自己发布的作业列表
    @GetMapping("/info/teacher/page")
    public Result getTeacherAssignments(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) Integer courseId) { // 新增 courseId 参数
        PageInfo<AssignmentInfo> page = assignmentService.selectTeacherAssignments(pageNum, pageSize, title, courseId); // 传递 courseId
        return Result.success(page);
    }

    // 通用：根据ID获取作业信息详情（教师编辑，学生查看作业要求）
    @GetMapping("/info/{id}")
    public Result getAssignmentInfoById(@PathVariable Integer id) {
        AssignmentInfo assignmentInfo = assignmentService.getAssignmentInfoById(id);
        return Result.success(assignmentInfo);
    }

    // 学生查看某课程下的作业列表
    @GetMapping("/info/course/{courseId}/page")
    public Result getCourseAssignmentsForStudent(@PathVariable Integer courseId,
                                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 @RequestParam(required = false) String title) {
        PageInfo<AssignmentInfo> page = assignmentService.selectStudentCourseAssignments(courseId, pageNum, pageSize, title);
        return Result.success(page);
    }


    // === 学生操作：提交作业 (StudentAssignment) ===
    @PostMapping("/submit")
    public Result submitAssignment(@RequestBody StudentAssignment studentAssignment) {
        // 文件路径 studentAssignment.submissionFilePath 应由前端在上传文件后设置
        // 权限检查和业务逻辑在 Service 层
        assignmentService.submitAssignment(studentAssignment);
        return Result.success("作业提交成功");
    }

    // 学生查看自己的提交记录
    @GetMapping("/submission/my/page")
    public Result getMySubmissions(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<StudentAssignment> page = assignmentService.getMySubmissions(pageNum, pageSize);
        return Result.success(page);
    }

    // 学生/教师查看单个提交详情
    @GetMapping("/submission/{submissionId}")
    public Result getStudentSubmissionDetail(@PathVariable Integer submissionId) {
        StudentAssignment submission = assignmentService.getStudentSubmissionDetail(submissionId);
        return Result.success(submission);
    }


    // === 教师操作：批改作业 (StudentAssignment) ===
    @GetMapping("/submission/list/{assignmentInfoId}/page")
    public Result getSubmissionsForAssignment(@PathVariable Integer assignmentInfoId,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<StudentAssignment> page = assignmentService.getSubmissionsForAssignment(assignmentInfoId, pageNum, pageSize);
        return Result.success(page);
    }

    @PutMapping("/submission/grade")
    public Result gradeSubmission(@RequestBody StudentAssignment studentAssignmentUpdate) {
        // studentAssignmentUpdate 包含 id (提交记录的id), score, teacherComment, gradedFilePath
        assignmentService.gradeAssignment(studentAssignmentUpdate);
        return Result.success("作业批改成功");
    }
}