<template>
  <div class="page">
    <el-card shadow="never" class="animate-item">
      <div class="toolbar">
        <el-input v-model="q.keyword" placeholder="姓名 / 学号 / 房号" clearable style="width:260px" @keyup.enter.native="reload" />
        <el-button type="primary" icon="el-icon-search" @click="reload">查询</el-button>
        <el-button icon="el-icon-refresh" @click="() => { q.keyword = ''; reload() }">重置</el-button>
        <div class="spacer"></div>
        <el-button type="success" icon="el-icon-plus" @click="openDlg">办理入住</el-button>
      </div>

      <el-table :data="rows" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" min-width="90" />
        <el-table-column prop="buildingName" label="楼宇" min-width="140" class-name="mobile-hide" label-class-name="mobile-hide" />
        <el-table-column prop="roomNumber" label="房号" min-width="80" />
        <el-table-column prop="checkInDate" label="入住日期" width="110" />
        <el-table-column prop="createTime" label="登记时间" width="170" class-name="mobile-hide" label-class-name="mobile-hide" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="mini" type="text" class="theme-danger" icon="el-icon-back" @click="out(row)">退宿</el-button>
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

    <el-dialog :visible.sync="dlg.show" title="办理入住" width="460px" @open="loadOptions">
      <el-form :model="dlg.form" label-width="90px">
        <el-form-item label="学生" required>
          <el-select v-model="dlg.form.studentId" filterable placeholder="选择未入住学生" style="width:100%">
            <el-option v-for="s in students" :key="s.id" :label="`${s.studentNo} - ${s.name}`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍" required>
          <el-select v-model="dlg.form.dormitoryId" filterable placeholder="选择可用宿舍" style="width:100%">
            <el-option
              v-for="d in dorms" :key="d.id"
              :label="`${d.roomNumber} (${d.currentNumber}/${d.capacity})`"
              :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入住日期">
          <el-date-picker
            v-model="dlg.form.checkInDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="选择日期"
            style="width:100%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dlg.show = false">取消</el-button>
        <el-button type="primary" @click="submit">确认入住</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { checkinPage, checkinSave, checkinOut, studentUnchecked, dormitoryAvailable } from '../api'
export default {
  name: 'CheckIn',
  data() {
    return {
      q: { current: 1, size: 10, keyword: '' },
      rows: [], total: 0,
      loading: false,
      students: [], dorms: [],
      dlg: { show: false, form: {} }
    }
  },
  mounted() { this.load() },
  methods: {
    reload() { this.q.current = 1; this.load() },
    async load() {
      this.loading = true
      try {
        const { data } = await checkinPage(this.q)
        this.rows = data.records; this.total = data.total
      } finally { this.loading = false }
    },
    openDlg() {
      const today = new Date()
      const p = n => String(n).padStart(2, '0')
      this.dlg.form = { checkInDate: `${today.getFullYear()}-${p(today.getMonth() + 1)}-${p(today.getDate())}` }
      this.dlg.show = true
    },
    async loadOptions() {
      const [{ data: s }, { data: d }] = await Promise.all([studentUnchecked(), dormitoryAvailable()])
      this.students = s; this.dorms = d
    },
    async submit() {
      if (!this.dlg.form.studentId || !this.dlg.form.dormitoryId) {
        this.$message.warning('请选择学生和宿舍'); return
      }
      await checkinSave(this.dlg.form)
      this.$message.success('入住成功'); this.dlg.show = false; this.load()
    },
    async out(row) {
      try { await this.$confirm(`确认为 ${row.studentName} 办理退宿?`, '提示', { type: 'warning' }) } catch (_) { return }
      await checkinOut(row.id); this.$message.success('已退宿'); this.load()
    }
  }
}
</script>
