<template>
  <div class="main-content" style="width: 70%; margin: 20px auto;">
    <el-card v-if="notice">
      <div slot="header">
        <span style="font-size: 20px; font-weight: bold;">{{ notice.title }}</span>
        <div style="font-size: 12px; color: #666; margin-top: 10px;">
          <span>发布人：{{ notice.user }}</span>
          <span style="margin-left: 20px;">发布时间：{{ notice.time }}</span>
        </div>
      </div>
      <div v-html="notice.content" class="w-e-text"></div>
    </el-card>
    <el-empty v-else description="公告不存在"></el-empty>
  </div>
</template>

<script>
import { getNoticeById } from '@/api/notice' // 确保路径正确

export default {
  name: "FrontNoticeDetail",
  data() {
    return {
      notice: null,
      noticeId: this.$route.params.id
    }
  },
  created() {
    this.loadNoticeDetail()
  },
  methods: {
    loadNoticeDetail() {
      if (this.noticeId) {
        getNoticeById(this.noticeId).then(res => {
          if (res.code === '200') {
            this.notice = res.data
          } else {
            this.$message.error(res.msg)
          }
        })
      }
    }
  }
}
</script>

<style scoped>
.w-e-text { /* 简易处理，如果用富文本编辑器，会有更复杂的样式 */
  line-height: 1.8;
  white-space: pre-wrap; /* 保留换行和空格 */
}
</style>