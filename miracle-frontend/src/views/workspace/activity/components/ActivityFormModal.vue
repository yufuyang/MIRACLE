<template>
  <a-modal
    :title="activity ? '编辑活动' : '新建活动'"
    :visible="visible"
    :confirm-loading="loading"
    :width="800"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 18 }"
    >
      <!-- 活动名称 -->
      <a-form-item label="活动名称" name="title">
        <a-input
          v-model:value="formData.title"
          placeholder="请输入活动名称"
          :maxLength="50"
          show-count
        />
      </a-form-item>

      <!-- 活动封面 -->
      <a-form-item label="活动封面" name="coverImage">
        <a-upload
          v-model:file-list="fileList"
          list-type="picture-card"
          :show-upload-list="false"
          :before-upload="beforeUpload"
          :customRequest="handleUpload"
        >
          <img
            v-if="formData.coverImage"
            :src="formData.coverImage"
            alt="活动封面"
            style="width: 100%"
          />
          <div v-else>
            <plus-outlined />
            <div style="margin-top: 8px">上传封面</div>
          </div>
        </a-upload>
        <div class="upload-tip">建议尺寸：800x400px，支持jpg、png格式</div>
      </a-form-item>

      <!-- 活动时间 -->
      <a-form-item label="活动时间">
        <div class="time-range-wrapper">
          <a-input
            v-model:value="formData.startTime"
            placeholder="请选择开始时间"
            readonly
            @click="() => showTimePicker('start')"
          />
          <span class="time-separator">~</span>
          <a-input
            v-model:value="formData.endTime"
            placeholder="请选择结束时间"
            readonly
            @click="() => showTimePicker('end')"
          />
        </div>
      </a-form-item>

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

      <!-- 活动描述 -->
      <a-form-item label="活动描述" name="description">
        <a-textarea
          v-model:value="formData.description"
          :rows="4"
          placeholder="请输入活动描述"
          :maxLength="500"
          show-count
          allow-clear
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, LeftOutlined, RightOutlined } from '@ant-design/icons-vue'
import { uploadImage } from '@/api/upload'
import { createActivity, updateActivity } from '@/api/activity'
import dayjs from 'dayjs'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  activity: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'success'])

// 表单相关
const formRef = ref(null)
const loading = ref(false)
const fileList = ref([])

// 表单数据
const formData = ref({
  title: '', // 活动标题
  description: '', // 活动描述
  coverImage: '', // 封面图片
  startTime: null, // 开始时间
  endTime: null // 结束时间
})

// 表单校验规则
const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { max: 50, message: '活动标题最多50个字符', trigger: 'blur' }
  ],
  coverImage: [
    { required: true, message: '请上传活动封面', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    {
      validator: (_, value) => {
        if (value && formData.value.startTime) {
          if (value <= formData.value.startTime) {
            return Promise.reject('结束时间必须大于开始时间')
          }
        }
        return Promise.resolve()
      },
      trigger: 'change'
    }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' },
    { max: 500, message: '活动描述最多500个字符', trigger: 'blur' }
  ]
}

// 监听活动数据变化
watch(
  () => props.activity,
  (activity) => {
    if (activity) {
      formData.value = {
        title: activity.title,
        description: activity.description,
        coverImage: activity.coverImage,
        startTime: activity.startTime ? dayjs(activity.startTime).format('YYYY-MM-DDTHH:mm:ss') : null,
        endTime: activity.endTime ? dayjs(activity.endTime).format('YYYY-MM-DDTHH:mm:ss') : null
      }
      // 如果有封面图片，设置 fileList
      if (activity.coverImage) {
        fileList.value = [
          {
            uid: '-1',
            name: 'cover',
            status: 'done',
            url: activity.coverImage
          }
        ]
      } else {
        fileList.value = []
      }
    } else {
      formData.value = {
        title: '',
        description: '',
        coverImage: '',
        startTime: null,
        endTime: null
      }
      fileList.value = []
    }
  },
  { immediate: true }
)

// 禁用过去的日期
const disabledDate = (current) => {
  return current && current < Date.now() - 8.64e7 // 禁用今天之前的日期
}

// 禁用早于开始时间的日期
const disabledStartDate = (current) => {
  return current && (current < Date.now() - 8.64e7 || (formData.value.startTime && current <= formData.value.startTime))
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isImage) {
    message.error('只能上传JPG/PNG格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片必须小于2MB!')
    return false
  }
  return true
}

// 处理图片上传
const handleUpload = async ({ file, onSuccess, onError }) => {
  try {
    const form = new FormData()
    form.append('file', file)
    const res = await uploadImage(form)
    if (res.code === 200) {
      formData.value.coverImage = res.data
      fileList.value = [
        {
          uid: '-1',
          name: file.name,
          status: 'done',
          url: res.data
        }
      ]
      onSuccess()
    } else {
      onError()
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    message.error('上传图片失败')
    onError()
  }
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate().then(async () => {
    loading.value = true
    try {
      const data = {
        id: props.activity?.id,
        title: formData.value.title,
        description: formData.value.description,
        coverImage: formData.value.coverImage,
        startTime: formData.value.startTime,
        endTime: formData.value.endTime
      }

      const api = props.activity ? updateActivity : createActivity
      const res = await api(data)
      if (res.code === 200) {
        message.success(props.activity ? '编辑成功' : '创建成功')
        emit('success')
        handleCancel()
      }
    } catch (error) {
      console.error(props.activity ? '编辑活动失败:' : '创建活动失败:', error)
      message.error(props.activity ? '编辑失败' : '创建失败')
    } finally {
      loading.value = false
    }
  })
}

// 取消
const handleCancel = () => {
  formRef.value?.resetFields()
  emit('update:visible', false)
}

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
  
  // 填充最后一周
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
  if (timePickerType.value === 'start') {
    return date.isBefore(dayjs(), 'day')
  } else {
    const startTime = dayjs(formData.value.startTime)
    return date.isBefore(startTime, 'day')
  }
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
    formData.value.startTime = dateTime
  } else {
    if (dateTime <= formData.value.startTime) {
      message.warning('结束时间必须大于开始时间')
      return
    }
    formData.value.endTime = dateTime
  }

  timePickerVisible.value = false
}
</script>

<style lang="less" scoped>
.upload-tip {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
  margin-top: 4px;
}

:deep(.ant-upload-select-picture-card) {
  width: 200px;
  height: 100px;
  margin: 0;
}

.time-range-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;

  .time-input {
    flex: 1;
  }

  .time-separator {
    color: rgba(0, 0, 0, 0.45);
  }
}

.calendar-wrapper {
  width: 300px;

  :deep(.ant-picker-calendar-header) {
    padding: 8px;
  }

  .calendar-header {
    text-align: center;
    font-weight: 500;
    padding: 8px 0;
  }

  .time-picker {
    padding: 8px;
    border-top: 1px solid #f0f0f0;
  }

  .calendar-footer {
    padding: 8px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}

.calendar-title {
  font-size: 14px;
  font-weight: 500;
}

:deep(.calendar-popover) {
  padding: 0;
  
  .ant-popover-inner {
    padding: 0;
  }

  .ant-popover-title {
    padding: 8px;
    border-bottom: 1px solid #f0f0f0;
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