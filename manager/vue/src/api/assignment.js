// src/api/assignment.js
import request from '@/utils/request'

// === 作业定义 (AssignmentInfo) ===
export const publishAssignment = (data) => request({ url: '/assignment/info/publish', method: 'post', data })
export const updateAssignmentInfo = (data) => request({ url: '/assignment/info/update', method: 'put', data })
export const deleteAssignmentInfo = (id) => request({ url: `/assignment/info/delete/${id}`, method: 'delete' })
export const getTeacherAssignmentsPage = (params) => request({ url: '/assignment/info/teacher/page', method: 'get', params })
export const getAssignmentInfoById = (id) => request({ url: `/assignment/info/${id}`, method: 'get' })
export const getCourseAssignmentsForStudentPage = (courseId, params) => request({ url: `/assignment/info/course/${courseId}/page`, method: 'get', params })


// === 学生作业提交 (StudentAssignment) ===
export const submitStudentAssignment = (data) => request({ url: '/assignment/submit', method: 'post', data })
export const getMySubmissionsPage = (params) => request({ url: '/assignment/submission/my/page', method: 'get', params })
export const getStudentSubmissionDetail = (submissionId) => request({ url: `/assignment/submission/${submissionId}`, method: 'get' })

// === 教师批改 ===
export const getSubmissionsForAssignmentPage = (assignmentInfoId, params) => request({ url: `/assignment/submission/list/${assignmentInfoId}/page`, method: 'get', params })
export const gradeStudentAssignment = (data) => request({ url: '/assignment/submission/grade', method: 'put', data })