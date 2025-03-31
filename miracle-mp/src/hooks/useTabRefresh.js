export function useTabRefresh(refreshCallback) {
  let lastTabClickTime = 0
  let lastPage = ''

  // 监听页面显示
  uni.$on('tabShow', (page) => {
    const now = Date.now()
    // 如果是同一个页面且时间间隔小于300ms，触发刷新
    if (page === lastPage && now - lastTabClickTime < 300) {
      refreshCallback()
    }
    lastPage = page
    lastTabClickTime = now
  })

  // 清理监听器
  return () => {
    uni.$off('tabShow')
  }
} 