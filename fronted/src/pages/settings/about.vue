<template>
  <view class="container">
    <view class="logo-section">
      <view class="logo">🏫</view>
      <text class="app-name">校园墙</text>
      <text class="version">Version 1.0.0</text>
    </view>

    <view class="list-section">
      <view class="item" @tap="checkUpdate">
        <text>检查更新</text>
        <text class="arrow">›</text>
      </view>
      <view class="item">
        <text>官方网站</text>
        <text class="value">www.schoolwall.com</text>
      </view>
      <view class="item">
        <text>联系我们</text>
        <text class="value">support@schoolwall.com</text>
      </view>
    </view>

    <view class="footer">
      <text>© 2024 校园墙项目组 版权所有</text>
    </view>
  </view>
</template>

<script setup>
function checkUpdate() {
  uni.showLoading({ title: '检查中...' });
  setTimeout(() => {
    uni.hideLoading();
    uni.showToast({ title: '已是最新版本', icon: 'none' });
  }, 1000);
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background-color: #f8f8f8;
}
.logo-section {
  padding: 100rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #fff;
}
.logo {
  width: 160rpx;
  height: 160rpx;
  background: linear-gradient(135deg, var(--primary, #ff5a35), var(--primary-mid, #ff7a5a));
  border-radius: 40rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 80rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 10rpx 30rpx rgba(255, 90, 53, 0.3);
}
.app-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}
.version {
  font-size: 24rpx;
  color: #999;
}
.list-section {
  margin-top: 20rpx;
  background-color: #fff;
}
.item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
  font-size: 30rpx;
  color: #333;
}
.item:last-child {
  border-bottom: none;
}
.arrow {
  color: #ccc;
  font-size: 40rpx;
}
.value {
  color: #999;
  font-size: 28rpx;
}
.footer {
  position: fixed;
  bottom: 40rpx;
  width: 100%;
  text-align: center;
}
.footer text {
  font-size: 22rpx;
  color: #ccc;
}
</style>
