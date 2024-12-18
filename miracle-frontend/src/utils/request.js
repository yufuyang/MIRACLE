import axios from 'axios'
import { message } from 'ant-design-vue'

// 创建axios实例
const service = axios.create({
  baseURL: '', // url = base url + request url
  timeout: 5000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 如果返回的状态码不是200，说明接口有问题，把错误信息显示出来
    if (res.code !== 200) {
      message.error(res.message || '系统错误')
      return Promise.reject(new Error(res.message || '系统错误'))
    } else {
      return res.data
    }
  },
  error => {
    console.error('响应错误:', error)
    if (error.code === 'ECONNREFUSED') {
      message.error('无法连接到服务器，请确保后端服务已启动')
    } else {
      message.error(error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default service 