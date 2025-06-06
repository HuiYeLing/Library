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
  <div class="search-bar">
    <input
      v-model="staffSearchText"
      @keyup.enter="searchStaff"
      placeholder="输入姓名或手机号搜索"/>
    <button @click="searchStaff" :disabled="staffLoading">🔍 搜索</button>
    <button @click="resetStaffSearch" :disabled="staffLoading">♻️ 重置</button>
    <button @click="showAddStaff = true">新增员工</button>
    <button class="refresh-btn" @click="fetchStaff" :disabled="staffLoading">
      {{ staffLoading ? '⏳ 加载中...' : '🔄 刷新列表' }}
    </button>
  </div>
  <div v-if="filteredStaff.length" class="staff-table-wrapper">
    <table class="staff-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>姓名</th>
          <th>性别</th>
          <th>手机号</th>
          <th>年龄</th>
          <th>薪资</th>
          <th>入职日期</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="staff in filteredStaff" :key="staff.id">
          <td>{{ staff.id }}</td>
          <td>{{ staff.name }}</td>
          <td>{{ staff.gender }}</td>
          <td>{{ staff.phone }}</td>
          <td>{{ staff.age }}</td>
          <td>{{ staff.salary }}</td>
          <td>{{ formatDate(staff.hireDate) }}</td>
          <td>
            <button class="delete-btn" @click="deleteStaff(staff.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div v-else class="empty-tip">暂无员工数据</div>

  <!-- 新增员工弹窗 -->
  <div v-if="showAddStaff" class="modal">
    <div class="modal-content">
      <h3>新增员工</h3>
      <form @submit.prevent="addStaff">
        <input v-model="newStaff.id" placeholder="工号(ID)" type="number" required />
        <input v-model="newStaff.name" placeholder="姓名" required />
        <input v-model="newStaff.gender" placeholder="性别" required />
        <input v-model="newStaff.phone" placeholder="手机号" required />
        <input v-model="newStaff.hireDate" placeholder="入职日期(yyyy-mm-dd)" required />
        <input v-model.number="newStaff.age" placeholder="年龄" type="number" required />
        <input v-model.number="newStaff.salary" placeholder="薪资" type="number" required />
        <div class="modal-actions">
          <button type="submit" :disabled="staffLoading">提交</button>
          <button type="button" @click="showAddStaff = false">取消</button>
        </div>
      </form>
    </div>
  </div>
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

// 员工相关
const staff = ref([])
const staffLoading = ref(false)
const staffSearchText = ref('')

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

// 获取所有员工
const fetchStaff = async () => {
  staffLoading.value = true
  try {
    const res = await axios.get('/api/employee/getAll')
    if (res.data.code === 200) {
      staff.value = res.data.data
    }
  } finally {
    staffLoading.value = false
  }
}

// 删除员工
const deleteStaff = async (id) => {
  if (!confirm('确定要删除该员工吗？')) return
  staffLoading.value = true
  try {
    const res = await axios.delete('/api/employee/delete', { params: { id } })
    if (res.data.code === 200) {
      staff.value = staff.value.filter(s => s.id !== id)
    } else {
      alert(res.data.msg || '删除失败')
    }
  } finally {
    staffLoading.value = false
  }
}

// 搜索员工
const searchStaff = async () => {
  if (!staffSearchText.value.trim()) {
    await fetchStaff()
    return
  }
  // 前端过滤
  const keyword = staffSearchText.value.trim()
  staff.value = staff.value.filter(
    s =>
      s.name.includes(keyword) ||
      (s.phone && s.phone.includes(keyword))
  )
}

// 重置员工搜索
const resetStaffSearch = async () => {
  staffSearchText.value = ''
  await fetchStaff()
}

// 员工过滤
const filteredStaff = computed(() => {
  if (!staffSearchText.value.trim()) return staff.value
  return staff.value.filter(
    s =>
      s.name.includes(staffSearchText.value.trim()) ||
      (s.phone && s.phone.includes(staffSearchText.value.trim()))
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

const formatDate = (dateStr) => {
  if (!dateStr || typeof dateStr !== 'string') return '无'
  dateStr = dateStr.trim()
  if (/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) {
    const [y, m, d] = dateStr.split('-')
    return `${y}年${parseInt(m)}月${parseInt(d)}日`
  }
  const date = new Date(dateStr)
  if (isNaN(date)) return '无'
  return date.toLocaleDateString()
}
onMounted(() => {
  fetchUsers()
  fetchSeats()
  fetchStaff()
})

// ...existing code...
const showAddStaff = ref(false)
const newStaff = ref({
  id: '',
  name: '',
  gender: '',
  phone: '',
  hireDate: '',
  age: '',
  salary: ''
})

const addStaff = async () => {
  staffLoading.value = true
  try {
    const res = await axios.post('/api/employee/add', newStaff.value)
    if (res.data.code === 200) {
      showAddStaff.value = false
      await fetchStaff()
      // 清空表单
      newStaff.value = { id: '', name: '', gender: '', phone: '', hireDate: '', age: '', salary: '' }
    } else {
      alert(res.data.msg || '新增失败')
    }
  } finally {
    staffLoading.value = false
  }
}

</script>

