<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveService, type SaveInfo } from '../api/userService'
import { gameContext } from '../api/gameContext'
import { Plus, Download, Upload, Delete } from '@element-plus/icons-vue'

const router = useRouter()

// 状态
const loading = ref(false)
const saves = ref<SaveInfo[]>([])
const currentSave = ref<SaveInfo | null>(null)
const createLoading = ref(false)

// 返回首页
const goHome = () => {
  router.push('/home')
}

// 获取所有存档
const loadSaves = async () => {
  loading.value = true
  try {
    // 先获取所有存档列表
    const allSavesRes = await saveService.getAllSaves()
    
    if (allSavesRes.data.success) {
      saves.value = allSavesRes.data.data || []
    }
    
    // 如果有存档，再获取当前存档
    if (saves.value.length > 0) {
      const currentSaveRes = await saveService.getCurrentSave().catch(() => null)
      if (currentSaveRes?.data?.success) {
        currentSave.value = currentSaveRes.data.data
        // 更新游戏上下文
        if (currentSaveRes.data.data) {
          gameContext.setSaveId(currentSaveRes.data.data.id)
          gameContext.setPlayerId(currentSaveRes.data.data.playerId)
        }
      }
    } else {
      currentSave.value = null
      gameContext.setSaveId(null as any)
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载存档失败')
  } finally {
    loading.value = false
  }
}

// 创建新存档
const createSave = async () => {
  // 确保 playerId 在上下文中
  const playerId = gameContext.getPlayerId()
  if (!playerId) {
    ElMessage.warning('请先选择角色')
    router.push('/player')
    return
  }
  
  createLoading.value = true
  try {
    const response = await saveService.createDefaultSave()
    if (response.data.success) {
      ElMessage.success('创建存档成功')
      await loadSaves()
    } else {
      ElMessage.error(response.data.message || '创建存档失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '创建存档失败')
  } finally {
    createLoading.value = false
  }
}

// 加载存档
const loadSave = async (save: SaveInfo) => {
  try {
    await ElMessageBox.confirm(
      `确定要加载存档「${save.id}」吗？当前进度将丢失。`,
      '确认加载',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const response = await saveService.loadSave(save)
    if (response.data.success) {
      ElMessage.success('加载存档成功')
      currentSave.value = save
      // 更新游戏上下文
      gameContext.setSaveId(save.id)
      gameContext.setPlayerId(save.playerId)
    } else {
      ElMessage.error(response.data.message || '加载存档失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '加载存档失败')
    }
  }
}

// 保存当前进度
const saveCurrent = async () => {
  if (!currentSave.value) {
    ElMessage.warning('当前没有可保存的存档')
    return
  }
  
  try {
    const response = await saveService.saveSave(currentSave.value)
    if (response.data.success) {
      ElMessage.success('保存成功')
      await loadSaves()
    } else {
      ElMessage.error(response.data.message || '保存失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 获取进度文本
const getProgressText = (save: SaveInfo) => {
  return `第 ${save.floor} 层 - 进度 ${save.progress}%`
}

onMounted(() => {
  loadSaves()
})
</script>

<template>
  <div class="save-container">
    <el-button class="back-button" circle type="default" @click="goHome">❮</el-button>

    <div class="save-header">
      <h1>存档管理</h1>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" :icon="Plus" @click="createSave" :loading="createLoading">
        新建存档
      </el-button>
      <el-button type="success" :icon="Upload" @click="saveCurrent" :disabled="!currentSave">
        保存当前进度
      </el-button>
    </div>

    <!-- 当前存档信息 -->
    <div v-if="currentSave" class="current-save-section">
      <h2>当前进度</h2>
      <el-card class="current-save-card">
        <div class="save-info-grid">
          <div class="info-item">
            <span class="label">存档ID</span>
            <span class="value">#{{ currentSave.id }}</span>
          </div>
          <div class="info-item">
            <span class="label">角色等级</span>
            <span class="value">Lv.{{ currentSave.level }}</span>
          </div>
          <div class="info-item">
            <span class="label">经验值</span>
            <span class="value">{{ currentSave.exp }} EXP</span>
          </div>
          <div class="info-item">
            <span class="label">当前层数</span>
            <span class="value">第 {{ currentSave.floor }} 层</span>
          </div>
          <div class="info-item">
            <span class="label">生命值</span>
            <span class="value">{{ currentSave.currentHp }} HP</span>
          </div>
          <div class="info-item">
            <span class="label">魔法值</span>
            <span class="value">{{ currentSave.currentMp }} MP</span>
          </div>
          <div class="info-item">
            <span class="label">战斗序号</span>
            <span class="value">{{ currentSave.battleOrder }}</span>
          </div>
          <div class="info-item">
            <span class="label">进度</span>
            <span class="value">{{ currentSave.progress }}%</span>
          </div>
        </div>
        <el-divider />
        <div class="save-time">
          <span>创建时间: {{ formatDate(currentSave.createTime) }}</span>
          <span>更新时间: {{ formatDate(currentSave.updateTime) }}</span>
        </div>
      </el-card>
    </div>

    <!-- 存档列表 -->
    <div class="saves-section">
      <h2>存档列表</h2>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 空状态 -->
      <div v-else-if="saves.length === 0" class="empty-state">
        <el-empty description="暂无存档">
          <el-button type="primary" @click="createSave" :loading="createLoading">
            创建第一个存档
          </el-button>
        </el-empty>
      </div>

      <!-- 存档列表 -->
      <div v-else class="saves-list">
        <el-card
          v-for="save in saves"
          :key="save.id"
          class="save-card"
          :class="{ 'current': currentSave?.id === save.id }"
        >
          <div class="save-card-header">
            <div class="save-id">存档 #{{ save.id }}</div>
            <el-tag v-if="currentSave?.id === save.id" type="success" effect="dark">
              当前
            </el-tag>
          </div>
          
          <el-divider />
          
          <div class="save-stats">
            <div class="stat">
              <span class="stat-label">等级</span>
              <span class="stat-value">Lv.{{ save.level }}</span>
            </div>
            <div class="stat">
              <span class="stat-label">层数</span>
              <span class="stat-value">{{ save.floor }}F</span>
            </div>
            <div class="stat">
              <span class="stat-label">HP</span>
              <span class="stat-value">{{ save.currentHp }}</span>
            </div>
            <div class="stat">
              <span class="stat-label">MP</span>
              <span class="stat-value">{{ save.currentMp }}</span>
            </div>
          </div>
          
          <div class="save-progress">
            <span class="progress-label">进度</span>
            <el-progress :percentage="save.progress" :stroke-width="8" />
          </div>
          
          <div class="save-time-info">
            <span>更新于 {{ formatDate(save.updateTime) }}</span>
          </div>
          
          <div class="save-actions">
            <el-button
              type="primary"
              :icon="Download"
              @click="loadSave(save)"
              :disabled="currentSave?.id === save.id"
            >
              加载
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.save-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
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

.save-header {
  text-align: center;
  color: white;
  margin-bottom: 30px;
}

.save-header h1 {
  font-size: 32px;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

/* 操作栏 */
.action-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  justify-content: center;
}

/* 当前存档 */
.current-save-section {
  margin-bottom: 30px;
}

.current-save-section h2 {
  color: white;
  font-size: 20px;
  margin-bottom: 15px;
  padding-left: 10px;
  border-left: 4px solid #67c23a;
}

.current-save-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
}

.save-info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  color: #888;
  font-size: 14px;
}

.info-item .value {
  color: #333;
  font-size: 18px;
  font-weight: bold;
}

.save-time {
  display: flex;
  justify-content: space-between;
  color: #888;
  font-size: 13px;
}

/* 存档列表 */
.saves-section h2 {
  color: white;
  font-size: 20px;
  margin-bottom: 15px;
  padding-left: 10px;
  border-left: 4px solid #409eff;
}

.loading-state {
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;
  border-radius: 12px;
}

.empty-state {
  background: rgba(255, 255, 255, 0.95);
  padding: 40px;
  border-radius: 12px;
}

.saves-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.save-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.save-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.save-card.current {
  border: 2px solid #67c23a;
}

.save-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.save-id {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.save-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 15px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
}

.stat-label {
  font-size: 12px;
  color: #888;
}

.stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.save-progress {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.progress-label {
  font-size: 12px;
  color: #888;
  white-space: nowrap;
}

.save-time-info {
  font-size: 12px;
  color: #888;
  margin-bottom: 15px;
}

.save-actions {
  display: flex;
  justify-content: flex-end;
}

/* 响应式 */
@media (max-width: 768px) {
  .save-info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .saves-list {
    grid-template-columns: 1fr;
  }
  
  .action-bar {
    flex-direction: column;
  }
}
</style>
