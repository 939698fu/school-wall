<template>
  <view class="publish-page">
    <scroll-view scroll-y class="scroll-area">
      <view class="form-wrap">
        <!-- 标题输入 -->
        <view class="form-item">
          <input
            class="title-input"
            v-model="title"
            placeholder="输入一个吸引人的标题..."
            :maxlength="50"
          />
          <text class="word-count">{{ title.length }}/50</text>
        </view>

        <view class="form-divider"></view>

        <!-- 正文输入 -->
        <view class="form-item">
          <textarea
            class="content-input"
            v-model="content"
            placeholder="分享你的故事、问题或想法...（支持 @ 同学，# 话题标签）"
          />
          <text class="content-tip"
            >输入如 @数学学长 或 #期末复习，发布后可直接点击跳转</text
          >
          <text class="word-count">{{ content.length }}/2000</text>
        </view>

        <!-- 图片上传 & 发布按钮 -->
        <view class="img-section">
          <view class="img-list">
            <view v-for="(img, i) in selectedImages" :key="i" class="img-item">
              <image :src="img" class="preview-img" mode="aspectFill" />
              <view class="img-del" @tap="removeImg(i)">×</view>
            </view>
            <view
              v-if="selectedImages.length < 9"
              class="img-add-btn"
              @tap="chooseImg"
            >
              <text class="img-add-icon">＋</text>
              <text class="img-add-tip">{{ selectedImages.length }}/9</text>
            </view>
            <!-- 发布按钮移到这里 -->
          </view>
          <view
            class="publish-btn-inline"
            :class="{ active: canPublish }"
            @tap="onPublish"
          >
            <text>发布</text>
          </view>
        </view>

        <view class="form-divider"></view>

        <!-- 标签选择 -->
        <view class="form-item">
          <text class="form-label">选择话题</text>
          <view class="tag-list">
            <view
              v-for="tag in availableTags"
              :key="tag.label"
              class="tag-option"
              :class="{ selected: selectedTag === tag.label }"
              @tap="selectTag(tag)"
            >
              <text>{{ tag.label }}</text>
            </view>
          </view>
        </view>

        <view class="form-divider"></view>

        <!-- 匿名开关 -->
        <view class="form-item switch-row">
          <view class="switch-info">
            <text class="switch-label">匿名发布</text>
            <text class="switch-desc">开启后其他用户看不到你的昵称</text>
          </view>
          <switch
            :checked="isAnon"
            color="#FF5A35"
            @change="isAnon = $event.detail.value"
          />
        </view>

        <view class="form-divider"></view>

        <!-- 仅自己可见 -->
        <view class="form-item switch-row">
          <view class="switch-info">
            <text class="switch-label">仅自己可见</text>
            <text class="switch-desc">发布后只有自己能看到</text>
          </view>
          <switch
            :checked="onlyMe"
            color="#FF5A35"
            @change="onlyMe = $event.detail.value"
          />
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed } from "vue";
import { usePostsStore } from "@/stores/posts";
import { useUserStore } from "@/stores/user";

const postsStore = usePostsStore();
const userStore = useUserStore();

const title = ref("");
const content = ref("");
const selectedImages = ref([]);
const selectedTag = ref("");
const isAnon = ref(false);
const onlyMe = ref(false);
const publishing = ref(false);

const availableTags = [
  { value: "orange", label: "🍜 美食" },
  { value: "blue", label: "📚 学习" },
  { value: "pink", label: "💕 表白" },
  { value: "green", label: "🌱 资源" },
  { value: "gray", label: "😤 吐槽" },
  { value: "orange", label: "🎮 娱乐" },
  { value: "blue", label: "🏃 运动" },
  { value: "gray", label: "🏠 校园生活" },
];

const canPublish = computed(
  () =>
    !publishing.value &&
    title.value.trim().length >= 2 &&
    content.value.trim().length >= 10,
);

function chooseImg() {
  uni.chooseImage({
    count: 9 - selectedImages.value.length,
    sizeType: ["compressed"],
    sourceType: ["album", "camera"],
    success: ({ tempFilePaths }) => {
      selectedImages.value.push(...tempFilePaths);
    },
  });
}

function removeImg(index) {
  selectedImages.value.splice(index, 1);
}

function selectTag(tag) {
  selectedTag.value = selectedTag.value === tag.label ? "" : tag.label;
}

async function onPublish() {
  if (!userStore.isLoggedIn) {
    uni.showToast({ title: "请先登录", icon: "none" });
    return;
  }
  if (!canPublish.value) return;
  const tagEntry = availableTags.find((t) => t.label === selectedTag.value) || {
    value: "gray",
    label: "校园生活",
  };
  publishing.value = true;
  try {
    const images = await postsStore.uploadPostImages(selectedImages.value);
    await postsStore.createPost({
      title: title.value.trim(),
      content: content.value.trim(),
      images,
      tag: selectedTag.value
        ? selectedTag.value.replace(/^.+?\s/, "")
        : "校园生活",
      tagColor: tagEntry.value,
      isAnon: isAnon.value,
      isPrivate: onlyMe.value,
    });
    uni.showToast({ title: "发布成功！", icon: "success" });
    setTimeout(() => {
      uni.navigateBack();
    }, 800);
  } catch (error) {
    uni.showToast({ title: error?.message || "发布失败", icon: "none" });
  } finally {
    publishing.value = false;
  }
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

.publish-page {
  min-height: 100vh;
  background: #ffffff;
  display: flex;
  flex-direction: column;
}

.scroll-area {
  flex: 1;
}

.form-wrap {
  padding: 0;
}

.form-item {
  padding: 24rpx 32rpx;
  position: relative;
}

.form-divider {
  height: 12rpx;
  background: var(--bg);
}

/* 标题输入 */
.title-input {
  width: 100%;
  font-size: 36rpx;
  font-weight: 700;
  color: var(--text-main);
  line-height: 1.5;
  height: 80rpx;
}

/* 正文输入 */
.content-input {
  width: 100%;
  font-size: 30rpx;
  color: var(--text-sub);
  line-height: 1.8;
  min-height: 280rpx;
}

.content-tip {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  color: var(--text-hint);
}

.word-count {
  font-size: 22rpx;
  color: var(--text-hint);
  position: absolute;
  bottom: 20rpx;
  right: 32rpx;
}

/* 图片上传 & 发布按钮 */
.img-section {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 32rpx 24rpx;
  border-bottom: 1rpx solid var(--border);
}

.img-list {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
}

.img-item {
  position: relative;
  width: 186rpx;
  height: 186rpx;
}

.preview-img {
  width: 186rpx;
  height: 186rpx;
  border-radius: 16rpx;
  object-fit: cover;
}

.img-del {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  line-height: 1;
}

.img-add-btn {
  width: 186rpx;
  height: 186rpx;
  border-radius: 16rpx;
  border: 2rpx dashed var(--border);
  background: var(--bg);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.img-add-icon {
  font-size: 48rpx;
  color: var(--text-hint);
  font-weight: 300;
}

.img-add-tip {
  font-size: 22rpx;
  color: var(--text-hint);
}

/* 行内发布按钮 */
.publish-btn-inline {
  width: 120rpx;
  height: 80rpx;
  margin-top: 108rpx;
  border-radius: 16rpx;
  background: #f5f5f5;
  color: #bbbbbb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: 600;
  transition: all 0.2s;
}

.publish-btn-inline.active {
  background: var(--primary);
  color: #ffffff;
}

.publish-btn-inline:active {
  transform: scale(0.95);
}

/* 标签选择 */
.form-label {
  font-size: 28rpx;
  color: var(--text-sub);
  font-weight: 500;
  margin-bottom: 18rpx;
  display: block;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 16rpx;
}

.tag-option {
  border: 1rpx solid var(--border);
  border-radius: 100rpx;
  padding: 12rpx 28rpx;
  font-size: 26rpx;
  color: var(--text-sub);
  background: var(--bg);
  transition: all 0.15s;
}

.tag-option.selected {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-light);
}

/* 开关行 */
.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.switch-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.switch-label {
  font-size: 30rpx;
  color: var(--text-main);
  font-weight: 500;
}

.switch-desc {
  font-size: 24rpx;
  color: var(--text-hint);
}
</style>
