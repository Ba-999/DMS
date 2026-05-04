<template>
  <div class="page">
    <el-card shadow="never" class="animate-item">
      <div class="toolbar">
        <el-select v-model="q.status" placeholder="状态" clearable style="width:140px" @change="reload">
          <el-option label="待处理" :value="0" />
          <el-option label="维修中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <div class="spacer"></div>
        <el-button type="success" icon="el-icon-plus" @click="dlg.show = true">提交报修</el-button>
      </div>
      <el-table :data="rows" border stripe v-loading="loading">
        <el-table-column prop="content" label="报修内容" min-width="220" />
        <el-table-column prop="applyTime" label="申请时间" min-width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ ['待处理','维修中','已完成'][row.status] || '-' }}</template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        style="margin-top:12px"
        :current-page.sync="q.current"
        :page-size.sync="q.size"
        :total="total"
        layout="total, sizes, prev, pager, next"
        :page-sizes="[10,20,50]"
        @current-change="load"
        @size-change="load"
      />
    </el-card>

    <el-dialog :visible.sync="dlg.show" title="提交报修" width="480px">
      <el-form :model="dlg.form" label-width="90px">
        <el-form-item label="报修内容" required>
          <el-input v-model="dlg.form.content" type="textarea" :rows="4" placeholder="请描述宿舍维修问题" />
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
import { repairMine, repairStudentSave } from '../api'
export default {
  name: 'StudentRepair',
  data() {
    return {
      q: { current: 1, size: 10, status: null },
      rows: [],
      total: 0,
      loading: false,
      dlg: { show: false, form: { content: '' } }
    }
  },
  mounted() { this.load() },
  methods: {
    reload() { this.q.current = 1; this.load() },
    async load() {
      this.loading = true
      try {
        const { data } = await repairMine(this.q)
        this.rows = data.records || []
        this.total = data.total || 0
      } finally {
        this.loading = false
      }
    },
    async submit() {
      if (!this.dlg.form.content) { this.$message.warning('请填写报修内容'); return }
      await repairStudentSave(this.dlg.form)
      this.$message.success('提交成功')
      this.dlg.show = false
      this.dlg.form = { content: '' }
      this.load()
    }
  }
}
</script>
