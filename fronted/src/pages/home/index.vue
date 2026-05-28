<template>
  <view class="home-page">
    <!-- 自定义导航栏 (通透质感) -->
    <view class="nav-bar">
      <view class="nav-inner">
        <text class="nav-title">校园微墙</text>
      </view>
    </view>

    <!-- 外层滚动区域 -->
    <scroll-view
      scroll-y
      class="scroll-area"
      @scroll="onOuterScroll"
      @scrolltolower="loadMore"
      :refresher-enabled="isTop"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 搜索栏 -->
      <view class="search-container" @tap="goSearch">
        <view class="search-bar" :class="{ 'search-active': isSearching }">
          <image class="search-icon-svg" src="/static/icons/search.svg" mode="aspectFit" />
          <text class="search-placeholder">大家都在搜 "期末复习资料"</text>
        </view>
      </view>

      <!-- Banner (增加悬浮发光质感) -->
      <view class="banner-wrapper">
        <view class="banner" @tap="onBanner">
          <view class="banner-text">
            <view class="banner-title-wrap">
              <image class="banner-title-icon-svg" src="/static/icons/party.svg" mode="aspectFit" />
              <text class="banner-title">期末加油！</text>
            </view>
            <text class="banner-sub">距期末考试还有 21 天，冲！</text>
          </view>
          <image class="banner-emoji-svg" src="/static/icons/books.svg" mode="aspectFit" />
        </view>
      </view>

      <!-- 分类 Tab (去线留白) -->
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

      <!-- 帖子列表 (使用 Swiper 滑动，底层背景为淡灰) -->
      <swiper
        class="swiper-box"
        :current="activeIndex"
        @change="onSwiperChange"
      >
        <swiper-item v-for="tab in tabs" :key="tab.key">
          <scroll-view scroll-y class="swiper-scroll" @scrolltolower="loadMore">
            <view class="post-list">
              <!-- 修改为双标签，避免微信小程序编译报错 -->
              <PostCard
                v-for="post in getFilteredPosts(tab.key)"
                :key="post.id"
                :post="post"
              ></PostCard>
            </view>
            <!-- 加载更多 -->
            <view class="load-more">
              <text v-if="loadingMore" class="load-more-text">正在加载更多精彩...</text>
              <text v-else class="load-more-text">— 已经到底啦 —</text>
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
import { useUserStore } from "@/stores/user";
import PostCard from "@/components/PostCard.vue";

const postsStore = usePostsStore();
const userStore = useUserStore();
const activeIndex = ref(0);
const refreshing = ref(false);
const loadingMore = ref(false);
const isSearching = ref(false);

// 新增：动态控制是否允许下拉刷新，避免遮挡 Bug
const isTop = ref(true);

const tabs = [
  { key: "latest", label: "最新" },
  { key: "hot", label: "热门" },
  { key: "hole", label: "树洞" },
  { key: "love", label: "表白墙" },
];

onMounted(() => {
  postsStore.fetchPostsByTab("latest", { refresh: true }).catch(showError);
});

// 新增：监听外层滚动，只有在完全顶部时才允许下拉刷新
function onOuterScroll(e) {
  isTop.value = e.detail.scrollTop <= 10;
}

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
  if (!isTop.value) return; // 双重拦截，防止滚动中误触发
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
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: "/pages/login/index" });
    return;
  }
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
  height: 100vh; /* 修复：从 min-height 改为 height，彻底锁死页面 */
  background: #f5f6f8; 
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 修复：切断页面级原生滚动 */
}

/* =============== 导航栏 =============== */
.nav-bar {
  background: rgba(255, 255, 255, 0.98);
  padding-top: 85rpx;
  flex-shrink: 0;
  /* 修复：移除原本多余的 position: sticky，它在 flex 容器中会导致渲染错位 */
  z-index: 100;
}

.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 27rpx 32rpx 16rpx;
}

.nav-title {
  font-size: 42rpx;
  font-weight: 800;
  color: #1a1a1a;
  letter-spacing: 1rpx;
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

/* =============== 滚动区域 =============== */
.scroll-area {
  flex: 1;
  height: 0; /* 修复：必须给 0 才能让 flex: 1 完美自适应内部尺寸，不再超出屏幕 */
  width: 100%;
}

/* =============== 搜索栏 =============== */
.search-container {
  padding: 16rpx 32rpx 24rpx;
  background: #ffffff;
}

.search-bar {
  background: #f8f9fa; 
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  padding: 0 32rpx;
  gap: 16rpx;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2rpx solid transparent;
}

.search-active {
  transform: scale(0.97);
  background: #f0f0f0;
  border-color: rgba(255, 90, 53, 0.5);
}

.search-icon-svg {
  width: 36rpx;
  height: 36rpx;
  display: block;
}

.search-placeholder {
  font-size: 30rpx;
  color: #aaaaaa;
}

/* =============== Banner =============== */
.banner-wrapper {
  background: #ffffff;
  padding-bottom: 24rpx;
}

.banner {
  margin: 0 32rpx; 
  border-radius: 32rpx; 
  background: linear-gradient(135deg, #ff7a1a 0%, #ff5722 100%);
  padding: 32rpx 40rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 16rpx 32rpx rgba(255, 87, 34, 0.2); 
}

.banner-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.banner-title-wrap {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.banner-title-icon-svg {
  width: 36rpx;
  height: 36rpx;
  display: block;
}

.banner-title {
  font-size: 34rpx;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 0.5rpx;
}

.banner-sub {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.9);
}

.banner-emoji-svg {
  width: 80rpx;
  height: 80rpx;
  margin-left: 16rpx;
  display: block;
}

/* =============== 分类 Tab =============== */
.section-tabs {
  display: flex;
  background: #ffffff;
  padding: 0 20rpx;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.015); 
}

.tab-item {
  padding: 24rpx 28rpx;
  font-size: 30rpx;
  color: #888888;
  position: relative;
  transition: all 0.25s cubic-bezier(0.1, 0.7, 0.1, 1);
}

.tab-item.active {
  color: var(--primary);
  font-weight: 700;
  font-size: 32rpx;
}

.tab-item.active::after {
  content: "";
  position: absolute;
  bottom: 8rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 32rpx;
  height: 8rpx;
  background: var(--primary);
  border-radius: 4rpx;
}

/* =============== Swiper 盒子 =============== */
.swiper-box {
  flex: 1;
  /* 修复：精准计算减去顶部吸顶区域的高度，确保滑动列表恰好填满剩余屏幕，不再有留白 */
  height: calc(100vh - 260rpx);
  background: #f5f6f8;
}

.swiper-scroll {
  height: 100%;
}

/* =============== 帖子列表 =============== */
.post-list {
  padding: 8rpx 0 24rpx;
}

.load-more {
  text-align: center;
  padding: 32rpx 0 64rpx;
}

.load-more-text {
  font-size: 24rpx;
  color: #bbbbbb;
}

/* =============== 悬浮发布按钮 =============== */
.fab-btn {
  position: fixed;
  right: 40rpx;
  bottom: calc(60rpx + env(safe-area-inset-bottom));
  width: 112rpx;
  height: 112rpx;
  background: linear-gradient(135deg, #ff7a1a 0%, #ff5722 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12rpx 32rpx rgba(255, 87, 34, 0.35); 
  z-index: 99;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.fab-btn:active {
  transform: scale(0.9);
}

.fab-icon {
  font-size: 64rpx;
  color: #ffffff;
  font-weight: 300;
  margin-top: -6rpx;
}
</style>