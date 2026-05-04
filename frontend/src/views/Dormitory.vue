<template>
  <div class="page">
    <el-card shadow="never" class="animate-item">
      <div class="toolbar">
        <el-select v-model="q.buildingId" placeholder="所属楼宇" clearable style="width:180px" @change="reload">
          <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
        <el-input v-model="q.roomNumber" placeholder="房号" clearable style="width:160px" @keyup.enter.native="reload" />
        <el-button type="primary" icon="el-icon-search" @click="reload">查询</el-button>
        <el-button icon="el-icon-refresh" @click="resetQ">重置</el-button>
        <div class="spacer"></div>
        <el-button type="success" icon="el-icon-plus" @click="openDlg()">新增宿舍</el-button>
      </div>

      <el-table :data="rows" v-loading="loading" border stripe style="width:100%">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="buildingName" label="楼宇" min-width="140" />
        <el-table-column prop="roomNumber" label="房号" min-width="100" />
        <el-table-column label="入住情况" min-width="160">
          <template #default="{ row }">
            <div class="flex-row">
              <el-progress
                :class="row.currentNumber >= row.capacity ? 'theme-progress-full' : 'theme-progress-normal'"
                :percentage="Math.round((row.currentNumber / row.capacity) * 100)"
                :stroke-width="10"
                style="flex:1;margin-right:8px"
              />
              <span>{{ row.currentNumber }}/{{ row.capacity }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="住宿费/年" width="120" class-name="mobile-hide" label-class-name="mobile-hide">
          <template #default="{ row }">¥{{ row.price || 0 }}</template>
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
        :current-page.sync="q.current"
        :page-size.sync="q.size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50]"
        @current-change="load" @size-change="load"
      />
    </el-card>

    <el-dialog :visible.sync="dlg.show" :title="dlg.form.id ? '编辑宿舍' : '新增宿舍'" width="460px">
      <el-form :model="dlg.form" label-width="90px">
        <el-form-item label="所属楼宇" required>
          <el-select v-model="dlg.form.buildingId" placeholder="请选择" style="width:100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房号" required>
          <el-input v-model="dlg.form.roomNumber" placeholder="如 302" />
        </el-form-item>
        <el-form-item label="满员人数">
          <el-input-number v-model="dlg.form.capacity" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="住宿费/年">
          <el-input-number v-model="dlg.form.price" :min="0" :precision="2" />
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
import { dormitoryPage, dormitorySave, dormitoryUpdate, dormitoryDelete, buildingList } from '../api'
export default {
  name: 'Dormitory',
  data() {
    return {
      q: { current: 1, size: 10, buildingId: null, roomNumber: '' },
      rows: [], total: 0,
      loading: false,
      buildings: [],
      dlg: { show: false, form: { capacity: 4 } }
    }
  },
  async mounted() {
    const { data } = await buildingList()
    this.buildings = data
    this.load()
  },
  methods: {
    resetQ() { this.q.buildingId = null; this.q.roomNumber = ''; this.reload() },
    reload() { this.q.current = 1; this.load() },
    async load() {
      this.loading = true
      try {
        const { data } = await dormitoryPage(this.q)
        this.rows = data.records; this.total = data.total
      } finally { this.loading = false }
    },
    openDlg(row) {
      this.dlg.form = row ? { ...row } : { capacity: 4, currentNumber: 0, price: 0 }
      this.dlg.show = true
    },
    async submit() {
      if (!this.dlg.form.buildingId || !this.dlg.form.roomNumber) { this.$message.warning('请填写必填项'); return }
      if (this.dlg.form.id) await dormitoryUpdate(this.dlg.form)
      else await dormitorySave(this.dlg.form)
      this.$message.success('保存成功'); this.dlg.show = false; this.load()
    },
    async del(row) {
      try { await this.$confirm(`确认删除宿舍 ${row.roomNumber}?`, '提示', { type: 'warning' }) } catch (_) { return }
      await dormitoryDelete(row.id); this.$message.success('已删除'); this.load()
    }
  }
}
</script>
