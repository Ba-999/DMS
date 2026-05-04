<template>
  <div class="page">
    <el-card shadow="never" class="animate-item">
      <div class="toolbar">
        <el-select v-model="q.status" placeholder="状态" clearable style="width:140px" @change="reload">
          <el-option label="待处理" :value="0" />
          <el-option label="维修中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <el-input v-model="q.keyword" placeholder="姓名 / 房号" clearable style="width:200px" @keyup.enter.native="reload" />
        <el-button type="primary" icon="el-icon-search" @click="reload">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetQ">重置</el-button>
        <div class="spacer"></div>
        <el-button type="success" icon="el-icon-plus" @click="openDlg">新增报修</el-button>
      </div>

      <el-table :data="rows" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="studentName" label="申请人" min-width="90" />
        <el-table-column prop="buildingName" label="楼宇" min-width="130" class-name="mobile-hide" label-class-name="mobile-hide" />
        <el-table-column prop="roomNumber" label="房号" width="80" />
        <el-table-column prop="content" label="报修内容" min-width="160" show-overflow-tooltip />
        <el-table-column prop="applyTime" label="申请时间" width="160" class-name="mobile-hide" label-class-name="mobile-hide" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="mini" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="mini" type="text" @click="changeStatus(row, 1)">开始维修</el-button>
            <el-button v-if="row.status === 1" size="mini" type="text" class="theme-success" @click="changeStatus(row, 2)">完成</el-button>
            <el-button size="mini" type="text" class="theme-danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        background style="margin-top:12px"
        :current-page.sync="q.current" :page-size.sync="q.size" :total="total"
        layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 50]"
        @current-change="load" @size-change="load"
      />
    </el-card>

    <el-dialog :visible.sync="dlg.show" title="新增报修" width="460px" @open="loadOptions">
      <el-form :model="dlg.form" label-width="90px">
        <el-form-item label="申请学生" required>
          <el-select v-model="dlg.form.studentId" filterable placeholder="请选择" style="width:100%">
            <el-option v-for="s in students" :key="s.id" :label="`${s.studentNo} - ${s.name}`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍" required>
          <el-select v-model="dlg.form.dormitoryId" filterable placeholder="请选择" style="width:100%">
            <el-option
              v-for="d in dorms" :key="d.id"
              :label="`${d.buildingName || ''} ${d.roomNumber}`" :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报修内容" required>
          <el-input v-model="dlg.form.content" type="textarea" :rows="4" placeholder="请描述问题" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dlg.show = false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { repairPage, repairSave, repairStatus, repairDelete, studentPage, dormitoryPage } from '../api'
export default {
  name: 'Repair',
  data() {
    return {
      q: { current: 1, size: 10, status: null, keyword: '' },
      rows: [], total: 0,
      loading: false,
      students: [], dorms: [],
      dlg: { show: false, form: {} }
    }
  },
  mounted() { this.load() },
  methods: {
    statusText(s) { return ['待处理', '维修中', '已完成'][s] || '-' },
    statusType(s) { return ['warning', 'primary', 'success'][s] || 'info' },
    resetQ() { this.q.status = null; this.q.keyword = ''; this.reload() },
    reload() { this.q.current = 1; this.load() },
    async load() {
      this.loading = true
      try {
        const { data } = await repairPage(this.q)
        this.rows = data.records; this.total = data.total
      } finally { this.loading = false }
    },
    openDlg() { this.dlg.form = {}; this.dlg.show = true },
    async loadOptions() {
      const [{ data: s }, { data: d }] = await Promise.all([
        studentPage({ current: 1, size: 1000 }),
        dormitoryPage({ current: 1, size: 1000 })
      ])
      this.students = s.records; this.dorms = d.records
    },
    async submit() {
      if (!this.dlg.form.studentId || !this.dlg.form.dormitoryId || !this.dlg.form.content) {
        this.$message.warning('请填写完整'); return
      }
      await repairSave(this.dlg.form); this.$message.success('已提交'); this.dlg.show = false; this.load()
    },
    async changeStatus(row, status) {
      await repairStatus(row.id, status); this.$message.success('已更新'); this.load()
    },
    async del(row) {
      try { await this.$confirm('确认删除该报修?', '提示', { type: 'warning' }) } catch (_) { return }
      await repairDelete(row.id); this.$message.success('已删除'); this.load()
    }
  }
}
</script>
