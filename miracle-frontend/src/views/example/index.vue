<template>
  <div class="example-page">
    <!-- 产品展示区域 -->
    <section-container
      title="热门产品推荐"
      view-all-text="比较各款机型"
      @view-all="handleViewAllProducts"
    >
      <div class="product-grid">
        <div v-for="(product, index) in products" :key="product.id" class="product-item">
          <div class="product-image">
            <img :src="product.imageUrl || defaultImage" :alt="product.productName" />
          </div>
          <div class="product-info">
            <h3>{{ product.productName }}</h3>
            <p class="description">{{ product.description }}</p>
            <div class="price">¥{{ product.price }}</div>
          </div>
        </div>
      </div>
    </section-container>

    <!-- 企业展示区域 -->
    <section-container
      title="优质企业展示"
      @view-all="handleViewAllCompanies"
    >
      <div class="company-grid">
        <div v-for="(company, index) in companies" :key="company.id" class="company-item">
          <div class="company-image">
            <img :src="company.logo || defaultImage" :alt="company.companyName" />
          </div>
          <div class="company-info">
            <h3>{{ company.companyName }}</h3>
            <p class="description">{{ company.description }}</p>
            <div class="stats">
              <span>产品数: {{ company.productCount }}</span>
              <span>浏览量: {{ company.viewCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </section-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import SectionContainer from '@/components/SectionContainer.vue'
import defaultImage from '@/assets/images/default.jpg'
import { mockProducts, mockCompanies } from '@/mock/data'

const router = useRouter()
const products = ref(mockProducts.slice(0, 4))
const companies = ref(mockCompanies.slice(0, 4))

const handleViewAllProducts = () => {
  router.push('/product')
}

const handleViewAllCompanies = () => {
  router.push('/company')
}
</script>

<style scoped lang="less">
.example-page {
  padding: 20px 0;
}

.product-grid,
.company-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.product-item,
.company-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }

  .product-image,
  .company-image {
    width: 100%;
    height: 200px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .product-info,
  .company-info {
    padding: 20px;

    h3 {
      margin: 0 0 10px;
      font-size: 18px;
      font-weight: 600;
    }

    .description {
      color: #666;
      margin-bottom: 15px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .price {
      color: #f60;
      font-size: 20px;
      font-weight: 600;
    }

    .stats {
      display: flex;
      justify-content: space-between;
      color: #666;
      font-size: 14px;
    }
  }
}

@media (max-width: 768px) {
  .product-grid,
  .company-grid {
    grid-template-columns: 1fr;
  }
}
</style> 