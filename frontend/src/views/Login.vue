<template>
  <div class="login-wrap">
    <div class="bg-blob blob1"></div>
    <div class="bg-blob blob2"></div>
    <el-card class="login-card animate-item" shadow="always">
      <div class="hero-banner">
        <div class="hero-overlay"></div>
        <div class="hero-window window-left"></div>
        <div class="hero-window window-right"></div>
        <div class="hero-building"></div>
        <div class="hero-ground"></div>
        <div class="hero-caption">温馨宿舍 · 便捷管理</div>
      </div>
      <div class="logo">
        <i class="el-icon-s-home"></i>
      </div>
      <h2 class="title">宿舍管理系统</h2>
      <p class="subtitle">Dormitory Management</p>
      <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter.native="submit">
        <el-form-item prop="loginType">
          <el-radio-group v-model="form.loginType" size="small" style="width:100%;display:flex;justify-content:center">
            <el-radio-button label="admin">管理员</el-radio-button>
            <el-radio-button label="student">学生</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="账号" prefix-icon="el-icon-user" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="密码" prefix-icon="el-icon-lock" clearable />
        </el-form-item>
        <div class="remember-row">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
        </div>
        <el-button type="primary" :loading="loading" class="submit-btn" @click="submit">登 录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { login } from '../api'

const REMEMBER_LOGIN_KEY = 'dms_remember_login'
const LAST_LOGIN_KEY = 'dms_last_login'

export default {
  name: 'Login',
  data() {
    return {
      form: { username: 'admin', password: '123456', loginType: 'admin' },
      rememberMe: false,
      loading: false,
      rules: {
        loginType: [{ required: true, message: '请选择登录类型', trigger: 'change' }],
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.restoreLoginForm()
  },
  watch: {
    'form.loginType'(val) {
      const saved = this.getSavedLoginByType(val)
      if (saved) {
        this.form.username = saved.username || ''
        this.form.password = saved.password || ''
        this.rememberMe = !!saved.rememberMe
      } else {
        this.form.username = val === 'student' ? '' : 'admin'
        this.form.password = '123456'
        this.rememberMe = false
      }
    }
  },
  methods: {
    getStoredLogin(key, storage) {
      const raw = storage.getItem(key)
      if (!raw) return null
      try {
        return JSON.parse(raw)
      } catch (_) {
        storage.removeItem(key)
        return null
      }
    },
    getStoredLoginMap(key, storage) {
      const data = this.getStoredLogin(key, storage)
      return data && typeof data === 'object' ? data : {}
    },
    getSavedLoginByType(loginType) {
      const remembered = this.getStoredLoginMap(REMEMBER_LOGIN_KEY, localStorage)[loginType]
      if (remembered) return remembered
      const lastLogin = this.getStoredLoginMap(LAST_LOGIN_KEY, sessionStorage)[loginType]
      if (lastLogin) return lastLogin
      return null
    },
    restoreLoginForm() {
      const rememberedMap = this.getStoredLoginMap(REMEMBER_LOGIN_KEY, localStorage)
      const lastLoginMap = this.getStoredLoginMap(LAST_LOGIN_KEY, sessionStorage)
      const target = rememberedMap.admin || rememberedMap.student || lastLoginMap.admin || lastLoginMap.student
      if (!target) return
      this.rememberMe = !!target.rememberMe
      this.form = {
        username: target.username || '',
        password: target.password || '',
        loginType: target.loginType || 'admin'
      }
    },
    persistLoginPreference() {
      const payload = {
        loginType: this.form.loginType,
        username: this.form.username,
        password: this.form.password,
        rememberMe: this.rememberMe
      }
      const lastLoginMap = this.getStoredLoginMap(LAST_LOGIN_KEY, sessionStorage)
      lastLoginMap[this.form.loginType] = payload
      sessionStorage.setItem(LAST_LOGIN_KEY, JSON.stringify(lastLoginMap))

      const rememberMap = this.getStoredLoginMap(REMEMBER_LOGIN_KEY, localStorage)
      if (this.rememberMe) rememberMap[this.form.loginType] = payload
      else delete rememberMap[this.form.loginType]
      if (Object.keys(rememberMap).length) localStorage.setItem(REMEMBER_LOGIN_KEY, JSON.stringify(rememberMap))
      else localStorage.removeItem(REMEMBER_LOGIN_KEY)
    },
    submit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const { data } = await login(this.form)
          this.persistLoginPreference()
          this.$store.dispatch('setLogin', { token: data.token, user: data.user })
          this.$message.success('登录成功')
          this.$router.push(this.form.loginType === 'student' ? '/student-dormitory' : '/')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-wrap {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fafdff 0%, #eef8ff 38%, #dff2ff 72%, #bfe6ff 100%);
  overflow: hidden;
  padding: 16px;
}
.bg-blob {
  position: absolute; border-radius: 50%;
  filter: blur(60px); opacity: 0.45;
  animation: floating 6s ease-in-out infinite alternate;
}
.blob1 { width: 320px; height: 320px; background: rgba(168, 223, 255, 0.72); top: -90px; left: -90px; }
.blob2 { width: 380px; height: 380px; background: rgba(110, 196, 248, 0.58); bottom: -120px; right: -110px; animation-delay: 2s; }
@keyframes floating {
  from { transform: translate3d(0, 0, 0); }
  to   { transform: translate3d(0, 40px, 0); }
}
.login-card {
  width: 100%; max-width: 400px;
  padding: 12px 14px 18px;
  border-radius: 16px !important;
  box-shadow: 0 22px 55px rgba(87, 110, 145, 0.18) !important;
  background: rgba(255,255,255,0.9) !important;
  backdrop-filter: blur(10px);
  z-index: 1;
}
.hero-banner {
  position: relative;
  height: 136px;
  border-radius: 14px;
  margin-bottom: 14px;
  overflow: hidden;
  background: linear-gradient(180deg, #8fd2ff 0%, #dff3ff 58%, #fafdff 100%);
  box-shadow: inset 0 1px 0 rgba(255,255,255,0.55);
}
.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.28), rgba(255,255,255,0.08));
}
.hero-building {
  position: absolute;
  left: 50%;
  bottom: 26px;
  width: 180px;
  height: 72px;
  transform: translateX(-50%);
  border-radius: 8px 8px 0 0;
  background: linear-gradient(180deg, #fafdff, #dcecf8);
  box-shadow:
    0 0 0 1px rgba(120, 166, 205, 0.08),
    inset 0 -10px 0 rgba(165, 202, 228, 0.2),
    inset 0 0 0 999px transparent;
}
.hero-building::before {
  content: '';
  position: absolute;
  left: 16px;
  right: 16px;
  top: 14px;
  height: 34px;
  background-image:
    linear-gradient(rgba(124, 159, 189, 0.75), rgba(124, 159, 189, 0.75)),
    linear-gradient(rgba(124, 159, 189, 0.75), rgba(124, 159, 189, 0.75)),
    linear-gradient(rgba(124, 159, 189, 0.75), rgba(124, 159, 189, 0.75)),
    linear-gradient(rgba(124, 159, 189, 0.75), rgba(124, 159, 189, 0.75)),
    linear-gradient(rgba(124, 159, 189, 0.75), rgba(124, 159, 189, 0.75)),
    linear-gradient(rgba(124, 159, 189, 0.75), rgba(124, 159, 189, 0.75));
  background-size: 18px 14px;
  background-repeat: no-repeat;
  background-position: 0 0, 28px 0, 56px 0, 84px 0, 112px 0, 140px 0;
}
.hero-building::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 26px;
  height: 30px;
  transform: translateX(-50%);
  border-radius: 6px 6px 0 0;
  background: linear-gradient(180deg, #8ebfe2, #6ca7d2);
}
.hero-window {
  position: absolute;
  top: 18px;
  width: 42px;
  height: 62px;
  border-radius: 22px 22px 6px 6px;
  background: rgba(255,255,255,0.42);
  border: 1px solid rgba(255,255,255,0.38);
}
.window-left { left: 42px; }
.window-right { right: 42px; }
.hero-ground {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 28px;
  background: linear-gradient(180deg, #d5efff, #aedfff);
}
.hero-caption {
  position: absolute;
  left: 14px;
  top: 12px;
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.72);
  color: #7f8aa3;
  font-size: 12px;
  letter-spacing: 1px;
}
.logo {
  width: 64px; height: 64px; border-radius: 50%; margin: 0 auto 8px;
  background: linear-gradient(135deg, #63bbff, #dff4ff);
  color: #fff; font-size: 30px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8px 20px rgba(146, 188, 231, 0.42);
}
.title { text-align: center; margin: 8px 0 0; font-size: 22px; color: #5c6b86; }
.subtitle { text-align: center; color: #9aa6b8; font-size: 12px; letter-spacing: 2px; margin: 4px 0 20px; }
.submit-btn {
  width: 100%; height: 42px; font-size: 16px;
  background: linear-gradient(135deg, #69bfff, #fafdff);
  border: none;
  box-shadow: 0 8px 18px rgba(156, 188, 226, 0.4);
}
.remember-row {
  display: flex;
  justify-content: flex-end;
  margin: -2px 0 14px;
  color: #7f8aa3;
}
.tip { text-align: center; color: #98a3b3; font-size: 12px; margin: 14px 0 0; }
@media (max-width: 767px) {
  .login-card { padding: 8px 10px 14px; }
  .hero-banner { height: 118px; }
  .hero-building { width: 150px; height: 62px; }
  .hero-window { display: none; }
  .title { font-size: 20px; }
}
</style>
