// com/example/controller/ClassesController.java
package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Classes;
import com.example.service.ClassesService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/classes")
public class ClassesController {

    @Resource
    private ClassesService classesService;

    private boolean isAdmin() {
        Account currentUser = TokenUtils.getCurrentUser();
        return RoleEnum.ADMIN.name().equals(currentUser.getRole());
    }

    @PostMapping("/add")
    public Result add(@RequestBody Classes classes) {
        if (!isAdmin()) return Result.error(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        classesService.add(classes);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        if (!isAdmin()) return Result.error(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        classesService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result updateById(@RequestBody Classes classes) {
        if (!isAdmin()) return Result.error(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        classesService.updateById(classes);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Classes classes = classesService.selectById(id);
        return Result.success(classes);
    }

    @GetMapping("/selectAll")
    public Result selectAll(Classes classes) {
        List<Classes> list = classesService.selectAll(classes);
        return Result.success(list);
    }

    @GetMapping("/selectPage")
    public Result selectPage(Classes classes,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Classes> page = classesService.selectPage(classes, pageNum, pageSize);
        return Result.success(page);
    }
}