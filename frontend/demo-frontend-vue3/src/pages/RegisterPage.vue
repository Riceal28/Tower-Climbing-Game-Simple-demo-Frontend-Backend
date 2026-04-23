<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, CircleCloseFilled } from '@element-plus/icons-vue'
import { userService } from '../api/userService'

const router = useRouter()

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const showPassword = ref(false)
const showConfirmPassword = ref(false)
const loading = ref(false)
const ruleFormRef = ref()

// 密码强度计算
const passwordStrength = computed(() => {
  const pwd = form.value.password
  if (!pwd) return 0
  let strength = 0
  if (pwd.length >= 8) strength++
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength++
  if (/\d/.test(pwd)) strength++
  if (/[!@#$%^&*]/.test(pwd)) strength++
  return Math.min(strength, 3)
})

// 密码强度描述
const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return ''
  if (strength === 1) return '弱'
  if (strength === 2) return '中'
  return '强'
})

// 密码强度颜色
const passwordStrengthColor = computed(() => {
  const strength = passwordStrength.value
  if (strength === 1) return '#f56c6c'
  if (strength === 2) return '#e6a23c'
  if (strength === 3) return '#67c23a'
  return ''
})

// 验证密码一致性
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.value.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

// 验证邮箱
const validateEmail = (rule: any, value: any, callback: any) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (value === '') {
    callback(new Error('请输入邮箱地址'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('邮箱格式不正确'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20之间', trigger: 'blur' },
  ],
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

const handleRegister = async () => {
  if (!ruleFormRef.value) return
  
  await ruleFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    loading.value = true
    try {
      const response = await userService.register({
        username: form.value.username,
        email: form.value.email,
        password: form.value.password,
      })

      if (response.data.success) {
        ElMessage.success('注册成功，请登录')
        // Redirect to login page
        router.push('/login')
      } else {
        ElMessage.error(response.data.message || '注册失败')
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '注册失败，请检查网络连接')
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

const goToLogin = () => {
  router.push('/login')
}

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !loading.value) {
    handleRegister()
  }
}
</script>

<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 注册卡片 -->
    <el-card class="register-card">
      <!-- 标题区域 -->
      <div class="register-header">
        <div class="logo">✨</div>
        <h2>创建账户</h2>
        <p>Join Adventure</p>
      </div>

      <!-- 表单区域 -->
      <el-form
        ref="ruleFormRef"
        :model="form"
        :rules="rules"
        @keydown="handleKeydown"
        class="register-form"
      >
        <!-- 用户名输入 -->
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            clearable
            maxlength="20"
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 邮箱输入 -->
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱地址"
            clearable
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 密码输入 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码（6-20位）"
            clearable
            maxlength="20"
            @keyup.enter="handleRegister"
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

        <!-- 密码强度提示 -->
        <div v-if="form.password" class="password-strength">
          <span class="strength-label">密码强度：</span>
          <el-progress
            :percentage="passwordStrength * 33"
            :color="passwordStrengthColor"
            :show-text="false"
            class="strength-bar"
          />
          <span class="strength-text" :style="{ color: passwordStrengthColor }">
            {{ passwordStrengthText }}
          </span>
        </div>

        <!-- 确认密码输入 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            placeholder="请再次输入密码"
            clearable
            maxlength="20"
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
            <template #suffix>
              <el-icon
                class="toggle-password"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <CircleCloseFilled v-if="showConfirmPassword" />
                <Lock v-else />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 按钮区域 -->
        <el-form-item class="button-group">
          <el-button
            type="primary"
            @click="handleRegister"
            :loading="loading"
            size="large"
            class="register-button"
          >
            注 册
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
      <el-divider>已有账户？</el-divider>

      <!-- 登录链接 -->
      <div class="login-section">
        <p>返回登录开启冒险</p>
        <el-button
          type="info"
          plain
          @click="goToLogin"
          size="large"
          class="login-button"
        >
          前往登录
        </el-button>
      </div>

      <!-- 底部提示 -->
      <div class="footer-tip">
        <el-text type="info" size="small">注册后可立即开始游戏冒险</el-text>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.register-container {
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

/* 注册卡片 */
.register-card {
  width: 100%;
  max-width: 450px;
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

/* 注册头部 */
.register-header {
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

.register-header h2 {
  margin: 0 0 5px 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-header p {
  margin: 0;
  color: #999;
  font-size: 12px;
  letter-spacing: 2px;
}

/* 表单区域 */
.register-form {
  margin-bottom: 10px;
}

.register-form :deep(.el-input__prefix) {
  display: flex;
  align-items: center;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.register-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

/* 密码强度提示 */
.password-strength {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: -10px 0 15px 0;
  font-size: 12px;
}

.strength-label {
  color: #999;
  font-weight: 500;
}

.strength-bar {
  flex: 1;
  max-width: 120px;
}

.strength-text {
  font-weight: 600;
  font-size: 13px;
}

:deep(.el-progress__bar) {
  transition: width 0.3s ease;
}

/* 可见性切换 */
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

.register-button {
  flex: 1;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 8px;
  height: 40px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.register-button:hover {
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

/* 登录区域 */
.login-section {
  text-align: center;
  margin-bottom: 15px;
}

.login-section p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.login-button {
  width: 100%;
  border-radius: 8px;
  height: 40px;
  font-size: 16px;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(96, 125, 139, 0.4);
}

/* 底部提示 */
.footer-tip {
  text-align: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 480px) {
  .register-card {
    max-width: 100%;
    margin: 20px;
  }

  :deep(.el-card__body) {
    padding: 30px 20px;
  }

  .register-header {
    margin-bottom: 25px;
    padding-bottom: 15px;
  }

  .register-header h2 {
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

  .password-strength {
    flex-wrap: wrap;
  }

  .strength-bar {
    order: 3;
    width: 100%;
  }
}
</style>
