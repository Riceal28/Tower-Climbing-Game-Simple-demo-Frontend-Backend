<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userService } from '../api/userService'
import { User, FolderOpened, SwitchButton } from '@element-plus/icons-vue'

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
    ElMessage.warning('后端服务不可用，已本地登出')
  } finally {
    localStorage.removeItem('token')
    router.push('/login')
  }
}

const goToPlayer = () => {
  router.push('/player')
}

const goToBattle = () => {
  router.push('/battle')
}

const goToSave = () => {
  router.push('/save')
}
</script>

<template>
  <div class="home-container">
    <div class="navbar">
      <h1>🎮 冒险者大厅</h1>
      <div class="navbar-actions">
        <el-button type="primary" :icon="User" @click="goToPlayer">角色信息</el-button>
        <el-button type="danger" @click="goToBattle">开始战斗</el-button>
        <el-button type="warning" :icon="FolderOpened" @click="goToSave">存档管理</el-button>
        <el-button :icon="SwitchButton" @click="handleLogout">登出</el-button>
      </div>
    </div>
    
    <div class="content">
      <div class="welcome-section">
        <h2>欢迎来到冒险世界</h2>
        <p>选择下方功能开始你的冒险之旅</p>
      </div>
      
      <div class="feature-cards">
        <el-card class="feature-card" @click="goToPlayer">
          <div class="card-icon">🧙‍♂️</div>
          <h3>角色信息</h3>
          <p>查看角色属性、等级和装备</p>
        </el-card>
        
        <el-card class="feature-card battle" @click="goToBattle">
          <div class="card-icon">⚔️</div>
          <h3>战斗</h3>
          <p>挑战魔物，获取经验和奖励</p>
        </el-card>
        
        <el-card class="feature-card save" @click="goToSave">
          <div class="card-icon">💾</div>
          <h3>存档管理</h3>
          <p>保存和加载游戏进度</p>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background-color: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.navbar h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.navbar-actions {
  display: flex;
  gap: 10px;
}

.content {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-section h2 {
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.welcome-section p {
  font-size: 16px;
  color: #666;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.feature-card {
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  padding: 20px;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.feature-card.battle:hover {
  border-color: #f56c6c;
}

.feature-card.save:hover {
  border-color: #e6a23c;
}

.card-icon {
  font-size: 64px;
  margin-bottom: 15px;
}

.feature-card h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 10px;
}

.feature-card p {
  font-size: 14px;
  color: #666;
}

/* 响应式 */
@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 15px;
  }
  
  .navbar-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .feature-cards {
    grid-template-columns: 1fr;
  }
}
</style>
