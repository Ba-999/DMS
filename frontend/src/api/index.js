import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'
import store from '../store'

const request = axios.create({ baseURL: '/api', timeout: 15000 })

request.interceptors.request.use(config => {
  if (store.state.token) config.headers['Authorization'] = store.state.token
  return config
})

request.interceptors.response.use(
  resp => {
    const data = resp.data
    if (data.code === 200) return data
    Message.error(data.msg || '请求失败')
    return Promise.reject(new Error(data.msg))
  },
  err => {
    if (err.response && err.response.status === 401) {
      const hasLoggedInThisSession = sessionStorage.getItem('dms_has_logged_in') === '1'
      if (hasLoggedInThisSession) Message.error('登录已过期，请重新登录')
      store.dispatch('logout')
      if (router.currentRoute.path !== '/login') router.replace('/login')
    } else {
      Message.error((err.response && err.response.data && err.response.data.msg) || err.message || '网络错误')
    }
    return Promise.reject(err)
  }
)

/* 认证 */
export const login = (data) => request.post('/auth/login', data)
export const logout = () => request.post('/auth/logout')
export const getMe = () => request.get('/auth/me')

/* 当前用户：更新资料 / 修改密码 */
export const updateProfile = (data) => request.put('/user/profile', data)
export const updatePassword = (data) => request.put('/user/password', data)
/* 头像上传：返回 { url } */
export const uploadAvatarUrl = '/api/user/avatar'

/* 首页统计 */
export const statsOverview = () => request.get('/stats/overview')

/* 楼宇 */
export const buildingPage = (params) => request.get('/building/page', { params })
export const buildingList = () => request.get('/building/list')
export const buildingSave = (data) => request.post('/building', data)
export const buildingUpdate = (data) => request.put('/building', data)
export const buildingDelete = (id) => request.delete(`/building/${id}`)

/* 宿舍 */
export const dormitoryPage = (params) => request.get('/dormitory/page', { params })
export const dormitoryStudentPage = (params) => request.get('/dormitory/student/page', { params })
export const dormitoryAvailable = () => request.get('/dormitory/available')
export const dormitorySave = (data) => request.post('/dormitory', data)
export const dormitoryUpdate = (data) => request.put('/dormitory', data)
export const dormitoryDelete = (id) => request.delete(`/dormitory/${id}`)

/* 学生 */
export const studentPage = (params) => request.get('/student/page', { params })
export const studentUnchecked = () => request.get('/student/unchecked')
export const studentMyCheckin = () => request.get('/student/me/checkin')
export const studentSave = (data) => request.post('/student', data)
export const studentUpdate = (data) => request.put('/student', data)
export const studentDelete = (id) => request.delete(`/student/${id}`)

/* 入住 */
export const checkinPage = (params) => request.get('/checkin/page', { params })
export const checkinSave = (data) => request.post('/checkin', data)
export const checkinOut = (id) => request.delete(`/checkin/${id}`)

/* 报修 */
export const repairPage = (params) => request.get('/repair/page', { params })
export const repairMine = (params) => request.get('/repair/mine', { params })
export const repairSave = (data) => request.post('/repair', data)
export const repairStudentSave = (data) => request.post('/repair/student', data)
export const repairStatus = (id, status) => request.put('/repair/status', null, { params: { id, status } })
export const repairDelete = (id) => request.delete(`/repair/${id}`)

/* 换宿申请 */
export const dormChangePage = (params) => request.get('/dorm-change/page', { params })
export const dormChangeMine = (params) => request.get('/dorm-change/mine', { params })
export const dormChangeSave = (data) => request.post('/dorm-change', data)
export const dormChangeHandle = (data) => request.put('/dorm-change/handle', data)
