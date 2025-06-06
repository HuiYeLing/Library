<template>
  <div class="admin-layout">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <div class="sidebar-title">后台管理</div>
      <ul>
        <li :class="{active: activeTab==='user'}" @click="activeTab='user'">用户管理</li>
        <li :class="{active: activeTab==='staff'}" @click="activeTab='staff'">员工管理</li>
        <li :class="{active: activeTab==='seat'}" @click="activeTab='seat'">座位管理</li>
      </ul>
    </aside>
    <!-- 右侧内容区 -->
    <main class="main-content">
      <div v-if="activeTab==='user'" class="panel user-panel">
        <h2>用户管理</h2>
        <div class="search-bar">
          <input
            v-model="searchText"
            @keyup.enter="searchUsers"
            placeholder="输入用户名或邮箱搜索"
          />
          <button @click="searchUsers" :disabled="loading">🔍 搜索</button>
          <button @click="resetSearch" :disabled="loading">♻️ 重置</button>
          <button class="refresh-btn" @click="fetchUsers" :disabled="loading">
            {{ loading ? '⏳ 加载中...' : '🔄 刷新列表' }}
          </button>
        </div>
        <div v-if="filteredUsers.length" class="user-list">
          <div
            v-for="user in filteredUsers"
            :key="user.id"
            class="user-card"
            :class="{ expanded: expandedUserId === user.id }"
          >
            <div class="user-header" @click="toggleExpand(user.id)">
              <div>
                <span class="user-avatar">{{ user.username[0]?.toUpperCase() }}</span>
                <span class="user-name">{{ user.username }}</span>
                <span class="user-role" :class="user.role">{{ user.role }}</span>
              </div>
              <button class="delete-btn" @click="deleteUser(user.id)">删除</button>
              <span class="expand-arrow">{{ expandedUserId === user.id ? '▲' : '▼' }}</span>
            </div>
            <transition name="fade">
              <div v-if="expandedUserId === user.id" class="user-detail">
                <p><b>ID：</b>{{ user.id }}</p>
                <p><b>用户名：</b>{{ user.username }}</p>
                <p><b>邮箱：</b>{{ user.email }}</p>
                <p><b>角色：</b>{{ user.role }}</p>
              </div>
            </transition>
          </div>
        </div>
        <div v-else class="empty-tip">暂无用户数据</div>
      </div>
      <div v-else-if="activeTab==='staff'" class="panel staff-panel">
        <h2>员工管理</h2>
        <div class="empty-tip">员工管理功能开发中...</div>
      </div>
      <div v-else class="panel seat-panel">
        <h2>座位管理</h2>
        <button class="refresh-btn" @click="fetchSeats" :disabled="seatLoading">
         {{ seatLoading ? '⏳ 加载中...' : '🔄 刷新座位' }}
        </button>
        <div v-if="seats.length" class="seat-grid">
          <div
            v-for="seat in seats"
            :key="seat.id"
            class="seat-item"
            :class="seatStatusClass(seat.status)"
          >
            <div class="seat-id">#{{ seat.id }}</div>
            <div class="seat-status">{{ seat.status }}</div>
            <div class="seat-actions">
              <button
                v-if="seat.status === '空闲'"
                @click="updateSeatStatus(seat.id, '已预订')"
                :disabled="seatLoading"
              >预订</button>
              <button
                v-if="seat.status === '已预订'"
                @click="updateSeatStatus(seat.id, '空闲')"
                :disabled="seatLoading"
              >释放</button>
              <button
                v-if="seat.status !== '空闲' && seat.status !== '已预订'"
                @click="updateSeatStatus(seat.id, '空闲')"
                :disabled="seatLoading"
              >重置</button>
            </div>
          </div>
        </div>
        <div v-else class="empty-tip">暂无座位数据</div>
      </div>
    </main>
  </div>
</template>

<script setup>
import '../css/Admin.css'
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const users = ref([])
const loading = ref(false)
const searchText = ref('')
const expandedUserId = ref(null)
const seats = ref([])
const seatLoading = ref(false)
const activeTab = ref('user')

// 获取所有用户
const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/user/getAllUsers')
    if (res.data.code === 200) {
      users.value = res.data.data
    }
  } finally {
    loading.value = false
  }
}


// 删除用户
const deleteUser = async (id) => {
  if (!confirm('确定要删除该用户吗？')) return
  loading.value = true
  try {
    const res = await axios.delete('/api/user/deleteUser', { params: { id } })
    if (res.data.code === 200) {
      users.value = users.value.filter(u => u.id !== id)
      if (expandedUserId.value === id) expandedUserId.value = null
    } else {
      alert(res.data.msg || '删除失败')
    }
  } finally {
    loading.value = false
  }
}

// 展开/收起用户详情
const toggleExpand = (id) => {
  expandedUserId.value = expandedUserId.value === id ? null : id
}

// 搜索用户
const searchUsers = async () => {
  if (!searchText.value.trim()) {
    await fetchUsers()
    return
  }
  loading.value = true
  try {
    const keyword = searchText.value.trim()
    const res = await axios.get('/api/user/getUserByUsername', { params: { username: keyword } })
    if (res.data.code === 200 && res.data.data) {
      users.value = [res.data.data]
    } else {
      users.value = []
    }
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = async () => {
  searchText.value = ''
  await fetchUsers()
}

// 用户过滤
const filteredUsers = computed(() => {
  if (!searchText.value.trim()) return users.value
  return users.value.filter(
    u =>
      u.username.includes(searchText.value.trim()) ||
      (u.email && u.email.includes(searchText.value.trim()))
  )
})

// 获取所有座位
const fetchSeats = async () => {
  seatLoading.value = true
  try {
    const res = await axios.get('/api/seats/getListSeats')
    if (res.data.code === 200) {
      seats.value = res.data.data
    }
  } finally {
    seatLoading.value = false
  }
}

// 更新座位状态
const updateSeatStatus = async (id, status) => {
  seatLoading.value = true
  try {
    const res = await axios.get('/api/seats/updateSeatStatus', {
      params: { id, status }
    })
    if (res.data.code === 200) {
      // 更新成功后刷新座位列表
      await fetchSeats()
    } else {
      alert(res.data.message || '更新失败')
    }
  } finally {
    seatLoading.value = false
  }
}

// 状态样式
const seatStatusClass = (status) => {
  if (status === '空闲') return 'seat-free'
  if (status === '已预订') return 'seat-booked'
  return 'seat-other'
}

onMounted(() => {
  fetchUsers()
  fetchSeats()
})
</script>