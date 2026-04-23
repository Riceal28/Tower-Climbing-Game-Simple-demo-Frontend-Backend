<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, CircleCloseFilled } from '@element-plus/icons-vue'
import { userService } from '../api/userService'
import { gameContext } from '../api/gameContext'

const router = useRouter()

const form = ref({
  username: '',
  password: '',
})

const showPassword = ref(false)
const loading = ref(false)
const ruleFormRef = ref()

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20之间', trigger: 'blur' },
  ],
}

const handleLogin = async () => {
  if (!ruleFormRef.value) return
  
  await ruleFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    loading.value = true
    try {
      const response = await userService.login({
        username: form.value.username,
        password: form.value.password,
      })

      if (response.data.success) {
        // Save token to localStorage
        const token = response.data.data.token
        localStorage.setItem('token', token)
        // 初始化游戏上下文
        const userId = response.data.data.userId
        if (userId) {
          localStorage.setItem('userId', String(userId))
          gameContext.init(userId)
        }
        ElMessage.success('登录成功')
        // Redirect to home page
        router.push('/home')
      } else {
        ElMessage.error(response.data.message || '登录失败')
      }
    } catch (error: any) {
      console.error('Login error:', error)
      const message = error.response?.data?.message || error.message || '登录失败'
      ElMessage.error(message)
    } finally {
      loading.value = false
    }
  })
}

const handleReset = () => {
  if (ruleFormRef.value) {
    ruleFormRef.value.resetFields()
  }
}

const goToRegister = () => {
  router.push('/register')
}

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !loading.value) {
    handleLogin()
  }
}
</script>

<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 登录卡片 -->
    <el-card class="login-card">
      <!-- 标题区域 -->
      <div class="login-header">
        <div class="logo">🎮</div>
        <h2>冒险之旅</h2>
        <p>Adventure Game</p>
      </div>

      <!-- 表单区域 -->
      <el-form
        ref="ruleFormRef"
        :model="form"
        :rules="rules"
        @keydown="handleKeydown"
        class="login-form"
      >
        <!-- 用户名输入 -->
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            clearable
            maxlength="20"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 密码输入 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            clearable
            maxlength="20"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
            <template #suffix>
              <el-icon
                class="toggle-password"
                @click="showPassword = !showPassword"
              >
                <CircleCloseFilled v-if="showPassword" />
                <Lock v-else />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 按钮区域 -->
        <el-form-item class="button-group">
          <el-button
            type="primary"
            @click="handleLogin"
            :loading="loading"
            size="large"
            class="login-button"
          >
            登 录
          </el-button>
          <el-button
            @click="handleReset"
            size="large"
            class="reset-button"
          >
            重 置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 分隔线 -->
      <el-divider>没有账户？</el-divider>

      <!-- 注册链接 -->
      <div class="register-section">
        <p>立即注册开启冒险之旅</p>
        <el-button
          type="success"
          plain
          @click="goToRegister"
          size="large"
          class="register-button"
        >
          前往注册
        </el-button>
      </div>

      <!-- 底部提示 -->
      <div class="footer-tip">
        <el-text type="info" size="small">测试账号：admin / 123456</el-text>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 背景装饰圆形 */
.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: white;
  top: -100px;
  right: -100px;
  animation: float1 20s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: white;
  bottom: -50px;
  left: -50px;
  animation: float2 25s ease-in-out infinite;
}

.circle-3 {
  width: 250px;
  height: 250px;
  background: white;
  top: 50%;
  left: 10%;
  animation: float3 30s ease-in-out infinite;
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, -30px); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-30px, 30px); }
}

@keyframes float3 {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, 30px); }
}

/* 登录卡片 */
.login-card {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 10;
  border-radius: 16px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  padding: 0;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.el-card__body) {
  padding: 40px 30px;
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.logo {
  font-size: 48px;
  margin-bottom: 10px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.login-header h2 {
  margin: 0 0 5px 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-header p {
  margin: 0;
  color: #999;
  font-size: 12px;
  letter-spacing: 2px;
}

/* 表单区域 */
.login-form {
  margin-bottom: 10px;
}

.login-form :deep(.el-input__prefix) {
  display: flex;
  align-items: center;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

/* 密码可见性切换 */
.toggle-password {
  cursor: pointer;
  color: #999;
  transition: color 0.3s ease;
}

.toggle-password:hover {
  color: #667eea;
}

/* 按钮组 */
.button-group {
  display: flex;
  gap: 12px;
  margin: 0;
}

.button-group :deep(.el-form-item__content) {
  justify-content: space-between;
}

.login-button {
  flex: 1;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 8px;
  height: 40px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.reset-button {
  flex: 1;
  border-radius: 8px;
  height: 40px;
  font-size: 16px;
  letter-spacing: 1px;
}

/* 分隔线 */
:deep(.el-divider) {
  margin: 20px 0;
  color: #999;
}

/* 注册区域 */
.register-section {
  text-align: center;
  margin-bottom: 15px;
}

.register-section p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.register-button {
  width: 100%;
  border-radius: 8px;
  height: 40px;
  font-size: 16px;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

/* 底部提示 */
.footer-tip {
  text-align: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-card {
    max-width: 100%;
    margin: 20px;
  }

  :deep(.el-card__body) {
    padding: 30px 20px;
  }

  .login-header {
    margin-bottom: 25px;
    padding-bottom: 15px;
  }

  .login-header h2 {
    font-size: 24px;
  }

  .logo {
    font-size: 40px;
  }

  .button-group {
    flex-direction: column;
    gap: 8px;
  }

  .button-group :deep(.el-form-item__content) {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>
