<template>
  <view class="detail-page">
    <scroll-view scroll-y class="scroll-area">
      <view v-if="post" class="content-wrap">
        <!-- 标签 -->
        <view class="detail-tags">
          <text class="tag" :class="`tag-${post.tagColor}`">{{
            post.tag
          }}</text>
          <text v-if="post.isAnon" class="tag tag-anon">匿名</text>
        </view>

        <!-- 标题 -->
        <text class="detail-title">{{ post.title }}</text>

        <!-- 作者信息 -->
        <view class="author-row">
          <image
            v-if="String(post.authorAvatar).includes('/')"
            :src="post.authorAvatar"
            class="author-avatar"
            mode="aspectFill"
          />
          <view v-else class="author-avatar">{{ post.authorAvatar }}</view>
          <view class="author-info" @tap="goAuthorProfile">
            <text class="author-name">{{
              post.isAnon ? "匿名用户" : post.author
            }}</text>
            <text class="author-time">{{ post.fullTime }}</text>
          </view>
          <view class="follow-btn" @tap="onFollow">+ 关注</view>
        </view>

        <!-- 正文 -->
        <view class="detail-content">
          <ParsedPostText
            class="content-text"
            :content="post.content"
            multiline
            @mention="goMentionProfile"
            @tag="goTagSearch"
          />
        </view>

        <!-- 图片 -->
        <view v-if="post.images && post.images.length" class="detail-imgs">
          <image
            v-for="(img, i) in post.images"
            :key="i"
            :src="img"
            class="detail-img"
            mode="aspectFill"
            @tap="previewImg(i)"
          />
        </view>

        <!-- 点赞/收藏统计 -->
        <view class="stat-row">
          <view class="stat-item" :class="{ active: post.liked }" @tap="onLike">
            <text class="stat-icon">{{ post.liked ? "❤️" : "🤍" }}</text>
            <text class="stat-num">{{ post.likes }} 人觉得有用</text>
          </view>
          <view class="stat-item">
            <text class="stat-icon">💬</text>
            <text class="stat-num">{{ post.commentCount }} 条评论</text>
          </view>
        </view>

        <!-- 分割线 -->
        <view class="divider"></view>

        <!-- 评论区 -->
        <view class="comments-section">
          <text class="comments-title"
            >💬 全部评论 {{ post.commentCount }}</text
          >

          <view v-if="post.comments.length === 0" class="no-comment">
            <text>还没有评论，来抢沙发吧 🛋️</text>
          </view>

          <view
            v-for="comment in post.comments"
            :key="comment.id"
            class="comment-item"
            :class="{ 'is-author': comment.isAuthor }"
          >
            <image
              v-if="String(comment.avatar).includes('/')"
              :src="comment.avatar"
              class="comment-av"
              mode="aspectFill"
            />
            <view v-else class="comment-av">{{ comment.avatar }}</view>
            <view class="comment-right">
              <view class="comment-name-row">
                <text class="comment-name">{{ comment.author }}</text>
                <text v-if="comment.isAuthor" class="author-badge">楼主</text>
              </view>
              <ParsedPostText
                class="comment-text"
                :content="comment.content"
                multiline
                @mention="goMentionProfile"
                @tag="goTagSearch"
              />
              <view class="comment-footer">
                <text class="comment-time">{{ comment.time }}</text>
                <view class="comment-like" @tap="likeComment(comment)">
                  <text class="comment-like-icon">{{
                    comment.liked ? "❤️" : "🤍"
                  }}</text>
                  <text class="comment-like-num">{{ comment.likes }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view style="height: 120rpx"></view>
      </view>

      <view v-else class="loading-wrap">
        <text>加载中...</text>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="action-bar safe-area-bottom" v-if="post">
      <view class="comment-input" @tap="focusInput">
        <text class="input-placeholder">说点什么...</text>
      </view>
      <view class="action-btns">
        <view class="action-btn" :class="{ liked: post.liked }" @tap="onLike">
          <text class="action-btn-icon">{{ post.liked ? "❤️" : "🤍" }}</text>
          <text class="action-btn-num">{{ post.likes }}</text>
        </view>
        <view
          class="action-btn"
          :class="{ collected: post.collected }"
          @tap="onCollect"
        >
          <text class="action-btn-icon">{{ post.collected ? "⭐" : "☆" }}</text>
        </view>
        <view class="action-btn" @tap="onShare">
          <text class="action-btn-icon">↗</text>
        </view>
      </view>
    </view>

    <!-- 评论输入框弹窗 -->
    <view
      v-if="showCommentInput"
      class="comment-popup-mask"
      :class="{ 'mask-active': showCommentInput }"
      @tap.self="hideInput"
      @touchmove.stop.prevent
    >
      <view class="comment-popup-inner" @tap.stop>
        <view class="popup-header">
          <text class="popup-title">发表评论</text>
          <text class="popup-close" @tap="hideInput">✕</text>
        </view>
        <textarea
          class="comment-textarea"
          v-model="commentText"
          placeholder="说点好听的吧..."
          :auto-focus="true"
          :fixed="true"
          :cursor-spacing="40"
          :adjust-position="true"
          :show-confirm-bar="false"
          maxlength="200"
        />
        <view class="comment-popup-footer">
          <text class="word-count">{{ commentText.length }}/200</text>
          <view
            class="send-btn"
            :class="{ 'send-btn-active': commentText.trim() }"
            @tap="submitComment"
          >
            发送
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from "vue";
import { onLoad, onShow } from "@dcloudio/uni-app";
import { usePostsStore } from "@/stores/posts";
import { useUserStore } from "@/stores/user";
import ParsedPostText from "@/components/ParsedPostText.vue";

const postsStore = usePostsStore();
const userStore = useUserStore();

// 使用响应式的 postId
const postId = ref(0);

// ✅ 在 onLoad 中安全地获取路由参数
onLoad((options) => {
  if (options.id) {
    postId.value = Number(options.id);
    postsStore.fetchPostDetail(postId.value).catch(showError);
  }
});

onShow(() => {
  if (postId.value) {
    postsStore.fetchPostDetail(postId.value).catch(() => {});
  }
});

// 让 post 依赖响应式的 postId.value
const post = computed(() => postsStore.getPostById(postId.value));

const showCommentInput = ref(false);
const commentText = ref("");

function goBack() {
  uni.navigateBack();
}

function showMore() {
  uni.showActionSheet({
    itemList: ["复制链接", "举报", "不感兴趣"],
    success: ({ tapIndex }) => {
      if (tapIndex === 0) uni.showToast({ title: "链接已复制", icon: "none" });
      if (tapIndex === 1)
        uni.showToast({ title: "已举报，感谢反馈", icon: "none" });
    },
  });
}

async function onLike() {
  try {
    await postsStore.toggleLike(postId.value);
  } catch (error) {
    showError(error);
  }
}

async function onCollect() {
  try {
    await postsStore.toggleCollect(postId.value);
  } catch (error) {
    showError(error);
  }
}

function onShare() {
  uni.showToast({ title: "分享功能开发中", icon: "none" });
}

function onFollow() {
  uni.showToast({ title: "已关注", icon: "success" });
}

function goAuthorProfile() {
  if (!post.value || post.value.isAnon || !post.value.authorId) return;
  uni.navigateTo({
    url: `/pages/user-profile/index?userId=${post.value.authorId}`,
  });
}

function goMentionProfile(name) {
  uni.navigateTo({ url: `/pages/user-profile/index?name=${encodeURIComponent(name)}` });
}

function goTagSearch(tag) {
  uni.navigateTo({ url: `/pages/search/index?keyword=${encodeURIComponent(tag)}` });
}

function previewImg(index) {
  if (!post.value?.images?.length) return;
  uni.previewImage({
    current: post.value.images[index],
    urls: post.value.images,
  });
}

function focusInput() {
  showCommentInput.value = true;
}

function hideInput() {
  showCommentInput.value = false;
  commentText.value = "";
}

async function submitComment() {
  if (!commentText.value.trim()) return;
  try {
    await postsStore.addComment(postId.value, {
      content: commentText.value.trim(),
    });
    commentText.value = "";
    showCommentInput.value = false;
    uni.showToast({ title: "评论成功", icon: "success" });
  } catch (error) {
    showError(error);
  }
}

async function likeComment(comment) {
  try {
    await postsStore.likeComment({
      ...comment,
      postId: post.value?.id,
    });
  } catch (error) {
    showError(error);
  }
}

function showError(error) {
  uni.showToast({
    title: error?.message || "操作失败",
    icon: "none",
  });
}
</script>

<style scoped>
.tag {
  display: inline-block;
  font-size: 20rpx;
  border-radius: 8rpx;
  padding: 2rpx 10rpx;
  line-height: 1.6;
}

.tag-anon {
  background: #fff0ec;
  color: #ff5a35;
}
.tag-orange {
  background: #fff0ec;
  color: #ff5a35;
}
.tag-blue {
  background: #eef5ff;
  color: #3d7ee8;
}
.tag-pink {
  background: #fff0f6;
  color: #e84393;
}
.tag-green {
  background: #eefaf0;
  color: #29a84a;
}
.tag-gray {
  background: #f5f5f5;
  color: #888888;
}

.detail-page {
  min-height: 100vh;
  background: #ffffff;
  display: flex;
  flex-direction: column;
}

/* 导航 */
.nav-bar {
  background: #ffffff;
  border-bottom: 1rpx solid var(--border);
  padding-top: var(--status-bar-height, 44px);
  position: sticky;
  top: 0;
  z-index: 100;
  flex-shrink: 0;
}

.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 28rpx;
}

.back-btn {
  font-size: 30rpx;
  color: var(--text-main);
  padding: 8rpx 0;
  min-width: 80rpx;
}

.nav-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-main);
}

.more-btn {
  font-size: 40rpx;
  color: var(--text-sub);
  padding: 8rpx 0;
  min-width: 80rpx;
  text-align: right;
}

/* 滚动区 */
.scroll-area {
  flex: 1;
  height: calc(100vh - 100rpx - var(--status-bar-height, 44px));
}

.content-wrap {
  padding: 28rpx 32rpx 0;
}

/* 标签行 */
.detail-tags {
  display: flex;
  gap: 10rpx;
  margin-bottom: 18rpx;
  flex-wrap: wrap;
}

/* 标题 */
.detail-title {
  font-size: 38rpx;
  font-weight: 800;
  color: var(--text-main);
  line-height: 1.45;
  margin-bottom: 22rpx;
  display: block;
}

/* 作者行 */
.author-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 26rpx;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid var(--border);
}

.author-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #f5f5f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34rpx;
  flex-shrink: 0;
}

.author-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.author-name {
  font-size: 26rpx;
  font-weight: 600;
  color: var(--text-main);
}

.author-time {
  font-size: 22rpx;
  color: var(--text-hint);
}

.follow-btn {
  font-size: 24rpx;
  color: var(--primary);
  border: 1rpx solid var(--primary);
  border-radius: 100rpx;
  padding: 6rpx 22rpx;
}

/* 正文 */
.detail-content {
  margin-bottom: 24rpx;
}

.content-text {
  font-size: 30rpx;
  color: var(--text-sub);
  line-height: 1.9;
  white-space: pre-wrap;
  display: block;
}

/* 图片 */
.detail-imgs {
  display: flex;
  gap: 10rpx;
  flex-wrap: wrap;
  margin-bottom: 24rpx;
}

.detail-img {
  width: 200rpx;
  height: 200rpx;
  border-radius: 16rpx;
  background: #f0ede8;
}

/* 统计行 */
.stat-row {
  display: flex;
  gap: 32rpx;
  padding: 20rpx 0;
  border-top: 1rpx solid var(--border);
  border-bottom: 1rpx solid var(--border);
  margin-bottom: 4rpx;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.stat-item.active .stat-num {
  color: var(--primary);
}

.stat-icon {
  font-size: 28rpx;
}
.stat-num {
  font-size: 24rpx;
  color: var(--text-hint);
}

/* 分割线 */
.divider {
  height: 12rpx;
  background: var(--bg);
  margin: 0 -32rpx;
}

/* 评论区 */
.comments-section {
  padding: 24rpx 0;
}

.comments-title {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--text-sub);
  margin-bottom: 22rpx;
  display: block;
}

.no-comment {
  text-align: center;
  padding: 48rpx 0;
  color: var(--text-hint);
  font-size: 28rpx;
}

.comment-item {
  display: flex;
  gap: 16rpx;
  margin-bottom: 26rpx;
  padding: 16rpx;
  border-radius: 16rpx;
}

.comment-item.is-author {
  background: var(--primary-light);
}

.comment-av {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #f5f5f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  flex-shrink: 0;
}

.comment-right {
  flex: 1;
}

.comment-name-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-bottom: 6rpx;
}

.comment-name {
  font-size: 26rpx;
  font-weight: 600;
  color: var(--text-main);
}

.author-badge {
  font-size: 20rpx;
  background: var(--primary);
  color: #fff;
  border-radius: 6rpx;
  padding: 2rpx 8rpx;
}

.comment-text {
  font-size: 28rpx;
  color: var(--text-sub);
  line-height: 1.7;
  display: block;
  margin-bottom: 8rpx;
}

.comment-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.comment-time {
  font-size: 22rpx;
  color: var(--text-hint);
}

.comment-like {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.comment-like-icon {
  font-size: 22rpx;
}
.comment-like-num {
  font-size: 22rpx;
  color: var(--text-hint);
}

/* 底部操作栏 */
.action-bar {
  background: #ffffff;
  border-top: 1rpx solid #f0f0f0;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  gap: 20rpx;
  flex-shrink: 0;
  z-index: 100;
}

.comment-input {
  flex: 1;
  background: #f5f5f5;
  border-radius: 100rpx;
  padding: 18rpx 32rpx;
  display: flex;
  align-items: center;
  transition: all 0.2s;
}

.comment-input:active {
  background: #eeeeee;
}

.input-placeholder {
  font-size: 28rpx;
  color: #999999;
}

.action-btns {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8rpx;
  min-width: 64rpx;
  transition: all 0.2s;
}

.action-btn:active {
  transform: scale(0.9);
}

.action-btn.liked .action-btn-icon {
  color: #ff4d4f;
}

.action-btn.collected .action-btn-icon {
  color: #ffcc00;
}

.action-btn-icon {
  font-size: 36rpx;
  line-height: 1;
}

.action-btn-num {
  font-size: 20rpx;
  color: #999999;
  margin-top: 4rpx;
}

/* 评论输入弹窗优化 */
.comment-popup-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  opacity: 0;
  transition: opacity 0.25s ease;
  pointer-events: none;
}

.mask-active {
  opacity: 1;
  pointer-events: auto;
}

.comment-popup-inner {
  background: #ffffff;
  border-radius: 40rpx 40rpx 0 0;
  padding: 32rpx 32rpx calc(32rpx + env(safe-area-inset-bottom));
  transform: translateY(100%);
  transition: transform 0.3s cubic-bezier(0.2, 0, 0, 1);
  box-shadow: 0 -8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.mask-active .comment-popup-inner {
  transform: translateY(0);
}

.popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
  padding: 0 4rpx;
}

.popup-title {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--text-main);
}

.popup-close {
  font-size: 32rpx;
  color: var(--text-hint);
  padding: 8rpx;
}

.comment-textarea {
  width: 100%;
  height: 240rpx;
  font-size: 32rpx;
  color: var(--text-main);
  line-height: 1.6;
  background: #f7f7f7;
  border-radius: 24rpx;
  padding: 24rpx;
  box-sizing: border-box;
  margin-bottom: 24rpx;
}

.comment-popup-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4rpx;
}

.word-count {
  font-size: 24rpx;
  color: var(--text-hint);
}

.send-btn {
  width: 160rpx;
  height: 72rpx;
  background: #f0f0f0;
  color: #bbbbbb;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 600;
  transition: all 0.2s;
}

.send-btn-active {
  background: var(--primary);
  color: #ffffff;
  box-shadow: 0 4rpx 12rpx rgba(255, 90, 53, 0.3);
}

.send-btn:active {
  transform: scale(0.96);
}

.loading-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  color: var(--text-hint);
  font-size: 28rpx;
}
</style>
