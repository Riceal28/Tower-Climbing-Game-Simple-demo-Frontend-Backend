<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userService, playerService, saveService, PlayerShowResp, SaveInfo } from '../api/userService'
import { gameContext } from '../api/gameContext'
import { User, FolderOpened, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()

// 游戏状态
const currentPlayer = ref<PlayerShowResp | null>(null)
const currentSave = ref<SaveInfo | null>(null)

// 获取当前 ID
const playerId = computed(() => gameContext.getPlayerId())
const saveId = computed(() => gameContext.getSaveId())

// 步骤逻辑
const currentStep = computed(() => {
  if (!playerId.value) return 'player'
  if (!saveId.value) return 'save'
  return 'battle'
})

// 加载基础信息
const loadContextInfo = async () => {
  if (playerId.value) {
    try {
      const pResp = await playerService.getPlayerBaseInfo()
      if (pResp.data.success) {
        currentPlayer.value = pResp.data.data
      }
    } catch (e) {
      console.error('Failed to load player info')
    }
  }

  if (saveId.value) {
    try {
      const sResp = await saveService.getCurrentSave()
      if (sResp.data.success) {
        currentSave.value = sResp.data.data
      }
    } catch (e) {
      console.error('Failed to load save info')
    }
  }
}

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
    localStorage.removeItem('playerId')
    localStorage.removeItem('saveId')
    router.push('/login')
  }
}

const goToPlayer = () => router.push('/player')
const goToSave = () => router.push('/save')
const goToBattle = () => router.push('/battle')

// 重置选择 - 重选角色
const resetPlayer = () => {
  gameContext.setPlayerId(null as any)
  gameContext.setSaveId(null as any)
  localStorage.removeItem('playerId')
  localStorage.removeItem('saveId')
  currentPlayer.value = null
  currentSave.value = null
  // 导航到角色选择页面
  router.push('/player')
}

// 切换存档
const resetSave = () => {
  gameContext.setSaveId(null as any)
  localStorage.removeItem('saveId')
  currentSave.value = null
  // 导航到存档选择页面
  router.push('/save')
}

onMounted(() => {
  loadContextInfo()
})

// 职阶辅助
const getClassName = (cls: string) => {
  const map: any = { 'SABER': '剑士', 'ARCHER': '弓兵', 'CASTER': '魔法师' }
  return map[cls] || cls
}
const getClassIcon = (cls: string) => {
  const map: any = { 'SABER': '⚔️', 'ARCHER': '🏹', 'CASTER': '🔮' }
  return map[cls] || '👤'
}
</script>

<template>
  <div class="home-container">
    <div class="navbar">
      <h1>🎮 冒险者大厅</h1>
      <div class="navbar-actions">
        <el-button :icon="SwitchButton" @click="handleLogout">登出</el-button>
      </div>
    </div>
    
    <!-- 顶部状态栏：仅在选择角色后显示 -->
    <transition name="el-zoom-in-top">
      <div v-if="currentPlayer" class="status-banner">
        <div class="status-item main">
          <span class="icon">{{ getClassIcon(currentPlayer.playerClass) }}</span>
          <div class="info">
            <span class="label">{{ getClassName(currentPlayer.playerClass) }}</span>
            <span class="value">Lv.{{ currentPlayer.level }}</span>
          </div>
          <el-button link type="primary" size="small" @click="resetPlayer" style="margin-left: 10px">重选</el-button>
        </div>
        
        <div v-if="currentSave" class="status-item">
          <span class="icon">📂</span>
          <div class="info">
            <span class="label">当前进度</span>
            <span class="value">第 {{ currentSave.floor }} 层</span>
          </div>
          <el-button link type="warning" size="small" @click="resetSave" style="margin-left: 10px">切换</el-button>
        </div>
      </div>
    </transition>

    <div class="content">
      <div class="step-container">
        <!-- 步骤1：选择角色 -->
        <transition name="fade-transform" mode="out-in">
          <el-card v-if="currentStep === 'player'" class="central-card" @click="goToPlayer" key="player">
            <div class="card-content">
              <div class="card-icon">🧙‍♂️</div>
              <h2>第一步：选择英雄</h2>
              <p>在开始冒险之前，请先选择或创建一个角色</p>
              <el-button type="primary" size="large" round>前往角色中心</el-button>
            </div>
          </el-card>

          <!-- 步骤2：选择存档 -->
          <el-card v-else-if="currentStep === 'save'" class="central-card save-card" @click="goToSave" key="save">
            <div class="card-content">
              <div class="card-icon">💾</div>
              <h2>第二步：加载进度</h2>
              <p>角色已准备就绪，现在请选择一个存档</p>
              <el-button type="warning" size="large" round>管理存档</el-button>
            </div>
          </el-card>

          <!-- 步骤3：进入战斗 -->
          <el-card v-else class="central-card battle-card" @click="goToBattle" key="battle">
            <div class="card-content">
              <div class="card-icon">⚔️</div>
              <h2>最后一步：踏入战场</h2>
              <p>万事俱备，勇敢的冒险者，出发吧！</p>
              <el-button type="danger" size="large" round>立即开始战斗</el-button>
            </div>
          </el-card>
        </transition>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.navbar h1 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

/* 顶部状态栏 */
.status-banner {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 10px 40px;
  display: flex;
  gap: 20px;
  border-bottom: 1px solid #eee;
  justify-content: center;
  flex-wrap: wrap;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  min-width: 150px;
}

.status-item .icon {
  font-size: 24px;
}

.status-item .info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.status-item .label {
  font-size: 11px;
  color: #999;
  text-transform: uppercase;
}

.status-item .value {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

/* 内容区域 */
.content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.step-container {
  width: 100%;
  max-width: 450px;
}

.central-card {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 20px;
  border: none;
  box-shadow: 0 15px 35px rgba(0,0,0,0.1);
}

.central-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0,0,0,0.15);
}

.card-content {
  padding: 40px 20px;
  text-align: center;
}

.card-icon {
  font-size: 80px;
  margin-bottom: 25px;
  filter: drop-shadow(0 5px 15px rgba(0,0,0,0.1));
}

.central-card h2 {
  margin-bottom: 15px;
  color: #2c3e50;
  font-size: 24px;
}

.central-card p {
  color: #7f8c8d;
  margin-bottom: 30px;
  font-size: 16px;
}

/* 各步骤主题色 */
.save-card {
  border-bottom: 5px solid #e6a23c;
}
.battle-card {
  border-bottom: 5px solid #f56c6c;
  background: linear-gradient(to bottom, #ffffff, #fff5f5);
}

/* 动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.5s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
