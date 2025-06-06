<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 假设token已存储在localStorage
const token = localStorage.getItem('token')
const user = ref(null)

onMounted(async () => {
  try {
    const res = await axios.get('/api/user/getCurrentUser', {
      headers: { Authorization: token ? `Bearer ${token}` : '' }
    })
    if (res.data.code === 200) {
      user.value = res.data.data
    } else {
      user.value = null
    }
  } catch (e) {
    user.value = null
  }
})
</script>

<template>
  <div class="profile-view">
    <div class="profile-card">
      <div class="avatar">
        <img src="https://api.dicebear.com/7.x/identicon/svg?seed={{user?.username || 'user'}}" alt="avatar" />
      </div>
      <h2>个人信息</h2>
      <div class="profile-info" v-if="user">
        <p><i class="iconfont">&#xe612;</i> <strong>用户名：</strong>{{ user.username }}</p>
        <p><i class="iconfont">&#xe60d;</i> <strong>邮箱：</strong>{{ user.email }}</p>
        <p><i class="iconfont">&#xe601;</i> <strong>角色：</strong>{{ user.role }}</p>
        <p><i class="iconfont">&#xe600;</i> <strong>ID：</strong>{{ user.id }}</p>
      </div>
      <div v-else class="loading">
        <span class="loader"></span> 加载中...
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('//at.alicdn.com/t/c/font_4322202_1k1v4w2o9kq.css');

.profile-view {
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.profile-view::before {
  content: "";
  position: absolute;
  left: -100px;
  top: -100px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #6c63ff33 0%, transparent 70%);
  z-index: 0;
  pointer-events: none;
}

.profile-view::after {
  content: "";
  position: absolute;
  right: -120px;
  bottom: -120px;
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, #fbc2eb44 0%, transparent 70%);
  z-index: 0;
  pointer-events: none;
}

.profile-card {
  position: relative;
  background: rgba(255,255,255,0.95);
  border-radius: 22px;
  box-shadow: 0 8px 40px rgba(108,99,255,0.10), 0 1.5px 8px rgba(0,0,0,0.06);
  padding: 44px 38px 32px 38px;
  max-width: 440px;
  width: 100%;
  text-align: center;
  transition: box-shadow 0.2s;
  z-index: 1;
  border: 1.5px solid #e0eafc;
  backdrop-filter: blur(2px);
}

.profile-card:hover {
  box-shadow: 0 12px 48px rgba(108,99,255,0.16), 0 2px 12px rgba(0,0,0,0.08);
}

.avatar {
  width: 96px;
  height: 96px;
  margin: 0 auto 22px auto;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #a1c4fd;
  background: #f4f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px #a1c4fd33;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

h2 {
  margin-bottom: 22px;
  color: #2d3a4b;
  font-weight: 800;
  letter-spacing: 2.5px;
  font-size: 2rem;
  text-shadow: 0 2px 8px #e0eafc77;
}

.profile-info {
  text-align: left;
  margin-top: 14px;
  padding: 18px 18px 8px 18px;
  border-radius: 12px;
  background: linear-gradient(90deg, #f8fafc 60%, #e0eafc 100%);
  box-shadow: 0 1px 4px #e0eafc55;
}

.profile-info p {
  margin: 18px 0;
  font-size: 18px;
  color: #34495e;
  display: flex;
  align-items: center;
  font-weight: 500;
  letter-spacing: 1px;
}

.profile-info strong {
  margin-right: 4px;
  color: #6c63ff;
  font-weight: 700;
}

.iconfont {
  color: #6c63ff;
  font-size: 22px;
  margin-right: 10px;
  transition: color 0.2s;
}

.profile-info p:hover .iconfont {
  color: #fbc2eb;
}

.loading {
  margin-top: 36px;
  color: #888;
  font-size: 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loader {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #6c63ff;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg);}
  100% { transform: rotate(360deg);}
}
</style>