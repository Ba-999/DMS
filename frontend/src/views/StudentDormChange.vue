<template>
  <div class="page">
    <el-card shadow="never" class="animate-item">
      <div class="toolbar">
        <el-select v-model="q.status" placeholder="状态" clearable style="width:140px" @change="reload">
          <el-option label="待审核" :value="0" />
          <el-option label="已同意" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
        <div class="spacer"></div>
        <el-button type="success" icon="el-icon-plus" @click="openDlg">申请换宿</el-button>
      </div>
      <el-table :data="rows" border stripe v-loading="loading">
        <el-table-column prop="fromBuildingName" label="原宿舍楼" min-width="120" />
        <el-table-column prop="fromRoomNumber" label="原房号" min-width="90" />
        <el-table-column prop="toBuildingName" label="目标楼" min-width="120" />
        <el-table-column prop="toRoomNumber" label="目标房号" min-width="90" />
        <el-table-column prop="reason" label="申请原因" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ ['待审核','已同意','已拒绝'][row.status] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="handleRemark" label="审批备注" min-width="160" show-overflow-tooltip />
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

    <el-dialog :visible.sync="dlg.show" title="换宿申请" width="500px" @open="loadDorms">
      <el-form :model="dlg.form" label-width="100px">
        <el-form-item label="目标宿舍" required>
          <el-select v-model="dlg.form.toDormitoryId" filterable placeholder="请选择宿舍" style="width:100%">
            <el-option v-for="d in dorms" :key="d.id" :label="`${d.buildingName || ''} ${d.roomNumber}`" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请原因">
          <el-input v-model="dlg.form.reason" type="textarea" :rows="4" placeholder="请输入换宿原因" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dlg.show = false">取消</el-button>
        <el-button type="primary" @click="submit">提交申请</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { dormChangeMine, dormChangeSave, dormitoryStudentPage } from '../api'
export default {
  name: 'StudentDormChange',
  data() {
    return {
      q: { current: 1, size: 10, status: null },
      rows: [],
      total: 0,
      loading: false,
      dorms: [],
      dlg: { show: false, form: { toDormitoryId: null, reason: '' } }
    }
  },
  mounted() { this.load() },
  methods: {
    reload() { this.q.current = 1; this.load() },
    openDlg() { this.dlg.show = true; this.dlg.form = { toDormitoryId: null, reason: '' } },
    async load() {
      this.loading = true
      try {
        const { data } = await dormChangeMine(this.q)
        this.rows = data.records || []
        this.total = data.total || 0
      } finally {
        this.loading = false
      }
    },
    async loadDorms() {
      const { data } = await dormitoryStudentPage({ current: 1, size: 1000 })
      this.dorms = (data.records || []).filter(item => Number(item.currentNumber) < Number(item.capacity))
    },
    async submit() {
      if (!this.dlg.form.toDormitoryId) { this.$message.warning('请选择目标宿舍'); return }
      await dormChangeSave(this.dlg.form)
      this.$message.success('申请已提交')
      this.dlg.show = false
      this.load()
    }
  }
}
</script>
