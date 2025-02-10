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

    <!-- 功能卡片列表 -->
    <view class="menu-list">
      <view 
        class="menu-card" 
        v-for="item in menuList" 
        :key="item.key"
        @tap="handleMenuClick(item)"
      >
        <view class="icon">
          <image :src="item.icon" mode="aspectFit" />
        </view>
        <text class="name">{{ item.name }}</text>
        <text class="desc">{{ item.desc }}</text>
      </view>
    </view>

    <!-- 退出按钮 -->
    <view class="logout-wrap">
      <button class="logout-btn" @tap="handleLogout">退出登录</button>
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
    desc: '查看和编辑商户基本信息',
    icon: '/static/images/icons/profile.png',
    path: '/pages/merchant/profile/index'
  },
  {
    key: 'intention',
    name: '意向管理',
    desc: '管理产品合作意向',
    icon: '/static/images/icons/intention.png',
    path: '/pages/merchant/intention/list'
  },
  {
    key: 'cooperation',
    name: '合作管理',
    desc: '查看企业合作情况',
    icon: '/static/images/icons/cooperation.png',
    path: '/pages/merchant/cooperation/list'
  },
  {
    key: 'order',
    name: '订单管理',
    desc: '管理所有交易订单',
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
  userStore.getUserInfo()
})
</script>

<style lang="scss" scoped>
.user-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 32rpx;
}

.user-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);

  .avatar-wrap {
    margin-right: 24rpx;
    
    .avatar {
      width: 120rpx;
      height: 120rpx;
      border-radius: 60rpx;
    }
  }

  .info {
    .name {
      font-size: 36rpx;
      font-weight: 500;
      color: #333;
      display: block;
      margin-bottom: 8rpx;
    }

    .role {
      font-size: 28rpx;
      color: #666;
    }
  }
}

.menu-list {
  padding: 0 32rpx;
  
  .menu-card {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    min-height: 200rpx;

    &:active {
      opacity: 0.7;
    }

    .icon {
      width: 64rpx;
      height: 64rpx;
      margin-bottom: 16rpx;

      image {
        width: 100%;
        height: 100%;
      }
    }

    .name {
      font-size: 36rpx;
      font-weight: 500;
      color: #333;
      margin-bottom: 8rpx;
    }

    .desc {
      font-size: 24rpx;
      color: #999;
      text-align: center;
    }
  }
}

.logout-wrap {
  margin-top: 48rpx;
  padding: 0 32rpx;

  .logout-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    text-align: center;
    background-color: #fff;
    color: #ff4d4f;
    border-radius: 44rpx;
    font-size: 32rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);

    &:active {
      opacity: 0.7;
    }
  }
}
</style> 