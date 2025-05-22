<template>
  <div class="main-content" style="width: 80%; margin: 20px auto;">
    <el-card header="教学资源库">
      <div style="margin-bottom: 20px;">
        <el-input placeholder="请输入资源名称搜索" v-model="searchName" style="width: 200px; margin-right: 10px;"></el-input>
        <el-select v-model="searchCourseId" placeholder="按课程筛选" clearable style="width: 200px; margin-right: 10px;">
          <el-option v-for="course in courseList" :key="course.id" :label="course.name" :value="course.id"></el-option>
        </el-select>
        <el-button @click="loadResources(1)">筛选</el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="6" v-for="resource in tableData" :key="resource.id" style="margin-bottom: 20px;">
          <el-card shadow="hover" style="height: 100%;">
            <div slot="header" class="clearfix">
              <span class="line1" :title="resource.name"><strong>{{ resource.name }}</strong></span>
            </div>
            <div style="font-size: 13px; color: #666;">
              <p class="line2" :title="resource.description">描述：{{ resource.description || '暂无描述' }}</p>
              <p>类型：{{ resource.type || '未知' }}</p>
              <p>大小：{{ resource.size ? (resource.size / 1024).toFixed(2) + ' KB' : '未知' }}</p>
              <p>课程：{{ resource.courseName || '通用资源' }}</p>
              <p>教师：{{ resource.teacherName || '匿名' }}</p>
              <p>上传时间：{{ resource.uploadTime }}</p>
              <p>下载次数：{{ resource.downloads }}</p>
            </div>
            <div style="margin-top: 10px; text-align: right;">
              <el-button type="primary" size="mini" @click="downloadResource(resource)">下载</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!tableData.length && !loading" description="暂无教学资源"></el-empty>

      <div class="pagination" style="margin-top: 20px; text-align: center;">
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
import { getTeachingResourcesPage, incrementResourceDownloads } from '@/api/teachingResource';
import { getAllCourses } from '@/api/course';

export default {
  name: "ResourceLibrary",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 12, // 每页显示12个，方便3列或4列布局
      total: 0,
      searchName: '',
      searchCourseId: null,
      courseList: [],
      loading: false,
    };
  },
  created() {
    this.loadCourses();
    this.loadResources(1);
  },
  methods: {
    loadCourses() {
      getAllCourses({}).then(res => {
        if (res.code === '200') {
          this.courseList = res.data;
        }
      });
    },
    loadResources(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.loading = true;
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
          if(res.code !== '200') this.$message.error(res.msg || '加载资源失败');
        }
      }).catch(err => {
        this.$message.error('请求资源失败');
      }).finally(() => {
        this.loading = false;
      });
    },
    handleCurrentChange(pageNum) {
      this.loadResources(pageNum);
    },
    downloadResource(resource) {
      incrementResourceDownloads(resource.id).then(() => {
        // 更新前端显示的下载次数，或重新加载列表以获取最新数据
        // 为简单起见，这里直接打开链接，下载次数依赖下次列表刷新
        resource.downloads++; // 立即更新UI下载次数
      }).catch(err => console.error("下载计数增加失败", err));
      window.open(resource.path); // 直接打开文件URL进行下载
    }
  }
};
</script>

<style scoped>
.main-content { /* 样式已在 global.css 定义 */
  /* padding: 10px; */
}
.el-card__header .clearfix:before,
.el-card__header .clearfix:after {
  display: table;
  content: "";
}
.el-card__header .clearfix:after {
  clear: both
}
.line1 { /* global.css 中已有 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.line2 { /* global.css 中已有 */
  word-break: break-all;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2; /* 超出几行省略 */
  overflow: hidden;
}
</style>