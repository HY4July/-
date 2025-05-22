// com/example/service/ClassesService.java
package com.example.service;

import com.example.entity.Account;
import com.example.entity.Classes;
import com.example.mapper.ClassesMapper;
import com.example.mapper.TeacherMapper; // 用于填充teacherName
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ClassesService {

    @Resource
    private ClassesMapper classesMapper;
    @Resource
    private TeacherMapper teacherMapper;

    public void add(Classes classes) {
        classesMapper.insert(classes);
    }

    public void deleteById(Integer id) {
        classesMapper.deleteById(id);
    }

    public void updateById(Classes classes) {
        classesMapper.updateById(classes);
    }

    public Classes selectById(Integer id) {
        Classes classes = classesMapper.selectById(id);
        // if (classes != null && classes.getTeacherId() != null) {
        //     Account teacher = teacherMapper.selectById(classes.getTeacherId());
        //     if (teacher != null) {
        //         classes.setTeacherName(teacher.getName());
        //     }
        // } // Mapper中已处理
        // 同样, specialityName 的逻辑如果需要，也类似处理
        return classes;
    }

    public List<Classes> selectAll(Classes classes) {
        return classesMapper.selectAll(classes); // Mapper中已处理teacherName
    }

    public PageInfo<Classes> selectPage(Classes classes, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Classes> list = classesMapper.selectAll(classes);
        return PageInfo.of(list);
    }
}