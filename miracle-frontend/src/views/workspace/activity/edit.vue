<template>
  <div class="activity-edit">
    <div class="page-header">
      <a-page-header
        title="编辑活动"
        @back="() => router.back()"
      />
    </div>
    
    <activity-form-modal
      :visible="true"
      :activity="activity"
      @success="handleSuccess"
      @cancel="handleCancel"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import ActivityFormModal from './components/ActivityFormModal.vue'
import { getActivityDetail } from '@/api/activity'

const router = useRouter()
const route = useRoute()
const activity = ref(null)

// 获取活动详情
const loadActivityDetail = async () => {
  try {
    const res = await getActivityDetail(route.params.id)
    activity.value = res.data
  } catch (error) {
    console.error('获取活动详情失败:', error)
    message.error('获取活动详情失败')
  }
}

// 编辑成功
const handleSuccess = () => {
  message.success('编辑成功')
  router.back()
}

// 取消编辑
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  loadActivityDetail()
})
</script>

<style scoped lang="less">
.activity-edit {
  padding: 24px;
  background: #f0f2f5;

  .page-header {
    margin-bottom: 24px;
    background: #fff;
  }
}
</style> 