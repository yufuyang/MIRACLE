import { createRouter, createWebHistory } from 'vue-router'
import WebsiteLayout from '@/layout/website/index.vue'
import WorkspaceLayout from '@/layout/workspace/index.vue'
import EmptyLayout from '@/layout/empty.vue'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/register/index.vue')
  },
  // 前台路由
  {
    path: '/',
    component: WebsiteLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/website/home/index.vue')
      },
      {
        path: 'product',
        name: 'Product',
        component: EmptyLayout,
        children: [
          {
            path: '',
            name: 'ProductList',
            component: () => import('@/views/website/product/list.vue'),
            meta: { title: '产品列表' }
          },
          {
            path: ':id',
            name: 'ProductDetail',
            component: () => import('@/views/website/product/detail.vue'),
            meta: { title: '产品详情' }
          }
        ]
      },
      {
        path: 'company',
        name: 'Company',
        component: EmptyLayout,
        children: [
          {
            path: '',
            name: 'CompanyList',
            component: () => import('@/views/website/company/list.vue')
          },
          {
            path: ':id',
            name: 'CompanyDetail',
            component: () => import('@/views/website/company/detail.vue')
          }
        ]
      },
      {
        path: 'activity',
        name: 'WebsiteActivity',
        component: EmptyLayout,
        children: [
          {
            path: '',
            name: 'WebsiteActivityList',
            component: () => import('@/views/website/activity/list.vue'),
            meta: { title: '活动列表' }
          },
          {
            path: ':id',
            name: 'WebsiteActivityDetail',
            component: () => import('@/views/website/activity/detail.vue'),
            meta: { title: '活动详情' }
          }
        ]
      }
    ]
  },
  // 工作台
  {
    path: '/workspace',
    name: 'workspace',
    component: WorkspaceLayout,
    meta: { requiresAuth: true, showSidebar: true },
    redirect: to => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo'))
      console.log('Current user role:', userInfo?.role)
      switch (userInfo?.role) {
        case 'MERCHANT':
          return '/workspace/merchant/home'
        case 'COMPANY':
          return '/workspace/profile'
        default:
          return '/workspace/stats/overview'
      }
    },
    children: [
      {
        path: '',
        name: 'Workspace',
        redirect: (to) => {
          const userRole = JSON.parse(localStorage.getItem('userInfo'))?.role
          return userRole === 'COMPANY' ? '/workspace/profile' : '/workspace/merchant/home'
        }
      },
      {
        path: 'stats',
        meta: { roles: ['ADMIN', 'COMPANY'] },
        children: [
          {
            path: 'overview',
            component: () => import('@/views/workspace/stats/overview/index.vue'),
            meta: { title: '数据概览' }
          }
        ]
      },
      {
        path: 'product',
        name: 'CompanyProduct',
        redirect: 'product/list',
        children: [
          {
            path: 'list',
            name: 'CompanyProductList',
            component: () => import('@/views/workspace/company/product/list.vue'),
            meta: { title: '产品列表' }
          },
          {
            path: 'detail/:id',
            name: 'CompanyProductDetail',
            component: () => import('@/views/workspace/company/product/detail.vue'),
            meta: { title: '产品详情' }
          },
          {
            path: 'category',
            name: 'CompanyProductCategory',
            component: () => import('@/views/workspace/company/product/category.vue'),
            meta: { title: '产品分类' }
          },
          {
            path: 'statistics',
            name: 'ProductStatistics',
            component: () => import('@/views/workspace/company/product/statistics.vue'),
            meta: { title: '产品统计' }
          }
        ]
      },
      {
        path: 'activity/list',
        name: 'WorkspaceActivityList',
        component: () => import('@/views/workspace/company/activity/list.vue'),
        meta: { title: '活动列表' }
      },
      {
        path: 'activity/detail/:id',
        name: 'WorkspaceActivityDetail',
        component: () => import('@/views/workspace/company/activity/detail.vue'),
        meta: { title: '活动详情' }
      },
      {
        path: 'activity/edit/:id',
        name: 'ActivityEdit',
        component: () => import('@/views/workspace/company/activity/edit.vue'),
        meta: { title: '编辑活动', showSidebar: true }
      },
      {
        path: 'activity/stats',
        name: 'ActivityStats',
        component: () => import('@/views/workspace/company/activity/stats.vue'),
        meta: { title: '活动统计' }
      },
      {
        path: 'inquiry',
        name: 'WorkspaceInquiry',
        redirect: 'inquiry/list',
        children: [
          {
            path: 'list',
            name: 'InquiryList',
            component: () => import('@/views/workspace/company/inquiry/list.vue'),
            meta: { title: '意向列表' }
          },
          {
            path: 'stats',
            name: 'InquiryStats',
            component: () => import('@/views/workspace/company/inquiry/stats.vue'),
            meta: { title: '意向统计' }
          }
        ]
      },
      {
        path: 'order',
        name: 'WorkspaceOrder',
        redirect: 'order/list',
        children: [
          {
            path: 'list',
            name: 'OrderList',
            component: () => import('@/views/workspace/company/order/list.vue'),
            meta: { title: '订单列表' }
          },
          {
            path: 'stats',
            name: 'OrderStats',
            component: () => import('@/views/workspace/company/order/stats.vue'),
            meta: { title: '订单统计' }
          }
        ]
      },
      {
        path: 'cooperation',
        name: 'WorkspaceCooperation',
        redirect: 'cooperation/list',
        children: [
          {
            path: 'list',
            name: 'CooperationList',
            component: () => import('@/views/workspace/company/cooperation/list.vue'),
            meta: { title: '合作列表' }
          },
          {
            path: 'stats',
            name: 'CooperationStats',
            component: () => import('@/views/workspace/company/cooperation/stats.vue'),
            meta: { title: '合作统计' }
          }
        ]
      },
      {
        path: 'profile',
        name: 'WorkspaceProfile',
        component: () => import('@/views/workspace/profile/index.vue'),
        meta: { title: '企业资料' }
      },
      {
        path: 'merchant',
        meta: { roles: ['MERCHANT'] },
        children: [
          {
            path: 'home',
            name: 'MerchantHome',
            component: () => import('@/views/workspace/merchant/home/index.vue'),
            meta: { title: '商户首页' }
          },
          {
            path: 'activity',
            name: 'MerchantActivity',
            children: [
              {
                path: 'list',
                name: 'MerchantActivityList',
                component: () => import('@/views/workspace/merchant/activity/list.vue'),
                meta: { title: '活动列表' }
              }
            ]
          },
          {
            path: 'intention',
            name: 'MerchantIntention',
            children: [
              {
                path: 'list',
                name: 'MerchantIntentionList',
                component: () => import('@/views/workspace/merchant/intention/list.vue'),
                meta: { title: '意向列表' }
              }
            ]
          },
          {
            path: 'cooperation',
            name: 'MerchantCooperation',
            children: [
              {
                path: 'list',
                name: 'MerchantCooperationList',
                component: () => import('@/views/workspace/merchant/cooperation/list.vue'),
                meta: { title: '合作列表' }
              }
            ]
          }
        ]
      }
    ]
  },
  {
    path: '/company',
    component: WebsiteLayout,
    meta: { requiresAuth: true, showSidebar: true },
    children: [
      {
        path: 'product/list',
        redirect: '/workspace/product/list'
      },
      {
        path: 'product/category',
        redirect: '/workspace/product/category'
      },
      {
        path: 'activity/list',
        redirect: '/workspace/activity/list'
      },
      {
        path: 'activity/stats',
        redirect: '/workspace/activity/stats'
      },
      {
        path: 'inquiry/list',
        redirect: '/workspace/inquiry/list'
      },
      {
        path: 'inquiry/stats',
        redirect: '/workspace/inquiry/stats'
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  const token = localStorage.getItem('token')
  console.log('Router guard - userInfo:', userInfo)

  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
      return
    }

    if (userInfo?.role === 'MERCHANT' && !to.path.startsWith('/workspace/merchant')) {
      next('/workspace/merchant/home')
      return
    }

    if (userInfo?.role === 'COMPANY' && !to.path.startsWith('/workspace/profile')) {
      next('/workspace/profile')
      return
    }
  }

  next()
})

export default router 