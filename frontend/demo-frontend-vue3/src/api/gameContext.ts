// 游戏上下文 - 管理当前用户的 userId、playerId、saveId、battleId
import { reactive } from 'vue'

export interface GameContextState {
  userId: number | null
  playerId: number | null
  saveId: number | null
  battleId: number | null
}

class GameContext {
  private state = reactive<GameContextState>({
    userId: null,
    playerId: null,
    saveId: null,
    battleId: null,
  })

  // 初始化上下文
  init(userId: number, playerId?: number, saveId?: number, battleId?: number): void {
    this.state.userId = userId
    this.state.playerId = playerId ?? null
    this.state.saveId = saveId ?? null
    this.state.battleId = battleId ?? null
    console.log('GameContext initialized:', this.state)
  }

  // 获取当前上下文
  getState(): GameContextState {
    return { ...this.state }
  }

  // 获取 userId
  getUserId(): number | null {
    return this.state.userId
  }

  // 获取 playerId
  getPlayerId(): number | null {
    return this.state.playerId
  }

  // 获取 saveId
  getSaveId(): number | null {
    return this.state.saveId
  }

  // 获取 battleId
  getBattleId(): number | null {
    return this.state.battleId
  }

  // 设置 playerId
  setPlayerId(playerId: number): void {
    this.state.playerId = playerId
  }

  // 设置 saveId
  setSaveId(saveId: number): void {
    this.state.saveId = saveId
  }

  // 设置 battleId
  setBattleId(battleId: number): void {
    this.state.battleId = battleId
  }

  // 清空上下文
  clear(): void {
    this.state.userId = null
    this.state.playerId = null
    this.state.saveId = null
    this.state.battleId = null
  }

  // 检查是否已初始化
  isInitialized(): boolean {
    return this.state.userId !== null
  }

  // 获取用于 axios header 的对象
  toHeaders(): Record<string, string> {
    const headers: Record<string, string> = {}
    if (this.state.userId !== null) {
      headers['X-User-Id'] = String(this.state.userId)
    }
    if (this.state.playerId !== null) {
      headers['X-Player-Id'] = String(this.state.playerId)
    }
    if (this.state.saveId !== null) {
      headers['X-Save-Id'] = String(this.state.saveId)
    }
    if (this.state.battleId !== null) {
      headers['X-Battle-Id'] = String(this.state.battleId)
    }
    return headers
  }
}

// 导出单例
export const gameContext = new GameContext()
