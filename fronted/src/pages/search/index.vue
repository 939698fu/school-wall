<template>
  <view class="search-page">
    <view class="nav-bar">
      <view class="search-box-wrap">
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
      <view v-if="!hasSearched" class="history-section">
        <view class="section-header">
          <text class="section-title">历史搜索</text>
          <text v-if="history.length" class="delete-icon" @tap="clearHistory">🗑️</text>
        </view>
        <view v-if="history.length" class="history-list">
          <view
            v-for="item in history"
            :key="item"
            class="history-item"
            @tap="setKeyword(item)"
          >
            {{ item }}
          </view>
        </view>
        <view v-else class="empty-tip">
          <text>暂无历史搜索</text>
        </view>
      </view>

      <!-- 热门搜索 -->
      <view v-if="!hasSearched" class="hot-section">
        <view class="section-header">
          <text class="section-title">热门话题</text>
        </view>
        <view v-if="hotTags.length" class="hot-list">
          <view
            v-for="(item, index) in hotTags"
            :key="item.name"
            class="hot-item"
            @tap="setKeyword(item.name)"
          >
            <text class="hot-rank" :class="'rank-' + (index + 1)">{{
              index + 1
            }}</text>
            <text class="hot-text">{{ item.name }}</text>
            <text class="hot-count">{{ item.count }} 热度</text>
          </view>
        </view>
        <view v-else class="empty-tip">
          <text>暂无热门话题</text>
        </view>
      </view>

      <!-- 搜索结果 -->
      <view v-else class="result-section">
        <!-- Tab 切换 -->
        <view class="result-tabs">
          <view
            class="result-tab"
            :class="{ active: activeTab === 'posts' }"
            @tap="activeTab = 'posts'"
          >
            帖子 {{ postTotal }}
          </view>
          <view
            class="result-tab"
            :class="{ active: activeTab === 'users' }"
            @tap="activeTab = 'users'"
          >
            用户 {{ userTotal }}
          </view>
        </view>

        <!-- Loading -->
        <view v-if="loading" class="result-placeholder">
          <text>搜索中...</text>
        </view>

        <!-- 帖子结果 -->
        <view v-else-if="activeTab === 'posts'">
          <view v-if="resultPosts.length" class="result-list">
            <PostCard v-for="post in resultPosts" :key="post.id" :post="post" />
          </view>
          <view v-else class="result-placeholder">
            <text>没有找到相关帖子</text>
          </view>
        </view>

        <!-- 用户结果 -->
        <view v-else>
          <view v-if="resultUsers.length" class="user-list">
            <view
              v-for="user in resultUsers"
              :key="user.id"
              class="user-item"
              @tap="goUserProfile(user)"
            >
              <image
                v-if="String(user.avatar).includes('/')"
                :src="user.avatar"
                class="user-avatar"
                mode="aspectFill"
              />
              <view v-else class="user-avatar">{{ user.avatar }}</view>
              <view class="user-info">
                <text class="user-nickname">{{ user.nickname }}</text>
                <text class="user-school">{{ user.school || "未填写学校" }}</text>
              </view>
              <text class="user-post-count">{{ user.postCount || 0 }} 帖</text>
            </view>
          </view>
          <view v-else class="result-placeholder">
            <text>没有找到相关用户</text>
          </view>
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

const HISTORY_KEY = "school_wall_search_history";

const postsStore = usePostsStore();
const keyword = ref("");
const history = ref(uni.getStorageSync(HISTORY_KEY) || []);

const activeTab = ref("posts");
const loading = ref(false);
const hasSearched = ref(false);
const resultPosts = ref([]);
const resultUsers = ref([]);
const postTotal = ref(0);
const userTotal = ref(0);

const hotTags = computed(() => postsStore.hotTags || []);

onLoad((options) => {
  postsStore.fetchHotTags().catch(() => {});
  if (options.keyword) {
    keyword.value = decodeURIComponent(options.keyword);
    onSearch();
  }
});

function persistHistory() {
  uni.setStorageSync(HISTORY_KEY, history.value);
}

function pushHistory(word) {
  const filtered = history.value.filter((item) => item !== word);
  history.value = [word, ...filtered].slice(0, 10);
  persistHistory();
}

function clear() {
  keyword.value = "";
  hasSearched.value = false;
  resultPosts.value = [];
  resultUsers.value = [];
  postTotal.value = 0;
  userTotal.value = 0;
}

async function onSearch() {
  const word = keyword.value.trim();
  if (!word) return;
  keyword.value = word;
  pushHistory(word);
  hasSearched.value = true;
  loading.value = true;
  try {
    const data = await postsStore.searchFromServer(word);
    resultPosts.value = data.posts;
    resultUsers.value = data.users;
    postTotal.value = data.postTotal;
    userTotal.value = data.userTotal;
    if (data.posts.length === 0 && data.users.length > 0) {
      activeTab.value = "users";
    } else {
      activeTab.value = "posts";
    }
  } catch (error) {
    uni.showToast({ title: error?.message || "搜索失败", icon: "none" });
  } finally {
    loading.value = false;
  }
}

function setKeyword(val) {
  keyword.value = val;
  onSearch();
}

function clearHistory() {
  history.value = [];
  persistHistory();
}

function goUserProfile(user) {
  if (!user?.id) return;
  uni.navigateTo({
    url: `/pages/user-profile/index?userId=${user.id}`,
  });
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

.empty-tip {
  padding: 0 0 40rpx;
  color: var(--text-hint);
  font-size: 26rpx;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
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
  flex: 1;
  font-size: 28rpx;
  color: var(--text-main);
}

.hot-count {
  font-size: 22rpx;
  color: var(--text-hint);
}

.result-section {
  margin: 0 -32rpx;
}

.result-tabs {
  display: flex;
  border-bottom: 1rpx solid var(--border);
  background: #ffffff;
  position: sticky;
  top: calc(var(--status-bar-height, 44px) + 104rpx);
  z-index: 99;
}

.result-tab {
  flex: 1;
  padding: 22rpx 0;
  text-align: center;
  font-size: 28rpx;
  color: var(--text-sub);
  font-weight: 600;
  position: relative;
}

.result-tab.active {
  color: var(--primary);
}

.result-tab.active::after {
  content: "";
  position: absolute;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 56rpx;
  height: 4rpx;
  background: var(--primary);
  border-radius: 2rpx;
}

.result-list {
  margin: 0;
}

.result-placeholder {
  padding: 100rpx 0;
  text-align: center;
  color: var(--text-hint);
  font-size: 28rpx;
}

.user-list {
  background: #ffffff;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 22rpx 32rpx;
  border-bottom: 1rpx solid var(--border);
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: #f5f5f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
}

.user-nickname {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--text-main);
}

.user-school {
  font-size: 22rpx;
  color: var(--text-hint);
}

.user-post-count {
  font-size: 24rpx;
  color: var(--text-hint);
  flex-shrink: 0;
}
</style>
