<template>
  <view class="user-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="avatar-wrap">
        <image :src="userInfo?.avatar || defaultAvatar" class="avatar" mode="aspectFill" />
      </view>
      <view class="info">
        <text class="name">{{ userInfo?.username || '未登录' }}</text>
        <text class="role">{{ userInfo?.role === 'MERCHANT' ? '商户' : '企业' }}</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-title">常用功能</view>
      <view class="menu-list">
        <view 
          class="menu-item" 
          v-for="item in menuList" 
          :key="item.key"
          @tap="handleMenuClick(item)"
        >
          <view class="icon">
            <image :src="item.icon" mode="aspectFit" />
          </view>
          <text class="name">{{ item.name }}</text>
          <text class="arrow">></text>
        </view>
      </view>
    </view>

    <!-- 退出登录按钮 -->
    <view class="logout-btn" @tap="handleLogout">
      退出登录
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const defaultAvatar = '/static/images/default-avatar.png'

// 获取用户信息
const userInfo = ref(userStore.userInfo)

// 菜单配置
const menuList = ref([
  {
    key: 'profile',
    name: '商户资料',
    icon: '/static/images/icons/profile.png',
    path: '/pages/merchant/profile/index'
  },
  {
    key: 'intention',
    name: '意向管理',
    icon: '/static/images/icons/intention.png',
    path: '/pages/merchant/intention/list'
  },
  {
    key: 'cooperation',
    name: '合作管理',
    icon: '/static/images/icons/cooperation.png',
    path: '/pages/merchant/cooperation/list'
  },
  {
    key: 'order',
    name: '订单管理',
    icon: '/static/images/icons/order.png',
    path: '/pages/merchant/order/list'
  }
])

// 处理菜单点击
const handleMenuClick = (item) => {
  uni.navigateTo({
    url: item.path,
    fail: (err) => {
      console.error('页面跳转失败:', err)
      uni.showToast({
        title: '页面跳转失败',
        icon: 'none'
      })
    }
  })
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
      }
    }
  })
}

// 初始化
onMounted(() => {
  // 如果需要重新获取用户信息
  userStore.getUserInfo()
})
</script>

<style lang="scss" scoped>
.user-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;

  .user-card {
    display: flex;
    align-items: center;
    padding: 40rpx;
    background: #fff;
    margin-bottom: 20rpx;

    .avatar-wrap {
      margin-right: 30rpx;
      
      .avatar {
        width: 120rpx;
        height: 120rpx;
        border-radius: 60rpx;
      }
    }

    .info {
      .name {
        font-size: 36rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 12rpx;
        display: block;
      }

      .role {
        font-size: 28rpx;
        color: #666;
      }
    }
  }

  .menu-section {
    background: #fff;
    padding: 0 30rpx;
    margin-bottom: 20rpx;

    .menu-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      padding: 30rpx 0;
      border-bottom: 1rpx solid #eee;
    }

    .menu-list {
      .menu-item {
        display: flex;
        align-items: center;
        padding: 30rpx 0;
        border-bottom: 1rpx solid #eee;

        &:last-child {
          border-bottom: none;
        }

        .icon {
          width: 48rpx;
          height: 48rpx;
          margin-right: 20rpx;

          image {
            width: 100%;
            height: 100%;
          }
        }

        .name {
          flex: 1;
          font-size: 30rpx;
          color: #333;
        }

        .arrow {
          font-size: 30rpx;
          color: #999;
        }
      }
    }
  }

  .logout-btn {
    margin: 40rpx 30rpx;
    height: 88rpx;
    line-height: 88rpx;
    text-align: center;
    background: #fff;
    border-radius: 8rpx;
    font-size: 32rpx;
    color: #ff4d4f;
  }
}
</style> 