// src/api/teachingResource.js
import request from '@/utils/request'

export const addTeachingResource = (data) => {
    return request({
        url: '/teaching-resource/add',
        method: 'post',
        data
    })
}

export const getTeachingResourcesPage = (params) => {
    return request({
        url: '/teaching-resource/selectPage',
        method: 'get',
        params
    })
}

export const getTeachingResourceById = (id) => {
    return request({
        url: `/teaching-resource/selectById/${id}`,
        method: 'get'
    })
}

export const updateTeachingResource = (data) => {
    return request({
        url: '/teaching-resource/update',
        method: 'put',
        data
    })
}

export const deleteTeachingResource = (id) => {
    return request({
        url: `/teaching-resource/delete/${id}`,
        method: 'delete'
    })
}

export const incrementResourceDownloads = (id) => {
    return request({
        url: `/teaching-resource/incrementDownloads/${id}`,
        method: 'post'
    })
}