<template>
  <view class="company-list">
    <!-- 搜索框 -->
    <view class="search-box">
      <input 
        type="text" 
        v-model="searchKey" 
        placeholder="搜索企业名称" 
        class="search-input"
        @confirm="handleSearch"
      />
    </view>

    <!-- 企业列表 -->
    <view class="company-grid">
      <view 
        class="company-card" 
        v-for="item in companies" 
        :key="item.id"
        @tap="handleCompanyDetail(item.id)"
      >
        <image :src="item.logoUrl || defaultImage" mode="aspectFill" class="logo" />
        <view class="info">
          <text class="name">{{ item.companyName }}</text>
          <text class="desc">{{ item.description || '暂无描述' }}</text>
          <view class="stats">
            <text class="stat">产品数 {{ item.productCount }}</text>
            <text class="stat">意向数 {{ item.intentionCount }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore">加载中...</view>
    <view class="no-more" v-else>没有更多了</view>
  </view>
</template>

<style lang="scss" scoped>
.company-list {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.search-box {
  padding: 20rpx;
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 20rpx;

  .search-input {
    width: 100%;
    height: 72rpx;
    background: #f5f5f5;
    border-radius: 36rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
  }
}

.company-grid {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.company-card {
  background: #fff;
  border-radius: 12rpx;
  overflow: hidden;
}

.company-card .logo {
  width: 100%;
  height: 400rpx;
  background-color: #f5f5f5;
}

.company-card .info {
  padding: 20rpx;
}

.company-card .name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.company-card .desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.company-card .stats {
  display: flex;
  gap: 32rpx;
}

.company-card .stat {
  font-size: 24rpx;
  color: #999;
}

.load-more, .no-more {
  text-align: center;
  color: #999;
  font-size: 24rpx;
  padding: 30rpx 0;
}

.company-card:active {
  opacity: 0.8;
}
</style>

<script setup>
import { ref, onMounted } from 'vue'
import { getCompanyList } from '@/api/company'

const defaultImage = '/static/images/default-company.png'
const companies = ref([])
const searchKey = ref('')
const pageNum = ref(1)
const pageSize = 10
const hasMore = ref(true)

// 加载企业列表
const loadCompanies = async (isRefresh = false) => {
  console.log('开始加载企业列表', { isRefresh, pageNum: pageNum.value })
  
  if (isRefresh) {
    pageNum.value = 1
    companies.value = []
  }

  try {
    const res = await getCompanyList({
      companyName: searchKey.value,
      pageNum: pageNum.value,
      pageSize
    })

    console.log('企业列表请求结果:', res)

    if (res.code === 200) {
      const list = res.data || []
      console.log('处理后的企业列表:', list)
      
      if (isRefresh) {
        companies.value = list
      } else {
        companies.value.push(...list)
      }
      hasMore.value = list.length === pageSize
      pageNum.value++
    }
  } catch (error) {
    console.error('获取企业列表失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  }
}

// 搜索
const handleSearch = () => {
  loadCompanies(true)
}

// 跳转到详情
const handleCompanyDetail = (id) => {
  uni.navigateTo({
    url: `/pages/company/detail/index?id=${id}`
  })
}

// 定义页面生命周期钩子
defineExpose({
  onPullDownRefresh() {
    console.log('触发下拉刷新')
    loadCompanies(true).then(() => {
      uni.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    console.log('触发上拉加载')
    if (hasMore.value) {
      loadCompanies()
    }
  }
})

// 页面加载
onMounted(() => {
  console.log('页面加载完成，开始初始化数据')
  loadCompanies()
})
</script> 