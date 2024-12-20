<template>
  <div class="section">
    <div class="section-content">
      <div class="section-header" ref="headerRef">
        <h2 class="section-title">{{ title }}</h2>
        <a class="view-all-link" v-if="showViewAll" @click="$emit('viewAll')">{{ viewAllText }} ></a>
      </div>
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  showViewAll: {
    type: Boolean,
    default: true
  },
  viewAllText: {
    type: String,
    default: '查看全部'
  }
})

const headerRef = ref(null)

// 创建观察器
const createObserver = () => {
  return new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('show')
      }
    })
  }, {
    threshold: 0.1
  })
}

onMounted(() => {
  // 为标题添加观察器
  const headerObserver = createObserver()
  if (headerRef.value) headerObserver.observe(headerRef.value)
})
</script>

<style scoped lang="less">
.section {
  padding: 80px 0;
  background: #fff;
  color: #333;
  overflow: hidden;

  .section-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    opacity: 0;
    transform: translateY(20px);
    transition: all 0.8s cubic-bezier(0.33, 1, 0.68, 1);

    &.show {
      opacity: 1;
      transform: translateY(0);
    }

    .section-title {
      font-size: 32px;
      font-weight: 600;
      margin: 0;
    }

    .view-all-link {
      color: #0066cc;
      font-size: 17px;
      cursor: pointer;
      transition: opacity 0.3s;

      &:hover {
        opacity: 0.7;
      }
    }
  }
}

@media (max-width: 768px) {
  .section {
    padding: 40px 0;

    .section-header {
      .section-title {
        font-size: 32px;
      }
    }
  }
}
</style> 