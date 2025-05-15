<script setup>
// 不需要登录相关的逻辑
</script>

<template>
  <div id="app">
    <!-- router-view 用于渲染当前路由对应的组件 -->
    <router-view />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  overflow: hidden;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

#app {
  height: 100%;
  width: 100%;
}
</style>