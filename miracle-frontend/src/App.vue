<template>
  <router-view></router-view>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(async () => {
  // 初始化用户状态
  userStore.initUserState()
  
  // 如果已登录，获取用户详情
  if (userStore.isLoggedIn()) {
    await userStore.fetchUserDetail()
  }
})
</script>

<style>
#app {
  width: 100%;
  height: 100%;
}

html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
</style>
