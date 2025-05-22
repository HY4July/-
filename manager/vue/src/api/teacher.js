// src/api/teacher.js
import request from '@/utils/request'

// 分页查询教师列表 (可用于后台教师管理页面)
export const getTeachersPage = (params) => {
    return request({
        url: '/teacher/selectPage', // 对应 TeacherController 中的 /selectPage 接口
        method: 'get',
        params
    })
}

// 根据ID查询教师信息
export const getTeacherById = (id) => {
    return request({
        url: `/teacher/selectById/${id}`, // 对应 TeacherController 中的 /selectById/{id} 接口
        method: 'get'
    })
}

// 新增教师
export const addTeacher = (data) => {
    return request({
        url: '/teacher/add', // 对应 TeacherController 中的 /add 接口
        method: 'post',
        data
    })
}

// 修改教师信息
export const updateTeacher = (data) => {
    return request({
        url: '/teacher/update', // 对应 TeacherController 中的 /update 接口
        method: 'put',
        data
    })
}

// 根据ID删除教师
export const deleteTeacher = (id) => {
    return request({
        url: `/teacher/delete/${id}`, // 对应 TeacherController 中的 /delete/{id} 接口
        method: 'delete'
    })
}

// 批量删除教师
export const deleteBatchTeachers = (data) => {
    return request({
        url: '/teacher/delete/batch', // 对应 TeacherController 中的 /delete/batch 接口
        method: 'delete',
        data
    })
}

// 查询所有教师 (主要用于课程管理模块中选择授课教师的下拉列表)
export const getAllTeachers = (params) => {
    // params 可以是一个空对象 {}，或者用于未来可能的简单筛选（如按姓名模糊搜索，需后端支持）
    // 这个接口对应 TeacherController 中的 /selectAll 接口
    return request({
        url: '/teacher/selectAll',
        method: 'get',
        params // 如果后端 selectAll 支持按名称等条件查询，可以在这里传入
    })
}