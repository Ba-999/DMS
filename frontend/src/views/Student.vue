<template>
  <div class="page">
    <el-card shadow="never" class="animate-item">
      <div class="toolbar">
        <el-input v-model="q.keyword" placeholder="姓名 / 学号" clearable style="width:220px" @keyup.enter.native="reload" />
        <el-select v-model="q.status" placeholder="状态" clearable style="width:140px" @change="reload">
          <el-option label="未入住" :value="0" />
          <el-option label="已入住" :value="1" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="reload">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetQ">重置</el-button>
        <div class="spacer"></div>
        <el-button type="success" icon="el-icon-plus" @click="openDlg()">新增学生</el-button>
      </div>

      <el-table :data="rows" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" min-width="90" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="{ row }">{{ row.gender === 'F' ? '女' : '男' }}</template>
        </el-table-column>
        <el-table-column prop="college" label="学院" min-width="140" class-name="mobile-hide" label-class-name="mobile-hide" />
        <el-table-column prop="phone" label="电话" width="130" class-name="mobile-hide" label-class-name="mobile-hide" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已入住' : '未入住' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="openDlg(row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" class="theme-danger" @click="del(row)">删除</el-button>
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

    <el-dialog :visible.sync="dlg.show" :title="dlg.form.id ? '编辑学生' : '新增学生'" width="460px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="学号" required>
          <el-input v-model="dlg.form.studentNo" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="dlg.form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="dlg.form.gender">
            <el-radio label="M">男</el-radio>
            <el-radio label="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="dlg.form.college" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="dlg.form.phone" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dlg.show = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { studentPage, studentSave, studentUpdate, studentDelete } from '../api'
export default {
  name: 'Student',
  data() {
    return {
      q: { current: 1, size: 10, keyword: '', status: null },
      rows: [], total: 0,
      loading: false,
      dlg: { show: false, form: { gender: 'M' } }
    }
  },
  mounted() { this.load() },
  methods: {
    resetQ() { this.q.keyword = ''; this.q.status = null; this.reload() },
    reload() { this.q.current = 1; this.load() },
    async load() {
      this.loading = true
      try {
        const { data } = await studentPage(this.q)
        this.rows = data.records; this.total = data.total
      } finally { this.loading = false }
    },
    openDlg(row) { this.dlg.form = row ? { ...row } : { gender: 'M' }; this.dlg.show = true },
    async submit() {
      if (!this.dlg.form.studentNo || !this.dlg.form.name) { this.$message.warning('请填写必填项'); return }
      if (this.dlg.form.id) await studentUpdate(this.dlg.form)
      else await studentSave(this.dlg.form)
      this.$message.success('保存成功'); this.dlg.show = false; this.load()
    },
    async del(row) {
      try { await this.$confirm(`确认删除学生 ${row.name}?`, '提示', { type: 'warning' }) } catch (_) { return }
      await studentDelete(row.id); this.$message.success('已删除'); this.load()
    }
  }
}
</script>
