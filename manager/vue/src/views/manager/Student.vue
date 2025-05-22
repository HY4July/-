<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入账号查询" style="width: 200px; margin-right: 10px" v-model="username"></el-input>
      <el-input placeholder="请输入姓名查询" style="width: 200px; margin-right: 10px" v-model="searchName"></el-input>
      <el-select v-model="searchClassId" placeholder="请选择班级筛选" style="width: 200px; margin-right: 10px" clearable>
        <el-option v-for="item in classesList" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-button type="info" plain @click="load(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column label="头像">
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px; border-radius: 50%" v-if="scope.row.avatar"
                        :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="账号"></el-table-column>
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="role" label="角色"></el-table-column>
        <el-table-column prop="collegeName" label="学院"></el-table-column> <el-table-column prop="specialityName" label="专业"></el-table-column> <el-table-column prop="className" label="班级"></el-table-column> <el-table-column prop="score" label="学分"></el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" style="margin-top: 20px">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>


    <el-dialog title="学生信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="头像">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handleAvatarSuccess"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="用户名"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="学院ID" prop="collegeId"> <el-input v-model="form.collegeId" placeholder="请输入学院ID"></el-input>
        </el-form-item>
        <el-form-item label="专业ID" prop="specialityId"> <el-input v-model="form.specialityId" placeholder="请输入专业ID"></el-input>
        </el-form-item>
        <el-form-item label="所属班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%" clearable>
            <el-option v-for="item in classesList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="学分" prop="score">
          <el-input-number v-model="form.score" :min="0" placeholder="学分"></el-input-number>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAllClasses } from '@/api/classes' // 确保你已经创建了 src/api/classes.js 并导出了此方法

export default {
  name: "Student",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      username: null,    // 按账号搜索
      searchName: null,  // 新增：按姓名搜索
      searchClassId: null, // 新增：按班级ID搜索
      fromVisible: false,
      form: {}, // 编辑/新增表单数据
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        name: [ // 为姓名添加校验
          { required: true, message: '请输入姓名', trigger: 'blur' },
        ],
        classId: [ // 为班级添加校验
          { required: true, message: '请选择班级', trigger: 'change' }
        ],
        // 可为 collegeId, specialityId, score 添加校验规则
      },
      ids: [],
      classesList: [], // 存储班级列表数据
    }
  },
  created() {
    this.load(1);
    this.loadClasses(); // 组件创建时加载班级列表
  },
  methods: {
    loadClasses() {
      getAllClasses().then(res => {
        if (res.code === '200' && res.data) {
          this.classesList = res.data;
        } else {
          this.$message.error(res.msg || "加载班级列表失败");
        }
      }).catch(err => {
        console.error("请求班级列表接口失败:", err);
        this.$message.error("请求班级列表接口失败");
      });
    },
    handleAdd() {
      this.form = { role: 'STUDENT', score: 0 }; // 新增数据时清空表单，并设置默认角色和学分
      this.fromVisible = true;
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.fromVisible = true;
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/student/update' : '/student/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {
              this.$message.success('保存成功');
              this.load(this.form.id ? this.pageNum : 1); // 编辑刷新当前页，新增刷新第一页
              this.fromVisible = false;
            } else {
              this.$message.error(res.msg);
            }
          });
        }
      });
    },
    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', { type: "warning" }).then(() => {
        this.$request.delete('/student/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(this.pageNum); // 删除后刷新当前页
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => {});
    },
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id);
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据');
        return;
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', { type: "warning" }).then(() => {
        this.$request.delete('/student/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(1); // 批量删除后加载第一页
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => {});
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.$request.get('/student/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
          name: this.searchName, // 传递姓名搜索条件
          classId: this.searchClassId // 传递班级ID搜索条件
        }
      }).then(res => {
        if (res.code === '200' && res.data) {
          this.tableData = res.data.list;
          this.total = res.data.total;
        } else {
          this.tableData = [];
          this.total = 0;
          if(res.code !== '200') this.$message.error(res.msg || '加载数据失败');
        }
      }).catch(err => {
        console.error("加载学生列表失败:", err);
        this.$message.error('请求失败');
        this.tableData = [];
        this.total = 0;
      });
    },
    reset() {
      this.username = null;
      this.searchName = null;
      this.searchClassId = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
    handleAvatarSuccess(response, file, fileList) {
      if (response.code === '200') {
        this.form.avatar = response.data;
      } else {
        this.$message.error('头像上传失败: ' + response.msg);
      }
    },
  }
}
</script>

<style scoped>
/* 您可以添加或修改这里的样式 */
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
/* 如果您不使用 el-icon-plus 作为上传触发器，可以移除或调整以下样式 */
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px; /* 根据您的布局调整 */
  height: 178px; /* 根据您的布局调整 */
  line-height: 178px; /* 根据您的布局调整 */
  text-align: center;
}
.avatar { /* 这个样式可能用于预览，确保它与el-image的样式协调 */
  width: 100%; /* 或具体尺寸 */
  height: 100%; /* 或具体尺寸 */
  display: block;
}
</style>