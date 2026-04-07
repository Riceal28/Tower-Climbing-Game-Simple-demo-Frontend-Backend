<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { playerService } from '../api/userService'

interface PlayerInfo {
  level: number
  exp: number
  attackBase: number
  maxHp: number
  currentHp: number
  maxMp: number
  currentMp: number
}

const router = useRouter()
const loading = ref(true)
const playerInfo = ref<PlayerInfo | null>(null)
const playerExists = ref(false)
const creating = ref(false)

const goHome = () => {
  router.push('/home')
}

const loadPlayerInfo = async () => {
  loading.value = true
  try {
    const response = await playerService.getPlayerInfo()
    if (response.data.success) {
      playerInfo.value = response.data.data
      playerExists.value = true
    } else {
      playerInfo.value = null
      playerExists.value = false
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载玩家信息失败')
    playerExists.value = false
  } finally {
    loading.value = false
  }
}

const handleCreatePlayer = async () => {
  creating.value = true
  try {
    const response = await playerService.createPlayer()
    if (response.data.success) {
      ElMessage.success('角色创建成功！')
      // 重新加载玩家信息
      await loadPlayerInfo()
    } else {
      ElMessage.error(response.data.message || '角色创建失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '角色创建失败，请重试')
  } finally {
    creating.value = false
  }
}

onMounted(() => {
  loadPlayerInfo()
})

const hpPercentage = () => {
  if (!playerInfo.value) return 0
  return Math.round((playerInfo.value.currentHp / playerInfo.value.maxHp) * 100)
}

const mpPercentage = () => {
  if (!playerInfo.value) return 0
  return Math.round((playerInfo.value.currentMp / playerInfo.value.maxMp) * 100)
}
</script>

<template>
  <div class="player-view-container">
    <el-button class="back-button" circle type="default" @click="goHome">❮</el-button>

    <div class="player-header">
      <h1>角色信息</h1>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 角色存在 -->
    <div v-else-if="playerExists && playerInfo" class="player-card-container">
      <el-card class="player-card">
        <template #header>
          <div class="card-header">
            <span>等级 {{ playerInfo.level }}</span>
          </div>
        </template>

        <!-- 等级和经验 -->
        <div class="player-section">
          <div class="stat-row">
            <span class="stat-label">经验值</span>
            <span class="stat-value">{{ playerInfo.exp }} EXP</span>
          </div>
        </div>

        <!-- 基础属性 -->
        <el-divider />
        <div class="player-section">
          <h3>基础属性</h3>
          <div class="stat-row">
            <span class="stat-label">攻击力</span>
            <span class="stat-value">{{ playerInfo.attackBase }}</span>
          </div>
        </div>

        <!-- 生命信息 -->
        <el-divider />
        <div class="player-section">
          <h3>生命</h3>
          <div class="stat-row">
            <span class="stat-label">生命值</span>
            <span class="stat-value">{{ playerInfo.currentHp }} / {{ playerInfo.maxHp }}</span>
          </div>
          <el-progress :percentage="hpPercentage()" color="#f56c6c" style="margin-top: 8px" />

          <div class="stat-row" style="margin-top: 16px">
            <span class="stat-label">魔法值</span>
            <span class="stat-value">{{ playerInfo.currentMp }} / {{ playerInfo.maxMp }}</span>
          </div>
          <el-progress :percentage="mpPercentage()" color="#409eff" style="margin-top: 8px" />
        </div>
      </el-card>
    </div>

    <!-- 角色不存在 -->
    <div v-else class="create-player-state">
      <div class="create-prompt">
        <div class="game-icon">🎮</div>
        <h2>冒险等待中...</h2>
        <p>您还没有创建角色，让我们开始一场新的冒险吧！</p>
        <el-button
          type="primary"
          size="large"
          @click="handleCreatePlayer"
          :loading="creating"
          class="create-button"
        >
          ✨ 创建角色
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.player-view-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.back-button {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
  width: 50px;
  height: 50px;
  min-width: 50px;
  background-color: #f2f2f2;
  color: #4a4a4a;
  border: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
  font-size: 24px;
  line-height: 1;
}

.back-button:hover {
  background-color: #e6e6e6;
}

.player-header {
  text-align: center;
  color: white;
  margin-bottom: 30px;
}

.player-header h1 {
  font-size: 32px;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.loading-state {
  max-width: 500px;
  margin: 0 auto;
  width: 100%;
}

.player-card-container {
  max-width: 500px;
  margin: 0 auto;
  width: 100%;
}

.player-card {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 18px;
  color: #333;
}

.player-section {
  margin: 12px 0;
}

.player-section h3 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
}

.stat-label {
  color: #666;
  font-weight: 500;
}

.stat-value {
  color: #333;
  font-weight: bold;
  font-size: 16px;
}

.create-player-state {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  min-height: 60vh;
}

.create-prompt {
  text-align: center;
  background: white;
  padding: 60px 40px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  max-width: 400px;
  animation: scaleIn 0.5s ease-out;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.game-icon {
  font-size: 64px;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.create-prompt h2 {
  margin: 20px 0 10px 0;
  color: #333;
  font-size: 24px;
}

.create-prompt p {
  margin: 0 0 30px 0;
  color: #999;
  font-size: 14px;
  line-height: 1.6;
}

.create-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 1px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.create-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
}
</style>
