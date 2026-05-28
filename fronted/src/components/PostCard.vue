<template>
  <view class="post-card" @tap="goDetail">
    <!-- 头部：头像 + 作者 + 标签 -->
    <view class="post-header">
      <view class="avatar-wrap">
        <image
          v-if="String(post.authorAvatar).includes('/')"
          :src="post.authorAvatar"
          class="post-avatar"
          mode="aspectFill"
        />
        <view v-else class="post-avatar">{{ post.authorAvatar }}</view>
      </view>
      
      <view class="post-meta" @tap.stop="goProfile">
        <view class="post-author-row">
          <text class="post-author">{{
            post.isAnon ? "匿名用户" : post.author
          }}</text>
          <text v-if="post.isAnon" class="tag tag-anon">匿名</text>
          <text class="tag" :class="`tag-${post.tagColor}`">{{
            post.tag
          }}</text>
        </view>
        <text class="post-time">{{ post.time }}</text>
      </view>
    </view>

    <!-- 标题 -->
    <text class="post-title">{{ post.title }}</text>

    <!-- 正文摘要 -->
    <ParsedPostText
      class="post-excerpt"
      :content="post.content"
      :max-lines="2"
      @mention="goMentionProfile"
      @tag="goTagSearch"
    ></ParsedPostText>

    <!-- 动态图片网格（1图/2图/3图自适应排列） -->
    <view v-if="post.images && post.images.length" class="post-img-row">
      <image
        v-for="(img, i) in post.images.slice(0, 3)"
        :key="i"
        :src="img"
        class="post-img"
        :class="['img-count-' + Math.min(post.images.length, 3)]"
        mode="aspectFill"
      />
    </view>

    <!-- 底部操作栏 (极简无边框设计) -->
    <view class="post-footer">
      <view
        class="post-action"
        :class="{ liked: post.liked }"
        @tap.stop="onLike"
      >
        <image 
          class="action-icon-svg" 
          :src="post.liked ? '/static/icons/heart-fill.svg' : '/static/icons/heart-outline.svg'" 
          mode="aspectFit" 
        />
        <text class="action-num">{{ post.likes }}</text>
      </view>
      
      <view class="post-action">
        <image class="action-icon-svg" src="/static/icons/comment.svg" mode="aspectFit" />
        <text class="action-num">{{ post.commentCount }}</text>
      </view>
      
      <view
        class="post-action"
        :class="{ collected: post.collected }"
        @tap.stop="onCollect"
      >
        <image 
          class="action-icon-svg" 
          :src="post.collected ? '/static/icons/star-fill.svg' : '/static/icons/star-outline.svg'" 
          mode="aspectFit" 
        />
        <text class="action-num">收藏</text>
      </view>
      
      <view class="post-action post-share" @tap.stop="onShare">
        <image class="action-icon-svg" src="/static/icons/share.svg" mode="aspectFit" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { usePostsStore } from "@/stores/posts";

// 依赖于 easycom 或相对路径引入组件
import ParsedPostText from "./ParsedPostText.vue";

const props = defineProps({
  post: { type: Object, required: true },
});

const postsStore = usePostsStore();

function goDetail() {
  uni.navigateTo({ url: `/pages/post-detail/index?id=${props.post.id}` });
}

function goProfile() {
  if (props.post.isAnon || !props.post.authorId) return;
  uni.navigateTo({ url: `/pages/user-profile/index?userId=${props.post.authorId}` });
}

function goMentionProfile(name) {
  uni.navigateTo({ url: `/pages/user-profile/index?name=${encodeURIComponent(name)}` });
}

function goTagSearch(tag) {
  uni.navigateTo({ url: `/pages/search/index?keyword=${encodeURIComponent(tag)}` });
}

function onLike() {
  postsStore.toggleLike(props.post.id).catch((error) => {
    uni.showToast({ title: error?.message || "操作失败", icon: "none" });
  });
}

function onCollect() {
  postsStore.toggleCollect(props.post.id).catch((error) => {
    uni.showToast({ title: error?.message || "操作失败", icon: "none" });
  });
}

function onShare() {
  uni.showToast({ title: "复制链接成功！", icon: "none" });
}
</script>

<style scoped>
/* =============== 卡片外层与悬浮感 =============== */
.post-card {
  margin: 24rpx 32rpx;
  padding: 36rpx;
  background: #ffffff;
  border-radius: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.025);
  transition: all 0.25s cubic-bezier(0.1, 0.7, 0.1, 1);
  border: 1rpx solid rgba(255, 255, 255, 0.8);
}

.post-card:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.015);
}

/* =============== 标签样式 (更加通透) =============== */
.tag {
  display: inline-block;
  font-size: 20rpx;
  border-radius: 8rpx;
  padding: 4rpx 12rpx;
  line-height: 1.4;
  font-weight: 600;
}

.tag-anon { background: rgba(255, 90, 53, 0.08); color: #ff5a35; }
.tag-orange { background: rgba(255, 90, 53, 0.08); color: #ff5a35; }
.tag-blue { background: rgba(61, 126, 232, 0.08); color: #3d7ee8; }
.tag-pink { background: rgba(232, 67, 147, 0.08); color: #e84393; }
.tag-green { background: rgba(41, 168, 74, 0.08); color: #29a84a; }
.tag-gray { background: #f5f5f5; color: #888888; }

/* =============== 头部信息 =============== */
.post-header {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.avatar-wrap {
  position: relative;
}

.post-avatar {
  width: 76rpx;
  height: 76rpx;
  border-radius: 50%;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  flex-shrink: 0;
  border: 1rpx solid rgba(0, 0, 0, 0.03);
}

.post-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.post-author-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-wrap: wrap;
}

.post-author {
  font-size: 28rpx;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: 0.5rpx;
}

.post-time {
  font-size: 22rpx;
  color: #999999;
}

/* =============== 文本内容 =============== */
.post-title {
  font-size: 34rpx;
  font-weight: 800;
  color: #1a1a1a;
  line-height: 1.5;
  margin-bottom: 12rpx;
  display: block;
  letter-spacing: 0.5rpx;
}

.post-excerpt {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.65;
  margin-bottom: 24rpx;
  display: block;
  max-height: 3.3em;
  overflow: hidden;
}

/* =============== 动态图片网格设计 =============== */
.post-img-row {
  display: flex;
  gap: 12rpx;
  margin-bottom: 24rpx;
}

.post-img {
  border-radius: 16rpx;
  background: #f8f9fa;
  object-fit: cover;
}

/* 单图：横向大图，极具视觉冲击 */
.img-count-1 {
  width: 85%;
  height: 320rpx;
  border-radius: 20rpx;
}

/* 双图：对半平分 */
.img-count-2 {
  width: calc((100% - 12rpx) / 2);
  height: 240rpx;
}

/* 三图：等分三分之一 */
.img-count-3 {
  width: calc((100% - 24rpx) / 3);
  height: 210rpx;
}

/* =============== 底部操作栏 (无边框极简) =============== */
.post-footer {
  display: flex;
  align-items: center;
  gap: 40rpx; /* 拉开间距，更透气 */
  margin-top: 12rpx;
}

.post-action {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx 8rpx 0; /* 增加点击热区 */
  transition: opacity 0.2s;
}

.post-action:active {
  opacity: 0.5;
}

.post-action.liked .action-num {
  color: var(--primary);
  font-weight: 600;
}
.post-action.collected .action-num {
  color: #ffaa00;
  font-weight: 600;
}

.action-icon-svg {
  width: 36rpx;
  height: 36rpx;
  display: block;
}

.action-num {
  font-size: 24rpx;
  color: #888888;
  font-weight: 500;
}

.post-share {
  margin-left: auto;
  padding-right: 0;
}
</style>