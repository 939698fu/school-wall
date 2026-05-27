<template>
  <view class="home-page">
    <!-- 自定义导航栏 -->
    <view class="nav-bar">
      <view class="nav-inner">
        <text class="nav-title">校园微墙</text>
      </view>
    </view>

    <scroll-view
      scroll-y
      class="scroll-area"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 搜索栏 -->
      <view class="search-container" @tap="goSearch">
        <view class="search-bar" :class="{ 'search-active': isSearching }">
          <text class="search-icon">🔍</text>
          <text class="search-placeholder">大家都在搜 "期末复习资料"</text>
        </view>
      </view>

      <!-- Banner -->
      <view class="banner-wrapper">
        <view class="banner" @tap="onBanner">
        <view class="banner-text">
          <text class="banner-title">🎉 期末加油！</text>
          <text class="banner-sub">距期末考试还有 21 天，冲！</text>
        </view>
        <text class="banner-emoji">📚</text>
      </view>
      </view>

      <!-- 分类 Tab -->
      <view class="section-tabs">
        <view
          v-for="(tab, index) in tabs"
          :key="tab.key"
          class="tab-item"
          :class="{ active: activeIndex === index }"
          @tap="switchTab(index)"
        >
          <text>{{ tab.label }}</text>
        </view>
      </view>

      <!-- 帖子列表 (使用 Swiper 滑动) -->
      <swiper
        class="swiper-box"
        :current="activeIndex"
        @change="onSwiperChange"
      >
        <swiper-item v-for="tab in tabs" :key="tab.key">
          <scroll-view scroll-y class="swiper-scroll" @scrolltolower="loadMore">
            <view class="post-list">
              <PostCard
                v-for="post in getFilteredPosts(tab.key)"
                :key="post.id"
                :post="post"
              />
            </view>
            <!-- 加载更多 -->
            <view class="load-more">
              <text v-if="loadingMore" class="load-more-text">加载中...</text>
              <text v-else class="load-more-text">— 已经到底了 —</text>
            </view>
          </scroll-view>
        </swiper-item>
      </swiper>
    </scroll-view>

    <!-- 悬浮发布按钮 -->
    <view class="fab-btn" @tap="goPublish">
      <text class="fab-icon">＋</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { usePostsStore } from "@/stores/posts";
import PostCard from "@/components/PostCard.vue";

const postsStore = usePostsStore();
const activeIndex = ref(0);
const refreshing = ref(false);
const loadingMore = ref(false);
const isSearching = ref(false);

const tabs = [
  { key: "latest", label: "最新" },
  { key: "hot", label: "热门" },
  { key: "hole", label: "树洞" },
  { key: "love", label: "表白墙" },
];

onMounted(() => {
  postsStore.fetchPostsByTab("latest", { refresh: true }).catch(showError);
});

function getFilteredPosts(key) {
  return postsStore.getPostsByTab(key);
}

function switchTab(index) {
  activeIndex.value = index;
  const tabKey = tabs[index].key;
  postsStore.setActiveTab(tabKey);
  if (!postsStore.getPostsByTab(tabKey).length) {
    postsStore.fetchPostsByTab(tabKey, { refresh: true }).catch(showError);
  }
}

function onSwiperChange(e) {
  switchTab(e.detail.current);
}

async function onRefresh() {
  refreshing.value = true;
  try {
    const tabKey = tabs[activeIndex.value].key;
    postsStore.setActiveTab(tabKey);
    await postsStore.fetchPostsByTab(tabKey, { refresh: true });
  } catch (error) {
    showError(error);
  } finally {
    refreshing.value = false;
  }
}

function loadMore() {
  const tabKey = tabs[activeIndex.value].key;
  postsStore.setActiveTab(tabKey);
  postsStore.fetchPostsByTab(tabKey).catch(showError);
}

function goSearch() {
  isSearching.value = true;
  // 延迟导航以展示动画
  setTimeout(() => {
    uni.navigateTo({
      url: "/pages/search/index",
      complete: () => {
        isSearching.value = false;
      },
    });
  }, 200);
}

function goNotice() {
  uni.showToast({ title: "暂无新通知", icon: "none" });
}

function onBanner() {
  uni.showToast({ title: "活动详情开发中", icon: "none" });
}

function goPublish() {
  uni.navigateTo({
    url: "/pages/publish/index",
  });
}

function showError(error) {
  uni.showToast({
    title: error?.message || "加载失败",
    icon: "none",
  });
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: var(--bg);
  display: flex;
  flex-direction: column;
}

/* 导航栏 */
.nav-bar {
  background: #ffffff;
  border-bottom: 1rpx solid var(--border);
  padding-top: 85rpx;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 27rpx 32rpx 16rpx;
}

.nav-title {
  font-size: 36rpx;
  font-weight: 800;
  color: var(--text-main);
  letter-spacing: 2rpx;
}

.nav-right {
  display: flex;
  gap: 8rpx;
}

.nav-icon-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: var(--bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
}

/* 滚动区域 */
.scroll-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 搜索栏 */
.search-container {
  padding: 20rpx 24rpx;
  background: #ffffff;
}

.search-bar {
  background: #f5f5f0;
  height: 80rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  padding: 0 32rpx;
  gap: 16rpx;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2rpx solid transparent;
}

.search-active {
  transform: scale(0.96);
  background: #f0f0f0;
  border-color: #ff5a35;
}

.search-icon {
  font-size: 32rpx;
}

.search-placeholder {
  font-size: 28rpx;
  color: #aaaaaa;
}

/* Banner */
.banner-wrapper {
  background: #ffffff;
}

.banner {
  margin: 0 24rpx;
  border-radius: 24rpx;
  background: linear-gradient(120deg, #ff5a35, #ff9035);
  padding: 28rpx 32rpx;
  display: flex;
  align-items: center;
}

.banner-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.banner-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #ffffff;
}

.banner-sub {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
}

.banner-emoji {
  font-size: 72rpx;
  margin-left: 16rpx;
}

/* 分类 Tab */
.section-tabs {
  display: flex;
  background: #ffffff;
  border-bottom: 1rpx solid var(--border);
  padding: 0 24rpx;
  position: sticky;
  top: 0;
  z-index: 10;
}

.tab-item {
  padding: 24rpx 32rpx;
  font-size: 30rpx;
  color: var(--text-sub);
  position: relative;
  transition: all 0.2s;
}

.tab-item.active {
  color: var(--primary);
  font-weight: 600;
}

.tab-item.active::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 6rpx;
  background: var(--primary);
  border-radius: 3rpx;
}

/* Swiper 盒子 */
.swiper-box {
  flex: 1;
  height: calc(100vh - 450rpx);
}

.swiper-scroll {
  height: 100%;
}

/* 帖子列表 */
.post-list {
  padding: 12rpx 0;
}

.load-more {
  text-align: center;
  padding: 32rpx 0 64rpx;
}

.load-more-text {
  font-size: 24rpx;
  color: var(--text-hint);
}

/* 悬浮发布按钮 */
.fab-btn {
  position: fixed;
  right: 40rpx;
  bottom: 60rpx;
  width: 110rpx;
  height: 110rpx;
  background: var(--primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 32rpx rgba(255, 90, 53, 0.4);
  z-index: 99;
  transition: transform 0.2s active;
}

.fab-btn:active {
  transform: scale(0.9);
}

.fab-icon {
  font-size: 60rpx;
  color: #ffffff;
  font-weight: 300;
  margin-top: -4rpx;
}
</style>
