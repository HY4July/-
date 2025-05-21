<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入公告标题查询" style="width: 200px; margin-right: 10px" v-model="searchTitle"></el-input>
      <el-button type="info" plain @click="load(1)">查询</el-button>
      <el-button type="warning" plain @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd" v-if="user.role === 'ADMIN' || user.role === 'TEACHER'">新增公告</el-button>
      <el-button type="danger" plain @click="handleDeleteBatch" v-if="user.role === 'ADMIN' || user.role === 'TEACHER'">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="title" label="标题" show-overflow-tooltip></el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip>
          <template v-slot="scope">
            <el-button type="primary" text @click="viewContent(scope.row.content)">点击查看</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="发布时间"></el-table-column>
        <el-table-column prop="user" label="发布人"></el-table-column>
        <el-table-column label="操作" width="180" align="center" v-if="user.role === 'ADMIN' || user.role === 'TEACHER'">
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
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="公告信息" :visible.sync="formVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input type="textarea" :rows="5" v-model="form.content" placeholder="请输入公告内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="公告内容" :visible.sync="contentVisible" width="50%">
      <div v-html="currentContent" class="w-e-text"></div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="contentVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { getNotices, addNotice, updateNotice, deleteNotice, deleteBatchNotices } from '@/api/notice' // 确保路径正确

export default {
  name: "ManagerNotice",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      searchTitle: '',
      formVisible: false,
      contentVisible: false,
      currentContent: '',
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' },
        ]
      },
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    handleAdd() {
      this.form = {} // 清空表单数据
      this.formVisible = true
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.formVisible = true
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          const request = this.form.id ? updateNotice(this.form) : addNotice(this.form)
          request.then(res => {
            if (res.code === '200') {
              this.$message.success('操作成功')
              this.formVisible = false
              this.load(this.form.id ? this.pageNum : 1) // 编辑后刷新当前页，新增后刷新第一页
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
    handleDelete(id) {
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(() => {
        deleteNotice(id).then(res => {
          if (res.code === '200') {
            this.$message.success('删除成功')
            this.load(this.pageNum)
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {})
    },
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id)
    },
    handleDeleteBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(() => {
        deleteBatchNotices(this.ids).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1) // 批量删除后返回第一页
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {})
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      getNotices({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        title: this.searchTitle,
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list || []
          this.total = res.data?.total || 0
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reset() {
      this.searchTitle = ''
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    viewContent(content) {
      this.currentContent = content;
      this.contentVisible = true;
    }
  }
}
</script>

<style scoped>
/* 可以在这里添加一些特定于此页面的样式 */
</style>