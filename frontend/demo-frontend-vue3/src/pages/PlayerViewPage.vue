<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { playerService, PlayerClass, PlayerShowResp } from '../api/userService'
import { gameContext } from '../api/gameContext'

interface PlayerInfo {
  level: number
  exp: number
  attackBase: number
  maxHp: number
  currentHp: number
  maxMp: number
  currentMp: number
  playerClass: string
  id?: number
}

// 职阶定义
const playerClasses = [
  {
    value: PlayerClass.SABER,
    name: '剑士',
    icon: '⚔️',
    desc: '近战输出专精，HP和攻击力成长较高',
    color: '#e74c3c'
  },
  {
    value: PlayerClass.ARCHER,
    name: '游侠',
    icon: '🏹',
    desc: '均衡型角色，成长均衡但无专精技能',
    color: '#27ae60'
  },
  {
    value: PlayerClass.CASTER,
    name: '魔法师',
    icon: '🔮',
    desc: '法力输出专精，MP成长较高但攻击力和HP成长低',
    color: '#9b59b6'
  }
]

const router = useRouter()
const loading = ref(true)
const playerList = ref<PlayerShowResp[]>([])
const playerInfo = ref<PlayerInfo | null>(null)
const viewState = ref<'list' | 'detail'>('list') // 'list' 显示列表，'detail' 显示详情
const selectedPlayerId = ref<number | null>(null)
const creating = ref(false)
const showClassDialog = ref(false)
const selectedClass = ref<typeof playerClasses[0] | null>(null)
const showDeleteConfirm = ref(false)
const deleteTargetPlayer = ref<PlayerShowResp | null>(null)
const deleting = ref(false)

const goHome = () => {
  router.push('/home')
}

// 加载所有角色列表
const loadPlayerList = async () => {
  loading.value = true
  try {
    const response = await playerService.getPlayerAll()
    if (response.data.success) {
      playerList.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '加载角色列表失败')
      playerList.value = []
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载角色列表失败')
    playerList.value = []
  } finally {
    loading.value = false
  }
}

// 选择一个角色，加载其详细信息
const selectPlayer = async (player: PlayerShowResp) => {
  try {
    // 将 playerId 放入 GameContext
    gameContext.setPlayerId(player.id)
    localStorage.setItem('playerId', String(player.id))
    selectedPlayerId.value = player.id

    // 调用 showbase 获取选中角色的详细信息
    const response = await playerService.getPlayerBaseInfo()
    if (response.data.success) {
      const data = response.data.data
      playerInfo.value = {
        level: data.level,
        exp: data.exp,
        attackBase: data.attackBase,
        maxHp: data.maxHp,
        currentHp: data.currentHp,
        maxMp: data.maxMp,
        currentMp: data.currentMp,
        playerClass: data.playerClass,
        id: data.id
      }
      viewState.value = 'detail'
    } else {
      ElMessage.error(response.data.message || '加载角色详情失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载角色详情失败')
  }
}

// 返回到角色列表
const backToList = () => {
  viewState.value = 'list'
  selectedPlayerId.value = null
}

const openClassDialog = () => {
  selectedClass.value = null
  showClassDialog.value = true
}

const handleCreatePlayer = async () => {
  if (!selectedClass.value) {
    ElMessage.warning('请选择一个职阶')
    return
  }
  
  creating.value = true
  try {
    const response = await playerService.createPlayer(selectedClass.value.value)
    if (response.data.success) {
      ElMessage.success(`${selectedClass.value.icon} ${selectedClass.value.name}创建成功！`)
      showClassDialog.value = false
      // 重新加载角色列表
      await loadPlayerList()
    } else {
      ElMessage.error(response.data.message || '角色创建失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '角色创建失败，请重试')
  } finally {
    creating.value = false
  }
}

// 打开删除确认对话框
const openDeleteConfirm = (player: PlayerShowResp, e: Event) => {
  e.stopPropagation()
  deleteTargetPlayer.value = player
  showDeleteConfirm.value = true
}

// 确认删除角色
const confirmDeletePlayer = async () => {
  if (!deleteTargetPlayer.value) return
  
  deleting.value = true
  try {
    const response = await playerService.deletePlayer(deleteTargetPlayer.value.id)
    if (response.data.success) {
      ElMessage.success(`${getClassName(deleteTargetPlayer.value.playerClass)}已删除`)
      showDeleteConfirm.value = false
      deleteTargetPlayer.value = null
      // 重新加载角色列表
      await loadPlayerList()
    } else {
      ElMessage.error(response.data.message || '删除角色失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '删除角色失败，请重试')
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  loadPlayerList()
})

const hpPercentage = () => {
  if (!playerInfo.value) return 0
  return Math.round((playerInfo.value.currentHp / playerInfo.value.maxHp) * 100)
}

const mpPercentage = () => {
  if (!playerInfo.value) return 0
  return Math.round((playerInfo.value.currentMp / playerInfo.value.maxMp) * 100)
}

// 获取职阶名称
const getClassName = (playerClass: string) => {
  const cls = playerClasses.find(c => c.value === playerClass)
  return cls ? cls.name : playerClass
}

// 获取职阶图标
const getClassIcon = (playerClass: string) => {
  const cls = playerClasses.find(c => c.value === playerClass)
  return cls ? cls.icon : '❓'
}

// 获取职阶颜色
const getClassColor = (playerClass: string) => {
  const cls = playerClasses.find(c => c.value === playerClass)
  return cls ? cls.color : '#999'
}
</script>

<template>
  <div class="player-view-container">
    <el-button class="back-button" circle type="default" @click="goHome">❮</el-button>

    <div class="player-header">
      <h1>{{ viewState === 'list' ? '角色选择' : '角色信息' }}</h1>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 角色列表视图 -->
    <div v-else-if="viewState === 'list'" class="player-list-container">
      <!-- 创建新角色按钮 -->
      <el-button
        type="primary"
        @click="openClassDialog"
        :loading="creating"
        class="create-new-button"
      >
        ✨ 创建新角色
      </el-button>

      <!-- 有角色的情况 -->
      <div v-if="playerList.length > 0" class="player-cards-grid">
        <div
          v-for="player in playerList"
          :key="player.id"
          class="player-card-item"
          :style="{ '--class-color': getClassColor(player.playerClass) }"
          @click="selectPlayer(player)"
        >
          <div class="card-icon">{{ getClassIcon(player.playerClass) }}</div>
          <div class="card-name">{{ getClassName(player.playerClass) }}</div>
          <div class="card-level">Lv.{{ player.level }}</div>
          <div class="card-exp">EXP: {{ player.exp }}</div>
          <div class="card-hp">❤️ {{ player.currentHp }}/{{ player.maxHp }}</div>
          <div class="card-mp">💙 {{ player.currentMp }}/{{ player.maxMp }}</div>
          <div class="card-arrow">→</div>
          <el-button
            type="danger"
            size="small"
            circle
            plain
            class="delete-button"
            @click="openDeleteConfirm(player, $event)"
          >
            🗑
          </el-button>
        </div>
      </div>

      <!-- 没有角色的情况 -->
      <div v-else class="create-player-state">
        <div class="create-prompt">
          <div class="game-icon">🎮</div>
          <h2>冒险等待中...</h2>
          <p>您还没有创建角色，让我们开始一场新的冒险吧！</p>
          <el-button
            type="primary"
            size="large"
            @click="openClassDialog"
            :loading="creating"
            class="create-button"
          >
            ✨ 创建角色
          </el-button>
        </div>
      </div>
    </div>

    <!-- 角色详情视图 -->
    <div v-else-if="viewState === 'detail' && playerInfo" class="player-card-container">
      <el-button class="back-to-list-button" @click="backToList">← 返回列表</el-button>
      
      <el-card class="player-card">
        <template #header>
          <div class="card-header">
            <span>{{ getClassName(playerInfo.playerClass) }} Lv.{{ playerInfo.level }}</span>
            <span class="class-badge" :style="{ backgroundColor: getClassColor(playerInfo.playerClass) }">
              {{ getClassIcon(playerInfo.playerClass) }}
            </span>
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

    <!-- 职阶选择对话框 -->
    <el-dialog
      v-model="showClassDialog"
      title="选择你的职阶"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="class-selection">
        <div
          v-for="cls in playerClasses"
          :key="cls.value"
          class="class-card"
          :class="{ selected: selectedClass?.value === cls.value }"
          :style="{ '--class-color': cls.color }"
          @click="selectedClass = cls"
        >
          <div class="class-icon">{{ cls.icon }}</div>
          <div class="class-name">{{ cls.name }}</div>
          <div class="class-desc">{{ cls.desc }}</div>
          <div v-if="selectedClass?.value === cls.value" class="selected-badge">已选择</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showClassDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreatePlayer" :loading="creating" :disabled="!selectedClass">
          确认创建
        </el-button>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="showDeleteConfirm"
      title="删除角色"
      width="400px"
      :close-on-click-modal="false"
    >
      <div v-if="deleteTargetPlayer" class="delete-confirm-content">
        <div class="warning-icon">⚠️</div>
        <p>确定要删除 <strong>{{ getClassName(deleteTargetPlayer.playerClass) }} Lv.{{ deleteTargetPlayer.level }}</strong> 吗？</p>
        <p class="warning-text">此操作不可撤销，所有相关数据将被永久删除。</p>
      </div>
      <template #footer>
        <el-button @click="showDeleteConfirm = false">取消</el-button>
        <el-button type="danger" @click="confirmDeletePlayer" :loading="deleting">
          确认删除
        </el-button>
      </template>
    </el-dialog>
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

.back-to-list-button {
  margin-bottom: 20px;
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
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
}

/* 角色列表容器 */
.player-list-container {
  max-width: 1000px;
  margin: 0 auto;
  width: 100%;
}

.player-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.player-card-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid var(--class-color);
  position: relative;
  overflow: hidden;
}

.delete-button {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}

.player-card-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0) 0%, rgba(102, 126, 234, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.player-card-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.player-card-item:hover::before {
  opacity: 1;
}

.player-card-item:hover .delete-button {
  opacity: 1;
}

.card-icon {
  font-size: 48px;
  margin-bottom: 12px;
  text-align: center;
}

.card-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  text-align: center;
}

.card-level {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-bottom: 8px;
}

.card-exp {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin-bottom: 8px;
}

.card-hp {
  font-size: 13px;
  color: #e74c3c;
  margin-bottom: 4px;
}

.card-mp {
  font-size: 13px;
  color: #409eff;
  margin-bottom: 12px;
}

.card-arrow {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  font-size: 24px;
  color: var(--class-color);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.player-card-item:hover .card-arrow {
  opacity: 1;
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

/* 创建新角色按钮样式 */
.create-new-button {
  margin-bottom: 20px;
  width: 200px;
  height: 50px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.create-new-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
}

/* 职阶选择 */
.class-selection {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.class-card {
  flex: 1;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 2px solid #dee2e6;
  border-radius: 12px;
  padding: 20px 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.class-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--class-color);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.class-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  border-color: var(--class-color);
}

.class-card:hover::before {
  opacity: 1;
}

.class-card.selected {
  border-color: var(--class-color);
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.class-card.selected::before {
  opacity: 1;
}

.class-icon {
  font-size: 48px;
  margin-bottom: 10px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.class-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.class-desc {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.selected-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: var(--class-color);
  color: white;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: bold;
}

/* 删除确认对话框 */
.delete-confirm-content {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  font-size: 48px;
  margin-bottom: 16px;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-8px);
  }
  75% {
    transform: translateX(8px);
  }
}

.delete-confirm-content p {
  margin: 12px 0;
  font-size: 14px;
  color: #333;
}

.delete-confirm-content p strong {
  color: #e74c3c;
}

.warning-text {
  color: #999;
  font-size: 12px !important;
}

.class-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 16px;
}
</style>
