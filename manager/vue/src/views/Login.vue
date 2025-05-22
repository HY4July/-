<template>
  <div class="container">
    <div style="width: 400px; padding: 30px; background-color: white; border-radius: 5px;">
      <div style="text-align: center; font-size: 20px; margin-bottom: 20px; color: #333">欢迎使用</div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item prop="role"> <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
          <el-option label="管理员" value="ADMIN"></el-option>
          <el-option label="教师" value="TEACHER"></el-option>
          <el-option label="学生" value="STUDENT"></el-option>
        </el-select>
        </el-form-item>
        <el-form-item>
          <el-button style="width: 100%; background-color: #333; border-color: #333; color: white" @click="login">登 录</el-button>
        </el-form-item>
        <div style="display: flex; align-items: center">
          <div style="flex: 1"></div>
          <div style="flex: 1; text-align: right">
            还没有账号？请 <a href="/register">注册</a>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      // dialogVisible: true, // 如果法律声明弹窗是必要的，请取消注释并确保其逻辑正确
      form: {
        username: '',
        password: '',
        role: ''
      },
      rules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
        ],
        role: [ // 为角色选择添加校验
          {required: true, message: '请选择角色', trigger: 'change'}
        ]
      }
    }
  },
  created() {
    // 如果 dialogVisible 由其他逻辑控制，请确保该逻辑存在
  },
  methods: {
    login() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 验证通过
          this.$request.post('/login', this.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem("xm-user", JSON.stringify(res.data))  // 存储用户数据
              this.$message.success('登录成功')

              // 根据角色进行不同的页面跳转
              if (res.data.role === 'STUDENT') {
                this.$router.push('/front/home'); // 学生跳转到前台首页
              } else if (res.data.role === 'TEACHER' || res.data.role === 'ADMIN') {
                this.$router.push('/home');     // 教师或管理员跳转到后台管理首页
              } else {
                this.$router.push('/'); // 默认或未知角色跳转
              }
            } else {
              this.$message.error(res.msg)
            }
          }).catch(error => {
            console.error("登录请求失败:", error);
            this.$message.error('登录请求失败，请检查网络或联系管理员');
          });
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/imgs/bg.jpg");
  background-size: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

a {
  color: #2a60c9;
}
</style>