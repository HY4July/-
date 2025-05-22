package com.example.mapper;

import com.example.entity.Discussion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussionMapper {

    /**
     * 新增讨论/提问
     */
    int insert(Discussion discussion);

    /**
     * 根据ID删除讨论
     */
    int deleteById(Integer id);

    /**
     * 修改讨论（主要用于教师回复）
     */
    int updateById(Discussion discussion);

    /**
     * 根据ID查询讨论详情
     */
    Discussion selectById(Integer id);

    /**
     * 查询所有讨论（可带条件筛选）
     * 后续会在XML中通过JOIN查询学生姓名、课程名称等信息
     */
    List<Discussion> selectAll(Discussion discussion);

    /**
     * 根据课程ID查询讨论列表
     * @param courseId 课程ID
     * @return 讨论列表
     */
    List<Discussion> selectByCourseId(@Param("courseId") Integer courseId);

    /**
     * 根据学生ID查询其提出的问题列表
     * @param studentId 学生ID
     * @return 该学生提出的问题列表
     */
    List<Discussion> selectByStudentId(@Param("studentId") Integer studentId);

}