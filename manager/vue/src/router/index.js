import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'), // 假设这是管理员/教师的布局组件
    redirect: '/home',  // 默认重定向到后台首页
    children: [
      { path: '403', name: 'NoAuth', meta: { name: '无权限' }, component: () => import('../views/manager/403') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'admin', name: 'Admin', meta: { name: '管理员信息' }, component: () => import('../views/manager/Admin') },
      { path: 'teacher', name: 'Teacher', meta: { name: '教师信息' }, component: () => import('../views/manager/Teacher') },
      { path: 'student', name: 'Student', meta: { name: '学生信息' }, component: () => import('../views/manager/Student') },
      { path: 'adminPerson', name: 'AdminPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/AdminPerson') },
      { path: 'teacherPerson', name: 'TeacherPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/TeacherPerson') },
      { path: 'studentPerson', name: 'StudentPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/StudentPerson') }, // 注意：这个是 Manager 布局下的学生个人信息，可能与 /front/person 功能重复或不同
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'notice', name: 'Notice', meta: { name: '公告信息' }, component: () => import('../views/manager/Notice') },
      { path: 'courses', name: 'CourseManagement', meta: { name: '课程管理' }, component: () => import('../views/manager/CourseManagement.vue') },
      { path: 'classes', name: 'ClassManagement', meta: { name: '班级管理' }, component: () => import('../views/manager/ClassManagement.vue') },
      { path: 'resources', name: 'ResourceManagement', meta: { name: '教学资源管理' }, component: () => import('../views/manager/ResourceManagement.vue') },
      { path: 'assignments', name: 'AssignmentManagement', meta: { name: '作业管理' }, component: () => import('../views/manager/AssignmentManagement.vue') },
    ]
  },
  {
    path: '/front',
    name: 'Front',
    component: () => import('../views/Front.vue'), // 这是学生的布局组件
    // 建议为 /front 也添加一个默认的子路由，例如 /front/home
    redirect: '/front/home',
    children: [
      { path: 'home', name: 'FrontHome', meta: { name: '系统首页' }, component: () => import('../views/front/Home') },
      { path: 'person', name: 'FrontPerson', meta: { name: '个人信息' }, component: () => import('../views/front/Person') },
      { path: 'notices', name: 'FrontNoticeList', meta: { name: '公告列表' }, component: () => import('../views/front/NoticeList.vue')},
      { path: 'notice/:id', name: 'FrontNoticeDetail', meta: { name: '公告详情' }, component: () => import('../views/front/NoticeDetail.vue')},
      { path: 'resources', name: 'FrontResourceLibrary', meta: { name: '资源库' }, component: () => import('../views/front/ResourceLibrary.vue') },
      { path: 'course/:courseId/assignments', name: 'FrontCourseAssignments', meta: { name: '课程作业' }, component: () => import('../views/front/CourseAssignments.vue'), props: true },
      { path: 'my-submissions', name: 'FrontMySubmissions', meta: { name: '我的作业' }, component: () => import('../views/front/MySubmissions.vue') },
      { path: 'my-courses', name: 'FrontMyCourses', meta: { name: '我的课程' }, component: () => import('../views/front/MyCourses.vue')},
    ]
  },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },
  { path: '*', name: 'NotFound', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 启用并修改路由守卫
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem("xm-user") || '{}');
  const userRole = user.role;

  // 如果用户尝试访问根路径 /
  if (to.path === '/') {
    if (userRole) {
      if (userRole === 'STUDENT') {
        next('/front/home'); // 学生重定向到 /front/home
      } else { // ADMIN 或 TEACHER
        next('/home');       // 管理员或教师重定向到 /home
      }
    } else {
      next('/login'); // 没有用户信息则重定向到登录
    }
  }
      // 如果学生尝试访问非 /front/ 开头的路径 (且不是登录或注册页)
      // 可以选择将他们重定向回 /front/home 或显示一个无权限页面
      // 此部分为可选增强，取决于你的具体需求
  /*
  else if (userRole === 'STUDENT' && !to.path.startsWith('/front') && to.name !== 'Login' && to.name !== 'Register') {
    next('/front/home');
  }
  */
  else {
    next(); // 其他情况正常放行
  }
});

export default router