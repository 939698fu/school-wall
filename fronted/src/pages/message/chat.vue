<template>
  <view class="chat-page">
    <!-- 导航栏 -->
    <view class="nav-bar">
      <view class="nav-inner">
        <!-- <view class="back-btn" @tap="goBack">
           <text class="back-icon">‹</text>
        </view> -->
        <view class="nav-center">
          <text class="nav-name">{{ chatName }}</text>
          <text class="nav-status">在线</text>
        </view>
        <view class="nav-more" @tap="showMore">⋯</view>
      </view>
    </view>

    <!-- 消息列表 -->
    <scroll-view
      scroll-y
      class="msg-scroll"
      :scroll-top="scrollTop"
      :scroll-into-view="lastMsgId"
      scroll-with-animation
      :style="{ paddingBottom: keyboardHeight + 'px' }"
    >
      <view class="msg-list">
        <view class="chat-date-tip">{{ today }}</view>

        <view
          v-for="msg in messages"
          :key="msg.id"
          class="msg-row"
          :class="{ 'msg-row-me': msg.fromMe }"
          :id="`msg-${msg.id}`"
        >
          <!-- 对方头像 -->
          <view v-if="!msg.fromMe" class="chat-avatar-wrap">
            <image
              v-if="String(chatAvatar).includes('/')"
              :src="chatAvatar"
              class="chat-avatar chat-avatar-image"
              mode="aspectFill"
            />
            <view v-else class="chat-avatar">{{ chatAvatar }}</view>
          </view>

        <!-- 消息主体 -->
        <view class="bubble-wrap">
          <view
            class="bubble"
            :class="[
              msg.fromMe ? 'bubble-me' : 'bubble-other',
              msg.type === 'image' ? 'bubble-image' : '',
            ]"
          >
              <text v-if="msg.type === 'text'" class="bubble-text">{{ msg.content }}</text>
              <image
                v-else-if="msg.type === 'image'"
                :src="msg.imageUrl"
                class="bubble-image-inner"
                mode="widthFix"
                @tap="previewImage(msg.imageUrl)"
              />
            </view>
            <view class="msg-meta">
              <text class="msg-time">{{ msg.time }}</text>
              <text v-if="msg.fromMe" class="msg-status">{{ getStatusText(msg.status) }}</text>
            </view>
          </view>

          <!-- 自己头像 -->
          <view v-if="msg.fromMe" class="chat-avatar-wrap">
            <image
              v-if="String(myAvatar).includes('/')"
              :src="myAvatar"
              class="chat-avatar chat-avatar-me chat-avatar-image"
              mode="aspectFill"
            />
            <view v-else class="chat-avatar chat-avatar-me">{{ myAvatar }}</view>
          </view>
        </view>

        <view style="height: 40rpx"></view>
      </view>
    </scroll-view>

    <!-- 输入栏 -->
    <view 
      class="input-bar safe-area-bottom"
      :style="{ transform: `translateY(-${keyboardHeight}px)`, transition: 'transform 0.25s ease' }"
    >
      <view class="input-extra-btn" @tap="toggleExtra">
        <text class="icon">＋</text>
      </view>
      <view class="input-wrap">
        <textarea
          class="chat-input"
          v-model="inputText"
          placeholder="发消息..."
          :auto-height="true"
          :fixed="true"
          :cursor-spacing="20"
          :adjust-position="false"
          maxlength="500"
          @focus="onInputFocus"
          @blur="onInputBlur"
          @keyboardheightchange="onKeyboardHeightChange"
        />
      </view>
      <view v-if="inputText.trim()" class="send-btn-active" @tap="sendMsg">
        <text>发送</text>
      </view>
      <view v-else class="input-emoji-btn" @tap="showEmoji">
        <text class="icon">😊</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import { onLoad, onUnload } from "@dcloudio/uni-app";
import { useChatStore } from "@/stores/chat";
import { useUserStore } from "@/stores/user";

const chatName = ref("用户");
const chatAvatar = ref("👤");
const targetUserId = ref(0);
const chatStore = useChatStore();
const userStore = useUserStore();
const myAvatar = computed(() => userStore.userInfo?.avatar || "🙂");

const keyboardHeight = ref(0);

function onInputFocus(e) {
  // 部分平台可以通过 focus 事件获取键盘高度
  if (e.detail.height) {
    keyboardHeight.value = e.detail.height;
  }
  scrollToBottom();
}

function onInputBlur() {
  keyboardHeight.value = 0;
}

function onKeyboardHeightChange(e) {
  keyboardHeight.value = e.detail.height;
  if (keyboardHeight.value > 0) {
    scrollToBottom();
  }
}

onLoad((options) => {
  const chatId = Number(options.userId || options.id || 0);
  targetUserId.value = chatId;
  if (options.name) chatName.value = decodeURIComponent(options.name);
  if (options.avatar) chatAvatar.value = decodeURIComponent(options.avatar);

  const conv = chatStore.getConversationByUserId(chatId);
  if (conv) {
    chatName.value = options.name ? decodeURIComponent(options.name) : conv.name;
    chatAvatar.value = options.avatar ? decodeURIComponent(options.avatar) : conv.avatar;
  }
  loadChat();
});

onUnload(() => {
  keyboardHeight.value = 0;
});

const inputText = ref("");
const scrollTop = ref(99999);
const lastMsgId = ref("");
const messages = computed(
  () => chatStore.getMessagesByUserId(targetUserId.value) || [],
);

const today = new Date().toLocaleDateString("zh-CN", {
  month: "long",
  day: "numeric",
});

onMounted(() => {
  scrollToBottom();
});

function scrollToBottom() {
  nextTick(() => {
    if (messages.value.length > 0) {
      lastMsgId.value = `msg-${messages.value[messages.value.length - 1].id}`;
    }
    scrollTop.value = 999999;
  });
}

function sendMsg() {
  if (!inputText.value.trim()) return;
  chatStore
    .sendText(targetUserId.value, inputText.value.trim(), userStore.userInfo?.id)
    .then(() => {
      inputText.value = "";
      scrollToBottom();
      chatStore.fetchConversations().catch(() => {});
    })
    .catch(showError);
}

function goBack() {
  uni.navigateBack();
}

function showMore() {
  uni.showActionSheet({
    itemList: ["查看对方主页", "投诉/举报", "清空聊天记录"],
    success: ({ tapIndex }) => {
      if (tapIndex === 0) {
        const conv = chatStore.getConversationByUserId(targetUserId.value);
        if (!conv?.userId) return;
        uni.navigateTo({
          url: `/pages/user-profile/index?userId=${conv.userId}`,
        });
      }
      if (tapIndex === 2) {
        uni.showModal({
          title: "提示",
          content: "确定清空聊天记录吗？",
          success: ({ confirm }) => {
            if (confirm) chatStore.clearMessages(targetUserId.value);
          },
        });
      }
    },
  });
}

function toggleExtra() {
  uni.showActionSheet({
    itemList: ["发送图片"],
    success: ({ tapIndex }) => {
      if (tapIndex === 0) {
        chooseSingleImage();
      }
    },
  });
}

function showEmoji() {
  uni.showToast({ title: "emoji 开发中", icon: "none" });
}

function chooseSingleImage() {
  uni.chooseImage({
    count: 1,
    sizeType: ["compressed"],
    sourceType: ["album", "camera"],
    success: async ({ tempFilePaths }) => {
      const imageUrl = tempFilePaths?.[0];
      if (!imageUrl) return;
      try {
        const uploaded = await chatStore.uploadMessageImage(imageUrl);
        await chatStore.sendImage(
          targetUserId.value,
          uploaded,
          userStore.userInfo?.id,
        );
        scrollToBottom();
        chatStore.fetchConversations().catch(() => {});
      } catch (error) {
        showError(error);
      }
    },
  });
}

function previewImage(imageUrl) {
  if (!imageUrl) return;
  const imageUrls = messages.value
    .filter((item) => item.type === "image" && item.imageUrl)
    .map((item) => item.imageUrl);
  uni.previewImage({
    current: imageUrl,
    urls: imageUrls,
  });
}

function getStatusText(status) {
  const statusMap = {
    pending: "发送中",
    sent: "已发送",
    received: "已送达",
    failed: "发送失败",
  };
  return statusMap[status] || "已发送";
}

async function loadChat() {
  if (!targetUserId.value || !userStore.isLoggedIn) {
    return;
  }
  try {
    await chatStore.fetchChatHistory(targetUserId.value, userStore.userInfo?.id);
    await chatStore.markRead(targetUserId.value);
    scrollToBottom();
  } catch (error) {
    showError(error);
  }
}

function showError(error) {
  uni.showToast({
    title: error?.message || "发送失败",
    icon: "none",
  });
}
</script>

<style scoped>
.chat-page {
  height: 100vh;
  background: #f8f8f8;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.nav-bar {
  background: #ffffff;
  flex-shrink: 0;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 100;
  padding-top: var(--status-bar-height, 44px);
}

.nav-inner {
  display: flex;
  align-items: center;
  padding: 12rpx 28rpx;
  height: 88rpx;
}

.back-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 60rpx;
  color: var(--text-main);
  line-height: 1;
}

.nav-center {
  flex: 1;
  display: flex;
  /* flex-direction: column; */
  align-items: center;
  /* justify-content: left; */
  gap: 10rpx;
}

.nav-name {
  font-size: 32rpx;
  font-weight: 700;
  color: var(--text-main);
  line-height: 1.2;
}

.nav-status {
  font-size: 20rpx;
  margin-top: 4rpx;
}

.nav-more {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  color: var(--text-sub);
}

/* 消息列表 */
.msg-scroll {
  flex: 1;
  height: 100%; /* 使用 100% 确保占满 flex 容器 */
  overflow: hidden;
}

.msg-list {
  padding: 32rpx 24rpx;
}

.chat-date-tip {
  text-align: center;
  font-size: 22rpx;
  color: var(--text-hint);
  margin: 10rpx auto 40rpx;
  background: rgba(0, 0, 0, 0.05);
  padding: 4rpx 20rpx;
  border-radius: 20rpx;
  width: fit-content;
}

.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-bottom: 40rpx;
  width: 100%;
}

.msg-row-me {
  justify-content: flex-end;
}

.chat-avatar-wrap {
  flex-shrink: 0;
}

.chat-avatar {
  width: 84rpx;
  height: 84rpx;
  border-radius: 28rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.chat-avatar-me {
  background: var(--primary);
}

.bubble-wrap {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.msg-row-me .bubble-wrap {
  align-items: flex-end;
}

.bubble {
  padding: 20rpx 28rpx;
  position: relative;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.02);
}

.bubble-image {
  padding: 10rpx;
}

.bubble-other {
  background: #ffffff;
  border-radius: 4rpx 28rpx 28rpx 28rpx;
  color: var(--text-main);
}

.bubble-me {
  background: var(--primary);
  border-radius: 28rpx 4rpx 28rpx 28rpx;
  color: #ffffff;
}

.bubble-text {
  font-size: 30rpx;
  line-height: 1.6;
  word-break: break-all;
}

.bubble-image-inner {
  width: 320rpx;
  max-height: 420rpx;
  border-radius: 20rpx;
  display: block;
  background: #f3f3f3;
}

.msg-meta {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.msg-time {
  font-size: 20rpx;
  color: var(--text-hint);
  margin: 0 8rpx;
}

.msg-status {
  font-size: 20rpx;
  color: var(--text-hint);
}

/* 输入栏 */
.input-bar {
  background: #ffffff;
  border-top: 1rpx solid #eeeeee;
  padding: 20rpx 24rpx;
  padding-bottom: 50rpx;
  display: flex;
  align-items: flex-end;
  gap: 20rpx;
  flex-shrink: 0;
}

.input-extra-btn,
.input-emoji-btn {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
  flex-shrink: 0;
}

.icon {
  font-size: 40rpx;
  color: var(--text-sub);
}

.input-wrap {
  flex: 1;
  background: #f5f5f5;
  border-radius: 20rpx;
  padding: 0 24rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
}

.chat-input {
  width: 100%;
  font-size: 30rpx;
  color: var(--text-main);
  line-height: 40rpx;
}

.send-btn-active {
  background: var(--primary);
  color: #ffffff;
  border-radius: 16rpx;
  padding: 0 28rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 700;
  flex-shrink: 0;
  transition: all 0.2s;
}

.send-btn-active:active {
  opacity: 0.8;
  transform: scale(0.95);
}
</style>
