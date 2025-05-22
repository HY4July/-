package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Discussion;
import com.example.service.DiscussionService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/discussion")
public class DiscussionController {

    @Resource
    private DiscussionService discussionService;

    /**
     * 学生发布新提问
     */
    @PostMapping("/add")
    public Result add(@RequestBody Discussion discussion) {
        // Service层已包含权限检查和必要字段设置
        if (ObjectUtil.isEmpty(discussion.getContent()) || ObjectUtil.isEmpty(discussion.getCourseId())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR.code, "提问内容和关联课程不能为空");
        }
        discussionService.add(discussion);
        return Result.success("发布成功");
    }

    /**
     * 教师回复或更新回复
     */
    @PutMapping("/reply") // 或者用 /update，根据语义选择
    public Result reply(@RequestBody Discussion discussion) {
        if (discussion.getId() == null || ObjectUtil.isEmpty(discussion.getReply())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR.code, "讨论ID和回复内容不能为空");
        }
        // Service层进行权限检查和更新
        discussionService.update(discussion);
        return Result.success("回复成功");
    }

    /**
     * 删除讨论
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        // Service层进行权限检查
        discussionService.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 根据ID查询讨论详情
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Discussion discussion = discussionService.selectById(id);
        if (discussion == null) {
            return Result.error(ResultCodeEnum.PARAM_ERROR.code, "讨论记录不存在");
        }
        return Result.success(discussion);
    }

    /**
     * 分页查询讨论列表
     * - 学生可用于查看某课程下的所有讨论
     * - 教师可用于查看其课程下的所有讨论，或管理所有讨论（管理员）
     * - 前端根据角色和场景传入不同的过滤条件 (如 courseId)
     */
    @GetMapping("/selectPage")
    public Result selectPage(Discussion discussion, // 可包含 courseId, studentId（查询我的提问时）等作为查询条件
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        // 可以在这里根据当前用户角色进一步调整查询参数 discussion，
        // 例如，如果当前是教师，且未指定 courseId，则可能需要限制只查询其教授课程的讨论。
        // Account currentUser = TokenUtils.getCurrentUser();
        // if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
        //     // 补充教师权限下的查询逻辑，例如关联其教授的课程
        // }
        PageInfo<Discussion> page = discussionService.selectPage(discussion, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 学生查询自己提出的所有问题
     */
    @GetMapping("/myQuestions")
    public Result selectMyQuestions(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null || currentUser.getId() == null || !RoleEnum.STUDENT.name().equals(currentUser.getRole())) {
            return Result.error(ResultCodeEnum.PERMISSION_DENIED_ERROR.code, "只有学生可以查看我的提问");
        }
        Discussion discussionQuery = new Discussion();
        discussionQuery.setStudentId(currentUser.getId());
        PageInfo<Discussion> page = discussionService.selectPage(discussionQuery, pageNum, pageSize);
        return Result.success(page);
    }
}