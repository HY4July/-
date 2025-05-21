<template>
  <div class="main-content" style="width: 70%; margin: 5px auto">
    <el-card header="平台公告">
      <el-table :data="tableData" stripe @row-click="goToDetail">
        <el-table-column prop="title" label="标题"></el-table-column>
        <el-table-column prop="user" label="发布人" width="150"></el-table-column>
        <el-table-column prop="time" label="发布时间" width="180"></el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="primary" text @click.stop="goToDetail(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination" style="margin-top: 20px; text-align: right;">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getNotices } from '@/api/notice' // 确保路径正确

export default {
  name: "FrontNoticeList",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
    }
  },
  created() {
    this.loadNotices()
  },
  methods: {
    loadNotices() {
      getNotices({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list || []
          this.total = res.data?.total || 0
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.loadNotices()
    },
    goToDetail(row) {
      this.$router.push('/front/notice/' + row.id);
    }
  }
}
</script>

<style scoped>
.main-content .el-table__row {
  cursor: pointer;
}
</style>