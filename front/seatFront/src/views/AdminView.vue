<template>
  <div class="admin-container">
    <h1>管理员管理页面</h1>
    <p>欢迎，管理员！你可以在这里管理用户和座位。</p>

    <div class="user-list-section">
      <h2>用户列表</h2>
      <div class="search-bar">
        <input
          v-model="searchText"
          @keyup.enter="searchUsers"
          placeholder="输入用户名或邮箱搜索"
        />
        <button @click="searchUsers" :disabled="loading">搜索</button>
        <button @click="resetSearch" :disabled="loading">重置</button>
      </div>
      <button class="refresh-btn" @click="fetchUsers" :disabled="loading">
        {{ loading ? '加载中...' : '刷新列表' }}
      </button>
      <div v-if="filteredUsers.length">
        <div
          v-for="user in filteredUsers"
          :key="user.id"
          class="user-accordion"
        >
          <div
            class="user-summary"
            @click="toggleExpand(user.id)"
            :class="{ expanded: expandedUserId === user.id }"
          >
            <span><b>{{ user.username }}</b>（{{ user.role }}）</span>
            <span class="expand-arrow">{{ expandedUserId === user.id ? '▲' : '▼' }}</span>
          </div>
          <div v-if="expandedUserId === user.id" class="user-detail">
            <p><b>ID：</b>{{ user.id }}</p>
            <p><b>用户名：</b>{{ user.username }}</p>
            <p><b>邮箱：</b>{{ user.email }}</p>
            <p><b>角色：</b>{{ user.role }}</p>
            <button class="delete-btn" @click="deleteUser(user.id)">删除</button>
          </div>
        </div>
      </div>
      <div v-else class="empty-tip">暂无用户数据</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const users = ref([])
const loading = ref(false)
const searchText = ref('')
const expandedUserId = ref(null)

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

const toggleExpand = (id) => {
  expandedUserId.value = expandedUserId.value === id ? null : id
}

const searchUsers = async () => {
  if (!searchText.value.trim()) {
    await fetchUsers()
    return
  }
  loading.value = true
  try {
    // 支持用户名或邮箱搜索
    const keyword = searchText.value.trim()
    // 优先用户名搜索
    const res = await axios.get('/api/user/getUserByUsername', { params: { username: keyword } })
    if (res.data.code === 200 && res.data.data) {
      users.value = [res.data.data]
    } else {
      // 尝试邮箱搜索（假如后端支持邮箱搜索接口，可替换为邮箱接口）
      users.value = []
    }
  } finally {
    loading.value = false
  }
}

const resetSearch = async () => {
  searchText.value = ''
  await fetchUsers()
}

const filteredUsers = computed(() => {
  if (!searchText.value.trim()) return users.value
  // 前端过滤邮箱（如后端不支持邮箱搜索）
  return users.value.filter(
    u =>
      u.username.includes(searchText.value.trim()) ||
      (u.email && u.email.includes(searchText.value.trim()))
  )
})

onMounted(fetchUsers)
</script>

<style scoped>
.admin-container {
  max-width: 900px;
  margin: 40px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 40px 32px;
  text-align: center;
}
.user-list-section {
  margin-top: 32px;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 24px 16px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.03);
}
.user-list-section h2 {
  margin-bottom: 18px;
  color: #2c3e50;
}
.search-bar {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 16px;
}
.search-bar input {
  padding: 6px 12px;
  border: 1px solid #d0d7de;
  border-radius: 4px;
  width: 220px;
}
.search-bar button {
  padding: 6px 14px;
  border: none;
  border-radius: 4px;
  background: #1976d2;
  color: #fff;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s;
}
.search-bar button:disabled {
  background: #b0bec5;
  cursor: not-allowed;
}
.user-accordion {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
  margin-bottom: 12px;
  overflow: hidden;
  transition: box-shadow 0.2s;
}
.user-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  cursor: pointer;
  font-size: 16px;
  background: #f5f5f5;
  border-bottom: 1px solid #e0e0e0;
  transition: background 0.2s;
}
.user-summary.expanded {
  background: #e3f2fd;
}
.expand-arrow {
  font-size: 14px;
  color: #888;
}
.user-detail {
  padding: 16px 24px;
  text-align: left;
  background: #fafbfc;
}
.delete-btn {
  background: #e53935;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 4px 12px;
  cursor: pointer;
  font-size: 14px;
  margin-top: 10px;
  transition: background 0.2s;
}
.delete-btn:hover {
  background: #b71c1c;
}
.empty-tip {
  color: #888;
  margin-top: 18px;
}
.refresh-btn {
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 6px 18px;
  margin-bottom: 10px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s;
}
.refresh-btn:disabled {
  background: #b0bec5;
  cursor: not-allowed;
}
</style>