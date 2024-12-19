<template>
  <!-- 活动轮播图 -->
  <a-carousel class="banner-carousel" autoplay>
    <div class="banner-item" v-for="(banner, index) in banners" :key="index">
      <img :src="banner.imageUrl || defaultImage" :alt="banner.title" />
      <div class="banner-info">
        <h2>{{ banner.title }}</h2>
        <p>{{ banner.description }}</p>
      </div>
    </div>
  </a-carousel>

  <!-- 产品展示区域 -->
  <div class="product-section">
    <section-container
      title="热门产品推荐"
      view-all-text="查看全部"
      @view-all="goToProduct"
    >
      <div class="product-grid">
        <div v-for="(product, index) in hotProducts" :key="product.id"
             class="product-item"
             :ref="el => productRefs[index] = el"
             @click="goToProduct(product.id)">
          <div class="product-image">
            <img :src="product.imageUrl || defaultImage" :alt="product.productName" />
          </div>
          <div class="product-info">
            <div class="product-tag" v-if="product.isNew">新款</div>
            <h3>{{ product.productName }}</h3>
            <div class="product-subtitle">{{ product.description }}</div>
            <div class="stats">
              <span><eye-outlined /> {{ product.viewCount }} 浏览</span>
              <span><heart-outlined /> {{ product.intentionCount }} 意向</span>
            </div>
            <a-button type="link" class="learn-more">
              了解更多 >
            </a-button>
          </div>
        </div>
      </div>
    </section-container>
  </div>

  <!-- 优质企业展示 -->
  <div class="company-section">
    <section-container
      title="优质企业展示"
      @view-all="goToCompany"
    >
      <div class="company-grid">
        <div v-for="(company, index) in featuredCompanies" :key="company.id"
             class="company-item"
             :ref="el => companyRefs[index] = el"
             @click="goToCompany(company.id)">
          <div class="company-image">
            <img :src="company.logo || defaultImage" :alt="company.companyName" />
          </div>
          <div class="company-info">
            <h3>{{ company.companyName }}</h3>
            <div class="company-subtitle">{{ company.description }}</div>
            <div class="stats">
              <span><shop-outlined /> {{ company.productCount }} 产品</span>
              <span><eye-outlined /> {{ company.viewCount }} 浏览</span>
            </div>
            <a-button type="link" class="learn-more">
              了解更多 >
            </a-button>
          </div>
        </div>
      </div>
    </section-container>
  </div>

  <!-- 合作邀请区域 -->
  <div class="cooperation-section">
    <div class="cooperation-content">
      <h2>欢迎加入 MIRACLE</h2>
      <p>立即注册，开启智能制造新征程</p>
      <a-button type="primary" size="large" @click="goToRegister">免费注册</a-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ShopOutlined, EyeOutlined, HeartOutlined } from '@ant-design/icons-vue'
import { mockProducts, mockCompanies } from '@/mock/data'
import defaultImage from '@/assets/images/default.jpg'
import SectionContainer from '@/components/SectionContainer.vue'

const router = useRouter()
const productRefs = ref([])
const companyRefs = ref([])
const companySectionRef = ref(null)

// 模拟轮播图数据
const banners = ref([
  {
    title: '工业智造，引领未来',
    description: '探索智能制造新境界',
    imageUrl: defaultImage
  },
  {
    title: '品质铸就品牌',
    description: '专业服务，值得信赖',
    imageUrl: defaultImage
  },
  {
    title: '创新驱动发展',
    description: '技术引领，持续创新',
    imageUrl: defaultImage
  }
])

// 使用模拟数据
const hotProducts = ref(mockProducts.slice(0, 6))
const featuredCompanies = ref(mockCompanies.slice(0, 6))

// 创建观察器
const createObserver = (delay = 0) => {
  return new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        setTimeout(() => {
          entry.target.classList.add('show')
        }, delay)
      }
    })
  }, {
    threshold: 0.1
  })
}

onMounted(() => {
  // 为产品添加观察器
  productRefs.value.forEach((el, index) => {
    if (el) {
      const observer = createObserver(index * 200)
      observer.observe(el)
    }
  })

  // 为企业添加观察器
  companyRefs.value.forEach((el, index) => {
    if (el) {
      const observer = createObserver(index * 200)
      observer.observe(el)
    }
  })

  // 为企业区域添加滚动观察器
  const sectionObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('show')
          sectionObserver.unobserve(entry.target)
        }
      })
    },
    {
      threshold: 0.2
    }
  )

  const companySection = document.querySelector('.company-section')
  if (companySection) {
    sectionObserver.observe(companySection)
  }
})

// 跳转函数
const goToProduct = (id) => {
  if (id) {
    router.push(`/product/${id}`)
  } else {
    router.push('/product')
  }
}

const goToCompany = (id) => {
  if (id) {
    router.push(`/company/${id}`)
  } else {
    router.push('/company')
  }
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped lang="less">
.banner-carousel {
  width: 100%;
  max-width: 1000px;
  height: 500px;
  margin: 40px auto;
  padding: 0;
  position: relative;

  :deep(.slick-slide) {
    img {
      width: 100%;
      height: 500px;
      object-fit: cover;
      border-radius: 20px;
      filter: brightness(0.85);
      transition: all 0.6s ease;
    }
  }

  :deep(.slick-dots) {
    bottom: 30px;
    z-index: 3;

    li {
      margin: 0 8px;
      
      button {
        width: 30px;
        height: 4px;
        background: rgba(255, 255, 255, 0.4);
        border-radius: 2px;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.6);
        }
      }

      &.slick-active button {
        width: 50px;
        background: #fff;
      }
    }
  }
}

.banner-item {
  position: relative;
  height: 500px;

  .banner-info {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: #fff;
    z-index: 2;

    h2 {
      font-size: 48px;
      font-weight: 600;
      margin-bottom: 16px;
      color: #fff;
    }

    p {
      font-size: 24px;
      margin: 0;
    }
  }
}

.product-grid,
.company-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.product-item,
.company-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  opacity: 0;
  transform: translateY(20px);
  height: 100%;
  display: flex;
  flex-direction: column;

  &.show {
    opacity: 1;
    transform: translateY(0);
  }

  &:hover {
    transform: translateY(-5px);
  }

  .product-image,
  .company-image {
    width: 100%;
    height: 240px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.6s ease;
    }
  }

  .product-info,
  .company-info {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;

    .product-tag {
      display: inline-block;
      padding: 2px 8px;
      background: #ff6b00;
      color: #fff;
      border-radius: 4px;
      font-size: 12px;
      margin-bottom: 8px;
    }

    h3 {
      margin: 0 0 10px;
      font-size: 18px;
      font-weight: 600;
      line-height: 1.4;
      height: 50px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .product-subtitle,
    .company-subtitle {
      color: #666;
      margin-bottom: 15px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      height: 40px;
      line-height: 1.4;
    }

    .stats {
      display: flex;
      justify-content: space-between;
      color: #666;
      font-size: 14px;
      margin-bottom: 12px;
      margin-top: auto;
    }

    .learn-more {
      color: #0066cc;
      font-size: 17px;
      padding: 0;
      height: auto;
      border: none;
      background: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

@media (max-width: 768px) {
  .banner-carousel {
    height: 300px;
    margin: 20px auto;

    :deep(.slick-slide) img {
      height: 300px;
    }

    .banner-info {
      h2 {
        font-size: 32px;
      }

      p {
        font-size: 18px;
      }
    }
  }

  .product-grid,
  .company-grid {
    grid-template-columns: 1fr;
  }
}

.product-section {
  position: relative;
  padding: 40px 0;
  background: #fff;

  :deep(.section) {
    position: relative;
    z-index: 2;
    background: transparent;
    padding-top: 0;
  }
}

.company-section {
  position: relative;
  padding: 40px 0;
  background: #f0f2f5;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);

  &.show {
    opacity: 1;
    transform: translateY(0);
  }

  :deep(.section) {
    position: relative;
    z-index: 2;
    background: transparent;
    padding-top: 0;
  }

  .company-grid {
    position: relative;
    z-index: 2;
  }

  .company-item {
    background: #fff;
  }
}

@media (max-width: 768px) {
  .product-section,
  .company-section {
    padding: 40px 0;
  }
}

.cooperation-section {
  padding: 80px 0;
  background: #fff;
  text-align: center;
  color: #333;

  .cooperation-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;

    h2 {
      font-size: 36px;
      font-weight: 600;
      margin-bottom: 16px;
      color: #333;
    }

    p {
      font-size: 18px;
      margin-bottom: 32px;
      color: #666;
    }

    .ant-btn {
      height: 48px;
      padding: 0 40px;
      font-size: 16px;
      border-radius: 24px;
      background: #1890ff;
      color: #fff;
      border: none;
      font-weight: 600;
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);

      &:hover {
        background: #40a9ff;
      }
    }
  }
}

@media (max-width: 768px) {
  .cooperation-section {
    padding: 40px 0;

    .cooperation-content {
      h2 {
        font-size: 28px;
      }

      p {
        font-size: 16px;
      }

      .ant-btn {
        height: 44px;
        padding: 0 32px;
        font-size: 15px;
      }
    }
  }
}
</style>