// src/api/classes.js
import request from '@/utils/request'

export const addClass = (data) => request({ url: '/classes/add', method: 'post', data })
export const deleteClass = (id) => request({ url: `/classes/delete/${id}`, method: 'delete' })
export const updateClass = (data) => request({ url: '/classes/update', method: 'put', data })
export const getClassById = (id) => request({ url: `/classes/selectById/${id}`, method: 'get' })
export const getClassesPage = (params) => request({ url: '/classes/selectPage', method: 'get', params })
export const getAllClasses = () => request({ url: '/classes/selectAll', method: 'get' }) // 获取所有班级，用于学生信息表单下拉