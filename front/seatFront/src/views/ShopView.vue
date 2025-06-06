<template>
    <div>
      <h2>小卖部</h2>
      <button @click="fetchProducts">刷新商品列表</button>
      <div class="product-card-list">
        <div class="product-card" v-for="p in products" :key="p.id">
          <div class="product-info">
            <div class="product-name">{{ p.name }}</div>
            <div>ID: {{ p.id }}</div>
            <div>价格: {{ p.price }}</div>
            <div>库存: {{ p.stock }}</div>
          </div>
          <button @click="buyProduct(p.id)" :disabled="p.stock <= 0">
            {{ p.stock > 0 ? '购买' : '缺货' }}
          </button>
        </div>
      </div>
      <div v-if="!products.length" style="margin: 16px 0;">暂无商品</div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  
  const products = ref([])
  
  const fetchProducts = async () => {
    const res = await axios.get('/api/product/getAll')
    if (res.data.code === 200) {
      products.value = res.data.data
    }
  }
  
  const buyProduct = async (id) => {
    const res = await axios.post('/api/product/buy', { id })
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
  .product-card-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    margin-top: 20px;
  }
  .product-card {
    flex: 1 1 220px;
    max-width: 260px;
    min-width: 180px;
    background: #fff;
    border: 1px solid #eee;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
    padding: 18px 16px 14px 16px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: space-between;
  }
  .product-info {
    margin-bottom: 12px;
  }
  .product-name {
    font-weight: bold;
    font-size: 18px;
    margin-bottom: 8px;
  }
  .product-card button {
    align-self: stretch;
    padding: 8px 0;
    border: none;
    border-radius: 4px;
    background: #409eff;
    color: #fff;
    font-size: 15px;
    cursor: pointer;
    transition: background 0.2s;
  }
  .product-card button:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
  @media (max-width: 600px) {
    .product-card-list {
      flex-direction: column;
      gap: 14px;
    }
    .product-card {
      max-width: 100%;
      min-width: 0;
    }
  }
  </style>