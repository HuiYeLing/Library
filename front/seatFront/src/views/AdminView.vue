<template>
  <div class="admin-layout">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <div class="sidebar-title">后台管理</div>
      <ul>
        <li :class="{active: activeTab==='user'}" @click="activeTab='user'">用户管理</li>
        <li :class="{active: activeTab==='staff'}" @click="activeTab='staff'">员工管理</li>
        <li :class="{active: activeTab==='seat'}" @click="activeTab='seat'">座位管理</li>
        <li :class="{active: activeTab==='product'}" @click="activeTab='product'">商品管理</li>
        <li :class="{active: activeTab==='order'}" @click="activeTab='order'">订单管理</li>
      </ul>
    </aside>
    <!-- 右侧内容区 -->
    <main class="main-content">
      <!-- 用户管理 -->
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
              <button class="delete-btn" @click.stop="deleteUser(user.id)">删除</button>
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

      <!-- 员工管理 -->
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

      <!-- 座位管理 -->
      <div v-else-if="activeTab==='seat'" class="panel seat-panel">
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

      <!-- 商品管理 -->
      <div v-else-if="activeTab==='product'" class="panel product-panel">
        <h2>商品管理</h2>
        <div class="search-bar">
          <input v-model="productSearchText" placeholder="输入商品名搜索" @keyup.enter="searchProduct"/>
          <button @click="searchProduct" :disabled="productLoading">🔍 搜索</button>
          <button @click="resetProductSearch" :disabled="productLoading">♻️ 重置</button>
          <button @click="showAddProduct = true">新增商品</button>
          <button class="refresh-btn" @click="fetchProducts" :disabled="productLoading">
            {{ productLoading ? '⏳ 加载中...' : '🔄 刷新列表' }}
          </button>
        </div>
        <div v-if="filteredProducts.length" class="product-card-list">
          <div class="product-card" v-for="product in filteredProducts" :key="product.id">
            <div class="product-title">{{ product.name }}</div>
            <div class="product-info">ID：{{ product.id }}</div>
            <div class="product-info">价格：￥{{ product.price }}</div>
            <div class="product-info">库存：{{ product.stock }}</div>
            <div class="product-actions">
              <button class="seat-action reserve" @click="editProduct(product)">编辑</button>
              <button class="delete-btn" @click="deleteProduct(product.id)">删除</button>
            </div>
          </div>
        </div>
        <div v-else class="empty-tip">暂无商品数据</div>
        <!-- 新增/编辑商品弹窗 -->
        <div v-if="showAddProduct" class="modal">
          <div class="modal-content">
            <h3>{{ editingProduct ? '编辑商品' : '新增商品' }}</h3>
            <form @submit.prevent="submitProduct">
              <input v-model="productForm.name" placeholder="商品名称" required />
              <input v-model.number="productForm.price" placeholder="价格" type="number" required />
              <input v-model.number="productForm.stock" placeholder="库存" type="number" required />
              <div class="modal-actions">
                <button type="submit" :disabled="productLoading">提交</button>
                <button type="button" @click="closeProductModal">取消</button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 订单管理 -->
<div v-else-if="activeTab==='order'" class="panel order-panel">
  <h2>订单管理</h2>
  <div class="search-bar">
    <input v-model="orderSearchText" placeholder="输入用户ID或订单ID搜索" @keyup.enter="searchOrder"/>
    <button @click="searchOrder" :disabled="orderLoading">🔍 搜索</button>
    <button @click="resetOrderSearch" :disabled="orderLoading">♻️ 重置</button>
    <button class="refresh-btn" @click="fetchOrders" :disabled="orderLoading">
      {{ orderLoading ? '⏳ 加载中...' : '🔄 刷新列表' }}
    </button>
  </div>
  <div v-if="filteredOrders.length" class="order-table-wrapper">
    <table class="order-card-table">
      <thead>
        <tr>
          <th>订单号</th>
          <th>用户ID</th>
          <th>用户名</th>
          <th>商品ID</th>
          <th>商品名</th>
          <th>价格</th>
          <th>下单时间</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in filteredOrders" :key="order.id">
  <td>{{ order.id }}</td>
  <td>{{ order.userId }}</td>
  <td>{{ order.username }}</td>
  <td>{{ order.productId }}</td>
  <td>{{ order.productName }}</td>
  <td>￥{{ (!isNaN(Number(order.totalPrice)) && order.totalPrice !== null && order.totalPrice !== '') ? Number(order.totalPrice).toFixed(2) : '0.00' }}</td>
  <td>{{ order.createTime }}</td>
  <td>
    <button
      :class="{'delivered': order.status == 1}"
      @click="deliverOrder(order)"
      :disabled="order.status == 1"
    >
      {{ order.status == 1 ? '已送达' : '待送' }}
    </button>
  </td>
  <td>
    <button class="delete-btn" @click="deleteOrder(order.id)">删除</button>
  </td>
</tr>
      </tbody>
    </table>
  </div>
  <div v-else class="empty-tip">暂无订单数据</div>
</div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import "../css/admin.css"
const backendUrl = 'http://localhost:8080'
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

// 商品管理
const products = ref([])
const productLoading = ref(false)
const productSearchText = ref('')
const showAddProduct = ref(false)
const editingProduct = ref(null)
const productForm = ref({ name: '', price: '', stock: '' })

// 订单管理
const orders = ref([])
const orderLoading = ref(false)
const orderSearchText = ref('')

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
  fetchProducts()
  fetchOrders()
})

// 新增员工弹窗相关
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

// 商品管理相关（无图片字段）
const fetchProducts = async () => {
  productLoading.value = true
  try {
    const res = await axios.get('/api/product/getAll')
    if (res.data.code === 200) {
      products.value = res.data.data
    }
  } finally {
    productLoading.value = false
  }
}

const addProduct = async () => {
  productLoading.value = true
  try {
    const res = await axios.post('/api/product/add', productForm.value)
    if (res.data.code === 200) {
      showAddProduct.value = false
      await fetchProducts()
      productForm.value = { name: '', price: '', stock: '' }
    } else {
      alert(res.data.msg || '新增失败')
    }
  } finally {
    productLoading.value = false
  }
}

const updateProduct = async () => {
  productLoading.value = true
  try {
    const res = await axios.put('/api/product/update', { ...productForm.value, id: editingProduct.value.id })
    if (res.data.code === 200) {
      showAddProduct.value = false
      await fetchProducts()
      editingProduct.value = null
      productForm.value = { name: '', price: '', stock: '' }
    } else {
      alert(res.data.msg || '更新失败')
    }
  } finally {
    productLoading.value = false
  }
}

const deleteProduct = async (id) => {
  if (!confirm('确定要删除该商品吗？')) return
  productLoading.value = true
  try {
    const res = await axios.delete('/api/product/delete', { params: { id } })
    if (res.data.code === 200) {
      await fetchProducts()
    } else {
      alert(res.data.msg || '删除失败')
    }
  } finally {
    productLoading.value = false
  }
}

const editProduct = (product) => {
  editingProduct.value = product
  productForm.value = {
    name: product.name,
    price: product.price,
    stock: product.stock
  }
  showAddProduct.value = true
}

const submitProduct = () => {
  if (editingProduct.value) {
    updateProduct()
  } else {
    addProduct()
  }
}

const closeProductModal = () => {
  showAddProduct.value = false
  editingProduct.value = null
  productForm.value = { name: '', price: '', stock: '' }
}

const searchProduct = () => {
  if (!productSearchText.value.trim()) {
    fetchProducts()
    return
  }
  const keyword = productSearchText.value.trim()
  products.value = products.value.filter(p => p.name.includes(keyword))
}

const resetProductSearch = () => {
  productSearchText.value = ''
  fetchProducts()
}

const filteredProducts = computed(() => {
  if (!productSearchText.value.trim()) return products.value
  return products.value.filter(p => p.name.includes(productSearchText.value.trim()))
})

// 订单管理相关
const fetchOrders = async () => {
  orderLoading.value = true
  try {
    const res = await axios.get('/api/order/getAll')
    if (res.data.code === 200) {
      orders.value = res.data.data
    }
  } finally {
    orderLoading.value = false
  }
}

const deleteOrder = async (id) => {
  if (!confirm('确定要删除该订单吗？')) return
  orderLoading.value = true
  try {
    const res = await axios.delete('/api/order/delete', { params: { id } })
    if (res.data.code === 200) {
      await fetchOrders()
    } else {
      alert(res.data.msg || '删除失败')
    }
  } finally {
    orderLoading.value = false
  }
}

const searchOrder = async () => {
  if (!orderSearchText.value.trim()) {
    await fetchOrders()
    return
  }
  const keyword = orderSearchText.value.trim()
  if (/^\d+$/.test(keyword)) {
    orderLoading.value = true
    try {
      const res = await axios.get('/api/order/getById', { params: { id: keyword } })
      if (res.data.code === 200 && res.data.data) {
        orders.value = [res.data.data]
      } else {
        const res2 = await axios.get('/api/order/getByUserId', { params: { userId: keyword } })
        if (res2.data.code === 200 && Array.isArray(res2.data.data)) {
          orders.value = res2.data.data
        } else {
          orders.value = []
        }
      }
    } finally {
      orderLoading.value = false
    }
  } else {
    orders.value = []
  }
}

const resetOrderSearch = async () => {
  orderSearchText.value = ''
  await fetchOrders()
}

const filteredOrders = computed(() => {
  if (!orderSearchText.value.trim()) return orders.value
  const keyword = orderSearchText.value.trim()
  return orders.value.filter(
    o =>
      String(o.id).includes(keyword) ||
      String(o.userId).includes(keyword)
  )
})

const deliverOrder = async (order) => {
  if (order.status == 1) return
  const res = await axios.post('/api/order/deliver', null, {
    params: { orderId: order.id }
  })
  if (res.data.code === 200) {
    order.status = 1
  } else {
    alert(res.data.msg || '操作失败')
  }
}
</script>