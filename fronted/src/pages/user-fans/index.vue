<template>
  <view class="fans-page">
    <!-- Tab 切换 -->
    <view class="tab-bar">
      <view
        class="tab-item"
        :class="{ active: activeTab === 'followers' }"
        @tap="switchTab('followers')"
      >粉丝</view>
      <view
        class="tab-item"
        :class="{ active: activeTab === 'following' }"
        @tap="switchTab('following')"
      >关注</view>
    </view>

    <scroll-view scroll-y class="scroll-area">
      <view v-if="loading" class="empty-tip">
        <text>加载中...</text>
      </view>
      <view v-else-if="!list.length" class="empty-tip">
        <text>{{ activeTab === 'followers' ? '还没有粉丝' : '还没有关注任何人' }}</text>
      </view>
      <view v-else class="user-list">
        <view
          v-for="user in list"
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
          <view
            v-if="canShowFollowBtn(user)"
            class="follow-btn"
            :class="{ 'follow-btn-active': user.isFollowed }"
            @tap.stop="toggleFollow(user)"
          >{{ user.isFollowed ? "已关注" : "+ 关注" }}</view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

const routeUserId = ref(null);
const activeTab = ref("followers");
const loading = ref(false);
const followersList = ref([]);
const followingList = ref([]);
const followingBusy = ref(new Set());

const list = computed(() =>
  activeTab.value === "followers" ? followersList.value : followingList.value,
);

const currentUserId = computed(() => Number(userStore.userInfo?.id || 0));

onLoad((options) => {
  routeUserId.value = options.userId ? Number(options.userId) : currentUserId.value;
  activeTab.value = options.type === "following" ? "following" : "followers";
  uni.setNavigationBarTitle({
    title:
      Number(routeUserId.value) === currentUserId.value
        ? "我的关注与粉丝"
        : "TA 的关注与粉丝",
  });
  loadData();
});

async function loadData() {
  if (!routeUserId.value) return;
  loading.value = true;
  try {
    if (activeTab.value === "followers") {
      followersList.value = await userStore.fetchFollowers(routeUserId.value);
    } else {
      followingList.value = await userStore.fetchFollowing(routeUserId.value);
    }
  } catch (error) {
    uni.showToast({ title: error?.message || "加载失败", icon: "none" });
  } finally {
    loading.value = false;
  }
}

function switchTab(tab) {
  if (activeTab.value === tab) return;
  activeTab.value = tab;
  const target =
    tab === "followers" ? followersList.value : followingList.value;
  if (target.length === 0) {
    loadData();
  }
}

function canShowFollowBtn(user) {
  if (!currentUserId.value) return false;
  return Number(user.id) !== currentUserId.value;
}

async function toggleFollow(user) {
  if (followingBusy.value.has(user.id)) return;
  if (!userStore.isLoggedIn) {
    uni.showToast({ title: "请先登录", icon: "none" });
    return;
  }
  followingBusy.value.add(user.id);
  try {
    if (user.isFollowed) {
      await userStore.unfollowUser(user.id);
      user.isFollowed = false;
      uni.showToast({ title: "已取消关注", icon: "none" });
    } else {
      await userStore.followUser(user.id);
      user.isFollowed = true;
      uni.showToast({ title: "关注成功", icon: "success" });
    }
  } catch (error) {
    uni.showToast({ title: error?.message || "操作失败", icon: "none" });
  } finally {
    followingBusy.value.delete(user.id);
  }
}

function goUserProfile(user) {
  if (!user?.id) return;
  if (Number(user.id) === currentUserId.value) {
    uni.switchTab({ url: "/pages/profile/index" });
    return;
  }
  uni.navigateTo({ url: `/pages/user-profile/index?userId=${user.id}` });
}
</script>

<style scoped>
.fans-page {
  min-height: 100vh;
  background: var(--bg);
  display: flex;
  flex-direction: column;
}

.tab-bar {
  display: flex;
  background: #ffffff;
  border-bottom: 1rpx solid var(--border);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: var(--text-sub);
  font-weight: 600;
  position: relative;
}

.tab-item.active {
  color: var(--primary);
}

.tab-item.active::after {
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

.scroll-area {
  flex: 1;
}

.empty-tip {
  padding: 120rpx 0;
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
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid var(--border);
}

.user-avatar {
  width: 84rpx;
  height: 84rpx;
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
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.user-nickname {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-main);
}

.user-school {
  font-size: 22rpx;
  color: var(--text-hint);
}

.follow-btn {
  font-size: 24rpx;
  color: var(--primary);
  border: 1rpx solid var(--primary);
  border-radius: 100rpx;
  padding: 8rpx 24rpx;
  background: #ffffff;
  flex-shrink: 0;
}

.follow-btn-active {
  background: #f0f0f0;
  color: #999999;
  border-color: #e0e0e0;
}
</style>
