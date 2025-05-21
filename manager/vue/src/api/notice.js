// src/api/notice.js
import request from '@/utils/request'

// 分页查询公告列表
export const getNotices = (params) => {
    return request({
        url: '/notice/selectPage',
        method: 'get',
        params
    })
}

// 根据ID查询公告
export const getNoticeById = (id) => {
    return request({
        url: `/notice/selectById/${id}`,
        method: 'get'
    })
}

// 新增公告
export const addNotice = (data) => {
    return request({
        url: '/notice/add',
        method: 'post',
        data
    })
}

// 修改公告
export const updateNotice = (data) => {
    return request({
        url: '/notice/update',
        method: 'put',
        data
    })
}

// 根据ID删除公告
export const deleteNotice = (id) => {
    return request({
        url: `/notice/delete/${id}`,
        method: 'delete'
    })
}

// 批量删除公告
export const deleteBatchNotices = (data) => {
    return request({
        url: '/notice/delete/batch',
        method: 'delete',
        data
    })
}