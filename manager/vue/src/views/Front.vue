<template>
  <div>
    <div class="front-notice"><i class="el-icon-bell" style="margin-right: 2px"></i>公告：{{ top }}</div>
    <div class="front-header">
      <div class="front-header-left">
        <img src="@/assets/imgs/logo.png" alt="">
        <div class="title">项目前台</div>
      </div>
      <div class="front-header-center">
        <div class="front-header-nav">
          <el-menu :default-active="$route.path" mode="horizontal" router>
            <el-menu-item index="/front/home">首页</el-menu-item>
            <el-menu-item index="/front/notices">通知公告</el-menu-item>
            <el-menu-item index="/front/person" v-if="user.username">个人中心</el-menu-item>
            <el-menu-item index="/front/resources">资源库</el-menu-item>
          </el-menu>
        </div>
      </div>
      <div class="front-header-right">
        <div v-if="!user.username">
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </div>
        <div v-else>
          <el-dropdown @command="handleCommand"> <div class="front-header-dropdown">
            <img :src="user.avatar || require('@/assets/imgs/default-avatar.png')" alt=""> <div style="margin-left: 10px">
            <span>{{ user.name }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
          </div>
          </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="person">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
    <div class="main-body">
      <router-view ref="child" @update:user="updateUser" />
    </div>
  </div>

</template>

<script>
// ... script 部分保持不变，但为了让个人中心通过 el-dropdown-item 跳转，
// 你需要在 methods 中添加 handleCommand 方法 (如果还没有的话，或者修改现有的)
// 我在上面的 template 的 el-dropdown 中补充了 @command="handleCommand"
// 并且在 el-dropdown-menu 中补充了 command="person"
// 同时，你可能需要确保用户头像 user.avatar 有一个默认值，以防图片链接失效或用户未上传头像
// 例如： <img :src="user.avatar || require('@/assets/imgs/default-avatar.png')" alt="">
// 请确保你有一个默认头像图片放在 @/assets/imgs/default-avatar.png

export default {
  name: "FrontLayout",
  data () {
    return {
      top: '',
      notice: [],
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
    }
  },
  mounted() {
    this.loadNotice()
  },
  methods: {
    loadNotice() {
      // 假设 this.$request 是在 main.js 中全局配置的 axios 实例
      this.$request.get('/notice/selectAll').then(res => {
        if (res.code === '200' && res.data) { // 确保 res.data 存在
          this.notice = res.data;
          let i = 0;
          if (this.notice && this.notice.length) {
            this.top = this.notice[0].content;
            // 建议将 setInterval 放在 mounted 钩子中，并在 beforeDestroy 中清除，避免内存泄漏
            // 但为了保持与您原代码逻辑一致，暂时保留在此
            const intervalId = setInterval(() => {
              if (this.notice && this.notice.length > 0) { // 增加判断
                this.top = this.notice[i].content;
                i++;
                if (i === this.notice.length) {
                  i = 0;
                }
              }
            }, 2500);
            // 如果组件销毁，记得清除定时器
            this.$once('hook:beforeDestroy', () => {
              clearInterval(intervalId);
            });
          }
        } else {
          // 可以选择性地处理错误，例如 this.top = "暂无公告";
          this.top = "暂无最新公告";
        }
      }).catch(err => {
        console.error("加载公告失败:", err);
        this.top = "加载公告失败";
      });
    },
    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}');
    },
    logout() {
      localStorage.removeItem("xm-user");
      this.user = {}; // 清空本地用户信息
      this.$router.push("/login");
    },
    handleCommand(command) { // 新增或修改此方法
      if (command === 'logout') {
        this.logout();
      } else if (command === 'person') {
        this.$router.push('/front/person');
      }
    }
  }
}
</script>

<style scoped>
@import "@/assets/css/front.css";
</style>