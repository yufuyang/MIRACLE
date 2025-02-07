<template>
  <view class="company">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchForm.name"
        placeholder="搜索企业名称" 
        @confirm="handleSearch"
      />
      <button class="search-btn" @tap="handleSearch">搜索</button>
    </view>
    
    <!-- 企业列表 -->
    <view class="company-list">
      <view 
        class="company-item" 
        v-for="item in companies" 
        :key="item.id"
        @tap="handleCompanyDetail(item)"
      >
        <image 
          :src="item.logoUrl || defaultImage" 
          mode="aspectFill" 
          class="logo"
        />
        <view class="info">
          <text class="name">{{ item.companyName }}</text>
          <text class="desc">{{ item.description || '暂无描述' }}</text>
          <view class="stats">
            <view class="stat-item">
              <text class="label">产品数</text>
              <text class="value">{{ item.productCount }}</text>
            </view>
            <view class="stat-item">
              <text class="label">意向数</text>
              <text class="value">{{ item.intentionCount }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view class="loading-more" v-if="loading">
      <text>加载中...</text>
    </view>

    <!-- 无数据提示 -->
    <view class="empty" v-if="!loading && companies.length === 0">
      <text>暂无企业数据</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCompanyList } from '../../api/company'

// 默认图片
const defaultImage = '/static/images/default-logo.png'

// 数据定义
const companies = ref([])
const loading = ref(false)
const searchForm = ref({
  name: '',
  pageNum: 1,
  pageSize: 10
})
const hasMore = ref(true)

// 获取企业列表
const fetchCompanyList = async (isLoadMore = false) => {
  if (loading.value) return
  loading.value = true
  
  try {
    const res = await getCompanyList({
      name: searchForm.value.name,
      pageNum: searchForm.value.pageNum,
      pageSize: searchForm.value.pageSize
    })
    
    console.log('企业列表返回:', res)
    
    if (res.code === 200) {
      const list = res.data || []
      if (isLoadMore) {
        companies.value.push(...list)
      } else {
        companies.value = list
      }
      hasMore.value = list.length === searchForm.value.pageSize
    }
  } catch (error) {
    console.error('获取企业列表失败:', error)
    uni.showToast({
      title: '获取数据失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  searchForm.value.pageNum = 1
  fetchCompanyList()
}

// 查看详情
const handleCompanyDetail = (company) => {
  uni.navigateTo({
    url: `/pages/company/detail/index?id=${company.id}`
  })
}

// 下拉刷新
const onPullDownRefresh = () => {
  searchForm.value.pageNum = 1
  fetchCompanyList().finally(() => {
    uni.stopPullDownRefresh()
  })
}

// 上拉加载
const onReachBottom = () => {
  if (!hasMore.value || loading.value) return
  searchForm.value.pageNum++
  fetchCompanyList(true)
}

// 初始化
onMounted(() => {
  fetchCompanyList()
})
</script>

<style lang="scss" scoped>
@import '../../styles/index.scss';  // 导入全局样式变量

.company {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20rpx;

  .search-bar {
    display: flex;
    align-items: center;
    padding: 20rpx;
    background: #fff;
    position: sticky;
    top: 0;
    z-index: 100;

    input {
      flex: 1;
      height: 72rpx;
      background: #f5f5f5;
      border-radius: 36rpx;
      padding: 0 30rpx;
      font-size: 28rpx;
    }

    .search-btn {
      margin-left: 20rpx;
      height: 72rpx;
      line-height: 72rpx;
      padding: 0 30rpx;
      border-radius: 36rpx;
      font-size: 28rpx;
      background: #1890ff;  // 直接使用颜色值替代变量
      color: #fff;
    }
  }

  .company-list {
    padding: 20rpx;

    .company-item {
      display: flex;
      padding: 30rpx;
      background: #fff;
      border-radius: 12rpx;
      margin-bottom: 20rpx;

      .logo {
        width: 120rpx;
        height: 120rpx;
        border-radius: 12rpx;
        margin-right: 20rpx;
      }

      .info {
        flex: 1;

        .name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 12rpx;
          display: block;
        }

        .desc {
          font-size: 28rpx;
          color: #666;
          margin-bottom: 16rpx;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          overflow: hidden;
        }

        .stats {
          display: flex;
          gap: 30rpx;

          .stat-item {
            .label {
              font-size: 24rpx;
              color: #999;
              margin-right: 8rpx;
            }

            .value {
              font-size: 24rpx;
              color: #1890ff;  // 直接使用颜色值替代变量
            }
          }
        }
      }
    }
  }

  .loading-more, .empty {
    text-align: center;
    padding: 30rpx;
    color: #999;
    font-size: 28rpx;
  }
}
</style> 