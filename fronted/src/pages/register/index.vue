<template>
  <view class="register-page">
    <!-- 顶部 -->
    <view class="register-header">
      <view class="back-btn" @tap="goBack">‹</view>
      <text class="header-title">创建账号</text>
    </view>

    <scroll-view scroll-y class="register-scroll">
      <view class="register-hero">
        <view class="brand-logo">🏫</view>
        <text class="brand-title">加入校园微墙</text>
        <text class="brand-sub">填写信息，开始你的校园树洞之旅</text>
      </view>

      <view class="form-card">
        <view class="form-row">
          <text class="form-label">用户名 *</text>
          <input
            class="form-input"
            v-model.trim="form.username"
            placeholder="2-20 位，用于登录"
            :maxlength="20"
          />
        </view>

        <view class="form-row">
          <text class="form-label">密码 *</text>
          <input
            class="form-input"
            v-model="form.password"
            password
            placeholder="6-32 位"
            :maxlength="32"
          />
        </view>

        <view class="form-row">
          <text class="form-label">确认密码 *</text>
          <input
            class="form-input"
            v-model="form.confirmPassword"
            password
            placeholder="再次输入密码"
            :maxlength="32"
          />
        </view>

        <view class="form-row">
          <text class="form-label">昵称 *</text>
          <input
            class="form-input"
            v-model.trim="form.nickname"
            placeholder="2-50 字符，可显示给其他同学"
            :maxlength="50"
          />
        </view>

        <view class="form-row">
          <text class="form-label">学校</text>
          <input
            class="form-input"
            v-model.trim="form.school"
            placeholder="选填，如：某某大学"
            :maxlength="100"
          />
        </view>

        <view class="form-row">
          <text class="form-label">个人简介</text>
          <textarea
            class="form-textarea"
            v-model.trim="form.bio"
            placeholder="选填，最多 100 字"
            :maxlength="100"
            auto-height
          />
        </view>
      </view>

      <view
        class="submit-btn"
        :class="{ active: canSubmit }"
        @tap="onSubmit"
      >
        <text v-if="!submitting">注册并登录</text>
        <text v-else>注册中...</text>
      </view>

      <view class="bottom-tip">
        <text class="tip-text">已有账号？</text>
        <text class="link-text" @tap="goLogin">去登录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { reactive, ref, computed } from "vue";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const submitting = ref(false);

const form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  nickname: "",
  school: "",
  bio: "",
});

const canSubmit = computed(() => {
  return (
    !submitting.value &&
    form.username.length >= 2 &&
    form.password.length >= 6 &&
    form.confirmPassword.length >= 6 &&
    form.nickname.length >= 2
  );
});

function goBack() {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack();
  } else {
    uni.redirectTo({ url: "/pages/login/index" });
  }
}

function goLogin() {
  uni.redirectTo({ url: "/pages/login/index" });
}

function validate() {
  if (form.username.length < 2 || form.username.length > 20) {
    return "用户名长度需在 2-20 位之间";
  }
  if (form.password.length < 6 || form.password.length > 32) {
    return "密码长度需在 6-32 位之间";
  }
  if (form.password !== form.confirmPassword) {
    return "两次输入的密码不一致";
  }
  if (form.nickname.length < 2 || form.nickname.length > 50) {
    return "昵称长度需在 2-50 字符之间";
  }
  if (form.school && form.school.length > 100) {
    return "学校名称过长";
  }
  if (form.bio && form.bio.length > 100) {
    return "个人简介不能超过 100 字";
  }
  return "";
}

async function onSubmit() {
  if (!canSubmit.value) return;
  const errMsg = validate();
  if (errMsg) {
    uni.showToast({ title: errMsg, icon: "none" });
    return;
  }
  submitting.value = true;
  try {
    await userStore.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      school: form.school || undefined,
      bio: form.bio || undefined,
    });
    uni.showToast({ title: "注册成功", icon: "success" });
    setTimeout(() => {
      uni.switchTab({ url: "/pages/home/index" });
    }, 600);
  } catch (error) {
    uni.showToast({
      title: error?.message || "注册失败，请重试",
      icon: "none",
    });
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #ffffff;
  display: flex;
  flex-direction: column;
}

.register-header {
  position: relative;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: var(--status-bar-height, 40rpx);
  background: #ffffff;
  border-bottom: 1rpx solid var(--border);
}

.back-btn {
  position: absolute;
  left: 24rpx;
  top: 50%;
  transform: translateY(-25%);
  width: 60rpx;
  height: 60rpx;
  font-size: 56rpx;
  color: var(--text-main);
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.header-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-main);
}

.register-scroll {
  flex: 1;
  padding: 0 32rpx 60rpx;
}

.register-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 0 36rpx;
}

.brand-logo {
  width: 96rpx;
  height: 96rpx;
  border-radius: 28rpx;
  background: var(--primary-light);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  margin-bottom: 16rpx;
}

.brand-title {
  font-size: 36rpx;
  font-weight: 700;
  color: var(--text-main);
}

.brand-sub {
  font-size: 24rpx;
  color: var(--text-hint);
  margin-top: 6rpx;
}

.form-card {
  background: #ffffff;
  border-radius: 24rpx;
  border: 1rpx solid var(--border);
  padding: 8rpx 28rpx;
}

.form-row {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.form-row:last-child {
  border-bottom: none;
}

.form-label {
  font-size: 26rpx;
  color: var(--text-sub);
  font-weight: 500;
}

.form-input {
  font-size: 30rpx;
  color: var(--text-main);
  height: 64rpx;
  width: 100%;
}

.form-textarea {
  font-size: 28rpx;
  color: var(--text-main);
  width: 100%;
  min-height: 80rpx;
  line-height: 1.6;
}

.submit-btn {
  margin-top: 40rpx;
  height: 96rpx;
  border-radius: 200rpx;
  background: #f5f5f5;
  color: #bbbbbb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
  transition: all 0.2s;
}

.submit-btn.active {
  background: var(--primary);
  color: #ffffff;
}

.submit-btn:active {
  transform: scale(0.98);
}

.bottom-tip {
  margin-top: 28rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8rpx;
}

.tip-text {
  font-size: 26rpx;
  color: var(--text-hint);
}

.link-text {
  font-size: 26rpx;
  color: var(--primary);
  font-weight: 500;
}
</style>
