import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '首页概览', icon: 'el-icon-data-line', roles: ['admin'] } },
      { path: 'building',  name: 'building',  component: () => import('../views/Building.vue'),  meta: { title: '楼宇管理', icon: 'el-icon-office-building', roles: ['admin'] } },
      { path: 'dormitory', name: 'dormitory', component: () => import('../views/Dormitory.vue'), meta: { title: '宿舍管理', icon: 'el-icon-house', roles: ['admin'] } },
      { path: 'student',   name: 'student',   component: () => import('../views/Student.vue'),   meta: { title: '学生管理', icon: 'el-icon-user', roles: ['admin'] } },
      { path: 'checkin',   name: 'checkin',   component: () => import('../views/CheckIn.vue'),   meta: { title: '入住管理', icon: 'el-icon-key', roles: ['admin'] } },
      { path: 'repair',    name: 'repair',    component: () => import('../views/Repair.vue'),    meta: { title: '报修管理', icon: 'el-icon-s-tools', roles: ['admin'] } },
      { path: 'student-dormitory', name: 'studentDormitory', component: () => import('../views/StudentDormitory.vue'), meta: { title: '宿舍情况', icon: 'el-icon-house', roles: ['student'] } },
      { path: 'student-repair', name: 'studentRepair', component: () => import('../views/StudentRepair.vue'), meta: { title: '维修申请', icon: 'el-icon-s-tools', roles: ['student'] } },
      { path: 'student-dorm-change', name: 'studentDormChange', component: () => import('../views/StudentDormChange.vue'), meta: { title: '换宿申请', icon: 'el-icon-refresh', roles: ['student'] } },
      { path: 'dorm-change', name: 'dormChange', component: () => import('../views/DormChange.vue'), meta: { title: '换宿审批', icon: 'el-icon-finished', roles: ['admin'] } },
      { path: 'profile',   name: 'profile',   component: () => import('../views/Profile.vue'),   meta: { title: '个人中心', icon: 'el-icon-user-solid', roles: ['admin', 'student'] } }
    ]
  },
  { path: '*', redirect: '/dashboard' }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path !== '/login' && !store.state.token) next('/login')
  else if (to.path !== '/login' && to.meta && to.meta.roles && store.state.userType && !to.meta.roles.includes(store.state.userType)) {
    next(store.state.userType === 'student' ? '/student-dormitory' : '/dashboard')
  } else next()
})

export default router
