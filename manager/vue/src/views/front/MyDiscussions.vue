<template>
  <div class="main-content">
    <el-card>
      <div slot="header">
        <span>我的提问</span>
      </div>
      <div v-if="discussions.length === 0" style="text-align: center; color: #888; padding: 20px;">
        您还没有发起过任何提问。
      </div>
      <div v-else>
        <div v-for="discussion in discussions" :key="discussion.id" class="discussion-item">
          <div class="discussion-header">
            <el-avatar :src="discussion.studentAvatar || defaultAvatar" size="small"></el-avatar>
            <span class="student-name">{{ discussion.studentName }}</span>
            <span class="course-info"> (课程: {{ discussion.courseName }})</span>
            <span class="create-time">{{ discussion.createTime }}</span>
          </div>
          <div class="discussion-content">
            <p style="font-weight: bold; margin-bottom: 5px;">问：{{ discussion.content }}</p>
            <div v-if="discussion.reply" class="discussion-reply">
              <p style="color: #409EFF; margin-bottom: 5px;">
                <span style="font-weight: bold;">教师回复：</span>
                {{ discussion.reply }}
              </p>
            </div>
            <div v-else class="discussion-no-reply">
              <p style="color: #909399; font-style: italic;">暂无回复</p>
            </div>
          </div>
          <div class="discussion-actions">
            <el-button type="text" size="mini" @click="navigateToCourseDiscussion(discussion.courseId)">查看课程讨论</el-button>
            <el-popconfirm title="确定删除这条提问吗？" @confirm="handleDeleteDiscussion(discussion.id)" style="margin-left: 10px;">
              <el-button type="text" icon="el-icon-delete" slot="reference" style="color: #F56C6C;" size="mini">删除</el-button>
            </el-popconfirm>
          </div>
        </div>
        <div class="pagination" style="margin-top: 20px; text-align: center;">
          <el-pagination
              background
              @current-change="handleCurrentChange"
              :current-page="pageNum"
              :page-size="pageSize"
              layout="total, prev, pager, next"
              :total="totalDiscussions">
          </el-pagination>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getMyQuestionsPage, deleteDiscussion } from '@/api/discussion'; // 确保 API 路径正确

export default {
  name: 'MyDiscussions',
  data() {
    return {
      discussions: [],
      pageNum: 1,
      pageSize: 10,
      totalDiscussions: 0,
      currentUser: null,
      defaultAvatar: require('@/assets/imgs/default-avatar.png'),
    };
  },
  created() {
    this.currentUser = JSON.parse(localStorage.getItem('xm-user') || '{}');
    this.loadMyDiscussions();
  },
  methods: {
    async loadMyDiscussions() {
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          // studentId 会在后端从 token 中获取，或者由 API 封装
        };
        const res = await getMyQuestionsPage(params); // 使用 discussion.js 中的 API
        if (res.code === '200') {
          this.discussions = res.data.list;
          this.totalDiscussions = res.data.total;
        } else {
          this.$message.error(res.msg);
        }
      } catch (error) {
        this.$message.error('加载我的提问列表失败');
        console.error(error);
      }
    },
    async handleDeleteDiscussion(discussionId) {
      try {
        const res = await deleteDiscussion(discussionId);
        if (res.code === '200') {
          this.$message.success('删除成功');
          this.loadMyDiscussions(); // 重新加载列表
        } else {
          this.$message.error(res.msg);
        }
      } catch (error) {
        this.$message.error('删除失败');
        console.error(error);
      }
    },
    navigateToCourseDiscussion(courseId) {
      this.$router.push(`/front/course/${courseId}/discussion`);
    },
    handleCurrentChange(newPage) {
      this.pageNum = newPage;
      this.loadMyDiscussions();
    },
  },
};
</script>

<style scoped>
.discussion-item {
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}
.discussion-item:last-child {
  border-bottom: none;
}
.discussion-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.discussion-header .student-name {
  font-weight: bold;
  margin-left: 8px;
}
.course-info {
  margin-left: 5px;
  color: #555;
  font-size: 0.9em;
}
.discussion-header .create-time {
  margin-left: auto;
  color: #888;
  font-size: 12px;
}
.discussion-content {
  margin-left: 40px; /* 根据头像大小调整 */
}
.discussion-reply {
  background-color: #f4f4f5;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
}
.discussion-no-reply {
  margin-top: 5px;
}
.discussion-actions {
  margin-top: 8px;
  text-align: right;
}
</style>