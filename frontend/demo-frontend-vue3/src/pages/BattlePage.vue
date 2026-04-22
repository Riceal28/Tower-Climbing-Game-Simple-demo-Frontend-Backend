<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { battleService, type BattleResp } from '../api/userService'
import { gameContext } from '../api/gameContext'

const router = useRouter()

// 战斗状态
const loading = ref(false)
const battleData = ref<BattleResp | null>(null)
const battleLogs = ref<string[]>([])
const actionLoading = ref(false)

// 计算属性：玩家HP百分比
const playerHpPercent = computed(() => {
  if (!battleData.value?.battleInfo) return 0
  const info = battleData.value.battleInfo
  // 这里需要最大HP，从其他接口获取或使用默认值
  return Math.max(0, Math.min(100, (info.playerCurrentHp / 100) * 100))
})

// 计算属性：魔物HP百分比
const monsterHpPercent = computed(() => {
  if (!battleData.value?.battleInfo || !battleData.value?.monsterInfo) return 0
  const current = battleData.value.battleInfo.monsterCurrentHp
  const max = battleData.value.monsterInfo.hp
  return Math.max(0, Math.min(100, (current / max) * 100))
})

// 计算属性：玩家MP百分比
const playerMpPercent = computed(() => {
  if (!battleData.value?.battleInfo) return 0
  const info = battleData.value.battleInfo
  return Math.max(0, Math.min(100, (info.playerCurrentMp / 100) * 100))
})

// 计算属性：魔物MP百分比
const monsterMpPercent = computed(() => {
  if (!battleData.value?.battleInfo || !battleData.value?.monsterInfo) return 0
  const current = battleData.value.battleInfo.monsterCurrentMp
  const max = battleData.value.monsterInfo.mp
  return Math.max(0, Math.min(100, (current / max) * 100))
})

// 返回首页
const goHome = () => {
  router.push('/home')
}

// 开始战斗
const startBattle = async () => {
  loading.value = true
  try {
    const response = await battleService.startBattle()
    if (response.data.success) {
      battleData.value = response.data.data
      battleLogs.value = []
      if (response.data.data.log) {
        battleLogs.value.push(response.data.data.log)
      }
      // 设置 battleId 到游戏上下文
      if (response.data.data.battleInfo?.id) {
        gameContext.setBattleId(response.data.data.battleInfo.id)
      }
      ElMessage.success('战斗开始！')
    } else {
      ElMessage.error(response.data.message || '开始战斗失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '开始战斗失败')
  } finally {
    loading.value = false
  }
}

// 获取战斗状态
const getBattleStatus = async () => {
  try {
    const response = await battleService.getBattleStatus()
    if (response.data.success) {
      battleData.value = response.data.data
      if (response.data.data.log) {
        battleLogs.value.push(response.data.data.log)
      }
    }
  } catch (error: any) {
    console.error('获取战斗状态失败:', error)
  }
}

// 执行动作
const executeAction = async (actionId: number) => {
  if (actionLoading.value) return
  actionLoading.value = true
  try {
    const response = await battleService.executeAction(actionId)
    if (response.data.success) {
      battleData.value = response.data.data
      if (response.data.data.log) {
        battleLogs.value.push(response.data.data.log)
      }
      checkBattleResult()
    } else {
      ElMessage.error(response.data.message || '执行动作失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '执行动作失败')
  } finally {
    actionLoading.value = false
  }
}

// 结束回合
const endRound = async () => {
  if (actionLoading.value) return
  actionLoading.value = true
  try {
    const response = await battleService.endRound()
    if (response.data.success) {
      battleData.value = response.data.data
      if (response.data.data.log) {
        battleLogs.value.push(response.data.data.log)
      }
      checkBattleResult()
    } else {
      ElMessage.error(response.data.message || '结束回合失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '结束回合失败')
  } finally {
    actionLoading.value = false
  }
}

// 检查战斗结果
const checkBattleResult = () => {
  if (!battleData.value?.result) return
  
  const result = battleData.value.result
  // 清空战斗上下文
  gameContext.clear()
  
  if (result === 'WIN') {
    ElMessageBox.alert('恭喜你获得了胜利！', '战斗结束', {
      confirmButtonText: '确定',
      type: 'success',
    })
  } else if (result === 'LOSE') {
    ElMessageBox.alert('战斗失败，请再接再厉！', '战斗结束', {
      confirmButtonText: '确定',
      type: 'error',
    })
  }
}

// 模拟动作列表（实际应从后端获取）
const actions = ref([
  { id: 1, name: '普通攻击', icon: '⚔️', mpCost: 0 },
  { id: 2, name: '强力一击', icon: '💥', mpCost: 10 },
  { id: 3, name: '治疗术', icon: '💚', mpCost: 15 },
  { id: 4, name: '防御姿态', icon: '🛡️', mpCost: 5 },
])

onMounted(() => {
  startBattle()
})
</script>

<template>
  <div class="battle-container">
    <el-button class="back-button" circle type="default" @click="goHome">❮</el-button>

    <div class="battle-header">
      <h1>战斗</h1>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading && !battleData" class="loading-state">
      <el-skeleton :rows="8" animated />
    </div>

    <!-- 战斗主区域 -->
    <div v-else-if="battleData" class="battle-main">
      <!-- 战斗场景 -->
      <div class="battle-scene">
        <!-- 玩家方 -->
        <div class="combatant player-side">
          <div class="avatar">🧙‍♂️</div>
          <div class="name">冒险者</div>
          <div class="hp-bar">
            <el-progress 
              :percentage="playerHpPercent" 
              :color="'#67c23a'"
              :stroke-width="16"
              :show-text="false"
            />
            <span class="hp-text">{{ battleData.battleInfo.playerCurrentHp }} HP</span>
          </div>
          <div class="mp-bar">
            <el-progress 
              :percentage="playerMpPercent" 
              :color="'#409eff'"
              :stroke-width="12"
              :show-text="false"
            />
            <span class="mp-text">{{ battleData.battleInfo.playerCurrentMp }} MP</span>
          </div>
          <div class="defend-info">
            <span class="defend-badge">🛡️ {{ battleData.battleInfo.playerCurrentDefend }}</span>
          </div>
        </div>

        <!-- VS 标志 -->
        <div class="vs-badge">VS</div>

        <!-- 魔物方 -->
        <div class="combatant monster-side">
          <div class="avatar">👹</div>
          <div class="name">{{ battleData.monsterInfo.monsterName }}</div>
          <div class="hp-bar">
            <el-progress 
              :percentage="monsterHpPercent" 
              :color="'#f56c6c'"
              :stroke-width="16"
              :show-text="false"
            />
            <span class="hp-text">{{ battleData.battleInfo.monsterCurrentHp }}/{{ battleData.monsterInfo.hp }} HP</span>
          </div>
          <div class="mp-bar">
            <el-progress 
              :percentage="monsterMpPercent" 
              :color="'#9b59b6'"
              :stroke-width="12"
              :show-text="false"
            />
            <span class="mp-text">{{ battleData.battleInfo.monsterCurrentMp }}/{{ battleData.monsterInfo.mp }} MP</span>
          </div>
          <div class="defend-info">
            <span class="defend-badge">🛡️ {{ battleData.battleInfo.monsterCurrentDefend }}</span>
          </div>
        </div>
      </div>

      <!-- 战斗日志 -->
      <div class="battle-logs">
        <h3>战斗记录</h3>
        <div class="logs-container">
          <div 
            v-for="(log, index) in battleLogs" 
            :key="index" 
            class="log-item"
            :class="{ 'new': index === battleLogs.length - 1 }"
          >
            {{ log }}
          </div>
          <div v-if="battleLogs.length === 0" class="empty-log">
            战斗开始...
          </div>
        </div>
      </div>

      <!-- 动作按钮 -->
      <div class="action-panel">
        <div class="actions-grid">
          <el-button
            v-for="action in actions"
            :key="action.id"
            class="action-btn"
            :type="action.mpCost > (battleData?.battleInfo?.playerCurrentMp || 0) ? 'info' : 'primary'"
            :disabled="action.mpCost > (battleData?.battleInfo?.playerCurrentMp || 0) || actionLoading || battleData?.result !== null"
            @click="executeAction(action.id)"
            :loading="actionLoading"
          >
            <span class="action-icon">{{ action.icon }}</span>
            <span class="action-name">{{ action.name }}</span>
            <span class="action-cost" v-if="action.mpCost > 0">{{ action.mpCost }} MP</span>
          </el-button>
        </div>
        <el-button
          class="end-round-btn"
          type="warning"
          size="large"
          :disabled="actionLoading || battleData?.result !== null"
          @click="endRound"
          :loading="actionLoading"
        >
          结束回合
        </el-button>
      </div>

      <!-- 战斗结果 -->
      <div v-if="battleData?.result" class="battle-result">
        <el-result
          :icon="battleData.result === 'WIN' ? 'success' : 'error'"
          :title="battleData.result === 'WIN' ? '胜利！' : '失败！'"
          :sub-title="battleData.result === 'WIN' ? `获得 ${battleData.monsterInfo.gainExp} 经验值` : '请再接再厉'"
        >
          <template #extra>
            <el-button type="primary" @click="startBattle">再次挑战</el-button>
            <el-button @click="goHome">返回首页</el-button>
          </template>
        </el-result>
      </div>
    </div>

    <!-- 无战斗数据 -->
    <div v-else class="no-battle">
      <div class="no-battle-content">
        <div class="game-icon">⚔️</div>
        <h2>暂无战斗</h2>
        <p>点击开始一场新的战斗吧！</p>
        <el-button type="primary" size="large" @click="startBattle" :loading="loading">
          开始战斗
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.battle-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
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
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  border: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  font-size: 24px;
  line-height: 1;
}

.back-button:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.battle-header {
  text-align: center;
  color: white;
  margin-bottom: 20px;
}

.battle-header h1 {
  font-size: 32px;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.loading-state {
  max-width: 600px;
  margin: 0 auto;
  width: 100%;
}

.battle-main {
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 战斗场景 */
.battle-scene {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  padding: 30px;
  position: relative;
}

.combatant {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.avatar {
  font-size: 80px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
}

.name {
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.hp-bar, .mp-bar {
  width: 100%;
  max-width: 200px;
  position: relative;
}

.hp-text, .mp-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 12px;
  font-weight: bold;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.defend-info {
  margin-top: 5px;
}

.defend-badge {
  background: rgba(255, 255, 255, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  color: white;
  font-size: 14px;
}

.vs-badge {
  font-size: 36px;
  font-weight: bold;
  color: #f39c12;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  padding: 0 20px;
}

/* 战斗日志 */
.battle-logs {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 12px;
  padding: 15px;
}

.battle-logs h3 {
  color: white;
  margin: 0 0 10px 0;
  font-size: 16px;
}

.logs-container {
  max-height: 150px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.log-item {
  color: #ddd;
  font-size: 14px;
  padding: 5px 10px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
  animation: fadeIn 0.3s ease;
}

.log-item.new {
  background: rgba(243, 156, 18, 0.2);
  color: #f39c12;
}

.empty-log {
  color: #888;
  text-align: center;
  padding: 20px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 动作面板 */
.action-panel {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 20px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 15px;
}

.action-btn {
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 14px;
}

.action-icon {
  font-size: 28px;
}

.action-name {
  font-weight: 500;
}

.action-cost {
  font-size: 12px;
  color: #888;
}

.end-round-btn {
  width: 100%;
  font-size: 16px;
  font-weight: bold;
}

/* 战斗结果 */
.battle-result {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

/* 无战斗状态 */
.no-battle {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
}

.no-battle-content {
  text-align: center;
  background: rgba(255, 255, 255, 0.05);
  padding: 60px 40px;
  border-radius: 16px;
}

.game-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.no-battle-content h2 {
  color: white;
  margin: 0 0 10px 0;
}

.no-battle-content p {
  color: #888;
  margin: 0 0 30px 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .battle-scene {
    flex-direction: column;
    gap: 20px;
  }

  .vs-badge {
    transform: rotate(90deg);
  }

  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
