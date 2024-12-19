// 模拟产品数据
export const mockProducts = [
  {
    id: 1,
    productName: '高性能工业机器人',
    price: 299999,
    imageUrl: '',
    viewCount: 1234,
    intentionCount: 56,
    categoryId: 1,
    companyId: 1,
    description: '六轴工业机器人,具有高精度、高稳定性的特点',
  },
  {
    id: 2,
    productName: '智能包装生产线',
    price: 499999,
    imageUrl: '',
    viewCount: 890,
    intentionCount: 45,
    categoryId: 2,
    companyId: 2,
    description: '全自动智能包装生产线,效率提升300%',
  },
  {
    id: 3,
    productName: '工业物联网传感器',
    price: 1999,
    imageUrl: '',
    viewCount: 2345,
    intentionCount: 120,
    categoryId: 3,
    companyId: 3,
    description: '高精度工业物联网传感器,支持多种协议',
  },
  {
    id: 4,
    productName: '激光切割机',
    price: 199999,
    imageUrl: '',
    viewCount: 1567,
    intentionCount: 89,
    categoryId: 4,
    companyId: 1,
    description: '高功率激光切割机,切割精度达0.01mm',
  },
  {
    id: 5,
    productName: '3D打印机',
    price: 29999,
    imageUrl: '',
    viewCount: 3456,
    intentionCount: 234,
    categoryId: 5,
    companyId: 2,
    description: '工业级3D打印机,支持多种材料打印',
  },
  {
    id: 6,
    productName: 'AGV小车',
    price: 49999,
    imageUrl: '',
    viewCount: 2789,
    intentionCount: 167,
    categoryId: 6,
    companyId: 3,
    description: '智能AGV物流小车,载重1000kg',
  },
  {
    id: 7,
    productName: '工业控制系统',
    price: 99999,
    imageUrl: '',
    viewCount: 1890,
    intentionCount: 78,
    categoryId: 7,
    companyId: 1,
    description: 'PLC工业控制系统,支持远程控制',
  },
  {
    id: 8,
    productName: '工业机械臂',
    price: 149999,
    imageUrl: '',
    viewCount: 2345,
    intentionCount: 145,
    categoryId: 1,
    companyId: 2,
    description: '四轴机械臂,适用于轻型工业应用',
  }
]

// 模拟企业数据
export const mockCompanies = [
  {
    id: 1,
    companyName: '科技创新有限公司',
    logo: '',
    description: '专注于人工智能和大数据技术的创新型企业，致力于为客户提供智能化解决方案。',
    address: '北京市海淀区',
    phone: '010-12345678',
    email: 'contact@tech.com',
    productCount: 12,
    viewCount: 1580
  },
  {
    id: 2,
    companyName: '环保科技股份公司',
    logo: '',
    description: '致力于环保技术研发和应用，提供全方位的环保解决方案。',
    address: '上海市浦东新区',
    phone: '021-87654321',
    email: 'info@eco.com',
    productCount: 8,
    viewCount: 960
  },
  {
    id: 3,
    companyName: '智能制造技术公司',
    logo: '',
    description: '专业从事工业自动化和智能制造设备研发生产的高新技术企业。',
    address: '深圳市南山区',
    phone: '0755-98765432',
    email: 'support@smart.com',
    productCount: 15,
    viewCount: 2100
  },
  {
    id: 4,
    companyName: '新能源科技有限公司',
    logo: '',
    description: '专注于新能源技术研发和应用，提供清洁能源解决方案。',
    address: '广州市天河区',
    phone: '020-45678901',
    email: 'contact@energy.com',
    productCount: 6,
    viewCount: 850
  },
  {
    id: 5,
    companyName: '医疗器械研发公司',
    logo: '',
    description: '致力于医疗器械研发和生产的高科技企业，拥有多项专利技术。',
    address: '杭州市滨江区',
    phone: '0571-23456789',
    email: 'info@medical.com',
    productCount: 10,
    viewCount: 1280
  },
  {
    id: 6,
    companyName: '物联网技术有限公司',
    logo: '',
    description: '专业提供物联网技术解决方案，助力企业数字化转型。',
    address: '成都市高新区',
    phone: '028-34567890',
    email: 'support@iot.com',
    productCount: 9,
    viewCount: 920
  }
]

// 模拟产品分类数据
export const mockCategories = [
  {
    value: 1,
    title: '工业机器人',
    children: [
      {
        value: 11,
        title: '六轴机器人'
      },
      {
        value: 12,
        title: '四轴机器人'
      }
    ]
  },
  {
    value: 2,
    title: '自动化生产线',
    children: [
      {
        value: 21,
        title: '包装生产线'
      },
      {
        value: 22,
        title: '装配生产线'
      }
    ]
  },
  {
    value: 3,
    title: '工业物联网设备',
    children: [
      {
        value: 31,
        title: '传感器'
      },
      {
        value: 32,
        title: '控制器'
      }
    ]
  },
  {
    value: 4,
    title: '加工设备',
    children: [
      {
        value: 41,
        title: '激光切割机'
      },
      {
        value: 42,
        title: '数控机床'
      }
    ]
  }
]

// 模拟统计数据
export const mockStats = {
  companyCount: 1234,
  merchantCount: 5678,
  productCount: 9012,
  intentionCount: 3456
} 