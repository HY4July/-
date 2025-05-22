// com/example/mapper/TeachingResourceMapper.java
package com.example.mapper;

import com.example.entity.TeachingResource;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TeachingResourceMapper {
    int insert(TeachingResource teachingResource);
    int deleteById(Integer id);
    int updateById(TeachingResource teachingResource);
    TeachingResource selectById(Integer id);
    List<TeachingResource> selectAll(TeachingResource teachingResource); // 支持按名称、类型、教师ID、课程ID等查询
    int incrementDownloads(@Param("id") Integer id); // 下载次数+1
}