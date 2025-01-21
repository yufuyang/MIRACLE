<template>
  <a-card title="合作列表" :loading="loading">
    <template #extra>
      <a @click="handleViewMore">查看更多</a>
    </template>
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="false"
      size="small"
    >
      <!-- 商户名称 -->
      <template #merchantName="{ record }">
        <a @click="handleViewMerchant(record)">{{ record.merchantName }}</a>
      </template>

      <!-- 商户地址 -->
      <template #address="{ record }">
        <span>{{ record.province }} {{ record.city }} {{ record.address }}</span>
      </template>
      
      <!-- 营业执照号 -->
      <template #licenseNo="{ record }">
        <span>{{ record.licenseNo || '-' }}</span>
      </template>

      <!-- 状态 -->
      <template #status="{ record }">
        <a-tag :color="getStatusColor(record.status)">
          {{ getStatusText(record.status) }}
        </a-tag>
      </template>
    </a-table>
  </a-card>
</template>

<script setup>
const columns = [
  {
    title: '商户名称',
    dataIndex: 'merchantName',
    key: 'merchantName',
    slots: { customRender: 'merchantName' }
  },
  {
    title: '商户地址',
    dataIndex: 'address',
    key: 'address',
    slots: { customRender: 'address' },
    ellipsis: true,  // 超出部分显示省略号
    width: 200
  },
  {
    title: '营业执照号',
    dataIndex: 'licenseNo',
    key: 'licenseNo',
    slots: { customRender: 'licenseNo' },
    width: 160
  },
  {
    title: '合作状态',
    dataIndex: 'status',
    key: 'status',
    slots: { customRender: 'status' },
    width: 100
  }
]

// ... 其他代码保持不变 ...
</script>

<style scoped lang="less">
.cooperation-list {
  :deep(.ant-table-small) {
    .ant-table-thead > tr > th {
      background-color: #fafafa;
    }
  }
}
</style> 