<template>
  <div>
    <el-table :data="submissions" stripe v-loading="loadingSubmissions">
      <el-table-column prop="studentName" label="提交学生" width="150"></el-table-column>
      <el-table-column prop="submissionTime" label="提交时间" width="180"></el-table-column>
      <el-table-column label="提交内容">
        <template v-slot="scope">
          <div v-if="scope.row.submissionContent" class="line2" :title="scope.row.submissionContent">{{ scope.row.submissionContent }}</div>
          <el-link v-if="scope.row.submissionFilePath" :href="scope.row.submissionFilePath" target="_blank" type="primary">查看/下载附件</el-link>
          <span v-if="!scope.row.submissionContent && !scope.row.submissionFilePath">未提交任何内容</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template v-slot="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="得分" width="100"></el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template v-slot="scope">
          <el-button size="mini" type="warning" plain @click="handleGrade(scope.row)">批改</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination" style="margin-top: 20px">
      <el-pagination background @current-change="handleSubmissionsPageChange" :current-page="pageNumSubmissions" :page-size="pageSizeSubmissions" layout="total, prev, pager, next" :total="totalSubmissions"></el-pagination>
    </div>

    <el-dialog title="批改作业" :visible.sync="gradeFormVisible" width="50%" :close-on-click-modal="false" destroy-on-close  @close="clearGradedUpload">
      <el-form :model="gradeForm" label-width="100px" ref="gradeFormRef">
        <el-form-item label="学生姓名">
          <el-input :value="currentSubmission.studentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="提交附件" v-if="currentSubmission.submissionFilePath">
          <el-link :href="currentSubmission.submissionFilePath" target="_blank" type="primary">下载学生附件</el-link>
        </el-form-item>
        <el-form-item label="提交内容" v-if="currentSubmission.submissionContent">
          <el-input type="textarea" :value="currentSubmission.submissionContent" disabled :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="打分/等级" prop="score">
          <el-input v-model="gradeForm.score" placeholder="请输入分数或等级"></el-input>
        </el-form-item>
        <el-form-item label="教师评语" prop="teacherComment">
          <el-input type="textarea" :rows="4" v-model="gradeForm.teacherComment" placeholder="请输入评语"></el-input>
        </el-form-item>
        <el-form-item label="上传批改附件">
          <el-upload ref="gradedUploadRef" :action="$baseUrl + '/files/upload'" :headers="{ token: user.token }" :on-success="handleGradedFileSuccess" :on-remove="handleGradedFileRemove" :file-list="gradedFileList" :limit="1">
            <el-button size="small" type="primary">上传批改稿</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="gradeFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveGrade">提交批改</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSubmissionsForAssignmentPage, gradeStudentAssignment } from '@/api/assignment';

export default {
  name: "AssignmentSubmissions",
  props: {
    assignmentInfo: Object, // 从父组件传入当前作业信息
  },
  data() {
    return {
      submissions: [],
      loadingSubmissions: false,
      pageNumSubmissions: 1,
      pageSizeSubmissions: 10,
      totalSubmissions: 0,

      gradeFormVisible: false,
      gradeForm: {}, // 包含 id (StudentAssignment的id), score, teacherComment, gradedFilePath
      currentSubmission: {}, // 当前正在批改的提交
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      gradedFileList: [],
    };
  },
  watch: {
    assignmentInfo: {
      immediate: true,
      handler(newVal) {
        if (newVal && newVal.id) {
          this.loadSubmissions(1);
        }
      }
    }
  },
  methods: {
    clearGradedUpload() {
      if(this.$refs.gradedUploadRef) {
        this.$refs.gradedUploadRef.clearFiles();
      }
      this.gradedFileList = [];
    },
    loadSubmissions(pageNum) {
      if (!this.assignmentInfo || !this.assignmentInfo.id) return;
      if (pageNum) this.pageNumSubmissions = pageNum;
      this.loadingSubmissions = true;
      getSubmissionsForAssignmentPage(this.assignmentInfo.id, {
        pageNum: this.pageNumSubmissions,
        pageSize: this.pageSizeSubmissions,
      }).then(res => {
        if (res.code === '200' && res.data) {
          this.submissions = res.data.list;
          this.totalSubmissions = res.data.total;
        } else {
          this.submissions = []; this.totalSubmissions = 0;
          this.$message.error(res.msg || '加载提交列表失败');
        }
      }).finally(() => {
        this.loadingSubmissions = false;
      });
    },
    handleSubmissionsPageChange(pageNum) {
      this.loadSubmissions(pageNum);
    },
    handleGrade(submission) {
      this.currentSubmission = submission;
      this.gradeForm = {
        id: submission.id, // StudentAssignment的ID
        score: submission.score || '',
        teacherComment: submission.teacherComment || '',
        gradedFilePath: submission.gradedFilePath || null,
      };
      if (this.gradeForm.gradedFilePath) {
        this.gradedFileList = [{name: this.gradeForm.gradedFilePath.split('/').pop(), url: this.gradeForm.gradedFilePath}];
      } else {
        this.gradedFileList = [];
      }
      if (this.$refs.gradedUploadRef) this.$refs.gradedUploadRef.clearFiles();
      this.gradeFormVisible = true;
    },
    saveGrade() {
      // 可以添加表单校验
      gradeStudentAssignment(this.gradeForm).then(res => {
        if (res.code === '200') {
          this.$message.success('批改成功');
          this.gradeFormVisible = false;
          this.loadSubmissions(this.pageNumSubmissions);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleGradedFileSuccess(response, file) {
      if (response.code === '200') {
        this.gradeForm.gradedFilePath = response.data;
        this.gradedFileList = [{ name: file.name, url: response.data }];
        this.$message.success('批改附件上传成功');
      } else {
        this.$message.error('批改附件上传失败: ' + response.msg);
        this.gradedFileList = [];
        if (this.$refs.gradedUploadRef) this.$refs.gradedUploadRef.clearFiles();
      }
    },
    handleGradedFileRemove() {
      this.gradeForm.gradedFilePath = null;
      this.gradedFileList = [];
    },
    formatStatus(status) {
      if (status === 'PENDING_REVIEW') return '待批阅';
      if (status === 'REVIEWED') return '已批阅';
      if (status === 'LATE') return '迟交';
      return status;
    },
    statusTagType(status) {
      if (status === 'REVIEWED') return 'success';
      if (status === 'LATE') return 'warning';
      return 'info';
    }
  }
};
</script>