<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入班级名称查询" style="width: 200px; margin-right: 10px" v-model="searchName"></el-input>
      <el-button type="info" plain @click="load(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd" v-if="user.role === 'ADMIN'">新增班级</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="班级ID" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="班级名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="content" label="班级描述" show-overflow-tooltip></el-table-column>
        <el-table-column prop="teacherName" label="负责教师"></el-table-column>
        <el-table-column prop="specialityId" label="专业ID"></el-table-column> <el-table-column label="操作" width="180" align="center" v-if="user.role === 'ADMIN'">
        <template v-slot="scope">
          <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini">编辑</el-button>
          <el-button plain type="danger" size="mini" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
      </el-table>

      <div class="pagination" style="margin-top: 20px">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="班级信息" :visible.sync="formVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入班级名称"></el-input>
        </el-form-item>
        <el-form-item label="班级描述" prop="content">
          <el-input type="textarea" v-model="form.content" placeholder="请输入班级描述"></el-input>
        </el-form-item>
        <el-form-item label="负责教师" prop="teacherId">
          <el-select v-model="form.teacherId" placeholder="请选择负责教师" style="width: 100%" clearable>
            <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="专业ID" prop="specialityId">
          <el-input-number v-model="form.specialityId" :min="0" placeholder="请输入专业ID"></el-input-number>
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
import { getClassesPage, addClass, updateClass, deleteClass } from '@/api/classes'
import { getAllTeachers } from '@/api/teacher'

export default {
  name: "ClassManagement",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      searchName: '',
      formVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      teacherList: [],
      rules: {
        name: [ { required: true, message: '请输入班级名称', trigger: 'blur' } ],
      }
    }
  },
  created() {
    this.load(1);
    this.loadTeachers();
  },
  methods: {
    loadTeachers() {
      getAllTeachers().then(res => {
        if (res.code === '200') {
          this.teacherList = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleAdd() {
      this.form = {};
      this.formVisible = true;
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.formVisible = true;
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          const request = this.form.id ? updateClass(this.form) : addClass(this.form);
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
    handleDelete(id) {
      this.$confirm('您确定删除该班级吗？', '确认删除', {type: "warning"}).then(() => {
        deleteClass(id).then(res => {
          if (res.code === '200') {
            this.$message.success('删除成功');
            this.load(this.pageNum);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => {});
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      getClassesPage({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        name: this.searchName,
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list || [];
          this.total = res.data?.total || 0;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    reset() {
      this.searchName = '';
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    }
  }
}
</script>