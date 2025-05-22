// com/example/service/TeachingResourceService.java
package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.TeachingResource;
import com.example.exception.CustomException;
import com.example.mapper.TeachingResourceMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // For file type and size

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class TeachingResourceService {

    @Resource
    private TeachingResourceMapper teachingResourceMapper;

    @Transactional
    public void add(TeachingResource teachingResource, MultipartFile file) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (!"TEACHER".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }

        teachingResource.setTeacherId(currentUser.getId());
        teachingResource.setUploadTime(DateUtil.now()); // yyyy-MM-dd HH:mm:ss
        teachingResource.setDownloads(0);

        if (file != null && !file.isEmpty()) {
            // The actual file upload is handled by FileController.
            // Here we just set the path returned by FileController and other metadata.
            // The 'path' (URL) and 'type' should be set in the Controller after file upload.
            teachingResource.setSize(file.getSize());
            if (teachingResource.getType() == null) { // If type not set by controller, try to determine
                try {
                    teachingResource.setType(FileTypeUtil.getType(file.getInputStream(), file.getOriginalFilename()));
                } catch (IOException e) {
                    // Log error or handle, for now, leave type as null or a default
                    System.err.println("Error determining file type: " + e.getMessage());
                }
            }
        }
        teachingResourceMapper.insert(teachingResource);
    }

    // Overload add method if file is not directly handled here (path is pre-set)
    public void add(TeachingResource teachingResource) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (!"TEACHER".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }
        teachingResource.setTeacherId(currentUser.getId());
        teachingResource.setUploadTime(DateUtil.now());
        if (teachingResource.getDownloads() == null) teachingResource.setDownloads(0);
        // Assume path, type, size are already set on teachingResource object by the controller
        teachingResourceMapper.insert(teachingResource);
    }


    public void deleteById(Integer id) {
        Account currentUser = TokenUtils.getCurrentUser();
        TeachingResource resource = teachingResourceMapper.selectById(id);
        if (resource == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "资源不存在");
        }
        // Only admin or the uploader can delete
        if (!"ADMIN".equals(currentUser.getRole()) && !currentUser.getId().equals(resource.getTeacherId())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }
        // Actual file deletion from disk should be handled separately,
        // possibly by calling an endpoint in FileController or a utility method.
        teachingResourceMapper.deleteById(id);
    }

    public void updateById(TeachingResource teachingResource) {
        Account currentUser = TokenUtils.getCurrentUser();
        TeachingResource dbResource = teachingResourceMapper.selectById(teachingResource.getId());
        if (dbResource == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "资源不存在");
        }
        if (!"ADMIN".equals(currentUser.getRole()) && !currentUser.getId().equals(dbResource.getTeacherId())) {
            throw new CustomException(ResultCodeEnum.PERMISSION_DENIED_ERROR);
        }
        // Similar to add, if file changes, controller handles upload and sets new path/type/size
        teachingResourceMapper.updateById(teachingResource);
    }

    public TeachingResource selectById(Integer id) {
        return teachingResourceMapper.selectById(id);
    }

    public List<TeachingResource> selectAll(TeachingResource teachingResource) {
        return teachingResourceMapper.selectAll(teachingResource);
    }

    public PageInfo<TeachingResource> selectPage(TeachingResource teachingResource, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TeachingResource> list = teachingResourceMapper.selectAll(teachingResource);
        return PageInfo.of(list);
    }

    @Transactional
    public void incrementDownloadCount(Integer id) {
        teachingResourceMapper.incrementDownloads(id);
    }
}