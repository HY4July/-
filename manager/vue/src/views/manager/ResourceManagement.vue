<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入资源名称" style="width: 200px; margin-right: 10px" v-model="searchName"></el-input>
      <el-select v-model="searchCourseId" placeholder="请选择课程" style="width: 200px; margin-right: 10px" clearable>
        <el-option v-for="item in courseList" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-button type="info" plain @click="load(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd" v-if="user.role === 'ADMIN' || user.role === 'TEACHER'">上传资源</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="资源名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="类型" width="100"></el-table-column>
        <el-table-column prop="courseName" label="所属课程"></el-table-column>
        <el-table-column prop="teacherName" label="上传教师"></el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="150"></el-table-column>
        <el-table-column prop="size" label="大小(KB)" width="100">
          <template v-slot="scope">{{ (scope.row.size / 1024).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="downloads" label="下载次数" width="100"></el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="success" size="mini" @click="downloadResource(scope.row.path)">下载</el-button>
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini" v-if="user.role === 'ADMIN' || user.id === scope.row.teacherId">编辑</el-button>
            <el-button plain type="danger" size="mini" @click="handleDelete(scope.row)" v-if="user.role === 'ADMIN' || user.id === scope.row.teacherId">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination" style="margin-top: 20px">
        <el-pagination background @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, prev, pager, next" :total="total"></el-pagination>
      </div>
    </div>

    <el-dialog title="教学资源信息" :visible.sync="formVisible" width="50%" :close-on-click-modal="false" destroy-on-close @close="clearUploadedFile">
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="资源名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入资源名称"></el-input>
        </el-form-item>
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择所属课程" style="width: 100%" clearable>
            <el-option v-for="item in courseList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="form.description" placeholder="请输入资源描述"></el-input>
        </el-form-item>
        <el-form-item label="上传文件" prop="path">
          <el-upload
              ref="upload"
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              :on-success="handleFileUploadSuccess"
              :on-remove="handleFileRemove"
              :before-upload="beforeFileUpload"
              :file-list="fileList"
              :limit="1">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传单个文件，大小不超过100MB</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="文件类型" prop="type">
          <el-input v-model="form.type" placeholder="文件类型 (如: pdf, mp4)" :disabled="!!form.id"></el-input>
        </el-form-item>
        <el-form-item label="文件大小(KB)" prop="size">
          <el-input-number v-model="form.size" :min="0" placeholder="文件大小(KB)" :disabled="true"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getTeachingResourcesPage, addTeachingResource, updateTeachingResource, deleteTeachingResource, incrementResourceDownloads } from '@/api/teachingResource';
import { getAllCourses } from '@/api/course'; // 引入获取课程列表的API

export default {
  name: "ResourceManagement",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      searchName: '',
      searchCourseId: null,
      formVisible: false,
      form: { path: '', type: '', size: 0 }, // 初始化 path, type, size
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      courseList: [], // 存储课程列表
      fileList: [], // 用于 el-upload 的 file-list
      rules: {
        name: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
        path: [{ required: true, message: '请上传资源文件', trigger: 'blur' }], // 触发方式改为blur，因为是上传后自动填充
        courseId: [{ required: true, message: '请选择所属课程', trigger: 'change' }],
      }
    }
  },
  created() {
    this.load(1);
    this.loadCourses();
  },
  methods: {
    loadCourses() {
      getAllCourses({}).then(res => { // 获取所有课程用于下拉
        if (res.code === '200') {
          this.courseList = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    beforeFileUpload(file) {
      // 可以在这里限制文件大小和类型
      const isLt100M = file.size / 1024 / 1024 < 100;
      if (!isLt100M) {
        this.$message.error('上传文件大小不能超过 100MB!');
      }
      return isLt100M;
    },
    handleFileUploadSuccess(response, file, fileList) {
      if (response.code === '200') {
        this.$message.success("文件上传成功");
        this.form.path = response.data; // 后端返回的文件URL
        this.form.type = file.name.substring(file.name.lastIndexOf('.') + 1); // 获取文件后缀作为类型
        this.form.size = parseFloat((file.size / 1024).toFixed(2)); // 文件大小KB，保留两位小数
        this.fileList = [{ name: file.name, url: response.data }]; // 更新 fileList 以显示已上传文件
        this.$refs.formRef.validateField('path'); // 手动触发路径校验
      } else {
        this.$message.error('文件上传失败: ' + response.msg);
        this.fileList = []; // 清空文件列表
        if (this.$refs.upload) { // 清空el-upload组件的文件列表
          this.$refs.upload.clearFiles();
        }
      }
    },
    handleFileRemove(file, fileList) {
      this.form.path = '';
      this.form.type = '';
      this.form.size = 0;
      this.fileList = [];
    },
    clearUploadedFile(){ // Dialog关闭时清空
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles();
      }
      this.fileList = [];
    },
    handleAdd() {
      this.form = { path: '', type: '', size: 0, downloads: 0 }; // 初始化表单
      this.fileList = []; // 清空文件列表
      if (this.$refs.upload) { this.$refs.upload.clearFiles();}
      this.formVisible = true;
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      if (this.form.path) { // 如果有已上传的文件，设置到fileList中用于显示
        this.fileList = [{ name: this.form.name, url: this.form.path }];
      } else {
        this.fileList = [];
      }
      this.formVisible = true;
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          if (!this.form.path) {
            this.$message.warning("请先上传资源文件");
            return;
          }
          const request = this.form.id ? updateTeachingResource(this.form) : addTeachingResource(this.form);
          request.then(res => {
            if (res.code === '200') {
              this.$message.success('操作成功');
              this.formVisible = false;
              this.load(this.form.id ? this.pageNum : 1);
            } else {
              this.$message.error(res.msg);
            }
          });
        }
      });
    },
    handleDelete(row) {
      this.$confirm('您确定删除该资源吗？删除后文件也将从服务器移除。', '确认删除', {type: "warning"}).then(() => {
        deleteTeachingResource(row.id).then(res => {
          if (res.code === '200') {
            this.$message.success('数据库记录删除成功');
            // 调用后端接口删除实际文件
            if (row.path) {
              const flag = row.path.substring(row.path.lastIndexOf('/') + 1);
              this.$request.delete(`/files/${flag}`).then(fileRes => {
                if (fileRes.code === '200') { // 假设FileController删除成功也返回Result
                  this.$message.success('服务器文件删除成功');
                } else {
                  this.$message.error(fileRes.msg || '服务器文件删除失败');
                }
              });
            }
            this.load(this.pageNum);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => {});
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      getTeachingResourcesPage({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        name: this.searchName,
        courseId: this.searchCourseId,
      }).then(res => {
        if (res.code === '200' && res.data) {
          this.tableData = res.data.list;
          this.total = res.data.total;
        } else {
          this.tableData = [];
          this.total = 0;
          if(res.code !== '200') this.$message.error(res.msg || '加载资源列表失败');
        }
      }).catch(err => {
        console.error("加载资源列表失败:", err);
        this.$message.error('请求资源列表失败');
        this.tableData = [];
        this.total = 0;
      });
    },
    reset() {
      this.searchName = '';
      this.searchCourseId = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
    downloadResource(url) {
      // 触发下载计数
      const resourceId = this.tableData.find(item => item.path === url)?.id;
      if (resourceId) {
        incrementResourceDownloads(resourceId).catch(err => console.error("下载计数增加失败", err));
      }
      window.open(url); // 直接打开链接进行下载或预览
    }
  }
}
</script>
<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: auto; /* 调整以适应按钮 */
  display: inline-block;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
</style>