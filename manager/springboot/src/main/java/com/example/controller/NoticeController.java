package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum; // 新增导入
import com.example.common.enums.RoleEnum;     // 新增导入
import com.example.entity.Account;            // 新增导入
import com.example.entity.Notice;
import com.example.service.NoticeService;
import com.example.utils.TokenUtils;          // 新增导入
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 公告信息表前端操作接口
 **/
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    /**
     * 检查当前用户是否有权限操作公告（ADMIN 或 TEACHER）
     * @return true 如果有权限, false 如果无权限
     */
    private boolean hasPermission() {
        Account currentUser = TokenUtils.getCurrentUser();
        return RoleEnum.ADMIN.name().equals(currentUser.getRole()) || RoleEnum.TEACHER.name().equals(currentUser.getRole());
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Notice notice) {
        if (!hasPermission()) {
            return Result.error(ResultCodeEnum.TOKEN_CHECK_ERROR.code, "无权限操作"); // 或者定义一个新的权限不足错误码
        }
        noticeService.add(notice); // service层会自动设置发布人和时间
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        if (!hasPermission()) {
            return Result.error(ResultCodeEnum.TOKEN_CHECK_ERROR.code, "无权限操作");
        }
        noticeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        if (!hasPermission()) {
            return Result.error(ResultCodeEnum.TOKEN_CHECK_ERROR.code, "无权限操作");
        }
        noticeService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Notice notice) {
        if (!hasPermission()) {
            return Result.error(ResultCodeEnum.TOKEN_CHECK_ERROR.code, "无权限操作");
        }
        // 注意：更新时，通常不应改变原始发布人(user)和发布时间(time)
        // NoticeService 的 add 方法中已经设置了 user 和 time
        // 如果更新也需要更新时间，可以在 Service 层处理或在这里设置 notice.setTime(DateUtil.now());
        // 但原 `user` 字段一般不建议修改，除非业务允许。
        // 当前 Service 层的 updateById 逻辑是直接更新传入的字段，所以前端需要确保不传递 user 和 time 字段，或者后端在 service 层做处理。
        // 为简单起见，我们这里假设前端传递过来的 notice 对象不包含需要保持不变的 user 和 time，或者在 service 中处理。
        noticeService.updateById(notice);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Notice notice = noticeService.selectById(id);
        return Result.success(notice);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Notice notice ) {
        List<Notice> list = noticeService.selectAll(notice);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Notice notice,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Notice> page = noticeService.selectPage(notice, pageNum, pageSize);
        return Result.success(page);
    }

}