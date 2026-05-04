<template>
  <div class="page">
    <el-row :gutter="16">
      <el-col :xs="12" :sm="8" :md="8" :lg="4" v-for="(c, i) in cards" :key="c.key">
        <el-card
          shadow="hover"
          class="stat-card animate-item hoverable"
          :style="{ animationDelay: (i * 60) + 'ms' }"
          @click.native="c.to && $router.push(c.to)"
        >
          <div class="stat-row">
            <div class="stat-icon" :style="{ background: c.bg }">
              <i :class="c.icon"></i>
            </div>
            <div class="stat-info">
              <div class="stat-num">{{ data[c.key] != null ? data[c.key] : '-' }}</div>
              <div class="stat-label">{{ c.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="overview-row" style="margin-top:16px">
      <el-col :xs="24" :sm="24" :md="12" :lg="12" class="overview-col">
        <el-card shadow="hover" class="animate-item overview-card" style="animation-delay:360ms">
          <div slot="header"><i class="el-icon-data-line"></i> 系统概览</div>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="当前用户">{{ user.nickname || user.username }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ user.role === 0 ? '超级管理员' : '普通宿管' }}</el-descriptions-item>
            <el-descriptions-item label="楼宇">{{ data.buildings || 0 }}</el-descriptions-item>
            <el-descriptions-item label="宿舍">{{ data.dormitories || 0 }}</el-descriptions-item>
            <el-descriptions-item label="学生">{{ data.students || 0 }}</el-descriptions-item>
            <el-descriptions-item label="入住率">{{ checkInRate }}%</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" class="overview-col">
        <el-card shadow="hover" class="animate-item overview-card" style="animation-delay:420ms">
          <div slot="header"><i class="el-icon-s-flag"></i> 快捷入口</div>
          <div class="quick-grid">
            <div
              v-for="q in quicks" :key="q.to"
              class="quick-item animate-item"
              @click="$router.push(q.to)"
            >
              <i :class="q.icon" :style="{ color: q.color }"></i>
              <span>{{ q.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { statsOverview } from '../api'
export default {
  name: 'Dashboard',
  data() {
    return {
      data: {},
      cards: [
        { key: 'buildings',     label: '楼宇数',     icon: 'el-icon-office-building', bg: 'linear-gradient(135deg,#69bfff,#fafdff)', to: '/building' },
        { key: 'dormitories',   label: '宿舍数',     icon: 'el-icon-house',           bg: 'linear-gradient(135deg,#7ac7ff,#e8f7ff)', to: '/dormitory' },
        { key: 'students',      label: '学生数',     icon: 'el-icon-user',            bg: 'linear-gradient(135deg,#5fb8ff,#dbf2ff)', to: '/student' },
        { key: 'checkedIn',     label: '已入住',     icon: 'el-icon-key',             bg: 'linear-gradient(135deg,#73c4ff,#f3fbff)', to: '/checkin' },
        { key: 'checkInTotal',  label: '入住记录',   icon: 'el-icon-tickets',         bg: 'linear-gradient(135deg,#66beff,#e4f5ff)', to: '/checkin' },
        { key: 'repairPending', label: '待处理报修', icon: 'el-icon-s-tools',         bg: 'linear-gradient(135deg,#84ccff,#eefaff)', to: '/repair' }
      ],
      quicks: [
        { icon: 'el-icon-office-building', label: '楼宇管理', to: '/building',  color: '#69bfff' },
        { icon: 'el-icon-house',           label: '宿舍管理', to: '/dormitory', color: '#82d1ff' },
        { icon: 'el-icon-user',            label: '学生管理', to: '/student',   color: '#5fb8ff' },
        { icon: 'el-icon-key',             label: '入住管理', to: '/checkin',   color: '#7cccf8' },
        { icon: 'el-icon-s-tools',         label: '报修管理', to: '/repair',    color: '#74c4fb' },
        { icon: 'el-icon-user-solid',      label: '个人中心', to: '/profile',   color: '#8ca2bb' }
      ]
    }
  },
  computed: {
    user() { return this.$store.state.user || {} },
    checkInRate() {
      if (!this.data.students) return 0
      return Math.round(((this.data.checkedIn || 0) / this.data.students) * 100)
    }
  },
  mounted() { this.load() },
  methods: {
    async load() {
      try { const { data } = await statsOverview(); this.data = data } catch (_) {}
    }
  }
}
</script>

<style scoped>
.stat-card {
  margin-bottom: 16px;
}
.stat-row { display: flex; align-items: center; gap: 14px; }
.stat-icon {
  width: 48px; height: 48px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 22px;
  box-shadow: 0 8px 18px rgba(156, 188, 226, 0.28);
}
.stat-info { flex: 1; min-width: 0; }
.stat-num { font-size: 24px; font-weight: 700; color: #5c6b86; line-height: 1.1; }
.stat-label { font-size: 12px; color: #98a3b3; margin-top: 4px; }

.overview-row {
  display: flex;
  flex-wrap: wrap;
}
.overview-col {
  display: flex;
  margin-bottom: 16px;
}
.overview-card {
  width: 100%;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.quick-item {
  padding: 18px 8px;
  text-align: center;
  border: 1px solid rgba(217, 234, 247, 0.85);
  border-radius: 8px;
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
  background: rgba(255,255,255,0.72);
}
.quick-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 24px rgba(156, 188, 226, 0.18);
  border-color: rgba(120, 200, 255, 0.32);
}
.quick-item i { font-size: 28px; display: block; margin-bottom: 6px; }
.quick-item span { font-size: 13px; color: #6c7b94; }

@media (max-width: 767px) {
  .stat-num { font-size: 20px; }
  .stat-icon { width: 40px; height: 40px; font-size: 18px; }
  .quick-grid { grid-template-columns: repeat(3, 1fr); gap: 8px; }
  .quick-item { padding: 14px 4px; }
  .quick-item i { font-size: 24px; }
}
</style>
