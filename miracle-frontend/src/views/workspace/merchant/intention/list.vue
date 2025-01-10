<template>
  <div class="intention-list">
    <!-- 搜索表单 -->
    <div class="search-wrapper">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="企业名称">
          <a-input
            v-model:value="queryParams.companyName"
            placeholder="请输入企业名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="intentionList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <!-- 产品图片列 -->
      <template #productImage="{ text }">
        <a-image
          :src="text || defaultImage"
          alt="产品图片"
          style="width: 80px; height: 80px; object-fit: cover;"
          :preview="{
            src: text || defaultImage,
            mask: false
          }"
        />
      </template>

      <!-- 产品名称列 -->
      <template #productName="{ text, record }">
        <a @click="handleViewProduct(record)">{{ text }}</a>
      </template>

      <!-- 操作列 -->
      <template #action="{ record }">
        <a-popconfirm
          title="确定要取消意向吗？"
          ok-text="确定"
          cancel-text="取消"
          @confirm="handleCancel(record)"
        >
          <a-button type="link" danger>取消意向</a-button>
        </a-popconfirm>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import defaultImage from '@/assets/images/default.jpg'
import { getMerchantIntentionList, cancelIntention } from '@/api/merchant'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  companyName: '',
  pageNum: 1,
  pageSize: 10
})

// 加载状态
const loading = ref(false)

// 表格列定义
const columns = [
  {
    title: '产品图片',
    dataIndex: 'productImage',
    width: '120px',
    slots: { 
      customRender: 'productImage' 
    }
  },
  {
    title: '产品名称',
    dataIndex: 'productName',
    width: '15%',
    slots: {
      customRender: 'productName'
    }
  },
  {
    title: '公司名称',
    dataIndex: 'companyName',
    width: '15%'
  },
  {
    title: '联系人',
    dataIndex: 'companyContact',
    width: '10%'
  },
  {
    title: '联系电话',
    dataIndex: 'companyPhone',
    width: '15%'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: '15%',
    customRender: ({ text }) => formatDateTime(text)
  },
  {
    title: '操作',
    width: '10%',
    slots: { 
      customRender: 'action' 
    }
  }
]

// 分页配置
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
  showSizeChanger: true,
  showQuickJumper: true
})

// 默认数据
const intentionList = ref([
  {
    id: 1,
    productImage: 'https://example.com/image.jpg',
    productName: '智能制造解决方案',
    companyName: '科技有限公司',
    companyContact: '张经理',
    companyPhone: '13800138000',
    createTime: '2024-03-15 10:00:00'
  }
])

// 获取列表数据
const fetchList = async () => {
  loading.value = true
  try {
    const { data } = await getMerchantIntentionList({
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      companyName: queryParams.companyName
    })
    intentionList.value = data.map(item => ({
      id: item.id,
      productId: item.productId,
      productImage: item.productLogo,
      productName: item.productName,
      companyId: item.companyId,
      companyName: item.companyName || '-',
      companyContact: item.contactName || '-',
      companyPhone: item.contactPhone || '-',
      createTime: item.createTime
    }))
    pagination.total = 1
  } catch (error) {
    console.error('获取意向列表失败:', error)
    message.error('获取意向列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  queryParams.pageNum = 1
  fetchList()
}

// 重置
const handleReset = () => {
  queryParams.companyName = ''
  queryParams.pageNum = 1
  fetchList()
}

// 表格变化
const handleTableChange = (pag) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  fetchList()
}

// 取消意向
const handleCancel = async (record) => {
  try {
    await cancelIntention({
      id: record.id,
      productId: record.productId,
      companyId: record.companyId
    })
    message.success('已取消意向')
    fetchList()
  } catch (error) {
    message.error('取消失败')
  }
}

// 格式化日期时间
const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 跳转到产品详情
const handleViewProduct = (record) => {
  router.push(`/product/${record.productId}`)
}

// 初始化加载数据
onMounted(() => {
  fetchList()
})
</script>

<style lang="less" scoped>
.intention-list {
  .search-wrapper {
    margin-bottom: 16px;
    padding: 24px;
    background: #fff;
  }

  :deep(.ant-table-wrapper) {
    background: #fff;
    padding: 24px;

    .ant-table-cell a {
      color: #1890ff;
      cursor: pointer;
      
      &:hover {
        color: #40a9ff;
      }
    }
  }
}
</style> 