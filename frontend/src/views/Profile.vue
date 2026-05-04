<template>
  <div class="page">
    <el-row :gutter="16">
      <!-- 左侧：头像卡片 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="6">
        <el-card shadow="hover" class="animate-item">
          <div class="avatar-card">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="onUploadSuccess"
              :on-error="onUploadError"
              accept="image/*"
              name="file"
            >
              <div class="avatar-wrap">
                <img v-if="avatarSrc" :src="avatarSrc" class="avatar-img" alt="avatar" />
                <i v-else class="el-icon-user-solid avatar-placeholder"></i>
                <div class="avatar-mask">
                  <i class="el-icon-camera-solid"></i>
                  <span>更换头像</span>
                </div>
                <el-progress
                  v-if="uploading"
                  type="circle"
                  :width="120"
                  :percentage="uploadPercent"
                  class="avatar-progress"
                />
              </div>
            </el-upload>
            <div class="nickname">{{ form.nickname || form.username }}</div>
            <div class="role-tag">
              <el-tag size="mini" :type="form.userType === 'student' ? 'primary' : (form.role === 0 ? 'danger' : 'primary')">
                {{ form.userType === 'student' ? '学生' : (form.role === 0 ? '超级管理员' : '普通宿管') }}
              </el-tag>
            </div>
            <div class="tip">支持 JPG / PNG，大小不超过 2MB</div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：基本信息 + 修改密码 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="18">
        <el-card shadow="hover" class="animate-item" style="animation-delay:80ms">
          <div slot="header"><i class="el-icon-user"></i> 基本信息</div>
          <el-form :model="form" label-width="80px" style="max-width:520px">
            <el-form-item label="账号">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingInfo" @click="saveInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="hover" class="animate-item" style="animation-delay:160ms;margin-top:16px">
          <div slot="header"><i class="el-icon-lock"></i> 修改密码</div>
          <el-form :model="pwd" :rules="pwdRules" ref="pwdForm" label-width="100px" style="max-width:520px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwd.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwd.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="pwd.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingPwd" @click="savePwd">更新密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { updateProfile, updatePassword, uploadAvatarUrl, getMe } from '../api'
export default {
  name: 'Profile',
  data() {
    const samePwd = (rule, value, cb) => {
      if (value !== this.pwd.newPassword) cb(new Error('两次密码不一致'))
      else cb()
    }
    return {
      uploadUrl: uploadAvatarUrl,
      form: {
        username: '', nickname: '', phone: '', role: 1, userType: 'admin', avatar: ''
      },
      pwd: { oldPassword: '', newPassword: '', confirmPassword: '' },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' },
                      { min: 6, message: '密码至少 6 位', trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '请再次输入密码', trigger: 'blur' },
                          { validator: samePwd, trigger: 'blur' }]
      },
      savingInfo: false,
      savingPwd: false,
      uploading: false,
      uploadPercent: 0
    }
  },
  computed: {
    uploadHeaders() { return { Authorization: this.$store.state.token || '' } },
    avatarSrc() {
      const a = this.form.avatar || (this.$store.state.user && this.$store.state.user.avatar) || ''
      if (!a) return ''
      return a.startsWith('http') ? a : '/api' + a
    }
  },
  mounted() { this.loadMe() },
  methods: {
    async loadMe() {
      const { data } = await getMe()
      if (data) {
        this.form = {
          id: data.id,
          username: data.username,
          nickname: data.nickname || '',
          phone: data.phone || '',
          role: data.role,
          userType: data.userType || this.$store.state.userType || 'admin',
          avatar: data.avatar || (this.$store.state.user && this.$store.state.user.avatar) || ''
        }
        this.$store.commit('SET_LOGIN', { token: this.$store.state.token, user: data })
      }
    },
    beforeUpload(file) {
      const isImg = /image\/(jpeg|png|jpg|gif|webp)/.test(file.type)
      const lt2M = file.size / 1024 / 1024 < 2
      if (!isImg) { this.$message.error('只能上传图片'); return false }
      if (!lt2M) { this.$message.error('图片大小不能超过 2MB'); return false }
      this.uploading = true; this.uploadPercent = 10
      return true
    },
    onUploadSuccess(res) {
      this.uploading = false
      if (res && res.code === 200) {
        this.form.avatar = res.data.url
        const user = { ...this.$store.state.user, avatar: res.data.url }
        this.$store.commit('SET_LOGIN', { token: this.$store.state.token, user })
        this.$message.success('头像上传成功')
      } else {
        this.$message.error((res && res.msg) || '上传失败')
      }
    },
    onUploadError() {
      this.uploading = false
      this.$message.error('上传失败')
    },
    async saveInfo() {
      this.savingInfo = true
      try {
        await updateProfile({ nickname: this.form.nickname, phone: this.form.phone })
        this.$message.success('保存成功')
        this.loadMe()
      } finally { this.savingInfo = false }
    },
    savePwd() {
      this.$refs.pwdForm.validate(async valid => {
        if (!valid) return
        this.savingPwd = true
        try {
          await updatePassword({ oldPassword: this.pwd.oldPassword, newPassword: this.pwd.newPassword })
          this.$message.success('密码已更新，请重新登录')
          this.$store.dispatch('logout')
          this.$router.replace('/login')
        } finally { this.savingPwd = false }
      })
    }
  }
}
</script>

<style scoped>
.avatar-card { text-align: center; padding: 16px 0 8px; }
.avatar-uploader /deep/ .el-upload { display: inline-block; }
.avatar-wrap {
  position: relative;
  width: 120px; height: 120px; border-radius: 50%;
  overflow: hidden; margin: 0 auto;
  background: linear-gradient(135deg, rgba(243, 166, 181, 0.12), rgba(132, 213, 255, 0.18));
  border: 2px solid rgba(234, 221, 230, 0.75);
  transition: box-shadow 0.2s;
}
.avatar-wrap:hover { box-shadow: 0 10px 24px rgba(126, 153, 189, 0.18); }
.avatar-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.avatar-placeholder {
  font-size: 72px; color: #b6bdd0;
  line-height: 120px; display: block;
}
.avatar-mask {
  position: absolute; inset: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  font-size: 12px;
}
.avatar-mask i { font-size: 24px; margin-bottom: 4px; }
.avatar-wrap:hover .avatar-mask { opacity: 1; }
.avatar-progress {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(255,255,255,0.8);
}
.nickname { font-size: 18px; font-weight: 600; margin-top: 12px; }
.role-tag { margin-top: 6px; }
.tip { font-size: 12px; color: #98a3b3; margin-top: 12px; }
</style>
