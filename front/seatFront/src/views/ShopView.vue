<template>
    <div class="shop-container">
      <h2 class="shop-title">🛒 小卖部</h2>
      <button class="refresh-btn" @click="fetchProducts">🔄 刷新商品列表</button>
      <div class="product-card-list">
        <div class="product-card" v-for="p in products" :key="p.id">
          <div class="product-info">
            <div class="product-name">{{ p.name }}</div>
            <div class="product-meta">
              <span>ID: {{ p.id }}</span>
              <span>价格: <b>￥{{ p.price }}</b></span>
              <span>库存: <b :class="{ 'out-stock': p.stock <= 0 }">{{ p.stock }}</b></span>
            </div>
          </div>
          <button
            class="buy-btn"
            @click="buyProduct(p.id)"
            :disabled="p.stock <= 0"
          >
            {{ p.stock > 0 ? '购买' : '缺货' }}
          </button>
        </div>
      </div>
      <div v-if="!products.length" class="empty-tip">暂无商品</div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  
  const products = ref([])
  
  // 直接在需要时获取 userId
  const fetchProducts = async () => {
    const res = await axios.get('/api/product/getAll')
    if (res.data.code === 200) {
      products.value = res.data.data
    }
  }
  
  const buyProduct = async (productId) => {
    const userId = localStorage.getItem('userId')
    if (!userId || userId === 'null' || userId === 'undefined') {
      alert('请先登录')
      return
    }
    const res = await axios.post(`/api/product/buy?userId=${userId}&productId=${productId}`)
    if (res.data.code === 200) {
      alert('购买成功！')
      fetchProducts()
    } else {
      alert(res.data.msg || '购买失败')
    }
  }
  
  onMounted(fetchProducts)
  </script>

  <style scoped>
  .shop-container {
    max-width: 900px;
    margin: 32px auto 0 auto;
    padding: 24px 18px 40px 18px;
    background: #f8fafc;
    border-radius: 16px;
    box-shadow: 0 4px 24px rgba(0,0,0,0.07);
    min-height: 80vh;
  }
  .shop-title {
    font-size: 2rem;
    font-weight: 700;
    color: #409eff;
    margin-bottom: 18px;
    letter-spacing: 2px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .refresh-btn {
    background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
    color: #fff;
    border: none;
    border-radius: 6px;
    padding: 8px 18px;
    font-size: 15px;
    margin-bottom: 18px;
    cursor: pointer;
    transition: background 0.2s, box-shadow 0.2s;
    box-shadow: 0 2px 8px rgba(64,158,255,0.08);
  }
  .refresh-btn:hover {
    background: linear-gradient(90deg, #66b1ff 0%, #409eff 100%);
  }
  .product-card-list {
    display: flex;
    flex-wrap: wrap;
    gap: 24px;
    margin-top: 10px;
  }
  .product-card {
    flex: 1 1 220px;
    max-width: 260px;
    min-width: 180px;
    background: #fff;
    border: 1px solid #e6e8eb;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(64,158,255,0.07);
    padding: 22px 18px 16px 18px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: space-between;
    transition: box-shadow 0.2s, transform 0.2s;
  }
  .product-card:hover {
    box-shadow: 0 8px 32px rgba(64,158,255,0.13);
    transform: translateY(-3px) scale(1.03);
  }
  .product-info {
    margin-bottom: 16px;
    width: 100%;
  }
  .product-name {
    font-weight: bold;
    font-size: 20px;
    margin-bottom: 10px;
    color: #222;
    letter-spacing: 1px;
  }
  .product-meta {
    display: flex;
    flex-direction: column;
    gap: 4px;
    font-size: 15px;
    color: #666;
  }
  .product-meta b {
    color: #409eff;
    font-weight: 600;
  }
  .product-meta .out-stock {
    color: #e74c3c;
  }
  .buy-btn {
    align-self: stretch;
    padding: 10px 0;
    border: none;
    border-radius: 6px;
    background: linear-gradient(90deg, #67c23a 0%, #409eff 100%);
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s, box-shadow 0.2s;
    box-shadow: 0 2px 8px rgba(103,194,58,0.08);
  }
  .buy-btn:hover:not(:disabled) {
    background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
    box-shadow: 0 4px 16px rgba(103,194,58,0.13);
  }
  .buy-btn:disabled {
    background: #ccc;
    cursor: not-allowed;
    color: #fff;
    opacity: 0.7;
  }
  .empty-tip {
    margin: 32px 0;
    color: #aaa;
    font-size: 17px;
    text-align: center;
  }
  @media (max-width: 700px) {
    .shop-container {
      padding: 10px 2vw 30px 2vw;
    }
    .product-card-list {
      flex-direction: column;
      gap: 16px;
    }
    .product-card {
      max-width: 100%;
      min-width: 0;
    }
  }
  </style>