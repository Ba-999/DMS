<template>
  <div class="page">
    <el-card shadow="never" class="animate-item">
      <div class="toolbar">
        <el-input v-model="q.name" placeholder="楼宇名称" clearable style="width:220px" @keyup.enter.native="reload">
          <i slot="prefix" class="el-icon-search"></i>
        </el-input>
        <el-button type="primary" icon="el-icon-search" @click="reload">查询</el-button>
        <el-button icon="el-icon-refresh" @click="() => { q.name = ''; reload() }">重置</el-button>
        <div class="spacer"></div>
        <el-button type="success" icon="el-icon-plus" @click="openDlg()">新增楼宇</el-button>
      </div>

      <el-table :data="rows" v-loading="loading" border stripe style="width:100%">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="name" label="楼宇名称" min-width="140" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="mini" :type="row.type === '女生宿舍' ? 'danger' : ''">{{ row.type || '--' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" class-name="mobile-hide" label-class-name="mobile-hide" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="openDlg(row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" class="theme-danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        background
        style="margin-top:12px"
        :current-page.sync="q.current"
        :page-size.sync="q.size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50]"
        @current-change="load"
        @size-change="load"
      />
    </el-card>

    <el-dialog :visible.sync="dlg.show" :title="dlg.form.id ? '编辑楼宇' : '新增楼宇'" width="460px">
      <el-form :model="dlg.form" label-width="80px">
        <el-form-item label="楼宇名称" required>
          <el-input v-model="dlg.form.name" placeholder="如 男生1号楼" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="dlg.form.type" placeholder="请选择" style="width:100%">
            <el-option label="男生宿舍" value="男生宿舍" />
            <el-option label="女生宿舍" value="女生宿舍" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dlg.form.remark" type="textarea" :rows="3" />
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
import { buildingPage, buildingSave, buildingUpdate, buildingDelete } from '../api'
export default {
  name: 'Building',
  data() {
    return {
      q: { current: 1, size: 10, name: '' },
      rows: [], total: 0,
      loading: false,
      dlg: { show: false, form: {} }
    }
  },
  mounted() { this.load() },
  methods: {
    reload() { this.q.current = 1; this.load() },
    async load() {
      this.loading = true
      try {
        const { data } = await buildingPage(this.q)
        this.rows = data.records; this.total = data.total
      } finally { this.loading = false }
    },
    openDlg(row) { this.dlg.form = row ? { ...row } : {}; this.dlg.show = true },
    async submit() {
      if (!this.dlg.form.name) { this.$message.warning('请填写楼宇名称'); return }
      if (this.dlg.form.id) await buildingUpdate(this.dlg.form)
      else await buildingSave(this.dlg.form)
      this.$message.success('保存成功')
      this.dlg.show = false; this.load()
    },
    async del(row) {
      try { await this.$confirm(`确认删除 "${row.name}"?`, '提示', { type: 'warning' }) } catch (_) { return }
      await buildingDelete(row.id); this.$message.success('已删除'); this.load()
    }
  }
}
</script>
