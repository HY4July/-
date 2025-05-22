<template>
  <div class="manager-container">
    <div v-if="top" class="front-notice-bar">
      <i class="el-icon-bell" style="margin-right: 2px"></i>公告：{{ top }}
    </div>

    <div class="manager-header">
      <div class="manager-header-left">
        <img src="@/assets/imgs/logo.png" alt="Logo" @click="goToFrontHome" style="cursor: pointer;">
        <div class="title" @click="goToFrontHome" style="cursor: pointer;">教学互动平台</div>
      </div>
      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right" v-if="$route.meta.name && $route.path !== '/front/home'">
          <el-breadcrumb-item :to="{ path: '/front/home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="manager-header-right">
        <div v-if="!user.username" style="display: flex; align-items: center;">
          <el-button style="margin-right: 10px;" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </div>
        <div v-else>
          <el-dropdown class="avatar-container" @command="handleCommand">
            <div class="avatar-wrapper">
              <img :src="user.avatar || require('@/assets/imgs/default-avatar.png')" class="user-avatar" alt="User Avatar">
              <span class="user-name">{{ user.name || user.username }}</span>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="person">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="manager-main">
      <div class="manager-main-left">
        <el-menu
            :default-active="$route.path"
            class="el-menu-vertical-demo"
            router
            style="border-right: none;"
            background-color="#222b40"
            text-color="#ccc"
            active-text-color="#409EFF"
            :unique-opened="true">

          <el-menu-item index="/front/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>

          <template v-if="user.role === 'STUDENT'">
            <el-menu-item index="/front/my-courses">
              <i class="el-icon-notebook-2"></i>
              <span slot="title">我的课程</span>
            </el-menu-item>
            <el-menu-item index="/front/my-submissions">
              <i class="el-icon-document-checked"></i>
              <span slot="title">我的作业</span>
            </el-menu-item>
          </template>

          <el-menu-item index="/front/resources">
            <i class="el-icon-files"></i>
            <span slot="title">资源库</span>
          </el-menu-item>

          <el-menu-item index="/front/notices">
            <i class="el-icon-bell"></i>
            <span slot="title">通知公告</span>
          </el-menu-item>

          <el-menu-item index="/front/person" v-if="user.username">
            <i class="el-icon-user"></i>
            <span slot="title">个人中心</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="manager-main-right">
        <router-view @update:user="updateUser" />
      </div>
    </div>
  </div>
</template>

<script>
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
    this.loadNotice();
    this.checkUser(); // 确保用户信息被加载
  },
  methods: {
    checkUser() {
      // 如果this.user为空对象但localStorage中有数据，则重新加载
      if (Object.keys(this.user).length === 0 && localStorage.getItem("xm-user")) {
        this.user = JSON.parse(localStorage.getItem("xm-user"));
      }
    },
    goToFrontHome() {
      if (this.$route.path !== '/front/home') {
        this.$router.push('/front/home');
      }
    },
    loadNotice() {
      this.$request.get('/notice/selectAll').then(res => {
        if (res.code === '200' && res.data && res.data.length) {
          this.notice = res.data;
          let i = 0;
          this.top = this.notice[0].content;
          const intervalId = setInterval(() => {
            if (this.notice.length > 0) { // 确保 notice 数组仍然有内容
              this.top = this.notice[i % this.notice.length].content;
              i++;
            } else {
              clearInterval(intervalId); // 如果公告列表为空则清除定时器
              this.top = "暂无最新公告";
            }
          }, 7000);
          this.$once('hook:beforeDestroy', () => {
            clearInterval(intervalId);
          });
        } else {
          this.top = "暂无最新公告";
        }
      }).catch(err => {
        console.error("加载公告失败:", err);
        this.top = "公告加载失败";
      });
    },
    updateUser() { // 当子组件（如个人中心）更新了用户信息后，触发此方法
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}');
    },
    logout() {
      localStorage.removeItem("xm-user");
      this.user = {};
      this.$router.push("/login");
      this.$message.success("退出成功");
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.logout();
      } else if (command === 'person') {
        if (this.$route.path !== '/front/person') {
          this.$router.push('/front/person');
        }
      }
    }
  }
}
</script>

<style scoped>
/* 引入 manager.css 以复用其布局和 ElementUI 覆写样式 */
@import "@/assets/css/manager.css";
@import "@/assets/css/global.css"; /* 引入全局样式 */

.manager-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8f8ff;
}

.front-notice-bar {
  background-color: #fff9ea;
  color: #e6a23c;
  padding: 8px 20px;
  font-size: 13px;
  text-align: center;
  border-bottom: 1px solid #ebeef5;
  z-index: 1001;
}

/* 确保 manager.css 中的样式能正确应用，或在此处进行必要的覆盖和调整 */
.manager-header {
  /* background-color: #2c334c; */ /* 应由 manager.css 提供 */
}

.manager-header-left .title {
  /* color: #ddd; */ /* 应由 manager.css 提供 */
}

.manager-header-center .el-breadcrumb {
  margin-left: 20px;
  line-height: 60px;
}

.manager-header-center .el-breadcrumb__inner,
.manager-header-center .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: #a2a8be !important;
}
.manager-header-center .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: #fff !important;
}

.manager-header-center .el-breadcrumb__inner.is-link:hover,
.manager-header-center .el-breadcrumb__item:last-child .el-breadcrumb__inner:hover {
  color: #409EFF !important;
}

.manager-header-right .avatar-container {
  margin-right: 20px;
  cursor: pointer;
}

.manager-header-right .avatar-wrapper {
  display: flex;
  align-items: center;
  color: #ccc;
}

.manager-header-right .user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}
.manager-header-right .user-name {
  font-size: 14px;
}

.manager-main-left {
  /* width: 200px; */ /* 期望由 manager.css 控制 */
  /* background-color: #222b40; */ /* 期望由 manager.css 控制 */
  /* min-height: calc(100vh - 60px (头部高度) - (公告栏高度，如果存在)); */
}

.manager-main-left .el-menu {
  height: 100%;
}

.el-menu-vertical-demo .el-menu-item i,
.el-menu-vertical-demo .el-submenu__title i {
  margin-right: 10px;
  width: 24px; /* 与 manager.css 中对图标的定义保持一致 */
  text-align: center; /* 与 manager.css 中对图标的定义保持一致 */
  font-size: 18px; /* 与 manager.css 中对图标的定义保持一致 */
  color: #ccc; /* 确保图标颜色 */
}

.el-menu-vertical-demo .el-menu-item.is-active i,
.el-menu-vertical-demo .el-menu-item.is-active span {
  color: #409EFF !important; /* Element UI 默认激活颜色或主题色 */
}
.el-menu-vertical-demo .el-menu-item.is-active {
  background-color: #1d2637 !important; /* 激活项背景色，可微调 */
}


.el-menu-vertical-demo .el-menu-item:hover {
  background-color: #001f3a !important; /* 鼠标悬浮时背景色 */
}
.el-menu-vertical-demo .el-menu-item:hover i,
.el-menu-vertical-demo .el-menu-item:hover span {
  color: #fff !important; /* 鼠标悬浮时文字和图标颜色 */
}

</style>