// com/example/controller/TeachingResourceController.java
package com.example.controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.TeachingResource;
import com.example.service.TeachingResourceService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/teaching-resource") // 统一使用短横线分隔
public class TeachingResourceController {

    @Resource
    private TeachingResourceService teachingResourceService;

    // 引用 FileController 进行实际文件上传，这里只处理元数据
    // FileController 的 /files/upload 接口返回文件URL

    private boolean canOperate() {
        Account currentUser = TokenUtils.getCurrentUser();
        return RoleEnum.ADMIN.name().equals(currentUser.getRole()) || RoleEnum.TEACHER.name().equals(currentUser.getRole());
    }

    /**
     * 新增资源元数据 (文件已通过 FileController 上传)
     */
    @PostMapping("/add")
    public Result add(@RequestBody TeachingResource teachingResource) {
        if (!canOperate()) {
            return Result.error(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }
        if (StrUtil.isBlank(teachingResource.getName()) || StrUtil.isBlank(teachingResource.getPath())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR.code, "资源名称和文件路径不能为空");
        }
        // type 和 size 最好由前端在调用FileController上传成功后，随元数据一起传来
        teachingResourceService.add(teachingResource);
        return Result.success();
    }


    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        // 权限检查在Service层完成
        teachingResourceService.deleteById(id);
        // 注意：这里只删除了数据库记录，实际文件需要额外处理（例如前端调用FileController的删除接口）
        return Result.success();
    }

    @PutMapping("/update")
    public Result updateById(@RequestBody TeachingResource teachingResource) {
        // 权限检查在Service层完成
        if (StrUtil.isBlank(teachingResource.getName())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR.code, "资源名称不能为空");
        }
        // 如果 path 字段被更新，意味着文件可能已重新上传，前端应确保 path 是新的文件 URL
        teachingResourceService.updateById(teachingResource);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        TeachingResource resource = teachingResourceService.selectById(id);
        return Result.success(resource);
    }

    @GetMapping("/selectAll")
    public Result selectAll(TeachingResource teachingResource) {
        List<TeachingResource> list = teachingResourceService.selectAll(teachingResource);
        return Result.success(list);
    }

    @GetMapping("/selectPage")
    public Result selectPage(TeachingResource teachingResource,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<TeachingResource> page = teachingResourceService.selectPage(teachingResource, pageNum, pageSize);
        return Result.success(page);
    }

    @PostMapping("/incrementDownloads/{id}")
    public Result incrementDownloads(@PathVariable Integer id) {
        teachingResourceService.incrementDownloadCount(id);
        return Result.success();
    }
}