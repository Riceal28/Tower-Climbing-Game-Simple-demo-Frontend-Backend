<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userService } from '../api/userService'

const router = useRouter()

const handleLogout = async () => {
  const token = localStorage.getItem('token')

  if (!token) {
    ElMessage.warning('未找到登录令牌，请重新登录')
    router.push('/login')
    return
  }

  try {
    const response = await userService.logout()

    if (response.data.success) {
      ElMessage.success('登出成功')
    } else {
      ElMessage.warning(response.data.message || '登出失败')
    }
  } catch (error: any) {
    // 网络错误时显示提示，但仍继续登出流程
    ElMessage.warning('后端服务不可用，已本地登出')
  } finally {
    // 无论后端调用是否成功，都清除本地token并跳转
    localStorage.removeItem('token')
    router.push('/login')
  }
}
</script>

<template>
  <div class="home-container">
    <div class="navbar">
      <h1>欢迎来到首页</h1>
      <el-button @click="handleLogout">登出</el-button>
    </div>
    <div class="content">
      <p>这是首页</p>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.content {
  padding: 30px;
}

.content p {
  font-size: 16px;
  color: #666;
}
</style>
