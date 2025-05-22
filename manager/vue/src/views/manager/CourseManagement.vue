<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入课程名称查询" style="width: 200px; margin-right: 10px" v-model="searchName"></el-input>
      <el-select v-model="searchTeacherId" placeholder="请选择授课教师" style="width: 200px; margin-right: 10px" clearable>
        <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-button type="info" plain @click="load(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd" v-if="user.role === 'ADMIN'">新增课程</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="课程ID" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="课程名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="课程类型"></el-table-column>
        <el-table-column prop="teacherName" label="授课教师"></el-table-column>
        <el-table-column prop="score" label="学分"></el-table-column>
        <el-table-column prop="num" label="人数限制"></el-table-column>
        <el-table-column prop="room" label="上课教室"></el-table-column>
        <el-table-column prop="week" label="上课星期"></el-table-column>
        <el-table-column prop="segment" label="上课时段"></el-table-column>
        <el-table-column prop="status" label="状态"></el-table-column>
        <el-table-column label="操作" width="280" align="center"> {/* 调整宽度以容纳新按钮 */}
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini" v-if="user.role === 'ADMIN'">编辑</el-button>
            <el-button plain type="danger" size="mini" @click="handleDelete(scope.row.id)" v-if="user.role === 'ADMIN'" style="margin-left: 5px;">删除</el-button>
            <el-button type="info" size="mini" @click="handleManageDiscussions(scope.row.id)" style="margin-left: 5px;" v-if="user.role === 'ADMIN' || user.role === 'TEACHER'">讨论管理</el-button>
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

    <el-dialog title="课程信息" :visible.sync="formVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程类型" prop="type">
          <el-input v-model="form.type" placeholder="请输入课程类型"></el-input>
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="form.teacherId" placeholder="请选择授课教师" style="width: 100%">
            <el-option v-for="item in teacherList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程学分" prop="score">
          <el-input-number v-model="form.score" :min="0" placeholder="请输入课程学分"></el-input-number>
        </el-form-item>
        <el-form-item label="人数限制" prop="num">
          <el-input-number v-model="form.num" :min="0" placeholder="请输入人数限制"></el-input-number>
        </el-form-item>
        <el-form-item label="上课教室" prop="room">
          <el-input v-model="form.room" placeholder="请输入上课教室"></el-input>
        </el-form-item>
        <el-form-item label="上课星期" prop="week">
          <el-input v-model="form.week" placeholder="例如：周一、周三"></el-input>
        </el-form-item>
        <el-form-item label="上课时段" prop="segment">
          <el-input v-model="form.segment" placeholder="例如：1-2节、下午3-4节"></el-input>
        </el-form-item>
        <el-form-item label="课程状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择课程状态" style="width: 100%">
            <el-option label="即将开课" value="即将开课"></el-option>
            <el-option label="进行中" value="进行中"></el-option>
            <el-option label="已结束" value="已结束"></el-option>
          </el-select>
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
import { getCoursesPage, addCourse, updateCourse, deleteCourse } from '@/api/course'
import { getAllTeachers } from '@/api/teacher'

export default {
  name: "CourseManagement",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      searchName: '',
      searchTeacherId: null,
      formVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      teacherList: [],
      rules: {
        name: [ { required: true, message: '请输入课程名称', trigger: 'blur' } ],
        teacherId: [ { required: true, message: '请选择授课教师', trigger: 'change' } ],
        score: [ { required: true, message: '请输入课程学分', trigger: 'blur' } ],
        status: [ { required: true, message: '请选择课程状态', trigger: 'change'} ]
      },
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
          const request = this.form.id ? updateCourse(this.form) : addCourse(this.form);
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
      this.$confirm('您确定删除该课程吗？', '确认删除', {type: "warning"}).then(() => {
        deleteCourse(id).then(res => {
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
      getCoursesPage({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        name: this.searchName,
        teacherId: this.searchTeacherId,
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
      this.searchTeacherId = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
    // 新增方法：跳转到讨论管理页面
    handleManageDiscussions(courseId) {
      this.$router.push(`/course/${courseId}/manage-discussions`);
    }
  }
}
</script>

<style scoped>
/* 您现有的样式 */
</style>