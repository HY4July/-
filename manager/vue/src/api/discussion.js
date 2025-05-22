import request from '@/utils/request'

// 学生发布新提问
export const addDiscussion = (data) => {
    return request({
        url: '/discussion/add',
        method: 'post',
        data
    })
}

// 教师回复讨论
export const replyDiscussion = (data) => {
    return request({
        url: '/discussion/reply', // 假设后端接口路径为 /reply
        method: 'put',
        data
    })
}

// 删除讨论
export const deleteDiscussion = (id) => {
    return request({
        url: `/discussion/delete/${id}`,
        method: 'delete'
    })
}

// 根据ID查询讨论详情
export const getDiscussionById = (id) => {
    return request({
        url: `/discussion/selectById/${id}`,
        method: 'get'
    })
}

// 分页查询讨论列表 (通用)
// params 可以包含 courseId, studentId, content 等查询条件
export const getDiscussionsPage = (params) => {
    return request({
        url: '/discussion/selectPage',
        method: 'get',
        params
    })
}

// 学生查询自己提出的所有问题
export const getMyQuestionsPage = (params) => {
    return request({
        url: '/discussion/myQuestions',
        method: 'get',
        params
    })
}