// com/example/mapper/ClassesMapper.java
package com.example.mapper;

import com.example.entity.Classes;
import java.util.List;

public interface ClassesMapper {
    int insert(Classes classes);
    int deleteById(Integer id);
    int updateById(Classes classes);
    Classes selectById(Integer id);
    List<Classes> selectAll(Classes classes);
}