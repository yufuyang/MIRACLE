<template>
  <div class="activity-list">
    <div class="page-header">
      <h2>活动列表</h2>
      <a-button type="primary" @click="handleAdd">
        <template #icon>
          <plus-outlined />
        </template>
        新建活动
      </a-button>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="活动名称">
          <a-input
            v-model:value="searchForm.title"
            placeholder="请输入活动名称"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="活动状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择活动状态"
            style="width: 200px"
            allow-clear
          >
            <a-select-option :value="0">未开始</a-select-option>
            <a-select-option :value="1">进行中</a-select-option>
            <a-select-option :value="2">已结束</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="活动时间">
          <div class="time-picker-group">
            <a-input
              v-model:value="searchForm.startTime"
              placeholder="请选择开始时间"
              readonly
              @click="() => showTimePicker('start')"
              style="width: 200px"
            />
            <span style="margin: 0 8px">至</span>
            <a-input
              v-model:value="searchForm.endTime"
              placeholder="请选择结束时间"
              readonly
              @click="() => showTimePicker('end')"
              style="width: 200px"
            />
          </div>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">
            <template #icon>
              <search-outlined />
            </template>
            搜索
          </a-button>
          <a-button style="margin-left: 8px" @click="handleReset">
            <template #icon>
              <redo-outlined />
            </template>
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="activities"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="id"
      :scroll="{ x: 1300 }"
      class="data-table"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'coverImage'">
          <a-image
            :width="80"
            :height="80"
            :src="record.coverImage"
            :fallback="defaultImage"
            style="object-fit: cover; border-radius: 4px;"
          />
        </template>
        <template v-else-if="column.dataIndex === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'action'">
          <a-space size="middle">
            <a @click="handleView(record)">详情</a>
            <a @click="handleEdit(record)">编辑</a>
            <a-popconfirm
              title="确定要删除这个活动吗？"
              @confirm="handleDelete(record)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a class="danger">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 新建/编辑活动对话框 -->
    <activity-form-modal
      v-model:visible="modalVisible"
      :activity="currentActivity"
      @success="handleSuccess"
    />

    <!-- 时间选择弹窗 -->
    <a-modal
      v-model:visible="timePickerVisible"
      :title="timePickerTitle"
      :width="300"
      :footer="null"
      :mask-closable="false"
      class="time-picker-modal"
    >
      <div class="time-picker-content">
        <div class="time-picker-header">
          <div class="month-selector">
            <a-button type="link" @click="prevMonth">
              <template #icon><left-outlined /></template>
            </a-button>
            <span>{{ currentDate.format('YYYY年 MM月') }}</span>
            <a-button type="link" @click="nextMonth">
              <template #icon><right-outlined /></template>
            </a-button>
          </div>
        </div>
        <div class="calendar-header">
          <span>日</span>
          <span>一</span>
          <span>二</span>
          <span>三</span>
          <span>四</span>
          <span>五</span>
          <span>六</span>
        </div>
        <div class="calendar-body">
          <div
            v-for="(week, weekIndex) in calendar"
            :key="weekIndex"
            class="calendar-row"
          >
            <div
              v-for="(day, dayIndex) in week"
              :key="dayIndex"
              class="calendar-cell"
              :class="{
                'disabled': isDisabledDate(day),
                'selected': isSelectedDate(day),
                'today': isToday(day),
                'empty': !day
              }"
              @click="selectDate(day)"
            >
              {{ day ? day.date() : '' }}
            </div>
          </div>
        </div>
        <div class="time-selector">
          <a-time-picker
            v-model:value="selectedTime"
            format="HH:mm"
            :minute-step="1"
            placeholder="请选择时间"
            style="width: 100%"
          />
        </div>
        <div class="modal-footer">
          <a-button @click="closeTimePicker">取消</a-button>
          <a-button type="primary" @click="confirmTime">确定</a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, h, resolveComponent, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  SearchOutlined,
  RedoOutlined,
  PlusOutlined,
  LeftOutlined,
  RightOutlined
} from '@ant-design/icons-vue'
import { getActivityList, deleteActivity } from '@/api/activity'
import ActivityFormModal from './components/ActivityFormModal.vue'

const router = useRouter()
const defaultImage = 'https://via.placeholder.com/200x200'

// 搜索表单
const searchForm = ref({
  title: '',
  status: undefined,
  startTime: undefined,
  endTime: undefined
})

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: total => `共 ${total} 条`,
  showSizeChanger: true,
  showQuickJumper: true
})

// 表格列定义
const columns = [
  {
    title: '活动封面',
    dataIndex: 'coverImage',
    key: 'coverImage',
    width: 100
  },
  {
    title: '活动名称',
    dataIndex: 'title',
    key: 'title',
    width: 200,
    ellipsis: true
  },
  {
    title: '活动时间',
    key: 'time',
    width: 300,
    customRender: ({ record }) => {
      return `${formatDateTime(record.startTime)} ~ ${formatDateTime(record.endTime)}`
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    customRender: ({ record }) => {
      const now = new Date().getTime()
      const start = new Date(record.startTime?.replace('T', ' ')).getTime()
      const end = new Date(record.endTime?.replace('T', ' ')).getTime()
      
      let status = 0 // 默认未开始
      if (now >= start && now <= end) {
        status = 1 // 进行中
      } else if (now > end) {
        status = 2 // 已结束
      }
      
      return h(resolveComponent('a-tag'), {
        color: getStatusColor(status)
      }, () => getStatusText(status))
    }
  },
  {
    title: '浏览量',
    dataIndex: 'viewCount',
    key: 'viewCount',
    width: 100,
    align: 'right'
  },
  {
    title: '报名人数',
    dataIndex: 'registerCount',
    key: 'registerCount',
    width: 100,
    align: 'right'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    customRender: ({ record }) => {
      return formatDateTime(record.createTime)
    }
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
    customRender: ({ record }) => {
      return h('div', { class: 'action-buttons' }, [
        h('a', {
          onClick: () => handleView(record),
          style: { marginRight: '12px' }
        }, '详情'),
        h('a', {
          onClick: () => handleEdit(record),
          style: { marginRight: '12px' }
        }, '编辑'),
        h(resolveComponent('a-popconfirm'), {
          title: '确定要删除这个活动吗？',
          onConfirm: () => handleDelete(record),
          okText: '确定',
          cancelText: '取消'
        }, {
          default: () => h('a', {
            class: 'danger'
          }, '删除')
        })
      ])
    }
  }
]

// 数相关
const loading = ref(false)
const activities = ref([])
const modalVisible = ref(false)
const currentActivity = ref(null)

// 时间选择相关
const timePickerVisible = ref(false)
const timePickerType = ref('start') // 'start' 或 'end'
const currentDate = ref(dayjs())
const selectedDate = ref(null)
const selectedTime = ref(null)

const timePickerTitle = computed(() => {
  return timePickerType.value === 'start' ? '选择开始时间' : '选择结束时间'
})

// 生成日历数据
const calendar = computed(() => {
  const firstDay = currentDate.value.startOf('month')
  const lastDay = currentDate.value.endOf('month')
  const firstWeekday = firstDay.day()
  const days = []
  let week = Array(7).fill(null)
  
  // 填充第一周之前的空白
  for (let i = 0; i < firstWeekday; i++) {
    week[i] = null
  }
  
  // 填充日期
  for (let date = firstDay; date.isBefore(lastDay) || date.isSame(lastDay); date = date.add(1, 'day')) {
    week[date.day()] = date
    if (date.day() === 6) {
      days.push(week)
      week = Array(7).fill(null)
    }
  }
  
  // 填充��后一周
  if (week.some(d => d !== null)) {
    days.push(week)
  }
  
  return days
})

// 切换月份
const prevMonth = () => {
  currentDate.value = currentDate.value.subtract(1, 'month')
}

const nextMonth = () => {
  currentDate.value = currentDate.value.add(1, 'month')
}

// 日期判断函数
const isDisabledDate = (date) => {
  if (!date) return true
  return false
}

const isSelectedDate = (date) => {
  if (!date || !selectedDate.value) return false
  return date.format('YYYY-MM-DD') === selectedDate.value.format('YYYY-MM-DD')
}

const isToday = (date) => {
  if (!date) return false
  return date.format('YYYY-MM-DD') === dayjs().format('YYYY-MM-DD')
}

// 选择日期
const selectDate = (date) => {
  if (!date || isDisabledDate(date)) return
  selectedDate.value = date
}

// 显示时间选择器
const showTimePicker = (type) => {
  timePickerType.value = type
  timePickerVisible.value = true
  currentDate.value = dayjs()
  selectedDate.value = null
  selectedTime.value = null
}

// 关闭时间选择器
const closeTimePicker = () => {
  timePickerVisible.value = false
}

// 确认时间选择
const confirmTime = () => {
  if (!selectedDate.value || !selectedTime.value) {
    message.warning('请选择日期和时间')
    return
  }

  const dateStr = selectedDate.value.format('YYYY-MM-DD')
  const timeStr = dayjs(selectedTime.value).format('HH:mm:ss')
  const dateTime = `${dateStr}T${timeStr}` // 使用 ISO 格式

  if (timePickerType.value === 'start') {
    searchForm.value.startTime = dateTime
  } else {
    if (dateTime <= searchForm.value.startTime) {
      message.warning('结束时间必须大于开始时间')
      return
    }
    searchForm.value.endTime = dateTime
  }

  timePickerVisible.value = false
}

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.pageSize,
      title: searchForm.value.title,
      status: searchForm.value.status,
      startTime: searchForm.value.startTime,
      endTime: searchForm.value.endTime
    }
    console.log('请求参数:', params)
    const res = await getActivityList(params)
    console.log('返回数据:', res)
    if (res.code === 200) {
      activities.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
      pagination.value.total = res.total || res.data?.total || 0
      console.log('活动列表:', activities.value)
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    message.error('获取活动列表失败')
  } finally {
    loading.value = false
  }
}

// 表格变化处理
const handleTableChange = (pag) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchActivities()
}

// 搜索处理
const handleSearch = () => {
  pagination.value.current = 1
  fetchActivities()
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    title: '',
    status: undefined,
    startTime: undefined,
    endTime: undefined
  }
  pagination.value.current = 1
  fetchActivities()
}

// 新建活动
const handleAdd = () => {
  currentActivity.value = null
  modalVisible.value = true
}

// 编辑活动
const handleEdit = (record) => {
  console.log('编辑活动:', record)
  currentActivity.value = { ...record }
  modalVisible.value = true
}

// 查看活动
const handleView = (record) => {
  console.log('查看活动:', record)
  try {
    if (!record || !record.id) {
      message.error('无效的活动记录')
      return
    }
    router.push(`/workspace/activity/detail/${record.id}`)
  } catch (error) {
    console.error('查看活动失败:', error)
    message.error('查看活动失败')
  }
}

// 删除活动
const handleDelete = async (record) => {
  console.log('删除活动:', record)
  try {
    const res = await deleteActivity(record.id)
    console.log('删除返回:', res)
    if (res.code === 200) {
      message.success('删除成功')
      fetchActivities()
    }
  } catch (error) {
    console.error('删除活动失败:', error)
    message.error('删除失败')
  }
}

// 表单提交成功
const handleSuccess = () => {
  modalVisible.value = false
  fetchActivities()
}

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 0:
      return 'blue' // 未开始
    case 1:
      return 'green' // 进行中
    case 2:
      return 'gray' // 已结束
    default:
      return 'default'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0:
      return '未开始'
    case 1:
      return '进行中'
    case 2:
      return '已结束'
    default:
      return '未知'
  }
}

// 格式化时间
const formatDateTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  fetchActivities()
})
</script>

<style lang="less" scoped>
.activity-list {
  padding: 24px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.65);
    }
  }

  .search-form {
    margin-bottom: 16px;

    :deep(.ant-form-item) {
      margin-bottom: 16px;
    }
  }

  .data-table {
    :deep(.ant-table) {
      font-size: 14px;

      .ant-table-thead > tr > th {
        font-weight: 500;
        background: #fff;
        border-bottom: 1px solid #f0f0f0;
      }

      .ant-table-tbody > tr > td {
        color: rgba(0, 0, 0, 0.65);
      }
    }
  }

  :deep(.ant-form-item-label > label) {
    font-size: 14px;
    color: rgba(0, 0, 0, 0.65);
  }

  .danger {
    color: #ff4d4f;
    &:hover {
      color: #ff7875;
    }
  }

  :deep(a) {
    color: #1890ff;
    &:hover {
      color: #40a9ff;
    }
  }
}

.time-picker-modal {
  :deep(.ant-modal-body) {
    padding: 0;
  }
}

.time-picker-content {
  .time-picker-header {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;

    .month-selector {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;
    }
  }

  .calendar-header {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    text-align: center;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;
    font-size: 12px;
    color: rgba(0, 0, 0, 0.45);
  }

  .calendar-body {
    padding: 8px;

    .calendar-row {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 4px;
      margin-bottom: 4px;
    }

    .calendar-cell {
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      border-radius: 2px;
      font-size: 14px;

      &:hover:not(.disabled):not(.empty) {
        background-color: #f5f5f5;
      }

      &.disabled {
        color: rgba(0, 0, 0, 0.25);
        cursor: not-allowed;
      }

      &.selected {
        background-color: #1890ff;
        color: #fff;
      }

      &.today {
        color: #1890ff;
        font-weight: bold;
      }

      &.empty {
        cursor: default;
      }
    }
  }

  .time-selector {
    padding: 16px;
    border-top: 1px solid #f0f0f0;
  }

  .modal-footer {
    padding: 16px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}
</style> 