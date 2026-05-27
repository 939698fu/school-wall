<template>
  <view class="search-page">
    <view class="nav-bar">
      <view class="search-box-wrap">
        <!-- <view class="back-btn" @tap="goBack">‹</view> -->
        <view class="search-box">
          <text class="search-icon">🔍</text>
          <input
            class="search-input"
            v-model="keyword"
            placeholder="搜索帖子、用户、关键词"
            :focus="true"
            @confirm="onSearch"
          />
          <text v-if="keyword" class="clear-icon" @tap="clear">✕</text>
        </view>
        <view class="search-btn" @tap="onSearch">搜索</view>
      </view>
    </view>

    <view class="search-content">
      <!-- 历史搜索 -->
      <view v-if="!keyword" class="history-section">
        <view class="section-header">
          <text class="section-title">历史搜索</text>
          <text class="delete-icon" @tap="clearHistory">🗑️</text>
        </view>
        <view class="history-list">
          <view
            v-for="item in history"
            :key="item"
            class="history-item"
            @tap="setKeyword(item)"
          >
            {{ item }}
          </view>
        </view>
      </view>

      <!-- 热门搜索 -->
      <view v-if="!keyword" class="hot-section">
        <view class="section-header">
          <text class="section-title">热门搜索</text>
        </view>
        <view class="hot-list">
          <view
            v-for="(item, index) in hotTags"
            :key="item"
            class="hot-item"
            @tap="setKeyword(item)"
          >
            <text class="hot-rank" :class="'rank-' + (index + 1)">{{
              index + 1
            }}</text>
            <text class="hot-text">{{ item }}</text>
          </view>
        </view>
      </view>

      <!-- 搜索结果 -->
      <view v-else class="result-section">
        <view class="result-head">
          <text class="result-count">共找到 {{ resultPosts.length }} 条相关帖子</text>
        </view>
        <view v-if="resultPosts.length" class="result-list">
          <PostCard v-for="post in resultPosts" :key="post.id" :post="post" />
        </view>
        <view v-else class="result-placeholder">
          <text>没有找到相关内容，换个关键词试试</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { usePostsStore } from "@/stores/posts";
import PostCard from "@/components/PostCard.vue";

const postsStore = usePostsStore();
const keyword = ref("");
const history = ref(["期末复习", "数学学长", "表白墙", "宿舍"]);
const hotTags = ["毕业季", "校园卡招领", "期末复习", "图书馆"];

const resultPosts = computed(() => postsStore.searchPosts(keyword.value));

onLoad((options) => {
  if (options.keyword) {
    keyword.value = decodeURIComponent(options.keyword);
  }
});

function goBack() {
  uni.navigateBack();
}

function clear() {
  keyword.value = "";
}

function onSearch() {
  if (!keyword.value.trim()) return;
  const normalizedKeyword = keyword.value.trim();
  keyword.value = normalizedKeyword;
  if (!history.value.includes(normalizedKeyword)) {
    history.value.unshift(normalizedKeyword);
    history.value = history.value.slice(0, 8);
  }
}

function setKeyword(val) {
  keyword.value = val;
  onSearch();
}

function clearHistory() {
  history.value = [];
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #ffffff;
}

.nav-bar {
  padding-top: var(--status-bar-height, 44px);
  background: #ffffff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.search-box-wrap {
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  gap: 16rpx;
}

.back-btn {
  font-size: 56rpx;
  color: var(--text-main);
  padding: 0 10rpx;
}

.search-box {
  flex: 1;
  background: #f5f5f5;
  height: 72rpx;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
  gap: 12rpx;
}

.search-icon {
  font-size: 28rpx;
  color: var(--text-hint);
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: var(--text-main);
}

.clear-icon {
  font-size: 32rpx;
  color: var(--text-hint);
  padding: 10rpx;
}

.search-btn {
  font-size: 30rpx;
  color: var(--primary);
  font-weight: 600;
  padding-right: 10rpx;
}

.search-content {
  padding: 32rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--text-main);
}

.delete-icon {
  font-size: 32rpx;
}

.history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 60rpx;
}

.history-item {
  background: #f5f5f5;
  padding: 12rpx 28rpx;
  border-radius: 100rpx;
  font-size: 26rpx;
  color: var(--text-sub);
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.result-head {
  margin-bottom: 16rpx;
}

.result-count {
  font-size: 24rpx;
  color: var(--text-hint);
}

.result-list {
  margin: 0 -24rpx;
}

.result-placeholder {
  padding: 80rpx 0;
  text-align: center;
  color: var(--text-hint);
  font-size: 28rpx;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.hot-rank {
  font-size: 28rpx;
  font-weight: 700;
  color: #999;
  width: 32rpx;
}

.rank-1 {
  color: #ff5a35;
}
.rank-2 {
  color: #ff9035;
}
.rank-3 {
  color: #ffcc35;
}

.hot-text {
  font-size: 28rpx;
  color: var(--text-main);
}

.result-placeholder {
  text-align: center;
  padding-top: 100rpx;
  color: var(--text-hint);
  font-size: 28rpx;
}
</style>
