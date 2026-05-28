<template>
  <view class="profile-page">
    <scroll-view 
      scroll-y 
      class="scroll-area" 
      @scrolltolower="handleReachBottom"
      :lower-threshold="100"
    >
      <!-- 全新高级感：个人资料卡片区 -->
      <view class="profile-header-modern">
        <!-- 动态光感背景，带一点装饰图形元素 -->
        <view class="header-bg-modern">
          <view class="bg-shape shape-1"></view>
          <view class="bg-shape shape-2"></view>
        </view>

        <!-- 悬浮信息卡片 -->
        <view class="user-card-floating">
          
          <!-- 第一层：突破边界的头像 与 主次分明的按钮 -->
          <view class="card-top-row">
            <view class="avatar-wrap-modern">
              <image
                v-if="displayUser.avatar && displayUser.avatar.includes('/')"
                class="user-avatar-img"
                :src="displayUser.avatar"
                mode="aspectFill"
              />
              <view v-else class="user-avatar-text">{{ displayUser.avatar }}</view>
            </view>
            <view class="action-buttons-modern">
              <view class="btn-chat-modern" @tap="startChat">私信</view>
              <view 
                class="btn-follow-modern" 
                :class="{ 'is-followed': isFollowed }"
                @tap="onFollow"
              >
                {{ isFollowed ? "已关注" : "+ 关注" }}
              </view>
            </view>
          </view>

          <!-- 第二层：文字信息与标签 -->
          <view class="user-info-modern">
            <view class="name-line">
              <text class="user-nickname-modern">{{ displayUser.nickname }}</text>
              <view class="school-badge">
                <text class="badge-icon">🎓</text>
                <text>{{ displayUser.school }}</text>
              </view>
            </view>
            <text class="user-bio-modern">{{ displayUser.bio || "这个人很懒，什么都没写~" }}</text>
          </view>

          <!-- 第三层：纯净无分割线的数据统计 -->
          <view class="stats-modern">
            <view class="stat-item-m">
              <text class="stat-num-m">{{ displayPostCount }}</text>
              <text class="stat-label-m">帖子</text>
            </view>
            <view class="stat-item-m" @tap="goFans('following')">
              <text class="stat-num-m">{{ displayUser.followingCount || 0 }}</text>
              <text class="stat-label-m">关注</text>
            </view>
            <view class="stat-item-m" @tap="goFans('followers')">
              <text class="stat-num-m">{{ displayUser.followerCount || 0 }}</text>
              <text class="stat-label-m">粉丝</text>
            </view>
            <view class="stat-item-m">
              <text class="stat-num-m">{{ displayLikeCount }}</text>
              <text class="stat-label-m">获赞</text>
            </view>
          </view>
          
        </view>
      </view>

      <!-- 视觉分区色块界定 -->
      <view class="section-divider"></view>

      <!-- 动态帖子列表区 -->
      <view class="content-section">
        <view class="profile-tabs">
          <view class="ptab active">TA 的帖子</view>
        </view>

        <view class="post-list-section">
          <!-- 空状态 -->
          <view v-if="authoredPosts.length === 0" class="empty-tip">
            <text class="empty-emoji">📝</text>
            <text class="empty-text">TA 还没有发过帖子</text>
          </view>
          
          <!-- 动态渲染当前页数的帖子 -->
          <PostCard v-for="post in displayedPosts" :key="post.id" :post="post" />

          <!-- 情况 A：超过2条，且用户还没有点击“查看更多” -->
          <view v-if="authoredPosts.length > 2 && !isExpanded" class="show-more-wrap">
            <view class="show-more-btn" @tap="handleExpand">
              <text>查看更多帖子</text>
              <text class="arrow-down"> ∨</text>
            </view>
          </view>
          
          <!-- 情况 B：已经展开了列表，底部展示“加载中”或“到底了”的提示 -->
          <view v-if="isExpanded" class="load-status-bar">
            <view v-if="loadingMore" class="loading-box">
              <view class="spinner"></view>
              <text>正在加载中...</text>
            </view>
            <view v-else-if="!hasMore" class="no-more-tip">
              <text>— 已经到底啦 —</text>
            </view>
          </view>
        </view>
      </view>

      <view style="height: 40rpx"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { computed, ref } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import PostCard from "@/components/PostCard.vue";
import { usePostsStore } from "@/stores/posts";
import { useUserStore } from "@/stores/user";

const postsStore = usePostsStore();
const userStore = useUserStore();

const routeUserId = ref(null);
const routeUserName = ref("");
const followLoading = ref(false);

// ================= 分页/展开状态控制 =================
const isExpanded = ref(false);    // 是否点击了“查看更多”展开列表
const loadingMore = ref(false);   // 是否正在加载下一页
const pageSize = 10;              // 每页加载的数量
const visibleCount = ref(2);      // 当前界面真正渲染的帖子数量限制

onLoad((options) => {
  routeUserId.value = options.userId ? Number(options.userId) : null;
  routeUserName.value = options.name ? decodeURIComponent(options.name) : "";
  if (routeUserId.value) {
    initData();
  }
});

onShow(() => {
  if (routeUserId.value) {
    initData();
  }
});

// 数据初始化
function initData() {
  userStore.fetchUserById(routeUserId.value).catch(() => {});
  postsStore.fetchUserPosts(routeUserId.value).catch(() => {});
  
  // 切回页面或切换用户时重置分页状态
  isExpanded.value = false;
  visibleCount.value = 2;
  loadingMore.value = false;
}

const displayUser = computed(() => {
  if (routeUserId.value) {
    return (
      userStore.getUserById(routeUserId.value) || {
        id: routeUserId.value,
        nickname: "校园同学",
        avatar: "🙂",
        school: "某某大学",
        bio: "",
      }
    );
  }

  return (
    userStore.getUserByName(routeUserName.value) || {
      id: 0,
      nickname: routeUserName.value || "校园同学",
      avatar: "🙂",
      school: "某某大学",
      bio: "",
    }
  );
});

// 获取该用户的全部帖子原始数组
const authoredPosts = computed(() => {
  if (!displayUser.value?.id) return [];
  return postsStore.userPostsMap[displayUser.value.id] || [];
});

// 核心：计算当前应该在前端 DOM 渲染展示的帖子
const displayedPosts = computed(() => {
  return authoredPosts.value.slice(0, visibleCount.value);
});

// 是否还有更多数据可以加载
const hasMore = computed(() => {
  return visibleCount.value < authoredPosts.value.length;
});

// 点击“查看更多”按钮：转为分页加载模式，默认展示前10条
function handleExpand() {
  isExpanded.value = true;
  visibleCount.value = pageSize; // 初始展示10条
}

// 核心：scroll-view 触底事件
function handleReachBottom() {
  if (!isExpanded.value || loadingMore.value || !hasMore.value) return;
  loadingMore.value = true;
  setTimeout(() => {
    visibleCount.value += pageSize; // 增加渲染上限
    loadingMore.value = false;
  }, 800);
}

const displayPostCount = computed(
  () => Number(displayUser.value?.postCount ?? authoredPosts.value.length ?? 0),
);

const displayLikeCount = computed(
  () =>
    Number(
      displayUser.value?.likeCount ??
        authoredPosts.value.reduce((total, post) => total + Number(post.likes || 0), 0),
    ),
);

const isFollowed = computed(() => displayUser.value?.isFollowed === true);

async function onFollow() {
  if (!userStore.isLoggedIn) {
    uni.showToast({ title: "请先登录", icon: "none" });
    return;
  }
  if (!displayUser.value?.id || followLoading.value) return;
  followLoading.value = true;
  try {
    if (isFollowed.value) {
      await userStore.unfollowUser(displayUser.value.id);
      uni.showToast({ title: "已取消关注", icon: "none" });
    } else {
      await userStore.followUser(displayUser.value.id);
      uni.showToast({ title: "关注成功", icon: "success" });
    }
  } catch (error) {
    uni.showToast({ title: error?.message || "操作失败", icon: "none" });
  } finally {
    followLoading.value = false;
  }
}

function goFans(type) {
  const uid = displayUser.value?.id;
  if (!uid) return;
  uni.navigateTo({
    url: `/pages/user-fans/index?userId=${uid}&type=${type}`,
  });
}

function startChat() {
  if (!displayUser.value?.id) return;
  if (!userStore.isLoggedIn) {
    uni.showToast({ title: "请先登录", icon: "none" });
    return;
  }
  uni.navigateTo({
    url: `/pages/message/chat?userId=${displayUser.value.id}&name=${encodeURIComponent(displayUser.value.nickname)}&avatar=${encodeURIComponent(displayUser.value.avatar || "🙂")}`,
  });
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--bg, #f5f6f8); 
}

.scroll-area {
  height: 100vh;
}

/* ================= 全新头部设计样式 ================= */
.profile-header-modern {
  position: relative;
  padding-bottom: 20rpx;
  background: var(--bg, #f5f6f8);
}

.header-bg-modern {
  position: relative;
  height: 280rpx;
  background: linear-gradient(135deg, #FF8C20 0%, #FF5722 100%);
  overflow: hidden;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(180deg, rgba(255,255,255,0.15) 0%, rgba(255,255,255,0) 100%);
}
.shape-1 {
  width: 300rpx;
  height: 300rpx;
  top: -100rpx;
  right: -50rpx;
}
.shape-2 {
  width: 200rpx;
  height: 200rpx;
  bottom: -50rpx;
  left: 50rpx;
}

.user-card-floating {
  margin: -100rpx 32rpx 0;
  background: #ffffff;
  border-radius: 32rpx;
  padding: 0 32rpx 32rpx;
  position: relative;
  z-index: 10;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.05);
}

.card-top-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24rpx;
}

.avatar-wrap-modern {
  margin-top: -40rpx;
  position: relative;
}
.user-avatar-img, .user-avatar-text {
  width: 144rpx;
  height: 144rpx;
  border-radius: 50%;
  border: 8rpx solid #ffffff;
  background: #ffffff;
  box-shadow: 0 8rpx 24rpx rgba(255, 87, 34, 0.15);
}
.user-avatar-text {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 72rpx;
}

.action-buttons-modern {
  display: flex;
  gap: 16rpx;
  padding-bottom: 8rpx;
}
.btn-chat-modern, .btn-follow-modern {
  height: 64rpx;
  border-radius: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: 600;
  padding: 0 36rpx;
  transition: all 0.2s;
}
.btn-chat-modern {
  background: #F5F6F8;
  color: #333333;
}
.btn-follow-modern {
  background: linear-gradient(90deg, #FF7A1A, #FF5722);
  color: #ffffff;
  box-shadow: 0 6rpx 16rpx rgba(255, 87, 34, 0.25);
}
.btn-follow-modern.is-followed {
  background: #F5F6F8;
  color: #999999;
  box-shadow: none;
}

.user-info-modern {
  margin-bottom: 32rpx;
}
.name-line {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 12rpx;
}
.user-nickname-modern {
  font-size: 42rpx;
  font-weight: 800;
  color: #1A1A1A;
  letter-spacing: 1rpx;
}

.school-badge {
  display: flex;
  align-items: center;
  gap: 4rpx;
  background: rgba(255, 122, 26, 0.1);
  color: #FF7A1A;
  font-size: 22rpx;
  font-weight: 600;
  padding: 4rpx 14rpx;
  border-radius: 8rpx;
}
.badge-icon {
  font-size: 20rpx;
}

.user-bio-modern {
  font-size: 28rpx;
  color: #777777;
  line-height: 1.5;
}

.stats-modern {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16rpx;
}
.stat-item-m {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}
.stat-num-m {
  font-size: 38rpx;
  font-weight: 900;
  color: #1A1A1A;
  font-family: 'DIN', -apple-system, sans-serif;
}
.stat-label-m {
  font-size: 24rpx;
  color: #888888;
}

/* ================= 列表区样式 ================= */
.section-divider {
  height: 20rpx;
  background: transparent;
}

.content-section {
  background: #ffffff;
}

.profile-tabs {
  display: flex;
  background: #ffffff;
  border-bottom: 1rpx solid #f0f0f0;
}

.ptab {
  flex: 1;
  padding: 26rpx 0;
  text-align: center;
  font-size: 30rpx;
  color: var(--primary, #ff7a1a);
  font-weight: 700;
  position: relative;
}

.ptab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 6rpx;
  border-radius: 6rpx;
  background: var(--primary, #ff7a1a);
}

.post-list-section {
  padding-top: 16rpx;
  background: var(--bg, #f5f6f8);
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
  gap: 16rpx;
  background: #ffffff;
}

.empty-emoji {
  font-size: 64rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
}

/* 查看更多按钮 */
.show-more-wrap {
  padding: 30rpx 0 50rpx;
  display: flex;
  justify-content: center;
  background: var(--bg, #f5f6f8);
}

.show-more-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 48rpx;
  background: #ffffff;
  border-radius: 40rpx;
  font-size: 26rpx;
  color: #666666;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.04);
}

.arrow-down {
  font-size: 20rpx;
  transform: scale(0.8);
}

/* 底部状态栏与加载动画 */
.load-status-bar {
  padding: 40rpx 0 60rpx;
  text-align: center;
  background: var(--bg, #f5f6f8);
}

.loading-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  font-size: 26rpx;
  color: #888888;
}

.spinner {
  width: 32rpx;
  height: 32rpx;
  border: 3rpx solid #e0e0e0;
  border-top-color: var(--primary, #ff7a1a);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.no-more-tip {
  font-size: 24rpx;
  color: #cccccc;
}
</style>