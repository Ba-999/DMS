<template>
  <div class="page">
    <el-card shadow="never" class="animate-item">
      <div class="toolbar">
        <el-select v-model="q.status" placeholder="状态" clearable style="width:140px" @change="reload">
          <el-option label="待审核" :value="0" />
          <el-option label="已同意" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
        <el-input v-model="q.keyword" placeholder="学生姓名/学号/目标房号" clearable style="width:220px" @keyup.enter.native="reload" />
        <el-button type="primary" icon="el-icon-search" @click="reload">查询</el-button>
      </div>
      <el-table :data="rows" border stripe v-loading="loading">
        <el-table-column prop="studentNo" label="学号" min-width="120" />
        <el-table-column prop="studentName" label="姓名" min-width="100" />
        <el-table-column prop="fromBuildingName" label="原楼宇" min-width="120" />
        <el-table-column prop="fromRoomNumber" label="原房号" min-width="90" />
        <el-table-column prop="toBuildingName" label="目标楼宇" min-width="120" />
        <el-table-column prop="toRoomNumber" label="目标房号" min-width="90" />
        <el-table-column prop="reason" label="换宿原因" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ ['待审核','已同意','已拒绝'][row.status] || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="mini" type="text" @click="handle(row, 1)">同意</el-button>
            <el-button v-if="row.status === 0" size="mini" type="text" class="theme-danger" @click="handle(row, 2)">拒绝</el-button>
          </template>
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
  </div>
</template>

<script>
import { dormChangePage, dormChangeHandle } from '../api'
export default {
  name: 'DormChange',
  data() {
    return {
      q: { current: 1, size: 10, status: null, keyword: '' },
      rows: [],
      total: 0,
      loading: false
    }
  },
  mounted() { this.load() },
  methods: {
    reload() { this.q.current = 1; this.load() },
    async load() {
      this.loading = true
      try {
        const { data } = await dormChangePage(this.q)
        this.rows = data.records || []
        this.total = data.total || 0
      } finally {
        this.loading = false
      }
    },
    async handle(row, status) {
      const text = status === 1 ? '同意' : '拒绝'
      const { value } = await this.$prompt(`请输入${text}备注`, '审批换宿', { inputValue: '', confirmButtonText: '确定', cancelButtonText: '取消' }).catch(() => ({ value: null }))
      if (value === null) return
      await dormChangeHandle({ id: row.id, status, handleRemark: value })
      this.$message.success('处理成功')
      this.load()
    }
  }
}
</script>
