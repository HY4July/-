// src/api/course.js
import request from '@/utils/request'

export const addCourse = (data) => request({ url: '/course/add', method: 'post', data })
export const deleteCourse = (id) => request({ url: `/course/delete/${id}`, method: 'delete' })
export const updateCourse = (data) => request({ url: '/course/update', method: 'put', data })
export const getCourseById = (id) => request({ url: `/course/selectById/${id}`, method: 'get' })
export const getCoursesPage = (params) => request({ url: '/course/selectPage', method: 'get', params })
export const getAllCourses = (params) => request({ url: '/course/selectAll', method: 'get', params }) // 获取所有课程，用于下拉选择等

// 学生选课相关
export const enrollCourse = (courseId) => request({ url: `/course/enroll/${courseId}`, method: 'post' })
export const dropCourse = (courseId) => request({ url: `/course/drop/${courseId}`, method: 'post' })
export const getMyCourses = (params) => request({ url: '/course/student/myCourses', method: 'get', params})
export const getAvailableCourses = (params) => request({ url: '/course/student/available', method: 'get', params})