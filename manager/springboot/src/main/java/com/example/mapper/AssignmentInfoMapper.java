// com/example/mapper/AssignmentInfoMapper.java
package com.example.mapper;

import com.example.entity.AssignmentInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AssignmentInfoMapper {
    int insert(AssignmentInfo assignmentInfo);
    int deleteById(Integer id);
    int updateById(AssignmentInfo assignmentInfo);
    AssignmentInfo selectById(Integer id);
    List<AssignmentInfo> selectAll(AssignmentInfo assignmentInfo); // For filtering
    List<AssignmentInfo> selectByCourseId(@Param("courseId") Integer courseId);
    List<AssignmentInfo> selectByTeacherId(@Param("teacherId") Integer teacherId);
}