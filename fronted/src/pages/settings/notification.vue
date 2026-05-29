<template>
  <view class="container">
    <view class="section">
      <view class="item">
        <text class="label">新消息通知</text>
        <switch :checked="notifyNewMessage" @change="toggleNotify" color="#ff5a35" />
      </view>
      <view class="item">
        <text class="label">点赞通知</text>
        <switch :checked="notifyLike" @change="toggleLike" color="#ff5a35" />
      </view>
      <view class="item">
        <text class="label">评论通知</text>
        <switch :checked="notifyComment" @change="toggleComment" color="#ff5a35" />
      </view>
    </view>
    <view class="tip">开启后，你将在第一时间收到相关的动态提醒。</view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const notifyNewMessage = ref(true);
const notifyLike = ref(true);
const notifyComment = ref(true);

function toggleNotify(e) {
  notifyNewMessage.value = e.detail.value;
}
function toggleLike(e) {
  notifyLike.value = e.detail.value;
}
function toggleComment(e) {
  notifyComment.value = e.detail.value;
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding: 20rpx;
}
.section {
  background-color: #fff;
  border-radius: 12rpx;
  overflow: hidden;
}
.item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
}
.item:last-child {
  border-bottom: none;
}
.label {
  font-size: 30rpx;
  color: #333;
}
.tip {
  padding: 20rpx 30rpx;
  font-size: 24rpx;
  color: #999;
}
</style>
