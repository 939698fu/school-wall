<template>
  <view class="profile-page">
    <scroll-view scroll-y class="scroll-area">
      <!-- 头部用户信息卡片 -->
      <view class="profile-header">
        <view class="header-bg"></view>
        <view class="header-content">
          <view class="avatar-wrap" @tap="changeAvatar">
            <image
              v-if="displayUser.avatar && displayUser.avatar.includes('/')"
              class="user-avatar"
              :src="displayUser.avatar"
              mode="aspectFill"
            />
            <view v-else class="user-avatar">{{ displayUser.avatar }}</view>
            <view v-if="isOwnProfile" class="avatar-edit-badge">✏</view>
          </view>
          <view class="user-info">
            <view class="nickname-row">
              <text class="user-nickname">{{ displayUser.nickname }}</text>
              <view v-if="isOwnProfile" class="edit-btn" @tap="editProfile"
                >编辑资料</view
              >
            </view>
            <text class="user-school">{{ displayUser.school }}</text>
            <text class="user-bio">{{
              displayUser.bio || "这个人很懒，什么都没写~"
            }}</text>
          </view>
        </view>

        <!-- 数据统计 -->
        <view class="stat-bar">
          <view class="stat-item" @tap="goMyPosts">
            <text class="stat-num">{{ displayPostCount }}</text>
            <text class="stat-label">帖子</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-num">{{ displayLikeCount }}</text>
            <text class="stat-label">获赞</text>
          </view>
          <template v-if="isOwnProfile">
            <view class="stat-divider"></view>
            <view class="stat-item" @tap="activeTab = 'collect'">
              <text class="stat-num">{{ displayCollectCount }}</text>
              <text class="stat-label">收藏</text>
            </view>
          </template>
        </view>
      </view>

      <!-- Tab 切换：我的帖子 / 收藏 -->
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

      <!-- 帖子列表 -->
      <view class="post-list-section" v-if="activeTab === 'posts'">
        <view v-if="authoredPosts.length === 0" class="empty-tip">
          <text class="empty-emoji">📝</text>
          <text class="empty-text">{{
            isOwnProfile ? "还没有发过帖子" : "TA 还没有发过帖子"
          }}</text>
          <view v-if="isOwnProfile" class="empty-btn" @tap="goPublish"
            >去发帖</view
          >
        </view>
        <PostCard v-for="post in authoredPosts" :key="post.id" :post="post" />
      </view>

      <view class="post-list-section" v-if="isOwnProfile && activeTab === 'collect'">
        <view v-if="collectedPosts.length === 0" class="empty-tip">
          <text class="empty-emoji">⭐</text>
          <text class="empty-text">还没有收藏任何帖子</text>
        </view>
        <PostCard v-for="post in collectedPosts" :key="post.id" :post="post" />
      </view>

      <!-- 设置菜单 -->
      <view v-if="isOwnProfile" class="settings-section">
        <text class="settings-title">设置</text>
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

      <!-- 退出登录 -->
      <view v-if="isOwnProfile" class="logout-wrap">
        <view class="logout-btn" @tap="onLogout">退出登录</view>
      </view>

      <view style="height: 40rpx"></view>
    </scroll-view>

    <!-- 编辑资料弹窗 -->
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

const authoredPosts = computed(() => {
  if (isOwnProfile.value) {
    return postsStore.myPosts;
  }
  if (!displayUser.value?.id) return [];
  return postsStore.userPostsMap[displayUser.value.id] || [];
});

const collectedPosts = computed(() => {
  if (!isOwnProfile.value) return [];
  return postsStore.myCollections;
});

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

const displayCollectCount = computed(
  () => Number(displayUser.value?.collectCount ?? collectedPosts.value.length ?? 0),
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
  background: var(--bg);
}

.scroll-area {
  height: 100vh;
}

/* 头部卡片 */
.profile-header {
  background: #ffffff;
  position: relative;
  padding-top: var(--status-bar-height, 44px);
  margin-bottom: 0;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 240rpx;
  background: linear-gradient(160deg, #ff5a35 0%, #ff9060 100%);
}

.header-content {
  position: relative;
  padding: 40rpx 32rpx 20rpx;
  display: flex;
  align-items: flex-end;
  gap: 20rpx;
}

.avatar-wrap {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  background: #fff;
  border: 4rpx solid #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.12);
}

.avatar-edit-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: var(--primary);
  color: #fff;
  font-size: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #fff;
}

.user-info {
  flex: 1;
  padding-bottom: 4rpx;
}

.nickname-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6rpx;
}

.user-nickname {
  font-size: 36rpx;
  font-weight: 800;
  color: var(--text-main);
}

.edit-btn {
  font-size: 24rpx;
  color: var(--text-sub);
  border: 1rpx solid var(--border);
  border-radius: 100rpx;
  padding: 6rpx 22rpx;
  background: #fff;
}

.user-school {
  font-size: 24rpx;
  color: var(--text-hint);
  display: block;
  margin-bottom: 6rpx;
}

.user-bio {
  font-size: 26rpx;
  color: var(--text-sub);
  display: block;
}

/* 统计条 */
.stat-bar {
  display: flex;
  align-items: center;
  padding: 24rpx 40rpx;
  margin-top: 8rpx;
  border-top: 1rpx solid var(--border);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

.stat-num {
  font-size: 36rpx;
  font-weight: 800;
  color: var(--text-main);
}

.stat-label {
  font-size: 22rpx;
  color: var(--text-hint);
}

.stat-divider {
  width: 1rpx;
  height: 48rpx;
  background: var(--border);
}

/* 内容 Tab */
.profile-tabs {
  display: flex;
  background: #ffffff;
  border-bottom: 1rpx solid var(--border);
  margin-top: 12rpx;
}

.ptab {
  flex: 1;
  padding: 22rpx 0;
  text-align: center;
  font-size: 28rpx;
  color: var(--text-hint);
  border-bottom: 4rpx solid transparent;
  margin-bottom: -1rpx;
}

.ptab.active {
  color: var(--primary);
  font-weight: 600;
  border-bottom-color: var(--primary);
}

/* 帖子区 */
.post-list-section {
  padding-top: 8rpx;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0;
  gap: 16rpx;
}

.empty-emoji {
  font-size: 64rpx;
}
.empty-text {
  font-size: 28rpx;
  color: var(--text-hint);
}
.empty-btn {
  margin-top: 8rpx;
  background: var(--primary);
  color: #fff;
  border-radius: 100rpx;
  padding: 14rpx 48rpx;
  font-size: 28rpx;
}

/* 设置区 */
.settings-section {
  background: #ffffff;
  margin-top: 16rpx;
  padding: 24rpx 32rpx 8rpx;
}

.settings-title {
  font-size: 26rpx;
  color: var(--text-hint);
  margin-bottom: 8rpx;
  display: block;
}

.settings-list {
}

.settings-item {
  display: flex;
  align-items: center;
  padding: 26rpx 0;
  border-bottom: 1rpx solid var(--border);
  gap: 20rpx;
}

.settings-item:last-child {
  border-bottom: none;
}

.settings-icon {
  font-size: 32rpx;
  width: 40rpx;
  text-align: center;
}
.settings-label {
  flex: 1;
  font-size: 30rpx;
  color: var(--text-main);
}
.settings-arrow {
  font-size: 32rpx;
  color: var(--text-hint);
}

/* 退出登录 */
.logout-wrap {
  padding: 32rpx 32rpx 16rpx;
}

.logout-btn {
  width: 100%;
  height: 88rpx;
  background: #ffffff;
  border-radius: 200rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: #ff3b30;
  border: 1rpx solid #ffcdd0;
}

/* 编辑弹窗 */
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
  color: var(--text-main);
}

.modal-close {
  font-size: 44rpx;
  color: var(--text-hint);
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
  color: var(--text-hint);
}

.field-input {
  border-bottom: 2rpx solid #f0f0f0;
  padding: 16rpx 0;
  font-size: 32rpx;
  color: var(--text-main);
  transition: border-color 0.2s;
}

.field-input:focus {
  border-bottom-color: var(--primary);
}

.field-textarea {
  background: #f8f8f8;
  border-radius: 20rpx;
  padding: 24rpx;
  font-size: 30rpx;
  color: var(--text-main);
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
  border: 1rpx solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: var(--text-sub);
}

.modal-confirm {
  flex: 2;
  height: 88rpx;
  border-radius: 200rpx;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: #ffffff;
  font-weight: 600;
}
</style>
