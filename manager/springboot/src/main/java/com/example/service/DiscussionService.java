package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Discussion;
import com.example.entity.Course; // 需要引入Course实体
import com.example.exception.CustomException;
import com.example.mapper.CourseMapper; // 需要引入CourseMapper
import com.example.mapper.DiscussionMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DiscussionService {

    @Resource
    private DiscussionMapper discussionMapper;

    @Resource
    private CourseMapper courseMapper; // 注入CourseMapper用于校验

    /**
     * 学生发布新提问
     */
    @Transactional
    public void add(Discussion discussion) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null || !RoleEnum.STUDENT.name().equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "只有学生可以发布提问");
        }

        // 校验课程是否存在
        if (discussion.getCourseId() == null) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR.code, "必须关联一个课程");
        }
        Course course = courseMapper.selectById(discussion.getCourseId());
        if (course == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "关联的课程不存在");
        }
        // 校验学生是否选修了该课程 (如果需要此逻辑，则需要查询 student_course_enrollment 表)
        // 此处暂时省略，假设只要是学生就能在任何课程下提问，或由前端保证课程选择的正确性

        discussion.setStudentId(currentUser.getId());
        discussion.setCreateTime(DateUtil.now()); // 设置提问时间为当前时间
        discussionMapper.insert(discussion);
    }

    /**
     * 教师回复或修改回复
     */
    @Transactional
    public void update(Discussion discussion) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }

        Discussion dbDiscussion = discussionMapper.selectById(discussion.getId());
        if (dbDiscussion == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "讨论记录不存在");
        }

        Course course = courseMapper.selectById(dbDiscussion.getCourseId());
        if (course == null) {
            // 理论上不应该发生，因为创建时已校验
            throw new CustomException(ResultCodeEnum.SYSTEM_ERROR.code, "讨论关联的课程数据异常");
        }

        // 只有该课程的授课教师或管理员才能回复
        if (!RoleEnum.ADMIN.name().equals(currentUser.getRole()) &&
                !(RoleEnum.TEACHER.name().equals(currentUser.getRole()) && currentUser.getId().equals(course.getTeacherId()))) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "您无权回复此讨论");
        }

        // 只允许更新回复内容
        Discussion discussionToUpdate = new Discussion();
        discussionToUpdate.setId(discussion.getId());
        discussionToUpdate.setReply(discussion.getReply());
        // discussionToUpdate.setReplyTime(DateUtil.now()); // 如果有回复时间字段

        discussionMapper.updateById(discussionToUpdate);
    }

    /**
     * 删除讨论 (通常由管理员或提问者自己操作)
     */
    public void deleteById(Integer id) {
        Account currentUser = TokenUtils.getCurrentUser();
        Discussion discussion = discussionMapper.selectById(id);
        if (discussion == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "讨论记录不存在或已被删除");
        }

        // 管理员可以删除任何讨论
        // 学生只能删除自己的提问 (且在没有回复的情况下，或业务规定允许)
        // 教师可以删除其课程下的讨论 (如果业务需要)
        boolean canDelete = false;
        if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            canDelete = true;
        } else if (RoleEnum.STUDENT.name().equals(currentUser.getRole()) && currentUser.getId().equals(discussion.getStudentId())) {
            // 可选：如果已有回复，学生可能不能删除，或者删除行为需要更复杂的逻辑
            canDelete = true;
        } else if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            Course course = courseMapper.selectById(discussion.getCourseId());
            if (course != null && currentUser.getId().equals(course.getTeacherId())) {
                // 教师可以删除自己课程下的讨论
                canDelete = true;
            }
        }

        if (!canDelete) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }
        discussionMapper.deleteById(id);
    }

    /**
     * 根据ID查询
     */
    public Discussion selectById(Integer id) {
        Discussion discussion = discussionMapper.selectById(id);
        if (discussion == null) {
            // 可以选择抛出异常或返回null，根据前端如何处理
            // throw new CustomException(ResultCodeEnum.PARAM_ERROR, "讨论记录不存在");
        }
        return discussion;
    }

    /**
     * 分页查询讨论列表 (通用，可供学生查看课程讨论，教师管理等)
     * @param discussion 查询条件 (可包含 courseId, studentId, content 关键词)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageInfo<Discussion> selectPage(Discussion discussion, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser(); // 用于权限控制或特定筛选
        // 例如：如果需要根据教师角色筛选其所教课程的讨论，可以在这里加入逻辑
        // if (RoleEnum.TEACHER.name().equals(currentUser.getRole()) && discussion.getCourseId() == null) {
        //    // 限制教师只能查看自己课程的讨论，除非明确指定了 courseId
        //    List<Course> teacherCourses = courseMapper.selectByTeacherId(currentUser.getId());
        //    if (teacherCourses.isEmpty()) return PageInfo.of(new ArrayList<>()); // 没有课程则没有讨论
        //    //  需要调整 discussionMapper.selectAll 支持多课程ID查询或在Service层多次查询合并
        // }

        PageHelper.startPage(pageNum, pageSize);
        List<Discussion> list = discussionMapper.selectAll(discussion);
        return PageInfo.of(list);
    }
}