<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入作业标题查询" style="width: 200px; margin-right: 10px" v-model="searchTitle"></el-input>
      <el-select v-model="searchCourseId" placeholder="筛选课程" clearable style="width: 200px; margin-right: 10px;">
        <el-option v-for="item in teacherCourses" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-button type="info" plain @click="loadAssignments(1)">查询</el-button>
      <el-button type="warning" plain @click="resetSearch">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handlePublish">发布新作业</el-button>
    </div>

    <div class="table">
      <el-table :data="assignments" stripe>
        <el-table-column prop="id" label="ID" width="70" sortable></el-table-column>
        <el-table-column prop="title" label="作业标题"></el-table-column>
        <el-table-column prop="courseName" label="所属课程"></el-table-column>
        <el-table-column prop="publishTime" label="发布时间"></el-table-column>
        <el-table-column prop="dueTime" label="截止时间"></el-table-column>
        <el-table-column label="附件">
          <template v-slot="scope">
            <el-link v-if="scope.row.attachmentPath" :href="scope.row.attachmentPath" target="_blank" type="primary">查看附件</el-link>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="handleDelete(scope.row.id)">删除</el-button>
            <el-button size="mini" type="success" plain @click="viewSubmissions(scope.row)">查看提交</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination" style="margin-top: 20px">
        <el-pagination background @current-change="handleCurrentChangeAssignments" :current-page="pageNumAssignments" :page-size="pageSizeAssignments" layout="total, prev, pager, next" :total="totalAssignments"></el-pagination>
      </div>
    </div>

    <el-dialog :title="formTitle" :visible.sync="formVisible" width="60%" :close-on-click-modal="false" destroy-on-close @close="clearUpload">
      <el-form :model="assignmentForm" label-width="100px" ref="assignmentFormRef" :rules="assignmentRules">
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="assignmentForm.title" placeholder="请输入作业标题"></el-input>
        </el-form-item>
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="assignmentForm.courseId" placeholder="请选择课程" style="width: 100%;">
            <el-option v-for="item in teacherCourses" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="作业内容" prop="content">
          <el-input type="textarea" :rows="5" v-model="assignmentForm.content" placeholder="请输入作业内容和要求"></el-input>
        </el-form-item>
        <el-form-item label="截止时间" prop="dueTime">
          <el-date-picker v-model="assignmentForm.dueTime" type="datetime" placeholder="选择截止日期时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="作业附件">
          <el-upload ref="assignmentUploadRef" :action="$baseUrl + '/files/upload'" :headers="{ token: user.token }" :on-success="handleAssignmentAttachmentSuccess" :on-remove="handleAssignmentAttachmentRemove" :file-list="assignmentAttachmentList" :limit="1">
            <el-button size="small" type="primary">上传附件</el-button>
            <div slot="tip" class="el-upload__tip">可选，单个文件</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveAssignment">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="学生提交列表" :visible.sync="submissionsVisible" width="70%" destroy-on-close>
      <AssignmentSubmissions :assignment-info="currentAssignmentInfo" v-if="submissionsVisible"/>
    </el-dialog>

  </div>
</template>

<script>
import { getTeacherAssignmentsPage, publishAssignment, updateAssignmentInfo, deleteAssignmentInfo, getAssignmentInfoById } from '@/api/assignment';
import { getAllCourses } from '@/api/course'; //  或者一个只获取该教师课程的接口
import AssignmentSubmissions from './AssignmentSubmissions.vue'; // 新建的子组件

export default {
  name: "AssignmentManagement",
  components: { AssignmentSubmissions },
  data() {
    return {
      assignments: [],
      pageNumAssignments: 1,
      pageSizeAssignments: 10,
      totalAssignments: 0,
      searchTitle: '',
      searchCourseId: null,

      formVisible: false,
      formTitle: '',
      assignmentForm: {},
      assignmentRules: {
        title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        dueTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }],
      },
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      teacherCourses: [], // 当前教师的课程列表
      assignmentAttachmentList: [],

      submissionsVisible: false,
      currentAssignmentInfo: null,
    };
  },
  created() {
    this.loadTeacherCourses();
    this.loadAssignments(1);
  },
  methods: {
    clearUpload() {
      if(this.$refs.assignmentUploadRef) {
        this.$refs.assignmentUploadRef.clearFiles();
      }
      this.assignmentAttachmentList = [];
    },
    loadTeacherCourses() {
      // 实际应获取当前登录教师所教授的课程列表
      // 这里为了简化，先获取所有课程，实际应有后端接口支持按teacherId查询
      getAllCourses({ teacherId: this.user.id }).then(res => { // 假设getAllCourses支持teacherId筛选
        if (res.code === '200') {
          this.teacherCourses = res.data;
        } else {
          this.$message.error('加载课程列表失败: ' + res.msg);
        }
      });
    },
    loadAssignments(pageNum) {
      if (pageNum) this.pageNumAssignments = pageNum;
      getTeacherAssignmentsPage({
        pageNum: this.pageNumAssignments,
        pageSize: this.pageSizeAssignments,
        title: this.searchTitle,
        courseId: this.searchCourseId // 后端需要支持此参数
      }).then(res => {
        if (res.code === '200' && res.data) {
          this.assignments = res.data.list;
          this.totalAssignments = res.data.total;
        } else {
          this.assignments = []; this.totalAssignments = 0;
          this.$message.error(res.msg || '加载作业列表失败');
        }
      });
    },
    resetSearch() {
      this.searchTitle = '';
      this.searchCourseId = null;
      this.loadAssignments(1);
    },
    handleCurrentChangeAssignments(pageNum) {
      this.loadAssignments(pageNum);
    },
    handlePublish() {
      this.formTitle = '发布新作业';
      this.assignmentForm = { attachmentPath: null }; // 初始化
      this.assignmentAttachmentList = [];
      if (this.$refs.assignmentUploadRef) this.$refs.assignmentUploadRef.clearFiles();
      this.formVisible = true;
    },
    handleEdit(row) {
      this.formTitle = '编辑作业';
      // this.assignmentForm = JSON.parse(JSON.stringify(row));
      getAssignmentInfoById(row.id).then(res => { // 重新获取完整信息，特别是content
        if(res.code === '200') {
          this.assignmentForm = res.data;
          if (this.assignmentForm.attachmentPath) {
            this.assignmentAttachmentList = [{ name: this.assignmentForm.attachmentPath.split('/').pop(), url: this.assignmentForm.attachmentPath }];
          } else {
            this.assignmentAttachmentList = [];
          }
        } else {
          this.$message.error(res.msg);
        }
      });
      this.formVisible = true;
    },
    saveAssignment() {
      this.$refs.assignmentFormRef.validate(valid => {
        if (valid) {
          const action = this.assignmentForm.id ? updateAssignmentInfo : publishAssignment;
          action(this.assignmentForm).then(res => {
            if (res.code === '200') {
              this.$message.success('操作成功');
              this.formVisible = false;
              this.loadAssignments(this.assignmentForm.id ? this.pageNumAssignments : 1);
            } else {
              this.$message.error(res.msg);
            }
          });
        }
      });
    },
    handleDelete(id) {
      this.$confirm('确定删除此作业吗？相关的学生提交记录也会被删除。', '确认删除', { type: 'warning' }).then(() => {
        deleteAssignmentInfo(id).then(res => {
          if (res.code === '200') {
            this.$message.success('作业删除成功');
            this.loadAssignments(this.pageNumAssignments);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => {});
    },
    handleAssignmentAttachmentSuccess(response, file) {
      if (response.code === '200') {
        this.assignmentForm.attachmentPath = response.data; // 文件URL
        this.assignmentAttachmentList = [{ name: file.name, url: response.data }];
        this.$message.success('附件上传成功');
      } else {
        this.$message.error('附件上传失败: ' + response.msg);
        this.assignmentAttachmentList = [];
        if (this.$refs.assignmentUploadRef) this.$refs.assignmentUploadRef.clearFiles();
      }
    },
    handleAssignmentAttachmentRemove() {
      this.assignmentForm.attachmentPath = null;
      this.assignmentAttachmentList = [];
    },
    viewSubmissions(assignmentInfo) {
      this.currentAssignmentInfo = assignmentInfo;
      this.submissionsVisible = true;
    }
  }
}
</script>