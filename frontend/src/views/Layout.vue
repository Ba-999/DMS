<template>
  <el-container class="layout">
    <!-- 桌面端侧边栏 -->
    <el-aside :width="isMobile ? '0' : '210px'" class="aside mobile-hide">
      <div class="brand">
        <i class="el-icon-s-home" style="margin-right:6px"></i>
        <span>宿舍管理系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#f8fbff"
        active-text-color="#ffffff"
      >
        <el-menu-item v-for="r in menus" :key="r.path" :index="'/' + r.path">
          <i :class="r.meta.icon"></i>
          <span slot="title">{{ r.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 移动端抽屉菜单 -->
    <el-drawer
      :visible.sync="drawerOpen"
      direction="ltr"
      size="220px"
      :with-header="false"
      custom-class="mobile-drawer"
    >
      <div class="brand">
        <i class="el-icon-s-home" style="margin-right:6px"></i>
        <span>宿舍管理系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#f8fbff"
        active-text-color="#ffffff"
        @select="drawerOpen = false"
      >
        <el-menu-item v-for="r in menus" :key="r.path" :index="'/' + r.path">
          <i :class="r.meta.icon"></i>
          <span slot="title">{{ r.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-drawer>

    <el-container>
      <el-header class="header">
        <div class="flex-row">
          <el-button
            class="mobile-only"
            icon="el-icon-menu"
            circle size="mini"
            @click="drawerOpen = true"
          ></el-button>
          <span class="page-title">{{ $route.meta.title }}</span>
        </div>
        <el-dropdown @command="onCmd" trigger="click">
          <div class="user-info flex-row">
            <el-avatar :size="32" :src="avatarSrc" icon="el-icon-user-solid" />
            <span class="nickname">{{ user.nickname || user.username }}</span>
            <i class="el-icon-arrow-down"></i>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile" icon="el-icon-user-solid">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" icon="el-icon-switch-button" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-header>
      <el-main class="main">
        <transition name="fade" mode="out-in">
          <router-view />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { logout as apiLogout } from '../api'

const LAST_LOGIN_KEY = 'dms_last_login'

export default {
  name: 'Layout',
  data() {
    return {
      drawerOpen: false,
      isMobile: window.innerWidth < 768
    }
  },
  computed: {
    user() { return this.$store.state.user || {} },
    userType() { return this.$store.state.userType || 'admin' },
    avatarSrc() {
      const a = this.user.avatar
      if (!a) return ''
      return a.startsWith('http') ? a : '/api' + a
    },
    menus() {
      const layout = this.$router.options.routes.find(r => r.path === '/')
      return (layout.children || []).filter(r => r.name !== 'profile' && (!r.meta || !r.meta.roles || r.meta.roles.includes(this.userType)))
    }
  },
  mounted() {
    window.addEventListener('resize', this.onResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize)
  },
  methods: {
    onResize() { this.isMobile = window.innerWidth < 768 },
    saveLastLoginPreference() {
      const user = this.user || {}
      const loginType = this.userType === 'student' ? 'student' : 'admin'
      const username = loginType === 'student' ? (user.username || '') : (user.username || 'admin')
      const remembered = (() => {
        try {
          return JSON.parse(localStorage.getItem('dms_remember_login') || 'null')
        } catch (_) {
          return null
        }
      })()
      const rememberedCurrent = remembered && remembered[loginType] ? remembered[loginType] : null
      const lastLogin = (() => {
        try {
          return JSON.parse(sessionStorage.getItem(LAST_LOGIN_KEY) || '{}') || {}
        } catch (_) {
          return {}
        }
      })()
      lastLogin[loginType] = {
        loginType,
        username,
        password: rememberedCurrent ? rememberedCurrent.password || '' : '',
        rememberMe: !!rememberedCurrent
      }
      sessionStorage.setItem(LAST_LOGIN_KEY, JSON.stringify(lastLogin))
    },
    async onCmd(cmd) {
      if (cmd === 'profile') {
        this.$router.push('/profile')
      } else if (cmd === 'logout') {
        try {
          await this.$confirm('确认退出登录?', '提示', { type: 'warning' })
        } catch (_) { return }
        try { await apiLogout() } catch (_) {}
        this.saveLastLoginPreference()
        this.$store.dispatch('logout')
        this.$message.success('已退出')
        this.$router.replace('/login')
      }
    }
  }
}
</script>

<style scoped>
.layout { height: 100vh; }
.aside {
  background: linear-gradient(180deg, #7dc8ff 0%, #dff4ff 100%);
  overflow: hidden;
  transition: width 0.25s ease;
  box-shadow: 8px 0 24px rgba(143, 173, 207, 0.16);
}
.brand {
  height: 56px; line-height: 56px;
  color: #ffffff; font-size: 16px; font-weight: 600;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.35);
  background: rgba(255,255,255,0.12);
}
.aside >>> .el-menu { border-right: none; }
.aside >>> .el-menu-item,
.mobile-drawer /deep/ .el-menu-item {
  margin: 6px 10px;
  border-radius: 10px;
}
.aside >>> .el-menu-item:hover,
.mobile-drawer /deep/ .el-menu-item:hover {
  background: rgba(255,255,255,0.16) !important;
}
.aside >>> .el-menu-item.is-active,
.mobile-drawer /deep/ .el-menu-item.is-active {
  background: rgba(255,255,255,0.26) !important;
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.18);
}

.header {
  background: rgba(255,255,255,0.82);
  border-bottom: 1px solid rgba(217, 234, 247, 0.8);
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 16px;
  box-shadow: 0 4px 16px rgba(126, 153, 189, 0.08);
  backdrop-filter: blur(10px);
}
.page-title { font-size: 16px; font-weight: 600; margin-left: 8px; color: #5c6b86; }
.user-info > * { margin-right: 6px; }
.nickname { font-size: 14px; color: #6c7b94; }

.main {
  background: var(--theme-page-bg);
  padding: 0;
  overflow-y: auto;
}

.mobile-drawer /deep/ .el-drawer { background: linear-gradient(180deg, #7dc8ff 0%, #dff4ff 100%); }
.mobile-drawer /deep/ .el-drawer__body { padding: 0; overflow: hidden; }
</style>
<style>
.mobile-drawer .el-menu { border-right: none !important; }
</style>
