<template>
  <div class="main-content" style="width: 80%; margin: 20px auto;">
    <el-card header="我的课程">
      <div v-if="loading" v-loading="loading" style="min-height: 200px; display: flex; align-items: center; justify-content: center;">加载中...</div>
      <div v-else>
        <el-table :data="enrolledCourses" stripe v-if="enrolledCourses.length">
          <el-table-column prop="name" label="课程名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="teacherName" label="授课教师" width="150"></el-table-column>
          <el-table-column prop="type" label="课程类型" width="150"></el-table-column>
          <el-table-column prop="score" label="学分" width="80" align="center"></el-table-column>
          <el-table-column prop="room" label="上课教室" width="150"></el-table-column>
          <el-table-column prop="week" label="上课星期" width="120"></el-table-column>
          <el-table-column prop="segment" label="上课时段" width="120"></el-table-column>
          <el-table-column prop="status" label="课程状态" width="100" align="center">
            <template v-slot="scope">
              <el-tag :type="courseStatusTagType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template v-slot="scope">
              <el-button size="mini" type="primary" plain @click="viewCourseAssignments(scope.row.id)">查看作业</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="您还没有选修任何课程"></el-empty>

        <div class="pagination" style="margin-top: 20px; text-align: center;" v-if="totalCourses > 0">
          <el-pagination
              background
              @current-change="handlePageChange"
              :current-page="pageNum"
              :page-size="pageSize"
              layout="total, prev, pager, next"
              :total="totalCourses">
          </el-pagination>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getMyCourses, dropCourse } from '@/api/course'; // 确保 course.js 中有 getMyCourses 和 dropCourse API

export default {
  name: "MyCourses",
  data() {
    return {
      enrolledCourses: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      totalCourses: 0,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    };
  },
  created() {
    if (!this.user || this.user.role !== 'STUDENT') {
      this.$message.error('只有学生才能访问此页面');
      this.$router.push('/front/home'); // 或者登录页
      return;
    }
    this.loadEnrolledCourses(1);
  },
  methods: {
    loadEnrolledCourses(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.loading = true;
      getMyCourses({ // 后端 getMyCourses 接口需要分页参数
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        // studentId 将通过token在后端获取，所以前端不需要传
      }).then(res => {
        if (res.code === '200' && res.data) {
          this.enrolledCourses = res.data.list;
          this.totalCourses = res.data.total;
        } else {
          this.enrolledCourses = [];
          this.totalCourses = 0;
          this.$message.error(res.msg || '加载已选课程失败');
        }
      }).catch(err => {
        console.error("加载已选课程失败:", err);
        this.$message.error('请求已选课程失败');
        this.enrolledCourses = [];
        this.totalCourses = 0;
      }).finally(() => {
        this.loading = false;
      });
    },
    handlePageChange(pageNum) {
      this.loadEnrolledCourses(pageNum);
    },
    viewCourseAssignments(courseId) {
      this.$router.push({ name: 'FrontCourseAssignments', params: { courseId: courseId } });
    },
    // dropCourse(courseId) { // 退课功能示例
    //   this.$confirm('您确定要退选这门课程吗？', '确认退选', { type: 'warning' }).then(() => {
    //     dropCourse(courseId).then(res => {
    //       if (res.code === '200') {
    //         this.$message.success('退选成功');
    //         this.loadEnrolledCourses(1); // 刷新列表
    //       } else {
    //         this.$message.error(res.msg || '退选失败');
    //       }
    //     }).catch(err => {
    //       this.$message.error('请求退选接口失败');
    //     });
    //   }).catch(() => {});
    // },
    courseStatusTagType(status) {
      if (status === '进行中') return 'success';
      if (status === '即将开课') return 'warning';
      if (status === '已结束') return 'info';
      return 'primary';
    }
  }
};
</script>

<style scoped>
/* 您可以在这里添加特定样式 */
.main-content {
  /* 已在 global.css 或 front.css 中定义 */
}
</style>