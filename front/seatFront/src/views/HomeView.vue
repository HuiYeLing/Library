<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import '../css/HomeView.css'
import '../css/global.css'

const router = useRouter()
const seats = ref([])
const loading = ref(false)
const error = ref('')
const successMessage = ref('')
// 添加当前楼层状态
const currentFloor = ref('First Floor')

// 按楼层分组的座位
const firstFloorSeats = computed(() => {
  return seats.value.filter(seat => seat.location === 'First Floor')
})

const secondFloorSeats = computed(() => {
  return seats.value.filter(seat => seat.location === 'Second Floor')
})

// 当前显示的座位
const currentSeats = computed(() => {
  return seats.value.filter(seat => seat.location === currentFloor.value)
})

// 切换楼层
const switchFloor = (floor) => {
  currentFloor.value = floor
  error.value = ''
  successMessage.value = ''
}

// 检查用户是否已登录
const checkAuth = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    router.push('/login')
    return false
  }
  return true
}

// 获取所有座位
const fetchSeats = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await axios.get('/api/seats/getListSeats')
    if (response.data.code === 200) {
      seats.value = response.data.data
    } else {
      error.value = response.data.message || '获取座位列表失败'
    }
  } catch (err) {
    console.error('获取座位列表出错', err)
    error.value = '服务器错误，无法获取座位列表'
  } finally {
    loading.value = false
  }
}

// 预订座位
const reserveSeat = async (seatId) => {
  if (!checkAuth()) return
  
  loading.value = true
  error.value = ''
  successMessage.value = ''
  
  try {
    const response = await axios.get('/api/seats/reserveSeat', {
      params: { id: seatId }
    })
    
    if (response.data.code === 200) {
      successMessage.value = '座位预订成功!'
      // 刷新座位列表
      await fetchSeats()
    } else {
      error.value = response.data.message || '预订座位失败'
    }
  } catch (err) {
    console.error('预订座位出错', err)
    error.value = '服务器错误，预订失败'
  } finally {
    loading.value = false
  }
}

// 离开座位
const leaveSeat = async (seatId) => {
  if (!checkAuth()) return
  
  loading.value = true
  error.value = ''
  successMessage.value = ''
  
  try {
    const response = await axios.get('/api/seats/leaveSeat', {
      params: { id: seatId }
    })
    
    if (response.data.code === 200) {
      successMessage.value = '已成功离开座位!'
      // 刷新座位列表
      await fetchSeats()
    } else {
      error.value = response.data.message || '离开座位失败'
    }
  } catch (err) {
    console.error('离开座位出错', err)
    error.value = '服务器错误，操作失败'
  } finally {
    loading.value = false
  }
}

// 模拟初始数据 - 替换为实际API调用
const initializeSeats = () => {
  seats.value = [
    { id: 1, seat_number: "A1", location: "First Floor", status: "AVAILABLE" },
    { id: 2, seat_number: "A2", location: "First Floor", status: "AVAILABLE" },
    { id: 3, seat_number: "A3", location: "First Floor", status: "AVAILABLE" },
    { id: 4, seat_number: "A4", location: "First Floor", status: "AVAILABLE" },
    { id: 5, seat_number: "A5", location: "First Floor", status: "AVAILABLE" },
    { id: 6, seat_number: "A6", location: "First Floor", status: "AVAILABLE" },
    { id: 7, seat_number: "A7", location: "First Floor", status: "AVAILABLE" },
    { id: 8, seat_number: "A8", location: "First Floor", status: "AVAILABLE" },
    { id: 9, seat_number: "A9", location: "First Floor", status: "AVAILABLE" },
    { id: 10, seat_number: "A10", location: "First Floor", status: "AVAILABLE" },
    { id: 11, seat_number: "A11", location: "Second Floor", status: "AVAILABLE" },
    { id: 12, seat_number: "A12", location: "Second Floor", status: "AVAILABLE" },
    { id: 13, seat_number: "A13", location: "Second Floor", status: "AVAILABLE" },
    { id: 14, seat_number: "A14", location: "Second Floor", status: "AVAILABLE" },
    { id: 15, seat_number: "A15", location: "Second Floor", status: "AVAILABLE" },
    { id: 16, seat_number: "A16", location: "Second Floor", status: "AVAILABLE" },
    { id: 17, seat_number: "A17", location: "Second Floor", status: "AVAILABLE" },
    { id: 18, seat_number: "A18", location: "Second Floor", status: "AVAILABLE" },
    { id: 19, seat_number: "A19", location: "Second Floor", status: "AVAILABLE" },
    { id: 20, seat_number: "A20", location: "Second Floor", status: "AVAILABLE" },
  ]
}

// 组件挂载时加载座位列表
onMounted(() => {
  // 在实际环境中，取消下面的注释并注释掉 initializeSeats()
  // fetchSeats()
  initializeSeats()
})
</script>

<template>
  <div class="library-container">
    <header class="library-header">
      <h1>图书馆座位预订系统</h1>
      <div class="header-actions">
        <div class="seat-legend">
          <span class="legend-item">
            <span class="legend-color available"></span> 可预订
          </span>
          <span class="legend-item">
            <span class="legend-color occupied"></span> 已占用
          </span>
        </div>
        <button class="refresh-btn" @click="fetchSeats" :disabled="loading">
          {{ loading ? '更新中' : '刷新状态' }}
        </button>
      </div>
    </header>
    
    <div v-if="error" class="error-message">{{ error }}</div>
    <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>加载中，请稍候...</p>
    </div>
    
    <!-- 楼层切换菜单 -->
    <div class="floor-tabs">
      <button 
        :class="['floor-tab', { active: currentFloor === 'First Floor' }]"
        @click="switchFloor('First Floor')">
        一楼
      </button>
      <button 
        :class="['floor-tab', { active: currentFloor === 'Second Floor' }]"
        @click="switchFloor('Second Floor')">
        二楼
      </button>
    </div>

    <!-- 当前楼层座位区域 -->
    <div class="floor-section">
      <h2 class="floor-title">
        {{ currentFloor === 'First Floor' ? '一楼阅览区' : '二楼自习区' }}
      </h2>
      
      <div class="seat-matrix">
        <div 
          v-for="seat in currentSeats" 
          :key="seat.id"
          :class="['seat', seat.status.toLowerCase(), `seat-${seat.seat_number.toLowerCase()}`]"
        >
          <div class="seat-content">
      <div class="seat-number">{{ seat.seat_number }}</div>
      <div class="seat-status">{{ seat.status === 'OCCUPIED' ? '已占用' : '可预订' }}</div>
      <button 
        v-if="seat.status === 'AVAILABLE'" 
        @click="reserveSeat(seat.id)"
        class="seat-action reserve">
        预订
      </button>
      <button 
        v-else-if="seat.status === 'OCCUPIED'" 
        @click="leaveSeat(seat.id)"
        class="seat-action leave">
        结束使用
      </button>
      </div>

        </div>
      </div>
      
      
    </div>
  </div>
</template>