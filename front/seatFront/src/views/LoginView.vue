<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import '../css/global.css'
import '../css/login.css'

// 不再需要单独配置baseURL，使用相对路径
// 所有请求都会通过Vite的代理转发
const activeTab = ref('login')
const router = useRouter()

const loginForm = ref({
  username: '',
  password: ''
})

const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const loginError = ref('')
const registerSuccess = ref(false)
const registerError = ref('')

const switchTab = (tab) => {
  activeTab.value = tab
  loginError.value = ''
}

const handleLogin = async () => {
  // 表单验证
  if (!loginForm.value.username || !loginForm.value.password) {
    loginError.value = '用户名和密码不能为空'
    return
  }
  
  loading.value = true
  loginError.value = ''
  
  try {
    // 使用params发送参数
    const response = await axios.post('/api/user/login', null, {
      params: {
        username: loginForm.value.username,
        password: loginForm.value.password
      }
    })
    
    // 处理响应
    if (response.data.code === 200) {
      // 登录成功，保存token
      localStorage.setItem('token', response.data.data.token)
      // 保存用户角色
      localStorage.setItem('role', response.data.data.role)

      // 根据角色跳转
      if (response.data.data.role === 'admin') {
        router.push('/admin')
      } else {
        router.push('/home')
      }
    } else {
      // 处理非200状态码
      loginError.value = response.data.message || '登录失败'
    }
  } catch (error) {
    console.error('登录失败', error)
    if (error.response?.status === 404) {
      loginError.value = '接口不存在，请检查API地址是否正确'
    } else {
      loginError.value = error.response?.data?.message || '服务器错误，请稍后再试'
    }
  } finally {
    loading.value = false
  }
}



const handleRegister = async () => {
  // 表单验证
  if (!registerForm.value.username || !registerForm.value.email || 
      !registerForm.value.password || !registerForm.value.confirmPassword) {
    registerError.value = '所有字段都必须填写'
    return
  }
  
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    registerError.value = '两次输入的密码不一致'
    return
  }
  
  loading.value = true
  registerError.value = ''
  
  try {
    // 调用注册API
    const response = await axios.post('/api/user/register', null, {
      params: {
        username: registerForm.value.username,
        email: registerForm.value.email,
        password: registerForm.value.password
      }
    })
    
    // 处理成功响应
if (response.data.code === 200) {
  // 注册成功
  registerSuccess.value = true
  registerError.value = ''
  
  // 清空注册表单
  registerForm.value = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  }
  
  // 3秒后自动切换到登录选项卡
  setTimeout(() => {
    registerSuccess.value = false
    activeTab.value = 'login'
  }, 3000)
} else {
  // 处理非200状态码
  registerError.value = response.data.message || '注册失败，请稍后再试'
  registerSuccess.value = false
}

  } catch (error) {
    console.error('注册失败', error)
    
    // 增强错误处理
    if (error.response?.data?.message?.includes('Duplicate entry') && 
        error.response?.data?.message?.includes('email')) {
      registerError.value = '该邮箱已被注册，请使用其他邮箱'
    } else if (error.response?.status === 404) {
      registerError.value = '接口不存在，请检查API地址是否正确'
    } else {
      registerError.value = error.response?.data?.message || '服务器错误，请稍后再试'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="form-container">
        <div class="logo">
          <div class="logo-icon">LS</div>
        </div>
        
        <div class="welcome-text">
          <h1>自习室管理系统</h1>
          <p>欢迎使用自习室管理系统</p>
        </div>
        
        <div class="tabs">
          <button 
            :class="['tab-btn', { active: activeTab === 'login' }]" 
            @click="switchTab('login')"
          >
            登录
          </button>
          <button 
            :class="['tab-btn', { active: activeTab === 'register' }]" 
            @click="switchTab('register')"
          >
            注册
          </button>
        </div>

        <!-- 登录表单部分 -->
        <div v-if="activeTab === 'login'" class="form login-form">
          <div class="form-group">
            <label for="username">用户名</label>
            <input 
              type="text" 
              id="username" 
              v-model="loginForm.username" 
              placeholder="请输入用户名" 
              required
            >
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input 
              type="password" 
              id="password" 
              v-model="loginForm.password" 
              placeholder="请输入密码" 
              required
            >
          </div>
          
          <div v-if="loginError" class="error-message">
            {{ loginError }}
          </div>
          
          <button 
            class="submit-btn" 
            @click="handleLogin" 
            :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>

        <!-- 注册表单 -->
        <div v-if="activeTab === 'register'" class="form register-form">
          <div class="form-group">
            <label for="reg-username">用户名</label>
            <input 
              type="text" 
              id="reg-username" 
              v-model="registerForm.username" 
              placeholder="请创建用户名" 
              required
            >
          </div>
          <div class="form-group">
            <label for="email">邮箱</label>
            <input 
              type="email" 
              id="email" 
              v-model="registerForm.email" 
              placeholder="请输入邮箱" 
              required
            >
          </div>
          <div class="form-group">
            <label for="reg-password">密码</label>
            <input 
              type="password" 
              id="reg-password" 
              v-model="registerForm.password" 
              placeholder="请设置密码" 
              required
            >
          </div>
          <div class="form-group">
            <label for="confirm-password">确认密码</label>
            <input 
              type="password" 
              id="confirm-password" 
              v-model="registerForm.confirmPassword" 
              placeholder="请再次输入密码" 
              required
            >
          </div>
         <!-- 在注册表单中显示成功信息 -->
<div v-if="registerSuccess" class="success-message">
  注册成功！3秒后将自动切换到登录页面...
</div>

<div v-if="registerError" class="error-message">
  {{ registerError }}
</div>
          <button 
            class="submit-btn" 
            @click="handleRegister" 
            :disabled="loading"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </div>
        
        <div class="footer-text">
          <p>&copy; 2025 图书馆座位管理系统 | 简约·高效</p>
        </div>
      </div>
      
      <div class="quote-container">
        <div class="quote">
          <h2>知识的殿堂</h2>
          <p>"书籍是人类进步的阶梯"</p>
        </div>
      </div>
    </div>
  </div>
</template>