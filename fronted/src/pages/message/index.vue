<template>
  <view class="msg-page">
    <!-- <view class="nav-bar">
      <view class="nav-inner">
        <text class="nav-title">私信</text>
        <view class="nav-icon-btn" @tap="newChat">✏️</view>
      </view>
    </view> -->

    <scroll-view scroll-y class="scroll-area">
      <!-- 搜索框 -->
      <view class="search-wrap">
        <view class="search-box">
          <text class="search-icon">🔍</text>
          <input
            class="search-input"
            placeholder="搜索联系人或聊天记录"
            placeholder-class="ph-color"
          />
        </view>
      </view>

      <!-- 会话列表 -->
      <view class="conv-list">
        <view
          v-for="conv in conversations"
          :key="conv.id"
          class="conv-item"
          hover-class="conv-item-hover"
          @tap="goChat(conv)"
        >
          <view class="conv-avatar-wrap">
            <image
              v-if="String(conv.avatar).includes('/')"
              :src="conv.avatar"
              class="conv-avatar"
              mode="aspectFill"
            />
            <view v-else class="conv-avatar">{{ conv.avatar }}</view>
            <view v-if="conv.unread > 0" class="unread-badge">{{
              conv.unread > 99 ? "99+" : conv.unread
            }}</view>
          </view>
          <view class="conv-info">
            <view class="conv-top">
              <text class="conv-name">{{ conv.name }}</text>
              <text class="conv-time">{{ conv.lastTime }}</text>
            </view>
            <view class="conv-bottom">
              <text
                class="conv-last-msg"
                :class="{ unread: conv.unread > 0 }"
                >{{ conv.lastMsg }}</text
              >
            </view>
          </view>
        </view>
      </view>

      <view class="list-tip">
        <text class="list-tip-text">— 仅展示最近 30 天的私信 —</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { computed } from "vue";
import { onShow, onLoad } from "@dcloudio/uni-app";
import { useChatStore } from "@/stores/chat";
import { useUserStore } from "@/stores/user";

const chatStore = useChatStore();
const userStore = useUserStore();
const conversations = computed(() => chatStore.conversationList);

onLoad(() => {
});

onShow(() => {
  if (!userStore.isLoggedIn) {
    return;
  }
  chatStore.fetchConversations().catch((error) => {
    uni.showToast({ title: error?.message || "加载失败", icon: "none" });
  });
});

function goChat(conv) {
  chatStore.markRead(conv.userId).catch(() => {});
  uni.navigateTo({
    url: `/pages/message/chat?userId=${conv.userId}&name=${encodeURIComponent(conv.name)}&avatar=${encodeURIComponent(conv.avatar)}`,
  });
}

function newChat() {
  uni.showToast({ title: "请从帖子详情页发起私信", icon: "none" });
}
</script>

<style scoped>
.msg-page {
  min-height: 100vh;
  background: #ffffff;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  background: #ffffff;
  padding-top: var(--status-bar-height, 44px);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
}

.nav-title {
  font-size: 40rpx;
  font-weight: 800;
  color: var(--text-main);
  letter-spacing: 1rpx;
}

.nav-icon-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: var(--bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
}

.scroll-area {
  flex: 1;
}

/* 搜索框 */
.search-wrap {
  padding: 16rpx 32rpx 24rpx;
}

.search-box {
  background: var(--bg);
  border-radius: 20rpx;
  padding: 0 24rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.search-icon {
  font-size: 30rpx;
  color: var(--text-hint);
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: var(--text-main);
}

.ph-color {
  color: var(--text-hint);
}

/* 会话列表 */
.conv-list {
  padding: 0 8rpx;
}

.conv-item {
  display: flex;
  align-items: center;
  padding: 28rpx 24rpx;
  gap: 24rpx;
  transition: background-color 0.2s;
  border-radius: 24rpx;
  margin: 0 16rpx 4rpx;
}

.conv-item-hover {
  background-color: #f9f9f9;
}

.conv-avatar-wrap {
  position: relative;
  flex-shrink: 0;
}

.conv-avatar {
  width: 108rpx;
  height: 108rpx;
  border-radius: 36rpx;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 52rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.unread-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  min-width: 36rpx;
  height: 36rpx;
  background: #ff4d4f;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
  color: #fff;
  font-weight: bold;
  padding: 0 8rpx;
  border: 4rpx solid #fff;
}

.conv-info {
  flex: 1;
  min-width: 0;
}

.conv-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8rpx;
}

.conv-name {
  font-size: 32rpx;
  font-weight: 700;
  color: var(--text-main);
}

.conv-time {
  font-size: 22rpx;
  color: var(--text-hint);
}

.conv-bottom {
  display: flex;
  align-items: center;
}

.conv-last-msg {
  font-size: 26rpx;
  color: var(--text-sub);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.conv-last-msg.unread {
  color: var(--text-main);
  font-weight: 500;
}

.list-tip {
  text-align: center;
  padding: 60rpx 0 80rpx;
}

.list-tip-text {
  font-size: 22rpx;
  color: var(--text-hint);
  letter-spacing: 2rpx;
}
</style>
