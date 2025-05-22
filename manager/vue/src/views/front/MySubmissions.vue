<template>
  <div class="main-content" style="width: 80%; margin: 20px auto;">
    <el-card header="我的作业提交记录">
      <div v-if="loading" v-loading="loading" style="min-height: 200px;">加载中...</div>
      <div v-else>
        <el-table :data="submissions" stripe>
          <el-table-column prop="assignmentTitle" label="作业标题" show-overflow-tooltip></el-table-column>
          <el-table-column prop="courseName" label="所属课程" show-overflow-tooltip></el-table-column>
          <el-table-column prop="submissionTime" label="提交时间" width="160"></el-table-column>
          <el-table-column label="提交内容" show-overflow-tooltip>
            <template v-slot="scope">
              <span v-if="scope.row.submissionContent" class="line2" :title="scope.row.submissionContent">{{ scope.row.submissionContent }}<br/></span>
              <el-link v-if="scope.row.submissionFilePath" :href="scope.row.submissionFilePath" target="_blank" type="primary" @click="trackDownload(scope.row.id, 'student_submission')">查看/下载提交附件</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template v-slot="scope">
              <el-tag :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分/等级" width="120"></el-table-column>
          <el-table-column label="教师评语" show-overflow-tooltip>
            <template v-slot="scope">
              <el-popover placement="top-start" title="教师评语" width="300" trigger="hover" :content="scope.row.teacherComment || '暂无评语'">
                <div slot="reference" class="line2">{{ scope.row.teacherComment || '暂无评语' }}</div>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="批改附件" width="100" align="center">
            <template v-slot="scope">
              <el-link v-if="scope.row.gradedFilePath" :href="scope.row.gradedFilePath" target="_blank" type="success" @click="trackDownload(scope.row.id, 'graded_file')">下载</el-link>
              <span v-else>无</span>
            </template>
          </el-table-column>
          <el-table-column prop="gradedTime" label="批改时间" width="160"></el-table-column>
        </el-table>
        <el-empty v-if="!submissions.length && !loading" description="暂无提交记录"></el-empty>
        <div class="pagination" style="margin-top: 20px; text-align: center;" v-if="totalSubmissions > 0">
          <el-pagination background @current-change="handlePageChange" :current-page="pageNum" :page-size="pageSize" layout="total, prev, pager, next" :total="totalSubmissions"></el-pagination>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getMySubmissionsPage } from '@/api/assignment';

export default {
  name: "MySubmissions",
  data() {
    return {
      submissions: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      totalSubmissions: 0,
    };
  },
  created() {
    this.loadMySubmissions(1);
  },
  methods: {
    loadMySubmissions(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.loading = true;
      getMySubmissionsPage({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
      }).then(res => {
        if (res.code === '200' && res.data) {
          this.submissions = res.data.list;
          this.totalSubmissions = res.data.total;
        } else {
          this.submissions = [];
          this.totalSubmissions = 0;
          this.$message.error(res.msg || '加载提交记录失败');
        }
      }).finally(() => {
        this.loading = false;
      });
    },
    handlePageChange(pageNum) {
      this.loadMySubmissions(pageNum);
    },
    statusTagType(status) {
      if (status === 'REVIEWED') return 'success';
      if (status === 'PENDING_REVIEW') return 'warning';
      if (status === 'LATE') return 'danger';
      return 'info';
    },
    formatStatus(status) {
      if (status === 'PENDING_REVIEW') return '待批阅';
      if (status === 'REVIEWED') return '已批阅';
      if (status === 'LATE') return '迟交';
      return status || '未知';
    },
    trackDownload(submissionId, fileType){
      // 根据需要实现下载追踪
      console.log(`Download attempt for ${fileType} from submission ${submissionId}`);
    }
  }
};
</script>
<style scoped>
.line2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>