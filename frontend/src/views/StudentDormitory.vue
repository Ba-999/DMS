<template>
  <div class="page">
    <el-row :gutter="16" class="dorm-row">
      <el-col :xs="24" :lg="8" class="dorm-col">
        <el-card shadow="never" class="animate-item dorm-card dorm-summary-card">
          <div slot="header">我的宿舍</div>
          <div v-if="myCheckin" class="summary-content">
            <p><strong>楼宇：</strong>{{ myCheckin.buildingName || '-' }}</p>
            <p><strong>房号：</strong>{{ myCheckin.roomNumber || '-' }}</p>
            <p><strong>入住日期：</strong>{{ myCheckin.checkInDate || '-' }}</p>
          </div>
          <el-empty v-else description="当前暂无入住信息" :image-size="80" class="summary-empty" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="16" class="dorm-col">
        <el-card shadow="never" class="animate-item dorm-card">
          <div class="toolbar">
            <el-input v-model="q.roomNumber" placeholder="房号" clearable style="width:180px" @keyup.enter.native="reload" />
            <el-button type="primary" icon="el-icon-search" @click="reload">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetQ">重置</el-button>
          </div>
          <el-table :data="rows" border stripe v-loading="loading">
            <el-table-column prop="buildingName" label="楼宇" min-width="140" />
            <el-table-column prop="roomNumber" label="房号" min-width="100" />
            <el-table-column label="入住情况" min-width="150">
              <template #default="{ row }">{{ row.currentNumber }}/{{ row.capacity }}</template>
            </el-table-column>
            <el-table-column prop="price" label="住宿费/年" min-width="120" />
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
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { dormitoryStudentPage, studentMyCheckin } from '../api'
export default {
  name: 'StudentDormitory',
  data() {
    return {
      q: { current: 1, size: 10, roomNumber: '' },
      rows: [],
      total: 0,
      loading: false,
      myCheckin: null
    }
  },
  mounted() {
    this.load()
    this.loadMyCheckin()
  },
  methods: {
    resetQ() { this.q.roomNumber = ''; this.reload() },
    reload() { this.q.current = 1; this.load() },
    async load() {
      this.loading = true
      try {
        const { data } = await dormitoryStudentPage(this.q)
        this.rows = data.records || []
        this.total = data.total || 0
      } finally {
        this.loading = false
      }
    },
    async loadMyCheckin() {
      const { data } = await studentMyCheckin()
      this.myCheckin = data
    }
  }
}
</script>

<style scoped>
.dorm-row {
  display: flex;
  flex-wrap: wrap;
}
.dorm-col {
  display: flex;
  margin-bottom: 16px;
}
.dorm-card {
  width: 100%;
}
.dorm-summary-card {
  min-height: 100%;
}
.summary-content {
  min-height: 188px;
}
.summary-content p {
  margin: 0 0 14px;
  line-height: 1.8;
}
.summary-empty {
  min-height: 188px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
@media (max-width: 1199px) {
  .summary-content,
  .summary-empty {
    min-height: auto;
  }
}
</style>
