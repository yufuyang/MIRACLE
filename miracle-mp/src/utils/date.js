/**
 * 格式化时间
 * @param {string | number | Date} time 时间
 * @param {string} format 格式，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(time, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!time) return ''
  
  const date = new Date(time)
  
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  const formatObj = {
    YYYY: year,
    MM: padStart(month),
    DD: padStart(day),
    HH: padStart(hour),
    mm: padStart(minute),
    ss: padStart(second)
  }

  return format.replace(/(YYYY|MM|DD|HH|mm|ss)/g, (match) => {
    return formatObj[match]
  })
}

/**
 * 数字补零
 * @param {number} num 数字
 * @returns {string} 补零后的字符串
 */
function padStart(num) {
  return num.toString().padStart(2, '0')
}

/**
 * 获取相对时间
 * @param {string | number | Date} time 时间
 * @returns {string} 相对时间字符串
 */
export function getRelativeTime(time) {
  if (!time) return ''
  
  const now = new Date().getTime()
  const diff = now - new Date(time).getTime()
  
  if (diff < 0) return formatTime(time)
  
  const minute = 1000 * 60
  const hour = minute * 60
  const day = hour * 24
  const month = day * 30
  const year = month * 12
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < month) {
    return Math.floor(diff / day) + '天前'
  } else if (diff < year) {
    return Math.floor(diff / month) + '个月前'
  } else {
    return Math.floor(diff / year) + '年前'
  }
}

/**
 * 格式化日期范围
 * @param {string | number | Date} startTime 开始时间
 * @param {string | number | Date} endTime 结束时间
 * @param {string} format 格式，默认 'YYYY-MM-DD'
 * @returns {string} 格式化后的日期范围字符串
 */
export function formatDateRange(startTime, endTime, format = 'YYYY-MM-DD') {
  if (!startTime || !endTime) return ''
  return `${formatTime(startTime, format)} 至 ${formatTime(endTime, format)}`
} 