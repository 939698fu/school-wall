<template>
  <view class="profile-page">
    <scroll-view 
      scroll-y 
      class="scroll-area"
      @scrolltolower="handleReachBottom"
      :lower-threshold="100"
    >
      <view class="profile-header-modern">
        <view class="header-bg-modern">
          <view class="bg-shape shape-1"></view>
          <view class="bg-shape shape-2"></view>
        </view>

        <view class="user-card-floating">
          
          <view class="card-top-row">
            <view class="avatar-wrap-modern" @tap="changeAvatar">
              <image
                v-if="displayUser.avatar && displayUser.avatar.includes('/')"
                class="user-avatar-img"
                :src="displayUser.avatar"
                mode="aspectFill"
              />
              <view v-else class="user-avatar-text">{{ displayUser.avatar }}</view>
              <view v-if="isOwnProfile" class="avatar-edit-badge">✏️</view>
            </view>
            <view class="action-buttons-modern">
              <view v-if="isOwnProfile" class="btn-edit-modern" @tap="editProfile">
                编辑资料
              </view>
            </view>
          </view>

          <view class="user-info-modern">
            <view class="name-line">
              <text class="user-nickname-modern">{{ displayUser.nickname }}</text>
              <view class="school-badge">
                <text class="badge-icon">🎓</text>
                <text>{{ displayUser.school || '未填写学校' }}</text>
              </view>
            </view>
            <text class="user-bio-modern">{{ displayUser.bio || "这个人很懒，什么都没写~" }}</text>
          </view>

          <view class="stats-modern">
            <view class="stat-item-m" @tap="goMyPosts">
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

      <view class="section-divider"></view>

      <view class="content-section">
        <view class="profile-tabs">
          <view
            class="ptab"
            :class="{ active: activeTab === 'posts' }"
            @tap="activeTab = 'posts'"
            >{{ isOwnProfile ? "我的帖子" : "TA 的帖子" }}</view
          >
          <view
            v-if="isOwnProfile"
            class="ptab"
            :class="{ active: activeTab === 'collect' }"
            @tap="activeTab = 'collect'"
            >我的收藏</view
          >
        </view>

        <view class="post-list-section" v-if="activeTab === 'posts'">
          <view v-if="authoredPosts.length === 0" class="empty-tip">
            <text class="empty-emoji">📝</text>
            <text class="empty-text">{{
              isOwnProfile ? "还没有发过帖子" : "TA 还没有发过帖子"
            }}</text>
            <view v-if="isOwnProfile" class="empty-btn" @tap="goPublish">去发帖</view>
          </view>
          
          <view
            v-for="post in displayedAuthoredPosts"
            :key="post.id"
            @longpress="isOwnProfile && onPostLongPress(post)"
          >
            <PostCard :post="post" />
          </view>

          <view v-if="authoredPosts.length > 2 && !postsExpanded" class="show-more-wrap">
            <view class="show-more-btn" @tap="handleExpandPosts">
              <text>查看更多帖子</text>
              <text class="arrow-down"> ∨</text>
            </view>
          </view>
          <view v-if="postsExpanded" class="load-status-bar">
            <view v-if="postsLoading" class="loading-box">
              <view class="spinner"></view>
              <text>正在加载中...</text>
            </view>
            <view v-else-if="!hasMorePosts" class="no-more-tip">
              <text>— 已经到底啦 —</text>
            </view>
          </view>
        </view>

        <view class="post-list-section" v-if="isOwnProfile && activeTab === 'collect'">
          <view v-if="collectedPosts.length === 0" class="empty-tip">
            <text class="empty-emoji">⭐</text>
            <text class="empty-text">还没有收藏任何帖子</text>
          </view>
          
          <PostCard v-for="post in displayedCollectedPosts" :key="post.id" :post="post" />

          <view v-if="collectedPosts.length > 2 && !collectExpanded" class="show-more-wrap">
            <view class="show-more-btn" @tap="handleExpandCollect">
              <text>查看更多收藏</text>
              <text class="arrow-down"> ∨</text>
            </view>
          </view>
          <view v-if="collectExpanded" class="load-status-bar">
            <view v-if="collectLoading" class="loading-box">
              <view class="spinner"></view>
              <text>正在加载中...</text>
            </view>
            <view v-else-if="!hasMoreCollect" class="no-more-tip">
              <text>— 已经到底啦 —</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="isOwnProfile" class="settings-section">
        <text class="settings-title">系统与偏好</text>
        <view class="settings-list">
          <view
            class="settings-item"
            v-for="item in settingItems"
            :key="item.label"
            @tap="item.action"
          >
            <text class="settings-icon">{{ item.icon }}</text>
            <text class="settings-label">{{ item.label }}</text>
            <text class="settings-arrow">›</text>
          </view>
        </view>
      </view>

      <view v-if="isOwnProfile" class="logout-wrap">
        <view class="logout-btn" @tap="onLogout">退出当前账号</view>
      </view>

      <view style="height: 40rpx"></view>
    </scroll-view>

    <view
      v-if="showEditModal"
      class="modal-mask"
      :class="{ 'modal-mask-active': showEditModal }"
      @tap="showEditModal = false"
      @touchmove.stop.prevent
    >
      <view class="modal-wrap" @tap.stop>
        <view class="modal-header">
          <text class="modal-title">编辑资料</text>
          <view class="modal-close" @tap="showEditModal = false">×</view>
        </view>
        <scroll-view scroll-y class="modal-form">
          <view class="modal-field">
            <text class="field-label">昵称</text>
            <input
              class="field-input"
              v-model="editForm.nickname"
              placeholder="输入昵称"
              :maxlength="20"
              :cursor-spacing="100"
            />
          </view>
          <view class="modal-field">
            <text class="field-label">学校</text>
            <input
              class="field-input"
              v-model="editForm.school"
              placeholder="输入学校名称"
              :maxlength="30"
              :cursor-spacing="100"
            />
          </view>
          <view class="modal-field">
            <text class="field-label">简介</text>
            <textarea
              class="field-textarea"
              v-model="editForm.bio"
              placeholder="介绍一下自己..."
              :maxlength="100"
              :cursor-spacing="100"
              :fixed="true"
            />
          </view>
        </scroll-view>
        <view class="modal-footer">
          <view class="modal-cancel" @tap="showEditModal = false">取消</view>
          <view class="modal-confirm" @tap="saveProfile">保存</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, reactive } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import PostCard from "@/components/PostCard.vue";
import { usePostsStore } from "@/stores/posts";
import { useUserStore } from "@/stores/user";

const postsStore = usePostsStore();
const userStore = useUserStore();

const activeTab = ref("posts");
const showEditModal = ref(false);
const routeUserId = ref(null);
const routeUserName = ref("");

// ================= 分页与折叠控制逻辑 =================
const pageSize = 10;
// 帖子的状态
const postsExpanded = ref(false);
const postsVisible = ref(2);
const postsLoading = ref(false);
// 收藏的状态
const collectExpanded = ref(false);
const collectVisible = ref(2);
const collectLoading = ref(false);

const editForm = reactive({
  nickname: "",
  school: "",
  bio: "",
});

onLoad((options) => {
  routeUserId.value = options.userId ? Number(options.userId) : null;
  routeUserName.value = options.name ? decodeURIComponent(options.name) : "";
});

onShow(() => {
  if (!routeUserId.value && !routeUserName.value) {
    userStore.fetchCurrentUser().catch(() => {});
    postsStore.fetchMyPosts().catch(() => {});
    postsStore.fetchMyCollections().catch(() => {});
    return;
  }
  if (routeUserId.value) {
    userStore.fetchUserById(routeUserId.value).catch(() => {});
    postsStore.fetchUserPosts(routeUserId.value).catch(() => {});
  }
});

const isOwnProfile = computed(() => {
  if (!routeUserId.value && !routeUserName.value) return true;
  if (
    routeUserName.value &&
    routeUserName.value === (userStore.userInfo?.nickname || userStore.userInfo?.name)
  ) {
    return true;
  }
  return Number(routeUserId.value) === Number(userStore.userInfo?.id);
});

const displayUser = computed(() => {
  if (isOwnProfile.value) return userStore.userInfo || {};
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

// 数据源
const authoredPosts = computed(() => {
  if (isOwnProfile.value) return postsStore.myPosts;
  if (!displayUser.value?.id) return [];
  return postsStore.userPostsMap[displayUser.value.id] || [];
});
const collectedPosts = computed(() => {
  if (!isOwnProfile.value) return [];
  return postsStore.myCollections;
});

// 分页截取渲染的数据
const displayedAuthoredPosts = computed(() => authoredPosts.value.slice(0, postsVisible.value));
const displayedCollectedPosts = computed(() => collectedPosts.value.slice(0, collectVisible.value));

// 是否还有更多
const hasMorePosts = computed(() => postsVisible.value < authoredPosts.value.length);
const hasMoreCollect = computed(() => collectVisible.value < collectedPosts.value.length);

// 展开动作
function handleExpandPosts() {
  postsExpanded.value = true;
  postsVisible.value = pageSize;
}
function handleExpandCollect() {
  collectExpanded.value = true;
  collectVisible.value = pageSize;
}

// 触底加载动作
function handleReachBottom() {
  if (activeTab.value === 'posts') {
    if (!postsExpanded.value || postsLoading.value || !hasMorePosts.value) return;
    postsLoading.value = true;
    setTimeout(() => {
      postsVisible.value += pageSize;
      postsLoading.value = false;
    }, 800);
  } else if (activeTab.value === 'collect') {
    if (!collectExpanded.value || collectLoading.value || !hasMoreCollect.value) return;
    collectLoading.value = true;
    setTimeout(() => {
      collectVisible.value += pageSize;
      collectLoading.value = false;
    }, 800);
  }
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

const settingItems = [
  { icon: "🔔", label: "消息通知设置", action: () => toast("开发中") },
  { icon: "🔒", label: "隐私设置", action: () => toast("开发中") },
  { icon: "🎨", label: "外观设置", action: () => toast("开发中") },
  { icon: "❓", label: "帮助与反馈", action: () => toast("开发中") },
  { icon: "📋", label: "用户协议", action: () => toast("开发中") },
  { icon: "ℹ️", label: "关于校园墙", action: () => toast("v1.0.0") },
];

function toast(msg) {
  uni.showToast({ title: msg, icon: "none" });
}

function goMyPosts() {
  activeTab.value = "posts";
}

function goFans(type) {
  const uid = displayUser.value?.id;
  if (!uid) return;
  uni.navigateTo({
    url: `/pages/user-fans/index?userId=${uid}&type=${type}`,
  });
}

function goPublish() {
  uni.navigateTo({ url: "/pages/publish/index" });
}

function changeAvatar() {
  if (!isOwnProfile.value) return;
  uni.chooseImage({
    count: 1,
    sizeType: ["compressed"],
    sourceType: ["album", "camera"],
    success: async ({ tempFilePaths }) => {
      try {
        await userStore.uploadAvatar(tempFilePaths[0]);
        uni.showToast({ title: "头像已更新", icon: "success" });
      } catch (error) {
        uni.showToast({ title: error?.message || "更新失败", icon: "none" });
      }
    },
  });
}

function editProfile() {
  if (!isOwnProfile.value) return;
  editForm.nickname = userStore.userInfo?.nickname || "";
  editForm.school = userStore.userInfo?.school || "";
  editForm.bio = userStore.userInfo?.bio || "";
  showEditModal.value = true;
}

async function saveProfile() {
  try {
    await userStore.updateProfile({
      nickname: editForm.nickname,
      school: editForm.school,
      bio: editForm.bio,
    });
    showEditModal.value = false;
    uni.showToast({ title: "保存成功", icon: "success" });
  } catch (error) {
    uni.showToast({ title: error?.message || "保存失败", icon: "none" });
  }
}

function onPostLongPress(post) {
  uni.showActionSheet({
    itemList: ["删除帖子"],
    itemColor: "#ff4d4f",
    success: ({ tapIndex }) => {
      if (tapIndex === 0) confirmDeletePost(post);
    },
  });
}

function confirmDeletePost(post) {
  uni.showModal({
    title: "提示",
    content: `确定删除帖子「${post.title || ""}」吗？`,
    confirmColor: "#ff4d4f",
    success: async ({ confirm }) => {
      if (!confirm) return;
      try {
        await postsStore.deletePost(post.id);
        uni.showToast({ title: "已删除", icon: "success" });
      } catch (error) {
        uni.showToast({ title: error?.message || "删除失败", icon: "none" });
      }
    },
  });
}

function onLogout() {
  uni.showModal({
    title: "提示",
    content: "确定退出登录吗？",
    success: ({ confirm }) => {
      if (confirm) {
        userStore.logout();
        uni.reLaunch({ url: "/pages/login/index" });
      }
    },
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

/* ================= 头部高级感样式 ================= */
.profile-header-modern {
  position: relative;
  padding-bottom: 20rpx;
  background: var(--bg, #f5f6f8);
}

.header-bg-modern {
  position: relative;
  height: 280rpx;
  background: linear-gradient(135deg, #ff7a1a 0%, #ff5722 100%);
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

.avatar-edit-badge {
  position: absolute;
  bottom: 8rpx;
  right: 8rpx;
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  background: #ffffff;
  color: #ff5722;
  font-size: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #eeeeee;
  box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.1);
  z-index: 2;
}

.action-buttons-modern {
  display: flex;
  gap: 16rpx;
  padding-bottom: 8rpx;
}

.btn-edit-modern {
  height: 64rpx;
  border-radius: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: 600;
  padding: 0 36rpx;
  transition: all 0.2s;
  background: #F5F6F8;
  color: #333333;
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
  height: 16rpx;
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
  color: #999999;
  font-weight: 600;
  position: relative;
  transition: color 0.3s;
}

.ptab.active {
  color: var(--primary, #ff7a1a);
  font-weight: 700;
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
  padding: 80rpx 0;
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
.empty-btn {
  margin-top: 16rpx;
  background: linear-gradient(90deg, #ff7a1a, #ff5722);
  color: #fff;
  border-radius: 100rpx;
  padding: 14rpx 48rpx;
  font-size: 28rpx;
  box-shadow: 0 6rpx 16rpx rgba(255, 87, 34, 0.25);
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

/* ================= 设置与退出区 ================= */
.settings-section {
  background: #ffffff;
  margin-top: 16rpx;
  padding: 32rpx 32rpx 8rpx;
}

.settings-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #1A1A1A;
  margin-bottom: 12rpx;
  display: block;
}

.settings-item {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  gap: 24rpx;
}
.settings-item:last-child {
  border-bottom: none;
}
.settings-icon {
  font-size: 36rpx;
  width: 48rpx;
  text-align: center;
}
.settings-label {
  flex: 1;
  font-size: 30rpx;
  color: #333333;
}
.settings-arrow {
  font-size: 36rpx;
  color: #cccccc;
}

.logout-wrap {
  padding: 40rpx 32rpx 20rpx;
}

.logout-btn {
  width: 100%;
  height: 96rpx;
  background: #ffffff;
  border-radius: 200rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: 600;
  color: #ff3b30;
  border: 2rpx solid rgba(255, 59, 48, 0.15);
}

/* ================= 编辑弹窗 ================= */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  transition: opacity 0.3s ease;
  opacity: 0;
  pointer-events: none;
}

.modal-mask-active {
  opacity: 1;
  pointer-events: auto;
}

.modal-wrap {
  background: #ffffff;
  border-radius: 40rpx 40rpx 0 0;
  width: 100%;
  padding: 32rpx 32rpx calc(40rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.3s cubic-bezier(0.2, 0, 0, 1);
}

.modal-mask-active .modal-wrap {
  transform: translateY(0);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32rpx;
  padding: 0 8rpx;
}

.modal-title {
  font-size: 34rpx;
  font-weight: 800;
  color: #333;
}

.modal-close {
  font-size: 44rpx;
  color: #999;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 50%;
}

.modal-form {
  max-height: 60vh;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  margin-bottom: 40rpx;
}

.modal-field {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-bottom: 24rpx;
  padding: 0 8rpx;
}

.field-label {
  font-size: 26rpx;
  font-weight: 600;
  color: #888;
}

.field-input {
  border-bottom: 2rpx solid #f0f0f0;
  padding: 16rpx 0;
  font-size: 32rpx;
  color: #333;
  transition: border-color 0.2s;
}

.field-input:focus {
  border-bottom-color: var(--primary, #ff7a1a);
}

.field-textarea {
  background: #f8f8f8;
  border-radius: 20rpx;
  padding: 24rpx;
  font-size: 30rpx;
  color: #333;
  width: 100%;
  box-sizing: border-box;
  min-height: 200rpx;
  line-height: 1.6;
}

.modal-footer {
  display: flex;
  gap: 20rpx;
  padding: 0 8rpx;
}

.modal-cancel {
  flex: 1;
  height: 88rpx;
  border-radius: 200rpx;
  border: 1rpx solid #eeeeee;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: #666;
}

.modal-confirm {
  flex: 2;
  height: 88rpx;
  border-radius: 200rpx;
  background: var(--primary, #ff7a1a);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: #ffffff;
  font-weight: 600;
}
</style>