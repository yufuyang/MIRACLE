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
  <div class="section product-section">
    <div class="section-content">
      <div class="section-header" ref="headerRef">
        <h2 class="section-title">热门产品推荐</h2>
        <a class="compare-link">比较各款机型 ></a>
      </div>

      <div class="product-grid">
        <div v-for="(product, index) in hotProducts" :key="product.id"
             class="product-item"
             :ref="el => productRefs[index] = el"
             @click="goToProduct(product.id)">
          <div class="product-image">
            <img :src="product.imageUrl || defaultImage" :alt="product.productName" />
          </div>
          <div class="product-info">
            <div class="product-tag">新款</div>
            <h3>{{ product.productName }}</h3>
            <div class="product-subtitle">{{ product.description }}</div>
            <div class="price-info">
              <div class="monthly">
                <span class="amount">¥{{ Math.floor(product.price/24) }}</span>/月起或
              </div>
              <div class="total">
                ¥{{ product.price }} 起**
              </div>
            </div>
            <a-button type="link" class="learn-more">
              了解更多 >
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 优质企业展示 -->
  <div class="section company-section">
    <div class="section-content">
      <div class="section-header" ref="companyHeaderRef">
        <h2 class="section-title">优质企业展示</h2>
        <a class="view-all-link">查看全部 ></a>
      </div>

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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { RightOutlined, ShopOutlined, EyeOutlined } from '@ant-design/icons-vue'
import { mockProducts, mockCompanies } from '@/mock/data'
import defaultImage from '@/assets/images/default.jpg'

const router = useRouter()
const headerRef = ref(null)
const companyHeaderRef = ref(null)
const productRefs = ref([])
const companyRefs = ref([])

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
const hotProducts = ref(mockProducts.slice(0, 4))
const featuredCompanies = ref(mockCompanies.slice(0, 4))

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
  // 为标题添加观察器
  const headerObserver = createObserver()
  if (headerRef.value) headerObserver.observe(headerRef.value)
  if (companyHeaderRef.value) headerObserver.observe(companyHeaderRef.value)

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
})

// 跳转函数
const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

const goToCompany = (id) => {
  router.push(`/company/${id}`)
}
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
    margin-bottom: 40px;
    opacity: 0;
    transform: translateY(20px);
    transition: all 0.8s cubic-bezier(0.33, 1, 0.68, 1);

    &.show {
      opacity: 1;
      transform: translateY(0);
    }

    .section-title {
      font-size: 40px;
      font-weight: 600;
      margin: 0;
    }

    .compare-link, .view-all-link {
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

.product-grid, .company-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  margin-top: 30px;
}

.product-item, .company-item {
  text-align: center;
  cursor: pointer;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s cubic-bezier(0.33, 1, 0.68, 1);

  &.show {
    opacity: 1;
    transform: translateY(0);
  }

  &:hover {
    transform: translateY(-5px);
  }

  .product-image, .company-image {
    width: 100%;
    height: 300px;
    margin-bottom: 20px;
    border-radius: 18px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.6s ease;
    }
  }

  .product-info, .company-info {
    padding: 0 20px;

    .product-tag {
      display: inline-block;
      color: #bf4800;
      font-size: 12px;
      margin-bottom: 8px;
    }

    h3 {
      font-size: 24px;
      font-weight: 600;
      margin-bottom: 8px;
      color: #1d1d1f;
    }

    .product-subtitle, .company-subtitle {
      font-size: 14px;
      color: #86868b;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .price-info {
      margin-bottom: 12px;

      .monthly {
        font-size: 14px;
        color: #86868b;
        margin-bottom: 4px;

        .amount {
          font-size: 17px;
          color: #1d1d1f;
          font-weight: 600;
        }
      }

      .total {
        font-size: 14px;
        color: #86868b;
      }
    }

    .stats {
      display: flex;
      justify-content: center;
      gap: 16px;
      font-size: 14px;
      color: #86868b;
      margin-bottom: 12px;
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
  .section {
    padding: 40px 0;

    .section-header {
      .section-title {
        font-size: 32px;
      }
    }
  }

  .product-grid, .company-grid {
    grid-template-columns: 1fr;
  }
}

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

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(180deg, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0) 50%, rgba(0,0,0,0.3) 100%);
    border-radius: 20px;
    z-index: 1;
    pointer-events: none;
  }
}

.banner-item {
  position: relative;
  height: 500px;
  overflow: hidden;

  .banner-info {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: #fff;
    z-index: 2;
    width: 80%;
    max-width: 800px;
    opacity: 0;
    animation: fadeInUp 1s forwards;

    h2 {
      font-size: 52px;
      font-weight: 700;
      margin-bottom: 16px;
      color: #fff;
      letter-spacing: -0.02em;
      line-height: 1.2;
      text-shadow: 0 2px 20px rgba(0,0,0,0.3);
      transform: translateY(20px);
      opacity: 0;
      animation: slideUp 0.8s forwards 0.3s;
    }

    p {
      font-size: 24px;
      margin: 0;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 400;
      letter-spacing: 0.02em;
      transform: translateY(20px);
      opacity: 0;
      animation: slideUp 0.8s forwards 0.5s;
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translate(-50%, -40%);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>