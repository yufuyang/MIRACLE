import {
  DashboardOutlined,
  CalendarOutlined,
  OrderedListOutlined,
  HandshakeOutlined,
  HeartOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

// 商户工作台菜单
export const merchantMenus = [
  {
    key: 'merchant-dashboard',
    icon: DashboardOutlined,
    label: '工作台首页',
    path: '/workspace/merchant/dashboard'
  },
  {
    key: 'merchant-activity',
    icon: CalendarOutlined,
    label: '活动管理',
    children: [
      {
        key: 'merchant-activity-list',
        label: '活动列表',
        path: '/workspace/merchant/activity/list'
      }
    ]
  },
  {
    key: 'merchant-order',
    icon: OrderedListOutlined,
    label: '订单管理',
    children: [
      {
        key: 'merchant-order-list',
        label: '订单列表',
        path: '/workspace/merchant/order/list'
      }
    ]
  },
  {
    key: 'merchant-cooperation',
    icon: HandshakeOutlined,
    label: '合作管理',
    children: [
      {
        key: 'merchant-cooperation-list',
        label: '合作列表',
        path: '/workspace/merchant/cooperation/list'
      }
    ]
  },
  {
    key: 'merchant-intention',
    icon: HeartOutlined,
    label: '意向管理',
    children: [
      {
        key: 'merchant-intention-list',
        label: '意向列表',
        path: '/workspace/merchant/intention/list'
      }
    ]
  },
  {
    key: 'merchant-profile',
    icon: UserOutlined,
    label: '商户资料',
    path: '/workspace/merchant/profile'
  }
]

// 根据角色返回对应的菜单
export const getMenusByRole = (role) => {
  return role === 'merchant' ? merchantMenus : companyMenus
} 