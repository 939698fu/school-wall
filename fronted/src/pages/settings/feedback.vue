<template>
  <view class="container">
    <view class="feedback-type">
      <text class="title">反馈类型</text>
      <view class="tags">
        <view 
          v-for="type in types" 
          :key="type" 
          class="tag" 
          :class="{ active: currentType === type }"
          @tap="currentType = type"
        >{{ type }}</view>
      </view>
    </view>
    
    <view class="feedback-content">
      <textarea 
        class="textarea" 
        placeholder="请详细描述您遇到的问题或建议..." 
        v-model="content"
        maxlength="200"
      />
      <text class="count">{{ content.length }}/200</text>
    </view>

    <view class="contact">
      <input class="input" placeholder="留下您的联系方式(选填)" v-model="contact" />
    </view>

    <view class="submit-btn" @tap="handleSubmit">提交反馈</view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const types = ['功能建议', '系统Bug', '内容投诉', '其他'];
const currentType = ref('功能建议');
const content = ref('');
const contact = ref('');

function handleSubmit() {
  if (!content.value) {
    uni.showToast({ title: '请输入反馈内容', icon: 'none' });
    return;
  }
  uni.showLoading({ title: '提交中...' });
  setTimeout(() => {
    uni.hideLoading();
    uni.showToast({ title: '提交成功', icon: 'success' });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }, 1000);
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding: 30rpx;
}
.title {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
  display: block;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 40rpx;
}
.tag {
  padding: 10rpx 30rpx;
  background-color: #fff;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #333;
  border: 1rpx solid #ddd;
}
.tag.active {
  background-color: var(--primary, #ff5a35);
  color: #fff;
  border-color: var(--primary, #ff5a35);
}
.feedback-content {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  position: relative;
  margin-bottom: 30rpx;
  border: 1rpx solid #eee;
}
.textarea {
  width: 100%;
  height: 300rpx;
  font-size: 28rpx;
}
.count {
  position: absolute;
  right: 20rpx;
  bottom: 20rpx;
  font-size: 24rpx;
  color: #999;
}
.contact {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 60rpx;
  border: 1rpx solid #eee;
}
.input {
  font-size: 28rpx;
}
.submit-btn {
  height: 88rpx;
  background: linear-gradient(to right, var(--primary, #ff5a35), var(--primary-mid, #ff7a5a));
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  box-shadow: 0 10rpx 20rpx rgba(255, 90, 53, 0.2);
}
</style>
