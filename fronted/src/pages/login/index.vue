<template>
  <view class="login-page">
    <!-- 上部分：品牌区域 -->
    <view class="login-hero">
      <view class="login-logo">🏫</view>
      <text class="login-title">校园微墙</text>
      <text class="login-sub">你的校园匿名树洞</text>

      <view class="login-features">
        <view class="feat-item">
          <text class="feat-icon">📢</text>
          <text class="feat-text">匿名发布，大胆说真话</text>
        </view>
        <view class="feat-item">
          <text class="feat-icon">💬</text>
          <text class="feat-text">互动讨论，同校学生专属</text>
        </view>
        <view class="feat-item">
          <text class="feat-icon">🤝</text>
          <text class="feat-text">私信交友，拓展校园圈子</text>
        </view>
      </view>
    </view>

    <!-- 下部分：登录操作区 -->
    <view class="login-bottom">
      <!-- 账号密码登录区 -->
      <view v-if="mode === 'password'" class="pwd-form">
        <input
          class="pwd-input"
          v-model.trim="username"
          placeholder="用户名"
          :maxlength="20"
        />
        <input
          class="pwd-input"
          v-model="password"
          password
          placeholder="密码"
          :maxlength="32"
        />
        <button
          class="primary-btn"
          :loading="loading"
          :disabled="loading || !username || !password"
          @tap="handlePwdLogin"
        >
          <text v-if="!loading">登录</text>
          <text v-else>登录中...</text>
        </button>
        <view class="switch-row">
          <text class="link-text" @tap="goRegister">没有账号？去注册</text>
          <text class="link-text" @tap="mode = 'wechat'">微信登录</text>
        </view>
      </view>

      <!-- 微信登录区 -->
      <view v-else class="wx-form">
        <button
          class="wx-login-btn"
          :loading="loading"
          :disabled="loading"
          @tap="handleWxLogin"
        >
          <text v-if="!loading">微信一键登录</text>
          <text v-else>登录中...</text>
        </button>

        <view class="switch-row">
          <text class="link-text" @tap="mode = 'password'">账号密码登录</text>
          <text class="link-text" @tap="goRegister">注册新账号</text>
        </view>
      </view>

      <text class="login-tip"> 登录即代表同意《用户协议》和《隐私政策》 </text>
      <text class="login-tip school-tip">仅本校学生可使用本应用</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const loading = ref(false);
const mode = ref("wechat");
const username = ref("");
const password = ref("");

async function handleWxLogin() {
  if (loading.value) return;
  loading.value = true;
  try {
    await userStore.loginWithWechat();
    uni.switchTab({ url: "/pages/home/index" });
  } catch (e) {
    uni.showToast({ title: e?.message || "登录失败，请重试", icon: "none" });
  } finally {
    loading.value = false;
  }
}

async function handlePwdLogin() {
  if (loading.value) return;
  if (!username.value || !password.value) {
    uni.showToast({ title: "请输入用户名和密码", icon: "none" });
    return;
  }
  loading.value = true;
  try {
    await userStore.loginWithUsername({
      username: username.value,
      password: password.value,
    });
    uni.switchTab({ url: "/pages/home/index" });
  } catch (e) {
    uni.showToast({ title: e?.message || "登录失败，请重试", icon: "none" });
  } finally {
    loading.value = false;
  }
}

function goRegister() {
  uni.navigateTo({ url: "/pages/register/index" });
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

/* ---- 品牌区域 ---- */
.login-hero {
  flex: 1;
  background: linear-gradient(160deg, #ff5a35 0%, #ff7a55 55%, #ffb49a 100%);
  padding: 100rpx 48rpx 72rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-logo {
  width: 128rpx;
  height: 128rpx;
  background: rgba(255, 255, 255, 0.22);
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64rpx;
  margin-bottom: 24rpx;
}

.login-title {
  font-size: 52rpx;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 4rpx;
  margin-bottom: 10rpx;
}

.login-sub {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 56rpx;
}

.login-features {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.feat-item {
  background: rgba(255, 255, 255, 0.18);
  border-radius: 20rpx;
  padding: 22rpx 28rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.feat-icon {
  font-size: 32rpx;
  flex-shrink: 0;
}

.feat-text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 500;
}

/* ---- 操作区域 ---- */
.login-bottom {
  background: #ffffff;
  padding: 48rpx 48rpx 80rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.pwd-form,
.wx-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.pwd-input {
  width: 100%;
  height: 88rpx;
  padding: 0 28rpx;
  border-radius: 16rpx;
  background: var(--bg);
  font-size: 30rpx;
  color: var(--text-main);
  box-sizing: border-box;
}

.primary-btn {
  width: 100%;
  height: 96rpx;
  background: var(--primary) !important;
  color: #ffffff !important;
  border-radius: 200rpx !important;
  font-size: 32rpx !important;
  font-weight: 600 !important;
  border: none !important;
  display: flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 2rpx;
}

.primary-btn[disabled] {
  background: #f0c2b3 !important;
  color: #ffffff !important;
}

.primary-btn::after {
  border: none;
}

.switch-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8rpx 4rpx;
}

.link-text {
  font-size: 26rpx;
  color: var(--primary);
  font-weight: 500;
}

.wx-login-btn {
  width: 100%;
  height: 96rpx;
  background: #07c160 !important;
  color: #ffffff !important;
  border-radius: 200rpx !important;
  font-size: 32rpx !important;
  font-weight: 600 !important;
  border: none !important;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  letter-spacing: 2rpx;
}

.wx-login-btn::after {
  border: none;
}

.login-tip {
  font-size: 22rpx;
  color: var(--text-hint);
  text-align: center;
  line-height: 1.6;
}

.school-tip {
  margin-top: -8rpx;
}
</style>
