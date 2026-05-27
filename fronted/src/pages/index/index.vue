<template>
  <view class="splash-screen">
    <text>正在加载应用环境...</text>
  </view>
</template>

<script setup>
import { onMounted } from "vue";

onMounted(() => {
  uni.switchTab({ url: "/pages/home/index" });
});
</script>

<style scoped>
.splash-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 14px;
  color: #999;
}
</style>
