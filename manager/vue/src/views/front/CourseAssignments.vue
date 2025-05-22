<template>
  <div class="main-content" style="width: 80%; margin: 20px auto;">
    <el-card>
      <div slot="header">
        <span>{{ courseName ? courseName + ' - ' : '' }}作业列表</span>
      </div>
      <div v-if="loadingAssignments" v-loading="loadingAssignments" style="min-height: 200px;">加载中...</div>
      <div v-else>
        <el-table :data="assignments" stripe v-if="assignments.length">
          <el-table-column prop="title" label="作业标题" show-overflow-tooltip></el-table-column>
          <el-table-column prop="content" label="作业内容" show-overflow-tooltip>
            <template v-slot="scope">
              <el-popover placement="top-start" title="作业内容" width="400" trigger="hover" :content="scope.row.content">
                <div slot="reference" class="line2">{{ scope.row.content }}</div>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="publishTime" label="发布时间" width="160"></el-table-column>
          <el-table-column prop="dueTime" label="截止时间" width="160"></el-table-column>
          <el-table-column label="附件" width="100" align="center">
            <template v-slot="scope">
              <el-link v-if="scope.row.attachmentPath" :href="scope.row.attachmentPath" target="_blank" type="primary" @click="trackDownload(scope.row.id, 'assignment_info_attachment')">下载</el-link>
              <span v-else>无</span>
            </template>
          </el-table-column>
          <el-table-column label="我的提交状态" width="140" align="center">
            <template v-slot="scope">
              <el-tag :type="getSubmissionStatusType(scope.row.studentSubmissionStatus)">
                {{ formatSubmissionStatus(scope.row.studentSubmissionStatus) }}
              </el-tag>
              <div v-if="scope.row.studentScore" style="font-size: 12px; color: #E6A23C;">得分: {{ scope.row.studentScore }}</div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template v-slot="scope">
              <el-button size="mini" type="success" plain @click="openSubmitDialog(scope.row)">
                {{ scope.row.studentSubmissionId ? '查看/修改提交' : '提交作业' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="该课程暂无作业"></el-empty>
        <div class="pagination" style="margin-top: 20px; text-align: center;" v-if="totalAssignments > 0">
          <el-pagination background @current-change="handleAssignmentsPageChange" :current-page="pageNumAssignments" :page-size="pageSizeAssignments" layout="total, prev, pager, next" :total="totalAssignments"></el-pagination>
        </div>
      </div>
    </el-card>

    <el-dialog title="提交作业" :visible.sync="submitDialogVisible" width="50%" :close-on-click-modal="false" destroy-on-close @close="clearSubmitForm">
      <el-form :model="submissionForm" label-width="100px" ref="submissionFormRef" :rules="submissionRules">
        <el-form-item label="作业标题">
          <el-input :value="currentAssignment.title" disabled></el-input>
        </el-form-item>
        <el-form-item label="提交内容" prop="submissionContent">
          <el-input type="textarea" :rows="5" v-model="submissionForm.submissionContent" placeholder="请输入作业内容（可选）"></el-input>
        </el-form-item>
        <el-form-item label="上传附件" prop="submissionFilePath">
          <el-upload
              ref="submissionUploadRef"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              :on-success="handleSubmissionFileSuccess"
              :on-remove="handleSubmissionFileRemove"
              :before-upload="beforeFileUpload"
              :file-list="submissionFileList"
              :limit="1">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传一个附件，大小不超过100MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="submitDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitMyAssignment" :loading="submitting">提 交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCourseAssignmentsForStudentPage, submitStudentAssignment, getStudentSubmissionDetail } from '@/api/assignment';
import { getCourseById } from '@/api/course'; // 用于获取课程名称

export default {
  name: "CourseAssignments",
  props: {
    courseId: { // 从路由参数接收 courseId
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      assignments: [],
      loadingAssignments: false,
      pageNumAssignments: 1,
      pageSizeAssignments: 10,
      totalAssignments: 0,
      courseName: '',

      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),

      submitDialogVisible: false,
      currentAssignment: {}, // 当前要提交的作业信息
      submissionForm: {
        assignmentId: null,
        submissionContent: '',
        submissionFilePath: null,
      },
      submissionFileList: [],
      submissionRules: {
        // submissionFilePath: [{ required: true, message: '请上传作业附件', trigger: 'change' }] // 如果附件是必须的
      },
      submitting: false,
    };
  },
  created() {
    this.fetchCourseName();
    this.loadAssignments(1);
  },
  methods: {
    fetchCourseName() {
      getCourseById(this.courseId).then(res => {
        if (res.code === '200' && res.data) {
          this.courseName = res.data.name;
        }
      });
    },
    loadAssignments(pageNum) {
      if (pageNum) this.pageNumAssignments = pageNum;
      this.loadingAssignments = true;
      getCourseAssignmentsForStudentPage(this.courseId, {
        pageNum: this.pageNumAssignments,
        pageSize: this.pageSizeAssignments,
        // title: this.searchTitle // 如果需要按作业标题搜索
      }).then(res => {
        if (res.code === '200' && res.data) {
          this.assignments = res.data.list.map(item => ({
            ...item,
            // 假设Service层已在AssignmentInfo中填充了 studentSubmissionStatus, studentScore, studentSubmissionId
          }));
          this.totalAssignments = res.data.total;
        } else {
          this.assignments = [];
          this.totalAssignments = 0;
          this.$message.error(res.msg || '加载作业列表失败');
        }
      }).finally(() => {
        this.loadingAssignments = false;
      });
    },
    handleAssignmentsPageChange(pageNum) {
      this.loadAssignments(pageNum);
    },
    async openSubmitDialog(assignment) {
      this.currentAssignment = assignment;
      this.submissionForm = {
        assignmentId: assignment.id,
        submissionContent: '',
        submissionFilePath: null,
      };
      this.submissionFileList = [];
      if (this.$refs.submissionUploadRef) {
        this.$refs.submissionUploadRef.clearFiles();
      }

      // 如果学生已提交过，则加载之前的提交信息
      if (assignment.studentSubmissionId) { // 假设 studentSubmissionId 是在 AssignmentInfo 上附加的学生提交记录ID
        try {
          const res = await getStudentSubmissionDetail(assignment.studentSubmissionId);
          if (res.code === '200' && res.data) {
            this.submissionForm.submissionContent = res.data.submissionContent || '';
            this.submissionForm.submissionFilePath = res.data.submissionFilePath || null;
            if (res.data.submissionFilePath) {
              this.submissionFileList = [{ name: res.data.submissionFilePath.split('/').pop(), url: res.data.submissionFilePath }];
            }
          } else {
            this.$message.error('加载上次提交失败: ' + res.msg);
          }
        } catch (error) {
          this.$message.error('加载上次提交请求失败');
        }
      }
      this.submitDialogVisible = true;
    },
    clearSubmitForm() {
      this.submissionForm = { assignmentId: null, submissionContent: '', submissionFilePath: null };
      this.submissionFileList = [];
      if (this.$refs.submissionUploadRef) {
        this.$refs.submissionUploadRef.clearFiles();
      }
    },
    beforeFileUpload(file) {
      const isLt100M = file.size / 1024 / 1024 < 100;
      if (!isLt100M) {
        this.$message.error('上传文件大小不能超过 100MB!');
      }
      return isLt100M;
    },
    handleSubmissionFileSuccess(response, file) {
      if (response.code === '200') {
        this.submissionForm.submissionFilePath = response.data;
        this.submissionFileList = [{ name: file.name, url: response.data }];
        this.$message.success('附件上传成功');
      } else {
        this.$message.error('附件上传失败: ' + response.msg);
        this.submissionFileList = [];
        if (this.$refs.submissionUploadRef) this.$refs.submissionUploadRef.clearFiles();
      }
    },
    handleSubmissionFileRemove() {
      this.submissionForm.submissionFilePath = null;
      this.submissionFileList = [];
    },
    submitMyAssignment() {
      if (!this.submissionForm.submissionContent && !this.submissionForm.submissionFilePath) {
        this.$message.warning('请至少填写作业内容或上传一个附件');
        return;
      }
      this.$refs.submissionFormRef.validate((valid) => {
        if (valid) {
          this.submitting = true;
          submitStudentAssignment(this.submissionForm).then(res => {
            if (res.code === '200') {
              this.$message.success('作业提交成功');
              this.submitDialogVisible = false;
              this.loadAssignments(this.pageNumAssignments); // 刷新列表以更新提交状态
            } else {
              this.$message.error(res.msg);
            }
          }).finally(() => {
            this.submitting = false;
          });
        }
      });
    },
    getSubmissionStatusType(status) {
      if (status === 'REVIEWED') return 'success';
      if (status === 'PENDING_REVIEW') return 'warning';
      if (status === 'LATE') return 'danger';
      return 'info';
    },
    formatSubmissionStatus(status) {
      if (status === 'PENDING_REVIEW') return '待批阅';
      if (status === 'REVIEWED') return '已批阅';
      if (status === 'LATE') return '迟交';
      if (!status) return '未提交';
      return status;
    },
    trackDownload(resourceId, resourceType) {
      // resourceType 可以是 'assignment_info_attachment' 或 'graded_file' 等
      // 目前下载计数是针对教学资源的，如果作业附件也需要计数，需要后端支持
      console.log(`Download attempt for ${resourceType} with id ${resourceId}`);
      // 如果有统一的下载计数接口，可以在这里调用
    }
  }
};
</script>

<style scoped>
/* 可以在这里添加特定样式 */
.line2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>