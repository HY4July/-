<template>
  <div class="main-content">
    <el-card style="margin-bottom: 10px;">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <span style="font-size: 20px;">{{ courseName }} - 讨论区</span>
        <el-button type="primary" @click="openNewDiscussionDialog">发起新讨论/提问</el-button>
      </div>
    </el-card>

    <el-card>
      <div v-if="discussions.length === 0" style="text-align: center; color: #888; padding: 20px;">
        暂无讨论，快来发起第一个讨论吧！
      </div>
      <div v-else>
        <div v-for="discussion in discussions" :key="discussion.id" class="discussion-item">
          <div class="discussion-header">
            <el-avatar :src="discussion.studentAvatar || defaultAvatar" size="small"></el-avatar>
            <span class="student-name">{{ discussion.studentName }}</span>
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
          <div class="discussion-actions" v-if="currentUser && currentUser.id === discussion.studentId">
            <el-popconfirm title="确定删除这条提问吗？" @confirm="handleDeleteDiscussion(discussion.id)">
              <el-button type="text" icon="el-icon-delete" slot="reference" style="color: #F56C6C;">删除</el-button>
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

    <el-dialog title="发起新讨论" :visible.sync="newDiscussionDialogVisible" width="50%" @close="resetNewDiscussionForm">
      <el-form :model="newDiscussionForm" :rules="newDiscussionRules" ref="newDiscussionFormRef" label-width="80px">
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" :rows="5" v-model="newDiscussionForm.content" placeholder="请输入讨论内容..."></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="newDiscussionDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitNewDiscussion">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getDiscussionsPage, addDiscussion, deleteDiscussion, getCourseById } from '@/api/course'; // 假设讨论区API在course.js或独立的discussion.js
// 如果 discussion.js 独立，则使用: import { getDiscussionsPage, addDiscussion, deleteDiscussion } from '@/api/discussion';
// import { getCourseById } from '@/api/course'; // 获取课程信息

export default {
  name: 'CourseDiscussion',
  props: ['courseId'], // 从路由接收 courseId
  data() {
    return {
      courseName: '',
      discussions: [],
      pageNum: 1,
      pageSize: 10,
      totalDiscussions: 0,
      newDiscussionDialogVisible: false,
      newDiscussionForm: {
        content: '',
        courseId: null, // 将自动设置为当前 courseId
      },
      newDiscussionRules: {
        content: [
          { required: true, message: '请输入讨论内容', trigger: 'blur' },
        ],
      },
      currentUser: null,
      defaultAvatar: require('@/assets/imgs/default-avatar.png'), // 确保有默认头像图片
    };
  },
  created() {
    this.currentUser = JSON.parse(localStorage.getItem('xm-user') || '{}');
    this.newDiscussionForm.courseId = parseInt(this.courseId); // 将路由参数赋值给表单
    this.fetchCourseName();
    this.loadDiscussions();
  },
  methods: {
    async fetchCourseName() {
      try {
        const res = await getCourseById(this.courseId);
        if (res.code === '200') {
          this.courseName = res.data.name;
        }
      } catch (error) {
        console.error("获取课程名称失败", error);
        this.courseName = `课程 #${this.courseId}`;
      }
    },
    async loadDiscussions() {
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          courseId: this.courseId,
        };
        // **重要：** 确保您已创建 manager/vue/src/api/discussion.js 并在其中定义了 getDiscussionsPage 函数
        // 如果您将讨论区API放在了 course.js 中，请确保该文件中存在 getDiscussionsPage
        // 或者，如果您还没有创建 discussion.js，请现在创建，并将上一条回复中的 discussion.js 内容放进去。
        const res = await getDiscussionsPage(params); // 使用 discussion.js 中的 API
        if (res.code === '200') {
          this.discussions = res.data.list;
          this.totalDiscussions = res.data.total;
        } else {
          this.$message.error(res.msg);
        }
      } catch (error) {
        this.$message.error('加载讨论列表失败');
        console.error(error);
      }
    },
    openNewDiscussionDialog() {
      this.newDiscussionDialogVisible = true;
    },
    resetNewDiscussionForm() {
      this.$refs.newDiscussionFormRef.resetFields();
      this.newDiscussionForm.content = '';
    },
    submitNewDiscussion() {
      this.$refs.newDiscussionFormRef.validate(async (valid) => {
        if (valid) {
          try {
            // **重要：** 确保您已创建 manager/vue/src/api/discussion.js 并在其中定义了 addDiscussion 函数
            const res = await addDiscussion(this.newDiscussionForm); // 使用 discussion.js 中的 API
            if (res.code === '200') {
              this.$message.success('发布成功');
              this.newDiscussionDialogVisible = false;
              this.loadDiscussions(); // 重新加载列表
            } else {
              this.$message.error(res.msg);
            }
          } catch (error) {
            this.$message.error('发布失败');
            console.error(error);
          }
        }
      });
    },
    async handleDeleteDiscussion(discussionId) {
      try {
        // **重要：** 确保您已创建 manager/vue/src/api/discussion.js 并在其中定义了 deleteDiscussion 函数
        const res = await deleteDiscussion(discussionId); // 使用 discussion.js 中的 API
        if (res.code === '200') {
          this.$message.success('删除成功');
          this.loadDiscussions(); // 重新加载列表
        } else {
          this.$message.error(res.msg);
        }
      } catch (error) {
        this.$message.error('删除失败');
        console.error(error);
      }
    },
    handleCurrentChange(newPage) {
      this.pageNum = newPage;
      this.loadDiscussions();
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
  margin-top: 5px;
  text-align: right;
}
</style>