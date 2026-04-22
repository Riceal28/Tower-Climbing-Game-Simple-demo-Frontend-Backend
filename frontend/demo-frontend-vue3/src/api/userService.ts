import axios from 'axios'
import { gameContext } from './gameContext'

const API_BASE_URL = 'http://localhost:8080/api'

// ==================== 请求/响应接口定义 ====================

interface LoginRequest {
  username: string
  password: string
}

interface RegisterRequest {
  username: string
  email: string
  password: string
}

interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  success: boolean
}

// 玩家职业枚举
export enum PlayerClass {
  SABER = 'SABER',
  ARCHER = 'ARCHER',
  CASTER = 'CASTER'
}

// 玩家信息接口
export interface PlayerInfo {
  playerClass: PlayerClass
  level: number
  exp: number
  attackBase: number
  maxHp: number
  maxMp: number
  currentHp?: number
  currentMp?: number
}

// 玩家展示响应接口
export interface PlayerShowResp {
  id: number
  playerClass: string
  level: number
  exp: number
  attackBase: number
  maxHp: number
  maxMp: number
  currentHp: number
  currentMp: number
}

// 存档信息接口
export interface SaveInfo {
  id: number
  userId: number
  playerId: number
  level: number
  exp: number
  currentHp: number
  currentMp: number
  floor: number
  battleOrder: number
  progress: number
  createTime: string
  updateTime: string
}

// 战斗信息接口
export interface BattleInfo {
  id: number
  saveId: number
  monsterId: number
  playerCurrentHp: number
  playerCurrentMp: number
  playerCurrentDefend: number
  monsterCurrentHp: number
  monsterCurrentMp: number
  monsterCurrentDefend: number
  createTime: string
  updateTime: string
}

// 魔物信息接口
export interface MonsterInfo {
  id: number
  monsterId: number
  monsterName: string
  description: string
  hp: number
  mp: number
  attackBase: number
  gainExp: number
  createTime: string
  updateTime: string
}

// 战斗响应接口
export interface BattleResp {
  battleInfo: BattleInfo
  monsterInfo: MonsterInfo
  log: string
  result: 'WIN' | 'LOSE' | null
}

// 技能信息接口
export interface ActionInfo {
  id: number
  actionId: number
  actionType: string
  actionName: string
  description: string
  targetIsForSelf: boolean
  forHp: number
  forMp: number
  forDefend: number
  mpCost: number
  isContinue: boolean
  continueRound: number
  cd: number
}

// ==================== Axios 配置 ====================

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add token to request headers if it exists
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  // 添加游戏上下文 header
  const contextHeaders = gameContext.toHeaders()
  Object.entries(contextHeaders).forEach(([key, value]) => {
    config.headers.set(key, value)
  })
  return config
})

// Add response interceptor for better error handling
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    console.error('API Error:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
    })
    return Promise.reject(error)
  }
)

// ==================== 用户服务 ====================

export const userService = {
  login(data: LoginRequest): Promise<ApiResponse<{ token: string }>> {
    return api.post('/user/login', data)
  },

  register(data: RegisterRequest): Promise<ApiResponse<string>> {
    return api.post('/user/register', data)
  },

  logout(): Promise<ApiResponse<string>> {
    return api.post('/user/logout')
  },
}

// ==================== 玩家服务 ====================

export const playerService = {
  // 获取所有用户的所有角色
  getPlayerAll(): Promise<ApiResponse<PlayerShowResp[]>> {
    return api.get('/player/showall')
  },

  // 获取玩家基础信息 (showbase)
  getPlayerBaseInfo(): Promise<ApiResponse<PlayerShowResp>> {
    return api.get('/player/showbase')
  },

  // 创建角色
  createPlayer(playerClass: PlayerClass): Promise<ApiResponse<string>> {
    return api.post('/player/create', playerClass)
  },

  // 升级
  levelUp(exp: number): Promise<ApiResponse<string>> {
    return api.post('/player/levelup', exp)
  },

  // 重置角色
  resetPlayer(): Promise<ApiResponse<string>> {
    return api.post('/player/reset')
  },
}

// ==================== 存档服务 ====================

export const saveService = {
  // 创建默认存档
  createDefaultSave(): Promise<ApiResponse<string>> {
    return api.post('/save/create')
  },

  // 获取所有存档
  getAllSaves(): Promise<ApiResponse<SaveInfo[]>> {
    return api.get('/save/showallp')
  },

  // 获取当前存档
  getCurrentSave(): Promise<ApiResponse<SaveInfo>> {
    return api.get('/save/show')
  },

  // 保存存档
  saveSave(saveInfo: SaveInfo): Promise<ApiResponse<string>> {
    return api.post('/save/save', saveInfo)
  },

  // 加载存档
  loadSave(saveInfo: SaveInfo): Promise<ApiResponse<string>> {
    return api.post('/save/load', saveInfo)
  },
}

// ==================== 战斗服务 ====================

export const battleService = {
  // 开始战斗
  startBattle(): Promise<ApiResponse<BattleResp>> {
    return api.post('/battle/start')
  },

  // 获取战斗状态
  getBattleStatus(): Promise<ApiResponse<BattleResp>> {
    return api.get('/battle/status')
  },

  // 执行动作
  executeAction(actionId: number): Promise<ApiResponse<BattleResp>> {
    return api.post('/battle/action', actionId)
  },

  // 结束回合
  endRound(): Promise<ApiResponse<BattleResp>> {
    return api.post('/battle/round-end')
  },
}
