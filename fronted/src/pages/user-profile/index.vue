<template>
  <view class="profile-page">
    <scroll-view scroll-y class="scroll-area">
      <view class="profile-header">
        <view class="header-bg"></view>
        <view class="header-content">
          <view class="avatar-wrap">
            <image
              v-if="displayUser.avatar && displayUser.avatar.includes('/')"
              class="user-avatar"
              :src="displayUser.avatar"
              mode="aspectFill"
            />
            <view v-else class="user-avatar">{{ displayUser.avatar }}</view>
          </view>
          <view class="user-info">
            <text class="user-nickname">{{ displayUser.nickname }}</text>
            <text class="user-school">{{ displayUser.school }}</text>
            <text class="user-bio">{{
              displayUser.bio || "这个人很懒，什么都没写~"
            }}</text>
          </view>
        </view>

        <view class="stat-bar">
          <view class="stat-item">
            <text class="stat-num">{{ displayPostCount }}</text>
            <text class="stat-label">帖子</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-num">{{ displayLikeCount }}</text>
            <text class="stat-label">获赞</text>
          </view>
        </view>

        <view class="profile-actions">
          <view class="action-btn action-btn-primary" @tap="startChat">私信 TA</view>
        </view>
      </view>

      <view class="profile-tabs">
        <view class="ptab active">TA 的帖子</view>
      </view>

      <view class="post-list-section">
        <view v-if="authoredPosts.length === 0" class="empty-tip">
          <text class="empty-emoji">📝</text>
          <text class="empty-text">TA 还没有发过帖子</text>
        </view>
        <PostCard v-for="post in authoredPosts" :key="post.id" :post="post" />
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

onLoad((options) => {
  routeUserId.value = options.userId ? Number(options.userId) : null;
  routeUserName.value = options.name ? decodeURIComponent(options.name) : "";
  if (routeUserId.value) {
    userStore.fetchUserById(routeUserId.value).catch(() => {});
    postsStore.fetchUserPosts(routeUserId.value).catch(() => {});
  }
});

onShow(() => {
  if (routeUserId.value) {
    userStore.fetchUserById(routeUserId.value).catch(() => {});
    postsStore.fetchUserPosts(routeUserId.value).catch(() => {});
  }
});

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

const authoredPosts = computed(() => {
  if (!displayUser.value?.id) return [];
  return postsStore.userPostsMap[displayUser.value.id] || [];
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
  background: var(--bg);
}

.scroll-area {
  height: 100vh;
}

.profile-header {
  background: #ffffff;
  position: relative;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 240rpx;
  background: linear-gradient(160deg, #ff7a1a 0%, #ffb05e 100%);
}

.header-content {
  position: relative;
  padding: 40rpx 32rpx 20rpx;
  display: flex;
  align-items: flex-end;
  gap: 20rpx;
}

.avatar-wrap {
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

.user-info {
  flex: 1;
  padding-bottom: 4rpx;
}

.user-nickname {
  font-size: 36rpx;
  font-weight: 800;
  color: var(--text-main);
  display: block;
  margin-bottom: 6rpx;
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

.profile-actions {
  padding: 0 32rpx 28rpx;
}

.action-btn {
  height: 84rpx;
  border-radius: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 700;
}

.action-btn-primary {
  background: var(--primary);
  color: #ffffff;
}

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
  color: var(--primary);
  font-weight: 600;
  border-bottom: 4rpx solid var(--primary);
  margin-bottom: -1rpx;
}

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
</style>
