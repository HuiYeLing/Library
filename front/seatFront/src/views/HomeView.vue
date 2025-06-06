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
// 添加详情卡片状态
const selectedSeat = ref(null)
const showSeatDetail = ref(false)
// 跳转到个人信息页面
const goToProfile = () => {
  // 检查用户是否登录
  if (!checkAuth()) return

  // 导航到个人信息页面
  router.push('/profile')
}
// 显示座位详情
const viewSeatDetail = (seat) => {
  selectedSeat.value = seat
  showSeatDetail.value = true
}

// 关闭座位详情
const closeSeatDetail = () => {
  showSeatDetail.value = false
}

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
      <h1>自习室管理系统</h1>
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

        <button class="refresh-btn" @click="goToProfile">
          个人信息
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
      <button :class="['floor-tab', { active: currentFloor === 'First Floor' }]" @click="switchFloor('First Floor')">
        一楼
      </button>
      <button :class="['floor-tab', { active: currentFloor === 'Second Floor' }]" @click="switchFloor('Second Floor')">
        二楼
      </button>
    </div>

    <!-- 当前楼层座位区域 -->
    <div class="floor-section">
      <h2 class="floor-title">
        {{ currentFloor === 'First Floor' ? '一楼阅览区' : '二楼自习区' }}
      </h2>

      <div class="seat-matrix">
        <div v-for="seat in currentSeats" :key="seat.id"
          :class="['seat', seat.status.toLowerCase(), `seat-${seat.seat_number.toLowerCase()}`]">
          <div class="seat-content">
            <div class="seat-number">{{ seat.seat_number }}</div>
            <div class="seat-status">{{ seat.status === 'OCCUPIED' ? '已占用' : '可预订' }}</div>
            <button v-if="seat.status === 'AVAILABLE'" @click="viewSeatDetail(seat)" class="seat-action reserve">
              查看
            </button>
            <button v-else-if="seat.status === 'OCCUPIED'" @click="leaveSeat(seat.id)" class="seat-action leave">
              结束使用
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 座位详情弹窗 -->
    <div v-if="showSeatDetail" class="seat-detail-modal">
      <div class="seat-detail-card">
        <button class="close-button" @click="closeSeatDetail">×</button>
        <h3>座位详情</h3>
        <div v-if="selectedSeat" class="seat-info">
          <p><strong>座位编号:</strong> {{ selectedSeat.seat_number }}</p>
          <p><strong>位置:</strong> {{ selectedSeat.location === 'First Floor' ? '一楼阅览区' : '二楼自习区' }}</p>
          <p><strong>状态:</strong> {{ selectedSeat.status === 'OCCUPIED' ? '已占用' : '可预订' }}</p>
          <div class="detail-actions">
            <button v-if="selectedSeat.status === 'AVAILABLE'" @click="reserveSeat(selectedSeat.id); closeSeatDetail()"
              class="reserve-btn">
              预订此座位
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
/* 添加详情卡片样式 (不知名原因必须留在这边)*/
.seat-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(149, 145, 145, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.seat-detail-card {
  background-color: #ffffff;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.3);
  width: 320px;
  position: relative;
  border: 1px solid #3c4150;

}

.close-button {
  position: absolute;
  top: 12px;
  right: 12px;
  background: none;
  border: none;
  font-size: 22px;
  cursor: pointer;
  color: #adb5bd;
}

.close-button:hover {
  color: #e8eaed;
}

.seat-detail-card h3 {
  color: #000000;
  border-bottom: 1px solid #3c4150;
  padding-bottom: 10px;
  margin-top: 0;
}

.seat-info {
  margin-top: 15px;
}

.seat-info p {
  margin: 12px 0;
  color: #000000;
}

.seat-info p strong {
  color: #000000;
}

.detail-actions {
  margin-top: 25px;
  display: flex;
  justify-content: center;
}

.reserve-btn {
  background-color: #7B97FC;
  color: #1a1d24;
  border: none;
  padding: 10px 18px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s;
}

.reserve-btn:hover {
  background-color: #96ACFD;
  box-shadow: 0 2px 10px rgba(123, 151, 252, 0.3);
}
</style>