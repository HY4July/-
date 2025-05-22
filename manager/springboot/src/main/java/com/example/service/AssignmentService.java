// com/example/service/AssignmentService.java
package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.AssignmentInfo;
import com.example.entity.Course;
import com.example.entity.StudentAssignment;
import com.example.exception.CustomException;
import com.example.mapper.AssignmentInfoMapper;
import com.example.mapper.CourseMapper;
import com.example.mapper.StudentAssignmentMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
// 如果AssignmentInfo需要附加学生提交状态，可以考虑引入StudentAssignment的部分字段或创建一个DTO
// import java.util.stream.Collectors;

@Service
public class AssignmentService {

    @Resource
    private AssignmentInfoMapper assignmentInfoMapper;

    @Resource
    private StudentAssignmentMapper studentAssignmentMapper;

    @Resource
    private CourseMapper courseMapper;

    // === AssignmentInfo (教师发布作业) ===

    @Transactional
    public void publishAssignment(AssignmentInfo assignmentInfo) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }
        if (!RoleEnum.TEACHER.name().equals(currentUser.getRole()) && !RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }

        // 校验课程是否存在以及该教师是否有权在该课程下发布作业
        Course course = courseMapper.selectById(assignmentInfo.getCourseId());
        if (course == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "关联的课程不存在");
        }
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            if (!currentUser.getId().equals(course.getTeacherId())) {
                throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "您无权在该课程下发布作业");
            }
        }

        assignmentInfo.setTeacherId(currentUser.getId());
        assignmentInfo.setPublishTime(DateUtil.now()); // 当前时间，格式 "yyyy-MM-dd HH:mm:ss"
        assignmentInfoMapper.insert(assignmentInfo);
    }

    @Transactional
    public void updateAssignmentInfo(AssignmentInfo assignmentInfo) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }

        AssignmentInfo dbInfo = assignmentInfoMapper.selectById(assignmentInfo.getId());
        if (dbInfo == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "作业不存在");
        }

        // 只有发布该作业的教师或管理员才能修改
        if (!RoleEnum.ADMIN.name().equals(currentUser.getRole()) && !currentUser.getId().equals(dbInfo.getTeacherId())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }
        // 确保courseId和teacherId不被恶意修改，或者只允许修改部分字段
        assignmentInfo.setTeacherId(dbInfo.getTeacherId()); // 不允许修改发布教师
        // assignmentInfo.setCourseId(dbInfo.getCourseId()); // 通常也不允许修改关联课程

        assignmentInfoMapper.updateById(assignmentInfo);
    }

    @Transactional
    public void deleteAssignmentInfo(Integer assignmentInfoId) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }
        AssignmentInfo dbInfo = assignmentInfoMapper.selectById(assignmentInfoId);
        if (dbInfo == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "作业不存在或已被删除");
        }
        // 只有发布该作业的教师或管理员才能删除
        if (!RoleEnum.ADMIN.name().equals(currentUser.getRole()) && !currentUser.getId().equals(dbInfo.getTeacherId())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }
        // 删除作业定义前，需要先删除所有学生对该作业的提交记录，以避免外键约束问题
        studentAssignmentMapper.deleteByAssignmentId(assignmentInfoId); // 需在StudentAssignmentMapper中添加此方法
        assignmentInfoMapper.deleteById(assignmentInfoId);
        // 注意：实际的文件（作业附件）也需要从文件系统中删除，这里只处理数据库记录
    }

    public PageInfo<AssignmentInfo> selectTeacherAssignments(Integer pageNum, Integer pageSize, String title, Integer courseId) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }
        if (!RoleEnum.TEACHER.name().equals(currentUser.getRole()) && !RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }

        AssignmentInfo query = new AssignmentInfo();
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            query.setTeacherId(currentUser.getId()); // 教师只能查看自己发布的
        }
        if (ObjectUtil.isNotEmpty(title)) {
            query.setTitle(title);
        }
        if (ObjectUtil.isNotEmpty(courseId)) {
            query.setCourseId(courseId);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<AssignmentInfo> list = assignmentInfoMapper.selectAll(query);
        return PageInfo.of(list);
    }

    public PageInfo<AssignmentInfo> selectStudentCourseAssignments(Integer courseId, Integer pageNum, Integer pageSize, String title) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }
        AssignmentInfo query = new AssignmentInfo();
        query.setCourseId(courseId); // 必须传入课程ID
        if (ObjectUtil.isNotEmpty(title)) {
            query.setTitle(title);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<AssignmentInfo> list = assignmentInfoMapper.selectAll(query);

        // 如果是学生查看，需要附加其提交状态
        if (RoleEnum.STUDENT.name().equals(currentUser.getRole())) {
            Integer studentId = currentUser.getId();
            for (AssignmentInfo ai : list) {
                StudentAssignment sa = studentAssignmentMapper.selectByAssignmentAndStudent(ai.getId(), studentId);
                if (sa != null) {
                    // 在 AssignmentInfo 实体中添加临时字段以携带这些信息到前端
                    // 例如：ai.setStudentSubmissionStatus(sa.getStatus()); ai.setStudentScore(sa.getScore());
                    // ai.setStudentSubmissionId(sa.getId()); // 方便学生查看或编辑自己的提交
                }
            }
        }
        return PageInfo.of(list);
    }

    public AssignmentInfo getAssignmentInfoById(Integer id) {
        return assignmentInfoMapper.selectById(id);
    }


    // === StudentAssignment (学生提交与教师批改) ===

    @Transactional
    public void submitAssignment(StudentAssignment studentAssignment) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null || !RoleEnum.STUDENT.name().equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "只有登录的学生可以提交作业");
        }
        if (studentAssignment.getAssignmentId() == null) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR.code, "作业ID不能为空");
        }

        AssignmentInfo assignmentInfo = assignmentInfoMapper.selectById(studentAssignment.getAssignmentId());
        if (assignmentInfo == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "指定的作业不存在");
        }

        // 检查截止日期
        if (assignmentInfo.getDueTime() != null && DateUtil.parseDateTime(assignmentInfo.getDueTime()).before(new Date())) {
            studentAssignment.setStatus("LATE"); // 迟交
        } else {
            studentAssignment.setStatus("PENDING_REVIEW"); // 待批阅
        }

        studentAssignment.setStudentId(currentUser.getId());
        studentAssignment.setSubmissionTime(DateUtil.now());

        // 检查是否已提交过，如果允许覆盖则更新，否则报错
        StudentAssignment existingSubmission = studentAssignmentMapper.selectByAssignmentAndStudent(studentAssignment.getAssignmentId(), studentAssignment.getStudentId());
        if (existingSubmission != null) {
            // 更新现有提交 (允许学生在截止日期前多次修改提交)
            studentAssignment.setId(existingSubmission.getId());
            // 保留原有的评分和评语（如果存在），除非业务逻辑允许学生提交后清空
            studentAssignment.setScore(existingSubmission.getScore());
            studentAssignment.setTeacherComment(existingSubmission.getTeacherComment());
            studentAssignment.setGradedTime(existingSubmission.getGradedTime());
            studentAssignment.setGradedFilePath(existingSubmission.getGradedFilePath());
            studentAssignmentMapper.updateById(studentAssignment);
        } else {
            studentAssignmentMapper.insert(studentAssignment);
        }
    }

    @Transactional
    public void gradeAssignment(StudentAssignment studentAssignmentUpdate) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }
        if (!RoleEnum.TEACHER.name().equals(currentUser.getRole()) && !RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }

        if (studentAssignmentUpdate.getId() == null) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR.code, "提交记录ID不能为空");
        }

        StudentAssignment dbSubmission = studentAssignmentMapper.selectById(studentAssignmentUpdate.getId());
        if (dbSubmission == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "学生提交记录不存在");
        }

        AssignmentInfo assignmentInfo = assignmentInfoMapper.selectById(dbSubmission.getAssignmentId());
        if (assignmentInfo == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "关联的作业信息不存在");
        }
        // 教师只能批改自己发布的作业
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            if (!currentUser.getId().equals(assignmentInfo.getTeacherId())) {
                throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "您无权批改此作业");
            }
        }

        // 更新评分、评语、批改文件路径和状态
        dbSubmission.setScore(studentAssignmentUpdate.getScore());
        dbSubmission.setTeacherComment(studentAssignmentUpdate.getTeacherComment());
        dbSubmission.setGradedFilePath(studentAssignmentUpdate.getGradedFilePath());
        dbSubmission.setStatus("REVIEWED"); // 已批阅
        dbSubmission.setGradedTime(DateUtil.now());
        studentAssignmentMapper.updateById(dbSubmission);
    }

    public PageInfo<StudentAssignment> getSubmissionsForAssignment(Integer assignmentInfoId, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser(); // 保留您原有的权限等检查
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }
        if (!RoleEnum.TEACHER.name().equals(currentUser.getRole()) && !RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }

        AssignmentInfo assignmentInfo = assignmentInfoMapper.selectById(assignmentInfoId);
        if (assignmentInfo == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "作业信息不存在");
        }
        // 教师只能查看自己发布的作业的提交
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            if (!currentUser.getId().equals(assignmentInfo.getTeacherId())) {
                throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "您无权查看此作业的提交记录");
            }
        }

        PageHelper.startPage(pageNum, pageSize);
        StudentAssignment queryParams = new StudentAssignment();
        queryParams.setAssignmentId(assignmentInfoId); // 正确：设置 assignmentId 用于筛选

        // **确保 StudentAssignmentMapper.xml 中的 selectAll 方法的 <where> 子句中，
        // 如果有针对 courseId 的筛选，它是基于 JOIN 后的 assignment_info 表的 course_id，
        // 而不是直接期望 StudentAssignment 参数对象有 courseId 属性。
        // 鉴于错误信息，问题在于 Mybatis 尝试从 StudentAssignment 对象获取 courseId。
        // 所以，确保 StudentAssignmentMapper.xml 中 selectAll 方法的 <if test="courseId != null"> 这一行被移除或修改。
        // (已在上一条回复中建议移除 StudentAssignmentMapper.xml 中 selectAll 的 courseId 条件)

        List<StudentAssignment> list = studentAssignmentMapper.selectAll(queryParams);
        return PageInfo.of(list);
    }

    public PageInfo<StudentAssignment> getMySubmissions(Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null || !RoleEnum.STUDENT.name().equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "只有学生可以查看我的提交");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StudentAssignment> list = studentAssignmentMapper.selectByStudentId(currentUser.getId());
        return PageInfo.of(list);
    }

    public StudentAssignment getStudentSubmissionDetail(Integer submissionId) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }

        StudentAssignment submission = studentAssignmentMapper.selectById(submissionId);
        if (submission == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "提交记录不存在");
        }

        // 学生只能看自己的
        if (RoleEnum.STUDENT.name().equals(currentUser.getRole()) && !currentUser.getId().equals(submission.getStudentId())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }
        // 教师可以看其负责作业的
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            AssignmentInfo assignmentInfo = assignmentInfoMapper.selectById(submission.getAssignmentId());
            if (assignmentInfo == null || !currentUser.getId().equals(assignmentInfo.getTeacherId())) {
                throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "您无权查看此提交记录");
            }
        }
        // 管理员可以查看所有
        return submission;
    }
}